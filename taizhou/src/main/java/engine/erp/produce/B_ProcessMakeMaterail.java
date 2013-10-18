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
 * <p>Title: 生产加工单编辑（通用加工单）页面里生成加工单物料</p>
 * <p>Description: 生产加工单编辑（通用加工单）页面里生成加工单物料<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_ProcessMakeMaterail extends BaseAction implements Operate
{
  public  static final String SUMIT_PROPERTY = "1009";//选择规格属性提交

  //生产加工单物料清单结构SQL语句
  private static final String DRAWMATERAIL_STRUT_SQL = "SELECT * FROM sc_jgdwl WHERE 1<>1";
  private static final String DRAWMATERAIL_SQL = "SELECT * FROM sc_jgdwl WHERE jgdmxid='?' ORDER BY cpid, jgdmxid ";
  //通过加工单主表生产计划ID和从表的任务单明细ID得到合同ID再根据cpid等于实际BOM表的上级cpid得到物料
  private static final String MATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, a.htid, b.scjhid "
         + " FROM sc_sjbom a, sc_jh b, sc_jhmx c WHERE a.scjhmxid=c.scjhmxid AND b.scjhid=c.scjhid AND a.htid IN( "
         + " SELECT d.htid FROM sc_wlxqjhmx d, sc_rwdmx e, sc_jgdmx f WHERE d.wlxqjhmxid=e.wlxqjhmxid AND e.rwdmxid='?') AND b.scjhid='?' AND a.sjcpid='?' "
         + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, a.htid, b.scjhid ORDER BY a.cpid ";
  //如果通用加工单明细中手工输入的半成品的原料SQL，要从实际BOM中得到
  private static final String BOM_SQL = "SELECT a.* FROM sc_bom a WHERE a.sjcpid='?'";

  private EngineDataSet dsFactBom = new EngineDataSet();//客户实际BOM数据集

  private EngineDataSet dsProcessMaterail = new EngineDataSet();//加工单物料清单数据集如果是通用计划该数据集只用于显示，不能编辑
  private EngineDataSet dsDrawMaterail = new EngineDataSet();//通用加工单时，对应加工单中每一条纪录都存在物料，该数据集用于修改

  public  HtmlTableProducer Producer = new HtmlTableProducer(dsProcessMaterail, "sc_jgdwl");
  public boolean isAdd = true;    //是否在添加状态
  private int    masterRow = -1;         //保存生产计划主表操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  /**
  private LookUp productBean = null; //产品的bean的引用, 用于提取产品
  private LookUp planUseAbleBean = null; //产品的bean的引用, 用于提取产品生产单位
  private LookUp propertyBean = null; //产品信息的bean的引用, 用于提取产品信息
 */

  public  String retuUrl = null;

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String scjhid = null;
  private String jgdmxid = null;
  private String jhlx = null;//计划类型
  private String sjcpid = null;//上级产品ＩＤ
  private String jgdid = null;
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  public String SC_PRODUCE_UNIT_STYLE = null;//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划--系统参数
  private boolean isInit = true;
  /**
   * 物料需求计划列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回物料需求计划列表的实例
   */
  public static B_ProcessMakeMaterail getInstance(HttpServletRequest request)
  {
    B_ProcessMakeMaterail processMakeMaterailBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "processMakeMaterailBean";
      processMakeMaterailBean = (B_ProcessMakeMaterail)session.getAttribute(beanName);
      if(processMakeMaterailBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        processMakeMaterailBean = new B_ProcessMakeMaterail();
        processMakeMaterailBean.qtyFormat = loginBean.getQtyFormat();

        processMakeMaterailBean.fgsid = loginBean.getFirstDeptID();
        processMakeMaterailBean.loginId = loginBean.getUserID();
        processMakeMaterailBean.loginName = loginBean.getUserName();
        processMakeMaterailBean.SC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam("SC_PRODUCE_UNIT_STYLE");//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划--系统参数
        processMakeMaterailBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        //设置格式化的字段
        processMakeMaterailBean.dsProcessMaterail.setColumnFormat("sl", processMakeMaterailBean.qtyFormat);
        processMakeMaterailBean.dsProcessMaterail.setColumnFormat("scsl", processMakeMaterailBean.qtyFormat);
        session.setAttribute(beanName, processMakeMaterailBean);
      }
    }
    return processMakeMaterailBean;
  }

  /**
   * 构造函数
   */
  private B_ProcessMakeMaterail()
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
    setDataSetProperty(dsProcessMaterail, DRAWMATERAIL_STRUT_SQL);
    setDataSetProperty(dsDrawMaterail, null);
    setDataSetProperty(dsFactBom, null);

    dsProcessMaterail.setSequence(new SequenceDescriptor(new String[]{"jgdwlid"}, new String[]{"s_sc_jgdwl"}));
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(POST), new Master_Post());
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
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
      if(dsProcessMaterail.isOpen() && dsProcessMaterail.changesPending())
        dsProcessMaterail.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   */
  public final void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsProcessMaterail != null){
      dsProcessMaterail.close();
      dsProcessMaterail = null;
    }
    if(dsDrawMaterail != null){
      dsDrawMaterail.close();
      dsDrawMaterail = null;
    }
    if(dsFactBom != null){
      dsFactBom.close();
      dsFactBom = null;
    }
    log = null;
    d_RowInfos = null;
    if(Producer != null)
    {
      Producer.release();
      Producer = null;
    }
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
      EngineDataSet dsDetail = dsProcessMaterail;
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
   * 从表保存操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = new RowMap();
    //保存网页的所有信息
    rowInfo.put(request);

    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//生产单位需求量
      detailRow.put("sl", rowInfo.get("sl_"+i));//需购量
      detailRow.put("scsl", rowInfo.get("scsl_"+i));//计量需求量
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//物资规格属性ID
      //保存用户自定义的字段
      FieldInfo[] fields = Producer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
    }
  }

  /*得到从表表对象*/
  public final EngineDataSet getMaterailTable(){
    return dsProcessMaterail;
  }

  /*得到从表多列的信息*/
  public final RowMap[] getMaterailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }

  /**
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  public final boolean masterIsAdd() {return isAdd; }

  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      Producer.init(request, loginId);
      if(!isInit)
        return;
      //
      scjhid = request.getParameter("scjhid");
      jgdid = request.getParameter("jgdid");
      jgdmxid = request.getParameter("jgdmxid");
      String rwdmxid = request.getParameter("rwdmxid");
      jhlx = request.getParameter("jhlx");
      sjcpid = request.getParameter("cpid");
      String sl = request.getParameter("sl");
      String scsl = request.getParameter("scsl");
      double d_sl = sl.length()>0 ? Double.parseDouble(sl) : 0;//加工单中产品要加工的数量
      double d_scsl = scsl.length()>0 ? Double.parseDouble(scsl) : 0;//
      String SQL = combineSQL(DRAWMATERAIL_SQL, "?", new String[]{jgdmxid});
      dsProcessMaterail.setQueryString(SQL);
      if(dsProcessMaterail.isOpen())
        dsProcessMaterail.refresh();
      else
        dsProcessMaterail.open();
      if(dsProcessMaterail.getRowCount() > 0)//通过参数生产加工单明细ID得出已经生成加工单物料
        isAdd=false;
      else {                     //没有生成加工单物料
        isAdd = true;
        dsProcessMaterail.setQueryString(DRAWMATERAIL_STRUT_SQL);
        if(dsProcessMaterail.isOpen())
          dsProcessMaterail.refresh();
        else
          dsProcessMaterail.open();
        //如果该条纪录是手工增加不是引入任务单，物料从实际ＢＯＭ中抽取
        if(sjcpid.equals("") || sjcpid==null)
          return;
        if(rwdmxid.equals("") || scjhid.equals("") || scjhid.equals("null") ||scjhid==null){
          String FACTBOM = combineSQL(BOM_SQL,"?", new String[]{sjcpid});
          dsFactBom.setQueryString(FACTBOM);
          if(dsFactBom.isOpen())
            dsFactBom.refresh();
          else
            dsFactBom.openDataSet();

          dsFactBom.first();
          for(int i=0;i<dsFactBom.getRowCount();i++){
            dsProcessMaterail.insertRow(false);
            dsProcessMaterail.setValue("jgdwlid", "-1");
            dsProcessMaterail.setValue("jgdmxid", jgdmxid);
            dsProcessMaterail.setValue("jgdid",jgdid);
            dsProcessMaterail.setValue("cpid", dsFactBom.getValue("cpid"));
            double zjsl = dsFactBom.getValue("sl").length()>0 ? Double.parseDouble(dsFactBom.getValue("sl")) : 0;//生产该产品需要下级原料的数量
            double shl = dsFactBom.getValue("shl").length()>0 ? Double.parseDouble(dsFactBom.getValue("shl")) : 0;//生产该产品需要下级原料的数量
            double m_sl = d_sl*zjsl*(1+shl);
            dsProcessMaterail.setValue("sl",formatNumber(String.valueOf(m_sl), qtyFormat));
            dsProcessMaterail.setValue("scsl",formatNumber(String.valueOf(m_sl), qtyFormat));
            dsProcessMaterail.post();
            dsFactBom.next();
          }
        }
        else{
          String  SQl = combineSQL(MATERAIL_LIST_SQL, "?", new String[]{rwdmxid, scjhid,sjcpid});
          if(dsDrawMaterail.isOpen())
            dsDrawMaterail.refresh();
          else
            dsDrawMaterail.openDataSet();
          dsDrawMaterail.first();
          for(int i=0;i<dsDrawMaterail.getRowCount();i++){
            dsProcessMaterail.insertRow(false);
            dsProcessMaterail.setValue("jgdwlid", "-1");
            dsProcessMaterail.setValue("jgdmxid", jgdmxid);
            dsProcessMaterail.setValue("jgdid",jgdid);
            dsProcessMaterail.setValue("cpid", dsDrawMaterail.getValue("cpid"));
            dsProcessMaterail.setValue("dmsxid", dsDrawMaterail.getValue("dmsxid"));
            dsProcessMaterail.setValue("sl",dsDrawMaterail.getValue("xql"));
            dsProcessMaterail.setValue("scsl",dsDrawMaterail.getValue("scxql"));
            dsProcessMaterail.post();
            dsFactBom.next();
          }
        }
      }
      isInit = false;
      initRowInfo(true);
    }
  }
  /**
   *  从表增加操作(增加一个空白行)
   */
  class Detail_Add implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getMaterailTable();
      detail.insertRow(false);
      detail.setValue("jgdwlid", "-1");
      detail.setValue("jgdid", jgdid);
      detail.setValue("jgdmxid",jgdmxid);
      detail.post();
      d_RowInfos.add(new RowMap());
    }
  }
  /**
   *  从表删除操作
   */
  class Detail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getMaterailTable();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      //删除临时数组的一列数据
      d_RowInfos.remove(rownum);
      ds.goToRow(rownum);
      ds.deleteRow();
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
      String temp = null;
      temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getMaterailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录

        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("sl", detailrow.get("sl"));//生产单位需求量
        detail.setValue("scsl", detailrow.get("scsl"));//需购量
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        //保存用户自定义的字段
        FieldInfo[] fields = Producer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        detail.next();
      }
      //保存用户自定义的字段
      //data.setMessage(showJavaScript("refresh();"));
    }
    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo()
    {
      String temp = null;
      RowMap detailrow = null;
      ArrayList list = new ArrayList(d_RowInfos.size());
      String cpid=null, dmsxid=null,unit=null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid = detailrow.get("cpid");
        dmsxid = detailrow.get("dmsxid");
        StringBuffer buf = new StringBuffer().append(cpid).append(",").append(dmsxid).append(",");
        unit = buf.toString();
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        if(list.contains(unit))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(unit);
        String scsl = detailrow.get("scsl");//生产单位需求量
        if(scsl.length()>0 &&  (temp = checkNumber(scsl, "第"+row+"行生产数量")) != null)
          return temp;
        String sl = detailrow.get("sl");//计量单位需求量
        if(sl.equals(""))
          return showJavaScript("alert('第"+row+"行数量不能为空');");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
      }
      return null;
    }
  }
}