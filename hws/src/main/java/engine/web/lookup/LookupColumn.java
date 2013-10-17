package engine.web.lookup;

/**
 * <p>Title: Lookup列对象</p>
 * <p>Description: 用于保存主键名称和主键值</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public final class LookupColumn
{
  private String key = null;
  private String value = null;

  public LookupColumn(){}

  public LookupColumn(String key, String value)
  {
    this.key = key;
    this.value = value;
  }

  public String getKey()
  {
    return this.key;
  }

  public String getValue()
  {
    return this.value;
  }

  public void setKey(String key)
  {
    this.key = key;
  }

  public void setValue(String value)
  {
    this.value = value;
  }
}