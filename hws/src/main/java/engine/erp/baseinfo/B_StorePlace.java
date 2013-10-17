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
import engine.project.*;
import engine.common.LoginBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
import engine.project.LookUp;
/**
 * <p>Title: 基础信息维护--库位列表</p>
 * <p>Description: 基础信息维护--库位列表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

/**
 * 2004.4.21 添加功能,库位提交或删除时需要同步仓库信息
 */
public final class B_StorePlace extends BaseAction implements Operate//, LookUp
{
  /**
   * 提取仓库信息的SQL语句
   */
  private static final String STOREPLACE_SQL = "SELECT * FROM kc_kw";//
  //同意仓库下具有相同的库位编码
  private static final String CODE_COUNT_SQL = "SELECT pck_base.fieldCodeCount('kc_kw','dm','@','storeid=@') FROM dual";
  //同意仓库下具有相同的库位名称
  private static final String NAME_COUNT_SQL = "SELECT pck_base.fieldCodeCount('kc_kw','mc','@','storeid=@') FROM dual";
  /**
   * 保存库位信息的数据集
   */
  private EngineDataSet dsStorePlace = new EngineDataSet();

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
   * 得到库位信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回库位信息的实例
   */
  public static B_StorePlace getInstance(HttpServletRequest request)
  {
    B_StorePlace storePlaceBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "storePlaceBean";
      storePlaceBean = (B_StorePlace)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(storePlaceBean == null)
      {
        storePlaceBean = new B_StorePlace();
        session.setAttribute(beanName, storePlaceBean);
      }
    }
    return storePlaceBean;
  }
  /**
   * 构造函数
   */
  private B_StorePlace()
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
    setDataSetProperty(dsStorePlace, STOREPLACE_SQL);
    dsStorePlace.setSort(new SortDescriptor("", new String[]{"storeid", "dm"}, new boolean[]{false, false}, null, 0));
    dsStorePlace.setSequence(new SequenceDescriptor(new String[]{"kwid"}, new String[]{"s_kc_kw"}));
    //添加操作的触发对象
    B_StorePlace_Add_Edit add_edit = new B_StorePlace_Add_Edit();
    addObactioner(String.valueOf(INIT), new B_StorePlace_Init());
    addObactioner(String.valueOf(ADD), add_edit);
    addObactioner(String.valueOf(EDIT), add_edit);
    addObactioner(String.valueOf(POST), new B_Storeplace_Post());
    addObactioner(String.valueOf(DEL), new B_Storeplace_Delete());
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
      if(dsStorePlace.isOpen() && dsStorePlace.changesPending())
        dsStorePlace.reset();
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
    if(dsStorePlace != null){
      dsStorePlace.close();
      dsStorePlace = null;
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
   * 初始化行信息
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
    if(!dsStorePlace.isOpen())
      dsStorePlace.open();
    return dsStorePlace;
  }

  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }

  //------------------------------------------
  //操作实现的类
  //------------------------------------------
  /**
   * 初始化操作的触发类
   */
  class B_StorePlace_Init implements Obactioner
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
  class B_StorePlace_Add_Edit implements Obactioner
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
        dsStorePlace.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsStorePlace.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }

  /**
   * 保存操作的触发类
   */
  class B_Storeplace_Post implements Obactioner
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
      String storeid = rowInfo.get("storeid");
      String dm  = rowInfo.get("dm");
      String mc  = rowInfo.get("mc");
      if(storeid.equals("")){
        data.setMessage(showJavaScript("alert('仓库ID不能为空！');"));
        return;
      }
      if(dm.equals("")){
        data.setMessage(showJavaScript("alert('库位编码不能为空！');"));
        return;
      }
      if(mc.equals("")){
        data.setMessage(showJavaScript("alert('仓库名称不能为空！');"));
        return;
      }

      if(!isAdd)
        ds.goToInternalRow(editrow);

      if(isAdd || !dm.equals(ds.getValue("dm")))
      {
        String sql = combineSQL(CODE_COUNT_SQL, "@", new String[]{dm, storeid});
        String count = dataSetProvider.getSequence(sql);
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('仓库下的库位编码("+ dm +")已经存在!');"));
          return;
        }
      }
      if(isAdd || !mc.equals(ds.getValue("mc")))
      {
        String sql = combineSQL(NAME_COUNT_SQL, "@", new String[]{mc, storeid});
        String count = dataSetProvider.getSequence(sql);
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('仓库下的库位名称("+ mc +")已经存在!');"));
          return;
        }
      }

      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("kwid", "-1");
      }
      ds.setValue("storeid", storeid);
      ds.setValue("dm", dm);
      ds.setValue("mc", mc);
      ds.post();
      ds.saveChanges();
      //刷新数据集，保持数据的同步
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_AREA);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE);

      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }

  /**
   * 删除操作的触发类
   */
  class B_Storeplace_Delete implements Obactioner
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
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE_AREA);
      //同步仓库信息
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE);
    }
  }

  /**
   * 得到库位列表的页面的<select>控件的<option></option>的字符串
   * @param mixId 初始化选中的仓库ID
   * @param lx 库位类型
   * @return 库位列表

  public synchronized final String getList(String kwid)
  {
    return dataSetToOption(getOneTable(), "kwid", "mc", kwid, null, null);
  }
  /**
   * 根据库位ID得到仓库一行信息
   * @param kwid 初始化选中的库位ID
   * @return 库位名称

  public synchronized final String getLookupName(String kwid)
  {
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "kwid");
    locateRow.setValue(0, kwid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      return getOneTable().getValue("mc");
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
  }*/
}
