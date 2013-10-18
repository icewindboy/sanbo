package engine.erp.person.shengyu;
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
import engine.dataset.EngineDataSetProvider;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;


public final class B_EmployeeChange extends BaseAction implements Operate
{
  public  static final String CANCER_APPROVE = "10235131";
  public  static final String BDLX_CHANGE = "55555556";
  public  static final String PERSON_ONCHANGE = "55555554";
  private static final String MASTER_STRUT_SQL = "SELECT * FROM rl_emp_change";//返回表结构
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM rl_emp_change_detail where 1<>1 ";//返回表结构
  private static final String MASTER_SQL    = "SELECT a.* FROM rl_emp_change a, emp b WHERE  a.personid=b.personid ORDER BY b.bm DESC";
  //private static final String MASTER_SQL    = "SELECT * FROM rl_emp_change WHERE 1=1 ?   ORDER BY chang_id DESC";
  private static final String DETAIL_SQL    = "SELECT * FROM rl_emp_change_detail WHERE chang_ID='?' ";//
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM rl_emp_change WHERE chang_ID='?'";
  private static final String SEARCH_SQL = "SELECT * FROM VW_PERSON_ZGXXBD WHERE 1=1 ? ";
  private static final String YGFZXX_SQL    = "SELECT * FROM rl_ygfzxx WHERE lx='8' ";//

  private static final String HTH_SQL ="SELECT t.hth FROM rl_zgqtxx t,rl_emp_change k where t.personid=k.personid AND t.personid = ? ";


  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsSearchTable = new EngineDataSet();//查询数据集
  private EngineDataSet dsYgfzxxTable = new EngineDataSet();//员工辅助信息数据集

  //public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, SysConstant.TABLE_SALE_ORDER);//"xs_ht"
  //public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, SysConstant.TABLE_SALE_ORDER_GOODS);
  public boolean isMasterAdd = true;    //是否在添加状态
  private long    masterRow = 0;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private LookUp personBean = null;

