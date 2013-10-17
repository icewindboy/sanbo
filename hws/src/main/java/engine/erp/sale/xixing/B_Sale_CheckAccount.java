package engine.erp.sale.xixing;

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
import engine.common.LoginBean;
import engine.project.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;

/**
 * <p>Title: 销售管理-核对帐款</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @version 1.0
 */
public final class B_Sale_CheckAccount extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1009";
  private static final String XSDZ_SQL = "SELECT * FROM VW_SALE_DZDJ WHERE 1=1 and (fgsid=? OR fgsid IS NULL) ? ";
  private static final String XSDZ_STRUCT_SQL = "SELECT * FROM VW_SALE_DZDJ WHERE 1<>1  ";

  private EngineDataSet dsXsTable = new EngineDataSet();
  private EngineDataSet dsJsTable = new EngineDataSet();
  private EngineDataSet dsThTable = new EngineDataSet();
  private EngineDataSet dsQtTable = new EngineDataSet();

  private ArrayList d_XsInfos = null; //多行记录的引用
  private ArrayList d_JsInfos = null;
  private ArrayList d_ThInfos = null;
  private ArrayList d_QtInfos = null;
  public  String retuUrl = null;//点击返回按钮的URL
  private boolean isInitQuery = false;
  public  String loginName = ""; //登录员工的姓名
  public  String personid = ""; //登录员工personid
  private String fgsID = null;   //分公司ID
  private QueryFixedItem fixedQuery = new QueryFixedItem();//定义固定查询类
  public String currentyear="";//当前
  public RowMap masterRow = new RowMap();
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
    this.fgsID = fgsid;
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
    setDataSetProperty(dsXsTable, XSDZ_STRUCT_SQL);
    setDataSetProperty(dsJsTable, XSDZ_STRUCT_SQL);
    setDataSetProperty(dsThTable, XSDZ_STRUCT_SQL);
    setDataSetProperty(dsQtTable, XSDZ_STRUCT_SQL);

    addObactioner(String.valueOf(INIT), new B_Sale_CheckAccount_Init());//初始化
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());//查询
  }
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
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsXsTable != null){
      dsXsTable.close();
      dsXsTable = null;
    }
    if(dsJsTable != null){
      dsJsTable.close();
      dsJsTable = null;
    }
    if(dsThTable != null){
      dsThTable.close();
      dsThTable = null;
    }
    if(dsQtTable != null){
      dsQtTable.close();
      dsQtTable = null;
    }
    d_XsInfos = null; //多行记录的引用
    d_JsInfos = null;
    d_ThInfos = null;
    d_QtInfos = null;
    log = null;
  }
  /*本月销售*/
  public final RowMap[] getXsRowinfos() {
    RowMap[] rows = new RowMap[d_XsInfos.size()];
    d_XsInfos.toArray(rows);
    return rows;
  }
  /*本月收款*/
  public final RowMap[] getJsRowinfos() {
    RowMap[] rows = new RowMap[d_JsInfos.size()];
    d_JsInfos.toArray(rows);
    return rows;
  }
  /*本月退货*/
  public final RowMap[] getThRowinfos() {
    RowMap[] rows = new RowMap[d_ThInfos.size()];
    d_ThInfos.toArray(rows);
    return rows;
  }
  /*其他应收款*/
  public final RowMap[] getQtRowinfos() {
    RowMap[] rows = new RowMap[d_QtInfos.size()];
    d_QtInfos.toArray(rows);
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
  private final void initRowInfo(boolean isInit) throws java.lang.Exception
  {
    /**本月销售**/
    double zxsje = 0;
    double zjje = 0;
    if(d_XsInfos == null)
      d_XsInfos = new ArrayList(dsXsTable.getRowCount());
    if(isInit)
      d_XsInfos.clear();
    else
    {
      dsXsTable.first();
      for(int i=0; i<dsXsTable.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsXsTable);
        String xsje = dsXsTable.getValue("xsje");
        zxsje = zxsje+Double.parseDouble(xsje.equals("")?"0":xsje);
        String jje = dsXsTable.getValue("jje");
        zjje = zjje+Double.parseDouble(jje.equals("")?"0":jje);
        d_XsInfos.add(row);
        dsXsTable.next();
      }
    }
    /**本月收款**/
    double zjsje = 0;
    if(d_JsInfos == null)
      d_JsInfos = new ArrayList(dsJsTable.getRowCount());
    if(isInit)
      d_JsInfos.clear();
    else{
      dsJsTable.first();
      for(int i=0; i<dsJsTable.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsJsTable);
        String je = dsJsTable.getValue("je");
        zjsje = zjsje+Double.parseDouble(je.equals("")?"0":je);
        d_JsInfos.add(row);
        dsJsTable.next();
      }
    }
    /**本月退货**/
    double zthje = 0;
    if(d_ThInfos == null)
      d_ThInfos = new ArrayList(dsThTable.getRowCount());
    if(isInit)
      d_ThInfos.clear();
    else
    {
      dsXsTable.first();
      for(int i=0; i<dsThTable.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsThTable);
        String tjje = dsThTable.getValue("tjje");
        zthje = zthje+Double.parseDouble(tjje.equals("")?"0":tjje);
        d_ThInfos.add(row);
        dsThTable.next();
      }
    }
    /**其他应收款**/
    double zfy = 0;
    if(d_QtInfos == null)
      d_QtInfos = new ArrayList(dsQtTable.getRowCount());
    if(isInit)
      d_QtInfos.clear();
    else
    {
      dsQtTable.first();
      for(int i=0; i<dsQtTable.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsQtTable);
        String fy = dsQtTable.getValue("fy");
        zfy= zfy+Double.parseDouble(fy.equals("")?"0":fy);
        d_QtInfos.add(row);
        dsQtTable.next();
      }
    }
    masterRow.put("zxsje",String.valueOf(zxsje));
    masterRow.put("zjje",String.valueOf(zjje));
    masterRow.put("zjsje",String.valueOf(zjsje));
    masterRow.put("zthje",String.valueOf(zthje));
    masterRow.put("zfy",String.valueOf(zfy));
    //masterRow.put("zsk",String.valueOf(zjsje+zfy));
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


      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      row.put("nf",currentyear);
      row.put("yf",cutyf);
      d_XsInfos = null; //多行记录的引用
      d_JsInfos = null;
      d_ThInfos = null;
      d_QtInfos = null;
      if(!dsXsTable.isOpen())
        dsXsTable.open();
      dsXsTable.empty();

      if(!dsJsTable.isOpen())
        dsJsTable.open();
      dsJsTable.empty();

      if(!dsThTable.isOpen())
        dsThTable.open();
      dsThTable.empty();

      if(!dsQtTable.isOpen())
        dsQtTable.open();
      dsQtTable.empty();


      masterRow = new RowMap();
      initRowInfo(true);
      initRowInfo(false);
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
      String yf = request.getParameter("yf");
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
      //corpBean.regConditionData("dwtxid",new String[]{dwtxid});
      corpBean.regData(new String[]{dwtxid});
      RowMap corprow = corpBean.getLookupRow(dwtxid);
      String addr = corprow.get("addr");
      String tel = corprow.get("tel");
      String cz = corprow.get("cz");
      masterRow.put("nf",nf);
      masterRow.put("yf",yf);
      masterRow.put("dwmc",dwmc);
      masterRow.put("startday","1");
      masterRow.put("endday",days+"");
      masterRow.put("addr",addr);
      masterRow.put("tel",tel);
      masterRow.put("cz",cz);
      initQueryItem(data.getRequest());
      QueryBasic queryBasic = fixedQuery;
      queryBasic.setSearchValue(data.getRequest());
      String SQL = queryBasic.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND " + SQL;

      String sql = combineSQL(XSDZ_SQL, "?", new String[]{fgsID, SQL}) ;
      sql = sql+" and djlx in(1,2,4,5)";
      System.out.print(sql);
      if(dsXsTable.isOpen())
        dsXsTable.close();
      setDataSetProperty(dsXsTable,sql);
      dsXsTable.open();

      sql = combineSQL(XSDZ_SQL, "?", new String[]{fgsID, SQL}) ;
      sql = sql+" and djlx=8";
      if(dsJsTable.isOpen())
        dsJsTable.close();
      setDataSetProperty(dsJsTable,sql);
      dsJsTable.open();

      sql = combineSQL(XSDZ_SQL, "?", new String[]{fgsID, SQL}) ;
      sql = sql+" and djlx=-1";
      if(dsThTable.isOpen())
        dsThTable.close();
      setDataSetProperty(dsThTable,sql);
      dsThTable.open();


      sql = combineSQL(XSDZ_SQL, "?", new String[]{fgsID, SQL}) ;
      sql = sql+" and djlx=7";
      if(dsQtTable.isOpen())
        dsQtTable.close();
      setDataSetProperty(dsQtTable,sql);
      dsQtTable.open();

      initRowInfo(true);
      initRowInfo(false);
    }

    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet master = dsXsTable;
      //初始化固定的查询项目
      //往来单位dwtxId;信誉额度xyed;信誉等级xydj;回款天数hkts;
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("nf"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("yf"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }
}
