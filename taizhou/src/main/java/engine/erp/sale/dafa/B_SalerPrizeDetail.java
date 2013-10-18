package engine.erp.sale.dafa;


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
 */
public final class B_SalerPrizeDetail extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL =        "100001";
  public  static final String DETAIL_POST =        "100002";
  private static final String MASTER_SQL    = "SELECT * FROM xs_jjzb where jjzbid='";//

  private static final String SALER_CJJ_SQL = "select *  from vw_saler_cjj_two a  WHERE 1=1 and personid=";//差价奖

  private static final String SALER_TCJ_SQL ="select *  from VW_SALER_TCJ_TWO a  WHERE 1=1 and personid=";

  private static final String SALER_TCJ_INIT_SQL ="select *  from vw_saler_tcj_init2 a  WHERE 1=1 and personid=";

  private static final String SALER_TSLXL_SQL = " select sum(nvl(b.hlts,0)*nvl(b.jje,0)*c.fundxs*0.01) XSA from xs_td a,xs_tdhw b,xs_khdjxs c,xs_khxyed d where a.tdid=b.tdid  AND a.zt=8 AND c.xydj=d.xydj AND a.dwtxid=d.dwtxid ";//销售允许天数利息
  private static final String SALER_TOTAL_SL_SQL = "select sum(nvl(b.sl,0))sl from xs_td a,xs_tdhw b,dwtx e,dwdq f where a.tdid=b.tdid  AND a.dwtxid=e.dwtxid AND e.dqh=f.dqh AND f.sfws=1 ";//省外销售数量
  //省外初贴

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
  //private static final String SALER_LX_SQL = "select a.tdrq,sum(nvl(b.jje,0)*c.fundxs*0.01)jje from xs_td a,xs_tdhw b,xs_khdjxs c,xs_khxyed d where a.tdid=b.tdid  AND c.xydj=d.xydj AND a.dwtxid=d.dwtxid and personid =? and a.tdrq >= to_date('?','YYYY-MM-DD') AND a.tdrq<to_date('?','YYYY-MM-DD') and a.zt in(2,3,8) group by a.tdrq";//销售利息
  //private static final String SALER_LQJ_SQL = "select sum(nvl(b.jsje,0)*g.hktcl*c.adjustxs*0.15) je from cw_xsjs a,cw_xsjshx b, xs_khdjxs c,xs_khxyed d,xs_tdhw e,xs_wzdj g WHERE a.xsjsid=b.xsjsid AND a.zt=8 AND c.xydj=d.xydj AND a.dwtxid=d.dwtxid  AND e.wzdjid=g.wzdjid  ";

  private static final String RLL_SQL = "select rll from xs_jjjsgs";//引入日利率
  private static final String JSFS_SQL = "select jsfs from xs_jjjsgs";//引入结算方式(按提单还是按发票计算月初余额)



  //private static final String SALER_TSLXL_SQL = "select sum(nvl(b.hlts,0)*nvl(b.jje,0))XSA from xs_td a,xs_tdhw b where a.tdid=b.tdid  AND a.zt=8 ";//销售允许天数利息
  /*
  private static final String SALER_YCYE_SQL =
      "SELECT nvl(jf,0)+nvl(ysk,0)-nvl(df,0)ye  FROM  "
      +"("
      +"  SELECT "
      +"  ("
      +"    SELECT SUM(nvl(m.jshj,0)) FROM cw_xsfp f ,cw_xsfpmx m WHERE f.kprq< to_date('?','yyyy-mm-dd') AND f.xsfpid=m.xsfpid   AND f.personid=? group by f.personid"
      +"  ) jf,"
      +" ("
      +" SELECT SUM(nvl(x.ysk,0))ysk FROM xs_ysk x WHERE x.personid=? group by x.personid "
      +") ysk,   "
      +" (SELECT SUM(nvl(l.je,0)) FROM cw_xsjs l WHERE l.rq < to_date('?','yyyy-mm-dd') AND l.personid=? group by l.personid ) df    "
      +"  FROM dual )";//按发票月初余额
  private static final String TD_YCYE_SQL=
      "SELECT nvl(jf,0)+nvl(ysk,0)-nvl(df,0)ye  FROM  "
      +"("
      +" SELECT "
      +"  ("
      +"   SELECT SUM(nvl(m.jje,0)) FROM xs_td f ,xs_tdhw m WHERE f.tdrq< to_date('?','yyyy-mm-dd') AND f.tdid=m.tdid and f.zt=8   AND f.personid=? group by f.personid"
      +" ) jf,"
      +" ("
      +" SELECT SUM(nvl(x.ysk,0))ysk FROM xs_ysk x WHERE x.personid=? group by x.personid "
      +") ysk,   "
      +" ("
      +" SELECT SUM(nvl(l.je,0)) FROM cw_xsjs l WHERE l.rq < to_date('?','yyyy-mm-dd') and l.zt in(1,8) AND l.personid=? group by l.personid "
      +") df    "
      +" FROM dual )";//按提单月初余额
  //private static final String SALER_LX_SQL = "select a.tdrq,sum(nvl(b.jje,0))jje from xs_td a,xs_tdhw b where a.tdid=b.tdid and personid =? and a.tdrq >= to_date('?','YYYY-MM-DD') AND a.tdrq<to_date('?','YYYY-MM-DD') and a.zt in(8) group by a.tdrq";//销售利息
  private static final String SALER_TOTAL_SL_SQL = "select sum(nvl(b.sl,0))sl from xs_td a,xs_tdhw b,dwtx e,dwdq f where a.tdid=b.tdid  AND a.dwtxid=e.dwtxid AND e.dqh=f.dqh AND f.sfws=1 ";//省外销售数量
  private static final String JSFS_SQL = "select jsfs from xs_jjjsgs";//引入结算方式(按提单还是按发票计算月初余额)
  private static final String RLL_SQL = "select rll from xs_jjjsgs";//引入日利率
*/
  private EngineDataSet dsCjjTable = new EngineDataSet();//差价奖
  private EngineDataSet dsTcjTable = new EngineDataSet();//提成奖

  private EngineDataSet dsDetailTable = new EngineDataSet();//修改
  private RowMap dsDetailRow = new RowMap();

  private ArrayList tcj_rows= new ArrayList();
  private ArrayList ccj_rows= new ArrayList();

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
  private RowMap lxRow=null;
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_SalerPrizeDetail getInstance(HttpServletRequest request)
  {
    B_SalerPrizeDetail B_SalerPrizeDetailBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_SalerPrizeDetailBean";
      B_SalerPrizeDetailBean = (B_SalerPrizeDetail)session.getAttribute(beanName);
      if(B_SalerPrizeDetailBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        B_SalerPrizeDetailBean = new B_SalerPrizeDetail();
        //B_SalerPrizeDetailBean.qtyFormat = loginBean.getQtyFormat();
        B_SalerPrizeDetailBean.priceFormat = loginBean.getPriceFormat();
        //B_SalerPrizeDetailBean.sumFormat = loginBean.getSumFormat();
        B_SalerPrizeDetailBean.fgsid = loginBean.getFirstDeptID();
        B_SalerPrizeDetailBean.loginId = loginBean.getUserID();
        B_SalerPrizeDetailBean.loginName = loginBean.getUserName();
        //设置格式化的字段
        session.setAttribute(beanName, B_SalerPrizeDetailBean);
      }
    }
    return B_SalerPrizeDetailBean;
  }
  /**
   * 构造函数
   */
  private B_SalerPrizeDetail()
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
    setDataSetProperty(dsCjjTable,SALER_CJJ_SQL+"''");//差价奖
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(SHOW_DETAIL), new ShowDetail());
    addObactioner(String.valueOf(DETAIL_POST), new DetailPost());
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
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * Session失效时，调用的函数
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsCjjTable!=null)
    {
      dsCjjTable.close();
      dsCjjTable = null;
    }
    log = null;
  }
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }
      /*得到差价奖对象*/
  public final EngineDataSet getCjjTable()
  {
    return dsCjjTable;
  }
  public final RowMap getDetailRow()
  {
    return dsDetailRow;
  }
      /*得到利息对象*/
  public final RowMap getLxRow()
  {
    return lxRow;
  }
  public final  RowMap[] getTcjRows()
  {
    RowMap[] rows = new RowMap[tcj_rows.size()];
    tcj_rows.toArray(rows);
    return rows;
  }
  public final  RowMap[] getCjjRows()
  {
    RowMap[] rows = new RowMap[ccj_rows.size()];
    ccj_rows.toArray(rows);
    return rows;
  }
  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      String jjzbid = request.getParameter("jjzbid");
      String personid = request.getParameter("personid");
      if(jjzbid.equals(""))
        return;
      if(personid.equals(""))
        return;
      EngineDataSet tmp = new EngineDataSet();
      setDataSetProperty(tmp,MASTER_SQL+jjzbid+"'");
      tmp.open();
      if(tmp.getRowCount()<1)
        return;
      tmp.first();
      String nf=tmp.getValue("nf");
      String yf=tmp.getValue("yf");
      int dnf=Integer.parseInt(nf);
      int dyf = Integer.parseInt(yf);
      String startDate = nf+"-"+yf+"-1";
      String endDate = dnf+"-"+(dyf+1)+"-1";
      if(dyf==12)
        endDate = (dnf+1)+"-1-1";

      Date stdate = new SimpleDateFormat("yyyy-MM-dd").parse(nf+"-"+yf+"-1");//当前月份第一天
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(stdate);
      int days = calendar.getActualMaximum(Calendar.DATE);//当前月份的天数

      if(dsCjjTable.isOpen())
        dsCjjTable.close();
      setDataSetProperty(dsCjjTable,SALER_CJJ_SQL+personid+" and rq>=to_date('"+startDate+"','yyyy-mm-dd') and rq<to_date('"+endDate+"','yyyy-mm-dd')");
      dsCjjTable.open();

      if(ccj_rows.size()>0)
        ccj_rows.clear();
      RowMap cjrow=null;
      double sss=0;
      double dxslx = 0;
      dsCjjTable.first();
      for(int h=0;h<dsCjjTable.getRowCount();h++)
      {
        cjrow=new RowMap();
        Date tmpdate = new SimpleDateFormat("yyyy-MM-dd").parse(dsCjjTable.getValue("rq"));
        Calendar caldar = new GregorianCalendar();
        caldar.setTime(tmpdate);
        int day = caldar.get(Calendar.DAY_OF_MONTH);//
        int cdays = caldar.getActualMaximum(Calendar.DATE);//当前月份的天数
        sss=(cdays-day)*Double.parseDouble(dsCjjTable.getValue("jje"));
        dxslx = dxslx+(cdays-day)*Double.parseDouble(dsCjjTable.getValue("jje"));
        //dxsje = dxsje+Double.parseDouble(dsXSLX.getValue("jje"));ccj_rows
        String cjtcl = dsCjjTable.getValue("cjtcl");
        cjrow.put("tdbh",dsCjjTable.getValue("tdbh"));
        cjrow.put("rq",dsCjjTable.getValue("rq"));
        cjrow.put("khlx",dsCjjTable.getValue("khlx"));
        cjrow.put("cpbm",dsCjjTable.getValue("cpbm"));
        cjrow.put("pm",dsCjjTable.getValue("pm"));
        cjrow.put("gg",dsCjjTable.getValue("gg"));
        cjrow.put("sl",dsCjjTable.getValue("sl"));
        cjrow.put("jldw",dsCjjTable.getValue("jldw"));
        cjrow.put("dj",dsCjjTable.getValue("dj"));
        cjrow.put("jje",dsCjjTable.getValue("jje"));
        cjrow.put("cjj",dsCjjTable.getValue("cjj"));
        cjrow.put("jzj",dsCjjTable.getValue("jzj"));
        cjrow.put("xscj",dsCjjTable.getValue("xscj"));
        cjrow.put("cjtcl",dsCjjTable.getValue("cjtcl"));
        cjrow.put("jxts",dsCjjTable.getValue("jxts"));
        cjrow.put("hltcl",dsCjjTable.getValue("hltcl"));
        cjrow.put("yxtslx",dsCjjTable.getValue("yxtslx"));
        cjrow.put("tdhwid",dsCjjTable.getValue("tdhwid"));
        cjrow.put("hlts",dsCjjTable.getValue("hlts"));
        cjrow.put("xslx",formatNumber(String.valueOf(sss), priceFormat));
        if(cjtcl.equals("")||cjtcl.equals("0"))
          cjrow.put("xscj","0");

        ccj_rows.add(cjrow);

        dsCjjTable.next();
      }


      dsTcjTable= new EngineDataSet();
      setDataSetProperty(dsTcjTable,SALER_TCJ_SQL+personid+" and rq>=to_date('"+startDate+"','yyyy-mm-dd') and rq<to_date('"+endDate+"','yyyy-mm-dd')");
      dsTcjTable.open();

      EngineDataSet dsinittable = new EngineDataSet();
      setDataSetProperty(dsinittable,SALER_TCJ_INIT_SQL+personid+" and rq>=to_date('"+startDate+"','yyyy-mm-dd') and rq<to_date('"+endDate+"','yyyy-mm-dd')");
      dsinittable.open();

      tcj_rows = new ArrayList();
      double zhllx=0;
      RowMap detail = null;
      dsTcjTable.first();
      for(int i=0;i<dsTcjTable.getRowCount();i++)
      {
        detail = new RowMap();
        detail.put("djh",dsTcjTable.getValue("djh"));
        detail.put("rq",dsTcjTable.getValue("rq"));
        detail.put("dqmc",dsTcjTable.getValue("dqmc"));
        detail.put("dwdm",dsTcjTable.getValue("dwdm"));
        detail.put("dwmc",dsTcjTable.getValue("dwmc"));
        detail.put("khlx",dsTcjTable.getValue("khlx"));
        detail.put("jsfs",dsTcjTable.getValue("jsfs"));
        detail.put("jsdh",dsTcjTable.getValue("jsdh"));
        detail.put("je",dsTcjTable.getValue("je"));
        detail.put("tcl",dsTcjTable.getValue("tcl"));
        detail.put("tcj",dsTcjTable.getValue("tcj"));

        detail.put("yh",dsTcjTable.getValue("yh"));
        detail.put("zh",dsTcjTable.getValue("zh"));
        detail.put("bz",dsTcjTable.getValue("bz"));

        Date tmpdate = new SimpleDateFormat("yyyy-MM-dd").parse(dsTcjTable.getValue("rq"));
        Calendar caldar = new GregorianCalendar();
        caldar.setTime(tmpdate);
        int day = caldar.get(Calendar.DAY_OF_MONTH);//
        int cdays = caldar.getActualMaximum(Calendar.DATE);//当前月份的天数
        double dhllx=(cdays-day)*Double.parseDouble(dsTcjTable.getValue("je"));
        zhllx = zhllx+dhllx;


        detail.put("dhllx",String.valueOf(dhllx));


        tcj_rows.add(detail);
        dsTcjTable.next();
      }

      dsinittable.first();
      for(int i=0;i<dsinittable.getRowCount();i++)
      {
        detail = new RowMap();
        detail.put("djh",dsinittable.getValue("djh"));
        detail.put("rq",dsinittable.getValue("rq"));
        detail.put("dqmc",dsinittable.getValue("dqmc"));
        detail.put("dwdm",dsinittable.getValue("dwdm"));
        detail.put("dwmc",dsinittable.getValue("dwmc"));
        detail.put("khlx",dsinittable.getValue("khlx"));
        detail.put("jsfs",dsinittable.getValue("jsfs"));
        detail.put("jsdh",dsinittable.getValue("jsdh"));
        detail.put("je",dsinittable.getValue("je"));
        detail.put("tcl",dsTcjTable.getValue("tcl"));
        detail.put("tcj",dsinittable.getValue("tcj"));

        detail.put("yh",dsinittable.getValue("yh"));
        detail.put("zh",dsinittable.getValue("zh"));
        detail.put("bz",dsinittable.getValue("bz"));

        tcj_rows.add(detail);
        dsinittable.next();
      }
      lxRow = new RowMap();
      String SQL = SALER_TSLXL_SQL+" AND a.personid="+personid+" AND  a.tdrq>=to_date('"+startDate+"','YYYY-MM-DD') AND a.tdrq<to_date('"+endDate+"','YYYY-MM-DD') group by a.personid";
      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SQL);
      dssl.open();
      dssl.first();

      String xsa = dssl.getValue("XSA").equals("")?"0":dssl.getValue("XSA");///days
      //drow.put("XSA",formatNumber(dssl.getValue("XSA").equals("")?"0":dssl.getValue("XSA"), priceFormat));
      lxRow.put("XSA",new BigDecimal(xsa).divide(new BigDecimal(days),2));

      //计算省外销售数量
      SQL = SALER_TOTAL_SL_SQL+" AND a.personid="+personid+" AND  a.tdrq>=to_date('"+startDate+"','YYYY-MM-DD') AND a.tdrq<to_date('"+endDate+"','YYYY-MM-DD') group by a.personid";
      EngineDataSet dstotalsl = new EngineDataSet();
      setDataSetProperty(dstotalsl,SQL);
      dstotalsl.open();
      dstotalsl.first();
      String sl = dstotalsl.getValue("sl").equals("")?"0":dstotalsl.getValue("sl");
      lxRow.put("sl",formatNumber(sl, qtyFormat));

      //计算销售回笼利息xsb
      //(当月的总天数-当天的日期)*结算金额
      //按实际收回金额计算
          /*
          SQL = "select a.rq,nvl(b.jsje,0)jsje from cw_xsjs a,cw_xsjshx b where a.xsjsid = b.xsjsid and  a.personid="+personid+" AND  a.rq>=to_date('"+startDate+"','YYYY-MM-DD') AND a.rq<to_date('"+endDate+"','YYYY-MM-DD')";
          EngineDataSet dshlls = new EngineDataSet();
          setDataSetProperty(dshlls,SQL);
          dshlls.open();
          double s=0;
          dshlls.first();
          for(int j=0;j<dshlls.getRowCount();j++)
          {
            Date tmpdate = new SimpleDateFormat("yyyy-MM-dd").parse(dshlls.getValue("rq"));
            Calendar caldar = new GregorianCalendar();
            caldar.setTime(tmpdate);
            int day = caldar.get(Calendar.DAY_OF_MONTH);
            int cdays = caldar.getActualMaximum(Calendar.DATE);//当前月份的天数
            s=(cdays-day)*Double.parseDouble(dshlls.getValue("jsje"));
            dshlls.next();
          }
          */
      String xsb = String.valueOf(zhllx/days);
      //lxRow.put("XSB",formatNumber(xsb, priceFormat));
      lxRow.put("XSB",xsb);


      String jsfs =  dataSetProvider.getSequence(JSFS_SQL);//结算方式

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
      //String xsc = String.valueOf(days*Double.parseDouble(ye));
      String xsc = ye;
      //lxRow.put("XSC",formatNumber(xsc, priceFormat));
      lxRow.put("XSC",xsc);

      //计算总销售金额
      //计算销售利息
          /*
          SQL = combineSQL(SALER_LX_SQL,"?",new String[]{personid,startDate,endDate});
          EngineDataSet dsXSLX = new EngineDataSet();
          setDataSetProperty(dsXSLX,SQL);
          dsXSLX.open();
      //double dxsje=0;
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
      //dxsje = dxsje+Double.parseDouble(dsXSLX.getValue("jje"));
            dsXSLX.next();
          }
          */
      String xsd = String.valueOf(dxslx/days);
      //lxRow.put("XSD",formatNumber(xsd, priceFormat));
      lxRow.put("XSD",xsd);



      String rll = dataSetProvider.getSequence(RLL_SQL);//日利率
      if(rll==null||rll.equals(""))
        rll="0";
      BigDecimal br = new BigDecimal(rll);
      BigDecimal bd = new BigDecimal(days);
      rll = (br.divide(bd,6,BigDecimal.ROUND_HALF_UP)).toString();

      try{
        EngineDataSet dsrll = new EngineDataSet();
        setDataSetProperty(dsrll,"select rll from xs_jj where jjzbid='"+jjzbid+"'");
        dsrll.open();
        dsrll.first();
        if(!dsrll.getValue("rll").equals("")&&!dsrll.getValue("rll").equals("0"))
          rll=dsrll.getValue("rll");
        }catch(Exception e){}

        lxRow.put("rll",rll);
        lxRow.put("days",String.valueOf(days));

        lxRow.put("lxj",String.valueOf((Double.parseDouble(xsa)+Double.parseDouble(xsb)-Double.parseDouble(xsc)-Double.parseDouble(xsd))*(Double.parseDouble(rll))));


    }
  }
  class ShowDetail implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      String tdhwid = request.getParameter("rownum");
      //SELECT * FROM xs_tdhw WHERE tdhwid='1595'
      if(dsDetailTable.isOpen())
        dsDetailTable.close();
      setDataSetProperty(dsDetailTable,"SELECT * FROM xs_tdhw WHERE tdhwid='"+tdhwid+"'");
      dsDetailTable.open();
      dsDetailTable.first();
      dsDetailRow = new RowMap(dsDetailTable);
    }
  }
  class DetailPost implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      if(!dsDetailTable.isOpen())
        return;
      String tdhwid = request.getParameter("tdhwid");
      if(!dsDetailTable.getValue("tdhwid").equals(tdhwid))
        return;
      String hlts = request.getParameter("hlts");
      String jxts = request.getParameter("jxts");
      String cjtcl = request.getParameter("cjtcl");
      String jzj = request.getParameter("jzj");
      String hltcl = request.getParameter("hltcl");
      dsDetailTable.setValue("hlts",hlts);
      dsDetailTable.setValue("jxts",jxts);
      dsDetailTable.setValue("cjtcl",cjtcl);
      dsDetailTable.setValue("hltcl",hltcl);
      dsDetailTable.setValue("jzj",jzj);
      dsDetailTable.post();
      dsDetailTable.saveChanges();
      dsDetailRow = new RowMap(dsDetailTable);
    }
  }
}