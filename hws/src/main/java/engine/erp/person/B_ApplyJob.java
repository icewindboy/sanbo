package engine.erp.person;

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

/*************************************
 * @<title>:应聘信息</title>
 * @Copyright:
 * @author:
 ************************************/


public final class B_ApplyJob extends BaseAction implements Operate
{
  private static final String MASTER_STRUT_SQL = "SELECT * FROM rl_ypxx WHERE 1<>1";   //取主表的结构
  private static final String MASTER_SQL    = "SELECT * FROM rl_ypxx where 1=1 ? ";    //对主表的参数化查询
  /*以下是对从表的条件查询,需提供ypxxID参数.*/
  private static final String YPGZJL_SQL    = "SELECT * FROM rl_ypgzjl WHERE ypxxID= ";//应聘人员工作经历
  private static final String YPJYQK_SQL    = "SELECT * FROM rl_ypjyqk WHERE ypxxID= ";//应聘人员教育情况
  private static final String MSXX_SQL      = "SELECT * FROM rl_msxx WHERE  ypxxID= ";//应聘人员面试信息



  //操作
  public static final String YPGZJL_ADD    = "9000";    //应聘人员工作经历新增
  public static final String YPGZJL_DEL    = "9001";    //应聘人员工作经历删除操作

  public static final String YPJYQK_ADD    = "9003";    //应聘人员教育情况新增
  public static final String YPJYQK_DEL    = "9004";    //应聘人员教育情况删除操作

  public static final String MSXX_ADD = "9005";         //应聘人员面试信息新增
  public static final String MSXX_DEL = "9006";         //应聘人员面试信息删除操作


  public String activetab = "SetActiveTab(INFO_EX,'INFO_EX_0')";//从表当前的div

  public static final String VIEW_DETAIL = "1055";   //主从明细
  public static final String OPERATE_SEARCH = "1066";//主表查询操作
  public static final String DELETE_RETURN = "1067"; //主从删除操作
  /*主从表的数据集*/
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsrl_ypgzjl  = new EngineDataSet();//从表应聘人员工作经历
  private EngineDataSet dsrl_ypjyqk  = new EngineDataSet();//从表应聘人员教育情况
  private EngineDataSet dsrl_msxx  = new EngineDataSet();//应聘人员面试信息


  private boolean isMasterAdd = true;                          //主表是否在添加状态
  private long    masterRow = 0;                               //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap();                 //主表添加行或修改行的引用
  /*把从表数据放在ArrayList中,这样可以保存多行信息来对应主表*/
  public ArrayList arraylist_rl_ypgzjl  = null;//从表应聘人员工作经历
  public ArrayList arraylist_rl_ypjyqk  = null;//从表应聘人员教育情况
  public ArrayList arraylist_rl_msxx    = null;//应聘人员面试信息


