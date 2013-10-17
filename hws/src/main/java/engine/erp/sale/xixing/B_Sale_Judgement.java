package engine.erp.sale.xixing;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.project.*;
import engine.html.*;
import engine.common.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 合同是否被引用--</p>
 * <p>Description: <br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public class B_Sale_Judgement
{
  public static final boolean isUsed(EngineDataSet ds, String fieldname)
  {
    ds.first();
    for(int i=0;i<ds.getRowCount();i++)
    {
      if(ds.getBigDecimal(fieldname).doubleValue() != 0)
        return true;//已使用,不能改公司
      ds.next();
    }
    return false;
  }
  public static final boolean isUsedRow(EngineDataSet ds, String fieldname,int row)
  {
    ds.goToRow(row);
    if(ds.getBigDecimal(fieldname).doubleValue() != 0)
      return true;//该行已被使用.
    return false;
  }
}