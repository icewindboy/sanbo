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
import engine.common.LoginBean;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import engine.util.StringUtils;
import engine.dataset.*;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 工艺路线</p>
 * <p>Description: 工艺路线</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 杨来
 * @version 1.0
 */

public final class B_TechnicsRoute extends BaseAction implements Operate
{
  public static final int LISTCHANGE = 33011454;
  public static final int CPSELECT = 1000011;
  public static final int BKLIST = 2028899;
  public static final int GDJG_ONCHANGE = 10093;
  public static final int MASTER_COPY   = 10593;
  public static final String SHOW_DETAIL= "10011";
  public static final String BOM_SET    = "10039"; //BOM的超级连接
  //public static final int TECHNICS_CHANGE = 16593;
  //
   public static final String TECNICE_XCHANGE_INI  = "3022221";//可选工序初始化
   public static final String TECNICE_XCHANGE_ADD  = "3022222";//可选工序添加
   public static final String TECNICE_XCHANGE_POST = "3022223";//可选工序保存到内存
   public static final String TECNICE_XCHANGE_DEL  = "3022224";//可选工序删除

  private static final String MASTER_STRUT_SQL = "SELECT * FROM sc_gylx WHERE 1<>1";
  //工艺路线主表SQL语句，与库存代码关联，使得页面可以按产品编码排序
  private static final String MASTER_SQL
      = "SELECT * FROM sc_gylx , kc_dm  WHERE sc_gylx.cpid = kc_dm.cpid ? ORDER BY kc_dm.cpbm";
      //"SELECT * FROM sc_gylx a, kc_dm b, sc_gylxlx c WHERE a.cpid = b.cpid AND a.gylxlxid = c.gylxlxid ? ORDER BY b.cpbm, c.gylxlxbh ";
  private static final String MASTER_BM_SQL="SELECT cpid FROM kc_dm a  WHERE  a.cpid='?' ";
  private static final String MASTER_SINGLE_SQL
      = "SELECT * FROM sc_gylx a, kc_dm b WHERE a.cpid = b.cpid AND a.cpid='?' AND a.dmsxid='?' ORDER BY b.cpbm";
  private static final String MASTER_TEMP_SQL="SELECT * FROM sc_gylx WHERE cpid='?' AND dmsxid='?' ";

  //根据工艺路线主表ID得到明细信息
  private static final String DETAIL_SQL    = "SELECT * FROM sc_gylxmx WHERE gylxid='?' ORDER BY list";//
  private static final String DETAIL_STRUT_SQL    = "SELECT * FROM sc_gylxmx WHERE 1<>1 ";//
  //private static final String TECHNICS_CHANGE_SQL = "SELECT * FROM sc_gylx WHERE cpid=? AND gylxlxid=? ";//在同一个产品中选择不同工艺路线类型的SQl

  //可选工序
  private static final String BOM_SECTION_SQL = "SELECT * FROM sc_optionproc WHERE gylxid=? AND gylxmxid=? ";
  private static final String TECEXCHANGE_STRUCT_SQL = "SELECT * FROM sc_optionproc WHERE 1<>1";

  private static final String MASTER_COPY_SQL = "SELECT * FROM sc_gylx WHERE cpid IN(?)";//工艺路线复制给别的产品，别的产品的此工艺路线类型在工艺路线中是否已经存在
  private static final String DETAIL_COPY_SQL = "SELECT * FROM sc_gylxmx WHERE gylxid IN(?) ORDER BY list";//打开要复制给产品（即所选择产品）工艺路线类型存在于工艺路线中的从表数据
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表
  //private EngineDataSet dsTemp  = new EngineDataSet();//临时从表
  private EngineDataSet dsBomData  = new EngineDataSet();//
  private EngineDataSet dsTecData  = new EngineDataSet();//
  public  EngineDataSet dsTecnictable = new EngineDataSet();//可选工序

  public  boolean isMasterAdd = true;    //是否在添加状态
  public  boolean isFromBom   = false;   //是否来自BOM
  public  boolean isCpchanged = false;   //是否选择产品时的触发事件
  private long    masterRow = 0;     //保存从表行记录指针
  private long    dtRow = 0;
  private RowMap  m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  public  ArrayList d_RowInfos = null; //从表多行记录的引用
  public long rownum=0;//
  public int code=0;
  public int click=0;
  private boolean isInitQuery = false; //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();//查询类
  public  String retuUrl = null;
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  public  String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  //**产品更新总提前期
  private static final String TOTAL_TIME_SQL = "{CALL PCK_PRODUCE.updateBomAheadTime(@, @)}";
  private static final String AHEAD_TIME_SQL
      = "SELECT cpid, dmsxid, ahead_time FROM sc_prod_time WHERE cpid='@' AND dmsxid='@'";

