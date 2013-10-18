package engine.project;

import java.util.*;
import javax.servlet.http.*;
import engine.web.taglib.Select;
import engine.action.BaseAction;
import engine.dataset.*;
import engine.util.log.Log;
import engine.util.StringUtils;
import engine.util.EngineRuntimeException;

import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.DataSetView;
import com.borland.dx.dataset.Locate;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author hukn
 * @version 1.0
 */

public class LookupBeanFacade implements LookUp, HttpSessionBindingListener
{
  private static Hashtable lookupPool = new Hashtable();//LookupBeanFacade的缓存池

  /**
   * 刷新lookup的数据,以同步数据
   * @param beanName name of bean
   */
  public synchronized static void refreshLookup(String beanName)
  {
    DataSetPool.refreshDataSet(beanName);
  }

  /**
   * 创建树装下拉框
   * @param lookupBean lookupBeand对象
   * @param fatherCode 父结点编号
   * @param level 层
   * @return 返回是否还有子结点
   */
  private static boolean createOptionTree(LookupBeanFacade lookupBean, String fatherCode, int level,
                                       String selectNodeId, StringBuffer buf)
  {
    DataSetView ds = lookupBean.viewData;
    LookupHelper help = lookupBean.help;
    String codeFieldName = help.treeCode;

    String[] curNodeIds  = new String[help.keyColumns.length];
    for(int i=0; i<curNodeIds.length; i++)
      curNodeIds[i] = EngineDataSet.getValue(ds, ds.getColumn(help.keyColumns[i]).getOrdinal());

    String curNodeCode = EngineDataSet.getValue(ds, ds.getColumn(codeFieldName).getOrdinal());
    //得到显示的名称的字符串
    StringBuffer curNodeName = new StringBuffer();
    for(int i=0; i<help.capsColumn.length; i++)
    {
      String caps = EngineDataSet.getValue(ds, ds.getColumn(help.capsColumn[i]).getOrdinal());
      curNodeName.append(caps);
      if(i < help.capsColumn.length-1)
        curNodeName.append(" ");
    }

    boolean hasChild = false;//是否还有子结点
    boolean isLeaf = false;

    if(ds.next())
    {
      String nextCode = EngineDataSet.getValue(ds, ds.getColumn(codeFieldName).getOrdinal());
      isLeaf = !nextCode.startsWith(curNodeCode);
      hasChild = nextCode.startsWith(fatherCode);
    }
    else
    {
      isLeaf = true;
      hasChild = false;
    }

    buf.append("AddSelectItem('").append(StringUtils.getArrayValue(curNodeIds)).append("','");
    for(int i=0; i<level; i++)
      buf.append("  ");
    buf.append(curNodeName.toString().trim()).append("');");

    if(!isLeaf)
    {
      //打印子结点
      while(true)
      {
        boolean isHasChild = createOptionTree(lookupBean, curNodeCode, level+1, selectNodeId, buf);
        if(!isHasChild)
          break;
      }

      String name = EngineDataSet.getValue(ds, ds.getColumn(codeFieldName).getOrdinal());
      hasChild = ds.inBounds() && name.startsWith(fatherCode);
    }
    return hasChild;
  }

  /**
   * 将数据集转化为网页的下拉框的option的内容
   * @param ds 数据集
   * @param idColumn id列名,即要得到的值
   * @param capColumns 显示的名称
   * @param selectIdValue 初始化option时，默认选中的id列的值
   * @param existColumn 过滤的列名，即只打印以该列名为条件的值，若＝null，则不起作用
   * @param existValue  过滤的列名的值
   * @return 包含option的字符串
   */
  public synchronized static String dataSetToOption(DataSet ds, String idColumn,
      String capColumns[], String selectIdValue, String existColumn, String existValue)
  {
    LookupParam[] paramids = null;
    if(existColumn != null && existValue != null)
      paramids = new LookupParam[]{new LookupParam(existColumn, new String[]{existValue})};

    return dataSetToOption(ds, new String[]{idColumn}, new String[]{selectIdValue},
                           capColumns, paramids);
  }

