package engine.erp.person.shengyu;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import java.util.*;
import engine.html.*;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.project.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
import javax.swing.*;

public final class B_TrainFeedback extends BaseAction implements Operate
{
  private static final String MASTER_STRUT_SQL = "SELECT * FROM rl_train_back WHERE 1<>1";   //取主表的结构
  private static final String MASTER_SQL    = "SELECT * FROM rl_train_back where 1=1 ? ";    //对主表的参数化查询




  //操作
  public static final String PXKC_ADD    = "9000";    //主表新增
  public static final String PXKC_DEL    = "9001";    //主表删除操作

  public  static final String DEPT_CHANGE = "10011";//申请部门
  public String activetab = "SetActiveTab(INFO_EX,'INFO_EX_0')";//从表当前的div

  public static final String VIEW_DETAIL = "1055";   //主从明细
  public static final String OPERATE_SEARCH = "1066";//主表查询操作
  public static final String DELETE_RETURN = "1067"; //主从删除操作
  /*主从表的数据集*/
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表

  public boolean isMasterAdd = true;                          //主表是否在添加状态
  private long    masterRow   = 0;                               //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo   = new RowMap();                 //主表添加行或修改行的引用

  private boolean isInitQuery = false;            //是否已经初始化查询条件
  public  boolean isApprove = false;     //是否在审批状态
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  /**
   * 培训信息反馈的实例
   * @param request jsp请求
   * @return 返回培训信息反馈的实例
   */
  public static B_TrainFeedback getInstance(HttpServletRequest request)
  {
    B_TrainFeedback b_TrainFeedbackBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_TrainFeedbackBean";
      b_TrainFeedbackBean = (B_TrainFeedback)session.getAttribute(beanName);
      if(b_TrainFeedbackBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        b_TrainFeedbackBean = new B_TrainFeedback();
        session.setAttribute(beanName, b_TrainFeedbackBean);
      }
    }
    return b_TrainFeedbackBean;
  }

  /**
   * 构造函数
   */
  private B_TrainFeedback()
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
  private final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsMasterTable, combineSQL(MASTER_SQL,"?",new String[]{""}));

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"back_ID"}, new String[]{"s_rl_train_back"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"back_ID"}, new boolean[]{false}, null, 0));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());//初始化
    addObactioner(String.valueOf(OPERATE_SEARCH), new Master_Search());//定制查询

    addObactioner(String.valueOf(PXKC_ADD), new Rl_Pxkc_adddel());//新增删除操作
    addObactioner(String.valueOf(PXKC_DEL), new Rl_Pxkc_adddel());//新增删除操作
    addObactioner(String.valueOf(DEPT_CHANGE), new DeptChange());

    addObactioner(String.valueOf(VIEW_DETAIL), masterAddEdit);//修改主表,及其对应的从表
    addObactioner(String.valueOf(DELETE_RETURN), new Master_Delete());//删除主表某一行,及其对应的从表
    //addObactioner(String.valueOf(DELETE), new Master_Delete());//删除主表某一行,及其对应的从表


    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);

    addObactioner(String.valueOf(DEL), new Master_Delete());

    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
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
      String opearate = request.getParameter(OPERATE_KEY);
      if(opearate != null && opearate.trim().length() > 0)
      {
        RunData data = notifyObactioners(opearate, request, response, null);
        if(data == null)
          return showMessage("无效操作", false);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsMasterTable.isOpen() && dsMasterTable.changesPending())
        dsMasterTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   * @param event ,提交参数
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsMasterTable != null){
      dsMasterTable.close();
      dsMasterTable = null;
    }
    log = null;
    m_RowInfo = null;
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
   * 初始化行信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   * 保存继续
   * @param isMaster 主表新增
   * 主表编辑
   */
  private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否是主表
    if(isMaster)
    {
      if(isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();
      if(!isAdd)
        m_RowInfo.put(getMaterTable());
    }
  }

