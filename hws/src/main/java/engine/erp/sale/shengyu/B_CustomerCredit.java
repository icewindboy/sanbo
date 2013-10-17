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
 * <p>Title: �ͻ�������� </p>
 * �����������ݼ�,����һ�����ݼ����ڻ�ȡ����������λ,
 * ��һ�����ݼ���������ȱ��л�ȡ����,������ǰһ�����ݼ���ǰ״̬��������������λ
 * ��һ�����ݼ�ֻ����ǰһ�����ݼ�������д���ݵļ�¼
 * �ص�:���ݼ���ArrayList�м����ͬ������.
 * �ͻ�������ȱ�:xs_khxyed�ĵ������.
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * <p>Author:Engine </p>
 * @version 1.0
 */

public final class B_CustomerCredit extends BaseAction implements Operate
{
  /**
   *��Ӧ�޸İ�ť
   * */
  public static final String MODIFY = "1005";
  /**
   *Ĭ��״̬�ǲ����޸�
   * */
  private boolean canModify = false;
  /**
   * ��ѯ����
   **/
  public static final String FIXED_SEARCH = "1009";
  /**
   * ��ȡ�ͻ�������ȱ�Ľṹ
   */
  private static final String CUSTOMER_CREDIT_STRUCT_SQL = "SELECT * FROM xs_khxyed WHERE 1<>1 ";
  /**
   * ����ͻ�����������������ͼ����ȡ����
   * �Ա�ȡ������������λ��Ϣ
   * */
  private static final String CUSTOMER_CREDIT_SQL = "SELECT * FROM VW_XS_XYED WHERE fgsid=? ? ORDER BY areacode,dwdm ";//
  /**
   * �ͻ�������ȱ�
   * */
  private static final String XYED_SQL = "SELECT * FROM xs_khxyed  WHERE fgsid=? ? ";
  /**
   * ����ͻ����������Ϣ�����ݼ�(����ͼ�г�ȡ)
   */
  private EngineDataSet dsxs_khxyed = new EngineDataSet();
  /**
   * ����ͻ����������Ϣ�����ݼ�
   */
  private EngineDataSet dsxs_khxyed_salve = new EngineDataSet();
  /**
   * ���ڶ�λ���ݼ�
   */
  private EngineRow locateRow = null;

  /**
   * �����û��������Ϣ
   */
  private RowMap rowInfo = new RowMap();
  private ArrayList d_RowInfos = null;
  /**
   * �Ƿ������״̬
   */
  public  boolean isAdd = true;

  /**
   * �����޸Ĳ������м�¼ָ��
   */
  private long    editrow = 0;

  /**
   * ������ذ�ť��URL
   */
  public  String retuUrl = null;

