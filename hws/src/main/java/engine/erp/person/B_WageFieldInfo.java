package engine.erp.person;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title:人力资源--工资款项设置</p>
 * <p>Description: 人力资源--工资款项设置</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @version 1.0
 */
public final class B_WageFieldInfo extends BaseAction implements Operate
{
  private static final String GZKXSZ_SQL = "SELECT * FROM rl_gzkxsz ORDER BY PXH";//
  private static final String TABLE_COLUMN_SQL="{CALL pck_compile.tableColumn('rl_gzkx','@',@,@,@,@)}";
  private static final String TABLE_COLUMN_DEL_SQL="{CALL pck_compile.dropTableColumn('rl_gzkx','@')}";
  private EngineDataSet dsRl_gzkxsz = new EngineDataSet();
  private EngineRow locateRow = null;
  private RowMap rowInfo = new RowMap();
  public  boolean isAdd = true;
  private long  editrow = 0;
  public  String retuUrl = null;
  /**
   *获取实例
   * */
  public static B_WageFieldInfo getInstance(HttpServletRequest request)
  {
    B_WageFieldInfo b_WageFieldInfoBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_WageFieldInfoBean";
      b_WageFieldInfoBean = (B_WageFieldInfo)session.getAttribute(beanName);
      if(b_WageFieldInfoBean == null)
      {
        b_WageFieldInfoBean = new B_WageFieldInfo();
        session.setAttribute(beanName, b_WageFieldInfoBean);
      }
    }
    return b_WageFieldInfoBean;
  }
  /**
   * 构造函数
   */
  private B_WageFieldInfo()
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
    setDataSetProperty(dsRl_gzkxsz, GZKXSZ_SQL);
    dsRl_gzkxsz.setSort(new SortDescriptor("", new String[]{"pxh"}, new boolean[]{false}, null, 0));
    dsRl_gzkxsz.setSequence(new SequenceDescriptor(new String[]{"gzkxszID"}, new String[]{"S_RL_GZKXSZ"}));
    //添加操作的触发对象
    B_WorkProcedure_Add_Edit add_edit = new B_WorkProcedure_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_WorkProcedure_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_WorkProcedure_Post());
    addObactioner(String.valueOf(DEL), new B_WorkProcedure_Delete());
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
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsRl_gzkxsz.isOpen() && dsRl_gzkxsz.changesPending())
        dsRl_gzkxsz.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsRl_gzkxsz != null){
      dsRl_gzkxsz.close();
      dsRl_gzkxsz = null;
    }
    log = null;
    rowInfo = null;
    locateRow = null;
  }
  protected Class childClassName()
  {
    return getClass();
  }
  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
    if(!isAdd)
      rowInfo.put(getOneTable());
  }
  public final EngineDataSet getOneTable()
  {
    if(!dsRl_gzkxsz.isOpen())
      dsRl_gzkxsz.open();
    return dsRl_gzkxsz;
  }
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 初始化操作的触发类
   */
  class B_WorkProcedure_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
    }
  }
  /**
   * 添加或修改操作的触发类
   */
  class B_WorkProcedure_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsRl_gzkxsz.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsRl_gzkxsz.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }
  /**
   * 保存操作的触发类
   */
  class B_WorkProcedure_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
      rowInfo.put(data.getRequest());
      String temp="";
      String mc = rowInfo.get("mc");
      String id="";
      String dyzdm="";
      if(mc.trim().equals(""))
      {
        data.setMessage(showJavaScript("alert('名称不能空!')"));
      }
      else
      {
      String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM rl_gzkxsz WHERE mc='"+mc+"'");
      if(!count.equals("0")&&isAdd)
      {
        data.setMessage(showJavaScript("alert('此名称已被使用,请从新输入!')"));
        return;
      }
      }
      String cd = rowInfo.get("cd");
      String jd = rowInfo.get("jd");
      String pxh = rowInfo.get("pxh");
      if((temp = checkNumber(cd, "长度")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if((temp = checkNumber(jd, "精度")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if((temp = checkNumber(pxh, "排序号")) != null)
      {
        data.setMessage(temp);
        return;
      }
      if(!isAdd)
        ds.goToInternalRow(editrow);
      if(isAdd)
      {
        ds.insertRow(false);
        id = dataSetProvider.getSequence("S_RL_GZKXSZ");
        dyzdm = "gz"+engine.util.Format.formatNumber(id,"000000");
        ds.setValue("gzkxszID", id);
        ds.setValue("dyzdm", dyzdm);
      }
      ds.setValue("mc", mc);
      ds.setValue("lx", rowInfo.get("lx"));
      ds.setValue("ly", rowInfo.get("ly"));
      if(!rowInfo.get("ly").equals("2"))
        ds.setValue("jsgs", "");
      ds.setValue("cd", cd);
      ds.setValue("jd", jd);
      ds.setValue("pxh", pxh);
      String sql="";
      if(isAdd)
      {
        sql=combineSQL(TABLE_COLUMN_SQL,"@", new String[]{dyzdm,rowInfo.get("lx"),cd,jd,"1"});
      }
      else
      {
        dyzdm=ds.getValue("dyzdm");
        sql=combineSQL(TABLE_COLUMN_SQL,"@", new String[]{dyzdm,rowInfo.get("lx"),cd,jd,"0"});
      }
      ds.post();
      ds.saveChanges(new String[]{sql});
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
      isAdd=false;
    }
  }
  /**
   * 删除操作的触发类
   */
  class B_WorkProcedure_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String rownum=data.getParameter("rownum");
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(rownum));
      //String id=ds.getValue("gzkxszID");
      String field=ds.getValue("dyzdm");
      String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM rl_gzkx WHERE "+field+" IS NOT NULL");
      if(!count.equals("0"))
      {
        data.setMessage(showJavaScript("alert('此项目已被使用,不能删除!')"));
        return;
      }
      String sql=combineSQL(TABLE_COLUMN_DEL_SQL,"@", new String[]{field});
      ds.deleteRow();
      ds.saveChanges(new String[]{sql});
    }
  }
}
