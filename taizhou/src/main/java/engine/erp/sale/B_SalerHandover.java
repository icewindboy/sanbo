package engine.erp.sale;

import engine.action.BaseAction;
import engine.action.Operate;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.web.observer.Obactioner;
import engine.common.*;
import engine.html.*;
import engine.project.*;
import java.text.*;
import java.lang.String;
import java.util.*;
import java.util.ArrayList;
import javax.servlet.http.*;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;

public final class B_SalerHandover extends BaseAction implements Operate
{
  private static final String TDYJ_SQL = "SELECT * FROM xs_tdyj ";  //提单移交记录
  private static final String XSTD_SQL = "update xs_td set personid=? where tdid= ? ";  //销售提单
  private static final String TDYJ_SQL_ALL = "SELECT * FROM VW_SALE_TDYJ where 1=1  ? ";//关连
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM xs_tdyj WHERE tdyjid='?'";
  private static final String TDYJ_STRUCT_SQL = "SELECT * FROM xs_tdyj where 1<>1  ";//关连

  public static final String XSTD_ADD    = "9000";
  public static final String XSTD_DEL    = "1001";  //销售提单删除

  public static final String VIEW_DETAIL = "1055";   //主从明细
  public static final String DELETE_RETURN = "1067"; //主从删除操作
  public static final String PERSON_CHANGE = "1068";//选择业务员时
  public  static final String DEPT_CHANGE = "1069";//部门改变
  public  static final String AFTERPERSON_CHANGE = "1070";//选取择移交后业务员

  public  static final String CANCER_APPROVE = "1071";
  public  static final String AFTERDEPT_CHANGE = "1072";
  public  static final String NEW_POST = "1073";
  public  static final String NEW_DEL = "1074";

