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
 * <p>Title: 贷款管理</p>
 * <p>Copyright: right reserved (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @version 1.0
 */
public final class B_ReturnMoney extends BaseAction implements Operate
{
  private static final String MASTER_SQL       = "SELECT * FROM cw_loanmx WHERE 1=1   ? order by djh";
  private static final String MASTER_STRUT_SQL = "SELECT * FROM cw_loanmx WHERE 1<>1 ";
  private static final String APPROVE_SQL      = "SELECT * FROM cw_loanmx WHERE loanmxid='?' ";//用于审批时候提取一条记录
  private static final String CALCULATE_LX     = "SELECT a.loanid,a.loanfund,a.loandate,b.loanmxid,b.retnfund,b.retndate FROM cw_loan a,cw_loanmx b WHERE a.loanid=b.loanid ? order by b.loanmxid";
  private EngineDataSet dsB_ReturnMoney = new EngineDataSet();//数据集
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
  public static final String Accountio_DEL             = "9001";
  public static final String CANCER_APPROVE            = "9003";
  public static final String SELECT_LOAN               = "9004" ;
  public static final String OVER                      = "9005";
  private String fgsid = null;   //分公司ID
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  public boolean isReport = false;
  private RowMap  m_RowInfo    = new RowMap();                 //主表添加行或修改行的引用
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsB_ReturnMoney, "cw_loanmx");
  public ArrayList arraylist_cw_acountio  = null;//
  private String loanmxid = null;
  private String ioflag = "1";
  public String []zt;
  /**
   * 从会话中得到银行信用卡信息的实例
   * @param request jsp请求
   * @return 返回银行信用卡信息的实例
   */
  public static B_ReturnMoney getInstance(HttpServletRequest request)
  {
    B_ReturnMoney B_ReturnMoneyBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_ReturnMoneyBean";
      B_ReturnMoneyBean = (B_ReturnMoney)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(B_ReturnMoneyBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        B_ReturnMoneyBean = new B_ReturnMoney();
        B_ReturnMoneyBean.fgsid = loginBean.getFirstDeptID();
        B_ReturnMoneyBean.loginid = loginBean.getUserID();
        B_ReturnMoneyBean.dsB_ReturnMoney.setColumnFormat("loanfund", B_ReturnMoneyBean.sumFormat);
        B_ReturnMoneyBean.dsB_ReturnMoney.setColumnFormat("retnfund", B_ReturnMoneyBean.sumFormat);
        B_ReturnMoneyBean.user = loginBean.getUser();
        B_ReturnMoneyBean.loginName = loginBean.getUserName();
        B_ReturnMoneyBean.user = loginBean.getUser();
        session.setAttribute(beanName, B_ReturnMoneyBean);//加入到session中
      }
    }
    return B_ReturnMoneyBean;
  }
  /**
   * 主册监听器
   * 构造函数
   */
  private B_ReturnMoney()
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
    setDataSetProperty(dsB_ReturnMoney, MASTER_STRUT_SQL);
    dsB_ReturnMoney.setSort(new SortDescriptor("", new String[]{"loanmxid"}, new boolean[]{false}, null, 0));//设置排序方式
    //dsB_ReturnMoney.setSequence(new SequenceDescriptor(new String[]{"loanmxid"}, new String[]{"S_cw_loanmx"}));//设置主健的sequence
    dsB_ReturnMoney.setSequence(new SequenceDescriptor(new String[]{"djh"}, new String[]{"SELECT pck_base.billNextCode('cw_loanmx','djh') from dual"}));

    B_ReturnMoney_Add_Edit add_edit = new B_ReturnMoney_Add_Edit();
    //addObactioner(String.valueOf(ADD), new Cw_Accounio_adddel());//新增删除操作
    addObactioner(String.valueOf(Accountio_DEL), new Cw_Accounio_adddel());//
    addObactioner(String.valueOf(OPERATE_SEARCH), new Master_Search());//定制查询
    addObactioner(String.valueOf(DELETE_RETURN), new B_ReturnMoney_Delete());//删除主表某一行,及其对应的从表
    addObactioner(String.valueOf(INIT), new B_ReturnMoney_Init());//初始化 operate=0
    addObactioner(String.valueOf(ADD), add_edit);//新增
    addObactioner(String.valueOf(EDIT), add_edit);//修改
    addObactioner(String.valueOf(Accountio_ADD), add_edit);

    addObactioner(String.valueOf(POST), new B_ReturnMoney_Post());//保存
    addObactioner(String.valueOf(DEL), new B_ReturnMoney_Delete());//删除
    addObactioner(String.valueOf(SELECT_LOAN), new Loan_Select());

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
      if(dsB_ReturnMoney.isOpen() && dsB_ReturnMoney.changesPending())
        dsB_ReturnMoney.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsB_ReturnMoney != null){
      dsB_ReturnMoney.close();
      dsB_ReturnMoney = null;
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
      rowInfo.put("ioflag", ioflag);
      rowInfo.put("loandate", today);
      rowInfo.put("retndate", today);
      rowInfo.put("alertdate", today);

    }
  }
  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    //if(!dsB_ReturnMoney.isOpen())
    //  dsB_ReturnMoney.open();
    return dsB_ReturnMoney;
  }
