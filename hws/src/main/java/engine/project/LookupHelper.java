package engine.project;

import java.util.Map;
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
final class LookupHelper implements java.io.Serializable
{
  public  String select = null;    //Select 语句
  public  String[] whereClause = null; //where子句的字段名称
  public  String[] keyColumns = null; //主键数组
  public  String sortStr = null;//排序字段
  public  String[] capsColumn = null;//显示字段
  private boolean isOneOpen = false; //是否只打开一次
  public  boolean isTree = false;    //是否是树状结构
  public  String treeCode = null;    //树状结构的记录关键排序编码
  public  String[] filterKey = null; //过滤字段名
  /**
   * 构造函数
   * @param select Select 语句
   * @param whereClause where子句的字段名称
   * @param key 主键
   * @param sortStr 排序字符串
   * @param caps 显示字段
   */
  public LookupHelper(String select, String[] whereClause, String key, String sortStr, String[] caps)
  {
    this(select, whereClause, key, sortStr, caps, false);
  }

  /**
   * 构造函数
   * @param select Select 语句
   * @param whereClause where子句的字段名称
   * @param key 主键
   * @param sortStr 排序字符串
   * @param caps 显示字段
   * @param isOneOpen 是否只打开一次
   */
  public LookupHelper(String select, String[] whereClause, String key, String sortStr,
              String[] caps, boolean isOneOpen)
  {
    this(select, whereClause, key, sortStr, caps, isOneOpen, null);
  }

  public LookupHelper(String select, String[] whereClause, String key, String sortStr,
              String[] caps, boolean isOneOpen, String[] filterKey)
  {
    this(select, whereClause, key, sortStr, caps, isOneOpen, false, null, filterKey);
  }

  public LookupHelper(String select, String[] whereClause, String key, String sortStr,
              String[] caps, boolean isOneOpen, boolean isTree, String treeCode)
  {
    this(select, whereClause, key, sortStr, caps, isOneOpen, isTree, treeCode, null);
  }
  /**
   * 构造函数
   * @param select Select 语句
   * @param whereClause where子句的字段名称
   * @param key 主键
   * @param sortStr 排序字符串
   * @param caps 显示字段
   * @param isOneOpen 是否只打开一次
   * @param isTree 是否树状视图
   * @param treeCode 树状编码
   * @param existMap 存在的各个条件和值
   */
  public LookupHelper(String select, String[] whereClause, String key, String sortStr,
              String[] caps, boolean isOneOpen, boolean isTree, String treeCode, String[] filterKey)
  {
    this(select, whereClause, new String[]{key}, sortStr, caps, isOneOpen, isTree,
         treeCode, filterKey);
  }

  /**
   * 构造函数
   * @param select Select 语句
   * @param whereClause where子句的字段名称
   * @param key 主键
   * @param sortStr 排序字符串
   * @param caps 显示字段
   * @param isOneOpen 是否只打开一次
   * @param isTree 是否树状视图
   * @param treeCode 树状编码
   * @param existMap 存在的各个条件和值
   */
  public LookupHelper(String select, String[] whereClause, String[] keys, String sortStr,
              String[] caps, boolean isOneOpen, boolean isTree, String treeCode, String[] filterKey)
  {
    this.select = select;
    this.whereClause = whereClause;
    this.keyColumns = keys;
    this.sortStr = sortStr;
    this.capsColumn = caps;
    this.isOneOpen = whereClause == null ? isOneOpen : false;//若where子句的字段名称不为null, 则表示不是一次性打开数据集
    this.treeCode = treeCode;
    this.isTree = treeCode == null ? false : isTree;//若树状结构的关键排序编码为null, 则表示不是树状结构
    this.filterKey = filterKey;
  }

  /**
   * 是否是一次性打开
   * @return
   */
  public boolean isOneOpen()
  {
    return whereClause == null && isOneOpen;
  }
}
