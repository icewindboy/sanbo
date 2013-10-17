package engine.util.version;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Title: 版本对象类</p>
 * <p>Description: 版本对象类<br>
 * The immutable Version object describes a particular version of something: a product, API, etc.<br>
 * It is used in conjunction with instances of VersionRange to declare support;<br>
 * e.g. does a server support a particular version of an API.<br>
 * The server returns the supported VersionRange,<br>
 * which is compared against the Version returned for the API<br><br>
 * A version can have an any number of levels;
 * the first four are given (somewhat arbitrary) names,
 * and convenience constructors and accessors.
 * They are: <br>
 * major<br>
 * minor<br>
 * patch<br>
 * build<br>
 * For example, you might have version 5.0.1.47
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */
public final class Version implements Comparable
{
  private String versionString;
  private int    iLevels[];
  private String sLevels[];
  private int    levelLength;
  private static final int h = 3;
  private static final int e = 2;
  private static final int f = 1;
  private static final int g = 0;

  public Version(String[] args)
  {
    check(args);
  }

  public Version(int[] args)
  {
    String as[] = new String[args.length];
    for(int i = 0; i < args.length; i++)
      as[i] = String.valueOf(args[i]);

    check(as);
  }

  public Version(String major, String minor, String patch, String build)
  {
    check(new String[] {major, minor, patch, build});
  }

  public Version(int major, int minor, int patch, int build)
  {
    this(String.valueOf(major), String.valueOf(minor), String.valueOf(patch), String.valueOf(build));
  }

  public Version(String major, String minor, String patch)
  {
    this(major, minor, patch, null);
  }

  public Version(int major, int minor, int patch)
  {
    this(String.valueOf(major), String.valueOf(minor), String.valueOf(patch));
  }

  public Version(String major, String minor)
  {
    this(major, minor, null, null);
  }

  public Version(int major, int minor)
  {
    this(String.valueOf(major), String.valueOf(minor));
  }

  public Version(String complete)
  {
    this(parseComplete(complete));
  }

  /**
   * compareTo in interface java.lang.Comparable<br>
   * a negative integer, zero, or a positive integer as this object is less than, equal to,
   * or greater than the specified object
   * 负数，零，正数分别表示小于，等于，大于 指定的对象
   * @param obj 需要比较的对象
   * @return 返回比较结果
   */
  public int compareTo(Object obj)
  {
    if(this == obj)
      return 0;
    Version version = (Version)obj;
    int i = Math.min(levelLength, version.levelLength);
    for(int j = 0; j < i; j++)
    {
      if(iLevels[j] > -1 && version.iLevels[j] > -1){
        if(iLevels[j] < version.iLevels[j])
          return -(j + 1);
        if(iLevels[j] > version.iLevels[j])
          return j + 1;
        continue;
      }
      if(iLevels[j] <= -1 && version.iLevels[j] <= -1)
      {
        int i1 = sLevels[j].compareTo(version.sLevels[j]);
        if(i1 != 0)
          return i1;
      }
      else
      {
        int j1 = j + 1;
        return iLevels[j] <= -1 ? j1 : -j1;
      }
    }

    if(levelLength < version.levelLength)
    {
      for(int k = i; k < version.levelLength; k++)
        if(version.iLevels[k] != 0)
          return -(k + 1);

    }
    else if(levelLength > version.levelLength)
    {
      for(int l = i; l < levelLength; l++)
        if(iLevels[l] != 0)
          return l + 1;

    }
    return 0;
  }

  public int hashCode()
  {
    int i = 0;
    for(int j = 0; j < 4; j++)
    {
      int k = j >= iLevels.length ? 0 : iLevels[j] <= -1 ? sLevels[j].hashCode() : iLevels[j];
      i <<= 8;
      i ^= k;
    }

    return i;
  }

  public boolean equals(Object obj)
  {
    if(this == obj)
      return true;
    if(obj instanceof Version)
      return compareTo(obj) == 0;
    else
      return false;
  }

  public String toString()
  {
    return versionString;
  }

  public Object getLevel(int i)
  {
    if(i < iLevels.length)
    {
      if(iLevels[i] > -1)
        return new Integer(iLevels[i]);
      else
        return sLevels[i];
    } else
    {
      return new Integer(0);
    }
  }

  public Object getBuildLevel()
  {
    return getLevel(3);
  }

  public Object getPatchLevel()
  {
    return getLevel(2);
  }

  public Object getMinorLevel()
  {
    return getLevel(1);
  }

  public Object getMajorLevel()
  {
    return getLevel(0);
  }

  private int a(int i)
  {
    return ((Integer)getLevel(i)).intValue();
  }

  public int getBuild()
  {
    return a(3);
  }

  public int getPatch()
  {
    return a(2);
  }

  public int getMinor()
  {
    return a(1);
  }

  public int getMajor()
  {
    return a(0);
  }

  public static String[] parseComplete(String s)
  {
    ArrayList arraylist = new ArrayList();
    s = s.trim();
    boolean flag = false;
    StringBuffer stringbuffer = new StringBuffer();
    for(int i = 0; i < s.length(); i++)
    {
      char c1 = s.charAt(i);
      if(flag && !Character.isDigit(c1))
      {
        if(stringbuffer.length() > 0)
          arraylist.add(stringbuffer.toString());
        stringbuffer.setLength(0);
        flag = false;
      }
      else if(!flag && Character.isDigit(c1))
        flag = true;
      if(c1 != '.')
        stringbuffer.append(c1);
    }

    if(stringbuffer.length() > 0)
      arraylist.add(stringbuffer.toString());
    return (String[])arraylist.toArray(new String[arraylist.size()]);
  }

  private void format(String as[])
  {
    StringBuffer stringbuffer = new StringBuffer();
    stringbuffer.append(as[0]);
    for(int i = 1; i < as.length && as[i] != null; i++)
    {
      if(Character.isDigit(as[i].charAt(0)))
        stringbuffer.append('.');
      stringbuffer.append(as[i]);
    }

    versionString = stringbuffer.toString();
  }

  private void format(String as[], int i)
  {
    String s = as[i].trim();
    if(s.length() == 0)
    {
      s = "0";
      as[i] = s;
    }
    StringBuffer stringbuffer = new StringBuffer();
    int j = 0;
    do
    {
      if(j >= s.length())
        break;
      char c1 = s.charAt(j);
      if(Character.isDigit(c1))
      {
        s = s.substring(j);
        break;
      }
      stringbuffer.append(c1);
      j++;
    }
    while(true);
    //
    sLevels[i] = stringbuffer.toString();
    if(sLevels[i].equals(s))
    {
      iLevels[i] = -1;
      return;
    }
    try
    {
      iLevels[i] = Integer.parseInt(s);
    }
    catch(NumberFormatException numberformatexception)
    {
      throw new IllegalArgumentException("Non-numeric characters after numeric value: " + as[i]);
    }
  }

  private void check(String as[])
  {
    levelLength = as.length;
    if(levelLength == 0 || as[0].trim().length() == 0)
      throw new IllegalArgumentException("lawless version!");
    iLevels = new int[levelLength];
    sLevels = new String[levelLength];
    for(int i = 0; i < as.length && as[i] != null; i++)
      format(as, i);

    format(as);
  }
}
