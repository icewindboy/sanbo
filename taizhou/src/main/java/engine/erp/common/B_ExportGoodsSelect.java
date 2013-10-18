package engine.erp.common;


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
import engine.util.log.Log;
import engine.util.MessageFormat;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 销售子系统_销售货物选择</p>
 * <p>Description: 销售子系统_销售货物选择</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

/**
 * 2004.4.16 修改仓库库存没有经营过该产品，可能不会出现在列表中
 */
public final class B_ExportGoodsSelect extends BaseAction implements Operate
{
  public  static final String PRODUCT_KIND_CHANGE = "10001";
  public  static final String PRODUCT_CHANGE      = "10002";
  public  static final String TURN_PAGE_CHANGE    = "10003";
  public  static final String SELECT_ALL_CHANGE   = "10004";

  //private static final String SALE_GOODS_STRUCT_SQL
  //    = "SELECT *, NULL kcsl, NULL sdsl, NULL kckgl FROM VW_XS_WZDJ WHERE 1<>1";
  private static final String SALE_GOODS_SQL
      = "SELECT t.*, h.kcsl, h.sdsl, (h.kcsl-h.sdsl) kckgl "
      + "FROM VW_XP_WZDJ t, vw_kc_product_collect h WHERE t.fgsid=h.fgsid(+) AND t.cpid=h.cpid(+) "
      + "{other} AND t.isnet={isnet} AND t.fgsid={fgsid} ORDER BY cpbm";
  //2004.4.16 修改仓库库存没有经营过该产品，可能不会出现在列表中的BUG
  private static final String SALE_GOODS_STORE_SQL
      = "SELECT t.*, h.kcsl, p.sdsl, (nvl(h.kcsl,0)-nvl(p.sdsl,0)) kckgl FROM "
      + "(SELECT t.* FROM VW_XPT_WZDJ t WHERE t.fgsid={fgsid} AND (t.storeid IS NULL OR t.storeid={storeid}) "
      + " UNION "
      + " SELECT t.* FROM (SELECT * FROM VW_XPT_WZDJ t WHERE t.fgsid={fgsid}   ) t, "
      + "   (SELECT DISTINCT k.cpid FROM kc_wzmx k WHERE (k.zl<>0 OR k.hszl<>0) AND k.storeid={storeid}) k"
      + "   WHERE t.cpid=k.cpid "
      + ") t, kc_kchz h, vw_product_lock p "
      + "WHERE t.storeid=h.storeid(+) AND t.cpid=h.cpid(+) "
      + "AND t.storeid=p.storeid(+) AND t.cpid=p.cpid(+) {other} AND t.isnet={isnet} ORDER BY t.cpbm";

  //2004.9.14 盛宇的含有客户折扣SQL
  /*
  private static final String CUST_SALE_GOODS_SQL
      = "SELECT t.*, decode(nvl(z.djlx, nvl(y.djlx, t.mrjg)), 'ccj', t.ccj, 'msj', t.msj, 'lsj', t.lsj, 'qtjg1', t.qtjg1, 'qtjg2', t.qtjg2, 'qtjg3', t.qtjg3, NULL) price, "
      + " nvl(z.zk, nvl(y.zk, t.oldzk)) mrzk, h.kcsl, h.sdsl, (h.kcsl-h.sdsl) kckgl "
      + "FROM VW_XPT_WZDJ t, vw_kc_product_collect h,"
      + " (SELECT z.cpid, z.djlx, z.zk FROM xs_khcpzk z WHERE z.fgsid={fgsid} AND z.dwtxid='{dwtxid}') z,"
      + " (SELECT a.cplx, a.zk, a.djlx FROM xs_khtyzk a WHERE a.xydj='{xydj}') y "
      + "WHERE t.abc=y.cplx(+) AND t.cpid=z.cpid(+) AND t.fgsid=h.fgsid(+) AND t.cpid=h.cpid(+) "
      + "{other} AND t.fgsid={fgsid} ORDER BY cpbm";
  */
  //2004.9.7  只有库存中有改产品也显示在列表中
  /*
  private static final String CUST_SALE_GOODS_STORE_SQL
      = "SELECT t.*, decode(nvl(z.djlx, nvl(y.djlx, t.mrjg)), 'ccj', t.ccj, 'msj', t.msj, 'lsj', t.lsj, 'qtjg1', t.qtjg1, 'qtjg2', t.qtjg2, 'qtjg3', t.qtjg3, NULL) price, "
      + " nvl(z.zk, nvl(y.zk, t.oldzk)) mrzk, h.kcsl, p.sdsl, (nvl(h.kcsl,0)-nvl(p.sdsl,0)) kckgl FROM "
      + "(SELECT t.*, t.storeid newstoreid FROM VW_XPT_WZDJ t WHERE t.fgsid={fgsid} AND (t.storeid IS NULL OR t.storeid={storeid})  AND (t.wbid IS NULL OR t.wbid={wbid})  "
      + " UNION "
      + " SELECT t.*, {storeid} FROM (SELECT * FROM VW_XPT_WZDJ t WHERE t.fgsid={fgsid}  AND (t.wbid IS NULL OR t.wbid={wbid}) ) t, "
      + "   (SELECT DISTINCT k.cpid FROM kc_wzmx k WHERE (k.zl<>0 OR k.hszl<>0) AND k.storeid={storeid}) k"
      + "   WHERE t.cpid=k.cpid "
      + ") t, (SELECT z.cpid, z.djlx, z.zk FROM xs_khcpzk z WHERE z.fgsid={fgsid} AND z.dwtxid='{dwtxid}') z, "
      + " kc_kchz h, vw_product_lock p, (SELECT a.cplx, a.zk, a.djlx FROM xs_khtyzk a WHERE a.xydj='{xydj}') y "
      + "WHERE t.abc=y.cplx(+) AND t.cpid=z.cpid(+) AND t.newstoreid=h.storeid(+) AND t.cpid=h.cpid(+) "
      + "AND t.newstoreid=p.storeid(+) AND t.cpid=p.cpid(+) {other} ORDER BY t.cpbm";
  */
  //客户信誉等级 (xydj)
  private static final String CUST_GRADE_SQL
      = "SELECT b.xydj FROM xs_khxyed b WHERE b.fgsid='?' AND b.dwtxid='?'";

