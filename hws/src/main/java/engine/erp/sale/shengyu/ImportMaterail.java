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
 * <p>Title: ������_�ƿⵥ������������ϸ</p>
 * <p>Description: ������_�ƿⵥ������������ϸ</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author ���
 * @version 1.0
 */

public final class ImportMaterail extends BaseAction implements Operate
{
  private static final String IMPORT_MATERAIL_SQL = "SELECT * FROM VW_XS_KC_WZMX WHERE zl<>0 and fgsid=? and storeid=? ?  order by cpbm";

  private EngineDataSet dsMaterail  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //�ֹ�˾ID
  private String qtyFormat = null;
  private String storeid = null;
  /**
  * ����̶���ѯ��
   */
  private QueryBasic fixedQuery = new QueryFixedItem();
  private boolean isInitQuery = false; //�Ƿ��Ѿ���ʼ����ѯ����
  /**
   * �õ����������Ϣ��ʵ��
   * @param request jsp����
   * @param isApproveStat �Ƿ�������״̬
   * @return ���ؿ��������Ϣ��ʵ��
   */
  public static ImportMaterail getInstance(HttpServletRequest request)
  {
    ImportMaterail importMaterailBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "importMaterailBean";
      importMaterailBean = (ImportMaterail)session.getAttribute(beanName);
      if(importMaterailBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        importMaterailBean = new ImportMaterail();
        importMaterailBean.qtyFormat = loginBean.getQtyFormat();

        importMaterailBean.fgsid = loginBean.getFirstDeptID();
        importMaterailBean.dsMaterail.setColumnFormat("zl", importMaterailBean.qtyFormat);
        session.setAttribute(beanName, importMaterailBean);
      }
    }
    return importMaterailBean;
  }

  /**
   * ���캯��
   */
  private ImportMaterail()
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
    setDataSetProperty(dsMaterail, null);

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());
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
   * SessionʧЧʱ�����õĺ���
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsMaterail != null){
      dsMaterail.close();
      dsMaterail = null;
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

  /**
   * ��ʼ�������Ĵ�����
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = request.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //��ʼ����ѯ��Ŀ������
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      //�滻�ɱ��ַ�������װSQL
      storeid = request.getParameter("storeid");
      String SQL = combineSQL(IMPORT_MATERAIL_SQL, "?", new String[]{fgsid,storeid});
      dsMaterail.setQueryString(SQL);
        dsMaterail.setRowMax(null);
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
   *  ��ѯ����
   */
  class Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(IMPORT_MATERAIL_SQL, "?", new String[]{fgsid, storeid, SQL});
      if(!dsMaterail.getQueryString().equals(SQL))
      {
        dsMaterail.setQueryString(SQL);
        dsMaterail.setRowMax(null);
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
      EngineDataSet master = dsMaterail;
      //EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //��ʼ���̶��Ĳ�ѯ��Ŀ
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("cpbm"), null, null, null),
        new QueryColumn(master.getColumn("product"), null, null, null),
      });
      isInitQuery = true;
    }
  }
  /*
  *�õ�һ����Ϣ
    */
  public final RowMap getLookupRow(String wzmxid)
  {
    RowMap row = new RowMap();
    if(wzmxid == null || wzmxid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsMaterail, "wzmxid");
    locateRow.setValue(0, wzmxid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }

  public final EngineDataSet getOneTable()
  {
    return dsMaterail;
  }
}