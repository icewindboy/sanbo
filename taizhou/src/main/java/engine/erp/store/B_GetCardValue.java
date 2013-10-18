package engine.erp.store;

import engine.action.*;
import engine.dataset.*;
import engine.web.observer.*;
import engine.util.log.*;
import engine.util.StringUtils;
import javax.servlet.http.*;
import java.util.Hashtable;
import java.util.Map;
import java.math.BigDecimal;
import engine.common.LoginBean;
import engine.util.log.LogHelper;
import engine.util.log.Log;
/**
 * <p>Title: 规格属性选择</p>
 * <p>Description: 规格属性选择</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_GetCardValue extends BaseAction implements Operate
{
  public  static final String BACHNO_SEARCH = "2003";
  //合格证的批号
  private static final String CARD_SQL
      = "SELECT a.*, b.sxz, null zt FROM zl_certifiedcard a ,kc_dmsx b WHERE  a.dmsxid=b.dmsxid(+) @ @ @ ";
  //合格证
  private EngineDataSet dsCard  = new EngineDataSet();

  public String[] inputName = null;    //
  public String[] fieldName = null;    //字段名称
  public String srcFrm=null;           //传递的原form的名称
  //public boolean isMultiSelect = false;//是否是多选的
  //public String multiIdInput = null;   //多选的ID组合串

  public String methodName = null;   //调用window.opener中的方法
  //private boolean isInitQuery = false; //是否已经初始化查询条件
  //private QueryBasic fixedQuery = new QueryFixedItem();
  public  RowMap rowInfo = new RowMap();
  //public  RowMap searchRow = new RowMap();//保存查询信息的列
  public  String retuUrl = null;
  //private String fgsid = null;  //分公司ID
  public String ph = null;
  public String cpid =null;
  public String dmsxid = null;

  /**
   * 得到往来单位信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回往来单位信息的实例
   */
  public static B_GetCardValue getInstance(HttpServletRequest request)
  {
    B_GetCardValue getCardValueBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "getCardValueBean";
      getCardValueBean = (B_GetCardValue)session.getAttribute(beanName);
      if(getCardValueBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsid = loginBean.getFirstDeptID();
        getCardValueBean = new B_GetCardValue(fgsid);
        session.setAttribute(beanName, getCardValueBean);
      }
    }
    return getCardValueBean;
  }

  /**
   * 构造函数
   */
  private B_GetCardValue(String fgsid)
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  /**
   * 得到写日志的对象
   * @return 写日志的对象
   */
  public Log getLog()
  {
    return log;
  }
  /**
   * Implement this engine.project.OperateCommon abstract method
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsCard, null);

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(BACHNO_SEARCH), new NameSearch());
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
      if(dsCard.isOpen() && dsCard.changesPending())
        dsCard.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsCard != null){
      dsCard.close();
      dsCard = null;
    }
    log = null;
  }

  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }

  /**
   * 初始化表单查询信息和SQL语句
   * @param data data对象
   * @param isIframe 是否是你不框架调用
   * @throws Exception 异常信息
   */
  private void init(RunData data, boolean isIframe) throws Exception
  {
    retuUrl = data.getParameter("src","");
    retuUrl = retuUrl.trim();
    /**
    String temp = data.getParameter("cpid", "");
    if(temp.length() == 0)
    {
      data.setMessage(showJavaScript("alert('请输入产品！'); window.close();"));
      return;
    }
    */
    cpid = data.getParameter("cpid", "");
    ph = data.getParameter("batchno", "");
    dmsxid = data.getParameter("dmsxid");
    methodName = data.getParameter("method");
    srcFrm = data.getParameter("srcFrm");
    inputName = data.getParameterValues("srcVar");
    fieldName = data.getParameterValues("fieldVar");
  }

  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      init(data, false);
      if(data.hasMessage())
        return;
      String temp = null;
      if(cpid.equals(""))
        temp="";
      else
        temp=" AND a.cpid="+cpid;
      String temp1 = null;
      if(dmsxid.equals(""))
        temp1 ="";
      else
        temp1 = " AND a.dmsxid="+dmsxid;

      //提取产品属性数据
      String sql = combineSQL(CARD_SQL, "@", new String[]{temp,temp1});
      dsCard.setQueryString(sql);
      if(dsCard.isOpen())
        dsCard.refresh();
      else
        dsCard.openDataSet();
    }
  }
  /**
   * 属性名称模糊查询
   */
  class NameSearch implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      init(data, false);
      if(data.hasMessage())
        return;
      String temp = null;
      if(cpid.equals(""))
        temp="";
      else
        temp=" AND a.cpid="+cpid;
      String temp1 = null;
      if(dmsxid.equals(""))
        temp1 ="";
      else
        temp1 = " AND a.dmsxid="+dmsxid;
      //提取产品属性数据
      if(ph.length() > 0)
        ph = " AND a.batno LIKE '"+ ph +"%'";
      String sql = combineSQL(CARD_SQL, "@", new String[]{temp,temp1,ph});
      dsCard.setQueryString(sql);
      if(dsCard.isOpen()){
        dsCard.readyRefresh();
        dsCard.refresh();
      }
      else
        dsCard.openDataSet();
      if(dsCard.getRowCount()>0){
        dsCard.first();
        for(int i=0;i<dsCard.getRowCount();i++)
        {
          String cpid = dsCard.getValue("cpid");
          String ph = dsCard.getValue("batNo");
          String count  = dataSetProvider.getSequence("SELECT COUNT(*) FROM vw_kc_storebill t WHERE t.djxz IN(1,3,5,7,9) AND t.ph='"+ph+"' AND t.cpid='"+cpid+"'");
          if(!count.equals("0"))
          {
             dsCard.setValue("saleNum","");
             dsCard.setValue("produceNum","");
             dsCard.setValue("pageNum","");
             dsCard.setValue("zt","1");
             dsCard.post();
          }
          dsCard.next();
        }
      }
    }
  }

  /**
   * 得到数据集对象
   * @return 返回数据集对象
   */
  public final EngineDataSet getOneTable()
  {
    return dsCard;
  }
}

