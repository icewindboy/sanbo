package engine.html;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Hashtable;
import java.util.Enumeration;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import engine.action.*;
import engine.dataset.*;
import engine.dataset.sql.QueryWhereField;
import engine.dataset.sql.DefaultQueryWhereField;
import engine.project.LookupBeanFacade;
import engine.project.LookUp;
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

public final class TableInfo implements java.io.Serializable//extends BaseAction
{
  /*private static final String USER_FIELD_CHANGE_SQL
      = "SELECT COUNT(*) FROM empfield a, nodefield b "
      + "WHERE b.nodefieldid = a.nodefieldid AND b.ischange = 1 AND b.tablecode = '?' AND a.personid = ? ";
  */
  private static final String USER_FIELD_CODE_SQL
      = "SELECT b.fieldcode "
      + "FROM empfield a, nodefield b  WHERE b.tablecode = '?' AND a.personid = ? "
      + "AND  b.isshow = 1 AND b.nodefieldid = a.nodefieldid ORDER BY isbak, ordernum";

  private static PublicTable pubTable = new PublicTable();

  private EngineDataSetProvider dataSetProvider = new EngineDataSetProvider();
  //private EngineDataSet dsTableInfo = new EngineDataSet();
  private EngineDataSet dsUserField = new EngineDataSet();

  private List listFieldInfos = null;  //保存列表的字段数组
  private List bakFieldInfos =  null;  //保存备用的字段数组
  private List allFieldInfos = null;   //用来所有字段信息的对象
  private Map  hashLookUp = null;      //保存LookUp的实例
          Map  hashFieldInfos = null;  //保存所有的字段信息 //key 字段名, value 字段对象

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
    //取得全部字段的信息
    allFieldInfos = pubTable.getTableFields(tableName);
    int allcount = allFieldInfos.size();
    //初始化
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

    //提取用自定义的信息
    String SQL = BaseAction.combineSQL(USER_FIELD_CODE_SQL, "?", new String[]{tableName, personid});
    dsUserField.setQueryString(SQL);
    if(!dsUserField.isOpen())
      dsUserField.openDataSet();
    else{
      dsUserField.refresh();
    }
    int count = dsUserField.getRowCount();
    //用户是否有自选择的字段, 若有用用户自选择的字段名
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
    //处理字段
    for(int i=0; i<allcount; i++)
    {
      FieldInfo field = (FieldInfo)allFieldInfos.get(i);
      //得到LookUp实例对象
      String linkTable = field.getLinktable();
      LookUp lookup = linkTable == null ? null : LookupBeanFacade.getInstance(request, linkTable);
      if(lookup != null)
        hashLookUp.put(field.getFieldcode(), lookup);

      hashFieldInfos.put(field.getFieldcode(), field);
      //是否是显示字段
      if(field.isShow())
      {
        //用户没有自定义字段
        if(isDefault)
          listFieldInfos.add(field);
        else if(userFieldCodes.contains(field.getFieldcode()))
          listFieldInfos.add(field);
      }
      //是否备用字段
      if(field.isBak())
        bakFieldInfos.add(field);
    }

