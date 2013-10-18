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
 * 销售管理-销售合同
 * 未完成合同列表
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
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
 // private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsProvider ;
  private ArrayList d_RowInfos = new ArrayList(); //从表多行记录的引用
  public  String retuUrl = null;
  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String fgsid = null;   //分公司ID
  private String htid = null;
  private User user = null;
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
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
        //引用LoginBean
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
   * 构造函数
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
   * 初始化函数
   * @throws Exception 异常信息
   */
  private final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsMasterTable, MASTER_STRUT_SQL);
    addObactioner(String.valueOf(INIT), new Init());
  }
  /**
   * JSP调用的函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      String operate = request.getParameter(OPERATE_KEY);
      if(operate != null && operate.trim().length() > 0)
      {
        RunData data = notifyObactioners(operate, request, response, null);
        if(data == null)
          return showMessage("无效操作", false);
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
   * Session失效时，调用的函数
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
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }
  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }
  /**
   * 初始化操作的触发类
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