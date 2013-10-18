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

public final class B_TrainPlan extends BaseAction implements Operate
{
  private static final String MASTER_STRUT_SQL = "SELECT * FROM rl_pxjh WHERE 1<>1";   //取主表的结构
  private static final String MASTER_SQL    = "SELECT * FROM rl_pxjh where 1=1 ? order by pxbh";    //对主表的参数化查询
  /*以下是对从表的条件查询,需提供ypxxID参数.*/
  private static final String PXKC_SQL    = "SELECT * FROM rl_pxkc WHERE pxjhID= ";//应聘人员培训课程


  //操作
  public static final String PXKC_ADD    = "9000";    //应聘培训课程新增
  public static final String PXKC_DEL    = "9001";    //应聘培训课程删除操作





  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名


  public String activetab = "SetActiveTab(INFO_EX,'INFO_EX_0')";//从表当前的div

  public static final String VIEW_DETAIL = "1055";   //主从明细
  public static final String OPERATE_SEARCH = "1066";//主表查询操作
  public static final String DELETE_RETURN = "1067"; //主从删除操作
  /*主从表的数据集*/
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表培训课程


  private boolean isMasterAdd = true;                          //主表是否在添加状态
  private long    masterRow = 0;                               //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap();                 //主表添加行或修改行的引用
  /*把从表数据放在ArrayList中,这样可以保存多行信息来对应主表*/
  public ArrayList arraylist_rl_pxkc  = null;//从表应聘人员工作经历

  private boolean isInitQuery = false;                         //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_TrainPlan getInstance(HttpServletRequest request)
  {
    B_TrainPlan B_TrainPlanBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_TrainPlanBean";
      B_TrainPlanBean = (B_TrainPlan)session.getAttribute(beanName);
      if(B_TrainPlanBean == null)
      {
        //引用LoginBean

        LoginBean loginBean = LoginBean.getInstance(request);

        B_TrainPlanBean = new B_TrainPlan();
        B_TrainPlanBean.loginId = loginBean.getUserID();
        B_TrainPlanBean.loginName = loginBean.getUserName();
        session.setAttribute(beanName, B_TrainPlanBean);

      }
    }
    return B_TrainPlanBean;
  }

  /**
   * 构造函数
   */
  private B_TrainPlan()
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
    setDataSetProperty(dsDetailTable, null);  //从表应聘人员工作经历


    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"pxbh"}, new String[]{"SELECT pck_base.billNextCode('rl_pxjh','pxbh') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"pxbh"}, new boolean[]{true}, null, 0));


   // dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"pxjhID"}, new String[]{"S_RL_PXJH"}));
    //dsMasterTable.setSort(new SortDescriptor("", new String[]{"pxjhID"}, new boolean[]{false}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"pxkcID"}, new String[]{"S_RL_PXKC"}));


    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());//初始化
    addObactioner(String.valueOf(OPERATE_SEARCH), new Master_Search());//定制查询

    addObactioner(String.valueOf(PXKC_ADD), new Rl_Pxkc_adddel());//新增删除操作
    addObactioner(String.valueOf(PXKC_DEL), new Rl_Pxkc_adddel());//新增删除操作


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
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsMasterTable != null){
      dsMasterTable.close();
      dsMasterTable = null;
    }
    if(dsDetailTable != null){
      dsDetailTable.close();
      dsDetailTable = null;
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
   * 主表新增
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
    else
    {
      //主表新增
String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
//String lsh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('rl_invite_plan','lsh') from dual");
// m_RowInfo.put("lsh", lsh);
 m_RowInfo.put("createDate", today);//制单日期
 m_RowInfo.put("creator", loginName);
         m_RowInfo.put("creatorID", loginId);
      openDetailTable();
      arraylist_rl_pxkc=putDetailToArraylist(dsDetailTable,arraylist_rl_pxkc);

    }
  }
  /**
   *把从表数据集数据推入到ArrayList中
   *
   * */
