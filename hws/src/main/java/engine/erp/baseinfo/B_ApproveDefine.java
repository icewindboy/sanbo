package engine.erp.baseinfo;

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
import engine.common.*;
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
 * <p>Title: 基础管理--审批流程定义---</p>
 * <p>Description: 基础管理--审批流程定义<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author engine
 * @version 1.0
 */
public final class B_ApproveDefine extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "10901";
  public  static final String SPR_ADD = "20891";
  public  static final String SHOW_SPR = "22891";
  public  static final String DETAIL_SPR_DEL="54267";
  public  static final String SPRPOST="975674";
  private static final String MASTER_SQL    = "SELECT * FROM sp_xm ORDER BY spxmmc";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM sp_xmmx WHERE 1<>1 ";
  private static final String DETAIL_SQL    = "SELECT * FROM sp_xmmx WHERE spxmID= ";//
  private static final String DETAIL_SPR_SQL    = "SELECT * FROM sp_spr ";//
  private static final String SPR_SQL    = "SELECT * FROM sp_spr where spxmmxID= ";//
  private static final String SPR_DEL_SQL    = "delete  FROM sp_spr WHERE spxmmxID=";
  private static final String SPLX_SQL = "select * from sp_xmlx t WHERE  t.spxmid='?' ";
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsSPRTable  = new EngineDataSet();//从表
  private EngineDataSet dssplx = new EngineDataSet();//审批类型
  private boolean isMasterAdd = true;    //是否在添加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  private ArrayList spr_RowInfos = null; //从表审批人多行记录的引用
  private long    detaileditrow = 0;//从表保存修改操作的行记录指针
  public  boolean isDetailAdd = true;//从表是否在添加状态
  private RowMap rowInfo = new RowMap();//保存用户输入的信息
  public  String retuUrl = null;
  public String spxmid="";      //审批项目IDspxmmxID
  public String spxmmxID="";      //审批项目明细ID
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_ApproveDefine getInstance(HttpServletRequest request)
  {
    B_ApproveDefine b_ApproveDefineBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_ApproveDefineBean";
      b_ApproveDefineBean = (B_ApproveDefine)session.getAttribute(beanName);
      if(b_ApproveDefineBean == null)
      {
        b_ApproveDefineBean = new B_ApproveDefine();
        session.setAttribute(beanName, b_ApproveDefineBean);
      }
    }
    return b_ApproveDefineBean;
  }
  /**
   * 构造函数
   */
  private B_ApproveDefine()
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
    setDataSetProperty(dsMasterTable, MASTER_SQL);
    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
    setDataSetProperty(dsSPRTable, DETAIL_SPR_SQL);
    setDataSetProperty(dssplx,combineSQL(SPLX_SQL,"?",new String []{""}));

    //dsMasterTable.setSort(new SortDescriptor("", new String[]{"url"}, new boolean[]{false}, null, 0));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"spxmmxID"}, new String[]{"s_sp_xmmx"}));
    dsDetailTable.setSort(new SortDescriptor("", new String[]{"spdj"}, new boolean[]{false}, null, 0));
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(POST), new Detail_Post());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(SHOW_SPR), new Show_SPR());
    addObactioner(String.valueOf(SPR_ADD), new SPR_Add());
    addObactioner(String.valueOf(DETAIL_SPR_DEL), new Detail_SPR_Del());
    addObactioner(String.valueOf(SPRPOST), new Detail_SPR_Post());
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
    if(dsSPRTable != null){
      dsSPRTable.close();
      dsSPRTable = null;
    }
    log = null;
    m_RowInfo = null;
    rowInfo = null;
    d_RowInfos = null;
    spr_RowInfos=null;
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
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否是主表
    if(isMaster){
      if(isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();
      if(!isAdd)
        m_RowInfo.put(getMaterTable());
    }
    else
    {
      EngineDataSet dsDetail = dsDetailTable;
      if(d_RowInfos == null)
        d_RowInfos = new ArrayList(dsDetail.getRowCount());
      else if(isInit)
        d_RowInfos.clear();
      dsDetail.first();
      for(int i=0; i<dsDetail.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsDetail);
        d_RowInfos.add(row);
        dsDetail.next();
      }
    }
  }
  private final void initSPRRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
      if(spr_RowInfos == null)
        spr_RowInfos = new ArrayList(dsSPRTable.getRowCount());
      else if(isInit)
        spr_RowInfos.clear();
      dsSPRTable.first();
      for(int i=0; i<dsSPRTable.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsSPRTable);
        spr_RowInfos.add(row);
        dsSPRTable.next();
      }
  }
  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
      if(!dsMasterTable.isOpen())
        dsMasterTable.open();
      return dsMasterTable;
    }
    /*得到从表表对象*/
    public final EngineDataSet getDetailTable(){
      if(!dsDetailTable.isOpen())
        dsDetailTable.open();
      return dsDetailTable;
    }
    /*得到从表表对象*/
    public final EngineDataSet getDetailSPRTable(){
      if(!dsSPRTable.isOpen())
        dsSPRTable.open();
      return dsSPRTable;
    }
    /*得到从表表对象*/
    public final EngineDataSet getDetailSPLXTable(){
      if(!dssplx.isOpen())
        dssplx.open();
      return dssplx;
    }
    /*打开从表*/
    private final void openDetailTable(boolean isMasterAdd)
    {
      String SQL = DETAIL_SQL + (isMasterAdd ? "-1" : spxmid);
      if(!dsDetailTable.isOpen())
      {
        dsDetailTable.setQueryString(SQL);
        dsDetailTable.open();
      }
      else
      {
        dsDetailTable.setQueryString(SQL);
        dsDetailTable.refresh();
      }
      dssplx.setQueryString(combineSQL(SPLX_SQL,"?",new String[]{spxmid}));
      dssplx.refresh();
    }
    /*打开审批人从表*/
    private final void openSPRDetailTable()
    {
      String SQL = SPR_SQL + spxmmxID;
      if(!dsSPRTable.isOpen())
      {
        dsSPRTable.setQueryString(SQL);
        dsSPRTable.open();
      }
      else if(!SQL.equals(dsSPRTable.getQueryString()))
      {
        dsSPRTable.setQueryString(SQL);
        dsSPRTable.refresh();
      }
    }
    /*得到主表一行的信息*/
    public final RowMap getMasterRowinfo() { return m_RowInfo; }
    /*得到从表多列的信息*/
    public final RowMap[] getDetailRowinfos() {
      RowMap[] rows = new RowMap[d_RowInfos.size()];
      d_RowInfos.toArray(rows);
      return rows;
    }
    /*得到审批人从表多列的信息*/
    public final RowMap[] getDetailSPRRowinfos() {
      RowMap[] rows = new RowMap[spr_RowInfos.size()];
      spr_RowInfos.toArray(rows);
      return rows;
    }
  /*得到一列的信息*/
  public final RowMap getRowinfo()
  {
    return rowInfo;
  }
  /**
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  public final boolean masterIsAdd() {return isMasterAdd; }
  /**
   * 得到选中的行的行数
   * @return 若返回-1，表示没有选中的行
   */
  public final int getSelectedRow()
  {
    if(masterRow < 0)
      return -1;
    dsMasterTable.goToInternalRow(masterRow);
    return dsMasterTable.getRow();
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
    RowMap rowInfo = getMasterRowinfo();//保存网页的所有信息
    rowInfo.put(request);
    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    String url="";
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      String spxmtsmxID=rowInfo.get("spxmtsmxID_"+i);
      String tsz=rowInfo.get("tsz_"+i);
      detailRow.put("spxmtsmxID", spxmtsmxID);//审批项目特殊明细ID
      String spdj=rowInfo.get("spdj_"+i);
      detailRow.put("spdj", spdj);//审批等级
      detailRow.put("spmc", rowInfo.get("spmc_"+i));//审批名称
      detailRow.put("tsz", tsz);//特殊值
      detailRow.put("ztmstgz", rowInfo.get("ztmstgz_"+i));//状态描述通过值
      detailRow.put("ztmsbtgz", rowInfo.get("ztmsbtgz_"+i));//状态描述不通过值
      detailRow.put("URL", rowInfo.get("URL_"+i));//URL
      detailRow.put("personid", rowInfo.get("personid_"+i));//personid
    }
  }
  private final void putDetailSPRInfo(HttpServletRequest request)
  {
    RowMap rowInfo = getMasterRowinfo();//保存网页的所有信息
    rowInfo.put(request);
    int rownum = spr_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)spr_RowInfos.get(i);
      String d = "personid_"+i;
      String deptid = rowInfo.get("deptid_"+i);
      detailRow.put("spxmmxID", spxmmxID);//审批项目明细ID
      detailRow.put("personid", rowInfo.get("personid_"+i));//personid
      detailRow.put("deptid", rowInfo.get("deptid_"+i));//personid
    }
  }
  /**
   * 显示从表的列表信息
   */
  class ShowDetail implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int rownum=Integer.parseInt(data.getParameter("rownum"));
      dsMasterTable.goToRow(rownum);
      masterRow = dsMasterTable.getInternalRow();
      spxmid = dsMasterTable.getValue("spxmid");
      //打开从表
      openDetailTable(false);
      initRowInfo(false,false,true);
    }
  }
  /**
   * 显示从表的列表信息(审批人)
   */
  class Show_SPR implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      spxmmxID=data.getParameter("rownum");
      openSPRDetailTable();
      initSPRRowInfo(false,true);
    }
  }
  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      dsMasterTable.setQueryString(MASTER_SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      dsSPRTable.setQueryString(DETAIL_SPR_SQL);
      dsSPRTable.setRowMax(null);
      if(dsSPRTable.isOpen() && dsSPRTable.getRowCount() > 0)
        dsSPRTable.empty();

    }
  }
  /**
   *  从表新增操作
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      putDetailInfo(request);
      dsDetailTable.insertRow(false);
      dsDetailTable.setValue("spxmmxID", "-1");
      dsDetailTable.setValue("spxmid", spxmid);
      dsDetailTable.setValue("spdj","");//
      dsDetailTable.setValue("spmc","");//
      dsDetailTable.setValue("tsz","");//
      dsDetailTable.setValue("ztmstgz","");//
      dsDetailTable.setValue("ztmsbtgz","");//
      dsDetailTable.setValue("URL",dsMasterTable.getValue("URL"));//
      dsDetailTable.setValue("spxmtsmxID","");//
      dsDetailTable.post();
      dsDetailTable.saveChanges();

      initRowInfo(false,false, true);
    }
  }
  /**
   *  从表新增操作
   */
  class SPR_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      putDetailSPRInfo(request);//列新到ArrayList
      dsSPRTable.insertRow(false);
      dsSPRTable.setValue("spxmmxID", spxmmxID);
      dsSPRTable.setValue("personid", "");
      dsSPRTable.setValue("deptid", "");
      dsSPRTable.post();
      RowMap detailrow = new RowMap(dsSPRTable);
      spr_RowInfos.add(detailrow);
      //initSPRRowInfo(false,false);
    }
  }
  /**
   *  从表保存操作
   */
  class Detail_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      putDetailInfo(request);
      String splx = request.getParameter("splx");
      String temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      engine.project.LookUp spxmBean = engine.project.LookupBeanFacade.getInstance(request, engine.project.SysConstant.BEAN_APPROVE_SPECIAL);
      spxmBean.regData(detail,"spxmtsmxID");
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        detail.setValue("spxmid", spxmid);
        String spdj=detailrow.get("spdj");
        detail.setValue("spdj", spdj);//
        detail.setValue("spmc", detailrow.get("spmc"));//
        String tsz = detailrow.get("tsz");
        detail.setValue("tsz", detailrow.get("tsz"));//
        detail.setValue("ztmstgz", detailrow.get("ztmstgz"));//
        detail.setValue("ztmsbtgz", detailrow.get("ztmsbtgz"));//
        String spxmtsmxID=detailrow.get("spxmtsmxID");
        String url=detailrow.get("URL");
        if(!spxmtsmxID.equals(""))
        {
          RowMap tmpspmx =spxmBean.getLookupRow(spxmtsmxID);
          url=tmpspmx.get("URL");
        }
        detail.setValue("URL", url);//
        detail.setValue("spxmtsmxID", spxmtsmxID);//
        detail.post();
        String ts = detail.getValue("tsz");
        detail.next();
      }
      if(dssplx.getRowCount()==0)
      {
        dssplx.insertRow(false);
        dssplx.setValue("spxmid",spxmid);
      }
      dssplx.setValue("splx",splx);
      dssplx.post();
      detail.saveDataSets(new EngineDataSet[]{detail, dssplx}, null);
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      ArrayList table = new ArrayList(d_RowInfos.size());
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String spdj = detailrow.get("spdj");
        if((temp = checkNumber(spdj, "审批等级")) != null)
          return temp;
        if(spdj.equals(""))
          return showJavaScript("alert('审批等级为空！');");
        if(table.contains(spdj))
          return showJavaScript("alert('审批等级重复！');");
        else
          table.add(spdj);
        String spmc = detailrow.get("spmc");
        if(spmc.equals(""))
          return showJavaScript("alert('审批名称不能空！');");
      }
      return null;
    }
  }
  //Detail_SPR_Post
  /**
   *  从表保存操作
   */
  class Detail_SPR_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      putDetailSPRInfo(request);
      String tmp = checkDetailInfo();
      if(tmp != null)
      {
        data.setMessage(tmp);
        return;
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailSPRTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)spr_RowInfos.get(i);
        detail.setValue("spxmmxID", detailrow.get("spxmmxID"));//
        detail.setValue("personid", detailrow.get("personid"));//
        detail.setValue("deptid", detailrow.get("deptid"));//
        detail.next();
      }
      detail.post();
      detail.saveChanges();
    }
    public String checkDetailInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      for(int i=0; i<spr_RowInfos.size(); i++)
      {
        detailrow = (RowMap)spr_RowInfos.get(i);
        String deptid = detailrow.get("deptid");
        if(deptid.equals(""))
          return showJavaScript("alert('审批部门必选!!');");
        String personid = detailrow.get("personid");
        if(personid.equals(""))
          return showJavaScript("alert('审批人必选!!');");
      }
      return null;
    }
  }
  /**
   *  从表删除操作
   */
  class Detail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getDetailTable();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      ds.goToRow(rownum);
      String spxmmxID=ds.getValue("spxmmxID");
      if(!spxmmxID.equals("-1"))
      {
      String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM sp_jlmx WHERE spxmmxID="+spxmmxID);
      if(!count.equals("0"))
      {
        data.setMessage(showJavaScript("alert('已有项目在审批之中,不能删除此行记录!只有审批完毕才能删除!')"));
        return;
      }
      }
      d_RowInfos.remove(rownum);
      String [] tmp={SPR_DEL_SQL+spxmmxID};
      ds.deleteRow();
      ds.saveChanges(tmp,null);
    }
  }
   //Detail_SPR_Del
  class Detail_SPR_Del implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailSPRInfo(data.getRequest());
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      spr_RowInfos.remove(rownum);

      dsSPRTable.goToRow(rownum);
      dsSPRTable.deleteRow();
    }
  }
}
