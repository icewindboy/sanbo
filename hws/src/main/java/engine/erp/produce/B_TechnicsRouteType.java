package engine.erp.produce;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.project.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 工艺路线类型列表</p>
 * <p>Description: 工艺路线信息维护</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_TechnicsRouteType extends BaseAction implements Operate
{
  /**
   * 提取工艺路线类型信息的SQL语句
   */
  private static final String TECHNICSROUTETYPE_SQL = "SELECT * FROM sc_gylxlx WHERE gylxlxid=";//

  /**
   * 存放工艺路线类型信息的数据集
   */
  private EngineDataSet dsTechnicsRouteType = new EngineDataSet();

  /**
   * 用于定位数据集
   */
  private EngineRow locateRow = null;

  /**
   * 保存用户输入的信息
   */
  private RowMap rowInfo = new RowMap();

  /**
   * 是否在添加状态
   */
  public  boolean isAdd = true;

  /**
   * 保存修改操作的行记录指针
   */
  private long  editrow = 0;

  /**
   * 点击返回按钮的URL
   */
  public  String retuUrl = null;

  /**
   * 得到工艺路线类型信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 工艺路线类型信息的实例
   */
  public static B_TechnicsRouteType getInstance(HttpServletRequest request)
  {
   B_TechnicsRouteType b_TechnicsRouteTypeBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)//同步SESSION
    {
      String beanName = "b_TechnicsRouteTypeBean";
      b_TechnicsRouteTypeBean = (B_TechnicsRouteType)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_TechnicsRouteTypeBean == null)
      {
        b_TechnicsRouteTypeBean = new B_TechnicsRouteType();//NEW实例对象
        session.setAttribute(beanName, b_TechnicsRouteTypeBean);//实例对象存入session
      }
    }
    return b_TechnicsRouteTypeBean;
  }
  /**
   * 构造函数
   */
  private B_TechnicsRouteType()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsTechnicsRouteType, "SELECT * FROM sc_gylxlx ORDER BY gylxlxbh");//设置数据属性提供者

    dsTechnicsRouteType.setSort(new SortDescriptor("", new String[]{"gylxlxbh"}, new boolean[]{false}, null, 0));//数据集排序方法
    dsTechnicsRouteType.setSequence(new SequenceDescriptor(new String[]{"gylxlxid"}, new String[]{"s_sc_gylxlx"}));//数据集序列
    //添加操作的触发对象
    B_TechnicsRouteType_Add_Edit add_edit = new B_TechnicsRouteType_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_TechnicsRouteType_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_TechnicsRouteType_Post());
    addObactioner(String.valueOf(DEL), new B_TechnicsRouteType_Delete());
  }

  //----Implementation of the BaseAction abstract class
  /**
   * JSP调用的函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      String operate = request.getParameter(OPERATE_KEY);
      if(operate != null && operate.trim().length() > 0)
      {
        RunData data = notifyObactioners(operate, request, response, null);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsTechnicsRouteType.isOpen() && dsTechnicsRouteType.changesPending())
        dsTechnicsRouteType.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  //----Implementation of the BaseAction abstract class
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsTechnicsRouteType != null){
      dsTechnicsRouteType.close();
      dsTechnicsRouteType = null;
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
    else
    {
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('sc_gylxlx','gylxlxbh','','',3) from dual");
      rowInfo.put("gylxlxbh", code);//新增时得到下一个工艺路线类型编号，并推入到rowinfo中，页面显示
    }
  }

  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsTechnicsRouteType.isOpen())
      dsTechnicsRouteType.open();
    return dsTechnicsRouteType;
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 初始化操作的触发类
   */
  class B_TechnicsRouteType_Init implements Obactioner
  {
    //----Implementation of the Obactioner interface
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
    }
  }

  /**
   * 添加或修改操作的触发类
   */
  class B_TechnicsRouteType_Add_Edit implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 添加或修改的触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsTechnicsRouteType.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsTechnicsRouteType.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }

  /**
   * 保存操作的触发类
   */
  class B_TechnicsRouteType_Post implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发保存操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
      rowInfo.put(data.getRequest());
      String gylxlxbh = rowInfo.get("gylxlxbh");//工艺路线类型编号
      String gylxlxmc = rowInfo.get("gylxlxmc");//工艺路线类型名称
      if(gylxlxmc.equals("")){
        data.setMessage(showJavaScript("alert('工艺路线类型不能为空！');"));
        return;
      }
      if(!isAdd)
        ds.goToInternalRow(editrow);

      if(isAdd || !gylxlxmc.equals(ds.getValue("gylxlxmc")))//判断该工艺路线类型名称是否已经存在
      {
        String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('sc_gylxlx','gylxlxmc','"+gylxlxmc+"') from dual");
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('工艺路线类型("+ gylxlxmc +")已经存在!');"));
          return;
        }
      }
      if(isAdd || !gylxlxbh.equals(ds.getValue("gylxlxbh")))//判断该工艺路线类型编号是否已经存在
      {
        String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('sc_gylxlx','gylxlxbh','"+gylxlxbh+"') from dual");
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('工艺路线类型编号("+ gylxlxbh +")已经存在!');"));
          return;
        }
      }

      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("gylxlxid", "-1");
      }
      ds.setValue("gylxlxbh", gylxlxbh);
      ds.setValue("gylxlxmc", gylxlxmc);
      ds.post();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_TECHNICS_ROUTE_TYPE);
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }

  /**
   * 删除操作的触发类
   */
  class B_TechnicsRouteType_Delete implements Obactioner
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
      String gylxlxid = ds.getValue("gylxlxid");
      String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM sc_gylx WHERE gylxlxid="+gylxlxid);
      if(!count.equals("0")){
        data.setMessage(showJavaScript("alert('该类型已被引用不能删除！')"));
        return;
      }
      else{
        ds.deleteRow();
        ds.saveChanges();
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_TECHNICS_ROUTE_TYPE);//刷新LookUP，使得到下拉框更新
      }
    }
  }
}
