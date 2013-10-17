package engine.erp.jit;

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
import engine.erp.baseinfo.BasePublicClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 生产计划页面里生成实际BOM</p>
 * <p>Description: 生产计划页面里生成实际BOM<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 杨建国
 * @version 1.0
 */

public final class PlanBuildFactBom extends BaseAction implements Operate
{
  public static final int DETAIL_REPLACE_PRODUCT    = 1006;//替换可选件
  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_sjbom WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM sc_sjbom WHERE scjhmxid='?' ORDER BY cc,cpid";
  private static final String PRODUCEPLAN_DETAIL_SQL    = "SELECT sfsc,scjhid FROM sc_jhmx WHERE scjhmxid= ";//通过生产计划明细ID得到是否生成实际BOM
  private static final String UPDATE_SQL  = "UPDATE sc_jhmx SET sfsc='1' WHERE scjhmxid=";//生成实际BOM后UPDATE生产计划明细
  private static final String BUILD_FACTBOM_DATA = "{CALL pck_produce.buildFactBOMdata(@,'@',@,'@',@,'@',@)}";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表

  private PlanBomReplacer planBomReplaceBean = null;//制定实际BOM表替换可选件的BEAN

  private LookUp productBean = null; //产品信息的bean的引用, 用于提取产品信息
  private LookUp propertyBean = null; //产品信息的bean的引用, 用于提取产品信息

