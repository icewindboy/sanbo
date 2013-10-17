package engine.erp.sale;


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
import engine.common.*;
import java.util.*;
import java.text.*;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
/**
 * <p>Title:销售管理--业务员奖金计算</p>
 * <p>Description: 销售管理--业务员奖金计算</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author ENGINE
 * @version 1.0
 *
 * SimpleCalculator.arithmetic()计算公式的值
 *
 */
public final class B_SalerPrize extends BaseAction implements Operate
{
  public static final String MONTH_CHANGE = "77432243";//所选月份引发事件
  public static final String CALCULATE = "46573824";//计算操作
  public static final String OVER = "888888";//计算操作
  public static final String GET = "5554411";//取业务员
  private static final String LX = "2";

  private static final String MASTER_STRUT_SQL = "SELECT * FROM xs_jjzb where 1<>1";//业务员奖金主表结构
  private static final String MASTER_SQL    = "SELECT * FROM xs_jjzb ";//
  private static final String SEARCH_SQL = "SELECT * FROM (SELECT a.*,b.nf,b.yf,b.sfjz FROM xs_jj a,xs_jjzb b WHERE a.jjzbid=b.jjzbid) WHERE 1=1 ?  ";

  private static final String DETAIL_STRUT_SQL = "SELECT * FROM xs_jj where 1<>1";//业务员奖金明细表结构
  private static final String DETAIL_SQL    = "SELECT a.* FROM xs_jj a,emp b where a.personid=b.personid ? order by a.deptid,b.bm";//
  private static final String DETAIL_SEARCH_SQL    = "SELECT a.* FROM xs_jj a,emp b where a.personid=b.personid ? order by a.deptid,b.bm";//

  private static final String FIELD_NAME_SQL = "SELECT * FROM xs_jjgssz";//奖金项目名称及公式
  private static final String SALE_BALANCE_SQL = "SELECT * FROM VW_SALER ";//销售结算

  private static final String SALER_CJJ_SQL ="select *  from vw_saler_cjj a  WHERE 1=1 "; //"select sum(abs((nvl(a.dj,0)-nvl(a.jzj,0)))*nvl(a.cjtcl,0)*nvl(a.sl,0)*0.01) cgj,sum((nvl(a.dj,0)-nvl(a.jzj,0))*nvl(a.sl,0))xscj  from vw_saler_cjj a  WHERE 1=1 ";//差价奖
  private static final String SALER_TCJ_SQL = "select sum(nvl(a.tcj,0))tcj  from VW_SALER_TCJ a   WHERE  1=1  ";
  //private static final String SALER_LQJ_SQL = "select sum(nvl(a.tcj,0)*b.adjustxs*0.15)tcj  from VW_SALER_TCJ a,xs_khdjxs b,xs_khxyed c WHERE a.dwtxid=c.dwtxid AND c.xydj=b.xydj ";

  private static final String SALER_INIT_TCJ_SQL = "SELECT SUM(nvl(b.jje,0)*nvl(b.hltcl,0)*0.01)tcj FROM xs_td a,xs_tdhw b WHERE a.tdid=b.tdid AND a.Isinit=1 ";

  private static final String SALER_TSLXL_SQL = " select sum(nvl(b.hlts,0)*nvl(b.jje,0)*c.fundxs*0.01) XSA from xs_td a,xs_tdhw b,xs_khdjxs c,xs_khxyed d where a.tdid=b.tdid  AND a.zt=8 AND c.xydj=d.xydj AND a.dwtxid=d.dwtxid ";//销售允许天数利息
  private static final String SALER_TOTAL_SL_SQL = "select sum(nvl(a.sl,0))sl from vw_saler_cjj a where a.sfws=1 ";//省外销售数量
  //省外初贴
  private static final String SALER_BT_SQL = "SELECT sum(a.sl)sl,a.gls,a.dwtxid FROM vw_saler_cjj a WHERE a.sfws=1   ";

