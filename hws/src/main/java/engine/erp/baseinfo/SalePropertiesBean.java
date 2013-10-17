package engine.erp.baseinfo;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 基础管理--属性设置--</p>
 * <p>Description: 基础管理--属性设置--</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class SalePropertiesBean extends BaseAction implements Operate
{
  public static final String ENUM_EDIT = "1001";//双击"枚举值定义"所要触发的操作(propertyeidt.jsp)
  public static final String ENUM_DEFINE = "2001";//双击"枚举值定义"所要触发的操作(propertyeidt.jsp)ENUM_DEFINE
  public static final String ENUM_POST = "1003";//枚举值修改,新增时单击"保存"时所触发的操作的标识符.
  public static final String ENUM_DEL = "1004";//枚举值删除操作的标识符,
  public static final String ENUM_ADD = "1005";//枚举值新增操作的标识符.
  private static final String NODEFIELD_SQL = "SELECT * FROM nodeField WHERE isBak=1 and (tableCode='xs_ht' or tableCode='xs_hthw')";  //提取界面显示字段的SQL语句,条件是备用字段,表是销售合同或销售合同货物
  private EngineDataSet dsSaleProperties = new EngineDataSet(); //保存界面字段信息的数据集
  private EngineRow locateRow = null;      //用于定位数据集
  private RowMap rowInfo = new RowMap();   //保存用户输入的信息
  public  boolean isAdd = true;            //是否在添加状态
  public boolean isEnumAdd = true;         //表示枚举值在添加状态
  private long    editrow = 0;             //保存修改操作的行记录指针
  public   String retuUrl = null;          //点击返回按钮的URL
  public ArrayList enumkey =new ArrayList();  //存贮枚举ID号
  public ArrayList enumvalue =new ArrayList();//存贮枚举值
  public String venumkey="";                  //ID号
  public String venumvalue="";                //枚举值
  /**
   * 构造函数
   */
  private SalePropertiesBean()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("saleproperties", ex);
    }
  }
  /**
   * 类的主要事件
   * 1.添加各种操作的执行者(需要事项接口Obactioner)
   * 2.主要用doService方法为入口函数
   * 3.在doService方法中触发操作相对应的执行者
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected void jbInit() throws Exception
  {
    /**
     * setDataSetProperty方法是在BaseAction中继承下来的
     * 为数据集设置与SessionBean连接的属性
     * @param cds 数据集
     * @param sql SQL语句
     */
    setDataSetProperty(dsSaleProperties, null);//初始为不提取数据(空数据集)
    /*
    数据集排序设置
    String [] fields = new String[]{"tableName","orderNum"};//要排序的字段名称列表
    boolean[] sorts = new boolean[]{false, false};          //
    SortDescriptor sortd = new SortDescriptor("", fields, sorts, null, 0);
    dsSaleProperties.setSort(sortd);    */
    dsSaleProperties.setSort(new SortDescriptor("", new String[]{"tableName","fieldName"}, new boolean[]{false, false}, null, 0));
    dsSaleProperties.setSequence(new SequenceDescriptor(new String[]{"nodeFieldID"}, new String[]{"s_nodefield"}));//主健;序列名:s_表名
    SalePropertiesBean_Add_Edit add_edit = new SalePropertiesBean_Add_Edit();//添加,修改操作的执行者
    SalePropertiesBean_Post post = new SalePropertiesBean_Post();            //保存操作的执行者

    addObactioner(String.valueOf(INIT), new SalePropertiesBean_Init());      //注册初始化操作
    addObactioner(String.valueOf(ADD), add_edit);                            //注册新增操作
    addObactioner(String.valueOf(EDIT), add_edit);                           //注册修改操作
    addObactioner(String.valueOf(POST), new SalePropertiesBean_Post());      //注册保存操作
    addObactioner(String.valueOf(DEL), new SalePropertiesBean_Delete());     //注册删除操作
    //枚举值操作的执行者 .
    SalePropertiesBeanArraylist_Add array_add_edt = new SalePropertiesBeanArraylist_Add();
    addObactioner(ENUM_DEFINE, array_add_edt);
    addObactioner(ENUM_EDIT, array_add_edt);                                 //注册枚举值编辑操作.
    addObactioner(ENUM_POST, new SalePropertiesBeanArraylist_Post());        //注册枚举值保存操作.
    addObactioner(ENUM_DEL, new SalePropertiesBeanArraylist_Delete());       //注册枚举值删除操作.
    addObactioner(ENUM_ADD, array_add_edt);                                  //注册枚举值新增操作.
  }
  /**
   * 析构函数
   * 得到界面显示字段信息的实例
   * 判断该session是否有该bean的实例,如没有就创建一个新的实例,并加入到会话中.
   * @param request jsp请求
   * @return 返回界面显示字段信息的实例
   */
  public static SalePropertiesBean getInstance(HttpServletRequest request)
  {
    SalePropertiesBean salePropertiesBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "SaleProperties";
      salePropertiesBean = (SalePropertiesBean)session.getAttribute(beanName);
      if(salePropertiesBean == null)
      {
        salePropertiesBean = new SalePropertiesBean();
        session.setAttribute(beanName, salePropertiesBean);
      }
    }
    return salePropertiesBean;
  }
  /**
   * Implementation of the BaseAction abstract class
   * JSP调用的函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   */
  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      String opearate = request.getParameter(OPERATE_KEY);//String OPERATE_KEY="operate";operate接口中定义
      if(opearate != null && opearate.trim().length() > 0)
      {
        RunData data = notifyObactioners(opearate, request, response, null);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsSaleProperties.isOpen() && dsSaleProperties.changesPending())
        dsSaleProperties.reset();//复位
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  //----Implementation of the BaseAction abstract class
  /**
   * session失效时调用的方法
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsSaleProperties != null){
      dsSaleProperties.close();
      dsSaleProperties = null;
    }
    log = null;
    rowInfo = null;
    locateRow = null;
  }

  //----Implementation of the BaseAction abstract class
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
  {
    return getClass();
  }
  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
    if(!isAdd)
      rowInfo.put(getOneTable());
  }
  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsSaleProperties.isOpen())
      dsSaleProperties.open();
    return dsSaleProperties;
  }
  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  public static String chckList(String str)
  {
    if("1".equals(str) )
    {
      return "字符型";
     }
    else if("2".equals(str) )
    {
    return "文本型";
     }
    else
     return "枚举型";
  }
  //------------------------------------------
  //操作实现的类
  //------------------------------------------
  /**
   * 初始化操作的触发类
   */
  class SalePropertiesBean_Init implements Obactioner
  {
    /**
     * 触发初始化操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //执行SQL查询语句.
      dsSaleProperties.setQueryString(NODEFIELD_SQL);
      if(dsSaleProperties.isOpen())
        dsSaleProperties.refresh();
      else
        dsSaleProperties.open();
    }
  }
  /**
   * 添加或修改操作的触发类
   */
  class SalePropertiesBean_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsSaleProperties.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsSaleProperties.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }

  /**
   * 保存操作的触发类
   */
  class SalePropertiesBean_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
        rowInfo.put(data.getRequest());
        String tableCode = rowInfo.get("tableCode");
        String tableName = rowInfo.get("tableName");
        String fieldName = rowInfo.get("fieldName");
        String enumValues = rowInfo.get("enumValues");
        String orderNum = rowInfo.get("orderNum");
        String dataLen = rowInfo.get("dataLen");
        String inputType = rowInfo.get("inputType");
        String describe = rowInfo.get("describe");
        String temp = null;
        try
        {
          int len=Integer.parseInt(dataLen);
        }catch(NumberFormatException e)
        {
          data.setMessage(showJavaScript("alert('数据长度应为1至圣128之间的数字');"));
          return;
        }
        if(inputType.equals("1"))
        {
        if(Integer.parseInt(dataLen)>=128||Integer.parseInt(dataLen)<1)
        {
          data.setMessage(showJavaScript("alert('数据长度应为1至圣128之间的数字');"));
          return;
        }
        }
        if(inputType.equals("2"))
        {
        if(Integer.parseInt(dataLen)>=1024||Integer.parseInt(dataLen)<1)
        {
          data.setMessage(showJavaScript("alert('数据长度应为1至圣1024之间的数字');"));
          return;
        }
        }
        if(inputType.equals("3"))
        {
          if(Integer.parseInt(dataLen)>5||Integer.parseInt(dataLen)<1)
          {
            data.setMessage(showJavaScript("alert('数据长度应为1至圣5之间的数字');"));
            return;
          }
        }
        if(tableCode.equals("xs_ht"))
        {
          tableName="销售合同";
        }else
        {
          tableName="销售合同货物";
        }
        if(fieldName.equals("")){
         data.setMessage(showJavaScript("alert('显示名称不能为空！');"));
         return;
        }
        if(orderNum.equals(""))
          orderNum="0";

        if(!isAdd)
          ds.goToInternalRow(editrow);

        if(isAdd)
        {
          ds.insertRow(false);
          ds.setValue("nodeFieldID","-1");
          ds.setValue("isBak", "1");
          ds.setValue("isShow", "1");
          ds.setValue("fieldcode", "bak"+dataSetProvider.getSequence("S_NODEFIELD_FIELDCODE"));
        }
        ds.setValue("fieldName", fieldName);
        ds.setValue("tableCode", tableCode);
        ds.setValue("tableName", tableName);
        ds.setValue("enumValues", enumValues);
        ds.setValue("orderNum", orderNum);
        ds.setValue("dataLen", dataLen);
        ds.setValue("inputType", inputType);
        ds.setValue("describe", describe);

        ds.post();
        ds.saveChanges();
        data.setMessage(showJavaScript("parent.hideInterFrame();"));
      //}
    }
  }

  class SalePropertiesBeanArraylist_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int id=Integer.parseInt(data.getParameter("rownum"));
      venumvalue=data.getParameter("venumvalue");
      if(isEnumAdd)
      {
        enumkey.add(venumkey);
        enumvalue.add(venumvalue);
      }
      else
      enumvalue.set(id, venumvalue);
      StringBuffer ss= new StringBuffer();
      for(int i=0;i<enumkey.size();i++)
      {
        String key = (String)enumkey.get(i);
        String value = (String)enumvalue.get(i);
        if(i==enumkey.size()-1)
          ss.append(key).append("=").append(value);
        else
          ss.append(key).append("=").append(value).append("$");
      }
      EngineDataSet ds = getOneTable();
      ds.goToInternalRow(editrow);
      ds.setValue("enumValues", ss.toString());
      ds.post();
      ds.saveChanges();
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }

  /**
   * 删除操作的触发类
   */
  class SalePropertiesBean_Delete implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发删除操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.deleteRow();
      ds.saveChanges();
    }
  }

  /**
   *点击删除枚举值所触发动作.
   *
   * */
  class SalePropertiesBeanArraylist_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //获得所要删除的枚举值在ArrayList中偏移量(位置0---n)
      int id=Integer.parseInt(data.getParameter("rownum"));
      //从ArrayList中间变量中删除指定的值
      enumvalue.remove(id);
      enumkey.remove(id);
      //从ArrayList中间变量重构字段值(枚举值ID=枚举值,枚举值ID=枚举值).
      String ss="";
      for(int i=0;i<enumkey.size();i++)
      {
        if(i==enumkey.size()-1)
          ss=ss+enumkey.get(i)+"="+enumvalue.get(i);
        else
          ss=ss+enumkey.get(i)+"="+enumvalue.get(i)+"$";
      }
      //重新得到所修改的数据集,
      EngineDataSet ds = getOneTable();
      //记录指针移到所修改的行.
      ds.goToInternalRow(editrow);
      //更新字段值
      ds.setValue("enumValues", ss);
      //提交数据,更新数据集
      ds.post();
      //把新数据集保存回数据库
      ds.saveChanges();
      //关闭IFRAME
      //data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }
  /**
   *枚举值定义操作:鼠标单击(枚举值定义,新增,修改)所触发的事件.
   *
   * */
  class SalePropertiesBeanArraylist_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //到枚举值列表操作,单击"枚举值定义"时,所触发的操作及传入的对应的数据集的行号
      if(action.equals(ENUM_DEFINE))
      {
        //清空ArrayList
        enumkey.clear();
        enumvalue.clear();
        //数据集的指针移到所要操作的行.
        dsSaleProperties.goToRow(Integer.parseInt(data.getParameter("rownum")));
        //得到所要编辑的行的的位置的信息(editrow保存行号)
        editrow = dsSaleProperties.getInternalRow();
        //从数据集的当前行获取指定字段的值.
        String enumstr = dsSaleProperties.getValue("enumValues");
        //对所获得的字段信息进行解析.并把解析的值存贮在ArrayList中.
        String[] enumlist=BaseAction.parseString(enumstr,"$");
        for(int j=0;j<enumlist.length;j++)
        {
          String[] es=BaseAction.parseString(enumlist[j],"=");
          enumkey.add(es[0]);
          enumvalue.add(es[1]);
        }
        //调用JSP页面的"toDetail()"函数,转移到所指定的页面.doSdervice()方法的的执行返回此信息.showJavaScript()方法执行结果返回javascript脚本.
        data.setMessage(showJavaScript("toDetail();"));
      }
      else if(action.equals(ENUM_EDIT))
      {
        //枚举值的修改操作.点击修改时所触发的操作.捕获指定行号的信息.
        int id=Integer.parseInt(data.getParameter("rownum"));
        venumkey=(String)enumkey.get(id);
        venumvalue=(String)enumvalue.get(id);
      }
      //枚举值的添加修改操作
      else if(action.equals(ENUM_ADD))
      {
        isEnumAdd = true;
          //枚举值添加操作.点击新增时所触发的操作,设置键值.(从0开始增长或垒加.)
          int size  = enumkey.size();
          venumkey = (size == 0) ? "1" : String.valueOf(Integer.parseInt((String)enumkey.get(size-1))+1);
          venumvalue = "";
      }
    }
  }
}
