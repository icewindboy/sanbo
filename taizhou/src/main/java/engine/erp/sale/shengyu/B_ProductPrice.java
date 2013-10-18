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
 * <p>Title: ���۹���-��Ʒ���۶���</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @version 1.0
 */
public final class B_ProductPrice extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1009";
  /**
   *Ĭ��״̬�ǲ����޸�
   * */
  private boolean canModify = false;
  /**
   *��Ӧ�޸İ�ť
   * */
  public static final String MODIFY = "1005";
  /**
   * ��ȡ�������۵�����Ϣ��SQL���
   */
  private static final String PRODUCT_PRICE_SQL = "SELECT * FROM xs_wzdj WHERE fgsid=? ? ";//
  private static final String PRODUCT_LIST_SQL = "SELECT * FROM vw_sale_product_price WHERE (fgsid=? OR fgsid IS NULL) ? ORDER BY cpbm";
  /**
   *�������ݼ�
   * �����������۵�����Ϣ�����ݼ�
   */
  private EngineDataSet dsProductPriceList = new EngineDataSet();
  private EngineDataSet dsProductPrice = new EngineDataSet();
  private ArrayList d_RowInfos = null; //���м�¼������
  public  String retuUrl = null;//������ذ�ť��URL
  private boolean isInitQuery = false;
  public  String loginName = ""; //��¼Ա��������
  public  String personid = ""; //��¼Ա��personid
  private String fgsID = null;   //�ֹ�˾ID


  private QueryFixedItem fixedQuery = new QueryFixedItem();//����̶���ѯ��
  /**
   * �õ��������۵�����Ϣ��ʵ��
   * @param request jsp����
   * @return �����������۵�����Ϣ��ʵ��
   */
  public static B_ProductPrice getInstance(HttpServletRequest request)
  {
    B_ProductPrice b_ProductPriceBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_ProductPriceBean";//����-ֵ��Ӧ
      b_ProductPriceBean = (B_ProductPrice)session.getAttribute(beanName);
      if(b_ProductPriceBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsID = loginBean.getFirstDeptID();
        b_ProductPriceBean = new B_ProductPrice(fgsID);//���ù��캯��
        b_ProductPriceBean.loginName = loginBean.getUserName();
        b_ProductPriceBean.personid=loginBean.getUserID();
        session.setAttribute(beanName,b_ProductPriceBean);
      }
    }
    return b_ProductPriceBean;
  }
  /**
   * ���캯��(ʵ������:�ֹ�˾IDΪ��ʼ������)
   */
  private B_ProductPrice(String fgsid)
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
   * ��ʼ������
   * @throws Exception �쳣��Ϣ
   */
  protected void jbInit() throws Exception
  {
    //��ʼ�����ݼ���SQL���.
    setDataSetProperty(dsProductPriceList, null);
    setDataSetProperty(dsProductPrice, null);
    dsProductPrice.setSequence(new SequenceDescriptor(new String[]{"wzdjid"}, new String[]{"s_xs_wzdj"}));
    //dsProductPriceList���ݼ�������װ�����,���ܶ���ִ�в���(װ���귢��ͨ��)
    dsProductPriceList.addLoadListener(new com.borland.dx.dataset.LoadListener() {
      public void dataLoaded(LoadEvent e)
      {
        initRowInfo(false, true);
      }
    });
    //��Ӳ����Ĵ�������
    addObactioner(String.valueOf(INIT), new B_ProductPrice_Init());//��ʼ��
    addObactioner(String.valueOf(POST), new B_ProductPrice_Post());//����
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());//��ѯ
    addObactioner(String.valueOf(MODIFY), new Modify());
  }
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
      if(dsProductPrice.changesPending())
        dsProductPrice.reset();
      if(dsProductPriceList.changesPending())
          dsProductPriceList.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvmҪ���ĺ���,��������������
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsProductPriceList != null){
      dsProductPriceList.close();
      dsProductPriceList = null;
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
    if(!dsProductPriceList.isOpen())
    {
      dsProductPriceList.open();
    }
    int rownum = dsProductPriceList.getRowCount();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("ccj", rowInfo.get("ccj_"+i));//
      detailRow.put("msj", rowInfo.get("msj_"+i));//
      detailRow.put("lsj", rowInfo.get("lsj_"+i));//
      detailRow.put("qtjg1", rowInfo.get("qtjg1_"+i));//
      detailRow.put("qtjg2", rowInfo.get("qtjg2_"+i));//
      detailRow.put("wzdjid", rowInfo.get("wzdjid_"+i));//���ʵ���ID
      detailRow.put("qtjg3", rowInfo.get("qtjg3_"+i));//
      detailRow.put("mrjg", rowInfo.get("mrjg_"+i));
      detailRow.put("mrzk", rowInfo.get("mrzk_"+i));


      d_RowInfos.set(i,detailRow);
    }
  }
  /*�õ��ӱ���е���Ϣ*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
  /*�õ�״̬*/
  public final boolean getState()
  {
    return canModify;
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
    StringBuffer buf = null;
    if(d_RowInfos == null)
      d_RowInfos = new ArrayList(dsProductPriceList.getRowCount());
    else if(isInit)
      d_RowInfos.clear();

    dsProductPriceList.first();
    for(int i=0; i<dsProductPriceList.getRowCount(); i++)
    {
      if(buf == null)
        buf = new StringBuffer("AND cpid IN(").append(dsProductPriceList.getValue("cpid"));
      else
        buf.append(",").append(dsProductPriceList.getValue("cpid"));
      RowMap row = new RowMap(dsProductPriceList);
      d_RowInfos.add(row);
      dsProductPriceList.next();
    }

    if(buf == null)
      buf =new StringBuffer();
    else
      buf.append(")");

    String SQL = combineSQL(PRODUCT_PRICE_SQL, "?", new String[]{fgsID, buf.toString()});
    dsProductPrice.setQueryString(SQL);
    if(dsProductPrice.isOpen())
      dsProductPrice.refresh();
    else
      dsProductPrice.openDataSet();
  }
  /*�õ������*/
  public final EngineDataSet getDetailTable()
  {
    return dsProductPriceList;
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
  class B_ProductPrice_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      canModify= false;
      dsProductPriceList.setQueryString(combineSQL(PRODUCT_LIST_SQL,"?",new String[]{fgsID,""}));
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      fixedQuery.getSearchRow().clear();
      //data.setMessage(showJavaScript("showFixedQuery()"));
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
      String SQL = queryBasic.getWhereQuery();

      String ccj = request.getParameter("ccj");
      String msj = request.getParameter("msj");
      String lsj = request.getParameter("lsj");
      //String qtjg1 = request.getParameter("qtjg1");
      //String qtjg2 = request.getParameter("qtjg2");
      //String qtjg3 = request.getParameter("qtjg3");


      if(SQL.length() > 0)
        SQL = " AND " + SQL;
      if(ccj!=null)
        SQL = SQL+" AND ccj is null ";
      if(msj!=null)
        SQL = SQL+" AND msj is null ";
      if(lsj!=null)
        SQL = SQL+" AND lsj is null ";
      /*
      if(qtjg1!=null)
        SQL = SQL+" AND qtjg1 is null ";
      if(qtjg2!=null)
        SQL = SQL+" AND qtjg2 is null ";
      if(qtjg3!=null)
        SQL = SQL+" AND qtjg3 is null ";
      */


      SQL = combineSQL(PRODUCT_LIST_SQL, "?", new String[]{fgsID, SQL}) ;
      if(!dsProductPriceList.getQueryString().equals(SQL))
      {
        dsProductPriceList.setQueryString(SQL);
        dsProductPriceList.setRowMax(null);
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
      EngineDataSet master = dsProductPriceList;
      //��ʼ���̶��Ĳ�ѯ��Ŀ
      //������λdwtxId;�������xyed;�����ȼ�xydj;�ؿ�����hkts;
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("cpid"), "VW_SALE_WZDJ", "cpid", "prod", "prod", "like"),//�ӱ�Ʒ��
        new QueryColumn(master.getColumn("ccj"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("ccj"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("msj"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("msj"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("lsj"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("lsj"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("mrzk"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("mrzk"), null, null, null, "b", "<=")

      });
      isInitQuery = true;
    }
  }
  /**
   *����޸İ�ťʱ�����Ĳ���
   * MODIFY
   * */
  class Modify implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      canModify=true;
    }
  }
  /**
   * ��������Ĵ�����
   */
  class B_ProductPrice_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String czrq = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      RowMap detailrow = null;
      putDetailInfo(data.getRequest());
      String temp = null;
      if((temp = checkDetailInfo()) !=null)
      {
        data.setMessage(temp);
        return;
      }

      EngineRow row = new EngineRow(dsProductPrice, "cpid");
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String cpid = detailrow.get("cpid");

        String ccj = detailrow.get("ccj");
        String msj  = detailrow.get("msj");
        String lsj = detailrow.get("lsj");
        String qtjg1 = detailrow.get("qtjg1");
        String qtjg2 = detailrow.get("qtjg2");
        String qtjg3 = detailrow.get("qtjg3");
        String mrjg = detailrow.get("mrjg");
        String mrzk = detailrow.get("mrzk");

        dsProductPriceList.setValue("cpid", cpid);

        dsProductPriceList.setValue("ccj", ccj);
        dsProductPriceList.setValue("msj", msj);
        dsProductPriceList.setValue("lsj", lsj);
        dsProductPriceList.setValue("qtjg1", qtjg1);
        dsProductPriceList.setValue("qtjg2", qtjg2);
        dsProductPriceList.setValue("qtjg3", qtjg3);
        dsProductPriceList.setValue("mrjg", mrjg);
        dsProductPriceList.setValue("mrzk", mrzk);
        dsProductPriceList.setValue("fgsID",fgsID);
        dsProductPriceList.setValue("czrq",czrq);
        dsProductPriceList.setValue("czy",loginName);
        dsProductPriceList.setValue("czyID",personid);
        dsProductPriceList.post();
        if(ccj.length() > 0 || msj.length()>0 || lsj.length() > 0 || qtjg1.length() > 0 || qtjg2.length() >0|| qtjg3.length() >0)
        {
          row.setValue(0, cpid);
          if(!dsProductPrice.locate(row, Locate.FIRST))
          {
            dsProductPrice.insertRow(false);
            dsProductPrice.setValue("wzdjid","-1");
          }

          dsProductPrice.setValue("cpid", cpid);

          dsProductPrice.setValue("ccj", ccj);
          dsProductPrice.setValue("msj", msj);
          dsProductPrice.setValue("lsj", lsj);
          dsProductPrice.setValue("qtjg1", qtjg1);
          dsProductPrice.setValue("qtjg2", qtjg2);
          dsProductPrice.setValue("qtjg3", qtjg3);
          dsProductPrice.setValue("mrjg", mrjg);
          dsProductPrice.setValue("mrzk", mrzk);

          dsProductPrice.setValue("fgsID",fgsID);


          dsProductPrice.setValue("czrq",czrq);
          dsProductPrice.setValue("czy",loginName);
          dsProductPrice.setValue("czyID",personid);
          dsProductPrice.post();
        }
      }
      dsProductPrice.saveChanges();
      dsProductPriceList.resetPendingStatus(true);
      canModify=false;
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
        String ccj = detailrow.get("ccj");
        if(ccj.length()!=0)
        {
        if((temp = checkNumber(ccj, "������")) != null)
          return temp;
        }
        String msj = detailrow.get("msj");
        if(msj.length()!=0)
        {
        if((temp = checkNumber(msj, "���м�")) != null)
          return temp;
        }
        String lsj = detailrow.get("lsj");
        if(lsj.length()!=0)
        {
        if((temp = checkNumber(lsj, "���ۼ�")) != null)
          return temp;
        }
        String qtjg1 = detailrow.get("qtjg1");
        if(qtjg1.length()!=0)
        {
        if((temp = checkNumber(qtjg1, "�����۸�1")) != null)
          return temp;
        }
        String qtjg2 = detailrow.get("qtjg2");
        if(qtjg2.length()!=0)
        {
        if((temp = checkNumber(qtjg2, "�����۸�2")) != null)
          return temp;
        }
        String qtjg3 = detailrow.get("qtjg3");
        if(qtjg3.length()!=0)
        {
        if((temp = checkNumber(qtjg3, "�����۸�3")) != null)
          return temp;
        }
        String mrjg = detailrow.get("mrjg");
        if(mrjg.equals(""))
          return showJavaScript("alert('Ĭ�ϼ۸��ѡ!')");
        String mrzk = detailrow.get("mrzk");
        if(mrzk.equals(""))
          return showJavaScript("alert('Ĭ���ۿ۱���!')");
        if((temp = checkNumber(mrzk, "Ĭ���ۿ�")) != null)
          return temp;
      }
      return null;
    }
  }
  /**
   * ����ַ����Ƿ��������͵�
   * @param value ��Ҫ�����ַ���
   * @param caption javascipt��Ҫ��ʾ�ı���
   * @return ������null��ʾ�����֣���nullΪjavasrcip���
   */
  public static String checkNumber(String value, String caption)
  {
    try{
      Double.parseDouble(value);
    }catch(Exception ex){
      return showJavaScript("alert('�Ƿ� "+caption+"��');");
    }
    return null;
  }
}