  private static final String SALER_YCYE_SQL =
      "SELECT nvl(jf,0)+nvl(ysk,0)-nvl(df,0)ye  FROM  "
      +"("
      +"  SELECT "
      +"  ("
      +"    SELECT SUM(nvl(m.jshj,0)*c.fundxs*0.01) FROM cw_xsfp f ,cw_xsfpmx m,xs_khdjxs c,xs_khxyed d WHERE  c.xydj=d.xydj AND f.dwtxid=d.dwtxid and f.kprq< to_date('?','yyyy-mm-dd') AND f.xsfpid=m.xsfpid   AND f.personid=? group by f.personid"
      +"  ) jf,"
      +" ("
      +" SELECT SUM(nvl(x.ysk,0)*c.fundxs*0.01)ysk FROM xs_ysk x,xs_khdjxs c,xs_khxyed d WHERE  c.xydj=d.xydj AND x.dwtxid=d.dwtxid and  x.personid=? group by x.personid "
      +") ysk,   "
      +" (SELECT SUM(nvl(l.je,0)*c.fundxs*0.01) FROM cw_xsjs l,xs_khdjxs c,xs_khxyed d WHERE   c.xydj=d.xydj AND l.dwtxid=d.dwtxid and  l.rq < to_date('?','yyyy-mm-dd') AND l.personid=? group by l.personid ) df    "
      +"  FROM dual )";//按发票月初余额
  private static final String TD_YCYE_SQL=
      "SELECT nvl(jf,0)+nvl(ysk,0)-nvl(df,0)ye  FROM  "
      +"("
      +" SELECT "
      +"  ("
      +"   SELECT SUM(nvl(m.jje,0)*c.fundxs*0.01) FROM xs_td f ,xs_tdhw m,xs_khdjxs c,xs_khxyed d WHERE  c.xydj=d.xydj AND f.dwtxid=d.dwtxid and  f.tdrq< to_date('?','yyyy-mm-dd') AND f.tdid=m.tdid and f.zt=8   AND f.personid=? group by f.personid"
      +" ) jf,"
      +" ("
    +" SELECT SUM(nvl(x.ysk,0)*c.fundxs*0.01)ysk FROM xs_ysk x,xs_khdjxs c,xs_khxyed d WHERE  c.xydj=d.xydj AND x.dwtxid=d.dwtxid and  x.personid=? group by x.personid "
      +") ysk,   "
      +" ("
      +" SELECT SUM(nvl(l.je,0)*c.fundxs*0.01) FROM cw_xsjs l,xs_khdjxs c,xs_khxyed d WHERE   c.xydj=d.xydj AND l.dwtxid=d.dwtxid and  l.rq < to_date('?','yyyy-mm-dd') and l.zt in(1,8) AND l.personid=? group by l.personid "
      +") df    "
      +" FROM dual )";//按提单月初余额
  private static final String SALER_LX_SQL = "select a.tdrq,sum(nvl(b.jje,0)*c.fundxs*0.01)jje from xs_td a,xs_tdhw b,xs_khdjxs c,xs_khxyed d where a.tdid=b.tdid  AND c.xydj=d.xydj AND a.dwtxid=d.dwtxid and personid =? and a.tdrq >= to_date('?','YYYY-MM-DD') AND a.tdrq<to_date('?','YYYY-MM-DD') and a.zt in(2,3,8) group by a.tdrq";//销售利息
  private static final String SALER_LQJ_SQL = "select sum(nvl(b.jsje,0)*g.hktcl*c.adjustxs*0.15) je from cw_xsjs a,cw_xsjshx b, xs_khdjxs c,xs_khxyed d,xs_tdhw e,xs_wzdj g WHERE a.xsjsid=b.xsjsid AND a.zt=8 AND c.xydj=d.xydj AND a.dwtxid=d.dwtxid  AND e.wzdjid=g.wzdjid  ";

