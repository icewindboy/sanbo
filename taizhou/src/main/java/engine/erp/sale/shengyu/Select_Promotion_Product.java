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
import engine.common.LoginBean;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
import java.util.Hashtable;
import engine.util.MessageFormat;
import engine.util.log.Log;
/**
 * <p>Title: ������ϵͳ_</p>
 * <p>Description: ������ϵͳ_</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class Select_Promotion_Product extends BaseAction implements Operate
{
  private static final String IMPORT_SALE_SQL = "select t.dwtxid,t.cpid,t.startdate,t.enddate,t.memo,t.prom_price,b.wzdjid,c.storeid, c.jldw from xs_promotion t ,xs_wzdj b,VW_KC_DM c WHERE t.cpid=b.cpid(+) AND t.cpid=c.cpid AND t.dwtxid='?' AND t.startdate<=to_date('?','yyyy-mm-dd') AND t.enddate>=to_date('?','yyyy-mm-dd') and (c.storeid is null or c.storeid='?' ) ? ";//�ֹ�˾; ������λ
  private static final String BACK_SALE_SQL = "select t.dwtxid,t.cpid,t.startdate,t.enddate,t.memo,t.prom_price,b.wzdjid,c.storeid,c.jldw from xs_promotion t ,xs_wzdj b,VW_KC_DM c WHERE t.cpid=b.cpid(+) AND t.cpid=c.cpid AND t.dwtxid='?'  and (c.storeid is null or c.storeid='?' ) ? ";//�ֹ�˾; ������λ

  private static final String SALE_SQL = "select t.dwtxid,t.cpid,t.startdate,t.enddate,t.memo,t.prom_price,b.wzdjid,c.storeid,c.jldw from xs_promotion t ,xs_wzdj b,VW_KC_DM c WHERE t.cpid=b.cpid(+) AND t.cpid=c.cpid AND t.dwtxid={dwtxid}  and (c.storeid is null or c.storeid={storeid} ) {other} AND fgsid={fgsid} ";//�ֹ�˾; ������λ


  //private static final String MASTER_SQL    = "SELECT * FROM VW_SALE_INVOICE_IMPORT_TDDETAL WHERE  fgsid=? ?  order by tdbh,dwtxid ";
  private EngineDataSet dsSaleOrderProduct  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //�ֹ�˾ID
  private boolean isInitQuery = false; //�Ƿ��Ѿ���ʼ����ѯ����
  private QueryBasic fixedQuery = new QueryFixedItem();
  private String dwtxid = "";
  private String storeid = "";
  private String personid = "";
  private boolean isback = false;
  private String methodName = null;   //����window.opener�еķ���
  public String[] inputName = null;    //
  public String[] fieldName = null;    //�ֶ�����
  public String srcFrm=null;           //���ݵ�ԭform������
  /**
   * ��������
   * */
  public static Select_Promotion_Product getInstance(HttpServletRequest request)
  {
    Select_Promotion_Product select_Promotion_ProductBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "select_Promotion_ProductBean";
      select_Promotion_ProductBean = (Select_Promotion_Product)session.getAttribute(beanName);
      if(select_Promotion_ProductBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        select_Promotion_ProductBean = new Select_Promotion_Product();
        select_Promotion_ProductBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, select_Promotion_ProductBean);
      }
    }
    return select_Promotion_ProductBean;
  }
  /**
   * ���캯��
   */
  private Select_Promotion_Product()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  /**
   * Implement this engine.project.OperateCommon abstract method
   * ��ʼ������
   * @throws Exception �쳣��Ϣ
   */
  protected final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsSaleOrderProduct, null);//������
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(String.valueOf(PROD_CHANGE), new CodeSearch());
    addObactioner(String.valueOf(PROD_NAME_CHANGE), new NameSearch());
  }
  /**
   *��ҳ����
   * */
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
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
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
   * sessionʧЧʱ����
   * */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsSaleOrderProduct != null){
      dsSaleOrderProduct.close();
      dsSaleOrderProduct = null;
    }
    log = null;
  }
  protected final Class childClassName()
  {
    return getClass();
  }
  //�õ���ѯ����
  public final RowMap getLookupRow(String cpid)
  {
    RowMap row = new RowMap();
    if(cpid == null || cpid.equals(""))
      return row;//����
    EngineRow locateRow = new EngineRow(dsSaleOrderProduct, "cpid");//����ָ��DataSet�����1�е�EngineRow������û�����ݣ�
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "cpid");
    locateRow.setValue(0, cpid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsSaleOrderProduct;
  }



  /**
    * ��ʼ�������ݼ���SQL���
    * @return �������������
    */
   private void init(RunData data) throws Exception
   {
     HttpServletRequest request = data.getRequest();
     retuUrl = request.getParameter("src");
     retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
     dwtxid = request.getParameter("dwtxid");
     storeid = request.getParameter("storeid");
     LoginBean loginBean = LoginBean.getInstance(request);
     fgsid=loginBean.getFirstDeptID();
     if(request.getParameter("isback")!=null&&request.getParameter("isback").equals("1"))
        isback = true;
   //�õ��رմ���ǰҪ���õķ���
     methodName = data.getParameter("method");
     srcFrm = data.getParameter("srcFrm");

     inputName = data.getParameterValues("srcVar");
     fieldName = data.getParameterValues("fieldVar");
  }

