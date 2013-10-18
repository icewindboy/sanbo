package engine.erp.produce;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import engine.util.StringUtils;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.project.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 工序名称信息维护</p>
 * <p>Description: 工序名称设置</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_TechnicsName extends BaseAction implements Operate
{
  /**
   * 提取工序信息的SQL语句
   */
  private static final String TECHNICSNAME_SQL = "SELECT * FROM sc_gymc WHERE gxmcid=? ORDER BY gybh";//

  private static final String TECHNICS_COUNT_SQL
      = "SELECT COUNT(*) FROM sc_gymc c WHERE gymc='?' AND c.gxfdid ='?'";

  /**
   * 保存工序名称信息的数据集
   */
  private EngineDataSet dsTechnicsName = new EngineDataSet();

  /**
   * 用于定位数据集
   */
  private EngineRow locateRow = null;

  /**
   * 保存用户输入的信息
   */
  private RowMap rowInfo = new RowMap();

  /**
   * 是否在添加状态
   */
  public  boolean isAdd = true;

  /**
   * 保存修改操作的行记录指针
   */
  private long  editrow = 0;

  /**
   * 点击返回按钮的URL
   */
  public  String retuUrl = null;

  /**
   * 得到工序名称信息的实例
   * @param request jsp请求
   * @return 工序名称信息的实例
   */
  public static B_TechnicsName getInstance(HttpServletRequest request)
  {
   B_TechnicsName b_TechnicsNameBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_TechnicsNameBean";
      b_TechnicsNameBean = (B_TechnicsName)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_TechnicsNameBean == null)
      {
        b_TechnicsNameBean = new B_TechnicsName();
        session.setAttribute(beanName, b_TechnicsNameBean);
      }
    }
    return b_TechnicsNameBean;
  }
  /**
   * 构造函数
   */
  private B_TechnicsName()
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
    setDataSetProperty(dsTechnicsName, "SELECT * FROM sc_gymc ORDER BY gybh");
    dsTechnicsName.setSort(new SortDescriptor("", new String[]{"gybh"}, new boolean[]{false}, null, 0));
    dsTechnicsName.setSequence(new SequenceDescriptor(new String[]{"gymcID"}, new String[]{"s_sc_gymc"}));
    //添加操作的触发对象
    B_TechnicsName_Add_Edit add_edit = new B_TechnicsName_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_TechnicsName_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_TechnicsName_Post());
    addObactioner(String.valueOf(DEL), new B_TechnicsName_Delete());
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
      if(dsTechnicsName.isOpen() && dsTechnicsName.changesPending())
        dsTechnicsName.reset();
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
    if(dsTechnicsName != null){
      dsTechnicsName.close();
      dsTechnicsName = null;
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
    else
    {
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('sc_gymc','gybh','','',3) from dual");
      rowInfo.put("gybh", code);
    }
  }
  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsTechnicsName.isOpen())
      dsTechnicsName.open();
    return dsTechnicsName;
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 初始化操作的触发类
   */
  class B_TechnicsName_Init implements Obactioner
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
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
    }
  }

  /**
   * 添加或修改操作的触发类
   */
  class B_TechnicsName_Add_Edit implements Obactioner
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
        dsTechnicsName.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsTechnicsName.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }

  /**
   * 保存操作的触发类
   */
  class B_TechnicsName_Post implements Obactioner
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
      String gybh = rowInfo.get("gybh");//工序编号
      String gymc = rowInfo.get("gymc");//工序名称
      String gyms = rowInfo.get("gyms");//工序描述
      String gxfdid = rowInfo.get("gxfdid");//所属工段
      if(gymc.length() == 0){
        data.setMessage(showJavaScript("alert('工序名称不能为空！');"));
        return;
      }
      if(gxfdid.length() == 0){
        data.setMessage(showJavaScript("alert('所属工段不能为空！');"));
        return;
      }
      if(!isAdd)
        ds.goToInternalRow(editrow);

      if(isAdd || !gymc.equals(ds.getValue("gymc")))
      {
        String count = StringUtils.combine(TECHNICS_COUNT_SQL, "?", new String[]{gymc, gxfdid});
        count = dataSetProvider.getSequence(count);
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('工序名称("+ gymc +")已经存在!');"));
          return;
        }
      }
      if(isAdd || !gybh.equals(ds.getValue("gybh")))
      {
        String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('sc_gymc','gybh','"+gybh+"') from dual");
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('工序编号("+ gybh +")已经存在!');"));
          return;
        }
      }
      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("gymcid", "-1");
      }
      ds.setValue("gybh", gybh);
      ds.setValue("gymc", gymc);
      ds.setValue("gxfdid", gxfdid);
      ds.setValue("gyms", gyms);
      ds.post();
      ds.saveChanges();
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_TECHNICS_NAME);
    }
  }

  /**
   * 删除操作的触发类
   */
  class B_TechnicsName_Delete implements Obactioner
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
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      String gymcid = ds.getValue("gymcid");
      String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM sc_gylxmx WHERE gymcid="+gymcid);
      if(!count.equals("0")){
        data.setMessage(showJavaScript("alert('该工作中心已被引用不能删除！')"));
        return;
      }
      else{
        ds.deleteRow();
        ds.saveChanges();
        LookupBeanFacade.refreshLookup(SysConstant.BEAN_TECHNICS_NAME);
      }
    }
  }
}

