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
 * <p>Title: 采购子系统_采购货物选择</p>
 * <p>Description: 采购子系统_采购货物选择</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class BuyGoodsSelect extends BaseAction implements Operate
{
  private static final String BUY_GOODS_STRUCT_SQL = "SELECT * FROM cg_bj WHERE 1<>1";
  private static final String BUY_GOODS_SQL = "SELECT a.* FROM cg_bj a, kc_dm b WHERE a.cpid = b.cpid AND fgsid='?' AND dwtxid = '?' ORDER BY b.cpbm ";
  public static final String FIXED_SEARCH = "10009";
  private EngineDataSet dsBuyGoods  = new EngineDataSet();//主表
  //private EngineRow locateResult = null;

  private QueryBasic fixedQuery = null;
  private boolean isInitQuery = false; //是否已经初始化查询条件
  public  String retuUrl = null;
  private String fgsid = null;  //分公司ID
  /**
   * 得到往来单位信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回往来单位信息的实例
   */
  public static BuyGoodsSelect getInstance(HttpServletRequest request)
  {
    BuyGoodsSelect buyGoodsSelectBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "buyGoodsSelectBean";
      buyGoodsSelectBean = (BuyGoodsSelect)session.getAttribute(beanName);
      if(buyGoodsSelectBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsid = loginBean.getFirstDeptID();
        String qtyFormat  = loginBean.getQtyFormat();
        String priceFormat  = loginBean.getPriceFormat();
        buyGoodsSelectBean = new BuyGoodsSelect(fgsid);
        //buyGoodsSelectBean.dsBuyGoods.setColumnFormat("xsjzj", qtyFormat);
        //buyGoodsSelectBean.dsBuyGoods.setColumnFormat("xsj", qtyFormat);
        buyGoodsSelectBean.dsBuyGoods.setColumnFormat("bj", priceFormat);
        session.setAttribute(beanName, buyGoodsSelectBean);
      }
    }
    return buyGoodsSelectBean;
  }

  /**
   * 构造函数
   */
  private BuyGoodsSelect(String fgsid)
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
    setDataSetProperty(dsBuyGoods, null);

    addObactioner(String.valueOf(INIT), new Init());
    //addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());
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
    if(dsBuyGoods != null){
      dsBuyGoods.close();
      dsBuyGoods = null;
    }
    log = null;
  }
  /**
   *得到一行信息
   */
  public final RowMap getLookupRow(String cgbjId)
  {
    RowMap row = new RowMap();
    if(cgbjId == null || cgbjId.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsBuyGoods, "cgbjid");
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "cgbjid");
    locateRow.setValue(0, cgbjId);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
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
      String dwtxid = request.getParameter("dwtxid");
      String SQL = combineSQL(BUY_GOODS_SQL, "?", new String[]{fgsid, dwtxid});
      dsBuyGoods.setQueryString(SQL);
        dsBuyGoods.setRowMax(null);
    }
  }

  /**
   * 得到数据集对象
   * @return 返回数据集对象
   */
  public final EngineDataSet getOneTable()
  {
    return dsBuyGoods;
  }
}