  /**
   * 工艺路线的实例
   * @param request jsp请求
   * @return 返回工艺路线的实例
   */
  public static B_TechnicsRoute getInstance(HttpServletRequest request)
  {
    B_TechnicsRoute b_TechnicsRouteBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_TechnicsRouteBean";
      b_TechnicsRouteBean = (B_TechnicsRoute)session.getAttribute(beanName);
      if(b_TechnicsRouteBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        b_TechnicsRouteBean = new B_TechnicsRoute();
        b_TechnicsRouteBean.qtyFormat = loginBean.getQtyFormat();
        b_TechnicsRouteBean.priceFormat = loginBean.getPriceFormat();
        b_TechnicsRouteBean.sumFormat = loginBean.getSumFormat();
        b_TechnicsRouteBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……

        //设置格式化的字段
        //b_TechnicsRouteBean.dsDetailTable.setColumnFormat("desl", b_TechnicsRouteBean.qtyFormat);
        b_TechnicsRouteBean.dsDetailTable.setColumnFormat("lbjj", b_TechnicsRouteBean.priceFormat);
        b_TechnicsRouteBean.dsDetailTable.setColumnFormat("hsj", b_TechnicsRouteBean.priceFormat);
        b_TechnicsRouteBean.dsDetailTable.setColumnFormat("gdjg", b_TechnicsRouteBean.priceFormat);
        b_TechnicsRouteBean.dsDetailTable.setColumnFormat("wxjg", b_TechnicsRouteBean.priceFormat);
        //b_TechnicsRouteBean.dsDetailTable.setColumnFormat("deje", b_TechnicsRouteBean.priceFormat);
        session.setAttribute(beanName, b_TechnicsRouteBean);
      }
    }
    return b_TechnicsRouteBean;
  }

  /**
   * 构造函数
   */
  private B_TechnicsRoute()
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
    setDataSetProperty(dsMasterTable, combineSQL(MASTER_SQL, "?", new String[]{""}));
    setDataSetProperty(dsDetailTable, null);
    setDataSetProperty(dsTecData, null);
    setDataSetProperty(dsTecnictable, TECEXCHANGE_STRUCT_SQL);
   // setDataSetProperty(dsTemp, combineSQL(MASTER_SQL, "?", new String[]{""}));
    setDataSetProperty(dsBomData, combineSQL(AHEAD_TIME_SQL, "?", new String[]{""}));
    dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"gylxid"}, new String[]{"s_sc_gylx"}));
    //dsMasterTable.setSort(new SortDescriptor("", new String[]{"cpid","gylxlxid"}, new boolean[]{false,false}, null, 0));
    dsMasterTable.setTableName("sc_gylx");


    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"gylxmxid"}, new String[]{"s_sc_gylxmx"}));
    dsDetailTable.setSort(new SortDescriptor("", new String[]{"list"}, new boolean[]{false}, null, 0));

    dsTecnictable.setSequence(new SequenceDescriptor(new String[]{"optionId"}, new String[]{"s_sc_optionproc"}));

    dsTecnictable.addLoadListener(new com.borland.dx.dataset.LoadListener() {
     public void dataLoaded(LoadEvent e) {
     initTecnicRowInfo();
    }
    }
    );
    dsDetailTable.addLoadListener(new com.borland.dx.dataset.LoadListener() {
      public void dataLoaded(LoadEvent e) {
        initRowInfo(false,false, true);
      }
    }
    );
    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Master_Search());
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(SHOW_DETAIL, new ShowDetail());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(String.valueOf(GDJG_ONCHANGE), new Gdjg_Onchange());//选择工段触发事件
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    addObactioner(String.valueOf(MASTER_COPY), new Master_Copy());//工艺路线复制给别的产品操作
    addObactioner(String.valueOf(BOM_SET), new FromBom());
    addObactioner(String.valueOf(CPSELECT), new cpselect());
    addObactioner(String.valueOf(BKLIST), new backlist());
    addObactioner(String.valueOf(LISTCHANGE), new listchange());
    //addObactioner(String.valueOf(TECHNICS_CHANGE), new Technics_Change());
    //可选工序初始化
    addObactioner(String.valueOf(TECNICE_XCHANGE_INI), new TECNICE_XCHANGE_INI());
    //可选工序添加
    addObactioner(String.valueOf(TECNICE_XCHANGE_ADD), new tecnicadd());
    //可选工序保存
    addObactioner(String.valueOf(TECNICE_XCHANGE_POST), new TECNICE_XCHANGE_POST());
    //可选工序删除
    addObactioner(String.valueOf(TECNICE_XCHANGE_DEL), new TECNICE_XCHANGE_DEL());
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
      String operate = request.getParameter(OPERATE_KEY);//得到页面operate的值
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
    log = null;
    m_RowInfo = null;
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
   * 初始化行信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit)
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
        m_RowInfo.put("sxsj", today);
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
        row.put("internalRowNum",String.valueOf(dsDetail.getInternalRow()));
        d_RowInfos.add(row);
        dsDetail.next();
      }
    }
  }

    private ArrayList tecnicRows = null;
    private String OPTIONID = "";
    private String gylxID = "";
    private String gylxmxID = "";
    int trownum=0;
    public ArrayList gettecnicRows()
    {
    return tecnicRows;
    }
  /**
    * 初始化可选工序行信息
    */
   private final void initTecnicRowInfo()
   {
     if (!dsTecnictable.open())
       dsTecnictable.open();
        EngineDataSet dsDetail = dsTecnictable;

     int aaa=dsTecnictable.getRowCount();
     if(tecnicRows == null)
       tecnicRows = new ArrayList(dsDetail.getRowCount());
     else //if(isInit)
       tecnicRows.clear();

     dsDetail.first();
     for(int i=0; i<dsDetail.getRowCount(); i++)
     {
       RowMap row = new RowMap(dsDetail);
       tecnicRows.add(row);
       dsDetail.next();
     }
  }

  /**
    * 可选工序初始化
    */
   final class TECNICE_XCHANGE_INI implements Obactioner
   {
     //----Implementation of the Obactioner interface
     /**
      * 触发添加子节点或查看编辑记录节点操作
      * @parma  action 触发执行的参数（键值）
      * @param  o      触发者对象
      * @param  data   传递的信息的类
      * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
      */
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       int rownum = Integer.parseInt(data.getParameter("rownum"));
       trownum=rownum;
       dsDetailTable.goToRow(rownum);
       //OPTIONID = dsTecData.getValue("OPTIONID");
       gylxID= dsDetailTable.getValue("gylxID");
       gylxmxID=dsDetailTable.getValue("gylxmxID");
       openTecnicTable();
       initTecnicRowInfo();
     }
  }


  /**
   * 可选工序添加
   */
  final class tecnicadd implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      putTecnicInfo(data.getRequest());
      dsDetailTable.goToRow(trownum);
      String gxfdid=dsDetailTable.getValue("gxfdid");
      RowMap row = new RowMap();
      row.put("gylxID", gylxID);
      row.put("gylxmxID", gylxmxID);
      row.put("quot", "1");
      tecnicRows.add(row);
    }
  }


  /**
   * 可选工序从表保存操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putTecnicInfo(HttpServletRequest request)
  {

  RowMap rowInfo = getMasterRowinfo();
 //保存网页的所有信息
  rowInfo.put(request);
  int rownum = tecnicRows.size();
  RowMap detailRow = null;
  for(int i=0; i<rownum; i++)
  {
   detailRow = (RowMap)tecnicRows.get(i);
   String deje=rowInfo.get("deje_"+i);
   detailRow.put("quot", rowInfo.get("quot_"+i));//
   detailRow.put("gymcid", rowInfo.get("gymcid_"+i));
   detailRow.put("deje", rowInfo.get("deje_"+i));//
  }
  }
  /**
   * 可选工序删除
   */
  final class TECNICE_XCHANGE_DEL implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发添加子节点或查看编辑记录节点操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putTecnicInfo(data.getRequest());
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      tecnicRows.remove(rownum);
    }
  }

  /**
   * 可选工序数据保存到内存
   */
  final class TECNICE_XCHANGE_POST implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发添加子节点或查看编辑记录节点操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      putTecnicInfo(data.getRequest());
      //校验表单数据
     String temp = checkInfo();
    if(temp != null)
      {
       data.setMessage(temp);
       return;
     }
      dsTecnictable.deleteAllRows();
      for(int i=0; i<tecnicRows.size(); i++)
      {
        RowMap row = (RowMap)tecnicRows.get(i);
        dsTecnictable.insertRow(false);
        String gylxid=row.get("gylxid");
        String gylxmxid=row.get("gylxmxid");
        String gymcid=request.getParameter("gymcid_"+i);
        String quot=request.getParameter("quot_"+i);
        String deje=request.getParameter("deje_"+i);
        dsTecnictable.setValue("gylxid", row.get("gylxid"));
        dsTecnictable.setValue("gylxmxid", row.get("gylxmxid"));
        dsTecnictable.setValue("gymcid", gymcid);
        dsTecnictable.setValue("quot", quot);
        dsTecnictable.setValue("deje", deje);
       // dsTecnictable.setValue("bz", row.get("bz"));
        dsTecnictable.post();
      }
      data.setMessage(showJavaScript("hide();"));
    }

    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkInfo() throws Exception
    {
      String temp = null;
      RowMap detailrow = null;
      if(tecnicRows.size()<1)
        return showJavaScript("alert('不能保存空的数据')");
      int aaaaa=tecnicRows.size();
      ArrayList telist = new ArrayList(tecnicRows.size());
      for(int i=0; i<tecnicRows.size(); i++)
      {

        detailrow = (RowMap)tecnicRows.get(i);
        String deje = detailrow.get("deje");
        if(deje.equals(""))
          return showJavaScript("alert('第"+ (i+1) + "基准单价行不能为空！')");
        String gymcid = detailrow.get("gymcid");
        if(gymcid.equals(""))
          return showJavaScript("alert('不能保存空的工序')");
        if(telist.contains(gymcid))
          return showJavaScript("alert('工序不能重复')");
        else
          telist.add(gymcid);
      }
      return null;
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
    //页面从表多行纪录推入到ArrayList里面
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("internalRowNum", rowInfo.get("internalRowNum_"+i));//排序号
      detailRow.put("list", rowInfo.get("list_"+i));//排序号
      detailRow.put("unit", rowInfo.get("unit_"+i));//单位
      detailRow.put("quot", rowInfo.get("quot_"+i));//调整率
      detailRow.put("gymcid", rowInfo.get("gymcid_"+i));//工序名称
      detailRow.put("gzzxid", rowInfo.get("gzzxid_"+i));//工作中心ID
      detailRow.put("jjff", rowInfo.get("jjff_"+i));//计件单价的方法。0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
      detailRow.put("desl", rowInfo.get("desl_"+i));//定额数量
      detailRow.put("deje", rowInfo.get("deje_"+i));//定额金额
      detailRow.put("lbjj", formatNumber(rowInfo.get("lbjj_"+i), priceFormat));//零部件价
      detailRow.put("hsj", formatNumber(rowInfo.get("hsj_"+i), priceFormat));//回收价
      detailRow.put("gxfdid", rowInfo.get("gxfdid_"+i));//工序分段ID
      detailRow.put("gdjg", formatNumber(rowInfo.get("gdjg_"+i), priceFormat));//工段价格
      detailRow.put("scgs", rowInfo.get("scgs_"+i));//生产工时
      detailRow.put("ddgs", rowInfo.get("ddgs_"+i));//等待工时
      detailRow.put("sfwx", rowInfo.get("sfwx_"+i));//是否外协
      detailRow.put("wxjg", formatNumber(rowInfo.get("wxjg_"+i), priceFormat));//外协价格
      detailRow.put("bz", rowInfo.get("bz_"+i));//备注
      detailRow.put("fctryid", rowInfo.get("fctryid_"+i));
      detailRow.put("zybl", rowInfo.get("zybl_"+i));
      detailRow.put("cpid", rowInfo.get("cpid_"+i));
    }
  }

  /*得到主表对象*/
  public final EngineDataSet getMaterTable()
  {
      return dsMasterTable;
  }

  /*得到从表表对象*/
  public final EngineDataSet getDetailTable(){return dsDetailTable;}
  /*得到临时表对象*/
 // public final EngineDataSet getTemp(){return dsTemp;}


  /*打开可选表*/
  private final void openTecnicTable()
   {
     String sql=combineSQL(BOM_SECTION_SQL, "?", new String[]{gylxID,gylxmxID});
     dsTecnictable.setQueryString(sql);
     if(dsTecnictable.isOpen())
       dsTecnictable.refresh();
     else
       dsTecnictable.open();
  }
  /*打开从表*/
  public final void openDetailTable()
  {
    String gylxid = dsMasterTable.getValue("gylxid");
    dsDetailTable.setQueryString(combineSQL(DETAIL_SQL, "?", new String[]{isMasterAdd ? "-1" : gylxid}));
    if(dsDetailTable.isOpen())
      dsDetailTable.refresh();
    else
      dsDetailTable.open();
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

  class ShowDetail implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
       masterRow = dsMasterTable.getInternalRow();
       //String gylx_id = dsMasterTable.getValue("gylxid");
       //打开从表
       openDetailTable();
     }
  }
  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isFromBom = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      //初始化查询信息
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      d_RowInfos = null;
      dsMasterTable.setQueryString(combineSQL(MASTER_SQL, "?", new String[]{""}));
      dsMasterTable.readyRefresh();
    }
  }

  /**
   * BOM表连接操作的触发类
   */
  class FromBom implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isFromBom = true;
      retuUrl = null;
      //初始化查询信息
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      d_RowInfos = null;
      RowMap rowInfo= getMasterRowinfo();

      String nodeid = data.getParameter("nodeid"); //cpid_dmsxid. node=1_1
      String[] cpid_propid = parseString(nodeid, "_");
      String sql = combineSQL(MASTER_SINGLE_SQL, "?", cpid_propid);
      dsMasterTable.setQueryString(sql);
      String cpid=String.valueOf(cpid_propid[0]);
      String dmsxid=String.valueOf(cpid_propid[1]);
      String sxsj=rowInfo.get("sxsj");
      String count="0";
      count = dataSetProvider.getSequence("select count(*) FROM sc_gylx WHERE cpid='"+cpid+"'");
      code=0;
      click=0;
      //if(count.equals("0"))
    //  {
   //     if(dsMasterTable.open())

    //    dsMasterTable.close();
   //   }

     // String GYLXMX_SQL="SELECT b.* FROM sc_gylx a,sc_gylxmx b WHERE a.gylxid=b.gylxid AND a.cpid='"+cpid+"'";
     // dsDetailTable.setQueryString(GYLXMX_SQL);

      if(dsMasterTable.isOpen())
      {
         dsDetailTable.openDataSet();
      }

      else
      {
      dsMasterTable.open();
      }
      int a=dsMasterTable.getRowCount();
      isMasterAdd = dsMasterTable.getRowCount() == 0;

      //String count = dataSetProvider.getSequence("select count(*) FROM sc_gylx WHERE cpid='"+cpid+"'");
       //  dsTemp.open();
       if(count.equals("0"))
        {
         isMasterAdd=true;
         dsMasterTable.insertRow(false);
         dsMasterTable.setValue("cpid",cpid);
         dsMasterTable.setValue("dmsxid",dmsxid);
         dsMasterTable.setValue("sxsj",sxsj);
         dsMasterTable.post();
       }
       else
       {
         isMasterAdd=false;
         dsMasterTable.close();
         setDataSetProperty(dsMasterTable, combineSQL(MASTER_SQL, "?", new String[]{""}));//
         if(dsMasterTable.open())
         dsMasterTable.refresh();
         else
          dsMasterTable.open();
         dsMasterTable.first();
         int length =dsMasterTable.getRowCount();
         for(int i=0; i<length;i++)
         {
           String aaaaa=dsMasterTable.getValue("cpid");
           if(dsMasterTable.getValue("cpid").equals(cpid))
           {
            masterRow = i;
           }
          dsMasterTable.next();

         }
        dsMasterTable.close();
        setDataSetProperty(dsMasterTable, combineSQL(MASTER_SQL, "?", new String[]{"AND sc_gylx.cpid='"+cpid+"'"}));
        if(dsMasterTable.open())
          dsMasterTable.refresh();
        else
          dsMasterTable.open();
        dsMasterTable.setValue("cpid",cpid);
        dsMasterTable.setValue("dmsxid",dmsxid);
        dsMasterTable.post();

        }

      openDetailTable();
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      m_RowInfo.put(dsMasterTable);
    }
  }

