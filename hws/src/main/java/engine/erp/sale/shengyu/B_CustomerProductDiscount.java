package engine.erp.sale.shengyu;

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
 * 销售管理--客户产品折扣,
 * 定义具体客户要购买的产品的单价和折扣
 * 客户产品历史记录
 * */
public final class B_CustomerProductDiscount extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1009";
  public static final String BATCHADD     = "1010";
  public static final String BATCHPOST    = "1011";
  public static final String BATCHINIT    = "1012";
  public static final String BATCHEDIT    = "1013";

  private static final String CUSTOMER_PRODUCT_DISCOUNT_SQL = "SELECT a.* FROM xs_khcpzk a,dwtx b,kc_dm c WHERE a.dwtxid=b.dwtxid and b.isdelete=0 and a.cpid=c.cpid and a.fgsid=? ? ORDER BY b.dwdm,c.cpbm";//
  private static final String BATCH_SQL_ADD = "SELECT a.cpid,null bz,null dj,a.djlx FROM VW_SALE_WZDJ a WHERE a.cpid NOT IN(SELECT b.cpid FROM xs_khcpzk b WHERE b.fgsid=? AND b.dwtxid=?)";//
  private static final String BATCH_SQL_EDIT = "SELECT a.cpid,a.bz,a.dj,a.djlx,a.ksrq,a.jsrq FROM VW_EDIT_WZDJ a WHERE a.dwtxid=? and a.cpid IN(SELECT b.cpid FROM xs_khcpzk b WHERE b.fgsid=? AND b.dwtxid=? AND b.sflsbj=0) and a.sflsbj=0";//
  private static final String KHCPZK_STRUCT_SQL = "SELECT a.* FROM xs_khcpzk a,dwtx b,kc_dm c WHERE a.dwtxid=b.dwtxid and a.cpid=c.cpid and 1<>1 ORDER BY b.dwdm,c.cpbm  ";//
  private EngineDataSet dsxs_khcpzkTable = new EngineDataSet();
  private EngineDataSet dsBatchAddTable = new EngineDataSet();

  private EngineRow locateRow = null;
  private RowMap rowInfo = new RowMap();
  private RowMap row = new RowMap();
  public  boolean isAdd = true;
  public  boolean isBatchAdd = true;
  private long    editrow = 0;
  public  String retuUrl = null;
  private boolean isInitQuery = false;
  public  String loginName = ""; //登录员工的姓名
  private String fgsid = null;   //分公司ID
  private QueryFixedItem fixedQuery = new QueryFixedItem();
  private ArrayList d_RowInfos = null; //多行记录的引用

  public String dwdm="";//单位查询
  public String dwmc="";//单位查询
  public String cpbm="";
  public String product="";

  public String adddwtxid="";//单位查询
  public String adddwdm="";//单位查询
  public String adddwmc="";//单位查询
  public String adddj="";
  public String addsflsbj="";
  public String addksrq="";
  /**
   * 得到客户信誉额度信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回报价资料信息的实例
   */
  public static B_CustomerProductDiscount getInstance(HttpServletRequest request)
  {
    B_CustomerProductDiscount xs_khcpzkbean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "xs_khcpzkbean";
      xs_khcpzkbean = (B_CustomerProductDiscount)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(xs_khcpzkbean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsid = loginBean.getFirstDeptID();
        xs_khcpzkbean = new B_CustomerProductDiscount(fgsid);
        xs_khcpzkbean.loginName = loginBean.getUserName();
        //设置格式化的字段
        xs_khcpzkbean.dsxs_khcpzkTable.setColumnFormat("zk", loginBean.getPriceFormat());
        xs_khcpzkbean.dsxs_khcpzkTable.setColumnFormat("dj", loginBean.getPriceFormat());
        xs_khcpzkbean.dsxs_khcpzkTable.setColumnFormat("bj", loginBean.getPriceFormat());
        session.setAttribute(beanName, xs_khcpzkbean);
      }
    }
    return xs_khcpzkbean;
  }
  /**
   * 构造函数
   */
  private B_CustomerProductDiscount(String fgsid)
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
    setDataSetProperty(dsBatchAddTable, null);
    dsxs_khcpzkTable.setSequence(new SequenceDescriptor(new String[]{"khcpzkid"}, new String[]{"S_XS_KHCPZK"}));
    dsBatchAddTable.setTableName("xs_khcpzk");
    dsxs_khcpzkTable.setTableName("xs_khcpzk");

    setDataSetProperty(dsxs_khcpzkTable, combineSQL(CUSTOMER_PRODUCT_DISCOUNT_SQL, "?", new String[]{fgsid,""}));
    //dsxs_khcpzkTable.setSort(new SortDescriptor("", new String[]{"dwtxId","cpId"}, new boolean[]{false,false}, null, 0));
    //dsxs_khcpzkTable.setSequence(new SequenceDescriptor(new String[]{"cpId","dwtxId"}, new String[]{"s_xs_khcpzk","s_xs_khcpzk"}));
    //添加操作的触发对象
    dsBatchAddTable.addLoadListener(new com.borland.dx.dataset.LoadListener() {
      public void dataLoaded(LoadEvent e)
      {
        initRowInfo(false, true);
      }
    });
    B_CustomerProductDiscount_Add_Edit add_edit = new B_CustomerProductDiscount_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_CustomerProductDiscount_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_CustomerProductDiscount_Post());
    addObactioner(String.valueOf(DEL), new B_CustomerProductDiscount_Delete());
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());
    addObactioner(String.valueOf(BATCHADD), new Batch_Add_EDIT());
    addObactioner(String.valueOf(BATCHPOST), new Batch_Post());
    addObactioner(String.valueOf(BATCHINIT), new Batch_Add_Init());
    addObactioner(String.valueOf(BATCHEDIT), new Batch_Add_Init());


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
      if(dsxs_khcpzkTable.isOpen() && dsxs_khcpzkTable.changesPending())
        dsxs_khcpzkTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsxs_khcpzkTable != null){
      dsxs_khcpzkTable.close();
      dsxs_khcpzkTable = null;
    }
    log = null;
    rowInfo = null;
    locateRow = null;
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
   * 初始化行信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
