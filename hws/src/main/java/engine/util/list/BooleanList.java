// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 

package engine.util.list;

import java.io.Serializable;

public class BooleanList
    implements Serializable, Cloneable
{

    private boolean _flddo[];
    private int a;
    private int _fldif;

    public BooleanList()
    {
        this(10);
    }

    public BooleanList(int i)
    {
        _fldif = 0;
        a = i;
        _flddo = new boolean[a];
    }

    public void add(boolean flag)
    {
        _flddo[_fldif] = flag;
        _fldif++;
        if(_fldif == a)
        {
            a = a * 2;
            boolean aflag[] = new boolean[a];
            System.arraycopy(_flddo, 0, aflag, 0, _fldif);
            _flddo = aflag;
        }
    }

    public boolean get(int i)
    {
        if(i < 0 || i >= _fldif)
            throw new IndexOutOfBoundsException(String.valueOf(String.valueOf((new StringBuffer("Index ")).append(i).append(" not valid."))));
        else
            return _flddo[i];
    }

    public void set(int i, boolean flag)
    {
        if(i < 0 || i >= _fldif)
        {
            throw new IndexOutOfBoundsException(String.valueOf(String.valueOf((new StringBuffer("Index ")).append(i).append(" not valid."))));
        } else
        {
            _flddo[i] = flag;
            return;
        }
    }

    public int size()
    {
        return _fldif;
    }

    public boolean[] toArray()
    {
        int i = _fldif;
        boolean aflag[] = new boolean[i];
        System.arraycopy(_flddo, 0, aflag, 0, i);
        return aflag;
    }

    public void clear()
    {
        for(int i = 0; i < _fldif; i++)
            _flddo[i] = false;

        _fldif = 0;
    }

    public String toString()
    {
        StringBuffer stringbuffer = new StringBuffer();
        for(int i = 0; i < _fldif; i++)
            stringbuffer.append(_flddo[i]).append(" ");

        return stringbuffer.toString();
    }

    public Object clone()
    {
        try
        {
            BooleanList booleanlist = (BooleanList)super.clone();
            booleanlist._flddo = (boolean[])_flddo.clone();
            BooleanList booleanlist1 = booleanlist;
            return booleanlist1;
        }
        catch(CloneNotSupportedException clonenotsupportedexception)
        {
            throw new InternalError();
        }
    }
}
