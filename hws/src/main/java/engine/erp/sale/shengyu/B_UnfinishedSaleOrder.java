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
 * δ��ɺ�ͬ�б�
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @version 1.0
 */
public final class B_UnfinishedSaleOrder extends BaseAction implements Operate
{
  private static final String MASTER_STRUT_SQL = "SELECT * FROM xs_ht WHERE 1<>1 ORDER BY htbh DESC ";
  private static final String MASTER_SQL = "SELECT DISTINCT a.htbh,a.khlx,a.dwdm,a.dwmc,a.zsl,a.zje,a.xm,a.czy,a.htid,a.deptid FROM vw_sale_hthw a WHERE 1=1 AND ?  AND a.fgsid=? ? ORDER BY a.htbh DESC";
  /*
  private static final String ORDER_RECEIVE_GOODS
      = "SELECT htid FROM (SELECT a.htid, SUM(nvl(b.sl,0)) sl FROM xs_hthw a, xs_tdhw b "
      + "WHERE a.hthwid = b.hthwid AND a.htid IN (?) GROUP BY a.htid) t WHERE t.sl <> 0 ";
  */
  private EngineDataSet dsMasterTable  = new EngineDataSet();//����
 // private EngineDataSet dsDetailTable  = new EngineDataSet();//�ӱ�
  private EngineDataSet dsProvider ;
  private ArrayList d_RowInfos = new ArrayList(); //�ӱ���м�¼������
  public  String retuUrl = null;
  public  String loginId = "";   //��¼Ա����ID
  public  String loginCode = ""; //��½Ա���ı���
  public  String loginName = ""; //��¼Ա��������
  private String fgsid = null;   //�ֹ�˾ID
  private String htid = null;
  private User user = null;
  /**
   * ���ۺ�ͬ�б��ʵ��
   * @param request jsp����
   * @param isApproveStat �Ƿ�������״̬
   * @return �������ۺ�ͬ�б��ʵ��
   */
  public static B_UnfinishedSaleOrder getInstance(HttpServletRequest request)
  {
    B_UnfinishedSaleOrder b_UnfinishedSaleOrderBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_UnfinishedSaleOrderBean";
      b_UnfinishedSaleOrderBean = (B_UnfinishedSaleOrder)session.getAttribute(beanName);
      if(b_UnfinishedSaleOrderBean == null)
      {
        //����LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        b_UnfinishedSaleOrderBean = new B_UnfinishedSaleOrder();
        b_UnfinishedSaleOrderBean.fgsid = loginBean.getFirstDeptID();
        b_UnfinishedSaleOrderBean.loginId = loginBean.getUserID();
        b_UnfinishedSaleOrderBean.loginName = loginBean.getUserName();
        b_UnfinishedSaleOrderBean.user = loginBean.getUser();
        session.setAttribute(beanName, b_UnfinishedSaleOrderBean);
      }
    }
    return b_UnfinishedSaleOrderBean;
  }
  /**
   * ���캯��
   */
  private B_UnfinishedSaleOrder()
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
    addObactioner(String.valueOf(INIT), new Init());
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
    log = null;
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
  /*�õ������*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
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
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      dsProvider = new EngineDataSet();
      String SQL = " AND a.jhrq<=to_date('"+today+"','yyyy-mm-dd') ";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"), fgsid, SQL});
      /*
      setDataSetProperty(dsProvider,SQL);
      dsProvider.open();
      StringBuffer sbzt = null;
      ArrayList tmp = new ArrayList();
      dsProvider.first();
      for(int i=0;i<dsProvider.getRowCount();i++)
      {
        String htid = dsProvider.getValue("htid");
        if(tmp.contains(htid))
        {
          dsProvider.next();
          continue;
        }
        if(sbzt==null)
          sbzt= new StringBuffer(" where htid IN("+dsProvider.getValue("htid"));
        else
          sbzt.append(","+dsProvider.getValue("htid"));
        tmp.add(htid);
        dsProvider.next();
      }
      if(sbzt == null)
        sbzt =new StringBuffer();
      else
        sbzt.append(")");
      SQL = sbzt.toString();
      */
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
   }
}
}