private final ArrayList putDetailToArraylist(EngineDataSet dsDetail,ArrayList arrlist)
{
  if(!dsDetail.isOpen())
    dsDetail.open();
    arrlist = new ArrayList(dsDetail.getRowCount());
    dsDetail.first();
    for(int i=0; i<dsDetail.getRowCount(); i++)
    {
      RowMap row = new RowMap(dsDetail);
      arrlist.add(row);
      dsDetail.next();
    }
    return arrlist;
}
/**
   * 从表保存操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = getMasterRowinfo();
    rowInfo.put(request);

    int rownum=arraylist_rl_pxkc.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)arraylist_rl_pxkc.get(i);
      detailRow.put("pxjhID", dsMasterTable.getValue("pxjhID"));//
      detailRow.put("kcmc", rowInfo.get("kcmc_"+i));//
      detailRow.put("pxjs", rowInfo.get("pxjs_"+i));//
      detailRow.put("pxjc", rowInfo.get("pxjc_"+i));//
      detailRow.put("pxsj", rowInfo.get("pxsj_"+i));//
      detailRow.put("pxdj", rowInfo.get("pxdj_"+i));//
      detailRow.put("kcfy", rowInfo.get("kcfy_"+i));
      detailRow.put("zxfy", rowInfo.get("zxfy_"+i));
      detailRow.put("qtfy", rowInfo.get("qtfy_"+i));
      arraylist_rl_pxkc.set(i,detailRow);
    }
  }
  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /*得到从表表对象*/
  public final EngineDataSet getPxkcTable(){return dsDetailTable;}