  private static final String RLL_SQL = "select rll from xs_jjjsgs";//引入日利率
  private static final String JSFS_SQL = "select jsfs from xs_jjjsgs";//引入结算方式(按提单还是按发票计算月初余额)

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsJsgs = new EngineDataSet();//奖金项目名称及公式
  private EngineDataSet dsSearchTable = new EngineDataSet();

  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private boolean isMasterAdd = true;
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String djlx="" ;       //提单类型
  private String jjzbid = "";//主表id
  private String jjid = "";//从表id
  private String month = "";
  private LookUp personBean = null;
  public String currentyear="";//当前
  private String rll = "0";
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_SalerPrize getInstance(HttpServletRequest request)
  {
    B_SalerPrize B_SalerPrizeBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_SalerPrizeBean";
      B_SalerPrizeBean = (B_SalerPrize)session.getAttribute(beanName);
      if(B_SalerPrizeBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        B_SalerPrizeBean = new B_SalerPrize();
        //B_SalerPrizeBean.qtyFormat = loginBean.getQtyFormat();
        B_SalerPrizeBean.priceFormat = loginBean.getPriceFormat();
        //B_SalerPrizeBean.sumFormat = loginBean.getSumFormat();
        B_SalerPrizeBean.fgsid = loginBean.getFirstDeptID();
        B_SalerPrizeBean.loginId = loginBean.getUserID();
        B_SalerPrizeBean.loginName = loginBean.getUserName();
        //设置格式化的字段
        B_SalerPrizeBean.dsDetailTable.setColumnFormat("cgj", B_SalerPrizeBean.priceFormat);
        B_SalerPrizeBean.dsDetailTable.setColumnFormat("jj", B_SalerPrizeBean.priceFormat);
        session.setAttribute(beanName, B_SalerPrizeBean);
      }
    }
    return B_SalerPrizeBean;
  }
  /**
   * 构造函数
   */
  private B_SalerPrize()
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
    setDataSetProperty(dsJsgs,FIELD_NAME_SQL);
    setDataSetProperty(dsSearchTable,combineSQL(SEARCH_SQL,"?",new String[]{""}));

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jjzbid"}, new String[]{"s_xs_jjzb"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"jjzbid"}, new boolean[]{true}, null, 0));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"jjid"}, new String[]{"s_xs_jj"}));
    dsJsgs.setSort(new SortDescriptor("", new String[]{"pxh"}, new boolean[]{false}, null, 0));
    dsDetailTable.setTableName("xs_jj");

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());

    addObactioner(String.valueOf(POST), new Detail_Post());

    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());

    addObactioner(String.valueOf(MONTH_CHANGE),new MonthOnchange());
    //
    addObactioner(String.valueOf(OVER),new Master_Over());
    addObactioner(String.valueOf(CALCULATE),new Detail_Calculate());
    addObactioner(String.valueOf(DEPT_CHANGE),new Dept_Change());
    addObactioner(String.valueOf(GET),new GetPserson());



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
    if(dsJsgs!=null)
    {
      dsJsgs.close();
      dsJsgs = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
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
        d_RowInfos.add(row);
        dsDetail.next();
      }
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
      EngineDataSet dsFieldName = getJjTable();
      dsFieldName.first();
      for(int j=0;j<dsFieldName.getRowCount();j++)
      {
        detailRow.put(dsFieldName.getValue("dyzdm"), rowInfo.get(dsFieldName.getValue("dyzdm")+"_"+i));//
        dsFieldName.next();
      }
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
  /*得到奖金项目表对象*/
  public final EngineDataSet getJjTable(){
    return dsJsgs;
  }
  /*得到奖金项目表对象*/
  public final String getJjzbid(){
    return jjzbid;
  }
  /**得到要显示字段的数量**/
  public final String getShowFieldCont()
  {
    if(!dsJsgs.isOpen())
      dsJsgs.open();
    int count =0;
    dsJsgs.first();
    for(int i=0;i<dsJsgs.getRowCount();i++)
    {
      if(dsJsgs.getValue("sfxs").equals("1"))
        count= count+1;
      dsJsgs.next();
    }
    return String.valueOf(count);
  }
  /*得到奖金计算公式*/
  public final String getGS()
  {
    if(!dsJsgs.isOpen())
      dsJsgs.open();

    ArrayList allfields = new ArrayList();
    ArrayList fields = new ArrayList();//等于公式的字段列表
    ArrayList gs = new ArrayList();    //公式列表
    String jjgs = "";
    dsJsgs.first();
    for(int i=0;i<dsJsgs.getRowCount();i++)
    {
      if(dsJsgs.getValue("ly").equals("2"))
      {
       if(dsJsgs.getValue("dyzdm").equals("jj"))
       {
         jjgs=dsJsgs.getValue("jsgs");
         continue;
       }
        fields.add(dsJsgs.getValue("dyzdm"));
        gs.add(dsJsgs.getValue("jsgs"));
      }
      dsJsgs.next();
    }

    for(int i=0;i<fields.size();i++)
    {
      String fieldname = (String)fields.get(i);
      String sgs = (String)gs.get(i);
      jjgs=engine.util.StringUtils.replace(jjgs,fieldname,sgs);
    }
    dsJsgs.first();
    for(int j=0;j<dsJsgs.getRowCount();j++)
    {
      String tmpfieldname = dsJsgs.getValue("dyzdm");
      if(tmpfieldname.equals("jj"))
      {
        dsJsgs.next();
        continue;
      }
      jjgs=engine.util.StringUtils.replace(jjgs,tmpfieldname,"parseFloat("+tmpfieldname+".value)");
      dsJsgs.next();
    }
    return jjgs;
  }
  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    //jjzbid = dsMasterTable.getValue("jjzbid");//关链
    String SQL = combineSQL(DETAIL_SQL,"?",new String[]{(isMasterAdd ? " and jjzbid=-1" : " and jjzbid="+jjzbid)});
    dsDetailTable.setQueryString(SQL);
    if(!dsDetailTable.isOpen())
    {
      dsDetailTable.open();
    }
    else
    {
      dsDetailTable.readyRefresh();
      dsDetailTable.refresh();
    }
  }
  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }
  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
  /*当前操作的月份*/
  public final String getMonth(){return month;}
  /**得到日利率**/
  public final String getTrueRll()
  {
    return rll;
  }
  /**得到当前日利率**/
  public final String getRll()
  {
    String rll;
    try{
    rll = dataSetProvider.getSequence(RLL_SQL);//月利率
    if(rll==null||rll.equals(""))
      rll="0";
    Calendar calendar = new GregorianCalendar();
    int days = calendar.getActualMaximum(Calendar.DATE);
    BigDecimal br = new BigDecimal(rll);
    BigDecimal bd = new BigDecimal(days);
    rll = (br.divide(bd,6,BigDecimal.ROUND_HALF_UP)).toString();
    }catch(Exception e){return "0";}
   return rll;
  }
  /**
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  //public final boolean masterIsAdd() {return isMasterAdd; }

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
      GregorianCalendar calendar=new GregorianCalendar();
      currentyear=String.valueOf(calendar.get(Calendar.YEAR));

      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      HttpServletRequest request = data.getRequest();

      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      row.put("nf",currentyear);
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());

      dsMasterTable.setQueryString(MASTER_STRUT_SQL);
      dsMasterTable.open();
      dsMasterTable.refresh();
      dsJsgs.open();
      dsJsgs.refresh();
      //dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      dsDetailTable.refresh();
      openDetailTable(true);
      month="";
      initRowInfo(false,false,true);
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
      jjzbid = dsMasterTable.getValue("jjzbid");
      //打开从表
      openDetailTable(false);
    }
  }
  /**
   * 根据月份的变化显示主表的相应记录
   */
  class MonthOnchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String smonth = data.getParameter("month");
      if(smonth.equals(""))return;
      Calendar  cd= new GregorianCalendar();
      String year = String.valueOf(cd.get(Calendar.YEAR));//得到当前的年份
      String sql = MASTER_SQL+" WHERE lx=2 and nf="+year + " and yf="+smonth;//查主表



      int jm = Integer.parseInt(smonth)+1;
      String startDate = year+"-"+smonth+"-"+"1";
      String endDate = year+"-"+jm+"-"+"1";
      if(smonth.equals("12"))
        endDate=Integer.parseInt(year)+1+"-1-1";
      String count = dataSetProvider.getSequence(sql);

      if(count==null)
      {
        String s = "SELECT count(*) from VW_SALER where rq>=to_date('"+startDate+ "','YYYY-MM-DD') and rq<to_date('"+endDate+ "','YYYY-MM-DD')";
        String cnt = dataSetProvider.getSequence(s);
        if(cnt.equals("0"))
          {
          data.setMessage(showJavaScript("alert('"+smonth+"月份还没有数据!')"));
          return;
          }
        else
        {
          EngineDataSet tmp = new EngineDataSet();
          setDataSetProperty(tmp,"select distinct a.personid,a.deptid from xs_td a where  a.tdrq>=to_date('"+startDate+ "','YYYY-MM-DD') and a.tdrq<to_date('"+endDate+ "','YYYY-MM-DD') and a.zt in(2,3,8)");
          tmp.open();
          dsMasterTable.insertRow(false);
          jjzbid = dataSetProvider.getSequence("s_xs_jjzb");//得到主表主键值
          dsMasterTable.setValue("jjzbid",jjzbid);
          dsMasterTable.setValue("nf",year);
          dsMasterTable.setValue("yf",smonth);
          dsMasterTable.setValue("LX",LX);
          month=smonth;
          dsMasterTable.setValue("sfjz","0");
          dsMasterTable.post();
          rll = getRll();//得到日利率
          tmp.first();
          for(int i=0;i<tmp.getRowCount();i++)
          {
            dsDetailTable.insertRow(false);
            dsDetailTable.setValue("jjid","-1");
            dsDetailTable.setValue("jjzbid",jjzbid);
            dsDetailTable.setValue("personid",tmp.getValue("personid"));
            dsDetailTable.setValue("deptid",tmp.getValue("deptid"));
            dsDetailTable.setValue("rll",rll);
            dsDetailTable.post();
            tmp.next();
          }
          dsMasterTable.saveChanges();
          dsDetailTable.saveChanges();
        }
      }
      else
      {
        month=smonth;
        if(dsMasterTable.isOpen())
          dsMasterTable.close();
        setDataSetProperty(dsMasterTable,sql);
        dsMasterTable.open();
        dsMasterTable.first();
        jjzbid = dsMasterTable.getValue("jjzbid");
        openDetailTable(false);

        String yll =  dataSetProvider.getSequence(RLL_SQL);//月利率
        Date tmpdate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
        Calendar caldar = new GregorianCalendar();
        caldar.setTime(tmpdate);
        int days = caldar.getActualMaximum(Calendar.DATE);//当前月份的天数
        BigDecimal br = new BigDecimal(yll);
        BigDecimal bd = new BigDecimal(days);
        rll = (br.divide(bd,6,BigDecimal.ROUND_HALF_UP)).toString();

        if(dsDetailTable.getRowCount()>0)
        {
          dsDetailTable.first();
          rll = (dsDetailTable.getValue("rll").equals("")||dsDetailTable.getValue("rll").equals("0"))?rll:dsDetailTable.getValue("rll");//取从表的日利率
        }
      }
      initRowInfo(false,false,true);

    }
  }
  /**
   * 自动提取业务员
   */
  class GetPserson implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String smonth = dsMasterTable.getValue("yf");
      String year = dsMasterTable.getValue("nf");
      int jm = Integer.parseInt(smonth)+1;
      String startDate = year+"-"+smonth+"-"+"1";
      String endDate = year+"-"+jm+"-"+"1";
      if(smonth.equals("12"))
        endDate=Integer.parseInt(year)+1+"-1-1";
      String rll = getRll();//得到日利率
      EngineDataSet tmp = new EngineDataSet();
      setDataSetProperty(tmp,"select distinct a.personid,a.deptid from xs_td a where  a.tdrq>=to_date('"+startDate+ "','YYYY-MM-DD') and a.tdrq<to_date('"+endDate+ "','YYYY-MM-DD') and a.zt in(3,8)");
      tmp.open();
      for(int j=0;j<tmp.getRowCount();j++)
      {
        String personid = tmp.getValue("personid");
        String deptid = tmp.getValue("deptid");
        String cont = dataSetProvider.getSequence("select count(*) from xs_jj where jjzbid='"+jjzbid+"' and personid='"+personid+"' and deptid='"+deptid+"'");
        if(cont.equals("0"))
        {
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("jjid","-1");
          dsDetailTable.setValue("jjzbid",jjzbid);
          dsDetailTable.setValue("personid",personid);
          dsDetailTable.setValue("deptid",deptid);
          dsDetailTable.setValue("rll",rll);
          dsDetailTable.post();
        }
        tmp.next();
      }
      dsDetailTable.saveChanges();
      dsDetailTable.refresh();
      initRowInfo(false,false,true);
    }

  }
