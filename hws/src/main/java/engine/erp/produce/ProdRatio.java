package engine.erp.produce;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import java.util.*;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.project.*;
import java.text.SimpleDateFormat;
import engine.html.HtmlTableProducer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: </p>
 * <p>Description: 基础信息维护--报价列表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author liqiang
 * @version 1.0
 */

public final class ProdRatio extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1009";
  //public static final String WB_ONCHANGE = "1008";
  /**
   * 提取报价资料信息的SQL语句
   */
  private static final String ProdRatio_SQL = //"SELECT * FROM xs_area_price where fgsid=? ? ORDER BY cgbjID DESC";//
      "SELECT sc_prod_ratio.* FROM sc_prod_ratio WHERE 1=1  ?  ORDER BY sc_prod_ratio.ratio_code";
  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_prod_ratio where 1<>1";
  private static final String MASTER_SQL = "SELECT * FROM sc_prod_ratio ";

  /**
   * 保存报价资料信息的数据集
   */
  private EngineDataSet dsProdRatio = new EngineDataSet();

  private LookUp foreignBean =null;//外币信息bean

  public  HtmlTableProducer table = new HtmlTableProducer(dsProdRatio, "sc_prod_ratio", "sc_prod_ratio");
  /**
   * 用于定位数据集
   */
  private EngineRow locateRow = null;

  /**
   * 保存用户输入的信息
   */
  private RowMap rowInfo = new RowMap();

  /**
   * 是否在添加状态
   */
  public  boolean isAdd = true;

  /**
   * 保存修改操作的行记录指针
   */
  private long    editrow = 0;

  /**
   * 点击返回按钮的URL
   */
  public  String retuUrl = null;
  public  String loginId = "";   //登录员工的ID
  public  String loginName = ""; //登录员工的姓名
  private String fgsid = null;   //分公司ID
  /**
  * 定义固定查询类
   */
  //private QueryBasic fixedQuery = new QueryFixedItem();
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  /**
   * 得到报价资料信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回报价资料信息的实例
   */
  public static ProdRatio getInstance(HttpServletRequest request)
  {
    ProdRatio prodRatioBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "prodRatioBean";
      prodRatioBean = (ProdRatio)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(prodRatioBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        prodRatioBean = new ProdRatio();
        prodRatioBean.fgsid = loginBean.getFirstDeptID();
        prodRatioBean.loginId = loginBean.getUserID();
        prodRatioBean.loginName = loginBean.getUserName();
        prodRatioBean.priceFormat  = loginBean.getPriceFormat();
        //设置格式化的字段

        session.setAttribute(beanName, prodRatioBean);
      }
    }
    return prodRatioBean;
  }
  /**
   * 构造函数
   */
  private ProdRatio()
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
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsProdRatio, MASTER_STRUT_SQL);
    //dsProdRatio.setSequence(new SequenceDescriptor(new String[]{"cgbjid"}, new String[]{"s_cg_bj"}));
    dsProdRatio.setTableName("sc_prod_ratio");
    //添加操作的触发对象
    ProdRatio_Add_Edit add_edit = new ProdRatio_Add_Edit();
    addObactioner(String.valueOf(INIT), new ProdRatio_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new ProdRatio_Post());
    addObactioner(String.valueOf(DEL), new ProdRatio_Delete());
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());
    //addObactioner(String.valueOf(WB_ONCHANGE), new Wb_Onchange());
  }

  //----Implementation of the BaseAction abstract class
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
      if(dsProdRatio.isOpen() && dsProdRatio.changesPending())
        dsProdRatio.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  //----Implementation of the BaseAction abstract class
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsProdRatio != null){
      dsProdRatio.close();
      dsProdRatio = null;
    }
    log = null;
    rowInfo = null;
    locateRow = null;
  }

  //----Implementation of the BaseAction abstract class
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
  {
    return getClass();
  }
  /**
   * 初始化行信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();

    if(!isAdd)
      rowInfo.put(getOneTable());
  }

  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsProdRatio.isOpen())
      dsProdRatio.open();
    return dsProdRatio;
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo()
  {
    return rowInfo;
  }

  /**
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }
     */

  //------------------------------------------
  //操作实现的类
  //------------------------------------------
  /**
   * 初始化操作的触发类
   */
  class ProdRatio_Init implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发初始化操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //初始化时清空数据集
      //RowMap row = fixedQuery.getSearchRow();
      //row.clear();
      //table.getWhereInfo().clearWhereValues();
      dsProdRatio.setQueryString(MASTER_STRUT_SQL);
      if(dsProdRatio.isOpen() && dsProdRatio.getRowCount() > 0)
        dsProdRatio.empty();
      dsProdRatio.setRowMax(null);
     // data.setMessage(showJavaScript("showFixedQuery()"));//初始化弹出查询界面
    }
  }
  /**
   * 添加查询操作的触发类
   */
  class FIXED_SEARCH implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 添加或修改的触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      table.getWhereInfo().setWhereValues(data.getRequest());
      //fixedQuery.setSearchValue(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(ProdRatio_SQL, "?", new String[]{SQL});
      dsProdRatio.setQueryString(SQL);
      dsProdRatio.setRowMax(null);
    }

    /**
     * 初始化查询的各个列
     * @param request

    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      fixedQuery = new QueryFixedItem();
      EngineDataSet master = dsProdRatio;
      if(!master.isOpen())
        master.open();

      //初始化固定的查询项目
      fixedQuery.addShowColumn("cg_bj", new QueryColumn[]{
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("cgbjid"), "VW_BUY_PRICE", "cgbjid", "product","product","like"),
        new QueryColumn(master.getColumn("cgbjid"), "VW_BUY_PRICE", "cgbjid", "cpbm","cpbm","like"),
        new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("ksrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("ksrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("jsrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("jsrq"), null, null, null, "b", "<="),
        //new QueryColumn(master.getColumn("sfhssl"), null, null, null, null, "="),
      });
      isInitQuery = true;
    }
         */
  }
  /**
   * 添加或修改操作的触发类
   */
  class ProdRatio_Add_Edit implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 添加或修改的触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsProdRatio.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsProdRatio.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }
  /**
   *选择外币触发事件
   */
  /*class Wb_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      rowInfo.put(req);

      String wbid = rowInfo.get("wbid");
      String bj = rowInfo.get("bj");
      RowMap foreignRow = getForeignBean(req).getLookupRow(wbid);
      String hl = foreignRow.get("hl");
      double curhl = hl.length()>0 ? Double.parseDouble(hl) : 0 ;
      double curbj = bj.length()>0 ? Double.parseDouble(bj) : 0;
      double wbbj = curhl==0 ? 0 : curbj/curhl;
      rowInfo.put("wbid",wbid);
      rowInfo.put("hl",hl);
      rowInfo.put("wbbj", formatNumber(String.valueOf(wbbj), priceFormat) );
    }
  }

  /**
   * 保存操作的触发类
   */
  class ProdRatio_Post implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发保存操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
      rowInfo.put(data.getRequest());
      String ratio_code = rowInfo.get("ratio_code");

      String ratio_name  = rowInfo.get("ratio_name");

      String cpid = rowInfo.get("cpid");
      String kc_cpId = rowInfo.get("kc__cpid");



      if(isAdd)
        ds.insertRow(false);

      ds.setValue("ratio_code",ratio_code);

      //ds.setValue("sfhssl",sfhssl);
      ds.setValue("ratio_name",ratio_name);
      ds.setValue("cpid", rowInfo.get("cpid"));
       ds.setValue("kc__cpid", rowInfo.get("kc__cpid"));

      ds.post();
      ds.saveChanges();
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }

  /**
   * 删除操作的触发类
   */
  class ProdRatio_Delete implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发删除操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.deleteRow();
      ds.saveChanges();
    }
  }

}
