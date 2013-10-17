package engine.erp.sale.xixing;

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
 * <p>Title: 销售管理-产品销售定价</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @version 1.0
 */
public final class B_ProductPrice extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1009";
  /**
   *默认状态是不能修改
   * */
  private boolean canModify = false;
  /**
   *对应修改按钮
   * */
  public static final String MODIFY = "1005";
  /**
   * 提取物资销售单价信息的SQL语句
   */
  private static final String PRODUCT_PRICE_SQL = "SELECT * FROM xs_wzdj WHERE fgsid=? ? ";//
  private static final String PRODUCT_LIST_SQL = "SELECT * FROM vw_sale_product_price WHERE (fgsid=? OR fgsid IS NULL) ? ORDER BY cpbm";
  /**
   *本地数据集
   * 保存物资销售单价信息的数据集
   */
  private EngineDataSet dsProductPriceList = new EngineDataSet();
  private EngineDataSet dsProductPrice = new EngineDataSet();
  private ArrayList d_RowInfos = null; //多行记录的引用
  public  String retuUrl = null;//点击返回按钮的URL
  private boolean isInitQuery = false;
  public  String loginName = ""; //登录员工的姓名
  public  String personid = ""; //登录员工personid
  private String fgsID = null;   //分公司ID


  private QueryFixedItem fixedQuery = new QueryFixedItem();//定义固定查询类
  /**
   * 得到物资销售单价信息的实例
   * @param request jsp请求
   * @return 返回物资销售单价信息的实例
   */
  public static B_ProductPrice getInstance(HttpServletRequest request)
  {
    B_ProductPrice b_ProductPriceBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_ProductPriceBean";//名称-值对应
      b_ProductPriceBean = (B_ProductPrice)session.getAttribute(beanName);
      if(b_ProductPriceBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsID = loginBean.getFirstDeptID();
        b_ProductPriceBean = new B_ProductPrice(fgsID);//调用构造函数
        b_ProductPriceBean.loginName = loginBean.getUserName();
        b_ProductPriceBean.personid=loginBean.getUserID();
        session.setAttribute(beanName,b_ProductPriceBean);
      }
    }
    return b_ProductPriceBean;
  }
  /**
   * 构造函数(实例变量:分公司ID为初始化参数)
   */
  private B_ProductPrice(String fgsid)
  {
    this.fgsID = fgsid;
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
    //初始化数据集的SQL语句.
    setDataSetProperty(dsProductPriceList, null);
    setDataSetProperty(dsProductPrice, null);
    dsProductPrice.setSequence(new SequenceDescriptor(new String[]{"wzdjid"}, new String[]{"s_xs_wzdj"}));
    //dsProductPriceList数据集在数据装载完后,才能对其执行操作(装载完发出通告)
    dsProductPriceList.addLoadListener(new com.borland.dx.dataset.LoadListener() {
      public void dataLoaded(LoadEvent e)
      {
        initRowInfo(false, true);
      }
    });
    //添加操作的触发对象
    addObactioner(String.valueOf(INIT), new B_ProductPrice_Init());//初始化
    addObactioner(String.valueOf(POST), new B_ProductPrice_Post());//保存
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());//查询
    addObactioner(String.valueOf(MODIFY), new Modify());
  }
  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      String opearate = request.getParameter(OPERATE_KEY);
      if(opearate != null && opearate.trim().length() > 0)
      {
        RunData data = notifyObactioners(opearate, request, response, null);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsProductPrice.changesPending())
        dsProductPrice.reset();
      if(dsProductPriceList.changesPending())
          dsProductPriceList.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsProductPriceList != null){
      dsProductPriceList.close();
      dsProductPriceList = null;
    }
    log = null;
  }
  /**
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = new RowMap();
    //保存网页的所有信息
    rowInfo.put(request);
    //从数据集中获取记录行数
    if(!dsProductPriceList.isOpen())
    {
      dsProductPriceList.open();
    }
    String czrq = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    int rownum = dsProductPriceList.getRowCount();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("ccj", rowInfo.get("ccj_"+i));//
      detailRow.put("msj", rowInfo.get("msj_"+i));//
      detailRow.put("lsj", rowInfo.get("lsj_"+i));//
      detailRow.put("qtjg1", rowInfo.get("qtjg1_"+i));//
      detailRow.put("qtjg2", rowInfo.get("qtjg2_"+i));//
      detailRow.put("wzdjid", rowInfo.get("wzdjid_"+i));//物资单价ID
      detailRow.put("qtjg3", rowInfo.get("qtjg3_"+i));//
      detailRow.put("mrjg", rowInfo.get("mrjg_"+i));
      detailRow.put("mrzk", rowInfo.get("mrzk_"+i));
      detailRow.put("czy", loginName);
      detailRow.put("czrq", czrq);


      d_RowInfos.set(i,detailRow);
    }
  }
  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
  /*得到状态*/
  public final boolean getState()
  {
    return canModify;
  }
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
   */
  private final void initRowInfo(boolean isAdd, boolean isInit)
  {
    StringBuffer buf = null;
    if(d_RowInfos == null)
      d_RowInfos = new ArrayList(dsProductPriceList.getRowCount());
    else if(isInit)
      d_RowInfos.clear();

    dsProductPriceList.first();
    for(int i=0; i<dsProductPriceList.getRowCount(); i++)
    {
      if(buf == null)
        buf = new StringBuffer("AND cpid IN(").append(dsProductPriceList.getValue("cpid"));
      else
        buf.append(",").append(dsProductPriceList.getValue("cpid"));
      RowMap row = new RowMap(dsProductPriceList);
      d_RowInfos.add(row);
      dsProductPriceList.next();
    }

    if(buf == null)
      buf =new StringBuffer();
    else
      buf.append(")");

    String SQL = combineSQL(PRODUCT_PRICE_SQL, "?", new String[]{fgsID, buf.toString()});
    dsProductPrice.setQueryString(SQL);
    if(dsProductPrice.isOpen())
      dsProductPrice.refresh();
    else
      dsProductPrice.openDataSet();
  }
  /*得到表对象*/
  public final EngineDataSet getDetailTable()
  {
    return dsProductPriceList;
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
   * 初始化操作的触发类
   */
  class B_ProductPrice_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      canModify= false;
      dsProductPriceList.setQueryString(combineSQL(PRODUCT_LIST_SQL,"?",new String[]{fgsID,""}));
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      fixedQuery.getSearchRow().clear();
      //data.setMessage(showJavaScript("showFixedQuery()"));
    }
  }
  /**
   * 添加查询操作的触发类
   */
  class FIXED_SEARCH implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      initQueryItem(data.getRequest());
      QueryBasic queryBasic = fixedQuery;
      queryBasic.setSearchValue(data.getRequest());
      String SQL = queryBasic.getWhereQuery();

      String ccj = request.getParameter("ccj");
      String msj = request.getParameter("msj");
      String lsj = request.getParameter("lsj");
      //String qtjg1 = request.getParameter("qtjg1");
      //String qtjg2 = request.getParameter("qtjg2");
      //String qtjg3 = request.getParameter("qtjg3");


      if(SQL.length() > 0)
        SQL = " AND " + SQL;
      if(ccj!=null)
        SQL = SQL+" AND ccj is null ";
      if(msj!=null)
        SQL = SQL+" AND msj is null ";
      if(lsj!=null)
        SQL = SQL+" AND lsj is null ";
      /*
      if(qtjg1!=null)
        SQL = SQL+" AND qtjg1 is null ";
      if(qtjg2!=null)
        SQL = SQL+" AND qtjg2 is null ";
      if(qtjg3!=null)
        SQL = SQL+" AND qtjg3 is null ";
      */


      SQL = combineSQL(PRODUCT_LIST_SQL, "?", new String[]{fgsID, SQL}) ;
      if(!dsProductPriceList.getQueryString().equals(SQL))
      {
        dsProductPriceList.setQueryString(SQL);
        dsProductPriceList.setRowMax(null);
      }
    }

    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet master = dsProductPriceList;
      //初始化固定的查询项目
      //往来单位dwtxId;信誉额度xyed;信誉等级xydj;回款天数hkts;
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("cpid"), "VW_SALE_WZDJ", "cpid", "prod", "prod", "like"),//从表品名
        new QueryColumn(master.getColumn("ccj"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("ccj"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("msj"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("msj"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("lsj"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("lsj"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("mrzk"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("mrzk"), null, null, null, "b", "<=")

      });
      isInitQuery = true;
    }
  }
  /**
   *点击修改按钮时触发的操作
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
   * 保存操作的触发类
   */
  class B_ProductPrice_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String czrq = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      RowMap detailrow = null;
      putDetailInfo(data.getRequest());
      String temp = null;
      if((temp = checkDetailInfo()) !=null)
      {
        data.setMessage(temp);
        return;
      }

      EngineRow row = new EngineRow(dsProductPrice, "cpid");
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String cpid = detailrow.get("cpid");

        String ccj = detailrow.get("ccj");
        String msj  = detailrow.get("msj");
        String lsj = detailrow.get("lsj");
        String qtjg1 = detailrow.get("qtjg1");
        String qtjg2 = detailrow.get("qtjg2");
        String qtjg3 = detailrow.get("qtjg3");
        String mrjg = detailrow.get("mrjg");
        String mrzk = detailrow.get("mrzk");

        dsProductPriceList.setValue("cpid", cpid);

        dsProductPriceList.setValue("ccj", ccj);
        dsProductPriceList.setValue("msj", msj);
        dsProductPriceList.setValue("lsj", lsj);
        dsProductPriceList.setValue("qtjg1", qtjg1);
        dsProductPriceList.setValue("qtjg2", qtjg2);
        dsProductPriceList.setValue("qtjg3", qtjg3);
        dsProductPriceList.setValue("mrjg", "lsj");
        dsProductPriceList.setValue("mrzk", mrzk);
        dsProductPriceList.setValue("fgsID",fgsID);
        dsProductPriceList.setValue("czrq",czrq);
        dsProductPriceList.setValue("czy",loginName);
        dsProductPriceList.setValue("czyID",personid);

        if( lsj.length() > 0)
        {
          row.setValue(0, cpid);
          if(!dsProductPrice.locate(row, Locate.FIRST))
          {
            dsProductPrice.insertRow(false);
            dsProductPrice.setValue("wzdjid","-1");
          }
          dsProductPrice.setValue("cpid", cpid);
          dsProductPrice.setValue("ccj", ccj);
          dsProductPrice.setValue("msj", msj);
          dsProductPrice.setValue("lsj", lsj);
          dsProductPrice.setValue("qtjg1", qtjg1);
          dsProductPrice.setValue("qtjg2", qtjg2);
          dsProductPrice.setValue("qtjg3", qtjg3);
          dsProductPrice.setValue("mrjg",  "lsj");
          dsProductPrice.setValue("mrzk", mrzk);
          dsProductPrice.setValue("fgsID",fgsID);
          dsProductPrice.setValue("czrq",czrq);
          dsProductPrice.setValue("czy",loginName);
          dsProductPrice.setValue("czyID",personid);
          dsProductPrice.post();

          dsProductPriceList.setValue("czrq",czrq);
          dsProductPriceList.setValue("czy",loginName);
          dsProductPriceList.setValue("czyID",personid);
        }
        dsProductPriceList.post();
      }
      dsProductPrice.saveChanges();
      dsProductPriceList.resetPendingStatus(true);
      canModify=false;
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String lsj = detailrow.get("lsj");
        if(lsj.length()!=0)
        {
        if((temp = checkNumber(lsj, "零售价")) != null)
          return temp;
        }
      }
      return null;
    }
  }
  /**
   * 检测字符串是否是数字型的
   * @param value 需要检测的字符串
   * @param caption javascipt需要显示的标题
   * @return 若返回null表示是数字，非null为javasrcip语句
   */
  public static String checkNumber(String value, String caption)
  {
    try{
      Double.parseDouble(value);
    }catch(Exception ex){
      return showJavaScript("alert('非法 "+caption+"！');");
    }
    return null;
  }
}
