package engine.erp.store;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import java.util.*;
import engine.html.*;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.project.*;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
import javax.swing.*;

public final class B_Package extends BaseAction implements Operate
{
  private static final String MASTER_STRUT_SQL = "SELECT * FROM kc_package WHERE isdelete=0 order by package_code";   //取主表的结构
  private static final String MASTER_SQL    = "SELECT * FROM kc_package where isdelete=0 ? order by package_code";    //对主表的参数化查询
  /*以下是对从表的条件查询,需提供ypxxID参数.*/
  private static final String detail_SQL    = "SELECT a.* FROM kc_package_detail a WHERE  a.package_id=? ";//应聘人员培训课程
  //操作
  public  static final String  SHOW_DETAIL          = "12500";//查看bottom
  public static final String PXKC_ADD    = "9000";    //应聘培训课程新增
  public static final String PXKC_DEL    = "9001";    //应聘培训课程删除操作
  public  static final String INVOICE_OVER = "3423513";//完成
  public  static final String OVER = "3423513";
  public static final String VIEW_DETAIL = "1055";   //主从明细
  public static final String OPERATE_SEARCH = "1066";//主表查询操作
  public static final String DELETE_RETURN = "1067"; //主从删除操作

  /*主从表的数据集*/
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表培训课程
  private EngineDataSet dsopenDetailTable  = new EngineDataSet();
  private boolean isMasterAdd = true;                          //主表是否在添加状态
  private long    masterRow = 0;                               //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap();                 //主表添加行或修改行的引用
  /*把从表数据放在ArrayList中,这样可以保存多行信息来对应主表*/
  public ArrayList arraylist_rl_pxkc  = null;//从表应聘人员工作经历
  public ArrayList arraylist_detail  = null;//用于打印
  private boolean isInitQuery = false;                         //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  private String package_id = null;
  public int s_package_id=0;
  /**
   *运单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_Package getInstance(HttpServletRequest request)
  {
    B_Package b_PackageBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_PackageBean";
      b_PackageBean = (B_Package)session.getAttribute(beanName);
      if(b_PackageBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);
        b_PackageBean = new B_Package();
        session.setAttribute(beanName, b_PackageBean);
      }
    }
    return b_PackageBean;
  }

  /**
   * 构造函数
   */
  private B_Package()
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
    setDataSetProperty(dsDetailTable, null);

    //dsMasterTable.setSequence(new SequenceDescriptor(new String[]{"trans_code"}, new String[]{"SELECT pck_base.billNextCode('kc_package','trans_code') from dual"}));
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"package_id"}, new boolean[]{false}, null, 0));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"packagedetailid"}, new String[]{"S_kc_package_detail"}));
    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());//初始化
    addObactioner(String.valueOf(OPERATE_SEARCH), new Master_Search());//定制查询
    addObactioner(String.valueOf(PXKC_ADD), new Rl_Pxkc_adddel());//新增删除操作
    addObactioner(String.valueOf(PXKC_DEL), new Rl_Pxkc_adddel());//新增删除操作
    addObactioner(String.valueOf(VIEW_DETAIL), masterAddEdit);//修改主表,及其对应的从表
    addObactioner(String.valueOf(DELETE_RETURN), new Master_Delete());//删除主表某一行,及其对应的从表
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(INVOICE_OVER), new Invoice_Over());//完成
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(SHOW_DETAIL), new Show_Detail());
   // addObactioner(String.valueOf(COUNT_WET), new count_wet());

    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
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

    log = null;
    m_RowInfo = null;
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
   * 初始化行信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   * 保存继续
   * 主表新增
   * 主表编辑
   */
  private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否是主表
    if(isMaster)
    {
      if(isInit && m_RowInfo.size() > 0)
        m_RowInfo.clear();
      if(!isAdd)
        m_RowInfo.put(getMaterTable());
      else
      {
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        m_RowInfo.put("trans_date",today);
        //m_RowInfo.put("trans_code",trans_code);
        m_RowInfo.put("state","0" );

      }
    }

      openDetailTable();
      arraylist_rl_pxkc=putDetailToArraylist(dsDetailTable,arraylist_rl_pxkc);



  }
  /**
   *把从表数据集数据推入到ArrayList中
   *
   * */
  private final ArrayList putDetailToArraylist(EngineDataSet dsDetail,ArrayList arrlist)
  {
    arrlist = new ArrayList(dsDetail.getRowCount());
    dsDetail.first();
    for(int i=0; i<dsDetail.getRowCount(); i++)
    {
      RowMap row = new RowMap(dsDetail);
      arrlist.add(row);
      dsDetail.next();
    }
    return arrlist;
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
    rowInfo.put(request);

    int rownum=arraylist_rl_pxkc.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)arraylist_rl_pxkc.get(i);
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//
      detailRow.put("dmsxid", rowInfo.get("dmsxid_"+i));//

      detailRow.put("sl", rowInfo.get("sl_"+i));//
      detailRow.put("bz", rowInfo.get("bz_"+i));//
      arraylist_rl_pxkc.set(i,detailRow);
    }
  }
  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /*得到从表表对象*/
  public final EngineDataSet getPxkcTable(){return dsDetailTable;}
  public final EngineDataSet getdetailTable(){return dsopenDetailTable;}