private final void initRowInfo(boolean isAdd) throws java.lang.Exception
{
    //是否时添加操作
    if(rowInfo.size() > 0)
      rowInfo.clear();
    if(isAdd){
      Calendar day = new GregorianCalendar();
      int year = day.get(Calendar.YEAR);
      int month = day.get(Calendar.MONTH);
      day.clear();
      day.set(year,month+1,0);
      Date endDate = day.getTime();
      Date startDate = new Date();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
      String endday = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
      rowInfo.put("ksrq", today);
      rowInfo.put("jsrq", endday);
    }
    if(!isAdd)
      rowInfo.put(getOneTable());
}
/**
 * 初始化行信息
 * @param isAdd 是否时添加
 * @param isInit 是否从新初始化
 */
private final void initRowInfo(boolean isAdd, boolean isInit)
{
  if(d_RowInfos == null)
    d_RowInfos = new ArrayList(dsBatchAddTable.getRowCount());
  else if(isInit)
    d_RowInfos.clear();
  dsBatchAddTable.first();
  for(int i=0; i<dsBatchAddTable.getRowCount(); i++)
  {
    RowMap row = new RowMap(dsBatchAddTable);
    row.put("InternalRow", String.valueOf(dsBatchAddTable.getInternalRow()));
    d_RowInfos.add(row);
    dsBatchAddTable.next();
  }
  if(isBatchAdd){
    Calendar day = new GregorianCalendar();
    int year = day.get(Calendar.YEAR);
    int month = day.get(Calendar.MONTH);
    day.clear();
    day.set(year,month+1,0);
    Date endDate = day.getTime();
    Date startDate = new Date();
    String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
    String endday = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
    rowInfo.put("jsrq", endday);
    rowInfo.put("ksrq", today);
  }
}
/*得到表对象*/
public final EngineDataSet getOneTable()
{
  //if(!dsxs_khcpzkTable.isOpen())
  //  dsxs_khcpzkTable.open();
  return dsxs_khcpzkTable;
}
public final EngineDataSet getBatchAddTable()
{
  return dsBatchAddTable;
}
  /*得到从表多列的信息*/
