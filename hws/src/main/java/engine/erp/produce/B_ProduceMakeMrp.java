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
 * <p>Title: 生产计划页面里生成物料需求计划</p>
 * <p>Description: 生产计划页面里下达物料需求计划<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class B_ProduceMakeMrp extends BaseAction implements Operate
{
  public  static final String SUMIT_PROPERTY = "1009";//选择规格属性提交
  public  static final String DETAIL_ADD_BLANK = "11131";
  public  static final String SINGLE_PRODUCT_ADD = "10631";
  public  static final String PRODUCT_ONCHANGE = "10002";//在从表中输入产品编码触发事件

  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_wlxqjh WHERE 1<>1";
  private static final String MASTER_SQL    = "SELECT * FROM sc_wlxqjh WHERE fgsid=? and scjhid=? ";
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM sc_wlxqjhmx WHERE 1<>1";
  private static final String DETAIL_SQL = "SELECT * FROM sc_wlxqjhmx WHERE wlxqjhid='?' ORDER BY cc,cpid ";
  private static final String UPDATE_SQL = "UPDATE sc_jh SET zt='2' WHERE scjhid= ";//下达物料需求后update生产计划的状态
  private static final String PLAN_DETAIL_SQL = "SELECT * FROM sc_jhmx WHERE scjhid= ";//通过生产计划ID得到计划明细并得到明细是否生成实际BOM
  private static final String BUILD_FACT_MRP = "{CALL pck_produce.buildFactBOMdata(@,'@',@,'@',@,'@',@)}";//生成实际BOM调用存储过程
  private static final String UPDATE_PLAN_SQL = "UPDATE　sc_jhmx SET sfsc='1' WHERE scjhmxid= ";//UPDATE生产计划明细的sfsc实际BOM子段
  private static final String GET_MRP_DATA = "{CALL pck_produce.getMRPdata(?,@)}";

  //抽取客户实际BOM表数据SQL语句
  private static final String BOM_STRUT_SQL = "SELECT * FROM sc_sjbom WHERE 1<>1";
  private static final String BOM_SQL    = "SELECT * FROM sc_sjbom WHERE scjhmxid=? ORDER BY cc,cpid";
  private EngineDataSet dsFactBom = new EngineDataSet();//客户实际BOM数据集

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表

  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "sc_wlxqjh");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "sc_wlxqjhmx");
  public boolean isMasterAdd = true;    //是否在添加状态
  private int    masterRow = -1;         //保存生产计划主表操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private LookUp productBean = null; //产品的bean的引用, 用于提取产品
  private LookUp planUseAbleBean = null; //产品的bean的引用, 用于提取产品生产单位
  private LookUp propertyBean = null; //产品信息的bean的引用, 用于提取产品信息


  private EngineDataSet mrpdata = null;

  public  String retuUrl = null;

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private String wlxqjhid = null;
  private String scjhid = null;
  private String deptid = null;
  private String jhlx = null;//计划类型
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  /**
   * 物料需求计划列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回物料需求计划列表的实例
   */
  public static B_ProduceMakeMrp getInstance(HttpServletRequest request)
  {
    B_ProduceMakeMrp produceMakeMrpBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "produceMakeMrpBean";
      produceMakeMrpBean = (B_ProduceMakeMrp)session.getAttribute(beanName);
      if(produceMakeMrpBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        produceMakeMrpBean = new B_ProduceMakeMrp();
        produceMakeMrpBean.qtyFormat = loginBean.getQtyFormat();

        produceMakeMrpBean.fgsid = loginBean.getFirstDeptID();
        produceMakeMrpBean.loginId = loginBean.getUserID();
        produceMakeMrpBean.loginName = loginBean.getUserName();
        produceMakeMrpBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        //设置格式化的字段
        produceMakeMrpBean.dsDetailTable.setColumnFormat("xql", produceMakeMrpBean.qtyFormat);
        produceMakeMrpBean.dsMasterTable.setColumnFormat("xgl", produceMakeMrpBean.qtyFormat);
        session.setAttribute(beanName, produceMakeMrpBean);
      }
    }
    return produceMakeMrpBean;
  }

  /**
   * 构造函数
   */
  private B_ProduceMakeMrp()
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
    setDataSetProperty(dsFactBom, BOM_STRUT_SQL);

    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"wlxqh"}, new String[]{"SELECT pck_base.billNextCode('sc_wlxqjh','wlxqh') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"wlxqh"}, new boolean[]{true}, null, 0));

    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"wlxqjhmxid"}, new String[]{"s_sc_wlxqjhmx"}));
    dsDetailTable.setSort(new SortDescriptor("", new String[]{"cc","cpid"}, new boolean[]{false,false}, null, 0));
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(POST), new Master_Post());
    addObactioner(String.valueOf(SUMIT_PROPERTY), new Sumit_Property());
    addObactioner(String.valueOf(DETAIL_ADD_BLANK), new Detail_Add_Blank());//从表增加一空白行操作
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(SINGLE_PRODUCT_ADD), new Single_Product_Add());//从表选择单个产品增加操作
    addObactioner(String.valueOf(PRODUCT_ONCHANGE), new Product_Onchange());
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
    if(mrpdata != null){
      mrpdata.close();
      mrpdata = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
    mrpdata = null;
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
        m_RowInfo.put("rq", today);//计划日期
        m_RowInfo.put("zdrid", loginId);
        m_RowInfo.put("scjhid", scjhid);
        m_RowInfo.put("deptid", deptid);
      }
    }
    else
    {
      EngineDataSet dsDetail = dsDetailTable;
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

    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//产品id
      detailRow.put("xql", rowInfo.get("xql_"+i));//生产单位需求量
      detailRow.put("xgl", rowInfo.get("xgl_"+i));//需购量
      detailRow.put("jlxql", rowInfo.get("jlxql_"+i));//计量需求量
      detailRow.put("xqrq", rowInfo.get("xqrq_"+i));//需求日期
      detailRow.put("bz", rowInfo.get("bz_"+i));//备注
      detailRow.put("chxz", rowInfo.get("chxz_"+i));//存货性质
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//物资规格属性ID
      //保存用户自定义的字段
      FieldInfo[] fields = detailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
    }
  }

  /**
   *  从表输入产品编码触发操作
   */
  class Product_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      int row = Integer.parseInt(data.getParameter("rownum"));
      dsDetailTable.goToRow(row);
      RowMap detail=(RowMap)d_RowInfos.get(row);
      String cpid = detail.get("cpid");
      RowMap productRow = getProductBean(req).getLookupRow(cpid);
      long  ztqq = productRow.get("ztqq").length()>0 ? Long.parseLong(productRow.get("ztqq")) : 0;//总提前期
      Date startdate = new Date();
      Date enddate = new Date(startdate.getTime() + ztqq*60*60*24*1000);
      String endDate = new SimpleDateFormat("yyyy-MM-dd").format(enddate);
      detail.put("xqrq", endDate);
    }
  }

  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /*得到从表表对象*/
  public final EngineDataSet getDetailTable(){
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    return dsDetailTable;
  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }

  /**
   * 主表是否在添加状态
   * @return 是否在添加状态
   */
  public final boolean masterIsAdd() {return isMasterAdd; }

  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    private EngineDataSet dsPlanDetail = null;
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //
      scjhid = request.getParameter("scjhid");
      deptid = request.getParameter("deptid");
      jhlx = request.getParameter("jhlx");
      masterRow = Integer.parseInt(request.getParameter("rownum"));
      String SQL = combineSQL(MASTER_SQL, "?", new String[]{fgsid, scjhid});
      dsMasterTable.setQueryString(SQL);
      if(dsMasterTable.isOpen())
        dsMasterTable.refresh();
      else
        dsMasterTable.open();
      if(dsMasterTable.getRowCount() > 0)//通过参数生产计划ID得出已经生成物料需求
      {
        isMasterAdd=false;
        wlxqjhid = dsMasterTable.getValue("wlxqjhid");
        String detailSql = combineSQL(DETAIL_SQL, "?", new String[]{wlxqjhid});
        dsDetailTable.setQueryString(detailSql);
        if(dsDetailTable.isOpen())
          dsDetailTable.refresh();
        else
          dsDetailTable.open();
      }
      else {                     //没有生成物料需求
        isMasterAdd = true;
        dsDetailTable.setQueryString(DETAIL_STRUT_SQL);
        if(dsDetailTable.isOpen())
          dsDetailTable.refresh();
        else
          dsDetailTable.open();
        /**
         * 通过生产计划ID得到生产计划明细数据集
         * 如果计划明细中有没有生成实际BOM的纪录，即!sfsc=1;则调用存储过程生成实际BOM
         */
        if(dsPlanDetail == null)
        {
          dsPlanDetail = new EngineDataSet();
          setDataSetProperty(dsPlanDetail, null);
        }
        dsPlanDetail.setQueryString(PLAN_DETAIL_SQL+scjhid);
        if(!dsPlanDetail.isOpen())
          dsPlanDetail.openDataSet();
        else
          dsPlanDetail.refresh();
        dsPlanDetail.first();
        int count = dsPlanDetail.getRowCount();
        for(int k=0; k<dsPlanDetail.getRowCount(); k++)
        {
          dsPlanDetail.goToRow(k);
          String scjhmxid = dsPlanDetail.getValue("scjhmxid");
          String hthwid = dsPlanDetail.getValue("hthwid");//销售合同货物ID
          String ksrq = dsPlanDetail.getValue("ksrq");
          String cpid = dsPlanDetail.getValue("cpid");
          String p_dmsxid = dsPlanDetail.getValue("dmsxid");
          String xql = dsPlanDetail.getValue("sl");//计划明细数量
          String sfsc = dsPlanDetail.getValue("sfsc");
          if(!sfsc.equals("1"))
          {
            String sql = combineSQL(BUILD_FACT_MRP, "@", new String[]{scjhmxid,hthwid,cpid,p_dmsxid,xql,ksrq,String.valueOf(count)});
            dsMasterTable.updateQuery(new String[]{sql, UPDATE_PLAN_SQL + scjhmxid});
            String FACT_SQL = combineSQL(BOM_SQL, "?", new String[]{scjhmxid});
            dsFactBom.setQueryString(FACT_SQL);
            if(dsFactBom.isOpen())
              dsFactBom.refresh();
            else
              dsFactBom.open();
            dsFactBom.first();
            String b_cpid=null,b_xql=null, dmsxid=null, sxz=null, width=null,scdwgs=null, scsl=null, scxql=null;
            double d_xql=0, d_width=0, d_scdwgs=0, d_scsl=0;
            RowMap prodRow =null;
            for(int i=0; i< dsFactBom.getRowCount(); i++)
            {
              scxql = dsFactBom.getValue("scxql");//存储过程返回的生产需求量
              //如果返回不为空就不用继续计算了
              if(!scxql.equals(""))
              {
                dsFactBom.next();
                continue;
              }
              b_cpid = dsFactBom.getValue("cpid");
              b_xql = dsFactBom.getValue("xql");
              dmsxid = dsFactBom.getValue("dmsxid");
              prodRow = getPropertyBean(data.getRequest()).getLookupRow(b_cpid);
              scdwgs = prodRow.get("scdwgs");
              sxz = getPropertyBean(data.getRequest()).getLookupName(dmsxid);
              width = BasePublicClass.parseEspecialString(sxz,SYS_PRODUCT_SPEC_PROP, "()");
              d_xql = b_xql.length()>0 ? Double.parseDouble(b_xql) : 0;
              d_width = width.equals("0") ? 1 : Double.parseDouble(width);
              d_scdwgs = scdwgs.length()>0 ? Double.parseDouble(scdwgs) : 1;
              if(d_width==1)
                d_scsl = d_xql;
              else
                d_scsl=d_xql*d_scdwgs/d_width;
              scsl = formatNumber(String.valueOf(d_scsl), qtyFormat);
              dsFactBom.setValue("scxql", scsl);
              dsFactBom.post();
              //System.out.println(dsPlanDetail.getValue("scjhmxid")+":"+dsFactBom.getRow()+":"+scsl);
              dsFactBom.next();
            }
            //System.out.println("数据集："+dsFactBom.getRowCount());
            dsFactBom.saveChanges();
          }
          dsPlanDetail.next();
        }
        /**
         * 计划明细都生成实际BOM后下达总的物料需求
         * 调用存储过程返回数据集mrpData
         */
        String sql = combineSQL(GET_MRP_DATA, "@", new String[]{scjhid});
        if(mrpdata == null)
        {
          mrpdata = new EngineDataSet();
          setDataSetProperty(mrpdata, null);
        }
        mrpdata.setQueryString(sql);
        if(!mrpdata.isOpen())
          mrpdata.openDataSet();
        else
          mrpdata.refresh();

        /**
         * 把返回的mrpData数据集添加到从表数据集
         */
        EngineRow row = new EngineRow(dsDetailTable,
                                      new String[]{"scjhmxID","htID","cpID","dmsxid", "gylxID", "xql","jlxql","xqrq","cc","chxz"});
        mrpdata.first();
        for(int i=0; i<mrpdata.getRowCount(); i++){
          mrpdata.copyTo(row);
          dsDetailTable.addRow(row);
          mrpdata.next();
        }
        //---
        //EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "cpid");
        int rowCount=dsDetailTable.getRowCount();
        Hashtable table = new Hashtable(rowCount+1,1);//new出一个HashTable，下达物料需求时产品相同时用到
        LookUp look = getPlanUseAbleBean(data.getRequest());
        look.regData(dsDetailTable, new String[]{"cpid", "dmsxid"});
        if(!isMasterAdd)
          wlxqjhid = dsMasterTable.getValue("wlxqjhid");
        dsDetailTable.first();
        for(int j=0; j<rowCount; j++){
          dsDetailTable.setValue("wlxqjhid", isMasterAdd ? "-1" : wlxqjhid);
          String cpid = dsDetailTable.getValue("cpid");
          String dmsxid = dsDetailTable.getValue("dmsxid");
          String kgl = getPlanUseAbleBean( data.getRequest()).getLookupName(new String[]{cpid, dmsxid});//通过cpid和dmsxid得到计划可供量
          String xql = dsDetailTable.getValue("xql");
          String jlxql = dsDetailTable.getValue("jlxql");
          BigDecimal curValue = isDouble(jlxql) ? new BigDecimal(jlxql) : new BigDecimal(0);
          BigDecimal total = (BigDecimal)table.get(cpid);//从HashTable中得到现在下达的cpid得值
          if(total == null)//如果为空就设为当前值
            total = curValue;
          else//如果已经推入Hash中就叠加
            total = total.add(curValue);
          table.put("cpid",total);
          double jhkgl = kgl.length()>0 ? Double.parseDouble(kgl) : 0;
          if(jhkgl<0)
            jhkgl=0;
          BigDecimal jhkglVal = new BigDecimal(jhkgl);
          BigDecimal testsl = jhkglVal.subtract(total).doubleValue()>0 ? new BigDecimal(0) : total.subtract(jhkglVal);
          dsDetailTable.setValue("xgl", formatNumber(String.valueOf(testsl), qtyFormat));
          dsDetailTable.next();
        }
        initRowInfo(false, false, false);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
    }
  }
  /**
   *  从表增加操作(增加一个空白行)
   */
  class Detail_Add_Blank implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      EngineDataSet detail = getDetailTable();
      String wlxqjhid = null;
      if(!isMasterAdd)
         wlxqjhid = dsMasterTable.getValue("wlxqjhid");
      detail.insertRow(false);
      detail.setValue("wlxqjhid", isMasterAdd ? "-1" : wlxqjhid);
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
      EngineDataSet ds = getDetailTable();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      //删除临时数组的一列数据
      d_RowInfos.remove(rownum);
      ds.goToRow(rownum);
      ds.deleteRow();
    }
  }
  /**
  * 从表增加操作
  */
 class Single_Product_Add implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     //保存输入的明细信息
     putDetailInfo(data.getRequest());
     int row = Integer.parseInt(data.getParameter("rownum"));
     String singleIdInput = m_RowInfo.get("singleIdInput_"+row);
     if(singleIdInput.equals(""))
       return;

     //实例化查找数据集的类
     String cpid = singleIdInput;
     EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "cpid");
     if(!isMasterAdd)
       dsMasterTable.goToInternalRow(masterRow);
     String wlxqjhid = dsMasterTable.getValue("wlxqjhid");
     dsDetailTable.goToRow(row);
     RowMap detailrow = null;
     detailrow = (RowMap)d_RowInfos.get(row);
     locateGoodsRow.setValue(0, cpid);
     if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
     {
       detailrow.put("wlxqjhmxid", "-1");
       detailrow.put("cpid", cpid);
       RowMap productRow = getProductBean(req).getLookupRow(cpid);
       long  ztqq = productRow.get("ztqq").length()>0 ? Long.parseLong(productRow.get("ztqq")) : 0;//总提前期
       Date startdate = new Date();
       Date enddate = new Date(startdate.getTime() + ztqq*60*60*24*1000);
       String endDate = new SimpleDateFormat("yyyy-MM-dd").format(enddate);
       detailrow.put("xqrq", endDate);
       detailrow.put("wlxqjhid", isMasterAdd ? "-1" : wlxqjhid);
     }
   }
 }
  /**
   * 选择规格属性的触发类
   */
  class Sumit_Property implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      int num = Integer.parseInt(data.getParameter("rownum"));
      RowMap detailrow = (RowMap)d_RowInfos.get(num);
      String olddmsxid = detailrow.get("dmsxid");
      putDetailInfo(data.getRequest());
      RowMap  rowinfo = (RowMap)d_RowInfos.get(num);
      String dmsxid = rowinfo.get("dmsxid");
      if(olddmsxid.equals(dmsxid))
        return;
      String jlxql = rowinfo.get("jlxql");
      String xql = rowinfo.get("xql");
      String jhkgl = rowinfo.get("jhkgl");
      if(jlxql.equals("") && xql.equals(""))
        return;
      String cpid = rowinfo.get("cpid");
      RowMap productRow = getProductBean(data.getRequest()).getLookupRow(cpid);
      String scdwgs = productRow.get("scdwgs");
      String sxz = getPropertyBean(data.getRequest()).getLookupName(dmsxid);
      String width = BasePublicClass.parseEspecialString(sxz, SYS_PRODUCT_SPEC_PROP, "()");
      double d_scdwgs = scdwgs.length()>0 ? Double.parseDouble(scdwgs) : 0;
      double d_width = width.equals("0") ? 1 : Double.parseDouble(width);
      double d_jlxql = jlxql.length()>0 ? Double.parseDouble(jlxql) : 0;
      double d_xql = xql.length()>0 ? Double.parseDouble(xql) : 0;
      double d_jhkgl = jhkgl.length()>0 ? Double.parseDouble(jhkgl) : 0;
      if(jlxql.length()>0)
      {
        if(d_jhkgl<=0)
          rowinfo.put("xgl", jlxql);
        else if(d_jhkgl > d_jhkgl)
          rowinfo.put("xgl", "0");
        else if(d_jhkgl <= d_jlxql)
          rowinfo.put("xgl", String.valueOf(d_jlxql-d_jhkgl));
        rowinfo.put("xql", d_width==0 ? jlxql : formatNumber( String.valueOf(d_scdwgs==0 ? d_jlxql : d_jlxql*d_scdwgs/d_width), qtyFormat));
      }
      else if(xql.length()>0)
      {
        String temp = d_width==0 ? xql : formatNumber(String.valueOf(d_scdwgs==0 ? d_xql : d_xql*d_width/d_scdwgs), qtyFormat);
        rowinfo.put("jlxql", temp);
        double d_temp = temp.length()>0 ? Double.parseDouble(temp) : 0;
        if(d_jhkgl >= d_temp)
          rowinfo.put("xgl", "0");
        else
          rowinfo.put("xgl", String.valueOf(d_temp-d_jhkgl));
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
      if(!isMasterAdd)
        wlxqjhid = dsMasterTable.getValue("wlxqjhid");
      //得到主表主键值
      if(isMasterAdd){
        ds.insertRow(false);
        wlxqjhid = dataSetProvider.getSequence("s_sc_wlxqjh");
        ds.setValue("wlxqjhid", wlxqjhid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zt","0");
        ds.setValue("zdrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("zdrid", loginId);
        ds.setValue("zdr", loginName);//操作员
      }
      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      detail.first();
      for(int i=0; i<detail.getRowCount(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("wlxqjhid", wlxqjhid);


        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("xql", detailrow.get("xql"));//生产单位需求量
        detail.setValue("xgl", detailrow.get("xgl"));//需购量
        detail.setValue("jlxql", detailrow.get("jlxql"));//计量单位需求量
        detail.setValue("xqrq", detailrow.get("xqrq"));
        detail.setValue("bz", detailrow.get("bz"));//
        detail.setValue("cc", detailrow.get("cc"));//
        detail.setValue("chxz", detailrow.get("chxz"));//超产率
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("gylxid", detailrow.get("gylxid"));
        detail.setValue("scjhmxid", detailrow.get("scjhmxid"));//生差计划明细ID
        detail.setValue("htid", detailrow.get("htid"));//合同ID
        //保存用户自定义的字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        detail.next();
      }

      //保存主表数据
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      ds.setValue("scjhid", scjhid);//生产计划id
      ds.setValue("rq", rowInfo.get("rq"));//日期
      ds.setValue("ztms", rowInfo.get("ztms"));//状态描述
      ds.setValue("jhlx", jhlx);
      //保存用户自定义的字段
      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
      ds.post();
      B_ProducePlan producePlanBean = B_ProducePlan.getInstance(data.getRequest());
      EngineDataSet dsPlanMaster = producePlanBean.getMaterTable();
      dsPlanMaster.goToRow(masterRow);
      String zt = dsPlanMaster.getValue("zt");
      if(zt.equals("1"))
        dsPlanMaster.setValue("zt", "2");
      dsPlanMaster.post();
      //ds.setAfterResolvedSQL(new String[]{UPDATE_SQL + scjhid});
      ds.saveDataSets(new EngineDataSet[]{dsPlanMaster, ds, detail});
      data.setMessage(showJavaScript("refresh();"));
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
      String cpid=null, dmsxid=null, gylxid=null, unit=null, htid=null, scjhmxid=null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        cpid = detailrow.get("cpid");
        dmsxid = detailrow.get("dmsxid");
        gylxid = detailrow.get("gylxid");
        htid = detailrow.get("htid");
        scjhmxid = detailrow.get("scjhmxid");
        StringBuffer buf = new StringBuffer().append(htid).append(",").append(scjhmxid).append(",").append(cpid).append(",").append(dmsxid).append(",").append(gylxid);
        unit = buf.toString();
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        if(list.contains(unit))
          return showJavaScript("alert('第"+row+"行产品重复');");
        else
          list.add(unit);
        String xql = detailrow.get("xql");//生产单位需求量
        if(xql.equals(""))
          return showJavaScript("alert('第"+row+"行生产需求量不能为空');");
        if((temp = checkNumber(xql, detailProducer.getFieldInfo("xql").getFieldname())) != null)
          return temp;
        String jlxql = detailrow.get("jlxql");//计量单位需求量
        if(jlxql.equals(""))
          return showJavaScript("alert('第"+row+"行计量需求量不能为空');");
        if((temp = checkNumber(xql, "计量需求量")) != null)
          return temp;
        String xgl = detailrow.get("xgl");//需购量
        if((temp = checkNumber(xgl, detailProducer.getFieldInfo("xgl").getFieldname())) != null)
          return temp;
        temp = detailrow.get("ksrq");
        if(temp.length() > 0 && !isDate(temp))
          return showJavaScript("alert('非法开始日期！');");
        temp = detailrow.get("wcrq");
        if(temp.length() > 0 && !isDate(temp))
          return showJavaScript("alert('非法完成日期！');");
        if(d_RowInfos.size()==0)
          return showJavaScript("alert('没有生成物料需求不能保存')");
      }
      return null;
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
        return showJavaScript("alert('请选择部门！');");
      temp = rowInfo.get("scjhid");
      if(temp.equals(""))
         return showJavaScript("alert('请选择生产计划！');");
      return null;
    }
  }
  /**
  * 得到物资信息的bean
  * @param req WEB的请求
  * @return 返回得到物资信息的bean
  */
  public LookUp getProductBean(HttpServletRequest req)
 {
   if(productBean == null)
     productBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PRODUCT_STOCK);
   return productBean;
  }
  /**
  * 得到计划可供量信息的bean
  * @param req WEB的请求
  * @return 返回外币信息bean
  */
 public LookUp getPlanUseAbleBean(HttpServletRequest req)
 {
   if(planUseAbleBean == null)
     planUseAbleBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PALN_USABLE_NUMBER);
   return planUseAbleBean;
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
}