    if(!isDefault)
      dsUserField.closeDataSet();
    //设置没有校验字段
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
      //如果是虚拟字段，就不用管
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
      LookUp lookup = (LookUp)hashLookUp.get(fieldcode);
      if(lookup != null)
        lookup.regData(ds, fieldcode);
    }
  }

  /**
   * 得到字段或用户自己定义字段,修改状态的显示字符串
   * @param fieldCode 字段编码
   * @param fieldValue 字段在数据库中的值
   * @param style 控件的风格
   * @param isReadOnly 是否只读
   * @param isReadOnly 是否需要返回包含控件的代码
   * @param isInput 是否输入控件
   * @return 返回的字符串
   * @throws Exception 异常
  public String transFieldValue(FieldInfo field, String fieldValue,
                                 String inputName, String style, boolean isReadOnly, boolean isInput) throws Exception
  {
    if(field == null)
      return fieldValue;

    synchronized(field){
      String linkTable = field.getLinktable();
      int input = field.getType();
      StringBuffer buf = null;
      //是外键字段
      if(linkTable!=null)
      {
        LookUp lookupBean = (LookUp)hashLookUp.get(field.getFieldcode());
        if(lookupBean == null)
          return "";
        if(!isInput)
          return lookupBean.getLookupName(fieldValue);
        else
        {
          buf = new StringBuffer("<input type='text' name='");
          buf.append(inputName).append("' id='").append(inputName).append("' value='");
          buf.append(lookupBean.getLookupName(fieldValue)).append("' ");
          buf.append(isReadOnly ? style+" readonly" : style).append(">");
        }
        return buf.toString();
      }
      //表示是枚举型字段
      else if(input == FieldInfo.SELECT_TYPE)
      {
        List[] enumValues = field.getEnumvalues();
        int index = enumValues[0].indexOf(fieldValue);
        if(!isInput)
          return index > -1 ? (String)enumValues[1].get(index) : fieldValue;
        else
        {
          buf = new StringBuffer(isReadOnly ? "<input type='text' name='" : "<select name='");
          buf.append(inputName).append("' id='").append(inputName);
          if(isReadOnly)
            buf.append("' readonly value='").append(index > -1 ? (String)enumValues[0].get(index) : fieldValue).append("' ");
          if(style != null)
            buf.append(style).append(">");
          if(!isReadOnly)
            buf.append("<option value=''></option>").append(BaseAction.listToOption(enumValues, index)).append("</select>");
          return buf.toString();
        }
      }
      //不用带控件代码
      else if(!isInput)
        return fieldValue;
      //文本型字段
      else if(input == FieldInfo.MEMO_TYPE)
      {
        //<textarea name="bz" rows="3" style="width:690" readonly>fieldValue</textarea>
        buf = new StringBuffer("<textarea rows='3' name='").append(inputName);
        buf.append("' id='").append(inputName).append("' ").append(style);
        if(isReadOnly)
          buf.append(" readonly");
        buf.append(">").append(fieldValue).append("</textarea>");
        return buf.toString();
      }
      else
      {
        buf = new StringBuffer("<input type='text' name='").append(inputName);
        buf.append("' id='").append(inputName).append("' value='").append(fieldValue).append("' ").append(style);
        if(isReadOnly)
          buf.append(" readonly");
        buf.append(">");
        return buf.toString();
      }
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
  public LookUp getLookUp(String fieldcode)
  {
    return (LookUp)hashLookUp.get(fieldcode);
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
  //提取字段信息
  private static final String DEFAULT_FIELD_CODE_SQL                        //b.urlParams,
      = "SELECT b.fieldcode, b.fieldname, b.linktable, b.showFields, b.url, b.inputType, b.enumvalues, b.ordernum, b.isbak, b.isshow, b.isnull "
      + "FROM nodefield b WHERE b.tablecode = '?' ORDER BY isbak, ordernum";
  //得到字段信息变更数量
  private static final String FIELD_CHANGE_SQL
      = "SELECT COUNT(*) FROM nodefield b WHERE b.ischange=1 AND b.tablecode = '?'";
  //更新字段信息
  private static final String FIELD_UPDATE_SQL
      = "UPDATE nodefield b SET b.ischange = 0 WHERE b.ischange=1 AND b.tablecode = '?'";
  //提取查询条件信息
  private static final String WHERE_CODE_SQL
      = "SELECT fieldCode, extendName, fieldCaption, dataType, linkTable, linkColumn, queryColumn, "
      + "opersign, inputType, span, enumValues, lookup, script, initValue, isShow, need "
      + "FROM whereField WHERE tablecode='?' ORDER BY orderNum";
  //提取查询条件记录变更数量
  private static final String WHERE_CHANGE_SQL
      = "SELECT COUNT(*) FROM whereField WHERE tablecode='?' AND ischange=1";
  //更新查询条件信息
  private static final String WHERE_UPDATE_SQL
      = "UPDATE whereField b SET b.ischange=0 WHERE b.tablecode='?' AND b.ischange=1";

  //保存的表对象的hash表
  private static Hashtable tables = new Hashtable(50);
  //保存表的查询字段信息的hash表
  private static Hashtable whereFields = new Hashtable(50);
  //数据提供者
  private EngineDataSetProvider dataSetProvider = new EngineDataSetProvider();
  //数据更新者
  private EngineDataSetResolver dataSetResolver = new EngineDataSetResolver();
  //数据集
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
    String sql = BaseAction.combineSQL(WHERE_CHANGE_SQL, "?", new String[]{tablekey});
    boolean isRefresh = false;
    synchronized(lock)
    {
      //是否需要刷新（数据库的字段是否已经被更改）
      isRefresh = !dataSetProvider.getSequence(sql).equals("0");
      if(isRefresh)
      {
        fields = getWhereFieldsFromDB(tablekey);
        whereFields.put(tablekey, fields);
        //更新更改字段的相应记录
        sql = BaseAction.combineSQL(WHERE_UPDATE_SQL, "?", new String[]{tableCode});
        dataSetResolver.updateQuery(new String[]{sql});
      }
      else{
        fields = (QueryWhereField[])whereFields.get(tablekey);
        //缓存池是否已经有该对象
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
    String sql = BaseAction.combineSQL(FIELD_CHANGE_SQL, "?", new String[]{tablekey});
    boolean isRefresh = false;
    synchronized(lock)
    {
      //是否需要刷新（数据库的字段是否已经被更改）
      isRefresh = !dataSetProvider.getSequence(sql).equals("0");
      if(isRefresh)
      {
        fields = getFieldsFromDB(tablekey);
        tables.put(tablekey, fields);
        //更新更改字段的相应记录
        sql = BaseAction.combineSQL(FIELD_UPDATE_SQL, "?", new String[]{tableCode});
        dataSetResolver.updateQuery(new String[]{sql});
      }
      else{
        fields = (List)tables.get(tablekey);
        //缓存池是否已经有该对象
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
   * 得到特定字段中,所有可能要显示的字段（用于具有linktable的字段, 若没有则返回自己）
   * @param field 字段对象
   * @return 所有可能要显示的字段
   * @throws Exception 异常

  private void processShowFields(FieldInfo field) throws Exception
  {
    String[] showFields = field.getShowFields();
    String linkTable = field.getLinktable();
    if(showFields == null || linkTable == null)
      field.setShowFieldInfos(null);
    else
    {
      List showList = new ArrayList(showFields.length);
      for(int i=0; i<showFields.length; i++)
      {
        if(showFields[i] == null)
          continue;
        FieldInfo temp = getField(linkTable, showFields[i]);
        if(temp != null)
          showList.add(temp);
      }
      field.setShowFieldInfos(showList.size() == 0 ? null : showList);
    }
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
    String sql = BaseAction.combineSQL(DEFAULT_FIELD_CODE_SQL, "?", new String[]{tableCode});
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
    //释放资源
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
    String sql = BaseAction.combineSQL(WHERE_CODE_SQL, "?", new String[]{tableCode});
    EngineDataSet ds = dsWhereInfo;
    ds.setQueryString(sql);
    if(ds.isOpen())
      ds.refresh();
    else
      ds.openDataSet();

    QueryWhereField[] fields = new QueryWhereField[ds.getRowCount()];
    ds.first();
    for(int i=0; i<fields.length; i++)
    { //fieldCode, extendName, fieldCaption, dataType, linkTable, linkColumn, queryColumn, opersign, inputType,
      //span, enumValues, lookup, script
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
    //释放资源
    ds.closeDataSet();
    return fields;
  }

}
