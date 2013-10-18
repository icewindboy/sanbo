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
 * <p>Title: 与销售合同相关的单据</p>
 * <p>Description: 与销售合同相关的单据</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class B_Sale_Order_Relation extends BaseAction implements Operate
{
  private static final String XS_HTHW_JHH = "SELECT * FROM VW_XS_HTHW_JHH t WHERE    ? ";//
  private static final String XS_HTHW_RWD = "SELECT * FROM VW_XS_HTHW_RWD t WHERE    ? ";//
  private static final String XS_HTHW_THD = "SELECT * FROM VW_XS_HTHW_THD t WHERE    ? ";//

  private EngineDataSet dsHthwJhh  = new EngineDataSet();
  private EngineDataSet dsHthwRwd  = new EngineDataSet();
  private EngineDataSet dsHthwThd  = new EngineDataSet();

  private EngineRow locateResult = null;
  public  String retuUrl = null;

  ArrayList wlxqjhids ;

  public String htid = "";
  public String htbh = "";

/**
 *析构函数
 * */
public static B_Sale_Order_Relation getInstance(HttpServletRequest request)
{
  B_Sale_Order_Relation b_Sale_Order_RelationBean = null;
  HttpSession session = request.getSession(true);
  synchronized (session)
  {
    String beanName = "b_Sale_Order_RelationBean";
    b_Sale_Order_RelationBean = (B_Sale_Order_Relation)session.getAttribute(beanName);
    if(b_Sale_Order_RelationBean == null)
    {
      LoginBean loginBean = LoginBean.getInstance(request);
      b_Sale_Order_RelationBean = new B_Sale_Order_Relation();
      session.setAttribute(beanName, b_Sale_Order_RelationBean);
    }
  }
  return b_Sale_Order_RelationBean;
}
/**
 * 构造函数
 */
private B_Sale_Order_Relation()
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
  setDataSetProperty(dsHthwJhh, null);//无数据
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
  if(dsHthwJhh != null){
    dsHthwJhh.close();
    dsHthwJhh = null;
  }
  log = null;
}
protected final Class childClassName()
{
  return getClass();
}
/**
 *得到数据集
 * */
public final EngineDataSet getThdTable()
{
  return dsHthwThd;
}
/**
 *得到数据集
 * */
public final EngineDataSet getRwdTable()
{
  return dsHthwRwd;
}
/**
 *得到数据集
 * */
public final EngineDataSet getJhhTable()
{
  return dsHthwJhh;
}
/**
 *初始化操作
 * */
class Init implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    HttpServletRequest request = data.getRequest();
    //retuUrl = request.getParameter("src");
    //retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
    htid = request.getParameter("id");
    htbh = request.getParameter("bh");
    if(htid==null||htid.equals(""))
      return;
    String SQL = combineSQL(XS_HTHW_JHH, "?", new String[]{"  t.htid="+htid});
    if(dsHthwJhh.isOpen())
      dsHthwJhh.close();
    setDataSetProperty(dsHthwJhh,SQL);
    //dsHthwJhh.setQueryString(SQL);
    dsHthwJhh.open();
    wlxqjhids = new ArrayList();
    dsHthwJhh.first();
    for(int i=0;i<dsHthwJhh.getRowCount();i++)
    {
      RowMap tmp ;
      String wlxqjhid = dsHthwJhh.getValue("wlxqjhid");
      if(!wlxqjhids.contains(wlxqjhid)&&!wlxqjhid.equals(""))
      wlxqjhids.add(wlxqjhid);
      dsHthwJhh.next();
    }
    StringBuffer tmp = new StringBuffer();
    for(int i=0;i<wlxqjhids.size();i++)
    {
      tmp.append(wlxqjhids.get(i)+",");
    }
    tmp.append("0");
    SQL =  combineSQL(XS_HTHW_RWD, "?", new String[]{"  t.wlxqjhid IN("+tmp.toString()+")"});
    if(dsHthwRwd.isOpen())
      dsHthwRwd.close();
    setDataSetProperty(dsHthwRwd,SQL);
    dsHthwRwd.open();
    SQL =  combineSQL(XS_HTHW_THD, "?", new String[]{"  t.htid="+htid});
    if(dsHthwThd.isOpen())
      dsHthwThd.close();
    setDataSetProperty(dsHthwThd,SQL);
    dsHthwThd.open();
  }
}
}
