package engine.erp.sale;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 销售管理--业务员奖金计算公式--</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @version 1.0
 */

public final class B_PrixFormula extends BaseAction implements Operate
{
  /**
   * 提取业务员奖金计算公式信息的SQL语句
   */
  private static final String JJJSGS_SQL = "SELECT * FROM xs_jjjsgs";
  private static final String JJKX_SQL = "SELECT * FROM xs_jjgssz ORDER BY pxh ";//奖金公式设置
  private EngineDataSet dsb_PrixFormula = new EngineDataSet();
  private EngineDataSet ds_jjgssz = new EngineDataSet();
  private EngineRow locateRow = null;//用于定位数据集
  private RowMap rowInfo = new RowMap();//保存用户输入的信息
  public  String retuUrl = null;
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  /**
   * 得到业务员奖金计算公式信息的实例
   * @param request jsp请求
   * @return 返回业务员奖金计算公式信息的实例
   */
  public static B_PrixFormula getInstance(HttpServletRequest request)
  {
    B_PrixFormula b_PrixFormulaBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      LoginBean loginBean = LoginBean.getInstance(request);
      String beanName = "b_PrixFormulaBean";
      b_PrixFormulaBean = (B_PrixFormula)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_PrixFormulaBean == null)
      {
        b_PrixFormulaBean = new B_PrixFormula();
        b_PrixFormulaBean.qtyFormat = loginBean.getQtyFormat();
        b_PrixFormulaBean.priceFormat = loginBean.getPriceFormat();
        b_PrixFormulaBean.sumFormat = loginBean.getSumFormat();
        b_PrixFormulaBean.dsb_PrixFormula.setColumnFormat("xsjzj", b_PrixFormulaBean.priceFormat);//销售价增加
        b_PrixFormulaBean.dsb_PrixFormula.setColumnFormat("xsjjs", b_PrixFormulaBean.priceFormat);//销售价减少
        session.setAttribute(beanName, b_PrixFormulaBean);
      }
    }
    return b_PrixFormulaBean;
  }
  private B_PrixFormula()
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
    setDataSetProperty(dsb_PrixFormula, JJJSGS_SQL);//
    setDataSetProperty(ds_jjgssz, JJKX_SQL);
    dsb_PrixFormula.setSequence(new SequenceDescriptor(new String[]{"jjjsgsID"}, new String[]{"s_xs_jjjsgs"}));
    addObactioner(String.valueOf(INIT), new B_PrixFormula_Init());//初始化
    addObactioner(String.valueOf(POST), new B_PrixFormula_Post());//保存
  }
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
      if(dsb_PrixFormula.isOpen() && dsb_PrixFormula.changesPending())
        dsb_PrixFormula.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsb_PrixFormula != null){
      dsb_PrixFormula.close();
      dsb_PrixFormula = null;
    }
    if(ds_jjgssz != null){
      ds_jjgssz.close();
      ds_jjgssz = null;
    }
    log = null;
    rowInfo = null;
    locateRow = null;
  }
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
  {
    return getClass();
  }

      /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsb_PrixFormula.isOpen())
      dsb_PrixFormula.open();
    return dsb_PrixFormula;
  }
      /*得到表对象*/
  public final EngineDataSet getJjgsTable()
  {
    if(!ds_jjgssz.isOpen())
      ds_jjgssz.open();
    return ds_jjgssz;
  }
      /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 初始化操作的触发类
   */
  class B_PrixFormula_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      ds_jjgssz.refresh();
    }
  }
  /**
   * 保存操作的触发类
   */
  class B_PrixFormula_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();

      //校验数据
      rowInfo.put(data.getRequest());
      String temp="";
      String rll = rowInfo.get("rll");
      String xsjzj = rowInfo.get("xsjzj");
      String tclzj = rowInfo.get("tclzj");
      String hltszj = rowInfo.get("hltszj");
      String xsjjs = rowInfo.get("xsjjs");
      String tcljs = rowInfo.get("tcljs");
      String hltsjs = rowInfo.get("hltsjs");
      String jjjsgs = rowInfo.get("jjjsgs");
      String kxbl = rowInfo.get("kxbl");
      String sfxdw = rowInfo.get("sfxdw");
      if((temp = checkNumber(rll, "日利率")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if((temp = checkNumber(xsjzj, "销售价增加")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if((temp = checkNumber(tclzj, "提成率增加")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if((temp = checkNumber(hltszj, "回笼天数增加")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if((temp = checkNumber(xsjjs, "销售价减少")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if((temp = checkNumber(tcljs, "提成率减少")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if((temp = checkNumber(hltsjs, "回笼天数减少")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if((temp = checkNumber(kxbl, "扣息比例")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if(ds.getRowCount()==0)
      {
        ds.insertRow(false);
        ds.setValue("jjjsgsID", "-1");
      }
      ds.setValue("rll", rll);
      ds.setValue("xsjzj", xsjzj);
      ds.setValue("jsfs", rowInfo.get("jsfs"));
      ds.setValue("tclzj", tclzj);
      ds.setValue("hltszj", hltszj);
      ds.setValue("xsjjs", xsjjs);
      ds.setValue("tcljs", tcljs);
      ds.setValue("hltsjs", hltsjs);
      ds.setValue("jjjsgs", jjjsgs);
      ds.setValue("kxbl", kxbl);
      ds.setValue("sfxdw", sfxdw);
      ds.post();


      //以下是奖金相关
      EngineDataSet ds_js = getJjgsTable();
      ds_js.first();
      for(int i=0;i<ds_js.getRowCount();i++)
      {
        String jsgs=rowInfo.get("jsgs_"+i);
        if(!jsgs.equals(""))
        {
          String fieldname=ds_js.getValue("dyzdm");//公式字段
          if(jsgs.indexOf(fieldname)>-1)
          {
            data.setMessage(showJavaScript("alert('款项中"+ds_js.getValue("mc")+"公式错误!')"));
            return;
          }
          ds_js.setValue("jsgs",jsgs);
        }
        else
          ds_js.setValue("jsgs",jsgs);
        ds_js.next();
        ds_js.post();
      }
      ds.saveChanges();
      ds_js.saveChanges();
    }
  }
}