/**得到剩余本息**/
 public String getBX(String loanid,String lonamxid,String yearate)
 {
   String id = loanid;
   String mxid = lonamxid;
   EngineDataSet tmp = new EngineDataSet();
   setDataSetProperty(tmp,combineSQL(CALCULATE_LX,"?",new String[]{" and a.loanid='"+loanid+"' and b.loanmxid<="+mxid}));;
   tmp.openDataSet();
   String loanfund="";
   double dayrate =Double.parseDouble(yearate.equals("")?"0":yearate)/(365*100);
   double bx = 0;
   String retndate = "";
   String loandate = "";
   String retnfund ="";
   tmp.first();
   for(int i=0;i<tmp.getRowCount();i++)
   {
     if(i==0)
     {
       loanfund = tmp.getValue("loanfund");
       loandate = tmp.getValue("loandate");
     }
     retndate = tmp.getValue("retndate");
     retnfund = tmp.getValue("retnfund");
     try
     {
       Calendar  cd= new GregorianCalendar();
       Date startdate = new SimpleDateFormat("yyyy-MM-dd").parse(loandate);//提单的开单日期
       Date enddate = new SimpleDateFormat("yyyy-MM-dd").parse(retndate);
       cd.setTime(startdate);
       long s = cd.getTimeInMillis();
       cd.setTime(enddate);
       long e =cd.getTimeInMillis();
       long days = (e-s)/(60*60*24*1000);
       double dloanfund = Double.parseDouble(loanfund);
       double dretnfund = Double.parseDouble(retnfund);
       loanfund = String.valueOf(dloanfund+dloanfund*days*dayrate-dretnfund);
     }catch(Exception e){}
     loandate = retndate;
     tmp.next();
   }
   return engine.util.Format.formatNumber(loanfund,"#0.00");
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
  class B_ReturnMoney_Init implements Obactioner
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
      SQL = combineSQL(MASTER_SQL, "?", new String[]{SQL});
      dsB_ReturnMoney.setQueryString(SQL);
      dsB_ReturnMoney.setRowMax(null);
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
      detailRow.put("loanmxid", dsB_ReturnMoney.getValue("loanmxid"));//
      detailRow.put("loancode", rowInfo.get("loancode_"+i));//
      detailRow.put("loanfund", rowInfo.get("loanfund_"+i));//
      detailRow.put("retnfund", rowInfo.get("retnfund_"+i));//
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
      dsB_ReturnMoney.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      loanmxid = dsB_ReturnMoney.getValue("loanmxid");
      String content = dsB_ReturnMoney.getValue("djh");
      String deptid = dsB_ReturnMoney.getValue("deptid");
      approve.putAproveList(dsB_ReturnMoney, dsB_ReturnMoney.getRow(), "rtn_loan_list", content,deptid);
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
      dsB_ReturnMoney.setQueryString(sql);
      if(dsB_ReturnMoney.isOpen())
        dsB_ReturnMoney.readyRefresh();
      dsB_ReturnMoney.refresh();
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
      dsB_ReturnMoney.goToRow(Integer.parseInt(data.getParameter("rownum")));
      loanmxid = dsB_ReturnMoney.getValue("loanmxid");
      dsB_ReturnMoney.setValue("state","8");
      dsB_ReturnMoney.saveChanges();
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
      dsB_ReturnMoney.goToRow(row);
      dsB_ReturnMoney.setValue("state", "4");
      dsB_ReturnMoney.saveChanges();
    }
  }
  /**
   * 取消审批
   */
  class Cancer_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsB_ReturnMoney.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsB_ReturnMoney,dsB_ReturnMoney.getRow(),"rtn_loan_list");
    }
  }
  /**
   * 添加或修改操作的触发类
   */
  class B_ReturnMoney_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD))||action.equals(String.valueOf(Accountio_ADD));
      if(action.equals(String.valueOf(ADD)))
        ioflag = "1";
      else if(action.equals(String.valueOf(Accountio_ADD)))
        ioflag = "2";
      if(!isAdd)
      {
        dsB_ReturnMoney.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsB_ReturnMoney.getInternalRow();
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
        String loanmxid = dsB_ReturnMoney.getValue("loanmxid");
        dsB_ReturnMoney.insertRow(false);
        dsB_ReturnMoney.setValue("loanmxid", loanmxid);
        dsB_ReturnMoney.post();
        RowMap zg_Temp_Row = new RowMap(dsB_ReturnMoney);
        arraylist_cw_acountio.add(zg_Temp_Row);
      }
      if(action.equals(Accountio_DEL))
      {
        putDetailInfo(data.getRequest());
        int rownum=Integer.parseInt(data.getRequest().getParameter("rownum"));
        arraylist_cw_acountio.remove(rownum);
        dsB_ReturnMoney.goToRow(rownum);
        dsB_ReturnMoney.deleteRow();
      }
    }
  }
  /**
   *
   *
   * */
  class Loan_Select implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      rowInfo.put(data.getRequest());
    }
  }
  /**
   * 保存操作的触发类
   */
  class B_ReturnMoney_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
      rowInfo.put(data.getRequest());
      //String ioflag = rowInfo.get("ioflag");
      String deptid = rowInfo.get("deptid");
      //String loandate = rowInfo.get("loandate");
      String retndate = rowInfo.get("retndate");
      //String alertdate = rowInfo.get("alertdate");
      String retnfund = rowInfo.get("retnfund");
      //String yhid = rowInfo.get("yhid");
      String loandate = rowInfo.get("loandate");
      String personid = rowInfo.get("personid");
      String rtncode = rowInfo.get("rtncode");
      String loanid = rowInfo.get("loanid");
      String yearate = rowInfo.get("yearate");
      String loanfund = "";
      if(deptid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择提交部门!')"));
        return;
      }
      if(loanid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择贷款单号!')"));
        return;
      }
      if(retndate.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入还款日期!')"));
        return;
      }
      if(personid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择经手人!')"));
        return;
      }
      if(retnfund.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入还款金额!')"));
        return;
      }
      if(retndate.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入还款日期!')"));
        return;
      }
      if(rtncode.equals(""))
      {
        data.setMessage(showJavaScript("alert('请输入还款单号!')"));
        return;
      }
      String temp = null;
      if((temp = checkNumber(retnfund, "金额")) != null)
      {
        data.setMessage(temp);
        return;
      }

      try{
        Date ksdate = new SimpleDateFormat("yyyy-MM-dd").parse(loandate);
        Date jsdate = new SimpleDateFormat("yyyy-MM-dd").parse(retndate);
        if(ksdate.compareTo(jsdate)>0)
        {
          data.setMessage(showJavaScript("alert('非法还款日期!')"));
          return;
        }
        }catch(Exception e){}

      if(!isAdd)
        ds.goToInternalRow(editrow);
      if(isAdd)
      {
        ds.insertRow(false);
        loanmxid = dataSetProvider.getSequence("s_cw_loanmx");
        ds.setValue("loanmxid", loanmxid);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        ds.setValue("createdate", today);//制单日期
        //ds.setValue("iodate", today);//制单日期
        ds.setValue("creator", loginName);//操作员
        ds.setValue("creatorid", loginid);
        ds.setValue("state", "0");
        ds.setValue("filialeid", fgsid);
        String count = dataSetProvider.getSequence("select count(*) from cw_loanmx where rtncode='"+rtncode+"'");
        if(count!=null&&!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('贷款单号重复!')"));
          return;
        }
        ds.setValue("rtncode", rtncode);
      }
      //ds.setValue("ioflag", ioflag);

      //ds.setValue("loandate", loandate);
      ds.setValue("retndate", retndate);
      //ds.setValue("alertdate", alertdate);
      //ds.setValue("ioflag", "1");
      //ds.setValue("loanfund", loanfund);
      ds.setValue("retnfund", retnfund);
      //ds.setValue("yhid", yhid);
      ds.setValue("deptid", deptid);
      ds.setValue("loanid", rowInfo.get("loanid"));
      ds.setValue("personid", personid);
      //ds.setValue("creditor", creditor);
      ds.setValue("bz", rowInfo.get("bz"));
      //ds.setValue("yearate", yearate);

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

      SQL = combineSQL(MASTER_SQL, "?", new String[]{SQL});
      if(!dsB_ReturnMoney.getQueryString().equals(SQL))
      {
        dsB_ReturnMoney.setQueryString(SQL);
        dsB_ReturnMoney.setRowMax(null);
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
      EngineDataSet master = dsB_ReturnMoney;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("retndate"), null, null, null, "a", ">="),//开票日期
        new QueryColumn(master.getColumn("retndate"), null, null, null, "b", "<="),//开票日期
        //new QueryColumn(master.getColumn("loanfund"), null, null, null, "a", ">="),
        //new QueryColumn(master.getColumn("loanfund"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("retnfund"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("retnfund"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("loanid"), "VW_BEAN_LOAN", "loanid", "loancode", "loancode", "like"),
       // new QueryColumn(master.getColumn("loancode"), null, null, null, "b", "<="),
        //new QueryColumn(master.getColumn("ioflag"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("state"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("stateDesc"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("creator"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("bz"), null, null, null, null, "like")
      });
      isInitQuery = true;
    }
  }
  /**
   * 删除操作的触发类
   */
  class B_ReturnMoney_Delete implements Obactioner
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