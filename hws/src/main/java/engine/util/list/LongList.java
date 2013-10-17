// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 

package engine.util.list;

import java.io.Serializable;

public final class LongList
    implements Serializable, Cloneable
{

    private long _flddo[];
    private int a;
    private int _fldif;

    public LongList()
    {
        this(10);
    }

    public LongList(int i)
    {
        _fldif = 0;
        a = i;
        _flddo = new long[a];
    }

    public void add(long l)
    {
        _flddo[_fldif] = l;
        _fldif++;
        if(_fldif == a)
        {
            a = a * 2;
            long al[] = new long[a];
            System.arraycopy(_flddo, 0, al, 0, _fldif);
            _flddo = al;
        }
    }

    public long get(int i)
    {
        if(i < 0 || i >= _fldif)
            throw new IndexOutOfBoundsException(String.valueOf(String.valueOf((new StringBuffer("Index ")).append(i).append(" not valid."))));
        else
            return _flddo[i];
    }

    public void set(int i, long l)
    {
        if(i < 0 || i >= _fldif)
        {
            throw new IndexOutOfBoundsException(String.valueOf(String.valueOf((new StringBuffer("Index ")).append(i).append(" not valid."))));
        } else
        {
            _flddo[i] = l;
            return;
        }
    }

    public int size()
    {
        return _fldif;
    }

    public long[] toArray()
    {
        int i = _fldif;
        long al[] = new long[i];
        System.arraycopy(_flddo, 0, al, 0, i);
        return al;
    }

    public void clear()
    {
        for(int i = 0; i < _fldif; i++)
            _flddo[i] = 0L;

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
            LongList longlist = (LongList)super.clone();
            longlist._flddo = (long[])_flddo.clone();
            LongList longlist1 = longlist;
            return longlist1;
        }
        catch(CloneNotSupportedException clonenotsupportedexception)
        {
            throw new InternalError();
        }
    }
}