  /**
   * 将数据集转化为网页的下拉框的option的内容
   * @param ds 数据集
   * @param selectIds id列名和初始化option
   * @param selectIdValues id列名和初始化option时，默认选中的id列的值
   * @param capColumns 显示的名称
   * @param paramids 需要打印的列名和相应的值
   * @return 包含option的字符串
   */
  private static String dataSetToOption(DataSet ds, String[] selectIds, String[] selectIdValues,
      String capColumns[], LookupParam[] paramIds)
  {
    StringBuffer buf = new StringBuffer();
    synchronized(ds)
    {
      //创建打印的id字段和caption字段
      int[] idOrdianal = new int[selectIds.length];
      for(int i=0; i<idOrdianal.length; i++)
        idOrdianal[i] = ds.getColumn(selectIds[i]).getOrdinal();

      int[] capOrdinals = new int[capColumns.length];
      for(int i=0; i<capOrdinals.length; i++)
        capOrdinals[i] = ds.getColumn(capColumns[i]).getOrdinal();

      //打印添加网页选择框的各行的option的javascript
      ds.first();
      for(int i=0; i<ds.getRowCount(); i++)
      {
        if(paramIds == null)
          addSelectItem(buf, ds, idOrdianal, capOrdinals);
        else
        {
          boolean isAdd = true;
          for(int j=0; j<paramIds.length; j++)
          {
            String   key = paramIds[j].getKey();
            String[] values = paramIds[j].getValues();
            String   data = EngineDataSet.getValue(ds, ds.getColumn(key).getOrdinal());
            if(StringUtils.indexOf(values, data) < 0)
            {
              isAdd = false;
              break;
            }
          }
          if(isAdd)
            addSelectItem(buf, ds, idOrdianal, capOrdinals);
        }
        ds.next();
      }

      //
      String selectValuesStr = selectIdValues == null ? null : StringUtils.getArrayValue(selectIdValues);
      if(selectValuesStr != null && selectValuesStr.length() > 0)
        buf.append("SetSelectedIndex('").append(selectValuesStr).append("');");
    }
    return buf.toString();
  }

  /**
   * 添加选择框的选择项
   * @param buf 选择项对象
   * @param ds 数据集
   * @param idOrdianal id字段的Ordianal
   * @param capOrdinals 显示的各个字段的Ordianal
   */
  private static void addSelectItem(StringBuffer buf, DataSet ds, int idOrdianal, int[] capOrdinals)
  {
    String id = EngineDataSet.getValue(ds, idOrdianal);
    //AddSelectItem('1','北京');
    buf.append("AddSelectItem('").append(id).append("','");
    int j=0;
    for(; j<capOrdinals.length-1; j++)
    {
      buf.append(EngineDataSet.getValue(ds, capOrdinals[j]));
      buf.append(" ");
    }
    buf.append(EngineDataSet.getValue(ds, capOrdinals[j]));
    buf.append("');");
  }

  /**
   * 添加选择框的选择项
   * @param buf 选择项对象
   * @param ds 数据集
   * @param idOrdianal id字段的Ordianal
   * @param capOrdinals 显示的各个字段的Ordianal
   */
  private static void addSelectItem(StringBuffer buf, DataSet ds, int[] idOrdianals, int[] capOrdinals)
  {
    String[] idValues = new String[idOrdianals.length];
    for(int i=0; i<idValues.length; i++)
      idValues[i] = EngineDataSet.getValue(ds, idOrdianals[i]);
    //AddSelectItem('1','北京'); (OR) AddSelectItem('1,2','北京');
    buf.append("AddSelectItem('").append(StringUtils.getArrayValue(idValues)).append("','");
    int j=0;
    for(; j<capOrdinals.length-1; j++)
    {
      buf.append(EngineDataSet.getValue(ds, capOrdinals[j]).trim());
      buf.append(" ");
    }
    buf.append(EngineDataSet.getValue(ds, capOrdinals[j]).trim());
    buf.append("');");
  }
  /**
   * 将数组（必须两个数组）转化为网页的下拉框的option的内容
   * @param lists 两个数组, 一个用于id, 一个用于显示
   * @param selectedIndex 选择的数组下标数
   * @return 包含option的字符串
   */
  public synchronized static String listToOption(List[] lists, int selectedIndex)
  {
    StringBuffer buf = new StringBuffer();
    synchronized(lists)
    {
      for(int i=0; i<lists[0].size(); i++)
      {
        buf.append("AddSelectItem('").append(lists[0].get(i)).append("','");
        buf.append(lists[1].get(i)).append("');");
      }
      if(selectedIndex > -1 && selectedIndex < lists[0].size())
        buf.append("SetSelectedIndex('").append(lists[0].get(selectedIndex)).append("');");
      else
        buf.append("SetSelectedIndex('');");
    }
    return buf.toString();
  }