/**
 *增加业务员
 */
class Detail_Add implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest req = data.getRequest();
    putDetailInfo(data.getRequest());
    String multiIdInput = m_RowInfo.get("multiIdInput");
    if(multiIdInput.length() == 0)
      return;
    //实例化查找数据集的类
    EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "personid");
    String[] personids = parseString(multiIdInput,",");
    for(int i=0; i < personids.length; i++)
    {
      if(personids[i].equals("-1"))
        continue;
      jjzbid = dsMasterTable.getValue("jjzbid");
      RowMap detailrow = null;
      locateGoodsRow.setValue(0, personids[i]);
      if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
      {
        RowMap personRow = getpersonNameBean(req).getLookupRow(personids[i]);
        RowMap derow= new RowMap();
        String rll = getRll();
        derow.put("jjzbid", jjzbid);
        derow.put("rll", rll);
        derow.put("personid", personids[i]);
        derow.put("deptid", personRow.get("deptid"));
        d_RowInfos.add(derow);
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("jjid", "-1");
        dsDetailTable.setValue("rll", rll);
        dsDetailTable.setValue("jjzbid", jjzbid);
        dsDetailTable.setValue("personid", personids[i]);
        dsDetailTable.setValue("deptid", personRow.get("deptid"));
      }
    }
  }
}
/**
 *  完成操作
 */
