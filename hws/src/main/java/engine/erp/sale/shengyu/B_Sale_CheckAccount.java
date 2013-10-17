package engine.erp.sale.shengyu;

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
public final class B_Sale_CheckAccount extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1009";


  private static final String QC_SQL = "SELECT SUM(nvl(t.jje,0))+SUM(nvl(t.fy,0))+SUM(nvl(t.ysk,0))-SUM(nvl(t.je,0))-SUM(nvl(t.tjje,0))qcje FROM VW_SALE_DZDJ t WHERE (t.rq<to_date('?','yyyy-mm-dd') OR t.rq IS NULL) AND t.fgsid='?' AND t.dwtxid='?' GROUP BY t.dwtxid";


  private static final String XS_SQL = "SELECT * FROM VW_SALE_DZDJ_XS WHERE 1=1 and (fgsid=? OR fgsid IS NULL) ? ";
  private static final String TH_SQL = "SELECT * FROM VW_SALE_DZDJ_TH WHERE 1=1 and (fgsid=? OR fgsid IS NULL) ? ";
  private static final String QT_SQL = "SELECT * FROM VW_SALE_DZDJ_QT WHERE 1=1 and (fgsid=? OR fgsid IS NULL) ? ";
  private static final String JS_SQL = "SELECT * FROM VW_SALE_DZDJ_JS WHERE 1=1 and (fgsid=? OR fgsid IS NULL) ? ";


  private ArrayList d_XsInfos = null; //多行记录的引用
  //private ArrayList d_JsInfos = null;
  //private ArrayList d_ThInfos = null;
  //private ArrayList d_QtInfos = null;
  public  String retuUrl = null;//点击返回按钮的URL
  private boolean isInitQuery = false;
  public  String loginName = ""; //登录员工的姓名
  public  String personid = ""; //登录员工personid
  private String fgsid = null;   //分公司ID
  private QueryFixedItem fixedQuery = new QueryFixedItem();//定义固定查询类
  public String currentyear="";//当前
  //public RowMap masterRow = new RowMap();
  private String qcje = "0";
  public RowMap masterRow = new RowMap();
  private       String yf = "";
  /**
   * 得到物资销售单价信息的实例
   * @param request jsp请求
   * @return 返回物资销售单价信息的实例
   */
  public static B_Sale_CheckAccount getInstance(HttpServletRequest request)
  {
    B_Sale_CheckAccount b_Sale_CheckAccountBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_Sale_CheckAccountBean";//名称-值对应
      b_Sale_CheckAccountBean = (B_Sale_CheckAccount)session.getAttribute(beanName);
      if(b_Sale_CheckAccountBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsID = loginBean.getFirstDeptID();
        b_Sale_CheckAccountBean = new B_Sale_CheckAccount(fgsID);//调用构造函数
        b_Sale_CheckAccountBean.loginName = loginBean.getUserName();
        b_Sale_CheckAccountBean.personid=loginBean.getUserID();
        session.setAttribute(beanName,b_Sale_CheckAccountBean);
      }
    }
    return b_Sale_CheckAccountBean;
  }
  /**
   * 构造函数(实例变量:分公司ID为初始化参数)
   */
  private B_Sale_CheckAccount(String fgsid)
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
    addObactioner(String.valueOf(INIT), new B_Sale_CheckAccount_Init());//初始化
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
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isInit,
                                 EngineDataSet dsXsTable,
                                 EngineDataSet dsJsTable,
                                 EngineDataSet dsThTable,
                                 EngineDataSet dsQtTable )
      throws java.lang.Exception
  {
    /**本月销售**/
    double zxsje = 0;

    int xsCount = dsXsTable.getRowCount();
    int jsCount = dsJsTable.getRowCount();
    int thCount = dsThTable.getRowCount();
    int qtCount = dsQtTable.getRowCount();

    int maxCount = xsCount > jsCount ? xsCount : jsCount;
    maxCount = maxCount > thCount ? maxCount : thCount;
    maxCount = maxCount > qtCount ? maxCount : qtCount;
    double zjje = 0;
    double zjsje = 0;
    double zthje = 0;
    double zfy = 0;

    double pzjje = 0;
    double pzjsje = 0;
    double pzthje = 0;
    double pzfy = 0;

    if(d_XsInfos == null)
      d_XsInfos = new ArrayList(maxCount);
    if(isInit)
      d_XsInfos.clear();
    else
    {
      dsXsTable.first();
      for(int i=0; i<maxCount; i++)
      {
        RowMap row = new RowMap();
        if(i < xsCount)
        {
          dsXsTable.goToRow(i);
          row.put(dsXsTable);
          String jje = dsXsTable.getValue("jje");
          zjje = zjje+Double.parseDouble(jje.equals("")?"0":jje);
          pzjje = pzjje+Double.parseDouble(jje.equals("")?"0":jje);
        }
        /**本月收款**/
        if(i < jsCount){
          dsJsTable.goToRow(i);
          row.put(dsJsTable);
          String je = dsJsTable.getValue("jsje");
          zjsje = zjsje+Double.parseDouble(je.equals("")?"0":je);
          pzjsje = pzjsje+Double.parseDouble(je.equals("")?"0":je);
        }
        /**本月退货**/
        if(i < thCount){
          dsThTable.goToRow(i);
          row.put(dsThTable);
          String thjje = dsThTable.getValue("thjje");
          zthje = zthje+Double.parseDouble(thjje.equals("")?"0":thjje);
          pzthje = pzthje+Double.parseDouble(thjje.equals("")?"0":thjje);
        }
        /**其他应收款**/
        if(i < qtCount){
          dsQtTable.goToRow(i);
          row.put(dsQtTable);
          String fy = dsQtTable.getValue("fy");
          zfy= zfy+Double.parseDouble(fy.equals("")?"0":fy);
          pzfy= pzfy+Double.parseDouble(fy.equals("")?"0":fy);
        }
        d_XsInfos.add(row);
      }
    }



    masterRow = new RowMap();
    double dqcje = 0;
    masterRow.put("zjje",engine.util.Format.formatNumber(String.valueOf(zjje),"#0.00"));
    masterRow.put("zjsje",engine.util.Format.formatNumber(String.valueOf(zjsje),"#0.00"));
    masterRow.put("zthje",engine.util.Format.formatNumber(String.valueOf(zthje),"#0.00"));
    masterRow.put("zfy",engine.util.Format.formatNumber(String.valueOf(zfy),"#0.00"));

    if(qcje!=null&&!qcje.equals(""))
    {
      masterRow.put("qcje",engine.util.Format.formatNumber(String.valueOf(qcje),"#0.00"));
      dqcje = Double.parseDouble(qcje);
    }
    else
    {
      masterRow.put("qcje","0.00");
    }

    String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    masterRow.put("czrq",today);
    masterRow.put("czy",loginName);
    masterRow.put("zqk",engine.util.Format.formatNumber(String.valueOf(zjje+dqcje-zjsje-zthje+zfy),"#0.00"));




  }
  /**
   * 初始化操作的触发类
   */
  class B_Sale_CheckAccount_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      GregorianCalendar calendar=new GregorianCalendar();
      currentyear=String.valueOf(calendar.get(Calendar.YEAR));
      String cutyf = String.valueOf(calendar.get(Calendar.MONTH)+1);
      cutyf = cutyf.length()==1?"0"+cutyf:cutyf;
      qcje = "";


      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      row.put("nf",currentyear);
      row.put("yf",cutyf);
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
      String nf = request.getParameter("nf");
      yf = request.getParameter("yf");
      String dwtxid = request.getParameter("dwtxid");
      String dwmc = request.getParameter("dwmc");
      if(nf.equals("")||yf.equals("")||dwtxid.equals(""))
      {
        data.setMessage(showJavaScript("alert('缺少查询条件!')"));
        return;
      }
      Date stdate = new SimpleDateFormat("yyyy-MM-dd").parse(nf+"-"+yf+"-1");//当前月份第一天
      Calendar calendar = new GregorianCalendar();
      calendar.setTime(stdate);
      int days = calendar.getActualMaximum(Calendar.DATE);//当前月份的天数
      engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(request,engine.project.SysConstant.BEAN_CORP);

      corpBean.regData(new String[]{dwtxid});
      RowMap corprow = corpBean.getLookupRow(dwtxid);
      String addr = corprow.get("addr");
      String tel = corprow.get("tel");
      String cz = corprow.get("cz");

