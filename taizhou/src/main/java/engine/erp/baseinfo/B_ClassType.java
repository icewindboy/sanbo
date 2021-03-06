package engine.erp.baseinfo;

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
 * <p>Title: 基础信息管理——存货类别信息维护</p>
 * <p>Description: 存货类别信息维护</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_ClassType extends BaseAction implements Operate
{
  /**
   * 提取存货类别信息的SQL语句
   */
  private static final String STOCKSKIND_SQL = "SELECT * FROM jc_cutomcalss WHERE classid=? order by pxh";//

  /**
   * 保存存货类别信息的数据集
   */
  private EngineDataSet dsStocksKind = new EngineDataSet();

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
   * 得到工序名称信息的实例
   * @param request jsp请求
   * @return 工序名称信息的实例
   */
  public static B_ClassType getInstance(HttpServletRequest request)
  {
    B_ClassType classtypeBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "classtypeBean";
      classtypeBean = (B_ClassType)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(classtypeBean == null)
      {
        classtypeBean = new B_ClassType();
        session.setAttribute(beanName, classtypeBean);
      }
    }
    return classtypeBean;
  }
  /**
   * 构造函数
   */
  private B_ClassType()
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
    setDataSetProperty(dsStocksKind, "SELECT * FROM jc_cutomcalss order by pxh");
    dsStocksKind.setSort(new SortDescriptor("", new String[]{"pxh"}, new boolean[]{false}, null, 0));
    dsStocksKind.setSequence(new SequenceDescriptor(new String[]{"classid"}, new String[]{"s_jc_cutomcalss"}));
    //添加操作的触发对象
    B_ClassType_Add_Edit add_edit = new B_ClassType_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_ClassType_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_ClassType_Post());
    addObactioner(String.valueOf(DEL), new B_ClassType_Delete());
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
      String opearate = request.getParameter(OPERATE_KEY);
      if(opearate != null && opearate.trim().length() > 0)
      {
        RunData data = notifyObactioners(opearate, request, response, null);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsStocksKind.isOpen() && dsStocksKind.changesPending())
        dsStocksKind.reset();
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
    if(dsStocksKind != null){
      dsStocksKind.close();
      dsStocksKind = null;
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
    if(!dsStocksKind.isOpen())
      dsStocksKind.open();
    return dsStocksKind;
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 初始化操作的触发类
   */
  class B_ClassType_Init implements Obactioner
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
  class B_ClassType_Add_Edit implements Obactioner
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
        dsStocksKind.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsStocksKind.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }

  /**
   * 保存操作的触发类
   */
  class B_ClassType_Post implements Obactioner
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
      String pxh = rowInfo.get("pxh");//排序号
      String gj = rowInfo.get("gj");
      String khdj = rowInfo.get("khdj");
      String khbm = rowInfo.get("khbm");
      String isf = rowInfo.get("isf");
      String classmc = "";

      String temp = null;
      if((temp = checkInt(pxh, "排序号")) !=null ){
        data.setMessage(temp);
        return;
      }
      if(gj.equals("")){
        data.setMessage(showJavaScript("alert('降级不能为空')"));
      }
      if(khdj.equals("")){
        data.setMessage(showJavaScript("alert('客户等级不能为空')"));
      }
      if(khbm.equals("")){
        data.setMessage(showJavaScript("alert('客户编码不能为空')"));
      }

      classmc = isf+gj+khdj+khbm;
      if(!isAdd)
        ds.goToInternalRow(editrow);

      if(isAdd || !pxh.equals(ds.getValue("pxh")))
      {
        String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('jc_cutomcalss','pxh','"+pxh+"') from dual");
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('排序号("+ pxh +")已经存在!');"));
          return;
        }
      }
      if(isAdd )
      {
        String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('jc_cutomcalss','classmc','"+classmc+"') from dual");
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('识别码("+ classmc +")已经存在!');"));
          return;
        }
      }
      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("classid", "-1");
      }
      ds.setValue("pxh", pxh);
      ds.setValue("isf", isf);
      ds.setValue("gj", gj);
      ds.setValue("khdj", khdj);
      ds.setValue("khbm", khbm);
      ds.setValue("classmc", classmc);

      ds.post();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STOCKS_KIND);
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }

  /**
   * 删除操作的触发类
   */
  class B_ClassType_Delete implements Obactioner
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
}
