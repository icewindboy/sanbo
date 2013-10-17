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
 * <p>Title: 生产-物料可替换件设置</p>
 * <p>Description: 生产-物料可替换件设置<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */
public final class B_BomRefill  extends BaseAction implements Operate
{
  public static final String FIXED_SEARCH = "1009";
  public static final String CHECK_REPLACE_MATERIAL = "2222";

  //抽取在BOM表中子件类型为可选件的子件并把父件也抽出来
  public static final String CAN_REPLACE_BOM_SQL = "SELECT a.cpbm, b.sjcpid, b.cpid, b.sl FROM kc_dm a,sc_bom b WHERE a.cpid=b.cpid AND b.zjlx='2' ? ORDER BY a.cpbm";

  private static final String B_BomRefill_SQL = "SELECT * FROM sc_bomthj WHERE 1<>1 ";//
  private static final String B_Bom_SQL="SELECT * FROM sc_bomthj where kc__cpid= ";//从BOM替换件表里抽取数据
  private static final String B_Bom_SQL2="SELECT pck_produce.isBomFatherNode(@,0,@) FROM dual";//检测替换物料的正确性（是否是被替换件的父件，是否在被替换件父件的子件中存在替换物料）

  private EngineDataSet ds_bom = new EngineDataSet();
  private EngineDataSet ds_replace_bom = new EngineDataSet();
  //数据库维护查询条件
  public  HtmlTableProducer table = new HtmlTableProducer(ds_bom, "b", "b");

  private RowMap rowInfo = new RowMap();//保存页面提交时信息
  private ArrayList d_RowInfos = null; //BOM替换件表里多行记录的引用

  private long    masterRow = -1;         //保存主表修改操作的行记录指针

  public  String retuUrl = null;
  private boolean isInitQuery = false;
  public  String loginName = ""; //登录员工的姓名
  private String fgsID = null;   //分公司ID
  private String bomid = "";   //
  public String father_cpid = "";//父件CPID
  public String replace_cpid = "";   //子件CPID
  public String oldsl = "";
  private String qtyFormat = null;
  /**
   * 定义固定查询类
   */
  private QueryFixedItem fixedQuery = new QueryFixedItem();

