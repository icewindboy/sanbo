package engine.erp.produce;

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
 * <p>Title: 生产子系统_物料可替换件-子产品引入</p>
 * <p>Description: 生产子系统_物料可替换件-子产品引入</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class ImportSonProduct extends BaseAction implements Operate
{
  private static final String IMPORT_SON_PRODUCT_SQL = "SELECT a.*,b.bomid,B.ZJLX FROM VW_KC_DM a,sc_bom b where  a.cpid=b.cpid AND B.ZJLX=2 AND B.SJCPID=? ";//根据父件物料选子件物料并且子件物料为可选件
  private EngineDataSet dsSonProduct  = new EngineDataSet();//数据集
  private EngineRow locateResult = null;//用于定位数据集数据
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  public boolean isMultiSelect=false;
  public String multiIdInput=null;
  public String srcFrm="";
  public String [] inputName;
  public String [] fieldName;
  public String MethodName=null;
  /**
   *从会话中取出本类实例
   *
   * */
  public static ImportSonProduct getInstance(HttpServletRequest request)
  {
    ImportSonProduct ImportSonProductBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "ImportSonProductBean";
      ImportSonProductBean = (ImportSonProduct)session.getAttribute(beanName);
      if(ImportSonProductBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        ImportSonProductBean = new ImportSonProduct();
        ImportSonProductBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, ImportSonProductBean);
      }
    }
    return ImportSonProductBean;
  }
  /**
   * 构造函数
   */
  private ImportSonProduct()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsSonProduct, null);//空数据集
    addObactioner(String.valueOf(INIT), new Init());//注册初始化操作
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
      String operate = request.getParameter("operate");
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
   * Session失效时，调用的函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsSonProduct != null){
      dsSonProduct.close();
      dsSonProduct = null;
    }
    log = null;
  }
 public String getMethodName()
 {
   return MethodName;
 }
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }
  //得到一行信息
  public final RowMap getLookupRow(String cpid)
  {
    RowMap row = new RowMap();
    if(cpid == null || cpid.equals(""))
      return row;//返回
    EngineRow locateRow = new EngineRow(dsSonProduct, "cpid");//构建指定DataSet组件的1列的EngineRow（但是没有数据）
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "cpid");
    locateRow.setValue(0, cpid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsSonProduct;
  }
  /**
   * 初始化操作的的执行者
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = request.getParameter("src");//where is it coming?
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      String cpid = request.getParameter("cpid");
      inputName=request.getParameterValues("srcVar");
      fieldName=request.getParameterValues("fieldVar");
      srcFrm=request.getParameter("srcFrm");
      MethodName=request.getParameter("method");
      String SQL = combineSQL(IMPORT_SON_PRODUCT_SQL, "?", new String[]{cpid});
      dsSonProduct.setQueryString(SQL);
      dsSonProduct.setRowMax(null);
    }
  }

}