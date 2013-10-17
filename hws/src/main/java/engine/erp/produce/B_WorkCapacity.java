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
 * <p>Title: 生产能力设置</p>
 * <p>Description: 生产能力设置</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class  B_WorkCapacity  extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1999";
  /**
   * 提取生产能力信息的SQL语句
   */
  private static final String  B_WORKCAPACITY_SQL =
      "SELECT * FROM sc_scnl, kc_dm WHERE sc_scnl.cpid = kc_dm.cpid ? ORDER BY kc_dm.cpbm ";
  private static final String  B_WORKCAPACITY_STRUCT_SQL =
      "SELECT * FROM sc_scnl WHERE 1<>1 ";
  /**
   * 保存生产能力信息的数据集
   */
  private EngineDataSet dsWorkCapacity = new EngineDataSet();
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
  private long editrow = 0;

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
   * 得到生产能力信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回生产能力信息的实例
   */
  public static  B_WorkCapacity  getInstance(HttpServletRequest request)
  {
     B_WorkCapacity b_WorkCapacityBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_WorkCapacityBean";
      b_WorkCapacityBean = (B_WorkCapacity)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_WorkCapacityBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        b_WorkCapacityBean = new  B_WorkCapacity();
        session.setAttribute(beanName, b_WorkCapacityBean);
      }
    }
    return b_WorkCapacityBean;
  }
  /**
   * 构造函数
   */
  private  B_WorkCapacity()
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
    setDataSetProperty(dsWorkCapacity, "SELECT * FROM sc_scnl");
    //dsWorkCapacity.setSort(new SortDescriptor("", new String[]{"scnlid"}, new boolean[]{false}, null, 0));
    dsWorkCapacity.setSequence(new SequenceDescriptor(new String[]{"scnlid"}, new String[]{"s_sc_scnl"}));
    dsWorkCapacity.setTableName("sc_scnl");
    //添加操作的触发对象
     B_WorkCapacity_Add_Edit add_edit = new  B_WorkCapacity_Add_Edit();
    addObactioner(String.valueOf(INIT), new  B_WorkCapacity_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new  B_WorkCapacity_Post());
    addObactioner(String.valueOf(DEL), new  B_WorkCapacity_Delete());
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
      if(dsWorkCapacity.isOpen() && dsWorkCapacity.changesPending())
        dsWorkCapacity.reset();
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
    if(dsWorkCapacity != null){
      dsWorkCapacity.close();
      dsWorkCapacity = null;
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
   * 初始化行信息
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
    if(!dsWorkCapacity.isOpen())
      dsWorkCapacity.open();
    return dsWorkCapacity;
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo()
  {
    return rowInfo;
  }

  /**
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }
  //------------------------------------------
  //操作实现的类
  //------------------------------------------
  /**
   * 初始化操作的触发类
   */
  class  B_WorkCapacity_Init implements Obactioner
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
      String SQL = combineSQL(B_WORKCAPACITY_SQL, "?", new String[]{""});
      dsWorkCapacity.setQueryString(SQL);
      dsWorkCapacity.setRowMax(null);
      //data.setMessage(showJavaScript("showFixedQuery()"));//初始化弹出查询界面
    }
  }
  /**
   * 添加查询操作的触发类
   */
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
        SQL = " AND "+SQL;
      SQL = combineSQL( B_WORKCAPACITY_SQL, "?", new String[]{SQL});
      if(!dsWorkCapacity.getQueryString().equals(SQL))
      {
        dsWorkCapacity.setQueryString(SQL);
        dsWorkCapacity.setRowMax(null);
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
      EngineDataSet master = dsWorkCapacity;
      if(!master.isOpen())
        master.open();

      //初始化固定的查询项目
      fixedQuery.addShowColumn("sc_scnl", new QueryColumn[]{
      new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),
      new QueryColumn(master.getColumn("scnlid"), "VW_SC_SCNLQUERY", "scnlid", "product","product",null),
      new QueryColumn(master.getColumn("scnlid"), "VW_SC_SCNLQUERY", "scnlid", "cpbm","cpbm",null),
      new QueryColumn(master.getColumn("wzlbid"), null, null, null, null, "="),
      new QueryColumn(master.getColumn("gzzxid"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }
  /**
   * 添加或修改操作的触发类
   */
  class  B_WorkCapacity_Add_Edit implements Obactioner
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
        dsWorkCapacity.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsWorkCapacity.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }

  /**
   * 保存操作的触发类
   */
  class  B_WorkCapacity_Post implements Obactioner
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
      String cpid = rowInfo.get("cpid");
      String wzlbid = rowInfo.get("wzlbid");
      String gzzxid = rowInfo.get("gzzxid");
      String tqq = rowInfo.get("tqq");
      String sbs = rowInfo.get("sbs");
      String rgs = rowInfo.get("rgs");
      String rs = rowInfo.get("rs");
      String cl = rowInfo.get("cl");
      int sbs_i,rgs_i,rs_i,cl_i;
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
      if(rs.length() > 0 && (alert =checkInt(rs,"日时")) !=null)
      {
        data.setMessage(alert);
        return;
      }
      if(rgs.length()>5)
      {
        data.setMessage(showJavaScript("alert('日时超出指定范围！');"));
        return;
      }
      if(cl.length() > 0 && (alert =checkInt(cl,"产量")) !=null)
     {
       data.setMessage(alert);
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
      boolean rsIsNull = rs.length() == 0;
      rs_i = rgsIsNull ? 0 : Integer.parseInt(rs);

      if(!rsIsNull && rs_i<1)
      {
        data.setMessage(showJavaScript("alert('日时不能小于1！');"));
        return;
      }
      boolean clIsNull = cl.length() == 0;
      cl_i = clIsNull ? 0 : Integer.parseInt(cl);

      if(!clIsNull && cl_i<1)
      {
        data.setMessage(showJavaScript("alert('产量不能小于1！');"));
        return;
      }
      if(cpid.equals("")){
        data.setMessage(showJavaScript("alert('产品ID不能为空！');"));
        return;
      }
      if(!isAdd)
        ds.goToInternalRow(editrow);

      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("scnlid", "-1");
      }
      ds.setValue("cpid",cpid);
      ds.setValue("wzlbid",wzlbid);
      ds.setValue("gzzxid",gzzxid);
      ds.setValue("tqq",tqq);
      ds.setValue("sbs",sbs);
      ds.setValue("rgs",rgs);
      ds.setValue("rs",rs);
      ds.setValue("cl",cl);
      ds.post();
      ds.saveChanges();
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }

  /**
   * 删除操作的触发类
   */
  class  B_WorkCapacity_Delete implements Obactioner
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

