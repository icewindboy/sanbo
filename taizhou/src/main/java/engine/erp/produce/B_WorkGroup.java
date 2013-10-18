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
import java.util.Hashtable;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 工作组设置</p>
 * <p>Description: 工作组设置</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_WorkGroup extends BaseAction implements Operate
{
  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_gzz WHERE 1<>1";//工作组主表表结构
  private static final String MASTER_SQL    = "SELECT * FROM sc_gzz WHERE 1=1 ? ORDER BY gzzbh ";//工作组主表信息
  private static final String DETAIL_SQL    = "SELECT * FROM sc_gzzry WHERE gzzid=";//工作组从表信息

  public  static final String PUT_DEPT = "10901";//选择车间提交事件

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表

  private boolean isMasterAdd = true;    //是否在添加状态
  private long    masterRow = 0;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  /**
   * 工作组设置列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回工作组设置列表的实例
   */
  public static B_WorkGroup getInstance(HttpServletRequest request)
  {
    B_WorkGroup b_WorkGroupBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_WorkGroupBean";
      b_WorkGroupBean = (B_WorkGroup)session.getAttribute(beanName);
      if(b_WorkGroupBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        b_WorkGroupBean = new B_WorkGroup();
        session.setAttribute(beanName, b_WorkGroupBean);
      }
    }
    return b_WorkGroupBean;
  }

  /**
   * 构造函数
   */
  private B_WorkGroup()
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
    setDataSetProperty(dsMasterTable, null);
    setDataSetProperty(dsDetailTable, null);
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"gzzid"}, new String[]{"s_sc_gzz"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"gzzbh"}, new boolean[]{false}, null, 0));

    //dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"cgsqdhwid"}, new String[]{"s_cg_sqdhw"}));
    //dsDetailTable.setSort(new SortDescriptor("", new String[]{"hthwid"}, new boolean[]{false}, null, 0));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(PUT_DEPT), new Put_Dept());//选择车间触发事件
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
    d_RowInfos = null;
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
    //保存网页的所有信息
    rowInfo.put(request);

    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("personid", rowInfo.get("personid_"+i));//员工ID
      detailRow.put("gymcid", rowInfo.get("gymcid_"+i));//工序ID
      detailRow.put("ryjs", formatNumber(rowInfo.get("ryjs_"+i), qtyFormat));//人员基数
    }
  }

  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /*得到从表表对象*/
  public final EngineDataSet getDetailTable(){return dsDetailTable;}

  /*打开从表*/
  private final void openDetailTable()
  {
    String gzzid = dsMasterTable.getValue("gzzid");
    dsDetailTable.setQueryString(DETAIL_SQL + (isMasterAdd ? "-1" : gzzid));

    if(dsDetailTable.isOpen())
      dsDetailTable.refresh();
    else
      dsDetailTable.open();
  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
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
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      //初始化查询项目和内容
       RowMap row = fixedQuery.getSearchRow();
      row.clear();
      dsMasterTable.setQueryString(combineSQL(MASTER_SQL, "?", new String[]{""}));
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
      }
      //打开从表
      openDetailTable();

      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);

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
      putDetailInfo(data.getRequest());

      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      //校验表单数据
      String temp = checkMasterInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);

      //得到主表主键值
      String gzzid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        gzzid = dataSetProvider.getSequence("s_sc_gzz");
        ds.setValue("gzzid", gzzid);
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("gzzid", gzzid);

        detail.setValue("ryjs", detailrow.get("ryjs"));//人员基数
        detail.setValue("gymcid", detailrow.get("gymcid"));//工序名称
        detail.setValue("personid", detailrow.get("personid"));
        //保存
        detail.post();
        detail.next();
      }
      //保存主表数据
      //ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("deptid", rowInfo.get("deptid"));//人员ID
      ds.setValue("gzzbh", rowInfo.get("gzzbh"));//工作组编号
      ds.setValue("gzzmc", rowInfo.get("gzzmc"));//工作组名称
      ds.setValue("gzzms", rowInfo.get("gzzms"));//工作组描述
      //保存
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_WORK_GROUP);

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();
        initRowInfo(false, true, true);//重新初始化从表的各行信息}
      }
      else if(String.valueOf(POST).equals(action))
        data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }

    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      ArrayList table = new ArrayList(d_RowInfos.size());
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String personid = detailrow.get("personid");
        if(personid.equals(""))
          return showJavaScript("alert('工作组员工不能为空！');");
        if(table.contains(personid))
          return showJavaScript("alert('工作组员工重复！');");
        else
          table.add(personid);
        String ryjs = detailrow.get("ryjs");
        if(ryjs.length() > 0 &&(temp = checkNumber(ryjs, "人员基数")) != null)
          return temp;
      }
      return null;
    }

    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo() throws Exception
    {
      RowMap rowInfo = getMasterRowinfo();
      dsMasterTable.goToInternalRow(masterRow);
      String temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('所属部门不能为空！');");
      String gzzbh = rowInfo.get("gzzbh");
      if(gzzbh.equals(""))
        return showJavaScript("alert('工作组编号不能为空！');");
      String oldgzzbh = dsMasterTable.getValue("gzzbh");
      if(isMasterAdd || !gzzbh.equals(oldgzzbh))
      {
        String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('sc_gzz','gzzbh','"+gzzbh+"') from dual");
        if(!count.equals("0"))
        return showJavaScript("alert('工作组编号("+ gzzbh +")已经存在!');");
      }

      String gzzmc = rowInfo.get("gzzmc");
      if(gzzmc.equals(""))
        return showJavaScript("alert('工作组名称不能为空！');");
      if(isMasterAdd || !gzzmc.equals(dsMasterTable.getValue("gzzmc")))
      {
        String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('sc_gzz','gzzmc','"+gzzmc+"') from dual");
        if(!count.equals("0"))
        return showJavaScript("alert('工作组名称("+ gzzmc +")已经存在!');");
      }
      temp = rowInfo.get("gzzms");
      if(temp.getBytes().length > getMaterTable().getColumn("gzzms").getPrecision())
      return showJavaScript("alert('您输入的工作组描述的内容太长了！');");
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
      if(isMasterAdd){
        data.setMessage(showJavaScript("parent.hideInterFrame();"));
        return;
      }
      EngineDataSet ds = getMaterTable();
      ds.goToInternalRow(masterRow);
      String gzzid = ds.getValue("gzzid");
      String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM sc_gzzgzl WHERE gzzid="+gzzid);
      if(!count.equals("0")){
        data.setMessage(showJavaScript("alert('该工作组已被引用不能删除！')"));
        return;
      }
      else{
        dsDetailTable.deleteAllRows();
        ds.deleteRow();
        ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_WORK_GROUP);
      //
        d_RowInfos.clear();
        m_RowInfo.clear();
        data.setMessage(showJavaScript("parent.hideInterFrame();"));
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
      SQL = combineSQL(MASTER_SQL, "?", new String[]{SQL});
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
      //EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("gzzbh"), null, null, null),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("gzzmc"), null, null, null)
      });
      isInitQuery = true;
    }
  }

  /**
   *  从表增加操作
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getDetailTable();
      String gzzid = dsMasterTable.getValue("gzzid");
      ds.insertRow(false);
      ds.setValue("gzzid", gzzid);
      ds.post();
      d_RowInfos.add(new RowMap());
    }
  }
  /**
   *  提交选择车间触发的事件
   */
  class Put_Dept implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      m_RowInfo.put(req);
    }
  }

  /**
   *  从表删除操作
   */
  class Detail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getDetailTable();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      //删除临时数组的一列数据
      d_RowInfos.remove(rownum);
      ds.goToRow(rownum);
      ds.deleteRow();
    }
  }
}