  /**
   * 得到一次性打开的lookup的数据
   * @param beanName
   * @return
   */
  public synchronized static EngineDataSet getLookupDataSet(String beanName)
  {
    //设置SQL语句的缓存
    LookupHelper help = LookupHelperPool.getLookupHelper(beanName);
    if(help == null || !help.isOneOpen())
      return null;
    //如果是一次打开数据的
    EngineDataSet eds = null;
    synchronized(lookupPool)
    {
      LookupBeanFacade lookupBean = (LookupBeanFacade)lookupPool.get(beanName);
      if(lookupBean == null)
      {
        eds = DataSetPool.getDataSet(beanName);
        lookupBean = new LookupBeanFacade(help, eds);
        lookupPool.put(beanName, lookupBean);
        lookupBean.openOneDataSet();
      }
    }
    return eds;
  }


  /**
   * 得到基础信息的实例，用于查询一行信息，得到仓库,库位等信息
   * @param request web请求
   * @param beanName baen的名称，各个bean的名称见类engine.project.SysConstant(如仓库,入参为SysConstant.BEAN_STORE)
   * @return 返回对象的引用
   */
  public synchronized static LookUp getInstance(HttpServletRequest request, String beanName)
  {
    LookupBeanFacade lookupBean = null;
    //设置SQL语句的缓存
    LookupHelper help = LookupHelperPool.getLookupHelper(beanName);
    if(help == null)
      return null;
    //得到登陆Bean
    engine.common.LoginBean login = engine.common.LoginBean.getInstance(request);
    //如果是一次打开数据的
    if(help.isOneOpen())
    {
      lookupBean = (LookupBeanFacade)lookupPool.get(beanName);
      if(lookupBean == null)
      {
        EngineDataSet eds = DataSetPool.getDataSet(beanName);
        lookupBean = new LookupBeanFacade(help, eds);
        lookupPool.put(beanName, lookupBean);
      }
    }
    else
    {
      HttpSession session = request.getSession(true);
      //beanname$sessionid
      String sessBeanName = new StringBuffer(beanName).append("$").append(session.getId()).toString();
      //在session中查找LookupBeanFacade的实例，不存在则创建一个
      lookupBean = (LookupBeanFacade)session.getAttribute(sessBeanName);
      if(lookupBean == null)
      {
        //在session中查找EngineDataSetProvider的实例，不存在则创建一个
        String sessProdviderName = new StringBuffer("prodvider$").append(session.getId()).toString();
        EngineDataSetProvider provider = (EngineDataSetProvider)session.getAttribute(sessProdviderName);
        if(provider == null)
        {
          provider = new EngineDataSetProvider();
          session.setAttribute(sessProdviderName, provider);
        }
        //
        EngineDataSet eds = new EngineDataSet();
        eds.setProvider(provider);
        lookupBean = new LookupBeanFacade(help, eds);
        session.setAttribute(sessBeanName, lookupBean);
      }
      Hashtable clause = new Hashtable();
      clause.put("fgsid", login.getFirstDeptID());
      lookupBean.setWhereClause(clause);
    }
    String[] deptids = login.getUser().getHandleDepts();
    String[] storeids = login.getUser().getHandleStores();
    LookupParam[] filerValues = new LookupParam[]{
      new LookupParam("isshow", new String[]{"1"}),
      new LookupParam("isdelete", new String[]{"0"}),
      deptids != null ? new LookupParam("deptid", login.getUser().getHandleDepts()) : null,
      storeids != null ? new LookupParam("storeid", login.getUser().getHandleStores()) : null
    };
    lookupBean.setFilterValues(filerValues);
    return lookupBean;
  }

  ////////////////实现Lookup接口的类////////////////////////

  private EngineDataSet dsData = null;    //用于查询数据的数据集

  private DataSetView viewData = null; //用于查询数据的数据集

  private LookupHelper help = null;               //与数据集体有关的特定的属性

  //private EngineRow locateRow = null;     //用于定位数据集

  private Map whereClause = null;         //可能的where子句的值

