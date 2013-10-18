package engine.erp.sale;

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
 * <p>Title:销售管理--业务员奖金设置</p>
 * <p>Description: 销售管理--业务员奖金设置</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author ENGINE
 * @version 1.0
 */
public final class B_SalerPrixSet extends BaseAction implements Operate
{
  private static final String JJGSSZ_SQL = "SELECT * FROM xs_jjgssz order by pxh";//奖金公式设置表
  private static final String TABLE_COLUMN_SQL="{CALL pck_compile.tableColumn('xs_jj','@',@,@,@,@)}";
  private static final String TABLE_COLUMN_OTHER_SQL="{CALL pck_compile.tableColumn('xs_jj','@',@,@,@,@)}";//其它
  private static final String TABLE_COLUMN_DEL_SQL="{CALL pck_compile.dropTableColumn('xs_jj','@')}";
  private static final String TABLE_COLUMN_DEL_OTHER_SQL="{CALL pck_compile.dropTableColumn('xs_jj','@')}";//其它
  private EngineDataSet dsXs_jjgssz = new EngineDataSet();
  private EngineRow locateRow = null;
  private RowMap rowInfo = new RowMap();
  public  boolean isAdd = true;
  private long  editrow = 0;
  public  String retuUrl = null;
  public static B_SalerPrixSet getInstance(HttpServletRequest request)
  {
    B_SalerPrixSet b_SalerPrixSetBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_SalerPrixSetBean";
      b_SalerPrixSetBean = (B_SalerPrixSet)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_SalerPrixSetBean == null)
      {
        b_SalerPrixSetBean = new B_SalerPrixSet();
        session.setAttribute(beanName, b_SalerPrixSetBean);
      }
    }
    return b_SalerPrixSetBean;
  }
  /**
   * 构造函数
   */
  private B_SalerPrixSet()
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
    setDataSetProperty(dsXs_jjgssz, JJGSSZ_SQL);
    dsXs_jjgssz.setSort(new SortDescriptor("", new String[]{"pxh"}, new boolean[]{false}, null, 0));
    dsXs_jjgssz.setSequence(new SequenceDescriptor(new String[]{"jjgsszID"}, new String[]{"S_XS_JJGSSZ"}));
    //添加操作的触发对象
    B_WorkProcedure_Add_Edit add_edit = new B_WorkProcedure_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_WorkProcedure_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_WorkProcedure_Post());
    addObactioner(String.valueOf(DEL), new B_WorkProcedure_Delete());
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
      if(dsXs_jjgssz.isOpen() && dsXs_jjgssz.changesPending())
        dsXs_jjgssz.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  //----Implementation of the BaseAction abstract class
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsXs_jjgssz != null){
      dsXs_jjgssz.close();
      dsXs_jjgssz = null;
    }
    log = null;
    rowInfo = null;
    locateRow = null;
  }
  //----Implementation of the BaseAction abstract class
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
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
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
    if(!isAdd)
      rowInfo.put(getOneTable());
  }

  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsXs_jjgssz.isOpen())
      dsXs_jjgssz.open();
    return dsXs_jjgssz;
  }
  /**
   *状态
   */
  public boolean isAdd(){return isAdd;}
  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 初始化操作的触发类
   */
  class B_WorkProcedure_Init implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发初始化操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsXs_jjgssz.refresh();
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
    }
  }

  /**
   * 添加或修改操作的触发类
   */
  class B_WorkProcedure_Add_Edit implements Obactioner
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
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsXs_jjgssz.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsXs_jjgssz.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }
  /**
   * 保存操作的触发类
   */
  class B_WorkProcedure_Post implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发保存操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
      rowInfo.put(data.getRequest());
      String temp="";
      String mc = rowInfo.get("mc");
      String id="";
      if(mc.trim().equals(""))
      {
        data.setMessage(showJavaScript("alert('名称不能空!')"));
      }
      else
      {
      String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM xs_jjgssz WHERE mc='"+mc+"'");
      if(!count.equals("0")&&isAdd)
      {
        data.setMessage(showJavaScript("alert('此名称已被使用,请从新输入!')"));
        return;
      }
      }
      String cd = rowInfo.get("cd");
      String jd = rowInfo.get("jd");
      String pxh = rowInfo.get("pxh");
      String ly = rowInfo.get("ly");
      String xm = rowInfo.get("xm");
      String lx = rowInfo.get("lx");
      String dyzdm = "";

      if(lx.equals(""))
      {
        data.setMessage(showJavaScript("alert('必选类型')"));
        return;
      }
      if(ly.equals(""))
      {
        data.setMessage(showJavaScript("alert('必选来源')"));
        return;
      }
      if(ly.equals("3")&&xm.equals(""))
      {
        data.setMessage(showJavaScript("alert('当来源选择其它时,固定项目必选!')"));
        return;
      }
      if((temp = checkNumber(cd, "长度")) != null)
      {
        data.setMessage(temp);
        return;
      }
      double dcd = Double.parseDouble(cd);
      if(dcd<20)
      {
        data.setMessage(showJavaScript("alert('长度太小!')"));
        return;
      }
      if((temp = checkNumber(jd, "精度")) != null)
      {
        data.setMessage(temp);
        return;
      }
      double djd = Double.parseDouble(jd);
      if(dcd<8)
      {
        data.setMessage(showJavaScript("alert('精度太小!')"));
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
        id = dataSetProvider.getSequence("S_XS_JJGSSZ");
        ds.setValue("jjgsszID", id);
        dyzdm="gz"+engine.util.Format.formatNumber(id,"000000");
        ds.setValue("dyzdm",dyzdm );
        ds.setValue("sfxs", "1");
        ds.setValue("sfkxg", "1");
        if(!xm.equals(""))
        {
          dyzdm = xm;
          ds.setValue("dyzdm", rowInfo.get("xm"));
          ds.setValue("sfxs", "0");
          if((rowInfo.get("xm").equals("cgj"))||(rowInfo.get("xm").equals("tcj"))||(rowInfo.get("xm").equals("xsje"))||(rowInfo.get("xm").equals("lqj"))||(rowInfo.get("xm").equals("swbt")))
          {
            ds.setValue("sfkxg", "0");
            ds.setValue("sfxs", "1");
          }
          if((rowInfo.get("xm").equals("jj")))
          {
            ds.setValue("sfkxg", "0");
            ds.setValue("sfxs", "1");
            ds.setValue("ly", "2");
            ly="2";
          }
        }
      }
      ds.setValue("mc", mc);
      ds.setValue("lx", rowInfo.get("lx"));
      ds.setValue("jsgs", rowInfo.get("jsgs"));
      ds.setValue("ly", ly);
      ds.setValue("cd", cd);
      ds.setValue("jd", jd);
      ds.setValue("pxh", pxh);
      String sql="";
      if(isAdd)
      {
        sql=combineSQL(TABLE_COLUMN_SQL,"@", new String[]{dyzdm,rowInfo.get("lx"),cd,jd,"1"});
        if(rowInfo.get("ly").equals("3"))
          sql=combineSQL(TABLE_COLUMN_OTHER_SQL,"@", new String[]{rowInfo.get("xm"),rowInfo.get("lx"),cd,jd,"1"});
      }
      else
      {
        dyzdm=ds.getValue("dyzdm");
        //id=ds.getValue("jjgsszID");
        sql=combineSQL(TABLE_COLUMN_SQL,"@", new String[]{dyzdm,rowInfo.get("lx"),cd,jd,"0"});
        if(rowInfo.get("ly").equals("3"))
          sql=combineSQL(TABLE_COLUMN_OTHER_SQL,"@", new String[]{rowInfo.get("xm"),rowInfo.get("lx"),cd,jd,"0"});
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
    //----Implementation of the Obactioner interface
    /**
     * 触发删除操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String rownum=data.getParameter("rownum");

      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(rownum));
      String id=ds.getValue("jjgsszID");
      String ly = ds.getValue("ly");
      String field=ds.getValue("dyzdm");
      String count = "";
      if(ly.equals("3"))
      {
        field = ds.getValue("dyzdm");
        count = dataSetProvider.getSequence("SELECT COUNT(*) FROM xs_jj WHERE "+field+" IS NOT NULL");
      }
      else
        count = dataSetProvider.getSequence("SELECT COUNT(*) FROM xs_jj WHERE "+field+" IS NOT NULL");
      if(!count.equals("0"))
      {
        data.setMessage(showJavaScript("alert('此项目已被使用,不能删除!')"));
        return;
      }
      String sql=combineSQL(TABLE_COLUMN_DEL_SQL,"@", new String[]{field});
      if(ly.equals("3"))
        sql=combineSQL(TABLE_COLUMN_DEL_OTHER_SQL,"@", new String[]{field});
      ds.deleteRow();
      ds.saveChanges(new String[]{sql});
    }
  }
}

