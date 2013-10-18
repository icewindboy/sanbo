package engine.erp.jit;

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
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class View_Scjh extends BaseAction implements Operate
{
  private static final String IMPORT_SALE_SQL = " select a.processid,a.processdm,b.jhh,b.djh,c.cpid,d.cpbm,d.product,c.dmsxid,e.sxz from sc_process a,sc_jh  b,sc_jhmx c,vw_kc_dm  d, kc_dmsx e where  a.scjhid=b.scjhid and b.scjhid=c.scjhid and c.cpid=d.cpid and c.dmsxid=e.dmsxid(+) and a.processid in(select m.processid from sc_processmx m where m.processmxid=? ) ";//分公司; 往来单位
  //private static final String MASTER_SQL    = " select a.*,c.jhh,c.djh,b.processdm from sc_processmx a,sc_process b,sc_jh c where a.processid=b.processid and b.scjhid=c.scjhid order by a.processmxid desc ";
  private EngineDataSet dsSaleOrderProduct  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public String processmxid = "";

  /**
   * 析构函数
   * */
  public static View_Scjh getInstance(HttpServletRequest request)
  {
    View_Scjh View_ScjhBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "View_ScjhBean";
      View_ScjhBean = (View_Scjh)session.getAttribute(beanName);
      if(View_ScjhBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        View_ScjhBean = new View_Scjh();
        View_ScjhBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, View_ScjhBean);
      }
    }
    return View_ScjhBean;
  }
  /**
   * 构造函数
   */
  private View_Scjh()
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
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsSaleOrderProduct, null);//无数据
    addObactioner(String.valueOf(INIT), new Init());
  }
  /**
   *网页调用
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
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }

  /**
   *session失效时调用
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
      processmxid = request.getParameter("processmxid");
      String SQL = combineSQL(IMPORT_SALE_SQL,"?",(new String[]{(processmxid!=null&&!processmxid.equals(""))?processmxid:" -1 "}));//IMPORT_SALE_SQL;
      dsSaleOrderProduct.setQueryString(SQL);
      dsSaleOrderProduct.setRowMax(null);
    }
  }
}