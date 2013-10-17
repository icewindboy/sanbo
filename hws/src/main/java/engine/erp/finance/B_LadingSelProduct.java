package engine.erp.finance;

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
 * <p>Title: 销售子系统_销售提单选择产品</p>
 * <p>Description: 销售子系统_销售提单选择产品</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public final class B_LadingSelProduct extends BaseAction implements Operate
{
  private static final String LADING_GOODS_STRUCT_SQL = "SELECT * FROM VW_LADING_SEL_PRODUCT WHERE 1<>1";
  private static final String LADING_GOODS_SQL = "SELECT * FROM VW_LADING_SEL_PRODUCT WHERE isnet=0 AND fgsid=? ? AND (storeid='?' OR storeid IS NULL) ORDER BY cpbm";

  private EngineDataSet dsLadingGoods  = new EngineDataSet();//主表
  private EngineRow locateResult = null;

  private QueryBasic fixedQuery = new QueryFixedItem();
  private boolean isInitQuery = false;
  public  String retuUrl = null;
  private String fgsid = null;  //分公司ID
  private String storeid = null;//仓库ID
  /**
   * 得到往来单位信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回往来单位信息的实例
   */
  public static B_LadingSelProduct getInstance(HttpServletRequest request)
  {
    B_LadingSelProduct ladingSelProductBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "ladingSelProductBean";
      ladingSelProductBean = (B_LadingSelProduct)session.getAttribute(beanName);
      if(ladingSelProductBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsid = loginBean.getFirstDeptID();
        String qtyFormat  = loginBean.getQtyFormat();
        String priceFormat  = loginBean.getPriceFormat();
        ladingSelProductBean = new B_LadingSelProduct(fgsid);
        ladingSelProductBean.dsLadingGoods.setColumnFormat("xsjzj", qtyFormat);
        ladingSelProductBean.dsLadingGoods.setColumnFormat("xsj", qtyFormat);
        ladingSelProductBean.dsLadingGoods.setColumnFormat("kcsl", qtyFormat);
        ladingSelProductBean.dsLadingGoods.setColumnFormat("kckgl", qtyFormat);
        ladingSelProductBean.dsLadingGoods.setColumnFormat("xsdj", priceFormat);
        session.setAttribute(beanName, ladingSelProductBean);
      }
    }
    return ladingSelProductBean;
  }

  /**
   * 构造函数
   */
  private B_LadingSelProduct(String fgsid)
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
    setDataSetProperty(dsLadingGoods, LADING_GOODS_STRUCT_SQL);

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());
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
    if(dsLadingGoods != null){
      dsLadingGoods.close();
      dsLadingGoods = null;
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
   * 初始化查询的各个列
   * @param request web请求对象
   */
  private void initQueryItem(HttpServletRequest request)
  {
    if(isInitQuery)
      return;
    EngineDataSet ds = dsLadingGoods;
    if(!ds.isOpen())
      ds.open();

    //初始化固定的查询项目
    fixedQuery.addShowColumn("", new QueryColumn[]{//"",表示默认的表名
      new QueryColumn(ds.getColumn("cpbm"), null, null, null),
      new QueryColumn(ds.getColumn("th"), null, null, null),
      new QueryColumn(ds.getColumn("cpid"), null, null, null),
      new QueryColumn(ds.getColumn("txm"), null, null, null),
      new QueryColumn(ds.getColumn("zjm"), null, null, null)
    });
    isInitQuery = true;
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
      fixedQuery.getSearchRow().clear();
      //仓库id
      /*String curID = request.getParameter("curID");
      if(curID == null)
      {
        data.setMessage(showJavaScript("alert('请先选择仓库！')"));
        return;
      }
      curID = curID.trim();
      try{
        Integer.parseInt(curID);
      }
      catch(Exception ex){
        data.setMessage(showJavaScript("alert('请选择的仓库非法！')"));
        return;
      }*/
      storeid = request.getParameter("curID").trim();;
      //替换可变字符串，组装SQL
      String SQL = combineSQL(LADING_GOODS_SQL, "?", new String[]{fgsid, "", storeid});
      dsLadingGoods.setQueryString(SQL);
      dsLadingGoods.setRowMax(null);
    }
  }

  /**
   *  查询操作
   */
  class Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      QueryBasic queryBasic = fixedQuery;
      queryBasic.setSearchValue(data.getRequest());
      String SQL = queryBasic.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND " + SQL;
      //替换可变字符串，组装SQL
      SQL = combineSQL(LADING_GOODS_SQL, "?", new String[]{fgsid, SQL});
      if(!dsLadingGoods.getQueryString().equals(SQL))
      {
        dsLadingGoods.setQueryString(SQL);
        dsLadingGoods.setRowMax(null);
      }
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
   * 得到数据集对象
   * @return 返回数据集对象
   */
  public final EngineDataSet getOneTable()
  {
    return dsLadingGoods;
  }

  /**
   * 根据物资单价ID得到选中的物资单价ID，批次号，合同编号等信息.此方法用得到列表的选择后的行的信息，不能用于显示
   * @param wzdjid 物资单价ID
   * @return 选中的信息
   */
  public final RowMap getSelectedRow(String wzdjid) throws Exception
  {
    RowMap row = new RowMap();
    if(wzdjid == null || wzdjid.length() == 0)
      return row;
    EngineRow locateRow = new EngineRow(dsLadingGoods, "wzdjid");
    locateRow.setValue(0, wzdjid);
    if(dsLadingGoods.locate(locateRow, Locate.FIRST))
      row.put(dsLadingGoods);
    return row;
  }
}
