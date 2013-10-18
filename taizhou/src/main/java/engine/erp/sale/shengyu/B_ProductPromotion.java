package engine.erp.sale.shengyu;

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
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;

/**
 * <p>Title: ������Ʒ����</p>
 * <p>Copyright: right reserved (c) 2004</p>
 * <p>Company: ENGINE</p>
 * <p>Author: ������</p>
 * @version 1.0
 *             dsBatchAddTable.setValue("startdate",rowInfo.get("startdate"));
            dsBatchAddTable.setValue("enddate",rowInfo.get("enddate"));
            dsBatchAddTable.setValue("prom_price",rowInfo.get("prom_price"));
            dsBatchAddTable.setValue("memo",rowInfo.get("memo"));
            dsBatchAddTable.setValue("zk",rowInfo.get("zk"));
            dsBatchAddTable.setValue("djlx",rowInfo.get("djlx"));
 */

public final class B_ProductPromotion extends BaseAction implements Operate
{

  private static final String CPFHDJ_SQL = "SELECT a.* FROM xs_promotion a,VW_KC_DM b WHERE a.cpid=b.cpid  ? order by b.cpbm,a.dwtxid ";
  private static final String CPFHDJ_STRUCT_SQL = "SELECT a.* FROM xs_promotion a,kc_dm b WHERE  a.cpid=b.cpid and 1<>1 ORDER BY b.cpbm  ";//

  private static final String BATCH_SQL = "SELECT a.dwtxid FROM VW_SALE_KHXYED a WHERE a.dwtxid NOT IN(SELECT b.dwtxid FROM xs_promotion b WHERE b.cpid='?')";//
  private static final String BATCH_EDIT_SQL = "SELECT a.* FROM xs_promotion a WHERE a.cpid='?'";//
  private static final String BATCH_UPDATE_SQL = "UPDATE  xs_promotion a SET a.startdate=to_date('?','yyyy-mm-dd'), a.enddate=to_date('?','yyyy-mm-dd'),a.prom_price='?',a.memo='?',a.zk='?',a.djlx='?'   WHERE a.cpid='?' and a.dwtxid in(?) ";//