public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
}
/*得到一列的信息*/
public final RowMap getRowinfo()
  {
       return rowInfo;
  }
  /*得到一列的信息*/
public final RowMap getRowBatchinfo()
  {
       return row;
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
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = new RowMap();
    rowInfo.put(request);
    int rownum = dsBatchAddTable.getRowCount();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//
      detailRow.put("djlx", rowInfo.get("djlx_"+i));//
      String bz = rowInfo.get("bz_"+i);
      String dj2 = rowInfo.get("dj_"+i);
      detailRow.put("bz", rowInfo.get("bz_"+i));//
      detailRow.put("dj", rowInfo.get("dj_"+i));//
      d_RowInfos.set(i,detailRow);
    }
  }
  /**
   * 初始化操作的触发类
   */
  class B_CustomerProductDiscount_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      fixedQuery.getSearchRow().clear();
      dwdm="";//单位查询
      dwmc="";//单位查询
      cpbm="";
      product="";

      adddwtxid="";
      adddwdm="";
      adddwmc="";
      adddj="";
      addsflsbj="";

      String SQL = combineSQL(CUSTOMER_PRODUCT_DISCOUNT_SQL, "?", new String[]{fgsid, ""});
      if(!dsxs_khcpzkTable.getQueryString().equals(SQL))
      {
        dsxs_khcpzkTable.setQueryString(SQL);
        dsxs_khcpzkTable.setRowMax(null);
      }
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
      initQueryItem(data.getRequest());
      QueryBasic queryBasic = fixedQuery;
      queryBasic.setSearchValue(data.getRequest());
      String SQL = "";//queryBasic.getWhereQuery();
      dwdm =request.getParameter("dwdm");
      dwmc =request.getParameter("dwmc");
      cpbm =request.getParameter("cpbm");
      product =request.getParameter("product");

      String dwtxid = request.getParameter("dwtxid");
      String cpid = request.getParameter("cpid");
      String dj$a = request.getParameter("dj$a");
      String dj$b = request.getParameter("dj$b");
      String sflsbj = request.getParameter("sflsbj");
      String ksrq$a = request.getParameter("ksrq$a");
      String ksrq$b = request.getParameter("ksrq$b");

      if(dwtxid.length() > 0)
        SQL = SQL+" AND a.dwtxid="+dwtxid;
      if(cpid.length() > 0)
        SQL = SQL+" AND a.cpid="+cpid;
      if(dj$a.length() > 0)
        SQL = SQL+" AND a.dj>="+dj$a;
      if(dj$b.length() > 0)
        SQL = SQL+" AND a.dj<="+dj$b;
      if(ksrq$a.length() > 0)
        SQL = SQL+" AND a.ksrq>=to_date('"+ksrq$a+"','yyyy-mm-dd')";
      if(ksrq$b.length() > 0)
        SQL = SQL+" AND a.ksrq<=to_date('"+ksrq$b+"','yyyy-mm-dd')";
      if(sflsbj.length() > 0)
        SQL = SQL+" AND a.sflsbj="+sflsbj;
      SQL = combineSQL(CUSTOMER_PRODUCT_DISCOUNT_SQL, "?", new String[]{fgsid, SQL});
      if(!dsxs_khcpzkTable.getQueryString().equals(SQL))
      {
        dsxs_khcpzkTable.setQueryString(SQL);
        dsxs_khcpzkTable.setRowMax(null);
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
      EngineDataSet master = dsxs_khcpzkTable;
      if(!master.isOpen())
       master.open();
      //初始化固定的查询项目
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("dwtxid"),  null, null, null, null, "="),
        new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("dj"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("dj"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("ksrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("ksrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("sflsbj"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }
  /**
   * 添加或修改操作的触发类
   */
  class B_CustomerProductDiscount_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsxs_khcpzkTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsxs_khcpzkTable.getInternalRow();
      }
      initRowInfo(isAdd);
    }
  }
