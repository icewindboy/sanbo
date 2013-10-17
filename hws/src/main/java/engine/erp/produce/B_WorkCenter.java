package engine.erp.produce;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import java.util.*;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.project.*;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 工作中心列表</p>
 * <p>Description: 工作中心信息维护</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_WorkCenter extends BaseAction implements Operate
{
    public static final String FIXED_SEARCH = "1099";
  /**
   * 提取工作中心信息的SQL语句
   */
  private static final String WORKCENTER_SQL = "SELECT * FROM sc_gzzx ORDER BY gzzxbh";//

  /**
   * 保存工作中心信息的数据集
   */
  private EngineDataSet dsWorkCenter = new EngineDataSet();

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
  * 定义固定查询类
   */
  private QueryBasic fixedQuery = new QueryFixedItem();
  private boolean isInitQuery = false; //是否已经初始化查询条件

  /**
   * 得到工序分段信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回仓库信息的实例
   */
  public static B_WorkCenter getInstance(HttpServletRequest request)
  {
   B_WorkCenter b_WorkCenterBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_WorkCenterBean";
      b_WorkCenterBean = (B_WorkCenter)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_WorkCenterBean == null)
      {
        b_WorkCenterBean = new B_WorkCenter();
        session.setAttribute(beanName, b_WorkCenterBean);
      }
    }
    return b_WorkCenterBean;
  }
  /**
   * 构造函数
   */
  private B_WorkCenter()
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
    setDataSetProperty(dsWorkCenter, WORKCENTER_SQL);
    dsWorkCenter.setSort(new SortDescriptor("", new String[]{"gzzxbh"}, new boolean[]{false}, null, 0));
    dsWorkCenter.setSequence(new SequenceDescriptor(new String[]{"gzzxID"}, new String[]{"s_sc_gzzx"}));
    //添加操作的触发对象
    B_WorkCenter_Add_Edit add_edit = new B_WorkCenter_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_WorkCenter_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_WorkCenter_Post());
    addObactioner(String.valueOf(DEL), new B_WorkCenter_Delete());
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());
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
      if(dsWorkCenter.isOpen() && dsWorkCenter.changesPending())
        dsWorkCenter.reset();
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
    if(dsWorkCenter != null){
      dsWorkCenter.close();
      dsWorkCenter = null;
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
    if(!dsWorkCenter.isOpen())
      dsWorkCenter.open();
    return dsWorkCenter;
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 初始化操作的触发类
   * /**
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }
  class B_WorkCenter_Init implements Obactioner
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
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      dsWorkCenter.setQueryString(WORKCENTER_SQL);
      dsWorkCenter.setRowMax(null);
    }
  }
  class FIXED_SEARCH implements Obactioner
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
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " WHERE "+SQL;
      SQL = WORKCENTER_SQL + SQL;
      if(!dsWorkCenter.getQueryString().equals(SQL))
      {
        dsWorkCenter.setQueryString(SQL);
        dsWorkCenter.setRowMax(null);
      }
    }
    /**
    * 初始化查询的各个列
    * @param request web请求对象
    */
   private void initQueryItem(HttpServletRequest request)
   {
     if(isInitQuery)
       return;
     fixedQuery = new QueryFixedItem();
     EngineDataSet master = dsWorkCenter;
     if(!master.isOpen())
       master.open();

     //初始化固定的查询项目
     fixedQuery.addShowColumn("", new QueryColumn[]{
       new QueryColumn(master.getColumn("gzzxbh"), null, null, null),//工作中心编号
       new QueryColumn(master.getColumn("gzzxmc"), null, null, null),//工作中心名称
       new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//所属部门
       new QueryColumn(master.getColumn("sfgjgzzx"), null, null, null, null, "=")//是否关键工作中心
     });
     isInitQuery = true;
    }
  }
  /**
   * 添加或修改操作的触发类
   */
  class B_WorkCenter_Add_Edit implements Obactioner
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
        dsWorkCenter.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsWorkCenter.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }

  /**
   * 保存操作的触发类
   */
  class B_WorkCenter_Post implements Obactioner
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
      String deptid = rowInfo.get("deptid");//所属部门
      String gzzxbh = rowInfo.get("gzzxbh");//工作中心编号
      String gzzxmc = rowInfo.get("gzzxmc");//工作中心名称
      String sbs = rowInfo.get("sbs");//设备数
      String rgs = rowInfo.get("rgs");//人工数
      String lyl = rowInfo.get("lyl");//利用率
      String sfgjgzzx = rowInfo.get("sfgjgzzx");//是否关键工作中心
      int sbs_i,rgs_i,lyl_i;
      String alert = null;
      if(sbs.length() > 0 && (alert =checkInt(sbs,"设备数")) !=null)
      {
        data.setMessage(alert);
        return;
      }
      if(sbs.length()>5)
      {
        data.setMessage(showJavaScript("alert('设备数超出指定范围！');"));
        return;
      }
      if(rgs.length() > 0 && (alert =checkInt(rgs,"人工数")) !=null)
      {
        data.setMessage(alert);
        return;
      }
      if(rgs.length()>5)
      {
        data.setMessage(showJavaScript("alert('人工数超出指定范围！');"));
        return;
      }
      boolean sbsIsNull = sbs.length() == 0;
      sbs_i = sbsIsNull ? 0 : Integer.parseInt(sbs);

      if(!sbsIsNull && sbs_i<1)
      {
        data.setMessage(showJavaScript("alert('设备数不能小于1！');"));
        return;
      }
      boolean rgsIsNull = rgs.length() == 0;
      rgs_i = rgsIsNull ? 0 : Integer.parseInt(rgs);

      if(!rgsIsNull && rgs_i<1)
      {
        data.setMessage(showJavaScript("alert('人工数不能小于1！');"));
        return;
      }
      if(gzzxbh.equals("")){
        data.setMessage(showJavaScript("alert('工作中心编号不能为空！');"));
        return;
      }
      if(gzzxmc.equals("")){
        data.setMessage(showJavaScript("alert('工作中心名称不能为空！');"));
        return;
      }
      if(!isAdd)
        ds.goToInternalRow(editrow);

      if(isAdd || !gzzxbh.equals(ds.getValue("gzzxbh")))//添加时判断工作中心编号是否已经存在
      {
        String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('sc_gzzx','gzzxbh','"+gzzxbh+"') from dual");
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('工作中心编号("+ gzzxbh +")已经存在!');"));
          return;
        }
      }
      if(isAdd || !gzzxmc.equals(ds.getValue("gzzxmc")))//添加时判断工作中心是否已经存在
     {
       String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('sc_gzzx','gzzxmc','"+gzzxmc+"') from dual");
       if(!count.equals("0"))
       {
         data.setMessage(showJavaScript("alert('工作中心名称("+ gzzxmc +")已经存在!');"));
         return;
       }
      }
      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("gzzxid", "-1");
      }
      ds.setValue("deptid", deptid);
      ds.setValue("gzzxbh", gzzxbh);
      ds.setValue("gzzxmc", gzzxmc);
      ds.setValue("sbs", sbs);
      ds.setValue("rgs", rgs);
      ds.setValue("lyl", lyl);
      ds.setValue("sfgjgzzx", sfgjgzzx);
      ds.post();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_WORK_CENTER);
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }

  /**
   * 删除操作的触发类
   */
  class B_WorkCenter_Delete implements Obactioner
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
      String gzzxid = ds.getValue("gzzxid");
      String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM sc_gylxmx WHERE gzzxid="+gzzxid);
      if(!count.equals("0")){
        data.setMessage(showJavaScript("alert('该工作中心已被引用不能删除！')"));
        return;
      }
      else{
      ds.deleteRow();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_WORK_CENTER);
      }
    }
  }
}
