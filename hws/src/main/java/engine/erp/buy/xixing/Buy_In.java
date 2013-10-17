package engine.erp.buy.xixing;

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
import engine.erp.buy.ImportOrder;
import engine.erp.buy.B_SingleSelectOrder;

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
 * <p>Title: 采购--采购进货单列表</p>
 * <p>Description: 采购--采购进货单列表<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author xiaozhi
 * @version 1.0
 */

public final class Buy_In extends BaseAction implements Operate
{
  public  static final String SHOW_DETAIL = "10901";
  public  static final String DETAIL_ORDER_ADD = "10801";
  public  static final String THD_ADD = "10401";
  public  static final String ONCHANGE = "10501";
  public  static final String CANCLE_APPROVE = "12301";
  public  static final String SINGLE_SELECT_ORDER = "12451";
  public  static final String  SINGLE_PRODUCT_ADD = "10811";
  public  static final String HSBL_ONCHANGE = "10051";//提交换算比例
  public  static final String DEPT_CHANGE = "10011";//提交选者部门
  public  static final String STORE_CHANGE = "10021";//提交选者部门
  public  static final String PRODUCT_ONCHANGE = "10111";//手工输入产品代码和名称时触发的事件
  public  static final String REPORT = "2000";//报表追踪操作
  public  static final String COMPLETE = "2001";//手工完成操作
  public  static final String INSTORE_CONFIRM = "2002"; //入库确认
  public  static final String WB_ONCHANGE = "10031";
  public  static final String DETAIL_COPY = "10041";//从表复制多行触发事件
  public  static final String DELETE_BLANK = "2007";//删除数量为空行触发事件s

  public  static final String OPERATE_A = "2008"; //入库确认用
  public  static final String MATERIAL_ON_CHANGE = "2009"; //引入物资后带入采购报价中的单价


  private static final String REFERENCED_SQL        = "select count(*) from ?  where ? ? ?";//
  private static final String MASTER_STRUT_SQL = "SELECT * FROM cg_htjhd WHERE 1<>1";//取得主表结构
  private static final String MASTER_SQL    = "SELECT * FROM cg_htjhd WHERE ? AND djlx='1' and fgsid=? ? ORDER BY djlx DESC, jhdbm DESC";//取得主表数据
  private static final String DETAIL_STRUT_SQL = "SELECT * FROM cg_htjhdhw WHERE 1<>1";//取得从表结构
  //private static final String DETAIL_SQL    = "SELECT * FROM cg_htjhdhw WHERE jhdid='?' ORDER BY hthwid,cpid";//
  private static final String DETAIL_SQL    = "SELECT a.* FROM cg_htjhdhw a ,kc_dm b where a.cpid=b.cpid and a.jhdid='?' order by b.cpbm  ";//取得从表数据
  //判断进货单是否被入库单引用
  private static final String DETAIL_IS_REFERENCE = "SELECT count(*) FROM cg_htjhdhw WHERE nvl(sjrkl,0)>0 AND jhdid= ";

