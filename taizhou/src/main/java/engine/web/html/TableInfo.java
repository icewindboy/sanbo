package engine.web.html;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import engine.dataset.*;
import engine.dataset.sql.QueryWhereField;
import engine.dataset.sql.DefaultQueryWhereField;
import engine.web.lookup.LookupFacade;
import engine.web.lookup.Lookup;
import engine.util.log.Log;
import engine.util.StringUtils;

import com.borland.dx.dataset.*;

/**
 * <p>Title: 表字段各个信息信息</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author hukn
 * @version 1.0
 */

public final class TableInfo implements java.io.Serializable
{
  private static final String USER_FIELD_CODE_SQL
      = "SELECT TOP 100 PERCENT, b.fieldcode "
      + "FROM empfield a, nodefield b  WHERE b.tablecode = '?' AND a.personid = ? "
      + "AND  b.isshow = 1 AND b.nodefieldid = a.nodefieldid ORDER BY isbak, ordernum";

  private static PublicTable pubTable = new PublicTable();

  private EngineDataSetProvider dataSetProvider = new EngineDataSetProvider();
  //private EngineDataSet dsTableInfo = new EngineDataSet();
  private EngineDataSet dsUserField = new EngineDataSet();

  private List listFieldInfos = null;
  private List bakFieldInfos =  null;
  private List allFieldInfos = null;
  private Map  hashLookUp = null;
          Map  hashFieldInfos = null;

  private boolean checkColumn = false;

  private Log log = new Log(getClass());

  public TableInfo()
  {
    dsUserField.setProvider(dataSetProvider);
  }

  /**
   * 得到表名相对应的所有用于查询的字段信息
   * @param tableCode 表名
   * @return 返回字段数组
   * @throws Exception 异常
   */
  public QueryWhereField[] getWhereFields(String tableCode) throws Exception
  {
    return pubTable.getWhereFields(tableCode);
  }

  /**
   * 根据表名提取用户自定义的表的信息
   * @param tableName 表名
   * @throws Exception 异常
   */
  public void openTableInfo(HttpServletRequest request, String personid, String tableName) throws Exception
  {
    if(tableName == null)
      return;

    allFieldInfos = pubTable.getTableFields(tableName);
    int allcount = allFieldInfos.size();

    if(listFieldInfos == null)
      listFieldInfos = new ArrayList(allcount);
    else if(listFieldInfos.size() > 0)
      listFieldInfos.clear();

    if(bakFieldInfos == null)
      bakFieldInfos = new ArrayList(allcount);
    else if(bakFieldInfos.size() > 0)
      bakFieldInfos.clear();

    if(hashFieldInfos == null)
      hashFieldInfos = new Hashtable(allcount+1, 1);
    else if(hashFieldInfos.size() > 0)
      hashFieldInfos.clear();

    if(hashLookUp == null)
      hashLookUp = new Hashtable(allcount, 1);
    else if(hashLookUp.size() > 0)
      hashLookUp.clear();

    String SQL = StringUtils.combine(USER_FIELD_CODE_SQL, "?", new String[]{tableName, personid});
    dsUserField.setQueryString(SQL);
    if(!dsUserField.isOpen())
      dsUserField.openDataSet();
    else{
      dsUserField.refresh();
    }
    int count = dsUserField.getRowCount();
    boolean isDefault = count == 0;
    ArrayList userFieldCodes = null;
    if(!isDefault)
    {
      userFieldCodes = new ArrayList(count);
      dsUserField.first();
      for(int i =0; i < count; i++)
      {
        userFieldCodes.add(dsUserField.getValue("fieldcode").toLowerCase());
        dsUserField.next();
      }
    }

    for(int i=0; i<allcount; i++)
    {
      FieldInfo field = (FieldInfo)allFieldInfos.get(i);
      String linkTable = field.getLinktable();
      Lookup lookup = linkTable == null ? null : LookupFacade.getInstance(request, linkTable);
      if(lookup != null)
        hashLookUp.put(field.getFieldcode(), lookup);

      hashFieldInfos.put(field.getFieldcode(), field);

      if(field.isShow())
      {
        if(isDefault)
          listFieldInfos.add(field);
        else if(userFieldCodes.contains(field.getFieldcode()))
          listFieldInfos.add(field);
      }

      if(field.isBak())
        bakFieldInfos.add(field);
    }

    if(!isDefault)
      dsUserField.closeDataSet();

    checkColumn = false;
  }

