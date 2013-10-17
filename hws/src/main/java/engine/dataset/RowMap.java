package engine.dataset;

import java.util.*;
import javax.servlet.*;

import engine.util.StringUtils;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author 江海岛
 * @version 1.0
 */

public class RowMap extends Hashtable
{
  //private Hashtable rowInfo = null;

  private String id = null;

  public RowMap(){
    super();
  }

  /**
   * 构造函数
   * @param id 对象的id
   */
  public RowMap(String id){
    super();
    this.id = id;
  }

  /**
  * 创建一个新的对象, 并保存DataSet组件当前行数据到内部的哈希表中
  * @param ds DataSet组件
  */
  public RowMap(DataSet ds)
  {
    this(null, ds);
  }

  /**
   * 创建一个新的对象, 并保存DataSet组件当前行数据到内部的哈希表中
   * @param id 对象的id
   * @param ds DataSet组件
   */
  public RowMap(String id, DataSet ds)
  {
    super();
    this.id = id;
    put(ds);
  }

  public RowMap(String id, Map t)
  {
    super(t);
    this.id = id;
  }

  public RowMap(RowMap t)
  {
    put(t);
    this.id = t.id;
  }

  public String getId()
  {
    return this.id;
  }

  public void setId(String id)
  {
    this.id = id;
  }

  /**
   * 保存DataSet组件当前行数据到内部的哈希表中，如果表中的值已经存在，就会被替换。
   * @param ds DataSet组件
   */
  public void put(DataSet ds)
  {
    if(ds == null)
      return;

    Column[] cols = ds.getColumns();
    for(int i=0; i<cols.length; i++)
      this.put(cols[i].getColumnName().toUpperCase(), EngineDataSet.getValue(ds, i));
  }

  /**
   * 将另外一个RowMap的数据保存到当前RowMap中
   * @param t 另外一个RowMap
   */
  public void put(RowMap t) {
    Iterator i = t.entrySet().iterator();
    while (i.hasNext()) {
      Map.Entry e = (Map.Entry) i.next();
      super.put(e.getKey(), ((String[])e.getValue()).clone());
    }
  }

  /**
   * 保存request对象的数据到内部的哈希表中，如果表中的值已经存在，就会被替换。
   * @param request WEB请求的request对象
   */
  public void put(ServletRequest request)
  {
    Map map = request.getParameterMap();
    super.putAll(map);
  }

  /**
   * 保存request对象的特定数据到内部的哈希表中，如果表中的值已经存在，就会被替换。
   * @param request WEB请求的request对象
   * @param parameters WEB请求的request对象的参数数组
   */
  public void put(ServletRequest request, String[] parameters)
  {
    if(parameters == null)
      return;
    Map map = request.getParameterMap();
    for(int i=0; i<parameters.length; i++)
    {
      Object value = map.get(parameters[i]);
      this.put(parameters[i], value);
    }
  }

  /**
   * 保存request对象的特定数据到内部的哈希表中，如果表中的值已经存在，就会被替换。
   * @param request WEB请求的request对象
   * @param paramPrefixs WEB请求的request对象的前缀参数数组
   * @param sep 前缀参数与paramIndex的分割符号
   * @param paramIndex WEB请求的request对象的参数数组
   */
  public void put(ServletRequest request, String[] paramPrefixs, String sep, int paramIndex)
  {
    if(paramPrefixs == null)
      return;
    Map map = request.getParameterMap();
    for(int i=0; i<paramPrefixs.length; i++)
    {
      String key = paramPrefixs[i] + sep + paramIndex;
      Object value = map.get(key);
      this.put(paramPrefixs[i], value);
    }
  }

  /**
   * 保存数据。如果columnName==null将抛出NullPointException异常
   * @param columnName 列名
   * @param value[] 值
   */
  public void put(String columnName, String[] value)
  {
    super.put(columnName.toUpperCase(), value);
  }

  /**
   * 保存数据。如果columnName==null将抛出NullPointException异常
   * @param columnName 列名
   * @param value 值
   */
  public synchronized Object put(Object key, Object value)
  {
    if(key == null)
      throw new NullPointerException("the key must not null");
    if(!(key instanceof String))
      throw new IllegalArgumentException("the key must instance of the class of String ");

    String sKey = ((String)key).toUpperCase();
    if(value instanceof String)
      return super.put(sKey, new String[]{value == null ? "" : ((String)value).trim()});
    else if(value instanceof String[]){
      String[] values = (String[])value;
      for(int i=0; values!=null && i<values.length; i++)
        if(values[i] != null)
          values[i] = values[i].trim();

      return super.put(sKey, value);
    }
    else
      return null;
	}

  /**
   * 得到所包含的某一列的值
   * @param columnName 列名
   * @return 所有值，如果没有包含该列将返回空字符串;
   */
  public String get(String columnName)
  {
    String[] o = (String[])super.get(columnName.toUpperCase());
    if(o == null || o.length == 0 || o.length > 1)
      return "";
    return o[0];
  }

  /**
   * 剔除某一列的值
   * @param columnName 列名
   * @return 返回删除的值
   */
  public String[] remove(String columnName)
  {
    return (String[])super.remove(columnName.toUpperCase());
  }

  /**
   * 得到所包含的某一列的值(将半角的引号转化为全角的引号)
   * @param columnName 列名
   * @return 所有值，如果没有包含该列将返回空字符串;
   */
  public String getSBCcase(String columnname)
  {
    return StringUtils.replaceQuotatemark(get(columnname));
  }

  public String[] getValues(String columnName)
  {
    return (String[])super.get(columnName.toUpperCase());
  }

  /**
   * 得到所包含的所有列名（即Hashtable的所有key）
   * @return 以Enumeration类型返回所有列名
   */
  public Enumeration columnNames()
  {
    return super.keys();
  }

  /**
   * 将RowMap数组总的数据
   * @param fieldNames 字段名称
   */
  public void setDataSetValues(DataSet ds, String[] fieldNames)
  {
    if(fieldNames == null || ds == null)
      return;
    for(int i=0; i<fieldNames.length; i++)
    {
      int ordinal = ds.getColumn(fieldNames[i]).getOrdinal();
      EngineDataSet.setValue(ds, ordinal, get(fieldNames[i]));
    }
    //ds.post();
  }

  /**
   * 将RowMap数组总的数据
   * @param ordinals 字段顺序数组
   */
  public void setDataSetValues(DataSet ds, int[] ordinals)
  {
    if(ordinals == null || ds == null)
      return;
    for(int i=0; i<ordinals.length; i++)
    {
      String fieldname = ds.getColumn(ordinals[i]).getColumnName();
      EngineDataSet.setValue(ds, ordinals[i], get(fieldname));
    }
    //ds.post();
  }
}