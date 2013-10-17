package engine.erp.store;

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
import engine.project.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;

/**
 * <p>Title: 仓库基础信息维护--用途设置信息</p>
 * <p>Description: 仓库基础信息维护--用途设置信息</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_StoreClose
    extends BaseAction
    implements Operate {
  /**
   * 提取用途设置信息的SQL语句
   */
  private static final String PRODUCEUSE_SQL =
      " select t.* from jc_checkout t ORDER BY t.checkid DESC"; //
  private static final String CHECKOUT_SQL =
      "{CALL PCK_STORE.kc_year_checkout(?,@,@)}";

  /**
   * 保存用途设置信息的数据集
   */
  private EngineDataSet dsProduceUse = new EngineDataSet();

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
  public boolean isAdd = true;

  /**
   * 保存修改操作的行记录指针
   */
  private long editrow = 0;

  /**
   * 点击返回按钮的URL
   */
  public String retuUrl = null;
  private String fgsid = null;
  public String jsrq = null;
  /**
   * 得到用途设置信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回用途设置信息的实例
   */
  public static B_StoreClose getInstance(HttpServletRequest request) {
    B_StoreClose StoreCloseBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session) {
      String beanName = "StoreCloseBean";
      StoreCloseBean = (B_StoreClose) session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if (StoreCloseBean == null) {
        LoginBean loginBean = LoginBean.getInstance(request);
        StoreCloseBean = new B_StoreClose();
        StoreCloseBean.fgsid = loginBean.getFirstDeptID();
        session.setAttribute(beanName, StoreCloseBean);
      }
    }
    return StoreCloseBean;
  }

  /**
   * 构造函数
   */
  private B_StoreClose() {
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
  protected void jbInit() throws Exception {
    //02.24 20:54 新增 给下面句sql加上order by ytbh语句. yjg
    setDataSetProperty(dsProduceUse, PRODUCEUSE_SQL);

    B_StoreClose_Add_Edit add_edit = new B_StoreClose_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_StoreClose_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_StoreClose_Post());
    addObactioner(String.valueOf(DEL), new B_StoreClose_Delete());
  }

  //----Implementation of the BaseAction abstract class
  /**
   * JSP调用的函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  public String doService(HttpServletRequest request,
                          HttpServletResponse response) {
    try {
      String operate = request.getParameter(OPERATE_KEY);
      if (operate != null && operate.trim().length() > 0) {
        RunData data = notifyObactioners(operate, request, response, null);
        if (data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch (Exception ex) {
      if (dsProduceUse.isOpen() && dsProduceUse.changesPending())
        dsProduceUse.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  //----Implementation of the BaseAction abstract class
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event) {
    if (dsProduceUse != null) {
      dsProduceUse.close();
      dsProduceUse = null;
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
  protected Class childClassName() {
    return getClass();
  }

  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.
      lang.Exception {
    //是否时添加操作
    if (isInit && rowInfo.size() > 0)
      rowInfo.clear();
    if (!isAdd)
      rowInfo.put(getOneTable());
  }

  /*得到表对象*/
  public final EngineDataSet getOneTable() {
    if (!dsProduceUse.isOpen())
      dsProduceUse.open();
    return dsProduceUse;
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo() {
    return rowInfo;
  }

  /**
   * 初始化操作的触发类
   */
  class B_StoreClose_Init
      implements Obactioner {
    //----Implementation of the Obactioner interface
    /**
     * 触发初始化操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl != null ? retuUrl.trim() : retuUrl;
    }
  }

  /**
   * 添加或修改操作的触发类
   */
  class B_StoreClose_Add_Edit
      implements Obactioner {
    //----Implementation of the Obactioner interface
    /**
     * 添加或修改的触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      isAdd = action.equals(String.valueOf(ADD));
      if (!isAdd) {
        dsProduceUse.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsProduceUse.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }

  /**
   * 保存操作的触发类
   */
  class B_StoreClose_Post
      implements Obactioner {
    //----Implementation of the Obactioner interface
    /**
     * 触发保存操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      jsrq = data.getParameter("jsrq");
      if (jsrq.equals(""))
        return;
      EngineDataSet newbne = new EngineDataSet();
      String sql = combineSQL(CHECKOUT_SQL, "@", new String[] {"'"+jsrq+"'", fgsid});
      setDataSetProperty(newbne, sql);
      newbne.open();
      newbne.first();
      dsProduceUse.readyRefresh();
      String resultCode = newbne.getValue("resultCode");
      if ("-20014".equals(resultCode)) {
        data.setMessage(showJavaScript("alert('还有未记帐单据！')"));
        return;
      }
      else if ("1".equals(resultCode)) {
        data.setMessage(showJavaScript("alert('有未记帐的单据！')"));
        return;
      }


    }
  }

  /**
   * 删除操作的触发类
   */
  class B_StoreClose_Delete
      implements Obactioner {
    //----Implementation of the Obactioner interface
    /**
     * 触发删除操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      EngineDataSet ds = getOneTable();
      ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
      ds.deleteRow();
      ds.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCE_USE);
    }
  }
}
