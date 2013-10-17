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
import engine.erp.baseinfo.BasePublicClass;
import engine.util.StringUtils;

import java.util.Map;
import java.util.Hashtable;
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
 * <p>Title: 生产子系统_生产计划维护选择合同货物</p>
 * <p>Description: 生产子系统_生产计划维护选择合同货物</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 杨建国
 * @version 1.0
 */

public final class PlanSelectSaleDetail extends BaseAction implements Operate
{
  private static final String IMPORT_ORDER_SQL
      = "SELECT * FROM VW_PRODUCEPLAN_SEL_SALE WHERE zt<>8 AND fgsid='?' ? ";
  private static final String LOOK_ORDER_SQL
      = "SELECT * FROM VW_PRODUCEPLAN_SEL_SALE WHERE fgsid='?' ?";
  //查询出来的物料类别属性
  private static final String PROPERTY_SQL
      = "SELECT DISTINCT t.sxmc,t.sxlx FROM kc_dmlbsx t WHERE t.wzlbid IN(?)";
  private static final String PROPERTY_SQL2
      = "SELECT DISTINCT t.sxmc,t.sxlx FROM kc_dmlbsx t WHERE 1<>1";
  //更新提单货物是否生产的SQL
  private static final String UPDATE_ORDER_SQL
      = "UPDATE xs_hthw SET isproduce=? WHERE hthwid=? ";

  public static final String NEED_PRODUCE = "10001";
  public static final String NOT_PRODUCE  = "10002";

  private EngineDataSet dsPlanSelectSale  = new EngineDataSet();
  private EngineDataSet dsproperty = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private String method = null;   //方法
  private RowMap searchRow = new RowMap();//保存查询条件
  private String jhlx =null;
  /**
   * 定义固定查询类
   */
  private QueryBasic fixedQuery = new QueryFixedItem();
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……

