package engine.web.lookup;

import java.util.Map;
import java.util.Hashtable;
import java.io.Serializable;

import engine.util.log.Log;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

/**
 * 保存LookUp的助手的类
 */
public final class LookupHelper implements Serializable
{
  private final static String SELECT = "SELECT";
  private final static String FROM   = "FROM";
  private final static String COUNT  = " COUNT(*) ";
  private final static String ORDER_BY = "ORDER BY ";

  private String   select = null;
  private String[] keyColumns = null;
  private String   sortStr = null;
  private String[] capsColumn = null;
  private String[] regkeys = null;
  private boolean  isOneOpen = false;
  private boolean  isList    = false;
  //public  boolean isTree = false;
  private String   treeCode = null;
  private LookupParam[] wheres = null;
  private LookupParam[] filters = null;

  private String initListenerClass = null;
  private LookupInitListener initListener = null;

  private static Log log = new Log("LookupHelper");


  /**
   * 构造函数
   * @param fields 字段字符串: field1,fields
   * @param tableName 表名
   * @param select SELECT field1,fields FROM tableName
   * @param keys 主键
   * @param sorts 排序字符串
   * @param caps 显示字段
   * @param isOneOpen 是否只打开一次
   * @param isList 是否是下拉框数据用的。若该lookup是一次性打开的。
   * 无论配置文件的属性值是否是list, 都将返回true, 下拉框数据用的不需要注册。
   * @param treeCode 树状编码
   */
  public LookupHelper(String select, String sorts, String[] keys, String[] regkeys, String[] caps,
                      boolean isOneOpen, boolean isList, String treeCode)
  {
    this.select = select;
    //this.whereClause = whereClause;
    this.keyColumns = keys;
    this.sortStr = sorts!=null && sorts.length() > 0 ?
                   new StringBuffer(ORDER_BY).append(sorts).toString() : "";
    this.capsColumn = caps;
    this.regkeys = regkeys;
    this.isOneOpen = isOneOpen;
    this.isList = isList;
    this.treeCode = treeCode == null ? null : treeCode.trim();
    //this.filterKey = filterKey;
  }

  void setWheres(LookupParam[] wheres)
  {
    this.wheres = wheres;
  }

  void setFilters(LookupParam[] filters)
  {
    this.filters = filters;
  }
  /**
   * 是否是一次性打开, 即保存状态是application的。所有用户供应的
   * @return
   */
  public boolean isOneOpen()
  {
    return isOneOpen;
  }

  /**
   * 是否是下拉框数据用的。若该lookup是一次性打开的。无论配置文件的属性值是否是list, 都将返回true
   * 下拉框数据用的不需要注册。
   * @return 是否是下拉框数据用的。
   */
  public boolean isList()
  {
    return isOneOpen ? true : isList;
  }

  /**
   * 是否是树状结构
   * @return 是否是树状结构
   */
  public boolean isTree(){
    return treeCode != null && treeCode.length() > 0;
  }

  public String[] getCaptionColumns()
  {
    return capsColumn;
  }

  public String[] getKeyColumns()
  {
    return keyColumns;
  }

  /**
   * 得到注册用的字段数组。若为null,则返回主键字段数组
   * @return 注册用的字段数组
   */
  public String[] getRegKeyColumns(){
    return regkeys == null ? keyColumns : regkeys;
  }

  public String getSelect()
  {
    return this.select;
    //return new StringBuffer(SELECT).append(fields).append(FROM).append(tableName).toString();
  }

  public String getCountSelect()
  {
    String uppSelect = select.toUpperCase();
    int selectIndex = select.indexOf(SELECT);
    int fromIndex = select.indexOf(FROM);
    StringBuffer buf = new StringBuffer(select.substring(0, selectIndex+6));
    buf.append(COUNT).append(select.substring(fromIndex));
    return buf.toString();
  }

  public String getSortStr()
  {
    return sortStr;
  }

  public String getTreeCode()
  {
    return treeCode;
  }

  public LookupParam[] getWheres()
  {
    if(wheres == null || wheres.length == 0)
      return null;
    LookupParam[] params = new LookupParam[wheres.length];
    for(int i=0; i<params.length; i++)
      params[i] = (LookupParam)wheres[i].clone();
    return params;
  }

  public LookupParam[] getFilters()
  {
    if(filters == null || filters.length == 0)
      return null;
    LookupParam[] params = new LookupParam[filters.length];
    for(int i=0; i<params.length; i++)
      params[i] = (LookupParam)filters[i].clone();
    return params;
  }

  public LookupInitListener getInitListener()
  {
    if(this.initListenerClass == null)
      return null;
    if(this.initListener == null)
    {
      try{
        initListener = (LookupInitListener)Class.forName(initListenerClass).newInstance();
      }
      catch(ClassNotFoundException ex){
        log.warn("Not found the class of "+initListenerClass, ex);
      }
      catch(ClassCastException ex){
        log.warn("Incorrect cast class engine.web.lookup.LookupInitListener for class "+initListenerClass, ex);
      }
      catch(Exception ex){
        log.warn("Exception for get instance of class engine.web.lookup.LookupInitListene", ex);
      }
    }
    return initListener;
  }

  public void setInitListenerClass(String initListenerClass)
  {
    this.initListenerClass = initListenerClass;
    this.initListener = null;
  }
}
