package engine.erp.common;

import engine.action.*;
import engine.dataset.*;
import engine.web.observer.*;
import engine.util.log.*;
import engine.util.StringUtils;
import engine.common.LoginBean;

import javax.servlet.http.*;
import java.util.Hashtable;
import java.util.Map;
import java.math.BigDecimal;
/**
 * <p>Title: 规格属性选择</p>
 * <p>Description: 规格属性选择</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public final class B_PropertySelect extends BaseAction implements Operate
{
  //产品的规格属性
  private static final String PROD_PROPERTY_SQL
      = "SELECT dmsxid, cpid, sxz, isdelete FROM kc_dmsx WHERE @ (isdelete IS NULL OR isdelete=0) AND cpid='@' ORDER BY sxz";
  //产品的规格属性结构
  private static final String PROD_PROPERTY_STRUCT_SQL
      = "SELECT dmsxid, cpid, sxz, isdelete FROM kc_dmsx WHERE 1<>1";
  //类别属性
  private static final String SPEC_PROPERTY_SQL
      = "SELECT a.dlsxID, a.sxmc, a.sxlx FROM kc_dmlbsx a WHERE a.wzlbid='@'";
  //类别属性结构
  private static final String SPEC_PROPERTY_STRUCT_SQL
      = "SELECT a.dlsxID, a.sxmc, a.sxlx FROM kc_dmlbsx a WHERE 1<>1";
  //是否具有规格属性. 2004-11-11删除的物资可选择规格属性
  private static final String PROD_ISPROP_SQL
      = "SELECT wzlbid FROM kc_dm WHERE isProps=1 AND cpid='@'"; //AND isdelete=0
  //得到一条属性数据,用于判断产品属性是否已经存在, 若是删除的则改变删除状态
  private static final String PROD_PROPERTY_ONE_SQL
      = "SELECT dmsxid, cpid, sxz, isdelete FROM kc_dmsx WHERE sxz='@' AND cpid='@'";
  //"SELECT pck_base.fieldCodeCount('kc_dmsx','sxz','@','cpid=@') FROM dual";
  //产品属性
  private EngineDataSet dsProdProperty  = new EngineDataSet();
  //大类属性
  public  EngineDataSet dsSpecProperty  = new EngineDataSet();

  public String[] inputName = null;    //
  public String[] fieldName = null;    //字段名称
  public String srcFrm=null;           //传递的原form的名称
  public boolean isMultiSelect = false;//是否是多选的
  public String multiIdInput = null;   //多选的ID组合串

  public String methodName = null;   //调用window.opener中的方法
  //private boolean isInitQuery = false; //是否已经初始化查询条件
  //private QueryBasic fixedQuery = new QueryFixedItem();
  public  RowMap rowInfo = new RowMap();
  public  RowMap searchRow = new RowMap();//保存查询信息的列
  public  String retuUrl = null;
  private String fgsid = null;  //分公司ID
  private String cpid = null;
  private String wzlbid = null; //保存物资类别ID

  /**
   * 得到往来单位信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回往来单位信息的实例
   */
  public static B_PropertySelect getInstance(HttpServletRequest request)
  {
    B_PropertySelect propertySelectBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "propertySelectBean";
      propertySelectBean = (B_PropertySelect)session.getAttribute(beanName);
      if(propertySelectBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsid = loginBean.getFirstDeptID();
        propertySelectBean = new B_PropertySelect(fgsid);
        session.setAttribute(beanName, propertySelectBean);
      }
    }
    return propertySelectBean;
  }

  /**
   * 构造函数
   */
  private B_PropertySelect(String fgsid)
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
    setDataSetProperty(dsProdProperty, PROD_PROPERTY_STRUCT_SQL);
    setDataSetProperty(dsSpecProperty, SPEC_PROPERTY_STRUCT_SQL);
    dsProdProperty.setSequence(new SequenceDescriptor(new String[]{"dmsxid"}, new String[]{"s_kc_dmsx"}));

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(POST), new Post());
    addObactioner(String.valueOf(PROD_PROP_NAME_CHANGE), new NameSearch());
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
      if(dsProdProperty.isOpen() && dsProdProperty.changesPending())
        dsProdProperty.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsProdProperty != null){
      dsProdProperty.close();
      dsProdProperty = null;
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
   * 初始化表单查询信息和SQL语句
   * @param data data对象
   * @param isIframe 是否是你不框架调用
   * @throws Exception 异常信息
   */
  private void init(RunData data, boolean isIframe) throws Exception
  {
    retuUrl = data.getParameter("src","");
    retuUrl = retuUrl.trim();
    String temp = data.getParameter("cpid", "");
    if(temp.length() == 0)
    {
      data.setMessage(showJavaScript("alert('非法的产品！'); window.close();"));
      return;
    }
    cpid = temp;
    //是否具有规格属性
    temp = combineSQL(PROD_ISPROP_SQL, "@", new String[]{cpid});
    wzlbid = dataSetProvider.getSequence(temp);
    if(wzlbid == null)
    {
      data.setMessage(showJavaScript("alert('该产品没有设置规格属性！'); window.close();"));
      return;
    }
    //table.getWhereInfo().clearWhereValues();
    //得到关闭窗体前要调用的方法
    methodName = data.getParameter("method");
    srcFrm = data.getParameter("srcFrm");
    //是否是多选
    String multi = data.getParameter("multi");
    isMultiSelect = multi!=null && multi.equals("1");
    if(isMultiSelect)
      multiIdInput = data.getParameter("srcVar");
    else
    {
      inputName = data.getParameterValues("srcVar");
      fieldName = data.getParameterValues("fieldVar");
    }
    //提取所属的大类属性
    String SQL = combineSQL(SPEC_PROPERTY_SQL, "@", new String[]{wzlbid});
    dsSpecProperty.setQueryString(SQL);
    if(dsSpecProperty.isOpen())
      dsSpecProperty.refresh();
    else
      dsSpecProperty.openDataSet();
  }

  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      init(data, false);
      if(data.hasMessage())
        return;
      /*大类编码
      String prodcode = dataSetProvider.getSequence(PROD_CODE_SQL);
      if(prodcode == null)
      {
        data.setMessage(showJavaScript("alert('请先设置产品编码规则！'); window.close();"));
        return;
      }
      prodcode = parseString(prodcode, ",")[0];
      cpbm = cpbm.substring(0, Integer.parseInt(prodcode));*/
      //提取产品属性数据
      String sql = combineSQL(PROD_PROPERTY_SQL, "@", new String[]{"",cpid});
      dsProdProperty.setQueryString(sql);
      if(dsProdProperty.isOpen())
        dsProdProperty.refresh();
      else
        dsProdProperty.openDataSet();

      rowInfo.clear();
      searchRow.clear();
    }
  }

  /**
   * 属性名称模糊查询
   */
  class NameSearch implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      init(data, false);
      if(data.hasMessage())
        return;
      //提取产品属性数据
      String name = data.getParameter("name", "");
      if(name.length() > 0)
        name = "sxz LIKE '%" + name + "%' AND";
      String sql = combineSQL(PROD_PROPERTY_SQL, "@", new String[]{name, cpid});
      dsProdProperty.setQueryString(sql);
      if(dsProdProperty.isOpen())
        dsProdProperty.refresh();
      else
        dsProdProperty.openDataSet();

      rowInfo.clear();
    }
  }

  /**
   * 提交操作的触发类
   */
  class Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      rowInfo.put(data.getRequest());
      EngineDataSet ds = dsSpecProperty;
      ds.first();
      int count = ds.getRowCount();
      //校验是否全部未空
      boolean isNotNull = false;
      for(int i=0; i<count; i++)
      {
        if(rowInfo.get("sxmc_"+i).length() > 0)
          isNotNull = true;
      }
      if(!isNotNull)
      {
        data.setMessage(showJavaScript("alert('至少需要填写一个属性值！')"));
        return;
      }
      //
      StringBuffer buf = null;
      for(int i=0; i<count; i++)
      {
        String sxmc = rowInfo.get("sxmc_"+i);
        if(sxmc.indexOf("(") > 0 || sxmc.indexOf(")") > 0)
        {
          data.setMessage(showJavaScript("alert('输入的"+ ds.getValue("sxmc") +"属性值含有非法字符( 或 )！')"));
          return;
        }
        if(sxmc.length() > 0)
        {
          if(buf == null)
            buf = new StringBuffer();
          else
            buf.append(" ");

          String sxlx = ds.getValue("sxlx");
          if(sxlx.equals("number") && !isDouble(sxmc))
          {
            data.setMessage(showJavaScript("alert('输入的"+ ds.getValue("sxmc") +"属性值不是数字型的！')"));
            return;
          }
          else if(sxlx.equals("upperchar"))
            sxmc = sxmc.toUpperCase();
          else if(sxlx.equals("lowerchar"))
            sxmc = sxmc.toLowerCase();

          buf.append(ds.getValue("sxmc")).append("(").append(sxmc).append(")");
        }
        ds.next();
      }
      String sxz = buf.toString();
      EngineRow locateRow = new EngineRow(dsProdProperty, "sxz");
      locateRow.setValue(0, sxz);
      String SQL = combineSQL(PROD_PROPERTY_ONE_SQL, "@", new String[]{sxz, cpid});
      //String result = dataSetProvider.getSequence(SQL);
      if(sureLocateData(dsProdProperty, locateRow, SQL))
      {
        //删除的属性可重用
        if(dsProdProperty.getValue("isdelete").equals("1"))
        {
          dsProdProperty.setValue("isdelete", "0");
          dsProdProperty.post();
          dsProdProperty.saveChanges();
        }
        //data.setMessage(showJavaScript("alert('该属性已经存在,请重填！')"));
        data.setMessage(showJavaScript("selectPropRow("+ dsProdProperty.getRow() +")"));
        return;
      }
      dsProdProperty.insertRow(false);
      dsProdProperty.setValue("dmsxid", "-1");
      dsProdProperty.setValue("cpid", cpid);
      dsProdProperty.setValue("sxz", sxz);
      dsProdProperty.setValue("isdelete", "0");
      dsProdProperty.post();
      dsProdProperty.saveChanges();
      data.setMessage(showJavaScript("selectPropRow("+ dsProdProperty.getRow() +")"));
    }
  }

  /**
   * 查询操作的触发类
   */
  class Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //得到所有的规格属性
      String sql = combineSQL(PROD_PROPERTY_SQL, "@", new String[]{"",cpid});
      dsProdProperty.setQueryString(sql);
      if(dsProdProperty.isOpen())
        dsProdProperty.refresh();
      else
        dsProdProperty.openDataSet();
      //
      if(dsProdProperty.getRowCount() == 0)
        return;
      //校验输入的正确性
      searchRow.put(data.getRequest());
      Hashtable propValues = new Hashtable();//用于保存属性值的查询条件
      EngineDataSet ds = dsSpecProperty;
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
      dsProdProperty.first();
      int count = dsProdProperty.getRowCount();
      for(int i=0; i<count; i++)
      {
        String sxz = dsProdProperty.getValue("sxz");
        boolean isDelete = true;
        if(sxz.length() > 0)
          isDelete = !matchPropertyValue(propValues, sxz);
        if(isDelete)
        {
          long rownum = dsProdProperty.getInternalRow();
          dsProdProperty.deleteRow();
          dsProdProperty.resetPendingStatus(rownum, true);
        }
        else
          dsProdProperty.next();
      }
    }

    //保存属性值拆分的key和value
    Hashtable mapSxz = new Hashtable();
    /**
     * 校验属性值是否匹配需要查询的表达式
     * @param searchExps 查询的表达式
     * @param sxz 数据集中的属性值
     * @return 返回是否匹配
     */
    private boolean matchPropertyValue(Hashtable searchExps, String sxz)
    {
      if(mapSxz.size() > 0)
        mapSxz.clear();
      //分解属性值
      String[] sxzs = StringUtils.parseString(sxz, "()");
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
  }

  /**
   * 得到数据集对象
   * @return 返回数据集对象
   */
  public final EngineDataSet getOneTable()
  {
    return dsProdProperty;
  }

  /**
   * 得到写日志的对象
   * @return 写日志的对象
   */
  public Log getLog()
  {
    return log;
  }

  //用于属性的选择框。仓库报表用
  private EngineDataSet dsPropertyData = null;
  /**
   * 得到物资类别的定义属性
   * @param wzlbid 物资类别id
   * @return 返回定义的属性
   */
  public EngineDataSet getPropertyData(String wzlbid)
  {
    if(dsPropertyData == null)
    {
      dsPropertyData = new EngineDataSet();
      dsPropertyData.setProvider(getProvider());
    }
    String sql = combineSQL(SPEC_PROPERTY_SQL, "@", new String[]{wzlbid});
    if(!dsPropertyData.isOpen())
    {
      dsPropertyData.setQueryString(sql);
      dsPropertyData.open();
    }
    else if(!sql.equals(dsPropertyData.getQueryString()))
    {
      dsPropertyData.setQueryString(sql);
      dsPropertyData.refresh();
    }

    return dsPropertyData;
  }
}