  private LookupParam[] filterValues = null;         //可以保留的字段和相应的值

  private String SQL = null;

  private EngineDataSet dsOtherData = null; //其他数据集,用于非主键的getlist

  private Log log = new Log("LookupBeanFacade");
  /**
   * 构造函数
   */
  private LookupBeanFacade(LookupHelper help, EngineDataSet dsData)
  {
    this.help = help;
    this.dsData = dsData;
    this.SQL = help.select;
  }

  /**
   * 设置过滤条件的值
   * @param values 过滤条件的值
   */
  private void setFilterValues(LookupParam[] values)
  {
    if(values == null)
      return;
    String[] keys = this.help.filterKey;
    if(keys == null)
      this.filterValues = null;
    else
    {
      this.filterValues = new LookupParam[keys.length];
      //得到需要的值
      for(int i=0; i<keys.length; i++)
      {
        this.filterValues[i] = LookupParam.getLookupParam(values, keys[i]);
        if(this.filterValues[i] == null)
          this.filterValues[i] = new LookupParam(keys[i], new String[]{"null"});
      }
    }
  }

  /**
   * 设置分公司ID等特定的查询条件
   * @param mapWhereClause 分公司ID等特定的查询条件
   */
  private void setWhereClause(Map mapWhereClause)
  {
    this.whereClause = mapWhereClause;
    this.SQL = help.select;
    //对一次性打开的不起作用
    if(help.isOneOpen() || help.whereClause == null)
      return;
    //预先组装SQL语句. 若有WHRER子句的定义，则组装WHRER子句
    boolean hasWhere = SQL.toUpperCase().indexOf("WHERE") > -1;
    StringBuffer buf = new StringBuffer(SQL);
    for(int i=0; i < help.whereClause.length; i++)
    {
      String value = (String)mapWhereClause.get(help.whereClause[i]);
      //若值为空，则继续循环
      if(value == null)
        continue;

      if(hasWhere)
        buf.append(" AND ");
      else
      {
        buf.append(" WHERE ");
        hasWhere = true;
      }
      String upperValue = value.toUpperCase();
      if(upperValue.indexOf("IN") > -1 || upperValue.indexOf("<>") > -1)
        buf.append(help.whereClause[i]).append(" ").append(value).append(" ");
      else
        buf.append(help.whereClause[i]).append("='").append(value).append("'");
    }
    SQL = buf.toString();
  }

  /**
   * 锁定并打开只打开一次的数据集
   */
  private void openOneDataSet()
  {
    synchronized(dsData)
    {
      if(!dsData.isOpen())
      {
        dsData.setQueryString(help.sortStr != null ? (help.select + " " + help.sortStr) : help.select);
        dsData.openDataSet();
        if(viewData == null)
          viewData = dsData.cloneDataSetView();
      }
    }
  }

  /**
   * 定位数据集
   * @param columnValue 定位数据集的字段名称值
   * @return 返回是否有此记录
   */
  private boolean locateDataSet(String columnValue)// throws Exception
  {
    //检测数据集是否已经注册
    checkDataSetReg();
    if(help.keyColumns.length > 1)
      throw new InvalidLookupArgumentException("lookup length of key not equals length of value");

    return locateDataSet(viewData, help.keyColumns, new String[]{columnValue});
  }

  /**
   * 定位数据集
   * @param columnValues 定位数据集的字段名称值
   * @return 返回是否有此记录
   */
  private boolean locateDataSet(String[] columnValues)// throws Exception
  {
    //检测数据集是否已经注册
    checkDataSetReg();
    if(help.keyColumns.length != columnValues.length)
      throw new InvalidLookupArgumentException("lookup length of key not equals length of value");

    return locateDataSet(viewData, help.keyColumns, columnValues);
  }

