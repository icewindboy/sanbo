package engine.erp.sale.shengyu;

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
/**
 * <p>Title: Ա�����ÿ����б�</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @version 1.0
 */

public final class B_ReceivableAccount extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "10009";
  public static final String OVER= "10001";
  public static final String copy= "10002";
  public static final String DETAIL_CHANGE = "10003";
  public static final String BATCH_ADD = "10004";

  private static final String RECEIVEABLE_ACCOUNT_SAVE_STRUCT_SQL = "select * from xs_ysk where 1<>1";
  private static final String RECEIVEABLE_ACCOUNT_SAVE_SQL = "select * from VW_XS_YSK where fgsid=? ? ";//

  private static final String SUM_SQL = "select SUM(nvl(ysk,0))zysk from VW_XS_YSK where fgsid=? ? ";//

  private static final String BATCH_IMPORT_SQL = "SELECT a.dwtxid,a.dqh,a.dwdm,a.fgsid,a.personid FROM dwtx a,dwtx_lx b,dwdq c WHERE a.dwtxid=b.dwtxid AND b.ywlx=2 AND a.dqh=c.dqh AND a.Isdelete='0' ";

  private static final String B_QUERY_SQL = "select * from VW_XS_YSK t  WHERE t.fgsid=? ? ";//

  private static final String XSINIT_SQL = "SELECT t.value FROM xs_init t WHERE t.fgsid=";
  private static final String XSINIT_OVER_SQL = "INSERT INTO xs_init t VALUES(?,1)";
  private static final String XSINIT_UPDATE_SQL = "update xs_init t SET t.value=1 where t.fgsid=? ";
  /**
   *�������ݼ�
   * �����������۵�����Ϣ�����ݼ�
   */
  private EngineDataSet dsB_ReceivableAccount_save = new EngineDataSet();

  //���м�¼������
  private ArrayList d_RowInfos = null;

  public  HtmlTableProducer table = new HtmlTableProducer(dsB_ReceivableAccount_save, "xs_ysk", "xs_ysk");
  /**
   * ������ذ�ť��URL
   */
  public  static String retuUrl = null;
  public  static String XS_INIT_OVER = null;
  private boolean isInitQuery = false;
  public static String loginName = ""; //��¼Ա��������
  private static String fgsid =null;   //�ֹ�˾ID
  private User user = null;
  private String zysje="";//��Ӧ�ս��
  private String YSSQL="";

  /**
   * ����̶���ѯ��
   */
  private QueryFixedItem fixedQuery = new QueryFixedItem();
  /**
   * �õ��������۵�����Ϣ��ʵ��
   * @param request jsp����
   * @return �����������۵�����Ϣ��ʵ��
   */

  public static B_ReceivableAccount getInstance(HttpServletRequest request)
  {
    B_ReceivableAccount dsB_ReceivableAccountBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "dsB_ReceivableAccountBean";
      dsB_ReceivableAccountBean = (B_ReceivableAccount)session.getAttribute(beanName);
      //�жϸ�session�Ƿ��и�bean��ʵ��
      //b_employeecardnoBean = (B_EmployeeCardNo)session.getAttribute(beanName);
      if(dsB_ReceivableAccountBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        fgsid = loginBean.getFirstDeptID();
        dsB_ReceivableAccountBean = new B_ReceivableAccount(fgsid);
        dsB_ReceivableAccountBean.loginName = loginBean.getUserName();
        dsB_ReceivableAccountBean.user = loginBean.getUser();
       session.setAttribute(beanName,dsB_ReceivableAccountBean);
      }
    }
     return dsB_ReceivableAccountBean;
  }
  /**
   * ���캯��(ʵ������:�ֹ�˾IDΪ��ʼ������)
   */
  private B_ReceivableAccount(String fgsid)
 {  this.fgsid = fgsid;
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
    setDataSetProperty(dsB_ReceivableAccount_save, RECEIVEABLE_ACCOUNT_SAVE_STRUCT_SQL);

    dsB_ReceivableAccount_save.setSort(new SortDescriptor("", new String[]{"areacode","dwdm"}, new boolean[]{false,false}, null, 0));
    dsB_ReceivableAccount_save.setTableName("xs_ysk");

    dsB_ReceivableAccount_save.addLoadListener(new com.borland.dx.dataset.LoadListener() {
      public void dataLoaded(LoadEvent e) {
        initRowInfo(false, true);
      }
    }
    );
    //��Ӳ����Ĵ�������
    addObactioner(String.valueOf(OVER), new dsB_ReceivableAccount_save_Post());
    addObactioner(String.valueOf(INIT), new dsB_ReceivableAccount_save_Init());
    addObactioner(String.valueOf(POST), new dsB_ReceivableAccount_save_Post());
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());
    addObactioner(String.valueOf(DEL), new dsB_ReceivableAccount_save_Delete());
    addObactioner(String.valueOf(copy), new dsB_ReceivableAccount_save_copy());
    addObactioner(String.valueOf(DETAIL_CHANGE), new Detail_Change());
    addObactioner(String.valueOf(ADD), new Detail_Add());
    addObactioner(String.valueOf(BATCH_ADD), new Batch_Add());
  }
  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      String operate = request.getParameter("operate");
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
   * jvmҪ���ĺ���,��������������
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsB_ReceivableAccount_save != null){
      dsB_ReceivableAccount_save.close();
      dsB_ReceivableAccount_save = null;
    }
    log = null;
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
    //������ҳ��������Ϣ
    rowInfo.put(request);
    //�����ݼ��л�ȡ��¼����
    //int rownum = dsB_ReceivableAccount_save.getRowCount();
    RowMap detailRow = null;
    int num = d_RowInfos.size();
    for(int i=0; i<d_RowInfos.size(); i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);//d_RowInfos
      detailRow.put("dwtxid", rowInfo.get("dwtxid_"+i));//������λID
      detailRow.put("ysk", rowInfo.get("ysk_"+i));//��λӦ�տ�
      detailRow.put("khlx", rowInfo.get("khlx_"+i));//�ͻ�
      detailRow.put("personid", rowInfo.get("personid_"+i));
    }
  }
  /*�õ��ӱ���е���Ϣ*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
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
   */
  private final void initRowInfo(boolean isAdd, boolean isInit)
  {
    if(d_RowInfos == null)
      d_RowInfos = new ArrayList(dsB_ReceivableAccount_save.getRowCount());
    else if(isInit)
      d_RowInfos.clear();
    //dsB_ReceivableAccount_save.setSort(new SortDescriptor("", new String[]{"dwtxid"}, new boolean[]{false}, null, 0));
    dsB_ReceivableAccount_save.first();
    for(int i=0; i<dsB_ReceivableAccount_save.getRowCount(); i++)
    {
      RowMap row = new RowMap(dsB_ReceivableAccount_save);
      row.put("internalRowNum",String.valueOf(dsB_ReceivableAccount_save.getInternalRow()));
      d_RowInfos.add(row);
      dsB_ReceivableAccount_save.next();
    }
  }
  /*�õ���Ӧ�տ�*/
 public final String getZysk()
 {
   return zysje;
 }
  /*�õ������*/
  public final EngineDataSet getDetailTable()
  {
    return dsB_ReceivableAccount_save;
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
   * ��ʼ�������Ĵ�����
   */
  class dsB_ReceivableAccount_save_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {

      XS_INIT_OVER= dataSetProvider.getSequence("SELECT t.value FROM xs_init t WHERE t.fgsid="+fgsid);

      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      table.getWhereInfo().clearWhereValues();
      String dept = user.getHandleDeptValue();
      String SQL = combineSQL(RECEIVEABLE_ACCOUNT_SAVE_SQL,"?",new String[]{fgsid," and "+dept});
      //SUM_SQL
      YSSQL =  combineSQL(SUM_SQL,"?",new String[]{fgsid," and "+dept});

      //��ʼ����ѯ��Ŀ������
      RowMap row = fixedQuery.getSearchRow();
      row.clear();

      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,YSSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zysje="0";
      else
        zysje=dssl.getValue("zysk");

      dsB_ReceivableAccount_save.setQueryString(SQL);
      dsB_ReceivableAccount_save.setRowMax(null);
    }
  }
  /**
  * ��Ӳ�ѯ�����Ĵ�����
  */
 class FIXED_SEARCH implements Obactioner
 {
   //----Implementation of the Obactioner interface
   /**
    * ��ӻ��޸ĵĴ�������
    * @parma  action ����ִ�еĲ�������ֵ��
    * @param  o      �����߶���
    * @param  data   ���ݵ���Ϣ����
    * @param  arg    �����߶������<code>notifyObactioners</code>�������ݵĲ���
    */
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     initQueryItem(data.getRequest());
     QueryBasic queryBasic = fixedQuery;
     queryBasic.setSearchValue(data.getRequest());
     String SQL = queryBasic.getWhereQuery();
     if(SQL.length() > 0)
       SQL = " AND "+SQL;
     String dept = user.getHandleDeptValue();
     SQL =SQL+ " and "+dept;

     YSSQL =  combineSQL(SUM_SQL,"?",new String[]{fgsid, SQL});
     EngineDataSet dssl = new EngineDataSet();
     setDataSetProperty(dssl,YSSQL);
     dssl.open();
     dssl.first();
     int cn = dssl.getRowCount();
     if(dssl.getRowCount()<1)
       zysje="0";
     else
        zysje=dssl.getValue("zysk");


     SQL = combineSQL(B_QUERY_SQL, "?", new String[]{fgsid, SQL});
     if(!dsB_ReceivableAccount_save.getQueryString().equals(SQL))
     {
       dsB_ReceivableAccount_save.setQueryString(SQL);
       dsB_ReceivableAccount_save.setRowMax(null);
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
      EngineDataSet master = dsB_ReceivableAccount_save;
      if(!master.isOpen())
        master.open();
      //��ʼ���̶��Ĳ�ѯ��Ŀ
      //������λdwtxId;�������xyed;�����ȼ�xydj;�ؿ�����hkts;
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("dwtxId"), null, null, null, null, "="),
        //new QueryColumn(master.getColumn("dwmc"), null, null, null, null, "="),
        //new QueryColumn(master.getColumn("dwdm"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("khlx"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("xm"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("areacode"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("areacode"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("ysk"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("ysk"), null, null, null, "b", "<=")
      });
      isInitQuery = true;
    }
  }

  /**
   * ��������Ĵ�����
   */
  class dsB_ReceivableAccount_save_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      boolean isOver=String.valueOf(OVER).equals(action);
      RowMap detailrow = null;
      if(!action.equals(copy))
      {
        HttpServletRequest req = data.getRequest();
        putDetailInfo(req);
      }
      ArrayList al = new ArrayList();
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String dwtxid=detailrow.get("dwtxid");
        String khlx=detailrow.get("khlx");
        String ysk=detailrow.get("ysk");
        String personid=detailrow.get("personid");
        if(dwtxid.equals(""))
        {
          data.setMessage(showJavaScript("alert('�ͻ���!')"));
          return;
        }
        if(al.contains(dwtxid+"k"+khlx))
        {
          data.setMessage(showJavaScript("alert('�ͻ�+�ͻ��������߲����ظ�!')"));
          return;
        }
        al.add(dwtxid+"k"+khlx);
        String tmp = null;
        if(ysk.length()>0)
        {
          tmp = checkNumber(ysk,"Ӧ�տ�");
        }
        if(tmp!=null)
        {
          data.setMessage(tmp);
          return;
        }
      }
      dsB_ReceivableAccount_save.first();
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String dwtxid=detailrow.get("dwtxid");
        String khlx=detailrow.get("khlx");
        String ysk=detailrow.get("ysk");
        String personid=detailrow.get("personid");

        String count=null;
        if(khlx.equals(""))
          count=dataSetProvider.getSequence("SELECT count(*) FROM xs_ysk WHERE dwtxid='"+dwtxid+"' AND fgsid='"+fgsid+"'");
        else
          count=dataSetProvider.getSequence("SELECT count(*) FROM xs_ysk WHERE dwtxid='"+dwtxid+"' AND fgsid='"+fgsid+"' AND khlx='"+khlx+"'");

        dsB_ReceivableAccount_save.setValue("dwtxid", dwtxid);
        dsB_ReceivableAccount_save.setValue("fgsid", fgsid);
        dsB_ReceivableAccount_save.setValue("khlx", khlx);
        dsB_ReceivableAccount_save.setValue("ysk", ysk);
        dsB_ReceivableAccount_save.setValue("personid", personid);
        dsB_ReceivableAccount_save.setValue("areacode", detailrow.get("areacode"));
        dsB_ReceivableAccount_save.setValue("dwdm", detailrow.get("dwdm"));
        if(dsB_ReceivableAccount_save.isNew(i)&&!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('�ͻ�+�ͻ��������߲����ظ�!')"));
          return;
        }
        //Date today = new Date();
        //String day = new SimpleDateFormat("yyyy-MM-dd").format(today);
        //String update_sql = B_update_SQL+"to_date('"+day+"','YYYY-MM-DD')";

        dsB_ReceivableAccount_save.next();
      }
      if(isOver)
      {
        if(XS_INIT_OVER==null)
        {
          String sql = combineSQL(XSINIT_OVER_SQL,"?",new String[]{fgsid});
          dsB_ReceivableAccount_save.updateQuery(new String[]{sql});
        }else
        {
          String sql = combineSQL(XSINIT_UPDATE_SQL,"?",new String[]{fgsid});
          dsB_ReceivableAccount_save.updateQuery(new String[]{sql});
        }
      }
      dsB_ReceivableAccount_save.post();
      dsB_ReceivableAccount_save.getColumn("areacode").setResolvable(false);
      dsB_ReceivableAccount_save.getColumn("dwdm").setResolvable(false);
      dsB_ReceivableAccount_save.saveChanges();

      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,YSSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zysje="0";
      else
        zysje=dssl.getValue("zysk");

      initRowInfo(false,true);
      //dsB_ReceivableAccount_save.refresh();
      XS_INIT_OVER= dataSetProvider.getSequence("SELECT t.value FROM xs_init t WHERE t.fgsid="+fgsid);
    }
  }
  /**
   *  �ӱ����
   */
  class Detail_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
    }
  }
  /**
   *  ����
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      dsB_ReceivableAccount_save.insertRow(false);
      dsB_ReceivableAccount_save.setValue("dwtxid","");
      dsB_ReceivableAccount_save.setValue("khlx", "");
      dsB_ReceivableAccount_save.setValue("fgsid", fgsid);
      dsB_ReceivableAccount_save.post();

      RowMap row = new RowMap();
      row.put("dwtxid","");
      row.put("khlx", "");
      row.put("fgsid", fgsid);
      d_RowInfos.add(row);
    }
  }
  /**
   *  ����
   */
  class Batch_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      String scope = req.getParameter("scope");
      String dqh = req.getParameter("batchdqh");
      String dwdm_a =  req.getParameter("dwdm_a");
      String dwdm_b =  req.getParameter("dwdm_b");
      String SQL="";
      if(scope.equals("1"))
      {
        if(dqh.equals(""))
          return;
        SQL = BATCH_IMPORT_SQL+" AND a.dqh='"+dqh+"' AND fgsid ='"+fgsid+"'";
      }
      else
        SQL = BATCH_IMPORT_SQL+" AND a.dwdm>='"+dwdm_a+"' AND a.dwdm<'"+dwdm_b+"' AND fgsid ='"+fgsid+"'";
      String count = "";
      EngineDataSet tmp = new EngineDataSet();
      setDataSetProperty(tmp,SQL);
      tmp.open();
      tmp.first();
      for(int i=0;i<tmp.getRowCount();i++)
      {
        String dwtxid = tmp.getValue("dwtxid");
        String personid = tmp.getValue("personid");
        count = dataSetProvider.getSequence("select count(*) from xs_ysk where dwtxid='"+dwtxid+"' and fgsid ='"+fgsid+"'");
        if(!count.equals("0"))
          continue;
        dsB_ReceivableAccount_save.insertRow(false);
        dsB_ReceivableAccount_save.setValue("dwtxid",dwtxid);
        dsB_ReceivableAccount_save.setValue("khlx", "");
        dsB_ReceivableAccount_save.setValue("fgsid", fgsid);
        dsB_ReceivableAccount_save.setValue("personid", personid);
        dsB_ReceivableAccount_save.post();
        tmp.next();
      }
      initRowInfo(false,true);
    }
  }
  class dsB_ReceivableAccount_save_Delete implements Obactioner
  {
    /**
     * ����ɾ������
     * @parma  action ����ִ�еĲ�������ֵ��
     * @param  o      �����߶���
     * @param  data   ���ݵ���Ϣ����
     * @param  arg    �����߶������<code>notifyObactioners</code>�������ݵĲ���
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getDetailTable();
      int num = Integer.parseInt(data.getParameter("rownum"));
      d_RowInfos.remove(num);
      ds.goToRow(num);
      if(ds.goToRow(num))
      ds.deleteRow();
      //ds.saveChanges();
    }
  }
  class dsB_ReceivableAccount_save_copy implements Obactioner
  {
    /**
     * ����ɾ������
     * @parma  action ����ִ�еĲ�������ֵ��
     * @param  o      �����߶���
     * @param  data   ���ݵ���Ϣ����
     * @param  arg    �����߶������<code>notifyObactioners</code>�������ݵĲ���
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      putDetailInfo(request);
      RowMap rowinfo= null;
      long masterRow;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        rowinfo = (RowMap)d_RowInfos.get(i);
        masterRow = Long.parseLong(rowinfo.get("internalRowNum"));
        dsB_ReceivableAccount_save.goToInternalRow(masterRow);
        dsB_ReceivableAccount_save.setValue("dwtxid", rowinfo.get("dwtxid"));
        dsB_ReceivableAccount_save.setValue("khlx", rowinfo.get("khlx"));
        dsB_ReceivableAccount_save.setValue("ysk", rowinfo.get("ysk"));
        dsB_ReceivableAccount_save.post();
      }

      int num = Integer.parseInt(data.getParameter("rownum"));
      dsB_ReceivableAccount_save.goToRow(num);
      String dwtxid = dsB_ReceivableAccount_save.getValue("dwtxid");
      String khlx = dsB_ReceivableAccount_save.getValue("khlx");
      String areacode = dsB_ReceivableAccount_save.getValue("areacode");
      String dwdm = dsB_ReceivableAccount_save.getValue("dwdm");

      dsB_ReceivableAccount_save.insertRow(false);
      dsB_ReceivableAccount_save.setValue("dwtxid",dwtxid);
      dsB_ReceivableAccount_save.setValue("areacode",areacode);
      dsB_ReceivableAccount_save.setValue("dwdm",dwdm);
      dsB_ReceivableAccount_save.setValue("khlx", "");
      dsB_ReceivableAccount_save.post();

     initRowInfo(false,true);
    }
  }
}