  private int    masterRow = -1;         //保存生产计划主表操作的行记录指针
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  public  String retuUrl = null;

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String wlxqjhid = null;
  private String scjhmxid = null;
  private String hthwid = null;
  private String cpid = null;
  private String xql = null;
  private String ksrq = null;
  private String dmsxid = null;
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  /**
   * 实际BOM列表的实例
   * @param request jsp请求
   * @return 返回实际BOM列表的实例
   */
  public static PlanBuildFactBom getInstance(HttpServletRequest request)
  {
    PlanBuildFactBom planBuildFactBomBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "planBuildFactBomBean";
      planBuildFactBomBean = (PlanBuildFactBom)session.getAttribute(beanName);
      if(planBuildFactBomBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        planBuildFactBomBean = new PlanBuildFactBom();
        planBuildFactBomBean.qtyFormat = loginBean.getQtyFormat();

        planBuildFactBomBean.loginId = loginBean.getUserID();
        planBuildFactBomBean.loginName = loginBean.getUserName();
        planBuildFactBomBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        //设置格式化的字段
        planBuildFactBomBean.dsMasterTable.setColumnFormat("xql", planBuildFactBomBean.qtyFormat);
        planBuildFactBomBean.dsMasterTable.setColumnFormat("scxql", planBuildFactBomBean.qtyFormat);
        session.setAttribute(beanName, planBuildFactBomBean);
      }
    }
    return planBuildFactBomBean;
  }

  /**
   * 构造函数
   */
  private PlanBuildFactBom()
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
    setDataSetProperty(dsMasterTable, null);

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"sjbomid"}, new String[]{"s_sc_sjbom"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"cc", "cpid"}, new boolean[]{false, false}, null, 0));

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(POST), new Master_Post());
    addObactioner(String.valueOf(DETAIL_REPLACE_PRODUCT), new Detail_Replace_Product());
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
        if(data == null)
          return showMessage("无效操作", false);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsMasterTable.isOpen() && dsMasterTable.changesPending())
        dsMasterTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsMasterTable != null){
      dsMasterTable.close();
      dsMasterTable = null;
    }
    log = null;
    d_RowInfos = null;
  }

  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }

  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isInit) throws java.lang.Exception
  {
      EngineDataSet ds = dsMasterTable;
      if(d_RowInfos == null)
        d_RowInfos = new ArrayList(ds.getRowCount());
      else if(isInit)
        d_RowInfos.clear();

      ds.first();
      for(int i=0; i<ds.getRowCount(); i++)
      {
        RowMap row = new RowMap(ds);
        d_RowInfos.add(row);
        ds.next();
      }
  }

  /**
   * 从表保存操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putDetailInfo(HttpServletRequest request)
  {
    RowMap rowinfo = new RowMap();
    rowinfo.put(request);
    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("dmsxid", rowinfo.get("dmsxid_"+i));//物资规格属性
      detailRow.put("sl", rowinfo.get("sl_"+i));//子件数量
      detailRow.put("xql", rowinfo.get("xql_"+i));//需求量
    }
  }

  /*得到表对象*/
  public final EngineDataSet getDetailTable(){
    return dsMasterTable;
  }

  /*得到表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }

  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    EngineDataSet dsPlanDetail = null;//生产计划从表数据集
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {

      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      //得到传入参数的值
      scjhmxid = request.getParameter("scjhmxid");
      hthwid = request.getParameter("hthwid");
      cpid = request.getParameter("cpid");
      xql = request.getParameter("xql");
      ksrq = request.getParameter("ksrq");
      dmsxid = request.getParameter("dmsxid");
      if(dsPlanDetail == null)
      {
        dsPlanDetail = new EngineDataSet();
        setDataSetProperty(dsPlanDetail, null);
      }
      dsPlanDetail.setQueryString(PRODUCEPLAN_DETAIL_SQL+scjhmxid);
      if(!dsPlanDetail.isOpen())
        dsPlanDetail.openDataSet();
      else
        dsPlanDetail.refresh();
      String sfsc = dsPlanDetail.getValue("sfsc");
      String scjhid = dsPlanDetail.getValue("scjhid");
      String count = dataSetProvider.getSequence("select count(*) from sc_jhmx where scjhid ="+scjhid);
      if(!sfsc.equals("1")){
        String sql = combineSQL(BUILD_FACTBOM_DATA, "@", new String[]{scjhmxid,hthwid,cpid,dmsxid,xql,ksrq,count});
        dsMasterTable.updateQuery(new String[]{sql});
        dsPlanDetail.updateQuery(new String[]{UPDATE_SQL + scjhmxid});
        String SQL = combineSQL(MASTER_SQL, "?", new String[]{scjhmxid});
        dsMasterTable.setQueryString(SQL);
        if(dsMasterTable.isOpen())
          dsMasterTable.refresh();
        else
          dsMasterTable.open();
        dsMasterTable.first();
        String cpid=null,xql=null, dmsxid=null, sxz=null, width=null,scdwgs=null, scsl=null, scxql=null;
        double d_xql=0, d_width=0, d_scdwgs=0, d_scsl=0;
        RowMap prodRow =null;
        dsMasterTable.first();
        for(int i=0; i< dsMasterTable.getRowCount(); i++)
        {
          scxql = dsMasterTable.getValue("scxql");//存储过程返回的生产需求量
          //如果返回不为空就不用继续计算了
          if(!scxql.equals("")){
            dsMasterTable.next();
            continue;
          }

          cpid = dsMasterTable.getValue("cpid");
          xql = dsMasterTable.getValue("xql");
          dmsxid = dsMasterTable.getValue("dmsxid");
          prodRow = getPropertyBean(data.getRequest()).getLookupRow(cpid);
          scdwgs = prodRow.get("scdwgs");
          sxz = getPropertyBean(data.getRequest()).getLookupName(dmsxid);
          width = BasePublicClass.parseEspecialString(sxz,SYS_PRODUCT_SPEC_PROP, "()");
          d_xql = xql.length()>0 ? Double.parseDouble(xql) : 0;
          d_width = width.equals("0") ? 1 : Double.parseDouble(width);
          d_scdwgs = scdwgs.length()>0 ? Double.parseDouble(scdwgs) : 1;
          if(d_width==0)
            d_scsl = d_xql;
          else
            d_scsl=d_xql*d_scdwgs/d_width;
          scsl = formatNumber(String.valueOf(d_scsl), qtyFormat);
          dsMasterTable.setValue("scxql", scsl);
          dsMasterTable.post();
          dsMasterTable.next();
        }
        dsMasterTable.saveChanges();
      }
      else{
        String SQL = combineSQL(MASTER_SQL, "?", new String[]{scjhmxid});
        dsMasterTable.setQueryString(SQL);
        if(dsMasterTable.isOpen())
          dsMasterTable.refresh();
        else
          dsMasterTable.open();
      }
      initRowInfo(true);
    }
  }
  /**
   *  可选件替换产品操作
   */
  class Detail_Replace_Product implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      int row = Integer.parseInt(req.getParameter("rownum"));
      dsMasterTable.goToRow(row);
      RowMap detail = (RowMap)d_RowInfos.get(row);
      String kc__cpid = detail.get("cpid");
      double zjsl = detail.get("sl").length()>0 ? Double.parseDouble(detail.get("sl")) : 0;//子件数量
      double xql = detail.get("xql").length()>0 ? Double.parseDouble(detail.get("xql")) : 0;//需求量
      double scxql = detail.get("scxql").length()>0 ? Double.parseDouble(detail.get("scxql")) : 0; //生产需求量
      String singleProduct = req.getParameter("singleProduct_"+row);
      if(singleProduct.equals(""))
        return;
      EngineRow locatRow = new EngineRow(dsMasterTable, "cpid");
      RowMap repalceProductRow = getReplaceProductBean(req).getLookupRow(kc__cpid, singleProduct);
      double thsl = repalceProductRow.get("sl").length()>0 ? Double.parseDouble(repalceProductRow.get("sl")) : 0;//替换件数量
      double thzjsl = zjsl*thsl;//替换件数量
      double thxql = xql*thsl;//替换件需求数量
      double thscxql = scxql*thsl;//替换生产需求量
      locatRow.setValue(0, singleProduct);
      if(dsMasterTable.locate(locatRow, Locate.FIRST))
      {
        d_RowInfos.remove(row);
        String dwsl = dsMasterTable.getValue("sl");
        String dwxqsl = dsMasterTable.getValue("xql");
        double d_dwsl = dwsl.length()>0 ? Double.parseDouble(dwsl) : 0;//定位到当前行的子件数量
        double d_dwxqsl = dwxqsl.length()>0 ? Double.parseDouble(dwxqsl) : 0;//定位到当前行的数量
        dsMasterTable.setValue("xql", formatNumber(String.valueOf(thxql+d_dwxqsl),qtyFormat));
        dsMasterTable.setValue("scxql", formatNumber(String.valueOf(thscxql+d_dwxqsl),qtyFormat));
      }
      else
      {
        detail.put("cpid", singleProduct);
        detail.put("sl", formatNumber(String.valueOf(zjsl*thsl),qtyFormat ));
        detail.put("xql", formatNumber(String.valueOf(xql*thsl),qtyFormat));
        detail.put("scxql", formatNumber(String.valueOf(thscxql), qtyFormat) );
        detail.put("chxz", "1");
      }
    }
  }
  /**
   * 主表保存操作的触发类
   */
  class Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());

      //校验表单数据
      String temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        detail.setValue("sjbomid", detailrow.get("sjbomid"));
        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("xql", detailrow.get("xql"));//生产计划明细中数量乘以子件数量
        detail.setValue("sl", detailrow.get("sl"));//子件数量
        detail.setValue("scxql", detailrow.get("scxql"));//转化为生产公式的需求量
        detail.setValue("xqrq", detailrow.get("xqrq"));
        detail.setValue("zjlx", detailrow.get("zjlx"));//
        detail.setValue("cc", detailrow.get("cc"));//
        detail.setValue("chxz", detailrow.get("chxz"));//超产率
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("scjhmxid", detailrow.get("scjhmxid"));//生差计划明细ID
        detail.setValue("htid", detailrow.get("htid"));//合同ID
        detail.post();
        detail.next();
      }
      detail.saveDataSets(new EngineDataSet[]{detail});
      data.setMessage(showJavaScript("window.close();"));
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String xql = detailrow.get("xql");
        if((temp = checkNumber(xql, "需求量")) != null)
          return temp;
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, "子件数量")) != null)
          return temp;
        temp = detailrow.get("xqrq");
        if(temp.length() > 0 && !isDate(temp))
          return showJavaScript("alert('非法需求日期！');");
        if(d_RowInfos.size()==0)
          return showJavaScript("alert('没有生成物料需求不能保存')");
      }
      return null;
    }
  }
  /**
   * 得到用于替换可选物资信息的bean
   * @param req WEB的请求
   * @return 返回用于替换可选物资信息的bean
   */
  public PlanBomReplacer getReplaceProductBean(HttpServletRequest req)
  {
    if(planBomReplaceBean == null)
      planBomReplaceBean = PlanBomReplacer.getInstance(req);
    return planBomReplaceBean;
  }
  /**
  * 得到规格属性的bean
  * @param req WEB的请求
  * @return 返回规格属性的bean
  */
 public LookUp getPropertyBean(HttpServletRequest req)
 {
   if(propertyBean == null)
     propertyBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_SPEC_PROPERTY);
     return propertyBean;
 }
 /**
  * 得到用于查找产品单价的bean
  * @param req WEB的请求
  * @return 返回用于查找产品单价的bean
  */
 public LookUp getProductBean(HttpServletRequest req)
 {
   if(productBean == null)
     productBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PRODUCT);
   return productBean;
 }
}