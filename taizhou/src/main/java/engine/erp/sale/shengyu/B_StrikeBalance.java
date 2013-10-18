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
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
import engine.report.util.ReportData;
/**
 * <p>Title: ���۹���--���۷���֪ͨ��--</p>
 * <p>Description: ���۹���--���۷���֪ͨ��--<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class B_StrikeBalance extends BaseAction implements Operate
{

  public  static final String SHOW_DETAIL      = "1001";
  public  static final String DETAIL_SALE_ADD  = "1002";
  //public  static final String TD_RETURN_ADD   = "1003";//�ᵥ�˻�����
  public  static final String CANCER_APPROVE  = "1004";
  public  static final String LADING_OVER     = "1005";//���
  public  static final String LADING_CANCER   = "1006";//����
  public  static final String DWTXID_CHANGE   = "1007";
  public  static final String IMPORT_ORDER    = "1008";//�����ͬ(����)
  public  static final String DETAIL_CHANGE   = "1009";//�ӱ����
  public  static final String DETAIL_PRODUCT_ADD = "1010";//����ͻ���ʷ��Ʒ
  public  static final String PRODUCT_ADD        = "1011";  //�����ͬ����
  public  static final String REPORT             ="645355666";
  public  static final String WRAPPER_PRINT      ="1012";
  public  static final String DETAIL_COPY        ="1013";//���Ƶ�ǰѡ����
  public  static final String DEL_NULL           = "1014";            //ɾ������Ϊ�յ���
  public  static final String LADING_OUT         = "1015";//����ȷ��
  public  static final String MASTER_ADD         = "1018";
  public  static final String APPROVED_MASTER_ADD = "1019";//������ı���
  public  static final String LADDING_CANCER      = "1020";     //�ᵥ����
  public  static final String CYQK_ADD            = "1021";           //�����������
  public  static final String QTFY_ADD            = "1022";           //�����������
  public  static final String CYQK_DEL            = "1023";           //�������ɾ��
  public  static final String QTFY_DEL            = "1024";           //�������ɾ��
  public  static final String CHONGZHANG_ADD      = "1025";

  private static final String MASTER_STRUT_SQL     = "SELECT * FROM xs_td WHERE 1<>1  ";
  private static final String MASTER_SQL           = "SELECT * FROM xs_td WHERE djlx=6 AND ? AND fgsid=? ?  order by djlx desc,tdbh desc";
  private static final String MASTER_EDIT_SQL       = "SELECT * FROM xs_td WHERE tdid='?' ";
  //private static final String MASTER_SUM_SQL       = "SELECT SUM(nvl(zsl,0))zsl FROM xs_td WHERE djlx=6 AND ? AND fgsid=? ?  ";
  private static final String MASTER_JE_SQL        = "SELECT SUM(nvl(zje,0))zje FROM xs_td WHERE djlx=6 AND ? AND fgsid=? ?  ";
  private static final String SALEABLE_PRODUCT_SQL = " SELECT * FROM vw_lading_sel_product WHERE cpid=? and (storeid is null or storeid= ? )";//������Ʒ

  private static final String DETAIL_STRUT_SQL      = "SELECT * FROM xs_tdhw WHERE 1<>1 ";
  private static final String DETAIL_SQL            = " SELECT * FROM xs_tdhw WHERE tdid='?' ";//


  public static final String ORDER_DETAIL_SQL       = "SELECT * FROM VW_SALE_IMPORT_TD_ORDER_DETAIL WHERE nvl(sl,0)>0 and lb=2 and htid= ";//���������HTID������Ӧ��ͬ����ϸ
  public static final String TH_DETAIL_SQL          = "SELECT * FROM VW_SALE_IMPORT_TH_ORDER_DETAIL WHERE lb=1 and  htid= ";//�˻��������ͬ����
  private static final String MASTER_APPROVE_SQL    = "SELECT * FROM xs_td WHERE tdid='?'";
  private static final String CAN_OVER_SQL          = "select count(*) from xs_tdhw a where nvl(a.sl,0)>nvl(a.stsl,0) and a.tdid=";//
  private static final String CAN_CANCER_SQL        = "select count(*) from xs_tdhw a where nvl(a.stsl,0)>0 and a.tdid=";//
  private static final String REFERENCED_SQL        = "select count(*) from xs_tdhw a where nvl(a.stsl,0)>0 and a.tdid=";//
  private static final String SEARCH_SQL            = "SELECT * FROM VW_SALE_LADING_PRODUCT WHERE 1=1 ? ";//��������ӱ��ѯ
  private static final String KHCPZK_SQL            = "SELECT * FROM xs_khcpzk where dwtxid=? and cpid=? ";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//����
  private EngineDataSet dsMasterList  = new EngineDataSet();//����

  private EngineDataSet dsDetailTable  = new EngineDataSet();//�ӱ�

  private EngineDataSet dsSearchTable  = new EngineDataSet();//��ѯ�õ������ݼ�
  private EngineDataSet hthwmxTable      = new EngineDataSet();//��ͬ������ϸ
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "xs_td.6");
  public  HtmlTableProducer masterListProducer = new HtmlTableProducer(dsMasterList, "xs_td.6");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "xs_tdhw.6");
  public  boolean isApprove = false;     //�Ƿ�������״̬
  private boolean isMasterAdd = true;    //�Ƿ������״̬
  private long    masterRow = -1;         //���������޸Ĳ������м�¼ָ��
  private RowMap  m_RowInfo    = new RowMap(); //��������л��޸��е�����

  private ArrayList d_RowInfos = null;         //�ӱ���м�¼������


  private ArrayList drows=null;
  private B_ImportOrder b_ImportOrderBean = null; //������������ۺ�ͬ
  private ImportOrderProduct importOrderProductBean = null;
  private LookUp salePriceBean = null; //���۵��۵�bean������, ������ȡ���۵���
  private boolean isInitQuery = false; //�Ƿ��Ѿ���ʼ����ѯ����
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public  String loginId = "";   //��¼Ա����ID
  public  String loginCode = ""; //��½Ա���ı���
  public  String loginName = ""; //��¼Ա��������
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //�ֹ�˾ID
  private String djlx="6" ;       //�ᵥ����
  private String tdid = null;
  private User user = null;
  public boolean isReport = false;
  public boolean submitType;//�����ж�true=���ƶ��˿��ύ,false=��Ȩ���˿��ύ
  public boolean conversion=false;//���ۺ�ͬ�Ļ��������������Ƿ���Ҫǿ��ת��ȡ
  private static ArrayList keys = new ArrayList();
  private static Hashtable table = new Hashtable();
  public String []zt;
  public String tCopyNumber = "1";
  //private String zzsl="";//������
  private String zzje="";//�ܽ��
  //private String SLSQL="";//ͳ��������SQL
  private String JESQL="";//ͳ�ƽ���SQL
  public String dwdm="";//��λ��ѯ
  public String dwmc="";//��λ��ѯ
  public boolean canOperate=false;//
  public String activetab = "SetActiveTab(INFO_EX,'INFO_EX_0')";//�ӱ�ǰ��div
  //public String jglx = "";//��������
  public String zkl = "";
  /**
   * ���ۺ�ͬ�б��ʵ��
   * @param request jsp����
   * @param isApproveStat �Ƿ�������״̬
   * @return �������ۺ�ͬ�б��ʵ��
   */
  public static B_StrikeBalance getInstance(HttpServletRequest request)
  {
    B_StrikeBalance b_StrikeBalanceBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_StrikeBalanceBean";
      b_StrikeBalanceBean = (B_StrikeBalance)session.getAttribute(beanName);
      if(b_StrikeBalanceBean == null)
      {
        //����LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        b_StrikeBalanceBean = new B_StrikeBalance();
        b_StrikeBalanceBean.qtyFormat = loginBean.getQtyFormat();
        b_StrikeBalanceBean.priceFormat = loginBean.getPriceFormat();
        b_StrikeBalanceBean.sumFormat = loginBean.getSumFormat();
        b_StrikeBalanceBean.fgsid = loginBean.getFirstDeptID();
        b_StrikeBalanceBean.loginId = loginBean.getUserID();
        b_StrikeBalanceBean.loginName = loginBean.getUserName();
        b_StrikeBalanceBean.user = loginBean.getUser();
        if(loginBean.getSystemParam("SC_STORE_UNIT_STYLE").equals("1"))
          b_StrikeBalanceBean.conversion = true;
        if(loginBean.getSystemParam("XS_LADINGBILL_HANDWORK").equals("1"))
          b_StrikeBalanceBean.canOperate = true;//�Ƿ�����ֹ����ᵥ
        //���ø�ʽ�����ֶ�

        b_StrikeBalanceBean.dsMasterTable.setColumnFormat("zje", b_StrikeBalanceBean.sumFormat);
        b_StrikeBalanceBean.dsMasterTable.setColumnFormat("zsl", b_StrikeBalanceBean.qtyFormat);

        b_StrikeBalanceBean.dsDetailTable.setColumnFormat("sl", b_StrikeBalanceBean.qtyFormat);
        b_StrikeBalanceBean.dsDetailTable.setColumnFormat("hssl", b_StrikeBalanceBean.qtyFormat);
        b_StrikeBalanceBean.dsDetailTable.setColumnFormat("xsj", b_StrikeBalanceBean.priceFormat);
        b_StrikeBalanceBean.dsDetailTable.setColumnFormat("dj", b_StrikeBalanceBean.priceFormat);
        b_StrikeBalanceBean.dsDetailTable.setColumnFormat("jje", b_StrikeBalanceBean.sumFormat);
        b_StrikeBalanceBean.dsDetailTable.setColumnFormat("xsje", b_StrikeBalanceBean.sumFormat);

        session.setAttribute(beanName, b_StrikeBalanceBean);
      }
    }
    return b_StrikeBalanceBean;
  }
  /**
   * ���캯��
   */
  private B_StrikeBalance()
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
    setDataSetProperty(dsMasterList, MASTER_STRUT_SQL);
    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);

    dsDetailTable.setTableName("xs_tdhw");
    setDataSetProperty(dsSearchTable, combineSQL(SEARCH_SQL,"?",new String[]{""}));
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"tdbh"}, new String[]{"SELECT pck_base.billNextCode('xs_td.6','tdbh') from dual"}));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"tdhwid"}, new String[]{"s_xs_tdhw"}));
    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    Detail_Delete detaildel = new Detail_Delete();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    //addObactioner(String.valueOf(TD_RETURN_ADD), masterAddEdit);//�ᵥ�˻�����
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());

    //addObactioner(String.valueOf(CHONGZHANG_ADD), new Detail_Add());
    addObactioner(String.valueOf(CYQK_ADD), new Detail_Add());
    addObactioner(String.valueOf(QTFY_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_SALE_ADD), new Detail_SALE_Add());
    addObactioner(String.valueOf(DETAIL_DEL), detaildel);
    addObactioner(String.valueOf(CYQK_DEL), detaildel);
    addObactioner(String.valueOf(QTFY_DEL), detaildel);
    //&#$//��˲���
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(REPORT), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());
    addObactioner(String.valueOf(DETAIL_CHANGE), new Detail_Change());
    addObactioner(String.valueOf(DWTXID_CHANGE), new Dwtxid_Change());
    addObactioner(String.valueOf(DEPT_CHANGE), new Dept_Change());
    addObactioner(String.valueOf(PRODUCT_ADD), new Multi_Product_Add());
    addObactioner(String.valueOf(DETAIL_COPY), new Detail_Copy_Add());//DETAIL_COPY
    addObactioner(String.valueOf(DEL_NULL), new Detail_Delete_Null());//Detail_Delete_Null
    addObactioner(String.valueOf(LADING_OUT), new Lading_Out());
    addObactioner(String.valueOf(MASTER_ADD), new Master_Add());
    addObactioner(String.valueOf(APPROVED_MASTER_ADD), new Approved_Master_Post());
    addObactioner(String.valueOf(LADDING_CANCER), new Cancer());//����
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
   * SessionʧЧʱ�����õĺ���
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsMasterTable != null){
      dsMasterTable.close();
      dsMasterTable = null;
    }
    if(dsMasterList != null){
      dsMasterList.close();
      dsMasterList = null;
    }
    if(dsDetailTable != null){
      dsDetailTable.close();
      dsDetailTable = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
    if(masterProducer != null)
    {
      masterProducer.release();
      masterProducer = null;
    }
    if(detailProducer != null)
    {
      detailProducer.release();
      detailProducer = null;
    }
  }
  /**
   * �õ����������
   * @return �������������
   */
  protected final Class childClassName()
  {
    //Returns the runtime class of an object.
    //That Class object is the object that is locked by static synchronized methods of the represented class.
    return getClass();//Object��ķ���
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
      if(!isAdd)
        m_RowInfo.put(getMaterTable());
      else
      {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String tdbh="";
        m_RowInfo.put("czrq", today);//�Ƶ�����
        m_RowInfo.put("czy", loginName);//����Ա
        m_RowInfo.put("jhrq", today);
        m_RowInfo.put("kdrq", today);
        m_RowInfo.put("tdrq", today);
        m_RowInfo.put("czyid", loginId);
        m_RowInfo.put("jhfhrq", today);
        m_RowInfo.put("djlx", djlx);
        m_RowInfo.put("zt", "0");
        tdbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_td.6','tdbh') from dual");
        m_RowInfo.put("tdbh", tdbh);
        m_RowInfo.put("jbr", loginName);
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
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));
      detailRow.put("wzdjid", rowInfo.get("wzdjid_"+i));//
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//
      detailRow.put("kdsl", formatNumber(rowInfo.get("kdsl_"+i), qtyFormat));//
      detailRow.put("hssl", rowInfo.get("hssl_"+i));//
      detailRow.put("jje", formatNumber(rowInfo.get("jje_"+i), sumFormat));//�����
      detailRow.put("xsj", formatNumber(rowInfo.get("xsj_"+i), priceFormat));//���ۼ�
      detailRow.put("zk", rowInfo.get("zk_"+i));//�ۿ�
      detailRow.put("dj", rowInfo.get("dj_"+i));//����
      detailRow.put("bz", rowInfo.get("bz_"+i));//��ע
      detailRow.put("hthwid", rowInfo.get("hthwid_"+i));
    }
  }

          /*�õ������*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }
  public final EngineDataSet getMaterListTable()
  {
    return dsMasterList;
  }
          /*�õ��ӱ�����*/
  public final EngineDataSet getDetailTable(){
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    return dsDetailTable;
  }
          /*�õ��ܽ��*/
  public final String getZje()
  {
    return zzje;
  }
          /*�򿪴ӱ�*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    String SQL =  combineSQL(DETAIL_SQL,"?",new String[]{isMasterAdd ? "-1" : tdid});
    dsDetailTable.setQueryString(SQL);
    if(!dsDetailTable.isOpen())
    {
      dsDetailTable.open();
    }
    else
    {
      dsDetailTable.refresh();
    }
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
   * �����Ƿ������״̬
   * @return �Ƿ������״̬
   */
  public final boolean masterIsAdd() {return isMasterAdd; }
  /**
   *���뵥λ�Ŀͻ�������Ϣ
   * */
  public RowMap getkhcpzk(String dwtxid,String cpid)
  {
    RowMap xyedRow ;
    try{
      EngineDataSet dskhcpzk = new EngineDataSet();
      setDataSetProperty(dskhcpzk,combineSQL(KHCPZK_SQL,"?",new String[]{dwtxid,cpid}));
      dskhcpzk.open();
      dskhcpzk.first();
      xyedRow= new RowMap(dskhcpzk);
      }catch(Exception e)
      {
        xyedRow=new RowMap();
      }
      return xyedRow;
  }
  /**
   * �͵��ӱ��������ʵĴ����������
   *
   **/
  public String getChlbmc(String tdid) throws Exception
  {
    StringBuffer chlbmc = new StringBuffer();
    EngineDataSet dsChlbmc = new EngineDataSet();
    setDataSetProperty(dsChlbmc,"SELECT c.chmc FROM xs_tdhw a,kc_dm b,kc_chlb c WHERE a.cpid=b.cpid AND b.chlbid=c.chlbid AND a.tdid='"+tdid+"'");
    dsChlbmc.open();
    dsChlbmc.first();
    for(int i=0;i<dsChlbmc.getRowCount();i++)
    {
      chlbmc = chlbmc.append(dsChlbmc.getValue("chmc")).append(",");
      dsChlbmc.next();
    }
    dsChlbmc.close();
    return chlbmc.toString();
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
   * �õ�ѡ�е��е�����
   * @return ������-1����ʾû��ѡ�е���
   */
  public final int getSelectedRow()
  {
    if(masterRow < 0)
      return -1;
    dsMasterList.goToInternalRow(masterRow);
    return dsMasterList.getRow();
  }
  /**
   * �Ƿ�������
   * �ᵥ�������ڳ�������ʱ,�ſ������
   * ֻҪ��һ���ᵥ����û��ȫ����,�Ͳ������
   * @param htid
   * @return
   */
  public boolean isCanOver(String tdid)
  {
    if(tdid.equals(""))
      return false;
    String count="";
    try
    {
      count = dataSetProvider.getSequence(CAN_OVER_SQL+tdid);
    }
    catch(Exception e){}
    if(!count.equals("0"))
      return false;
    else
      return true;
  }
  /**
   *��ʵ����������0�������,���������ȡ������
   * */
  public boolean isCanCancer(String tdid)
  {
    if(tdid.equals(""))
      return false;
    String count="";
    try
    {
      count = dataSetProvider.getSequence(CAN_CANCER_SQL+tdid);
    }
    catch(Exception e){}
    if(!count.equals("0"))
      return false;
    else
      return true;
  }
  /**
   *
   * @param htid
   * @return
   */
  public boolean hasReferenced(String tdid)
  {
    if(tdid.equals(""))
      return false;
    String count="";
    try
    {
      count = dataSetProvider.getSequence(REFERENCED_SQL+tdid);
    }
    catch(Exception e){}
    if(!count.equals("0"))
      return true;
    else
      return false;
  }
  //&#$
  /**
   * ���������Ĵ�����
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isMasterAdd=false;
      String id=null;
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      if(String.valueOf(REPORT).equals(action))
      {
        isReport=true;
        isApprove = false;
        id=request.getParameter("id");
      }else
      {
        isApprove = true;
        id = data.getParameter("id", "");
      }
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen())
        dsMasterTable.readyRefresh();
      dsMasterTable.refresh();
      //�򿪴ӱ�
      tdid = id;
      openDetailTable(false);
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
      dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterList.getValue("tdbh");
      String deptid = dsMasterList.getValue("deptid");
      String tdid = dsMasterList.getValue("tdid");
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_td a WHERE a.tdid='"+tdid+"'");
      if(zt!=null&&!zt.equals("0"))
      {
        dsMasterList.readyRefresh();
        return;
      }
      approve.putAproveList(dsMasterList, dsMasterList.getRow(),"strike_balance", content,deptid);
    }
  }
  /**
   * ȡ������
   */
  class Cancer_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));

      String tdid = dsMasterList.getValue("tdid");
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_td a WHERE a.tdid='"+tdid+"'");
      if(zt!=null&&!zt.equals("1"))
      {
        dsMasterList.readyRefresh();
        return;
      }

      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterList,dsMasterList.getRow(),"strike_balance");
      dsMasterList.readyRefresh();
    }
  }
  /**
   * ����
   */
  class Cancer implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsMasterList.goToRow(row);
      tdid = dsMasterList.getValue("tdid");

      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_td a WHERE a.tdid='"+tdid+"'");
      if(zt!=null&&(zt.equals("8")||zt.equals("9")))
      {
        dsMasterList.readyRefresh();
        return;
      }

      dsMasterTable.setQueryString(combineSQL(MASTER_EDIT_SQL,"?",new String[]{tdid}));
      if(!dsMasterTable.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();
      dsMasterTable.setValue("zt", "4");
      dsMasterTable.saveChanges();

      dsMasterList.readyRefresh();
    }
  }
  /**
   * 2004-4-5
   * ���
   * sl>stslʱ����ȷ����ȫ������
   * */
  class Lading_Out implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterList.getInternalRow();
      tdid = dsMasterList.getValue("tdid");

      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_td a WHERE a.tdid='"+tdid+"'");
      if(zt!=null&&(zt.equals("0")||zt.equals("9")||zt.equals("4")))
      {
        dsMasterList.readyRefresh();
        return;
      }

      dsMasterTable.setQueryString(combineSQL(MASTER_EDIT_SQL,"?",new String[]{tdid}));
      if(!dsMasterTable.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();
      //String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      dsMasterTable.setValue("zt", "8");
      //dsMasterTable.setValue("tdrq",today);
      dsMasterTable.saveChanges();
      dsMasterList.readyRefresh();
    }
  }
  /**
   *��������������ϸ��Ӧ�տ�С�ڵ��ڸ��������ʵ�ս��ʱ������Ϊ��ɡ�
   * ���

     class Lading_Over implements Obactioner
     {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
     int rownum = Integer.parseInt(data.getParameter("rownum"));
     dsMasterTable.goToRow(rownum);
     tdid = dsMasterTable.getValue("tdid");
     openDetailTable(false);
     String lx = dsMasterTable.getValue("djlx");
     dsDetailTable.first();
     for(int i=0;i<dsDetailTable.getRowCount();i++)
     {
     String jje = dsDetailTable.getValue("jje");
     String ssje = dsDetailTable.getValue("ssje");
     if((Math.abs(Double.parseDouble(jje.equals("")?"0":jje)))>(Math.abs(Double.parseDouble(ssje.equals("")?"0":ssje))))
     {
     data.setMessage(showJavaScript(lx.equals("1")?"alert('���п���δ�ջ�,�������!')":"alert('���п���δ����,�������!')"));
     return;
     }
     dsDetailTable.next();
     }

     dsMasterTable.setValue("zt","8");
              dsMasterTable.saveChanges();
            }
          }
          */
         /**
          * ��ʼ�������Ĵ�����
          */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dwdm ="";
      dwmc ="";
      //&#$
      isApprove = false;
      isReport = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      tCopyNumber = "1";
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      masterListProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //��ʼ����ѯ��Ŀ������
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("kdrq$a", startDay);
      row.put("kdrq$b", today);
      //row.put("zt","0");
      zt = new String[]{""};
      isMasterAdd = true;
      String SQL = " AND zt<>8   AND zt<>3  AND zt<>4 ";
      String MSQL =  combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      dsMasterList.setQueryString(MSQL);
      dsMasterList.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      isReport=false;

      String ss=dsMasterTable.getQueryString();

      String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      if(code.equals("1"))
        submitType=true;
      else
        submitType=false;

      //SLSQL =  combineSQL(MASTER_SUM_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      JESQL = combineSQL(MASTER_JE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});



      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");


      zzje = zzje.equals("")?"0":zzje;

      //zzsl = formatNumber(zzsl, priceFormat);
      //zzje = formatNumber(zzje, priceFormat);
    }
  }
  /**
   * ��ʾ�ӱ���б���Ϣ
   */
  class ShowDetail implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterList.getInternalRow();
      tdid = dsMasterList.getValue("tdid");
      //�򿪴ӱ�
      openDetailTable(false);
    }
  }
  /**
   * ---------------------------------------------------------
   * ������ӻ��޸Ĳ����Ĵ�����
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //&#$
      isApprove = false;
      if(String.valueOf(EDIT).equals(action))
      {
        isMasterAdd=false;
        dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterList.getInternalRow();
        tdid = dsMasterList.getValue("tdid");
        String dwtxid = dsMasterList.getValue("dwtxid");
      }
      else
        isMasterAdd=true;

      dsMasterTable.setQueryString(isMasterAdd?MASTER_STRUT_SQL:combineSQL(MASTER_EDIT_SQL,"?",new String[]{tdid}));
      if(!dsMasterTable.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();

      openDetailTable(isMasterAdd);
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      data.setMessage(showJavaScript("toDetail();"));
    }
  }
  /**
   * ---------------------------------------------------------
   * ��ͬ�б���ֱ�������ᵥ
   */
  class Master_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //&#$
      isApprove = false;
      isReport = false;
      isMasterAdd = true;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      tCopyNumber = "1";
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //��ʼ����ѯ��Ŀ������
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("kdrq$a", startDay);
      row.put("kdrq$b", today);
      //row.put("zt","0");
      zt = new String[]{""};

      if(!dsMasterList.isOpen())
        dsMasterList.open();
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      isReport=false;

      String SQL = " AND zt<>8  AND zt<>2  AND zt<>3 ";
      //SLSQL =  combineSQL(MASTER_SUM_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      JESQL = combineSQL(MASTER_JE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
    }
  }
  /**
   * ����������Ĵ�����
   */
  class Master_Post implements Obactioner
  {
    private String zt="0";
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      zt = ds.getValue("zt");
      if(isMasterAdd)
        zt="0";
      //У�������
      String temp = checkMasterInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      if(isMasterAdd){
        String tdbh = rowInfo.get("tdbh");
        String count = dataSetProvider.getSequence("select count(*) from xs_td t where t.tdbh='"+tdbh+"'");
        if(!count.equals("0"))
        {
          tdbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_td.6','tdbh') from dual");
        }
        ds.insertRow(false);
        tdid = dataSetProvider.getSequence("s_xs_td");
        ds.setValue("tdid", tdid);//����
        zt="0";
        ds.setValue("zt","0");
        ds.setValue("tdbh",tdbh);
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//�Ƶ�����
        ds.setValue("czyid", loginId);
        ds.setValue("czy", loginName);//����Ա
        ds.setValue("fgsid", fgsid);//�ֹ�˾
      }else
        zt = ds.getValue("zt");
      //����ӱ������
      EngineDataSet detail = getDetailTable();
      if(isMasterAdd){
        detail.insertRow(false);
        detail.setValue("tdid", tdid);
      }else
        detail.first();
      detail.setValue("jje", rowInfo.get("zje"));//�ܽ��
      detail.setValue("zk", rowInfo.get("100"));
      detail.setValue("xsje", rowInfo.get("zje"));
      detail.post();

      //������������
      ds.setValue("kdrq", rowInfo.get("tdrq"));
      ds.setValue("tdrq", rowInfo.get("tdrq"));
      ds.setValue("deptid", rowInfo.get("deptid"));//����id
      ds.setValue("jsfsid", rowInfo.get("jsfsid"));//���㷽ʽID
      ds.setValue("dwtxid", rowInfo.get("dwtxid"));//������λID
      ds.setValue("personid", rowInfo.get("personid"));//��ԱID

      ds.setValue("djlx", djlx);//��������
      ds.setValue("dz", rowInfo.get("dz"));
      ds.setValue("bz", rowInfo.get("bz"));
      ds.setValue("zje", rowInfo.get("zje"));//�ܽ��
      ds.setValue("khlx", rowInfo.get("khlx"));//�ͻ�����
      ds.setValue("jbr", rowInfo.get("jbr"));
      ds.setValue("fgsid", fgsid);//�ֹ�˾
      //�����û��Զ�����ֶ�
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      isMasterAdd = false;

      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      zzje = zzje.equals("")?"0":zzje;
      dsMasterList.readyRefresh();

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);//���³�ʼ���ӱ�ĸ�����Ϣ
        detail.empty();
        initRowInfo(false, false, true);//���³�ʼ���ӱ�ĸ�����Ϣ
      }
    }
    /**
     * У����������Ϣ�ӱ��������Ϣ����ȷ��
     * @return null ��ʾû����Ϣ,У��ͨ��
     */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      String temp = rowInfo.get("tdrq");
      if(temp.equals(""))
        return showJavaScript("alert('�������ڲ���Ϊ�գ�');");
      else if(!isDate(temp))
        return showJavaScript("alert('�Ƿ��ᵥ���ڣ�');");
      temp = rowInfo.get("dwtxid");
      if(temp.equals(""))
        return showJavaScript("alert('��ѡ�񹺻���λ��');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('��ѡ���ţ�');");
      temp = rowInfo.get("personid");
      if(temp.equals(""))
        return showJavaScript("alert('��ѡ��ҵ��Ա��');");
      temp = rowInfo.get("jsfsid");
      if(temp.equals(""))
        return showJavaScript("alert('��ѡ����㷽ʽ��');");
      temp = rowInfo.get("khlx");
      if(temp.equals(""))
        return showJavaScript("alert('��ѡ��ͻ����ͣ�');");
      temp = rowInfo.get("zje");
      if(temp.equals(""))
        return showJavaScript("alert('�������');");
      String zje = rowInfo.get("zje");
      if((temp = checkNumber(zje, "���")) != null)
          return temp;
      //double dzje = Double.parseDouble(zje.equals("")?"0":zje);
      //if(dzje>0)
      //  return showJavaScript("alert('���ܴ���0��');");
      return null;
    }
  }
  /**
   * ����������Ĵ�����
   */
  class Approved_Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isMasterAdd=false;
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      ds.goToInternalRow(masterRow);

      //����ӱ������
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0),totalZje=new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      double zsl=0.0;
      double zje = 0;
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        detail.setValue("tdid", tdid);
        detail.setValue("cpid", detailrow.get("cpid"));//?
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        double xsj = detailrow.get("xsj").length() > 0 ? Double.parseDouble(detailrow.get("xsj")) : 0;//���ۼ�
        double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;  //����
        double dj = detailrow.get("dj").length() > 0 ? Double.parseDouble(detailrow.get("dj")) : 0;//����
        double hssl = detailrow.get("hssl").length() > 0 ? Double.parseDouble(detailrow.get("hssl")) : 0;
        double xsje =sl*xsj; //detailrow.get("xsje").length() > 0 ? Double.parseDouble(detailrow.get("xsje")) : 0;
        double jje = sl*dj;  //detailrow.get("jje").length() > 0 ? Double.parseDouble(detailrow.get("jje")) : 0;
        zje =zje+jje;
        zsl=zsl+sl;
        detail.setValue("sl", detailrow.get("sl"));//����
        detail.setValue("xsj", detailrow.get("xsj"));//���ۼ�

        detail.setValue("zk", detailrow.get("zk"));//�ۿ�
        detail.setValue("dj", detailrow.get("dj"));//����
        detail.setValue("hssl", detailrow.get("hssl"));//
        detail.setValue("wzdjid", detailrow.get("wzdjid"));  //����?
        detail.setValue("jje",formatNumber(String.valueOf(sl * dj), sumFormat));
        new BigDecimal(detailrow.get("sl")).multiply(new BigDecimal(detailrow.get("dj")));
        detail.setValue("bz", detailrow.get("bz"));//��ע

        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("sl"));
        totalSum = totalSum.add(detail.getBigDecimal("xsje"));
        totalZje =totalZje.add(new BigDecimal(String.valueOf(jje)));
        detail.next();
      }

      ds.setValue("zsl", String.valueOf(zsl));//�ܽ��
      ds.setValue("zje", String.valueOf(zje));//�ܽ��

      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);


      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");


      zzje = zzje.equals("")?"0":zzje;

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
      String id = ds.getValue("tdid");
      String count = dataSetProvider.getSequence("SELECT SUM(nvl(a.stsl,0)) FROM xs_tdhw a WHERE a.tdid='"+id+"'");
      if(!count.equals("0"))
      {
        data.setMessage(showJavaScript("alert('���ᵥ�ѱ�����,����ɾ��!')"));
        return;
      }
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_td a WHERE a.tdid='"+id+"'");
      if(zt!=null&&!zt.equals("0"))
      {
        data.setMessage(showJavaScript("alert('���ᵥ����ɾ��!')"));
        return;
      }
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);


      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      zzje = zzje.equals("")?"0":zzje;
      zzje = formatNumber(zzje, priceFormat);

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
      HttpServletRequest request = data.getRequest();
      dwdm =request.getParameter("dwdm");
      dwmc =request.getParameter("dwmc");

      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      zt = data.getRequest().getParameterValues("zt");
      if(!(zt==null))
      {
        StringBuffer sbzt = null;
        for(int i=0;i<zt.length;i++)
        {
          if(sbzt==null)
            sbzt= new StringBuffer(" AND zt IN(");
          sbzt.append(zt[i]+",");
        }
        if(sbzt == null)
          sbzt =new StringBuffer();
        else
          sbzt.append("-99)");
        SQL = SQL+sbzt.toString();
      }
      else
        zt = new String[]{""};


      String MSQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});//��װSQL���
      if(!dsMasterList.getQueryString().equals(MSQL))
      {
        dsMasterList.setQueryString(MSQL);
        dsMasterList.setRowMax(null);
      }
      //SLSQL =  combineSQL(MASTER_SUM_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      JESQL = combineSQL(MASTER_JE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});



      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      zzje = zzje.equals("")?"0":zzje;

      //zzsl = formatNumber(zzsl, priceFormat);
      //zzje = formatNumber(zzje, priceFormat);
    }
    /**
     * ��ʼ����ѯ�ĸ�����
     * @param request web�������
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet master = dsMasterList;
      //EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //��ʼ���̶��Ĳ�ѯ��Ŀ
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("tdbh"), null, null, null, null, "="),

        new QueryColumn(master.getColumn("tdrq"), null, null, null, "a", ">="),//�Ƶ�����
        new QueryColumn(master.getColumn("tdrq"), null, null, null, "b", "<="),//�Ƶ�����

        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//����id
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("personid"), "emp", "personid", "xm", "xm", "like"),//

        new QueryColumn(master.getColumn("czy"), null, null, null, null, "like")
      });
      isInitQuery = true;
    }
  }
  /**
   *  �ӱ�����
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      if(String.valueOf(DETAIL_ADD).equals(action))
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("tdhwid", "-1");
        //dsDetailTable.setValue("tdid", tdid);
        //dsDetailTable.setValue("sfcz", "0");
        dsDetailTable.post();
        RowMap detailrow = new RowMap(dsDetailTable);
        d_RowInfos.add(detailrow);
      }
    }
  }
  /**
   * ���Ƶ�ǰ��
   *
   * */
  class Detail_Copy_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      String rownum = req.getParameter("rownum");
      tCopyNumber = req.getParameter("tCopyNumber");
      int row = Integer.parseInt(rownum);
      int size = d_RowInfos.size();
      if(row>size)
        return;
      RowMap newadd = (RowMap)d_RowInfos.get(row);

      String cpid = newadd.get("cpid");
      String wzdjid = newadd.get("wzdjid");
      String xsj = newadd.get("xsj");
      String hthwid = newadd.get("hthwid");
      String zk = newadd.get("zk");
      int copynumber = Integer.parseInt(tCopyNumber);

      for(int i=0;i<copynumber;i++)
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("tdhwid", "-1");
        dsDetailTable.setValue("tdid", tdid);
        dsDetailTable.setValue("cpid", cpid);
        dsDetailTable.setValue("wzdjid", wzdjid);
        dsDetailTable.setValue("xsj", xsj);
        dsDetailTable.setValue("hthwid", hthwid);
        dsDetailTable.setValue("zk", zk);
        dsDetailTable.post();
        RowMap detailrow = new RowMap();
        detailrow.put("tdid", tdid);
        detailrow.put("cpid", cpid);
        detailrow.put("wzdjid", wzdjid);
        detailrow.put("xsj", xsj);
        detailrow.put("hthwid", hthwid);
        detailrow.put("zk", zk);
        d_RowInfos.add(detailrow);
      }
      tCopyNumber="1";
    }
  }
  class Detail_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      putDetailInfo(data.getRequest());
      String dwtxid = data.getParameter("dwtxid");

      RowMap detailRow = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailRow = (RowMap)d_RowInfos.get(i);
        String wzdjid = detailRow.get("wzdjid");
        String cpid = detailRow.get("cpid");
        if(rownum==i)
        {
          RowMap khcpzkrow = getkhcpzk(dwtxid,cpid);
          String zk = khcpzkrow.get("zk");
          String djlx = khcpzkrow.get("djlx");
          String mrzk = dataSetProvider.getSequence("select t.mrzk from xs_wzdj t WHERE t.wzdjid='"+wzdjid+"'");
          String xsj= "";
          if(djlx.equals(""))
          {
            String mrjg = dataSetProvider.getSequence("select t.mrjg from xs_wzdj t WHERE t.wzdjid='"+wzdjid+"'");
            if(mrjg==null||mrjg.equals(""))
            {
              data.setMessage(showJavaScript("alert('�ò�Ʒû����Ĭ�ϼ۸�!')"));
              return;
            }
            xsj = dataSetProvider.getSequence("select t."+mrjg+" from xs_wzdj t WHERE t.wzdjid='"+wzdjid+"'");
          }
          else
            xsj = dataSetProvider.getSequence("select t."+djlx+" from xs_wzdj t WHERE t.wzdjid='"+wzdjid+"'");

          if(xsj==null||xsj.equals(""))
          {
            data.setMessage(showJavaScript("alert('�ò�Ʒû����Ĭ�ϼ۸�!')"));
            return;
          }
          if(zk==null||zk.equals(""))
            zk=mrzk;
          if(zk==null||zk.equals(""))
            zk="100";
          detailRow.put("xsj", xsj);
          detailRow.put("dj", new BigDecimal(xsj).multiply(new BigDecimal(zk).divide(new BigDecimal(100),4,4)).toString());
          detailRow.put("zk", zk);
        }
      }
    }
  }
  /**
   *���Ÿı�
   * */
  class Dept_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      m_RowInfo.put(req);
    }
  }
  /**
   * �����ͬ
   * ��Ӧҳ�������ͬ����
   * һ��ֻ������һ�ź�ͬ�Ļ���
   * �������ۺ�ͬ����
   * */
  class Detail_SALE_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_CORP);
      //�����������ϸ��Ϣ
      putDetailInfo(data.getRequest());
      String importOrder = m_RowInfo.get("importOrder");
      String dwtxid =  m_RowInfo.get("dwtxid");
      String jsfsid =  m_RowInfo.get("jsfsid");
      String deptid =  m_RowInfo.get("deptid");
      String personid =  m_RowInfo.get("personid");
      String khlx =  m_RowInfo.get("khlx");
      String sendmodeid =  m_RowInfo.get("sendmodeid");
      String jhfhrq =  m_RowInfo.get("jhfhrq");
      String dz =  m_RowInfo.get("dz");
      if(importOrder.length() == 0)
        return;
      //ʵ�����������ݼ�����
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "hthwID");
      String[] hthwIDs = parseString(importOrder,",");//��������ͬ����ID����
      b_ImportOrderBean =getb_ImportOrderBean(req);     //���
      BigDecimal bd = new BigDecimal(0);
      double max = 0;
      for(int i=0; i < hthwIDs.length; i++)
      {
        if(hthwIDs[i].equals("-1"))
          continue;
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String tdid = dsMasterTable.getValue("tdid");
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, hthwIDs[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap orderRow =null;
          orderRow = b_ImportOrderBean.getLookupRow(hthwIDs[i]);//���
          if(dwtxid.equals(""))
          {
            dwtxid = orderRow.get("dwtxid");
            m_RowInfo.put("dwtxid",orderRow.get("dwtxid"));
          }
          if(jsfsid.equals(""))
          {
            jsfsid = orderRow.get("jsfsid");
            m_RowInfo.put("jsfsid",orderRow.get("jsfsid"));
          }
          if(dz.equals(""))
          {
            dz = orderRow.get("dz");
            m_RowInfo.put("dz",orderRow.get("dz"));
          }
          if(deptid.equals(""))
          {
            deptid = orderRow.get("deptid");
            m_RowInfo.put("deptid",orderRow.get("deptid"));
          }
          if(jhfhrq.equals(""))
          {
            jhfhrq = orderRow.get("jhfhrq");
            m_RowInfo.put("jhfhrq",orderRow.get("jhfhrq"));
          }
          if(sendmodeid.equals(""))
          {
            sendmodeid = orderRow.get("sendmodeid");
            m_RowInfo.put("sendmodeid",orderRow.get("sendmodeid"));
          }
          if(personid.equals(""))
          {
            personid = orderRow.get("personid");
            m_RowInfo.put("personid",orderRow.get("personid"));
          }
          if(khlx.equals(""))
          {
            khlx = orderRow.get("khlx");
            m_RowInfo.put("khlx",orderRow.get("khlx"));
          }
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("tdhwid", "-1");
          dsDetailTable.setValue("tdid", tdid);
          dsDetailTable.setValue("cpid", orderRow.get("cpid"));
          dsDetailTable.setValue("hthwid", hthwIDs[i]);
          dsDetailTable.setValue("wzdjid", orderRow.get("wzdjid"));
          String dmsxid = orderRow.get("dmsxid");
          dsDetailTable.setValue("dmsxid", dmsxid);

          String cjtcl = orderRow.get("cjtcl");
          dsDetailTable.setValue("cjtcl", cjtcl);
          dsDetailTable.setValue("hlts", orderRow.get("hlts"));
          String jzj = orderRow.get("jzj");
          dsDetailTable.setValue("jzj", orderRow.get("jzj"));
          String jxts = orderRow.get("jxts");
          dsDetailTable.setValue("jxts", orderRow.get("jxts"));
          String hltcl = orderRow.get("hltcl");
          dsDetailTable.setValue("hltcl", orderRow.get("hltcl"));


          double sl=Double.parseDouble(orderRow.get("sl").equals("")?"0":orderRow.get("sl"));//��ͬ����
          double stsl=Double.parseDouble(orderRow.get("stsl").equals("")?"0":orderRow.get("stsl"));//ʵ��������
          double skdsl=Double.parseDouble(orderRow.get("skdsl").equals("")?"0":orderRow.get("skdsl"));//ʵ��������
          double hssl = Double.parseDouble(orderRow.get("hssl").equals("")?"0":orderRow.get("hssl"));
          double xhssl = hssl*(sl-skdsl)/sl;
          double xsj = Double.parseDouble(orderRow.get("xsj").equals("")?"0":orderRow.get("xsj"));
          double dj = Double.parseDouble(orderRow.get("dj").equals("")?"0":orderRow.get("dj"));
          double zk = Double.parseDouble(orderRow.get("zk").equals("")?"0":orderRow.get("zk"));
          if(djlx.equals("-1"))
          {
            dsDetailTable.setValue("hssl", "-"+String.valueOf(xhssl));
            dsDetailTable.setValue("sl", "-"+String.valueOf(sl-skdsl));
            dsDetailTable.setValue("xsje",formatNumber("-"+String.valueOf(xsj*(sl-skdsl)), sumFormat) );
            dsDetailTable.setValue("jje",formatNumber("-"+String.valueOf(dj*(sl-skdsl)), sumFormat) );
          }else
          {
            dsDetailTable.setValue("hssl", String.valueOf(xhssl));
            dsDetailTable.setValue("sl", String.valueOf(sl-skdsl));
            dsDetailTable.setValue("xsje",formatNumber(String.valueOf(xsj*(sl-skdsl)), sumFormat) );
            dsDetailTable.setValue("jje", formatNumber(String.valueOf(dj*(sl-skdsl)), sumFormat) );
          }
          dsDetailTable.setValue("xsj", orderRow.get("xsj"));
          dsDetailTable.setValue("zk", orderRow.get("zk"));
          dsDetailTable.setValue("dj", orderRow.get("dj"));
          dsDetailTable.setValue("bz", orderRow.get("bz"));
          dsDetailTable.post();
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
      String ts = m_RowInfo.get("hkts");
      String kdrq = m_RowInfo.get("kdrq");
      Date startdate = new SimpleDateFormat("yyyy-MM-dd").parse(kdrq);//�ᵥ�Ŀ�������
      Date enddate = new Date(startdate.getTime() + (long)max*(60*60*24*1000));//����
      if(ts.equals(""))
      {
        m_RowInfo.put("hkts",String.valueOf((int)max));
        m_RowInfo.put("hkrq",new SimpleDateFormat("yyyy-MM-dd").format(enddate));
      }
    }
  }
  /**
   *��Ӧҳ�������ͬ�������
   * һ�ο���������ź�ͬ�Ļ���
   * */
  class Multi_Product_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      String importOrder = m_RowInfo.get("importOrderproduct");
      if(importOrder.length() == 0)
        return;
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "hthwID");
      String[] hthwIDs = parseString(importOrder,",");//��������ͬ����ID����
      for(int i=0; i < hthwIDs.length; i++)
      {
        if(hthwIDs[i].equals("-1"))
          continue;
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String hthwid = hthwIDs[i];
        importOrderProductBean =get_OrderProductBean(req);     //���
        String tdid = dsMasterTable.getValue("tdid");
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, hthwid);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap saleRow =null;
          saleRow = importOrderProductBean.getLookupRow(hthwid);
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("tdhwid", "-1");
          dsDetailTable.setValue("tdid", tdid);
          dsDetailTable.setValue("cpid", saleRow.get("cpid"));
          dsDetailTable.setValue("hthwid", hthwIDs[i]);
          dsDetailTable.setValue("wzdjid", saleRow.get("wzdjid"));
          String dmsxid = saleRow.get("dmsxid");
          dsDetailTable.setValue("dmsxid", dmsxid);


          double sl=Double.parseDouble(saleRow.get("sl").equals("")?"0":saleRow.get("sl"));
          double hssl = Double.parseDouble(saleRow.get("hssl").equals("")?"0":saleRow.get("hssl"));
          if(djlx.equals("-1"))
          {
            dsDetailTable.setValue("hssl", String.valueOf(-hssl));
            dsDetailTable.setValue("sl", "-"+saleRow.get("sl"));
            dsDetailTable.setValue("xsje",formatNumber("-"+saleRow.get("xsje"), sumFormat)  );
            dsDetailTable.setValue("jje",formatNumber("-"+saleRow.get("jje"), sumFormat) );
          }else
          {
            dsDetailTable.setValue("hssl", String.valueOf(hssl));
            dsDetailTable.setValue("sl", saleRow.get("sl"));
            dsDetailTable.setValue("xsje",formatNumber(saleRow.get("xsje"), sumFormat) );
            dsDetailTable.setValue("jje",formatNumber(saleRow.get("jje"), sumFormat) );
          }
          dsDetailTable.setValue("xsj", saleRow.get("xsj"));
          dsDetailTable.setValue("zk", saleRow.get("zk"));
          dsDetailTable.setValue("dj", saleRow.get("dj"));
          dsDetailTable.setValue("bz", saleRow.get("bz"));
          dsDetailTable.post();
          //����һ�����û����Ӧ����
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
    }
  }
  /**
   *�ı乺����λʱ�����Ĳ���
   * */
  class Dwtxid_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      engine.project.LookUp corpBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_CORP);
      engine.project.LookUp creditBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_CORP_CREDIT);
      String olddwtxId=m_RowInfo.get("dwtxid");
      putDetailInfo(data.getRequest());//�����������ϸ��Ϣ
      String dwtxid=m_RowInfo.get("dwtxid");
      RowMap corRow = corpBean.getLookupRow(dwtxid);
      creditBean.regData(new String[]{dwtxid});
      RowMap creditRow = creditBean.getLookupRow(dwtxid);
      if(olddwtxId.equals(dwtxid))
        return;
      else
      {
        m_RowInfo.put("dwtxid",dwtxid);
        m_RowInfo.put("kdrq",req.getParameter("kdrq"));
        m_RowInfo.put("tdbh",req.getParameter("tdbh"));
        m_RowInfo.put("personid",corRow.get("personid"));
        m_RowInfo.put("deptid",corRow.get("deptid"));
        m_RowInfo.put("hkts",creditRow.get("hkts"));
        //Date startdate = new SimpleDateFormat("yyyy-MM-dd").parse(req.getParameter("tdrq"));
        //long tqq = Long.parseLong(creditRow.get("hkts").equals("")?"0":creditRow.get("hkts"));
        //Date enddate = new Date(startdate.getTime() + tqq*60*60*24*1000);
        //String endDate = new SimpleDateFormat("yyyy-MM-dd").format(enddate);
        //m_RowInfo.put("hkrq",endDate);
        dsDetailTable.empty();
        d_RowInfos.clear();
      }
    }
  }

  /**
   *  �ӱ�ɾ������
   */
  class Detail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      //ɾ����ʱ�����һ������
      if(String.valueOf(DETAIL_DEL).equals(action))
      {
        d_RowInfos.remove(rownum);
        dsDetailTable.goToRow(rownum);
        dsDetailTable.deleteRow();
      }
    }
  }
  /**
   *  �ӱ�����Ϊ�յ���ɾ������
   */
  class Detail_Delete_Null implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      delRows();
    }
    /**
     * ɾ��
     * */
    public void delRows() throws Exception
    {
      for(int i=0;i<d_RowInfos.size();i++)
      {
        RowMap detailRow = (RowMap)d_RowInfos.get(i);
        String sl = detailRow.get("sl");
        if(sl.equals(""))
        {
          d_RowInfos.remove(i);
          delRows();
        }
      }
    }
  }
  /**
   * ���������
   * ��Ӧ�����ͬ
   * �õ����ڲ��Һ�ͬ��ŵ�bean
   * @param req WEB������
   * @return �������ڲ��Һ�ͬ��ŵ�bean
   */
  public B_ImportOrder getb_ImportOrderBean(HttpServletRequest req)
  {
    if(b_ImportOrderBean == null)
      b_ImportOrderBean = B_ImportOrder.getInstance(req);
    return b_ImportOrderBean;
  }

  /**
   * ���������
   * ��Ӧ�����ͬ
   * �õ����ڲ��Һ�ͬ��ŵ�bean
   * @param req WEB������
   * @return �������ڲ��Һ�ͬ��ŵ�bean
   */
  public ImportOrderProduct get_OrderProductBean(HttpServletRequest req)
  {
    if(importOrderProductBean == null)
      importOrderProductBean = ImportOrderProduct.getInstance(req);
    return importOrderProductBean;
  }
  /**
   * �õ����ڲ��Ҳ�Ʒ���۵�bean
   * @param req WEB������
   * @return �������ڲ��Ҳ�Ʒ���۵�bean
   */
  public LookUp getSalePriceBean(HttpServletRequest req)
  {
    if(salePriceBean == null)
      salePriceBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_SALE_PRICE);
    return salePriceBean;
  }
}