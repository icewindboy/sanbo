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
 *����������ͬ
 * */
public final class B_ImportPromotionToBackLading extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "9001";

  private static final String MASTER_STRUT_SQL = "SELECT  * FROM xs_td WHERE 1<>1 ";
  private static final String MASTER_SQL    = "SELECT * from xs_td WHERE djlx=2 ? ORDER BY tdbh DESC";

  private static final String DETAIL_STRUT_SQL = "SELECT * FROM xs_tdhw WHERE 1<>1";
  private static final String DETAIL_SQL    = "select * From VW_SALE_PROMOTION_DETAIL  where 1=1 AND zt =8 and tdid= ";
  private static final String HTHW_SQL    = "select * From VW_SALE_PROMOTION_DETAIL where 1=1 AND zt =8  AND ? AND fgsid=? ? ";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//����
  private EngineDataSet dsDetailTable  = new EngineDataSet();//�ӱ�
  private EngineDataSet dsSearchTable  = new EngineDataSet();//

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "xs_td.2");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "xs_tdhw");

  private boolean isMasterAdd = true;    //�Ƿ������״̬

  private long    masterRow = -1;         //���������޸Ĳ������м�¼ָ��
  private RowMap  m_RowInfo    = new RowMap(); //��������л��޸��е�����
  private ArrayList d_RowInfos = null; //�ӱ���м�¼������

  private LookUp salePriceBean = null; //���۵��۵�bean������, ������ȡ���۵���
  //private B_SaleGoodsSelect saleGoodsBean=null;
  private boolean isInitQuery = false; //�Ƿ��Ѿ���ʼ����ѯ����
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public  String loginId = "";   //��¼Ա����ID
  public  String loginCode = ""; //��½Ա���ı���
  public  String loginName = ""; //��¼Ա��������
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //�ֹ�˾ID

  private String tdid = null;
  private User user = null;
  public String dwtxid ="";
  public String djlx = "";
  public String storeid = "";
  public String personid ="";
  public String srcFrm = "";
  public String multiIdInput = "";
  public String khlx = "";
  public String jsfsid = "";
  public String sendmodeid = "";
  public String yfdj = "";
  /**
   * ���ۺ�ͬ�б��ʵ��
   * @param request jsp����
   * @param isApproveStat �Ƿ�������״̬
   * @return �������ۺ�ͬ�б��ʵ��
   */
  public static B_ImportPromotionToBackLading getInstance(HttpServletRequest request)
  {
    B_ImportPromotionToBackLading promotionToBackLadingBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "promotionToBackLadingBean";
      promotionToBackLadingBean = (B_ImportPromotionToBackLading)session.getAttribute(beanName);
      if(promotionToBackLadingBean == null)
      {
        //����LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        promotionToBackLadingBean = new B_ImportPromotionToBackLading();
        promotionToBackLadingBean.qtyFormat = loginBean.getQtyFormat();
        promotionToBackLadingBean.priceFormat = loginBean.getPriceFormat();
        promotionToBackLadingBean.sumFormat = loginBean.getSumFormat();

        promotionToBackLadingBean.fgsid = loginBean.getFirstDeptID();
        promotionToBackLadingBean.loginId = loginBean.getUserID();
        promotionToBackLadingBean.loginName = loginBean.getUserName();

        promotionToBackLadingBean.user = loginBean.getUser();
        session.setAttribute(beanName, promotionToBackLadingBean);
      }
    }
    return promotionToBackLadingBean;
  }

  /**
   * ���캯��
   */
  private B_ImportPromotionToBackLading()
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

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"tdbh"}, new String[]{"SELECT pck_base.billNextCode('xs_td','tdbh') FROM dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"tdbh"}, new boolean[]{true}, null, 0));


    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    storeid="-1";
    dwtxid = "-1";


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
    if(detailProducer != null)
    {
      detailProducer.release();
      detailProducer = null;
    }
    deleteObservers();
  }
  //�õ�һ����Ϣ
  public final RowMap getLookupRow(String tdhwid)
  {
    RowMap row = new RowMap();
    if(tdhwid == null || tdhwid.equals(""))
      return row;//����
    EngineRow locateRow = new EngineRow(dsDetailTable, "tdhwid");//����ָ��DataSet�����1�е�EngineRow������û�����ݣ�
    if(locateRow == null)
      locateRow = new EngineRow(getDetailTable(), "tdhwid");
    locateRow.setValue(0, tdhwid);
    if(getDetailTable().locate(locateRow, Locate.FIRST))
      row.put(getDetailTable());
    return row;
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
    tdid = dsMasterTable.getValue("tdid");//����
    //isMasterAddΪ���Ƿ��ؿյĴӱ����ݼ�(��������ʱ,�ӱ�Ҫ��)
    dsDetailTable.setQueryString(DETAIL_SQL + (isMasterAdd ? "-1" : ("'"+tdid+"' and (storeid is null or storeid='"+storeid+"')")));
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

      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      HttpServletRequest request = data.getRequest();
      storeid = request.getParameter("storeid");//��ѡ
      sendmodeid= request.getParameter("sendmodeid");//

      djlx = request.getParameter("djlx");//

      khlx = request.getParameter("khlx");//�Ǳ�ѡ
      dwtxid = request.getParameter("dwtxid");//�Ǳ�ѡ
      personid = request.getParameter("personid");//�Ǳ�ѡ
      jsfsid = request.getParameter("jsfsid");//�Ǳ�ѡ
      yfdj = request.getParameter("yfdj");

      if(storeid.equals(""))
        return;
      String SQL = "  AND (storeid='"+storeid+"' or storeid is null) ";

      if(khlx!=null&&!khlx.equals(""))
        SQL = SQL+" AND khlx='"+khlx+"' ";
      if(dwtxid!=null&&!dwtxid.equals(""))
        SQL = SQL+" AND dwtxid='"+dwtxid+"' ";
      if(personid!=null&&!personid.equals(""))
        SQL = SQL+" AND personid='"+personid+"' ";
      if(jsfsid!=null&&!jsfsid.equals(""))
        SQL = SQL+" AND jsfsid='"+jsfsid+"' ";
      if(sendmodeid!=null&&!sendmodeid.equals(""))
        SQL = SQL+" AND sendmodeid='"+sendmodeid+"' ";
      //if(!yfdj.equals(""))
      //  SQL = SQL+" AND yfdj='"+yfdj+"' ";

      srcFrm = request.getParameter("srcFrm");
      multiIdInput = request.getParameter("srcVar");

      //if(storeid.equals("")||djlx.equals("")||dwtxid.equals("")||personid.equals("")||khlx.equals(""))
      //  return;
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //��ʼ����ѯ��Ŀ������
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("kdrq$a", startDay);
      row.put("kdrq$b", today);
      row.put("zt", "0");
      isMasterAdd = true;
      EngineDataSet tmp = new EngineDataSet();
      String dept = user.getHandleDeptValue();
      SQL = combineSQL(HTHW_SQL, "?", new String[]{" "+dept, fgsid, SQL});
      setDataSetProperty(tmp,SQL);
      tmp.open();
      StringBuffer buf = null;
      ArrayList contain = new ArrayList();
      tmp.first();
      for(int i=0;i<tmp.getRowCount();i++)
      {
        String tdid = tmp.getValue("tdid");
        if(!contain.contains(tdid))
        {
          contain.add(tdid);
          if(buf == null)
            buf = new StringBuffer("AND tdid IN(").append(tdid);
          else
            buf.append(",").append(tdid);
        }
        tmp.next();
      }
      if(buf == null)
        buf =new StringBuffer();
      else
      buf.append(")");
      SQL = buf.toString();
      if(SQL.equals(""))
        SQL=" and 1<>1 ";

      //String SQL = " AND jsfsid='"+jsfsid+"' AND personid='"+personid+"'  AND khlx ='"+khlx+"'  AND dwtxid ='"+dwtxid+"' AND (storeid='"+storeid+"' or storeid is null) ";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
    }
  }

  /**
   * ��ʾ�ӱ���б���Ϣ
   */
  class ShowDetail implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterTable.getInternalRow();
      //�򿪴ӱ�
      openDetailTable(false);
      initRowInfo(false,false,true);
    }
  }

  /**
   * ������ӻ��޸Ĳ����Ĵ�����
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {

      isMasterAdd = String.valueOf(ADD).equals(action);//true��������
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));//�鿴���޸�
        masterRow = dsMasterTable.getInternalRow();//���ص�ǰ��ָ��(long)
      }
      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);

     // data.setMessage(showJavaScript("toDetail();"));
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
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();//�õ�WHERE�Ӿ�
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = SQL+" AND (storeid='"+storeid+"' or storeid is null) ";
      if(personid!=null&&!personid.equals(""))
        SQL = SQL+" AND personid='"+personid+"'";
      if(khlx!=null&&!khlx.equals(""))
        SQL = SQL+" AND khlx='"+khlx+"'";
      if(dwtxid!=null&&!dwtxid.equals(""))
        SQL = SQL+" AND dwtxid='"+dwtxid+"'";
      if(jsfsid!=null&&!jsfsid.equals(""))
        SQL = SQL+" AND jsfsid='"+jsfsid+"'";
      EngineDataSet tmp = new EngineDataSet();
      String dept = user.getHandleDeptValue();
      SQL = combineSQL(HTHW_SQL, "?", new String[]{" "+dept, fgsid, SQL});
      setDataSetProperty(tmp,SQL);
      tmp.open();
      StringBuffer buf = null;
      ArrayList contain = new ArrayList();
      int j=0;
      tmp.first();
      for(int i=0;i<tmp.getRowCount();i++)
      {
        String tdid = tmp.getValue("tdid");
        if(!contain.contains(tdid))
        {
          contain.add(tdid);
          if(buf == null)
            buf = new StringBuffer("AND tdid IN(").append(tdid);
          else
            buf.append(",").append(tdid);
          j= j+1;
        }
        if(j>=500)
          break;
        tmp.next();
      }
      if(buf == null)
        buf =new StringBuffer();
      else
      buf.append(")");
      SQL = buf.toString();
      if(SQL.equals(""))
        SQL=" and 1<>1 ";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{SQL});
      if(!dsMasterTable.getQueryString().equals(SQL))
      {
        dsMasterTable.setQueryString(SQL);
        dsMasterTable.setRowMax(null);//�Ա�dbNavigatorˢ�����ݼ�
      }
      openDetailTable(true);
    }
    /**
     * ��ʼ����ѯ�ĸ�����
     * @param request web�������
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet master = dsMasterTable;
      //EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //��ʼ���̶��Ĳ�ѯ��Ŀ
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("tdbh"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("kdrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("kdrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("tdrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("tdrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("dwtxid"), "dwtx", "dwtxid", "dwdm", "dwdm", "like"),
        new QueryColumn(master.getColumn("dwtxid"), "dwtx", "dwtxid", "dwmc", "dwmc", "like")
      });
      isInitQuery = true;
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
}