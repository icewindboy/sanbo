// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 

package engine.util.list;

import java.io.Serializable;

public final class IntList
    implements Serializable, Cloneable
{

    private int _flddo[];
    private int a;
    private int _fldif;

    public IntList()
    {
        this(10);
    }

    public IntList(int i)
    {
        _fldif = 0;
        a = i;
        _flddo = new int[a];
    }

    public void add(int i)
    {
        _flddo[_fldif] = i;
        _fldif++;
        if(_fldif == a)
        {
            a = a * 2;
            int ai[] = new int[a];
            System.arraycopy(_flddo, 0, ai, 0, _fldif);
            _flddo = ai;
        }
    }

    public int get(int i)
    {
        if(i < 0 || i >= _fldif)
            throw new IndexOutOfBoundsException(String.valueOf(String.valueOf((new StringBuffer("Index ")).append(i).append(" not valid."))));
        else
            return _flddo[i];
    }

    public void set(int i, int j)
    {
        if(i < 0 || i >= _fldif)
        {
            throw new IndexOutOfBoundsException(String.valueOf(String.valueOf((new StringBuffer("Index ")).append(i).append(" not valid."))));
        } else
        {
            _flddo[i] = j;
            return;
        }
    }

    public int size()
    {
        return _fldif;
    }

    public int[] toArray()
    {
        int i = _fldif;
        int ai[] = new int[i];
        System.arraycopy(_flddo, 0, ai, 0, i);
        return ai;
    }

    public void clear()
    {
        for(int i = 0; i < _fldif; i++)
            _flddo[i] = 0;

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
            IntList intlist = (IntList)super.clone();
            intlist._flddo = (int[])_flddo.clone();
            IntList intlist1 = intlist;
            return intlist1;
        }
        catch(CloneNotSupportedException clonenotsupportedexception)
        {
            throw new InternalError();
        }
    }
}
