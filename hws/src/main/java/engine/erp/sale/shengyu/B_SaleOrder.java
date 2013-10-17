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
 * ���۹���-���ۺ�ͬ
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @version 1.0
 */
public final class B_SaleOrder extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL        = "9001";          //��ʾ��ϸ
  public  static final String CANCER_APPROVE     = "9002";          //ȡ������
  public  static final String SALE_OVER          = "9003";          //���
  public  static final String SALE_CANCER        = "9004";          //����
  public  static final String DWTXID_CHANGE      = "9005";          //�ı�������λ
  public  static final String DEPT_CHANGE        = "9006";          //���Ÿı�
  public  static final String DETAIL_CHANGE      = "9007";          //�ӱ���ϸ�ı�
  public  static final String KHLX_CHANGE        = "9008";          //�ͻ����͸ı�!
  public  static final String DETAIL_PRODUCT_ADD = "9009";          //����ͻ���ʷ��Ʒ
  public  static final String PRICE_POST         = "9010";          //�Żݼ���������
  public  static final String REPORT             ="645355666";      //����׷��
  public  static final String CONFIRM            = "700001";        //ȷ��
  public  static final String CONFIRM_RETURN     = "700002";        //ȷ�Ϻ󷵻�
  public  static final String DETAIL_COPY        ="1013";           //���Ƶ�ǰѡ����
  public  static final String DEL_NULL           = "1014";          //ɾ������Ϊ�յ���
  public  static final String FORCE_OVER         = "1015";          //ǿ�����
  public  static final String PRODUCT_INVOC      = "1016";          //�������ú�ͬ
  public  static final String DETAIL_MULTI_ADD    = "258369";

  private static final String MASTER_STRUT_SQL   = "SELECT * FROM xs_ht WHERE 1<>1 ORDER BY htbh DESC ";
  private static final String MASTER_SQL         = "SELECT * FROM xs_ht WHERE 1=1 AND ?  AND fgsid=? ? ORDER BY htbh DESC";
  private static final String MASTER_EDIT_SQL         = "SELECT * FROM xs_ht WHERE htid='?' ";
  private static final String MASTER_SUM_SQL     = "SELECT SUM(nvl(zsl,0))zsl FROM xs_ht WHERE 1=1 AND ? AND fgsid=? ?  ";
  private static final String MASTER_JE_SQL      = "SELECT SUM(nvl(zje,0))zje FROM xs_ht WHERE 1=1 AND ? AND fgsid=? ?  ";
  private static final String DETAIL_STRUT_SQL   = "SELECT * FROM xs_hthw WHERE 1<>1";
  private static final String DETAIL_SQL         = "SELECT * FROM xs_hthw WHERE htid=";
  private static final String CAN_OVER_SQL       = "select count(*) from xs_hthw a where nvl(a.sl,0)>nvl(a.stsl,0) and a.htid=";
  private static final String CAN_GEN_SQL        = "SELECT COUNT(*) FROM xs_hthw a, xs_ht b WHERE a.htid=b.htid AND b.zt=1 AND abs(nvl(a.sl,0))>abs(nvl(a.skdsl,0)) AND a.htid=";//��ͬ��>ʵ�������Һ�ͬû���Ϻ����
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM xs_ht WHERE htid='?'";//��������ʱ����ȡһ����¼
  private static final String KHCPZK_SQL           = "SELECT * FROM xs_khcpzk where dwtxid=? and cpid=? ";

  private static final String ORDER_RECEIVE_GOODS
      = "SELECT htid FROM (SELECT a.htid, SUM(nvl(b.sl,0)) sl FROM xs_hthw a, xs_tdhw b "
      + "WHERE a.hthwid = b.hthwid AND a.htid IN (?) GROUP BY a.htid) t WHERE t.sl <> 0 ";
  private static final String XYDE_SQL = "SELECT nvl(xyed,0)-nvl(xysdl,0) FROM xs_khxyed where dwtxid= ";//�õ�������
  private static final String CUST_GRADE_SQL
      = "SELECT b.xydj FROM xs_khxyed b WHERE b.fgsid='?' AND b.dwtxid='?'";
  private static final String CUST_SALE_GOODS_STORE_SQL =
  " SELECT t.*, "
  +"       decode(nvl(z.djlx, nvl(y.djlx, t.mrjg)), 'ccj', t.ccj, 'msj', t.msj, 'lsj', t.lsj, 'qtjg1', t.qtjg1, 'qtjg2', t.qtjg2, 'qtjg3', t.qtjg3, NULL) price,"
  +"            nvl(z.zk, nvl(y.zk, t.oldzk)) mrzk, h.kcsl, p.sdsl, (nvl(h.kcsl,0)-nvl(p.sdsl,0)) kckgl "
  +" FROM "
  +" ("
  +" SELECT t.*, nvl(t.storeid,?) newstoreid FROM vw_xs_wzdj t WHERE t.fgsid='?' AND (t.storeid IS NULL OR t.storeid='?')  "
  +" UNION  "
  +" SELECT t.*, ? FROM (SELECT * FROM vw_xs_wzdj t WHERE t.fgsid='?' AND (t.storeid IS NULL OR t.storeid='?') ) t,(SELECT DISTINCT k.cpid FROM kc_wzmx k WHERE (k.zl<>0 OR k.hszl<>0) AND k.storeid='?' )k   "
  +" WHERE t.cpid=k.cpid "
  +" ) t, ("
  +" SELECT z.cpid, z.djlx, z.zk FROM xs_khcpzk z WHERE z.fgsid='?' AND z.dwtxid='?'"
  +" ) z,  kc_kchz h, vw_product_lock p, ( SELECT a.cplx, a.zk, a.djlx FROM xs_khtyzk a WHERE a.xydj='?') y "
  +" WHERE t.abc=y.cplx(+) AND t.cpid=z.cpid(+) AND t.newstoreid=h.storeid(+) AND t.cpid=h.cpid(+) "
  +"       AND t.newstoreid=p.storeid(+) AND t.cpid=p.cpid(+)  "
  +" ORDER BY t.cpbm";

  private static final String CUST_SALE_GOODS_SQL
      = "SELECT t.*, decode(nvl(z.djlx, nvl(y.djlx, t.mrjg)), 'ccj', t.ccj, 'msj', t.msj, 'lsj', t.lsj, 'qtjg1', t.qtjg1, 'qtjg2', t.qtjg2, 'qtjg3', t.qtjg3, NULL) price, "
      + " nvl(z.zk, nvl(y.zk, t.oldzk)) mrzk, h.kcsl, h.sdsl, (h.kcsl-h.sdsl) kckgl "
      + "FROM vw_xs_wzdj t, vw_kc_product_collect h,"
      + " (SELECT z.cpid, z.djlx, z.zk FROM xs_khcpzk z WHERE z.fgsid=? AND z.dwtxid='?') z,"
      + " (SELECT a.cplx, a.zk, a.djlx FROM xs_khtyzk a WHERE a.xydj='?') y "
      + "WHERE t.abc=y.cplx(+) AND t.cpid=z.cpid(+) AND t.fgsid=h.fgsid(+) AND t.cpid=h.cpid(+) "
      + " AND t.fgsid=? ORDER BY cpbm";

  private EngineDataSet dsMasterTable  = new EngineDataSet();  //����
  private EngineDataSet dsMasterList  = new EngineDataSet();  //����
  private EngineDataSet dsDetailTable  = new EngineDataSet();  //�ӱ�
  private EngineDataSet dsCancel = new EngineDataSet();        //��
  private ArrayList cancelOrder = new ArrayList();             //������
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "xs_ht");
  public  HtmlTableProducer masterListProducer = new HtmlTableProducer(dsMasterList, "xs_ht");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "xs_hthw");
  private boolean isMasterAdd = true;    //�Ƿ������״̬
  public  boolean isApprove = false;     //�Ƿ�������״̬
  private long    masterRow = -1;         //���������޸Ĳ������м�¼ָ��
  private RowMap  m_RowInfo    = new RowMap(); //��������л��޸��е�����
  private ArrayList d_RowInfos = new ArrayList(); //�ӱ���м�¼������
  private LookUp salePriceBean = null; //���۵��۵�bean������, ������ȡ���۵���
  private boolean isInitQuery = false; //�Ƿ��Ѿ���ʼ����ѯ����
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = "";
  public  String loginid = "";   //��¼Ա����ID
  public  String loginCode = ""; //��½Ա���ı���
  public  String loginName = ""; //��¼Ա��������
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //�ֹ�˾ID
  private String htid = null;
  private User user = null;
  public boolean isReport = false;
  public boolean submitType;//�����ж�true=���ƶ��˿��ύ,false=��Ȩ���˿��ύ
  public boolean appear=false;//�����ۺ�ͬ����ʱ,�Ƿ�Ҫ��ʾ��Ҫ��������Ϣ
  public boolean conversion=false;//���ۺ�ͬ�Ļ��������������Ƿ���Ҫǿ��ת��ȡ
  public String []zt;
  public String  hkts="";//�ÿͻ��Ļؿ�����
  public String tCopyNumber = "1";
  public String sfxdw ="";//�����С��λ
  private String zzsl="";//������
  private String zzje="";//�ܽ��
  private String SLSQL="";
  private String JESQL="";
  public String dwdm="";
  public String dwmc="";
  public boolean showable=false;//�Ƿ���ʾ ��������(%) ��Ϣ���� �������� ���������(%)
  public boolean isProductInvoke = false;
  public String jglx = "";
  public String zkl = "";
  /**
   * ���ۺ�ͬ�б��ʵ��
   * @param request jsp����
   * @param isApproveStat �Ƿ�������״̬
   * @return �������ۺ�ͬ�б��ʵ��
   */
  public static B_SaleOrder getInstance(HttpServletRequest request)
  {
    B_SaleOrder saleOrderBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "saleOrderBean";
      saleOrderBean = (B_SaleOrder)session.getAttribute(beanName);
      if(saleOrderBean == null)
      {
        //����LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        saleOrderBean = new B_SaleOrder();
        saleOrderBean.qtyFormat = loginBean.getQtyFormat();
        saleOrderBean.priceFormat = loginBean.getPriceFormat();
        saleOrderBean.sumFormat = loginBean.getSumFormat();

        saleOrderBean.fgsid = loginBean.getFirstDeptID();
        saleOrderBean.loginid = loginBean.getUserID();
        saleOrderBean.loginName = loginBean.getUserName();
        if(loginBean.getSystemParam("XS_ORDER_SHOW_CREDIT").equals("1"))
          saleOrderBean.appear=true;
        if(loginBean.getSystemParam("SC_STORE_UNIT_STYLE").equals("1"))
          saleOrderBean.conversion = true;
        if(loginBean.getSystemParam("XS_ORDER_SHOW_ADD_FIELD").equals("1"))
          saleOrderBean.showable = true;//��ʾ ��������(%) ��Ϣ���� �������� ���������(%)
        //���ø�ʽ�����ֶ�
        saleOrderBean.dsDetailTable.setColumnFormat("sl", saleOrderBean.qtyFormat);
        saleOrderBean.dsDetailTable.setColumnFormat("hssl", saleOrderBean.qtyFormat);
        saleOrderBean.dsDetailTable.setColumnFormat("xsj", saleOrderBean.priceFormat);
        saleOrderBean.dsDetailTable.setColumnFormat("dj", saleOrderBean.priceFormat);
        saleOrderBean.dsDetailTable.setColumnFormat("xsje", saleOrderBean.sumFormat);
        saleOrderBean.dsDetailTable.setColumnFormat("jje", saleOrderBean.sumFormat);
        saleOrderBean.dsMasterTable.setColumnFormat("zsl", saleOrderBean.qtyFormat);
        saleOrderBean.dsMasterTable.setColumnFormat("zje", saleOrderBean.sumFormat);
        saleOrderBean.dsMasterTable.setColumnFormat("wbje", saleOrderBean.sumFormat);

        saleOrderBean.user = loginBean.getUser();
        session.setAttribute(beanName, saleOrderBean);
      }
    }
    return saleOrderBean;
  }

  /**
   * ���캯��
   */
  private B_SaleOrder()
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
    setDataSetProperty(dsCancel, null);
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"hthwid"}, new String[]{"s_xs_hthw"}));
    dsMasterList.addLoadListener(new com.borland.dx.dataset.LoadListener(){
      public void dataLoaded(com.borland.dx.dataset.LoadEvent event)
      {
        cancelOrder.clear();
        if(dsMasterList.getRowCount() == 0)
          return;
        String sql = getWhereIn(dsMasterList, "htid", null);
        sql = combineSQL(ORDER_RECEIVE_GOODS, "?", new String[]{sql});
        dsCancel.setQueryString(sql);
        if(dsCancel.isOpen())
          dsCancel.refresh();
        else
          dsCancel.openDataSet();
        for(int i=0; i<dsCancel.getRowCount(); i++)
        {
          cancelOrder.add(dsCancel.getValue("htid"));
          dsCancel.next();
        }
        dsCancel.closeDataSet();
      }
    });

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(PRODUCT_INVOC), new Init());

    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(PRICE_POST), masterPost);
    //PRICE_POST
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    //&#$//��˲���
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());//ȡ������
    addObactioner(String.valueOf(SALE_CANCER), new Cancer());//����
    addObactioner(String.valueOf(SALE_OVER), new Over());//���
    addObactioner(String.valueOf(DWTXID_CHANGE), new Dwtxid_Change());
    addObactioner(String.valueOf(DETAIL_CHANGE), new Detail_Change());
    addObactioner(String.valueOf(KHLX_CHANGE), new Khlx_Change());//DETAIL_PRODUCT_ADD

    addObactioner(String.valueOf(DEPT_CHANGE), new Dept_Change());

    addObactioner(String.valueOf(REPORT), new Approve());
    addObactioner(String.valueOf(CONFIRM), masterPost);
    addObactioner(String.valueOf(CONFIRM_RETURN), masterPost);
    addObactioner(String.valueOf(DETAIL_COPY), new Detail_Copy_Add());//DETAIL_COPY
    addObactioner(String.valueOf(DEL_NULL), new Detail_Delete_Null());//Detail_Delete_Null
    addObactioner(String.valueOf(FORCE_OVER), new Over());

    addObactioner(String.valueOf(DETAIL_MULTI_ADD), new Detail_Multi_Add());
  }
  /**
   *��������˺�ͬδ�������
   * */
  public RowMap getHtzje()
  {
    EngineDataSet tmp=new EngineDataSet();
    String dwtxid=m_RowInfo.get("dwtxid");
    try{
      setDataSetProperty(tmp,"select xs_ht.dwtxid,xs_hthw.jje jje  from xs_ht,xs_hthw where xs_ht.htid=xs_hthw.htid AND xs_ht.zt='1' AND xs_ht.dwtxid="+dwtxid);
      }catch(Exception e){}
      tmp.open();
      RowMap jerow=new RowMap();
      BigDecimal zje=new BigDecimal(0);
      int j=tmp.getRowCount();
      tmp.first();
      for(int i=0;i<tmp.getRowCount();i++)
      {
        zje = tmp.getBigDecimal("jje").add(zje);//���۽��
        tmp.next();
      }
      jerow.put("zje",zje.toString());
      return jerow;
  }
  /**
   *����ҵ��Ա������㹫ʽ
   * */
  public RowMap getJjjsgs()
  {
    EngineDataSet jsgs=new EngineDataSet();
    try{
      setDataSetProperty(jsgs,"select * FROM xs_jjjsgs");
      }catch(Exception e){}
      jsgs.open();
      jsgs.first();
      RowMap jsgsrow=new RowMap();
      jsgsrow.put(jsgs);
      String xsjzj=jsgs.getValue("xsjzj");
      String tclzj=jsgs.getValue("tclzj");
      String hltszj=jsgs.getValue("hltszj");
      String xsjjs=jsgs.getValue("xsjjs");
      return jsgsrow;
  }
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
  /********�õ����۲�Ʒ���۵�Ĭ�ϼ۸�*********/
  public final String getCpbj(String cpid)
  {
    String mrjg=null;
    try{
      mrjg = dataSetProvider.getSequence("SELECT mrjg  FROM xs_wzdj b WHERE b.cpid='"+cpid+"'");
      if(mrjg==null||mrjg.equals(""))
        return "";
      mrjg = dataSetProvider.getSequence("SELECT "+mrjg+"  FROM xs_wzdj b WHERE b.cpid='"+cpid+"'");
      if(mrjg==null||mrjg.equals(""))
        return "";
      }catch(Exception e)
      {
        return "";
      }
      return mrjg;
  }