/*
      String qcsql = "select t.ysk FROM xs_ysk t WHERE t.dwtxid=? AND t.fgsid=?";
      qcsql = combineSQL(qcsql,"?",new String[]{dwtxid,fgsid});
      String ysk = dataSetProvider.getSequence(qcsql);
      if(ysk!=null)
        qcje = ysk;
      */



      initQueryItem(data.getRequest());
      QueryBasic queryBasic = fixedQuery;
      queryBasic.setSearchValue(data.getRequest());
      String SQL = queryBasic.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND " + SQL;

      EngineDataSet dsXsTable = new EngineDataSet();
      EngineDataSet dsJsTable = new EngineDataSet();
      EngineDataSet dsThTable = new EngineDataSet();
      EngineDataSet dsQtTable = new EngineDataSet();
      EngineDataSet dsSumTable = new EngineDataSet();

      String sql = combineSQL(XS_SQL, "?", new String[]{fgsid, SQL}) ;
      setDataSetProperty(dsXsTable,sql);
      dsXsTable.openDataSet();

      sql = combineSQL(JS_SQL, "?", new String[]{fgsid, SQL}) ;
      setDataSetProperty(dsJsTable, sql);
      dsJsTable.openDataSet();

      sql = combineSQL(TH_SQL, "?", new String[]{fgsid, SQL}) ;
      setDataSetProperty(dsThTable, sql);
      dsThTable.openDataSet();


      sql = combineSQL(QT_SQL, "?", new String[]{fgsid, SQL}) ;
      setDataSetProperty(dsQtTable, sql);
      dsQtTable.openDataSet();

      sql = combineSQL(QC_SQL, "?", new String[]{nf+"-"+yf+"-01",fgsid, dwtxid}) ;

      qcje = dataSetProvider.getSequence(sql);


      initRowInfo(true, dsXsTable, dsJsTable, dsThTable, dsQtTable);
      initRowInfo(false, dsXsTable, dsJsTable, dsThTable, dsQtTable);

      masterRow.put("nf",nf);
      masterRow.put("yf",yf);
      masterRow.put("dwmc",dwmc);
      masterRow.put("startday","1");
      masterRow.put("endday",days+"");
      masterRow.put("addr",addr);
      masterRow.put("tel",tel);
      masterRow.put("cz",cz);



      dsXsTable.closeDataSet();
      dsJsTable.closeDataSet();
      dsThTable.closeDataSet();
      dsQtTable.closeDataSet();
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
        new QueryColumn(new Column("dwtxid", "", Variant.BIGDECIMAL), null, null, null, null, "="),
        new QueryColumn(new Column("nf", "", Variant.BIGDECIMAL), null, null, null, null, "="),
        new QueryColumn(new Column("yf", "", Variant.BIGDECIMAL), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }
}