  /**
   * 定位数据集
   * @param ds 需要定位的数据集体（可以事view也可以是）
   * @param columnName 定位数据集的字段名称
   * @param columnValue 定位数据集的字段名称值
   * @return 返回是否有此记录
   */
  private boolean locateDataSet(DataSet ds, String[] columnNames, String[] columnValues)// throws Exception
  {
    EngineRow locateRow = new EngineRow(ds, columnNames);//
    for(int i=0; i<columnValues.length; i++)
      locateRow.setValue(i, columnValues[i]);
    //定位
    if(!ds.locate(locateRow, Locate.FIRST))
    {
      EngineDataSetProvider provider =  (EngineDataSetProvider)dsData.getProvider();
      StringBuffer buf = new StringBuffer(SQL);
      //组装SQL
      buf.append(SQL.toUpperCase().indexOf("WHERE") > -1 ? " AND " : " WHERE ");
      for(int i=0; i<columnNames.length; i++)
      {
        if(columnValues[i].length() == 0)
          buf.append(columnNames[i]).append(" IS NULL ");
        else
          buf.append(columnNames[i]).append("='").append(columnValues[i]).append("'");
        if(i < columnNames.length -1)
          buf.append(" AND ");
      }
      if(help.sortStr != null)
        buf.append(" ").append(help.sortStr);
      //
      ProvideInfo provideInfo = new ProvideInfo(buf.toString());
      provideInfo.setLoadDataUseSelf(false);
      ProvideInfo[] provide = new ProvideInfo[]{provideInfo};
      try{
        provide = provider.getDataSetData_info(provide);
      }
      catch(Exception ex){
        throw new EngineRuntimeException("locate at db", ex);
      }
      provide[0].getProvideData().loadDataSet(ds);
      provide = null;
      //StoreDataSetData storeDsd = provider.getDataSetData_info(new String[]{buf.toString()}, null)[0];
      /*if(storeDsd.getRowcount() > 0)
      {*/
      return ds.locate(locateRow, Locate.FIRST);
      /*}
      else
        return false;*/
    }
    return true;
  }

  /**
   * 注册数据,有些需要表由于记录的太多，需要注册要得到数据的记录，以用于抽取数据
   * @param ds 传入的数据集（需要有）
   * @param idName 与默认主键值相对应的数据集ds的字段名称, 主要用于提取数据集中的各行的值
   * @throws Exception 异常信息
   */
  public synchronized void regData(DataSet ds, String idColumnName) //throws Exception
  {
    if(help.isOneOpen())
    {
      openOneDataSet();
      return;
    }
    if(help.keyColumns.length > 1)
      throw new InvalidLookupArgumentException("lookup length of key not equals length of value");

    String[] ids = columnToArray(ds, idColumnName);
    //
    regData(new LookupParam[]{new LookupParam(help.keyColumns[0], ids)});
    //regData(ds, null, idColumnName);
  }

  /**
   * 注册数据,有些需要表由于记录的太多，需要注册要得到数据的记录，以用于抽取数据
   * @param ds 传入的数据集（需要有）
   * @param idColumnNames 与默认主键值相对应的数据集ds的字段名称数组, 主要用于提取数据集中的各行的值
   * @throws Exception 异常信息
   */
  public synchronized void regData(DataSet ds, String[] idColumnNames)
  {
    if(help.isOneOpen())
    {
      openOneDataSet();
      return;
    }
    //if is null, do nothing
    if(idColumnNames == null)
      return;

    String[] keys = help.keyColumns;
    if(keys.length != idColumnNames.length)
      throw new InvalidLookupArgumentException("lookup length of key not equals length of value");

    LookupParam[] params = new LookupParam[keys.length];
    for(int i=0; i<keys.length; i++)
    {
      String[] ids = columnToArray(ds, idColumnNames[i]);
      params[i] = new LookupParam(keys[i], ids);
    }
    //
    regData(params);
  }

  /**
   * 注册数据,有些需要表由于记录的太多，需要注册要得到数据的记录，以用于抽取数据
   * @param ds 传入的数据集（需要有）
   * @param regColumnName 需要用于注册的字段名称, 数据集ds中的名称可能与之不同, 等于null将用默认的主键字段
   * @param regValuesColumn 于regColumnName相对应的数据集ds的字段名称, 主要用于提取数据集中的各行的值
   * @throws Exception 异常信息
   *
  private void regData(DataSet ds, String regColumnName, String regValueColumnName) //throws Exception
  {
    if(help.isOneOpen())
    {
      openOneDataSet();
      return;
    }
    if(help.keyColumns.length > 1)
      throw new InvalidLookupArgumentException("lookup length of key not equals length of value");

    String[] ids = columnToArray(ds, regValueColumnName);
    //
    regData(new LookupParam[]{new LookupParam(regColumnName, ids)});
  }

  /**
   * 注册数据,有些需要表由于记录的太多，需要注册要得到数据的记录，以用于抽取数据
   * @param idValues 主键值数组
   * @throws Exception 异常信息
   */
  public synchronized void regData(String[] idValues) //throws Exception
  {
    if(help.keyColumns.length > 1)
      throw new InvalidLookupArgumentException("lookup length of key not equals length of value");

    regData(new LookupParam[]{new LookupParam(help.keyColumns[0], idValues)});
  }

