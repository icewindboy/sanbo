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
 * ���۹���--�ͻ���Ʒ�ۿ�,
 * �������ͻ�Ҫ����Ĳ�Ʒ�ĵ��ۺ��ۿ�
 * �ͻ���Ʒ��ʷ��¼
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
  public  String loginName = ""; //��¼Ա��������
  private String fgsid = null;   //�ֹ�˾ID
  private QueryFixedItem fixedQuery = new QueryFixedItem();
  private ArrayList d_RowInfos = null; //���м�¼������

  public String dwdm="";//��λ��ѯ
  public String dwmc="";//��λ��ѯ
  public String cpbm="";
  public String product="";

  public String adddwtxid="";//��λ��ѯ
  public String adddwdm="";//��λ��ѯ
  public String adddwmc="";//��λ��ѯ
  public String adddj="";
  public String addsflsbj="";
  public String addksrq="";
  /**
   * �õ��ͻ����������Ϣ��ʵ��
   * @param request jsp����
   * @param isApproveStat �Ƿ�������״̬
   * @return ���ر���������Ϣ��ʵ��
   */
  public static B_CustomerProductDiscount getInstance(HttpServletRequest request)
  {
    B_CustomerProductDiscount xs_khcpzkbean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "xs_khcpzkbean";
      xs_khcpzkbean = (B_CustomerProductDiscount)session.getAttribute(beanName);
      //�жϸ�session�Ƿ��и�bean��ʵ��
      if(xs_khcpzkbean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsid = loginBean.getFirstDeptID();
        xs_khcpzkbean = new B_CustomerProductDiscount(fgsid);
        xs_khcpzkbean.loginName = loginBean.getUserName();
        //���ø�ʽ�����ֶ�
        xs_khcpzkbean.dsxs_khcpzkTable.setColumnFormat("zk", loginBean.getPriceFormat());
        xs_khcpzkbean.dsxs_khcpzkTable.setColumnFormat("dj", loginBean.getPriceFormat());
        xs_khcpzkbean.dsxs_khcpzkTable.setColumnFormat("bj", loginBean.getPriceFormat());
        session.setAttribute(beanName, xs_khcpzkbean);
      }
    }
    return xs_khcpzkbean;
  }
  /**
   * ���캯��
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
   * ��ʼ������
   * @throws Exception �쳣��Ϣ
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
    //��Ӳ����Ĵ�������
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
   * JSP���õĺ���
   * @param request ��ҳ���������
   * @param response ��ҳ����Ӧ����
   * @return ����HTML��javascipt�����
   * @throws Exception �쳣
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
   * jvmҪ���ĺ���,��������������
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
   * �õ����������
   * @return �������������
   */
  protected Class childClassName()
  {
    return getClass();
  }
  /**
   * ��ʼ������Ϣ
   * @param isAdd �Ƿ�ʱ���
   * @param isInit �Ƿ���³�ʼ��
   * @throws java.lang.Exception �쳣
   */
private final void initRowInfo(boolean isAdd) throws java.lang.Exception
{
    //�Ƿ�ʱ��Ӳ���
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
 * ��ʼ������Ϣ
 * @param isAdd �Ƿ�ʱ���
 * @param isInit �Ƿ���³�ʼ��
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
/*�õ������*/
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
  /*�õ��ӱ���е���Ϣ*/
public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
}
/*�õ�һ�е���Ϣ*/
public final RowMap getRowinfo()
  {
       return rowInfo;
  }
  /*�õ�һ�е���Ϣ*/
public final RowMap getRowBatchinfo()
  {
       return row;
  }
  /**
   * �õ��̶���ѯ���û������ֵ
   * @param col ��ѯ������
   * @return �û������ֵ
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }
  /**
   * @param request ��ҳ���������
   * @param response ��ҳ����Ӧ����
   * @return ����HTML��javascipt�����
   * @throws Exception �쳣
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
   * ��ʼ�������Ĵ�����
   */
  class B_CustomerProductDiscount_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      fixedQuery.getSearchRow().clear();
      dwdm="";//��λ��ѯ
      dwmc="";//��λ��ѯ
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
   * ��Ӳ�ѯ�����Ĵ�����
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
     * ��ʼ����ѯ�ĸ�����
     * @param request web�������
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet master = dsxs_khcpzkTable;
      if(!master.isOpen())
       master.open();
      //��ʼ���̶��Ĳ�ѯ��Ŀ
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
   * ��ӻ��޸Ĳ����Ĵ�����
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
/**��������**/
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
/**��������**/
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
  /**��������**/
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
         //   data.setMessage(showJavaScript("alert('�������ڲ���С�ڿ�ʼ���ڣ�');"));
        //    return;
       //   }
     //   }

        if(sel==null||sel.length==0)
          return;
        //if(checkNumber(dj, "�ۿ�") != null)
        //{
        //  data.setMessage(showJavaScript("alert('������Ч!')"));
        //  return;
        //}
        if(sel==null||sel.length==0)
        {
          data.setMessage(showJavaScript("alert('��ѡ���Ʒ!')"));
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
     * ��������Ĵ�����
     */
    class B_CustomerProductDiscount_Post implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
        EngineDataSet ds = getOneTable();
        rowInfo.put(data.getRequest());
        String cpid = rowInfo.get("cpid");
        String dj  = rowInfo.get("dj");
        String ksrq  = rowInfo.get("ksrq");    //��ʼ����
        String jsrq  = rowInfo.get("jsrq");    //��������
        String sflsbj = rowInfo.get("sflsbj");  //�Ƿ���ʷ����
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
      //      data.setMessage(showJavaScript("alert('�������ڲ���С�ڿ�ʼ���ڣ�');"));
     //       return;
     //     }
   //     }
        if(dwtxid.equals("")){
          data.setMessage(showJavaScript("alert('�ͻ�����Ϊ�գ�');"));
          return;
        }
        if(cpid.equals("")){
          data.setMessage(showJavaScript("alert('��ƷID����Ϊ�գ�');"));
          return;
        }
        if(dj.equals(""))
        {
          data.setMessage(showJavaScript("alert('���۲���Ϊ�գ�');"));
          return;
        }
        if(!isAdd){
          ds.goToInternalRow(editrow);
          String count =  dataSetProvider.getSequence("SELECT COUNT(*)  FROM xs_khcpzk t WHERE t.cpid='"+cpid+"' AND (t.dwtxid is null or t.dwtxid='"+dwtxid+"') AND t.sflsbj=0  AND t.khcpzkid<>'"+khcpzkid+"'" );
          if(count!=null&&!count.equals("0"))
          {
          data.setMessage(showJavaScript("alert('���иò�Ʒ!')"));
          return;
        }
      }
      if(isAdd)
      {
        // String count = dataSetProvider.getSequence("SELECT count(*) FROM xs_khcpzk where fgsid="+fgsid+" and dwtxid="+dwtxid+" and cpid="+cpid+" and khcpzkid<>"+khcpzkid);
      String count =  dataSetProvider.getSequence("SELECT COUNT(*)  FROM xs_khcpzk t WHERE t.cpid='"+cpid+"' AND ( t.dwtxid is null or t.dwtxid='"+dwtxid+"' ) AND t.sflsbj=0 AND t.jsrq=to_date('"+jsrq+"','yyyy-mm-dd')" );
        if(count!=null&&!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('���иò�Ʒ!')"));
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
   * ɾ�������Ĵ�����
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