  private String custName = "";//SYS_CUST_NAME

  private EngineDataSet dsSaleGoods  = new EngineDataSet();//主表
  public HtmlTableProducer table = new HtmlTableProducer(dsSaleGoods, "export_product_select", "t");

  public String[] inputName = null;    //
  public String[] fieldName = null;    //字段名称
  public String srcFrm=null;           //传递的原form的名称
  public boolean isMultiSelect = false;//是否是多选的
  public String multiIdInput = null;   //多选的ID组合串

  private String methodName = null;   //调用window.opener中的方法

  private String fgsid = null;  //分公司ID
  private String userid = null;  //登录人ID
  private String storeid = null; //仓库id
  //private String wbid = null; //外币id
  public String dwtxid = "";  //单位通讯ID
  private String xydj   = "";    //往来单位信誉等级
  private String isnet  = "0";   //是否来料加工
  public  boolean isCheckPromotion = false;
  public ArrayList wzdjlist = new ArrayList();

  /**
   * 得到往来单位信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回往来单位信息的实例
   */
  public static B_ExportGoodsSelect getInstance(HttpServletRequest request)
  {
    B_ExportGoodsSelect exportGoodsSelectBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "exportGoodsSelectBean";
      exportGoodsSelectBean = (B_ExportGoodsSelect)session.getAttribute(beanName);
      if(exportGoodsSelectBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsid = loginBean.getFirstDeptID();
        String qtyFormat  = loginBean.getQtyFormat();
        String priceFormat  = loginBean.getPriceFormat();
        exportGoodsSelectBean = new B_ExportGoodsSelect(fgsid, loginBean.getUserID());
        exportGoodsSelectBean.dsSaleGoods.setColumnFormat("xsjzj", qtyFormat);
        exportGoodsSelectBean.dsSaleGoods.setColumnFormat("xsj", qtyFormat);
        exportGoodsSelectBean.dsSaleGoods.setColumnFormat("kcsl", qtyFormat);
        exportGoodsSelectBean.dsSaleGoods.setColumnFormat("kckgl", qtyFormat);
        exportGoodsSelectBean.dsSaleGoods.setColumnFormat("xsdj", priceFormat);
        exportGoodsSelectBean.custName = loginBean.getSystemParam("SYS_CUST_NAME");
        session.setAttribute(beanName, exportGoodsSelectBean);
      }
    }
    return exportGoodsSelectBean;
  }

  /**
   * 构造函数
   */
  private B_ExportGoodsSelect(String fgsid, String userid)
  {
    this.fgsid = fgsid;
    this.userid = userid;
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
    setDataSetProperty(dsSaleGoods, null);

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());
    addObactioner(String.valueOf(PROD_CHANGE), new CodeSearch());
    addObactioner(String.valueOf(PROD_NAME_CHANGE), new NameSearch());
    addObactioner(PRODUCT_KIND_CHANGE, new KindChange());
    addObactioner(TURN_PAGE_CHANGE, new ProductChange());
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
    if(dsSaleGoods != null){
      dsSaleGoods.close();
      dsSaleGoods = null;
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
   * 初始化表单数据及其SQL语句
   * @return 返回子类的类名
   */
  private void init(RunData data) throws Exception
  {
    storeid = data.getParameter("storeid", "");
    //wbid = data.getParameter("wbid", "");
    isCheckPromotion = data.getParameter("check", "0").equals("1");
    dwtxid = data.getParameter("dwtxid", "");
    isnet = data.getParameter("isnet", "0");
    if("yuzhou".equals(custName))
    {
      xydj = dataSetProvider.getSequence(combineSQL(CUST_GRADE_SQL, "?", new String[]{fgsid, dwtxid}));
      xydj = xydj == null ? "" : xydj;
    }
    table.init(data.getRequest(), userid);
    //
    table.getWhereInfo().clearWhereValues();
    //得到关闭窗体前要调用的方法
    methodName = data.getParameter("method");
    srcFrm = data.getParameter("srcFrm");
    //是否是多选
    String multi = data.getParameter("multi");
    isMultiSelect = multi!=null && multi.equals("1");
    if(isMultiSelect){
      multiIdInput = data.getParameter("multiid");
    }

    inputName = data.getParameterValues("srcVar");
    fieldName = data.getParameterValues("fieldVar");
  }

  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //初始化查询项目和内容
      init(data);
      //替换可变字符串，组装SQL
      Hashtable table = new Hashtable();
      table.put("fgsid", fgsid);
      table.put("storeid", storeid);
      //table.put("wbid", wbid);
      table.put("other", "");
      table.put("dwtxid", dwtxid);
      table.put("xydj", xydj);
      table.put("isnet", isnet);
      //combineSQL(SALE_GOODS_SQL, "?", new String[]{"", fgsid});
      String SQL = null;
      if(storeid.length() == 0)
        SQL = MessageFormat.format(SALE_GOODS_SQL, table);
      else
        SQL = MessageFormat.format(SALE_GOODS_STORE_SQL, table);

      dsSaleGoods.setQueryString(SQL);
      dsSaleGoods.readyRefresh();
    }
  }

  /**
   * 通过产品编码得到产品信息的触发类
   */
  final class CodeSearch implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //初始化查询项目和内容
      init(data);
      //table.getWhereInfo().clearWhereValues();
      String cpbm = data.getParameter("code", "");
      table.getWhereInfo().putWhereValue("t$cpbm", cpbm);
      Hashtable table = new Hashtable();
      table.put("fgsid", fgsid);
      table.put("storeid", storeid);
      //table.put("wbid", wbid);
      table.put("other", "AND cpbm LIKE '"+cpbm+"%'");
      table.put("dwtxid", dwtxid);
      table.put("xydj", xydj);
      table.put("isnet", isnet);
      String SQL = null;
      if(storeid.length() == 0)
        SQL = MessageFormat.format(SALE_GOODS_SQL, table);
      else{
        SQL = MessageFormat.format(SALE_GOODS_STORE_SQL, table);
      }

      EngineDataSet ds = getOneTable();
      ds.setQueryString(SQL);
      if(ds.isOpen())
      {
        ds.readyRefresh();
        ds.refresh();
      }
      else
        ds.openDataSet();
    }
  }

  /**
   * 通过产品品名规格得到产品信息的触发类
   */
  final class NameSearch implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      init(data);
      String name = data.getParameter("name", "");
      table.getWhereInfo().putWhereValue("t$product", name);
      Hashtable table = new Hashtable();
      table.put("fgsid", fgsid);
      table.put("storeid", storeid);
      //table.put("wbid", wbid);
      table.put("other", "AND product LIKE '%"+name+"%'");
      table.put("dwtxid", dwtxid);
      table.put("xydj", xydj);
      table.put("isnet", isnet);
      String SQL = null;
      if(storeid.length() == 0)
        SQL = MessageFormat.format(SALE_GOODS_SQL, table);
      else{
        SQL = MessageFormat.format(SALE_GOODS_STORE_SQL, table);
      }

      EngineDataSet ds = getOneTable();
      ds.setQueryString(SQL);
      if(ds.isOpen())
      {
        ds.readyRefresh();
        ds.refresh();
      }
      else
        ds.openDataSet();
    }
  }

  /**
   * 选择产品
   */
  final class ProductChange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      String rownum = req.getParameter("rownum");
      if(action.equals(PRODUCT_CHANGE))
      {
      if(!rownum.equals("")&&!rownum.equals("undefined")){
        if(!wzdjlist.contains(rownum))
          wzdjlist.add(rownum);
        else
          wzdjlist.remove(rownum);
      }
      }
      else if(action.equals(TURN_PAGE_CHANGE))//翻页
      {
        String[] sels = req.getParameterValues("sel");
        String[] wzdjids = req.getParameterValues("wzdjid");
        if(wzdjids!=null&&wzdjids.length>0)
        {
          int lenth = wzdjids.length;
          for(int i=0;i<lenth;i++){
            String wzdjid = wzdjids[i];
            if(wzdjlist.contains(wzdjid))
              wzdjlist.remove(wzdjid);
          }
        }
        if(sels!=null&&sels.length>0)
        {
          int lenth = sels.length;
          for(int i=0;i<lenth;i++){
            String wzdjid = sels[i];
            if(!wzdjlist.contains(wzdjid))
              wzdjlist.add(wzdjid);
          }
        }
        int len=wzdjlist.size();
        for(int i=0;i<len;i++)
        {
          String aaa=wzdjlist.get(i).toString();
            if(wzdjlist.get(i).equals(""))
              wzdjlist.remove(i);
        }
      }
      else if(action.equals(SELECT_ALL_CHANGE))//全选
      {
        String[] wzdjids = req.getParameterValues("wzdjid");
        if(wzdjids!=null&&wzdjids.length>0)
        {
          int lenth = wzdjids.length;
          for(int i=0;i<lenth;i++){
            String wzdjid = wzdjids[i];
            if(!wzdjlist.contains(wzdjid))
              wzdjlist.add(wzdjid);
            else
              wzdjlist.remove(wzdjid);
          }
        }
      }
    }
  }
  /**
   * 产品类别变更的触发类
   */
  final class KindChange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String wzlbid = data.getParameter("wzlbid", "");
      table.getWhereInfo().clearWhereValues();
      table.getWhereInfo().putWhereValue("t$wzlbid", wzlbid);
      String SQL = wzlbid.length()==0 ? "" : "AND wzlbid="+wzlbid;
      Hashtable table = new Hashtable();
      table.put("fgsid", fgsid);
      table.put("storeid", storeid);
      //table.put("wbid", wbid);
      table.put("other", SQL);
      table.put("dwtxid", dwtxid);
      table.put("xydj", xydj);
      table.put("isnet", isnet);
      if(storeid.length() == 0)
        SQL = MessageFormat.format(SALE_GOODS_SQL, table);
      else{
        SQL = MessageFormat.format(SALE_GOODS_STORE_SQL, table);
      }

      getOneTable().setQueryString(SQL);
      getOneTable().setRowMax(null);
    }
  }

  /**
   *  查询操作
   */
  class Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      table.getWhereInfo().setWhereValues(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND " + SQL;
      Hashtable table = new Hashtable();
      table.put("fgsid", fgsid);
      table.put("storeid", storeid);
      //table.put("wbid", wbid);
      table.put("other", SQL);
      table.put("dwtxid", dwtxid);
      table.put("xydj", xydj);
      table.put("isnet", isnet);
      if(storeid.length() == 0)
        SQL = MessageFormat.format(SALE_GOODS_SQL, table);
      else{
        SQL = MessageFormat.format(SALE_GOODS_STORE_SQL, table);
      }

      dsSaleGoods.setQueryString(SQL);
      dsSaleGoods.setRowMax(null);
    }
  }

  /**
   * 得到数据集对象
   * @return 返回数据集对象
   */
  public final EngineDataSet getOneTable()
  {
    return dsSaleGoods;
  }

  /**
   * 得到需要调用window.opener中的方法的名称
   * @return 方法的名称
   */
  public String getMethodName()
  {
    if(methodName != null && methodName.length() == 0)
      return null;
    return methodName;
  }

  /**
   * 得到写日志的对象
   * @return 写日志的对象
   */
  public Log getLog()
  {
    return log;
  }

  /**
   * 根据物资单价ID得到选中的物资单价ID，批次号，合同编号等信息.此方法用得到列表的选择后的行的信息，不能用于显示
   * @param wzdjid 物资单价ID
   * @return 选中的信息
   */
  public final RowMap getSelectedRow(String exwzdjid) throws Exception
  {
    RowMap row = new RowMap();
    if(exwzdjid == null || exwzdjid.length() == 0)
      return row;
    EngineRow locateRow = new EngineRow(dsSaleGoods, "exwzdjid");
    locateRow.setValue(0, exwzdjid);
    if(dsSaleGoods.locate(locateRow, Locate.FIRST))
      row.put(dsSaleGoods);
    return row;
  }
}