/*打开从表*/
  public final void openDetailTable()
  {

    /*应聘人员工作经历*/
    dsDetailTable.setQueryString(combineSQL(detail_SQL,"?",new String[]{package_id}));
    if(dsDetailTable.isOpen())
      dsDetailTable.refresh();
    else
      dsDetailTable.open();

  }

  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到应聘人员工作经历从表的多行信息*/
  public final RowMap[] getPxkcRowinfos() {
    RowMap[] rows = new RowMap[arraylist_rl_pxkc.size()];
    arraylist_rl_pxkc.toArray(rows);
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
   *
   *初始化从表信息
   */
  public void initArrayList()
  {
    if(arraylist_rl_pxkc!=null){
      if(arraylist_rl_pxkc.size()>0)
      {
        arraylist_rl_pxkc.clear();
      }
    }

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
      HttpServletRequest request = data.getRequest();

      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();

      dsMasterTable.setQueryString(combineSQL(MASTER_SQL, "?", new String[]{ ""}));
      if(!dsMasterTable.isOpen())
      dsMasterTable.openDataSet();
      else
       dsMasterTable.refresh();

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
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        package_id = dsMasterTable.getValue("package_id");
        initRowInfo(true, false, true);
        initRowInfo(false, false, true);
      }
      if(isMasterAdd){
        package_id = String.valueOf(--s_package_id);

        m_RowInfo.clear();
        initArrayList();

        initRowInfo(true, isMasterAdd, true);
        initRowInfo(false, isMasterAdd, true);


      }
      //打开从表
      data.setMessage(showJavaScript("toDetail();"));

    }
  }
  /**
   * 完成
   */
  class Invoice_Over implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      dsMasterTable.setValue("state","8");

      dsMasterTable.saveChanges();
      initRowInfo(true,false,false);
    }
  }
  /**
   * 主表保存操作的触发类
   */
  class Master_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //把网页中的从表的信息推入ArrayList中
      putDetailInfo(data.getRequest());
       String isSendcar=null;
      //得到主表的数据集
      EngineDataSet ds = getMaterTable();
      //所要修改或查询的主表的一条记录信息
      RowMap rowInfo = getMasterRowinfo();
      RowMap detailrow = null;
      EngineDataSet detail_rl_pxkc = getPxkcTable();

      //校验主表
      if (rowInfo.get("package_name").equals(""))
      {
        data.setMessage(showJavaScript("alert('方案名称不能为空!')"));
        return;
      }
      //校验主表
      if (rowInfo.get("funditemid").equals(""))
      {
        data.setMessage(showJavaScript("alert('包装类别不能为空!')"));
        return;
      }

      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      //得到主表主键值
      if(isMasterAdd){
        ds.insertRow(false);
        package_id = dataSetProvider.getSequence("S_kc_package") ;
        ds.setValue("package_id", package_id);
      }else
        package_id = ds.getValue("package_id");
      //保存从表的数据

      detail_rl_pxkc.first();
      for(int i=0; i<arraylist_rl_pxkc.size(); i++)
      {
        detailrow = (RowMap)arraylist_rl_pxkc.get(i);
        detail_rl_pxkc.setValue("package_id", package_id);


        detail_rl_pxkc.setValue("cpid", detailrow.get("cpid"));
        detail_rl_pxkc.setValue("dmsxid", detailrow.get("dmsxid"));//
        detail_rl_pxkc.setValue("sl", detailrow.get("sl"));//
        detail_rl_pxkc.setValue("bz", detailrow.get("bz"));//


        detail_rl_pxkc.post();

        detail_rl_pxkc.next();

      }
      //保存主表数据

      ds.setValue("package_code", rowInfo.get("package_code"));//
       ds.setValue("funditemid", rowInfo.get("funditemid"));//
      ds.setValue("package_name", rowInfo.get("package_name"));//


      ds.post();

      ds.saveDataSets(new EngineDataSet[]{ds, detail_rl_pxkc,}, null);
      //刷新lookup的数据集，保持数据的同步.入参是相应的lookup名称
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PACKAGE);
      if(String.valueOf(POST_CONTINUE).equals(action))
      {
        isMasterAdd = true;
        initRowInfo(true, true, true);//重新初始化从表的各行信息

        m_RowInfo.clear();
        initArrayList();

        initRowInfo(true,false,false);;//重新初始化从表的各行信息
      }
      else if(String.valueOf(POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
    }


  }
  /**
   * 主表删除操作
   */
  class Master_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getMaterTable();
      if(!action.equals(String.valueOf(DEL)))
      {
        //在主从明细里执行删除操作.
        if(isMasterAdd){
          data.setMessage(showJavaScript("backList();"));//主从表新增,还未保存时,
          return;
        }
        dsMasterTable.goToInternalRow(masterRow);
      }
      else
      {
        //在主表的列表里执行删除操作
        String rownum=data.getRequest().getParameter("rownum");
        ds.goToRow(Integer.parseInt(rownum));
      }


      ds.setValue("isdelete","1");
      ds.post();
      ds.saveChanges();
      ds.refresh();

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
      SQL = combineSQL(MASTER_SQL, "?", new String[]{ SQL});
       dsMasterTable.setQueryString(SQL);

      if(!dsMasterTable.isOpen())
        dsMasterTable.openDataSet();
      else
        dsMasterTable.refresh();

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
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("package_code"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("package_name"), null, null, null, null, "like")



      });
      isInitQuery = true;
    }
  }
  //02.16 16:23 新增 新增一个当页面上单击事件发生时想要查看明细资料 这个单击事件的Show_Detail类 . yjg
 /**
 * 显示从表的列表信息
 */
class Show_Detail implements Obactioner
{
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
     masterRow = dsMasterTable.getInternalRow();
     package_id = dsMasterTable.getValue("package_id");
     //打开从表
     openDetailTable();
   }
  }

  /**
   * 0号
   *
   *
   * */
  class Rl_Pxkc_adddel  implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(action.equals(PXKC_ADD))
      {
        putDetailInfo(data.getRequest());
        if(!isMasterAdd)
        {
          dsMasterTable.goToInternalRow(masterRow);
          package_id = dsMasterTable.getValue("package_id");
        }else
          package_id = package_id;
        dsDetailTable.insertRow(false);
        dsDetailTable.setValue("package_id",package_id);
        dsDetailTable.post();
        RowMap zg_Temp_Row = new RowMap(dsDetailTable);
        arraylist_rl_pxkc.add(zg_Temp_Row);
      }
      if(action.equals(PXKC_DEL))
      {
        putDetailInfo(data.getRequest());
        int rownum=Integer.parseInt(data.getRequest().getParameter("rownum"));
        arraylist_rl_pxkc.remove(rownum);
        dsDetailTable.goToRow(rownum);
        dsDetailTable.deleteRow();

      }

    }
  }
}
