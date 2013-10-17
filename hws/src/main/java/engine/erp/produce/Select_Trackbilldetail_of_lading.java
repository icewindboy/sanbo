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
 * <p>Title: 销售子系统_销售发票_引入销售提单</p>
 * <p>Description: 销售子系统_销售发票_引入销售提单</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class Select_Trackbilldetail_of_lading extends BaseAction implements Operate
{
  private static final String IMPORT_SALE_SQL = " SELECT t.*,g.xm FROM ( "
      +" SELECT d.receivedetailid, d.cpid, d.gx ,d.dmsxid,d.batchNo,e.ydzs,e.dzcs,e.jhsh,e.sjsh,e.jf,e.shyy,e.jt,e.bc,e.zlyz,e.creator,e.handleperson,e.approveid "
                      +" FROM sc_receiveproddetail d,sc_receiveprod e WHERE d.receiveid=e.receiveid AND d.gx IS NOT NULL "
                      +"  UNION ALL "
                      +"  SELECT d.receivedetailid, d.cpid, d.gx2 gx,d.dmsxid,d.batchNo ,e.ydzs,e.dzcs,e.jhsh,e.sjsh,e.jf,e.shyy,e.jt,e.bc,e.zlyz,e.creator,e.handleperson,e.approveid "
                      +" FROM sc_receiveproddetail d,sc_receiveprod e WHERE d.receiveid=e.receiveid  AND d.gx2  IS NOT NULL "
                       +" UNION ALL "
                       +" SELECT d.receivedetailid, d.cpid, d.gx3 gx,d.dmsxid,d.batchNo ,e.ydzs,e.dzcs,e.jhsh,e.sjsh,e.jf,e.shyy,e.jt,e.bc,e.zlyz,e.creator,e.handleperson,e.approveid "
                      +"  FROM sc_receiveproddetail d,sc_receiveprod e WHERE d.receiveid=e.receiveid  AND d.gx3  IS NOT NULL "
                       +"  ) t,emp g where g.personid=t.approveid ";//工序名称
  private static final String MASTER_SQL    = " SELECT t.*,g.xm FROM ( "
                      +" SELECT d.receivedetailid, d.cpid, d.gx ,d.dmsxid,d.batchNo ,e.ydzs,e.dzcs,e.jhsh,e.sjsh,e.jf,e.shyy,e.jt,e.bc,e.zlyz,e.creator,e.handleperson,e.approveid "
                      +" FROM sc_receiveproddetail d,sc_receiveprod e WHERE d.receiveid=e.receiveid AND d.gx IS NOT NULL "
                      +"  UNION ALL "
                      +"  SELECT d.receivedetailid, d.cpid, d.gx2 gx,d.dmsxid,d.batchNo ,e.ydzs,e.dzcs,e.jhsh,e.sjsh,e.jf,e.shyy,e.jt,e.bc,e.zlyz,e.creator,e.handleperson,e.approveid "
                      +" FROM sc_receiveproddetail d,sc_receiveprod e WHERE d.receiveid=e.receiveid  AND d.gx2  IS NOT NULL "
                       +" UNION ALL "
                       +" SELECT d.receivedetailid, d.cpid, d.gx3 gx,d.dmsxid,d.batchNo ,e.ydzs,e.dzcs,e.jhsh,e.sjsh,e.jf,e.shyy,e.jt,e.bc,e.zlyz,e.creator,e.handleperson,e.approveid "
                      +"  FROM sc_receiveproddetail d,sc_receiveprod e WHERE d.receiveid=e.receiveid  AND d.gx3  IS NOT NULL "
                       +"  ) t,emp g where g.personid=t.approveid ? ";

  private EngineDataSet dsSaleOrderProduct  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl = null;

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
 ///public String dwtxid = "";
  //public String personid = "";
  /**
   * 析构函数
   * */
  public static Select_Trackbilldetail_of_lading getInstance(HttpServletRequest request)
  {
    Select_Trackbilldetail_of_lading Select_TrackBilldetail_Of_LadingBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "Select_TrackBilldetail_Of_LadingBean";
      Select_TrackBilldetail_Of_LadingBean = (Select_Trackbilldetail_of_lading)session.getAttribute(beanName);
      if(Select_TrackBilldetail_Of_LadingBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        Select_TrackBilldetail_Of_LadingBean = new Select_Trackbilldetail_of_lading();
        //Select_TrackBilldetail_Of_LadingBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, Select_TrackBilldetail_Of_LadingBean);
      }
    }
    return Select_TrackBilldetail_Of_LadingBean;
  }
  /**
   * 构造函数
   */
  private Select_Trackbilldetail_of_lading()
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
  public final RowMap getLookupRow(String receivedetailid)
  {
    RowMap row = new RowMap();
    if(receivedetailid == null || receivedetailid.equals(""))
      return row;//返回
    EngineRow locateRow = new EngineRow(dsSaleOrderProduct, "receivedetailid");//构建指定DataSet组件的1列的EngineRow（但是没有数据）
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "receivedetailid");
    locateRow.setValue(0, receivedetailid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsSaleOrderProduct;
  }

  /**
   *
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = request.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      String gx = request.getParameter("gx");
      String SQL =IMPORT_SALE_SQL;
      if(!gx.equals(""))
        SQL=SQL+"AND gx= '"+gx+"'";
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


      if(SQL.length() > 0)
          SQL = " AND "+SQL;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{ SQL});
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
       new QueryColumn(master.getColumn("batchno"), null, null, null, null, "="),//批号
     new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),
     new QueryColumn(master.getColumn("jt"), null, null, null, null, "="),
     new QueryColumn(master.getColumn("handlePerson"), null, null, null, null, "="),
     new QueryColumn(master.getColumn("bc"), null, null, null, null, "="),
     new QueryColumn(master.getColumn("creator"), null, null, null, null, "=")

     });
     isInitQuery = true;
  }
}
}
