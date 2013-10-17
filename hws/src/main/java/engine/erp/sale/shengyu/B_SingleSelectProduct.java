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
 * <p>Title: ������ϵͳ_�²�Ʒ</p>
 * <p>Description: ������ϵͳ_�²�Ʒ</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class B_SingleSelectProduct extends BaseAction implements Operate
{
  private static final String IMPORT_SALE_SQL = "SELECT * FROM VW_SALE_NEW_PRODUCT WHERE zt=8 and  fgsid=? ? ";//�ֹ�˾; ������λ
  private EngineDataSet dsSaleOrderProduct  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //�ֹ�˾ID
  private boolean isInitQuery = false; //�Ƿ��Ѿ���ʼ����ѯ����
  private QueryBasic fixedQuery = new QueryFixedItem();
  /**
   * ��������
   * */
  public static B_SingleSelectProduct getInstance(HttpServletRequest request)
  {
    B_SingleSelectProduct b_SingleSelectProductBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_SingleSelectProductBean";
      b_SingleSelectProductBean = (B_SingleSelectProduct)session.getAttribute(beanName);
      if(b_SingleSelectProductBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        b_SingleSelectProductBean = new B_SingleSelectProduct();
        b_SingleSelectProductBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, b_SingleSelectProductBean);
      }
    }
    return b_SingleSelectProductBean;
  }
  /**
   * ���캯��
   */
  private B_SingleSelectProduct()
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
*sessionʧЧʱ����
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
  public final RowMap getLookupRow(String tdId)
  {
    RowMap row = new RowMap();
    if(tdId == null || tdId.equals(""))
      return row;//����
    EngineRow locateRow = new EngineRow(dsSaleOrderProduct, "tdId");//����ָ��DataSet�����1�е�EngineRow������û�����ݣ�
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "tdId");
    locateRow.setValue(0, tdId);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsSaleOrderProduct;
  }
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = request.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      String cpid = request.getParameter("cpid");
      String dmsxid = request.getParameter("dmsxid");
      String dwtxid = request.getParameter("dwtxid");
      String deptid = request.getParameter("deptid");
      String SQL = combineSQL(IMPORT_SALE_SQL, "?", new String[]{fgsid,""});
      if(!cpid.equals(""))
        SQL=SQL+" AND cpid='"+cpid+"'";
      if(!dmsxid.equals(""))
        SQL=SQL+" AND dmsxid='"+dmsxid+"'";
      if(!dwtxid.equals(""))
        SQL=SQL+" AND dwtxid='"+dwtxid+"'";
      if(!deptid.equals(""))
        SQL=SQL+" AND deptid='"+deptid+"'";
      dsSaleOrderProduct.setQueryString(SQL);
        dsSaleOrderProduct.setRowMax(null);
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
      SQL = combineSQL(IMPORT_SALE_SQL, "?", new String[]{fgsid, SQL});
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
       new QueryColumn(master.getColumn("tdbh"), null, null, null, null, "="),//�ᵥ���
       new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),//������λ
       new QueryColumn(master.getColumn("tdrq"), null, null, null, "a", ">="),//�ᵥ����
       new QueryColumn(master.getColumn("tdrq"), null, null, null, "b", "<="),//�ᵥ����
       new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//����id
       new QueryColumn(master.getColumn("djlx"), null, null, null, null, "=")//����id
     });
     isInitQuery = true;
  }
}
}