  /**
   * 注册与外键关联的相应对象的数据
   * @param ds 含有外键关联的数据集
   */
  public void regDatas(DataSet ds)// throws Exception
  {
    for(int i=0; i<allFieldInfos.size(); i++)
    {
      FieldInfo field = (FieldInfo)allFieldInfos.get(i);

      if(field.getType() == FieldInfo.VIRTUAL_TYPE)
        continue;

      String fieldcode = field.getFieldcode();
      try{
        ds.getColumn(field.getFieldcode());
      }
      catch(DataSetException ex) {
        log.warn("checkColumn", ex);
        int index;
        if((index = listFieldInfos.indexOf(field)) > -1)
          listFieldInfos.remove(index);
        if((index = bakFieldInfos.indexOf(field)) > -1)
          bakFieldInfos.remove(index);

        if(hashLookUp.get(fieldcode) != null)
          hashLookUp.remove(fieldcode);

        hashFieldInfos.remove(field.getFieldcode());

        allFieldInfos.remove(i);
        i--;
        continue;
      }
      Lookup lookup = (Lookup)hashLookUp.get(fieldcode);
      if(lookup != null)
        lookup.regData(ds, fieldcode);
    }
  }

  /**
   * 得到当前用户自定义的各个字段
   * @return 返回自定义的各个字段数组
   */
  public FieldInfo[] getBakFieldCodes()
  {
    return ListToFieldInfo(bakFieldInfos);
  }

  /**
   * 得到当前用户列表的各个字段
   * @return 返回字段数组
   */
  public FieldInfo[] getListFieldCodes()
  {
    return ListToFieldInfo(listFieldInfos);
  }

  /**
   * 得到所有可用的字段
   * @return 返回字段数组
   */
  public FieldInfo[] getAllFields()
  {
    return ListToFieldInfo(allFieldInfos);
  }

  /**
   * 将List实例对象转化为FieldInfo数组
   * @param list List实例对象
   * @return FieldInfo数组
   */
  private static FieldInfo[] ListToFieldInfo(List list)
  {
    FieldInfo[] temp = new FieldInfo[list.size()];
    list.toArray(temp);
    return temp;
  }

  /**
   * 根据字段编码得到其LookUp对象
   * @param fieldCode 字段编码
   * @return 返回LookUp对象
   */
  public Lookup getLookup(String fieldcode)
  {
    return (Lookup)hashLookUp.get(fieldcode);
  }

  /**
   * 释放资源
   */
  public void release()
  {
    if(dsUserField != null){
      dsUserField.closeDataSet();
      dsUserField = null;
    }
    dataSetProvider = null;
    listFieldInfos = null;
    bakFieldInfos = null;
    allFieldInfos = null;
    hashLookUp = null;
    hashFieldInfos = null;
  }
}