  /**
   * 得到销售合同货物信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回采购合同信息的实例
   */
  public static PlanSelectSaleDetail getInstance(HttpServletRequest request)
  {
    PlanSelectSaleDetail planSelectSaleBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "planSelectSaleBean";
      planSelectSaleBean = (PlanSelectSaleDetail)session.getAttribute(beanName);
      if(planSelectSaleBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        planSelectSaleBean = new PlanSelectSaleDetail();
        planSelectSaleBean.fgsid = loginBean.getFirstDeptID();
        planSelectSaleBean.qtyFormat = loginBean.getQtyFormat();
        planSelectSaleBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……

        planSelectSaleBean.dsPlanSelectSale.setColumnFormat("sl", planSelectSaleBean.qtyFormat);
        session.setAttribute(beanName, planSelectSaleBean);
      }
    }
    return planSelectSaleBean;
  }

  /**
   * 构造函数
   */
  private PlanSelectSaleDetail()
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
    setDataSetProperty(dsPlanSelectSale, null);
    setDataSetProperty(dsproperty, null);
    /**
    dsPlanSelectSale.addLoadListener(new com.borland.dx.dataset.LoadListener() {
       public void dataLoaded(LoadEvent e) {
         for(int i=0; i<dsPlanSelectSale.getRowCount(); i++)
         {
           double scdwgs = dsPlanSelectSale.getValue("scdwgs").length()>0 ? Double.parseDouble(dsPlanSelectSale.getValue("scdwgs")) : 1;
           double wjhsl = dsPlanSelectSale.getValue("wjhsl").length()>0 ? Double.parseDouble(dsPlanSelectSale.getValue("sl")) : 0;
           String sxz = dsPlanSelectSale.getValue("sxz");
           String width = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP, "()");
           double d_width = width.equals("0") ? 1 : Double.parseDouble(width);
           double scsl=0;
           if(d_width==0)
             scsl =wjhsl;
           else
             scsl = wjhsl*scdwgs/d_width;
           dsPlanSelectSale.setValue("scsl", formatNumber(String.valueOf(scsl), qtyFormat));
           dsPlanSelectSale.post();
           dsPlanSelectSale.next();
         }
       }
     }
    );
    */
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());
    addObactioner(NEED_PRODUCE, new ChangeProduce());
    addObactioner(NOT_PRODUCE, new ChangeProduce());
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
    if(dsPlanSelectSale != null){
      dsPlanSelectSale.close();
      dsPlanSelectSale = null;
    }
    log = null;
    searchRow = null;
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
   * 变更是否生产
   */
  class ChangeProduce implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String rownum = data.getParameter("rownum");
      int row = Integer.parseInt(rownum);
      dsPlanSelectSale.goToRow(row);
      String hthwid = dsPlanSelectSale.getValue("hthwid");
      String isproduce = NEED_PRODUCE.equals(action) ? "1" : "0";
      String sql = combineSQL(UPDATE_ORDER_SQL, "?", new String[]{isproduce, hthwid});
      dsPlanSelectSale.updateQuery(new String[]{sql});
      dsPlanSelectSale.readyRefresh();
    }
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
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      searchRow.clear();
      String SQL=null;
      jhlx = request.getParameter("jhlx");
      if(jhlx.equals("0"))
      {
        String where = " AND isproduce=1 ";
        fixedQuery.getSearchRow().put("isproduce", "1");
        SQL = combineSQL(IMPORT_ORDER_SQL, "?", new String[]{fgsid,where});
      }
      else if(jhlx.equals("1"))
      {
        String where = " AND sxz LIKE '%"+SYS_PRODUCT_SPEC_PROP+"%'";
        SQL = combineSQL(IMPORT_ORDER_SQL, "?", new String[]{fgsid,where});
      }
      //查看销售合同
      else if(jhlx.equals("3"))
        SQL = combineSQL(LOOK_ORDER_SQL, "?", new String[]{fgsid});
      dsPlanSelectSale.setQueryString(SQL);
      if(dsPlanSelectSale.isOpen())
        dsPlanSelectSale.refresh();
      else
        dsPlanSelectSale.openDataSet();
      dsPlanSelectSale.first();
      StringBuffer propertyBuf= new StringBuffer();
      for(int i=0; i<dsPlanSelectSale.getRowCount(); i++)
      {
        String wzlbid = dsPlanSelectSale.getValue("wzlbid");
        if(i==dsPlanSelectSale.getRowCount()-1)
         propertyBuf = propertyBuf.append("'").append(wzlbid).append("'");
        else
          propertyBuf = propertyBuf.append("'").append(wzlbid).append("',");
        double scdwgs = dsPlanSelectSale.getValue("scdwgs").length()>0 ? Double.parseDouble(dsPlanSelectSale.getValue("scdwgs")) : 1;
        double wjhsl = dsPlanSelectSale.getValue("wjhsl").length()>0 ? Double.parseDouble(dsPlanSelectSale.getValue("sl")) : 0;
        String sxz = dsPlanSelectSale.getValue("sxz");
        String width = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP, "()");
        double d_width = width.equals("0") ? 1 : Double.parseDouble(width);
        double scsl=0;
        if(d_width==0)
          scsl =wjhsl;
        else
          scsl = wjhsl*scdwgs/d_width;
        dsPlanSelectSale.setValue("scsl", formatNumber(String.valueOf(scsl), qtyFormat));
        dsPlanSelectSale.post();
        dsPlanSelectSale.next();
      }
      String scope = propertyBuf.toString();
      String wzlbidsql =null;
      if(!scope.equals(""))
        wzlbidsql = combineSQL(PROPERTY_SQL,"?", new String[]{scope});
      else
        wzlbidsql = PROPERTY_SQL2;
      dsproperty.setQueryString(wzlbidsql);
      if(dsproperty.isOpen())
        dsproperty.refresh();
      else
        dsproperty.openDataSet();
    }
  }
  /*
  *得到一行信息
  */
  public final RowMap getLookupRow(String hthwid)
  {
    RowMap row = new RowMap();
    if(hthwid == null || hthwid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsPlanSelectSale, "hthwid");
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "hthwid");
    locateRow.setValue(0, hthwid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  /**
   *  查询操作
   */
  class Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      if(jhlx.equals("3"))
        SQL = combineSQL(LOOK_ORDER_SQL, "?", new String[]{fgsid,SQL});
      else
        SQL = combineSQL(IMPORT_ORDER_SQL, "?", new String[]{fgsid,SQL});
      dsPlanSelectSale.setQueryString(SQL);
      if(dsPlanSelectSale.isOpen())
        dsPlanSelectSale.refresh();
      else
        dsPlanSelectSale.openDataSet();
      searchRow.put(data.getRequest());
      //EngineDataSet ds = dsMaterail;
      EngineDataSet ds = dsproperty;
      Hashtable propValues = new Hashtable();//用于保存属性值的查询条件
      ds.first();
      for(int i=0; i<ds.getRowCount(); i++)
      {
        //校验数组型的
        String sxmc = ds.getValue("sxmc");
        if(ds.getValue("sxlx").equals("number"))
        {
          String min = searchRow.get("sxmc_"+i+"_min");
          String max = searchRow.get("sxmc_"+i+"_max");
          BigDecimal[] inputNums = new BigDecimal[2];
          if(min.length() > 0)
          {
            if(!isDouble(min)){
              data.setMessage(showJavaScript("alert('输入的"+ sxmc +"属性最小值不是数字型的！')"));
              return;
            }
            inputNums[0] = new BigDecimal(min);
            propValues.put(sxmc, inputNums);
          }

          if(max.length() > 0)
          {
            if(!isDouble(max))
            {
              data.setMessage(showJavaScript("alert('输入的"+ sxmc +"属性最大值不是数字型的！')"));
              return;
            }
            inputNums[1] = new BigDecimal(max);
            propValues.put(sxmc, inputNums);
          }
        }
        else
        {
          String value = searchRow.get("sxmc_"+i);
          if(value.length() > 0)
            propValues.put(sxmc, value);
        }
        ds.next();
      }
      if(propValues.size() == 0)
        return;

      //过滤规格属性列表
      dsPlanSelectSale.first();
      int count = dsPlanSelectSale.getRowCount();
      for(int i=0; i<count; i++)
      {
        String sxz = dsPlanSelectSale.getValue("sxz");
        boolean isDelete = true;
        if(sxz.length() > 0)
          isDelete = !matchValue(propValues, sxz);
        if(isDelete)
        {
          long rownum = dsPlanSelectSale.getInternalRow();
          dsPlanSelectSale.deleteRow();
          dsPlanSelectSale.resetPendingStatus(rownum, true);
        }
        else
          dsPlanSelectSale.next();
      }
      dsPlanSelectSale.first();
      for(int i=0; i<dsPlanSelectSale.getRowCount(); i++)
      {
        double scdwgs = dsPlanSelectSale.getValue("scdwgs").length()>0 ? Double.parseDouble(dsPlanSelectSale.getValue("scdwgs")) : 1;
        double sl = dsPlanSelectSale.getValue("sl").length()>0 ? Double.parseDouble(dsPlanSelectSale.getValue("sl")) : 0;
        String sxz = dsPlanSelectSale.getValue("sxz");
        String width = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP, "()");
        double d_width = width.equals("0") ? 1 : Double.parseDouble(width);
        double scsl=0;
        if(d_width==0)
          scsl =sl;
        else
          scsl = sl*scdwgs/d_width;
        dsPlanSelectSale.setValue("scsl", formatNumber(String.valueOf(scsl), qtyFormat));
        dsPlanSelectSale.post();
        dsPlanSelectSale.next();
      }
    }
    //保存属性值拆分的key和value
    Hashtable mapSxz = new Hashtable();
    private boolean matchValue(Hashtable searchExps, String sxz)
    {
      if(mapSxz.size() > 0)
        mapSxz.clear();
      //分解属性值
      String[] sxzs = StringUtils.parseString(sxz, "()");//物料的规格属性值拆分存入HashTable中，key为类别属性名称，value为值
      String key = null,  value = null;
      for(int i=0; i<sxzs.length; i++)
      {
        if(i%2 == 0)
          key = sxzs[i].trim();
        else
          mapSxz.put(key, sxzs[i].trim());
      }
      if(mapSxz.size() == 0)
        return false;
      //判断属性值
      Map.Entry[] entrys = (Map.Entry[])searchExps.entrySet().toArray(new Map.Entry[searchExps.size()]);
      for(int i=0; i<entrys.length; i++)
      {
        Object searchKey = entrys[i].getKey();
        Object searchValue = entrys[i].getValue();
        //查询的条件是否是字符串的
        if(searchValue instanceof String)
        {
        if(!searchValue.equals(mapSxz.get(searchKey)))
          return false;
        }
        else
        {
          String sxzValue = (String)mapSxz.get(searchKey);
          if(sxzValue ==null || sxzValue.length() == 0 || !isDouble(sxzValue))
            return false;
          BigDecimal bdSxzValue = new BigDecimal(sxzValue);
          BigDecimal[] values = (BigDecimal[])searchValue;
          //若小于最小值剔除
          if(values[0] != null && bdSxzValue.compareTo(values[0]) < 0)
            return false;
          //若大于最大值剔除
          if(values[1] != null && bdSxzValue.compareTo(values[1]) > 0)
            return false;
        }
      }
      return true;
    }

    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet master = dsPlanSelectSale;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("htbh"), null, null, null, "a",">="),
        new QueryColumn(master.getColumn("htbh"), null, null, null,"b","<="),
        new QueryColumn(master.getColumn("dwmc"), null, null, null),
        new QueryColumn(master.getColumn("dwtxid"), null, null, null,null,"="),
        new QueryColumn(master.getColumn("zt"), null, null, null,null,"="),
        new QueryColumn(master.getColumn("dwdm"), null, null, null, "a",">="),
        new QueryColumn(master.getColumn("dwdm"), null, null, null,"b","<="),
        new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),//产品
        new QueryColumn(master.getColumn("jhrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("jhrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("cpbm"), null, null, null, null, "like"),//产品编码
        new QueryColumn(master.getColumn("product"), null, null, null, null, "like"),//产品
        new QueryColumn(master.getColumn("isproduce"), null, null, null, null, "="),//产品
      });
      isInitQuery = true;
    }
  }

  public final EngineDataSet getOneTable()
  {
    return dsPlanSelectSale;
  }
  public final EngineDataSet getPropertyTable()
  {
    return dsproperty;
  }
  public final RowMap getSearchRow()
  {
    return searchRow;
  }
}