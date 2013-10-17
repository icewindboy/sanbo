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
import java.util.*;
import java.text.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import engine.erp.sale.B_CustProdHistorySelect;
import com.borland.dx.dataset.*;

/**
 * 销售管理-销售合同
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @version 1.0
 */
public final class B_RelateOrder
    extends BaseAction implements Operate {
  public static final String SHOW_DETAIL = "9001"; //显示明细
  public static final String CANCER_APPROVE = "9002"; //取消审批
  public static final String SALE_OVER = "9003"; //完成
  public static final String SALE_CANCER = "9004"; //作废
  public static final String DWTXID_CHANGE = "9005"; //改变往来单位
  public static final String DEPT_CHANGE = "9006"; //部门改变
  public static final String DETAIL_CHANGE = "9007"; //从表明细改变
  public static final String KHLX_CHANGE = "9008"; //客户类型改变!
  public static final String DETAIL_PRODUCT_ADD = "9009"; //引入客户历史产品
  public static final String PRICE_POST = "9010"; //优惠价审批保存
  public static final String WBZL_ONCHANGE = "190011"; //外币类别改变
  public static final String REPORT = "645355666"; //报表追踪
  public static final String CONFIRM = "700001"; //确认
  public static final String CONFIRM_RETURN = "700002"; //确认后返回
  public static final String DETAIL_COPY = "1013"; //复制当前选中行
  public static final String DEL_NULL = "1014"; //删除数量为空的行
  public static final String FORCE_OVER = "1015"; //强制完成
  public static final String PRODUCT_INVOC = "1016"; //生产调用合同
  public static final String MATERIAL_ADD = "1017"; //来料加工

  private static final String MASTER_STRUT_SQL =
      "SELECT * FROM xs_ht WHERE 1<>1 ORDER BY htbh DESC ";
  private static final String MASTER_SQL =
      "SELECT * FROM xs_ht WHERE  ?  AND fgsid=? ? ORDER BY htbh DESC";
  private static final String MASTER_EDIT_SQL =
      "SELECT * FROM xs_ht WHERE htid='?' ";
  private static final String MASTER_SUM_SQL =
      "SELECT SUM(nvl(zsl,0))zsl FROM xs_ht WHERE 1=1 AND ? AND fgsid=? ?  ";
  private static final String MASTER_JE_SQL =
      "SELECT SUM(nvl(zje,0))zje FROM xs_ht WHERE 1=1 AND ? AND fgsid=? ?  ";
  private static final String DETAIL_STRUT_SQL =
      "SELECT * FROM xs_hthw WHERE 1<>1";
  private static final String DETAIL_SQL = "SELECT * FROM xs_hthw WHERE htid=";
  private static final String CAN_GEN_SQL = "SELECT COUNT(*) FROM xs_hthw a, xs_ht b WHERE a.htid=b.htid AND b.zt=1 AND abs(nvl(a.sl,0))>abs(nvl(a.skdsl,0)) AND a.htid="; //合同数>实开单数且合同没作废和完成
  private static final String MASTER_APPROVE_SQL =
      "SELECT * FROM xs_ht WHERE htid='?'"; //用于审批时候提取一条记录
  private static final String XYED_SQL = "SELECT b.xyedid,b.dwtxid,b.xyed,b.xydj,b.fgsid,b.hkts,b.ysk, a.locknum xysdl FROM VW_ORDER_CUSTCREDIT_LOCK a,xs_khxyed b WHERE b.dwtxid=a.dwtxid AND a.fgsid=b.fgsid and a.dwtxid=";
  private static final String ORDER_RECEIVE_GOODS
      = "SELECT htid FROM (SELECT a.htid, SUM(nvl(b.sl,0)) sl FROM xs_hthw a, xs_tdhw b "
      + "WHERE a.hthwid = b.hthwid AND a.htid IN (?) GROUP BY a.htid) t WHERE t.sl <> 0 ";
  private static final String XYDE_SQL =
      "SELECT nvl(xyed,0)-nvl(xysdl,0) FROM vw_xs_new_xyed where dwtxid= "; //得到信誉度
  private EngineDataSet dsMasterList = new EngineDataSet(); //主表
  private EngineDataSet dsMasterTable = new EngineDataSet(); //主表
  private EngineDataSet dsDetailTable = new EngineDataSet(); //从表
  private EngineDataSet dsCancel = new EngineDataSet(); //用
  private ArrayList cancelOrder = new ArrayList(); //可作废
  public HtmlTableProducer masterListProducer = new HtmlTableProducer(dsMasterList, "xs_hta");
  public HtmlTableProducer masterProducer = new HtmlTableProducer(dsMasterTable, "xs_hta");
  public HtmlTableProducer detailProducer = new HtmlTableProducer(dsDetailTable, "xs_hthwa");
  private boolean isMasterAdd = true; //是否在添加状态
  public boolean isApprove = false; //是否在审批状态
  private long masterRow = -1; //保存主表修改操作的行记录指针
  private RowMap m_RowInfo = new RowMap(); //主表添加行或修改行的引用
  private ArrayList d_RowInfos = new ArrayList(); //从表多行记录的引用
  private LookUp salePriceBean = null; //销售单价的bean的引用, 用于提取销售单价
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public String retuUrl = null;
  public String loginId = ""; //登录员工的ID
  public String loginCode = ""; //登陆员工的编码
  public String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null; //分公司ID
  private String htid = null;
  private User user = null;
  public boolean isReport = false;
  public boolean submitType; //用于判断true=仅制定人可提交,false=有权限人可提交
  public boolean appear = false; //在销售合同保存时,是否要显示需要审批的信息
  public boolean conversion = false; //销售合同的换算数量与数量是否需要强制转换取
  public String[] zt;
  public String hkts = ""; //该客户的回款天数
  public String tCopyNumber = "1";
  public String sfxdw = ""; //允许更小单位


  public String dwdm = "";
  public String dwmc = "";
  public boolean showable = false; //是否显示 差价提成率(%) 计息天数 回笼天数 回笼提成率(%)
  public boolean isProductInvoke = false;
  public String isnet = "0";
  /**
   * 销售合同列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_RelateOrder getInstance(HttpServletRequest request) {
    B_RelateOrder B_RelateOrderBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session) {
      String beanName = "B_RelateOrderBean";
      B_RelateOrderBean = (B_RelateOrder) session.getAttribute(beanName);
      if (B_RelateOrderBean == null) {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        B_RelateOrderBean = new B_RelateOrder();
        B_RelateOrderBean.qtyFormat = loginBean.getQtyFormat();
        B_RelateOrderBean.priceFormat = loginBean.getPriceFormat();
        B_RelateOrderBean.sumFormat = loginBean.getSumFormat();

        B_RelateOrderBean.fgsid = loginBean.getFirstDeptID();
        B_RelateOrderBean.loginId = loginBean.getUserID();
        B_RelateOrderBean.loginName = loginBean.getUserName();
        if (loginBean.getSystemParam("XS_ORDER_SHOW_CREDIT").equals("1"))
          B_RelateOrderBean.appear = true;
        if (loginBean.getSystemParam("SC_STORE_UNIT_STYLE").equals("1"))
          B_RelateOrderBean.conversion = true;
        if (loginBean.getSystemParam("XS_ORDER_SHOW_ADD_FIELD").equals("1"))
          B_RelateOrderBean.showable = true; //显示 差价提成率(%) 计息天数 回笼天数 回笼提成率(%)
        //设置格式化的字段
        B_RelateOrderBean.dsDetailTable.setColumnFormat("sl",
            B_RelateOrderBean.qtyFormat);
        B_RelateOrderBean.dsDetailTable.setColumnFormat("hssl",
            B_RelateOrderBean.qtyFormat);
        B_RelateOrderBean.dsDetailTable.setColumnFormat("xsj",
            B_RelateOrderBean.priceFormat);
        B_RelateOrderBean.dsDetailTable.setColumnFormat("dj",
            B_RelateOrderBean.priceFormat);
        B_RelateOrderBean.dsDetailTable.setColumnFormat("xsje",
            B_RelateOrderBean.sumFormat);
        B_RelateOrderBean.dsDetailTable.setColumnFormat("jje",
            B_RelateOrderBean.sumFormat);

        B_RelateOrderBean.dsMasterTable.setColumnFormat("zsl",
            B_RelateOrderBean.qtyFormat);
        B_RelateOrderBean.dsMasterTable.setColumnFormat("zje",
            B_RelateOrderBean.sumFormat);
        B_RelateOrderBean.dsMasterTable.setColumnFormat("wbje",
            B_RelateOrderBean.sumFormat);

        B_RelateOrderBean.user = loginBean.getUser();
        session.setAttribute(beanName, B_RelateOrderBean);
      }
    }
    return B_RelateOrderBean;
  }

  /**
   * 构造函数
   */
  private B_RelateOrder() {
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
  private final void jbInit() throws java.lang.Exception {
    setDataSetProperty(dsMasterTable, MASTER_STRUT_SQL);
    setDataSetProperty(dsMasterList, MASTER_STRUT_SQL);
    setDataSetProperty(dsDetailTable, DETAIL_STRUT_SQL);
    setDataSetProperty(dsCancel, null);
    dsDetailTable.setSequence(new SequenceDescriptor(new String[] {"hthwid"},
        new String[] {"s_xs_hthw"}));
    dsMasterList.addLoadListener(new com.borland.dx.dataset.LoadListener() {
      public void dataLoaded(com.borland.dx.dataset.LoadEvent event) {
        cancelOrder.clear();
        if (dsMasterList.getRowCount() == 0)
          return;
        String sql = getWhereIn(dsMasterList, "htid", null);
        sql = combineSQL(ORDER_RECEIVE_GOODS, "?", new String[] {sql});
        dsCancel.setQueryString(sql);
        if (dsCancel.isOpen())
          dsCancel.refresh();
        else
          dsCancel.openDataSet();
        for (int i = 0; i < dsCancel.getRowCount(); i++) {
          cancelOrder.add(dsCancel.getValue("htid"));
          dsCancel.next();
        }
        dsCancel.closeDataSet();
      }
    });

    Master_Add_Edit masterAddEdit = new Master_Add_Edit();

    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(PRODUCT_INVOC), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(MATERIAL_ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);

  }

  /**
   *引入已审核合同未发货金额
   * */
  public RowMap getHtzje() {
    EngineDataSet tmp = new EngineDataSet();
    String dwtxid = m_RowInfo.get("dwtxid");
    try {
      setDataSetProperty(tmp,
          "select xs_ht.dwtxid,xs_hthw.jje jje  from xs_ht,xs_hthw where xs_ht.htid=xs_hthw.htid AND xs_ht.zt='1' AND xs_ht.dwtxid=" +
          dwtxid);
    }
    catch (Exception e) {}
    tmp.open();
    RowMap jerow = new RowMap();
    BigDecimal zje = new BigDecimal(0);
    int j = tmp.getRowCount();
    tmp.first();
    for (int i = 0; i < tmp.getRowCount(); i++) {
      zje = tmp.getBigDecimal("jje").add(zje); //销售金额
      tmp.next();
    }
    jerow.put("zje", zje.toString());
    return jerow;
  }

  /**
   *引入业务员奖金计算公式
   * */
  public RowMap getJjjsgs() {
    EngineDataSet jsgs = new EngineDataSet();
    try {
      setDataSetProperty(jsgs, "select * FROM xs_jjjsgs");
    }
    catch (Exception e) {}
    jsgs.open();
    jsgs.first();
    RowMap jsgsrow = new RowMap();
    jsgsrow.put(jsgs);
    String xsjzj = jsgs.getValue("xsjzj");
    String tclzj = jsgs.getValue("tclzj");
    String hltszj = jsgs.getValue("hltszj");
    String xsjjs = jsgs.getValue("xsjjs");
    return jsgsrow;
  }

  /**
   *引入单位的信誉额度信息
   * */
  public RowMap getXYED() {
    RowMap xyedRow;
    try {
      EngineDataSet dsxyed = new EngineDataSet();
      setDataSetProperty(dsxyed, XYED_SQL + dsMasterTable.getValue("dwtxid"));
      dsxyed.open();
      dsxyed.first();
      xyedRow = new RowMap(dsxyed);
    }
    catch (Exception e) {
      xyedRow = new RowMap();
    }
    return xyedRow;
  }


  /**
   *得到与此合同相关的总销售结算金额
   * */
  public String getTotalJsje(String dwtxid) {
    String wsje = "";
    //String dwtxid = m_RowInfo.get("dwtxid");
    if (dwtxid.equals(""))
      return "0";
    try {
      wsje = dataSetProvider.getSequence("select sum(nvl(b.jje,0)-nvl(b.ssje,0))wsje from xs_td a,xs_tdhw b where a.tdid=b.tdid and a.zt<>4 and a.hkrq<=sysdate and a.dwtxid=" +
          dwtxid);
      if (wsje == null)
        wsje = "";
    }
    catch (Exception e) {}
    return wsje;
  }

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
        if (data == null)
          return showMessage("无效操作", false);
        if (data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch (Exception ex) {
      if (dsMasterTable.isOpen() && dsMasterTable.changesPending())
        dsMasterTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   */
  public final void valueUnbound(HttpSessionBindingEvent event) {
    if (dsMasterTable != null) {
      dsMasterTable.closeDataSet();
      dsMasterTable = null;
    }
    if (dsMasterList != null) {
      dsMasterList.closeDataSet();
      dsMasterList = null;
    }
    if (dsDetailTable != null) {
      dsDetailTable.close();
      dsDetailTable = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfos = null;
    if (masterListProducer != null) {
      masterListProducer.release();
      masterListProducer = null;
    }
    if (masterProducer != null) {
      masterProducer.release();
      masterProducer = null;
    }
    if (detailProducer != null) {
      detailProducer.release();
      detailProducer = null;
    }
    deleteObservers();
  }

  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName() {
    return getClass();
  }

  /**
   *
   * @param htid
   * @return
   */
  public boolean isCanCancel(String htid) {
    return!cancelOrder.contains(htid);
  }

  /**是否可以生成提单**/
  public boolean iscanGen(String htid) {
    if (htid.equals(""))
      return false;
    String count = "";
    try {
      count = dataSetProvider.getSequence(CAN_GEN_SQL + "'" + htid + "'");
    }
    catch (Exception e) {}
    if (count.equals("0"))
      return false;
    else
      return true;
  }

  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isMaster, boolean isAdd,
      boolean isInit) throws java.lang.Exception {
    //是否是主表
    if (isMaster) {
      if (isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear(); //清除旧数据
      if (!isAdd)
        m_RowInfo.put(getMaterTable()); //不是新增时,推入主表当前行
      else {
        //主表新增
        Calendar cd = new GregorianCalendar();
        int year = cd.get(Calendar.YEAR);
        int month = cd.get(Calendar.MONTH);
        cd.clear();
        cd.set(year, month + 1, 0);
        Date ed = cd.getTime();
        String endday = new SimpleDateFormat("yyyy-MM-dd").format(ed);

        Date startDate = new Date();
        //Date endDate = new Date(startDate.getYear(), startDate.getMonth()+1, 0);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(startDate);
        //String endday = new SimpleDateFormat("yyyy-MM-dd").format(endDate);
        String htbh = dataSetProvider.getSequence(
            "SELECT pck_base.billNextCode('xs_ht','htbh')htbh FROM dual");
        m_RowInfo.put("htbh", htbh);
        m_RowInfo.put("ksrq", today);
        m_RowInfo.put("jsrq", endday);
        m_RowInfo.put("czrq", today); //制单日期
        m_RowInfo.put("czy", loginName); //操作员
        m_RowInfo.put("htrq", today);
        m_RowInfo.put("jhrq", today);
        m_RowInfo.put("czyid", loginId);
        m_RowInfo.put("zt", "0");
        m_RowInfo.put("isnet", isnet);
      }
      m_RowInfo.put("isdock", "1");
    }
    else {
      EngineDataSet dsDetail = dsDetailTable;
      if (d_RowInfos == null)
        d_RowInfos = new ArrayList(dsDetail.getRowCount());
      else if (isInit)
        d_RowInfos.clear();
      dsDetail.first();
      for (int i = 0; i < dsDetail.getRowCount(); i++) {
        RowMap row = new RowMap(dsDetail);
        d_RowInfos.add(row);
        dsDetail.next();
      }
    }
  }

  /*得到表对象*/
  public final EngineDataSet getMaterTable() {
    return dsMasterTable;
  }

  /*得到表对象*/
  public final EngineDataSet getMaterList() {
    return dsMasterList;
  }

  /*得到从表表对象*/
  public final EngineDataSet getDetailTable() {
    return dsDetailTable;
  }

  /*打开从表*/
  public final void openDetailTable(boolean isMasterAdd) {
    //htid = dsMasterTable.getValue("htid");//关链
    //isMasterAdd为真是返回空的从表数据集(主表新增时,从表要打开)
    dsDetailTable.setQueryString(DETAIL_SQL + (isMasterAdd ? "-1" : htid));
    if (dsDetailTable.isOpen())
      dsDetailTable.refresh();
    else
      dsDetailTable.open();
  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() {
    return m_RowInfo;
  }

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
  public final boolean masterIsAdd() {
    return isMasterAdd;
  }

  /**
   * 得到固定查询的用户输入的值
   * @param col 查询项名称
   * @return 用户输入的值
   */
  public final String getFixedQueryValue(String col) {
    return fixedQuery.getSearchRow().get(col);
  }

  public final boolean getState() {
    return isMasterAdd;
  }

  /**
   * 得到选中的行的行数
   * @return 若返回-1，表示没有选中的行
   */
  public final int getSelectedRow() {
    if (masterRow < 0)
      return -1;
    dsMasterList.goToInternalRow(masterRow);
    return dsMasterList.getRow();
  }

  /**
   * 初始化操作的触发类
   */
  class Init
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      dwdm = "";
      dwmc = "";
      //&#$
      isApprove = false;
      isReport = false;
      isProductInvoke = false;
      if (String.valueOf(PRODUCT_INVOC).equals(action))
        isProductInvoke = true;

      retuUrl = data.getParameter("src");
      retuUrl = retuUrl != null ? retuUrl.trim() : retuUrl;
      //
      HttpServletRequest request = data.getRequest();
      masterProducer.init(request, loginId);
      masterListProducer.init(request, loginId);
      detailProducer.init(request, loginId);
      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
      String startDay = new SimpleDateFormat("yyyy-MM-01").format(new Date());
      row.put("htrq$a", startDay);
      row.put("htrq$b", today);
      //row.put("zt", "0");
      isMasterAdd = true;
      zt = new String[] {
          ""};
      String SQL = " AND zt<>4 AND zt<>8 ";
      if (isProductInvoke)
        SQL = " AND zt<>0 AND zt<>9 ";
      String MSQL = combineSQL(MASTER_SQL, "?",
          new String[] {user.
          getHandleDeptWhereValue("deptid", "czyid"),
          fgsid, SQL});
      dsMasterList.setQueryString(MSQL);
      dsMasterList.setRowMax(null);

      if (dsDetailTable.isOpen() && dsDetailTable.getRowCount() > 0)
        dsDetailTable.empty();

      String code = dataSetProvider.getSequence(
          "select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
      if (code!=null&&code.equals("1"))
        submitType = true;
      else
        submitType = false;

      sfxdw = dataSetProvider.getSequence("select t.sfxdw from xs_jjjsgs t");

    }
  }

  /**
   * 显示从表的列表信息
   */
  class ShowDetail
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum")));
      masterRow = dsMasterList.getInternalRow();
      //打开从表
      htid = dsMasterList.getValue("htid");
      openDetailTable(false);
    }
  }

  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      //&#$
      isApprove = false;
      if (String.valueOf(ADD).equals(action)) {
        isMasterAdd = true;
        isnet = "0";
      }
      else if (String.valueOf(MATERIAL_ADD).equals(action)) {
        isMasterAdd = true;
        isnet = "1";
      }
      else
        isMasterAdd = false;
      if (!isMasterAdd) {
        dsMasterList.goToRow(Integer.parseInt(data.getParameter("rownum"))); //查看或修改
        masterRow = dsMasterList.getInternalRow(); //返回当前行指针(long)
        htid = dsMasterList.getValue("htid");
        isnet = dsMasterList.getValue("isnet");
      }

      dsMasterTable.setQueryString(isMasterAdd ? MASTER_STRUT_SQL :
          combineSQL(MASTER_EDIT_SQL, "?",
          new String[] {htid}));
      if (!dsMasterTable.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();

      synchronized (dsDetailTable) {
        openDetailTable(isMasterAdd);
      }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      data.setMessage(showJavaScript("toDetail();"));
    }
  }

  /**
   *  查询操作
   *  QueryColumn
   *  QueryFixedItem
   */
  class Master_Search
      implements Obactioner {
    public void execute(String action, Obationable o, RunData data, Object arg) throws
        Exception {
      HttpServletRequest request = data.getRequest();
      dwdm = request.getParameter("dwdm");
      dwmc = request.getParameter("dwmc");

      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery(); //得到WHERE子句
      if (SQL.length() > 0)
        SQL = " AND " + SQL;

      zt = data.getRequest().getParameterValues("zt");
      if (! (zt == null)) {
        StringBuffer sbzt = null;
        for (int i = 0; i < zt.length; i++) {
          if (sbzt == null)
            sbzt = new StringBuffer(" AND zt IN(");
          sbzt.append(zt[i] + ",");
        }
        if (sbzt == null)
          sbzt = new StringBuffer();
        else
          sbzt.append("-99)");
        SQL = SQL + sbzt.toString();
      }
      else
        zt = new String[] {
            ""};
      if (zt == null && isProductInvoke)
        SQL = "  AND zt<>0 AND zt<>9  ";

      String MSQL = combineSQL(MASTER_SQL, "?",
          new String[] {user.
          getHandleDeptWhereValue("deptid", "czyid"),
          fgsid, SQL}); //组装SQL语句
      if (!dsMasterList.getQueryString().equals(MSQL)) {
        dsMasterList.setQueryString(MSQL);
        dsMasterList.setRowMax(null); //以便dbNavigator刷新数据集
      }
    }

    /**
     * 初始化查询的各个列
     * @param request web请求对象
     */
    private void initQueryItem(HttpServletRequest request) {
      if (isInitQuery)
        return; //已初始化查询条件
      EngineDataSet master = dsMasterList;
      if (!master.isOpen())
        master.open(); //打开主表数据集
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[] {

          new QueryColumn(master.getColumn("htbh"), null, null, null, "a", ">="),
          new QueryColumn(master.getColumn("htbh"), null, null, null, "b", "<="),
          new QueryColumn(master.getColumn("htrq"), null, null, null, "a", ">="),
          new QueryColumn(master.getColumn("htrq"), null, null, null, "b", "<="),
          new QueryColumn(master.getColumn("ksrq"), null, null, null, "a", ">="),
          new QueryColumn(master.getColumn("ksrq"), null, null, null,
          "b", "<="),
          new QueryColumn(master.getColumn("czrq"), null, null, null,
          "a", ">="),
          new QueryColumn(master.getColumn("czrq"), null, null, null,
          "b", "<="),
          new QueryColumn(master.getColumn("jsrq"), null, null, null,
          "a", ">="),
          new QueryColumn(master.getColumn("jsrq"), null, null, null,
          "b", "<="),
          new QueryColumn(master.getColumn("dwtxid"), null, null, null, null,
          "="), //购货单位
          new QueryColumn(master.getColumn("deptid"), null, null, null, null,
          "="), //部门ID
          new QueryColumn(master.getColumn("khlx"), null, null, null, null,
          "="), //
          new QueryColumn(master.getColumn("isnet"), null, null, null, null,
          "="), //
          new QueryColumn(master.getColumn("qddd"), null, null, null),
          new QueryColumn(master.getColumn("personid"), null, null, null, null,
          "="), //
          new QueryColumn(master.getColumn("czy"), null, null, null, null,
          "like"), //
          new QueryColumn(master.getColumn("sprid"), null, null, null, null,
          "="), //
          new QueryColumn(master.getColumn("htid"),
          "vw_xs_hthw", "htid", "cpbm",
          "cpbm$a", ">="), //
          new QueryColumn(master.getColumn("htid"),
          "vw_xs_hthw", "htid", "cpbm",
          "cpbm$b", "<="), //
          new QueryColumn(master.getColumn("htid"),
          "vw_xs_hthw", "htid", "pm", "pm",
          "like"), //从表品名
          new QueryColumn(master.getColumn("htid"),
          "vw_xs_hthw", "htid", "gg", "gg",
          "=") //从表规格
          //new QueryColumn(master.getColumn("zt"), null, null, null, null, "=")
      });
      isInitQuery = true; //初始化完成
    }
  }

}
