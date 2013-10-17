package engine.erp.sale;

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

public final class B_Transport extends BaseAction implements Operate
{
  private static final String MASTER_STRUT_SQL = "SELECT * FROM xs_transport WHERE 1<>1";   //取主表的结构
  private static final String MASTER_SQL    = "SELECT * FROM xs_transport where 1=1 ? order by trans_code";    //对主表的参数化查询
  /*以下是对从表的条件查询,需提供ypxxID参数.*/
  private static final String PXKC_SQL    = "SELECT * FROM xs_trans_detail WHERE transport_id= ";//应聘人员培训课程
  //操作
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
  private boolean isMasterAdd = true;                          //主表是否在添加状态
  private long    masterRow = 0;                               //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap();                 //主表添加行或修改行的引用
  /*把从表数据放在ArrayList中,这样可以保存多行信息来对应主表*/
  public ArrayList arraylist_rl_pxkc  = null;//从表应聘人员工作经历
  private boolean isInitQuery = false;                         //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  private String transport_id = null;
  public int s_transport_id=0;
  /**
   *运单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_Transport getInstance(HttpServletRequest request)
  {
    B_Transport b_TransportBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_TransportBean";
      b_TransportBean = (B_Transport)session.getAttribute(beanName);
      if(b_TransportBean == null)
      {
      //引用LoginBean
      LoginBean loginBean = LoginBean.getInstance(request);
      b_TransportBean = new B_Transport();
      session.setAttribute(beanName, b_TransportBean);
      }
    }
    return b_TransportBean;
  }

  /**
   * 构造函数
   */
  private B_Transport()
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
    setDataSetProperty(dsMasterTable, combineSQL(MASTER_SQL,"?",new String[]{""}));
    setDataSetProperty(dsDetailTable, null);
    dsMasterTable.setSort(new SortDescriptor("", new String[]{"transport_id"}, new boolean[]{false}, null, 0));
    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"trans_detail_id"}, new String[]{"S_XS_TRANS_DETAIL"}));
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

      return showMessage("请检查是否有必填数据未填入!", true);
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
        m_RowInfo.put("state","0" );
      }
    }
    else{
      openDetailTable();
      arraylist_rl_pxkc=putDetailToArraylist(dsDetailTable,arraylist_rl_pxkc);
    }
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
      detailRow.put("car_id", rowInfo.get("car_id_"+i));//
      detailRow.put("area_price_id", rowInfo.get("area_price_id_"+i));//

      detailRow.put("weight", rowInfo.get("weight_"+i));//
      detailRow.put("fee_weight", rowInfo.get("fee_weight_"+i));//
      detailRow.put("per_price", rowInfo.get("per_price_"+i));//
      detailRow.put("wage_cust", rowInfo.get("wage_cust_"+i));//

      detailRow.put("wage_price", rowInfo.get("wage_price_"+i));//
      detailRow.put("per_fee", rowInfo.get("per_fee_"+i));//
      detailRow.put("fee_cust", rowInfo.get("fee_cust_"+i));//
      detailRow.put("fee_price", rowInfo.get("fee_price_"+i));//

      detailRow.put("isSendcar", rowInfo.get("isSendcar_"+i));//
      detailRow.put("personid", rowInfo.get("personid_"+i));//
      detailRow.put("dwtxid", rowInfo.get("dwtxid_"+i));//
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

