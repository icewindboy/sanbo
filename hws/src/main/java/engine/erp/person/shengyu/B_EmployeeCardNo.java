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
import engine.common.*;
import engine.project.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 员工信用卡号列表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ENGINE</p>
 * @version 1.0
 */
public final class B_EmployeeCardNo extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1009";
  /**
   * 提取物资销售单价信息的SQL语句rl_ygxykh
   */
  private static final String B_EMPLOYEECARDNO_SQL = "SELECT * FROM VW_PERSON_CREDITCARD where 1=1 ? ? ORDER BY BM";//

  /**
   *本地数据集
   * 保存物资销售单价信息的数据集
   */
  private EngineDataSet ds_rl_ygxykh = new EngineDataSet();
  //多行记录的引用
  private ArrayList d_RowInfos = null;
  /**
   * 点击返回按钮的URL
   */
  public  static String retuUrl = null;
  private boolean isInitQuery = false;
  public static String loginName = ""; //登录员工的姓名
  private static String fgsID = null;   //分公司ID
  private User user = null;
  /**
   * 定义固定查询类
   */
  private QueryFixedItem fixedQuery = new QueryFixedItem();
  /**
   * 得到物资销售单价信息的实例
   * @param request jsp请求
   * @return 返回物资销售单价信息的实例
   */
  public static B_EmployeeCardNo getInstance(HttpServletRequest request)
  {
    B_EmployeeCardNo b_employeecardnoBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_employeecardnoBean";
      b_employeecardnoBean = (B_EmployeeCardNo)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      //b_employeecardnoBean = (B_EmployeeCardNo)session.getAttribute(beanName);
      if(b_employeecardnoBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        fgsID = loginBean.getFirstDeptID();
        b_employeecardnoBean = new B_EmployeeCardNo(fgsID);
        b_employeecardnoBean.loginName = loginBean.getUserName();
        b_employeecardnoBean.user = loginBean.getUser();
        session.setAttribute(beanName,b_employeecardnoBean);
      }
    }
    return b_employeecardnoBean;
  }
  /**
   * 构造函数(实例变量:分公司ID为初始化参数)
   */
  private B_EmployeeCardNo(String fgsid)
  {

    this.fgsID = fgsid;
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
    //初始化数据集的SQL语句.
    setDataSetProperty(ds_rl_ygxykh,combineSQL(B_EMPLOYEECARDNO_SQL, "?", new String[]{"",""}));
    //在ds_rl_ygxykh装载完数据后执行initRowInfo(false, true);
    ds_rl_ygxykh.addLoadListener(new com.borland.dx.dataset.LoadListener() {
      public void dataLoaded(LoadEvent e) {
        initRowInfo(false, true);
      }
    });
    //添加操作的触发对象
    addObactioner(String.valueOf(INIT), new B_EmployeeCardNo_Init());
    addObactioner(String.valueOf(POST), new B_EmployeeCardNo_Post());
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());
  }

  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      String opearate = request.getParameter(OPERATE_KEY);
      if(opearate != null && opearate.trim().length() > 0)
      {
        RunData data = notifyObactioners(opearate, request, response, null);
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
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(ds_rl_ygxykh != null){
      ds_rl_ygxykh.close();
      ds_rl_ygxykh = null;
    }
    log = null;
  }
  /**
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = new RowMap();
    //保存网页的所有信息
    rowInfo.put(request);
    //从数据集中获取记录行数
    if(!ds_rl_ygxykh.isOpen())
    {
      ds_rl_ygxykh.open();
    }
    int rownum = ds_rl_ygxykh.getRowCount();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("mc", rowInfo.get("mc_"+i));//部门中文名称
      detailRow.put("personid", rowInfo.get("personid_"+i));//人员ID
      detailRow.put("xm", rowInfo.get("xm_"+i));//姓名
      detailRow.put("lb", rowInfo.get("lb_"+i));//类别
      detailRow.put("xykID", rowInfo.get("xykID_"+i));//信用卡ID
      detailRow.put("ygxykh", rowInfo.get("ygxykh_"+i));//员工信用卡号
      detailRow.put("xykhcd", rowInfo.get("xykhcd_"+i));//员工信用卡号长度
      d_RowInfos.set(i,detailRow);
    }
  }
  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
  {
    return getClass();
  }
  /**
   * 初始化行信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   */
  private final void initRowInfo(boolean isAdd, boolean isInit)
  {
    //EngineDataSet ds_rl_ygxykh_tmp = ds_rl_ygxykh;
    if(d_RowInfos == null)
      d_RowInfos = new ArrayList(ds_rl_ygxykh.getRowCount());
    else if(isInit)
      d_RowInfos.clear();
    ds_rl_ygxykh.first();
    for(int i=0; i<ds_rl_ygxykh.getRowCount(); i++)
    {
      RowMap row = new RowMap(ds_rl_ygxykh);
      d_RowInfos.add(row);
      ds_rl_ygxykh.next();
    }
  }
  /*得到表对象*/
  public final EngineDataSet getDetailTable()
  {
    if(!ds_rl_ygxykh.isOpen())
      ds_rl_ygxykh.open();
    return ds_rl_ygxykh;
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
  class B_EmployeeCardNo_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      fixedQuery.getSearchRow().clear();
      //user.getHandleDeptWhereValue("deptid", "czyid")
      ds_rl_ygxykh.setQueryString(combineSQL(B_EMPLOYEECARDNO_SQL, "?", new String[]{" and "+user.getHandleDeptValue("deptid"),""}));//user.getHandleDeptWhereValue("deptid", "czyid")
      ds_rl_ygxykh.refresh();
    }
  }
  /**
   * 添加查询操作的触发类
   */
  class FIXED_SEARCH implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      QueryBasic queryBasic = fixedQuery;
      queryBasic.setSearchValue(data.getRequest());
      String SQL = queryBasic.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(B_EMPLOYEECARDNO_SQL,"?",new String[]{" and "+user.getHandleDeptValue("deptid"),SQL});
      if(!ds_rl_ygxykh.getQueryString().equals(SQL))
      {
        ds_rl_ygxykh.setQueryString(SQL);
        ds_rl_ygxykh.setRowMax(null);
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
      EngineDataSet master = ds_rl_ygxykh;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      //往来单位dwtxId;信誉额度xyed;信誉等级xydj;回款天数hkts;
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("bm"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("xm"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("lb"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("xykID"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("ygxykh"), null, null, null, null, "like"),
      });
      isInitQuery = true;
    }
  }

  /**
   * 保存操作的触发类
   */
  class B_EmployeeCardNo_Post implements Obactioner
  {
    engine.project.LookUp creditCardBean  = null;
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      RowMap detailrow = null;
      putDetailInfo(data.getRequest());
      HttpServletRequest req = data.getRequest();
      creditCardBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_BANK_CREDIT_CARD);
      String temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      EngineDataSet ds = new EngineDataSet();
      //setDataSetProperty(ds,"SELECT * FROM rl_ygxykh");
      setDataSetProperty(ds,"select b.*,a.xykhcd from rl_yhxyk a,rl_ygxykh b where a.xykid(+)=b.xykid");
      ds.setSequence(new SequenceDescriptor(new String[]{"ygxykhID"}, new String[]{"s_rl_ygxykh"}));
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String personid=detailrow.get("personid");
        String ygxykh=detailrow.get("ygxykh");
        String xykID=detailrow.get("xykID");
        String xykhcd=detailrow.get("xykhcd");
          if(!xykID.trim().equals(""))
          {
            ds.setQueryString("SELECT * FROM rl_ygxykh where personid='"+personid+"'");
            if(!ds.isOpen())
              ds.open();
            ds.refresh();
            if(ds.getRowCount()==0)
            {
              ds.insertRow(false);
              ds.setValue("ygxykhID","-1");
              ds.setValue("personid", detailrow.get("personid"));
            }
            ds.setValue("xykID", detailrow.get("xykID"));
            ds.setValue("ygxykh", detailrow.get("ygxykh"));
            ds.post();
            ds.saveChanges();
            //刷新lookup的数据集，保持数据的同步.入参是相应的lookup名称
            LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE);
          }
      }
    }


    private String checkDetailInfo()
    {
      //RowMap rowInfo = getMasterRowinfo();
      String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      ArrayList list = new ArrayList(d_RowInfos.size());


      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        String personid=detailrow.get("personid");
        String ygxykh=detailrow.get("ygxykh");
        String xykcd=detailrow.get("xykhcd");
        String xykID=detailrow.get("xykID");
        RowMap prodRow = creditCardBean.getLookupRow(xykID);
       String tmpxykcd=prodRow.get("xykhcd");
        if(xykID.equals(""))
          continue;
        int xykhcd=Integer.parseInt(tmpxykcd);
        //String xykhcd=detailrow.get("xykhcd");
        if(((temp = checkNumber(ygxykh, "第"+row+"行卡号")) != null)&&!ygxykh.equals(""))
            return temp;
        if((ygxykh.length() != xykhcd)&&!ygxykh.equals(""))
        {
          return showJavaScript("alert('第"+row+"行员工信用卡号位数不对');");
        }
        StringBuffer buf = new StringBuffer().append(xykID).append(",").append(ygxykh);
        String xykIDygxykh = buf.toString();
        if(list.contains(xykIDygxykh)&&!ygxykh.equals(""))
          return showJavaScript("alert('第"+row+"行卡号重复！');");
        else
          list.add(xykIDygxykh);
        //return temp;

      }
      return null;
    }

  }
}
