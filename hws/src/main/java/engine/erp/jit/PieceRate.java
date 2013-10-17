package engine.erp.jit;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.project.*;
import engine.html.*;
import engine.util.*;
import engine.common.*;
import engine.erp.produce.ImportProcess;
import engine.erp.produce.B_WageFormula;
import engine.erp.produce.B_WorkLoad_Sel_Process;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 计件工作量</p>
 * <p>Description: 计件工作量<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 杨建国
 * @version 1.0
 */

public final class PieceRate extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "10001";
  public  static final String DETAIL_SELECT_PROCESS = "10021";
  public  static final String ONCHANGE = "10031";
  public  static final String GXMC_ONCHANGE = "10731";
  public  static final String DEPTCHANGE = "11731";
  public  static final String DEPTCHANGE2 = "11733";
  public  static final String GZZCHANGE = "11732";
  //public  static final String GZZCHANGE2 = "11734";
  public  static final String SINGLE_SEL_PROCESS = "10041";//单选生产加工单主表操作
  public  static final String SINGLE_SELECT_PRODUCT = "10091";//单选产品操作
  public  static final String PRODUCT_ONCHANGE = "14591";//输入产品触发事件
  public  static final String COMPLETE = "10011";//强制完成触发事件
  public  static final String REPORT = "2000";//报表追踪操作
  public  static final String CANCEL_APPROVE = "12345";//取消审批
  public  static final String DRAW_SINGLE_PROCESSTASK = "12346";//流转单引入任务单事件
  public  static final String PERSONCHANGE = "12347";//人员更改触发的事件
  public  static final String IMPORT_OTHER_BILL = "12348";//引其它单据
  public  static final String CPID_CHANGE = "12349";//
  public  static final String CPID_DELETE = "12350";//
  public  static final String COPYROW = "9997";
  public static final String GXONCHANGE = "1225";
  public  static final String PERSON_ADD        = "2003";
  public static final String PROCESS_CARD_ADD = "55555";

  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_piecewage WHERE 1<>1";
  private static final String MASTER_SQL = "SELECT * FROM sc_piecewage where ? ? ORDER BY piece_code DESC";
  private static final String MASTER_EDIT_SQL = "SELECT * FROM sc_piecewage where piecewage_ID=? ";
  //报表调用工人工作量的SQL
  private static final String MASTER_REPORT_SQL = "SELECT * FROM sc_piecewage WHERE piecewage_ID =";

  private static final String DETAIL_STRUT_SQL = "SELECT * FROM sc_piecewage_emp WHERE 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM sc_piecewage_emp WHERE piecewage_ID ='?' ";


  //工作组人员
  private static final String GZZ_RY_SQL =
      " select a.deptid ,a.gzzbh,a.gzzmc ,b.*  from sc_gzz a,sc_gzzry b where a.gzzid=b.gzzid ? order by ordernum";

  private static final String SC_JGD_SQL =
      " select a.*,b.deje,b.gylxid,c.personid  from VW_SELECT_JGD a,sc_gylxmx b,sc_gzzry c where  a.gylxmxid=b.gylxmxid(+) and a.gzzid=c.gzzid ";
  //取出某部门下的所有的人员资料
  private static final String DEPT_EMP_SQL = " SELECT * FROM emp where 1=1 ?  ";// 人员信息
  //select the data from sc_gylxmx that make the condition cpid = cpid and dmsxid = dmsxid
  private static final String SC_GX_CPID_SQL = " SELECT b.* FROM sc_gylx a, sc_gylxmx b "
      + " WHERE a.gylxid = b.gylxid ";
  //+ "  AND a.cpid = ? AND a.dmsxid = ? and b.gxfdid = ? ";
  //取出属于指定车间,工作组的人员的工资数据.

  private static final String PROCESSTASK_DETAIL_SQL = "SELECT * FROM VW_DRAW_PROCESSTASKDETAIL WHERE rwdid = '?' ";
  private static final String PIECE_SELECT_BILL = " select a.*,b.jgdmxid,b.cpid,b.sl from sc_jgd a,sc_jgdmx b where a.jgdid=b.jgdid ";

  private static final String IMPORT_OTHERBILL_CPID =  " SELECT * from  VW_SC_PIECEWAGE_OTHER_BILLCPID "
      + "  where jgdid = ? ";
  private String LOCATEGYLXID = "SELECT gylxid, cpid, dmsxid from sc_gylx where cpid = ? and dmsxid = ? ";

  //the group of gylxid and gxfdid.
  private static final  String GYLX_GXFD_GROUP_SQL =
      " SELECT a.gylxmxid, a.gylxid, a.gxfdid,  a.desl, a.deje, "
      + "  a.sfwx, a.wxjg, a.jjff FROM sc_gylxmx a "
      + " WHERE 1=1 AND gylxid = ? AND gxfdid  = ? ";
  private static final String TECHNICS_OPTION_PRODUCE_SQL = "  SELECT * FROM ( "
  //取工艺路线设置中给这个产品正常设置的工序工段
      + "    SELECT a.gylxmxid, a.gylxid, a.gxfdid, b.gymc, b.gymcID, a.desl, a.deje, a.sfwx, a.wxjg, a.jjff "
      + "    FROM sc_gylxmx a, sc_gymc b "
      + "    WHERE a.gymcID=b.gymcID AND a.gxfdid = b.gxfdid "
      + " "
      + " UNION ALL "
  //取可选替换工序
      + " SELECT a.gylxmxid, c.gylxid, a.gxfdid, b.gymc, b.gymcID, a.desl, c.deje, a.sfwx, a.wxjg, a.jjff "
      + " FROM sc_gylxmx a, sc_gymc b, sc_optionproc c "
      + " WHERE c.gymcid = b.gymcid AND a.gylxmxid = c.gylxmxid AND c.gylxid = a.gylxid  AND b.gxfdid = a.gxfdid "
      + " ) "
      + " WHERE 1=1";
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsDeptEmpInfoTable  = null;//计时工资设置表数据集
  private EngineDataSet dsImportOtherTable  = null;
  private EngineDataSet dsImportOtherCpidTable  = null;
  private LookUp technicsBean = null; //工艺路线信息的bean的引用, 用于提取工艺路线信息

  public HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sc_piecewage");
  public HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sc_piecewage_emp");
  public HtmlTableProducer table = new HtmlTableProducer(dsMasterTable, "sc_piecewage", "sc_piecewage");

  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isDetailAdd = false;    //从表是否在增加状态
  public  boolean isApprove = false;     //是否在审批状态

  public boolean isReport = false; // 从表是否在报表引用状态

  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public ImportProcess  importprocessBean = null;//引入加工单的bean的引用, 用于提取引入加工单信息
  public B_WorkLoad_Sel_Process  workloadSelProcessBean = null;//加工单主表信息BEAN的引用

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String piecewage_id = null;
  private String lx = null;
  public String SC_STORE_UNIT_STYLE = null;//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  public String SC_PRODUCE_UNIT_STYLE =null;//1=强制换算,0=仅空值时换算
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  private User user = null;

  private String importedSl = "";
  private String piecewage_ID = "";
  //private String rq = null;
  /**
   * 工作量列表（按工人输入）的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回工作量列表（按工人输入）的实例
   */
  public static PieceRate getInstance(HttpServletRequest request)
  {
    PieceRate pieceRateBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "PieceRate";
      pieceRateBean = (PieceRate)session.getAttribute(beanName);
      if(pieceRateBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        pieceRateBean = new PieceRate();
        pieceRateBean.qtyFormat = loginBean.getQtyFormat();
        pieceRateBean.sumFormat = loginBean.getSumFormat();
        pieceRateBean.priceFormat = loginBean.getPriceFormat();

        pieceRateBean.fgsid = loginBean.getFirstDeptID();
        pieceRateBean.loginDept = loginBean.getDeptID();
        pieceRateBean.loginId = loginBean.getUserID();
        pieceRateBean.loginName = loginBean.getUserName();
        pieceRateBean.user = loginBean.getUser();
        pieceRateBean.SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和换算单位是否强制换算
        pieceRateBean.SC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam("SC_PRODUCE_UNIT_STYLE");//计量单位和生产单位是否强制换算
        pieceRateBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        pieceRateBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        //pieceRateBean.lx = "1";
        //设置格式化的字段
        pieceRateBean.dsDetailTable.setColumnFormat("proc_num", pieceRateBean.qtyFormat);
        pieceRateBean.dsDetailTable.setColumnFormat("piece_num", pieceRateBean.qtyFormat);
        //pieceRateBean.dsDetailTable.setColumnFormat("piece_price", pieceRateBean.priceFormat);
        pieceRateBean.dsDetailTable.setColumnFormat("piece_wage", pieceRateBean.sumFormat);
        //pieceRateBean.dsMasterTable.setColumnFormat("zjjgz", pieceRateBean.priceFormat);
        session.setAttribute(beanName, pieceRateBean);
      }
    }
    return pieceRateBean;
  }

  /**
   * 构造函数
   */
  private PieceRate()
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
  private final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsMasterTable, MASTER_STRUT_SQL);
    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"piece_code"}, new String[]{"SELECT pck_base.billNextCode('sc_piecewage','piece_code') from dual"}));
    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"tdbh"}, new String[]{"SELECT pck_base.billNextCode('xs_td.t','tdbh') from dual"}));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"pieceemp_ID"}, new String[]{"s_sc_piecewage_emp"}));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(DETAIL_SELECT_PROCESS), new Detail_Select_Process());
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    //addObactioner(String.valueOf(SINGLE_SEL_PROCESS), new Single_Select_Process());//单选加工单主表
    addObactioner(String.valueOf(SINGLE_SELECT_PRODUCT), new Single_Product_Add());//单选产品
    addObactioner(String.valueOf(COMPLETE), new Complete());//强制完成事件
    addObactioner(String.valueOf(REPORT), new Report());//报表引用事件
    addObactioner(String.valueOf(GZZCHANGE), new GzzChange());//工作组改变事件
    addObactioner(String.valueOf(DEPTCHANGE), new DeptChange());//工作组改变事件
    // addObactioner(String.valueOf(GZZCHANGE2), new GzzChange());//工作组改变事件
    addObactioner(String.valueOf(DEPTCHANGE2), new DeptChange());//工作组改变事件
    //addObactioner(String.valueOf(PERSONCHANGE), new PersonChage());//工作组改变事件
    addObactioner(String.valueOf(IMPORT_OTHER_BILL), new Import_Other_Bill());//工作组改变事件
    addObactioner(String.valueOf(CPID_CHANGE), new Cpid_Change());//工作组改变事件
    addObactioner(String.valueOf(CPID_DELETE), new Cpid_Delete());//工作组改变事件
    addObactioner(COPYROW, new Copy_CurrentRow());
    addObactioner(GXONCHANGE, new GxOnChange());
    addObactioner(PROCESS_CARD_ADD, new Master_Add());
    addObactioner(String.valueOf(PERSON_ADD), new Detail_Multi_ADD());

    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(REPORT), new Approve());
    addObactioner(String.valueOf(CANCEL_APPROVE), new Cancel_Approve());//取消审批
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
        if(data == null)
          return showMessage("无效操作", false);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsMasterTable.isOpen() && dsMasterTable.changesPending())
        dsMasterTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsMasterTable != null){
      dsMasterTable.close();
      dsMasterTable = null;
    }
    if(dsDetailTable != null){
      dsDetailTable.close();
      dsDetailTable = null;
    }
    if(dsDeptEmpInfoTable != null){
      dsDeptEmpInfoTable.close();
      dsDeptEmpInfoTable = null;
    }

    if(dsImportOtherTable != null){
      dsImportOtherTable.close();
      dsImportOtherTable = null;
    }

    if(dsImportOtherCpidTable != null){
      dsImportOtherCpidTable.close();
      dsImportOtherCpidTable = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
    if(masterProducer != null)
    {
      masterProducer.release();
      masterProducer = null;
    }
    if(detailProducer != null)
    {
      detailProducer.release();
      detailProducer = null;
    }
  }

  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }

  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否是主表
    if(isMaster){
      if(isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();

      if(!isAdd)
        m_RowInfo.put(getMaterTable());
      else
      {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        m_RowInfo.put("piece_date", today);
        m_RowInfo.put("isImport", "0");
        m_RowInfo.put("zdrq", today);//制单日期
        m_RowInfo.put("zdr", loginName);//操作员
        m_RowInfo.put("zdrid", loginId);

      }
    }
    else
    {
      EngineDataSet dsDetail = dsDetailTable;
      if(d_RowInfos == null)
        d_RowInfos = new ArrayList(dsDetail.getRowCount());
      else if(isInit)
        d_RowInfos.clear();

      dsDetail.first();
      for(int i=0; i<dsDetail.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsDetail);
        row.put("InternalRow", String.valueOf(dsDetail.getInternalRow()));
        d_RowInfos.add(row);
        dsDetail.next();
      }
      for(int l=0; l<d_RowInfos.size();l++)
      {
        RowMap row = (RowMap)d_RowInfos.get(l);
        String ss = row.get("gylxmxid");
        row = (RowMap)d_RowInfos.get(l);
      }
    }
  }
  /**
   *改变工作组的触发事件:
   * 1. 须要把所有的此工作组中的人员取出来.当成明细资料插入到明细数据集中
   *  1.1 取出在工资设置功能中设置好的此车间此人员工资数据.
   */
  class GzzChange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      RowMap rowInfo = getMasterRowinfo();
      detail.deleteAllRows();
      d_RowInfos.clear();
      String deptid = rowInfo.get("deptid");
      String gzzid =  rowInfo.get("gzzid");
      String cpid  = rowInfo.get("cpid");
      //String processmxid  = rowInfo.get("processmxid");
      EngineDataSet tmp = new EngineDataSet();
      setDataSetProperty(tmp,combineSQL(GZZ_RY_SQL,"?",new String[]{" and b.gzzid='"+gzzid+"'"}));
      tmp.open();
      tmp.first();
      for(int i=0,n=tmp.getRowCount();i<n;i++)
      {
        detail.insertRow(false);
        detail.setValue("personid",tmp.getValue("personid"));
        detail.setValue("pieceemp_ID","-1");
        //detail.setValue("piece_price",tmp.getValue("deje"));
        //detail.setValue("piece_num",tmp.getValue("sl"));
        //detail.setValue("jgdmxid",tmp.getValue("jgdmxid"));

        //String deje = tmp.getValue("deje").equals("")?"0":tmp.getValue("deje");
        //String sl =tmp.getValue("sl").equals("")?"0":tmp.getValue("sl");

        //detail.setValue("piece_wage", String.valueOf(Double.parseDouble(sl)*Double.parseDouble(deje)));


        detail.post();
        RowMap detailrow = new RowMap(detail);
        d_RowInfos.add(detailrow);
        tmp.next();
      }
      /*
      if (!String.valueOf(GZZCHANGE).equals(action))
      {
      //m_RowInfo.put("cpid", "");
        m_RowInfo.put("djid", "");
      //m_RowInfo.put("dmsxid", "");
        m_RowInfo.put("djh", "");
        m_RowInfo.put("piece_num", "");
        m_RowInfo.put("proc_num", "");
        m_RowInfo.put("cjlzdmxID", "");
      //m_RowInfo.put("receiveDetailID", "");
      }
      else
      {
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String piecewage_ID = dsMasterTable.getValue("piecewage_ID");
        String deptid = rowInfo.get("deptid");
        String gzzid =  rowInfo.get("gzzid");
        String cpid  = rowInfo.get("cpid");
        String dmsxid = rowInfo.get("dmsxid");
        String gylxid = rowInfo.get("gylxid");
        String gxfdid = rowInfo.get("gxfdid");
        produceDeatiWorkloadlData(cpid, dmsxid, gylxid, gxfdid);
      }
      */
    }
  }
 /*
  *  选择个人触发的事件须要取得页面上部门和工作组的值.依此条件找出person的数据.
  *

  class PersonChage implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      RowMap detailrowUI = null;
      int row = Integer.parseInt(data.getParameter("rownum"));
      detailrowUI = (RowMap)d_RowInfos.get(row);
      EngineDataSet detail = getDetailTable();
      EngineDataSet dspersonHourWageInfoTable = null;//零时任务单从表信息数据集
      RowMap rowInfo = getMasterRowinfo();
      String deptid = rowInfo.get("deptid");
      String gzzid =  rowInfo.get("gzzid");
      String personid =  detailrowUI.get("personid");
      personid = personid.equals("")?"":" and e.personid = " + personid;
      deptid = deptid.equals("")?"":" and e.deptid = " + deptid;
      gzzid =  gzzid.equals("")?"":" and c.gzzid = " + gzzid;
      String SQL = deptid + gzzid + personid;
      SQL = combineSQL(SC_GZZ_PERSON_SQL, "?", new String[]{SQL});
      if(dspersonHourWageInfoTable==null)
      {
        dspersonHourWageInfoTable = new EngineDataSet();
        setDataSetProperty(dspersonHourWageInfoTable,null);
      }
      dspersonHourWageInfoTable.setQueryString(SQL);
      if(!dspersonHourWageInfoTable.isOpen())
        dspersonHourWageInfoTable.openDataSet();
      else
        dspersonHourWageInfoTable.refresh();
      detail.deleteAllRows();
      d_RowInfos.clear();
      dspersonHourWageInfoTable.first();
      for (int i=0;i<dspersonHourWageInfoTable.getRowCount();i++)
      {
        //String personid = dspersonHourWageInfoTable.getValue("personid");
        String work_price = dspersonHourWageInfoTable.getValue("hour_wage");
        String over_price = dspersonHourWageInfoTable.getValue("over_wage");
        String night_price = dspersonHourWageInfoTable.getValue("night_wage");
        detail.insertRow(false);
        detail.setValue("houremp_ID","-1");
        detail.setValue("personid",personid);
        detail.setValue("work_price",work_price);
        detail.setValue("over_price",over_price);
        detail.setValue("night_price",night_price);
        detail.post();
        RowMap detailrow = new RowMap(dsDetailTable);
        d_RowInfos.add(detailrow);
        dspersonHourWageInfoTable.next();
      }
    }
  }
 */
  /*
  * 部门更改触发事件.
  * 1.首先判断是否此部门下面有工作组.
  * 2.如有工作组则不做任何动作.等待使用者选择工作组
  * 3.如没有则把此车间下的所有人员的信息全取过来.
   */
  class DeptChange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      detail.deleteAllRows();
      d_RowInfos.clear();
      //当不是由引其它单据触发的时候清空主表的行数据
      if (!String.valueOf(DEPTCHANGE2).equals(action))
      {
        m_RowInfo.put("cpid", "");
        m_RowInfo.put("djid", "");
        m_RowInfo.put("dmsxid", "");
        m_RowInfo.put("djh", "");
        m_RowInfo.put("piece_num", "");
        m_RowInfo.put("proc_num", "");
        m_RowInfo.put("cjlzdmxID", "");
        m_RowInfo.put("receiveDetailID", "");
      }//操作符是DEPECHANGE2的时候,即:引入其它单据事件的时候做数据集操作
      else
      {
        EngineDataSet dspersonHourWageInfoTable = null;//零时任务单从表信息数据集
        RowMap rowInfo = getMasterRowinfo();
        String deptid = rowInfo.get("deptid");
        deptid = deptid.equals("")?"":" and deptid = " + deptid;
        String gzzid = "";//rowInfo.get("gzzid");
        String cpid = rowInfo.get("cpid");
        String dmsxid = rowInfo.get("dmsxid");
        String gxfdid = rowInfo.get("gxfdid");
        String gylxid = rowInfo.get("gylxid");
        String SQL = " select count(*) from sc_gzz where  1=1 ? ";
        SQL = combineSQL(SQL, "?", new String[]{deptid});
        String count = dataSetProvider.getSequence(SQL);
        //如果车间下面没有工作组的话.那么就取整个车间的人员作为明细数据插入到明细数据集中
        /*
           business logic alteration: the detail dataset data would be
             collected from sc_gylxmx that make this condition:
             sc_gylxmx.cpid = m_rowInfo.cpid and sc_gylxmx.dmsxid = m_rowInfo.dmsxid
           the logic search data before this alteration is collect workers belong to workshop
           and insert into detail dataset
         */
        if(count.equals("0"))
        {
          SQL = !cpid.equals("")?" and b.cpid = "+ cpid:"";
          SQL += !dmsxid.equals("")?" and b.dmsxid = "+ dmsxid:"";
          SQL += !gxfdid.equals("")?" and a.gxfdid = "+ gxfdid:"";
          SQL += !gylxid.equals("")?" and a.gylxid = "+ gylxid:"";
          SQL = combineSQL(SC_GX_CPID_SQL, "?", new String[]{cpid, dmsxid});
          if(dsDeptEmpInfoTable==null)
          {
            dsDeptEmpInfoTable = new EngineDataSet();
            setDataSetProperty(dsDeptEmpInfoTable,null);
          }
          dsDeptEmpInfoTable.setQueryString(SQL);
          if(!dsDeptEmpInfoTable.isOpen())
            dsDeptEmpInfoTable.openDataSet();
          else
            dsDeptEmpInfoTable.refresh();
          detail.deleteAllRows();
          d_RowInfos.clear();
          dsDeptEmpInfoTable.first();
          for (int i=0;i<dsDeptEmpInfoTable.getRowCount();i++)
          {
            String gymcID = dsDeptEmpInfoTable.getValue("gymcID");
            String jjff = dsDeptEmpInfoTable.getValue("jjff");
            String deje = dsDeptEmpInfoTable.getValue("deje");

            String personid = dsDeptEmpInfoTable.getValue("personid");
            detail.insertRow(false);
            detail.setValue("pieceemp_ID","-1");
            //detail.setValue("personid",personid);
            detail.setValue("piece_type",jjff);
            detail.setValue("deje",deje);

            detail.post();
            RowMap detailrow = new RowMap(detail);
            d_RowInfos.add(detailrow);
            dsDeptEmpInfoTable.next();
          }
        }
      }
      data.setMessage(showJavaScript("deptChange()"));
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
    RowMap rowInfo = getMasterRowinfo();
    //保存网页的所有信息
    rowInfo.put(request);

    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("personid", rowInfo.get("personid_"+i));//产品
      detailRow.put("piece_num", formatNumber(rowInfo.get("piece_num_"+i), qtyFormat));//计时工时
      detailRow.put("proc_num", formatNumber(rowInfo.get("proc_num_"+i), priceFormat));//计时单价
      detailRow.put("piece_wage", formatNumber(rowInfo.get("piece_wage_"+i), sumFormat));//计时金额
      detailRow.put("piece_price", rowInfo.get("piece_price_"+i));//加班工时
      detailRow.put("work_proc", rowInfo.get("work_proc_"+i));//加班单价
      detailRow.put("piece_type", rowInfo.get("piece_type_"+i));//加班金额
      detailRow.put("gylxmxid", rowInfo.get("gylxmxid_"+i));//加班金额
      detailRow.put("v_work_proc", rowInfo.get("v_work_proc_"+i));//加班金额

      detailRow.put("bt", rowInfo.get("bt_"+i));
      detailRow.put("ccj", rowInfo.get("ccj_"+i));
      detailRow.put("zhj", rowInfo.get("zhj_"+i));
      detailRow.put("bf", rowInfo.get("bf_"+i));
      detailRow.put("qq", rowInfo.get("qq_"+i));
      detailRow.put("zhf", rowInfo.get("zhf_"+i));
      detailRow.put("zgz", rowInfo.get("zgz_"+i));
      detailRow.put("bz", rowInfo.get("bz_"+i));

    }
  }

  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /*得到从表表对象*/
  public final EngineDataSet getDetailTable(){
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    return dsDetailTable;
  }

  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    piecewage_ID = dsMasterTable.getValue("piecewage_ID");
    String SQL = isMasterAdd ? "-1" : piecewage_ID;
    SQL = combineSQL(DETAIL_SQL, "?", new String[]{SQL});

    dsDetailTable.setQueryString(SQL);
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    else
      dsDetailTable.refresh();
  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }

  /**
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  public final boolean masterIsAdd() {return isMasterAdd; }

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
   * 得到选中的行的行数
   * @return 若返回-1，表示没有选中的行
   */
  public final int getSelectedRow()
  {
    if(masterRow < 0)
      return -1;

    dsMasterTable.goToInternalRow(masterRow);
    return dsMasterTable.getRow();
  }
  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isReport = false;
      isApprove = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
      table.getWhereInfo().clearWhereValues();
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      isDetailAdd = false;
      //
      //初始化时不显示已完成的单据
      String SQL = "";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid")});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      B_WageFormula.getInstance(request).readyExpressions();
    }
  }

  /**
   * 显示从表的列表信息
   */
  class ShowDetail implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterTable.getInternalRow();
      //gzlid = dsMasterTable.getValue("gzlid");
      //打开从表
      openDetailTable(false);
    }
  }

  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isMasterAdd = String.valueOf(ADD).equals(action);
      isDetailAdd = false;
      isReport = false;
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        piecewage_ID = dsMasterTable.getValue("piecewage_ID");
        //cjlzdID = dsMasterTable.getValue("gzlid");
        //personid = dsMasterTable.getValue("personid");
        //rq = dsMasterTable.getValue("rq");
      }
      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);

      data.setMessage(showJavaScript("toDetail();"));
    }
  }
  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();


      isReport = false;
      String deptid = req.getParameter("deptid");
      String cpid = req.getParameter("cpid");
      //String processmxid = req.getParameter("processmxid");
      String gymcid = req.getParameter("gymcid");
      String gylxmxid = req.getParameter("gylxmxid");
      String gxfdid = req.getParameter("gxfdid");



      String SQL = " and deptid='"+deptid+"' and cpid ='"+cpid;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),SQL});
      if(dsMasterTable.isOpen())
        dsMasterTable.close();
      setDataSetProperty(dsMasterTable,SQL);
      dsMasterTable.open();
      dsMasterTable.first();
      synchronized(dsDetailTable){
        String piecewage_ID = dsMasterTable.getValue("piecewage_ID");
        isMasterAdd = piecewage_ID.equals("")?true:false;
        String sql = piecewage_ID.equals("") ? "-1" : piecewage_ID;
        SQL = combineSQL(DETAIL_SQL, "?", new String[]{sql});
        dsDetailTable.setQueryString(SQL);
        if(!dsDetailTable.isOpen())
          dsDetailTable.open();
        else
          dsDetailTable.refresh();
      }
      masterProducer.init(req, loginId);
      detailProducer.init(req, loginId);

      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      m_RowInfo.put("deptid",deptid);
      m_RowInfo.put("cpid",cpid);
      m_RowInfo.put("gymcid",gymcid);
      //m_RowInfo.put("processmxid",processmxid);
      m_RowInfo.put("gylxmxid",gylxmxid);
      m_RowInfo.put("gxfdid",gxfdid);
    }
  }
  /**
   * 报表调用工人工作量操作的触发类
   */
  class Report implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isReport = true;
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      String id = request.getParameter("piecewage_id");
      String SQL = MASTER_REPORT_SQL+id;
      dsMasterTable.setQueryString(SQL);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.open();

      piecewage_id = dsMasterTable.getValue("piecewage_id");
      B_WageFormula.getInstance(request).readyExpressions();
      //打开从表
      openDetailTable(false);

      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
    }
  }

  /**
   * 主表保存操作的触发类
   */
  class Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());

      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      //校验表单数据
      String temp = checkMasterInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);


      if(isMasterAdd){
        ds.insertRow(false);
        piecewage_ID = dataSetProvider.getSequence("s_sc_piecewage");
        ds.setValue("piecewage_ID", piecewage_ID);
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("piecewage_ID", piecewage_ID);
        double sl = detailrow.get("sl").length()>0 ? Double.parseDouble(detailrow.get("sl")) : 0;
        double piece_num = detailrow.get("piece_num").length()>0 ? Double.parseDouble(detailrow.get("piece_num")) : 0;
        double proc_num = detailrow.get("proc_num").length()>0 ? Double.parseDouble(detailrow.get("proc_num")) : 0;
        double piece_price = detailrow.get("piece_price").length()>0 ? Double.parseDouble(detailrow.get("piece_price")) : 0;
        //计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
        String piece_type = detailrow.get("piece_type");
        piece_type = piece_type.equals("")?"0":piece_type;
        double piece_wage = piece_num*piece_price;
        String personid = detailrow.get("personid");
        String work_proc = detailrow.get("work_proc");
        String gylxmxid = detailrow.get("gylxmxid");

        /*String piece_num = detailrow.get("piece_num");
        String proc_num  = detailrow.get("proc_num");
        String piece_wage = detailrow.get("piece_wage");
        String piece_price = detailrow.get("piece_price");
        */
        detail.setValue("personid" , personid);
        detail.setValue("work_proc",work_proc);
        detail.setValue("piece_num", String.valueOf(piece_num));
        detail.setValue("proc_num", String.valueOf(proc_num));
        detail.setValue("piece_wage", String.valueOf(piece_wage));
        detail.setValue("piece_price", String.valueOf(piece_price));
        detail.setValue("piece_type", piece_type);
        detail.setValue("gylxmxid", gylxmxid);

        double bt = detailrow.get("bt").length()>0 ? Double.parseDouble(detailrow.get("bt")) : 0;
        double ccj = detailrow.get("ccj").length()>0 ? Double.parseDouble(detailrow.get("ccj")) : 0;
        double zhj = detailrow.get("zhj").length()>0 ? Double.parseDouble(detailrow.get("zhj")) : 0;

        double bf = detailrow.get("bf").length()>0 ? Double.parseDouble(detailrow.get("bf")) : 0;
        double qq = detailrow.get("sl").length()>0 ? Double.parseDouble(detailrow.get("qq")) : 0;
        double zhf = detailrow.get("zhf").length()>0 ? Double.parseDouble(detailrow.get("zhf")) : 0;

        double zgz = piece_wage+ccj+bt+zhj-bf-qq-zhf;

        detail.setValue("bt", detailrow.get("bt"));
        detail.setValue("ccj", detailrow.get("ccj"));
        detail.setValue("zhj", detailrow.get("zhj"));

        detail.setValue("bf", detailrow.get("bf"));
        detail.setValue("qq", detailrow.get("qq"));
        detail.setValue("zhf", detailrow.get("zhf"));

        detail.setValue("zgz", String.valueOf(zgz));

        detail.setValue("bz", detailrow.get("bz"));



        detail.post();
        detail.next();
      }
      //保存主表数据



      ds.setValue("deptid", rowInfo.get("deptid"));//车间
      ds.setValue("piece_date", rowInfo.get("piece_date"));//日期
      ds.setValue("gzzid", rowInfo.get("gzzid"));//完工车间班组
      ds.setValue("piece_num", rowInfo.get("piece_num"));//数量
      ds.setValue("proc_num", rowInfo.get("proc_num"));
      ds.setValue("cpid", rowInfo.get("cpid"));
      ds.setValue("gxfdid", rowInfo.get("gxfdid"));
      ds.setValue("gylxmxid", rowInfo.get("gylxmxid"));
      ds.setValue("gymcid", rowInfo.get("gymcid"));
      //ds.setValue("processmxid", rowInfo.get("processmxid"));
      ds.setValue("jgdid", rowInfo.get("jgdid"));
      ds.setValue("gylxid", rowInfo.get("gylxid"));
      ds.setValue("bz", rowInfo.get("bz"));

      ds.setValue("zt", "0");
      String djkind = rowInfo.get("djkind");
      String cjlzdmxID = rowInfo.get("cjlzdmxID");
      String receiveDetailID = rowInfo.get("receiveDetailID");

      //djkind=0车间流转单.=1自制收货单
      if (!cjlzdmxID.equals(""))
        ds.setValue("cjlzdmxID", cjlzdmxID);
      else if (!receiveDetailID.equals(""))
        ds.setValue("receiveDetailID", receiveDetailID);


      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);

      isMasterAdd = false;
      masterRow = ds.getInternalRow();//2004-3-30 14:53 新增 如果是保存按钮行为完成后定位数据集 yjg
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);

    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      ArrayList list = new ArrayList(d_RowInfos.size());
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        String personid = detailrow.get("personid");
        if(personid.equals(""))
          return showJavaScript("alert('第"+row+"行人员不能为空')");

        //String work_proc = detailrow.get("work_proc");
        //if(work_proc.equals(""))
        //  return showJavaScript("alert('第"+row+"请选择工序')");

        String piece_num = detailrow.get("piece_num");
        if(!piece_num.equals("")&&(temp = checkNumber(piece_num, "第"+row+"行数量")) != null)
          return temp;
        //if(piece_num.length()>0 && piece_num.equals("0"))
        //  return showJavaScript("alert('第"+row+"行数量不能为零！');");

        String bt = detailrow.get("bt");
        if(!bt.equals("")&&(temp = checkNumber(bt, "第"+row+"行补贴")) != null)
          return temp;
        String ccj = detailrow.get("ccj");
        if(!ccj.equals("")&&(temp = checkNumber(ccj, "第"+row+"行超产奖")) != null)
          return temp;
        String zhj = detailrow.get("zhj");
        if(!zhj.equals("")&&(temp = checkNumber(zhj, "第"+row+"行综合奖")) != null)
          return temp;
        String bf = detailrow.get("bf");
        if(!bf.equals("")&&(temp = checkNumber(bf, "第"+row+"行报废")) != null)
          return temp;
        String qq = detailrow.get("qq");
        if(!qq.equals("")&&(temp = checkNumber(qq, "第"+row+"行缺勤")) != null)
          return temp;
        String zhf = detailrow.get("zhf");
        if(!zhf.equals("")&&(temp = checkNumber(zhf, "第"+row+"行综合罚")) != null)
          return temp;
        /*String proc_num = detailrow.get("proc_num");
        if((temp = checkNumber(proc_num, "第"+row+"行生产数量")) != null)
          return temp;
        if(proc_num.length()>0 && proc_num.equals("0"))
          return showJavaScript("alert('第"+row+"行生产数量不能为零！');");
        */

        String piece_price = detailrow.get("piece_price");
        if(!piece_price.equals("")&&(temp = checkNumber(piece_price, "第"+row+"行计价单价")) != null)
          return temp;
        //if(piece_price.length()>0 && piece_price.equals("0"))
        // return showJavaScript("alert('第"+row+"行计价单价不能为零！');");

       /*String piece_wage = detailrow.get("piece_wage");
       if((temp = checkNumber(piece_wage, "第"+row+"行计价工资")) != null)
         return temp;
       if(piece_wage.length()>0 && piece_wage.equals("0"))
          return showJavaScript("alert('第"+row+"行计价工资不能为零！');");
      */
      }
      return null;
    }

    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo() throws Exception
    {
      RowMap rowInfo = getMasterRowinfo();
      String temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择车间！');");
      String tempgzz = rowInfo.get("gzzid");
      if(tempgzz.equals(""))
        return showJavaScript("alert('请选择工作班组！');");

      String piece_date = rowInfo.get("piece_date");
      if(piece_date.equals(""))
        return showJavaScript("alert('日期不能为空！');");
      else if(!isDate(piece_date))
        return showJavaScript("alert('非法日期！');");

      //String piece_num = rowInfo.get("piece_num");
      //if((temp = checkNumber(piece_num, "数量")) != null)
      //  return temp;
      //if(piece_num.length()>0 && piece_num.equals("0"))
      //  return showJavaScript("alert('数量不能为零！');");

      /*String proc_num = rowInfo.get("proc_num");
      if((temp = checkNumber(proc_num, "总生产数量")) != null)
        return temp;
      if(proc_num.length()>0 && proc_num.equals("0"))
        return showJavaScript("alert('总生产数量不能为零！');");
      */

      String cpid = rowInfo.get("cpid");
      if(cpid.equals(""))
        return showJavaScript("alert('请引用加工单！');");

      //String cjlzdmxID = rowInfo.get("cjlzdmxID");
      //String receiveDetailID = rowInfo.get("receiveDetailID");

      //if(cjlzdmxID.equals("")&&receiveDetailID.equals(""))
      //  return showJavaScript("alert('其它单据号(自制收货单, 车间流转单明细ID)不能为空！');");

      return null;
    }
  }

  /**
   * 主表删除操作
   */
  class Master_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(isMasterAdd){
        data.setMessage(showJavaScript("backList();"));
        return;
      }
      EngineDataSet ds = getMaterTable();
      ds.goToInternalRow(masterRow);
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
      //
      d_RowInfos.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }

  /**
   *  查询操作
   */
  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      table.getWhereInfo().setWhereValues(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"), SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
    }

    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet master = dsMasterTable;
      EngineDataSet detail = dsMasterTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("cjlzdh"), null, null, null),
        new QueryColumn(master.getColumn("wgrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("wgrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("jsrq"), null, null, null, "c", ">="),
        new QueryColumn(master.getColumn("jsrq"), null, null, null, "d", "<="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "="),//状态
        new QueryColumn(master.getColumn("cjlzdid"), "VW_CJLZD_QUERY", "cjlzdid", "gg", "gg", "like"),//从表品名
        new QueryColumn(master.getColumn("cjlzdid"), "VW_CJLZD_QUERY", "cjlzdid", "cpbm", "cpbm", "="),//从表产品编码
        new QueryColumn(master.getColumn("cjlzdid"), "VW_CJLZD_QUERY", "cjlzdid", "product", "product", "like"),//从表品名
      });
      isInitQuery = true;
    }
  }
  /**
   *  根据加工单从表增加操作
   */
  class Detail_Select_Process implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String mutiprocess = m_RowInfo.get("mutiprocess");
      if(mutiprocess.length() == 0)
        return;

      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdmxid");
      String[] jgdmxID = parseString(mutiprocess,",");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String gzlid = dsMasterTable.getValue("gzlid");
      for(int i=0; i < jgdmxID.length; i++)
      {
        if(jgdmxID[i].equals("-1"))
          continue;
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, jgdmxID[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap importProcessRow = getProcessGoodsBean(req).getLookupRow(jgdmxID[i]);
          double sl = importProcessRow.get("sl").length()>0 ? Double.parseDouble(importProcessRow.get("sl")) : 0;
          //double hsbl = importProcessRow.get("hsbl").length()>0 ? Double.parseDouble(importProcessRow.get("hsbl")) : 0;
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("gzlmxid", "-1");
          dsDetailTable.setValue("jgdmxid",jgdmxID[i]);
          dsDetailTable.setValue("cpid", importProcessRow.get("cpid"));
          dsDetailTable.setValue("sl", importProcessRow.get("sl"));
          //dsDetailTable.setValue("hssl", formatNumber(String.valueOf(hsbl==0 ? 0 : sl/hsbl), qtyFormat));
          dsDetailTable.setValue("gzlid", isMasterAdd ? "-1" : gzlid);
          dsDetailTable.setValue("dmsxid", importProcessRow.get("dmsxid"));
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
        data.setMessage(showJavaScript("big_change()"));
      }
    }
  }
  /**
   * 从表增加操作（单选产品）
   */
  class Single_Product_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      int row = Integer.parseInt(data.getParameter("rownum"));
      String singleIdInput = m_RowInfo.get("singleIdInput_"+row);
      if(singleIdInput.equals(""))
        return;

      //实例化查找数据集的类
      String cpid = singleIdInput;
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String gzlid = dsMasterTable.getValue("gzlid");
      dsDetailTable.goToRow(row);
      RowMap detailrow = null;
      detailrow = (RowMap)d_RowInfos.get(row);
      detailrow.put("gzlmxid", "-1");
      detailrow.put("cpid", cpid);
      detailrow.put("gzlid", isMasterAdd ? "-1" : gzlid);
    }
  }

  class Import_Other_Bill implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String jgdid = m_RowInfo.get("jgdid");
      if(jgdid.equals(""))
        return;
      String SQL = SC_JGD_SQL+" and a.jgdid='"+jgdid+"' order by c.ordernum";


      EngineDataSet detail = getDetailTable();
      detail.deleteAllRows();
      d_RowInfos.clear();
      isMasterAdd = true;


      EngineDataSet tmp = new EngineDataSet();
      setDataSetProperty(tmp,SQL);
      tmp.open();
      tmp.first();
      for(int i=0,n=tmp.getRowCount();i<n;i++)
      {

        detail.insertRow(false);
        detail.setValue("personid",tmp.getValue("personid"));
        detail.setValue("pieceemp_ID","-1");
        detail.setValue("piece_price",tmp.getValue("deje"));
        //detail.setValue("piece_num",tmp.getValue("sl"));
        //detail.setValue("jgdmxid",tmp.getValue("jgdmxid"));

        //String deje = tmp.getValue("deje").equals("")?"0":tmp.getValue("deje");
        //String sl =tmp.getValue("sl").equals("")?"0":tmp.getValue("sl");

        //detail.setValue("piece_wage", String.valueOf(Double.parseDouble(sl)*Double.parseDouble(deje)));

        m_RowInfo.put("deptid",tmp.getValue("deptid"));
        m_RowInfo.put("cpid",tmp.getValue("cpid"));
        m_RowInfo.put("gymcid",tmp.getValue("gymcid"));
        //m_RowInfo.put("processmxid",tmp.getValue("processmxid"));
        m_RowInfo.put("gylxmxid",tmp.getValue("gylxmxid"));
        m_RowInfo.put("gzzid",tmp.getValue("gzzid"));
        m_RowInfo.put("gxfdid",tmp.getValue("gxfdid"));
        m_RowInfo.put("gylxid",tmp.getValue("gylxid"));
        m_RowInfo.put("proc_num",tmp.getValue("zsl"));

        detail.post();
        RowMap detailrow = new RowMap(detail);
        d_RowInfos.add(detailrow);
        tmp.next();
      }
    }
  }
  class Cpid_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String piecewage_ID = dsMasterTable.getValue("piecewage_ID");
      //int row = Integer.parseInt(data.getParameter("rownum"));
      String jgdid = m_RowInfo.get("jgdid");
      String oldcpid = m_RowInfo.get("cpid");
      String olddmsxid = m_RowInfo.get("olddmsxid");
      if(jgdid.equals(""))
        return;
      String SQL = combineSQL(IMPORT_OTHERBILL_CPID, "?", new String[]{jgdid});
      if(dsImportOtherCpidTable==null)
      {
        dsImportOtherCpidTable = new EngineDataSet();
        setDataSetProperty(dsImportOtherCpidTable,null);
      }
      dsImportOtherCpidTable.setQueryString(SQL);
      if(!dsImportOtherCpidTable.isOpen())
        dsImportOtherCpidTable.openDataSet();
      else
        dsImportOtherCpidTable.refresh();
      //实例化查找数据集的类
      jgdid = dsImportOtherCpidTable.getValue("jgdid");
      String djkind = dsImportOtherCpidTable.getValue("djkind");
      String cpid = dsImportOtherCpidTable.getValue("cpid");
      String sl = dsImportOtherCpidTable.getValue("sl");
      importedSl = sl;//get other bill sl
      String scsl = dsImportOtherCpidTable.getValue("scsl");
      String gxfdid = dsImportOtherCpidTable.getValue("gxfdid");
      String dmsxid = dsImportOtherCpidTable.getValue("dmsxid");
      String gylxid = "";
      String locateGylxid = combineSQL(LOCATEGYLXID, "?", new String[]{cpid, dmsxid});
      gylxid = dataSetProvider.getSequence(locateGylxid);;
      if (djkind.equals("0"))
      {
        m_RowInfo.put("cjlzdmxID",jgdid);
        m_RowInfo.put("receiveDetailID","");
      }
      else
      {
        m_RowInfo.put("receiveDetailID",jgdid);
        m_RowInfo.put("cjlzdmxID","");
      }
      m_RowInfo.put("cpid",cpid);
      m_RowInfo.put("piece_num",sl);
      m_RowInfo.put("proc_num",scsl);
      m_RowInfo.put("gxfdid",gxfdid);
      m_RowInfo.put("gylxid",gylxid);
      m_RowInfo.put("dmsxid",dmsxid);
      produceDeatiWorkloadlData(cpid, dmsxid, gylxid, gxfdid);
      data.setMessage(showJavaScript("master_sl_onchange(true)"));
    }
  }
  class Cpid_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      //int row = Integer.parseInt(data.getParameter("rownum"));
      m_RowInfo.put("cpid","");
      m_RowInfo.put("piece_num","");
      m_RowInfo.put("proc_num","");
      //m_RowInfo.put("gxfdid","");
      m_RowInfo.put("dmsxid","");
      m_RowInfo.put("cjlzdmxID","");
      m_RowInfo.put("receiveDetailID","");
      data.setMessage(showJavaScript("master_sl_onchange(true)"));
    }
  }
  /**
   *  选择任务单主表，引入从表所有未加工信息

  class Single_Select_Process implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String singleImportProcess = m_RowInfo.get("drawSingleProcessTask");
      if(singleImportProcess.equals(""))
        return;
      String SQL = PROCESS_DETAIL_SQL+singleImportProcess;
      EngineDataSet tempProcessData = null;//零时加工单从表信息数据集
      if(tempProcessData==null)
      {
        tempProcessData = new EngineDataSet();
        setDataSetProperty(tempProcessData,null);
      }
      tempProcessData.setQueryString(SQL);
      if(!tempProcessData.isOpen())
        tempProcessData.openDataSet();
      else
        tempProcessData.refresh();

      RowMap processMasterRow = getProcessMasterBean(req).getLookupRow(singleImportProcess);
      rowInfo.put("deptid", processMasterRow.get("deptid"));
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdmxid");
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String gzlid = dsMasterTable.getValue("gzlid");
      for(int i=0; i<tempProcessData.getRowCount(); i++)
      {
        tempProcessData.goToRow(i);
        //double hsbl = tempProcessData.getValue("hsbl").length()>0 ? Double.parseDouble(tempProcessData.getValue("hsbl")) : 0;
        String jgdmxid = tempProcessData.getValue("jgdmxid");
        String cpid = tempProcessData.getValue("cpid");
        locateGoodsRow.setValue(0, jgdmxid);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          double sl = tempProcessData.getValue("sl").length()>0 ? Double.parseDouble(tempProcessData.getValue("sl")) : 0;//加工单需要的加工数量
          double ypgzl = tempProcessData.getValue("ypgzl").length()>0 ? Double.parseDouble(tempProcessData.getValue("ypgzl")) : 0;//加工单中已加工量
          double wpgzl = sl-ypgzl>0 ? sl-ypgzl : 0;
          if(wpgzl==0)
            continue;
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("gzlmxid", "-1");
          dsDetailTable.setValue("jgdmxid",jgdmxid);
          dsDetailTable.setValue("cpid", cpid);
          dsDetailTable.setValue("sl", String.valueOf(wpgzl));
          //dsDetailTable.setValue("hssl", formatNumber(String.valueOf(hsbl==0 ? 0 : wpgzl/hsbl), qtyFormat));
          dsDetailTable.setValue("gylxid", tempProcessData.getValue("gylxid"));
          dsDetailTable.setValue("gzlid", isMasterAdd ? "-1" : gzlid);
          dsDetailTable.setValue("dmsxid", tempProcessData.getValue("dmsxid"));
          dsDetailTable.post();
          //创建一个与用户相对应的行
          RowMap detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
        data.setMessage(showJavaScript("big_change()"));
      }
    }
  }
  */
 /**
  *  选择任务单主表.得到的任务单id定位出任务单明细资料,然后把任务单明细资料数据引入到此张流转单中来.
  */
  class Draw_Single_ProcessTask implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();

      String mulitProcessTask = m_RowInfo.get("drawSingleProcessTask");
      if(mulitProcessTask.equals(""))
        return;
      String[] singleImportProcessTask = parseString(mulitProcessTask,",");
      for(int k=0; k < singleImportProcessTask.length; k++)
      {
        String processTask = singleImportProcessTask[k];
        String SQL = combineSQL(PROCESSTASK_DETAIL_SQL, "?", new String[]{processTask});
        EngineDataSet tempProcessData = null;//零时任务单从表信息数据集
        if(tempProcessData==null)
        {
          tempProcessData = new EngineDataSet();
          setDataSetProperty(tempProcessData,null);
        }
        tempProcessData.setQueryString(SQL);
        if(!tempProcessData.isOpen())
          tempProcessData.openDataSet();
        else
          tempProcessData.refresh();

        //实例化查找数据集的类
        EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "jgdmxid");
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String cjlzdid = dsMasterTable.getValue("cjlzdid");
        for(int i=0; i<tempProcessData.getRowCount(); i++)
        {
          tempProcessData.goToRow(i);
          String jgdmxid = tempProcessData.getValue("rwdmxid");
          locateGoodsRow.setValue(0, jgdmxid);
          if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
          {
            double sl = tempProcessData.getValue("sl").length()>0 ? Double.parseDouble(tempProcessData.getValue("sl")) : 0;//加工单需要的加工数量
            double yjgl = tempProcessData.getValue("yjgl").length()>0 ? Double.parseDouble(tempProcessData.getValue("yjgl")) : 0;//加工单需要的加工数量
            double wjgl = sl-yjgl>0 ? sl-yjgl : 0;
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("cjlzdmxid", "-1");
            dsDetailTable.setValue("jgdmxid",jgdmxid);
            dsDetailTable.setValue("cpid", tempProcessData.getValue("cpid"));
            dsDetailTable.setValue("sl", String.valueOf(wjgl));
            dsDetailTable.setValue("cjlzdid", isMasterAdd ? "-1" : cjlzdid);
            dsDetailTable.setValue("dmsxid", tempProcessData.getValue("dmsxid"));
            dsDetailTable.post();
            //创建一个与用户相对应的行
            RowMap detailrow = new RowMap(dsDetailTable);
            d_RowInfos.add(detailrow);
          }
          //data.setMessage(showJavaScript("big_change()"));
        }
      }
    }
  }
  /**
   *
   * 员工
   *
   * */
  class Detail_Multi_ADD implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      String personids = m_RowInfo.get("personids");
      if(personids.length() == 0)
        return;
      EngineDataSet ds = getMaterTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      piecewage_ID = dsMasterTable.getValue("piecewage_ID");
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "personid");
      String[] personid = parseString(personids,",");//解析出合同货物ID数组
      for(int i=0; i < personid.length; i++)
      {
        if(personid[i].equals("-1"))
          continue;
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);

        RowMap detailrow = null;
        locateGoodsRow.setValue(0, personid[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("piecewage_ID", isMasterAdd ? "-1" : piecewage_ID);
          dsDetailTable.setValue("personid", personid[i]);
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
    }
  }
  /**
   * 添加到审核列表的操作类
   */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String num = data.getParameter("rownum");
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterTable.getValue("piece_code");
      String deptid = dsMasterTable.getValue("deptid");
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "sc_piecewage", content,deptid);
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
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //得到request的参数,值若为null, 则用""代替
      isReport = String.valueOf(REPORT).equals(action);
      String id = data.getParameter("id", "");
      if(isReport){
        isApprove = false;
        id = data.getParameter("id");//得到报表传递的参数既收发单据主表ID
      }
      else{
        isApprove = true;//审批操作
        id = data.getParameter("id", "");
      }
      String sql = combineSQL(MASTER_EDIT_SQL, "?", new String[]{id});

      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.openDataSet();
      piecewage_ID = dsMasterTable.getValue("piecewage_ID");

      openDetailTable(false);
      initRowInfo(true, false, true);
      initRowInfo(false, false, true);

      isReport = false;

    }
  }
  /**
   * 取消审批的操作类
   */
  class Cancel_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterTable, dsMasterTable.getRow(), "sc_piecewage");
    }
  }
  /**
   *  强制完成触发事件
   */
  class Complete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsMasterTable.goToRow(row);
      dsMasterTable.setValue("zt", "8");
      dsMasterTable.post();
      dsMasterTable.saveChanges();
    }
  }
  /**
   *  从表增加操作(增加一个空白行)
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      piecewage_ID = dsMasterTable.getValue("piecewage_ID");
      detail.insertRow(false);
      detail.setValue("piecewage_ID", isMasterAdd ? "-1" : piecewage_ID);
      detail.setValue("pieceemp_ID","-1");
      detail.post();
      RowMap detailrow = new RowMap(detail);
      detailrow.put("InternalRow", String.valueOf(detail.getInternalRow()));
      d_RowInfos.add(detailrow);
    }
  }
  /**
   *  从表删除操作
   */
  class Detail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getDetailTable();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      //删除临时数组的一列数据
      d_RowInfos.remove(rownum);
      ds.goToRow(rownum);
      ds.deleteRow();
    }
  }
  /**
   * 03:24 10:39 拷贝当前行功能.
   */
  class Copy_CurrentRow implements Obactioner
  {
    /**
     * 取得网页上明细资料表格中当前行的资料,然后再复制指定数量的此行的资料.加入到网页数据集中,显示出来在网页上.
     *  1. 从request中得到页面上当前光标所停留在的行数是下方明细表格中的那一行.设为i行.
     *  2. 接着要从保存网页资料的RowMap集中取出此i行的数据.
     *  3. 将此i行的数据加入到保存网页资料的RowMap集中,连续加入指定数量次.
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //取得网页上光标所停留在的那一行的行数.
      int currentRow = Integer.parseInt(data.getParameter("rownum"));
      //取得使用复制功能的时候,默认一次复制几笔. yjg
      String tCopyNumber = data.getParameter("tCopyNumber");
      int copyNum= (tCopyNumber==null || tCopyNumber.equals("0")) ? 1 : Integer.parseInt(tCopyNumber);
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      EngineDataSet ds = getMaterTable();
      RowMap detailRow = (RowMap)d_RowInfos.get(currentRow);
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String piecewage_ID = dsMasterTable.getValue("piecewage_ID");
      RowMap rowinfo = null;
      long swapdetailRow = -1;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        rowinfo = (RowMap)d_RowInfos.get(i);
        swapdetailRow = Long.parseLong(rowinfo.get("InternalRow"));
        detail.goToInternalRow(swapdetailRow);
        detail.setValue("personid", rowinfo.get("personid"));
        detail.setValue("work_proc", rowinfo.get("work_proc"));
        detail.setValue("piece_num", rowinfo.get("piece_num"));
        detail.setValue("proc_num", rowinfo.get("proc_num"));
        detail.setValue("piece_price", rowinfo.get("piece_price"));
        detail.setValue("piece_wage", rowinfo.get("piece_wage"));
        detail.setValue("piece_type", rowinfo.get("piece_type"));

        detail.post();
      }
      for (int i =0; i<copyNum; i++)
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("pieceemp_ID", "-1");
        dsDetailTable.setValue("piecewage_ID", isMasterAdd ? "-1" : piecewage_ID);
        dsDetailTable.setValue("personid", detailRow.get("personid"));
        dsDetailTable.setValue("work_proc", detailRow.get("work_proc"));
        dsDetailTable.setValue("piece_price", detailRow.get("piece_price"));
        dsDetailTable.setValue("gylxmxid", detailRow.get("gylxmxid"));
        dsDetailTable.setValue("piece_num", detailRow.get("piece_num"));

        dsDetailTable.setValue("piece_wage", detailRow.get("piece_wage"));
        dsDetailTable.setValue("piece_type", detailRow.get("piece_type"));
        //03.07 14:50 新增 在界面上用引入进货单按钮引入进货单时同时也取出规格属性来. yjg
        dsDetailTable.post();
        //创建一个与用户相对应的行
        //RowMap detailrow = new RowMap(dsDetailTable);
        //d_RowInfos.add(detailrow);
      }
      //2004-3-31 14:48  修改 为防止排序出错而改成多加一句put("InternalRow")这样的.以及相关改动 yjg
      initRowInfo(false, false, true);
    }
  }

  /**
   *改变工序下拉框触发事件
   * 1. 须要把所有的此工作组中的人员取出来.当成明细资料插入到明细数据集中
   *  1.1 取出在工资设置功能中设置好的此车间此人员工资数据.
   */
  class GxOnChange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      RowMap rowInfo = getMasterRowinfo();
      EngineDataSet detail = getDetailTable();
      //取得网页上光标所停留在的那一行的行数.
      int currentRow = Integer.parseInt(data.getParameter("rownum"));
      RowMap detailRow = (RowMap)d_RowInfos.get(currentRow);
      String gylxmxid = detailRow.get("gylxmxid");
      String gymcid = detailRow.get("v_work_proc");
      String gylxid = m_RowInfo.get("gylxid");
      String gxfdid = m_RowInfo.get("gxfdid");
      gylxmxid = gylxmxid.equals("")?gylxmxid:" and gylxmxid = " + gylxmxid;
      gylxid = gylxid.equals("")?gylxid:" and gylxid = " + gylxid;
      gymcid = gymcid.equals("")?gymcid:" and gymcid = " + gymcid;
      gxfdid = gxfdid.equals("")?gxfdid:" and gxfdid = " + gxfdid;
      String SQL = TECHNICS_OPTION_PRODUCE_SQL + gylxmxid + gylxid + gymcid + gxfdid;
      EngineDataSet dsTechnicsOption = null;//零时任务单从表信息数据集
      if(dsTechnicsOption==null)
      {
        dsTechnicsOption = new EngineDataSet();
        setDataSetProperty(dsTechnicsOption,null);
      }
      dsTechnicsOption.setQueryString(SQL);
      if(!dsTechnicsOption.isOpen())
        dsTechnicsOption.openDataSet();
      else
        dsTechnicsOption.refresh();
      dsTechnicsOption.first();
      String piece_price = dsTechnicsOption.getValue("deje");

      piece_price = piece_price.equals("")?"0":piece_price;

      gylxmxid = dsTechnicsOption.getValue("gylxmxid");
      detailRow.put("piece_price", piece_price);
      detailRow.put("gylxmxid", gylxmxid);

      String piece_num = detailRow.get("piece_num");
      piece_num = piece_num.equals("")?"0":piece_num;


      dsTechnicsOption.closeDataSet();
      dsTechnicsOption = null;
    }
  }
  /**
   * 得到用于查找生产加工单信息的bean
   * @param req WEB的请求
   * @return 返回用于查找生产加工单信息的bean
   */
  public ImportProcess getProcessGoodsBean(HttpServletRequest req)
  {
    if(importprocessBean == null)
      importprocessBean = ImportProcess.getInstance(req);
    return importprocessBean;
  }
  /**
   * 得到生产加工单主表一行信息的bean
   * @param req WEB的请求
   * @return 返回生产加工单主表一行信息的bean
   */
  public B_WorkLoad_Sel_Process getProcessMasterBean(HttpServletRequest req)
  {
    if(workloadSelProcessBean == null)
      workloadSelProcessBean = B_WorkLoad_Sel_Process.getInstance(req);
    return workloadSelProcessBean;
  }
  /**
   * 得到用于查找产品单价的bean
   * @param req WEB的请求
   * @return 返回用于查找产品单价的bean
   */
  public LookUp getTechnicsBean(HttpServletRequest req)
  {
    if(technicsBean == null){
      technicsBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_TECHNICS_PROCEDURE);
      technicsBean.regData(new String[]{});
    }
    return technicsBean;
  }
  /**
   * 根据产品工艺路线工序产生明细表数据
   */
  public void produceDeatiWorkloadlData(String cpid, String dmsxid, String gylxid, String gxfdid)
  {
    EngineDataSet dsGylxidGxfdidGroup = null;
    EngineDataSet dsGetGymc = null;
    EngineDataSet detail = getDetailTable();
    if(!isMasterAdd)
      dsMasterTable.goToInternalRow(masterRow);
    String piecewage_ID = dsMasterTable.getValue("piecewage_ID");
    String SQL = combineSQL(GYLX_GXFD_GROUP_SQL, "?", new String[]{gylxid, gxfdid});
    if(dsGylxidGxfdidGroup==null)
    {
      dsGylxidGxfdidGroup = new EngineDataSet();
      setDataSetProperty(dsGylxidGxfdidGroup,null);
    }
    dsGylxidGxfdidGroup.setQueryString(SQL);
    if(!dsGylxidGxfdidGroup.isOpen())
      dsGylxidGxfdidGroup.openDataSet();
    else
      dsGylxidGxfdidGroup.refresh();
    detail.deleteAllRows();
    d_RowInfos.clear();
    dsGylxidGxfdidGroup.first();
    for (int i=0;i<dsGylxidGxfdidGroup.getRowCount();i++)
    {
      String gylxmxid = dsGylxidGxfdidGroup.getValue("gylxmxid");
      detail.insertRow(false);
      detail.setValue("piecewage_ID", isMasterAdd?"-1":piecewage_ID);
      detail.setValue("pieceemp_ID","-1");
      //estimate how many recods on conditon that technis routing
      //if only one
      String tmpgylxmxid = gylxmxid.equals("")?gylxmxid:" and gylxmxid = " + gylxmxid;
      String tmpgylxid = gylxid.equals("")?gylxid:" and gylxid = " + gylxid;
      String tmpgxfdid = gxfdid.equals("")?gxfdid:" and gxfdid = " + gxfdid;
      String tmpSQL = TECHNICS_OPTION_PRODUCE_SQL + tmpgylxmxid + tmpgylxid + tmpgxfdid;
      if(dsGetGymc==null)
      {
        dsGetGymc = new EngineDataSet();
        setDataSetProperty(dsGetGymc,null);
      }
      dsGetGymc.setQueryString(tmpSQL);
      if(!dsGetGymc.isOpen())
        dsGetGymc.openDataSet();
      else
        dsGetGymc.refresh();
      String piece_price = "";
      dsGetGymc.first();
      if (dsGetGymc.getRowCount()>=1)
      {
        String gymc = dsGetGymc.getValue("gymc");

        piece_price = dsGetGymc.getValue("deje");
        piece_price = piece_price.equals("")?"0":piece_price;

        detail.setValue("work_proc",gymc);

        detail.setValue("piece_price",piece_price);
        double piece_wage =  Double.parseDouble(piece_price) * Double.parseDouble(importedSl);;
        detail.setValue("piece_wage",formatNumber(piece_wage, priceFormat));
      }
      detail.setValue("gylxmxid",gylxmxid);
      detail.setValue("piece_num",importedSl);

      detail.post();
      RowMap detailrow = new RowMap(detail);
      detailrow.put("InternalRow", String.valueOf(detail.getInternalRow()));
      d_RowInfos.add(detailrow);
      dsGylxidGxfdidGroup.next();
    }
    dsGylxidGxfdidGroup.closeDataSet();
    dsGetGymc.closeDataSet();
  }
  /**
   * 取得总共有几个工艺路线
   */
  public String[] getGylxmxid(HttpServletRequest request)
  {
    putDetailInfo(request);
    int rownum = d_RowInfos.size();
    ArrayList gylxmxTmpList = new ArrayList();
    RowMap detailRow = null;
    for(int i = 0; i < rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      String tmpGylxmxidVariable = detailRow.get("gylxmxid");
      if (!gylxmxTmpList.contains(tmpGylxmxidVariable))
        gylxmxTmpList.add(tmpGylxmxidVariable);
    }
    int l = gylxmxTmpList.size();
    String[] gylxmxids = new String[l];
    gylxmxids = StringUtils.listToStrings(gylxmxTmpList);
      /*EngineDataSet getGylxmxid = null;
      EngineDataSet detail = getDetailTable();
      if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
      String piecewage_ID = dsMasterTable.getValue("piecewage_ID");
      String SQL = "select gylxmxid from sc_piecewage_emp where piecewage_ID = ? group by gylxmxid ";
      SQL = combineSQL(SQL, "?", new String[]{piecewage_ID});
      if(getGylxmxid==null)
      {
        getGylxmxid = new EngineDataSet();
        setDataSetProperty(getGylxmxid,null);
      }
      getGylxmxid.setQueryString(SQL);
      if(!getGylxmxid.isOpen())
        getGylxmxid.openDataSet();
      else
        getGylxmxid.refresh();
      getGylxmxid.first();
      int l = getGylxmxid.getRowCount();
      for (int i=0;i<l;i++)
      {
        gylxmxids[i] = getGylxmxid.getValue("gylxmxid");
        getGylxmxid.next();
      }
      */
    return gylxmxids;
  }
  public long getGylxmxNum(String gylxmxid)
  {
    EngineDataSet dsGetGymc = null;
    gylxmxid = gylxmxid.equals("")?gylxmxid:" and gylxmxid = " + gylxmxid;
    String tmpSQL = TECHNICS_OPTION_PRODUCE_SQL + gylxmxid;
    if(dsGetGymc==null)
    {
      dsGetGymc = new EngineDataSet();
      setDataSetProperty(dsGetGymc,null);
    }
    dsGetGymc.setQueryString(tmpSQL);
    if(!dsGetGymc.isOpen())
      dsGetGymc.openDataSet();
    else
      dsGetGymc.refresh();
    long l = dsGetGymc.getRowCount();
    return l;
  }
}