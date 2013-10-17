package engine.erp.person.shengyu;
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
import engine.dataset.EngineDataSetProvider;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;


public final class B_EmployeeChange extends BaseAction implements Operate
{
  public  static final String CANCER_APPROVE = "10235131";
  public  static final String BDLX_CHANGE = "55555556";
  public  static final String PERSON_ONCHANGE = "55555554";
  private static final String MASTER_STRUT_SQL = "SELECT * FROM rl_emp_change";//���ر�ṹ
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM rl_emp_change_detail where 1<>1 ";//���ر�ṹ
  private static final String MASTER_SQL    = "SELECT a.* FROM rl_emp_change a, emp b WHERE  a.personid=b.personid ORDER BY b.bm DESC";
  //private static final String MASTER_SQL    = "SELECT * FROM rl_emp_change WHERE 1=1 ?   ORDER BY chang_id DESC";
  private static final String DETAIL_SQL    = "SELECT * FROM rl_emp_change_detail WHERE chang_ID='?' ";//
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM rl_emp_change WHERE chang_ID='?'";
  private static final String SEARCH_SQL = "SELECT * FROM VW_PERSON_ZGXXBD WHERE 1=1 ? ";
  private static final String YGFZXX_SQL    = "SELECT * FROM rl_ygfzxx WHERE lx='8' ";//

  private static final String HTH_SQL ="SELECT t.hth FROM rl_zgqtxx t,rl_emp_change k where t.personid=k.personid AND t.personid = ? ";


  private EngineDataSet dsMasterTable  = new EngineDataSet();//����
  private EngineDataSet dsDetailTable  = new EngineDataSet();//�ӱ�
  private EngineDataSet dsSearchTable = new EngineDataSet();//��ѯ���ݼ�
  private EngineDataSet dsYgfzxxTable = new EngineDataSet();//Ա��������Ϣ���ݼ�

  //public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, SysConstant.TABLE_SALE_ORDER);//"xs_ht"
  //public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, SysConstant.TABLE_SALE_ORDER_GOODS);
  public boolean isMasterAdd = true;    //�Ƿ������״̬
  private long    masterRow = 0;         //���������޸Ĳ������м�¼ָ��
  private RowMap  m_RowInfo    = new RowMap(); //��������л��޸��е�����
  private ArrayList d_RowInfos = null; //�ӱ���м�¼������

  private LookUp personBean = null;

  //&#$
  public  boolean isApprove = false;     //�Ƿ�������״̬

