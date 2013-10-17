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
import engine.html.*;
import engine.common.*;

import java.util.*;

import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 物资规格属性编辑类</p>
 * <p>Description: 物资规格属性编辑类<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class ProductStore extends BaseAction implements Operate
{
  private static final String PROD_PROPERTY_SQL = "SELECT * FROM kc_cfck WHERE isdelete='0' AND cpid=";

  private EngineDataSet dsProductStore  = new EngineDataSet();//规格属性表
  // private EngineDataSet dsSpecProperty  = new EngineDataSet();//

  public String MULTY_ADD ="20001";
  private RowMap  rowInfo  = new RowMap(); //主表添加行或修改行的引用
  private RowMap  m_rowInfo  = new RowMap(); //父类的规格属性和保存在规格属性表中的属性并集
  public ArrayList storelist = null;//保存规格属性用特定分割符分割后的数组
  /**
   * 是否在添加状态
   */
  public  boolean isAdd = true;
  //public  int num = 0; //规格属性用特定分割符分割后的数组的长度
  private B_CopyProperty B_CopyPropertyBean = null;
  /**
   * 保存修改操作的行记录指针
   */
  private long    editrow = 0;

  public  String retuUrl = null;
  private String cpid = null;   //参数产品ID
  private String wzlbid = null;   //参数产品大类ID
  /**
   * 物资规格属性的实例
   * @param request jsp请求
   * @return 返回物资规格属性的实例
   */
  public static ProductStore getInstance(HttpServletRequest request)
  {
    ProductStore ProductStoreBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "ProductStoreBean";
      ProductStoreBean = (ProductStore)session.getAttribute(beanName);
      if(ProductStoreBean == null)
      {
        ProductStoreBean = new ProductStore();
        session.setAttribute(beanName, ProductStoreBean);
      }
    }
    return ProductStoreBean;
  }

  /**
   * 构造函数
   */
  private ProductStore()
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
  private final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsProductStore, null);
    //setDataSetProperty(dsSpecProperty, null);

    dsProductStore.setSequence(new SequenceDescriptor(new String[]{"cfckid"}, new String[]{"s_kc_cfck"}));

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(POST), new Post());
    addObactioner(String.valueOf(EDIT), new Property_Add_Edit());
    addObactioner(String.valueOf(ADD), new Property_Add_Edit());
    addObactioner(String.valueOf(DEL), new Delete());
    addObactioner(String.valueOf(MULTY_ADD), new Multy_Add());

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
        if(data == null)
          return showMessage("无效操作", false);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsProductStore.isOpen() && dsProductStore.changesPending())
        dsProductStore.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsProductStore != null){
      dsProductStore.close();
      dsProductStore = null;
    }

    log = null;
    rowInfo = null;
    m_rowInfo = null;
  }

  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }
  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    return dsProductStore;
  }

  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[storelist.size()];
    storelist.toArray(rows);
    return rows;
  }
  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_rowInfo; }

  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      retuUrl = data.getParameter("src","");
      retuUrl = retuUrl.trim();
      cpid = request.getParameter("cpid");
      //wzlbid = request.getParameter("wzlbid");
      //提取产品属性数据
      String sql = PROD_PROPERTY_SQL+cpid;
      dsProductStore.setQueryString(sql);
      if(dsProductStore.isOpen()){
        dsProductStore.readyRefresh();
        dsProductStore.refresh();
      }
      else
        dsProductStore.openDataSet();
      initRowInfo(false,true);
    }
  }
  class Multy_Add implements Obactioner
  {
    /**
     * 添加或修改的触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      String sxzs = request.getParameter("sxzs");
      if(sxzs.length() == 0)
        return;

      String[] dmsxids = parseString(sxzs,",");//解析出合同货物ID数组
      for(int i=0; i < dmsxids.length; i++)
      {
        if(dmsxids[i].equals("-1"))
          continue;

        String dmsxid = dmsxids[i];
        B_CopyPropertyBean =get_OrderProductBean(request);     //提货
        RowMap saleRow = B_CopyPropertyBean.getLookupRow(dmsxid);

        dsProductStore.insertRow(false);
        dsProductStore.setValue("dmsxid", "-1");
        String classmc=saleRow.get("classmc");
        String metalname =saleRow.get("metalname");
        dsProductStore.setValue("cpid", cpid);
        dsProductStore.setValue("sxz", classmc+metalname);
        dsProductStore.setValue("classmc", classmc);
        dsProductStore.setValue("metalname", metalname);
        dsProductStore.setValue("isdelete", "0");
        dsProductStore.post();
      }
      dsProductStore.saveChanges();

    }
  }
  /**
   * 添加或修改操作的触发类
   */
  class Property_Add_Edit implements Obactioner
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
      HttpServletRequest request = data.getRequest();
      putDetailInfo(request);

      isAdd = action.equals(String.valueOf(ADD));
      if(isAdd)
      {
        dsProductStore.insertRow(false);
        dsProductStore.setValue("cpid",cpid);
        RowMap detail = new RowMap(dsProductStore);
        storelist.add(detail);
      }

    }
  }
  private final void putDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = new RowMap(); //getMasterRowinfo();
    rowInfo.put(request);
    int rownum = storelist.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)storelist.get(i);
      detailRow.put("storeid", rowInfo.get("storeid_"+i));//提单IDstorelist
      storelist.set(i,detailRow);
    }
  }
  private final void initRowInfo(boolean isAdd, boolean isInit) //throws java.lang.Exception
  {
    if(storelist == null)
      storelist = new ArrayList(dsProductStore.getRowCount());
    else if(isInit)
      storelist.clear();
    RowMap detail=null;
    dsProductStore.first();
    for(int i=0;i<dsProductStore.getRowCount();i++)
    {
      detail = new RowMap(dsProductStore);
      storelist.add(detail);
      dsProductStore.next();
    }
  }
  /**
   * 删除操作的触发类
   */
  class Delete implements Obactioner
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
      ds.setValue("isdelete", "1");
      ds.post();
      ds.saveChanges();
      ds.deleteRow();
      try{
        ds.saveChanges();
      }
      catch(Exception ex){
        log.warn("delete",ex);
        if(dsProductStore.isOpen() && dsProductStore.changesPending())
          dsProductStore.reset();
        //data.setMessage(showJavaScript("alert('该物资规格属性已被引用')"));
        return;
      }
    }
  }
  /**
   * 主表保存操作的触发类
   */
  class Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      String       temp = checkDetailInfo();
      if(temp != null)
      {

        data.setMessage(temp);
        return;
      }

      RowMap detailrow = null;
      EngineDataSet ds = dsProductStore;
      ds.first();
      for(int i=0; i<ds.getRowCount(); i++)
      {
        detailrow = (RowMap)storelist.get(i);
        ds.setValue("storeid",detailrow.get("storeid"));
        ds.post();
        ds.next();
      }
      ds.saveChanges();

    }
    private String checkDetailInfo()
    {
      //HashSet wzdijds=new HashSet(d_RowInfos.size());
      HashSet htmp = new HashSet();
      htmp.clear();
      String temp = null;
      RowMap detailrow = null;
      if(storelist.size()<1)
      {
        return showJavaScript("alert('为空,不能保存!');");
      }
      for(int i=0; i<storelist.size(); i++)
      {
        detailrow = (RowMap)storelist.get(i);
        String storeid=detailrow.get("storeid");
        if(!htmp.add(storeid))
         return showJavaScript("alert('所选仓库重复!');");
      }
      return null;
    }
  }
  public B_CopyProperty get_OrderProductBean(HttpServletRequest req)
  {
    if(B_CopyPropertyBean == null)
      B_CopyPropertyBean = B_CopyProperty.getInstance(req);
    return B_CopyPropertyBean;
  }
}