  //AFTERPERSON_CHANGE
  //DEPT_CHANGE
  public  boolean isApprove = false;     //是否在审批状态
  private boolean isMasterAdd = true;                          //主表是否在添加状态
  private long    masterRow = 0;                               //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap();                 //主表添加行或修改行的引用
  /*主从表的数据集*/
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsrl_xstd  = new EngineDataSet();  //从表
  private EngineDataSet dsxs_tdyjTable = new EngineDataSet();//
  //private EngineDataSet dsxs_tdTable = new EngineDataSet();

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "xs_tdyj");

  public ArrayList arraylist_xstd  = null;
  private RowMap rowInfo = new RowMap();
  public  boolean isAdd = true;
  private long    editrow = 0;
  public  String retuUrl = null;
  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String fgsid = null;   //分公司ID
  public String beforedeptid = "";
  public String beforeid = "";

  /*
  * @从会话中得到业务员移交实例
  * @param request jsp请求
  * @return 返回业务员移交实例
   */
  public static B_SalerHandover getInstance(HttpServletRequest request)
  {
    B_SalerHandover b_SalerHandoverBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_SalerHandoverBean";
      b_SalerHandoverBean = (B_SalerHandover)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_SalerHandoverBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        b_SalerHandoverBean = new B_SalerHandover();
        b_SalerHandoverBean.fgsid = loginBean.getFirstDeptID();
        b_SalerHandoverBean.loginId = loginBean.getUserID();
        b_SalerHandoverBean.loginName = loginBean.getUserName();
        session.setAttribute(beanName, b_SalerHandoverBean);//加入到session中

      }
    }
    return b_SalerHandoverBean;
  }

  /***************************
   *@构造函数
   **************************/
  private B_SalerHandover()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  /*********************************
   *@初始化函数
   *@throws Exception 异常信息
   *********************************/
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsxs_tdyjTable, TDYJ_STRUCT_SQL);// 相当于personid=""
    setDataSetProperty(dsMasterTable, TDYJ_SQL);
    setDataSetProperty(dsrl_xstd, null);  //从表
    dsxs_tdyjTable.setTableName("xs_tdyj");
    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"tdyjID"}, new String[]{"S_XS_TDYJ"}));
    //dsMasterTable.setSort(new SortDescriptor("", new String[]{"tdyjID"}, new boolean[]{false}, null, 0));
    dsxs_tdyjTable.addLoadListener(new com.borland.dx.dataset.LoadListener() {
      public void dataLoaded(LoadEvent e) {
        initRowInfo(false, true);
      }
    }
    );
    dsrl_xstd.setSequence(new SequenceDescriptor(new String[]{"tdId"}, new String[]{"S_XS_TD"}));
    dsrl_xstd.setTableName("XS_TDYJ");

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());//初始化
    addObactioner(String.valueOf(XSTD_DEL), new Master_Add_Edit());
    addObactioner(String.valueOf(XSTD_ADD), new Master_Add_Edit());
    addObactioner(String.valueOf(VIEW_DETAIL),new Master_Add_Edit());//修改主表,及其对应的从表
    //addObactioner(String.valueOf(DELETE_RETURN), new Master_Delete());//删除主表某一行,及其对应的从表
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(PERSON_CHANGE), new Person_Change());//PERSON_CHANGE
    addObactioner(String.valueOf(DEPT_CHANGE), new Dept_Change());
    addObactioner(String.valueOf(AFTERPERSON_CHANGE), new After_Person_Change());
    //&#$//审核部分
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(CANCER_APPROVE), new Cancer_Approve());
    addObactioner(String.valueOf(AFTERDEPT_CHANGE), new After_Dept_Change());
    addObactioner(String.valueOf(NEW_POST), new New_Post());
    addObactioner(String.valueOf(NEW_DEL), new New_Delete());


  }

  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = false;
      if(String.valueOf(ADD).equals(action))
      {
        beforedeptid = "";
        if(dsxs_tdyjTable.isOpen())
          dsxs_tdyjTable.close();

        HttpServletRequest request = data.getRequest();
        masterProducer.init(request, loginId);

        setDataSetProperty(dsxs_tdyjTable,TDYJ_STRUCT_SQL);
        dsxs_tdyjTable.open();
        initRowInfo(true,true);
        data.setMessage(showJavaScript("toDetail();"));
      }
      if(action.equals(VIEW_DETAIL))
      {
        String rownum=data.getRequest().getParameter("rownum");
        dsMasterTable.goToRow(Integer.parseInt(rownum));
        editrow = dsMasterTable.getInternalRow();
        m_RowInfo = new RowMap(dsMasterTable);

      }
    }
  }
  class Master_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String rownum=data.getRequest().getParameter("rownum");
      dsMasterTable.goToRow(Integer.parseInt(rownum));
      dsMasterTable.deleteRow();
      dsMasterTable.saveChanges();
    }
  }
  /**新增的列表**/
  class New_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String rownum=data.getRequest().getParameter("rownum");
      dsxs_tdyjTable.goToRow(Integer.parseInt(rownum));
      dsxs_tdyjTable.deleteRow();
      dsxs_tdyjTable.post();
      arraylist_xstd.remove(Integer.parseInt(rownum));
      //dsxs_tdyjTable.saveChanges();
    }
  }
  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    try
    {
      String operate = request.getParameter("operate");
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
    catch(Exception ex)
    {
      if(dsrl_xstd.isOpen() && dsrl_xstd.changesPending())
        dsrl_xstd.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  public final void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsMasterTable != null)
    {
      dsMasterTable.close();
      dsMasterTable = null;
    }
    if(dsrl_xstd != null)
    {
      dsrl_xstd.close();
      dsrl_xstd = null;
    }
    if(dsxs_tdyjTable != null)
    {
      dsxs_tdyjTable.close();
      dsxs_tdyjTable = null;
    }
    if(masterProducer != null)
    {
      masterProducer.release();
      masterProducer = null;
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
  private final void initRowInfo(boolean isAdd, boolean isInit) //throws java.lang.Exception
  {
    if(arraylist_xstd == null)
      arraylist_xstd = new ArrayList(dsxs_tdyjTable.getRowCount());
    else if(isInit)
      arraylist_xstd.clear();
    RowMap detail=null;
    dsxs_tdyjTable.first();
    for(int i=0;i<dsxs_tdyjTable.getRowCount();i++)
    {
      detail = new RowMap(dsxs_tdyjTable);
      arraylist_xstd.add(detail);
      dsxs_tdyjTable.next();
    }
  }

  /*******************************
   *@把从表数据集数据推入到ArrayList中
   *******************************/
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
  private final void putDetailInfo(HttpServletRequest request,int row)
  {
    RowMap rowInfo = new RowMap(); //getMasterRowinfo();
    rowInfo.put(request);

    int rownum = arraylist_xstd.size();
    String beforeid = rowInfo.get("beforeid");
    LookUp bpersonBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
    RowMap bpersonrow = bpersonBean.getLookupRow(beforeid);

    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)arraylist_xstd.get(i);
      detailRow.put("tdid", rowInfo.get("tdid_"+i));//提单IDarraylist_xstd
      //String beforedeptid = rowInfo.get("deptid");
      detailRow.put("beforedeptid", beforedeptid);//移交前部门ID
      detailRow.put("beforeid", beforeid); //移交前业务员ID
      detailRow.put("beforename", bpersonrow.get("xm"));//移交前业务员
      String afterid = rowInfo.get("afterid_"+i);//移交后业务员ID
      String afterdeptid = rowInfo.get("afterdeptid_"+i);
      detailRow.put("afterid", rowInfo.get("afterid_"+i));//移交后业务员ID
      String aftername = rowInfo.get("aftername_"+i);
      detailRow.put("aftername", rowInfo.get("aftername_"+i));//移交后业务员ID
      detailRow.put("afterdeptid", rowInfo.get("afterdeptid_"+i));
      detailRow.put("tdyjid", rowInfo.get("tdyjid_"+i));
      if(i==row)
      {
        //LookUp personBean = LookupBeanFacade.getInstance(request, SysConstant.BEAN_PERSON);
        // RowMap personrow = personBean.getLookupRow(afterid);
        // String deptid = personrow.get("deptid");
        //String xm = personrow.get("xm");
        //detailRow.put("aftername", xm);//移交后业务员
        // detailRow.put("afterdeptid", deptid);
        detailRow.put("afterid", "");
        detailRow.put("aftername", "");
      }
      arraylist_xstd.set(i,detailRow);
    }
  }

  /*得到从表表对象*/
  //public final EngineDataSet getTdTable(){return dsxs_tdTable;}

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[arraylist_xstd.size()];
    arraylist_xstd.toArray(rows);
    return rows;
  }
  /*得主到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }
  public final EngineDataSet getTdyj()
  {
    return dsxs_tdyjTable;
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
      String content = dsMasterTable.getValue("tdyjid");
      String deptid = dsMasterTable.getValue("afterdeptid");
      approve.putAproveList(dsMasterTable, dsMasterTable.getRow(), "saler_handover", content,deptid);
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
      String id=null;
      isApprove = true;
      isMasterAdd=false;
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      isApprove = true;
      id = data.getParameter("id", "");
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      dsMasterTable.refresh();
      dsMasterTable.readyRefresh();
      m_RowInfo = new RowMap(dsMasterTable);
      String tdid = dsMasterTable.getValue("tdid");
      String afterid = dsMasterTable.getValue("afterid");
      String SQL = combineSQL(XSTD_SQL,"?",new String[]{afterid,tdid});
      dsMasterTable.updateQuery(new String[]{SQL});
    }
  }
  /**
   * 取消
   */
  class Cancer_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(),"saler_handover");
    }
  }
  /*得到从表的多行信息*/
  public final RowMap[] getXstdRowinfos()
  {
    RowMap[] rows = new RowMap[arraylist_xstd.size()];
    arraylist_xstd.toArray(rows);
    return rows;
  }

  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = false;

      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      HttpServletRequest request = data.getRequest();

      masterProducer.init(request, loginId);

      if(dsMasterTable.isOpen() && dsMasterTable.getRowCount() > 0)
        dsMasterTable.empty();
      dsMasterTable.setQueryString(TDYJ_SQL);
      dsMasterTable.setRowMax(null);

      /*
      if(dsxs_tdTable.isOpen())
        dsxs_tdTable.close();
      setDataSetProperty(dsxs_tdTable,combineSQL(TDYJ_SQL_ALL,"?",new String[]{" and beforeid=''"}));
      dsxs_tdTable.open();
      */
      //initRowInfo(false,false,true);
    }
  }
  public void initArrayList()
  {
    if(arraylist_xstd!=null)
    {
      if(arraylist_xstd.size()>0)
      {
        arraylist_xstd.clear();
      }
    }
  }

  class Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      RowMap rowInfo = getMasterRowinfo();
      rowInfo.put(request);
      if (rowInfo.get("afterdeptid").equals(""))
      {
        data.setMessage(showJavaScript("alert('移交后部门必须选择!')"));
        return;
      }

      dsMasterTable.goToInternalRow(masterRow);
      dsMasterTable.setValue("beforeid", rowInfo.get("beforeid"));
      dsMasterTable.setValue("beforename", rowInfo.get("beforename"));
      dsMasterTable.setValue("beforedeptid", rowInfo.get("beforedeptid"));
      dsMasterTable.setValue("afterid", rowInfo.get("afterid"));
      dsMasterTable.setValue("aftername", rowInfo.get("aftername"));
      dsMasterTable.setValue("afterdeptid", rowInfo.get("afterdeptid"));
      dsMasterTable.saveChanges();
      dsMasterTable.refresh();
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }
  class New_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //把网页中的从表的信息推入ArrayList中
      putDetailInfo(data.getRequest(),-1);
      //得到主表主键值
      RowMap detailrow = null;
      dsxs_tdyjTable.first();
      for(int i=0; i<dsxs_tdyjTable.getRowCount(); i++)
      {
        detailrow = (RowMap)arraylist_xstd.get(i);
        if (detailrow.get("afterdeptid").equals(""))
        {
          data.setMessage(showJavaScript("alert('移交后部门必须选择!')"));
          return;
        }
        String tdyjid =dataSetProvider.getSequence("S_XS_TDYJ");
        //新添的记录
        //dsxs_tdyjTable.insertRow(false);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        dsxs_tdyjTable.setValue("czrq", today);
        dsxs_tdyjTable.setValue("czy", loginName);
        dsxs_tdyjTable.setValue("czyID", loginId);
        dsxs_tdyjTable.setValue("fgsID", fgsid);
        dsxs_tdyjTable.setValue("zt", "0");
        dsxs_tdyjTable.setValue("tdyjid", tdyjid);
        //保存主表数据
        dsxs_tdyjTable.setValue("beforeid", detailrow.get("beforeid"));
        dsxs_tdyjTable.setValue("beforename", detailrow.get("beforename"));
        dsxs_tdyjTable.setValue("beforedeptid", detailrow.get("beforedeptid"));
        dsxs_tdyjTable.setValue("afterid", detailrow.get("afterid"));
        dsxs_tdyjTable.setValue("aftername", detailrow.get("aftername"));
        dsxs_tdyjTable.setValue("afterdeptid", detailrow.get("afterdeptid"));
        dsxs_tdyjTable.setValue("tdid", detailrow.get("tdid"));
        dsxs_tdyjTable.post();
        dsxs_tdyjTable.next();
      }
      dsxs_tdyjTable.saveChanges();
      if(String.valueOf(POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
    }
  }
  /**
   *
   * 选择业务员时要触发的事件
   *
   **/
  class Person_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      beforeid = request.getParameter("beforeid");
      beforedeptid= request.getParameter("beforedeptid");
      if(beforeid==null || beforeid.equals(""))
        return;
      EngineDataSet tmp = new EngineDataSet();
        String SQL = combineSQL(TDYJ_SQL_ALL,"?",new String[]{" and afterid is null and beforeid='"+beforeid+"' and beforedeptid='"+beforedeptid+"'"});
        if(tmp.isOpen())
          tmp.close();
        setDataSetProperty(tmp,SQL);
        tmp.open();
        EngineRow row = new EngineRow(dsxs_tdyjTable);
        tmp.first();
        for(int i=0;i<tmp.getRowCount();i++)
        {
          tmp.copyTo(row);
          dsxs_tdyjTable.addRow(row);
          tmp.next();
        }
        initRowInfo(true,true);
    }
  }
  /**
   *
   * 选择移交后业务员时要触发的事件
   *
   **/
  class After_Person_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      String rownum = request.getParameter("rownum");
      int row = Integer.parseInt(rownum);
      putDetailInfo(request,row);

    }
  }
  /**
   *部门改变
   * */
  class Dept_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      m_RowInfo.put(req);
      beforedeptid = req.getParameter("beforedeptid");
    }
  }
  /**
   * 移交后部门改变
   * */
  class After_Dept_Change implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      String rownum = request.getParameter("rownum");
      int row = Integer.parseInt(rownum);
      putDetailInfo(request,row);
    }
  }
}
