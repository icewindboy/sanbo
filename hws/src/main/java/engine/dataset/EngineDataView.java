// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi 

package engine.dataset;

import com.borland.dx.dataset.*;

// Referenced classes of package engine.dataset:
//            EngineDataSet

public class EngineDataView extends DataSetView
{

    public EngineDataView()
    {
    }

    public String getValue(String s)
    {
        return getValue(getColumn(s).getOrdinal());
    }

    public String getValue(int i)
    {
        return EngineDataSet.getValue(this, i);
    }
}
