package engine.erp.produce;

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
import engine.util.StringUtils;
import engine.erp.baseinfo.BasePublicClass;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Date;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 生产子系统_生产分切计划物料</p>
 * <p>Description: 通过分切计划的规格属性最小宽度从库存物资明细中抽取大于该宽度的物资</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class SubPlan_Sel_Materail extends BaseAction implements Operate
{
  //库存物资明细数据，用于分切。供选择
  private static final String Materail_SQL = "SELECT a.wzmxid, a.cpid, a.zl, c.cpbm, b.sxz, (c.pm|| '' || c.gg) product, c.jldw, a.dmsxid,NULL scsl, c.scydw, c.scdwgs,c.wzlbid FROM kc_wzmx a, kc_dmsx b, kc_dm c "
             + " WHERE a.cpid=c.cpid AND a.dmsxid=b.dmsxid(+) AND a.zl>0 AND b.sxz LIKE '%@%' @ @ @ @ ORDER BY c.cpbm ";
  //查询出来的物料类别属性
  private static final String PROPERTY_SQL ="SELECT DISTINCT t.sxmc,t.sxlx FROM kc_dmlbsx t WHERE t.wzlbid IN(?)";
  private static final String GET_BOMNODE_DATA = "{CALL pck_produce.getBomNodePath(?,@)}";

  private EngineDataSet dsMaterail  = new EngineDataSet();//物料数据集
  private EngineDataSet dsProperty = new EngineDataSet();//大类类别属性
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private ArrayList d_RowInfos = null;//保存页面信息
  private RowMap searchRow = new RowMap();//保存查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String cpid= null;//传入参数CPID
  private String sql=null;//组装SQl
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  /**
   * 得到当前库存物资明细信息的实例
   * @param request jsp请求
   * @return 返回当前库存物资明细的实例
   */
  public static SubPlan_Sel_Materail getInstance(HttpServletRequest request)
  {
    SubPlan_Sel_Materail subPlanMaterailBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "subPlanMaterailBean";
      subPlanMaterailBean = (SubPlan_Sel_Materail)session.getAttribute(beanName);
      if(subPlanMaterailBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        subPlanMaterailBean = new SubPlan_Sel_Materail();
        subPlanMaterailBean.fgsid = loginBean.getFirstDeptID();
        subPlanMaterailBean.qtyFormat = loginBean.getQtyFormat();
        subPlanMaterailBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……

        subPlanMaterailBean.dsMaterail.setColumnFormat("zl", subPlanMaterailBean.qtyFormat);
        session.setAttribute(beanName, subPlanMaterailBean);
      }
    }
    return subPlanMaterailBean;
  }

  /**
   * 构造函数
   */
  private SubPlan_Sel_Materail()
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
    setDataSetProperty(dsMaterail, null);
    setDataSetProperty(dsProperty,null);

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
    if(dsMaterail != null){
      dsMaterail.closeDataSet();
      dsMaterail = null;
    }
    if(dsProperty != null){
      dsProperty.closeDataSet();
      dsProperty = null;
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
   /*得到从表多列的信息*/
  public final RowMap[] getRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
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
      searchRow.clear();
      String minWidth = data.getParameter("minWidth");
      cpid = data.getParameter("cpid");
      String limit = null;
      if(!cpid.equals(""))
        limit = dataSetProvider.getSequence("SELECT pck_produce.getBomChildNodePath("+cpid+") from dual");
      if(!cpid.equals("") && limit !=null)
        sql=" AND a.cpid IN("+limit+","+cpid+")";
      else
        sql ="";
      String SQL = combineSQL(Materail_SQL, "@", new String[]{SYS_PRODUCT_SPEC_PROP,sql});
      dsMaterail.setQueryString(SQL);
      if(dsMaterail.isOpen())
        dsMaterail.refresh();
      else
        dsMaterail.openDataSet();
      //循环数据集，把宽度不符合得物料从数据集中删除
      dsMaterail.first();
      Hashtable props = new Hashtable();//用于保存属性值的查询条件
      if(!minWidth.equals("null")){
        props.put(SYS_PRODUCT_SPEC_PROP, new BigDecimal(minWidth));
        int count = dsMaterail.getRowCount();
        for(int i=0; i<count; i++)
        {
          double scdwgs = dsMaterail.getValue("scdwgs").length()>0 ? Double.parseDouble(dsMaterail.getValue("scdwgs")) : 1;
          double sl = dsMaterail.getValue("zl").length()>0 ? Double.parseDouble(dsMaterail.getValue("zl")) : 0;
          String sxz = dsMaterail.getValue("sxz");
          boolean isDelete = true;
          if(sxz.length() > 0)
            isDelete = !BasePublicClass.matchPropertyValue(props, sxz, true);
          if(isDelete)
          {
            long rownum = dsMaterail.getInternalRow();
            dsMaterail.deleteRow();
            dsMaterail.resetPendingStatus(rownum, true);
          }
          else
            dsMaterail.next();
        }
      }
      StringBuffer propertyBuf = new StringBuffer();//存放物料的类别属性ID
      dsMaterail.first();
      for(int i=0; i<dsMaterail.getRowCount(); i++)
      {
        String wzlbid = dsMaterail.getValue("wzlbid");
        if(i==dsMaterail.getRowCount()-1)
          propertyBuf = propertyBuf.append("'").append(wzlbid).append("'");
        else
          propertyBuf = propertyBuf.append("'").append(wzlbid).append("',");
        double scdwgs = dsMaterail.getValue("scdwgs").length()>0 ? Double.parseDouble(dsMaterail.getValue("scdwgs")) : 1;
        double sl = dsMaterail.getValue("zl").length()>0 ? Double.parseDouble(dsMaterail.getValue("zl")) : 0;
        String sxz = dsMaterail.getValue("sxz");
        String width = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP, "()");
        double d_width = width.equals("0") ? 1 : Double.parseDouble(width);
        double scsl=0;
        if(d_width==0)
          scsl =sl;
        else
          scsl = sl*scdwgs/d_width;
        dsMaterail.setValue("scsl", formatNumber(String.valueOf(scsl), qtyFormat));
        dsMaterail.post();
        dsMaterail.next();
      }
      String scope = propertyBuf.toString();
      String wzlbidsql = combineSQL(PROPERTY_SQL,"?", new String[]{scope});
      dsProperty.setQueryString(wzlbidsql);
      if(dsProperty.isOpen())
        dsProperty.refresh();
      else
        dsProperty.openDataSet();
    }
  }
  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常

  private final void initRowInfo(boolean isInit) throws java.lang.Exception
  {
      EngineDataSet dsDetail = getOneTable();
      dsDetail.first();
      for(int i=0; i<dsDetail.getRowCount(); i++)
      {
        double scdwgs = dsMaterail.getValue("scdwgs").length()>0 ? Double.parseDouble(dsMaterail.getValue("scdwgs")) : 1;
        double sl = dsMaterail.getValue("zl").length()>0 ? Double.parseDouble(dsMaterail.getValue("zl")) : 0;
        String sxz = dsMaterail.getValue("sxz");
        String width = BasePublicClass.parseEspecialString(sxz, "宽度", "()");
        double d_width = width.equals("0") ? 1 : Double.parseDouble(width);
        double scsl=0;
        if(d_width==0)
          scsl =sl;
        else
          scsl = sl*scdwgs*1000/d_width;
        dsMaterail.setValue("scsl", formatNumber(String.valueOf(scsl), qtyFormat));
        dsMaterail.post();
        dsMaterail.next();
      }
      if(d_RowInfos == null)
        d_RowInfos = new ArrayList(dsDetail.getRowCount());
      else if(isInit)
        d_RowInfos.clear();

      dsDetail.first();
      for(int i=0; i<dsDetail.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsDetail);
        d_RowInfos.add(row);
        dsDetail.next();
      }
  }
     */
  /*
  *得到一行信息
  */
  public final RowMap getLookupRow(String wzmxid)
  {
    RowMap row = new RowMap();
    if(wzmxid == null || wzmxid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsMaterail, "wzmxid");
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "wzmxid");
    locateRow.setValue(0, wzmxid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }

  /**
   * 生产分切计划物料选择库存物料时查询操作的触发类
   */
  class Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      searchRow.put(data.getRequest());
      String cpbm_a = searchRow.get("min_cpbm");
      String cpbm_b = searchRow.get("max_cpbm");
      String pm = searchRow.get("pm");
      String gg = searchRow.get("gg");
      String temp1 = "", temp2 = "", temp3="";//组装查询条件
      if(!cpbm_a.equals("") && !cpbm_b.equals(""))
        temp1 = " AND cpbm>='" + cpbm_a+"' AND cpbm<='"+cpbm_b+"'";
      else if(cpbm_a.equals("") && !cpbm_b.equals(""))
        temp1 = " AND cpbm<='"+cpbm_b+"'";
      else if(!cpbm_a.equals("") && cpbm_a.equals(""))
        temp1 = " AND cpbm>='"+cpbm_a+"'";
      if(!pm.equals(""))
        temp2 = " AND c.pm LIKE '%"+pm+"%'";
      if(!gg.equals(""))
        temp3 = " AND c.pm LIKE '%"+gg+"%'";

      //得到所有的规格属性
      String SQL = combineSQL(Materail_SQL, "@", new String[]{SYS_PRODUCT_SPEC_PROP,sql,temp1,temp2,temp3});
      dsMaterail.setQueryString(SQL);
      if(dsMaterail.isOpen()){
        dsMaterail.readyRefresh();
        dsMaterail.refresh();
      }
      else
        dsMaterail.openDataSet();
      //校验输入的正确性
      searchRow.put(data.getRequest());
      //EngineDataSet ds = dsMaterail;
      EngineDataSet ds = dsProperty;
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
      dsMaterail.first();
      int count = dsMaterail.getRowCount();
      for(int i=0; i<count; i++)
      {
        String sxz = dsMaterail.getValue("sxz");
        boolean isDelete = true;
        if(sxz.length() > 0)
          isDelete = !matchValue(propValues, sxz);
        if(isDelete)
        {
          long rownum = dsMaterail.getInternalRow();
          dsMaterail.deleteRow();
          dsMaterail.resetPendingStatus(rownum, true);
        }
        else
          dsMaterail.next();
      }
      dsMaterail.first();
      for(int i=0; i<dsMaterail.getRowCount(); i++)
      {
        double scdwgs = dsMaterail.getValue("scdwgs").length()>0 ? Double.parseDouble(dsMaterail.getValue("scdwgs")) : 1;
        double sl = dsMaterail.getValue("zl").length()>0 ? Double.parseDouble(dsMaterail.getValue("zl")) : 0;
        String sxz = dsMaterail.getValue("sxz");
        String width = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP, "()");
        double d_width = width.equals("0") ? 1 : Double.parseDouble(width);
        double scsl=0;
        if(d_width==0)
          scsl =sl;
        else
          scsl = sl*scdwgs/d_width;
        dsMaterail.setValue("scsl", formatNumber(String.valueOf(scsl), qtyFormat));
        dsMaterail.post();
        dsMaterail.next();
      }
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
   * 得到物料表数据集
   */
  public final EngineDataSet getOneTable()
  {
    return dsMaterail;
  }
  /**
   * 得到表
   */
  public final EngineDataSet getPropertyTable()
  {
    return dsProperty;
  }
  /**
   * 得到查询条件
   */
  public final RowMap getSearchRow()
  {
    return searchRow;
  }
}