class Master_Over implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    if(!dsMasterTable.isOpen())
      return;
    dsMasterTable.setValue("sfjz","1");
    dsMasterTable.saveChanges();
  }
}
/**
 *  查询操作
 */
class Master_Search implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest req = data.getRequest();
    initQueryItem(data.getRequest());
    fixedQuery.setSearchValue(data.getRequest());
    String SQL = fixedQuery.getWhereQuery();//得到WHERE子句
    if(SQL.length() > 0)
      SQL = " AND "+SQL;
    SQL = combineSQL(SEARCH_SQL,"?",new String[]{SQL});
    if(!dsSearchTable.getQueryString().equals(SQL))
    {
      dsSearchTable.setQueryString(SQL);
      dsSearchTable.setRowMax(null);
    }
    dsSearchTable.refresh();
    StringBuffer sb = new StringBuffer();
    ArrayList al = new ArrayList();
    String jjids="";
    String sjjzbid="";
    dsSearchTable.first();
    for(int i=0;i<dsSearchTable.getRowCount();i++)
    {
      sjjzbid = dsSearchTable.getValue("jjzbid");//关链
      jjid = dsSearchTable.getValue("jjid");
      if(jjid!=null&&!jjid.equals(""))
        if(!al.contains(jjid))
          al.add(jjid);
      dsSearchTable.next();
    }
    if(dsSearchTable.getRowCount()<1)
      sjjzbid="";
    if(sjjzbid.equals(""))
    {
      data.setMessage(showJavaScript("alert('没有数据')"));
      return;
    }
    jjzbid= sjjzbid;
    SQL=MASTER_SQL+" WHERE lx=2 and jjzbid="+jjzbid;
    if(dsMasterTable.isOpen())dsMasterTable.close();
    setDataSetProperty(dsMasterTable,SQL);
    dsMasterTable.open();
    if(al.size()==0)
      jjids="0";
    else
    {
      for(int j=0;j<al.size();j++)
        sb.append(al.get(j)+",");
      jjids=sb.append("0").toString();
    }
    SQL = combineSQL(DETAIL_SEARCH_SQL, "?", new String[]{" AND jjid IN("+jjids+") "});
    if(dsDetailTable.isOpen())dsDetailTable.close();
    setDataSetProperty(dsDetailTable,SQL);
    dsDetailTable.open();
    initRowInfo(false,false,true);
    month = req.getParameter("yf");
  }
}
private void initQueryItem(HttpServletRequest request)
{
  if(isInitQuery)
    return;//已初始化查询条件
  EngineDataSet master = dsSearchTable;
  if(!master.isOpen())
    master.open();//打开主表数据集
  //初始化固定的查询项目
  fixedQuery = new QueryFixedItem();
  fixedQuery.addShowColumn("", new QueryColumn[]{
    new QueryColumn(master.getColumn("nf"), null, null, null, null, "="),//年份
    new QueryColumn(master.getColumn("yf"), null, null, null, null, "="),//月份
    new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//月份
    new QueryColumn(master.getColumn("personid"), null, null, null, null, "=")//月份
  });
    isInitQuery = true;//初始化完成
  }