  private boolean isInitQuery = false; //�Ƿ��Ѿ���ʼ����ѯ����
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public  String loginId = "";   //��¼Ա����ID
  public  String loginCode = ""; //��½Ա���ı���
  public  String loginName = ""; //��¼Ա��������
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String filialeID = null;   //�ֹ�˾ID
  private String chang_ID="";
  public boolean submitType;//�����ж�true=���ƶ��˿��ύ,false=��Ȩ���˿��ύ
  /**
   * ���ۺ�ͬ�б��ʵ��
   * @param request jsp����
   * @param isApproveStat �Ƿ�������״̬
   * @return �������ۺ�ͬ�б��ʵ��
   */
  public static B_EmployeeChange getInstance(HttpServletRequest request)
  {
    B_EmployeeChange b_employeeinfoChangeBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_employeeinfoChangeBean";
      b_employeeinfoChangeBean = (B_EmployeeChange)session.getAttribute(beanName);
      if(b_employeeinfoChangeBean == null)
      {
        //����LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        b_employeeinfoChangeBean = new B_EmployeeChange();
        b_employeeinfoChangeBean.qtyFormat = loginBean.getQtyFormat();
        b_employeeinfoChangeBean.priceFormat = loginBean.getPriceFormat();
        b_employeeinfoChangeBean.sumFormat = loginBean.getSumFormat();

        b_employeeinfoChangeBean.filialeID = loginBean.getFirstDeptID();
        b_employeeinfoChangeBean.loginId = loginBean.getUserID();
        b_employeeinfoChangeBean.loginName = loginBean.getUserName();

        session.setAttribute(beanName, b_employeeinfoChangeBean);
      }
    }
    return b_employeeinfoChangeBean;
  }

  /**
   * ���캯��
   */
  private B_EmployeeChange()
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
  private final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsMasterTable, MASTER_STRUT_SQL);
    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
    setDataSetProperty(dsYgfzxxTable, null);
    dsMasterTable.setTableName("rl_emp_change");

    setDataSetProperty(dsSearchTable, combineSQL(SEARCH_SQL,"?",new String[]{""}));
    String pref = "HT";
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"chang_ID"}, new String[]{"s_rl_emp_change"}));
    //dsMasterTable.setSort(new SortDescriptor("", new String[]{"chang_ID"}, new boolean[]{true}, null, 0));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"chg_detail_ID"}, new String[]{"s_rl_emp_change_detail"}));
    //dsDetailTable.setSort(new SortDescriptor("", new String[]{"hthwid"}, new boolean[]{false}, null, 0));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(BDLX_CHANGE), new Bdlx_Onchange());
    //&#$//��˲���
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());//ȡ������
    addObactioner(String.valueOf(PERSON_ONCHANGE), new Person_Onchage());
  }

  //----Implementation of the BaseAction abstract class
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
      String opearate = request.getParameter(OPERATE_KEY);
      if(opearate != null && opearate.trim().length() > 0)
      {
         RunData data = notifyObactioners(opearate, request, response, null);
        if(data == null)
          return showMessage("��Ч����", false);
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
   * �����Ƿ������״̬
   * @return �Ƿ������״̬
   */
  public final boolean masterIsAdd() {return isMasterAdd; }
  /**
   * SessionʧЧʱ�����õĺ���
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
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
  }

  /**
   * �õ����������
   * @return �������������
   */
  protected final Class childClassName()
  {
    return getClass();
  }

  /**
   * ��ʼ������Ϣ
   * @param isAdd �Ƿ�ʱ���
   * @param isInit �Ƿ���³�ʼ��
   * @throws java.lang.Exception �쳣
   */
  private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //�Ƿ�������
    if(isMaster){
      if(isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();
      if(!isAdd){
        m_RowInfo.put(getMaterTable());
      }
      else
      {
        //��������
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //String lsh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('rl_emp_change','lsh') from dual");
       // m_RowInfo.put("lsh", lsh);
        m_RowInfo.put("createDate", today);//�Ƶ�����
        m_RowInfo.put("creator", loginName);
        m_RowInfo.put("chg_date", today);
        m_RowInfo.put("state", "0");
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
   * �ӱ������
   * @param request ��ҳ���������
   * @param response ��ҳ����Ӧ����
   * @return ����HTML��javascipt�����
   * @throws Exception �쳣
   */
  private final void putDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = getMasterRowinfo();
    //������ҳ��������Ϣ
    rowInfo.put(request);
    int rownum = d_RowInfos.size();
    //rownum   =   8;
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("chang_ID", dsMasterTable.getValue("chang_ID"));//
      detailRow.put("remove_goods", rowInfo.get("remove_goods_"+i));//
      detailRow.put("remove_thing", rowInfo.get("remove_thing_"+i));//
      detailRow.put("accepter", rowInfo.get("accepter_"+i));//
      detailRow.put("memo", rowInfo.get("memo_"+i));//��ע



      detailRow.put("personid", rowInfo.get("personid_"+i));//
      detailRow.put("bdqID", rowInfo.get("bdqID_"+i));//
      detailRow.put("bdhID", rowInfo.get("bdhID_"+i));//

      d_RowInfos.set(i,detailRow);
    }
  }

  /*�õ������*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /*�õ��ӱ�����*/
  public final EngineDataSet getDetailTable(){return dsDetailTable;}

  /*�򿪴ӱ�*/
  private final void openDetailTable()
  {
    chang_ID = dsMasterTable.getValue("chang_ID");
    String SQL = isMasterAdd ? "-1" : chang_ID;
     SQL = combineSQL(DETAIL_SQL, "?", new String[]{SQL});
     //if (!isMasterAdd){
       dsDetailTable.setQueryString(SQL);
       //chang_ID = dsMasterTable.getValue("chang_ID");
       // dsDetailTable.setQueryString(DETAIL_SQL + (isMasterAdd ? "-1" : chang_ID));

       if(dsDetailTable.isOpen())
         dsDetailTable.refresh();
       else
         dsDetailTable.open();
     //}
  }

  /*�õ�����һ�е���Ϣ*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*�õ��ӱ���е���Ϣ*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
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
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      //��ʼ����ѯ��Ŀ������
       RowMap row = fixedQuery.getSearchRow();
      row.clear();
      row.put("state", "0");
      //
      if(dsMasterTable.isOpen() && dsMasterTable.getRowCount() > 0)
        dsMasterTable.empty();
      dsMasterTable.setQueryString(MASTER_SQL);
      dsMasterTable.setRowMax(null) ;
      //data.setMessage(showJavaScript("showFixedQuery();"));
      String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      if(code.equals("1"))
        submitType=true;
      else
        submitType=false;
    }
  }
  /**
 * ѡ���������ύҳ�����
 */
  class Person_Onchage implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PERSON);//ˢ��lookupbean.2004.12.19
      String personid = m_RowInfo.get("personid");
      String chg_type = m_RowInfo.get("chg_type");
      String predeptid = dataSetProvider.getSequence("select deptid from emp where personid='"+personid+"'");
      String preduty   = dataSetProvider.getSequence("select zw from emp where personid='"+personid+"'");
      String preclass   = dataSetProvider.getSequence("select lb from emp where personid='"+personid+"'");
      if(predeptid!=null&&chg_type.equals("2"))
      {
        m_RowInfo.put("chg_before",predeptid);
      }
      else if(predeptid!=null&&chg_type.equals("4"))
      {
        m_RowInfo.put("chg_before",preduty);
      }
      else if(preclass!=null&&chg_type.equals("3"))
      {
      m_RowInfo.put("chg_before",preclass);
      }
      //LookUp personBean = LookupBeanFacade.getInstance(data.getRequest(), SysConstant.BEAN_PERSON);
    }
  }
  //&#$
  /**
   * ���������Ĵ�����
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = true;
      isMasterAdd=false;

      HttpServletRequest request = data.getRequest();
      //�õ�request�Ĳ���,ֵ��Ϊnull, ����""����
      String id = data.getParameter("id", "");
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen())
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      //�򿪴ӱ�
      isMasterAdd=false;
      openDetailTable();
      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
    }
  }
  //&#$
  /**
   * ��ӵ�����б�Ĳ�����
   */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterTable.getValue("chg_type");//?
      if(content.equals("1")){content="ְԱ��ְ";}
      else if(content.equals("2")){content="���ŵ���";}
      else if(content.equals("3")){content="������";}
      else if(content.equals("4")){content="ְ���Ǩ";}
      else if(content.equals("5")){content="ְԱ��ְλ";}
      String deptid = dsMasterTable.getValue("deptid");
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "employee_change", content,deptid);
    }
  }
  /**
   * ȡ��
   */
  class Cancer_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = true;

      HttpServletRequest request = data.getRequest();
      //�õ�request�Ĳ���,ֵ��Ϊnull, ����""����
      String id = data.getParameter("id", "");
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(),"employee_change");
    }
  }

  /**
   * 2004-2-18���
   * �ı�䶯����
   */
  class Bdlx_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      String oldbdlx=m_RowInfo.get("chg_type");
      putDetailInfo(data.getRequest());//�����������ϸ��Ϣ
      String chg_type=m_RowInfo.get("chg_type");
      if(oldbdlx.equals(chg_type))
      {
        return;
      }
      else
      {
        dsDetailTable.empty();
        d_RowInfos.clear();
      }
    }
  }
  /**
   * ������ӻ��޸Ĳ����Ĵ�����
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //&#$
      isApprove = false;
      isMasterAdd = String.valueOf(ADD).equals(action);
      //�򿪴ӱ�
      if(!isMasterAdd)
      {
        isMasterAdd=false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        chang_ID = dsMasterTable.getValue("chang_ID");
        synchronized(dsDetailTable){
        openDetailTable();
      }
      }
      else
      {
        synchronized(dsDetailTable){
          openDetailTable();
        }
        isMasterAdd=true;

        dsYgfzxxTable.setQueryString(YGFZXX_SQL);//�����ݼ�������Ӧ��SQ,��ʱִ�и�SQL
        if (dsYgfzxxTable.isOpen())
          dsYgfzxxTable.refresh();
        else
          dsYgfzxxTable.openDataSet();

        if (dsDetailTable.isOpen())
          dsDetailTable.refresh();
        else
          dsDetailTable.openDataSet();
        //dsDetailTable.deleteAllRows();
        //dsDetailTable.post();
        //String remove_goods[]={"�����ƽ�","��ҵָ����","�������","����Ь��Կ��","��Ʊ��֧","����Ƿ���裩��","�������"};
        for (int i=0;i<dsYgfzxxTable.getRowCount();i++)
        {
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("remove_goods", dsYgfzxxTable.getValue("mc"));
          dsDetailTable.post();
          dsYgfzxxTable.next();
        }
      }
      //�ж���ϸ���Ƿ���Щchange_id�ļ�¼.��opendetail.��:���ָ�����������¼
      //int count = dataSetProvider.sets
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      data.setMessage(showJavaScript("toDetail();"));
    }
  }

  /**
   * ����������Ĵ�����
   */
  class Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      //У�������
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
      EngineDataSet detail = getDetailTable();
      if(detail.getRowCount()<1)
      {
        //ds.refresh();
        data.setMessage(showJavaScript("alert('��ϸ�����Ҫ������!');"));
        return;
      }

      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      //�õ���������ֵ
      String chang_ID = null;
      if(isMasterAdd){
        ds.insertRow(false);
        chang_ID = dataSetProvider.getSequence("s_rl_emp_change");
        //ds.setValue("lsh", rowInfo.get("lsh"));
        ds.setValue("chang_ID", chang_ID);
        ds.setValue("filialeID", filialeID);
        ds.setValue("creatorID", loginId);
        ds.setValue("creator", loginName);//����Ա
        ds.setValue("state", "0");//����Ա
        ds.setValue("createDate", rowInfo.get("createDate"));
      }
      //����ӱ������
      RowMap detailrow = null;
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //����ļ�¼
        if(isMasterAdd)
          detail.setValue("chang_ID", chang_ID);
        detail.setValue("remove_goods", detailrow.get("remove_goods"));
        detail.setValue("remove_thing", detailrow.get("remove_thing"));
        detail.setValue("accepter", detailrow.get("accepter"));
        detail.setValue("memo", detailrow.get("memo"));//��ע
        // String aa = detailrow.get("personid");
        //detail.setValue("personid", detailrow.get("personid"));//
        // detail.setValue("bdqID", detailrow.get("bdqID"));
        //detail.setValue("bdhID", detailrow.get("bdhID"));
        detail.post();
        detail.next();
      }
      //������������
      ds.setValue("chg_date", rowInfo.get("chg_date"));//�䶯����
      //�Ƶ�����
      String chang_id = dataSetProvider.getSequence("s_rl_emp_change");
      String aa = rowInfo.get("zw");
      String cc = rowInfo.get("deptid");
      ds.setValue("personid", rowInfo.get("personid"));
      ds.setValue("chg_type", rowInfo.get("chg_type"));//�䶯����
      ds.setValue("chg_reason", rowInfo.get("chg_reason"));//�䶯ԭ��
      String bb = rowInfo.get("chg_type");
      //ds.setValue("chg_before", rowInfo.get("chg_before"));//�䶯ǰְ��
      if(bb.equals("4"))
      {
        ds.setValue("chg_before",rowInfo.get("zw"));//�䶯ǰְ��
        ds.setValue("chg_after", rowInfo.get("bdhzw"));//�䶯��ְ��
      }
      else if(bb.equals("2"))
      {
        ds.setValue("chg_before", rowInfo.get("ydeptid"));//�䶯ǰ����
        ds.setValue("chg_after", rowInfo.get("deptid2"));//�䶯����
      }
      else if(bb.equals("3"))
      {
        ds.setValue("chg_before",rowInfo.get("lb1"));
        ds.setValue("chg_after", rowInfo.get("lb"));//�䶯�����
      }
      ds.setValue("memo", rowInfo.get("memo"));//����ע
      ds.setValue("deptid", rowInfo.get("deptid"));
      //ds.setValue("state", rowInfo.get("state"));//�䶯ԭ��
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PERSON);
      if(String.valueOf(POST_CONTINUE).equals(action))
      {
        isMasterAdd = true;
        detail.empty();
        initRowInfo(true, true, true);
        initRowInfo(false, true, true);//���³�ʼ���ӱ�ĸ�����Ϣ
        //initRowInfo(false, false, true);//���³�ʼ���ӱ�ĸ�����Ϣ
      }
      else if(String.valueOf(POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
    }
    /**
     * У��ӱ����Ϣ�ӱ��������Ϣ����ȷ��
     * @return null ��ʾû����Ϣ
     */
    private String checkDetailInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //temp = detailrow.get("personid");
        //if(temp.equals(""))
        //return showJavaScript("alert('��ѡ������!');");
        //temp = detailrow.get("bdqID");
        //if(temp.equals(""))
          //return showJavaScript("alert('�䶯ǰ��ϢΪ��!');");
        //temp = detailrow.get("bdhID");
       // if(temp.equals(""))
         // return showJavaScript("alert('�䶯����ϢΪ��!');");
      }
      return null;
    }

    /**
     * У����������Ϣ�ӱ��������Ϣ����ȷ��
     * @return null ��ʾû����Ϣ,У��ͨ��
     */
    private String checkMasterInfo() throws Exception
    {
      RowMap rowInfo = getMasterRowinfo();
      String kk=rowInfo.get("deptid2");
      String bdlx=rowInfo.get("chg_type");
      String temp = rowInfo.get("chg_date");
      if(temp.equals(""))
        return showJavaScript("alert('�䶯���ڲ���Ϊ�գ�');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('�ύ���Ų���Ϊ�գ�');");
      temp = rowInfo.get("chg_type");
    if(temp.equals(""))
        return showJavaScript("alert('��ѡ��䶯����!');");
    temp = rowInfo.get("personid");
         if(temp.equals(""))
        return showJavaScript("alert('��ѡ������!');");

     temp = rowInfo.get("deptid2");
     if(temp.equals("")&&(bdlx.equals("2")))
        return showJavaScript("alert('�䶯���Ų���Ϊ��!');");
     temp = rowInfo.get("lb");
    if(temp.equals("")&&(bdlx.equals("3")))
        return showJavaScript("alert('�䶯��ְ����Ϊ��!');");
        temp = rowInfo.get("bdhzw");
    if(temp.equals("")&&(bdlx.equals("4")))
        return showJavaScript("alert('�䶯�������Ϊ��!');");
      //  String count="";
     //   String bSQL="";
     //   bSQL = combineSQL(HTH_SQL, "?", new String[]{temp});
     //   count  = dataSetProvider.getSequence(bSQL);
      return null;
    }
  }

  /**
   * ����ɾ������
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
   *  ��ѯ����
   */
  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(SEARCH_SQL, "?", new String[]{SQL});
  //    if(!dsSearchTable.getQueryString().equals(SQL))
   //   {
  //      dsSearchTable.setQueryString(SQL);
  //      dsSearchTable.setRowMax(null);
 //     }
  //    dsSearchTable.refresh();
   //   StringBuffer sb = new StringBuffer();
  //    ArrayList al = new ArrayList();
  //    String zgxxids="";
   //   dsSearchTable.first();
  //    for(int i=0;i<dsSearchTable.getRowCount();i++)
   //   {
  //      String chang_ID = dsSearchTable.getValue("chang_ID");
 //       if(chang_ID!=null&&!chang_ID.equals(""))
 //         if(!al.contains(chang_ID))
