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
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 生产-BOM表批量更新</p>
 * <p>Description: 生产-BOM表批量更新<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_BomBatch extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1009";
  private static final String B_BomBatch_SQL = "SELECT * FROM sc_bom WHERE 1<>1 ";//
  private static final String B_Bom_SQL="SELECT pck_produce.isBomFatherNode(@,0,@) FROM dual";
  private static final String SAME_SQL ="select COUNT(*) from sc_bom a,sc_bom b where  a.sjcpid=b.sjcpid and a.cpid= ? and b.cpid= ? ";
  private static final String PARENT_SQL = "SELECT a.* FROM sc_bom a,kc_dm b WHERE a.sjcpid=b.cpid and b.isdelete=0 and a.cpid=";
  public static final String CHECK_REPLACE_MATERIAL = "2222";
  private EngineDataSet ds_bom = new EngineDataSet();
  private RowMap rowInfo = new RowMap();//保存页面提交时信息
  public  String retuUrl = null;
  private boolean isInitQuery = false;
  public  String loginName = ""; //登录员工的姓名
  private String fgsID = null;   //分公司ID
  private QueryFixedItem fixedQuery = new QueryFixedItem();//定义固定查询类
  private boolean pass=true;//物料选择是否正确
  /**
   *获取实例
   * */
  public static B_BomBatch getInstance(HttpServletRequest request)
  {
    B_BomBatch b_BomBatchBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_BomBatchBean";
      b_BomBatchBean = (B_BomBatch)session.getAttribute(beanName);
      if(b_BomBatchBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        String fgsID = loginBean.getFirstDeptID();
        b_BomBatchBean = new B_BomBatch(fgsID);
        b_BomBatchBean.loginName = loginBean.getUserName();
        session.setAttribute(beanName, b_BomBatchBean);
      }
    }
    return b_BomBatchBean;
  }
  /**
   * 构造函数(实例变量:分公司ID为初始化参数)
   */
  private B_BomBatch(String fgsid)
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
    setDataSetProperty(ds_bom,B_BomBatch_SQL);
    ds_bom.setTableName("sc_bom");
    addObactioner(String.valueOf(INIT), new B_BomBatch_Init());//初始化
    addObactioner(String.valueOf(POST), new B_BomBatch_Post());//更新
    addObactioner(String.valueOf(MASTER_SEARCH), new B_BomBatch_Sreach());//显示所有父件物料
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());
    addObactioner(String.valueOf(CHECK_REPLACE_MATERIAL), new Check_Repalce_Materail());//CHECK_REPLACE_MATERIAL
  }
  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      String operate = request.getParameter(OPERATE_KEY);
      if(operate != null && operate.trim().length() > 0)
      {
        RunData data = notifyObactioners(operate, request, response, null);
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
 *
 * 得到网页所填信息
 * */
  public  RowMap getRowinfo()
  {
    return rowInfo;
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(ds_bom != null){
      ds_bom.close();
      ds_bom = null;
    }
    log = null;
  }
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
  {
    return getClass();
  }
  /*得到表对象*/
  public final EngineDataSet getDetailTable()
  {
    if(!ds_bom.isOpen())
      ds_bom.open();
    return ds_bom;
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
  class B_BomBatch_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      if(ds_bom.isOpen())
      ds_bom.empty();
      if(rowInfo.size()>0)
        rowInfo.clear();
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
        SQL = " and "+SQL;
      SQL = "SELECT * FROM sc_bom WHERE cpid='" +rowInfo.get("cpid")+"'"+SQL;
      if(!ds_bom.getQueryString().equals(SQL))
      {
        ds_bom.setQueryString(SQL);
        ds_bom.refresh();
      }
    }
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet master = ds_bom;
      if(!master.isOpen())
        master.open();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("sjcpId"), null, null, null, null, "="),
      });
      isInitQuery = true;
    }
  }

  /**
   * 更新操作的触发类
   */
  class B_BomBatch_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest rq=data.getRequest();
      String ret=putDetailInfo(rq);
      if(!pass)
      {
        data.setMessage(showJavaScript("alert('替换物料选择错误,请重新选择!')"));
        return;
      }
      if(ret!=null)
      {
        data.setMessage(showJavaScript("alert('"+ret+"');"));
        return;
      }
      ds_bom.saveChanges();
      ds_bom.refresh();
      rowInfo.clear();
    }
    /**
     * @param request 网页的请求对象
     * @return 返回HTML或javascipt的语句
     * @throws Exception 异常
     */
  private final String putDetailInfo(HttpServletRequest request) throws Exception
  {
    rowInfo.clear();
    rowInfo.put(request);
    if(!ds_bom.isOpen())
    {
      ds_bom.open();
    }
    int rownum = ds_bom.getRowCount();
    String bereplacecpid=rowInfo.get("cpid");
    String replace_cpid=rowInfo.get("cpid2");
    if(ds_bom.getValue("sjcpId").equals(replace_cpid))
      return "替换物料选择错误!";
    String[] selcpid=request.getParameterValues("sel");
    if(selcpid==null)
    {
      return "没有选择要变动的父件物料!";
    }
    int total=selcpid.length;
    String SQL = combineSQL(B_Bom_SQL, "@", new String[]{bereplacecpid,replace_cpid});
    String path = dataSetProvider.getSequence(SQL);
    if(path.equals("1"))
    {
      return "替换物料选择错误,请重新选择!";
    }
    SQL = combineSQL(SAME_SQL, "?", new String[]{bereplacecpid,replace_cpid});
    path = dataSetProvider.getSequence(SQL);
    if(!path.equals("0"))
    {
      return "替换物料选择错误,请重新选择!";
    }
    for(int i=0; i<total; i++)
    {
      int rowid=Integer.parseInt(selcpid[i]);
      ds_bom.goToRow(rowid);
      ds_bom.setValue("cpid",replace_cpid);
    }
    return null;
  }
  }
  /**
   * 根据所选项的被替换物料cpid筛选父件物料
   */
  class B_BomBatch_Sreach implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest rq=data.getRequest();
      rowInfo.clear();
      //保存网页的所有信息
      rowInfo.put(rq);
      ds_bom.setQueryString(PARENT_SQL+data.getRequest().getParameter("cpid"));
      if(ds_bom.isOpen())
        ds_bom.refresh();
      else
      ds_bom.open();
    }
  }
  /**
  *检查子件物料合法性
  * */
  class Check_Repalce_Materail implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest rq=data.getRequest();
      rowInfo.clear();
      //保存网页的所有信息
      rowInfo.put(rq);
      String cpid=rowInfo.get("cpid");//被替换
      String replace_cpid=rowInfo.get("cpid2");//替换
      String SQL = combineSQL(B_Bom_SQL, "@", new String[]{cpid,replace_cpid});//替换件是否是被替换件的父件
      String path = dataSetProvider.getSequence(SQL);
      if(cpid.equals(replace_cpid))
      {
        pass=false;
        data.setMessage(showJavaScript("alert('替换物料选择错误,请重新选择!')"));
        return;
      }
      if(path.equals("1"))
      {
        pass=false;
        data.setMessage(showJavaScript("alert('替换物料选择错误,请重新选择!')"));
        return;
      }
      SQL = combineSQL(B_Bom_SQL, "@", new String[]{replace_cpid,cpid});//替换件是否是被替换件的父件
      path = dataSetProvider.getSequence(SQL);
      if(path.equals("1"))
      {
        pass=false;
        data.setMessage(showJavaScript("alert('替换物料选择错误,请重新选择!')"));
        return;
      }
      pass=true;
    }
  }
}
