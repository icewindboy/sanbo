package engine.erp.store.xixing;



import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import java.util.*;
import engine.html.*;

import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.project.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.*;
import com.borland.dx.dataset.*;
import java.util.Hashtable;
import javax.servlet.*;
import engine.report.util.TempletData;
import engine.report.util.ContextData;
import engine.dataset.*;
import engine.report.event.ReportDataLoadingListener;
import engine.report.event.TempletProvideResponse;
import engine.report.event.TempletAfterProvideListener;
import engine.dataset.sql.QueryWhere;
import engine.util.MessageFormat;
import engine.report.util.ReportData;
/**
 * <p>Title: 销售管理-核对帐款</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @version 1.0
 */
public final class B_DeptAssistantMaterialCollect extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1009";
  private static final String KC_SRMX_SQL = "SELECT * FROM vw_bm_kc_sy_fc WHERE 1=1 and  ? ";
  private static final String KC_CQ_SQL = "SELECT sum(nvl(sl,0))sl,sum(nvl(je,0))je FROM vw_bm_kc_sy_fc  where 1=1 ? ";
  private static final String KC_DM_SQL =" select cpid,deptid from vw_bm_kc_sy_fc where 1=1 ? ";
  private ArrayList d_XsInfos = null; //多行记录的引用

  private static final String KC_SRMX_SQL_NEW = "SELECT sum(nvl(sl,0))sl,cpbm,product,jldw ,mc FROM vw_bm_kc_sy_fc WHERE 1=1 and  ? ";

  public  String retuUrl = null;//点击返回按钮的URL
  private boolean isInitQuery = false;
  public  String loginName = ""; //登录员工的姓名
  public  String personid = ""; //登录员工personid
  private String fgsid = null;   //分公司ID
  private QueryFixedItem fixedQuery = new QueryFixedItem();//定义固定查询类
  public RowMap masterRow = new RowMap();
  /**
   * 得到物资销售单价信息的实例
   * @param request jsp请求
   * @return 返回物资销售单价信息的实例
   */
  public static B_DeptAssistantMaterialCollect getInstance(HttpServletRequest request)
  {
    B_DeptAssistantMaterialCollect B_DeptAssistantMaterialCollectBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_DeptAssistantMaterialCollectBean";//名称-值对应
      B_DeptAssistantMaterialCollectBean = (B_DeptAssistantMaterialCollect)session.getAttribute(beanName);
      if(B_DeptAssistantMaterialCollectBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsID = loginBean.getFirstDeptID();
        B_DeptAssistantMaterialCollectBean = new B_DeptAssistantMaterialCollect(fgsID);//调用构造函数
        B_DeptAssistantMaterialCollectBean.loginName = loginBean.getUserName();
        B_DeptAssistantMaterialCollectBean.personid=loginBean.getUserID();
        session.setAttribute(beanName,B_DeptAssistantMaterialCollectBean);
      }
    }
    return B_DeptAssistantMaterialCollectBean;
  }
  /**
   * 构造函数(实例变量:分公司ID为初始化参数)
   */
  private B_DeptAssistantMaterialCollect(String fgsid)
  {
    this.fgsid = fgsid;
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
    addObactioner(String.valueOf(INIT), new B_DeptAssistantMaterialCollect_Init());//初始化
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());//查询
  }
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
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    d_XsInfos = null; //多行记录的引用
    log = null;
  }
  /*本月销售*/
  public final RowMap[] getXsRowinfos() {
    RowMap[] rows = new RowMap[d_XsInfos.size()];
    d_XsInfos.toArray(rows);
    return rows;
  }
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
  {
    return getClass();
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
   * 初始化操作的触发类
   */
  class B_DeptAssistantMaterialCollect_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      masterRow = new RowMap();
      d_XsInfos = new ArrayList(); //多行记录的引用
    }
  }
  /**
   * 添加查询操作的触发类
   */
  class FIXED_SEARCH implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      masterRow = new RowMap();
      //String cpid = request.getParameter("cpid");
      //String cpbm = request.getParameter("cpbm");
      //String product = request.getParameter("product");
      String storeid = request.getParameter("storeid");
      String deptid2 = request.getParameter("deptid");
      String ksrq = request.getParameter("ksrq");
      String jsrq = request.getParameter("jsrq");
      masterRow.put("ksrq",ksrq);
      masterRow.put("jsrq",jsrq);
      d_XsInfos = new ArrayList();

      if(ksrq.equals("")||jsrq.equals("")||storeid.equals("")||deptid2.equals(""))
      {
        data.setMessage(showJavaScript("alert('缺少查询条件!')"));
        return;
      }
      initQueryItem(data.getRequest());
      QueryBasic queryBasic = fixedQuery;
      queryBasic.setSearchValue(data.getRequest());
      String SQL = "";//queryBasic.getWhereQuery();
      ////////*************物资数*********************/////////

      String cksql = "select cpid,cpbm,product,jldw from vw_bm_kc_sy_fc where  storeid='"+storeid+"'   group by cpid,cpbm,product,jldw order by cpbm ";

      EngineDataSet tdscqck = new EngineDataSet();
      //String cksql = combineSQL(KC_DM_SQL, "?", new String[]{" and deptid='"+deptid2+"' and  sfrq>=to_date('"+ksrq+"','yyyy-mm-dd') and  sfrq<=to_date('"+jsrq+"','yyyy-mm-dd') and storeid='"+storeid+"' group by cpid,deptid"}) ;
      setDataSetProperty(tdscqck,cksql);
      tdscqck.openDataSet();
      tdscqck.first();
      for(int i=0;i<tdscqck.getRowCount();i++)
      {
        String cpid = tdscqck.getValue("cpid");
        String jldw = tdscqck.getValue("jldw");
        String cpbm = tdscqck.getValue("cpbm");
        String product = tdscqck.getValue("product");
        getData(cpid,ksrq,jsrq,storeid,deptid2,cpbm,product,jldw);
        tdscqck.next();
      }

    }
    /**
     *
     * 计算
     *
     **/

    public void getData(String cpid,String ksrq,String jsrq,String storeid,String deptid,String cpbm,String product,String jldw)
    {

      masterRow = new RowMap();


      String mc = "";


      masterRow.put("ksrq",ksrq);
      masterRow.put("jsrq",jsrq);

      ArrayList tmparraylist = new ArrayList();
      String cqdj="";
      String qcsl="";
      String qcje="";

      String SQL = "";//queryBasic.getWhereQuery();
      ////////*************计算期初*********************/////////

        EngineDataSet mdscqck = new EngineDataSet();
        String tcksql = combineSQL(KC_CQ_SQL, "?", new String[]{"  and cpid="+cpid+" and  storeid="+storeid+" and djlb=1 and sfrq<to_date('"+jsrq+"','yyyy-mm-dd') group by cpid"}) ;
        setDataSetProperty(mdscqck,tcksql);
        mdscqck.openDataSet();
        mdscqck.first();
        String tqcsl = mdscqck.getValue("sl");
        String tqcje = mdscqck.getValue("je");
        if(tqcsl.equals("")||tqcje.equals("")||tqcsl.equals("0")||tqcsl.equals("0"))
          cqdj="0";
        else
        cqdj =String.valueOf(Double.parseDouble(tqcje)/Double.parseDouble(tqcsl));


      /*
        EngineDataSet tdscqck = new EngineDataSet();
        String cksql = combineSQL(KC_CQ_SQL, "?", new String[]{" and cpid="+cpid+" and storeid='"+storeid+"' and sfrq<to_date('"+ksrq+"','yyyy-mm-dd') group by cpid"}) ;
        setDataSetProperty(tdscqck,cksql);
        tdscqck.openDataSet();
        if(tdscqck.getRowCount()==0||tqcsl.equals("0")||tqcsl.equals("0"))
        {
          qcsl = "0";
          qcje = "0";
          //cqdj = "0";
        }
        else
        {
          tdscqck.first();
          qcsl = tdscqck.getValue("sl").equals("")?"0":tdscqck.getValue("sl");
          String sl = tdscqck.getValue("sl").equals("")?"0":tdscqck.getValue("sl");
          qcje = String.valueOf(Double.parseDouble(cqdj)*Double.parseDouble(sl));
      }



      RowMap crqorw = new RowMap();


      crqorw.put("sfrq","");
      crqorw.put("sfdjdh","");

      crqorw.put("cqsl",qcsl);
      crqorw.put("qcje",engine.util.Format.formatNumber(qcje,"#.00"));

      crqorw.put("srsl","");
      crqorw.put("srsl","");

      crqorw.put("fcsl","");
      crqorw.put("fcje","");

      crqorw.put("bqsl",qcsl);
      crqorw.put("bqje",engine.util.Format.formatNumber(qcje,"#.00"));
      tmparraylist.add(crqorw);
      */

      /***********************计算期中*****************************/

      EngineDataSet dsczrc = new EngineDataSet();
      String srcsql = combineSQL(KC_SRMX_SQL_NEW, "?", new String[]{" djlb=-1 and  cpid="+cpid+"and storeid='"+storeid+"' and deptid="+deptid+" and sfrq>=to_date('"+ksrq+"','yyyy-mm-dd') and  sfrq<=to_date('"+jsrq+"','yyyy-mm-dd') group by cpbm,product,jldw,mc"}) ;
      setDataSetProperty(dsczrc,srcsql);
      dsczrc.openDataSet();
      RowMap tmp = null;
      //double ljsl =Double.parseDouble(qcsl);//累计数量
      //double ljsrsl =0;
      //double ljfcsl =0;
      //double ljje =Double.parseDouble(qcsl);//累计数量
      double dqcdj = Double.parseDouble(cqdj);//动态单价
      if(dsczrc.getRowCount()<0||dsczrc.getRowCount()==0)
      {
        /*
        String sqlf = "select * from vw_kc_dm where cpid='"+cpid+"' and chlbid=142";
        EngineDataSet dsczrcf = new EngineDataSet();
        setDataSetProperty(dsczrcf,sqlf);
        dsczrcf.openDataSet();
        dsczrcf.first();
        product = dsczrcf.getValue("product");
        jldw = dsczrcf.getValue("jldw");
        */
        tmp = new RowMap();
        tmp.put("srsl", "");
        tmp.put("srsl", "");
        tmp.put("fcsl", "0");
        tmp.put("fcje", "0");
        tmp.put("jldw", jldw);
        tmp.put("product", product);
        //tmp.put("deptid", deptid);
        tmp.put("deptid", deptid);
        d_XsInfos.add(tmp);
        return;


      }
      dsczrc.first();
      //for (int i = 0; i < dsczrc.getRowCount(); i++) {
        jldw = dsczrc.getValue("jldw");
        product = dsczrc.getValue("product");
        mc = dsczrc.getValue("mc");
        double sl = dsczrc.getValue("sl").length() > 0 ? Double.parseDouble(dsczrc.getValue("sl")) : 0; //数量
        double tmpje = dqcdj * sl;
        tmp = new RowMap();
        tmp.put("srsl", "");
        tmp.put("srsl", "");
        tmp.put("fcsl", engine.util.Format.formatNumber(sl*-1, "#.00"));
        tmp.put("fcje", engine.util.Format.formatNumber(tmpje*-1, "#.00"));
        tmp.put("jldw", jldw);
        tmp.put("product", product);
        //tmp.put("deptid", deptid);
        tmp.put("deptid", deptid);
        d_XsInfos.add(tmp);
        //tmparraylist.add(tmp);
        //dsczrc.next();
      //}





/*
      double bqsl =0;
      double bqje=0;
      for(int j=0;j<tmparraylist.size();j++)
      {
        RowMap xsrow = (RowMap)tmparraylist.get(j);

        bqsl = bqsl + Double.parseDouble(xsrow.get("cqsl").equals("")?"0":xsrow.get("cqsl"))+Double.parseDouble(xsrow.get("srsl").equals("")?"0":xsrow.get("srsl"))-+Double.parseDouble(xsrow.get("fcsl").equals("")?"0":xsrow.get("fcsl"));
        bqje = bqje + Double.parseDouble(xsrow.get("qcje").equals("")?"0":xsrow.get("qcje"))+Double.parseDouble(xsrow.get("srje").equals("")?"0":xsrow.get("srje"))-+Double.parseDouble(xsrow.get("fcje").equals("")?"0":xsrow.get("fcje"));

        xsrow.put("bqsl",engine.util.Format.formatNumber(bqsl,"#.00"));
        xsrow.put("bqje",engine.util.Format.formatNumber(bqje,"#.00"));
        xsrow.put("deptid",engine.util.Format.formatNumber(deptid,"#.00"));

      }
 */

      //double zqcsl=0;
      //double zqcje=0;

      //double zbcrksl=0;
      //double zbcrkje=0;


      /*
      double zbccksl=0;
      double zbcckje=0;

      RowMap tmprow = new RowMap();
      for(int j=0;j<tmparraylist.size();j++)
      {
        RowMap xsrow = (RowMap)tmparraylist.get(j);
        //zqcsl = zqcsl + Double.parseDouble(xsrow.get("cqsl").equals("")?"0":xsrow.get("cqsl"));
        //zqcje = zqcje + Double.parseDouble(xsrow.get("qcje").equals("")?"0":xsrow.get("qcje"));
        //zbcrksl = zbcrksl + Double.parseDouble(xsrow.get("srsl").equals("")?"0":xsrow.get("srsl"));
        //zbcrkje = zbcrkje + Double.parseDouble(xsrow.get("srje").equals("")?"0":xsrow.get("srje"));
        zbccksl = zbccksl + Double.parseDouble(xsrow.get("fcsl").equals("")?"0":xsrow.get("fcsl"));
        zbcckje = zbcckje + Double.parseDouble(xsrow.get("fcje").equals("")?"0":xsrow.get("fcje"));
      }

      //tmprow.put("cqsl",engine.util.Format.formatNumber(zqcsl,"#.00"));
      //tmprow.put("qcje",engine.util.Format.formatNumber(zqcje,"#.00"));
      //tmprow.put("srsl",engine.util.Format.formatNumber(zbcrksl,"#.00"));
      //tmprow.put("srje",engine.util.Format.formatNumber(zbcrkje,"#.00"));
      tmprow.put("fcsl",engine.util.Format.formatNumber(zbccksl,"#.00"));
      tmprow.put("fcje",engine.util.Format.formatNumber(zbcckje,"#.00"));
      //tmprow.put("bqsl",engine.util.Format.formatNumber(zqcsl+zbcrksl-zbccksl,"#.00"));
      //tmprow.put("bqje",engine.util.Format.formatNumber(zqcje+zbcrkje-zbcckje,"#.00"));
      tmprow.put("jldw",jldw);
      tmprow.put("product",product);
      tmprow.put("deptid",deptid);
      tmprow.put("mc",mc);
      //d_XsInfos.add(tmprow);

      */

    }
    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      //初始化固定的查询项目
      //往来单位dwtxId;信誉额度xyed;信誉等级xydj;回款天数hkts;
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(new Column("cpid", "", Variant.BIGDECIMAL), null, null, null, null, "="),
        new QueryColumn(new Column("ksrq", "", Variant.DATE), null, null, null, null, "="),
        new QueryColumn(new Column("jsrq", "", Variant.DATE), null, null, null, null, "="),
        new QueryColumn(new Column("deptid","",Variant.BIGDECIMAL), null, null, null, null, "="),
        new QueryColumn(new Column("storeid","",Variant.BIGDECIMAL), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }
}