  //&#$
  public  boolean isApprove = false;     //是否在审批状态

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String filialeID = null;   //分公司ID
  private String chang_ID="";
  public boolean submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_EmployeeChange getInstance(HttpServletRequest request)
  {
    B_EmployeeChange b_employeeinfoChangeBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_employeeinfoChangeBean";
      b_employeeinfoChangeBean = (B_EmployeeChange)session.getAttribute(beanName);
      if(b_employeeinfoChangeBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        b_employeeinfoChangeBean = new B_EmployeeChange();
        b_employeeinfoChangeBean.qtyFormat = loginBean.getQtyFormat();
        b_employeeinfoChangeBean.priceFormat = loginBean.getPriceFormat();
        b_employeeinfoChangeBean.sumFormat = loginBean.getSumFormat();

        b_employeeinfoChangeBean.filialeID = loginBean.getFirstDeptID();
        b_employeeinfoChangeBean.loginId = loginBean.getUserID();
        b_employeeinfoChangeBean.loginName = loginBean.getUserName();

        session.setAttribute(beanName, b_employeeinfoChangeBean);
      }
    }
    return b_employeeinfoChangeBean;
  }

  /**
   * 构造函数
   */
  private B_EmployeeChange()
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
    setDataSetProperty(dsMasterTable, MASTER_STRUT_SQL);
    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
    setDataSetProperty(dsYgfzxxTable, null);
    dsMasterTable.setTableName("rl_emp_change");

    setDataSetProperty(dsSearchTable, combineSQL(SEARCH_SQL,"?",new String[]{""}));
    String pref = "HT";
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"chang_ID"}, new String[]{"s_rl_emp_change"}));
    //dsMasterTable.setSort(new SortDescriptor("", new String[]{"chang_ID"}, new boolean[]{true}, null, 0));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"chg_detail_ID"}, new String[]{"s_rl_emp_change_detail"}));
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
    addObactioner(String.valueOf(BDLX_CHANGE), new Bdlx_Onchange());
    //&#$//审核部分
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());//取消审批
    addObactioner(String.valueOf(PERSON_ONCHANGE), new Person_Onchage());
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
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  public final boolean masterIsAdd() {return isMasterAdd; }
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
      if(!isAdd){
        m_RowInfo.put(getMaterTable());
      }
      else
      {
        //主表新增
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        //String lsh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('rl_emp_change','lsh') from dual");
       // m_RowInfo.put("lsh", lsh);
        m_RowInfo.put("createDate", today);//制单日期
        m_RowInfo.put("creator", loginName);
        m_RowInfo.put("chg_date", today);
        m_RowInfo.put("state", "0");
      }
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
    //rownum   =   8;
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("chang_ID", dsMasterTable.getValue("chang_ID"));//
      detailRow.put("remove_goods", rowInfo.get("remove_goods_"+i));//
      detailRow.put("remove_thing", rowInfo.get("remove_thing_"+i));//
      detailRow.put("accepter", rowInfo.get("accepter_"+i));//
      detailRow.put("memo", rowInfo.get("memo_"+i));//备注



      detailRow.put("personid", rowInfo.get("personid_"+i));//
      detailRow.put("bdqID", rowInfo.get("bdqID_"+i));//
      detailRow.put("bdhID", rowInfo.get("bdhID_"+i));//

      d_RowInfos.set(i,detailRow);
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
    chang_ID = dsMasterTable.getValue("chang_ID");
    String SQL = isMasterAdd ? "-1" : chang_ID;
     SQL = combineSQL(DETAIL_SQL, "?", new String[]{SQL});
     //if (!isMasterAdd){
       dsDetailTable.setQueryString(SQL);
       //chang_ID = dsMasterTable.getValue("chang_ID");
       // dsDetailTable.setQueryString(DETAIL_SQL + (isMasterAdd ? "-1" : chang_ID));

       if(dsDetailTable.isOpen())
         dsDetailTable.refresh();
       else
         dsDetailTable.open();
     //}
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
      row.put("state", "0");
      //
      if(dsMasterTable.isOpen() && dsMasterTable.getRowCount() > 0)
        dsMasterTable.empty();
      dsMasterTable.setQueryString(MASTER_SQL);
      dsMasterTable.setRowMax(null) ;
      //data.setMessage(showJavaScript("showFixedQuery();"));
      String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      if(code.equals("1"))
        submitType=true;
      else
        submitType=false;
    }
  }
  /**
 * 选择姓名后提交页面的类
 */
  class Person_Onchage implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PERSON);//刷新lookupbean.2004.12.19
      String personid = m_RowInfo.get("personid");
      String chg_type = m_RowInfo.get("chg_type");
      String predeptid = dataSetProvider.getSequence("select deptid from emp where personid='"+personid+"'");
      String preduty   = dataSetProvider.getSequence("select zw from emp where personid='"+personid+"'");
      String preclass   = dataSetProvider.getSequence("select lb from emp where personid='"+personid+"'");
      if(predeptid!=null&&chg_type.equals("2"))
      {
        m_RowInfo.put("chg_before",predeptid);
      }
      else if(predeptid!=null&&chg_type.equals("4"))
      {
        m_RowInfo.put("chg_before",preduty);
      }
      else if(preclass!=null&&chg_type.equals("3"))
      {
      m_RowInfo.put("chg_before",preclass);
      }
      //LookUp personBean = LookupBeanFacade.getInstance(data.getRequest(), SysConstant.BEAN_PERSON);
    }
  }
  //&#$
  /**
   * 审批操作的触发类
   */
  class Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = true;
      isMasterAdd=false;

      HttpServletRequest request = data.getRequest();
      //得到request的参数,值若为null, 则用""代替
      String id = data.getParameter("id", "");
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen())
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      //打开从表
      isMasterAdd=false;
      openDetailTable();
      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
    }
  }
  //&#$
  /**
   * 添加到审核列表的操作类
   */
  class Add_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      String content = dsMasterTable.getValue("chg_type");//?
      if(content.equals("1")){content="职员离职";}
      else if(content.equals("2")){content="部门调动";}
      else if(content.equals("3")){content="类别调动";}
      else if(content.equals("4")){content="职务变迁";}
      else if(content.equals("5")){content="职员复职位";}
      String deptid = dsMasterTable.getValue("deptid");
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "employee_change", content,deptid);
    }
  }
  /**
   * 取消
   */
  class Cancer_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = true;

      HttpServletRequest request = data.getRequest();
      //得到request的参数,值若为null, 则用""代替
      String id = data.getParameter("id", "");
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(),"employee_change");
    }
  }

  /**
   * 2004-2-18添加
   * 改变变动类型
   */
  class Bdlx_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      String oldbdlx=m_RowInfo.get("chg_type");
      putDetailInfo(data.getRequest());//保存输入的明细信息
      String chg_type=m_RowInfo.get("chg_type");
      if(oldbdlx.equals(chg_type))
      {
        return;
      }
      else
      {
        dsDetailTable.empty();
        d_RowInfos.clear();
      }
    }
  }
  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //&#$
      isApprove = false;
      isMasterAdd = String.valueOf(ADD).equals(action);
      //打开从表
      if(!isMasterAdd)
      {
        isMasterAdd=false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        chang_ID = dsMasterTable.getValue("chang_ID");
        synchronized(dsDetailTable){
        openDetailTable();
      }
      }
      else
      {
        synchronized(dsDetailTable){
          openDetailTable();
        }
        isMasterAdd=true;

        dsYgfzxxTable.setQueryString(YGFZXX_SQL);//给数据集设置相应的SQ,打开时执行该SQL
        if (dsYgfzxxTable.isOpen())
          dsYgfzxxTable.refresh();
        else
          dsYgfzxxTable.openDataSet();

        if (dsDetailTable.isOpen())
          dsDetailTable.refresh();
        else
          dsDetailTable.openDataSet();
        //dsDetailTable.deleteAllRows();
        //dsDetailTable.post();
        //String remove_goods[]={"工作移交","作业指导书","车间配件","宿舍鞋柜钥匙","菜票借支","财务欠（借）款","其他借款"};
        for (int i=0;i<dsYgfzxxTable.getRowCount();i++)
        {
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("remove_goods", dsYgfzxxTable.getValue("mc"));
          dsDetailTable.post();
          dsYgfzxxTable.next();
        }
      }
      //判断明细表是否有些change_id的记录.有opendetail.无:则手稿连续六条记录
      //int count = dataSetProvider.sets
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
      EngineDataSet detail = getDetailTable();
      if(detail.getRowCount()<1)
      {
        //ds.refresh();
        data.setMessage(showJavaScript("alert('明细情况需要有数据!');"));
        return;
      }

      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      //得到主表主键值
      String chang_ID = null;
      if(isMasterAdd){
        ds.insertRow(false);
        chang_ID = dataSetProvider.getSequence("s_rl_emp_change");
        //ds.setValue("lsh", rowInfo.get("lsh"));
        ds.setValue("chang_ID", chang_ID);
        ds.setValue("filialeID", filialeID);
        ds.setValue("creatorID", loginId);
        ds.setValue("creator", loginName);//操作员
        ds.setValue("state", "0");//操作员
        ds.setValue("createDate", rowInfo.get("createDate"));
      }
      //保存从表的数据
      RowMap detailrow = null;
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("chang_ID", chang_ID);
        detail.setValue("remove_goods", detailrow.get("remove_goods"));
        detail.setValue("remove_thing", detailrow.get("remove_thing"));
        detail.setValue("accepter", detailrow.get("accepter"));
        detail.setValue("memo", detailrow.get("memo"));//备注
        // String aa = detailrow.get("personid");
        //detail.setValue("personid", detailrow.get("personid"));//
        // detail.setValue("bdqID", detailrow.get("bdqID"));
        //detail.setValue("bdhID", detailrow.get("bdhID"));
        detail.post();
        detail.next();
      }
      //保存主表数据
      ds.setValue("chg_date", rowInfo.get("chg_date"));//变动日期
      //制单日期
      String chang_id = dataSetProvider.getSequence("s_rl_emp_change");
      String aa = rowInfo.get("zw");
      String cc = rowInfo.get("deptid");
      ds.setValue("personid", rowInfo.get("personid"));
      ds.setValue("chg_type", rowInfo.get("chg_type"));//变动类型
      ds.setValue("chg_reason", rowInfo.get("chg_reason"));//变动原因
      String bb = rowInfo.get("chg_type");
      //ds.setValue("chg_before", rowInfo.get("chg_before"));//变动前职务
      if(bb.equals("4"))
      {
        ds.setValue("chg_before",rowInfo.get("zw"));//变动前职务
        ds.setValue("chg_after", rowInfo.get("bdhzw"));//变动后职务
      }
      else if(bb.equals("2"))
      {
        ds.setValue("chg_before", rowInfo.get("ydeptid"));//变动前部门
        ds.setValue("chg_after", rowInfo.get("deptid2"));//变动后部门
      }
      else if(bb.equals("3"))
      {
        ds.setValue("chg_before",rowInfo.get("lb1"));
        ds.setValue("chg_after", rowInfo.get("lb"));//变动后类别
      }
      ds.setValue("memo", rowInfo.get("memo"));//主表备注
      ds.setValue("deptid", rowInfo.get("deptid"));
      //ds.setValue("state", rowInfo.get("state"));//变动原因
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PERSON);
      if(String.valueOf(POST_CONTINUE).equals(action))
      {
        isMasterAdd = true;
        detail.empty();
        initRowInfo(true, true, true);
        initRowInfo(false, true, true);//重新初始化从表的各行信息
        //initRowInfo(false, false, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //temp = detailrow.get("personid");
        //if(temp.equals(""))
        //return showJavaScript("alert('请选择姓名!');");
        //temp = detailrow.get("bdqID");
        //if(temp.equals(""))
          //return showJavaScript("alert('变动前信息为空!');");
        //temp = detailrow.get("bdhID");
       // if(temp.equals(""))
         // return showJavaScript("alert('变动后信息为空!');");
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
      String kk=rowInfo.get("deptid2");
      String bdlx=rowInfo.get("chg_type");
      String temp = rowInfo.get("chg_date");
      if(temp.equals(""))
        return showJavaScript("alert('变动日期不能为空！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('提交部门不能为空！');");
      temp = rowInfo.get("chg_type");
    if(temp.equals(""))
        return showJavaScript("alert('请选择变动类型!');");
    temp = rowInfo.get("personid");
         if(temp.equals(""))
        return showJavaScript("alert('请选择姓名!');");

     temp = rowInfo.get("deptid2");
     if(temp.equals("")&&(bdlx.equals("2")))
        return showJavaScript("alert('变动后部门不能为空!');");
     temp = rowInfo.get("lb");
    if(temp.equals("")&&(bdlx.equals("3")))
        return showJavaScript("alert('变动后职务不能为空!');");
        temp = rowInfo.get("bdhzw");
    if(temp.equals("")&&(bdlx.equals("4")))
        return showJavaScript("alert('变动后类别不能为空!');");
      //  String count="";
     //   String bSQL="";
     //   bSQL = combineSQL(HTH_SQL, "?", new String[]{temp});
     //   count  = dataSetProvider.getSequence(bSQL);
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
        data.setMessage(showJavaScript("backList();"));
        return;
      }
      EngineDataSet ds = getMaterTable();
      ds.goToInternalRow(masterRow);
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
      //
      d_RowInfos.clear();
      data.setMessage(showJavaScript("backList();"));
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
      SQL = combineSQL(SEARCH_SQL, "?", new String[]{SQL});
  //    if(!dsSearchTable.getQueryString().equals(SQL))
   //   {
  //      dsSearchTable.setQueryString(SQL);
  //      dsSearchTable.setRowMax(null);
 //     }
  //    dsSearchTable.refresh();
   //   StringBuffer sb = new StringBuffer();
  //    ArrayList al = new ArrayList();
  //    String zgxxids="";
   //   dsSearchTable.first();
  //    for(int i=0;i<dsSearchTable.getRowCount();i++)
   //   {
  //      String chang_ID = dsSearchTable.getValue("chang_ID");
 //       if(chang_ID!=null&&!chang_ID.equals(""))
 //         if(!al.contains(chang_ID))