/**
  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /**
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  public final boolean masterIsAdd() {return isMasterAdd; }

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
    * 初始化操作的触发类
    */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      HttpServletRequest request = data.getRequest();

      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      //初始化时清空数据集
      if(dsMasterTable.isOpen() && dsMasterTable.getRowCount() > 0)
        dsMasterTable.empty();
      dsMasterTable.setQueryString(combineSQL(MASTER_SQL,"?",new String[]{""}));
      dsMasterTable.setRowMax(null);

    }
  }
  /**
   *改变车间触发的事件
   */
  class DeptChange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      m_RowInfo.put(req);
      boolean isDept = String.valueOf(DEPT_CHANGE).equals(action);
    }
  }
  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isMasterAdd = String.valueOf(ADD).equals(action);
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        initRowInfo(true, isMasterAdd, true);
        initRowInfo(false, isMasterAdd, true);
      }
      if(isMasterAdd){
        initRowInfo(false, isMasterAdd, true);
        initRowInfo(true, isMasterAdd, true);
        m_RowInfo.clear();
      }
      data.setMessage(showJavaScript("toDetail();"));
    }
  }
  /**
   * 主表保存操作的触发类
   */
  class Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //把网页中的从表的信息推入ArrayList中
      //putDetailInfo(data.getRequest());
      //得到主表的数据集
      EngineDataSet ds = getMaterTable();
      //所要修改或查询的主表的一条记录信息
      RowMap rowInfo = getMasterRowinfo();
      rowInfo.put(data.getRequest());
      String temp = checkMasterInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }

      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      //得到主表主键值
      String back_ID = null;
      if(isMasterAdd){
        ds.insertRow(false);
        back_ID = isMasterAdd ? dataSetProvider.getSequence("s_rl_train_back") : ds.getValue("back_ID");

        ds.setValue("back_ID", back_ID);
      }

      //保存主表数据
      String aa = rowInfo.get("deptid");
      String bb = rowInfo.get("personid");
      ds.setValue("personid", rowInfo.get("personid"));//反馈人姓名
      ds.setValue("deptid", rowInfo.get("deptid"));//反馈人所在部门
      ds.setValue("train_proj", rowInfo.get("train_proj"));//培训项目
      ds.setValue("tech_material_ok", rowInfo.get("tech_material_ok"));//对教材的满意程度
      ds.setValue("techcher_ok", rowInfo.get("techcher_ok"));//对老师的满意程度
      ds.setValue("sum_up", rowInfo.get("sum_up"));//总结及建议
      ds.setValue("memo", rowInfo.get("memo"));//备注
      ds.setValue("isImpl", rowInfo.get("isImpl"));//是否实现培训目标
      ds.setValue("train_start", rowInfo.get("train_start"));//培训开始时间
      ds.setValue("train_end", rowInfo.get("train_end"));//培训结束时间

      ds.post();
      ds.saveChanges();
      //ds.saveDataSets(new EngineDataSet[]{ds, detail_rl_evection_rout,}, null);
      //刷新lookup的数据集，保持数据的同步.入参是相应的lookup名称
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PERSON);
      if(String.valueOf(POST_CONTINUE).equals(action))
      {
        isMasterAdd = true;
        initRowInfo(true, true, true);//重新初始化从表的各行信息

        m_RowInfo.clear();
        //initArrayList();
        initRowInfo(false, false, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
    }




    /**
 * 校验主表表表单信息从表输入的信息的正确性
 * @return null 表示没有信息,校验通过
 */
private String checkMasterInfo() throws Exception
{
  RowMap rowInfo = getMasterRowinfo();
  String temp = rowInfo.get("deptid");
  String temp1=temp;
  String aa=  rowInfo.get("train_proj");
  String bb = rowInfo.get("personid");
  String cc=  rowInfo.get("train_start");
  String dd=  rowInfo.get("train_end");
  if(cc.compareTo(dd)>0)
  return showJavaScript("alert('开始日期不能大于结束日期！');");
  if(temp.equals(""))
    return showJavaScript("alert('部门不能为空！');");
  temp=rowInfo.get("personid");
  if(temp.equals(""))
    return showJavaScript("alert('员工姓名不能为空！');");
  temp=rowInfo.get("train_start");
  if(temp.equals(""))
    return showJavaScript("alert('培训开始日期不能为空！');");
  temp=rowInfo.get("train_end");
if(temp.equals(""))
    return showJavaScript("alert('培训结束日期不能为空！');");

  String count= "0";
  count  = dataSetProvider.getSequence("SELECT COUNT(*)  FROM rl_train_back t WHERE t.personid='"+bb+"' AND t.deptid='"+temp1+"' AND t.train_proj='"+aa+"' AND t.train_start=to_date('"+cc+"','yyyy-mm-dd') AND t.train_end=to_date('"+dd+"','yyyy-mm-dd')");
  if(!count.equals("0")&&isMasterAdd)
  return showJavaScript("alert('不能保存相同数据！');");
  return null;
  }
}
  /**
   * 主表删除操作
   */
  class Master_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getMaterTable();
      if(!action.equals(String.valueOf(DEL)))
      {
        //在主从明细里执行删除操作.
        if(isMasterAdd){
          data.setMessage(showJavaScript("backList();"));//主从表新增,还未保存时,
          return;
        }
        dsMasterTable.goToInternalRow(masterRow);
      }
      else
      {
        //在主表的列表里执行删除操作
        String rownum=data.getRequest().getParameter("rownum");
        ds.goToRow(Integer.parseInt(rownum));
      }
      ds.deleteRow();
      ds.post();
      ds.saveChanges();
      ds.refresh();
      if(action.equals(String.valueOf(DELETE_RETURN)))
      {
        data.setMessage(showJavaScript("backList();"));
      }
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
      if(!dsMasterTable.getQueryString().equals(SQL))
      {
        dsMasterTable.setQueryString(SQL);
        dsMasterTable.setRowMax(null);
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
      EngineDataSet master = dsMasterTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("train_start"), null, null, null, null, ">="),
        new QueryColumn(master.getColumn("train_end"), null, null, null, null, "<="),
        new QueryColumn(master.getColumn("back_ID"), "VW_PERSON_PXFK", "back_ID", "xm", "xm", "like"),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门
        new QueryColumn(master.getColumn("train_proj"), null, null, null, null, "like"),
      });
      isInitQuery = true;
    }
  }
  /**
   * 0号
   *
   *
   * */
  class Rl_Pxkc_adddel  implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(action.equals(PXKC_ADD))
      {
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String back_ID = dsMasterTable.getValue("back_ID");
      }
      activetab="SetActiveTab(INFO_EX,'INFO_EX_0')";
      data.setMessage(showJavaScript(activetab));
    }
  }


}