/**批量增回**/
    class Batch_Add_Init implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest request = data.getRequest();
        if(!BATCHINIT.equals(action))
        isBatchAdd=false;
        else
        isBatchAdd=true;
        dsBatchAddTable.open();
        dsBatchAddTable.setQueryString(KHCPZK_STRUCT_SQL);
        dsBatchAddTable.setRowMax(null);
        if(dsBatchAddTable.isOpen() && dsBatchAddTable.getRowCount() > 0)
        dsBatchAddTable.empty();
        initRowInfo(isBatchAdd,true);

        adddwtxid="";
        adddwdm="";
        adddwmc="";
        adddj="";
        addsflsbj="";
        addksrq="";
      }
  }
/**批量增回**/
  class Batch_Add_EDIT implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      String dwtxid =request.getParameter("dwtxid");
      String SQL="";
      if(isBatchAdd==false)
       SQL = combineSQL(BATCH_SQL_EDIT, "?", new String[]{dwtxid,fgsid, dwtxid});
      else if(isBatchAdd=true)
       SQL = combineSQL(BATCH_SQL_ADD, "?", new String[]{fgsid, dwtxid});
      if(!dsBatchAddTable.getQueryString().equals(SQL))
      {
        dsBatchAddTable.setQueryString(SQL);
        dsBatchAddTable.setRowMax(null);
      }
      adddwtxid=dwtxid;
      adddwdm=request.getParameter("dwdm");
      adddwmc=request.getParameter("dwmc");
      adddj=request.getParameter("dj");
      addsflsbj=request.getParameter("sflsbj");
      addksrq=request.getParameter("ksrq");
    }
  }
  /**批量保存**/
    class Batch_Post implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
        HttpServletRequest request = data.getRequest();
        String[] sel =request.getParameterValues("sel");
        String dwtxid =request.getParameter("dwtxid");
        String dj =request.getParameter("dj");
        String sflsbj =request.getParameter("sflsbj");
        if (sflsbj==null)
          sflsbj="0";
        String ksrq =request.getParameter("ksrq");
        String jsrq =request.getParameter("jsrq");
        rowInfo.put("ksrq",ksrq);
        rowInfo.put("jsrq",jsrq);

        adddwtxid=dwtxid;
        adddwdm=request.getParameter("dwdm");
        adddwmc=request.getParameter("dwmc");
        adddj=request.getParameter("dj");
        addsflsbj=request.getParameter("sflsbj");
        addksrq=request.getParameter("ksrq");
        Calendar day = new GregorianCalendar();
        int year = day.get(Calendar.YEAR);
        int month = day.get(Calendar.MONTH);
        day.clear();
        day.set(year,month+1,0);
        Date endDate = day.getTime();
        Date startDate = new Date();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
        String endday = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
        //if(!ksrq.equals("") && !jsrq.equals("")){
        //  java.sql.Date ksrqDtae = java.sql.Date.valueOf(ksrq);
        //  java.sql.Date jsrqDtae = java.sql.Date.valueOf(jsrq);
        //  if(jsrqDtae.before(ksrqDtae)){
         //   data.setMessage(showJavaScript("alert('结束日期不能小于开始日期！');"));
        //    return;
       //   }
     //   }

        if(sel==null||sel.length==0)
          return;
        //if(checkNumber(dj, "折扣") != null)
        //{
        //  data.setMessage(showJavaScript("alert('单价无效!')"));
        //  return;
        //}
        if(sel==null||sel.length==0)
        {
          data.setMessage(showJavaScript("alert('请选择产品!')"));
          return;
        }
        putDetailInfo(request);
        if(sflsbj.equals("1"))
        {
          for(int i=0;i<sel.length;i++)
          {
            int j= Integer.parseInt(sel[i]);
            RowMap derow = (RowMap)d_RowInfos.get(j);
            long internalRow = Long.parseLong(derow.get("InternalRow"));
            dsxs_khcpzkTable.goToInternalRow(internalRow);
            //dsxs_khcpzkTable.setValue("dwtxid",dwtxid);
            //dsxs_khcpzkTable.setValue("cpid",derow.get("cpid"));
            //String bz = derow.get("bz");
            //dsxs_khcpzkTable.setValue("bz",derow.get("bz"));
            //dsxs_khcpzkTable.setValue("djlx",derow.get("djlx"));
            //dsxs_khcpzkTable.setValue("dj",dj);
            //dsxs_khcpzkTable.setValue("ksrq",ksrq);
            //dsxs_khcpzkTable.setValue("fgsid",fgsid);
            //dsxs_khcpzkTable.setValue("sflsbj", "1");
            //dsxs_khcpzkTable.post();
            dsxs_khcpzkTable.setValue("jsrq",ksrq);
            dsxs_khcpzkTable.setValue("sflsbj", "1");
            dsxs_khcpzkTable.post();
            String aa=dsxs_khcpzkTable.getValue("sflsbj");
            dsxs_khcpzkTable.insertRow(false);
            dsxs_khcpzkTable.setValue("khcpzkid", "-1");
            String dj2=derow.get("dj");
            dsxs_khcpzkTable.setValue("dj",dj2);
            dsxs_khcpzkTable.setValue("dj",dj);
            dsxs_khcpzkTable.setValue("cpid",derow.get("cpid"));
            dsxs_khcpzkTable.setValue("dwtxid",dwtxid);
            dsxs_khcpzkTable.setValue("bz",derow.get("bz"));
            dsxs_khcpzkTable.setValue("djlx",derow.get("djlx"));
            dsxs_khcpzkTable.setValue("ksrq",ksrq);
            dsxs_khcpzkTable.setValue("jsrq",endday);
            dsxs_khcpzkTable.setValue("fgsid",fgsid);
            dsxs_khcpzkTable.post();
            String aasdf=dsxs_khcpzkTable.getValue("sflsbj");
            String adf=dsxs_khcpzkTable.getValue("sflsbj");
          }
        }
        else if (sflsbj.equals("0")&&!isBatchAdd)
        {
          for(int i=0;i<sel.length;i++)
          {
            int j= Integer.parseInt(sel[i]);
            RowMap derow = (RowMap)d_RowInfos.get(j);
            long internalRow = Long.parseLong(derow.get("InternalRow"));
            dsxs_khcpzkTable.goToInternalRow(internalRow);
            String dj2=derow.get("dj");
            dsxs_khcpzkTable.setValue("dj",dj2);
            //dsxs_khcpzkTable.setValue("dj",dj);
            String bz=derow.get("bz");
            dsxs_khcpzkTable.setValue("bz",derow.get("bz"));
            dsxs_khcpzkTable.setValue("ksrq",ksrq);
            dsxs_khcpzkTable.setValue("jsrq",endday);
            dsxs_khcpzkTable.post();
          }
        }
        else if (sflsbj.equals("0")&&isBatchAdd)
        {
          for(int i=0;i<sel.length;i++)
          {
            int j= Integer.parseInt(sel[i]);
            RowMap derow = (RowMap)d_RowInfos.get(j);
            dsxs_khcpzkTable.insertRow(false);
            dsxs_khcpzkTable.setValue("khcpzkid", "-1");
            dsxs_khcpzkTable.setValue("cpid",derow.get("cpid"));
            dsxs_khcpzkTable.setValue("dwtxid",dwtxid);
            String cpid = derow.get("cpid");
            String dj2=derow.get("dj");
            dsxs_khcpzkTable.setValue("dj",dj2);
            //dsxs_khcpzkTable.setValue("dj",dj);
            dsxs_khcpzkTable.setValue("bz",derow.get("bz"));
            dsxs_khcpzkTable.setValue("ksrq",ksrq);
            dsxs_khcpzkTable.setValue("jsrq",jsrq);
            dsxs_khcpzkTable.setValue("djlx",derow.get("djlx"));
            dsxs_khcpzkTable.setValue("fgsid",fgsid);
            dsxs_khcpzkTable.post();
          }
        }
        dsxs_khcpzkTable.saveChanges();
        dsxs_khcpzkTable.refresh();
      }
    }
    /**
     * 保存操作的触发类
     */
    class B_CustomerProductDiscount_Post implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
        EngineDataSet ds = getOneTable();
        rowInfo.put(data.getRequest());
        String cpid = rowInfo.get("cpid");
        String dj  = rowInfo.get("dj");
        String ksrq  = rowInfo.get("ksrq");    //开始日期
        String jsrq  = rowInfo.get("jsrq");    //结束日期
        String sflsbj = rowInfo.get("sflsbj");  //是否历史报价
        String bz  = rowInfo.get("bz");
        String dwtxid  = rowInfo.get("dwtxid");
        String dmsxid  = rowInfo.get("dmsxid");
        String khcpzkid  = rowInfo.get("khcpzkid");
        String djlx  = rowInfo.get("djlx");
        Calendar day = new GregorianCalendar();
        int year = day.get(Calendar.YEAR);
        int month = day.get(Calendar.MONTH);
        day.clear();
        day.set(year,month+1,0);
        Date endDate = day.getTime();
        Date startDate = new Date();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
        String endday = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
        //if(!ksrq.equals("") && !jsrq.equals("")){
       //   java.sql.Date ksrqDtae = java.sql.Date.valueOf(ksrq);
        //  java.sql.Date jsrqDtae = java.sql.Date.valueOf(jsrq);
       //   if(jsrqDtae.before(ksrqDtae)){
      //      data.setMessage(showJavaScript("alert('结束日期不能小于开始日期！');"));
     //       return;
     //     }
   //     }
        if(dwtxid.equals("")){
          data.setMessage(showJavaScript("alert('客户不能为空！');"));
          return;
        }
        if(cpid.equals("")){
          data.setMessage(showJavaScript("alert('产品ID不能为空！');"));
          return;
        }
        if(dj.equals(""))
        {
          data.setMessage(showJavaScript("alert('报价不能为空！');"));
          return;
        }
        if(!isAdd){
          ds.goToInternalRow(editrow);
          String count =  dataSetProvider.getSequence("SELECT COUNT(*)  FROM xs_khcpzk t WHERE t.cpid='"+cpid+"' AND (t.dwtxid is null or t.dwtxid='"+dwtxid+"') AND t.sflsbj=0  AND t.khcpzkid<>'"+khcpzkid+"'" );
          if(count!=null&&!count.equals("0"))
          {
          data.setMessage(showJavaScript("alert('已有该产品!')"));
          return;
        }
      }
      if(isAdd)
      {
        // String count = dataSetProvider.getSequence("SELECT count(*) FROM xs_khcpzk where fgsid="+fgsid+" and dwtxid="+dwtxid+" and cpid="+cpid+" and khcpzkid<>"+khcpzkid);
      String count =  dataSetProvider.getSequence("SELECT COUNT(*)  FROM xs_khcpzk t WHERE t.cpid='"+cpid+"' AND ( t.dwtxid is null or t.dwtxid='"+dwtxid+"' ) AND t.sflsbj=0 AND t.jsrq=to_date('"+jsrq+"','yyyy-mm-dd')" );
        if(count!=null&&!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('已有该产品!')"));
          return;
        }
        ds.insertRow(false);
        ds.setValue("khcpzkid","-1");
       }
       if(sflsbj.equals("1")){
         ds.setValue("jsrq",ksrq);
         ds.setValue("sflsbj", "1");
         ds.post();
         ds.insertRow(false);
         ds.setValue("khcpzkid", "-1");
       }
       ds.setValue("djlx", djlx);
       ds.setValue("cpid", cpid);
       ds.setValue("dwtxId", dwtxid);
       ds.setValue("dj",dj);
       ds.setValue("ksrq",ksrq);
       ds.setValue("jsrq",endday);
       ds.setValue("fgsid",fgsid);
       ds.setValue("bz",bz);
       ds.post();
       ds.saveChanges();
       data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }
  /**
   * 删除操作的触发类
   */
  class B_CustomerProductDiscount_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.deleteRow();
      ds.saveChanges();
    }
  }
}