//            al.add(chang_ID);
 //       dsSearchTable.next();
 //     }
 //     if(al.size()==0)
 //       zgxxids="0";
 //     else
 //     {
  //      for(int j=0;j<al.size();j++)
  //        sb.append(al.get(j)+",");
 //       zgxxids=sb.append("0").toString();
 //     }
 //     if(!dsMasterTable.isOpen())
 //       dsMasterTable.open();
 //     SQL = combineSQL(MASTER_SQL, "?", new String[]{" AND chang_ID IN("+zgxxids+") "});
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
      EngineDataSet master = dsSearchTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        //new QueryColumn(master.getColumn("lsh"), null, null, null, null, "="),//流水号
        new QueryColumn(master.getColumn("chg_date"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("chg_date"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("createDate"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("createDate"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("chg_type"), null, null, null,null,"="),
        new QueryColumn(master.getColumn("creator"), null, null, null,null,"like"),
        new QueryColumn(master.getColumn("bm"), null, null, null,null,"like"),
        new QueryColumn(master.getColumn("xm"), null, null, null,null,"like"),
        new QueryColumn(master.getColumn("state"), null, null, null, null, "="),
        //new QueryColumn(master.getColumn("username"), null, null, null,null,"like"),
        //new QueryColumn(master.getColumn("chang_ID "), "VW_PERSON_ZGXXBD", "chang_ID", "bm", "bm", "left_like"),//从表产品
      });
      isInitQuery = true;
    }
  }

  /**
   * 从表增加操作
   *
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      String multiIdInput = m_RowInfo.get("multiIdInput");
      if(multiIdInput.length() == 0)
        return;
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "chang_ID");
      String[] personids = parseString(multiIdInput,",");
      for(int i=0; i < personids.length; i++)
      {
        if(personids[i].equals("-1"))
          continue;

        String chang_ID = dsMasterTable.getValue("chang_ID");
        RowMap detailrow = null;
        locateGoodsRow.setValue(0, personids[i]);
        if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap personRow = getpersonNameBean(req).getLookupRow(personids[i]);
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("chg_detail_ID", "-1");
          dsDetailTable.setValue("chang_ID", chang_ID);
          //dsDetailTable.setValue("personid", personids[i]);
          if(m_RowInfo.get("chg_type").equals("2"))
          {
            dsDetailTable.setValue("bdqID", personRow.get("deptid"));
          }
          if(m_RowInfo.get("chg_type").equals("3"))
          {
            String lbq=personRow.get("lb");
            dsDetailTable.setValue("bdqID", personRow.get("lb"));
          }
          if(m_RowInfo.get("chg_type").equals("4"))
          {
            dsDetailTable.setValue("bdqID", personRow.get("zw"));
          }
          dsDetailTable.setValue("bdhID", personRow.get("bdhID"));
          dsDetailTable.setValue("memo", personRow.get("memo"));
          dsDetailTable.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsDetailTable);
          d_RowInfos.add(detailrow);
        }
      }
    }
  }

  /**
   *   从表删除操作
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

  /**
    *人员信息
   */
  public LookUp getpersonNameBean(HttpServletRequest req)
  {
    if(personBean == null)
      personBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PERSON);
    return personBean;
  }
  public String getHth(String personid) throws Exception
  {
    String hth = "";
    if ( personid == null )
      return hth;
    String HTH_SQL ="SELECT nvl(t.hth, '') FROM rl_zgqtxx t,rl_emp_change k where t.personid=k.personid AND t.personid = ? ";
    String bSQL = combineSQL(HTH_SQL, "?", new String[]{personid});
    hth = dataSetProvider.getSequence(bSQL);
    if (hth==null)
    hth="";
    return hth;
  }

}