//            al.add(chang_ID);
 //       dsSearchTable.next();
 //     }
 //     if(al.size()==0)
 //       zgxxids="0";
 //     else
 //     {
  //      for(int j=0;j<al.size();j++)
  //        sb.append(al.get(j)+",");
 //       zgxxids=sb.append("0").toString();
 //     }
 //     if(!dsMasterTable.isOpen())
 //       dsMasterTable.open();
 //     SQL = combineSQL(MASTER_SQL, "?", new String[]{" AND chang_ID IN("+zgxxids+") "});
      if(!dsMasterTable.getQueryString().equals(SQL))
      {
        dsMasterTable.setQueryString(SQL);
        dsMasterTable.setRowMax(null);
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
      EngineDataSet master = dsSearchTable;
      if(!master.isOpen())
        master.open();
      //��ʼ���̶��Ĳ�ѯ��Ŀ
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        //new QueryColumn(master.getColumn("lsh"), null, null, null, null, "="),//��ˮ��
        new QueryColumn(master.getColumn("chg_date"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("chg_date"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("createDate"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("createDate"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("chg_type"), null, null, null,null,"="),
        new QueryColumn(master.getColumn("creator"), null, null, null,null,"like"),
        new QueryColumn(master.getColumn("bm"), null, null, null,null,"like"),
        new QueryColumn(master.getColumn("xm"), null, null, null,null,"like"),
        new QueryColumn(master.getColumn("state"), null, null, null, null, "="),
        //new QueryColumn(master.getColumn("username"), null, null, null,null,"like"),
        //new QueryColumn(master.getColumn("chang_ID "), "VW_PERSON_ZGXXBD", "chang_ID", "bm", "bm", "left_like"),//�ӱ��Ʒ
      });
      isInitQuery = true;
    }
  }

  /**
   * �ӱ����Ӳ���
   *
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //�����������ϸ��Ϣ
      putDetailInfo(data.getRequest());
      String multiIdInput = m_RowInfo.get("multiIdInput");
      if(multiIdInput.length() == 0)
        return;
      //ʵ�����������ݼ�����
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "chang_ID");
      String[] personids = parseString(multiIdInput,",");
      for(int i=0; i < personids.length; i++)
      {
        if(personids[i].equals("-1"))
          continue;

        String chang_ID = dsMasterTable.getValue("chang_ID");
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, personids[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap personRow = getpersonNameBean(req).getLookupRow(personids[i]);
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("chg_detail_ID", "-1");
          dsDetailTable.setValue("chang_ID", chang_ID);
          //dsDetailTable.setValue("personid", personids[i]);
          if(m_RowInfo.get("chg_type").equals("2"))
          {
            dsDetailTable.setValue("bdqID", personRow.get("deptid"));
          }
          if(m_RowInfo.get("chg_type").equals("3"))
          {
            String lbq=personRow.get("lb");
            dsDetailTable.setValue("bdqID", personRow.get("lb"));
          }
          if(m_RowInfo.get("chg_type").equals("4"))
          {
            dsDetailTable.setValue("bdqID", personRow.get("zw"));
          }
          dsDetailTable.setValue("bdhID", personRow.get("bdhID"));
          dsDetailTable.setValue("memo", personRow.get("memo"));
          dsDetailTable.post();
          //����һ�����û����Ӧ����
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
    }
  }

  /**
   *   �ӱ�ɾ������
   */
  class Detail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getDetailTable();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      //ɾ����ʱ�����һ������
      d_RowInfos.remove(rownum);
      ds.goToRow(rownum);
      ds.deleteRow();
    }
  }

  /**
    *��Ա��Ϣ
   */
  public LookUp getpersonNameBean(HttpServletRequest req)
  {
    if(personBean == null)
      personBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PERSON);
    return personBean;
  }
  public String getHth(String personid) throws Exception
  {
    String hth = "";
    if ( personid == null )
      return hth;
    String HTH_SQL ="SELECT nvl(t.hth, '') FROM rl_zgqtxx t,rl_emp_change k where t.personid=k.personid AND t.personid = ? ";
    String bSQL = combineSQL(HTH_SQL, "?", new String[]{personid});
    hth = dataSetProvider.getSequence(bSQL);
    if (hth==null)
    hth="";
    return hth;
  }

}
