package engine.web.lookup;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.Iterator;
/**
 * <p>Title: Lookup对象的缓冲池</p>
 * <p>Description: Lookup对象的缓冲池</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */
final class LookupPool implements Serializable
{
  private static Hashtable lookupTable = new Hashtable(100, 1);

  /**
   * 在缓冲池中得到Lookup对象
   * @return 返回Lookup对象，没有此对像，将返回null
   */
  public synchronized static Lookup getLookup(String name)
  {
    return (Lookup)lookupTable.get(name);
  }

  /**
   * 在缓冲池中添加Lookup对象
   * @param name 对象名称
   * @param lookup Lookup对象
   */
  public synchronized static void put(String name, Lookup lookup)
  {
    lookupTable.put(name, lookup);
  }

  /**
   * 从缓冲池中移去一个Lookup对象
   * @param name 对象名称
   * @return 返回移去的Lookup对象
   */
  public synchronized static Lookup remove(String name)
  {
    return (Lookup)lookupTable.remove(name);
  }

  /**
   * 清楚缓冲池中总的所有对象
   */
  public synchronized static void clear()
  {
    if(lookupTable.size() > 0)
    {
      Iterator iterator = lookupTable.values().iterator();
      while(iterator.hasNext()){
        Lookup lookup = (Lookup)iterator.next();
        lookup.release();
      }
      lookupTable.clear();
    }
  }

  /**
   * 缓冲池包含的对象的个数
   */
  public synchronized static int size(){
    return lookupTable.size();
  }
}