  /**
   * 注册数据,有些需要表由于记录的太多，需要注册要得到数据的记录，以用于抽取数据
   * @param regInfos 注册数据的字段名称以及相应的数据值数组, 用该字段提取数据(非主键字段)
   */
  public synchronized void regData(LookupParam[] regInfos)// throws Exception
  {
    if(help.isOneOpen())
    {
      openOneDataSet();
      return;
    }

    if(regInfos == null)
      return;
    //组装主键的WHERE IN 子句
    //LookupParam param = new LookupParam(
        //regColumnName == null ? help.keyColumns[0] : regColumnName, regColumnValues);

    //String tempSQL = regColumnValues.length >0 ? SQL : help.select;
    String tempSQL = arrryToSQL(SQL, help.select, regInfos);
    //锁定并打开数据或刷新数据集
    synchronized(dsData)
    {
      //if(!dsData.isOpen() || !tempSQL.equals(dsData.getQueryString()))
      //{
      dsData.setQueryString(tempSQL);
      if(dsData.isOpen())
        dsData.refresh();
      else
        dsData.openDataSet();

      if(viewData == null)
        viewData = dsData.cloneDataSetView();
    }
  }

  /**
   * 将数据集的值转化为数组
   * @param ds 传入的数据集
   * @param columnName 字段名称
   * @return 转化后的String数组
   */
  private static String[] columnToArray(DataSet ds, String columnName)
  {
    synchronized(ds)
    {
      ArrayList idList = new ArrayList(ds.getRowCount());
      int rownum = ds.getRow();
      ds.first();
      int ordinal = ds.getColumn(columnName).getOrdinal();
      for(int i = 0; i < ds.getRowCount(); i++)
      {
        String id = EngineDataSet.getValue(ds, ordinal);
        if(id.length() > 0 && idList.indexOf(id) < 0)
          idList.add(id);
        ds.next();
      }
      if(ds.getRowCount() > 0)
        ds.goToRow(rownum);

      String[] ids = new String[idList.size()];
      idList.toArray(ids);
      return ids;
    }
  }

  /**
   * 组装主键的WHERE IN 子句
   * @param select select..from 部分SQL语句
   * @param initSelect 初始化的select.部分SQL语句，不带多余的where条件的
   * @param wheresValues WHERE条件字段名称和值的数组
   * @return 返回装后的SQL语句
   */
  private static String arrryToSQL(String select, String initSelect, LookupParam[] wheresValues)
  {
    synchronized(wheresValues)
    {
      StringBuffer buf = new StringBuffer(select);
      buf.append(select.toUpperCase().indexOf("WHERE") > -1 ? " AND " : " WHERE ");
      int startLength = buf.length();
      for(int i=0; i<wheresValues.length; i++)
      {
        LookupParam whereValue = wheresValues[i];
        String[] columnValues = whereValue.getValues();
        if(columnValues==null)
          continue;
        if(columnValues.length > 0)
          buf.append(whereValue.getKey()).append(" IN ('" );
        for(int j=0; j<columnValues.length; j++)
        {
          String value = columnValues[j] == null || columnValues[j].length()==0 ? "-1" : columnValues[j];
          buf.append(value).append(j== columnValues.length-1 ? "')" : "','");
        }
        //add AND
        if(i<wheresValues.length-1   && columnValues.length > 0 &&
           wheresValues[i+1] != null && wheresValues[i+1].getValues() != null &&
           wheresValues[i+1].getValues().length > 0)
          buf.append(" AND ");
      }
      //若没有变化，则取结构
      if(buf.length() == startLength)
      {
        buf.setLength(0);
        buf.append(initSelect);
        buf.append(initSelect.toUpperCase().indexOf("WHERE") > -1 ? " AND " : " WHERE ");
        buf.append("1<>1");
      }

      return buf.toString();
    }
  }


