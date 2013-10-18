package engine.erp.buy;

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
 * <p>Title: 采购子系统_供应商报价选择</p>
 * <p>Description: 采购子系统_供应商报价选择</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class BuyCorpSelect extends BaseAction implements Operate
{
  private static final String BUY_CROP_SQL = "SELECT a.*, b.sxz FROM cg_bj a, kc_dmsx b WHERE a.dmsxid=b.dmsxid(+) AND a.fgsid='?' AND a.cpid=? ? ORDER BY a.cgbjID";

  private EngineDataSet dsBuyPrice  = new EngineDataSet();//主表
  private EngineRow locateResult = null;

  //private QueryBasic fixedQuery = null;
  public  String retuUrl = null;
  private String fgsid = null;  //分公司ID
  /**
   * 得到最低报价单位信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回最低报价单位信息的实例
   */
  public static BuyCorpSelect getInstance(HttpServletRequest request)
  {
    BuyCorpSelect buyCorpSelectBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "buyCorpSelectBean";
      buyCorpSelectBean = (BuyCorpSelect)session.getAttribute(beanName);
      if(buyCorpSelectBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsid = loginBean.getFirstDeptID();
        String priceFormat  = loginBean.getPriceFormat();
        buyCorpSelectBean = new BuyCorpSelect(fgsid);
        buyCorpSelectBean.dsBuyPrice.setColumnFormat("bj", priceFormat);
        session.setAttribute(beanName, buyCorpSelectBean);
      }
    }
    return buyCorpSelectBean;
  }

  /**
   * 构造函数
   */
  private BuyCorpSelect(String fgsid)
  {
    this.fgsid = fgsid;
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
    setDataSetProperty(dsBuyPrice, null);

    addObactioner(String.valueOf(INIT), new Init());
    //addObactioner(String.valueOf(FIXED_SEARCH), new Search());
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
   * Session失效时，调用的函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsBuyPrice != null){
      dsBuyPrice.close();
      dsBuyPrice = null;
    }
    log = null;
  }

  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }

  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = request.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //初始化查询项目和内容
      //initQueryItem(request);
      //fixedQuery.getSearchRow().clear();
      //替换可变字符串，组装SQL
      String cpid = request.getParameter("cpid");
      if(cpid==null)
        cpid = "";
      //String dmsxid = request.getParameter("dmsxid");
      //String sql = null;
      //if(dmsxid.equals(""))
       // sql ="";
     // else
       // sql = " AND dmsxid="+dmsxid;
      String SQL = combineSQL(BUY_CROP_SQL, "?", new String[]{fgsid, cpid,""});
      dsBuyPrice.setQueryString(SQL);
        dsBuyPrice.setRowMax(null);
    }
  }
  public final EngineDataSet getOneTable()
  {
    return dsBuyPrice;
  }
}
