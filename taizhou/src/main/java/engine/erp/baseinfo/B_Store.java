package engine.erp.baseinfo;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.project.LookupBeanFacade;
import engine.project.SysConstant;
import engine.common.LoginBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 基础信息维护--仓库列表</p>
 * <p>Description: 基础信息维护--仓库列表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public final class B_Store extends BaseAction implements Operate//, LookUp
{
  /**
   * 提取仓库信息的SQL语句
   */
  private static final String STORE_SQL = "SELECT * FROM kc_ck";//

  /**
   * 保存仓库信息的数据集
   */
  private EngineDataSet dsStore = new EngineDataSet();

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
  private long    editrow = 0;

  /**
   * 点击返回按钮的URL
   */
  public  String retuUrl = null;

  /**
   * 得到仓库信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回仓库信息的实例
   */
  public static B_Store getInstance(HttpServletRequest request)
  {
    B_Store storeBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "storeBean";
      storeBean = (B_Store)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(storeBean == null)
      {
        storeBean = new B_Store();
        session.setAttribute(beanName, storeBean);
      }
    }
    return storeBean;
  }
  /**
   * 构造函数
   */
  private B_Store()
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
    setDataSetProperty(dsStore, STORE_SQL);
    dsStore.setSort(new SortDescriptor("", new String[]{"ckbm"}, new boolean[]{false}, null, 0));
    dsStore.setSequence(new SequenceDescriptor(new String[]{"storeid"}, new String[]{"s_kc_ck"}));
    //添加操作的触发对象
    B_Store_Add_Edit add_edit = new B_Store_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_Store_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_Store_Post());
    addObactioner(String.valueOf(DEL), new B_Store_Delete());
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
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsStore.isOpen() && dsStore.changesPending())
        dsStore.reset();
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
    if(dsStore != null){
      dsStore.close();
      dsStore = null;
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
    if(!dsStore.isOpen())
      dsStore.open();
    return dsStore;
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }

  //------------------------------------------
  //操作实现的类
  //------------------------------------------
  /**
   * 初始化操作的触发类
   */
  class B_Store_Init implements Obactioner
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
  class B_Store_Add_Edit implements Obactioner
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
        dsStore.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsStore.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }

  /**
   * 保存操作的触发类
   */
  class B_Store_Post implements Obactioner
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
      String ckbm = rowInfo.get("ckbm");
      String ckmc = rowInfo.get("ckmc");
      String ckqc = rowInfo.get("ckqc");
      String ckdz = rowInfo.get("ckdz");
      String ckdh = rowInfo.get("ckdh");
      String ckcz = rowInfo.get("ckcz");
      String deptid = rowInfo.get("deptid");
      if(ckbm.equals("")){
        data.setMessage(showJavaScript("alert('仓库编码不能为空！');"));
        return;
      }
      if(ckmc.equals("")){
        data.setMessage(showJavaScript("alert('仓库名称不能为空！');"));
        return;
      }
      if(ckqc.equals("")){
        data.setMessage(showJavaScript("alert('仓库全称不能为空！');"));
        return;
      }
      if(deptid.equals("")){
        data.setMessage(showJavaScript("alert('所属部门不能为空！');"));
        return;
      }
      /*
      if(ckdz.equals("")){
        data.setMessage(showJavaScript("alert('仓库地址不能为空！');"));
        return;
      }
      if(ckdh.equals("")){
        data.setMessage(showJavaScript("alert('仓库电话不能为空！');"));
        return;
      }
      if(ckcz.equals("")){
        data.setMessage(showJavaScript("alert('仓库传真不能为空！');"));
        return;
      }
      */
      if(!isAdd)
        ds.goToInternalRow(editrow);

      if(isAdd || !ckbm.equals(ds.getValue("ckbm")))
      {
        String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('kc_ck','ckbm','"+ckbm+"') from dual");
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('仓库编码("+ ckbm +")已经存在!');"));
          return;
        }
      }
      if(isAdd || !ckmc.equals(ds.getValue("ckmc")))
      {
        String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('kc_ck','ckmc','"+ckmc+"') from dual");
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('仓库名称("+ ckmc +")已经存在!');"));
          return;
        }
      }

      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("storeid", "-1");
      }
      ds.setValue("ckbm", ckbm);
      ds.setValue("ckmc", ckmc);
      ds.setValue("ckqc", ckqc);
      ds.setValue("ckdz", ckdz);
      ds.setValue("ckdh", ckdh);
      ds.setValue("ckcz", ckcz);
      ds.setValue("deptid", deptid);
      ds.setValue("hzr", rowInfo.get("hzr"));
      ds.setValue("bz", rowInfo.get("bz"));
      ds.post();
      ds.saveChanges();
      //刷新数据集，保持数据的同步
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_BOTH);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_BRANCH);

      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }

  /**
   * 删除操作的触发类
   */
  class B_Store_Delete implements Obactioner
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
      ds.deleteRow();
      ds.saveChanges();
      //刷新数据集，保持数据的同步
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_BOTH);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_BRANCH);
    }
  }

  //-----Implement this engine.project.LookUp method
  /**
   * 得到仓库列表的页面的<select>控件的<option></option>的字符串
   * @param mixId 初始化选中的仓库ID
   * @param lx 仓库类型
   * @return 仓库列表
  public synchronized final String getList(String storeid)
  {
    return dataSetToOption(getOneTable(), "storeid", "ckmc", storeid, null, null);
  }
  /**
   * 根据仓库ID得到仓库一行信息
   * @param bankId 初始化选中的仓库ID
   * @return 仓库名称
  public synchronized final String getLookupName(String storeid)
  {
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "storeid");
    locateRow.setValue(0, storeid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      return getOneTable().getValue("ckmc");
    else
      return "";
  }

  public void regData(String[] ids) throws Exception
  {
    return;
  }

  public RowMap getLookupRow(String id) throws Exception
  {
    throw new java.lang.UnsupportedOperationException("Method getLookupRow() not yet implemented.");
  }

  public String getList(String selectid, RowMap paramlist) throws Exception
  {
    throw new java.lang.UnsupportedOperationException("Method getList() not yet implemented.");
  }
  */
}