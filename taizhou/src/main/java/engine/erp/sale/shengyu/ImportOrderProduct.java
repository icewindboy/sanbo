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

/**
 * <p>Title: ������ϵͳ_��������������ͬ����--</p>
 * <p>Description: ������ϵͳ_��������������ͬ����</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */

public final class ImportOrderProduct extends BaseAction implements Operate
{
  private static final String IMPORT_SALE_ORDER_SQL = "SELECT * FROM VW_SALE_IMPORT_TD_ORDER_DETAIL WHERE 1=1 ? ";//�ֹ�˾; ������λ(���ݹ�����λ��ȡ����)
  private EngineDataSet dsSaleOrderProduct  = new EngineDataSet();//���ݼ�
  private EngineRow locateResult = null;//���ڶ�λ���ݼ�����
  public  String retuUrl = null;
  private String fgsid = null;   //�ֹ�˾ID
  private String dwtxid="";
  private String storeid ="";
  private String personid ="";
  private String khlx ="";
  private String jsfsid ="";

  public String djlx="";
  private boolean isInitQuery = false; //�Ƿ��Ѿ���ʼ����ѯ����
  private QueryBasic fixedQuery = new QueryFixedItem();
  /**
   *�ӻỰ��ȡ������ʵ��
   *
   * */
  public static ImportOrderProduct getInstance(HttpServletRequest request)
  {
    ImportOrderProduct ImportOrderProductBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "ImportOrderProductBean";
      ImportOrderProductBean = (ImportOrderProduct)session.getAttribute(beanName);
      if(ImportOrderProductBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        ImportOrderProductBean = new ImportOrderProduct();
        ImportOrderProductBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, ImportOrderProductBean);
      }
    }
    return ImportOrderProductBean;
  }
  /**
   * ���캯��
   */
  private ImportOrderProduct()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  /**
   * ��ʼ������
   * @throws Exception �쳣��Ϣ
   */
  protected final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsSaleOrderProduct, null);//�����ݼ�
    addObactioner(String.valueOf(INIT), new Init());//ע���ʼ������
    addObactioner(String.valueOf(FIXED_SEARCH),new Master_Search());
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
   * SessionʧЧʱ�����õĺ���
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsSaleOrderProduct != null){
      dsSaleOrderProduct.close();
      dsSaleOrderProduct = null;
    }
    log = null;
  }
  /**
   * �õ����������
   * @return �������������
   */
  protected final Class childClassName()
  {
    return getClass();
  }
  //�õ�һ����Ϣ
  public final RowMap getLookupRow(String hthwid)
  {
    RowMap row = new RowMap();
    if(hthwid == null || hthwid.equals(""))
      return row;//����
    EngineRow locateRow = new EngineRow(dsSaleOrderProduct, "hthwid");//����ָ��DataSet�����1�е�EngineRow������û�����ݣ�
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "hthwid");
    locateRow.setValue(0, hthwid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsSaleOrderProduct;
  }
  /**
   * ��ʼ�������ĵ�ִ����
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = request.getParameter("src");//where is it coming?
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      dwtxid = request.getParameter("dwtxid").trim();
      //dwtxid=dwtxid.equals("")?"":" AND dwtxid="+dwtxid;
      storeid = request.getParameter("storeid").trim();//�ֿ�
      personid = request.getParameter("personid").trim();
      //khlx = request.getParameter("khlx").trim();
      jsfsid = request.getParameter("jsfsid").trim();
      djlx =  request.getParameter("djlx").trim();//��������
      if(dwtxid.equals("")||storeid.equals("")||djlx.equals("")||personid.equals(""))
        return;
      String SQL = "";//combineSQL(IMPORT_SALE_ORDER_SQL, "?", new String[]{" AND fgsid="+fgsid+" AND dwtxid="+dwtxid+" AND (storeid is null OR storeid="+storeid+") "});
      if(djlx.equals("-1"))
        SQL=combineSQL(IMPORT_SALE_ORDER_SQL, "?", new String[]{" AND jsfsid='"+jsfsid+"' AND personid='"+personid+"' AND fgsid='"+fgsid+"' AND dwtxid='"+dwtxid+"' AND (storeid is null OR storeid='"+storeid+"') AND isrefer=1 AND zt<>4 AND zt<>8  AND lb=2  "});
      else
        SQL=combineSQL(IMPORT_SALE_ORDER_SQL, "?", new String[]{" AND jsfsid='"+jsfsid+"' AND personid='"+personid+"' AND fgsid='"+fgsid+"' AND dwtxid='"+dwtxid+"' AND (storeid is null OR storeid='"+storeid+"') AND isrefer=1 AND zt<>4 AND zt<>8 AND lb=1 and sl>0 "});
      dsSaleOrderProduct.setQueryString(SQL);
      dsSaleOrderProduct.setRowMax(null);
      RowMap row = fixedQuery.getSearchRow();
      row.put("zt","1");
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
      String zt=data.getParameter("zt");
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;//+" AND jsfsid='"+jsfsid+"' AND personid='"+personid+"' AND khlx='"+khlx+"' AND dwtxid='"+dwtxid+"'  AND fgsid='"+fgsid+"' AND (storeid is null or storeid='"+storeid+"')";
      SQL=SQL+(djlx.equals("-1")?" AND lb=1 ":" AND lb=2 ")+" AND jsfsid='"+jsfsid+"' AND personid='"+personid+"' AND khlx='"+khlx+"' AND dwtxid='"+dwtxid+"'  AND fgsid='"+fgsid+"' AND (storeid is null or storeid='"+storeid+"')";
      SQL = combineSQL(IMPORT_SALE_ORDER_SQL, "?", new String[]{SQL});
      if(!dsSaleOrderProduct.getQueryString().equals(SQL))
      {
        dsSaleOrderProduct.setQueryString(SQL);
        dsSaleOrderProduct.setRowMax(null);
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
      EngineDataSet master = dsSaleOrderProduct;
      //EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //��ʼ���̶��Ĳ�ѯ��Ŀ
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("htbh"), null, null, null, null, "="),//��ͬ���
        new QueryColumn(master.getColumn("htrq"), null, null, null, "a", ">="),//��ͬ����
        new QueryColumn(master.getColumn("htrq"), null, null, null, "b", "<="),//��ͬ����
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")//״̬
      });
      isInitQuery = true;
    }
  }
}
