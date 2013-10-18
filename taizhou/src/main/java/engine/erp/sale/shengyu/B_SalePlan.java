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
 * ���۹���-���ۼƻ�
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @version 1.0
 */
public final class B_SalePlan extends BaseAction implements Operate
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
  public  static final String WBZL_ONCHANGE      = "190011";        //������ı�
  public  static final String REPORT             ="645355666";      //����׷��
  public  static final String CONFIRM            = "700001";        //ȷ��
  public  static final String CONFIRM_RETURN     = "700002";        //ȷ�Ϻ󷵻�
  public  static final String DETAIL_COPY        ="1013";           //���Ƶ�ǰѡ����
  public  static final String DEL_NULL           = "1014";          //ɾ������Ϊ�յ���
  public  static final String FORCE_OVER         = "1015";          //ǿ�����
  public  static final String PRODUCT_INVOC      = "1016";          //�������ú�ͬ

  private static final String MASTER_STRUT_SQL   = "SELECT * FROM xs_jh WHERE 1<>1 ORDER BY jhbh DESC ";
  private static final String MASTER_SQL         = "SELECT * FROM xs_jh WHERE 1=1 AND ?  AND fgsid=? ? ORDER BY jhbh DESC";

  private static final String DETAIL_STRUT_SQL   = "SELECT * FROM xs_jhmx WHERE 1<>1";
  private static final String DETAIL_SQL         = "SELECT * FROM xs_jhmx WHERE xsjhid=";
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM xs_jh WHERE xsjhid='?'";//��������ʱ����ȡһ����¼
  private EngineDataSet dsMasterTable  = new EngineDataSet();  //����
  private EngineDataSet dsDetailTable  = new EngineDataSet();  //�ӱ�
  private EngineDataSet dsCancel = new EngineDataSet();        //��
  private ArrayList cancelOrder = new ArrayList();             //������
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "xs_jh");
  //public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "xs_jhmx");

  private boolean isMasterAdd = true;    //�Ƿ������״̬
  public  boolean isApprove = false;     //�Ƿ�������״̬
  private long    masterRow = -1;         //���������޸Ĳ������м�¼ָ��
  private RowMap  m_RowInfo    = new RowMap(); //��������л��޸��е�����
  private ArrayList d_RowInfos = new ArrayList(); //�ӱ���м�¼������
  private LookUp salePriceBean = null; //���۵��۵�bean������, ������ȡ���۵���
  private boolean isInitQuery = false; //�Ƿ��Ѿ���ʼ����ѯ����
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public  String loginid = "";   //��¼Ա����ID
  public  String loginCode = ""; //��½Ա���ı���
  public  String loginName = ""; //��¼Ա��������

  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //�ֹ�˾ID
  private String xsjhid = null;
  private User user = null;
  public boolean isReport = false;
  public boolean submitType;//�����ж�true=���ƶ��˿��ύ,false=��Ȩ���˿��ύ

  public String []zt;

  public String tCopyNumber = "1";
  //public String sfxdw ="";//�����С��λ

  public String dwdm="";
  public String dwmc="";
  public RowMap SumRow = new RowMap();
  /**
   * ���ۺ�ͬ�б��ʵ��
   * @param request jsp����
   * @param isApproveStat �Ƿ�������״̬
   * @return �������ۺ�ͬ�б��ʵ��
   */
  public static B_SalePlan getInstance(HttpServletRequest request)
  {
    B_SalePlan b_SalePlanBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_SalePlanBean";
      b_SalePlanBean = (B_SalePlan)session.getAttribute(beanName);
      if(b_SalePlanBean == null)
      {
        //����LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        b_SalePlanBean = new B_SalePlan();
        b_SalePlanBean.qtyFormat = loginBean.getQtyFormat();
        b_SalePlanBean.priceFormat = loginBean.getPriceFormat();
        b_SalePlanBean.sumFormat = loginBean.getSumFormat();

        b_SalePlanBean.fgsid = loginBean.getFirstDeptID();
        b_SalePlanBean.loginid = loginBean.getUserID();
        b_SalePlanBean.loginName = loginBean.getUserName();

        //���ø�ʽ�����ֶ�
            /*
            b_SalePlanBean.dsDetailTable.setColumnFormat("sl", b_SalePlanBean.qtyFormat);
            b_SalePlanBean.dsDetailTable.setColumnFormat("hssl", b_SalePlanBean.qtyFormat);
            b_SalePlanBean.dsDetailTable.setColumnFormat("xsj", b_SalePlanBean.priceFormat);
            b_SalePlanBean.dsDetailTable.setColumnFormat("dj", b_SalePlanBean.priceFormat);
            b_SalePlanBean.dsDetailTable.setColumnFormat("xsje", b_SalePlanBean.sumFormat);
            b_SalePlanBean.dsDetailTable.setColumnFormat("jje", b_SalePlanBean.sumFormat);
            b_SalePlanBean.dsMasterTable.setColumnFormat("zsl", b_SalePlanBean.qtyFormat);
            b_SalePlanBean.dsMasterTable.setColumnFormat("zje", b_SalePlanBean.sumFormat);
            b_SalePlanBean.dsMasterTable.setColumnFormat("wbje", b_SalePlanBean.sumFormat);
            */

        b_SalePlanBean.user = loginBean.getUser();
        session.setAttribute(beanName, b_SalePlanBean);
      }
    }
    return b_SalePlanBean;
  }

  /**
   * ���캯��
   */
  private B_SalePlan()
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
    setDataSetProperty(dsCancel, null);

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"xsjhid"}, new String[]{"s_xs_jh"}));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"xs_jhmxid"}, new String[]{"s_xs_jhmx"}));


    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(PRODUCT_INVOC), new Init());

    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());

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

    addObactioner(String.valueOf(REPORT), new Approve());
    addObactioner(String.valueOf(CONFIRM), masterPost);
    addObactioner(String.valueOf(CONFIRM_RETURN), masterPost);

    addObactioner(String.valueOf(FORCE_OVER), new Over());

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
        int year = cd.get(Calendar.YEAR);    //��ǰ���
        int month = cd.get(Calendar.MONTH);  //��ǰ�·�
        cd.clear();
        cd.set(year,month+1,0);
        Date ed = cd.getTime();
        String endday =  new SimpleDateFormat("yyyy-MM-dd").format(ed);
        Date startDate = new Date();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
        String jhbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_jh','jhbh')jhbh FROM dual");

        m_RowInfo.put("jhbh", jhbh);
        m_RowInfo.put("jhnd", String.valueOf(year));

        m_RowInfo.put("czrq", today);//�Ƶ�����
        m_RowInfo.put("czy", loginName);//����Ա

        m_RowInfo.put("fgsid", fgsid);
        m_RowInfo.put("czyid", loginid);
        m_RowInfo.put("zt", "0");
      }
    }
    else
    {
      EngineDataSet dsDetail = dsDetailTable;
      if(d_RowInfos == null)
        d_RowInfos = new ArrayList(dsDetail.getRowCount());
      else if(isInit)
        d_RowInfos.clear();
      String zt = m_RowInfo.get("zt");
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

      detailRow.put("deptid", rowInfo.get("deptid_"+i));

      detailRow.put("jhxs1", rowInfo.get("jhxs1_"+i));
      detailRow.put("jhxs2", rowInfo.get("jhxs2_"+i));
      detailRow.put("jhxs3", rowInfo.get("jhxs3_"+i));
      detailRow.put("jhxs4", rowInfo.get("jhxs4_"+i));
      detailRow.put("jhxs5", rowInfo.get("jhxs5_"+i));
      detailRow.put("jhxs6", rowInfo.get("jhxs6_"+i));
      detailRow.put("jhxs7", rowInfo.get("jhxs7_"+i));
      detailRow.put("jhxs8", rowInfo.get("jhxs8_"+i));
      detailRow.put("jhxs9", rowInfo.get("jhxs9_"+i));
      detailRow.put("jhxs10", rowInfo.get("jhxs10_"+i));
      detailRow.put("jhxs11", rowInfo.get("jhxs11_"+i));
      detailRow.put("jhxs12", rowInfo.get("jhxs12_"+i));

      detailRow.put("khxs1", rowInfo.get("khxs1_"+i));
      detailRow.put("khxs2", rowInfo.get("khxs2_"+i));
      detailRow.put("khxs3", rowInfo.get("khxs3_"+i));
      detailRow.put("khxs4", rowInfo.get("khxs4_"+i));
      detailRow.put("khxs5", rowInfo.get("khxs5_"+i));
      detailRow.put("khxs6", rowInfo.get("khxs6_"+i));
      detailRow.put("khxs7", rowInfo.get("khxs7_"+i));
      detailRow.put("khxs8", rowInfo.get("khxs8_"+i));
      detailRow.put("khxs9", rowInfo.get("khxs9_"+i));
      detailRow.put("khxs10", rowInfo.get("khxs10_"+i));
      detailRow.put("khxs11", rowInfo.get("khxs11_"+i));
      detailRow.put("khxs12", rowInfo.get("khxs12_"+i));

    }
  }
      /*�õ������*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
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
    //isMasterAddΪ���Ƿ��ؿյĴӱ����ݼ�(��������ʱ,�ӱ�Ҫ��)
    dsDetailTable.setQueryString(DETAIL_SQL + (isMasterAdd ? "-1" : xsjhid));
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

    dsMasterTable.goToInternalRow(masterRow);
    return dsMasterTable.getRow();
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
      SumRow = new RowMap();


      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginid);

      //��ʼ����ѯ��Ŀ������
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      //row.put("htrq$a", startDay);
      //row.put("htrq$b", today);
      //row.put("zt", "0");
      isMasterAdd = true;
      zt = new String[]{""};

      String SQL = " AND zt<>4 AND zt<>8 ";
      String MSQL =  combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      dsMasterTable.setQueryString(MSQL);
      dsMasterTable.setRowMax(null);

      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();

      String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      if(code.equals("1"))
        submitType=true;
      else
        submitType=false;

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
      HttpServletRequest request = data.getRequest();
      isApprove = false;
      SumRow = new RowMap();
      isMasterAdd = String.valueOf(ADD).equals(action);//true��������
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));//�鿴���޸�
        masterRow = dsMasterTable.getInternalRow();//���ص�ǰ��ָ��(long)
        xsjhid = dsMasterTable.getValue("xsjhid");
      }
      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      RowMap rowInfo = getMasterRowinfo();
      String jhnd = rowInfo.get("jhnd");
      if(rowInfo.get("zt").equals("1"))
      {
        RowMap detailrow =null;
        BigDecimal zxsjh1 = new BigDecimal(0);
        BigDecimal zxsjh2 = new BigDecimal(0);
        BigDecimal zxsjh3 = new BigDecimal(0);
        BigDecimal zxsjh4 = new BigDecimal(0);
        BigDecimal zxsjh5 = new BigDecimal(0);
        BigDecimal zxsjh6 = new BigDecimal(0);
        BigDecimal zxsjh7 = new BigDecimal(0);
        BigDecimal zxsjh8 = new BigDecimal(0);
        BigDecimal zxsjh9 = new BigDecimal(0);
        BigDecimal zxsjh10 = new BigDecimal(0);
        BigDecimal zxsjh11 = new BigDecimal(0);
        BigDecimal zxsjh12 = new BigDecimal(0);

        BigDecimal zkhxs1 = new BigDecimal(0);
        BigDecimal zkhxs2 = new BigDecimal(0);
        BigDecimal zkhxs3 = new BigDecimal(0);
        BigDecimal zkhxs4 = new BigDecimal(0);
        BigDecimal zkhxs5 = new BigDecimal(0);
        BigDecimal zkhxs6 = new BigDecimal(0);
        BigDecimal zkhxs7 = new BigDecimal(0);
        BigDecimal zkhxs8 = new BigDecimal(0);
        BigDecimal zkhxs9 = new BigDecimal(0);
        BigDecimal zkhxs10 = new BigDecimal(0);
        BigDecimal zkhxs11 = new BigDecimal(0);
        BigDecimal zkhxs12 = new BigDecimal(0);

        BigDecimal zshijixs1 = new BigDecimal(0);
        BigDecimal zshijixs2 = new BigDecimal(0);
        BigDecimal zshijixs3 = new BigDecimal(0);
        BigDecimal zshijixs4 = new BigDecimal(0);
        BigDecimal zshijixs5 = new BigDecimal(0);
        BigDecimal zshijixs6 = new BigDecimal(0);
        BigDecimal zshijixs7 = new BigDecimal(0);
        BigDecimal zshijixs8 = new BigDecimal(0);
        BigDecimal zshijixs9 = new BigDecimal(0);
        BigDecimal zshijixs10 = new BigDecimal(0);
        BigDecimal zshijixs11 = new BigDecimal(0);
        BigDecimal zshijixs12 = new BigDecimal(0);

        double zwcbl1 = 0;
        double zwcbl2 = 0;
        double zwcbl3 = 0;
        double zwcbl4 = 0;
        double zwcbl5 = 0;
        double zwcbl6 = 0;
        double zwcbl7 = 0;
        double zwcbl8 = 0;
        double zwcbl9 = 0;
        double zwcbl10 = 0;
        double zwcbl11 = 0;
        double zwcbl12 = 0;

        BigDecimal zjhxs = new BigDecimal(0);

        for(int i=0;i<d_RowInfos.size();i++)
        {
          engine.project.LookUp deptBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_DEPT);//���ò���
          detailrow= (RowMap)d_RowInfos.get(i);
          String deptid = detailrow.get("deptid");
          RowMap deptrow = deptBean.getLookupRow(deptid);
          String dm = deptrow.get("dm");

          String shijixs1 = dataSetProvider.getSequence("select t.zsl from VW_XS_TDHW_STATISTIC t where t.dm like '"+dm+"%' and t.nf='"+jhnd+"' and t.yf='01'");
          if(shijixs1!=null&&!shijixs1.equals(""))
          {
            detailrow.put("shijixs1",shijixs1);
            zshijixs1 = zshijixs1.add(new BigDecimal(shijixs1));
            String khxs1 = detailrow.get("khxs1");
            double wcbL1 = Double.parseDouble(shijixs1)/Double.parseDouble(khxs1)*100;
            detailrow.put("wcbL1",engine.util.Format.formatNumber(String.valueOf(wcbL1),"#.00"));
          }
          String shijixs2 = dataSetProvider.getSequence("select t.zsl from VW_XS_TDHW_STATISTIC t where  t.dm like '"+dm+"%' and t.nf='"+jhnd+"' and t.yf='02'");
          if(shijixs2!=null&&!shijixs2.equals(""))
          {
            detailrow.put("shijixs2",shijixs2);
            zshijixs2 = zshijixs2.add(new BigDecimal(shijixs2));
            String khxs2 = detailrow.get("khxs2");
            double wcbL2 = Double.parseDouble(shijixs2)/Double.parseDouble(khxs2)*100;
            detailrow.put("wcbL2",engine.util.Format.formatNumber(String.valueOf(wcbL2),"#.00"));
          }
          String shijixs3 = dataSetProvider.getSequence("select t.zsl from VW_XS_TDHW_STATISTIC t where  t.dm like '"+dm+"%' and t.nf='"+jhnd+"' and t.yf='03'");
          if(shijixs3!=null&&!shijixs3.equals(""))
          {
            detailrow.put("shijixs3",shijixs3);
            zshijixs3 = zshijixs3.add(new BigDecimal(shijixs3));
            String khxs3 = detailrow.get("khxs3");
            double wcbL3 = Double.parseDouble(shijixs3)/Double.parseDouble(khxs3)*100;
            detailrow.put("wcbL3",engine.util.Format.formatNumber(String.valueOf(wcbL3),"#.00"));
          }
          String shijixs4 = dataSetProvider.getSequence("select t.zsl from VW_XS_TDHW_STATISTIC t where  t.dm like '"+dm+"%' and t.nf='"+jhnd+"' and t.yf='04'");
          if(shijixs4!=null&&!shijixs4.equals(""))
          {
            detailrow.put("shijixs4",shijixs4);
            zshijixs4 = zshijixs4.add(new BigDecimal(shijixs4));
            String khxs4 = detailrow.get("khxs4");
            double wcbL4 = Double.parseDouble(shijixs4)/Double.parseDouble(khxs4)*100;
            detailrow.put("wcbL4",engine.util.Format.formatNumber(String.valueOf(wcbL4),"#.00"));
          }
          String shijixs5 = dataSetProvider.getSequence("select t.zsl from VW_XS_TDHW_STATISTIC t where  t.dm like '"+dm+"%' and t.nf='"+jhnd+"' and t.yf='05'");
          if(shijixs5!=null&&!shijixs5.equals(""))
          {
            detailrow.put("shijixs5",shijixs5);
            zshijixs5 = zshijixs5.add(new BigDecimal(shijixs5));
            String khxs5 = detailrow.get("khxs5");
            double wcbL5 = Double.parseDouble(shijixs5)/Double.parseDouble(khxs5)*100;
            detailrow.put("wcbL5",engine.util.Format.formatNumber(String.valueOf(wcbL5),"#.00"));
          }
          String shijixs6 = dataSetProvider.getSequence("select t.zsl from VW_XS_TDHW_STATISTIC t where  t.dm like '"+dm+"%' and t.nf='"+jhnd+"' and t.yf='06'");
          if(shijixs6!=null&&!shijixs6.equals(""))
          {
            detailrow.put("shijixs6",shijixs6);
            zshijixs6 = zshijixs6.add(new BigDecimal(shijixs6));
            String khxs6 = detailrow.get("khxs6");
            double wcbL6 = Double.parseDouble(shijixs6)/Double.parseDouble(khxs6)*100;
            detailrow.put("wcbL6",engine.util.Format.formatNumber(String.valueOf(wcbL6),"#.00"));
          }
          String shijixs7 = dataSetProvider.getSequence("select t.zsl from VW_XS_TDHW_STATISTIC t where  t.dm like '"+dm+"%' and t.nf='"+jhnd+"' and t.yf='07'");
          if(shijixs7!=null&&!shijixs7.equals(""))
          {
            detailrow.put("shijixs7",shijixs7);
            zshijixs7 = zshijixs7.add(new BigDecimal(shijixs7));
            String khxs7 = detailrow.get("khxs7");
            double wcbL7 = Double.parseDouble(shijixs7)/Double.parseDouble(khxs7)*100;
            detailrow.put("wcbL7",engine.util.Format.formatNumber(String.valueOf(wcbL7),"#.00"));
          }
          String shijixs8 = dataSetProvider.getSequence("select t.zsl from VW_XS_TDHW_STATISTIC t where  t.dm like '"+dm+"%' and t.nf='"+jhnd+"' and t.yf='08'");
          if(shijixs8!=null&&!shijixs8.equals(""))
          {
            detailrow.put("shijixs8",shijixs8);
            zshijixs8 = zshijixs8.add(new BigDecimal(shijixs8));
            String khxs8 = detailrow.get("khxs8");
            double wcbL8 = Double.parseDouble(shijixs8)/Double.parseDouble(khxs8)*100;
            detailrow.put("wcbL8",engine.util.Format.formatNumber(String.valueOf(wcbL8),"#.00"));
          }
          String shijixs9 = dataSetProvider.getSequence("select t.zsl from VW_XS_TDHW_STATISTIC t where  t.dm like '"+dm+"%' and t.nf='"+jhnd+"' and t.yf='09'");
          if(shijixs9!=null&&!shijixs9.equals(""))
          {
            detailrow.put("shijixs9",shijixs9);
            zshijixs9 = zshijixs9.add(new BigDecimal(shijixs9));
            String khxs9 = detailrow.get("khxs9");
            double wcbL9 = Double.parseDouble(shijixs9)/Double.parseDouble(khxs9)*100;
            detailrow.put("wcbL9",engine.util.Format.formatNumber(String.valueOf(wcbL9),"#.00"));
          }
          String shijixs10 = dataSetProvider.getSequence("select t.zsl from VW_XS_TDHW_STATISTIC t where  t.dm like '"+dm+"%' and t.nf='"+jhnd+"' and t.yf='10'");
          if(shijixs10!=null&&!shijixs10.equals(""))
          {
            detailrow.put("shijixs10",shijixs10);
            zshijixs10 = zshijixs10.add(new BigDecimal(shijixs10));
            String khxs10 = detailrow.get("khxs10");
            double wcbL10 = Double.parseDouble(shijixs10)/Double.parseDouble(khxs10)*100;
            detailrow.put("wcbL10",engine.util.Format.formatNumber(String.valueOf(wcbL10),"#.00"));
          }
          String shijixs11 = dataSetProvider.getSequence("select t.zsl from VW_XS_TDHW_STATISTIC t where  t.dm like '"+dm+"%' and t.nf='"+jhnd+"' and t.yf='11'");
          if(shijixs11!=null&&!shijixs11.equals(""))
          {
            detailrow.put("shijixs11",shijixs11);
            zshijixs11 = zshijixs11.add(new BigDecimal(shijixs11));
            String khxs11 = detailrow.get("khxs11");
            double wcbL11 = Double.parseDouble(shijixs11)/Double.parseDouble(khxs11)*100;
            detailrow.put("wcbL11",engine.util.Format.formatNumber(String.valueOf(wcbL11),"#.00"));
          }
          String shijixs12 = dataSetProvider.getSequence("select t.zsl from VW_XS_TDHW_STATISTIC t where  t.dm like '"+dm+"%' and t.nf='"+jhnd+"' and t.yf='12'");
          if(shijixs12!=null&&!shijixs12.equals(""))
          {
            detailrow.put("shijixs12",shijixs12);
            zshijixs12 = zshijixs12.add(new BigDecimal(shijixs12));
            String khxs12 = detailrow.get("khxs12");
            double wcbL12 = Double.parseDouble(shijixs12)/Double.parseDouble(khxs12)*100;
            detailrow.put("wcbL12",engine.util.Format.formatNumber(String.valueOf(wcbL12),"#.00"));
          }
          zxsjh1 = zxsjh1.add(new BigDecimal(detailrow.get("jhxs1").equals("")?"0":detailrow.get("jhxs1")));
          zxsjh2 = zxsjh2.add(new BigDecimal(detailrow.get("jhxs2").equals("")?"0":detailrow.get("jhxs2")));
          zxsjh3 = zxsjh3.add(new BigDecimal(detailrow.get("jhxs3").equals("")?"0":detailrow.get("jhxs3")));
          zxsjh4 = zxsjh4.add(new BigDecimal(detailrow.get("jhxs4").equals("")?"0":detailrow.get("jhxs4")));
          zxsjh5 = zxsjh5.add(new BigDecimal(detailrow.get("jhxs5").equals("")?"0":detailrow.get("jhxs5")));
          zxsjh6 = zxsjh6.add(new BigDecimal(detailrow.get("jhxs6").equals("")?"0":detailrow.get("jhxs6")));
          zxsjh7 = zxsjh7.add(new BigDecimal(detailrow.get("jhxs7").equals("")?"0":detailrow.get("jhxs7")));
          zxsjh8 = zxsjh8.add(new BigDecimal(detailrow.get("jhxs8").equals("")?"0":detailrow.get("jhxs8")));
          zxsjh9 = zxsjh9.add(new BigDecimal(detailrow.get("jhxs9").equals("")?"0":detailrow.get("jhxs9")));
          zxsjh10 = zxsjh10.add(new BigDecimal(detailrow.get("jhxs10").equals("")?"0":detailrow.get("jhxs10")));
          zxsjh11 = zxsjh11.add(new BigDecimal(detailrow.get("jhxs11").equals("")?"0":detailrow.get("jhxs11")));
          zxsjh12 = zxsjh12.add(new BigDecimal(detailrow.get("jhxs12").equals("")?"0":detailrow.get("jhxs12")));

          zkhxs1 = zkhxs1.add(new BigDecimal(detailrow.get("khxs1").equals("")?"0":detailrow.get("khxs1")));
          zkhxs2 = zkhxs2.add(new BigDecimal(detailrow.get("khxs2").equals("")?"0":detailrow.get("khxs2")));
          zkhxs3 = zkhxs3.add(new BigDecimal(detailrow.get("khxs3").equals("")?"0":detailrow.get("khxs3")));
          zkhxs4 = zkhxs4.add(new BigDecimal(detailrow.get("khxs4").equals("")?"0":detailrow.get("khxs4")));
          zkhxs5 = zkhxs5.add(new BigDecimal(detailrow.get("khxs5").equals("")?"0":detailrow.get("khxs5")));
          zkhxs6 = zkhxs6.add(new BigDecimal(detailrow.get("khxs6").equals("")?"0":detailrow.get("khxs6")));
          zkhxs7 = zkhxs7.add(new BigDecimal(detailrow.get("khxs7").equals("")?"0":detailrow.get("khxs7")));
          zkhxs8 = zkhxs8.add(new BigDecimal(detailrow.get("khxs8").equals("")?"0":detailrow.get("khxs8")));
          zkhxs9 = zkhxs9.add(new BigDecimal(detailrow.get("khxs9").equals("")?"0":detailrow.get("khxs9")));
          zkhxs10 = zkhxs10.add(new BigDecimal(detailrow.get("khxs10").equals("")?"0":detailrow.get("khxs10")));
          zkhxs11 = zkhxs11.add(new BigDecimal(detailrow.get("khxs11").equals("")?"0":detailrow.get("khxs11")));
          zkhxs12 = zkhxs12.add(new BigDecimal(detailrow.get("khxs12").equals("")?"0":detailrow.get("khxs12")));
        }
        String zxsj = zxsjh1.toString();
        SumRow.put("zxsjh1",zxsj);
        SumRow.put("zxsjh2",zxsjh2.toString());
        SumRow.put("zxsjh3",zxsjh3.toString());
        SumRow.put("zxsjh4",zxsjh4.toString());
        SumRow.put("zxsjh5",zxsjh5.toString());
        SumRow.put("zxsjh6",zxsjh6.toString());
        SumRow.put("zxsjh7",zxsjh7.toString());
        SumRow.put("zxsjh8",zxsjh8.toString());
        SumRow.put("zxsjh9",zxsjh9.toString());
        SumRow.put("zxsjh10",zxsjh10.toString());
        SumRow.put("zxsjh11",zxsjh11.toString());
        SumRow.put("zxsjh12",zxsjh12.toString());

        SumRow.put("zkhxs1",zkhxs1.toString());
        SumRow.put("zkhxs2",zkhxs2.toString());
        SumRow.put("zkhxs3",zkhxs3.toString());
        SumRow.put("zkhxs4",zkhxs4.toString());
        SumRow.put("zkhxs5",zkhxs5.toString());
        SumRow.put("zkhxs6",zkhxs6.toString());
        SumRow.put("zkhxs7",zkhxs7.toString());
        SumRow.put("zkhxs8",zkhxs8.toString());
        SumRow.put("zkhxs9",zkhxs9.toString());
        SumRow.put("zkhxs10",zkhxs10.toString());
        SumRow.put("zkhxs11",zkhxs11.toString());
        SumRow.put("zkhxs12",zkhxs12.toString());

        SumRow.put("zshijixs1",zshijixs1.toString());
        SumRow.put("zshijixs2",zshijixs2.toString());
        SumRow.put("zshijixs3",zshijixs3.toString());
        SumRow.put("zshijixs4",zshijixs4.toString());
        SumRow.put("zshijixs5",zshijixs5.toString());
        SumRow.put("zshijixs6",zshijixs6.toString());
        SumRow.put("zshijixs7",zshijixs7.toString());
        SumRow.put("zshijixs8",zshijixs8.toString());
        SumRow.put("zshijixs9",zshijixs9.toString());
        SumRow.put("zshijixs10",zshijixs10.toString());
        SumRow.put("zshijixs11",zshijixs11.toString());
        SumRow.put("zshijixs12",zshijixs12.toString());

        SumRow.put("zwcbL1",zshijixs1.divide(zkhxs1,2,2).multiply(new BigDecimal(100)).toString());
        SumRow.put("zwcbL2",zshijixs2.divide(zkhxs2,2,2).multiply(new BigDecimal(100)).toString());
        SumRow.put("zwcbL3",zshijixs3.divide(zkhxs3,2,2).multiply(new BigDecimal(100)).toString());
        SumRow.put("zwcbL4",zshijixs4.divide(zkhxs4,2,2).multiply(new BigDecimal(100)).toString());
        SumRow.put("zwcbL5",zshijixs5.divide(zkhxs5,2,2).multiply(new BigDecimal(100)).toString());
        SumRow.put("zwcbL6",zshijixs6.divide(zkhxs6,2,2).multiply(new BigDecimal(100)).toString());
        SumRow.put("zwcbL7",zshijixs7.divide(zkhxs7,2,2).multiply(new BigDecimal(100)).toString());
        SumRow.put("zwcbL8",zshijixs8.divide(zkhxs8,2,2).multiply(new BigDecimal(100)).toString());
        SumRow.put("zwcbL9",zshijixs9.divide(zkhxs9,2,2).multiply(new BigDecimal(100)).toString());
        SumRow.put("zwcbL10",zshijixs10.divide(zkhxs10,2,2).multiply(new BigDecimal(100)).toString());
        SumRow.put("zwcbL11",zshijixs11.divide(zkhxs11,2,2).multiply(new BigDecimal(100)).toString());
        SumRow.put("zwcbL12",zshijixs12.divide(zkhxs12,2,2).multiply(new BigDecimal(100)).toString());
      }
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
      xsjhid = dsMasterTable.getValue("xsjhid");
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
      xsjhid = dsMasterTable.getValue("xsjhid");
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
      dsMasterTable.goToRow(row);
      dsMasterTable.setValue("zt", "4");
      dsMasterTable.saveChanges();
    }
  }
  /**
   * ȡ������
   */
  class Cancer_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(),"sale_plan");
    }
  }
  /**
   * ��ӵ�����б�Ĳ�����
   */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      xsjhid = dsMasterTable.getValue("xsjhid");
      openDetailTable(false);
      String content = dsMasterTable.getValue("jhbh");
      String deptid = dsMasterTable.getValue("deptid");
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "sale_plan", content,deptid);
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
      String count = null;
      if(isMasterAdd){
        ds.insertRow(false);
        xsjhid = dataSetProvider.getSequence("s_xs_jh");//�õ���������ֵ
        String jhbh = rowInfo.get("jhbh");
        String jhnd = rowInfo.get("jhnd");
        count = dataSetProvider.getSequence("select count(*) from xs_jh t where t.jhbh='"+jhbh+"'");
        if(count!=null&&!count.equals("0"))
          jhbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('xs_jh','jhbh')jhbh FROM dual");
        count = dataSetProvider.getSequence("select count(*) from xs_jh t where t.jhnd='"+jhnd+"'");
        if(count!=null&&!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('��������ƶ��ƻ�!');"));
          return;
        }
        ds.setValue("jhbh", jhbh);//
        ds.setValue("jhnd", jhnd);//
        ds.setValue("xsjhid", xsjhid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zt","0");
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//�Ƶ�����
        ds.setValue("czyid", loginid);
        ds.setValue("czy", loginName);//����Ա
      }
      //����ӱ������
      RowMap detailrow = null;

      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        detail.setValue("xsjhid", xsjhid);
        detail.setValue("deptid", detailrow.get("deptid"));

        detail.setValue("jhxs1", detailrow.get("jhxs1"));
        detail.setValue("jhxs2", detailrow.get("jhxs2"));
        detail.setValue("jhxs3", detailrow.get("jhxs3"));
        detail.setValue("jhxs4", detailrow.get("jhxs4"));
        detail.setValue("jhxs5", detailrow.get("jhxs5"));
        detail.setValue("jhxs6", detailrow.get("jhxs6"));
        detail.setValue("jhxs7", detailrow.get("jhxs7"));
        detail.setValue("jhxs8", detailrow.get("jhxs8"));
        detail.setValue("jhxs9", detailrow.get("jhxs9"));
        detail.setValue("jhxs10", detailrow.get("jhxs10"));
        detail.setValue("jhxs11", detailrow.get("jhxs11"));
        detail.setValue("jhxs12", detailrow.get("jhxs12"));

        detail.setValue("khxs1", detailrow.get("khxs1"));
        detail.setValue("khxs2", detailrow.get("khxs2"));
        detail.setValue("khxs3", detailrow.get("khxs3"));
        detail.setValue("khxs4", detailrow.get("khxs4"));
        detail.setValue("khxs5", detailrow.get("khxs5"));
        detail.setValue("khxs6", detailrow.get("khxs6"));
        detail.setValue("khxs7", detailrow.get("khxs7"));
        detail.setValue("khxs8", detailrow.get("khxs8"));
        detail.setValue("khxs9", detailrow.get("khxs9"));
        detail.setValue("khxs10", detailrow.get("khxs10"));
        detail.setValue("khxs11", detailrow.get("khxs11"));
        detail.setValue("khxs12", detailrow.get("khxs12"));

        detail.post();
        detail.next();
      }
      //������������
      ds.setValue("deptid", rowInfo.get("deptid"));//

      //�����û��Զ�����ֶ�
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
    }
    /**
     * У��ӱ����Ϣ�ӱ��������Ϣ����ȷ��
     * @return null ��ʾû����Ϣ
     */
    private String checkDetailInfo()
    {
      ArrayList tmp = new ArrayList();
      String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()<1)
      {
        return showJavaScript("alert('�ӱ�Ϊ��,���ܱ���!');");
      }
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String deptid = detailrow.get("deptid");
        if(!tmp.contains(deptid))
          tmp.add(deptid);
        else
          return  showJavaScript("alert('�����ظ�!');");
        String jhxs1 = detailrow.get("jhxs1");
        if((temp = checkNumber(jhxs1, "1�¼ƻ�����")) != null)
          return temp;
        String jhxs2 = detailrow.get("jhxs2");
        if((temp = checkNumber(jhxs2, "2�¼ƻ�����")) != null)
          return temp;
        String jhxs3 = detailrow.get("jhxs3");
        if((temp = checkNumber(jhxs3, "3�¼ƻ�����")) != null)
          return temp;
        String jhxs4 = detailrow.get("jhxs4");
        if((temp = checkNumber(jhxs4, "4�¼ƻ�����")) != null)
          return temp;
        String jhxs5 = detailrow.get("jhxs5");
        if((temp = checkNumber(jhxs5, "5�¼ƻ�����")) != null)
          return temp;
        String jhxs6 = detailrow.get("jhxs6");
        if((temp = checkNumber(jhxs6, "6�¼ƻ�����")) != null)
          return temp;
        String jhxs7 = detailrow.get("jhxs7");
        if((temp = checkNumber(jhxs7, "7�¼ƻ�����")) != null)
          return temp;
        String jhxs8 = detailrow.get("jhxs8");
        if((temp = checkNumber(jhxs8, "8�¼ƻ�����")) != null)
          return temp;
        String jhxs9 = detailrow.get("jhxs9");
        if((temp = checkNumber(jhxs9, "9�¼ƻ�����")) != null)
          return temp;
        String jhxs10 = detailrow.get("jhxs10");
        if((temp = checkNumber(jhxs10, "10�¼ƻ�����")) != null)
          return temp;
        String jhxs11 = detailrow.get("jhxs11");
        if((temp = checkNumber(jhxs11, "11�¼ƻ�����")) != null)
          return temp;
        String jhxs12 = detailrow.get("jhxs12");
        if((temp = checkNumber(jhxs12, "12�¼ƻ�����")) != null)
          return temp;


        String khxs1 = detailrow.get("khxs1");
        if((temp = checkNumber(khxs1, "1�¿�������")) != null)
          return temp;
        String khxs2 = detailrow.get("khxs2");
        if((temp = checkNumber(khxs2, "2�¿�������")) != null)
          return temp;
        String khxs3 = detailrow.get("khxs3");
        if((temp = checkNumber(khxs3, "3�¿�������")) != null)
          return temp;
        String khxs = detailrow.get("khxs4");
        if((temp = checkNumber(jhxs1, "4�¿�������")) != null)
          return temp;
        String khxs5 = detailrow.get("khxs5");
        if((temp = checkNumber(khxs5, "5�¿�������")) != null)
          return temp;
        String khxs6 = detailrow.get("khxs6");
        if((temp = checkNumber(khxs6, "6�¿�������")) != null)
          return temp;
        String khxs7 = detailrow.get("khxs7");
        if((temp = checkNumber(khxs7, "7�¿�������")) != null)
          return temp;
        String khxs8 = detailrow.get("khxs8");
        if((temp = checkNumber(khxs8, "8�¿�������")) != null)
          return temp;
        String khxs69 = detailrow.get("khxs9");
        if((temp = checkNumber(khxs69, "9�¿�������")) != null)
          return temp;
        String khxs10 = detailrow.get("khxs10");
        if((temp = checkNumber(khxs10, "10�¿�������")) != null)
          return temp;
        String khxs11 = detailrow.get("khxs11");
        if((temp = checkNumber(khxs11, "11�¿�������")) != null)
          return temp;
        String khxs12 = detailrow.get("khxs12");
        if((temp = checkNumber(khxs12, "12�¿�������")) != null)
          return temp;
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
      String temp =  rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('��ѡ���������ţ�');");
      return null;
    }
    /**
     *�õ��ǲ�������������Ϣ
     * */
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
      xsjhid = ds.getValue("xsjhid");
      String zt = dataSetProvider.getSequence("SELECT a.zt FROM xs_jh a WHERE a.xsjhid='"+xsjhid+"'");
      if(zt!=null&&!zt.equals("0"))
      {
        data.setMessage(showJavaScript("alert('�õ��ݲ���ɾ��!')"));
        return;
      }
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
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

      String MSQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});//��װSQL���
      if(!dsMasterTable.getQueryString().equals(MSQL))
      {
        dsMasterTable.setQueryString(MSQL);
        dsMasterTable.setRowMax(null);//�Ա�dbNavigatorˢ�����ݼ�
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
      EngineDataSet master = dsMasterTable;
      if(!master.isOpen())
        master.open();//���������ݼ�
      //��ʼ���̶��Ĳ�ѯ��Ŀ
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("jhbh"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("czy"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("czrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("czrq"), null, null, null, "b", "<=")
      });
      isInitQuery = true;//��ʼ�����
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
      String deptid = req.getParameter("mdeptid");
      ArrayList dts = new ArrayList();
      if(dts.contains(deptid))
      {
        data.setMessage(showJavaScript("alert('�����ظ�!')"));
        return;
      }
      else
        dts.add(deptid);
      dsDetailTable.insertRow(false);
      dsDetailTable.setValue("xs_jhmxid", "-1");
      dsDetailTable.setValue("deptid", deptid);
      dsDetailTable.post();
      RowMap detailrow = new RowMap(dsDetailTable);
      d_RowInfos.add(detailrow);
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