  public static B_BomRefill getInstance(HttpServletRequest request)
  {
    B_BomRefill b_BomRefillBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_BomRefillBean";
      b_BomRefillBean = (B_BomRefill)session.getAttribute(beanName);
      if(b_BomRefillBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        b_BomRefillBean = new B_BomRefill();
        b_BomRefillBean.fgsID = loginBean.getFirstDeptID();
        b_BomRefillBean.loginName = loginBean.getUserName();
        b_BomRefillBean.qtyFormat = loginBean.getQtyFormat();
        b_BomRefillBean.ds_replace_bom.setColumnFormat("sl", b_BomRefillBean.qtyFormat);
        session.setAttribute(beanName, b_BomRefillBean);
      }
    }
    return b_BomRefillBean;
  }
  /**
   * 构造函数
   */
  private B_BomRefill()
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
  protected void jbInit() throws Exception
  {
    setDataSetProperty(ds_replace_bom, null);
    setDataSetProperty(ds_bom, null);

    addObactioner(String.valueOf(INIT), new B_BomRefill_Init());
    addObactioner(String.valueOf(POST), new B_BomRefill_Post());//
    addObactioner(String.valueOf(FIXED_SEARCH), new FIXED_SEARCH());
    addObactioner(String.valueOf(ADD), new B_BomRefill_Add());
    addObactioner(String.valueOf(DEL), new B_BomRefill_Del());
    addObactioner(String.valueOf(EDIT), new Master_Edit());
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
    if(ds_replace_bom != null){
      ds_replace_bom.close();
      ds_replace_bom = null;
    }
    if(ds_bom != null){
      ds_bom.close();
      ds_bom = null;
    }
    rowInfo = null;
    d_RowInfos = null;
    log = null;
  }
  private final void putDetailInfo(HttpServletRequest request)
  {
    rowInfo.clear();
    rowInfo.put(request);
    //保存网页的所有信息
    RowMap drowInfo=new RowMap();
    int rownum = ds_replace_bom.getRowCount();
    ds_replace_bom.first();
    for(int i=0; i<rownum; i++)
    {
      drowInfo = (RowMap)d_RowInfos.get(i);
      drowInfo.put("cpid",rowInfo.get("cpid_"+i));
      drowInfo.put("sl",rowInfo.get("sl_"+i));
      drowInfo.put("shl", rowInfo.get("shl_"+i));
      ds_replace_bom.next();
    }
  }
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
  {
    return getClass();
  }
  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
  /*得到替换件表对象*/
  public final EngineDataSet getDetailTable()
  {
    if(!ds_replace_bom.isOpen())
      ds_replace_bom.open();
    return ds_replace_bom;
  }
  /*得到表对象*/
  public final EngineDataSet getMasterTable()
  {
    if(!ds_bom.isOpen())
      ds_bom.open();
    return ds_bom;
  }
  /**
   * 初始化操作的触发类
   */
  class B_BomRefill_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      table.getWhereInfo().clearWhereValues();
      //打开在BOM表中所有可选件物料
      ds_bom.setQueryString(combineSQL(CAN_REPLACE_BOM_SQL, "?", new String[]{""}));
      if(ds_bom.isOpen()){
        ds_bom.readyRefresh();
        ds_bom.refresh();
      }
      else
        ds_bom.open();
      //initRowInfo(true,true);//把BOM中可选件的信息推入到RowMap中
    }
  }
  /**
  * 主表添加或修改操作的触发类
  * 进入从表页面操作
  * 打开从表传递两个参数父件ID和子件ID
  */
  class Master_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      ds_bom.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = ds_bom.getInternalRow();
      father_cpid = ds_bom.getValue("sjcpid");
      replace_cpid = ds_bom.getValue("cpid");
      oldsl = ds_bom.getValue("sl");
      openDetailTable();
      initRowInfo(false, true);
      data.setMessage(showJavaScript("toDetail();"));
    }
  }
  /*打开从表*/
  public final void openDetailTable()
  {
    String SQL = B_Bom_SQL+replace_cpid;

    ds_replace_bom.setQueryString(SQL);
    if(!ds_replace_bom.isOpen())
      ds_replace_bom.open();
    else
      ds_replace_bom.refresh();
  }
  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isMaster,boolean isInit) throws java.lang.Exception
  {
    /**是否是主表
    if(isMaster){
        rowInfo.clear();
        rowInfo.put(getMasterTable());//把BOM中可选件的信息推入到RowMap中
    }
    else
    {
    */
      EngineDataSet dsDetail = getDetailTable();//把替换件信息存在ArrayList中
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
  /**
   * 保存操作的触发类
   */
  class B_BomRefill_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest rq=data.getRequest();
      putDetailInfo(rq);
      String temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      RowMap detail = null;
      String count = null, cpid=null;
      EngineDataSet ds = getDetailTable();
      ds.first();
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        ds.goToRow(i);
        detail = (RowMap)d_RowInfos.get(i);
        cpid = detail.get("cpid");
        double shl = detail.get("shl").length()>0 ? Double.parseDouble(detail.get("shl")) : 0;
        //判断所要保存的被替换物料和替换物料是否已经存在于数据库
        count = dataSetProvider.getSequence("select count(*) from sc_bomthj where kc__cpid='"+replace_cpid+"' and cpid='"+cpid+"'");
        if(count.equals("0"))
          ds.setValue("cpid", cpid);
        ds.setValue("sl", detail.get("sl"));
        ds.setValue("shl", String.valueOf(shl/100));
      }
      ds.post();
      ds.saveChanges();
      data.setMessage(showJavaScript("backList();"));
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo() throws Exception
    {
      String temp;
      String cpid = null;
      RowMap detailrow = null;
      for(int i=0;i<d_RowInfos.size();i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid=detailrow.get("cpid");
        if(cpid.equals(""))
          return showJavaScript("alert('请选择物料！');");
        if(cpid.equals(replace_cpid))
          return showJavaScript("alert('替换物料选择错误');");
        String sl=detailrow.get("sl");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
           return temp;
        String shl=detailrow.get("shl");
        if(shl.length()>0 && (temp = checkNumber(shl, "第"+row+"行数量")) != null)
           return temp;
        String SQL = combineSQL(B_Bom_SQL2, "@", new String[]{replace_cpid,cpid});//判断替换物料是否是被替换物料的父件
        String path = dataSetProvider.getSequence(SQL);
        if(path.equals("1"))
          return showJavaScript("alert('替换物料选择错误,请重新选择!');");
      }
      return null;
    }
  }
 /**
  * <p>从表增加一行操作 </p>
  */
  class B_BomRefill_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest rq=data.getRequest();
      putDetailInfo(rq);
      ds_replace_bom.insertRow(false);
      ds_replace_bom.setValue("kc__cpid",replace_cpid);
      ds_replace_bom.post();
      RowMap detailrow = new RowMap(ds_replace_bom);
      d_RowInfos.add(detailrow);
    }
  }
  /**
  * 添加查询操作的触发类
  */
 class FIXED_SEARCH implements Obactioner
 {
   //----Implementation of the Obactioner interface
   /**
    * 添加或修改的触发操作
    * @parma  action 触发执行的参数（键值）
    * @param  o      触发者对象
    * @param  data   传递的信息的类
    * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
    */
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     table.getWhereInfo().setWhereValues(data.getRequest());
     String SQL = table.getWhereInfo().getWhereQuery();//根据查询条件组装SQL
     if(SQL.length() > 0)
       SQL = " AND "+SQL;
     SQL = combineSQL(CAN_REPLACE_BOM_SQL, "?", new String[]{SQL});
     ds_bom.setQueryString(SQL);
     if(ds_bom.isOpen())
       ds_bom.refresh();
     else
       ds_bom.open();
     ds_bom.setRowMax(null);
    }
 }
  /**
  * <p>从表删除操作 </p>
  */
  class B_BomRefill_Del implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest rq=data.getRequest();
      putDetailInfo(rq);
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      d_RowInfos.remove(rownum);
      ds_replace_bom.goToRow(rownum);
      ds_replace_bom.deleteRow();
    }
  }
}
