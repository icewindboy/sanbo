package engine.util.version;

/**
 * <p>Title: 版本对象范围类/p>
 * <p>Description: 版本对象范围类<br>
 * Describes a range of versions that can be tested against individual Version objects
 * to see whether that version is in range.
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */
public final class VersionRange
{

    private Version e[];
    private boolean c;
    private boolean a;
    private Version d;
    private Version b;

    public String toString()
    {
        if(d == null)
            if(b == null)
                return "";
            else
                return String.valueOf(b.toString()) + String.valueOf('+');
        if(b == null)
            return String.valueOf('-') + String.valueOf(d.toString());
        else
            return String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(b.toString())))).append('-').append(d.toString())));
    }

    public boolean contains(Version version)
    {
        if(b != null)
        {
            int i = b.compareTo(version);
            if(i > 0 || i == 0 && !a)
                return false;
        }
        if(d != null)
        {
            int j = d.compareTo(version);
            if(j < 0 || j == 0 && !c)
                return false;
        }
        for(int k = 0; k < e.length; k++)
            if(e[k].equals(version))
                return false;

        return true;
    }

    public Version[] getExclusions()
    {
        return e;
    }

    public boolean isMaxInclusive()
    {
        return c;
    }

    public boolean isMinInclusive()
    {
        return a;
    }

    public Version getMaxVersion()
    {
        return d;
    }

    public Version getMinVersion()
    {
        return b;
    }

    public VersionRange(Version version, Version version1, boolean flag, boolean flag1, Version aversion[])
    {
        b = version;
        d = version1;
        a = flag;
        c = flag1;
        e = aversion;
    }

    public VersionRange(Version version, Version version1, boolean flag, boolean flag1)
    {
        this(version, version1, flag, flag1, new Version[0]);
    }

    public VersionRange(Version version, Version version1)
    {
        this(version, version1, true, false);
    }
}