  /**
   * 注册其他条件数据, 在调用方法getList(String selectid, String paramid, String paramvalue)之前
   * @param ds 数据集
   * @param regColumnName 注册的字段值
   * @throws Exception 异常
   */
  public synchronized void regConditionData(DataSet ds, String regColumnName) //throws Exception
  {
    if(regColumnName == null || ds == null)
      return;
    String[] values = columnToArray(ds, regColumnName);
    //
    regConditionData(regColumnName, values);
  }

  /**
   * 注册其他条件数据, 在调用方法getList(String selectid, String paramid, String paramvalue)之前
   * @param regColumnName 注册的字段名称
   * @param regColumnValues 注册的字段值
   * @throws Exception 异常
   */
  public synchronized void regConditionData(String regColumnName, String[] regColumnValues) //throws Exception
  {
    if(regColumnName == null || regColumnValues == null)
      return;
    if(dsOtherData == null)
    {
      dsOtherData = new EngineDataSet();
      dsOtherData.setProvider(dsData.getProvider());
    }
    //组装主键的WHERE IN 子句
    LookupParam param = new LookupParam(
        regColumnName == null ? help.keyColumns[0] : regColumnName, regColumnValues);

    //String tempSQL = regColumnValues.length >0 ? SQL : help.select;
    String tempSQL = arrryToSQL(SQL, help.select, new LookupParam[]{param});
    if(help.sortStr != null)
      tempSQL =  new StringBuffer(tempSQL).append(" ").append(help.sortStr).toString();
    //打开数据或刷新数据集
    dsOtherData.setQueryString(tempSQL);
    if(dsOtherData.isOpen())
      dsOtherData.refresh();
    else
      dsOtherData.openDataSet();
  }

  /**
   * 根据主键id得到需要查找的名称字段值
   * @param idValue 主键
   * @return 返回查找到的名称字段值
   */
  public synchronized String getLookupName(String idValue)// throws Exception
  {
    return idValue == null || idValue.length() == 0
                    ? "" : getLookupName(new String[]{idValue});
  }

  /**
   * 根据多个主键得到需要查找的名称字段值
   * @param ids 主键数组
   * @return 返回查找到的名称字段值
   */
  public synchronized String getLookupName(String[] idValues)
  {
    if(idValues == null || idValues.length == 0 || !locateDataSet(idValues))
      return "";

    StringBuffer name = new StringBuffer();
    int i=0;
    for(; i<help.capsColumn.length; i++){
      String cap = EngineDataSet.getValue(viewData, viewData.getColumn(help.capsColumn[i]).getOrdinal());
      name.append(cap);
      if(i < help.capsColumn.length-1)
        name.append(" ");
    }
    return name.toString();
  }

  /**
   * 根据主键id得到需要查找记录的所有信息
   * @param idValue 主键
   * @return 返回查找到的记录的所有信息
   * @throws Exception 异常信息
   */
  public synchronized RowMap getLookupRow(String idValue)
  {
    return idValue == null || idValue.length() == 0 || !locateDataSet(idValue)
                   ? new RowMap() : new RowMap(viewData);
  }

  /**
   * 根据多个主键得到需要查找记录的所有信息
   * @param ids 主键数组
   * @return 返回查找到的记录的所有信息
   */
  public synchronized RowMap getLookupRow(String[] idValues)
  {
    return idValues == null || idValues.length == 0 || !locateDataSet(idValues)
                    ? new RowMap() : new RowMap(viewData);
  }


  /**
   * 根据主键id得到需要查找记录的所有信息
   * @param idValue 主键
   * @param id 主键
   * @return 返回查找到的记录的所有信息
   * @throws Exception 异常信息
  public String[] getLookupValues(String idValue, String[] lookUpColumns) throws Exception
  {
    String[] temps = new String[lookUpColumns.length];
    for(int i=0; i<lookUpColumns.length; i++)
    {

    }
  }
  /**
   * 得到下拉框信息列表(如&lt;option value=1&gt;显示的值&lt;/option&gt;)
   * @param selectid 需要选中的id
   * @return 返回下拉框信息
   * @throws Exception 异常
   */
  public synchronized String getList(String selectid) //throws Exception
  {
    checkDataSetReg(); //检测数据集是否已经注册
    return getList(selectid, null);
  }