/*
  *返回操作的触发类*
  */

  class backlist implements Obactioner
    {
      public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
      {
         code=0;
         click=0;
         dsMasterTable.close();
         setDataSetProperty(dsMasterTable, combineSQL(MASTER_SQL, "?", new String[]{""}));
         dsMasterTable.readyRefresh();
         data.setMessage(showJavaScript("backlist();"));
      }
    }
/*
    *排序号改变的触发类
    */
    class listchange implements Obactioner
       {
         public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
         {
               HttpServletRequest request = data.getRequest();
               putDetailInfo(data.getRequest());
               RowMap detailrow = null;
               EngineDataSet detail = getDetailTable();
               int aaa=detail.getRowCount();
               detail.first();
               for(int i=0; i<detail.getRowCount(); i++)
               {
                 detailrow = (RowMap)d_RowInfos.get(i);
                 dtRow = Long.parseLong(detailrow.get("internalRowNum"));
                 detail.goToInternalRow(dtRow);
                 String list=detailrow.get("list");
                 String unit=detailrow.get("unit");
                 String quot=detailrow.get("quot");
                 String gymcid=detailrow.get("gymcid");
                 String gzzxid=detailrow.get("gzzxid");
                 String deje=detailrow.get("deje");
                 String jjff=detailrow.get("jjff");
                 String desl=formatNumber(detailrow.get("desl"), qtyFormat);
                 String lbjj=formatNumber(detailrow.get("lbjj"), priceFormat);
                 String hsj=formatNumber(detailrow.get("hsj"), priceFormat);
                 String gxfdid=detailrow.get("gxfdid");;
                 String gdjg=formatNumber(detailrow.get("gdjg"), priceFormat);
                 String scgs=detailrow.get("scgs");;
                 String ddgs=detailrow.get("ddgs");;
                 String sfwx=detailrow.get("sfwx");;
                 String wxjg=formatNumber(detailrow.get("wxjg"), priceFormat);
                 String bz=detailrow.get("bz");;


                 detail.setValue("list", list);//排序号
                 detail.setValue("unit", unit);//单位
                 detail.setValue("quot", quot);//调整率
                 detail.setValue("gymcid",gymcid);//工序名称
                 detail.setValue("gzzxid", gzzxid);//工作中心ID
                 detail.setValue("desl",desl);//定额数量
                 detail.setValue("deje", deje);//定额金额
                 detail.setValue("jjff", jjff);//0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
                 detail.setValue("lbjj",lbjj);//零部件价
                 detail.setValue("hsj", hsj);//回收价
                 detail.setValue("gxfdid", gxfdid);//工序分段ID
                 detail.setValue("gdjg", gdjg);//工段价格
                 detail.setValue("scgs", scgs);//生产工时
                 detail.setValue("ddgs", ddgs);//等待工时
                 detail.setValue("sfwx",sfwx);//是否外协
                 detail.setValue("wxjg", wxjg);//外协价格
                 detail.setValue("bz", bz);//备注
                 //保存用户自定义的字段
                 detail.post();
                 d_RowInfos.set(i,detail);
                 detail.next();
                }
              detail.first();
              for(int i=0; i<detail.getRowCount(); i++)
              {
               detail.setValue("list", String.valueOf(i+1));//排序号
               detail.next();
              }
               initRowInfo(false,false, true);
               }
           }
  /**
    * 主表选择产品操作的触发类
    */
   class cpselect implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       isCpchanged=true;
       d_RowInfos = null;
       EngineDataSet ds = getMaterTable();
       HttpServletRequest request = data.getRequest();
       String sxsj = request.getParameter("sxsj");
       String cpid = request.getParameter("cpid");
       String dmsxid = request.getParameter("dmsxid");
       if(dmsxid.equals(""))
         dmsxid="0";
       String count="0";
       count = dataSetProvider.getSequence("select count(*) FROM sc_gylx WHERE cpid='"+cpid+"'");
        String gylxid=null;
        if(!count.equals("0"))
        {
          isMasterAdd=false;
          ds.first();
          int length =ds.getRowCount();
          for(int i=0; i<length;i++)
          {
            String aaaaa=dsMasterTable.getValue("cpid");
            if(dsMasterTable.getValue("cpid").equals(cpid))
            {
             masterRow = i+1;
             gylxid=dsMasterTable.getValue("gylxid");
            }
           dsMasterTable.next();
          }
         ds.goToInternalRow(masterRow);
         ds.setValue("cpid",cpid);
         ds.setValue("dmsxid",dmsxid);
         ds.post();
        }
         else
         {
             isMasterAdd=true;
             dsMasterTable.insertRow(false);
             dsMasterTable.setValue("cpid",cpid);
             dsMasterTable.setValue("dmsxid",dmsxid);
             dsMasterTable.setValue("sxsj",sxsj);
             dsMasterTable.post();
         }
         String sql = combineSQL(DETAIL_SQL, "?", new String[]{isMasterAdd ? "-1" : gylxid});
         dsDetailTable.setQueryString(sql);
         if(dsDetailTable.isOpen())
           dsDetailTable.refresh();
         else
           dsDetailTable.open();
           initRowInfo(true, isMasterAdd, true);
           initRowInfo(false, isMasterAdd, true);
           m_RowInfo.put(dsMasterTable);
     }
   }



  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isMasterAdd = String.valueOf(ADD).equals(action);
      //dsMasterTable.open();
      String gylxid=dsMasterTable.getValue("gylxid");
      dsDetailTable.setQueryString(combineSQL(DETAIL_SQL, "?", new String[]{isMasterAdd ? "-1" : gylxid}));
     // dsTemp.open();
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
      }
      synchronized(dsDetailTable){
        openDetailTable();
        }
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
      data.setMessage(showJavaScript("toedit();"));
    }
  }

  /**
   * 选择工段触发时间
   * 工段相同，工段价格等于定额金额叠加
     */
  class Gdjg_Onchange implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      Hashtable table = new Hashtable(d_RowInfos.size()+1, 1);//Hash表
      for(int j=0; j<d_RowInfos.size();j++)
      {
        RowMap detailrow = (RowMap)d_RowInfos.get(j);
        String gxfdid = detailrow.get("gxfdid");
        String deje = detailrow.get("deje");
        BigDecimal curValue = isDouble(deje) ? new BigDecimal(deje) : new BigDecimal(0);
        BigDecimal total = (BigDecimal)table.get(gxfdid);//J=0时Hash表中没有数据
        if(total == null)//J=0时total等于null
          total = curValue;
        else
        total = total.add(curValue);
        detailrow.put("gdjg", total.toString());
        table.put(gxfdid, total);//推入Hash表中。key为工序分段Id,value等于total
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
      HttpServletRequest request = data.getRequest();
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getMaterTable();
      RowMap rowInfo = getMasterRowinfo();
      code=0;
      click=0;
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
      String gylxid = null;
      if(isMasterAdd){
        if(!isFromBom&&!isCpchanged)
        ds.insertRow(false);
        gylxid = dataSetProvider.getSequence("s_sc_gylx");
        ds.setValue("gylxid", gylxid);
      }
      else
      {
        ds.goToInternalRow(masterRow);
        gylxid = ds.getValue("gylxid");
      }

      //保存从表的数据
      RowMap detailrow = null;
      EngineDataSet detail = getDetailTable();
      detail.first();

          //最大提前期
      BigDecimal maxTime = new BigDecimal(0);
      for(int i=0; i<detail.getRowCount(); i++)
      {
                 detailrow = (RowMap)d_RowInfos.get(i);
                 dtRow = Long.parseLong(detailrow.get("internalRowNum"));
                 detail.goToInternalRow(dtRow);
                 String list=detailrow.get("list");
                 String unit=detailrow.get("unit");
                 String quot=detailrow.get("quot");
                 String gymcid=detailrow.get("gymcid");
                 String gzzxid=detailrow.get("gzzxid");
                 String deje=detailrow.get("deje");
                 String jjff=detailrow.get("jjff");
                 String desl=formatNumber(detailrow.get("desl"), qtyFormat);
                 String lbjj=formatNumber(detailrow.get("lbjj"), priceFormat);
                 String hsj=formatNumber(detailrow.get("hsj"), priceFormat);
                 String gxfdid=detailrow.get("gxfdid");;
                 String gdjg=formatNumber(detailrow.get("gdjg"), priceFormat);
                 String scgs=detailrow.get("scgs");;
                 String ddgs=detailrow.get("ddgs");;
                 String sfwx=detailrow.get("sfwx");;
                 String wxjg=formatNumber(detailrow.get("wxjg"), priceFormat);
                 String bz=detailrow.get("bz");;
                 String fctryid=detailrow.get("fctryid");

                  //新添的记录
                  if(isMasterAdd)
                  {
                  detail.setValue("gylxid", gylxid);
                  }
                  detail.setValue("list", list);//排序号
                  detail.setValue("unit", unit);//单位
                  detail.setValue("quot", quot);//调整率
                  detail.setValue("gymcid",gymcid);//工序名称
                  detail.setValue("gzzxid", gzzxid);//工作中心ID
                  detail.setValue("desl",desl);//定额数量
                  detail.setValue("deje", deje);//定额金额
                  detail.setValue("jjff", jjff);//0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
                  detail.setValue("lbjj",lbjj);//零部件价
                  detail.setValue("hsj", hsj);//回收价
                  detail.setValue("gxfdid", gxfdid);//工序分段ID
                  detail.setValue("gdjg", gdjg);//工段价格
                  detail.setValue("scgs", scgs);//生产工时
                  detail.setValue("ddgs", ddgs);//等待工时
                  detail.setValue("sfwx",sfwx);//是否外协
                  detail.setValue("wxjg", wxjg);//外协价格
                  detail.setValue("bz", bz);//备注
                  detail.setValue("fctryid", fctryid);
                  detail.setValue("zybl", detailrow.get("zybl"));
                  detail.setValue("cpid", detailrow.get("cpid"));
                 //保存用户自定义的字段
                  detail.post();
                 //当前提前期
                  BigDecimal rowTime = detail.getBigDecimal("scgs").add(detail.getBigDecimal("ddgs"));
                  maxTime = rowTime.compareTo(maxTime) > 0 ? rowTime : maxTime;
                  detail.next();
                  }
                  initRowInfo(false,false, true);
                  detail.setSort(new SortDescriptor("", new String[]{"list"}, new boolean[]{false}, null, 0));
                  detail.first();
                  for(int i=0; i<dsDetailTable.getRowCount(); i++)
                 {
                  detailrow = (RowMap)d_RowInfos.get(i);
                  dtRow = Long.parseLong(detailrow.get("internalRowNum"));
                  detail.goToInternalRow(dtRow);
                  String list=String.valueOf(i+1);
                  detail.setValue("list", list);//排序号
                  detail.post();
                  detail.next();
                 }
                 initRowInfo(false,false, true);
      String cpid=rowInfo.get("cpid");
      String dmsxid=rowInfo.get("dmsxid");
      String sxsj=rowInfo.get("sxsj");
      if(dmsxid.equals(""))
      dmsxid="0";

      //保存主表数据
      if(!isFromBom&&!isCpchanged)
      {
       ds.setValue("cpid", cpid);//cpID
       ds.setValue("dmsxid", dmsxid);//规格属性id
      }
       ds.setValue("sxsj", sxsj);//生效时间

       ds.setValue("gylxzid", rowInfo.get("gylxzid"));

     //保存用户自定义的字段
       ds.post();



      //提取提前期数据
      String timeSql = combineSQL(AHEAD_TIME_SQL, "@", new String[]{cpid, dmsxid});
      EngineDataSet dsTime = new EngineDataSet();
      setDataSetProperty(dsTime, timeSql);
      dsTime.openDataSet();
      if(dsTime.getRowCount() ==0)
      {
        dsTime.insertRow(false);
        dsTime.setValue("cpid", cpid);
        dsTime.setValue("dmsxid", dmsxid);

      }
      dsTime.setValue("ahead_time", maxTime.toString());

      dsTime.post();
      //
      String totalSQL = combineSQL(TOTAL_TIME_SQL, "@", new String[]{cpid, dmsxid});
      dsTime.setAfterResolvedSQL(new String[]{totalSQL});
      try{
       ds.saveDataSets(new EngineDataSet[]{ds, detail, dsTime,dsTecnictable});
        isMasterAdd=false;
      }
      catch(Exception ex){
        log.warn("post",ex);
        if(dsDetailTable.isOpen() && dsDetailTable.changesPending())
          dsDetailTable.reset();
         data.setMessage(showJavaScript("alert('该工艺路线已存在')"));
         return;
      }
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE);
      if(!isFromBom && String.valueOf(POST_CONTINUE).equals(action)){//
        isMasterAdd = true;
        initRowInfo(true, true, true);
        detail.empty();//清空从表数据集
        initRowInfo(false, true, true);//重新初始化从表的各行信息
      }
      //isFromBom ? "save" : "save return"
      else if(String.valueOf(POST).equals(action)) //save and return
      {
        if(isFromBom)
        isMasterAdd=false;
       // if(!isFromBom)
        //  data.setMessage(showJavaScript("backlist();"));
      }
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
      double zzybl=0;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        int row = i+1;
        detailrow = (RowMap)d_RowInfos.get(i);
        String pxh=detailrow.get("list");
        if(pxh.equals(""))
          return showJavaScript("alert('排序号不能为空')");
        String gymcid = detailrow.get("gymcid");
        if(gymcid.equals(""))
          return showJavaScript("alert('工序不能为空')");
        if(list.contains(gymcid))
          return showJavaScript("alert('工序不能重复')");
        else
          list.add(gymcid);
        String desl = detailrow.get("desl");
        if(desl.length() > 0 &&(temp = checkNumber(desl, "第"+row+"行定额数量")) != null)
          return temp;
        String deje = detailrow.get("deje");
        if((temp = checkNumber(deje, "第"+row+"行计件价格")) != null)
          return temp;
        String zybl = detailrow.get("zybl");
        if((temp = checkNumber(zybl, "第"+row+"行占用比例")) != null)
          return temp;
        zzybl = zzybl+Double.parseDouble(zybl);
        if(deje.equals("0"))
          return showJavaScript("alert('计件价格不能为零')");
        String lbjj = detailrow.get("lbjj");
        if(lbjj.length() > 0 &&(temp = checkNumber(lbjj, "第"+row+"行零部件价")) != null)
          return temp;
        String hsj = detailrow.get("hsj");
        if(hsj.length() > 0 &&(temp = checkNumber(hsj, "第"+row+"行回收价")) != null)
          return temp;
        String gdjg = detailrow.get("gdjg");
        if(gdjg.length() > 0 &&(temp = checkNumber(gdjg, "第"+row+"行工段价格")) != null)
          return temp;
        String wxj = detailrow.get("wxj");
        if(wxj.length() > 0 &&(temp = checkNumber(wxj, "第"+row+"行外协价")) != null)
          return temp;
      }
      if(zzybl!=100)
        return showJavaScript("alert('占用比例之和必须为100')");
      return null;
    }

    /**
     * 校验主表表表单信息从表输入的信息的正确性
     * @return null 表示没有信息,校验通过
     */
    private String checkMasterInfo() throws Exception
    {
      RowMap rowInfo = getMasterRowinfo();
      String cpid = rowInfo.get("cpid");
      if(cpid.equals(""))
        return showJavaScript("alert('产品不能为空！');");
      String dmsx = rowInfo.get("dmsxid");
      if(dmsx.equals(""))
          dmsx="0";


     // if(gylxlxid.equals(""))
    //    return showJavaScript("alert('工艺路线类型不能为空！');");
      String count = dataSetProvider.getSequence("select count(*) FROM sc_gylx WHERE cpid='"+cpid+"'and dmsxid='"+dmsx+"'");
      if(!count.equals("0") && isMasterAdd )
        return showJavaScript("alert('该产品的工艺路线已存在')");
      return null;
    }
  }
  /**
   * 选择不同工艺路线类型触发的事件
  class Technics_Change implements Obactioner
  {
    private EngineDataSet lsMasterData = null;//零时主表数据集

    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(data.getRequest());
      m_RowInfo.put(req);
      RowMap rowInfo = getMasterRowinfo();
      String cpid = rowInfo.get("cpid");
      String gylxlxid = rowInfo.get("gylxlxid");
      if(gylxlxid.equals(""))
        return;
      String sql = combineSQL(TECHNICS_CHANGE_SQL, "?", new String[]{cpid,gylxlxid});
      if(lsMasterData == null)
      {
        lsMasterData = new EngineDataSet();
        setDataSetProperty(lsMasterData, null);
      }
      lsMasterData.setQueryString(sql);
      if(!lsMasterData.isOpen())
        lsMasterData.openDataSet();
      else
        lsMasterData.refresh();
      if(lsMasterData.getRowCount()<1)
        return;
      String gylxid = lsMasterData.getValue("gylxid");
      if(gylxid.equals(""))
        return;
      dsDetailTable.setQueryString(DETAIL_SQL+gylxid);
      if(dsDetailTable.isOpen())
        dsDetailTable.refresh();
      else
        dsDetailTable.open();
      initRowInfo(true, isMasterAdd, true);
      initRowInfo(false, isMasterAdd, true);
    }
  }
     */
  /**
   * 主从表同时添加（从表数据复制给别的产品）操作的触发类
   */
  class Master_Copy implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      EngineDataSet ds = getMaterTable();
      RowMap rowinfo =getMasterRowinfo();
      rowinfo.put(req);
      String gylxid = rowinfo.get("gylxid");//工艺路线ID
      String sxsj = rowinfo.get("sxsj");//生效时间
      String dmsxid = rowinfo.get("dmsxid");
      String gylxlxid = rowinfo.get("gylxlxid");//工艺路线类型ID
      putDetailInfo(data.getRequest());


      String multiIdInput = req.getParameter("multiIdInput");//得到所要复制给的产品字符串，用逗号分割
      if(multiIdInput.length() == 0)
        return;
      String[] cpIDs = parseString(multiIdInput,",");//得到所要复制给的产品数组

      EngineDataSet masterData = new EngineDataSet();//new一个新的主表数据集
      EngineDataSet detailData = new EngineDataSet();//new一个新的从表数据集
      //工艺路线复制给别的产品，判断别的产品的此工艺路线类型在工艺路线中是否已经存在
      //setDataSetProperty(masterData, combineSQL(MASTER_COPY_SQL, "?", new String[]{multiIdInput}));
      setDataSetProperty(masterData, MASTER_STRUT_SQL);
      masterData.openDataSet();
      //EngineRow locateGoodsRow = new EngineRow(masterData, "cpid");//定位数据集

      //setDataSetProperty(detailData, combineSQL(DETAIL_COPY_SQL, "?" , new String[]{getWhereIn(masterData,"gylxid", "-2")}));
      setDataSetProperty(detailData,DETAIL_STRUT_SQL);
      detailData.setSequence(new SequenceDescriptor(new String[]{"gylxmxid"}, new String[]{"s_sc_gylxmx"}));
      detailData.openDataSet();
      //detailData.deleteAllRows();//打开要复制给产品（即所选择产品）工艺路线类型存在于工艺路线中的从表数据并删除


      for(int j=0; j<cpIDs.length; j++)
      {
        if(cpIDs[j].equals("-1"))
           continue;
        //locateGoodsRow.setValue(0, cpIDs[j]);
        boolean isFind = false;//masterData.locate(locateGoodsRow, Locate.FIRST);//判断是否在工艺路线主表中定位到了该选择产品并且工艺路线类型也和复制的相同
        String gylxidNew = dataSetProvider.getSequence("s_sc_gylx");//isFind ? masterData.getValue("gylxid") : dataSetProvider.getSequence("s_sc_gylx");//如果定为到了工艺路线ID直接从数据集中取
        if(!isFind)//如果没有定位到在工艺路线主表中插入一行信息
        {
        masterData.insertRow(false);
        masterData.setValue("gylxid", gylxidNew);
        masterData.setValue("cpid", cpIDs[j]);
        }
        if(isFind)
        {
          engine.project.LookUp CpBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_PRODUCT);
          RowMap Technicrow = CpBean.getLookupRow(cpIDs[j]);
          CpBean.regData(new String[]{cpIDs[j]});
          RowMap CpRow = CpBean.getLookupRow(cpIDs[j]);
          data.setMessage(showJavaScript("alert('编码为"+CpRow.get("cpbm")+"的产品工艺路线已经存在，请删除该工艺路线再进行复制！');"));
          return;
        }
        masterData.setValue("sxsj",sxsj);//设置生效时间
        //masterData.setValue("dmsxid",dmsxid);
        masterData.setValue("gylxlxid",gylxlxid);//设置工艺路线类型
        masterData.post();

        dsDetailTable.first();
        for(int i=0; i<dsDetailTable.getRowCount(); i++)
        {
          detailData.insertRow(false);
          detailData.setValue("gylxmxid", "-1");
          detailData.setValue("gylxid", gylxidNew);
          detailData.setValue("list", dsDetailTable.getValue("list"));//排序号
          detailData.setValue("unit", dsDetailTable.getValue("unit"));//单位
          detailData.setValue("quot", dsDetailTable.getValue("quot"));//调整率
          detailData.setValue("gxfdid", dsDetailTable.getValue("gxfdid"));
          detailData.setValue("gzzxid", dsDetailTable.getValue("gzzxid"));
          detailData.setValue("gymcid", dsDetailTable.getValue("gymcid"));
          detailData.setValue("desl", dsDetailTable.getValue("desl"));
          detailData.setValue("deje", dsDetailTable.getValue("deje"));
          detailData.setValue("lbjj", dsDetailTable.getValue("lbjj"));
          detailData.setValue("hsj", dsDetailTable.getValue("hsj"));
          detailData.setValue("gdjg", dsDetailTable.getValue("gdjg"));
          detailData.setValue("scgs", dsDetailTable.getValue("scgs"));
          detailData.setValue("ddgs", dsDetailTable.getValue("ddgs"));
          detailData.setValue("sfwx", dsDetailTable.getValue("sfwx"));
          detailData.setValue("wxjg", dsDetailTable.getValue("wxjg"));
          detailData.setValue("bz", dsDetailTable.getValue("bz"));
          detailData.setValue("fctryid", dsDetailTable.getValue("fctryid"));
          detailData.setValue("zybl",  dsDetailTable.getValue("zybl"));
          detailData.setValue("cpid",  dsDetailTable.getValue("cpid"));
          detailData.post();

          dsDetailTable.next();
        }
        masterData.saveDataSets(new EngineDataSet[]{masterData, detailData}, null);//主表信息和从表信息同时保存到数据库中
      }
      detailData.closeDataSet();
      masterData.closeDataSet();
      data.setMessage(showJavaScript("backlist();"));
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
        data.setMessage(showJavaScript("backlist();"));
        return;
      }
      code=0;
      EngineDataSet ds = getMaterTable();
      ds.goToInternalRow(masterRow);
      String gylxid = ds.getValue("gylxid");
      String countA = dataSetProvider.getSequence("SELECT COUNT(*) FROM sc_jhmx WHERE gylxid="+gylxid);
      String countB = dataSetProvider.getSequence("SELECT COUNT(*) FROM sc_wlxqjhmx WHERE gylxid="+gylxid);
      String countC = dataSetProvider.getSequence("SELECT COUNT(*) FROM sc_piecewage WHERE gylxid="+gylxid);
      //String countD = dataSetProvider.getSequence("SELECT COUNT(*) FROM sc_jgdmx WHERE gylxid="+gylxid);
      //String countE = dataSetProvider.getSequence("SELECT COUNT(*) FROM sc_grgzlmx WHERE gylxid="+gylxid);
      //String countF = dataSetProvider.getSequence("SELECT COUNT(*) FROM sc_bmgzlmx WHERE gylxid="+gylxid);
      //String countG = dataSetProvider.getSequence("SELECT COUNT(*) FROM sc_gzzgzlmx WHERE gylxid="+gylxid);
      if(!countA.equals("0") || !countB.equals("0")||!countC.equals("0")){
        data.setMessage(showJavaScript("alert('该工艺路线已被引用不能删除！');"));
        return;
      }
      else{
      dsDetailTable.deleteAllRows();
      ds.deleteRow();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable}, null);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_STORE);
      //
      d_RowInfos.clear();
      m_RowInfo.clear();
     // d_RowInfos = null;
      if(!isFromBom)
      {
      code=0;
      click=0;
      data.setMessage(showJavaScript("backlist();"));
      }
      }
    }
  }

  /**
   *  查询操作
   */
  class Master_Search implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      String sxsj = request.getParameter("sxsj");
      String sc_gylx$cpid = request.getParameter("sc_gylx$cpid");
      initQueryItem(data.getRequest());
      fixedQuery.setSearchValue(data.getRequest());
      String SQL = fixedQuery.getWhereQuery();
      if(SQL.length() > 0)
        SQL = " AND "+SQL;
      SQL = combineSQL(MASTER_SQL, "?", new String[]{SQL});
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
      //EngineDataSet detail = dsDetailTable;
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("sxsj"), null, null, null, null, ">="),
       // new QueryColumn(master.getColumn("gylxlxid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("gylxid"), "VW_SC_GYLXQUERY", "gylxid", "product","product","like"),
        new QueryColumn(master.getColumn("gylxid"), "VW_SC_GYLXQUERY", "gylxid", "cpbm","cpbm","like"),
       // new QueryColumn(master.getColumn("gylxid"), "VW_SC_GYLXQUERY", "gylxid", "sxz","sxz","like"),
      });
      fixedQuery.addShowColumn("sc_gylx", new QueryColumn[]{
        new QueryColumn(master.getColumn("cpid"), null, null, null, null, "="),
      });
      isInitQuery = true;
    }
  }

  /**
   *  从表增加操作
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
      if(!isMasterAdd)
      ds.goToInternalRow(masterRow);
      String gylxid = ds.getValue("gylxid");
      engine.project.LookUp prodBean = engine.project.LookupBeanFacade.getInstance(req, engine.project.SysConstant.BEAN_PRODUCT);
      RowMap prodRow = prodBean.getLookupRow(ds.getValue("cpid"));
      String gxdw=prodRow.get("jldw");
      detail.insertRow(false);
      detail.setValue("gylxid", isMasterAdd ? "" : gylxid);
      detail.post();
      if(!isMasterAdd)
      {
      String count="0";
      count = dataSetProvider.getSequence("select count(*) FROM sc_gylxmx WHERE gylxid='"+gylxid+"'");
      code=Integer.parseInt(count);
      click=click+1;
      code=code+click;
      }
      else
      code=code+1;
      RowMap detailrow = new RowMap();
      detailrow.put("list", String.valueOf(code));
      detailrow.put("internalRowNum",String.valueOf(detail.getInternalRow()));
      detailrow.put("quot", "1");
      detailrow.put("jjff", "0");
      detailrow.put("sfwx","0");
      detailrow.put("unit",gxdw);
      d_RowInfos.add(detailrow);
    }
  }

  /**
   *  从表删除操作
   */
  class Detail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
     HttpServletRequest request = data.getRequest();
      putDetailInfo(data.getRequest());
      EngineDataSet ds = getDetailTable();
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      ds.first();
      for(int i=0; i<ds.getRowCount(); i++)
             {
              String aaa=data.getParameter("gymcid_"+i);
              ds.setValue("list", String.valueOf(i+1));//排序号
              ds.setValue("unit", data.getParameter("unit_"+i));//单位
              ds.setValue("quot", data.getParameter("quot_"+i));//调整率
              ds.setValue("gymcid",data.getParameter("gymcid_"+i));//工序名称
              ds.setValue("gzzxid", data.getParameter("gzzxid_"+i));//工作中心ID
              ds.setValue("desl",formatNumber(data.getParameter("desl_"+i),qtyFormat));//定额数量
              ds.setValue("deje", formatNumber(data.getParameter("deje_"+i),priceFormat));//定额金额
              ds.setValue("jjff", data.getParameter("jjff_"+i));//0=计量单位计算1=生产单位计算2=换算单位计算3=领料的生产单位除参数(参数指系统参数的宽度)
              ds.setValue("lbjj",formatNumber(data.getParameter("lbjj_"+i),priceFormat));//零部件价
              ds.setValue("hsj", formatNumber(data.getParameter("hsj_"+i),priceFormat));//回收价
              ds.setValue("gxfdid", data.getParameter("gxfdid_"+i));//工序分段ID
              ds.setValue("gdjg", formatNumber(data.getParameter("gdjg_"+i),priceFormat));//工段价格
              ds.setValue("scgs", data.getParameter("scgs_"+i));//生产工时
              ds.setValue("ddgs", data.getParameter("ddgs_"+i));//等待工时
              ds.setValue("sfwx",data.getParameter("sfwx_"+i));//是否外协
              ds.setValue("wxjg", data.getParameter("wxjg_"+i));//外协价格
              ds.setValue("bz", data.getParameter("bz_"+i));//备注
              ds.post();
              d_RowInfos.add(ds);
              ds.next();
             }
      //删除临时数组的一列数据
      code=code-1;
      click=0;
      d_RowInfos.remove(rownum);
      ds.goToRow(rownum);
      ds.deleteRow();
      ds.first();
      for(int i=0; i<ds.getRowCount(); i++)
             {
              ds.setValue("list", String.valueOf(i+1));//排序号
              ds.next();
             }
      initRowInfo(false,false, true);
    }
  }
}