/**
 *  从表保存操作
 */
class Detail_Post implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest req = data.getRequest();
    putDetailInfo(data.getRequest());
    RowMap [] drows = getDetailRowinfos();
    dsDetailTable.first();
    for(int i=0;i<drows.length;i++)
    {
      RowMap drow = (RowMap)drows[i];
      EngineDataSet dsFieldName = getJjTable();
      dsFieldName.first();
      for(int j=0;j<dsFieldName.getRowCount();j++)
      {
        //formatNumber((drow.get(dsFieldName.getValue("dyzdm"))), priceFormat));
        dsDetailTable.setValue(dsFieldName.getValue("dyzdm"),formatNumber((drow.get(dsFieldName.getValue("dyzdm"))), priceFormat));
        dsFieldName.next();
      }
      dsDetailTable.next();
      dsDetailTable.saveChanges();
    }
  }
}
/**
 *部门改变
 * */
class Dept_Change implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest request = data.getRequest();
    RowMap row = fixedQuery.getSearchRow();
    row.put(request);
    data.setMessage(showJavaScript("showFixedQuery();"));
  }
}
/**
 *  从表计算业务员奖金
 */
class Detail_Calculate implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest req = data.getRequest();
    putDetailInfo(data.getRequest());
    String jsfs =  dataSetProvider.getSequence(JSFS_SQL);//结算方式
    String sfjz = dsMasterTable.getValue("sfjz");//是否结转 1=已结,0=未结
    String nf = dsMasterTable.getValue("nf");
    String yf = dsMasterTable.getValue("yf");
    Date stdate = new SimpleDateFormat("yyyy-MM-dd").parse(nf+"-"+yf+"-1");//当前月份第一天
    Calendar calendar = new GregorianCalendar();
    calendar.setTime(stdate);
    int days = calendar.getActualMaximum(Calendar.DATE);//当前月份的天数
    //计算当月的日利率
    String rll = dataSetProvider.getSequence(RLL_SQL);//日利率
    BigDecimal br = new BigDecimal(rll);
    BigDecimal bd = new BigDecimal(days);
    rll = (br.divide(bd,6,BigDecimal.ROUND_HALF_UP)).toString();

    if(sfjz.equals("1"))
      return;
    int jm = Integer.parseInt(yf)+1;
    String startDate = nf+"-"+yf+"-"+"1";
    String endDate = nf+"-"+jm+"-"+"1";
    if(yf.equals("12"))
      endDate=Integer.parseInt(nf)+1+"-1-1";
    RowMap [] drows = getDetailRowinfos();

    for(int i=0;i<drows.length;i++)
    {
      RowMap drow = (RowMap)drows[i];
      String personid = drow.get("personid");

      //计算差价奖=(dj-jzj)*cjtcl*0.01*jje
      //计算平均差价提成率
      //销售差价=dj-jzj
      String SQL = SALER_CJJ_SQL+" AND personid="+personid+" AND  a.rq>=to_date('"+startDate+"','YYYY-MM-DD') AND a.rq<to_date('"+endDate+"','YYYY-MM-DD')";
      EngineDataSet dscjj = new EngineDataSet();
      setDataSetProperty(dscjj,SQL);
      dscjj.open();
      double cjj=0;
      double xscj=0;
      BigDecimal zxslx=new BigDecimal(0);
      //double dxslx = 0;
      dscjj.first();
      for(int j=0;j<dscjj.getRowCount();j++)
      {

        double scjj=Double.parseDouble(dscjj.getValue("cjj").equals("")?"0":dscjj.getValue("cjj"));
        cjj = cjj+Double.parseDouble(dscjj.getValue("cjj").equals("")?"0":dscjj.getValue("cjj"));
        //xscj = xscj+Double.parseDouble(dscjj.getValue("xscj").equals("")?"0":dscjj.getValue("xscj"));
        String cjtcl = dscjj.getValue("cjtcl");
        if(!cjtcl.equals("")&&!cjtcl.equals("0"))
          xscj = xscj+Double.parseDouble(dscjj.getValue("xscj").equals("")?"0":dscjj.getValue("xscj"));

        Date tmpdate = new SimpleDateFormat("yyyy-MM-dd").parse(dscjj.getValue("rq"));
        Calendar caldar = new GregorianCalendar();
        caldar.setTime(tmpdate);
        int day = caldar.get(Calendar.DAY_OF_MONTH);//
        int cdays = caldar.getActualMaximum(Calendar.DATE);//当前月份的天数
        zxslx = zxslx.add(new BigDecimal((dscjj.getValue("jje").equals("")?"0":dscjj.getValue("jje"))).multiply(new BigDecimal(cdays-day)));
        //dxslx = dxslx+Double.parseDouble(dscjj.getValue("jje"))*(cdays-day);
        dscjj.next();
      }
      drow.put("cgj",formatNumber(String.valueOf(cjj), priceFormat));
      if(xscj==0)
       drow.put("avgcjtcl","0");
      else
        drow.put("avgcjtcl",formatNumber(String.valueOf(cjj/xscj), priceFormat));

       //内勤费
       String LQJSQL = SALER_LQJ_SQL+" AND a.personid="+personid+" AND  a.rq>=to_date('"+startDate+"','YYYY-MM-DD') AND a.rq<to_date('"+endDate+"','YYYY-MM-DD')";
       EngineDataSet dslqj = new EngineDataSet();
       setDataSetProperty(dslqj,LQJSQL);
       dslqj.open();
       double lqj=0;
       dslqj.first();
       String slqj = dslqj.getValue("je");
       if(slqj!=null&&!slqj.equals(""))
         lqj = Double.parseDouble(slqj);

       if(lqj==0)
        drow.put("lqj","0");
       else
         drow.put("lqj",formatNumber(String.valueOf(lqj), priceFormat));

      //计算提成奖
      //要核销货物时后并完成才有提成奖
      SQL = SALER_TCJ_SQL+" AND a.personid="+personid+" AND  a.rq>=to_date('"+startDate+"','YYYY-MM-DD') AND a.rq<to_date('"+endDate+"','YYYY-MM-DD') group by a.personid";
      EngineDataSet dstcj = new EngineDataSet();
      setDataSetProperty(dstcj,SQL);
      dstcj.open();
      dstcj.first();
      String tcj = dstcj.getValue("tcj").equals("")?"0":dstcj.getValue("tcj");
      //初始化提单的提成奖
      SQL = SALER_INIT_TCJ_SQL+" AND a.personid="+personid+" AND  a.tdrq>=to_date('"+startDate+"','YYYY-MM-DD') AND a.tdrq<to_date('"+endDate+"','YYYY-MM-DD') group by a.personid";
      EngineDataSet dsiittcj = new EngineDataSet();
      setDataSetProperty(dsiittcj,SQL);
      dsiittcj.open();
      dsiittcj.first();
      String inittcj = dsiittcj.getValue("tcj").equals("")?"0":dsiittcj.getValue("tcj");

      String ztcj = String.valueOf(Double.parseDouble(tcj)+Double.parseDouble(inittcj));//总提成奖

      drow.put("tcj",formatNumber(ztcj, priceFormat));


      //计算销售允许天数利息XSA
      //销售合同贷物的回笼天数*对应货物的提单销售金额
      SQL = SALER_TSLXL_SQL+" AND a.personid="+personid+" AND  a.tdrq>=to_date('"+startDate+"','YYYY-MM-DD') AND a.tdrq<to_date('"+endDate+"','YYYY-MM-DD') group by a.personid";
      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SQL);
      dssl.open();
      dssl.first();

      String xsa = dssl.getValue("XSA").equals("")?"0":dssl.getValue("XSA");///days
      //drow.put("XSA",formatNumber(dssl.getValue("XSA").equals("")?"0":dssl.getValue("XSA"), priceFormat));
      drow.put("XSA",new BigDecimal(xsa).divide(new BigDecimal(days),2));

      //计算省外销售数量
      SQL = SALER_TOTAL_SL_SQL+" AND a.personid="+personid+" AND  a.rq>=to_date('"+startDate+"','YYYY-MM-DD') AND a.rq<to_date('"+endDate+"','YYYY-MM-DD') group by a.personid";
      EngineDataSet dstotalsl = new EngineDataSet();
      setDataSetProperty(dstotalsl,SQL);
      dstotalsl.open();
      dstotalsl.first();
      String sl = dstotalsl.getValue("sl").equals("")?"0":dstotalsl.getValue("sl");
      drow.put("sl",formatNumber(sl, qtyFormat));

      //计算省外初贴
      SQL = SALER_BT_SQL+" AND a.personid="+personid+" AND  a.rq>=to_date('"+startDate+"','YYYY-MM-DD') AND a.rq<to_date('"+endDate+"','YYYY-MM-DD') GROUP BY a.gls,a.dwtxid";
      EngineDataSet dsswbt = new EngineDataSet();
      setDataSetProperty(dsswbt,SQL);
      dsswbt.open();
      //double s=0;
      BigDecimal bswbt = new BigDecimal(0);
      dsswbt.first();
      for(int j=0;j<dsswbt.getRowCount();j++)
      {
        String swsl = dsswbt.getValue("sl");
        double dsl = Double.parseDouble(swsl.equals("")?"0":swsl)*0.001;//吨
        String gls = dsswbt.getValue("gls");
        double dgls = Double.parseDouble(gls.equals("")?"0":gls);
        if(dgls<1000)
          bswbt = bswbt.add(new BigDecimal(dsl*100));
        else if(dgls>=1000&&dgls<2000)
          bswbt = bswbt.add(new BigDecimal(dsl*150));
        else if(dgls>=2000)
          bswbt = bswbt.add(new BigDecimal(dsl*200));
        dsswbt.next();
      }
      String sswbt =bswbt.toString();
      drow.put("swbt",sswbt);

      //计算销售回笼利息xsb
      //(当月的总天数-当天的日期)*结算金额
      SQL = "select a.rq,nvl(a.je,0)*c.fundxs*0.01 je from cw_xsjs a,xs_khdjxs c,xs_khxyed d where a.zt=8 AND c.xydj=d.xydj AND a.dwtxid=d.dwtxid  and  a.personid="+personid+" AND  a.rq>=to_date('"+startDate+"','YYYY-MM-DD') AND a.rq<to_date('"+endDate+"','YYYY-MM-DD')";
      EngineDataSet dshlls = new EngineDataSet();
      setDataSetProperty(dshlls,SQL);
      dshlls.open();
      //double s=0;
      BigDecimal zxsb = new BigDecimal(0);
      dshlls.first();
      for(int j=0;j<dshlls.getRowCount();j++)
      {
        Date tmpdate = new SimpleDateFormat("yyyy-MM-dd").parse(dshlls.getValue("rq"));
        Calendar caldar = new GregorianCalendar();
        caldar.setTime(tmpdate);
        int day = caldar.get(Calendar.DAY_OF_MONTH);
        int cdays = caldar.getActualMaximum(Calendar.DATE);//当前月份的天数
        //s=(cdays-day)*Double.parseDouble(dshlls.getValue("jsje"));
        zxsb = zxsb.add(new BigDecimal(dshlls.getValue("je").equals("")?"0":dshlls.getValue("je")).multiply(new BigDecimal(cdays-day)));
        dshlls.next();
      }
      String xsb =zxsb.toString(); //String.valueOf(s);
      //drow.put("XSB",formatNumber(xsb, priceFormat));
      drow.put("XSB",new BigDecimal(xsb).divide(new BigDecimal(days),2));


      //应收款月初余额利息
      //月初余额*月结算天数(当月的天数)
      if(jsfs.equals("1"))//按发票
      SQL = combineSQL(SALER_YCYE_SQL,"?",new String[]{nf+"-"+yf+"-1",personid,personid,nf+"-"+yf+"-1",personid});
      else//按提单
        SQL = combineSQL(TD_YCYE_SQL,"?",new String[]{nf+"-"+yf+"-1",personid,personid,nf+"-"+yf+"-1",personid});

      EngineDataSet dsycye = new EngineDataSet();
      setDataSetProperty(dsycye,SQL);
      dsycye.open();
      dsycye.first();
      String ye = dsycye.getValue("ye").equals("")?"0":dsycye.getValue("ye");
      BigDecimal bye= new BigDecimal(ye);
      //bye.multiply(new BigDecimal(days));
      //String xsc = String.valueOf(days*Double.parseDouble(ye));
      //String xsc = (bye.multiply(new BigDecimal(days))).toString();
      //drow.put("XSC",formatNumber(xsc, priceFormat));
      drow.put("XSC",bye.toString());

      //计算总销售金额
      //计算销售利息
      SQL = combineSQL(SALER_LX_SQL,"?",new String[]{personid,startDate,endDate});
      EngineDataSet dsXSLX = new EngineDataSet();
      setDataSetProperty(dsXSLX,SQL);
      dsXSLX.open();
      double dxsje=0;
      double ss=0;
      dsXSLX.first();
      for(int h=0;h<dsXSLX.getRowCount();h++)
      {
        Date tmpdate = new SimpleDateFormat("yyyy-MM-dd").parse(dsXSLX.getValue("tdrq"));
        Calendar caldar = new GregorianCalendar();
        caldar.setTime(tmpdate);
        int day = caldar.get(Calendar.DAY_OF_MONTH);//
        int cdays = caldar.getActualMaximum(Calendar.DATE);//当前月份的天数
        ss=(cdays-day)*Double.parseDouble(dsXSLX.getValue("jje"));
        dxsje = dxsje+Double.parseDouble(dsXSLX.getValue("jje"));
        dsXSLX.next();
      }
      String xsd = zxslx.toString();
      //drow.put("XSD",formatNumber(xsd, priceFormat));
      drow.put("XSD",new BigDecimal(xsd).divide(new BigDecimal(days),2));
      drow.put("xsje",formatNumber(String.valueOf(dxsje), priceFormat));
      drow.put("rll",rll);
    }
    data.setMessage(showJavaScript("cal("+d_RowInfos.size()+");"));
  }
}
/**
 *  从表删除操作
 */
class Detail_Delete implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest req = data.getRequest();
    putDetailInfo(req);
    EngineDataSet ds = getDetailTable();
    int rownum = Integer.parseInt(req.getParameter("rownum"));
    //删除临时数组的一列数据
    d_RowInfos.remove(rownum);
    ds.goToRow(rownum);
    ds.deleteRow();
  }
}

/**
 *人员信息
 */
public LookUp getpersonNameBean(HttpServletRequest req)
{
  if(personBean == null)
    personBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PERSON);
  return personBean;
}
}