  //用于审批时候提取一条记录
  private static final String MASTER_APPROVE_SQL = "SELECT * FROM cg_htjhd WHERE jhdid='?'";
  //通过合同ID得到合同货物信息,数量在审批之前就改变 2004.12.01 wlc
  private static final String ORDER_DETAIL_SQL  //数量在审批后改变的SQL = "SELECT a.* FROM cg_hthw a, kc_dm b WHERE a.cpid=b.cpid AND nvl(a.sl,0)>nvl(a.sjjhl,0) AND (b.storeid IS NULL OR b.storeid=? ) AND htid=? ";//
      = "SELECT a.hthwid,a.htid,a.cpid,a.cgsqdhwid,a.gyszyh,a.dj,a.ybje,a.jhrq,a.bz,a.bj,a.sjrkl,a.sjjhl,a.dmsxid, "
      + "(nvl(a.sl,0)-nvl((SELECT SUM(nvl(k.sl,0)) FROM cg_htjhdhw k WHERE k.hthwid = a.hthwid), 0)) sl, "
      + "(nvl(a.hssl,0)-nvl((SELECT SUM(nvl(k.hssl,0)) FROM cg_htjhdhw k WHERE k.hthwid = a.hthwid), 0)) hssl, "
      + "(nvl(a.je,0)-nvl((SELECT SUM(nvl(k.je,0)) FROM cg_htjhdhw k WHERE k.hthwid = a.hthwid), 0)) je "
      + " FROM cg_hthw a, kc_dm b WHERE a.cpid=b.cpid AND nvl(a.sl,0)>nvl(a.sjjhl,0) AND (b.storeid IS NULL OR b.storeid=? ) AND htid=? ";

  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "cg_htjhd");
  public  HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "cg_htjhdhw");
  public  boolean isRep = false;
  private boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isApprove = false;     //是否在审批状态
  public boolean isDetailAdd = false;     //从表是否在添加状态
  private long    masterRow = -1;         //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = null; //从表多行记录的引用

  private ImportOrder buyOrderBean = null; //采购合同的bean的引用, 用于提取采购合同
  private B_SingleSelectOrder singleOrderBean = null;//采购合同主表bean的引用
  private LookUp productBean = null;//产品Bean
  private LookUp corpBean = null;//往来单位Bean
  private LookUp foreignBean = null; //外币信息的bean的引用

  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  private User user = null;
  //private String deptLimit = null; //登陆员工的部门权限
  private String jhdid = null;
  public String bjfs = "";   //报价方式
  public String djlx = "1";   //单据类型
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public static String SYS_CUST_NAME = "";//客户名称,用于客制化程序
  /**
   * 采购进货单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回采购进货单列表的实例
   */
  public static Buy_In getInstance(HttpServletRequest request)
  {
    Buy_In buyInBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "buyInBean";
      buyInBean = (Buy_In)session.getAttribute(beanName);
      if(buyInBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        buyInBean = new Buy_In();
        buyInBean.qtyFormat = loginBean.getQtyFormat();
        buyInBean.priceFormat = loginBean.getPriceFormat();
        buyInBean.sumFormat = loginBean.getSumFormat();
        buyInBean.bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");

        buyInBean.fgsid = loginBean.getFirstDeptID();
        buyInBean.loginId = loginBean.getUserID();
        buyInBean.loginName = loginBean.getUserName();
        buyInBean.loginDept = loginBean.getDeptID();
        buyInBean.user = loginBean.getUser();
        buyInBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        SYS_CUST_NAME = loginBean.getSystemParam("SYS_CUST_NAME");//客户名称,用于客制化程序
        //buyInBean.deptLimit = loginBean.getUser().getHandleDeptValue();
        //设置格式化的字段
        buyInBean.dsDetailTable.setColumnFormat("sl", buyInBean.qtyFormat);
        buyInBean.dsDetailTable.setColumnFormat("hssl", buyInBean.qtyFormat);
        buyInBean.dsDetailTable.setColumnFormat("dj", buyInBean.priceFormat);
        buyInBean.dsDetailTable.setColumnFormat("je", buyInBean.sumFormat);
        buyInBean.dsDetailTable.setColumnFormat("sjrkl", buyInBean.sumFormat);
        buyInBean.dsMasterTable.setColumnFormat("zsl", buyInBean.sumFormat);
        buyInBean.dsMasterTable.setColumnFormat("zje", buyInBean.sumFormat);
        session.setAttribute(beanName, buyInBean);
      }
    }
    return buyInBean;
  }

  /**
   * 构造函数
   */
  private Buy_In()
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
    dsDetailTable.setTableName("cg_htjhdhw");

    dsMasterTable.setSort(new SortDescriptor("", new String[]{"jhdbm"}, new boolean[]{true}, null, 0));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"jhdhwid"}, new String[]{"s_cg_htjhdhw"}));
    //dsDetailTable.setSort(new SortDescriptor("", new String[]{"cpbm"}, new boolean[]{false}, null, 0));
    //dsDetailTable.setSort(new SortDescriptor("", new String[]{"hthwid","cpid"}, new boolean[]{false,false}, null, 0));

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(THD_ADD), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(ONCHANGE), new Onchange());
    addObactioner(String.valueOf(APPROVE), new Approve());
    addObactioner(String.valueOf(REPORT), new Approve());//报表追踪操作
    addObactioner(String.valueOf(ADD_APPROVE), new Add_Approve());
    addObactioner(String.valueOf(DETAIL_ORDER_ADD), new Detail_Order_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(CANCLE_APPROVE), new Cancle_Approve());
    addObactioner(String.valueOf(SINGLE_SELECT_ORDER), new Single_Select_Order());
    addObactioner(String.valueOf(HSBL_ONCHANGE), new Hsbl_Onchange());//从表输入代码后把cpid推入RowMap得到换算比例
    //addObactioner(String.valueOf(SINGLE_PRODUCT_ADD), new Single_Product_Add());
    addObactioner(String.valueOf(DEPT_CHANGE), new DeptChange());
    addObactioner(String.valueOf(STORE_CHANGE), new DeptChange());
    addObactioner(String.valueOf(PRODUCT_ONCHANGE), new Product_Onchange());
    addObactioner(String.valueOf(COMPLETE), new Complete());//手工完成
    addObactioner(String.valueOf(INSTORE_CONFIRM), new Instore_Confirm());//入库确认
    addObactioner(String.valueOf(OPERATE_A),  new Instore_Confirm());//入库确认
    addObactioner(String.valueOf(MATERIAL_ON_CHANGE),  new Import_MATERIAL_Price());//引入物资后带入采购报价中的单价
    addObactioner(String.valueOf(WB_ONCHANGE), new Wb_Onchange());
    addObactioner(String.valueOf(DETAIL_COPY), new Detail_Copy());//从表复制多行触发事件
    addObactioner(String.valueOf(DELETE_BLANK), new Delete_Blank());//删除数量为空白行触发事件
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
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
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
        m_RowInfo.put("czrq", today);//制单日期
        m_RowInfo.put("czy", loginName);//操作员
        m_RowInfo.put("jhrq", today);//交货日期
        m_RowInfo.put("czyid", loginId);
        m_RowInfo.put("djlx",djlx);
        m_RowInfo.put("deptid",loginDept);
        //m_RowInfo.put("khlx", "A");
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
        row.put("InternalRow", String.valueOf(dsDetail.getInternalRow()));
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
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//数量
      String sl = rowInfo.get("sl_"+i);
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//数量
      detailRow.put("hssl", formatNumber(rowInfo.get("hssl_"+i), qtyFormat));//换算数量
      detailRow.put("dj", formatNumber(rowInfo.get("dj_"+i), priceFormat));//单价
      detailRow.put("je", formatNumber(rowInfo.get("je_"+i), sumFormat));//金额
      detailRow.put("bz", rowInfo.get("bz_"+i));//到货情况
      detailRow.put("dhqk", rowInfo.get("dhqk_"+i));//备注
      detailRow.put("hthwid", rowInfo.get("hthwid_"+i));//备注

      //detailRow.put("gyszyh", rowInfo.get("gyszyh_"+i));//供应商资源号
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//物资规格属性
      detailRow.put("jhrq", rowInfo.get("jhrq_"+i));//交货日期

      //detailRow.put("ybje", formatNumber(rowInfo.get("ybje_"+i), sumFormat));//原币金额
      //保存用户自定义的字段
      FieldInfo[] fields = detailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }
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

  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd)
  {
    String SQL = isMasterAdd ? "-1" : jhdid;
    SQL = combineSQL(DETAIL_SQL, "?", new String[]{SQL});

    dsDetailTable.setQueryString(SQL);
    if(!dsDetailTable.isOpen())
      dsDetailTable.open();
    else
      dsDetailTable.refresh();
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
   * 根据参数判断改进货单是否被采购入库单引用
   * 如果被引用返回true
   * @return 是否在添加状态
   */
  public final boolean isReference(String jhdid) throws Exception
  {
    String count = dataSetProvider.getSequence(DETAIL_IS_REFERENCE+jhdid);
    if(count.equals("0"))
      return false;
    else
      return true;
  }
  /**
    * 入库确认，只有入库数量大于进货单数量时才可以确认
    * @return 是否在添加状态
    */
   class Instore_Confirm implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
       masterRow = dsMasterTable.getInternalRow();
       jhdid = dsMasterTable.getValue("jhdid");
       openDetailTable(false);
       String sfrk = dsMasterTable.getValue("sfrk");
       //boolean isCanConfirm = true;
       dsDetailTable.first();
       BigDecimal hssl=new BigDecimal(0),dj=new BigDecimal(0),zsl=new BigDecimal(0), sjrkhsl = new BigDecimal(0),zje = new BigDecimal(0),sl = new BigDecimal(0),sjrkl = new BigDecimal(0);
       for(int i=0;i<dsDetailTable.getRowCount(); i++)
       {
         hssl = dsDetailTable.getBigDecimal("hssl");
         sl = dsDetailTable.getBigDecimal("sl");
         String sll=dsDetailTable.getValue("sl");
         String hssll=dsDetailTable.getValue("hssl");
         sjrkhsl = dsDetailTable.getBigDecimal("sjrkhsl");//实际入库数量
         sjrkl = dsDetailTable.getBigDecimal("sjrkl");//实际入库数量
         dj = dsDetailTable.getBigDecimal("dj");

         if(sjrkl.compareTo(sl)!=0&&!sfrk.equals("2"))
         {
         data.setMessage(showJavaScript("alert('与入库数量不一致，不能入库确认')"));
         return;
         }

       /*改变入库确认条件----2004.11.13*/
        else
        {
            dsDetailTable.setValue("sjrkl",sll);
            dsDetailTable.setValue("sjrkhsl",hssll);
        }
        if (!sfrk.equals("2"))
        {
         if(bjfs.equals("1"))
         {
         String je = sjrkhsl.multiply(dj).toString();
         zje = zje.add(sjrkhsl.multiply(dj));
         zsl = zsl.add(sjrkhsl);
         dsDetailTable.setValue("je",sjrkhsl.multiply(dj).toString());
         }
         else
         {
         String je = sjrkl.multiply(dj).toString();
         zje = zje.add(sjrkl.multiply(dj));
         dsDetailTable.setValue("je",sjrkl.multiply(dj).toString());
         zsl = zsl.add(sjrkl);
         }
        }
        else
        {
          if(bjfs.equals("1"))
          {
            String je = hssl.multiply(dj).toString();
            zje = zje.add(hssl.multiply(dj));
            zsl = zsl.add(hssl);
            dsDetailTable.setValue("je",hssl.multiply(dj).toString());
          }
          else
          {
            String je = sl.multiply(dj).toString();
            zje = zje.add(sl.multiply(dj));
            dsDetailTable.setValue("je",sl.multiply(dj).toString());
            zsl = zsl.add(sl);
          }
        }
        dsDetailTable.next();
       }
       dsDetailTable.post();
       dsMasterTable.setValue("zt", "8");
       dsMasterTable.setValue("zsl", zsl.toString());
       dsMasterTable.setValue("zje", zje.toString());
       dsMasterTable.post();
       dsMasterTable.saveDataSets(new EngineDataSet[]{dsMasterTable, dsDetailTable}, null);
       dsMasterTable.readyRefresh();
     }
   }
  /**
   * 初始化操作的触发类
   */
   class Init implements Obactioner
   {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = false;
      isRep = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("zt", "0");
      row.put("jhrq$a", startDay);
      row.put("jhrq$b", today);
      isMasterAdd = true;
      isDetailAdd = false;
      //
      String SQL = " AND zt<>8";
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"),fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
      dsMasterTable.setRowMax(null);
      if(dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
       dsDetailTable.empty();
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
       jhdid = dsMasterTable.getValue("jhdid");
      //打开从表
       openDetailTable(false);
     }
   }

   /**
   * ---------------------------------------------------------
   * 主表添加或修改操作的触发类
   */
   class Master_Add_Edit implements Obactioner
   {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isApprove = false;
      isRep = false;
      if(String.valueOf(EDIT).equals(action))
      {
        isMasterAdd=false;
        isDetailAdd =false;
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        djlx = dsMasterTable.getValue("djlx");
        jhdid = dsMasterTable.getValue("jhdid");
      }
      else{
        isMasterAdd=true;
        isDetailAdd=false;
        djlx=String.valueOf(ADD).equals(action) ? "1" : "-1";//1:新增提货单,-1新增退货单
      }
      synchronized(dsDetailTable){
        openDetailTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      data.setMessage(showJavaScript("toDetail();"));
    }
   }
   /**
    * 审批操作的触发类
    */
   class Approve implements Obactioner
   {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      boolean isReport = String.valueOf(REPORT).equals(action);
      String id = null;
      /*
      * 如果是报表追踪操作
      */
      if(isReport){
        isRep = true;
        id = data.getParameter("id");
      }
      else{
        isApprove = true;
        id = data.getParameter("id", "");
      }

      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //得到request的参数,值若为null, 则用""代替
      String sql = combineSQL(MASTER_APPROVE_SQL, "?", new String[]{id});
      dsMasterTable.setQueryString(sql);
      if(dsMasterTable.isOpen()){
        dsMasterTable.readyRefresh();
        dsMasterTable.refresh();
      }
      else
        dsMasterTable.open();
      jhdid = dsMasterTable.getValue("jhdid");
      //打开从表
      openDetailTable(false);

      initRowInfo(true, false, true);
      initRowInfo(false, false, true);
    }
   }
   /**
    * 添加到审核列表的操作类
    */
   class Add_Approve implements Obactioner
  {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
       String billType = dsMasterTable.getValue("djlx");
       String content = dsMasterTable.getValue("jhdbm");
       String deptid = dsMasterTable.getValue("deptid");
      int row = dsMasterTable.getRow();
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      if(billType.equals("1"))
        approve.putAproveList(dsMasterTable, row, "buy_in", content,deptid);
      //else
      //approve.putAproveList(dsMasterTable, row, "unbuy_in", content,deptid);
     }
  }
  /**
   * 取消审批触发操作
   */
  class Cancle_Approve implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      jhdid = dsMasterTable.getValue("jhdid");
     String count = dataSetProvider.getSequence("select count(*) from cg_htjhdhw a,cw_cgjshx b where b.jhdhwid=a.jhdhwid and a.jhdid="+jhdid);
    if(!count.equals("0"))
    {
      data.setMessage(showJavaScript("alert('该单据已被采购结算单引用，不能取消审批，您可以删除销售结算单中的相应货物后再取消审批!')"));
      return;
    }
    String count2 = dataSetProvider.getSequence("select count(*) from cg_htjhdhw a,cw_cgfpmx b where b.jhdhwid=a.jhdhwid and a.jhdid="+jhdid);
   if(!count2.equals("0"))
   {
     data.setMessage(showJavaScript("alert('该单据已被采购发票引用，不能取消审批，您可以删除采购发票单中的相应货物后再取消审批!')"));
     return;
    }
    String count3 = dataSetProvider.getSequence("select count(*) from cg_htjhdhw a,kc_sfdjmx b where b.wjid=a.jhdhwid and b.djxz=1 and a.jhdid="+jhdid);
   if(!count3.equals("0"))
   {
     data.setMessage(showJavaScript("alert('该单据已被采购入库单引用，不能取消审批，您可以删除采购入库单中的相应货物后再取消审批!')"));
     return;
    }
      ApproveFacade approve = ApproveFacade.getInstance(data.getRequest());
      approve.cancelAprove(dsMasterTable,dsMasterTable.getRow(), "buy_in");
    }
  }
  /**
   * 输入产品编码把产品iD存入RorMap
   */
  class Hsbl_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      putDetailInfo(request);
    }
  }
  /**
  *手工输入产品代码和输入产品名称触发的事件
  */
  class Product_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
     HttpServletRequest req = data.getRequest();
     putDetailInfo(req);
    }
  }
  /**
   *改变往来单位触发的事件
   */
  class Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      EngineDataSet ds = getMaterTable();
      RowMap rowinfo = getMasterRowinfo();
      String oldDwtxid = rowinfo.get("dwtxid");
      putDetailInfo(data.getRequest());
      String dwtxid = rowinfo.get("dwtxid");
      if(!oldDwtxid.equals(dwtxid)){
        RowMap corpRow = getCorpBean(req).getLookupRow(dwtxid);
        rowinfo.put("deptid", corpRow.get("deptid"));
        rowinfo.put("personid", corpRow.get("personid"));
        EngineDataSet detail = getDetailTable();
        detail.first();
        while(detail.inBounds())
        {
          String hthwid = detail.getValue("hthwid");
          if(!hthwid.equals(""))
          {
            d_RowInfos.remove(detail.getRow());
            detail.deleteRow();
          }
          else
            detail.next();
        }
      }
      else return;
    }
  }
  /**
   *改变车间触发的事件
   */
  class DeptChange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      m_RowInfo.put(req);
      boolean isDept = String.valueOf(DEPT_CHANGE).equals(action);
      if(!isDept)
      {
        dsDetailTable.deleteAllRows();
        d_RowInfos.clear();
      }
    }
  }
  /**
   * 从表增加操作（单选产品）

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
   if(!isMasterAdd)
   dsMasterTable.goToInternalRow(masterRow);
   String jhdid = dsMasterTable.getValue("jhdid");
   dsDetailTable.goToRow(row);
   RowMap detailrow = null;
   detailrow = (RowMap)d_RowInfos.get(row);
   detailrow.put("jhdhwid", "-1");
   detailrow.put("cpid", cpid);
   detailrow.put("jhdid", isMasterAdd ? "-1" : jhdid);
   }
   }
   */

  /**
   *  进货单选择合同主表从表信息过来触发操作的类
   */

  class Single_Select_Order implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
     putDetailInfo(data.getRequest());
     RowMap rowinfo = getMasterRowinfo();
     String storeid = rowinfo.get("storeid");
     String djxz = m_RowInfo.get("djlx");

     String singleOrder = rowinfo.get("singleOrder");
     if(singleOrder.equals(""))
       return;
     String SQL = combineSQL(ORDER_DETAIL_SQL, "?", new String[]{storeid, singleOrder});
     EngineDataSet processData = null;
     if(processData==null)
     {
       processData = new EngineDataSet();
       setDataSetProperty(processData,null);
     }
     processData.setQueryString(SQL);
     if(!processData.isOpen())
       processData.openDataSet();
     else
       processData.refresh();
     RowMap singleRow = getOrderMasterBean(req).getLookupRow(singleOrder);//采购合同主表一行信息
     rowinfo.put("khlx", singleRow.get("khlx"));//客户类型
     rowinfo.put("dwtxid", singleRow.get("dwtxid"));
     rowinfo.put("deptid", singleRow.get("deptid"));
     rowinfo.put("personid", singleRow.get("personid"));
     rowinfo.put("wbid", singleRow.get("wbid"));
     rowinfo.put("hl", singleRow.get("hl"));
     //实例化查找数据集的类
     EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "hthwid");
     if(!isMasterAdd)
       dsMasterTable.goToInternalRow(masterRow);
     String jhdid = dsMasterTable.getValue("jhdid");
     for(int i=0; i < processData.getRowCount(); i++)
     {
       processData.goToRow(i);
       String hthwid = processData.getValue("hthwid");
       locateGoodsRow.setValue(0, hthwid);
       if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
       {
         double sjjhl=0, sl=0, dj=0;//实际入库量， 合同数量， 未进货量,金额
         sjjhl = processData.getValue("sjjhl").length() > 0 ? Double.parseDouble(processData.getValue("sjjhl")) : 0;//实际进货数量
         sl  = processData.getValue("sl").length() > 0 ? Double.parseDouble(processData.getValue("sl")) : 0;//数量
         dj  = processData.getValue("dj").length() > 0 ? Double.parseDouble(processData.getValue("dj")) : 0;//单价

         if(sl<=0)
           continue;
         dsDetailTable.insertRow(false);
         String cpid = processData.getValue("cpid");
         RowMap productRow = getProductBean(req).getLookupRow(cpid);

         //double hsbl = productRow.get("hsbl").length() > 0 ? Double.parseDouble(productRow.get("hsbl")) : 0;//数量
         dsDetailTable.setValue("jhdhwid", "-1");
         dsDetailTable.setValue("jhdid", isMasterAdd ? "" : jhdid);
         dsDetailTable.setValue("hthwid", hthwid);
         dsDetailTable.setValue("cpid", cpid);
         //  if(djlx.equals("1")){
         //   if(bjfs.equals("1"))
         //      dsDetailTable.setValue("hssl", String.valueOf(wjhl));
         //    else
         //       dsDetailTable.setValue("sl", String.valueOf(wjhl));
         /**
          dsDetailTable.setValue("sl", formatNumber(bjfs.equals("1") ? String.valueOf(hsbl==0 ? 0 : wjhl*hsbl) : String.valueOf(wjhl), qtyFormat));
          dsDetailTable.setValue("hssl",formatNumber(bjfs.equals("1") ? String.valueOf(wjhl) : String.valueOf(hsbl==0 ? 0 : wjhl/hsbl), qtyFormat));
            dsDetailTable.setValue("je", formatNumber(bjfs.equals("1") ? String.valueOf(hsbl==0 ? 0 : wjhl*hsbl*dj) : String.valueOf(wjhl*dj),sumFormat) );
            * */
           //     }
           //     else{
           //      if(bjfs.equals("1"))
           //        dsDetailTable.setValue("hssl", String.valueOf(-1*wjhl));
           //       else
           //         dsDetailTable.setValue("sl", String.valueOf(-1*wjhl));
           /**
            dsDetailTable.setValue("sl", formatNumber(bjfs.equals("1") ? String.valueOf(hsbl==0 ? 0 : wjhl*hsbl*-1) : String.valueOf(wjhl*-1), qtyFormat));
            dsDetailTable.setValue("hssl",formatNumber(bjfs.equals("1") ? String.valueOf(wjhl*-1) : String.valueOf(hsbl==0 ? 0 : -1*wjhl/hsbl), qtyFormat));
            dsDetailTable.setValue("je", formatNumber(bjfs.equals("1") ? String.valueOf(hsbl==0 ? 0 : -1*wjhl*hsbl*dj) : String.valueOf(-1*wjhl*dj),sumFormat) );
            * */
           //       }
         dsDetailTable.setValue("hssl", processData.getValue("hssl"));//换算数量
           dsDetailTable.setValue("sl", processData.getValue("sl"));//数量
           dsDetailTable.setValue("jhrq", processData.getValue("jhrq"));//交货日期
         dsDetailTable.setValue("dj", processData.getValue("dj"));
         dsDetailTable.setValue("dmsxid", processData.getValue("dmsxid"));
         //dsDetailTable.setValue("gyszyh", processData.getValue("gyszyh"));
         dsDetailTable.setValue("bz", processData.getValue("bz"));
         dsDetailTable.post();
         //创建一个与用户相对应的行
         RowMap detailrow = new RowMap(dsDetailTable);
         detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
         d_RowInfos.add(detailrow);
       }
     }
     if(bjfs.equals("1"))
       data.setMessage(showJavaScript("big_change(true)"));
     else
       data.setMessage(showJavaScript("big_change(false)"));
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
      if(!isMasterAdd){

        ds.goToInternalRow(masterRow);
      }

      //得到主表主键值
      String jhdid = null;
      if(isMasterAdd){
        for(int i=0; i<d_RowInfos.size(); i++)
      {
         RowMap row = (RowMap)d_RowInfos.get(i);
         if(!row.get("hthwid").equals(""))
         {
           String hthwid = row.get("hthwid");
           String count = dataSetProvider.getSequence("select count(*) from cg_hthw b,cg_ht c where  c.htid=b.htid and c.zt=1 and b.hthwid="+hthwid);
           if(count.equals("0"))
           {
             data.setMessage(showJavaScript("alert('到货单明细中第"+(i+1)+"行所引合同被取消审批或作废，请核对审批后再保存!');"));
             return;
           }
         }

        }
        ds.insertRow(false);
        jhdid = dataSetProvider.getSequence("s_cg_htjhd");
        ds.setValue("jhdid", jhdid);
        ds.setValue("fgsid", fgsid);
        ds.setValue("zt","0");
        ds.setValue("czrq", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));//制单日期
        ds.setValue("czyid", loginId);
        ds.setValue("czy", loginName);//操作员
      }
      String djlx = rowInfo.get("djlx");
      //保存从表的数据
      RowMap detailrow = null;
      BigDecimal totalNum = new BigDecimal(0), totalSum = new BigDecimal(0);//定义totalNum为总数量，totalSum为总金额
      EngineDataSet detail = getDetailTable();
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        long internalRow = Long.parseLong(detailrow.get("InternalRow"));
        detail.goToInternalRow(internalRow);
        //新添的记录
        if(isMasterAdd)
          detail.setValue("jhdid", jhdid);

        //detail.setValue("cpid", detailrow.get("cpid"));
        double dj = detailrow.get("dj").length() > 0 ? Double.parseDouble(detailrow.get("dj")) : 0;//单价
        double sl = detailrow.get("sl").length() > 0 ? Double.parseDouble(detailrow.get("sl")) : 0;//数量
        double je = detailrow.get("je").length() > 0 ? Double.parseDouble(detailrow.get("je")) : 0;//金额
        //double hsbl = detailrow.get("hsbl").length() > 0 ? Double.parseDouble(detailrow.get("hsbl")) : 0;//换算比例
        double hssl = detailrow.get("hssl").length() > 0 ? Double.parseDouble(detailrow.get("hssl")) : 0;//换算数量
        detail.setValue("sl", detailrow.get("sl"));//保存数量
        detail.setValue("hssl", detailrow.get("hssl"));//保存换算数量
        if(djlx.equals("1"))//单据为进货单时保存正数即页面信息
          detail.setValue("je", bjfs.equals("1") ? String.valueOf(hssl*(je/hssl)) : String.valueOf(sl * (je/sl)));//金额
        else//单据为退货单时保存负数即页面数据乘以-1
          detail.setValue("je", bjfs.equals("1") ? String.valueOf(-1 * dj) : String.valueOf(-1*sl * (je/sl)));//金额

        detail.setValue("dj", detailrow.get("dj"));//单价
        detail.setValue("cpid", detailrow.get("cpid"));
        detail.setValue("dmsxid", detailrow.get("dmsxid"));
        detail.setValue("bz", detailrow.get("bz"));//备注
        detail.setValue("dhqk", detailrow.get("dhqk"));//到货情况
        detail.setValue("jhrq", detailrow.get("jhrq"));//交货日期
        //detail.setValue("sjrkl", detailrow.get("sjrkl"));
        //保存用户自定义的字段
        FieldInfo[] fields = detailProducer.getBakFieldCodes();
        for(int j=0; j<fields.length; j++)
        {
          String fieldCode = fields[j].getFieldcode();
          detail.setValue(fieldCode, detailrow.get(fieldCode));
        }
        detail.post();
        totalNum = totalNum.add(detail.getBigDecimal("sl"));
        totalSum = totalSum.add(detail.getBigDecimal("je"));
      }

      //保存主表数据
      String aa = rowInfo.get("fplx");
      String aa1 = rowInfo.get("sfrk");
       jhdid = dataSetProvider.getSequence("s_cg_htjhd");
      ds.setValue("storeid", rowInfo.get("storeid"));//仓库id
      ds.setValue("personid", rowInfo.get("personid"));//人员ID
      ds.setValue("deptid", rowInfo.get("deptid"));//部门id
      //ds.setValue("dwt_dwtxid", rowInfo.get("dwt_dwtxid"));//承运单位
      ds.setValue("ztms", rowInfo.get("ztms"));//状态描述
      ds.setValue("dwtxid", rowInfo.get("dwtxid"));//购货单位ID
      ds.setValue("jhrq", rowInfo.get("jhrq"));//合同日期
      ds.setValue("djlx", rowInfo.get("djlx"));//合同日期
      ds.setValue("zsl", totalNum.toString());//总数量
      ds.setValue("zje", totalSum.toString());//总金额
      ds.setValue("khlx", rowInfo.get("khlx"));//客户类型

      ds.setValue("jsfsid", rowInfo.get("jsfsid"));//结算方式
      ds.setValue("fplx", rowInfo.get("fplx"));//发票类型

      ds.setValue("sfrk", rowInfo.get("sfrk"));//是否入库
      //ds.setValue("wbid", rowInfo.get("wbid"));//外币id
      //ds.setValue("hl", rowInfo.get("hl"));//汇率
      //保存用户自定义的字段


      FieldInfo[] fields = masterProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detail.setValue(fieldCode, rowInfo.get(fieldCode));
      }
       ds.post();
      if(djlx.equals("1"))
        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jhdbm"}, new String[]{"SELECT pck_base.billNextCode('cg_htjhd','jhdbm') from dual"}));
      if(djlx.equals("-1"))
        dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"jhdbm"}, new String[]{"SELECT pck_base.billNextCode('cg_htjhd','jhdbm','t') from dual"}));
      ds.saveDataSets(new EngineDataSet[]{ds, detail}, null);

      if(String.valueOf(POST_CONTINUE).equals(action)){
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();
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
      //RowMap rowInfo = getMasterRowinfo();
      String djlx = m_RowInfo.get("djlx");
      String temp = null;
      RowMap detailrow = null;
      if(d_RowInfos.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      ArrayList list = new ArrayList(d_RowInfos.size());
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        String cpid = detailrow.get("cpid");

        String dmsxid = detailrow.get("dmsxid");
        String hthwid = detailrow.get("hthwid");
        StringBuffer buf = new StringBuffer().append(cpid).append(",").append(dmsxid).append(",").append(hthwid);
        String cpiddmsxid = buf.toString();
        if(cpid.equals(""))
          return showJavaScript("alert('第"+row+"行产品不能为空');");
        if(list.contains(cpiddmsxid))
          return showJavaScript("alert('第"+row+"行产品重复');");
       else
          list.add(cpiddmsxid);
        /*    增加数量和换算数量校验 2004.11.10*/
       String sl = detailrow.get("sl");
        double b_sl = sl.length()>0 ? Double.parseDouble(sl) : 0;
        if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
            return temp;
//        if(b_sl < 0)
//          return showJavaScript("alert('第"+row+"行数量不能为负数');");
        String hssl = detailrow.get("hssl");
        double b_hssl = hssl.length()>0 ? Double.parseDouble(hssl) : 0;
        if((temp = checkNumber(hssl, "第"+row+"行换算数量")) != null)
            return temp;
//        if(b_hssl < 0)
//          return showJavaScript("alert('第"+row+"行换算数量不能为负数');");
        if(b_hssl == 0 && b_sl == 0)
          return showJavaScript("alert('第"+row+"行数量、换算数量不能同时为 0');");
      /*  if(bjfs.equals("0")){
          if((temp = checkNumber(sl, "第"+row+"行数量")) != null)
            return temp;
        }*/



       // if(b_sl<0)
        //  return showJavaScript("alert('第"+row+"行数量不能小于零');");

        String dj = detailrow.get("dj");
        if((temp = checkNumber(dj, "第"+row+"行单价")) != null)
          return temp;
        double b_dj = dj.length()>0 ? Double.parseDouble(dj) : 0;
        if(b_dj<0)
          return showJavaScript("alert('第"+row+"行换算单价不能小于零！');");
        temp = detailrow.get("jhrq");
       if(temp.equals(""))
         return showJavaScript("alert('交货日期不能为空！');");
       if(temp.length() > 0 && !isDate(temp))
          return showJavaScript("alert('第"+row+"行非法交货日期！');");
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
      String temp = rowInfo.get("jhrq");
      if(temp.equals(""))
        return showJavaScript("alert('进货日期不能为空！');");
      else if(!isDate(temp))
        return showJavaScript("alert('非法交货日期！');");
      temp = rowInfo.get("dwtxid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择供货单位！');");
      temp = rowInfo.get("storeid");
      if(temp.equals(""))
        return showJavaScript("alert('请选择仓库！');");
      temp = rowInfo.get("khlx");
      if(temp.equals(""))
        return showJavaScript("alert('客户类型不能为空！');");
      temp = rowInfo.get("jsfsid");
      if(temp.equals(""))
        return showJavaScript("alert('结算方式不能为空')");
      temp = rowInfo.get("deptid");
      if(temp.equals(""))
        return showJavaScript("alert('部门不能为空')");
      temp = rowInfo.get("personid");
      if(temp.equals(""))
        return showJavaScript("alert('业务员不能为空')");
      temp = rowInfo.get("fplx");
      if(temp.equals(""))
        return showJavaScript("alert('发票类型不能为空')");
      temp = rowInfo.get("sfrk");
      if(temp.equals(""))
        return showJavaScript("alert('是否入库不能为空')");
      return null;
    }
 }
  /**
   * 强制完成操作触发的类
   */
  class Complete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterTable.getInternalRow();
      jhdid = dsMasterTable.getValue("jhdid");
      openDetailTable(false);
      dsDetailTable.first();
      String lx = dsMasterTable.getValue("djlx");
      BigDecimal je = new BigDecimal(0), sfje = new BigDecimal(0);
      for(int i=0;i<dsDetailTable.getRowCount();i++)
      {
        je = dsDetailTable.getBigDecimal("je");
        sfje = dsDetailTable.getBigDecimal("sfje");
        if(sfje.compareTo(je)<0)
        {
          data.setMessage(showJavaScript(lx.equals("1") ? "alert('还有款项未付完,不能完成!')" : "alert('还有款项未结清,不能完成!')"));
          return;
        }
        dsDetailTable.next();
      }
     dsMasterTable.setValue("zt","8");
     dsMasterTable.post();
     dsMasterTable.saveChanges();
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
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
      //
      d_RowInfos.clear();
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
      SQL = combineSQL(MASTER_SQL, "?", new String[]{user.getHandleDeptWhereValue("deptid", "czyid"),fgsid, SQL});
      dsMasterTable.setQueryString(SQL);
      dsMasterTable.setRowMax(null);
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
      //EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("jhdbm"), null, null, null),
        new QueryColumn(master.getColumn("jhrq"), null, null, null, "a", ">="),
        new QueryColumn(master.getColumn("jhrq"), null, null, null, "b", "<="),
        new QueryColumn(master.getColumn("dwtxid"), null, null, null, null, "="),//购货单位
        //new QueryColumn(master.getColumn("dwt_dwtxid"), null, null, null, null, "="),//承运单位
        new QueryColumn(master.getColumn("storeid"), null, null, null, null, "="),//仓库
        new QueryColumn(master.getColumn("djlx"), null, null, null, null, "="),//单据类型
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),//部门
        new QueryColumn(master.getColumn("jhdid"), "cg_htjhdhw", "jhdid", "cpid", null, "="),//从表产品
        new QueryColumn(master.getColumn("jhdid"), "VW_CG_HTJHDHW", "jhdid", "product", "product", "like"),//从表产品
        new QueryColumn(master.getColumn("jhdid"), "VW_CG_HTJHDHW", "jhdid", "cpbm", "cpbm", "left_like"),//从表产品
        new QueryColumn(master.getColumn("jhdid"), "VW_CG_HTJHDHW", "jhdid", "sxz", "sxz", "like"),//从表产品名称
        new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")
      });
      isInitQuery = true;
    }
  }
  /**
  *选择外币触发事件
  */
 class Wb_Onchange implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     HttpServletRequest req = data.getRequest();
     putDetailInfo(data.getRequest());

     EngineDataSet ds = getMaterTable();
     RowMap rowInfo = getMasterRowinfo();
     String wbid = rowInfo.get("wbid");
     RowMap foreignRow = getForeignBean(req).getLookupRow(wbid);
     String hl = foreignRow.get("hl");
     rowInfo.put("wbid",wbid);
     rowInfo.put("hl",hl);
     double curhl = hl.length()>0 ? Double.parseDouble(hl) : 0 ;
     for(int j=0; j<d_RowInfos.size(); j++)
     {
       RowMap detailrow = (RowMap)d_RowInfos.get(j);
       String je = detailrow.get("je");
       double curje = isDouble(je) ? Double.parseDouble(je) : 0 ;
       //detailrow.put("ybje", formatNumber(curhl==0 ? "" : String.valueOf(curje/curhl),qtyFormat));
     }
   }
  }
  /**
    * 从表增加操作
    */
   class Detail_Add implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       HttpServletRequest req = data.getRequest();
       //保存输入的明细信息
       putDetailInfo(data.getRequest());
       EngineDataSet detail = getDetailTable();
       EngineDataSet ds = getMaterTable();
       isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
       if(!isMasterAdd)
         ds.goToInternalRow(masterRow);
       String jhdid = dsMasterTable.getValue("jhdid");
       detail.insertRow(false);
       detail.setValue("jhdid", isMasterAdd ? "-1" : jhdid);
       detail.post();
       RowMap detailrow = new RowMap(detail);
       detailrow.put("InternalRow", String.valueOf(detail.getInternalRow()));
       d_RowInfos.add(detailrow);
     }
  }
  /**
   *进货单货物复制操作
   */
  class Detail_Copy implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发复制操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      putDetailInfo(request);
      EngineDataSet ds = getMaterTable();
      EngineDataSet detail = getDetailTable();
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      String jhdid = dsMasterTable.getValue("jhdid");
      RowMap rowinfo = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        rowinfo = (RowMap)d_RowInfos.get(i);
        long DetailRow = Long.parseLong(rowinfo.get("InternalRow"));
        detail.goToInternalRow(DetailRow);
        detail.setValue("cpid", rowinfo.get("cpid"));
        detail.setValue("sl", rowinfo.get("sl"));
        detail.setValue("hssl", rowinfo.get("hssl"));
        detail.setValue("dmsxid", rowinfo.get("dmsxid"));
        detail.setValue("jhrq", rowinfo.get("jhrq"));
        detail.setValue("dj", rowinfo.get("dj"));
        detail.setValue("je", rowinfo.get("je"));
        detail.setValue("bz", rowinfo.get("bz"));
        //detail.setValue("gyszyh", rowinfo.get("gyszyh"));
        //detail.setValue("ybje", rowinfo.get("ybje"));
        detail.post();
      }
      int num = Integer.parseInt(data.getParameter("rownum"));
      RowMap temprow  = (RowMap)d_RowInfos.get(num);
      detail.goToInternalRow(Long.parseLong(temprow.get("InternalRow")));
      String cpid = detail.getValue("cpid");
      //String sl = detail.getValue("sl");
      String dmsxid = detail.getValue("dmsxid");
      String jhrq = detail.getValue("jhrq");
      //String gyszyh = detail.getValue("gyszyh");
      String hthwid = detail.getValue("hthwid");
      RowMap masterrow = getMasterRowinfo();
      String tCopyNumber = request.getParameter("tCopyNumber");
      int copyNum= (tCopyNumber==null || tCopyNumber.equals("0")) ? 1 : Integer.parseInt(tCopyNumber);
      for(int j=0; j<copyNum; j++){
        detail.insertRow(false);
        detail.setValue("jhdhwID","-1");
        detail.setValue("jhdid", isMasterAdd ? "-1" : jhdid);
        detail.setValue("cpid", cpid);
        detail.setValue("dmsxid",dmsxid);
        detail.setValue("jhrq",jhrq);
        detail.setValue("hthwid", hthwid);
        //detail.setValue("gyszyh", gyszyh);
        detail.post();
        RowMap detailrow = new RowMap(detail);
        detailrow.put("InternalRow", String.valueOf(detail.getInternalRow()));
        d_RowInfos.add(detailrow);
      }
    }
  }
  /**
   *  从表引入合同货物增加操作
   */
   class Detail_Order_Add implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       HttpServletRequest req = data.getRequest();
       //保存输入的明细信息
       putDetailInfo(data.getRequest());

       String importOrder = m_RowInfo.get("importOrder");
       if(importOrder.length() == 0)
         return;

       //实例化查找数据集的类
       EngineRow locateGoodsRow = new EngineRow(dsDetailTable, "hthwid");
       String djlx = m_RowInfo.get("djlx");
       if(!isMasterAdd)
         dsMasterTable.goToInternalRow(masterRow);
       String jhdid = dsMasterTable.getValue("jhdid");
       String[] hthwIDs = parseString(importOrder,",");
       for(int i=0; i < hthwIDs.length; i++)
       {
         if(hthwIDs[i].equals("-1"))
           continue;
         RowMap detailrow = null;
         locateGoodsRow.setValue(0, hthwIDs[i]);
         if(!dsDetailTable.locate(locateGoodsRow, Locate.FIRST))
         {
           RowMap priceRow = getBuyOrderBean(req).getLookupRow(hthwIDs[i]);//得到采购合同货物一行信息
           double sl=0, sjjhl=0, wjhl=0;
           sl = priceRow.get("wjhl").length() > 0 ? Double.parseDouble(priceRow.get("wjhl")) : 0;//未进货数量
           sjjhl = priceRow.get("sjjhl").length() > 0 ? Double.parseDouble(priceRow.get("sjjhl")) : 0;//已进货数量

           //double hsbl = priceRow.get("hsbl").length() > 0 ? Double.parseDouble(priceRow.get("hsbl")) : 0;//数量
           double je = priceRow.get("je").length() >0 ? Double.parseDouble(priceRow.get("je")) : 0;
           double dj = priceRow.get("dj").length() >0 ? Double.parseDouble(priceRow.get("dj")) : 0;
           dsDetailTable.insertRow(false);
           dsDetailTable.setValue("jhdhwid", "-1");
           dsDetailTable.setValue("jhdid", isMasterAdd ? "" : jhdid);
           dsDetailTable.setValue("hthwid", hthwIDs[i]);
           dsDetailTable.setValue("cpid", priceRow.get("cpid"));
           if(djlx.equals("1")){
             if(bjfs.equals("1"))
               //dsDetailTable.setValue("hssl", String.valueOf(sl));
             //else
               dsDetailTable.setValue("sl", String.valueOf(sl));
             /**
             dsDetailTable.setValue("sl", formatNumber(bjfs.equals("1") ? String.valueOf(hsbl==0 ? 0 : sl*hsbl) : String.valueOf(sl), qtyFormat));
             dsDetailTable.setValue("hssl",formatNumber(bjfs.equals("1") ? String.valueOf(sl) : String.valueOf(hsbl==0 ? 0 : sl/hsbl), qtyFormat));
             dsDetailTable.setValue("je", formatNumber(bjfs.equals("1") ? String.valueOf(hsbl==0 ? 0 : sl*hsbl*dj) : String.valueOf(sl*dj),sumFormat) );
             * */
           }
           else{
             if(bjfs.equals("1"))
               dsDetailTable.setValue("hssl", String.valueOf(-1*sl));
             else
               dsDetailTable.setValue("sl", String.valueOf(-1*sl));
             /**
             dsDetailTable.setValue("sl", formatNumber(bjfs.equals("1") ? String.valueOf(hsbl==0 ? 0 : sl*hsbl*-1) : String.valueOf(sl*-1), qtyFormat));
             dsDetailTable.setValue("hssl",formatNumber(bjfs.equals("1") ? String.valueOf(sl*-1) : String.valueOf(hsbl==0 ? 0 : -1*sl/hsbl), qtyFormat));
             dsDetailTable.setValue("je", formatNumber(bjfs.equals("1") ? String.valueOf(hsbl==0 ? 0 : -1*sl*hsbl*dj) : String.valueOf(-1*sl*dj),sumFormat) );
             * */
           }
           dsDetailTable.setValue("jhrq", priceRow.get("jhrq"));
           dsDetailTable.setValue("dj", priceRow.get("dj"));
             dsDetailTable.setValue("sl", priceRow.get("sl"));
              dsDetailTable.setValue("je", priceRow.get("je"));

           //dsDetailTable.setValue("gyszyh", priceRow.get("gyszyh"));
           dsDetailTable.setValue("bz", priceRow.get("bz"));
           dsDetailTable.setValue("dmsxid", priceRow.get("dmsxid"));
           dsDetailTable.post();
           //创建一个与用户相对应的行
           detailrow = new RowMap(dsDetailTable);
           detailrow.put("InternalRow", String.valueOf(dsDetailTable.getInternalRow()));
           d_RowInfos.add(detailrow);
         }
       }
       if(bjfs.equals("1"))
         data.setMessage(showJavaScript("big_change(true)"));
       else
         data.setMessage(showJavaScript("big_change(false)"));
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
      RowMap detailrow = (RowMap)d_RowInfos.get(rownum);
      long l_row  = Long.parseLong(detailrow.get("InternalRow"));
      d_RowInfos.remove(rownum);
      ds.goToInternalRow(l_row);
      ds.deleteRow();
    }
  }
  /**
   * 删除产品编码为空白行操作
   */
  class Delete_Blank implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      String cpid = null;
      EngineDataSet detail = getDetailTable();
      for(int i=0; i< d_RowInfos.size(); i++)
      {
        RowMap detailrow = (RowMap)d_RowInfos.get(i);
        long internalRow = Long.parseLong(detailrow.get("InternalRow"));
        detail.goToInternalRow(internalRow);
        String sl = "0";
        if(bjfs.equals("1"))
          sl = detailrow.get("hssl");
        else
          sl = detailrow.get("sl");
        if(sl.equals("0"))
        {
          d_RowInfos.remove(i);
          detail.deleteRow();
          i--;
        }
      }
    }
  }
  /**
   * 得到产品信息的bean
   * @param req WEB的请求
   * @return 返回产品信息的bean
   */
  public LookUp getProductBean(HttpServletRequest req)
  {
    if(productBean == null)
      productBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PRODUCT);
    return productBean;
  }
  /**
   * 得到往来单位信息的bean
   * @param req WEB的请求
   * @return 返回往来单位信息的bean
   */
  public LookUp getCorpBean(HttpServletRequest req)
  {
    if(corpBean == null)
      corpBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_CORP);
    return corpBean;
  }

  /**
   * 得到用于查找合同编号的bean
   * @param req WEB的请求
   * @return 返回用于查找合同编号的bean
   */
  public ImportOrder getBuyOrderBean(HttpServletRequest req)
  {
    if(buyOrderBean == null)
      buyOrderBean = ImportOrder.getInstance(req);
    return buyOrderBean;
  }
  /**
   * 得到用于合同主表信息的bean
   * @param req WEB的请求
   * @return 返回用于合同主表信息的bean
   */
  public B_SingleSelectOrder getOrderMasterBean(HttpServletRequest req)
  {
    if(singleOrderBean == null)
      singleOrderBean = B_SingleSelectOrder.getInstance(req);
    return singleOrderBean;
  }
  /**
   * 得到外币信息的bean
   * @param req WEB的请求
   * @return 返回外币信息bean
   */
  public LookUp getForeignBean(HttpServletRequest req)
  {
    if(foreignBean == null)
      foreignBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_FOREIGN_CURRENCY);
    return foreignBean;
  }
  /*引入物资后带入报价*/
  class Import_MATERIAL_Price implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      RowMap detail = null;
      RowMap prceRow = null;
      String dwtxid = m_RowInfo.get("dwtxid");
      if(dwtxid.equals(""))
      {
        data.setMessage(showJavaScript("alert('请先选取往来单位!')"));
      return;
      }
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detail = (RowMap)d_RowInfos.get(i);
        String cpid = detail.get("cpid");
        String dj = detail.get("dj");
        if(dj.equals(""))
        {
        String dmsxid = detail.get("dmsxid").trim();
        if(cpid.equals(""))
        {
          data.setMessage(showJavaScript("alert('请先选取产品!')"));
          return;
        }

        String bj = "";
        if(dmsxid.equals(""))
          bj = dataSetProvider.getSequence("select bj from cg_bj where sflsbj=0 and cpid='"+cpid+"' and dwtxid='"+dwtxid+"' and dmsxid is null");
        else
          bj = dataSetProvider.getSequence("select bj from cg_bj where sflsbj=0 and cpid='"+cpid+"' and dwtxid='"+dwtxid+"' and dmsxid ='"+dmsxid+"'");
        bj=bj==null?"":bj;
        detail.put("dj", bj);
        double djk = detail.get("dj").length() > 0 ? Double.parseDouble(detail.get("dj")) : 0;//计算金额功能
        detail.put("dmsxid", "");
        }
      }
    }
  }
  /**
   * @param table 表名或视图名
   * @param field 字段名
   * @param value 字段值
   * @return
   */
  public  boolean hasReferenced(String table,String field,String value)
  {
    if(table==null||table.equals(""))
      return false;
    if(field==null||field.equals(""))
      return false;
    if(value==null||value.equals(""))
      return false;
    String count="";
    String sql = combineSQL(REFERENCED_SQL,"?",new String []{table,field,"="+value});
    try
    {
      count = dataSetProvider.getSequence(sql);
    }
    catch(Exception e)
    {
      System.out.print("error");
      return false;
    }
    if(!count.equals("0"))
      return true;
    else
      return false;
  }
}