  private boolean isInitQuery = false;                         //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  private String ypxxID=null;

  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_ApplyJob getInstance(HttpServletRequest request)
  {
    B_ApplyJob B_ApplyJobBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_ApplyJobBean";
      B_ApplyJobBean = (B_ApplyJob)session.getAttribute(beanName);
      if(B_ApplyJobBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        B_ApplyJobBean = new B_ApplyJob();
        session.setAttribute(beanName, B_ApplyJobBean);
      }
    }
    return B_ApplyJobBean;
  }

  /**
   * 构造函数
   */
  private B_ApplyJob()
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
    setDataSetProperty(dsrl_ypgzjl, null);  //从表应聘人员工作经历
    setDataSetProperty(dsrl_ypjyqk, null);  //从表应聘人员教育情况
    setDataSetProperty(dsrl_msxx, null);    //应聘人员面试信息

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"ypxxID"}, new String[]{"S_RL_YPXX"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"ypxxID"}, new boolean[]{false}, null, 0));

    dsrl_ypgzjl.setSequence(new SequenceDescriptor(new String[]{"gzjlID"}, new String[]{"S_RL_YPGZJL"}));
    dsrl_ypjyqk.setSequence(new SequenceDescriptor(new String[]{"jyqkID"}, new String[]{"S_RL_YPJYQK"}));
    dsrl_msxx.setSequence(new SequenceDescriptor(new String[]{"msxxID"}, new String[]{"S_RL_MSXX"}));


    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());//初始化
    addObactioner(String.valueOf(OPERATE_SEARCH), new Master_Search());//定制查询

    addObactioner(String.valueOf(YPGZJL_ADD), new Rl_Ypgzjl_adddel());//应聘人员工作经历新增删除操作
    addObactioner(String.valueOf(YPGZJL_DEL), new Rl_Ypgzjl_adddel());//应聘人员工作经历新增删除操作YPGZJL_DEL

    addObactioner(String.valueOf(YPJYQK_DEL), new Rl_Ypjyqk_adddel());//应聘人员教育情况情况新增删除操作
    addObactioner(String.valueOf(YPJYQK_ADD), new Rl_Ypjyqk_adddel());//应聘人员教育情况情况新增删除操作

    addObactioner(String.valueOf(MSXX_ADD), new Rl_Msxx_adddel());    //应聘人员面试信息新增操作
    addObactioner(String.valueOf(MSXX_DEL), new Rl_Msxx_adddel());    //应聘人员面试信息删除操作


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
    if(dsrl_ypgzjl != null){
      dsrl_ypgzjl.close();
      dsrl_ypgzjl = null;
    }
    if(dsrl_ypjyqk != null){
      dsrl_ypjyqk.close();
      dsrl_ypjyqk = null;
    }
    if(dsrl_msxx != null)
    {
      dsrl_msxx.close();
      dsrl_msxx = null;
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
      openDetailTable();
      arraylist_rl_ypgzjl=putDetailToArraylist(dsrl_ypgzjl,arraylist_rl_ypgzjl);
      arraylist_rl_ypjyqk=putDetailToArraylist(dsrl_ypjyqk,arraylist_rl_ypjyqk);
      arraylist_rl_msxx = putDetailToArraylist(dsrl_msxx,arraylist_rl_msxx);
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

    int rownum=arraylist_rl_ypgzjl.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)arraylist_rl_ypgzjl.get(i);
      //detailRow.put("ypxxID", dsMasterTable.getValue("ypxxID"));//
      detailRow.put("kssj", rowInfo.get("gz_kssj_"+i));//
      detailRow.put("jssj", rowInfo.get("gz_jssj_"+i));//
      detailRow.put("gzdw", rowInfo.get("gzdw_"+i));//
      detailRow.put("zw", rowInfo.get("zw_"+i));//
      detailRow.put("bz", rowInfo.get("bz2_"+i));//备注
      arraylist_rl_ypgzjl.set(i,detailRow);
    }
    //应聘人员教育情况
    rownum=arraylist_rl_ypjyqk.size();
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)arraylist_rl_ypjyqk.get(i);
      //detailRow.put("ypxxID", dsMasterTable.getValue("ypxxID"));//
      detailRow.put("kssj", rowInfo.get("kssj_"+i));//
      detailRow.put("jssj", rowInfo.get("jssj_"+i));//
      detailRow.put("byxx", rowInfo.get("byxx_"+i));//
      detailRow.put("sxzy", rowInfo.get("sxzy_"+i));//
      detailRow.put("bz", rowInfo.get("bz_"+i));//
      detailRow.put("zmr", rowInfo.get("zmr_"+i));
      arraylist_rl_ypjyqk.set(i,detailRow);
    }
    //应聘人员面试信息
    rownum = arraylist_rl_msxx.size();
    for(int i = 0; i < rownum; ++i)
    {
      detailRow = (RowMap)arraylist_rl_msxx.get(i);
      //detailRow.put("ypxxID", dsMasterTable.getValue("ypxxID"));//
      detailRow.put("mssj", rowInfo.get("mssj_"+i));//
      detailRow.put("msfs", rowInfo.get("msfs_"+i));//
      detailRow.put("msry", rowInfo.get("msry_"+i));//
      //detailRow.put("msry", rowInfo.get("msry_"+i));//
      detailRow.put("msyj", rowInfo.get("msyj_"+i));//
      detailRow.put("zwppxx", rowInfo.get("zwppxx_"+i));//
    }
  }
  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /*得到从表表对象*/
  public final EngineDataSet getYpgzjlTable(){return dsrl_ypgzjl;}
  public final EngineDataSet getYpjyqkTable(){return dsrl_ypjyqk;}
  public final EngineDataSet getMsxxTable(){return dsrl_msxx;}



