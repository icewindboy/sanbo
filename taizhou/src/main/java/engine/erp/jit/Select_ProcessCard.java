package engine.erp.jit;


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
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class Select_ProcessCard extends BaseAction implements Operate
{
  private static final String IMPORT_SALE_SQL = " select a.*,c.scjhid,c.jhh,c.djh,b.processdm from sc_processmx a,sc_process b,sc_jh c where b.zt=1 and a.statte=0 and a.processid=b.processid and b.scjhid=c.scjhid ?  order by a.processmxid desc ";//分公司; 往来单位
  //private static final String MASTER_SQL    = " select a.*,c.jhh,c.djh,b.processdm from sc_processmx a,sc_process b,sc_jh c where a.processid=b.processid and b.scjhid=c.scjhid order by a.processmxid desc ";
  private EngineDataSet dsSaleOrderProduct  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;
  private String fgsid = null;   //分公司ID
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public String deptid = "";
  public String personid = "";
  /**
   * 析构函数
   * */
  public static Select_ProcessCard getInstance(HttpServletRequest request)
  {
    Select_ProcessCard Select_ProcessCardBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "Select_ProcessCardBean";
      Select_ProcessCardBean = (Select_ProcessCard)session.getAttribute(beanName);
      if(Select_ProcessCardBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        Select_ProcessCardBean = new Select_ProcessCard();
        Select_ProcessCardBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, Select_ProcessCardBean);
      }
    }
    return Select_ProcessCardBean;
  }
  /**
   * 构造函数
   */
  private Select_ProcessCard()
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
    setDataSetProperty(dsSaleOrderProduct, null);//无数据
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
  }
  /**
   *网页调用
   * */
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
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }

/**
*session失效时调用
* */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsSaleOrderProduct != null){
      dsSaleOrderProduct.close();
      dsSaleOrderProduct = null;
    }
    log = null;
  }
  protected final Class childClassName()
  {
    return getClass();
  }
  //得到查询的行
  public final RowMap getLookupRow(String processmxid)
  {
    RowMap row = new RowMap();
    if(processmxid == null || processmxid.equals(""))
      return row;//返回
    EngineRow locateRow = new EngineRow(dsSaleOrderProduct, "processmxid");//构建指定DataSet组件的1列的EngineRow（但是没有数据）
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "processmxid");
    locateRow.setValue(0, processmxid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsSaleOrderProduct;
  }
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = request.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      deptid = request.getParameter("deptid");
      String SQL = combineSQL(IMPORT_SALE_SQL,"?",(new String[]{(deptid!=null&&!deptid.equals(""))?" and a.deptid="+deptid:""}));//IMPORT_SALE_SQL;

      dsSaleOrderProduct.setQueryString(SQL);
      dsSaleOrderProduct.setRowMax(null);
    }
  }
  /**
   *  查询操作
   */
  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();

      SQL = combineSQL(IMPORT_SALE_SQL, "?", new String[]{fgsid, SQL});
      if(!dsSaleOrderProduct.getQueryString().equals(SQL))
      {
        dsSaleOrderProduct.setQueryString(SQL);
        dsSaleOrderProduct.setRowMax(null);
      }
   }
   /**
    * 初始化查询的各个列
    * @param request web请求对象
    */
   private void initQueryItem(HttpServletRequest request)
   {
     if(isInitQuery)
       return;
     EngineDataSet master = dsSaleOrderProduct;
     if(!master.isOpen())
       master.open();
     //初始化固定的查询项目
     fixedQuery = new QueryFixedItem();
     fixedQuery.addShowColumn("", new QueryColumn[]{
       new QueryColumn(master.getColumn("processdm"), null, null, null, null, "="),//提单编号
       new QueryColumn(master.getColumn("djh"), null, null, null, null, "=")//购货单位
     });
     isInitQuery = true;
  }
}
}