/**
 * ��ʼ��
 *
 * */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      init(data);
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String SQL = "";
      if(isback)
        SQL=combineSQL(BACK_SALE_SQL,"?",new String[]{dwtxid,storeid,SQL});
      else
        SQL=combineSQL(IMPORT_SALE_SQL,"?",new String[]{dwtxid,today,today,storeid,SQL});

      dsSaleOrderProduct.setQueryString(SQL);
      dsSaleOrderProduct.setRowMax(null);
    }
  }
/**
 *
 *
 * */
/**
 *  ��ѯ����
 */
class Master_Search implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest request = data.getRequest();
    initQueryItem(data.getRequest());
    fixedQuery.setSearchValue(data.getRequest());
    String cpid = request.getParameter("cpid");
    String pm = request.getParameter("pm");
    String prom_price = request.getParameter("prom_price");
    String SQL="";
    if(!cpid.equals(""))
      SQL = SQL+" AND t.cpid='"+cpid+"'";
    if(!prom_price.equals(""))
      SQL = SQL+" AND t.prom_price='"+prom_price+"'";
    if(!pm.equals(""))
      SQL = SQL+" AND c.product like '%"+pm+"%'";

    String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

    if(isback)
      SQL=combineSQL(BACK_SALE_SQL,"?",new String[]{dwtxid,storeid,SQL});
    else
        SQL=combineSQL(IMPORT_SALE_SQL,"?",new String[]{dwtxid,today,today,storeid,SQL});

    if(!dsSaleOrderProduct.getQueryString().equals(SQL))
    {
      dsSaleOrderProduct.setQueryString(SQL);
      dsSaleOrderProduct.setRowMax(null);
    }
  }

  /**
     * �õ���Ҫ����window.opener�еķ���������
     * @return ����������
     */
    public String getMethodName()
    {
      if(methodName != null && methodName.length() == 0)
        return null;
      return methodName;
  }
  /**
    * �õ�д��־�Ķ���
    * @return д��־�Ķ���
    */
   public Log getLog()
   {
     return log;
  }
  /**
   * ��ʼ����ѯ�ĸ�����
   * @param request web�������
   */
  private void initQueryItem(HttpServletRequest request)
  {
    if(isInitQuery)
      return;
    EngineDataSet master = dsSaleOrderProduct;
    //��ʼ���̶��Ĳ�ѯ��Ŀ
    fixedQuery = new QueryFixedItem();
    fixedQuery.addShowColumn("", new QueryColumn[]{
      new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),
      new QueryColumn(master.getColumn("prom_price"), null, null, null, null, "=")
    });
    isInitQuery = true;
  }
  }
  /**
  * ͨ����Ʒ����õ���Ʒ��Ϣ�Ĵ�����
  */
 final class CodeSearch implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     //��ʼ����ѯ��Ŀ������
     init(data);
     //table.getWhereInfo().clearWhereValues();
     String cpbm = data.getParameter("code", "");
     String storeid = data.getParameter("storeid", "");
     String dwtxid = data.getParameter("dwtxid", "");
     Hashtable table = new Hashtable();
     table.put("fgsid", fgsid);
     table.put("storeid", storeid);
     table.put("other", "AND cpbm LIKE '"+cpbm+"%'");
     table.put("dwtxid", dwtxid);
     //SQL = combineSQL(SALE_GOODS_SQL, "?", new String[]{"AND cpbm LIKE '"+cpbm+"%'", fgsid});
     String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
     String SQL = null;
      SQL=combineSQL(SALE_SQL,"?",new String[]{dwtxid,today,today,storeid,SQL});
      SQL = MessageFormat.format(SQL, table);
      System.out.print(SQL);
     EngineDataSet ds = getOneTable();
     ds.setQueryString(SQL);
     if(ds.isOpen())
     {
       ds.readyRefresh();
       ds.refresh();
     }
     else
       ds.openDataSet();
   }
 }

 /**
  * ͨ����ƷƷ�����õ���Ʒ��Ϣ�Ĵ�����
  */
 final class NameSearch implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     init(data);
     String name = data.getParameter("name", "");
     String storeid = data.getParameter("storeid", "");
     String dwtxid = data.getParameter("dwtxid", "");
     Hashtable table = new Hashtable();
     table.put("fgsid", fgsid);
     table.put("storeid", storeid);
     table.put("other", "AND product LIKE '%"+name+"%'");
     table.put("dwtxid", dwtxid);

     //SQL = combineSQL(SALE_GOODS_SQL, "?", new String[]{"AND product LIKE '%"+name+"%'", fgsid});

     String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
     String SQL = null;
     SQL=combineSQL(SALE_SQL,"?",new String[]{dwtxid,today,today,storeid,SQL});
     SQL = MessageFormat.format(SQL, table);

     System.out.print(SQL);
     EngineDataSet ds = getOneTable();
     ds.setQueryString(SQL);
     if(ds.isOpen())
     {
       ds.readyRefresh();
       ds.refresh();
     }
     else
       ds.openDataSet();
   }
 }

}