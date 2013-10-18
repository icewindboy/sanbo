package engine.project;

import java.util.ArrayList;
/**
 * <p>Title: Lookup参数对象</p>
 * <p>Description: 用于保存参数字段名称和参数值数组</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public final class LookupParam implements java.io.Serializable{
  private String key = null;
  private String[] values = null;

  public LookupParam(){}

  public LookupParam(String key, String[] values)
  {
    this.key = key;
    setValues(values);
  }

  public String getKey()
  {
    return this.key;
  }

  public void setKey(String key)
  {
    this.key = key;
  }

  public String[] getValues()
  {
    return this.values;
  }

  public void setValues(String[] values)
  {
    this.values = values == null || values.length ==0 ? null : values;
  }

  /**
   * 合并Lookup参数对象数组。若后面的数组中的LookupParam对象的key含有前面的数组中LookupParam对象的key，
   * 则覆盖前面的
   * @param before 前面数组
   * @param after 后面数组
   * @return 返回合并后的新数组
   */
  public synchronized static LookupParam[] union(LookupParam[] before, LookupParam[] after)
  {
    int num = (before == null ? 0 : before.length) + (after == null ? 0 : after.length);
    ArrayList list = new ArrayList(num);
    ArrayList keys = new ArrayList();
    for(int i=0; before!=null && i<before.length; i++)
    {
      String key = before[i].getKey();
      if(keys.indexOf(key) < 0){
        keys.add(key);
        list.add(before[i]);
      }
    }
    keys.clear();
    for(int i=0; after!=null && i<after.length; i++)
    {
      String key = after[i].getKey();
      if(keys.indexOf(key) < 0){
        keys.add(key);
        list.add(after[i]);
      }
    }
    return list.size() == 0 ? null : (LookupParam[])list.toArray(new LookupParam[list.size()]);
  }

  /**
   * 通过LookupParam对象的key的名称再数组中得到与该key相等的对象
   * @param lookupParams  LookupParam对象数值
   * @param key LookupParam对象的key
   * @return 返回LookupParam对象
   */
  public synchronized static LookupParam getLookupParam(LookupParam[] lookupParams, String key)
  {
    int index = indexOf(lookupParams, key);
    return index < 0 ? null : lookupParams[index];
  }

  /**
   * 通过LookupParam对象的key的名称再数组中得到与该key相等的对象的下标
   * @param lookupParams  LookupParam对象数值
   * @param key LookupParam对象的key
   * @return 返回在LookupParam数组中的下标
   */
  public synchronized static int indexOf(LookupParam[] lookupParams, String key)
  {
    int index = -1;
    if(lookupParams == null || key == null)
      return index;
    for(int i=0; i<lookupParams.length; i++)
    {
      if(lookupParams[i] == null)
        continue;
      if(key.equals(lookupParams[i].getKey())){
        index = i;
        break;
      }
    }
    return index;
  }
}