  private boolean isInitQuery = false;
  public  String loginName = ""; //��¼Ա��������
  private String fgsid = null;   //�ֹ�˾ID
  /**
   * ����̶���ѯ��
   */
  private QueryFixedItem fixedQuery = new QueryFixedItem();
  /**
   * �õ��ͻ����������Ϣ��ʵ��
   * @param request jsp����
   * @param isApproveStat �Ƿ�������״̬
   * @return ���ر���������Ϣ��ʵ��
   */
  public static B_CustomerCredit getInstance(HttpServletRequest request)
  {
    B_CustomerCredit b_CustomerCreditBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_CustomerCreditBean";
      b_CustomerCreditBean = (B_CustomerCredit)session.getAttribute(beanName);
      //�жϸ�session�Ƿ��и�bean��ʵ��
      if(b_CustomerCreditBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsid = loginBean.getFirstDeptID();
        b_CustomerCreditBean = new B_CustomerCredit(fgsid);
        b_CustomerCreditBean.loginName = loginBean.getUserName();
        //���ø�ʽ�����ֶ�
        //b_CustomerCreditBean.dsxs_khxyed.setColumnFormat("xyed", loginBean.getPriceFormat());
        session.setAttribute(beanName, b_CustomerCreditBean);
      }
    }
    return b_CustomerCreditBean;
  }
  /**
   * ���캯��
   */
  private B_CustomerCredit(String fgsid)
  {
    this.fgsid = fgsid;
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
    setDataSetProperty(dsxs_khxyed, CUSTOMER_CREDIT_STRUCT_SQL);
    setDataSetProperty(dsxs_khxyed_salve, CUSTOMER_CREDIT_STRUCT_SQL);
    //dsxs_khxyed_salve.setSequence(new SequenceDescriptor(new String[]{"xyedid"}, new String[]{"S_XS_KHXYED"}));
    dsxs_khxyed.setSort(new SortDescriptor("", new String[]{"areacode","dwdm"}, new boolean[]{false,false}, null, 0));
    dsxs_khxyed.setTableName("xs_khxyed");

    dsxs_khxyed.addLoadListener(new com.borland.dx.dataset.LoadListener() {
      public void dataLoaded(LoadEvent e) {
        initRowInfo(false, true);
      }
    });
    //��Ӳ����Ĵ�������
    addObactioner(String.valueOf(INIT), new B_CustomerCredit_Init());
    addObactioner(String.valueOf(DETAIL_DEL), new B_CustomerCredit_Delete());
    addObactioner(String.valueOf(POST), new B_CustomerCredit_Post());
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());
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
      if(dsxs_khxyed.isOpen() && dsxs_khxyed.changesPending())
        dsxs_khxyed.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  //----Implementation of the BaseAction abstract class
  /**
   * jvmҪ���ĺ���,��������������
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsxs_khxyed != null){
      dsxs_khxyed.close();
      dsxs_khxyed = null;
    }
    log = null;
    rowInfo = null;
    locateRow = null;
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
    if(!dsxs_khxyed.isOpen())
    {
      dsxs_khxyed.open();
    }
    int rownum = dsxs_khxyed.getRowCount();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("dwtxid", rowInfo.get("dwtxid_"+i));//������λ

      //detailRow.put("djlx", rowInfo.get("djlx_"+i));
      //detailRow.put("zklx", rowInfo.get("zklx_"+i));
      //detailRow.put("zkl", rowInfo.get("zkl_"+i));

      detailRow.put("xyed", rowInfo.get("xyed_"+i));//�������
      detailRow.put("xydj", rowInfo.get("xydj_"+i));//�����ȼ�
      detailRow.put("hkts", rowInfo.get("hkts_"+i));//�ؿ�����
      d_RowInfos.set(i,detailRow);
    }
  }

  //----Implementation of the BaseAction abstract class
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
   * @throws java.lang.Exception �쳣
   */
  private final void initRowInfo(boolean isAdd, boolean isInit) //throws java.lang.Exception
  {
    StringBuffer buf = null;
    EngineDataSet dsxs_khxyed_tmp = dsxs_khxyed;
    if(d_RowInfos == null)
      d_RowInfos = new ArrayList(dsxs_khxyed_tmp.getRowCount());
    else if(isInit)
      d_RowInfos.clear();
    dsxs_khxyed_tmp.first();
    for(int i=0; i<dsxs_khxyed_tmp.getRowCount(); i++)
    {
      if(buf == null)
        buf = new StringBuffer("AND dwtxid IN(").append(dsxs_khxyed_tmp.getValue("dwtxid"));
      else
        buf.append(",").append(dsxs_khxyed_tmp.getValue("dwtxid"));

      RowMap row = new RowMap(dsxs_khxyed_tmp);
      d_RowInfos.add(row);
      dsxs_khxyed_tmp.next();
    }
    if(buf == null)
      buf =new StringBuffer();
    else
      buf.append(")");
    String SQL = combineSQL(XYED_SQL, "?", new String[]{fgsid, buf.toString()});
    dsxs_khxyed_salve.setQueryString(SQL);
    if(dsxs_khxyed_salve.isOpen())
      dsxs_khxyed_salve.refresh();
    else
      dsxs_khxyed_salve.openDataSet();
  }
      /*�õ������*/
  public final EngineDataSet getOneTable()
  {
    return dsxs_khxyed;
  }
      /*�õ�һ�е���Ϣ*/
  public final RowMap getRowinfo()
  {
    return rowInfo;
  }
      /*�õ�״̬*/
  public final boolean getState()
  {
    return canModify;
  }
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

  //---------------------------------------------------------------------
  //�����ǲ���ʵ�ֵ���(��5���ڲ���)
  //---------------------------------------------------------------------

  /**
   * ��ʼ�������Ĵ�����
   */
  class B_CustomerCredit_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      canModify= false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      fixedQuery.getSearchRow().clear();

      String SQL = combineSQL(CUSTOMER_CREDIT_SQL,"?",new String[]{fgsid,""});
      dsxs_khxyed.setQueryString(SQL);
      dsxs_khxyed.setRowMax(null);
    }
  }
  /**
   * ��Ӳ�ѯ�����Ĵ�����
   */
  class FIXED_SEARCH implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      QueryBasic queryBasic = fixedQuery;
      queryBasic.setSearchValue(data.getRequest());
      String SQL = queryBasic.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(CUSTOMER_CREDIT_SQL, "?", new String[]{fgsid, SQL});
      if(!dsxs_khxyed.getQueryString().equals(SQL))
      {
        dsxs_khxyed.setQueryString(SQL);
        dsxs_khxyed.setRowMax(null);
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
      EngineDataSet master = dsxs_khxyed;
      if(!master.isOpen())
        master.open();
      //��ʼ���̶��Ĳ�ѯ��Ŀ
      //������λdwtxId;�������xyed;�����ȼ�xydj;�ؿ�����hkts;
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("dwtxId"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("dwmc"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("dwdm"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("areacode"), null, null, null,  "a", ">="),
        new QueryColumn(master.getColumn("areacode"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("xm"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("xyed"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("xyed"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("xydj"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("xydj"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("hkts"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("hkts"), null, null, null, "b", "<="),
      });
      isInitQuery = true;
    }
  }
  /**
   * ��������Ĵ�����
   */
  class B_CustomerCredit_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      RowMap detailrow = null;
      putDetailInfo(data.getRequest());

      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String xyed=detailrow.get("xyed");
        String hkts=detailrow.get("hkts");
        String tmp = null;
        if(xyed.length()>0)
        {
          tmp = checkNumber(xyed,"�������");
        }
        if(tmp!=null)
        {
          data.setMessage(tmp);
          return;
        }
        if(hkts.length()>0)
        {
          tmp = checkNumber(hkts,"�ؿ�����");
        }
        if(tmp!=null)
        {
          data.setMessage(tmp);
          return;
        }
      }
      EngineRow locateGoodsRow = new EngineRow(dsxs_khxyed_salve, new String[]{"dwtxid"});
      dsxs_khxyed_salve.first();
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String dwtxid=detailrow.get("dwtxid");
        String xyed=detailrow.get("xyed");
        String xydj=detailrow.get("xydj");
        String hkts=detailrow.get("hkts");

        //String djlx=detailrow.get("djlx");
        //String zklx=detailrow.get("zklx");
        //String zkl=detailrow.get("zkl");

        //if(xyed.equals("")&&xydj.equals("")&&hkts.equals(""))
        //  continue;
        locateGoodsRow.setValue(0,dwtxid);
        if(!dsxs_khxyed_salve.locate(locateGoodsRow, Locate.FIRST))
        {
          dsxs_khxyed_salve.insertRow(false);
          //String xyedid = dataSetProvider.getSequence("S_XS_KHXYED");
          //dsxs_khxyed.setValue("xyedid",xyedid);
        }
        dsxs_khxyed_salve.setValue("dwtxid", dwtxid);
        dsxs_khxyed_salve.setValue("fgsid", fgsid);

        //dsxs_khxyed_salve.setValue("djlx", djlx);
        //dsxs_khxyed_salve.setValue("zklx", zklx);
        //dsxs_khxyed_salve.setValue("zkl", zkl);

        dsxs_khxyed_salve.setValue("xyed", xyed);
        dsxs_khxyed_salve.setValue("xydj", xydj);
        dsxs_khxyed_salve.setValue("hkts", hkts);
        dsxs_khxyed_salve.post();
      }
      dsxs_khxyed_salve.saveChanges();
      dsxs_khxyed.refresh();
      initRowInfo(false,true);
      canModify=false;
         /*
          EngineDataSet ds = new EngineDataSet();
          setDataSetProperty(ds,XYED_SQL);
          ds.setSequence(new SequenceDescriptor(new String[]{"xyedid"}, new String[]{"S_XS_KHXYED"}));
          for(int i=0; i<d_RowInfos.size(); i++)
          {
            detailrow = (RowMap)d_RowInfos.get(i);
            String dwtxid=detailrow.get("dwtxid");
            String xyed=detailrow.get("xyed");
            String xydj=detailrow.get("xydj");
            String hkts=detailrow.get("hkts");
              if(!xyed.trim().equals("")||!xydj.trim().equals("")||!hkts.trim().equals(""))
              {
                ds.setQueryString("SELECT * FROM xs_khxyed where dwtxid='"+dwtxid+"'");
                if(!ds.isOpen())
                  ds.open();
                ds.refresh();
                if(ds.getRowCount()==0)
                {
                  ds.insertRow(false);
                  ds.setValue("xyedid","-1");
                  ds.setValue("dwtxid", dwtxid);
                  ds.setValue("fgsid", fgsid);
                }
                ds.setValue("xyed", xyed);
                ds.setValue("xydj", xydj);
                ds.setValue("hkts", hkts);
                ds.post();
                ds.saveChanges();
              }
          }
          dsxs_khxyed.refresh();
          */
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
   * ɾ�������Ĵ�����
   */
  class B_CustomerCredit_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      int num = Integer.parseInt(data.getParameter("rownum"));
      ds.goToRow(num);
      ds.deleteRow();
      ds.saveChanges();
    }
  }
}