/*打开从表*/
  public final void openDetailTable()
  {

    /*应聘人员工作经历*/
    dsDetailTable.setQueryString(PXKC_SQL +  transport_id);
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
      //初始化时清空数据集
      if(dsMasterTable.isOpen() && dsMasterTable.getRowCount() > 0)
      dsMasterTable.empty();
      dsMasterTable.setQueryString(combineSQL(MASTER_SQL,"?",new String[]{""}));
      dsMasterTable.setRowMax(null);

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
        transport_id = dsMasterTable.getValue("transport_id");
        initRowInfo(true, false, true);
        initRowInfo(false, false, true);
      }
      if(isMasterAdd){
        transport_id = String.valueOf(--s_transport_id);
        initRowInfo(false, isMasterAdd, true);
        initRowInfo(true, isMasterAdd, true);
        m_RowInfo.clear();
        initArrayList();
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

      //得到主表的数据集
      EngineDataSet ds = getMaterTable();
      //所要修改或查询的主表的一条记录信息
      RowMap rowInfo = getMasterRowinfo();
      RowMap detailrow = null;
      EngineDataSet detail_rl_pxkc = getPxkcTable();
      //校验主表
      if (rowInfo.get("personid").equals(""))
      {
        data.setMessage(showJavaScript("alert('驾驶员必须选择!')"));
        return;
      }
      if (rowInfo.get("trans_code").equals(""))
      {
        data.setMessage(showJavaScript("alert('必须填入运单号!')"));
        return;
      }
      //校验从表
      detail_rl_pxkc.first();
      for(int i=0; i<arraylist_rl_pxkc.size(); i++)
      {
        detailrow = (RowMap)arraylist_rl_pxkc.get(i);
        if (detailrow.get("car_id").equals(""))
        {
          data.setMessage(showJavaScript("alert('车型必须选择!')"));
          return;
        }
        if (detailrow.get("weight").equals(""))
        {
          data.setMessage(showJavaScript("alert('必须填入重量!')"));
          return;
        }
        if (detailrow.get("fee_weight").equals(""))
        {
          data.setMessage(showJavaScript("alert('必须填入运费重量!')"));
          return;
        }
        if (detailrow.get("area_price_id").equals(""))
        {
          data.setMessage(showJavaScript("alert('地区必须选择!')"));
          return;
        }
        if (detailrow.get("isSendcar").equals(""))
        {
          data.setMessage(showJavaScript("alert('是否送货用车必须选择!')"));
          return;
        }
        detail_rl_pxkc.next();
      }
      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      //得到主表主键值
      if(isMasterAdd){
        ds.insertRow(false);
        transport_id = dataSetProvider.getSequence("S_XS_TRANSPORT") ;
        ds.setValue("transport_id", transport_id);
      }else
        transport_id = ds.getValue("transport_id");
      //保存从表的数据

      detail_rl_pxkc.first();
      for(int i=0; i<arraylist_rl_pxkc.size(); i++)
      {
        detailrow = (RowMap)arraylist_rl_pxkc.get(i);
        detail_rl_pxkc.setValue("transport_id", transport_id);
        detail_rl_pxkc.setValue("car_id", detailrow.get("car_id"));//

        detail_rl_pxkc.setValue("area_price_id", detailrow.get("area_price_id"));
        detail_rl_pxkc.setValue("weight", detailrow.get("weight"));//
        detail_rl_pxkc.setValue("fee_weight", detailrow.get("fee_weight"));
        detail_rl_pxkc.setValue("per_price", detailrow.get("per_price"));//
        String isSendcar=detailrow.get("isSendcar");
        detail_rl_pxkc.setValue("wage_cust", detailrow.get("wage_cust"));//
        if(isSendcar.equals("1"))
        detail_rl_pxkc.setValue("wage_price", detailrow.get("wage_price"));
        if(isSendcar.equals("2"))
        detail_rl_pxkc.setValue("wage_price", "0");
        detail_rl_pxkc.setValue("per_fee", detailrow.get("per_fee"));//
        detail_rl_pxkc.setValue("fee_cust", detailrow.get("fee_cust"));
        if(isSendcar.equals("1"))
        detail_rl_pxkc.setValue("fee_price", detailrow.get("fee_price"));//
        if(isSendcar.equals("2"))
        detail_rl_pxkc.setValue("fee_price","0");//
        detail_rl_pxkc.setValue("isSendcar", detailrow.get("isSendcar"));//
        detail_rl_pxkc.setValue("personid", detailrow.get("personid"));
        detail_rl_pxkc.setValue("dwtxid", detailrow.get("dwtxid"));
        detail_rl_pxkc.post();
        detail_rl_pxkc.next();

      }
      //保存主表数据

      ds.setValue("personid", rowInfo.get("personid"));//
      ds.setValue("deptid", rowInfo.get("deptid"));//
      ds.setValue("trans_code", rowInfo.get("trans_code"));//
      ds.setValue("trans_date", rowInfo.get("trans_date"));//
      ds.setValue("state", "0");//

      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail_rl_pxkc,}, null);
      //刷新lookup的数据集，保持数据的同步.入参是相应的lookup名称
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PERSON);
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
      if(dsDetailTable.isOpen())
      dsDetailTable.close();
      String transport_id = dsMasterTable.getValue("transport_id");
      setDataSetProperty(dsDetailTable,PXKC_SQL + "'"+transport_id+"'");
      dsDetailTable.open();
      dsDetailTable.deleteAllRows();
      dsDetailTable.post();
      dsDetailTable.saveChanges();

      ds.deleteRow();
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
      if(!master.isOpen())
        master.open();
      //初始化固定的查询项目
      fixedQuery = new QueryFixedItem();
      fixedQuery.addShowColumn("", new QueryColumn[]{
        new QueryColumn(master.getColumn("transport_id"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("personid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("deptid"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("trans_code"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("trans_date"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("state"), null, null, null, null, "="),

      });
       isInitQuery = true;
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
          transport_id = dsMasterTable.getValue("transport_id");
        }else
          transport_id = transport_id;
          dsDetailTable.insertRow(false);
          dsDetailTable.setValue("transport_id",transport_id);
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


