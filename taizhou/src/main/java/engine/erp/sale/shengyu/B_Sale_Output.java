package engine.erp.sale.shengyu;

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
 * <p>Title: 销售提单货物出库情况</p>
 * <p>Description: 销售提单货物出库情况</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class B_Sale_Output extends BaseAction implements Operate
{
  private static final String XS_TDHW_OUTPUT = "SELECT * FROM vw_sale_tdhw t WHERE    ? ";//
  private EngineDataSet dsTdhwoutput  = new EngineDataSet();
  public  String retuUrl = null;
  public String tdid = "";
  public String tdbh = "";
  /**
   *析构函数
   * */
  public static B_Sale_Output getInstance(HttpServletRequest request)
  {
    B_Sale_Output b_Sale_OutputBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_Sale_OutputBean";
      b_Sale_OutputBean = (B_Sale_Output)session.getAttribute(beanName);
      if(b_Sale_OutputBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        b_Sale_OutputBean = new B_Sale_Output();
        session.setAttribute(beanName, b_Sale_OutputBean);
      }
    }
    return b_Sale_OutputBean;
  }
  /**
   * 构造函数
   */
  private B_Sale_Output()
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
    setDataSetProperty(dsTdhwoutput, null);//无数据
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
    if(dsTdhwoutput != null){
      dsTdhwoutput.close();
      dsTdhwoutput = null;
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
  public final EngineDataSet getJhhTable()
  {
    return dsTdhwoutput;
  }
  /**
   *初始化操作
   * */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      tdid = request.getParameter("id");
      if(tdid==null||tdid.equals(""))
        return;
      String SQL = combineSQL(XS_TDHW_OUTPUT, "?", new String[]{"  t.tdid="+tdid});
      if(dsTdhwoutput.isOpen())
        dsTdhwoutput.close();
      setDataSetProperty(dsTdhwoutput,SQL);
      //dsTdhwoutput.setQueryString(SQL);
      dsTdhwoutput.open();
      dsTdhwoutput.first();
      if(dsTdhwoutput.getRowCount()>0)
        tdbh=dsTdhwoutput.getValue("tdbh");
    }
  }
}