  /**
   * 得到下拉框信息列表(如&lt;option value=1&gt;显示的值&lt;/option&gt;)
   * @return 返回下拉框信息
   * @throws Exception 异常
   */
  public synchronized String getList() //throws Exception
  {
    checkDataSetReg(); //检测数据集是否已经注册
    return getList(null, null);
  }

  /**
   * 得到下拉框信息列表,需要先调用regConditionData方法(如&lt;option value=1&gt;显示的值&lt;/option&gt;)
   * @param selectid 需要选中的id
   * @param paramid 制定打印含有显示特定字段和值的下拉框列表
   * @param paramvalue 制定打印含有显示特定字段和值的下拉框列表
   * @return 返回下拉框信息
   */
  public synchronized String getList(String selectid, String paramid, String paramvalue)// throws Exception
  {
    LookupParam[] paramids = null;
    if(paramid != null && paramvalue != null)
    {
      if(dsOtherData == null)
      {
        log.warn(help.select+": you should call method of regConditionData() at first!");
        throw new LookupNoRegException("you should call method of regConditionData() at first!");
      }
      //定位数据集（到数据库）
      locateDataSet(dsOtherData, new String[]{paramid}, new String[]{paramvalue});
      LookupParam param = new LookupParam(paramid, new String[]{paramvalue});
      paramids = new LookupParam[]{param};
    }
    else
      checkDataSetReg(); //检测数据集是否已经注册

    return getList(selectid, paramids);
  }

  /**
   * 得到下拉框信息列表(如&lt;option value=1&gt;显示的值&lt;/option&gt;)
   * @param selectid 需要选中的id
   * @param paramids 各个参数id值
   * @return 返回下拉框信息
   * @throws Exception 异常
   */
  private String getList(String selectid, LookupParam[] paramids)// throws Exception
  {
    if(help.isTree)
    {
      synchronized(this){
        StringBuffer buf = new StringBuffer();
        viewData.first();
        while(viewData.inBounds())
        {
          String fatherCode = EngineDataSet.getValue(viewData, viewData.getColumn(help.treeCode).getOrdinal());
          createOptionTree(this, fatherCode, 0, selectid, buf);
        }
        if(selectid != null)
          buf.append("SetSelectedIndex('").append(selectid).append("');");
        return buf.toString();
      }
    }
    else if(paramids == null)
      return dataSetToOption(viewData, help.keyColumns, selectid == null ? null : new String[]{selectid},
                             help.capsColumn, this.filterValues);
    //有条件的
    else if(this.filterValues == null)
      return dataSetToOption(dsOtherData, help.keyColumns, selectid == null ? null : new String[]{selectid},
                             help.capsColumn, paramids);
    //
    /*@todo:考虑去掉dsOther,因为现在是有filterValues。*/
    else
    {
      LookupParam[] allParams = LookupParam.union(this.filterValues, paramids);
      return dataSetToOption(dsOtherData, help.keyColumns, selectid == null ? null : new String[]{selectid},
                             help.capsColumn, allParams);
    }
  }
  /**
   * 检测数据集是否已经注册
   * @throws Exception 异常信息
   */
  private void checkDataSetReg() throws LookupNoRegException
  {
    if(help.isOneOpen())
      openOneDataSet();

    if(viewData == null)
    {
      log.warn(help.select+": you should call method of regData() at first!");
      throw new LookupNoRegException("you should call method of regData() at first!");
    }
  }

  //如果help实例中的whereClause!=null 或 isOneOpen==false， 就将实例推入特定的session中。而不放在bean池中
  //实现接口HttpSessionBindingListener
  /**
   * 绑定bean实例到session中
   * @param event session绑定事件
   */
  public void valueBound(HttpSessionBindingEvent event) {
    //event.getSession().getId();
  }

  /**
   * 将bean实例在session中解除绑定
   * @param event session绑定事件
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(help.isOneOpen())
      return;

    if(this.dsData != null)
    {
      this.dsData.closeDataSet();
      this.dsData = null;
    }

    if(this.dsOtherData != null)
    {
      this.dsOtherData.closeDataSet();
      this.dsOtherData = null;
    }

    this.viewData = null;      //用于查询数据的数据集
    this.help = null;          //与数据集体有关的特定的属性
    //this.locateRow = null;     //用于定位数据集
    this.whereClause = null;   //可能的where子句的值
    this.SQL = null;
    this.log = null;
  }

}