package engine.erp.produce;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineDataView;
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
import engine.erp.produce.ImportTask;
import engine.erp.produce.B_Process_SingleSelTask;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 生产--生产加工单列表；委外加工单列表</p>
 * <p>Description: 生产--生产加工单列表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_ProduceProcess extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "10001";
  public  static final String DETAIL_SELECT_TASK = "10021";
  public  static final String ONCHANGE = "10031";
  public  static final String SINGLE_SELECT_TASK = "10531";
  public  static final String PRODUCT_ONCHANGE = "10091";//输入产品触发事件
  public  static final String SINGLE_SELECT_PRODUCT = "10891";//单选产品触发事件
  public  static final String COMPLETE = "11001";//手工强制完成事件
  public  static final String MATERAIL_ADD = "11002";//生产加工单物料增加事件
  public  static final String SUBTASK_ADD = "11003";//生产加工单新增分切计划任务单增加事件
  public  static final String MATERAIL_DEL = "11004";//生产加工单物料清单删除事件
  public  static final String COMMONMATERAIL_EDIT = "11005";//通用加工单。物料清单查看编辑事件
  public  static final String COMMONMATERAIL_ADD = "11006";//通用加工单。物料清单增加编辑事件
  public  static final String COMMONMATERAIL_DEL = "11007";//通用加工单。物料清单删除事件
  public  static final String COMMONDETAIL_DEL = "11008";//通用加工单。物料清单删除事件
  public  static final String CONFIRM = "11009";//通用加工单。对每一条加工单明细的物料清单保存到内存事件
  public  static final String COMMON_POST = "2001";//通用加工单。保存事件
  public  static final String COMMONPOST_CONTINUE = "2002";//通用加工单。保存事件
  public  static final String DETAIL_REFRESH = "2003";//通用加工单。保存事件
  public  static final String MATERAIL_REFRESH = "2004";//通用加工单。刷新该条加工单物料事件


  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_jgd WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM sc_jgd WHERE ? AND sfwjg='?' AND fgsid=? ? ORDER BY jgdh DESC";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM sc_jgdmx WHERE 1<>1";
  private static final String DETAIL_SQL    = "SELECT * FROM sc_jgdmx WHERE jgdid='?' ORDER BY cpid, jgdmxid";//
  //引任务单明细
  private static final String TASK_DETAIL_SQL = "SELECT * FROM sc_rwdmx WHERE nvl(sl,0)>nvl(yjgl,0) AND rwdid= ";

  //生产加工单物料清单结构SQL语句
  private static final String DRAWMATERAIL_STRUT_SQL = "SELECT * FROM sc_jgdwl WHERE 1<>1 ORDER BY cpid";
  private static final String DRAWMATERAIL_SQL = "SELECT * FROM sc_jgdwl WHERE jgdid='?' ORDER BY cpid, jgdmxid ";
  //生产加工单物料清单编辑页面SQL
  //private static final String DRAWMATERAIL_EDIT_SQL = "SELECT * FROM sc_jgdwl WHERE jgdid='?' AND jgdmxid='?'";
  //通过加工单主表生产计划ID和从表的任务单明细ID得到合同ID再根据cpid等于实际BOM表的上级cpid得到物料
  private static final String MATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, a.htid "
             + " FROM sc_sjbom a WHERE a.sjsjbomid IN (SELECT b.sjbomid "
             + " FROM sc_sjbom b, sc_jh c, sc_jhmx d WHERE b.scjhmxid=d.scjhmxid AND c.scjhid=d.scjhid  AND  nvl(b.htid,-1) IN( "
             + " SELECT nvl(d.htid,-1) FROM sc_wlxqjhmx d, sc_rwdmx e WHERE d.wlxqjhmxid=e.wlxqjhmxid AND  e.rwdmxid='?') "
             + " AND c.scjhid='?' AND b.cpid='?' ?) "
             + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, a.htid ORDER BY a.cpid";
  /**
  private static final String MATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, a.htid, b.scjhid "
         + " FROM sc_sjbom a, sc_jh b, sc_jhmx c WHERE a.scjhmxid=c.scjhmxid AND b.scjhid=c.scjhid AND nvl(a.htid,-1) IN( "
         + " SELECT nvl(d.htid,-1) FROM sc_wlxqjhmx d, sc_rwdmx e WHERE d.wlxqjhmxid=e.wlxqjhmxid AND  e.rwdmxid='?') AND b.scjhid='?' AND a.sjcpid='?' "
         + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, a.htid, b.scjhid ORDER BY a.cpid ";
         */
  //分切计划通过加工单主表生产计划ID和从表的任务单明细ID得到合同ID再根据cpid等于实际BOM表的上级cpid得到物料
  private static final String SUBMATERAIL_LIST_SQL =" SELECT a.cpid, a.sjcpid, a.dmsxid, sum(nvl(a.xql,0)) xql, sum(nvl(a.scxql,0)) scxql, b.scjhid "
         + " FROM sc_sjbom a, sc_jh b, sc_jhmx c WHERE a.scjhmxid=c.scjhmxid AND b.scjhid=c.scjhid AND nvl(a.htid,-1) IN( "
         + " SELECT nvl(d.htid,-1) FROM sc_wlxqjhmx d, sc_rwdmx e WHERE d.wlxqjhmxid=e.wlxqjhmxid  AND e.rwdmxid IN(?)) AND b.scjhid='?' AND a.sjcpid IN(?) "
         + " GROUP BY a.cpid, a.sjcpid, a.dmsxid, b.scjhid ORDER BY a.cpid ";
  //如果通用加工单明细中手工输入的半成品的原料SQL，要从实际BOM中得到
  private static final String BOM_SQL = "SELECT a.* FROM sc_bom a WHERE a.sjcpid='?'";


  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  private EngineDataSet dsProcessMaterail = new EngineDataSet();//分切加工单： 加工单物料清单数据集如果是通用计划该数据集只用于显示，不能编辑
  private EngineDataSet dsDrawMaterail = new EngineDataSet();//通用加工单时： 对应加工单中每一条纪录都存在物料，该数据集用于修改和保存每条明细的物料

  private EngineDataSet dsBom = new EngineDataSet();//BOM零时数据集
  private EngineDataSet dsFactBomMaterail = new EngineDataSet();//实际BOM零时数据集
  //private EngineDataView dataview = new EngineDataView();

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sc_jgd");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sc_jgdmx");
  public  HtmlTableProducer materailProducer = new HtmlTableProducer(dsProcessMaterail, "sc_jgdwl");
  public  HtmlTableProducer commonProducer = new HtmlTableProducer(dsDrawMaterail, "sc_jgdwl");

  private boolean isMasterAdd = true;    //是否在添加状态
  public boolean isDetailAdd = false; //从表是否在增加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针

  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用
  private ArrayList d_MaterailBill = null;//物料清单多行纪录的引用,分切加工单用
  private ArrayList d_CommonMaterail = null;//物料多行纪录，通用加工单


  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  private ImportTask importTaskBean = null; //生产任务单的bean的引用, 用于提取生产任务单信息
  private B_Process_SingleSelTask singleSelTaskBean = null; //生产任务单主表信息BEAN的引用

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String jgdid = null;
  private User user = null;
  public String SC_PRODUCE_UNIT_STYLE =null;//1=强制换算,0=仅空值时换算
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  private String scjhid = null;//生产计划ID
  private String rwdid = null;//生产任务单ID
  private String jglx = null;//生产加工单加工类型
  private String jgdmxid = null;
  public String SC_PLAN_ADD_STYLE = null;//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划--系统参数
  private int procDetaiLlSeq = -1;//在生成物料的时候可用,因为新增加工单明细的时候还没有得到加工单明细ＩＤ。而生成物料时又要用到

  public String sfwjg ="0";//是否外加工0是自加工，1是外加工
  /**
   * 生产加工单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回生产加工单列表的实例
   */
  public static B_ProduceProcess getInstance(HttpServletRequest request)
  {
    B_ProduceProcess produceProcessBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "produceProcessBean";
      produceProcessBean = (B_ProduceProcess)session.getAttribute(beanName);
      if(produceProcessBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        produceProcessBean = new B_ProduceProcess();
        produceProcessBean.qtyFormat = loginBean.getQtyFormat();
        produceProcessBean.sumFormat = loginBean.getSumFormat();

        produceProcessBean.fgsid = loginBean.getFirstDeptID();
        produceProcessBean.loginId = loginBean.getUserID();
        produceProcessBean.loginName = loginBean.getUserName();
        produceProcessBean.loginDept = loginBean.getDeptID();
        produceProcessBean.user = loginBean.getUser();
        produceProcessBean.SC_PLAN_ADD_STYLE = loginBean.getSystemParam("SC_PLAN_ADD_STYLE");//0=显示选择计划的对话框,1=直接生成通用计划,2=直接生成分切计划--系统参数
        produceProcessBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……

        produceProcessBean.SC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam("SC_PRODUCE_UNIT_STYLE");//计量单位和生产单位是否强制换算
        //设置格式化的字段
        produceProcessBean.dsDetailTable.setColumnFormat("sl", produceProcessBean.qtyFormat);
        produceProcessBean.dsDetailTable.setColumnFormat("scsl", produceProcessBean.sumFormat);
        produceProcessBean.dsMasterTable.setColumnFormat("zsl", produceProcessBean.sumFormat);
        produceProcessBean.dsProcessMaterail.setColumnFormat("sl",produceProcessBean.qtyFormat);
        produceProcessBean.dsProcessMaterail.setColumnFormat("scsl",produceProcessBean.qtyFormat);
        produceProcessBean.dsDrawMaterail.setColumnFormat("sl",produceProcessBean.qtyFormat);
        produceProcessBean.dsDrawMaterail.setColumnFormat("scsl",produceProcessBean.qtyFormat);
        session.setAttribute(beanName, produceProcessBean);
      }
    }
    return produceProcessBean;
  }

  /**
   * 构造函数
   */
  private B_ProduceProcess()
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
    setDataSetProperty(dsMasterTable, MASTER_STRUT_SQL);
    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
    setDataSetProperty(dsProcessMaterail, DRAWMATERAIL_STRUT_SQL);
    setDataSetProperty(dsDrawMaterail, DRAWMATERAIL_STRUT_SQL);
    setDataSetProperty(dsBom, null);
    setDataSetProperty(dsFactBomMaterail, null);

    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jgdh"}, new String[]{"SELECT pck_base.billNextCode('sc_jgd','jgdh') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"jgdh"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"jgdmxid"}, new String[]{"s_sc_jgdmx"}));

    dsProcessMaterail.setSequence(new SequenceDescriptor(new String[]{"jgdwlid"}, new String[]{"s_sc_jgdwl"}));//分切加工单
    dsDrawMaterail.setSequence(new SequenceDescriptor(new String[]{"jgdwlid"}, new String[]{"s_sc_jgdwl"}));//通用加工单
    //
    dsDrawMaterail.setMasterLink(
        new MasterLinkDescriptor(dsDetailTable,  new String[]{"jgdmxid"}, new String[]{"jgdmxid"}, false, true, true));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(SUBTASK_ADD), masterAddEdit);//新增分切计划任务触发事件
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    addObactioner(String.valueOf(DETAIL_SELECT_TASK), new Detail_Select_Task());
    addObactioner(String.valueOf(SINGLE_SELECT_TASK), new Single_Select_Task());//单选任务单
    addObactioner(String.valueOf(PRODUCT_ONCHANGE), new Product_Onchange());//输入产品编码触发事件
    addObactioner(String.valueOf(MATERAIL_ADD), new Materail_Add());//加工单物料增加事件
    addObactioner(String.valueOf(COMPLETE), new Complete());//强制完成操作
    addObactioner(String.valueOf(MATERAIL_DEL), new Materail_Delete());//物料删除操作
    addObactioner(String.valueOf(COMMONMATERAIL_EDIT), new CommonMaterail_Edit());//通用加工单，物料查看编辑操作
    addObactioner(String.valueOf(COMMONMATERAIL_ADD), new CommonMaterail_Add());//通用加工单，物料增加操作
    addObactioner(String.valueOf(COMMONMATERAIL_DEL), new CommonMaterail_Delete());//通用加工单，物料删除操作
    addObactioner(String.valueOf(COMMONDETAIL_DEL), new CommonMaster_Delete());//通用加工单，删除从表操作
    addObactioner(String.valueOf(CONFIRM), new Confirm());//通用加工单，删除从表操作
    addObactioner(String.valueOf(COMMON_POST), new Common_Post());//通用加工单，保存操作
    addObactioner(String.valueOf(COMMONPOST_CONTINUE), new Common_Post());//通用加工单，保存添加操作
    addObactioner(String.valueOf(DETAIL_REFRESH), new Detail_Refresh());//通用加工单，保存加工单明细页面信息操作
    addObactioner(String.valueOf(MATERAIL_REFRESH), new CommonMaterail_Edit());//通用加工单，保存操作
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
    if(dsDetailTable != null){
      dsDetailTable.close();
      dsDetailTable = null;
    }
    if(dsProcessMaterail != null){
      dsProcessMaterail.close();
      dsProcessMaterail = null;
    }
    if(dsDrawMaterail != null){
     dsDrawMaterail.close();
     dsDrawMaterail = null;
   }
   if(dsBom != null){
     dsBom.close();
     dsBom = null;
    }
    if(dsFactBomMaterail != null){
     dsFactBomMaterail.close();
     dsFactBomMaterail = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
    d_MaterailBill = null;
    d_CommonMaterail = null;
    if(masterProducer != null)
    {
      masterProducer.release();
      masterProducer = null;
    }
    if(detailProducer != null)
    {
      detailProducer.release();
      detailProducer = null;
    }
    if(materailProducer != null)
    {
      materailProducer.release();
      materailProducer = null;
    }
    if(commonProducer != null)
   {
     commonProducer.release();
     commonProducer = null;
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
  private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否是主表
    if(isMaster){
      if(isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();

      if(!isAdd)
        m_RowInfo.put(getMaterTable());
      else
      {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        m_RowInfo.put("zdrq", today);//制单日期
        m_RowInfo.put("zdr", loginName);//操作员
        m_RowInfo.put("zdrid", loginId);
        m_RowInfo.put("deptid", loginDept);
        m_RowInfo.put("rq", today);
        m_RowInfo.put("zt", "0");
      }
    }
    else
    {
      EngineDataSet dsDetail = dsDetailTable;
      if(d_RowInfos == null)
        d_RowInfos = new ArrayList(dsDetail.getRowCount());
      else if(isInit)
        d_RowInfos.clear();

      dsDetail.first();//循环加工单明细把数据存入RowMap里面
      for(int i=0; i<dsDetail.getRowCount(); i++)
      {
        RowMap row = new RowMap(dsDetail);
        d_RowInfos.add(row);
        dsDetail.next();
      }
       if(jglx.equals("1")){
         EngineDataSet dsMaterail = getMaterailTable();
         if(d_MaterailBill == null)
           d_MaterailBill = new ArrayList(dsMaterail.getRowCount());
         else if(isInit)
           d_MaterailBill.clear();

         dsMaterail.first();//循环物料清单数据集
         for(int j=0; j<dsMaterail.getRowCount(); j++)
         {
           RowMap row = new RowMap(dsMaterail);
           d_MaterailBill.add(row);
           dsMaterail.next();
         }
       }
       else{
         EngineDataSet dsCommon = getCommonMaterail();
         if(d_CommonMaterail == null)
           d_CommonMaterail = new ArrayList(dsCommon.getRowCount());
         else if(isInit)
           d_CommonMaterail.clear();

         dsCommon.first();//循环物料清单数据集
         for(int j=0; j<dsCommon.getRowCount(); j++)
         {
           RowMap row = new RowMap(dsCommon);
           d_CommonMaterail.add(row);
           dsCommon.next();
         }
       }
    }
  }

  /**
    * 初始化列信息
    * @param isAdd 是否时添加
    * @param isInit 是否从新初始化
    * @throws java.lang.Exception 异常
    */
   private final void initCommonInfo(boolean isInit) throws java.lang.Exception
   {
     EngineDataSet dsCommon = getCommonMaterail();
     if(d_CommonMaterail == null)
       d_CommonMaterail = new ArrayList(dsCommon.getRowCount());
     else if(isInit)
       d_CommonMaterail.clear();

     dsCommon.first();//循环物料清单数据集
     for(int j=0; j<dsCommon.getRowCount(); j++)
     {
       RowMap row = new RowMap(dsCommon);
       d_CommonMaterail.add(row);
       dsCommon.next();
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
    RowMap rowInfo = getMasterRowinfo();
    //保存网页的所有信息
    rowInfo.put(request);

    //生产加工单明细信息
    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      String cpid = rowInfo.get("cpid_"+i);
      String sl = rowInfo.get("sl_"+i);
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//产品
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//计量单位数量
      detailRow.put("scsl", formatNumber(rowInfo.get("scsl_"+i), qtyFormat));//生产单位数量
      detailRow.put("gylxid", rowInfo.get("gylxid_"+i));//工艺路线
      detailRow.put("cpl", rowInfo.get("cpl_"+i));//出品率
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//物资规格属性
      detailRow.put("jgyq", rowInfo.get("jgyq"));
      if(sfwjg.equals("1")){//委外加工单保存页面的加工单价和加工金额信息
        detailRow.put("jgdj", formatNumber(rowInfo.get("jgdj_"+i), priceFormat));
        detailRow.put("jgje", formatNumber(rowInfo.get("jgje_"+i), sumFormat));
      }
      //保存用户自定义的字段
      FieldInfo[] fields = detailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
    }
    if(jglx.equals("1")){
      //保存生产加工单物料信息
      int count = d_MaterailBill.size();
      RowMap materailRow = null;
      for(int m=0; m<count; m++)
      {
        materailRow = (RowMap)d_MaterailBill.get(m);
        materailRow.put("cpid", rowInfo.get("wlcpid_"+m));//产品
        materailRow.put("sl", formatNumber(rowInfo.get("wlsl_"+m), qtyFormat));//计量单位数量
        materailRow.put("scsl", formatNumber(rowInfo.get("wlscsl_"+m), qtyFormat));//生产单位数量
        materailRow.put("dmsxid", rowInfo.get("wldmsxid_"+m));//物资规格属性
        //保存用户自定义的字段
        FieldInfo[] fields = materailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          materailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + j));
        }
      }
    }
  }
  /**
   * 通用加工单
   * 保存用户输入的信息
   */
  private final void putCommonInfo(HttpServletRequest request)
  {
    RowMap rowInfo = new RowMap();
    //保存网页的所有信息
    rowInfo.put(request);
    //保存生产加工单物料信息
   int count = d_CommonMaterail.size();
   RowMap materailRow = null;
   for(int m=0; m<count; m++)
   {
     materailRow = (RowMap)d_CommonMaterail.get(m);
     materailRow.put("cpid", rowInfo.get("cpid_"+m));//产品
     materailRow.put("sl", formatNumber(rowInfo.get("sl_"+m), qtyFormat));//计量单位数量
     materailRow.put("scsl", formatNumber(rowInfo.get("scsl_"+m), qtyFormat));//生产单位数量
     materailRow.put("dmsxid", rowInfo.get("dmsxid_"+m));//物资规格属性
     //保存用户自定义的字段
     FieldInfo[] fields = commonProducer.getBakFieldCodes();
     for(int j=0; j<fields.length; j++)
     {
       String fieldCode = fields[j].getFieldcode();
       materailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + j));
     }
   }
  }

  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }
  /*通用加工单用于显示
  public final EngineDataView getDataView()
  {
    return dataview;
  }
  */

  /*得到从表表对象*/
  public final EngineDataSet getDetailTable(){
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    return dsDetailTable;
  }
  /*得到分切加工单物料表对象*/
  public final EngineDataSet getMaterailTable()
  {
    if(!dsProcessMaterail.isOpen())
      dsProcessMaterail.open();
    return dsProcessMaterail;
  }
  /*得到通用加工单物料表对象*/
  public final EngineDataSet getCommonMaterail()
  {
    if(!dsDrawMaterail.isOpen())
      dsDrawMaterail.open();
    return dsDrawMaterail;
  }
  /**
   * 得到任务是通过什么计划生成的。1.分切计划0通用计划
   */
  public final String getTaskType(){
    return jglx;
  }
  /**
   * 得到生产计划ＩＤ
   */
  public final String getPlanID(){
    return scjhid;
  }
  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    String id = isMasterAdd ? "-1" : jgdid;
    String SQL = combineSQL(DETAIL_SQL, "?", new String[]{id});

    dsDetailTable.setQueryString(SQL);//打开加工单明细数据集
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    else
      dsDetailTable.refresh();
  }

 /*加工单时打开物料从表*/
  public final void openMaterailTable(boolean isMasterAdd)
  {
    String id = isMasterAdd ? "-1" : jgdid;
    String materailSQL = combineSQL(DRAWMATERAIL_SQL, "?", new String[]{id});//打开物料清单数据集]
    if(jglx.equals("1")){
      dsProcessMaterail.setQueryString(materailSQL);
      if(!dsProcessMaterail.isOpen())
        dsProcessMaterail.open();
      else
        dsProcessMaterail.refresh();
    }
    else{
      dsDrawMaterail.setQueryString(materailSQL);
      if(!dsDrawMaterail.isOpen())
        dsDrawMaterail.open();
      else
        dsDrawMaterail.refresh();
    }
  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }
  /*得到物料表多列的信息：分切加工单用*/
  public final RowMap[] getMaterailRowinfos() {
    RowMap[] rows = new RowMap[d_MaterailBill.size()];
    d_MaterailBill.toArray(rows);
    return rows;
  }
  /*得到物料表多列的信息：通用加工单用*/
  public final RowMap[] getCommonRowinfos() {
    RowMap[] rows = new RowMap[d_CommonMaterail.size()];
    d_CommonMaterail.toArray(rows);
    return rows;
  }

  /**
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  public final boolean masterIsAdd() {return isMasterAdd; }

  /**
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getFixedQueryValue(String col)
  {
    return fixedQuery.getSearchRow().get(col);
  }

  /**
   * 得到选中的行的行数
   * @return 若返回-1，表示没有选中的行
   */
  public final int getSelectedRow()
  {
    if(masterRow < 0)
      return -1;

    dsMasterTable.goToInternalRow(masterRow);
    return dsMasterTable.getRow();
  }
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
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      materailProducer.init(request, loginId);
      commonProducer.init(request, loginId);

      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("zt", "0");
      row.put("rq$a", startDay);
      row.put("rq$b", today);
      isMasterAdd = true;
      isDetailAdd =false;
      //初始化时不显示已完成的单据
      String SQL = " AND zt<>8";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"),sfwjg,fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();
      if(dsProcessMaterail.isOpen() && dsProcessMaterail.getRowCount() > 0)
        dsProcessMaterail.empty();
    }
  }

  /**
   * 显示从表的列表信息
   */
  class ShowDetail implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterTable.getInternalRow();
      jgdid = dsMasterTable.getValue("jgdid");
      //打开从表
      openDetailTable(false);
    }
  }

  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      procDetaiLlSeq = -1;
      isMasterAdd = !String.valueOf(EDIT).equals(action);
      boolean isSubTaskAdd = String.valueOf(SUBTASK_ADD).equals(action);
      isDetailAdd = false;
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        scjhid = dsMasterTable.getValue("scjhid");
        jglx = dsMasterTable.getValue("jglx");
        jgdid = dsMasterTable.getValue("jgdid");
      }
      if(isMasterAdd){
        if(isSubTaskAdd)
          jglx = "1";
        else
          jglx = "0";
      }
      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      openMaterailTable(isMasterAdd);
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      if(jglx.equals("1"))
        data.setMessage(showJavaScript("toSubDetail();"));
      else
        data.setMessage(showJavaScript("toDetail();"));

    }
  }
  /**
  * 通用加工单中，加工单明细物料生成及修改操作的触发类
  * 如果该条明细没有物料，则生成
  * 如果是手工增加就去BOM表里面抽取
  * 如果是引入任务单并且是从生产计划里面过来的，就要关联到实际BOM里面收取
  */
 class CommonMaterail_Edit implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     boolean isRefresh = String.valueOf(MATERAIL_REFRESH).equals(action);

     if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
     String jgdid = dsMasterTable.getValue("jgdid");

     int rownum = Integer.parseInt(data.getParameter("rownum"));
     dsDetailTable.goToRow(rownum);
     if(dsDrawMaterail.getRowCount()>0){
       if(isRefresh)
         dsDrawMaterail.deleteAllRows();
       else{
         initCommonInfo(true);
         return;
       }
     }
     //RowMap detail = (RowMap)d_RowInfos.get(rownum);
     jgdmxid = dsDetailTable.getValue("jgdmxid");
     String rwdmxid = dsDetailTable.getValue("rwdmxid");
     String sjcpid = data.getParameter("cpid");
     String sl = data.getParameter("sl");
     String scsl = data.getParameter("scsl");
     String dmsxid = data.getParameter("dmsxid");
     double d_sl = sl.length()>0 ? Double.parseDouble(sl) : 0;//加工单中产品要加工的数量
     double d_scsl = scsl.length()>0 ? Double.parseDouble(scsl) : 0;//
     //如果该条纪录是手工增加不是引入任务单，物料从实际ＢＯＭ中抽取
     if(sjcpid.equals("") || sjcpid==null)
       return;
     if(rwdmxid.equals("") || scjhid.equals("")  || scjhid==null){
       String FACTBOM = combineSQL(BOM_SQL,"?", new String[]{sjcpid});
       dsBom.setQueryString(FACTBOM);
       if(dsBom.isOpen())
         dsBom.refresh();
       else
         dsBom.openDataSet();

       dsBom.first();
       for(int i=0;i<dsBom.getRowCount();i++){
         dsDrawMaterail.insertRow(false);
         dsDrawMaterail.setValue("jgdwlid", "-1");
         dsDrawMaterail.setValue("jgdmxid", jgdmxid);
         dsDrawMaterail.setValue("jgdid",isMasterAdd ? "-1" : jgdid);
         dsDrawMaterail.setValue("cpid", dsBom.getValue("cpid"));
         double zjsl = dsBom.getValue("sl").length()>0 ? Double.parseDouble(dsBom.getValue("sl")) : 0;//生产该产品需要下级原料的数量
         double shl = dsBom.getValue("shl").length()>0 ? Double.parseDouble(dsBom.getValue("shl")) : 0;//生产该产品需要下级原料的数量
         double m_sl = d_sl*zjsl*(1+shl);
         dsDrawMaterail.setValue("sl",formatNumber(String.valueOf(m_sl), qtyFormat));
         dsDrawMaterail.setValue("scsl",formatNumber(String.valueOf(m_sl), qtyFormat));
         dsDrawMaterail.post();
         dsBom.next();
       }
     }
     else if(!rwdmxid.equals("") && !scjhid.equals("")  && scjhid!=null){
       String temp = null;
       if(dmsxid.equals(""))
         temp =" AND b.dmsxid IS NULL";
       else
         temp = " AND b.dmsxid ="+ dmsxid;
       String  SQl = combineSQL(MATERAIL_LIST_SQL, "?", new String[]{rwdmxid, scjhid,sjcpid, temp});
       dsFactBomMaterail.setQueryString(SQl);
         if(dsFactBomMaterail.isOpen())
           dsFactBomMaterail.refresh();
         else
           dsFactBomMaterail.openDataSet();
         dsFactBomMaterail.first();
         for(int i=0;i<dsFactBomMaterail.getRowCount();i++){
           dsDrawMaterail.insertRow(false);
           dsDrawMaterail.setValue("jgdwlid", "-1");
           dsDrawMaterail.setValue("jgdmxid", jgdmxid);
           dsDrawMaterail.setValue("jgdid",isMasterAdd ? "-1" : jgdid);
           dsDrawMaterail.setValue("cpid", dsFactBomMaterail.getValue("cpid"));
           dsDrawMaterail.setValue("dmsxid", dsFactBomMaterail.getValue("dmsxid"));
           dsDrawMaterail.setValue("sl",dsFactBomMaterail.getValue("xql"));
           dsDrawMaterail.setValue("scsl",dsFactBomMaterail.getValue("scxql"));
           dsDrawMaterail.post();
           dsFactBomMaterail.next();
         }
     }
     initCommonInfo(true);
   }
 }
 /**
  *  通用加工单才有的功能
  *  加工单物料清单增加操作(增加一个空白行)
  */
 class CommonMaterail_Add implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     //保存输入的明细信息
     putDetailInfo(data.getRequest());
     putCommonInfo(data.getRequest());
     EngineDataSet dsMaterail = getCommonMaterail();
     EngineDataSet ds = getMaterTable();
     if(!isMasterAdd)
       ds.goToInternalRow(masterRow);
     String jgdid = dsMasterTable.getValue("jgdid");
     dsMaterail.insertRow(false);
     dsMaterail.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
     dsMaterail.setValue("jgdmxid", jgdmxid);
     dsMaterail.post();
     d_CommonMaterail.add(new RowMap());
   }
 }
 /**
  *  通用加工单才有的功能
  *  物料删除操作
  */
 class CommonMaterail_Delete implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     putDetailInfo(data.getRequest());
     EngineDataSet dsMaterail = getCommonMaterail();
     int rownum = Integer.parseInt(data.getParameter("rownum"));
     //删除临时数组的一列数据
     d_CommonMaterail.remove(rownum);
     dsMaterail.goToRow(rownum);
     dsMaterail.deleteRow();
   }
 }
 /**
  * 通用加工单主表保存操作的触发类
  */
 class Confirm implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     putCommonInfo(data.getRequest());
     //校验表单数据
     String temp = null;
     temp = checkInfo();
     if(temp != null)
     {
       data.setMessage(temp);
       return;
     }
     //保存从表的数据
     RowMap detailrow = null;
     EngineDataSet detail = getCommonMaterail();
     detail.first();
     for(int i=0; i<detail.getRowCount(); i++)
     {
       detailrow = (RowMap)d_CommonMaterail.get(i);
       //新添的记录
       detail.setValue("cpid", detailrow.get("cpid"));
       detail.setValue("sl", detailrow.get("sl"));//生产单位需求量
       detail.setValue("scsl", detailrow.get("scsl"));//需购量
       detail.setValue("dmsxid", detailrow.get("dmsxid"));
       //保存用户自定义的字段
       FieldInfo[] fields = commonProducer.getBakFieldCodes();
       for(int j=0; j<fields.length; j++)
       {
         String fieldCode = fields[j].getFieldcode();
         detail.setValue(fieldCode, detailrow.get(fieldCode));
       }
       detail.post();
       detail.next();
     }
     //保存用户自定义的字段
     data.setMessage(showJavaScript("refresh();"));
   }
   /**
    * 校验从表表单信息从表输入的信息的正确性
    * @return null 表示没有信息
    */
   private String checkInfo()
   {
     String temp = null;
     RowMap detailrow = null;
     ArrayList list = new ArrayList(d_CommonMaterail.size());
     String cpid=null, dmsxid=null,unit=null;
     for(int i=0; i<d_CommonMaterail.size(); i++)
     {
       int row = i+1;
       detailrow = (RowMap)d_CommonMaterail.get(i);
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
  /**
   * 分切加工单保存操作
   * 主表保存操作的触发类
   */
  class Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());

      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      //校验表单数据
      String temp = checkMasterInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      temp = checkMaterail();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);

      //得到主表主键值
      String jgdid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        jgdid = dataSetProvider.getSequence("s_sc_jgd");
        ds.setValue("jgdid", jgdid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
        ds.setValue("zt", "0");
      }
      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("jgdid", jgdid);

        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("sl", detailrow.get("sl"));//保存计量单位数量
        detail.setValue("scsl", detailrow.get("scsl"));//保存生产单位数量
        detail.setValue("gylxid", detailrow.get("gylxid"));
        detail.setValue("rwdmxid", detailrow.get("rwdmxid"));
        detail.setValue("cpl", detailrow.get("cpl"));//出品率
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("jgyq", detailrow.get("jgyq"));
        detail.setValue("jgdj", detailrow.get("jgdj"));//加工单价
        detail.setValue("jgje", detailrow.get("jgje"));//加工金额
        //保存用户自定义的字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("sl"));
        detail.next();
      }
      //保存加工单物料信息
      EngineDataSet dsMaterail = getMaterailTable();
      RowMap materailRow = null;
      dsMaterail.first();
      for(int k=0; k<dsMaterail.getRowCount(); k++)
      {
        materailRow = (RowMap)d_MaterailBill.get(k);
        //新添的记录
        if(isMasterAdd)
          dsMaterail.setValue("jgdid", jgdid);

        dsMaterail.setValue("cpid", materailRow.get("cpid"));
        dsMaterail.setValue("sl", materailRow.get("sl"));//保存计量单位数量
        dsMaterail.setValue("scsl", materailRow.get("scsl"));//保存生产单位数量
        dsMaterail.setValue("dmsxid", materailRow.get("dmsxid"));
        //保存用户自定义的字段
        FieldInfo[] fields = materailProducer.getBakFieldCodes();
        for(int t=0; t<fields.length; t++)
        {
          String fieldCode = fields[k].getFieldcode();
          dsMaterail.setValue(fieldCode, materailRow.get(fieldCode));
        }
        dsMaterail.post();
        //System.out.println(dsMaterail.getValue("jgdmxid"));
        dsMaterail.next();
      }

      //保存主表数据
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("rq", rowInfo.get("rq"));//日期
      ds.setValue("zsl", totalNum.toString());
      ds.setValue("describe", rowInfo.get("describe"));//加工说明
      boolean isAll = isAllMaterail();
      ds.setValue("rwdid", isAll ? rwdid : "");//任务单ID
      ds.setValue("scjhid", isAll ? scjhid : "");//生产计划ID
      ds.setValue("jglx", jglx);//加工类型
      ds.setValue("sfwjg", sfwjg);//是否外加工
      ds.setValue("dwtxid",rowInfo.get("dwtxid"));
      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      if(sfwjg.equals("1"))
        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jgdh"}, new String[]{"SELECT pck_base.billNextCode('sc_jgd','jgdh','a') from dual"}));
      else
        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jgdh"}, new String[]{"SELECT pck_base.billNextCode('sc_jgd','jgdh') from dual"}));
      ds.saveDataSets(new EngineDataSet[]{ds, detail, dsMaterail}, null);
      procDetaiLlSeq =-1;

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        jglx="1";
        initRowInfo(true, true, true);
        detail.empty();
        dsMaterail.empty();
        initRowInfo(false, true, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
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
      ArrayList list = new ArrayList(d_RowInfos.size());
      String cpid=null, dmsxid=null, gylxid=null,rwdmxid=null, unit=null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid = detailrow.get("cpid");
        dmsxid = detailrow.get("dmsxid");
        gylxid = detailrow.get("gylxid");
        rwdmxid = detailrow.get("rwdmxid");
        StringBuffer buf = new StringBuffer().append(rwdmxid).append(",").append(cpid).append(",").append(dmsxid).append(",").append(gylxid);
        unit = buf.toString();
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        if(list.contains(unit))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(unit);
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        if(sl.length()>0 && sl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");
        if(sfwjg.equals("1")){
          String jgdj = detailrow.get("jgdj");
          if((temp = checkNumber(sl, "第"+row+"行加工单价")) != null)
            return temp;
          String jgje = detailrow.get("jgje");
          if((temp = checkNumber(sl, "第"+row+"行加工金额")) != null)
            return temp;
        }
        String cpl = detailrow.get("cpl");
        if(cpl.length()>0 && (temp = checkNumber(cpl, "第"+row+"行出品率")) != null)
          return temp;
      }
      return null;
    }
    /**
    * 校验物料信息从表输入的信息的正确性
    * @return null 表示没有信息
    */
   private String checkMaterail()
   {
     String temp = null;
     RowMap materailrow = null;
     if(d_MaterailBill.size()<1)
       return showJavaScript("alert('不能保存空的数据')");
     String cpid=null, dmsxid=null, unit=null;
     for(int i=0; i<d_MaterailBill.size(); i++)
     {
       int row = i+1;
       materailrow = (RowMap)d_MaterailBill.get(i);
       cpid = materailrow.get("cpid");
       dmsxid = materailrow.get("dmsxid");
       if(cpid.equals(""))
         return showJavaScript("alert('第"+row+"行产品不能为空');");
       String sl = materailrow.get("sl");
       if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
         return temp;
       if(sl.length()>0 && sl.equals("0"))
         return showJavaScript("alert('第"+row+"行数量不能为零！');");
     }
     return null;
    }
    /**
    * 校验从表表单信息从表输入的信息中是否全部是引入任务单明细
    * @return true 只要有一条是引任务单明细过来的，忽略手工增加信息。同时任务单ID和生产计划ID保存入任务单主表
    * 如果全部是手工增加这不保存任务单计划ID和生产计划ID到任务单主表
    * 手工增加的信息将的不到物料
    */
    private boolean  isAllMaterail()
    {
      boolean isAllMaterail = false;
      RowMap detail = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detail = (RowMap)d_RowInfos.get(i);
        String rwdmxid = detail.get("rwdmxid");
        if(!rwdmxid.equals(""))
          return isAllMaterail=true;
      }
      return isAllMaterail;
    }

    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      String temp = rowInfo.get("rq");
      if(temp.equals(""))
        return showJavaScript("alert('日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法日期！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择车间！');");
      return null;
    }
  }

  /**
   * 主表保存操作的触发类
   * 通用加工单保存操作
   */
  class Common_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      //putCommonInfo(data.getRequest());

      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      //校验表单数据
      String temp = checkMasterInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      /**
      temp = checkCommonInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      */
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);

      //得到主表主键值
      String jgdid = null;
      if(isMasterAdd){
        ds.insertRow(false);
        jgdid = dataSetProvider.getSequence("s_sc_jgd");
        ds.setValue("jgdid", jgdid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
        ds.setValue("zt", "0");
      }
      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0);
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("jgdid", jgdid);

        if(detail.getBigDecimal("jgdmxid").intValue()<0 ){
          String id = dataSetProvider.getSequence("s_sc_jgdmx");
          detail.setValue("jgdmxid", id);
        }
        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("sl", detailrow.get("sl"));//保存计量单位数量
        detail.setValue("scsl", detailrow.get("scsl"));//保存生产单位数量
        detail.setValue("gylxid", detailrow.get("gylxid"));
        detail.setValue("rwdmxid", detailrow.get("rwdmxid"));
        detail.setValue("cpl", detailrow.get("cpl"));//出品率
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("jgyq", detailrow.get("jgyq"));
        detail.setValue("jgdj", detailrow.get("jgdj"));//加工单价
        detail.setValue("jgje", detailrow.get("jgje"));//加工金额
        //保存用户自定义的字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        //设置加工单物料的加工单ID
        dsDrawMaterail.first();
        for(int t=0;t<dsDrawMaterail.getRowCount();t++){
          if(isMasterAdd)
            dsDrawMaterail.setValue("jgdid", jgdid);
          //if(dsDrawMaterail.getBigDecimal("jgdmxid").intValue()<0)
            //dsDrawMaterail.setValue("jgdmxid", id);
          dsDrawMaterail.post();
          //System.out.println(dsDrawMaterail.getValue("jgdid"));
          //System.out.println("jgdmxid:"+dsDrawMaterail.getBigDecimal("jgdmxid").intValue());
          dsDrawMaterail.next();
        }
        totalNum = totalNum.add(detail.getBigDecimal("sl"));
        detail.next();
      }
      //保存加工单物料信息

      /**
      RowMap materailRow = null;
      dsMaterail.first();
      for(int k=0; k<dsMaterail.getRowCount(); k++)
      {
        materailRow = (RowMap)d_CommonMaterail.get(k);
        //新添的记录
        if(isMasterAdd)
          dsMaterail.setValue("jgdid", jgdid);

        dsMaterail.setValue("cpid", materailRow.get("cpid"));
        dsMaterail.setValue("sl", materailRow.get("sl"));//保存计量单位数量
        dsMaterail.setValue("scsl", materailRow.get("scsl"));//保存生产单位数量
        dsMaterail.setValue("dmsxid", materailRow.get("dmsxid"));
        //保存用户自定义的字段
        FieldInfo[] fields = materailProducer.getBakFieldCodes();
        for(int t=0; t<fields.length; t++)
        {
          String fieldCode = fields[k].getFieldcode();
          dsMaterail.setValue(fieldCode, materailRow.get(fieldCode));
        }
        dsMaterail.post();
        //System.out.println(dsMaterail.getValue("jgdmxid"));
        dsMaterail.next();
      }
      */

      //保存主表数据
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("rq", rowInfo.get("rq"));//日期
      ds.setValue("zsl", totalNum.toString());
      ds.setValue("describe", rowInfo.get("describe"));//加工说明
      boolean isAll = isAllMaterail();
      ds.setValue("rwdid", isAll ? rwdid : "");//任务单ID
      ds.setValue("scjhid", isAll ? scjhid : "");//生产计划ID
      ds.setValue("jglx", jglx);//加工类型
      ds.setValue("sfwjg", sfwjg);//是否外加工
      ds.setValue("dwtxid", rowInfo.get("dwtxid"));
      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      if(sfwjg.equals("1"))
        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jgdh"}, new String[]{"SELECT pck_base.billNextCode('sc_jgd','jgdh','a') from dual"}));
      else
        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jgdh"}, new String[]{"SELECT pck_base.billNextCode('sc_jgd','jgdh') from dual"}));
      ds.saveDataSets(new EngineDataSet[]{ds, detail, dsDrawMaterail}, null);
      procDetaiLlSeq =-1;

      if(String.valueOf(COMMONPOST_CONTINUE).equals(action)){
        isMasterAdd = true;
        jglx="0";
        initRowInfo(true, true, true);
        detail.empty();
        dsDrawMaterail.empty();
        initRowInfo(false, true, true);//重新初始化从表的各行信息
      }
      else if(String.valueOf(COMMON_POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
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
      ArrayList list = new ArrayList(d_RowInfos.size());
      String cpid=null, dmsxid=null, gylxid=null,rwdmxid=null, unit=null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid = detailrow.get("cpid");
        dmsxid = detailrow.get("dmsxid");
        gylxid = detailrow.get("gylxid");
        rwdmxid = detailrow.get("rwdmxid");
        StringBuffer buf = new StringBuffer().append(rwdmxid).append(",").append(cpid).append(",").append(dmsxid).append(",").append(gylxid);
        unit = buf.toString();
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        if(list.contains(unit))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(unit);
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        if(sl.length()>0 && sl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");
        if(sfwjg.equals("1")){
          String jgdj = detailrow.get("jgdj");
          if((temp = checkNumber(sl, "第"+row+"行加工单价")) != null)
            return temp;
          String jgje = detailrow.get("jgje");
          if((temp = checkNumber(sl, "第"+row+"行加工金额")) != null)
            return temp;
        }
        String cpl = detailrow.get("cpl");
        if(cpl.length()>0 && (temp = checkNumber(cpl, "第"+row+"行出品率")) != null)
          return temp;
      }
      return null;
    }
    /**
     * 校验物料信息从表输入的信息的正确性
     * @return null 表示没有信息

    private String checkCommonInfo()
    {
      String temp = null;
      RowMap materailrow = null;
      if(d_CommonMaterail.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      String cpid=null, dmsxid=null, unit=null;
      for(int i=0; i<d_CommonMaterail.size(); i++)
      {
        int row = i+1;
        materailrow = (RowMap)d_CommonMaterail.get(i);
        cpid = materailrow.get("cpid");
        dmsxid = materailrow.get("dmsxid");
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        String sl = materailrow.get("sl");
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
          return temp;
        if(sl.length()>0 && sl.equals("0"))
          return showJavaScript("alert('第"+row+"行数量不能为零！');");
      }
      return null;
    }
         */
    /**
     * 校验从表表单信息从表输入的信息中是否全部是引入任务单明细
     * @return true 只要有一条是引任务单明细过来的，忽略手工增加信息。同时任务单ID和生产计划ID保存入任务单主表
     * 如果全部是手工增加这不保存任务单计划ID和生产计划ID到任务单主表
     * 手工增加的信息将的不到物料
     */
    private boolean  isAllMaterail()
    {
      boolean isAllMaterail = false;
      RowMap detail = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detail = (RowMap)d_RowInfos.get(i);
        String rwdmxid = detail.get("rwdmxid");
        if(!rwdmxid.equals(""))
          return isAllMaterail=true;
      }
      return isAllMaterail;
    }

    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo()
    {
      RowMap rowInfo = getMasterRowinfo();
      String temp = rowInfo.get("rq");
      if(temp.equals(""))
        return showJavaScript("alert('日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法日期！');");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择车间！');");
      return null;
    }
  }

  /**
   * 主表删除操作
   */
  class Master_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(isMasterAdd){
        data.setMessage(showJavaScript("backList();"));
        return;
      }
      EngineDataSet ds = getMaterTable();
      ds.goToInternalRow(masterRow);
      dsProcessMaterail.deleteAllRows();
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable, dsProcessMaterail}, null);
      //
      d_RowInfos.clear();
      d_MaterailBill.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }
  /**
   * 主表删除操作
   */
  class CommonMaster_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(isMasterAdd){
        data.setMessage(showJavaScript("backList();"));
        return;
      }
      EngineDataSet ds = getMaterTable();
      ds.goToInternalRow(masterRow);
      dsDetailTable.first();
      for(int i=0;i<dsDetailTable.getRowCount();i++)
      {
        dsDrawMaterail.deleteAllRows();
        dsDetailTable.next();
      }
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable, dsDrawMaterail}, null);
      //
      d_RowInfos.clear();
      d_CommonMaterail.clear();
      data.setMessage(showJavaScript("backList();"));
    }
  }

  /**
   *  查询操作
   */
  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "zdrid"), sfwjg, fgsid, SQL});
      if(!dsMasterTable.getQueryString().equals(SQL))
      {
        dsMasterTable.setQueryString(SQL);
        dsMasterTable.setRowMax(null);
      }
    }

    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request)
    {
      if(isInitQuery)
        return;
      EngineDataSet master = dsMasterTable;
      EngineDataSet detail = dsMasterTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("jgdh"), null, null, null),
        new QueryColumn(master.getColumn("rq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("rq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门ID
        new QueryColumn(master.getColumn("jgdid"), "sc_jgdmx", "jgdid", "cpid", null, "="),//从表品名
        new QueryColumn(master.getColumn("jgdid"), "VW_SCJGD_QUERY", "jgdid", "cpbm", "cpbm", "like"),//从表产品编码
        new QueryColumn(master.getColumn("jgdid"), "VW_SCJGD_QUERY", "jgdid", "product", "product", "like"),//从表品名
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }
  /**
  *  根据生产任务单从表增加操作
  */
 class Detail_Select_Task implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     //保存输入的明细信息
     putDetailInfo(data.getRequest());
     RowMap rowInfo = getMasterRowinfo();

     String mutitask = m_RowInfo.get("mutitask");
     if(mutitask.length() == 0)
       return;

     //实例化查找数据集的类
     EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "rwdmxid");
     String[] rwdmxID = parseString(mutitask,",");
     if(!isMasterAdd)
        dsMasterTable.goToInternalRow(masterRow);
       String jgdid = dsMasterTable.getValue("jgdid");
     for(int i=0; i < rwdmxID.length; i++)
     {
       if(rwdmxID[i].equals("-1"))
         continue;
       RowMap detailrow = null;
       locateGoodsRow.setValue(0, rwdmxID[i]);
       if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
       {
         RowMap importTaskRow = getTaskGoodsBean(req).getLookupRow(rwdmxID[i]);
         dsDetailTable.insertRow(false);
         dsDetailTable.setValue("jgdmxid", "-1");
         dsDetailTable.setValue("rwdmxid",rwdmxID[i]);
         dsDetailTable.setValue("cpid", importTaskRow.get("cpid"));
         dsDetailTable.setValue("gylxid", importTaskRow.get("gylxid"));
         dsDetailTable.setValue("sl", importTaskRow.get("wjgl"));
         dsDetailTable.setValue("scsl", importTaskRow.get("wjgscl"));
         dsDetailTable.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
         String dmsxid = importTaskRow.get("dmsxid");
         dsDetailTable.setValue("dmsxid", importTaskRow.get("dmsxid"));
         dsDetailTable.setValue("jgyq", importTaskRow.get("jgyq"));
         dsDetailTable.post();
         //创建一个与用户相对应的行
         detailrow = new RowMap(dsDetailTable);
         d_RowInfos.add(detailrow);
       }
     }
   }
  }
  /**
  *  选择任务单主表，引入从表所有未加工信息
  */
 class Single_Select_Task implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     //保存输入的明细信息
     putDetailInfo(data.getRequest());
     RowMap rowInfo = getMasterRowinfo();

     String singleImportTask = m_RowInfo.get("singleImportTask");
     if(singleImportTask.equals(""))
       return;
     String SQL = TASK_DETAIL_SQL+singleImportTask;
     EngineDataSet tempTaskData = null;
     if(tempTaskData==null)
     {
       tempTaskData = new EngineDataSet();
       setDataSetProperty(tempTaskData,null);
     }
     tempTaskData.setQueryString(SQL);
     if(!tempTaskData.isOpen())
       tempTaskData.openDataSet();
     else
       tempTaskData.refresh();

     EngineDataSet detail = getDetailTable();
     detail.deleteAllRows();
     d_RowInfos.clear();
     RowMap taskMasterRow = getTaskMasterBean(req).getLookupRow(singleImportTask);
     rowInfo.put("deptid", taskMasterRow.get("deptid"));
     scjhid = taskMasterRow.get("scjhid");
     rwdid = singleImportTask;
     //实例化查找数据集的类
     EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "rwdmxid");
     if(!isMasterAdd)
       dsMasterTable.goToInternalRow(masterRow);
     String jgdid = dsMasterTable.getValue("jgdid");
     StringBuffer rwdmxidBuf = new StringBuffer();
     StringBuffer cpidBuf = new StringBuffer();
     tempTaskData.first();
     for(int i=0; i<tempTaskData.getRowCount(); i++)
     {
       tempTaskData.goToRow(i);
       String rwdmxid = tempTaskData.getValue("rwdmxid");
       String cpid = tempTaskData.getValue("cpid");
       String dmsxid = tempTaskData.getValue("dmsxid");
       locateGoodsRow.setValue(0, rwdmxid);
       if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
       {
         double sl = tempTaskData.getValue("sl").length()>0 ? Double.parseDouble(tempTaskData.getValue("sl")) : 0;//需求任务中需要的数量
         double yjgl = tempTaskData.getValue("yjgl").length()>0 ? Double.parseDouble(tempTaskData.getValue("yjgl")) : 0;//需求任务中已加工量
         double scsl = tempTaskData.getValue("scsl").length()>0 ? Double.parseDouble(tempTaskData.getValue("scsl")) : 0;//需求任务中需要的生产数量
         double yjgscl = tempTaskData.getValue("yjgscl").length()>0 ? Double.parseDouble(tempTaskData.getValue("yjgscl")) : 0;//需求任务中已加工生产量
         double wjgl = sl-yjgl>0 ? sl-yjgl : 0;
         double wjgscl = scsl-yjgscl>0 ? scsl-yjgscl :0;
         if(wjgl==0)
           continue;
         detail.insertRow(false);
         if(jglx.equals("1"))
           detail.setValue("jgdmxid", "-1");
         else
           detail.setValue("jgdmxid", String.valueOf(procDetaiLlSeq));
         detail.setValue("rwdmxid",rwdmxid);
         detail.setValue("cpid", cpid);
         detail.setValue("sl", String.valueOf(wjgl));
         detail.setValue("scsl", String.valueOf(wjgscl));
         detail.setValue("gylxid", tempTaskData.getValue("gylxid"));
         detail.setValue("dmsxid", tempTaskData.getValue("dmsxid"));//规格属性
         detail.setValue("jgyq", tempTaskData.getValue("jgyq"));//加工要求
         detail.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
         if(i==tempTaskData.getRowCount()-1){
             rwdmxidBuf = rwdmxidBuf.append("'").append(rwdmxid).append("'");
             cpidBuf = cpidBuf.append("'").append(cpid).append("'");
         }
         else{
           rwdmxidBuf = rwdmxidBuf.append("'").append(rwdmxid).append("',");
           cpidBuf = cpidBuf.append("'").append(cpid).append("',");
         }
         detail.post();
         //创建一个与用户相对应的行
         RowMap detailrow = new RowMap(detail);
         d_RowInfos.add(detailrow);
         if(!jglx.equals("1")){
           String temp = null;
           if(dmsxid.equals(""))
             temp =" AND b.dmsxid IS NULL";
           else
             temp = " AND b.dmsxid ="+ dmsxid;
           String M_SQL = combineSQL(MATERAIL_LIST_SQL, "?", new String[]{rwdmxid, scjhid, cpid, temp});
           dsFactBomMaterail.setQueryString(M_SQL);
           if(!dsFactBomMaterail.isOpen())
             dsFactBomMaterail.openDataSet();
           else
             dsFactBomMaterail.refresh();
           int row = dsFactBomMaterail.getRowCount();
           dsFactBomMaterail.first();

           for(int t=0; t<dsFactBomMaterail.getRowCount(); t++){
             dsDrawMaterail.insertRow(false);
             dsDrawMaterail.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
             dsDrawMaterail.setValue("jgdwlid", "-1");
             dsDrawMaterail.setValue("dmsxid", dsFactBomMaterail.getValue("dmsxid"));
             dsDrawMaterail.setValue("cpid", dsFactBomMaterail.getValue("cpid"));
             dsDrawMaterail.setValue("sl", dsFactBomMaterail.getValue("xql"));
             dsDrawMaterail.setValue("scsl", dsFactBomMaterail.getValue("scxql"));
             dsDrawMaterail.setValue("jgdmxid", String.valueOf(procDetaiLlSeq));
             dsDrawMaterail.post();
             RowMap Materailrow = new RowMap(dsProcessMaterail);
             d_CommonMaterail.add(Materailrow);
             dsFactBomMaterail.next();
           }
         }
         if(!jglx.equals("1"))
           procDetaiLlSeq--;
         tempTaskData.next();
       }
     }
     if(jglx.equals("1")){
       String BOM_SQL = combineSQL(SUBMATERAIL_LIST_SQL, "?", new String[]{rwdmxidBuf.toString(), scjhid, cpidBuf.toString()});
       dsFactBomMaterail.setQueryString(BOM_SQL);
       if(dsFactBomMaterail.isOpen())
         dsFactBomMaterail.refresh();
       else
         dsFactBomMaterail.openDataSet();
       dsFactBomMaterail.first();
       for(int t=0; t<dsFactBomMaterail.getRowCount(); t++){
         dsProcessMaterail.insertRow(false);
         dsProcessMaterail.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
         dsProcessMaterail.setValue("jgdwlid", "-1");
         dsProcessMaterail.setValue("dmsxid", dsFactBomMaterail.getValue("dmsxid"));
         dsProcessMaterail.setValue("cpid", dsFactBomMaterail.getValue("cpid"));
         dsProcessMaterail.setValue("sl", dsFactBomMaterail.getValue("xql"));
         dsProcessMaterail.setValue("scsl", dsFactBomMaterail.getValue("scxql"));
         //dsProcessMaterail.setValue("jgdmxid", String.valueOf(procDetaiLlSeq));
         dsProcessMaterail.post();
         RowMap Materailrow = new RowMap(dsProcessMaterail);
         d_MaterailBill.add(Materailrow);
         dsFactBomMaterail.next();
       }
     }
   }
 }
  /**
   *改变车间触发的事件
   */
 class Onchange implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     putDetailInfo(data.getRequest());
     EngineDataSet detail = getDetailTable();
      detail.first();
      while(detail.inBounds())
      {
        String rwdmxid = detail.getValue("rwdmxid");
        if(!rwdmxid.equals(""))
        {
          d_RowInfos.remove(detail.getRow());
          detail.deleteRow();
        }
        else
          detail.next();
      }
   }
 }
 /**
  *改变车间触发的事件
  */
 class Detail_Refresh implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     putDetailInfo(data.getRequest());
   }
 }
 /**
  *输入产品编码触发的事件
  */
 class Product_Onchange implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     putDetailInfo(data.getRequest());
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
     if(!jglx.equals("1"))
       putCommonInfo(data.getRequest());
     EngineDataSet detail = getDetailTable();
     EngineDataSet ds = getMaterTable();
     isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
     if(!isMasterAdd)
       ds.goToInternalRow(masterRow);
     String jgdid = dsMasterTable.getValue("jgdid");
     detail.insertRow(false);
     //String jgdmxid = dataSetProvider.getSequence("s_sc_jgdmx");
     if(!jglx.equals("1")){
       detail.setValue("jgdmxid", String.valueOf(procDetaiLlSeq));
       procDetaiLlSeq--;
     }
     else
       detail.setValue("jgdmxid", "-1");
     detail.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
     detail.post();
     d_RowInfos.add(new RowMap());
   }
 }
 /**
  *  增加分切加工单才有的功能
  *  加工单物料清单增加操作(增加一个空白行)
  */
 class Materail_Add implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     //保存输入的明细信息
     putDetailInfo(data.getRequest());
     EngineDataSet dsMaterail = getMaterailTable();
     EngineDataSet ds = getMaterTable();
     if(!isMasterAdd)
       ds.goToInternalRow(masterRow);
     String jgdid = dsMasterTable.getValue("jgdid");
     dsMaterail.insertRow(false);
     dsMaterail.setValue("jgdid", isMasterAdd ? "-1" : jgdid);
     dsMaterail.post();
     d_MaterailBill.add(new RowMap());
   }
 }
 /**
  *  分切加工单才有的功能
  *  物料删除操作
  */
 class Materail_Delete implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     putDetailInfo(data.getRequest());
     EngineDataSet dsMaterail = getMaterailTable();
     int rownum = Integer.parseInt(data.getParameter("rownum"));
     //删除临时数组的一列数据
     d_MaterailBill.remove(rownum);
     dsMaterail.goToRow(rownum);
     dsMaterail.deleteRow();
   }
 }
 /**
  *  强制完成触发事件
  *  根据已排工作量和加工数量手工完成操作
  */
 class Complete implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     int row = Integer.parseInt(data.getParameter("rownum"));
     dsMasterTable.goToRow(row);
     dsMasterTable.setValue("zt", "8");
     dsMasterTable.post();
     dsMasterTable.saveChanges();
   }
 }
 /**
  *  从表增加操作
  */
 class Detail_Delete implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     putDetailInfo(data.getRequest());
     EngineDataSet ds = getDetailTable();
     int rownum = Integer.parseInt(data.getParameter("rownum"));
     //删除临时数组的一列数据
     d_RowInfos.remove(rownum);
     ds.goToRow(rownum);
     ds.deleteRow();
   }
 }
 /**
  * 得到用于查找生产任务单信息的bean
  * @param req WEB的请求
  * @return 返回用于查找生产任务单信息的bean
  */
 public ImportTask getTaskGoodsBean(HttpServletRequest req)
 {
   if(importTaskBean == null)
     importTaskBean = ImportTask.getInstance(req);
   return importTaskBean;
 }
 /**
  * 得到用于查找生产任务单主表的一条信息的bean
  * @param req WEB的请求
  * @return 返回用于查找生产任务单主表的一条信息的bean
  */
 public B_Process_SingleSelTask getTaskMasterBean(HttpServletRequest req)
 {
   if(singleSelTaskBean == null)
     singleSelTaskBean = B_Process_SingleSelTask.getInstance(req);
   return singleSelTaskBean;
 }
}
