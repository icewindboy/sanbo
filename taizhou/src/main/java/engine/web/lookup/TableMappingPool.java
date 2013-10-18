package engine.web.lookup;

//import java.util.Map;
import java.util.ArrayList;
import java.util.Hashtable;
import java.io.Serializable;

import engine.web.lookup.Lookup;
/**
 * <p>Title: 表触发lookupbean缓冲池</p>
 * <p>Description: 表更新(添加，删除，修改)触发lookupbean刷新</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public final class TableMappingPool implements Serializable
{
  private static Hashtable mapTable = new Hashtable(50, 1);

  /**
   * 根据xml解析的元素对象生成LookupHelper对象
   * @param element LookupHelper对象对应的Element元素
   * @return 返回生成LookupHelper对象
   */
  public synchronized static void put(String table, String lookup)
  {
    if(table == null)
      return;
    table = table.toUpperCase();
    ArrayList lookupNames = (ArrayList)mapTable.get(table);
    if(lookupNames == null){
      lookupNames = new ArrayList();
      lookupNames.add(lookup);
      mapTable.put(table, lookupNames);
    }
    else if(lookupNames.indexOf(lookup) < 0)
      lookupNames.add(lookup);
  }

  /**
   * table数据更新时，映射到lookupbean. DatabaseResolver用到此方法
   * @param table 触发的表名称
   */
  public synchronized static void mappingLookup(String table){
    if(table == null)
      return;
    table = table.toUpperCase();
    ArrayList lookupNames = (ArrayList)mapTable.get(table);
    if(lookupNames == null)
      return;
    for(int i=0; i<lookupNames.size(); i++){
      String lookupName = (String)lookupNames.get(i);
      Lookup lookupBean = (Lookup)LookupPool.getLookup(lookupName);
      if(lookupBean != null)
        lookupBean.refresh();
    }
  }

  public synchronized static void clear()
  {
    mapTable.clear();
  }
}