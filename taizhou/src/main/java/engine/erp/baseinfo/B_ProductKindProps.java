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

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
import engine.project.LookUp;
/**
 * <p>Title: 基础信息维护--大类属性</p>
 * <p>Description: 基础信息维护--大类属性</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public final class B_ProductKindProps extends BaseAction implements Operate//, LookUp
{
  //负责大类属性
  public static final String COPY_FIRST_CODE = "10000";
  /**
   * 提取仓库信息的SQL语句
   */
  private static final String KIND_PROPERTY_SQL = "SELECT * FROM kc_dmlbsx WHERE wzlbid='@'";//
  //提取大类的名称
  private static final String KIND_FIRST_PROPERTY_SQL = "SELECT sxmc, sxlx FROM kc_dmlbsx WHERE wzlbid='@'";//
  //提取产品的编码规则
  private static final String PROD_CODE_SQL = "SELECT segformat FROM jc_coderule_cont WHERE coderule='product'";
  //
  private static final String KIND_CODE_SQL = "SELECT b.bm FROM kc_dmlb b WHERE b.wzlbid='@'";
  //
  private static final String KIND_FIRST_SQL = "SELECT b.wzlbid FROM kc_dmlb b WHERE b.bm='@'";
  /*private static final String NAME_COUNT_SQL
      = "SELECT COUNT(*) FROM kc_dmlb a, kc_dmlbsx b WHERE b.sxmc='@' AND a.wzlbid=b.wzlbid AND a.isdelete=0";
  */
  /**
   * 保存大类属性信息的数据集
   */
  private EngineDataSet dsKindProps = new EngineDataSet();

  /**
   * 保存用户输入的信息
   */
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  //private RowMap rowInfo = new RowMap();
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
   * 保存物资类别id
   */
  private String wzlbid = "";

  /**
   * 物资大类id
   */
  private String firstKindId = null;

  /**
   * 得到库位信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回库位信息的实例
   */
  public static B_ProductKindProps getInstance(HttpServletRequest request)
  {
    B_ProductKindProps productKindPropsBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "productKindPropsBean";
      productKindPropsBean = (B_ProductKindProps)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(productKindPropsBean == null)
      {
        productKindPropsBean = new B_ProductKindProps();
        session.setAttribute(beanName, productKindPropsBean);
      }
    }
    return productKindPropsBean;
  }
  /**
   * 构造函数
   */
  private B_ProductKindProps()
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
    setDataSetProperty(dsKindProps, null);
    //dsKindProps.setSort(new SortDescriptor("", new String[]{"dm"}, new boolean[]{ false}, null, 0));
    dsKindProps.setSequence(new SequenceDescriptor(new String[]{"dlsxID"}, new String[]{"s_kc_dmlbsx"}));
    //添加操作的触发对象
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(ADD), new Add());
    addObactioner(String.valueOf(POST), new Post());
    addObactioner(String.valueOf(DEL), new Delete());
    addObactioner(COPY_FIRST_CODE, new CopyFirstKind());
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
      if(dsKindProps.isOpen() && dsKindProps.changesPending())
        dsKindProps.reset();
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
    if(dsKindProps != null){
      dsKindProps.close();
      dsKindProps = null;
    }
    log = null;
    d_RowInfos = null;
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
  private final void initRowInfo(boolean isInit) throws java.lang.Exception
  {
    EngineDataSet dsDetail = getOneTable();
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

  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsKindProps.isOpen())
      dsKindProps.open();
    return dsKindProps;
  }

  /*得到一列的信息*/
  public final RowMap[] getRowinfo(){
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }

  /**
   * 是否是大类
   * @return 返回是否是大类
   */
  public final boolean isFirstKind()
  {

    return firstKindId==null ? false : firstKindId.equals(wzlbid);
  }
  //------------------------------------------
  //操作实现的类
  //------------------------------------------
  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
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
      retuUrl = data.getParameter("src", "");
      retuUrl = retuUrl.trim();
      String temp = data.getParameter("wzlbid", "");
      if(temp.length() == 0)
      {
        data.setMessage(showJavaScript("alert('非法的类别属性！');"+ (retuUrl.length() >= 0 ? "location.href='"+retuUrl+"'" : "")));
        return;
      }
      wzlbid = temp;
      String kindcode = dataSetProvider.getSequence(combineSQL(KIND_CODE_SQL, "@", new String[]{wzlbid}));
      if(kindcode == null || kindcode.length() == 0)
      {
        data.setMessage(showJavaScript("alert('非法的类别！');"+ (retuUrl.length() >= 0 ? "location.href='"+retuUrl+"'" : "")));
        return;
      }
      //大类编码
      String prodcode = dataSetProvider.getSequence(PROD_CODE_SQL);
      if(prodcode == null)
      {
        data.setMessage(showJavaScript("alert('请先设置产品编码规则！'); location.href='"+retuUrl+"';"));
        return;
      }
      String[] afters = parseString(parseString(prodcode, ",")[0], "$");
      prodcode = afters.length > 1 ? afters[1] : afters[0];
      String firstkindcode = kindcode.substring(0, Integer.parseInt(prodcode));
      //得到大类的id
      firstKindId = dataSetProvider.getSequence(combineSQL(KIND_FIRST_SQL, "@", new String[]{firstkindcode}));

      dsKindProps.setQueryString(combineSQL(KIND_PROPERTY_SQL, "@", new String[]{wzlbid}));
      if(dsKindProps.isOpen())
        dsKindProps.refresh();
      else
        dsKindProps.openDataSet();

      initRowInfo(true);
    }
  }

  /**
   * 添加或修改操作的触发类
   */
  class Add implements Obactioner
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
      //校验数据
      putInfo(data.getRequest());
      //isAdd = action.equals(String.valueOf(ADD));
      dsKindProps.insertRow(false);
      dsKindProps.setValue("dlsxid", "-1");
      dsKindProps.setValue("wzlbid", wzlbid);
      dsKindProps.setValue("sxlx", "varchar");
      dsKindProps.post();
      RowMap row = new RowMap(dsKindProps);
      d_RowInfos.add(row);
    }
  }

  /**
   * 保存操作的触发类
   */
  class Post implements Obactioner
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
      if(wzlbid.length() == 0)
      {
        data.setMessage(showJavaScript("alert('非法的大类属性！');"+ (retuUrl.length() >= 0 ? "location.href='"+retuUrl+"'" : "")));
        return;
      }

      EngineDataSet ds = getOneTable();
      //校验数据
      putInfo(data.getRequest());
      String temp = null;
      if((temp = checkInfo()) != null)
      {
        data.setMessage(temp);
        return;
      }

      RowMap detailrow = null;
      ds.first();
      for(int i=0; i<ds.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        ds.setValue("sxmc", detailrow.get("sxmc"));
        ds.setValue("sxlx", detailrow.get("sxlx"));
        ds.post();
        ds.next();
      }
      ds.saveChanges();
      if(retuUrl.length() > 0)
        data.setMessage(showJavaScript("location.href='"+retuUrl+"';"));
    }

    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      ArrayList check = new ArrayList(d_RowInfos.size());
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String sxlx = detailrow.get("sxlx");
        if(sxlx.length() == 0)
          return showJavaScript("alert('属性类型不能为空！');");
        String sxmc = detailrow.get("sxmc");
        if(sxmc.length() == 0)
          return showJavaScript("alert('属性名称不能为空！');");
        if(check.contains(sxmc))
          return showJavaScript("alert('属性名称不能相同！');");
        else if(sxmc.indexOf("(") > -1 || sxmc.indexOf(")") > -1 ||
                sxmc.indexOf("%") > -1 || sxmc.toLowerCase().indexOf("like") > -1)
          return showJavaScript("alert('第"+(i+1)+"属性名称含有非法字符！');");
        else
          check.add(sxmc);
      }
      return null;
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
      putInfo(data.getRequest());
      EngineDataSet ds = getOneTable();
      int row = Integer.parseInt(data.getParameter("rownum"));
      ds.goToRow(row);
      ds.deleteRow();
      d_RowInfos.remove(row);
    }
  }

  /**
   * 复制大类属性
   */
  class CopyFirstKind implements Obactioner
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
      if(isFirstKind())
        return;

      EngineDataSet ds = new EngineDataSet();
      ds.setProvider(dataSetProvider);
      ds.setQueryString(combineSQL(KIND_FIRST_PROPERTY_SQL, "@", new String[]{firstKindId}));
      ds.openDataSet();
      ds.first();
      EngineRow locateRow = new EngineRow(dsKindProps, "sxmc");
      for(int i=0; i<ds.getRowCount(); i++)
      {
        String sxmc = ds.getValue("sxmc");
        locateRow.setValue(0, sxmc);
        if(!dsKindProps.locate(locateRow, Locate.FIRST))
        {
          dsKindProps.insertRow(false);
          dsKindProps.setValue("dlsxid", "-1");
          dsKindProps.setValue("wzlbid", wzlbid);
          dsKindProps.setValue("sxmc", sxmc);
          dsKindProps.setValue("sxlx", ds.getValue("sxlx"));
          dsKindProps.post();
          RowMap row = new RowMap(dsKindProps);
          d_RowInfos.add(row);
        }
        ds.next();
      }
    }
  }

  /**
   * 从表用户输入操作
   * @param req 网页的请求对象
   */
  private final void putInfo(HttpServletRequest req)
  {
    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("sxmc", RunData.getParameter(req, "sxmc_"+i, ""));//属性名称
      detailRow.put("sxlx", RunData.getParameter(req, "sxlx_"+i, ""));//属性类型
    }
  }

  private LookUp firstKindBean = null;
  /**
   * 得到大类名称
   * @param req web请求
   * @return 返回大类名称
   * @throws Exception 异常
   */
  public final String getFirstKindName(HttpServletRequest req) throws Exception
  {
    if(firstKindBean == null)
    {
      firstKindBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PRODUCT_FIRST_KIND);
      firstKindBean.regData((String[])null);
    }
    return firstKindBean.getLookupName(wzlbid);
  }
}