/*�õ�������*/
  public final String getZsl()
  {
    return zzsl;
  }
/*�õ��ܽ��*/
  public final String getZje()
  {
    return zzje;
  }
  /**
   *�õ���˺�ͬ��ص������۽�����
   * */
  public String getTotalJsje()
  {
    String wsje="";
    String dwtxid = m_RowInfo.get("dwtxid");
    if(dwtxid.equals(""))
      return "0";
    try{
      wsje =dataSetProvider.getSequence("select sum(nvl(b.jje,0)-nvl(b.ssje,0))wsje from xs_td a,xs_tdhw b where a.tdid=b.tdid and a.hkrq<=sysdate and a.dwtxid="+dwtxid);
      if(wsje==null)
        wsje="";
      }catch(Exception e){}
      return wsje;
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
      if(dsMasterList.isOpen() && dsMasterList.changesPending())
        dsMasterList.reset();
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
      dsMasterList.closeDataSet();
      dsMasterList = null;
    }
    if(dsDetailTable != null){
      dsDetailTable.close();
      dsDetailTable = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
    if(masterListProducer != null)
    {
      masterListProducer.release();
      masterListProducer = null;
    }
    if(detailProducer != null)
    {
      detailProducer.release();
      detailProducer = null;
    }
    deleteObservers();
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
   *
   * @param htid
   * @return
   */
  public boolean isCanCancel(String htid)
  {
    return !cancelOrder.contains(htid);
  }
  /**�Ƿ���������ᵥ**/
  public boolean iscanGen(String htid)
  {
    if(htid.equals(""))
      return false;
    String count="";
    try
    {
      count = dataSetProvider.getSequence(CAN_GEN_SQL+"'"+htid+"'");
    }
    catch(Exception e){}
    if(count.equals("0"))
      return false;
    else
      return true;
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
        m_RowInfo.clear();//���������
      if(!isAdd)
        m_RowInfo.put(getMaterTable());//��������ʱ,��������ǰ��
      else
      {
        //��������
        Calendar  cd= new GregorianCalendar();
        int year = cd.get(Calendar.YEAR);
        int month = cd.get(Calendar.MONTH);
        cd.clear();
        cd.set(year,month+1,0);
        Date ed = cd.getTime();
        String endday =  new SimpleDateFormat("yyyy-MM-dd").format(ed);

        Date startDate = new Date();
        //Date endDate = new Date(startDate.getYear(), startDate.getMonth()+1, 0);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
        //String endday = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
        String htbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_ht','htbh')htbh FROM dual");
        m_RowInfo.put("htbh", htbh);
        m_RowInfo.put("ksrq", today);
        m_RowInfo.put("jsrq", endday);
        m_RowInfo.put("jhfhrq", today);
        m_RowInfo.put("czrq", today);//�Ƶ�����
        m_RowInfo.put("czy", loginName);//����Ա
        m_RowInfo.put("htrq", today);
        m_RowInfo.put("jhrq", today);
        m_RowInfo.put("czyid", loginid);
        m_RowInfo.put("zt", "0");
      }
      m_RowInfo.put("isdock", "1");
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
      detailRow.put("wzdjid", rowInfo.get("wzdjid_"+i));
      detailRow.put("cpid", rowInfo.get("cpid_"+i));
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));
      detailRow.put("hssl", formatNumber(rowInfo.get("hssl_"+i), qtyFormat));//
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//
      detailRow.put("zk", formatNumber(rowInfo.get("zk_"+i), priceFormat));//
      detailRow.put("dj", formatNumber(rowInfo.get("dj_"+i), priceFormat));//��˰����

      detailRow.put("xsje", formatNumber(rowInfo.get("xsje_"+i), sumFormat));
      detailRow.put("jje", formatNumber(rowInfo.get("jje_"+i), sumFormat));//��˰����
      detailRow.put("jzj", formatNumber(rowInfo.get("jzj_"+i), sumFormat));//��׼��
      detailRow.put("cjtcl", formatNumber(rowInfo.get("cjtcl_"+i), sumFormat));//��������
      detailRow.put("jxts", formatNumber(rowInfo.get("jxts_"+i), qtyFormat));//��Ϣ����
      detailRow.put("hlts", formatNumber(rowInfo.get("hlts_"+i), qtyFormat));//��������
      detailRow.put("hltcl", formatNumber(rowInfo.get("hltcl_"+i), sumFormat));//���������
      detailRow.put("wbje", formatNumber(rowInfo.get("wbje_"+i), sumFormat));
      String xs_jzj = rowInfo.get("xs_jzj_"+i);
      detailRow.put("xs_jzj", formatNumber(rowInfo.get("xs_jzj_"+i), qtyFormat));//����Ļ�׼��
      //
      detailRow.put("bz", rowInfo.get("bz_"+i));//��ע
      detailRow.put("jhrq", rowInfo.get("jhrq_"+i));//
      detailRow.put("xsj", rowInfo.get("xsj_"+i));//
      //�����û��Զ�����ֶ�
      FieldInfo[] fields = detailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
    }
  }
  /*�õ������*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }
  /*�õ������*/
  public final EngineDataSet getMaterList()
  {
    return dsMasterList;
  }
  /*�õ��ӱ�����*/
  public final EngineDataSet getDetailTable(){
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    return dsDetailTable;
  }
  /*�򿪴ӱ�*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    //htid = dsMasterTable.getValue("htid");//����
    //isMasterAddΪ���Ƿ��ؿյĴӱ����ݼ�(��������ʱ,�ӱ�Ҫ��)
    dsDetailTable.setQueryString(DETAIL_SQL + (isMasterAdd ? "-1" : htid));
    if(dsDetailTable.isOpen())
      dsDetailTable.refresh();
    else
      dsDetailTable.open();
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
   * �õ��̶���ѯ���û������ֵ
   * @param col ��ѯ������
   * @return �û������ֵ
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }
  public final boolean getState()
  {
    return isMasterAdd;
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
      isProductInvoke = false;
      if(String.valueOf(PRODUCT_INVOC).equals(action))
        isProductInvoke=true;

      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : "";
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginid);
      masterListProducer.init(request, loginid);
      detailProducer.init(request, loginid);
      //��ʼ����ѯ��Ŀ������
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("htrq$a", startDay);
      row.put("htrq$b", today);
      //row.put("zt", "0");
      isMasterAdd = true;
      zt = new String[]{""};
      String SQL = " AND zt<>4 AND zt<>8 ";
      if(isProductInvoke)
        SQL = " AND zt<>0 AND zt<>9 ";
      String MSQL =  combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      dsMasterList.setQueryString(MSQL);
      dsMasterList.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();

      String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      if(code.equals("1"))
        submitType=true;
      else
        submitType=false;

      sfxdw = dataSetProvider.getSequence("select t.sfxdw from xs_jjjsgs t");

      SLSQL =  combineSQL(MASTER_SUM_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      JESQL = combineSQL(MASTER_JE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SLSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zzsl="0";
      else
        zzsl=dssl.getValue("zsl");

      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      zzsl = zzsl.equals("")?"0":zzsl;
      zzje = zzje.equals("")?"0":zzje;

      zzsl = formatNumber(zzsl, priceFormat);
      zzje = formatNumber(zzje, priceFormat);
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
      //�򿪴ӱ�
      htid = dsMasterList.getValue("htid");
      openDetailTable(false);
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
      isMasterAdd = String.valueOf(ADD).equals(action);//true��������
      if(!isMasterAdd)
      {
        dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));//�鿴���޸�
        masterRow = dsMasterList.getInternalRow();//���ص�ǰ��ָ��(long)
        htid = dsMasterList.getValue("htid");
      }

      dsMasterTable.setQueryString(isMasterAdd?MASTER_STRUT_SQL:combineSQL(MASTER_EDIT_SQL,"?",new String[]{htid}));
      if(!dsMasterTable.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();

      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      data.setMessage(showJavaScript("toDetail();"));
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
      isMasterAdd=false;
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginid);
      detailProducer.init(request, loginid);
      //�õ�request�Ĳ���,ֵ��Ϊnull, ����""����

      String id = null;

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
      htid = dsMasterTable.getValue("htid");
      openDetailTable(false);

      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
    }
  }
  /**
   * ���
   */
  class Over implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      htid = dsMasterTable.getValue("htid");
      String count = dataSetProvider.getSequence(CAN_OVER_SQL+htid);
      if(String.valueOf(SALE_OVER).equals(action))
      {
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("if(confirm('��δ��ȫ����,Ҫǿ�������?')) sumitForm("+FORCE_OVER+","+data.getParameter("rownum")+");"));//ǿ�����
          return;
        }
      }
      //openDetailTable(false);
      dsMasterTable.setValue("zt","8");
      dsMasterTable.saveChanges();
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
      htid = dsMasterList.getValue("htid");

      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_ht a WHERE a.htid='"+htid+"'");
      if(zt!=null&&(zt.equals("8")||zt.equals("9")))
      {
        dsMasterList.readyRefresh();
        return;
      }

      dsMasterTable.setQueryString(combineSQL(MASTER_EDIT_SQL,"?",new String[]{htid}));
      if(!dsMasterList.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();
      dsMasterTable.setValue("zt", "4");
      dsMasterTable.saveChanges();

      dsMasterList.readyRefresh();
      //
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
      String htid = dsMasterList.getValue("htid");
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_ht a WHERE a.htid='"+htid+"'");
      if(zt!=null&&!zt.equals("1"))
      {
        dsMasterList.readyRefresh();
        return;
      }
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterList,dsMasterList.getRow(),"sale_order");
      dsMasterList.readyRefresh();
    }
  }
  /**
   * ��ӵ�����б�Ĳ�����
   */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      htid = dsMasterList.getValue("htid");
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_ht a WHERE a.htid='"+htid+"'");
      if(zt!=null&&!zt.equals("0"))
      {
        dsMasterList.readyRefresh();
        return;
      }
      openDetailTable(false);
      String content = dsMasterList.getValue("htbh");
      String deptid = dsMasterList.getValue("deptid");
      approve.putAproveList(dsMasterList, dsMasterList.getRow(), "sale_order", content,deptid);
    }
  }
  /**
   * ��ʱ����
   */
  class Master_Temp_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
    }
  }
  /**
   * ����������Ĵ�����
   */
  class Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(String.valueOf(PRICE_POST).equals(action))
        isMasterAdd=false;//�ڽ����Żݼ�����ʱ,�޸Ĵӱ����ݺ󱣴����
      putDetailInfo(data.getRequest());
      //String confirmString = getConfirmString();//����Ƿ�����Ҫ��������Ϣ
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
      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      if(isMasterAdd){
        ds.insertRow(false);
        htid = dataSetProvider.getSequence("s_xs_ht");//�õ���������ֵ
        String htbh = rowInfo.get("htbh");
        String count = dataSetProvider.getSequence("select count(*) from xs_ht t where t.htbh='"+htbh+"'");
        if(!count.equals("0"))
          htbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_ht','htbh')htbh FROM dual");
        ds.setValue("htbh", htbh);//
        ds.setValue("htid", htid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zt","0");
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//�Ƶ�����
        ds.setValue("czyid", loginid);
        ds.setValue("czy", loginName);//����Ա
        //ds.setValue("isnet", "0");//�Ƿ�����
      }
      //����ӱ������
      RowMap detailrow = null;
      //double hl = rowInfo.get("hl").length() > 0 ? Double.parseDouble(rowInfo.get("hl")) : 0;
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //����ļ�¼
        detail.setValue("wzdjid", detailrow.get("wzdjid"));
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("htid", htid);

        double xsj = detailrow.get("xsj").length() > 0 ? Double.parseDouble(detailrow.get("xsj")) : 0;//���۵���
        //double hsbl = detailrow.get("hsbl").length() > 0 ? Double.parseDouble(detailrow.get("hsbl")) : 0;//�������
        double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;
        double dj = detailrow.get("dj").length() > 0 ? Double.parseDouble(detailrow.get("dj")) : 0;

        if(dj==0)
        {
          data.setMessage(showJavaScript("alert('���۲���Ϊ0!')"));
          return;
        }
        detail.setValue("xsj", engine.util.Format.formatNumber(detailrow.get("xsj"),"#0.00") );
        detail.setValue("sl", detailrow.get("sl"));
        if(conversion)
        {
          detail.setValue("zk", String.valueOf(dj/xsj*100));
          detail.setValue("xsje", engine.util.Format.formatNumber(String.valueOf(sl * xsj),"#0.00") );
          detail.setValue("dj", engine.util.Format.formatNumber(String.valueOf(dj),"#0.00") );
          detail.setValue("jje", engine.util.Format.formatNumber(String.valueOf(sl * detail.getBigDecimal("dj").doubleValue()),"#0.00") );
          detail.setValue("hssl", detailrow.get("hssl"));
        }
        else
        {
          detail.setValue("zk", detailrow.get("zk"));
          detail.setValue("xsje", engine.util.Format.formatNumber(detailrow.get("xsje"),"#0.00") );
          detail.setValue("dj", engine.util.Format.formatNumber(detailrow.get("dj"),"#0.00") );
          detail.setValue("jje", engine.util.Format.formatNumber(detailrow.get("jje"),"#0.00") );
          detail.setValue("hssl", detailrow.get("hssl"));
        }
        /*
        detail.setValue("jzj", detailrow.get("jzj"));
        detail.setValue("cjtcl", detailrow.get("cjtcl"));
        detail.setValue("jxts", detailrow.get("jxts"));
        detail.setValue("hlts", detailrow.get("hlts"));
        detail.setValue("hltcl", detailrow.get("hltcl"));
        detail.setValue("jhrq", detailrow.get("jhrq"));
        */

        detail.setValue("bz", detailrow.get("bz"));//��ע

        /*
        if(hl==0)
          detail.setValue("wbje","0");
        else
           detail.setValue("wbje", formatNumber(String.valueOf(sl * detail.getBigDecimal("dj").doubleValue()/hl),qtyFormat));
        */

        //�����û��Զ�����ֶ�
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("sl"));
        totalSum = totalSum.add(detail.getBigDecimal("jje"));
        detail.next();
      }
      //������������
      ds.setValue("dh", rowInfo.get("dh"));
      ds.setValue("dz", rowInfo.get("dz"));
      ds.setValue("lxr", rowInfo.get("lxr"));
      ds.setValue("jhfhrq", rowInfo.get("jhfhrq"));

      ds.setValue("personid", rowInfo.get("personid"));//��ԱID
      ds.setValue("deptid", rowInfo.get("deptid"));//
      ds.setValue("dwtxid", rowInfo.get("dwtxid"));//������λID
      ds.setValue("htrq", rowInfo.get("htrq"));//��ͬ����
      // ds.setValue("ksrq", rowInfo.get("ksrq"));//��ͬ��Ч��ʼ
      //ds.setValue("jsrq", rowInfo.get("jsrq"));//��ͬ��Ч����
      //ds.setValue("qddd", rowInfo.get("qddd"));//ǩ���ص�
      ds.setValue("qtxx", rowInfo.get("qtxx"));//������Ϣ
      ds.setValue("khlx", rowInfo.get("khlx"));//�ͻ�����
      //ds.setValue("wbid", rowInfo.get("wbid"));
      ds.setValue("sendmodeid", rowInfo.get("sendmodeid"));
      ds.setValue("ordertypeid", rowInfo.get("ordertypeid"));
      //ds.setValue("hl", rowInfo.get("hl"));
      ds.setValue("zsl", totalNum.toString());//�ܽ��
      ds.setValue("zje", engine.util.Format.formatNumber(totalSum.toString(),"#0.00"));//�ܽ��
      ds.setValue("jsfsid", rowInfo.get("jsfsid"));
      //ds.setValue("yfdj", rowInfo.get("yfdj"));
      ds.setValue("isproduce", rowInfo.get("isproduce"));
      //isproduce
      //�����û��Զ�����ֶ�
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);

      //�ϼ����
      /*
      if(!(SLSQL.equals("")))
      {
      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SLSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zzsl="0";
      else
        zzsl=dssl.getValue("zsl");
      }

      if(!(JESQL.equals("")))
      {
      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");
      }

      zzsl = zzsl.equals("")?"0":zzsl;
      zzje = zzje.equals("")?"0":zzje;

      zzsl = formatNumber(zzsl, priceFormat);
      zzje = formatNumber(zzje, priceFormat);
      */

      if((String.valueOf(CONFIRM).equals(action))||(String.valueOf(POST_CONTINUE).equals(action)))
      {
        isMasterAdd = true;
        detail.empty();
        initRowInfo(true, true, true);
        initRowInfo(false, true, true);//���³�ʼ���ӱ�ĸ�����Ϣ
      }
      else if((String.valueOf(CONFIRM_RETURN).equals(action))||(String.valueOf(POST).equals(action)))
        data.setMessage(showJavaScript("backList();"));
    }
    /**
     * У��ӱ����Ϣ�ӱ��������Ϣ����ȷ��
     * @return null ��ʾû����Ϣ
     */
    private String checkDetailInfo()
    {
      //HashSet wzdijds=new HashSet(d_RowInfos.size());
      HashSet htmp = new HashSet();
      htmp.clear();
      String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()<1)
      {
        return showJavaScript("alert('�ӱ�Ϊ��,���ܱ���!');");
      }
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        temp = detailrow.get("wzdjid");
        if(temp.equals(""))
          return showJavaScript("alert('��Ʒ����Ϊ�գ�');");
        String wzdjid="w"+detailrow.get("wzdjid").trim();
        String dmsxid="d"+detailrow.get("dmsxid").trim();
        /*
        if(!wzdijds.add(wzdjid))
          return showJavaScript("alert('��ѡ��Ʒ�ظ�!');");
        */
        if(!htmp.add(wzdjid+dmsxid))
          return showJavaScript("alert('��ѡ��Ʒ�ظ�!');");
        String hssl = detailrow.get("hssl");
        if(!hssl.equals(""))
        {
          if((temp = checkNumber(hssl, detailProducer.getFieldInfo("hssl").getFieldname())) != null)
            return temp;
        }
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, detailProducer.getFieldInfo("sl").getFieldname())) != null)
          return temp;
        if(Double.parseDouble(sl)==0)
          return showJavaScript("alert('��������Ϊ��!');");
        String dj = detailrow.get("dj");
        if((temp = checkNumber(dj, detailProducer.getFieldInfo("dj").getFieldname())) != null)
          return temp;
        /*
        String jje = detailrow.get("jje");
        if((temp = checkNumber(jje, detailProducer.getFieldInfo("jje").getFieldname())) != null)
          return temp;
        String hlts = detailrow.get("hlts");
        if((temp = checkNumber(hlts, detailProducer.getFieldInfo("hlts").getFieldname())) != null)
          return temp;
        temp = detailrow.get("jhrq");
        if(temp.length() > 0 && !isDate(temp))
          return showJavaScript("alert('�Ƿ��������ڣ�');");
        */
      }
      return null;
    }
    /**
     * У����������Ϣ�ӱ��������Ϣ����ȷ��
     * @return null ��ʾû����Ϣ,У��ͨ��
     */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      String htrq = rowInfo.get("htrq");
      String jhfhrq = rowInfo.get("jhfhrq");
      String temp = rowInfo.get("htrq");

      if(temp.equals(""))
        return showJavaScript("alert('��ͬ���ڲ���Ϊ�գ�');");
      else if(!isDate(temp))
        return showJavaScript("alert('�Ƿ���ͬ���ڣ�');");
      temp = rowInfo.get("jhfhrq");
      if(temp.equals(""))
        return showJavaScript("alert('�ƻ�����ʱ�䲻��Ϊ�գ�');");
      else if(!isDate(temp))
        return showJavaScript("alert('�Ƿ��ƻ�����ʱ�䣡');");

      temp = rowInfo.get("dwtxid");
      if(temp.equals(""))
        return showJavaScript("alert('��ѡ�񹺻���λ��');");
      temp = rowInfo.get("personid");
      if(temp.equals(""))
        return showJavaScript("alert('��ѡ��ҵ��Ա��');");
      temp = rowInfo.get("ordertypeid");
      if(temp.equals(""))
        return showJavaScript("alert('��ѡ���ͬ���ͣ�');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('��ѡ���ţ�');");
      temp = rowInfo.get("khlx");
      if(temp.equals(""))
        return showJavaScript("alert('��ѡ��ͻ����ͣ�');");
      temp = rowInfo.get("qtxx");
      if(temp.getBytes().length > getMaterTable().getColumn("qtxx").getPrecision())
        return showJavaScript("alert('�������������Ϣ������̫���ˣ�');");
      return null;
    }
    /**
     *�õ��ǲ�������������Ϣ
     * */
    private String getConfirmString()
    {
      RowMap rowInfo = getMasterRowinfo();
      String dwtxid = rowInfo.get("dwtxid");
      double dzje =0;
      String confirmString="";
      for(int i=0;i<d_RowInfos.size();i++)
      {
        RowMap detail = (RowMap)d_RowInfos.get(i);
        double sl=Double.parseDouble(detail.get("sl").equals("")?"0":detail.get("sl"));
        double dj=Double.parseDouble(detail.get("dj").equals("")?"0":detail.get("dj"));
        double jzj =Double.parseDouble(detail.get("jzj").equals("")?"0":detail.get("jzj"));
        dzje = dzje+sl*dj;
        if(dj<jzj)
          confirmString = "�ú�ͬ��Ҫ�����Żݼ�����!";
      }
      String syed="";
      try{
        syed= dataSetProvider.getSequence(XYDE_SQL+dwtxid);
        }catch(Exception e){}
        double xyed=(syed==null?0:Double.parseDouble(syed.length()==0?"0":syed));
        if(dzje>xyed)
        {
          if(confirmString.equals(""))
            confirmString = "�ú�ͬ��Ҫ��������������!\\\n�ѳ���������"+(dzje-xyed);
          else
            confirmString = "�ú�ͬ��Ҫ�����Żݼ�����������������!\\\n�ѳ���������"+(dzje-xyed);
        }
        return confirmString;
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
      String htid = ds.getValue("htid");
      String max = dataSetProvider.getSequence("SELECT MAX(htid) FROM xs_ht");
      if(!htid.equals(max))
      {
        data.setMessage(showJavaScript("alert('�ú�ͬ����ɾ��!')"));
        return;
      }
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_ht a WHERE a.htid='"+htid+"'");
      if(zt!=null&&!zt.equals("0"))
      {
        data.setMessage(showJavaScript("alert('�õ��ݲ���ɾ��!')"));
        return;
      }
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);

      //�ϼ����
      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SLSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zzsl="0";
      else
        zzsl=dssl.getValue("zsl");

      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      zzsl = zzsl.equals("")?"0":zzsl;
      zzje = zzje.equals("")?"0":zzje;

      zzsl = formatNumber(zzsl, priceFormat);
      zzje = formatNumber(zzje, priceFormat);

      d_RowInfos.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }
  /**
   *  ��ѯ����
   *  QueryColumn
   *  QueryFixedItem
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
      String SQL = fixedQuery.getWhereQuery();//�õ�WHERE�Ӿ�
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
      if(zt==null&&isProductInvoke)
        SQL = "  AND zt<>0 AND zt<>9  ";

      String MSQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});//��װSQL���
      if(!dsMasterList.getQueryString().equals(MSQL))
      {
        dsMasterList.setQueryString(MSQL);
        dsMasterList.setRowMax(null);//�Ա�dbNavigatorˢ�����ݼ�
      }
      String SLSQL =  combineSQL(MASTER_SUM_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      String JESQL = combineSQL(MASTER_JE_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});

      EngineDataSet dssl = new EngineDataSet();
      setDataSetProperty(dssl,SLSQL);
      dssl.open();
      dssl.first();
      int cn = dssl.getRowCount();
      if(dssl.getRowCount()<1)
        zzsl="0";
      else
        zzsl=dssl.getValue("zsl");

      EngineDataSet dsje = new EngineDataSet();
      setDataSetProperty(dsje,JESQL);
      dsje.open();
      dsje.first();
      if(dsje.getRowCount()<1)
        zzje="0";
      else
        zzje=dsje.getValue("zje");

      zzsl = zzsl.equals("")?"0":zzsl;
      zzje = zzje.equals("")?"0":zzje;

      zzsl = formatNumber(zzsl, priceFormat);
      zzje = formatNumber(zzje, priceFormat);
    }
    /**
     * ��ʼ����ѯ�ĸ�����
     * @param request web�������
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;//�ѳ�ʼ����ѯ����
      EngineDataSet master = dsMasterList;
      if(!master.isOpen())
        master.open();//���������ݼ�
      //��ʼ���̶��Ĳ�ѯ��Ŀ
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("htbh"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("htbh"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("htrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("htrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("jhfhrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("jhfhrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("czrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("czrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),//������λ
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//����ID
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),//
        new QueryColumn(master.getColumn("czy"), null, null, null, null, "like"),//
        new QueryColumn(master.getColumn("sprid"), null, null, null, null, "="),//
        new QueryColumn(master.getColumn("htid"), "vw_xs_hthw", "htid", "cpbm", "cpbm$a", ">="),//
        new QueryColumn(master.getColumn("htid"), "vw_xs_hthw", "htid", "cpbm", "cpbm$b", "<="),//
        new QueryColumn(master.getColumn("htid"), "vw_xs_hthw", "htid", "pm", "pm", "like"),//�ӱ�Ʒ��
        new QueryColumn(master.getColumn("htid"), "vw_xs_hthw", "htid", "gg", "gg", "=")//�ӱ���
            //new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")
      });
      isInitQuery = true;//��ʼ�����
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
   *  �ӱ�����
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      dsDetailTable.insertRow(false);
      dsDetailTable.setValue("hthwid", "-1");
      dsDetailTable.post();
      RowMap detailrow = new RowMap(dsDetailTable);
      d_RowInfos.add(detailrow);
      int rownum = d_RowInfos.size()-1;
      data.setMessage(showJavaScript("document.form1.cpbm_"+rownum+".focus();"));
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
      String wzdjid = newadd.get("wzdjid");
      String htid = newadd.get("htid");
      String xsj = newadd.get("xsj");
      //String dj =  newadd.get("dj");
      String jzj = newadd.get("jzj");
      String hltcl = newadd.get("hltcl");
      int copynumber = Integer.parseInt(tCopyNumber);


      for(int i=0;i<copynumber;i++)
      {
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("hthwid", "-1");
        dsDetailTable.setValue("htid", htid);
        dsDetailTable.setValue("hltcl", hltcl);
        dsDetailTable.setValue("wzdjid", wzdjid);
        dsDetailTable.setValue("xsj", xsj);
        dsDetailTable.setValue("jzj", jzj);
        dsDetailTable.post();
        RowMap detailrow = new RowMap(dsDetailTable);
        d_RowInfos.add(detailrow);
      }
      tCopyNumber="1";
    }
  }
  /***���ʶ�ѡ**/
  class Detail_Multi_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putDetailInfo(data.getRequest());
      String importOrder = m_RowInfo.get("wzdjid");
      if (importOrder.equals("undefined-1"))
        return;
      String dwtxid = m_RowInfo.get("dwtxid");
      String storeid = "";//m_RowInfo.get("storeid");
      if(importOrder.length() == 0)
        return;
      String alert="";
      RowMap detailrow = null;
      if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
      String htid = dsMasterTable.getValue("htid");

      String GRADE_SQL =  combineSQL(CUST_GRADE_SQL,"?",new String[]{fgsid,dwtxid});
      String xydj = dataSetProvider.getSequence(GRADE_SQL);
      String sql = combineSQL(CUST_SALE_GOODS_SQL,"?",new String[]{fgsid,dwtxid,xydj,fgsid});
      String SQL = "select p.cpid,p.wzdjid,p.mrzk,p.price from ("+sql+")p where p.wzdjid IN("+importOrder+")";
      EngineDataSet tmp = new EngineDataSet();
      setDataSetProperty(tmp,SQL);
      tmp.open();

      String pricesql = "select p.price from ("+sql+")p where p.wzdjid =";
      String zksql = "select p.mrzk from ("+sql+")p where p.wzdjid=";

      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "wzdjid");
      String[] wzdjids = parseString(importOrder,",");//��������ͬ����ID����
      tmp.first();
      for(int i=0; i < tmp.getRowCount(); i++)
      {
        String wzdjid = tmp.getValue("wzdjid");//wzdjids[i];
        String xsj = tmp.getValue("price");
        String mrzk = tmp.getValue("mrzk");
        String cpid = tmp.getValue("cpid");
        locateGoodsRow.setValue(0, wzdjid);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          String cnn = dataSetProvider.getSequence("SELECT COUNT(*) FROM xs_promotion t,xs_wzdj b WHERE t.cpid=b.cpid and sysdate>t.startdate AND sysdate<=t.enddate AND t.dwtxid='"+dwtxid+"' AND b.wzdjid='"+wzdjid+"' ");
          if(cnn!=null&&!cnn.equals("0"))
          {
            alert="'��ѡ��Ʒ�ڴ�����'";
            continue;
          }
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("hthwid", "-1");
          dsDetailTable.setValue("htid", htid);
          dsDetailTable.setValue("wzdjid", wzdjid);
          if(xsj==null||xsj.equals(""))
            continue;
          if(mrzk==null)
            mrzk="";
          dsDetailTable.setValue("xsj", xsj);
          dsDetailTable.setValue("zk", mrzk);
          //dsDetailTable.setValue("cpid", cpid);
          dsDetailTable.post();
          //����һ�����û����Ӧ����
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
        tmp.next();
      }
      if(alert.length()>0)
        data.setMessage(showJavaScript("alert('�����в�Ʒû����!')"));
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
      data.setMessage(showJavaScript("document.form1.sl_"+rownum+".focus();"));
    }
  }
  /**
   * û��
   * ���ͻ�����ΪA��ʱ,��׼��Ϊ����Ļ�׼��,
   * ���ͻ�����Ϊc��ʱ,���ۺ�ͬ�Ļ�׼��Ϊ����Ļ�׼�۵�98%
   *
   *  �ӱ����
   */
  class Khlx_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      engine.project.LookUp salePriceBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_SALE_PRICE);
      String oldkhlx=m_RowInfo.get("khlx");
      putDetailInfo(data.getRequest());//�����������ϸ��Ϣ
      String khlx=m_RowInfo.get("khlx");
      if(oldkhlx.equals(""))
        return;
      if(oldkhlx.equals(khlx))
        return;
      if(khlx.equals(""))
      {
        data.setMessage(showJavaScript("alert('�ͻ����Ͳ���Ϊ��!')"));
        m_RowInfo.put("khlx",oldkhlx);
        return;
      }
      if(!isMasterAdd)
      {
        dsMasterTable.goToInternalRow(masterRow);
      }
      if(khlx.equals("A"))
      {
        RowMap jsfs=getJjjsgs();
        for(int i=0;i<d_RowInfos.size();i++)
        {
          RowMap detailRow = (RowMap)d_RowInfos.get(i);
          String wzdjid=detailRow.get("wzdjid");
          RowMap wzRow=salePriceBean.getLookupRow(wzdjid);
          double dj=Double.parseDouble(detailRow.get("dj"));
          double jzj=Double.parseDouble(detailRow.get("jzj"))*100/98;
          double xstcl= Double.parseDouble(wzRow.get("xstcl"));
          double hkts=Double.parseDouble(wzRow.get("hkts"));
          detailRow.put("jzj",formatNumber(String.valueOf(jzj), sumFormat));
          if(dj>jzj)
          {
            double xsjzj=Double.parseDouble(jsfs.get("xsjzj"));
            double hltszj=Double.parseDouble(jsfs.get("hltszj"));
            double tclzj=Double.parseDouble(jsfs.get("tclzj"));

            double bs=(dj-jzj)/xsjzj-1;
            detailRow.put("hlts",formatNumber(String.valueOf(hkts+bs*hltszj), sumFormat));//
            detailRow.put("cjtcl",formatNumber(String.valueOf(xstcl+bs*tclzj), sumFormat));//
            }else if(dj<jzj)
            {
              double xsjjs=Double.parseDouble(jsfs.get("xsjjs"));
              double hltsjs=Double.parseDouble(jsfs.get("hltsjs"));
              double tcljs=Double.parseDouble(jsfs.get("tcljs"));

              double bs=(jzj-dj)/xsjjs-1;
              detailRow.put("hlts",formatNumber(String.valueOf(-(hkts+bs*hltsjs)), sumFormat));//
              detailRow.put("cjtcl",formatNumber(wzRow.get("hkts"), sumFormat));//
            }
            else
            {
              detailRow.put("hlts",formatNumber(wzRow.get("hkts"), sumFormat));//
              detailRow.put("cjtcl","0");
            }
        }
      }
      else if(khlx.equals("C"))
      {
        RowMap jsfs=getJjjsgs();
        for(int i=0;i<d_RowInfos.size();i++)
        {
          RowMap detailRow = (RowMap)d_RowInfos.get(i);
          String wzdjid=detailRow.get("wzdjid");
          RowMap wzRow=salePriceBean.getLookupRow(wzdjid);
          double dj=Double.parseDouble(detailRow.get("dj"));
          double jzj=Double.parseDouble(detailRow.get("jzj"))*0.98;
          double xstcl= Double.parseDouble(wzRow.get("xstcl"));
          double hkts=Double.parseDouble(wzRow.get("hkts"));
          detailRow.put("jzj",formatNumber(String.valueOf(jzj), sumFormat));
          if(dj>jzj)
          {
            double xsjzj=Double.parseDouble(jsfs.get("xsjzj"));
            double hltszj=Double.parseDouble(jsfs.get("hltszj"));
            double tclzj=Double.parseDouble(jsfs.get("tclzj"));

            double bs=(dj-jzj)/xsjzj;//formatNumber(String.valueOf(-(xstcl+bs*tcljs)), sumFormat)
            detailRow.put("hlts",formatNumber(String.valueOf(hkts+bs*hltszj), sumFormat));//
            detailRow.put("cjtcl",formatNumber(String.valueOf(xstcl+bs*tclzj), sumFormat));//
            }else if(dj<jzj)
            {
              double xsjjs=Double.parseDouble(jsfs.get("xsjjs"));
              double hltsjs=Double.parseDouble(jsfs.get("hltsjs"));
              double tcljs=Double.parseDouble(jsfs.get("tcljs"));

              double bs=(jzj-dj)/xsjjs;
              detailRow.put("hlts",formatNumber(String.valueOf(-(hkts+bs*hltsjs)), sumFormat));//
              detailRow.put("cjtcl",formatNumber(String.valueOf(-(xstcl+bs*tcljs)), sumFormat));//
            }
            else
            {
              detailRow.put("hlts",formatNumber(wzRow.get("hkts"), sumFormat));//
              detailRow.put("cjtcl","0");
            }
        }
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
   *  �ӱ����Ӳ���
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
        m_RowInfo.put("tdrq",req.getParameter("tdrq"));
        m_RowInfo.put("tdbh",req.getParameter("tdbh"));
        m_RowInfo.put("personid",corRow.get("personid"));
        m_RowInfo.put("deptid",corRow.get("deptid"));
        m_RowInfo.put("hkts",creditRow.get("hkts"));
        dsDetailTable.empty();
        d_RowInfos.clear();
      }
    }
  }
  /**
   * �õ����ڲ��Ҳ�Ʒ���۵�bean
   * @param req WEB������private B_SaleGoodsSelect saleGoodsBean=null;
   * @return �������ڲ��Ҳ�Ʒ���۵�bean
   */
  public LookUp getSalePriceBean(HttpServletRequest req)
  {
    if(salePriceBean == null)
      salePriceBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_SALE_PRICE);
    return salePriceBean;
  }
  /*
  public B_SaleGoodsSelect getSaleGoodsBean(HttpServletRequest req)
  {
    if(saleGoodsBean == null)
      saleGoodsBean = B_SaleGoodsSelect.getInstance(req);
    return saleGoodsBean;
  }
  */
  public LookUp getForeignBean(HttpServletRequest req)
  {
    engine.project.LookUp wbBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_FOREIGN_CURRENCY);
    return wbBean;
  }
}