  private EngineDataSet dsCpfhdjTable = new EngineDataSet();//���ݼ�
  private EngineDataSet dsBatchAddTable = new EngineDataSet();
  private RowMap rowInfo = new RowMap();
  public  boolean isAdd = false;
  private long    editrow = 0;
  public  String retuUrl = null;

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsCpfhdjTable, "xs_promotion");
  public  HtmlTableProducer table = new HtmlTableProducer(dsCpfhdjTable, "xs_promotion", "a");//��ѯ�õ����ݿ������õ��ֶ�

  public  String loginid = "";   //��¼Ա����ID
  public  String loginCode = ""; //��½Ա���ı���
  public  String loginName = ""; //��¼Ա��������
  private String fgsid = null;   //�ֹ�˾ID
  public boolean isReport = false;
  public  boolean isApprove = false;     //�Ƿ�������״̬
  private ArrayList d_RowInfos = null; //���м�¼������

  public  static final String SELECT_PRODUCT           = "9005";
  public  static final String SXZ_CHANGE               = "9006";
  public  static final String AFTER_SELECT_PRODUCT     = "9006";
  public  static final String AFTER_POST               = "9007";
  public  static final String REPORT                   = "9008";      //����׷��
  public static final String BATCHINIT                 = "9009";
  public static final String BATCHPOST                 = "9010";
  public static final String BATCHADD                  = "9011";
  public  static final String PRODUCT_CHANGE           = "9012";
  public static final String BATCH_EDIT_INIT           = "9013";
  public static final String BATCH_EDIT                ="9014";
  public static final String BATCH_CONTINUE                ="9015";
  private QueryFixedItem fixedQuery = new QueryFixedItem();//����̶���ѯ��
  public boolean submitType;//�����ж�true=���ƶ��˿��ύ,false=��Ȩ���˿��ύ
  private boolean isInitQuery = false;
  public String dwdm="";
  public String dwmc="";
  private User user = null;//��½�û��������û�����Ȩ�ޣ�
  private String opratetype ="add";
  public ArrayList dwtxids = null;

  /**
   * �ӻỰ�еõ��������ÿ���Ϣ��ʵ��
   * @param request jsp����
   * @return �����������ÿ���Ϣ��ʵ��
   */

  public static B_ProductPromotion getInstance(HttpServletRequest request)
  {
    B_ProductPromotion b_ProductPromotionBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_ProductPromotionBean";
      b_ProductPromotionBean = (B_ProductPromotion)session.getAttribute(beanName);
      //�жϸ�session�Ƿ��и�bean��ʵ��
      if(b_ProductPromotionBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        b_ProductPromotionBean = new B_ProductPromotion();
        b_ProductPromotionBean.fgsid = loginBean.getFirstDeptID();
        b_ProductPromotionBean.loginid = loginBean.getUserID();
        b_ProductPromotionBean.user = loginBean.getUser();
        b_ProductPromotionBean.loginName = loginBean.getUserName();
        session.setAttribute(beanName, b_ProductPromotionBean);//���뵽session��
      }
    }
    return b_ProductPromotionBean;
  }
  /**
   * ���캯��
   */
  private B_ProductPromotion()
  {
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
    setDataSetProperty(dsCpfhdjTable, combineSQL(CPFHDJ_SQL,"?",new String[]{""}));
    setDataSetProperty(dsBatchAddTable, CPFHDJ_STRUCT_SQL);    //��ȡ��ȫ������
    //dsCpfhdjTable.setSequence(new SequenceDescriptor(new String[]{"cpfhdjid"}, new String[]{"s_xs_promotion"})); //����������sequence
    dsCpfhdjTable.setTableName("xs_promotion");
    dsBatchAddTable.setTableName("xs_promotion");

    dsBatchAddTable.addLoadListener(new com.borland.dx.dataset.LoadListener() {
      public void dataLoaded(LoadEvent e)
      {
        initRowInfo(true);
      }
    });


    //dsCpfhdjTable.setSort(new SortDescriptor("", new String[]{"cpfhdjid"}, new boolean[]{false}, null, 0));
    //��Ӳ����Ĵ�������
    Cpfhdj_Add_Edit add_edit = new Cpfhdj_Add_Edit();
    addObactioner(String.valueOf(ADD), add_edit);                  //����
    addObactioner(String.valueOf(EDIT), add_edit);                 //�޸�
    addObactioner(String.valueOf(INIT), new Cpfhdj_Init());  //��ʼ�� operate=0
    addObactioner(String.valueOf(POST), new Cpfhdj_Post());  //����
    addObactioner(String.valueOf(DEL), new Cpfhdj_Del()); //ɾ��

    addObactioner(String.valueOf(SELECT_PRODUCT), new Select_Product());
    addObactioner(String.valueOf(AFTER_SELECT_PRODUCT), new Select_Product());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());//��ѯ

    addObactioner(String.valueOf(BATCHINIT), new Batch_Add_Init());
    addObactioner(String.valueOf(BATCH_EDIT_INIT), new Batch_Add_Init());

    addObactioner(String.valueOf(BATCHADD), new Batch_Add_Edit());
    addObactioner(String.valueOf(BATCH_EDIT), new Batch_Add_Edit());



    addObactioner(String.valueOf(BATCHPOST), new Batch_Post());
    addObactioner(String.valueOf(BATCH_CONTINUE), new Batch_Post());

    addObactioner(String.valueOf(PRODUCT_CHANGE), new Cpfhdj_Change());

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
      if(dsCpfhdjTable.isOpen() && dsCpfhdjTable.changesPending())
        dsCpfhdjTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvmҪ���ĺ���,��������������
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsCpfhdjTable != null){
      dsCpfhdjTable.close();
      dsCpfhdjTable = null;
    }
    if(masterProducer != null)
    {
      masterProducer.release();
      masterProducer = null;
    }
    log = null;
    rowInfo = null;
  }
  public final EngineDataSet getBatchAddTable()
  {
    return dsBatchAddTable;
  }
  /**
   * �õ����������
   * ʵ��BaseAction�еĳ��󷽷�
   * ��־�е���
   * @return �������������
   */
  protected Class childClassName()
  {
    return getClass();
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
  /*�õ��ӱ���е���Ϣ*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
  /**
   * ��ʼ������Ϣ
   * @param isAdd �Ƿ�ʱ���
   * @param isInit �Ƿ���³�ʼ��
   * @throws java.lang.Exception �쳣
   */
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //�Ƿ�ʱ��Ӳ���
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
    if(!isAdd)
      rowInfo.put(getOneTable());
  }
  /**
   * @param request ��ҳ���������
   * @param response ��ҳ����Ӧ����
   * @return ����HTML��javascipt�����
   * @throws Exception �쳣
   */
  private final void putDetailInfo(HttpServletRequest request)
  {
    rowInfo.put(request);
    int rownum = dsBatchAddTable.getRowCount();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("dwtxid", rowInfo.get("dwtxid_"+i));//
      d_RowInfos.set(i,detailRow);
    }
  }
  /**
   * ��ʼ������Ϣ
   * @param isAdd �Ƿ�ʱ���
   * @param isInit �Ƿ���³�ʼ��
   */
  private final void initRowInfo(boolean isInit)
  {
    if(d_RowInfos == null)
      d_RowInfos = new ArrayList(dsBatchAddTable.getRowCount());
    else if(isInit)
      d_RowInfos.clear();
    dsBatchAddTable.first();
    for(int i=0; i<dsBatchAddTable.getRowCount(); i++)
    {
      RowMap row = new RowMap(dsBatchAddTable);
      d_RowInfos.add(row);
      dsBatchAddTable.next();
    }
  }
  /*�õ������*/
  public final EngineDataSet getOneTable()
  {
    //if(!dsCpfhdjTable.isOpen())
    //dsCpfhdjTable.open();
    return dsCpfhdjTable;
  }
  /**
   *�õ����һ����Ϣ
   * */
  public final RowMap getRowinfo() {return rowInfo;}


  //------------------------------------------
  //����ʵ�ֵ���:��ʼ��;����,�޸�,ɾ��
  //------------------------------------------
  /**
   * ��ʼ�������Ĵ�����
   */
  class Cpfhdj_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dwdm ="";
      dwmc ="";
      opratetype = "add";
      dwtxids = new ArrayList();
      HttpServletRequest request = data.getRequest();
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      fixedQuery.getSearchRow().clear();
      masterProducer.init(request, loginid);
      String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      if(code.equals("1"))
        submitType=true;
      else
        submitType=false;

      String MSQL =  combineSQL(CPFHDJ_SQL, "?", new String[]{ ""});
      dsCpfhdjTable.setQueryString(MSQL);
      dsCpfhdjTable.setRowMax(null);
    }
  }
  /**
   * ѡ���²�Ʒ
   **/
  class Select_Product implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      rowInfo.put(data.getRequest());
      String tdhwid = rowInfo.get("seltdhwid");
      String wzdjid = dataSetProvider.getSequence("select wzdjid from xs_tdhw where tdhwid='"+tdhwid+"'");
      if(wzdjid!=null)
        rowInfo.put("wzdjid",wzdjid);
      if(action.equals(String.valueOf(SELECT_PRODUCT)))
        rowInfo.put("tdhwid",tdhwid);
      else
        rowInfo.put("xs__tdhwid",tdhwid);
    }
  }

  class Search implements Obactioner
  {
    /**
     * ������ʼ������
     * @parma  action ����ִ�еĲ�������ֵ��
     * @param  o      �����߶���
     * @param  data   ���ݵ���Ϣ����
     * @param  arg    �����߶������<cardID >notifyObactioners</cardID >�������ݵĲ���
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = "";//�õ�WHERE�Ӿ�
      String cpid = request.getParameter("cpid");
      String dwtxid = request.getParameter("dwtxid");
      String pm = request.getParameter("pm");
      if(!dwtxid.equals(""))
        SQL = SQL+" AND a.dwtxid='"+dwtxid+"'";
      if(!cpid.equals(""))
        SQL = SQL+" AND b.cpid='"+cpid+"'";
      if(!pm.equals(""))
        SQL = SQL+" AND b.product like '%"+pm+"%'";

      String MSQL = combineSQL(CPFHDJ_SQL, "?", new String[]{SQL});//��װSQL���
      if(!dsCpfhdjTable.getQueryString().equals(MSQL))
      {
        dsCpfhdjTable.setQueryString(MSQL);
        dsCpfhdjTable.setRowMax(null);//�Ա�dbNavigatorˢ�����ݼ�
      }
    }
    /**
     * ��ʼ����ѯ�ĸ�����
     * @param request web�������
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;//�ѳ�ʼ����ѯ����
      EngineDataSet master = dsCpfhdjTable;
      if(!master.isOpen())
        master.open();//���������ݼ�
      //��ʼ���̶��Ĳ�ѯ��Ŀ
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "=")
      });
      isInitQuery = true;//��ʼ�����
    }
  }

  /**
   * ��ӻ��޸Ĳ����Ĵ�����
   */
  class Cpfhdj_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsCpfhdjTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsCpfhdjTable.getInternalRow();
      }
      initRowInfo(isAdd, true);
      //data.setMessage(showJavaScript("toDetail();"));
    }
  }
  /**
   * ��ӻ��޸Ĳ����Ĵ�����
   */
  class Cpfhdj_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      rowInfo.put(request);
      String cpid =rowInfo.get("cpid");
      String djlx =  rowInfo.get("djlx");
      String dj = dataSetProvider.getSequence("select "+djlx+" from xs_wzdj where cpid='"+cpid+"'");
      rowInfo.put("dj",dj);
      rowInfo.put("zk","");
      rowInfo.put("prom_price","");
      //initRowInfo(isAdd, true);
    }
  }
  /**��������**/
  class Batch_Add_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dwtxids = new ArrayList();
      HttpServletRequest request = data.getRequest();
      rowInfo = new RowMap();
      dsBatchAddTable.setQueryString(CPFHDJ_STRUCT_SQL);
      dsBatchAddTable.setRowMax(null);
      if(dsBatchAddTable.isOpen() && dsBatchAddTable.getRowCount() > 0)
        dsBatchAddTable.empty();
      if(action.equals(BATCH_EDIT_INIT))
        opratetype = "edit";
      else
        opratetype = "add";
    }
  }
  /**��������**/
  class Batch_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      rowInfo.put(request);
      String cpid =request.getParameter("cpid");
      String djlx =  rowInfo.get("djlx");
      String dj = dataSetProvider.getSequence("select "+djlx+" from xs_wzdj where cpid='"+cpid+"'");
      rowInfo.put("dj",dj);
      String SQL = combineSQL(BATCH_SQL, "?", new String[]{cpid});
      if(opratetype.equals("edit"))
        SQL = combineSQL(BATCH_EDIT_SQL, "?", new String[]{cpid});
      if(!dsBatchAddTable.getQueryString().equals(SQL))
      {
        dsBatchAddTable.setQueryString(SQL);
        dsBatchAddTable.setRowMax(null);
      }
    }
  }
  /**��������**/
  class Batch_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      rowInfo.put(request);

      String[] sel =request.getParameterValues("sel");
      String[] alldwtxids =request.getParameterValues("dwtxid");

      String cpid =request.getParameter("cpid");
      String prom_price =request.getParameter("prom_price");
      String startdate =request.getParameter("startdate");
      String enddate =request.getParameter("enddate");
      String djlx =request.getParameter("djlx");
      String zk =request.getParameter("zk");
      String memo =request.getParameter("memo");
      if(sel==null||sel.length==0)
        return;
      for(int i=0;i<alldwtxids.length;i++)
      {
        String dwtxid = alldwtxids[i];
        if(dwtxids.contains(dwtxid))
          dwtxids.remove(dwtxid);
      }
      for(int i=0;i<sel.length;i++)
      {
        String dwtxid = sel[i];
        if(!dwtxids.contains(dwtxid))
          dwtxids.add(dwtxid);
      }
      if(action.equals(String.valueOf(BATCH_CONTINUE)))
        return;

      if(checkNumber(prom_price, "��������") != null)
      {
        data.setMessage(showJavaScript("alert('����������Ч!')"));
        return;
      }
      if(checkNumber(zk, "�ۿ�") != null)
      {
        data.setMessage(showJavaScript("alert('�ۿ���Ч!')"));
        return;
      }
      if(sel==null||sel.length==0)
      {
        data.setMessage(showJavaScript("alert('��ѡ��ͻ�!')"));
        return;
      }
      String temp =startdate;
      if(temp.equals("") || !isDate(temp))
      {
        data.setMessage(showJavaScript("alert('�Ƿ�����!')"));
        return;
      }
      temp =enddate;
      if(temp.equals("") || !isDate(temp))
      {
        data.setMessage(showJavaScript("alert('�Ƿ�����!')"));
        return;
      }
      try{
        Date ksdate = new SimpleDateFormat("yyyy-MM-dd").parse(startdate);
        Date jsdate = new SimpleDateFormat("yyyy-MM-dd").parse(enddate);
        if(ksdate.compareTo(jsdate)>0){
          data.setMessage(showJavaScript("alert('�Ƿ�����!')"));
          return;
        }
        }catch(Exception e){}

        putDetailInfo(request);
        if(opratetype.equals("add"))
        {
          for(int i=0;i<dwtxids.size();i++)
          {
            String dwtxid = (String)dwtxids.get(i);
            //int j= Integer.parseInt(sel[i]);
            //RowMap derow = (RowMap)d_RowInfos.get(j);
            dsCpfhdjTable.insertRow(false);
            dsCpfhdjTable.setValue("cpid",cpid);
            dsCpfhdjTable.setValue("dwtxid",dwtxid);
            dsCpfhdjTable.setValue("startdate",rowInfo.get("startdate"));
            dsCpfhdjTable.setValue("enddate",rowInfo.get("enddate"));
            dsCpfhdjTable.setValue("prom_price",rowInfo.get("prom_price"));
            dsCpfhdjTable.setValue("memo",rowInfo.get("memo"));
            dsCpfhdjTable.setValue("zk",rowInfo.get("zk"));
            dsCpfhdjTable.setValue("djlx",rowInfo.get("djlx"));
            dsCpfhdjTable.post();
          }
          dsCpfhdjTable.saveChanges();
          dsCpfhdjTable.refresh();
          dsBatchAddTable.readyRefresh();
          initRowInfo(true);
        }else
        {
          String SQL="";
          for(int i=0;i<dwtxids.size();i++)
          {
            if(i<dwtxids.size()-1)
              SQL = SQL+(String)dwtxids.get(i)+",";
            else
              SQL = SQL+(String)dwtxids.get(i);
          }
          SQL = combineSQL(BATCH_UPDATE_SQL,"?",new String[]{startdate,enddate,prom_price,memo,zk,djlx,cpid,SQL});
          dsBatchAddTable.updateQuery(new String[]{SQL});
          dsBatchAddTable.readyRefresh();
          initRowInfo(true);
        }
        dsCpfhdjTable.readyRefresh();
        data.setMessage(showJavaScript("backList()"));
    }
  }
  /**
   * ��������Ĵ�����
   */
  class Cpfhdj_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //У������
      //t.wzdjid,t.tdhwid,t.deptid,t.dwtxid,t.xs__tdhwid,t.cpid,t.dj,t.fkqk,t.dmsxid
      //���ж����ݱ����Ƿ�����ͬ��������λ����Ʒ��������ԣ����ţ��ֹ�˾
      rowInfo.put(data.getRequest());


      String dwtxid = rowInfo.get("dwtxid");
      String cpid = rowInfo.get("cpid");
      String prom_price = rowInfo.get("prom_price");
      String startdate = rowInfo.get("startdate");
      String enddate = rowInfo.get("enddate");
      String memo = rowInfo.get("memo");
      String djlx =rowInfo.get("djlx");
      String zk =rowInfo.get("zk");
      //String startdate = rowInfo.get("startdate");
      String temp = "";
      if(dwtxid.equals(""))
      {
        data.setMessage(showJavaScript("alert('�����빺����λ!')"));
        return;
      }
      if(djlx.equals(""))
      {
        data.setMessage(showJavaScript("alert('��ѡ�񶨼�����!')"));
        return;
      }
      if(startdate.equals(""))
      {
        data.setMessage(showJavaScript("alert('��ʼʱ�䲻�ܿ�!')"));
        return;
      }
      if(enddate.equals(""))
      {
        data.setMessage(showJavaScript("alert('���������ʱ��!')"));
        return;
      }
      if(cpid.equals(""))
      {
        data.setMessage(showJavaScript("alert('�������Ʒ!')"));
        return;
      }
      if(checkNumber(zk, "�ۿ�") != null)
      {
        data.setMessage(showJavaScript("alert('�ۿ���Ч!')"));
        return;
      }
      String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM xs_promotion t WHERE t.dwtxid='"+dwtxid+"'  AND t.cpid='"+cpid+"'");
      if(count!=null&&!count.equals("0"))
      {
        if(isAdd||!count.equals("1"))
        {
          data.setMessage(showJavaScript("alert('������λ����Ʒ�����ظ�!')"));
          return;
        }
      }
      if((temp = checkNumber(prom_price, "��������")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if(isAdd)
      {
        ds.insertRow(false);
        isAdd=false;
      }
      else
        ds.goToInternalRow(editrow);
      ds.setValue("prom_price", prom_price);
      ds.setValue("startdate", startdate);
      ds.setValue("dwtxid", dwtxid);
      ds.setValue("cpid", cpid);
      ds.setValue("enddate", enddate);
      ds.setValue("memo", memo);
      ds.setValue("djlx", djlx);
      ds.setValue("zk", zk);

      ds.post();
      ds.saveChanges();
      editrow = ds.getInternalRow();
      //data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }

  /**
   * ɾ�������Ĵ�����
   */
  class Cpfhdj_Del implements Obactioner
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