/**
 * 公共的数据表对象
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
final class PublicTable
{
  private static final String DEFAULT_FIELD_CODE_SQL                        //b.urlParams,
      = "SELECT b.fieldcode, b.fieldname, b.linktable, b.showFields, b.url, b.inputType, b.enumvalues, b.ordernum, b.isbak, b.isshow, b.isnull "
      + "FROM nodefield b WHERE b.tablecode = '?' ORDER BY isbak, ordernum";

  private static final String FIELD_CHANGE_SQL
      = "SELECT COUNT(*) FROM nodefield b WHERE b.ischange=1 AND b.tablecode = '?'";

  private static final String FIELD_UPDATE_SQL
      = "UPDATE nodefield b SET b.ischange = 0 WHERE b.ischange=1 AND b.tablecode = '?'";

  private static final String WHERE_CODE_SQL
      = "SELECT fieldCode, extendName, fieldCaption, dataType, linkTable, linkColumn, queryColumn, "
      + "opersign, inputType, span, enumValues, lookup, script, initValue, isShow, need "
      + "FROM whereField WHERE tablecode='?' ORDER BY orderNum";

  private static final String WHERE_CHANGE_SQL
      = "SELECT COUNT(*) FROM whereField WHERE tablecode='?' AND ischange=1";

  private static final String WHERE_UPDATE_SQL
      = "UPDATE whereField b SET b.ischange=0 WHERE b.tablecode='?' AND b.ischange=1";

  private static Hashtable tables = new Hashtable(50);

  private static Hashtable whereFields = new Hashtable(50);

  private EngineDataSetProvider dataSetProvider = new EngineDataSetProvider();

  private EngineDataSetResolver dataSetResolver = new EngineDataSetResolver();

  private EngineDataSet dsTableInfo = new EngineDataSet();
  //
  private EngineDataSet dsWhereInfo = new EngineDataSet();

  private Object lock = new Object();

  /**
   * 构造函数
   */
  public PublicTable()
  {
    dsTableInfo.setProvider(dataSetProvider);
    dsWhereInfo.setProvider(dataSetProvider);
  }

  /**
   * 得到表名相对应的所有用于查询的字段信息
   * @param tableCode 表名
   * @return 返回字段数组
   * @throws Exception 异常
   */
  QueryWhereField[] getWhereFields(String tableCode) throws Exception
  {
    String tablekey = tableCode.toLowerCase();
    QueryWhereField[] fields = null;
    String sql = StringUtils.combine(WHERE_CHANGE_SQL, "?", new String[]{tablekey});
    boolean isRefresh = false;
    synchronized(lock)
    {
      isRefresh = !dataSetProvider.getSequence(sql).equals("0");
      if(isRefresh)
      {
        fields = getWhereFieldsFromDB(tablekey);
        whereFields.put(tablekey, fields);
        sql = StringUtils.combine(WHERE_UPDATE_SQL, "?", new String[]{tableCode});
        dataSetResolver.updateQuery(new String[]{sql});
      }
      else{
        fields = (QueryWhereField[])whereFields.get(tablekey);
        if(fields == null)
        {
          fields = getWhereFieldsFromDB(tablekey);
          whereFields.put(tablekey, fields);
        }
      }
    }
    return fields;
  }

  /**
   * 得到表名相对应的所有字段
   * @param tableCode 表名
   * @return 返回字段数组
   * @throws Exception 异常
   */
  List getTableFields(String tableCode) throws Exception
  {
    String tablekey = tableCode.toLowerCase();
    List fields = null;
    String sql = StringUtils.combine(FIELD_CHANGE_SQL, "?", new String[]{tablekey});
    boolean isRefresh = false;
    synchronized(lock)
    {
      isRefresh = !dataSetProvider.getSequence(sql).equals("0");
      if(isRefresh)
      {
        fields = getFieldsFromDB(tablekey);
        tables.put(tablekey, fields);
        sql = StringUtils.combine(FIELD_UPDATE_SQL, "?", new String[]{tableCode});
        dataSetResolver.updateQuery(new String[]{sql});
      }
      else{
        fields = (List)tables.get(tablekey);
        if(fields == null)
        {
          fields = getFieldsFromDB(tablekey);
          tables.put(tablekey, fields);
        }
      }
    }
    return fields;
  }

  /**
   * 得到特定表的字段对象
   * @param tableCode 表名
   * @param fieldCode 字段名
   * @return 返回字段对象
   * @throws Exception 异常
   */
  FieldInfo getField(String tableCode, String fieldCode) throws Exception
  {
    List fields = getTableFields(tableCode);
    FieldInfo field = null;
    for(int i=0; i<fields.size(); i++)
    {
      field = (FieldInfo)fields.get(i);
      if(field.getFieldcode().equals(fieldCode.toLowerCase()))
        return field;
    }
    return null;
  }

  /**
   * 得到表字段
   * @param tableCode 表名
   * @return 返回该表包含字段的数组
   */
  private List getFieldsFromDB(String tableCode)
  {
    String sql = StringUtils.combine(DEFAULT_FIELD_CODE_SQL, "?", new String[]{tableCode});
    dsTableInfo.setQueryString(sql);
    if(dsTableInfo.isOpen())
      dsTableInfo.refresh();
    else
      dsTableInfo.openDataSet();

    List fields = new ArrayList(dsTableInfo.getRowCount());
    dsTableInfo.first();
    for(int i=0; i<dsTableInfo.getRowCount(); i++)
    {
      String linkTable = dsTableInfo.getValue("linkTable");
      String fieldCode = dsTableInfo.getValue("fieldcode");
      String fieldname = dsTableInfo.getValue("fieldname");
      String inputType = dsTableInfo.getValue("inputType");
      String enumvalues = dsTableInfo.getValue("enumvalues");
      String showFields = dsTableInfo.getValue("showFields");
      String url = dsTableInfo.getValue("url");
      //String urlParams = dsTableInfo.getValue("urlParams");
      boolean isBak = dsTableInfo.getValue("isBak").equals("1");
      boolean isShow = dsTableInfo.getValue("isShow").equals("1");
      boolean isNull = dsTableInfo.getValue("isnull").equals("1");
      FieldInfo field = new FieldInfo(fieldCode, fieldname, linkTable, inputType, enumvalues,
                                showFields, url, isBak, isShow);
      field.setIsNull(isNull);

      fields.add(field);
      dsTableInfo.next();
    }

    dsTableInfo.closeDataSet();
    return fields;
  }

  /**
   * 得到表字段
   * @param tableCode 表名
   * @return 返回该表包含字段的数组
   */
  private QueryWhereField[] getWhereFieldsFromDB(String tableCode)
  {
    String sql = StringUtils.combine(WHERE_CODE_SQL, "?", new String[]{tableCode});
    EngineDataSet ds = dsWhereInfo;
    ds.setQueryString(sql);
    if(ds.isOpen())
      ds.refresh();
    else
      ds.openDataSet();

    QueryWhereField[] fields = new QueryWhereField[ds.getRowCount()];
    ds.first();
    for(int i=0; i<fields.length; i++)
    {
      String fieldCode    = ds.getValue("fieldCode");
      String extendName   = ds.getValue("extendName");
      String fieldCaption = ds.getValue("fieldCaption");
      String dataType     = ds.getValue("dataType");
      String linkTable    = ds.getValue("linkTable");
      String linkColumn   = ds.getValue("linkColumn");
      String queryColumn  = ds.getValue("queryColumn");
      String opersign     = ds.getValue("opersign");
      String inputType    = ds.getValue("inputType");
      String span         = ds.getValue("span");
      String enumValues   = ds.getValue("enumValues");
      String lookup       = ds.getValue("lookup");
      String script       = ds.getValue("script");
      String initValue    = ds.getValue("initValue");
      boolean isShow      = ds.getValue("isShow").equals("1");
      boolean isNeed      = ds.getValue("need").equals("1");
      //
      fields[i] = new DefaultQueryWhereField(fieldCode, fieldCaption, dataType, linkTable, linkColumn,
          queryColumn, extendName, opersign, inputType, StringUtils.isTrue(span), enumValues, lookup,
          script, initValue, isShow, isNeed);

      ds.next();
    }

    ds.closeDataSet();
    return fields;
  }

}
