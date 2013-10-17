package engine.erp.sale;

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
import engine.common.LoginBean;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 查看销售合同的相关货物的库存量与销售可供量</p>
 * <p>Description: 查看销售合同的相关货物的库存量与销售可供量</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class B_Store_Capacity extends BaseAction implements Operate
{
  private static final String IMPORT_SALE_SQL = "SELECT * FROM VW_XS_HTHW_KCL t WHERE   ? ";//
  private EngineDataSet dsSaleProductCapacity  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  ArrayList deRowInfos =  new ArrayList();;
/**
 *析构函数
 * */
public static B_Store_Capacity getInstance(HttpServletRequest request)
{
  B_Store_Capacity b_Store_CapacityBean = null;
  HttpSession session = request.getSession(true);
  synchronized (session)
  {
    String beanName = "b_Store_CapacityBean";
    b_Store_CapacityBean = (B_Store_Capacity)session.getAttribute(beanName);
    if(b_Store_CapacityBean == null)
    {
      LoginBean loginBean = LoginBean.getInstance(request);
      b_Store_CapacityBean = new B_Store_Capacity();
      session.setAttribute(beanName, b_Store_CapacityBean);
    }
  }
  return b_Store_CapacityBean;
}
/**
 * 构造函数
 */
private B_Store_Capacity()
{
  try {
    jbInit();
  }
  catch (Exception ex) {
    ex.printStackTrace();
  }
}
/**
 * Implement this engine.project.OperateCommon abstract method
 * 初始化函数
 * @throws Exception 异常信息
 */
protected final void jbInit() throws java.lang.Exception
{
  setDataSetProperty(dsSaleProductCapacity, null);//无数据
  addObactioner(String.valueOf(INIT), new Init());
}
public String doService(HttpServletRequest request, HttpServletResponse response)
{
  try{
    String operate = request.getParameter(OPERATE_KEY);
    if(operate != null && operate.trim().length() > 0)
    {
      RunData data = notifyObactioners(operate, request, response, null);
      if(data.hasMessage())
        return data.getMessage();
    }
    return "";
  }
  catch(Exception ex){
    log.error("doService", ex);
    return showMessage(ex.getMessage(), true);
  }
}
/**
 *session失效时调用
 * */
public void valueUnbound(HttpSessionBindingEvent event)
{
  if(dsSaleProductCapacity != null){
    dsSaleProductCapacity.close();
    dsSaleProductCapacity = null;
  }
  log = null;
}
protected final Class childClassName()
{
  return getClass();
}
  /*得到从表多列的信息*/
public final RowMap[] getDetailRowinfos() {
  RowMap[] rows = new RowMap[deRowInfos.size()];
  deRowInfos.toArray(rows);
  return rows;
}
//得到查询的行
public final RowMap getLookupRow(String hthwid)
{
  RowMap row = new RowMap();
  if(hthwid == null || hthwid.equals(""))
    return row;//返回
  EngineRow locateRow = new EngineRow(dsSaleProductCapacity, "hthwid");
  //构建指定DataSet组件的1列的EngineRow（但是没有数据）
  if(locateRow == null)
    locateRow = new EngineRow(getOneTable(), "hthwid");
  locateRow.setValue(0, hthwid);
  if(getOneTable().locate(locateRow, Locate.FIRST))
    row.put(getOneTable());
  return row;
}
/**
 *得到数据集
 * */
public final EngineDataSet getOneTable()
{
  return dsSaleProductCapacity;
}
/**
 *初始化操作
 * */
class Init implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest request = data.getRequest();
    retuUrl = request.getParameter("src");
    retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
    String htid = request.getParameter("htid");
    if(deRowInfos.size()>0)
      deRowInfos.clear();
    if(htid==null||htid.equals("")||htid.equals("undefined"))
      return;
    String SQL = combineSQL(IMPORT_SALE_SQL, "?", new String[]{"  t.htid='"+htid+"'"});
    if(dsSaleProductCapacity.isOpen())
      dsSaleProductCapacity.close();
    setDataSetProperty(dsSaleProductCapacity,SQL);
    //dsSaleProductCapacity.setQueryString(SQL);
    dsSaleProductCapacity.open();
    ArrayList temp = new ArrayList();
    dsSaleProductCapacity.first();
    for(int i=0;i<dsSaleProductCapacity.getRowCount();i++)
    {
      RowMap tmp ;
      String wzdjid = dsSaleProductCapacity.getValue("wzdjid");
      String dmsxid = dsSaleProductCapacity.getValue("dmsxid");
      String kcdmsxid = dsSaleProductCapacity.getValue("kcdmsxid");
      String test = wzdjid+dmsxid ;
      //String s = temp.toString();
      if(dmsxid.equals(kcdmsxid))
      {
        if(!temp.contains(test))
        {
        tmp = new RowMap(dsSaleProductCapacity);
        temp.add(test);
        deRowInfos.add(tmp);
        }
      }
      else if((!dmsxid.equals("-1"))&&kcdmsxid.equals("-1"))
      {
        if(!temp.contains(test))
        {
        tmp = new RowMap(dsSaleProductCapacity);
        temp.add(test);
        deRowInfos.add(tmp);
        }
      }
      dsSaleProductCapacity.next();
    }
  }
}
}