/*打开从表*/
  public final void openDetailTable()
  {
    String pxjhID = dsMasterTable.getValue("pxjhID");
    /*应聘人员工作经历*/
    dsDetailTable.setQueryString(PXKC_SQL + (isMasterAdd ? "-1" : pxjhID));
    if(dsDetailTable.isOpen())
      dsDetailTable.refresh();
    else
      dsDetailTable.open();

  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到应聘人员工作经历从表的多行信息*/
  public final RowMap[] getPxkcRowinfos() {
    RowMap[] rows = new RowMap[arraylist_rl_pxkc.size()];
    arraylist_rl_pxkc.toArray(rows);
    return rows;
  }

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
   *
   *初始化从表信息
   */
  public void initArrayList()
  {
    if(arraylist_rl_pxkc!=null){
    if(arraylist_rl_pxkc.size()>0)
    {
      arraylist_rl_pxkc.clear();
    }
    }

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
        //String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('emp','bm','','',6) from dual");
        m_RowInfo.clear();
        //m_RowInfo.put("bm", code);
        initArrayList();
      }
      //打开从表
      openDetailTable();
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
      putDetailInfo(data.getRequest());
      //得到主表的数据集
      EngineDataSet ds = getMaterTable();
      //所要修改或查询的主表的一条记录信息
      RowMap rowInfo = getMasterRowinfo();
      String temp = checkMasterInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
       if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      //得到主表主键值
      String pxjhID = null;
      if(isMasterAdd){
        ds.insertRow(false);
        pxjhID = isMasterAdd ? dataSetProvider.getSequence("S_RL_PXJH") : ds.getValue("pxjhID");

        ds.setValue("pxjhID", pxjhID);
        ds.setValue("creatorID", loginId);
        ds.setValue("creator", loginName);//操作员
        ds.setValue("createDate", rowInfo.get("createDate"));
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail_rl_pxkc = getPxkcTable();
      detail_rl_pxkc.first();
      for(int i=0; i<detail_rl_pxkc.getRowCount(); i++)
      {
        detailrow = (RowMap)arraylist_rl_pxkc.get(i);
        //新添的记录
        if(isMasterAdd)
          detail_rl_pxkc.setValue("pxjhID", pxjhID);
        detail_rl_pxkc.setValue("kcmc", detailrow.get("kcmc"));//
        detail_rl_pxkc.setValue("pxjs", detailrow.get("pxjs"));
        detail_rl_pxkc.setValue("pxjc", detailrow.get("pxjc"));//
        detail_rl_pxkc.setValue("pxsj", detailrow.get("pxsj"));//
        detail_rl_pxkc.setValue("pxdj", detailrow.get("pxdj"));
        detail_rl_pxkc.setValue("kcfy", detailrow.get("kcfy"));
        detail_rl_pxkc.setValue("zxfy", detailrow.get("zxfy"));
        detail_rl_pxkc.setValue("qtfy", detailrow.get("qtfy"));
        detail_rl_pxkc.next();
      }

      //保存主表数据      String aa=rowInfo.get("deptid");
      ds.setValue("pxbh", rowInfo.get("pxbh"));//
      ds.setValue("jhmc", rowInfo.get("jhmc"));//
      ds.setValue("fzdw", rowInfo.get("fzdw"));//
      ds.setValue("fzbm", rowInfo.get("deptid"));//
      ds.setValue("fzr", rowInfo.get("fzr"));//
      ds.setValue("pxlx", rowInfo.get("pxlx"));//
      ds.setValue("pxxs", rowInfo.get("pxxs"));//
      ds.setValue("pxmb", rowInfo.get("pxmb"));//
      ds.post();
      dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"pxbh"}, new String[]{"SELECT pck_base.billNextCode('rl_pxjh','pxbh') from dual"}));
      ds.saveDataSets(new EngineDataSet[]{ds, detail_rl_pxkc,}, null);
      //刷新lookup的数据集，保持数据的同步.入参是相应的lookup名称
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PERSON);
      if(String.valueOf(POST_CONTINUE).equals(action))
      {
        isMasterAdd = true;
        initRowInfo(true, true, true);//重新初始化从表的各行信息

        m_RowInfo.clear();
        initArrayList();
        initRowInfo(false, false, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
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
      if(dsDetailTable.isOpen())
        dsDetailTable.close();
      String pxjhID = dsMasterTable.getValue("pxjhID");
      setDataSetProperty(dsDetailTable,PXKC_SQL + "'"+pxjhID+"'");
      dsDetailTable.open();
      while(dsDetailTable.getRowCount()>0)
      {
        dsDetailTable.first();
        dsDetailTable.deleteRow();
        dsDetailTable.post();
        dsDetailTable.saveChanges();
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
        new QueryColumn(master.getColumn("pxbh"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("jhmc"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("fzdw"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("fzbm"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("fzr"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("pxlx"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("pxxs"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("pxmb"), null, null, null, null, "="),
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
        putDetailInfo(data.getRequest());
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        String pxjhID = dsMasterTable.getValue("pxjhID");
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("pxjhID", pxjhID);
        dsDetailTable.post();
        RowMap zg_Temp_Row = new RowMap(dsDetailTable);
        arraylist_rl_pxkc.add(zg_Temp_Row);
      }
      if(action.equals(PXKC_DEL))
      {
        putDetailInfo(data.getRequest());
        int rownum=Integer.parseInt(data.getRequest().getParameter("rownum"));
       arraylist_rl_pxkc.remove(rownum);
       dsDetailTable.goToRow(rownum);
       dsDetailTable.deleteRow();
      }
      activetab="SetActiveTab(INFO_EX,'INFO_EX_0')";
      data.setMessage(showJavaScript(activetab));
    }
  }





  /**
   * 校验主表表表单信息从表输入的信息的正确性
   * @return null 表示没有信息,校验通过
   */
  private String checkMasterInfo()
  {
    RowMap rowInfo = getMasterRowinfo();
    //String temp = rowInfo.get("chg_date");
    //if(temp.equals(""))
    //return showJavaScript("alert('变动日期不能为空！');");
    String temp = rowInfo.get("jhmc");
   if(temp.equals(""))
      return showJavaScript("alert('计划名称不能为空！');");
    temp = rowInfo.get("fzr");
    if(temp.equals(""))
      return showJavaScript("alert('负责人不能为空！');");
    //temp = rowInfo.get("personid");
    //  if(temp.equals(""))
    // return showJavaScript("alert('请选择姓名!');");
    return null;
  }







  /**
 * 校验从表表单信息从表输入的信息的正确性
 * @return null 表示没有信息
 */
public String checkDetailInfo()
{
  String temp = null;
  RowMap detailrow = null;
  //应聘人员工作经历
  for(int i=0; i<arraylist_rl_pxkc.size(); i++)
  {
     detailrow = (RowMap)arraylist_rl_pxkc.get(i);

     temp = detailrow.get("kcfy");
    if(temp.length() > 0 && !isDate(temp))
      return showJavaScript("alert('必需是数字');");

    temp = detailrow.get("zxfy");
    if(temp.length() > 0 && !isDate(temp))
    return showJavaScript("alert('必需是数字！');SetActiveTab(INFO_EX,'INFO_EX_0')");

    temp = detailrow.get("qtfy");
    if(temp.length() > 0 && !isDate(temp))
      return showJavaScript("alert('必需是数字！');SetActiveTab(INFO_EX,'INFO_EX_0')");
          }


  return null;
}

}