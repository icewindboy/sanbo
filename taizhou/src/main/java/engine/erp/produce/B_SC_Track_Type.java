package engine.erp.produce;

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

public final class B_SC_Track_Type extends BaseAction implements Operate
{
  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_track_type WHERE 1<>1 order by type_code";   //取主表的结构
  private static final String MASTER_SQL    = "SELECT * FROM sc_track_type where 1=1 ? order by type_code";    //对主表的参数化查询
  /*以下是对从表的条件查询,需提供ypxxID参数.*/
  private static final String PXKC_SQL    = "SELECT * FROM sc_track_proc WHERE track_type_ID = ";//应聘人员培训课程


  //操作
  public static final String PXKC_ADD    = "9000";    //应聘培训课程新增
  public static final String PXKC_DEL    = "9001";    //应聘培训课程删除操作
  public  static final String INVOICE_OVER = "3423513";//完成

  //public String activetab = "SetActiveTab(INFO_EX,'INFO_EX_0')";//从表当前的div

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
  private String track_type_ID = null;
  int s_track_type_ID=0;
  int s_track_proc_ID=0;
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_SC_Track_Type getInstance(HttpServletRequest request)
  {
    B_SC_Track_Type Track_TypeBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "Track_TypeBean";
      Track_TypeBean = (B_SC_Track_Type)session.getAttribute(beanName);
      if(Track_TypeBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        Track_TypeBean = new B_SC_Track_Type();
        session.setAttribute(beanName, Track_TypeBean);
      }
    }
    return Track_TypeBean;
  }

  /**
   * 构造函数
   */
  private B_SC_Track_Type()
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

    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"track_type_ID"}, new String[]{"S_SC_TRACK_TYPE"}));


    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"track_proc_ID"}, new String[]{"S_SC_TRACK_PROC"}));


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
    addObactioner(String.valueOf(INVOICE_OVER), new Invoice_Over());//完成
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
      String operate = request.getParameter(OPERATE_KEY);
      if(operate != null && operate.trim().length() > 0)
      {
        RunData data = notifyObactioners(operate, request, response, null);
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
        initArrayList();
      if(!isAdd)
        m_RowInfo.put(getMaterTable());
      else{
       String type_code = dataSetProvider.getSequence("SELECT pck_base.billNextCode('sc_track_type','type_code') from dual");
       m_RowInfo.put("type_code",type_code);
       openDetailTable();
       arraylist_rl_pxkc=putDetailToArraylist(dsDetailTable,arraylist_rl_pxkc);
      }
     }
     else{

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
      //detailRow.put("track_type_ID", dsMasterTable.getValue("track_type_ID"));//
      detailRow.put("gymcID", rowInfo.get("gymcID_"+i));//
      String a=detailRow.get("gymcID");
      detailRow.put("track_type", rowInfo.get("track_type_"+i));//
      String b=detailRow.get("track_type");
      detailRow.put("order_no", rowInfo.get("order_no_"+i));//
        String c=detailRow.get("order_no");
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

    /*应聘人员工作经历*/
    dsDetailTable.setQueryString(PXKC_SQL+track_type_ID);
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
        track_type_ID = dsMasterTable.getValue("track_type_ID");
        initRowInfo(true, false, false);
        initRowInfo(false, false, false);
      }
      if(isMasterAdd){
        track_type_ID=String.valueOf(--s_track_type_ID);
        initRowInfo(false, isMasterAdd, true);
        initRowInfo(true, isMasterAdd, true);


      }
      //打开从表
      data.setMessage(showJavaScript("toDetail();"));
    }
  }
  /**
* 完成
*/
class Invoice_Over implements Obactioner
{
public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
{
  dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));


  dsMasterTable.saveChanges();
  initRowInfo(true,false,false);
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
       if(!isMasterAdd){
        ds.goToInternalRow(masterRow);
        track_type_ID = ds.getValue("track_type_ID");
       }
      //得到主表主键值

      if(isMasterAdd){
        track_type_ID=dataSetProvider.getSequence("S_SC_TRACK_TYPE");

        ds.insertRow(false);
        ds.setValue("track_type_ID",track_type_ID);
      }

      //保存从表的数据
      RowMap derow = null;
      EngineDataSet detail_rl_pxkc = getPxkcTable();
      detail_rl_pxkc.first();
      for(int i=0; i<arraylist_rl_pxkc.size(); i++)
      {
        derow = (RowMap)arraylist_rl_pxkc.get(i);
        detail_rl_pxkc.setValue("track_type_ID", track_type_ID);//
        detail_rl_pxkc.setValue("gymcID", derow.get("gymcID"));//
        String f=detail_rl_pxkc.getValue("gymcID");
        detail_rl_pxkc.setValue("track_type", derow.get("track_type"));
        String e=detail_rl_pxkc.getValue("track_type");
        detail_rl_pxkc.setValue("order_no", derow.get("order_no"));//
       String d=detail_rl_pxkc.getValue("order_no");

        detail_rl_pxkc.post();
        detail_rl_pxkc.next();
      }

      //保存主表数据

      ds.setValue("type_code", rowInfo.get("type_code"));//

      ds.setValue("type_name", rowInfo.get("type_name"));//
      ds.setValue("type_prop",rowInfo.get("type_prop"));//


      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail_rl_pxkc,}, null);
      //刷新lookup的数据集，保持数据的同步.入参是相应的lookup名称
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCE_TRACK_TYPE);
      if(String.valueOf(POST_CONTINUE).equals(action))
      {
        isMasterAdd = true;


        m_RowInfo.clear();
        initArrayList();

        initRowInfo(true,true,true);;//重新初始化从表的各行信息
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
      String track_type_ID = dsMasterTable.getValue("track_type_ID");
      setDataSetProperty(dsDetailTable,PXKC_SQL + "'"+track_type_ID+"'");
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
      //刷新lookup的数据集，保持数据的同步.入参是相应的lookup名称
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCE_TRACK_TYPE);
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
        new QueryColumn(master.getColumn("track_type_ID"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("type_code"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("type_name"), null, null, null, null, "="),


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

        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("track_type_ID",track_type_ID);
        dsDetailTable.setValue("track_proc_ID",String.valueOf(--s_track_proc_ID));
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

    }
  }
}

