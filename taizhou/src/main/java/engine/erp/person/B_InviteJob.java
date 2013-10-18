package engine.erp.person;


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
import java.util.*;
import java.text.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 人力资源-招聘计划</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @version 1.0
 */
public final class B_InviteJob extends BaseAction implements Operate
{

  public  static final String DETAIL_MULTI_ADD = "10801";
  public  static final String TO_NEXT_MONTH = "4938434";//结转至下月
  public  static final String MONTH_CHANGE = "555558888";//所选月份变化
  public  static final String OVER = "88888888888";
  private static final String ZPJH_STRUT_SQL = "SELECT * FROM rl_zpjh WHERE 1<>1 ";//获取表结构的语名
  private static final String ZPJH_SQL    = "SELECT * FROM vw_invite  WHERE 1=1 ? ?   ";//根据参数查询数据
  private static final String YEAR_SQL="select zpnf nf from rl_zpjh ORDER BY nf DESC";
  private EngineDataSet ds_InviteTable  = new EngineDataSet();//主表
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  private LookUp personBean = null;
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String deptid ="";//主表
  public String  year="";//
  public String month="";//
  public String currentmonth="";//当前
  public String currentyear="";//当前
  private User user = null;
  /**
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_InviteJob getInstance(HttpServletRequest request)
  {
    B_InviteJob B_InviteJobBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "B_InviteJobBean";
      B_InviteJobBean = (B_InviteJob)session.getAttribute(beanName);
      if(B_InviteJobBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        B_InviteJobBean = new B_InviteJob();
        B_InviteJobBean.user = loginBean.getUser();
        session.setAttribute(beanName, B_InviteJobBean);
      }
    }
    return B_InviteJobBean;
  }
  /**
   * 构造函数
   */
  private B_InviteJob()
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
    ds_InviteTable.setTableName("rl_zpjh");
    setDataSetProperty(ds_InviteTable, ZPJH_STRUT_SQL);
    Master_Add masterAdd=new Master_Add();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(String.valueOf(ADD), masterAdd);
    addObactioner(String.valueOf(POST), new Master_Post());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
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
      if(ds_InviteTable.isOpen() && ds_InviteTable.changesPending())
        ds_InviteTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * Session失效时，调用的函数
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {
    if(ds_InviteTable != null){
      ds_InviteTable.close();
      ds_InviteTable = null;
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
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit)
  {
    //是否是主表
    if(isMaster){
      if(isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();
      if(!isAdd)
        m_RowInfo.put(getInviteTable());
    }
    else
    {
      EngineDataSet dsDetail = ds_InviteTable;
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
   * 保存操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   **/
  private final void putDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = new RowMap();
    //保存网页的所有信息
    rowInfo.put(request);
    //从数据集中获取记录行数
    if(!ds_InviteTable.isOpen())
    {
      ds_InviteTable.open();
    }
    int rownum = ds_InviteTable.getRowCount();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("deptid", rowInfo.get("deptid_"+i));//部门中文名称
      detailRow.put("jan", rowInfo.get("jan_"+i));//一月
      detailRow.put("feb", rowInfo.get("feb_"+i));
      detailRow.put("mar", rowInfo.get("mar_"+i));
      detailRow.put("apr", rowInfo.get("apr_"+i));
      detailRow.put("may", rowInfo.get("may_"+i));
      detailRow.put("jun", rowInfo.get("jun_"+i));
      detailRow.put("jul", rowInfo.get("jul_"+i));
      detailRow.put("aug", rowInfo.get("aug_"+i));
      detailRow.put("sep", rowInfo.get("sep_"+i));
      detailRow.put("oct", rowInfo.get("oct_"+i));
      detailRow.put("nov", rowInfo.get("nov_"+i));
      detailRow.put("dec", rowInfo.get("dec_"+i));
      detailRow.put("bwzp", rowInfo.get("bwzp_"+i));
      d_RowInfos.set(i,detailRow);
    }
  }
    /*得到表对象*/
  public final EngineDataSet getInviteTable()
  {
    return ds_InviteTable;
  }
    /*得到从表表对象*/

  public final EngineDataSet getDetailTable(){

    return ds_InviteTable;

  }
    /*得到主表的年份列表*/
  public final EngineDataSet getYearList()
  {
    EngineDataSet dsyear=new EngineDataSet();
    try{
      setDataSetProperty(dsyear,YEAR_SQL);
      dsyear.open();
      }catch(Exception e){}
      return dsyear;
  }

    /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }
    /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
    /*当前操作的月份*/
  public final String getMonth(){return month;}
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
      GregorianCalendar calendar=new GregorianCalendar();
      currentyear=String.valueOf(calendar.get(Calendar.YEAR));//返回当前的年份
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      ds_InviteTable.setQueryString(combineSQL(ZPJH_SQL,"?",new String[]{" AND zpnf="+currentyear,""}));
      ds_InviteTable.open();
      initRowInfo(false,false,true);
    }
  }
  /**
   *
   */
  class Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request=data.getRequest();
      putDetailInfo(request);
      int i=0;
      ds_InviteTable.first();
      for(;i<ds_InviteTable.getRowCount();i++)
      {
        RowMap tmpRow = (RowMap)d_RowInfos.get(i);
        ds_InviteTable.setValue("jan",tmpRow.get("jan"));
        ds_InviteTable.setValue("feb",tmpRow.get("feb"));
        ds_InviteTable.setValue("mar",tmpRow.get("mar"));
        ds_InviteTable.setValue("apr",tmpRow.get("apr"));
        ds_InviteTable.setValue("may",tmpRow.get("may"));
        ds_InviteTable.setValue("jun",tmpRow.get("jun"));
        ds_InviteTable.setValue("jul",tmpRow.get("jul"));
        ds_InviteTable.setValue("aug",tmpRow.get("aug"));
        ds_InviteTable.setValue("sep",tmpRow.get("sep"));
        ds_InviteTable.setValue("oct",tmpRow.get("oct"));
        ds_InviteTable.setValue("nov",tmpRow.get("nov"));
        ds_InviteTable.setValue("dec",tmpRow.get("dec"));
        ds_InviteTable.setValue("bwzp",tmpRow.get("bwzp"));


        ds_InviteTable.next();
        ds_InviteTable.post();
      }
      ds_InviteTable.saveChanges();
    }
  }
  /**
   * 根据月份的变化显示主表的相应记录
   */



  /**
   *  查询操作
   */
  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();//得到WHERE子句
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(ZPJH_SQL, "?", new String[]{" and "+user.getHandleDeptValue(), SQL});//组装SQL语句
      if(!ds_InviteTable.getQueryString().equals(SQL))
      {
        ds_InviteTable.setQueryString(SQL);
        ds_InviteTable.setRowMax(null);//以便dbNavigator刷新数据集
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
      EngineDataSet detail = ds_InviteTable;
      if(!detail.isOpen())
        detail.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(detail.getColumn("zpnf"), null, null, null, null, "="),
        new QueryColumn(detail.getColumn("deptid"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }
  /**
   *  从表新增操作
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      deptid=ds_InviteTable.getValue("deptid");
      if(deptid.equals(""))
        return;
      if(action.equals(String.valueOf(DETAIL_MULTI_ADD)))
      {
        RowMap detailrow = null;
        String multiIdInput = m_RowInfo.get("multiIdInput");
        if(multiIdInput.length() == 0)
          return;
        String[] personids = parseString(multiIdInput,",");
        for(int i=0; i < personids.length; i++)
        {
          if(personids[i].equals("-1"))
            continue;
          RowMap personRow = getpersonNameBean(req).getLookupRow(personids[i]);
          String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM rl_gzkx WHERE deptid='"+deptid+"' and personid='"+personids[i]+"'");
          if(!count.equals("0"))
            continue;

        }
        d_RowInfos.add(detailrow);
      }
      initRowInfo(false,false,true);
    }
  }
  /**
   *  完成操作
   */

  /**
   *引用操作
   * */
  class Master_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request=data.getRequest();
      String buid = request.getParameter("buid");//
      if(buid.equals(""))
        return;
      String count = dataSetProvider.getSequence("select count(*) from rl_zpjh where zpnf="+currentyear+" and deptid="+buid);
      if(!count.equals("0"))
      {
        data.setMessage(showJavaScript("alert('已有该部门!')"));
        return;
      }
      putDetailInfo(request);
      ds_InviteTable.insertRow(false);
      ds_InviteTable.setValue("deptid",buid);
      ds_InviteTable.setValue("zpnf",currentyear);
      RowMap newRow = new RowMap(ds_InviteTable);
      d_RowInfos.add(newRow);
    }
  }
  /**
   *  从表删除操作
   */

  class Detail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request=data.getRequest();
      putDetailInfo(request);
      EngineDataSet ds = getDetailTable();
      String buid = request.getParameter("buid");
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      //删除临时数组的一列数据
      ds.goToRow(rownum);
      ds.deleteRow();
      d_RowInfos.remove(rownum);
      ds.saveChanges();
      ds.refresh();
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
}
