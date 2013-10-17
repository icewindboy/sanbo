package engine.erp.produce;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import java.util.*;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.project.*;
import java.text.SimpleDateFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 工人工资计算公式设置</p>
 * <p>Description: 工人工资计算公式设置</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_WageFormula extends BaseAction implements Operate
{
  private static final String WAGEFORMULA_SQL = "SELECT * FROM sc_grgzgs";

  private static final String WAGEFORMULA_RB_SQL = "SELECT gsz FROM sc_grgzgs WHERE gsbm='rb'";
  private static final String WAGEFORMULA_YB_SQL = "SELECT gsz FROM sc_grgzgs WHERE gsbm='yb'";
  private static final String WAGEFORMULA_JJGS_SQL = "SELECT gsz FROM sc_grgzgs WHERE gsbm='jjgs'";
  private static final String WAGEFORMULA_GZ_SQL = "SELECT gsz FROM sc_grgzgs WHERE gsbm='gz'";
  /**
   * 保存工人工资计算公式信息的数据集
   */
  private EngineDataSet dsWageFormula = new EngineDataSet();

  private EngineRow locateRow = null;
  /**
   * 保存用户输入的信息
   */
  private RowMap rowInfo = new RowMap();
  /**
   * 点击返回按钮的URL
   */
  public String retuUrl = null;
  private static final String RB= "rb";//定义日班公式编码
  private static final String YB= "yb";//定义夜班公式编码
  private static final String JJGS= "jjgs";//定义计件工时公式编码
  private static final String GZ= "gz";//定义工资公式编码

  /**
   * 得到工人工资计算公式信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回工人工资计算公式信息的实例
   */
  public static B_WageFormula getInstance(HttpServletRequest request)
  {
    B_WageFormula wageFormulaBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "wageFormulaBean";
      wageFormulaBean = (B_WageFormula)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(wageFormulaBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        wageFormulaBean = new B_WageFormula();
        session.setAttribute(beanName, wageFormulaBean);
      }
    }
    return wageFormulaBean;
  }

  /**
   * 构造函数
   */
  private B_WageFormula()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsWageFormula, WAGEFORMULA_SQL);
    //添加操作的触发对象
    addObactioner(String.valueOf(INIT), new B_WageFormula_Init());
    addObactioner(String.valueOf(POST), new B_WageFormula_Post());
  }

  //----Implementation of the BaseAction abstract class
  /**
   * JSP调用的函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
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
      if(dsWageFormula.isOpen() && dsWageFormula.changesPending())
        dsWageFormula.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  //----Implementation of the BaseAction abstract class
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsWageFormula != null){
      dsWageFormula.close();
      dsWageFormula = null;
    }
    log = null;
    rowInfo = null;
    locateRow = null;
  }

  //----Implementation of the BaseAction abstract class
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
  {
    return getClass();
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo()
  {
    return rowInfo;
  }
  /**
   * 一个得到公式值数组的方法
   * 用特定的逗号分割符分割字符串segformat
   * 例如：3,3,3表示每层的编码长度为3
   * @return
   */
  public String[] getCode()
  {
    return parseString(dsWageFormula.getValue("gsz"), ";");
  }

  /**
   *用于初始化调用的方法准备把公式推入到行
   */
  public void readyExpressions()
  {
    if(!dsWageFormula.isOpen())
      dsWageFormula.openDataSet();
    else
      dsWageFormula.refresh();

    if(locateRow == null)
      locateRow = new EngineRow(dsWageFormula, "gsbm");
    locateRow.setValue(0, RB);
    if(dsWageFormula.locate(locateRow, Locate.FIRST))
      rowInfo.put(RB, dsWageFormula.getValue("gsz"));
    locateRow.setValue(0, YB);
    if(dsWageFormula.locate(locateRow, Locate.FIRST))
      rowInfo.put(YB, dsWageFormula.getValue("gsz"));
    locateRow.setValue(0, JJGS);
    if(dsWageFormula.locate(locateRow, Locate.FIRST))
      rowInfo.put(JJGS, dsWageFormula.getValue("gsz"));
    locateRow.setValue(0, GZ);
    if(dsWageFormula.locate(locateRow, Locate.FIRST))
      rowInfo.put(GZ, dsWageFormula.getValue("gsz"));
  }

  /**
   * 得到日班夜班计件公式
   */
  public final String getScriptFunction(String funcName, boolean isDate)
  {
    StringBuffer buf = new StringBuffer("function ").append(funcName).append("(cqsj){");
    String rbStr = rowInfo.get(isDate ? RB : YB);
    String[] segments = parseString(rbStr, ";");
    for(int i=segments.length-1; i>=0; i--)
    {
      String rbCur = segments[i];
      String[] rbParse = parseString(rbCur,",");
      String num = null;
      boolean isEquals = rbParse[0].startsWith(">=");
      num = rbParse[0].substring(isEquals ? 2 : 1);
      buf.append("if(cqsj ").append(isEquals ? ">=" : ">");
      buf.append(num).append(")");
      buf.append("return ").append(rbParse[1]).append(";");
    }
    buf.append("return 0; }");
    return buf.toString();
  }

  /**
  * 得到计件工时公式
   */
  public final String getWorkTime()
 {
   String jjgsStr = rowInfo.get(JJGS);
   return jjgsStr;
  }
  /**
  * 得到工资计算方法
   */
  public final String getWage()
 {
   String gzStr = rowInfo.get(GZ);
   return gzStr;
  }
  /**
   * 初始化操作的触发类
   */
  class B_WageFormula_Init implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发初始化操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      readyExpressions();
    }
  }
  /**
   * 保存操作的触发类
   */
  class B_WageFormula_Post implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发保存操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //校验数据
      rowInfo.put(data.getRequest());
      String rbgs = rowInfo.get(RB);
      String ybgs = rowInfo.get(YB);
      String jjgsgs = rowInfo.get(JJGS);
      String gzgs = rowInfo.get(GZ);
      String alert=null;
      if(jjgsgs.length()>0 && (alert = checkNumber(jjgsgs, "计件公时输入值")) != null)
      {
        data.setMessage(alert);
        return;
      }
      if(rbgs.equals("")){
        data.setMessage(showJavaScript("alert('日班公式不能为空!');"));
         return;
      }
      if(ybgs.equals("")){
        data.setMessage(showJavaScript("alert('夜班公式不能为空!');"));
         return;
      }
      if(locateRow == null)
        locateRow = new EngineRow(dsWageFormula,"gsbm");
      locateRow.setValue(0, RB);
      if(!dsWageFormula.locate(locateRow, Locate.FIRST))
      {
        dsWageFormula.insertRow(false);
        dsWageFormula.setValue("gsbm", RB);
      }
      dsWageFormula.setValue("gsz", rbgs);
      dsWageFormula.post();

      locateRow.setValue(0, YB);
      if(!dsWageFormula.locate(locateRow, Locate.FIRST))
      {
        dsWageFormula.insertRow(false);
        dsWageFormula.setValue("gsbm",YB);
      }
      dsWageFormula.setValue("gsz", ybgs);
      dsWageFormula.post();

      locateRow.setValue(0, JJGS);
      if(!dsWageFormula.locate(locateRow, Locate.FIRST))
      {
        dsWageFormula.insertRow(false);
        dsWageFormula.setValue("gsbm", JJGS);
      }
      dsWageFormula.setValue("gsz",jjgsgs);
      dsWageFormula.post();

      locateRow.setValue(0, GZ);
      if(!dsWageFormula.locate(locateRow, Locate.FIRST))
      {
        dsWageFormula.insertRow(false);
        dsWageFormula.setValue("gsbm", GZ);
      }
      dsWageFormula.setValue("gsz", gzgs);
      dsWageFormula.post();

      dsWageFormula.saveChanges();
    }
  }
}