/*打开从表*/
  public final void openDetailTable()
  {
    //String ypxxID = dsMasterTable.getValue("ypxxID");
    /*应聘人员工作经历*/
    dsrl_ypgzjl.setQueryString(YPGZJL_SQL + (isMasterAdd ? "-1" : ypxxID));
    if(dsrl_ypgzjl.isOpen())
      dsrl_ypgzjl.refresh();
    else
      dsrl_ypgzjl.open();

    /*应聘人员教育情况*/
    dsrl_ypjyqk.setQueryString(YPJYQK_SQL + (isMasterAdd ? "-1" : ypxxID));
    if(dsrl_ypjyqk.isOpen())
      dsrl_ypjyqk.refresh();
    else
      dsrl_ypjyqk.open();

    /*应聘人员面试信息*/
    dsrl_msxx.setQueryString(MSXX_SQL + (isMasterAdd ? "-1" : ypxxID));
    if(dsrl_msxx.isOpen())
      dsrl_msxx.refresh();
    else
      dsrl_msxx.refresh();
  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到应聘人员工作经历从表的多行信息*/
  public final RowMap[] getYpgzjlRowinfos() {
    RowMap[] rows = new RowMap[arraylist_rl_ypgzjl.size()];
    arraylist_rl_ypgzjl.toArray(rows);
    return rows;
  }
  /*得到应聘人员教育情况从表的多行信息*/
  public final RowMap[] getYpjyqkRowinfos() {
    RowMap[] rows = new RowMap[arraylist_rl_ypjyqk.size()];
    arraylist_rl_ypjyqk.toArray(rows);
    return rows;
  }

  /*得到应聘人员面试信息从表的多行信息*/
  public final RowMap[] getMsxxRowinfos()
  {
    RowMap[] rows = new RowMap[arraylist_rl_msxx.size()];
    arraylist_rl_msxx.toArray(rows);
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
    if(arraylist_rl_ypgzjl!=null){
    if(arraylist_rl_ypgzjl.size()>0)
    {
      arraylist_rl_ypgzjl.clear();
    }
    }
    if(arraylist_rl_ypjyqk!=null){
    if(arraylist_rl_ypjyqk.size()>0)
    {
      arraylist_rl_ypjyqk.clear();
    }
    }
    if(arraylist_rl_msxx !=null)
    {
      if(arraylist_rl_msxx.size() > 0)
      {
        arraylist_rl_msxx.clear();
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
        ypxxID = dsMasterTable.getValue("ypxxID");
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
      //校验表单数据
      String temp = checkMasterInfo();//检验主表
      if(temp != null)
      {
        data.setMessage(temp);
        return ;
      }
       temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return ;
      }
       if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      //得到主表主键值
      //String ypxxID = null;
      if(isMasterAdd){
        ds.insertRow(false);
        ypxxID =  dataSetProvider.getSequence("S_RL_YPXX");

        ds.setValue("ypxxID", ypxxID);
      }
      RowMap detailrow = null;

    /*保存应聘人员教育情况*/
      EngineDataSet detail_rl_ypjyqk = getYpjyqkTable();
      detail_rl_ypjyqk.first();
      for(int i=0; i<detail_rl_ypjyqk.getRowCount(); i++)
      {
        detailrow = (RowMap)arraylist_rl_ypjyqk.get(i);
        //新添的记录
        if(isMasterAdd)
          detail_rl_ypjyqk.setValue("ypxxID", ypxxID);
        detail_rl_ypjyqk.setValue("byxx", detailrow.get("byxx"));//
        detail_rl_ypjyqk.setValue("sxzy", detailrow.get("sxzy"));//jssj
        detail_rl_ypjyqk.setValue("zmr", detailrow.get("zmr"));
        detail_rl_ypjyqk.setValue("kssj", detailrow.get("kssj"));//
        detail_rl_ypjyqk.setValue("jssj", detailrow.get("jssj"));
        detail_rl_ypjyqk.setValue("bz", detailrow.get("bz"));//备注

        detail_rl_ypjyqk.next();
      }

      //保存从表的数据
      EngineDataSet detail_rl_ypgzjl = getYpgzjlTable();
      detail_rl_ypgzjl.first();
      for(int i=0; i<detail_rl_ypgzjl.getRowCount(); i++)
      {
        detailrow = (RowMap)arraylist_rl_ypgzjl.get(i);
        //新添的记录
        if(isMasterAdd)
          detail_rl_ypgzjl.setValue("ypxxID", ypxxID);
        detail_rl_ypgzjl.setValue("gzdw", detailrow.get("gzdw"));//
        detail_rl_ypgzjl.setValue("zw", detailrow.get("zw"));//
        detail_rl_ypgzjl.setValue("kssj", detailrow.get("kssj"));//
        detail_rl_ypgzjl.setValue("jssj", detailrow.get("jssj"));
        detail_rl_ypgzjl.setValue("bz", detailrow.get("bz"));//备注
        detail_rl_ypgzjl.next();
      }
      /*保存应聘人员面试信息*/
      EngineDataSet detail_rl_msxx = getMsxxTable();
      detail_rl_msxx.first();
      for(int i = 0; i < detail_rl_msxx.getRowCount(); i++)
      {
        detailrow = (RowMap)arraylist_rl_msxx.get(i);
        if(isMasterAdd)
          detail_rl_msxx.setValue("ypxxID", ypxxID);
          detail_rl_msxx.setValue("msfs",detailrow.get("msfs"));   //
          detail_rl_msxx.setValue("msry",detailrow.get("msry"));   //
          detail_rl_msxx.setValue("mssj",detailrow.get("mssj"));
          detail_rl_msxx.setValue("msyj",detailrow.get("msyj"));  //
          detail_rl_msxx.setValue("zwppxx",detailrow.get("zwppxx"));
          detail_rl_msxx.next();
      }
      //保存主表数据
      //ds.setValue("bm", bm);//
      ds.setValue("xm", rowInfo.get("xm"));//
      ds.setValue("ypzw", rowInfo.get("ypzw"));//
      ds.setValue("sex", rowInfo.get("sex"));//
      ds.setValue("date_born", rowInfo.get("date_born"));//
      ds.setValue("study", rowInfo.get("study"));//
      ds.setValue("addr", rowInfo.get("addr"));//
      ds.setValue("phone", rowInfo.get("phone"));//


      ds.setValue("mobile", rowInfo.get("mobile"));//
      ds.setValue("email", rowInfo.get("email"));//
      ds.setValue("msry", rowInfo.get("msry"));//
      ds.setValue("sfzhm", rowInfo.get("sfzhm"));//
      ds.setValue("bz", rowInfo.get("bz"));//
      //ds.setValue("dgskf", rowInfo.get("dgskf"));//
      ds.setValue("jg", rowInfo.get("jg"));//
      ds.setValue("mz", rowInfo.get("mz"));//
      ds.setValue("zc", rowInfo.get("zc"));//
      ds.setValue("zzmm", rowInfo.get("zzmm"));//
      ds.setValue("sfms", rowInfo.get("sfms"));//
      ds.setValue("sfly", rowInfo.get("sfly"));//是否录用
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail_rl_ypgzjl,detail_rl_ypjyqk,detail_rl_msxx}, null);
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
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    public String checkDetailInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      //应聘人员工作经历
      for(int i=0; i<arraylist_rl_ypgzjl.size(); i++)
      {
         detailrow = (RowMap)arraylist_rl_ypgzjl.get(i);

         temp = detailrow.get("date_born");
        if(temp.length() > 0 && !isDate(temp))
          return showJavaScript("alert('出生日期非法！');");

        temp = detailrow.get("kssj");
        if(temp.length() > 0 && !isDate(temp))
        return showJavaScript("alert('工作经历的开始日期非法！');SetActiveTab(INFO_EX,'INFO_EX_1')");

        temp = detailrow.get("jssj");
        if(temp.length() > 0 && !isDate(temp))
          return showJavaScript("alert('工作经历的结束日期非法！');SetActiveTab(INFO_EX,'INFO_EX_1')");
              }
      //应聘人员教育情况
      for(int i=0; i<arraylist_rl_ypjyqk.size(); i++)
      {
        detailrow = (RowMap)arraylist_rl_ypjyqk.get(i);
        temp = detailrow.get("byxx");
        if(temp.equals(""))
          return showJavaScript("alert('毕业学校不能为空!');SetActiveTab(INFO_EX,'INFO_EX_0')");

        temp = detailrow.get("sxzy");
        if(temp.equals(""))
          return showJavaScript("alert('所学专业不能为空!');SetActiveTab(INFO_EX,'INFO_EX_0')");

        temp = detailrow.get("jy_kssj");
        if(temp.length() > 0 && !isDate(temp))
          return showJavaScript("alert('教育情况的开始日期非法！');SetActiveTab(INFO_EX,'INFO_EX_0')");

        temp = detailrow.get("jssj");
        if(temp.length() > 0 && !isDate(temp))
          return showJavaScript("alert('教育情况的结束日期非法！');SetActiveTab(INFO_EX,'INFO_EX_0')");
      }

      return null;
    }

    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      String temp = rowInfo.get("xm");
      if(temp.equals(""))
        return showJavaScript("alert('姓名不能为空！');");
      temp = rowInfo.get("bz");
      if(temp.getBytes().length > getMaterTable().getColumn("bz").getPrecision())
        return showJavaScript("alert('您输入的备注的内容太长了！');");
      return null;
    }
  }
  /**
   * 主表删除操作

  class Master_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getMaterTable();
      if(!action.equals(String.valueOf(DEL)))
      {
      if(isMasterAdd){
        data.setMessage(showJavaScript("backList();"));
        return;
      }
      dsMasterTable.goToInternalRow(masterRow);
      }else
      {
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
  }*/

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
       if(dsrl_ypgzjl.isOpen())
         dsrl_ypgzjl.close();
       //String ypxxID = dsMasterTable.getValue("ypxxID");
       setDataSetProperty(dsrl_ypgzjl,YPGZJL_SQL + "'"+ypxxID+"'");
       dsrl_ypgzjl.open();
       while(dsrl_ypgzjl.getRowCount()>0)
       {
         dsrl_ypgzjl.first();
         dsrl_ypgzjl.deleteRow();
         dsrl_ypgzjl.post();
         dsrl_ypgzjl.saveChanges();
       }
       if(dsrl_ypjyqk.isOpen())
         dsrl_ypjyqk.close();
       //String ypxxID = dsMasterTable.getValue("ypxxID");
       setDataSetProperty(dsrl_ypjyqk,YPJYQK_SQL + "'"+ypxxID+"'");
       dsrl_ypjyqk.open();
       while(dsrl_ypjyqk.getRowCount()>0)
       {
         dsrl_ypjyqk.first();
         dsrl_ypjyqk.deleteRow();
         dsrl_ypjyqk.post();
         dsrl_ypjyqk.saveChanges();
       }
       if(dsrl_msxx.isOpen())
         dsrl_msxx.close();
       //String ypxxID = dsMasterTable.getValue("ypxxID");
       setDataSetProperty(dsrl_msxx,MSXX_SQL + "'"+ypxxID+"'");
       dsrl_msxx.open();
       while(dsrl_msxx.getRowCount()>0)
       {
         dsrl_msxx.first();
         dsrl_msxx.deleteRow();
         dsrl_msxx.post();
         dsrl_msxx.saveChanges();
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

  /*******************************************
   *@查询操作
   ******************************************/
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
        new QueryColumn(master.getColumn("xm"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("sex"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("ypzw"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("phone"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("email"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("addr"), null, null, null, null, "="),
      });
      isInitQuery = true;
    }
  }
    /**
     * 应聘人员工作经历
     *
     * */
  class Rl_Ypgzjl_adddel  implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(action.equals(YPGZJL_ADD))
      {
        putDetailInfo(data.getRequest());
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        //String ypxxID = dsMasterTable.getValue("ypxxID");
        dsrl_ypgzjl.insertRow(false);
        dsrl_ypgzjl.setValue("ypxxID", ypxxID);
        dsrl_ypgzjl.post();
        RowMap zg_Temp_Row = new RowMap(dsrl_ypgzjl);
        arraylist_rl_ypgzjl.add(zg_Temp_Row);
      }
      if(action.equals(YPGZJL_DEL))
      {
        putDetailInfo(data.getRequest());
        int rownum=Integer.parseInt(data.getRequest().getParameter("rownum"));
       arraylist_rl_ypgzjl.remove(rownum);
       dsrl_ypgzjl.goToRow(rownum);
       dsrl_ypgzjl.deleteRow();
      }
      activetab="SetActiveTab(INFO_EX,'INFO_EX_1')";
      data.setMessage(showJavaScript(activetab));
    }
  }
  /**
   *应聘人员教育情况
   *
   **/
  class Rl_Ypjyqk_adddel  implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(action.equals(YPJYQK_ADD))
      {
        putDetailInfo(data.getRequest());
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        //String ypxxID = dsMasterTable.getValue("ypxxID");
        dsrl_ypjyqk.insertRow(false);
        dsrl_ypjyqk.setValue("ypxxID", ypxxID);
        dsrl_ypjyqk.post();
        RowMap zg_Temp_Row = new RowMap(dsrl_ypjyqk);
        arraylist_rl_ypjyqk.add(zg_Temp_Row);
      }
      if(action.equals(YPJYQK_DEL))
      {
        putDetailInfo(data.getRequest());
        int rownum=Integer.parseInt(data.getRequest().getParameter("rownum"));
        arraylist_rl_ypjyqk.remove(rownum);
        dsrl_ypjyqk.goToRow(rownum);
        dsrl_ypjyqk.deleteRow();
      }
      activetab="SetActiveTab(INFO_EX,'INFO_EX_0')";
      data.setMessage(showJavaScript(activetab));
    }
  }

  class Rl_Msxx_adddel  implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(action.equals(MSXX_ADD))
      {
        putDetailInfo(data.getRequest());
        if(!isMasterAdd)
          dsMasterTable.goToInternalRow(masterRow);
        //String ypxxID = dsMasterTable.getValue("ypxxID");
        dsrl_msxx.insertRow(false);
        dsrl_msxx.setValue("ypxxID", ypxxID);
        dsrl_msxx.post();
        RowMap zg_Temp_Row = new RowMap(dsrl_msxx);
        arraylist_rl_msxx.add(zg_Temp_Row);
      }
      if(action.equals(MSXX_DEL))
      {
        putDetailInfo(data.getRequest());
        int rownum=Integer.parseInt(data.getRequest().getParameter("rownum"));
        arraylist_rl_msxx.remove(rownum);
        dsrl_msxx.goToRow(rownum);
        dsrl_msxx.deleteRow();
      }
      activetab="SetActiveTab(INFO_EX,'INFO_EX_2')";
      data.setMessage(showJavaScript(activetab));
    }
  }
}
