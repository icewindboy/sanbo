package engine.erp.finance;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import java.util.*;
import engine.html.*;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.*;
import engine.project.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
import javax.swing.*;

/**
 * <p>Title: 银行帐户收支</p>
 * <p>Copyright: right reserved (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @version 1.0
 */
public final class B_AcountInOut extends BaseAction implements Operate
{
  private static final String MASTER_SQL       = "SELECT * FROM cw_accountio WHERE 1=1 and ? ? ";
  private static final String MASTER_STRUT_SQL = "SELECT * FROM cw_accountio WHERE 1<>1 ";
  private static final String APPROVE_SQL      = "SELECT * FROM cw_accountio WHERE accountioID='?' ";//用于审批时候提取一条记录

  private EngineDataSet dsB_AcountInOut = new EngineDataSet();//数据集
  private RowMap rowInfo = new RowMap();
  public  boolean isAdd = true;
  private long    editrow = 0;
  public  String retuUrl = null;
  private User user = null;
  private boolean isInitQuery = false;                         //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  boolean isApprove = false;     //是否在审批状态
  public  String loginid = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public static final String OPERATE_SEARCH            = "1066";//主表查询操作
  public static final String DELETE_RETURN             = "1067"; //主从删除操作
  public  static final String REPORT                   = "9008"; //报表追踪
  public static final String Accountio_ADD             = "9000";    //新增
  public static final String Accountio_DEL              = "9001";
  public static final String CANCER_APPROVE            ="90003";
  public static final String OVER                      ="90004";
  private String fgsid = null;   //分公司ID
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  public boolean isReport = false;
  private RowMap  m_RowInfo    = new RowMap();                 //主表添加行或修改行的引用
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsB_AcountInOut, "cw_accountio");
  public ArrayList arraylist_cw_acountio  = null;//
  private String accountioID = null;
  public String []zt;
  /**
   * 从会话中得到银行信用卡信息的实例
   * @param request jsp请求
   * @return 返回银行信用卡信息的实例
   */
  public static B_AcountInOut getInstance(HttpServletRequest request)
  {
    B_AcountInOut b_AcountInOutBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_AcountInOutBean";
      b_AcountInOutBean = (B_AcountInOut)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_AcountInOutBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        b_AcountInOutBean = new B_AcountInOut();
        b_AcountInOutBean.fgsid = loginBean.getFirstDeptID();
        b_AcountInOutBean.loginid = loginBean.getUserID();
        b_AcountInOutBean.dsB_AcountInOut.setColumnFormat("Inmoney", b_AcountInOutBean.sumFormat);
        b_AcountInOutBean.dsB_AcountInOut.setColumnFormat("outmoney", b_AcountInOutBean.sumFormat);
        b_AcountInOutBean.user = loginBean.getUser();
        b_AcountInOutBean.loginName = loginBean.getUserName();
        b_AcountInOutBean.user = loginBean.getUser();
        session.setAttribute(beanName, b_AcountInOutBean);//加入到session中

      }
    }
    return b_AcountInOutBean;
  }
  /**
   * 主册监听器
   * 构造函数
   */
  private B_AcountInOut()
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
    setDataSetProperty(dsB_AcountInOut, MASTER_STRUT_SQL);
    dsB_AcountInOut.setSort(new SortDescriptor("", new String[]{"yhzhId"}, new boolean[]{false}, null, 0));//设置排序方式
    //dsB_AcountInOut.setSequence(new SequenceDescriptor(new String[]{"accountioID"}, new String[]{"S_CW_ACCOUNTIO"}));//设置主健的sequence
    dsB_AcountInOut.setSequence(new SequenceDescriptor(new String[]{"billcode"}, new String[]{"SELECT pck_base.billNextCode('cw_accountio','billcode') from dual"}));

    B_AcountInOut_Add_Edit add_edit = new B_AcountInOut_Add_Edit();
    //addObactioner(String.valueOf(ADD), new Cw_Accounio_adddel());//新增删除操作
    addObactioner(String.valueOf(Accountio_DEL), new Cw_Accounio_adddel());//
    addObactioner(String.valueOf(OPERATE_SEARCH), new Master_Search());//定制查询
    addObactioner(String.valueOf(DELETE_RETURN), new B_AcountInOut_Delete());//删除主表某一行,及其对应的从表
    addObactioner(String.valueOf(INIT), new B_AcountInOut_Init());//初始化 operate=0
    addObactioner(String.valueOf(ADD), add_edit);//新增
    addObactioner(String.valueOf(EDIT), add_edit);//修改
    addObactioner(String.valueOf(POST), new B_AcountInOut_Post());//保存
    addObactioner(String.valueOf(DEL), new B_AcountInOut_Delete());//删除

    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());
    addObactioner(String.valueOf(OVER), new Over());

  }
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
      if(dsB_AcountInOut.isOpen() && dsB_AcountInOut.changesPending())
        dsB_AcountInOut.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsB_AcountInOut != null){
      dsB_AcountInOut.close();
      dsB_AcountInOut = null;
    }
    if(masterProducer != null)
    {
      masterProducer.release();
      masterProducer = null;
    }
    log = null;
    rowInfo = null;
  }
  /**
   * 得到子类的类名
   * 实现BaseAction中的抽象方法
   * 日志中调用
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
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      rowInfo.put("createdate", today);//制单日期
      rowInfo.put("iodate", today);//制单日期
      rowInfo.put("creator", loginName);//操作员
      rowInfo.put("creatorid", loginid);
      rowInfo.put("state", "0");
      rowInfo.put("filialeid", fgsid);
    }
  }
  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    //if(!dsB_AcountInOut.isOpen())
    //  dsB_AcountInOut.open();
    return dsB_AcountInOut;
  }

  /**
   *得到表的一行信息
   **/
  public final RowMap getRowinfo() {return rowInfo;}

  /*得到主表一行的信息*/
  //public final RowMap getMasterRowinfo() { return m_RowInfo; }


  //==========================================
  //操作实现的类:初始化;新增,修改,删除
  //==========================================
  /**
   * 初始化操作的触发类
   */
  class B_AcountInOut_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      zt = new String[]{""};
      isReport = false;
      isApprove = false;
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginid);
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      String SQL = " AND state<>8";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "creatorid"), SQL});
      dsB_AcountInOut.setQueryString(SQL);
      dsB_AcountInOut.setRowMax(null);
    }
  }

  /**
   * 从表保存操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = getRowinfo();
    rowInfo.put(request);

    int rownum=arraylist_cw_acountio.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)arraylist_cw_acountio.get(i);
      detailRow.put("accountioID", dsB_AcountInOut.getValue("accountioID"));//
      detailRow.put("billcode", rowInfo.get("billcode_"+i));//
      detailRow.put("inMoney", rowInfo.get("inMoney_"+i));//
      detailRow.put("outMoney", rowInfo.get("outMoney_"+i));//
      detailRow.put("state", rowInfo.get("state_"+i));//
      detailRow.put("stateDesc", rowInfo.get("stateDesc_"+i));//备注
      detailRow.put("creator", rowInfo.get("creator_"+i));
      detailRow.put("deptid", rowInfo.get("deptid_"+i));
      arraylist_cw_acountio.set(i,detailRow);
    }

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
  /**
   * 添加到审核列表的操作类
   */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsB_AcountInOut.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      accountioID = dsB_AcountInOut.getValue("accountioID");
      String content = dsB_AcountInOut.getValue("billcode");
      String deptid = dsB_AcountInOut.getValue("deptid");
      approve.putAproveList(dsB_AcountInOut, dsB_AcountInOut.getRow(), "acount_in_out", content,deptid);
    }
  }
  /**
   * 审批操作的触发类
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginid);
      //得到request的参数,值若为null, 则用""代替
      String id = null;
      if(String.valueOf(REPORT).equals(action))
      {
        isReport=true;
        isApprove = false;
        id=request.getParameter("id");
      }else
      {
        isApprove = true;
        id = data.getParameter("id", "");
      }
      String sql = combineSQL(APPROVE_SQL, "?", new String[]{id});
      dsB_AcountInOut.setQueryString(sql);
      if(dsB_AcountInOut.isOpen())
        dsB_AcountInOut.readyRefresh();
      dsB_AcountInOut.refresh();
      initRowInfo(false, true);
    }
  }
  /**
   * 完成
   */
  class Over implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsB_AcountInOut.goToRow(Integer.parseInt(data.getParameter("rownum")));
      accountioID = dsB_AcountInOut.getValue("accountioID");
      dsB_AcountInOut.setValue("state","8");
      dsB_AcountInOut.saveChanges();
    }
  }
  /**
   * 作废
   */
  class Cancer implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsB_AcountInOut.goToRow(row);
      dsB_AcountInOut.setValue("state", "4");
      dsB_AcountInOut.saveChanges();
    }
  }
  /**
   * 取消审批
   */
  class Cancer_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsB_AcountInOut.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsB_AcountInOut,dsB_AcountInOut.getRow(),"acount_in_out");
    }
  }
  /**
   * 添加或修改操作的触发类
   */
  class B_AcountInOut_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsB_AcountInOut.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsB_AcountInOut.getInternalRow();
      }
      initRowInfo(isAdd, true);
      //data.setMessage(showJavaScript("toDetail();"));
    }
  }
  class Cw_Accounio_adddel  implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(action.equals(Accountio_ADD))
      {
        putDetailInfo(data.getRequest());
        String accountioID = dsB_AcountInOut.getValue("accountioID");
        dsB_AcountInOut.insertRow(false);
        dsB_AcountInOut.setValue("accountioID", accountioID);
        dsB_AcountInOut.post();
        RowMap zg_Temp_Row = new RowMap(dsB_AcountInOut);
        arraylist_cw_acountio.add(zg_Temp_Row);
      }
      if(action.equals(Accountio_DEL))
      {
        putDetailInfo(data.getRequest());
        int rownum=Integer.parseInt(data.getRequest().getParameter("rownum"));
        arraylist_cw_acountio.remove(rownum);
        dsB_AcountInOut.goToRow(rownum);
        dsB_AcountInOut.deleteRow();
      }
    }
  }
  /**
   * 保存操作的触发类
   */
  class B_AcountInOut_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
      rowInfo.put(data.getRequest());
      String ioflag = rowInfo.get("ioflag");
      String jsfsid = rowInfo.get("jsfsid");
      String ioDate = rowInfo.get("ioDate");
      String je = rowInfo.get("je");
      String yhid = rowInfo.get("yhid");
      String yhzhid = rowInfo.get("yhzhid");
      String personid = rowInfo.get("personid");
      String deptid = rowInfo.get("deptid");

      String Inmoney = "";
      String outmoney = "";
      if(deptid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择部门!')"));
        return;
      }
      if(personid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择经手人!')"));
        return;
      }
      if(jsfsid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择结算方式!')"));
        return;
      }
      if(yhid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择银行!')"));
        return;
      }
      if(yhzhid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择帐户!')"));
        return;
      }
      if(je.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入金额!')"));
        return;
      }
      String temp = null;
      if((temp = checkNumber(je, "金额")) != null)
      {
        data.setMessage(temp);
        return;
      }

      if(ioflag.equals("1"))
        Inmoney = je;
      else
        outmoney = je;

      if(!isAdd)
        ds.goToInternalRow(editrow);
      if(isAdd)
      {
        ds.insertRow(false);
        accountioID = dataSetProvider.getSequence("s_cw_accountio");
        ds.setValue("accountioID", accountioID);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        ds.setValue("createdate", today);//制单日期
        ds.setValue("iodate", today);//制单日期
        ds.setValue("creator", loginName);//操作员
        ds.setValue("creatorid", loginid);
        ds.setValue("state", "0");
        ds.setValue("filialeid", fgsid);
      }
      ds.setValue("ioflag", ioflag);
      ds.setValue("jsfsid", jsfsid);
      ds.setValue("ioDate", ioDate);
      ds.setValue("ioflag", ioflag);
      ds.setValue("Inmoney", Inmoney);
      ds.setValue("outmoney", outmoney);
      ds.setValue("yhid", yhid);
      ds.setValue("yhzhid", yhzhid);
      ds.setValue("personid", personid);
      ds.setValue("deptid", deptid);
      ds.setValue("summary", rowInfo.get("summary"));
      ds.post();
      ds.saveChanges();
      rowInfo.put(ds);
      //刷新lookup的数据集，保持数据的同步.入参是相应的lookup名称
    }
  }

  /*******************************************
   *@查询操作
   ******************************************/
  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;

      zt = data.getRequest().getParameterValues("zt");
      if(!(zt==null))
      {
        StringBuffer sbzt = null;
        for(int i=0;i<zt.length;i++)
        {
          if(sbzt==null)
            sbzt= new StringBuffer(" AND state IN(");
          sbzt.append(zt[i]+",");
        }
        if(sbzt == null)
          sbzt =new StringBuffer();
        else
          sbzt.append("-99)");
        SQL = SQL+sbzt.toString();
      }
      else
       zt = new String[]{""};


      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "creatorid"), SQL});
      if(!dsB_AcountInOut.getQueryString().equals(SQL))
      {
        dsB_AcountInOut.setQueryString(SQL);
        dsB_AcountInOut.setRowMax(null);
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
      EngineDataSet master = dsB_AcountInOut;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("ioDate"), null, null, null, "a", ">="),//开票日期
        new QueryColumn(master.getColumn("ioDate"), null, null, null, "b", "<="),//开票日期
        new QueryColumn(master.getColumn("inMoney"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("inMoney"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("outMoney"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("outMoney"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("billcode"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("billcode"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("ioflag"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("state"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("stateDesc"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("creator"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("summary"), null, null, null, null, "like")
      });
      isInitQuery = true;
    }
  }
  /**
   * 删除操作的触发类
   */
  class B_AcountInOut_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.deleteRow();
      ds.saveChanges();
      //刷新lookup的数据集，保持数据的同步.入参是相应的lookup名称
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_BANK_CREDIT_CARD);
    }
  }
}