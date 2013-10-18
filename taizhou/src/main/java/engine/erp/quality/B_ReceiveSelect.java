package engine.erp.quality;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.*;
import engine.project.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 质量管理--检验类型管理</p>
 * <p>Description: 质量管理--检验类型管理<</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author
 * @version 1.0
 */

public final class B_ReceiveSelect extends BaseAction implements Operate
{
  /**
   *提取采购入库单列表所需信息的SQL语句
   */
  private  static   final   String  Quality_SQL=
      "select a.receivecode,a.receivedate,a.storeid,a.sfdjlbid,"+
      "b.receivedetailid,b.jgdmxid,b.cpid,b.dmsxid,b.drawnum,b.kwid,b.batchno,b.memo,f.wzlbid"+
      " from sc_receiveprod a,sc_receiveproddetail b,kc_dm f"+
      " where a.receiveid=b.receiveid "+
      " and b.cpid in(select d.cpid from kc_dm d where d.wzlbid in (select e.wzlbid from  zl_checktype e where e.checktypeid='?'))"+
      " and a.state in (0,1,9) and b.cpid=f.cpid";
     private  static   final   String mastsql=
      "select a.receivecode,a.receivedate,a.storeid,a.sfdjlbid,"+
      "b.receivedetailid,b.jgdmxid,b.cpid,b.dmsxid,b.drawnum,b.kwid,b.batchno,b.memo,f.wzlbid"+
      " from sc_receiveprod a,sc_receiveproddetail b,kc_dm f"+
      " WHERE 1<>1";
  /**
   * 建立检验类型列表信息的数据集
   */
  private EngineDataSet  dsReceiveTable=new  EngineDataSet();
  /**
   * 用于定位数据集
   */
  private EngineRow locateRow = null;
  /**
   * 保存用户输入的信息
   */
  private RowMap rowInfo = new RowMap();
  /**
   * 是否在添加状态
   */
  public  boolean isAdd = true;
  /**
   * 保存修改操作的行记录指针
   */
  private long  editrow = 0;
  /**
   * 点击返回按钮的URL
   */
  public  String retuUrl = null;
  public String curID=null;
  public String checktypeid=null;
  /**
   * 得到检验类型信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_ReceiveSelect getInstance(HttpServletRequest request)
  {
    B_ReceiveSelect ReceiveSelectBean = null;
    HttpSession session = request.getSession(true);
    synchronized(session)
    {
      String beanName = "ReceiveSelectBean";
      ReceiveSelectBean = (B_ReceiveSelect)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(ReceiveSelectBean == null)
      {
        ReceiveSelectBean = new B_ReceiveSelect();
        session.setAttribute(beanName, ReceiveSelectBean);
      }
    }
    return ReceiveSelectBean;
  }
  /**
   * 构造函数
   */
  private B_ReceiveSelect()
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
    //String tempSQL=combineSQL(Quality_SQL,"?",new String[]{curID});
    setDataSetProperty(dsReceiveTable, mastsql);
    //添加操作的触发对象
    addObactioner(String.valueOf(INIT), new B_WorkProcedure_Init());
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
      if(dsReceiveTable.isOpen() && dsReceiveTable.changesPending())
        dsReceiveTable.reset();
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
    if(dsReceiveTable != null){
      dsReceiveTable.close();
      dsReceiveTable = null;
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
  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
    if(!isAdd)
      rowInfo.put(getOneTable());
  }

  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsReceiveTable.isOpen())
      dsReceiveTable.open();
    return dsReceiveTable;
  }
  //得到查询的行
  public final RowMap getLookupRow(String receivedetailid)
  {
    RowMap row = new RowMap();
    if(receivedetailid == null || receivedetailid.equals(""))
      return row;//返回
    EngineRow locateRow = new EngineRow(dsReceiveTable, "receivedetailid");//构建指定DataSet组件的1列的EngineRow（但是没有数据）
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "receivedetailid");
    locateRow.setValue(0, receivedetailid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 初始化操作的触发类
   */
  class B_WorkProcedure_Init implements Obactioner
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
      String SQL=combineSQL(Quality_SQL,"?",new String[]{checktypeid});
      dsReceiveTable.setQueryString(SQL);
      dsReceiveTable.setRowMax(null);
      //setDataSetProperty(dsReceiveTable, SQL);
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //initRowInfo(false,true);
    }
  }
}
