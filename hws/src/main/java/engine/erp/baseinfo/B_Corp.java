package engine.erp.baseinfo;

import engine.util.StringUtils;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.web.upload.*;
import engine.project.*;
import engine.html.*;
import engine.common.LoginBean;
import engine.common.User;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;

/**
 * <p>Title: 基础信息维护--往来单位列表</p>
 * <p>Description: 基础信息维护--往来单位列表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */
public final class B_Corp extends BaseAction implements Operate
{
  public static final String CORP_EDIT   = "10000";
  public static final String CORP_ADD    = "10001";
  public static final String DEPT_CHANGE = "10002";
  public static final String FILE_ADD    = "20001";
  public static final String FILE_EDIT   = "20002";
  public static final String FILE_DEL    = "20003";
  public static final String FILE_UPLOAD = "20004";
  public static final String FILE_DOWN   = "20005";
  public static final String REFRESH_FILE = "20006";

  private static final String CORP_STRUCT_SQL = "SELECT * FROM dwtx WHERE 1<>1";
  private static final String CORP_TYPE_STRUCT_SQL = "SELECT * FROM dwtx_lx WHERE 1<>1";
  private static final String CORP_MAN_STRCUT_SQL  = "SELECT * FROM dwtx_lxr WHERE 1<>1";

  private static final String CORP_SQL
      = "SELECT d.* FROM dwtx d "
      + "WHERE 1=1 @    "
      + "AND d.isdelete=0 AND d.fgsid=@ @ ";
  //多条往来单位的类型数据
  private static final String CORP_TYPE_LIST_SQL = "SELECT * FROM dwtx_lx WHERE dwtxid IN (@)";
  //单条往来单位的类型数据
  private static final String CORP_TYPE_SQL = "SELECT * FROM dwtx_lx WHERE dwtxid='@'";
  //往来单位联系人
  private static final String CORP_MAN_SQL  = "SELECT * FROM dwtx_lxr WHERE dwtxid='@'";
  //网页直接查看记录
  private static final String CORP_VIEW_SQL = "SELECT * FROM dwtx WHERE dwtxid='@'";
  //
  private static final String CORP_FILE_STRUT_SQL = "SELECT * FROM dwtx_file WHERE 1<>1";
  private static final String CORP_FILE_SQL = "SELECT * FROM dwtx_file WHERE dwtxid='@'";

  private boolean isMasterAdd = true;    //是否在添加状态
  private boolean isDetailAdd = true;    //是否在添加状态
  private long    masterRow   = 0;       //保存主表修改操作的行记录指针
  private long    detailRow   = 0;       //保存从表修改操作的行记录指针

  private RowMap m_RowInfo    = new RowMap(); //主表添加行或修改行的引用
  private RowMap d_RowInfo    = new RowMap(); //从表添加行或修改行的引用

  private EngineDataSet dsCorpTypeList= new EngineDataSet();//多条往来单位的类型数据
  public  EngineDataSet dsCorp    = new EngineDataSet();//主表
  public  EngineDataSet dsCorpType= new EngineDataSet();//往来单位的类型
  public  EngineDataSet dsCorpMan = new EngineDataSet();//从表
  public  EngineDataSet dsCorpFile = new EngineDataSet();//相关附件
  public  HtmlTableProducer table = new HtmlTableProducer(dsCorp, "dwtx", "d");

  public  ArrayList listCorpType = new ArrayList();
  public  ArrayList orderFieldCodes = new ArrayList(); //排序的字段编码
  public  ArrayList orderFieldNames = new ArrayList(); //排序的字段名称
  public  ArrayList selectedOrders  = null;
  private String    orderBy = "";

  public  String retuUrl = "";
  private String dwtxid = "";
  private User currUser = null;
  private String pesonid = null;
  private String fgsID = "";

  public  boolean isDirect = false; //是否是查看状态
  /**
   * 得到往来单位信息的实例
   * @param request jsp请求
   * @return 返回往来单位信息的实例
   */
  public static B_Corp getInstance(HttpServletRequest request)
  {
    B_Corp corpBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      corpBean = (B_Corp)session.getAttribute("corpBean_aa");
      if(corpBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        corpBean = new B_Corp();
        corpBean.currUser = loginBean.getUser();
        corpBean.fgsID = loginBean.getFirstDeptID();
        corpBean.pesonid = loginBean.getUserID();
        session.setAttribute("corpBean_aa", corpBean);
      }
    }
    return corpBean;
  }

  /**
   * 构造函数
   */
  private B_Corp()
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
  protected final void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsCorpTypeList, null);
    setDataSetProperty(dsCorp, null);
    setDataSetProperty(dsCorpType, null);
    setDataSetProperty(dsCorpMan, null);
    setDataSetProperty(dsCorpFile, null);

    dsCorpTypeList.setSort(new SortDescriptor("", new String[]{"dwtxid"}, new boolean[]{false,false}, null, 0));

    //dsCorp.setSort(new SortDescriptor("", new String[]{"dqh","dwdm"}, new boolean[]{false,false}, null, 0));
    dsCorp.setTableName("dwtx");
    //添加往来单位转载数据的监听器
    dsCorp.addLoadListener(new OpenCorpType());

    dsCorpType.setSequence(new SequenceDescriptor(new String[]{"dwtxlxid"}, new String[]{"s_dwtx_lx"}));

    //dsCorpMan.setSort(new SortDescriptor("", new String[]{"dwtxid"}, new boolean[]{false,false}, null, 0));
    dsCorpMan.setSequence(new SequenceDescriptor(new String[]{"lxrid"}, new String[]{"s_dwtx_lxr"}));
    dsCorpFile.setSequence(new SequenceDescriptor(new String[]{"file_id"}, new String[]{"s_dwtx_file"}));
    //
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(FIXED_SEARCH), new Search());
    addObactioner(String.valueOf(ORDERBY), new Orderby());
    //主表的添加修改操作
    MasterAddEdit masterAddEdit = new MasterAddEdit();
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    //主表的提交
    MasterPost masterPost = new MasterPost();
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    //主表的删除
    addObactioner(String.valueOf(DEL), new MasterDelete());
    //从表的添加修改
    DetailAddEdit detailAddEdit = new DetailAddEdit();
    addObactioner(String.valueOf(DETAIL_ADD), detailAddEdit);
    addObactioner(String.valueOf(DETAIL_EDIT), detailAddEdit);
    //从表的提交
    DetailPost detailPost = new DetailPost();
    addObactioner(String.valueOf(DETAIL_POST), detailPost);
    addObactioner(String.valueOf(DETAIL_RE_ADD), detailPost);
    //从表的删除
    addObactioner(String.valueOf(DETAIL_DEL), new DetailDelete());
    //部门更改
    addObactioner(DEPT_CHANGE, new DeptChange());
    //直接添加或修改
    DirectAddEdit directAddEdit = new DirectAddEdit();
    addObactioner(CORP_ADD, directAddEdit);
    addObactioner(CORP_EDIT, directAddEdit);
    //上传文件
    File_Add_Edit fileAddEdit = new File_Add_Edit();
    addObactioner(FILE_ADD,  fileAddEdit);
    addObactioner(FILE_EDIT, fileAddEdit);
    addObactioner(FILE_DEL, fileAddEdit);
    addObactioner(FILE_UPLOAD, new Upload());
    addObactioner(FILE_DOWN, new Download());
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
          return "";
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsCorp.isOpen() && dsCorp.changesPending())
        dsCorp.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Implement this engine.project.OperateCommon abstract method
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsCorpTypeList != null){
      dsCorpTypeList.closeDataSet();
      dsCorpTypeList = null;
    }
    if(dsCorp != null){
      dsCorp.closeDataSet();
      dsCorp = null;
    }
    if(dsCorpType != null){
      dsCorpType.closeDataSet();
      dsCorpType = null;
    }
    if(dsCorpMan != null){
      dsCorpMan.closeDataSet();
      dsCorpMan = null;
    }
    if(dsCorpFile != null){
      dsCorpFile.closeDataSet();
      dsCorpFile = null;
    }
    log = null;
    m_RowInfo = null;
    d_RowInfo = null;
  }

  /**
   * Implement this engine.project.OperateCommon abstract method
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }

  /**
   * 得到往来单位类型串
   * @param dwtxid 往来单位id
   * @return 往来单位类型串
   */
  public String getCorpTypeString(String dwtxid)
  {
    StringBuffer buf = null;
    String[] ywlxs = new String[]{"供货单位","销货单位","外加工单位","承运单位","内部往来"};
    EngineRow locateRow = new EngineRow(dsCorpTypeList, "dwtxid");
    locateRow.setValue(0,dwtxid);
    dsCorpTypeList.first();
    int locate = Locate.FIRST;
    while(dsCorpTypeList.locate(locateRow, locate))
    {
      locate = Locate.NEXT;
      int ywlx = dsCorpTypeList.getBigDecimal("ywlx").intValue();
      if(ywlx >5 || ywlx<1)
        continue;
      if(buf == null)
        buf = new StringBuffer(ywlxs[ywlx-1]);
      else
        buf.append(",").append(ywlxs[ywlx-1]);
    }
    return buf == null ? "" : buf.toString();
  }

  /**
   * 打开往来单位类型数据数据
   */
  final class OpenCorpType implements LoadListener
  {
    public void dataLoaded(LoadEvent loadEvent)
    {
      int count = dsCorp.getRowCount();
      if(count > 0)
      {
        String sql = getWhereIn(dsCorp, "dwtxid", null);
        sql = sql.length() > 0 ? combineSQL(CORP_TYPE_LIST_SQL, "@", new String[]{sql}) : CORP_TYPE_STRUCT_SQL;
        dsCorpTypeList.setQueryString(sql);
        if(dsCorpTypeList.isOpen())
          dsCorpTypeList.refresh();
        else
          dsCorpTypeList.openDataSet();
      }
      else if(!dsCorpTypeList.isOpen())
      {
        dsCorpTypeList.setQueryString(CORP_TYPE_STRUCT_SQL);
        dsCorpTypeList.openDataSet();
      }
    }
  }

  /**
   * 初始化操作的触发类
   */
  final class Init implements Obactioner
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
      isDirect = false;
      retuUrl = data.getParameter("src","").trim();
      table.init(data.getRequest(), pesonid);
      table.getWhereInfo().clearWhereValues();
      listCorpType.clear();
      dsCorp.setQueryString(CORP_STRUCT_SQL);
      //准备刷新
      dsCorp.readyRefresh();
      //排序的字段
      orderBy = "d.dwtxid";
      if(selectedOrders == null)
        selectedOrders = new ArrayList();
      else
        selectedOrders.clear();
      selectedOrders.add("dwdq.areacode");
      selectedOrders.add("d.dwdm");

      orderFieldCodes.clear();
      orderFieldNames.clear();
      if(!dsCorp.isOpen())
        dsCorp.open();
      FieldInfo[] fields = table.getAllField();
      for(int i=0; i<fields.length; i++)
      {
        String linkTable = fields[i].getLinktable();
        if(linkTable == null)
        {
          orderFieldCodes.add(table.getTableAliasName()+"."+fields[i].getFieldcode());
          orderFieldNames.add(fields[i].getFieldname());
        }
        else
        {
          String[] fieldCodes = fields[i].getShowFields();
          String[] fieldNames = fields[i].getShowFieldNames();
          for(int j=0; fieldCodes!=null && j<fieldCodes.length; j++)
          {
            orderFieldCodes.add(linkTable+"."+fieldCodes[j]);
            orderFieldNames.add(fieldNames[j]);
          }
        }
      }
      data.setMessage(showJavaScript("showFixedQuery();"));
    }
  }

  /**
   * 直接修改或添加操作的触发类（别的网页重定向）
   */
  final class DirectAddEdit implements Obactioner
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
      isDirect = true;
      isMasterAdd = CORP_ADD.equals(action);
      //
      String dwtxid = data.getParameter("dwtxid", "");
      String sql = combineSQL(CORP_VIEW_SQL, "@", new String[]{dwtxid});
      dsCorp.setQueryString(sql);
      if(dsCorp.isOpen())
        dsCorp.refresh();
      else
        dsCorp.open();

      if(!isMasterAdd)
      {
        dsCorp.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsCorp.getInternalRow();
        //把主表的指定行的信息放入rowmap
        initRowInfo(true, false, true);
      }
      else
        initRowInfo(true, true, true);

      openDetails();
      resetType();
    }
  }

  /**
   * 主表添加或修改操作的触发类
   */
  final class Search implements Obactioner
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
      table.getWhereInfo().setWhereValues(data.getRequest());
      String SQL = table.getWhereInfo().getWhereQuery();
      SQL = combineSQL(CORP_SQL, "@", new String[]{
                       SQL.length()==0 ? "" : SQL+" AND",
                       fgsID,
                       orderBy.length() > 0 ? "ORDER BY "+orderBy : orderBy});
      dsCorp.setQueryString(SQL);
      dsCorp.readyRefresh();
    }
  }

  private final static String CORP_REPEAT
      = "SELECT COUNT(*) FROM dwtx WHERE dwmc='@' AND isdelete=0 AND dwtxid<>'@'";
  public boolean isRepeat(String corpName, String corpId) throws Exception
  {
    if(corpId == null || corpId.length() == 0)
      corpId = "-2";
    String sql = StringUtils.combine(CORP_REPEAT, "@", new String[]{corpName, corpId});
    sql = dataSetProvider.getSequence(sql);
    return !"0".equals(sql);
  }

  /**
   * 排序操作
   */
  final class Orderby implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 排序触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      orderBy = data.getParameter("sortColumnStr", "");
      selectedOrders.clear();
      if(orderBy.length() > 0)
      {
        String[] sorts = StringUtils.parseString(orderBy, ",");
        for(int i=0; i<sorts.length; i++){
          selectedOrders.add(sorts[i]);
        }
      }
      String SQL = table.getWhereInfo().getWhereQuery();
      SQL = combineSQL(CORP_SQL, "@", new String[]{//orderBy.length() > 0 ? orderBy+"," : orderBy,
                       SQL.length()==0 ? "" : SQL+" AND",
                        fgsID,
                       orderBy.length() > 0 ? "ORDER BY "+orderBy : orderBy});
      dsCorp.setQueryString(SQL);
      dsCorp.readyRefresh();
    }
  }

  /**
   * 主表添加或修改操作的触发类
   */
  final class MasterAddEdit implements Obactioner
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
      isMasterAdd = String.valueOf(ADD).equals(action);
      if(!isMasterAdd)
      {
        dsCorp.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsCorp.getInternalRow();
        //把主表的指定行的信息放入rowmap
        initRowInfo(true, false, true);
        //m_RowInfo.put(dsCorp);
      }
      else
        initRowInfo(true, true, true);

      openDetails();
      resetType();
      data.setMessage(showJavaScript("toDetail();"));
    }
  }

  public boolean fileIsAdd = false;

  /**
   * 文件添加或修改
   */
  final class File_Add_Edit implements Obactioner
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
      //
      int row = 0;
      if(FILE_DEL.equals(action))
      {
        try{
          row = Integer.parseInt(data.getParameter("rownum"));
        }catch(Exception ex){
          return;
        }
        dsCorpFile.goToRow(row);
        dsCorpFile.deleteRow();
      }
      else
      {
        fileIsAdd = FILE_ADD.equals(action);
        if(!fileIsAdd){
          try{
            row = Integer.parseInt(data.getParameter("rownum"));
          }catch(Exception ex){
            fileIsAdd = false;
          }
          dsCorpFile.goToRow(row);
        }
      }
    }
  }

  /**
   * 上传文件
   */
  final class Upload implements Obactioner
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
      //String requestType =  data.getRequest().getContentType().toLowerCase();//.startsWith("multipart")
      FileUpload upload = new FileUpload();
      //设置禁止上传的文件扩展名列表
      upload.setDeniedFilesList("jsp,do,ex");
      //设置最大上传文件的大小:5M
      upload.setMaxFileSize(5*1024*1024);
      //分析上传文件
      upload.upload(data.getRequest());
      //得到表单的输入框参数。不可用data.getRequest().getParameter("file_name")方法,这样是得不到的;
      String file_name = upload.getRequest().getParameter("file_name");
      //得到第一个上传的文件
      File file = upload.getFiles().getFile(0);
      //将网页提交的文件流保存到数据集的指定字段中，并返回文件的MIME类型
      String mime_type = file.getContentType().trim();
      //
      if(fileIsAdd)
      {
        dsCorpFile.insertRow(false);
        dsCorpFile.setValue("file_id", "-1");
        dsCorpFile.setValue("dwtxid", dsCorp.getValue("dwtxid"));
      }
      //保存到数据的指定字段中
      file.saveAs(dsCorpFile, "file_content");
      //
      dsCorpFile.setValue("mime_type", mime_type);
      dsCorpFile.setValue("file_name", file_name);
      dsCorpFile.post();
      data.setMessage(showJavaScript("hideInterFrame();"));
    }
  }

  /**
   * 下载文件
   */
  final class Download implements Obactioner
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
      if(!dsCorpFile.isOpen())
        return;
      int row;
      try{
        row = Integer.parseInt(data.getParameter("rownum"));
      }
      catch(Exception ex){
        return;
      }
      int currRow = dsCorpFile.getRow();
      dsCorpFile.goToRow(row);
      String mime_type = dsCorpFile.getValue("mime_type");
      FileUpload upload = new FileUpload();
      String file_name = null;
      if(mime_type.endsWith("msword") || mime_type.endsWith("excel"))
        file_name = dsCorpFile.getValue("file_name");
      upload.downloadField(data.getResponse(), dsCorpFile, "file_content", mime_type, file_name);
      dsCorpFile.goToRow(currRow);
    }
  }

  /**
   * 打开从表
   */
  private void openDetails()
  {
    String dwtxid = dsCorp.getValue("dwtxid");
    //根据主表的dwtxid取dwtx_lx的信息
    dsCorpType.setQueryString(isMasterAdd ? CORP_TYPE_STRUCT_SQL: combineSQL(CORP_TYPE_SQL, "@", new String[]{dwtxid}));
    if(!dsCorpType.isOpen())
      dsCorpType.openDataSet();
    else
      dsCorpType.refresh();
    //根据主表的dwtxid取dwtx_lxr的信息
    dsCorpMan.setQueryString(isMasterAdd ? CORP_MAN_STRCUT_SQL : combineSQL(CORP_MAN_SQL, "@", new String[]{dwtxid}));
    if(!dsCorpMan.isOpen())
      dsCorpMan.openDataSet();
    else
      dsCorpMan.refresh();
    //根据相关文件列表
    dsCorpFile.setQueryString(isMasterAdd ? CORP_FILE_STRUT_SQL : combineSQL(CORP_FILE_SQL, "@", new String[]{dwtxid}));
    if(!dsCorpFile.isOpen())
      dsCorpFile.openDataSet();
    else
      dsCorpFile.refresh();
  }

  /**
   * 把指定的"类型"信息放入arraylist
   * @return 返回类型列表
   */
  private void resetType()
  {
    listCorpType.clear();
    EngineDataSet ds = dsCorpType;
    ds.first();
    while(ds.inBounds()){
      listCorpType.add(ds.getValue("ywlx"));
      ds.next();
    }
  }

  /**
   * 主表的删除的触发类
   */
  final class MasterDelete implements Obactioner
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
      dsCorp.goToInternalRow(masterRow);
      dsCorp.setValue("isdelete","1");
      dsCorp.post();
      dsCorp.saveChanges();

      dsCorp.deleteRow();
      listCorpType.clear();
      dsCorp.resetPendingStatus(true);
      data.setMessage(showJavaScript("backList();"));
    }
  }

  /**
   * 主表的提交操作的触发类
   */
  final class MasterPost implements Obactioner
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
      m_RowInfo.put(data.getRequest());
      String personid = m_RowInfo.get("personid");
      String cdm = m_RowInfo.get("cdm");
      String dqh = m_RowInfo.get("dqh");
      String deptid  = m_RowInfo.get("deptid");
      String dwdm = m_RowInfo.get("dwdm");
      String zjm  = m_RowInfo.get("zjm");
      String dwmc = m_RowInfo.get("dwmc");
      String addr = m_RowInfo.get("addr");
      String zp = m_RowInfo.get("zp");
      String tel = m_RowInfo.get("tel");
      String http = m_RowInfo.get("http");
      String email = m_RowInfo.get("email");
      String frdb = m_RowInfo.get("frdb");
      String khh = m_RowInfo.get("khh");
      String zh = m_RowInfo.get("zh");
      String nsrdjh = m_RowInfo.get("nsrdjh");
      String cz = m_RowInfo.get("cz");
      String lxr = m_RowInfo.get("lxr");
      String syz = m_RowInfo.get("syz");
      String gxsd = m_RowInfo.get("gxsd");
      String zxdj = m_RowInfo.get("zxdj");
      String hy = m_RowInfo.get("hy");
      String bz = m_RowInfo.get("bz");
//      String yfdj = m_RowInfo.get("yfdj");

      dsCorpMan = getDataSetLXR();//get the lxr info from the dataset

      String dwtxid = "";//for the dwtx_lxr 的dwtxid
      String temp = null;
      //判断数据的有效/
      if(dwdm.equals("")){
        data.setMessage(showJavaScript("alert('单位编码不能为空！');"));
        return;
      }
      else if((temp = checkInt(dwdm, "单位编码"))!=null)
      {
        data.setMessage(temp);
        return;
      }
      if(cdm.equals("")){
        data.setMessage(showJavaScript("alert('请选择单位的所属国家！');"));
        return;
      }
      if(dqh.equals("")){
        data.setMessage(showJavaScript("alert('请选择单位的所属地区！');"));
        return;
      }
      if(dwmc.equals("")){
        data.setMessage(showJavaScript("alert('单位名称不能为空！');"));
        return;
      }
//      if(yfdj.length() > 0 && !isDouble(yfdj)){
//        data.setMessage(showJavaScript("alert('非法运费单价！');"));
//        return;
//      }
      int bzPrecision = dsCorp.getColumn("bz").getPrecision();
      if(bz.getBytes().length > bzPrecision)
      {
        data.setMessage(showJavaScript("alert('您输入的备注的长度太长, 最大"+ bzPrecision +"个字节！');"));
        return;
      }
      //
      if(isMasterAdd){
        String count = "select count(*) from dwtx where isdelete = 0 and dwdm ='"+dwdm+"'";
        count = dataSetProvider.getSequence(count);
        if(!count.equals("0")){
          data.setMessage(showJavaScript("alert('单位编码已存在')"));
          return;
        }
      }
      else{
        dsCorp.goToInternalRow(masterRow);
        if(!dsCorp.getValue("dwdm").equals(dwdm))
        {
          String count = "select count(*) from dwtx where isdelete = 0 and dwdm ='"+dwdm+"'";
          count = dataSetProvider.getSequence(count);
          if(!count.equals("0")){
            data.setMessage(showJavaScript("alert('单位编码已存在')"));
            return;
          }
        }
      }

      //更改主表
      if(!isMasterAdd)
      {
        dsCorp.goToInternalRow(masterRow);
        dwtxid = dsCorp.getValue("dwtxid");
        lxEdit(dwtxid, data.getRequest());
      }
      else
      {
        dwtxid = dataSetProvider.getSequence("s_dwtx");
        dsCorp.insertRow(false);
        dsCorp.setValue("dwtxid",dwtxid);
        dsCorp.setValue("isDelete","0");
        dsCorp.setValue("fgsid", fgsID);
        lxAdd(dwtxid, data.getRequest());
        //此时dwtx_lxr的dwtxid把其更改
        dsCorpMan.first();
        while(dsCorpMan.inBounds())
        {
          dsCorpMan.setValue("dwtxid",dwtxid);
          dsCorpMan.post();
          dsCorpMan.next();
        }
        //此时dwtx_lxr的dwtxid把其更改
        dsCorpFile.first();
        while(dsCorpFile.inBounds())
        {
          dsCorpFile.setValue("dwtxid",dwtxid);
          dsCorpFile.post();
          dsCorpFile.next();
        }
      }
//      String[] lxs = data.getRequest().getParameterValues("ywlx");
//      if(lxs!=null)
//      {
//    	  StringBuffer types=new StringBuffer();
//          for(int i=0;i<lxs.length;i++)
//          {
//        	  if(types.length()>0)
//        		  types.append(",");
//        	  types.append(lxs[i]);
//          }
////          dsCorp.setValue("TYPES",types.toString());
//      }else
//    	  dsCorp.setValue("TYPES","");
      
      dsCorp.setValue("personid",personid);
      dsCorp.setValue("cdm",cdm);
      dsCorp.setValue("dqh",dqh);
      dsCorp.setValue("deptid",deptid);
      dsCorp.setValue("dwdm",dwdm);
      dsCorp.setValue("zjm",zjm);
      dsCorp.setValue("dwmc",dwmc);
      dsCorp.setValue("addr",addr);
      dsCorp.setValue("zp",zp);
      dsCorp.setValue("tel",tel);
      dsCorp.setValue("http",http);
      dsCorp.setValue("email",email);
      dsCorp.setValue("frdb",frdb);
      dsCorp.setValue("khh",khh);
      dsCorp.setValue("zh",zh);
      dsCorp.setValue("nsrdjh",nsrdjh);
      dsCorp.setValue("cz",cz);
      dsCorp.setValue("lxr",lxr);
      dsCorp.setValue("syz",syz);
      dsCorp.setValue("gxsd",gxsd);
      dsCorp.setValue("zxdj",zxdj);
      dsCorp.setValue("hy",hy);
      dsCorp.setValue("bz",bz);
//      dsCorp.setValue("yfdj", yfdj);
      dsCorp.post();//提交到本地内存

      dsCorp.saveDataSets(new EngineDataSet[]{dsCorp,dsCorpType,dsCorpMan,dsCorpFile});//提交到数据库
      if(String.valueOf(POST_CONTINUE).equals(action))
      {
        isMasterAdd = true;
        initRowInfo(true, true, true);
        dsCorpMan.empty();
        initRowInfo(false, true, true);
        listCorpType.clear();
      }
      else if(String.valueOf(POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
    }

    /**
     * 根据dwtx更改更改dwtx_lx，先删除在添加
     */
    private void lxEdit(String dwtxid,HttpServletRequest request)
    {
      dsCorpType.deleteAllRows();//删除所有行

      String[] lxs = request.getParameterValues("ywlx");
      if(lxs == null)
        return;
      for(int i=0; lxs!=null&& i<lxs.length;i++)
      {
        dsCorpType.insertRow(false);
        dsCorpType.setValue("dwtxlxid","-1");
        dsCorpType.setValue("dwtxid",dwtxid);
        dsCorpType.setValue("ywlx",lxs[i]);
        dsCorpType.post();
      }
    }

    /**
     * dwtx添加，dwtx_lx的添加
     */
    private void lxAdd(String dwtxid,HttpServletRequest request)
    {
      String[] lxs = request.getParameterValues("ywlx");
      if(lxs == null )
        return;
      for(int i=0;i<lxs.length;i++)
      {
        dsCorpType.insertRow(false);
        dsCorpType.setValue("dwtxlxid","-1");
        dsCorpType.setValue("dwtxid",dwtxid);
        dsCorpType.setValue("ywlx",lxs[i]);
        dsCorpType.post();
      }
    }
  }


  /**
   * 从表（联系人）的添加或修改操作的触发类
   */
  final class DetailAddEdit implements Obactioner
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
      isDetailAdd = String.valueOf(DETAIL_ADD).equals(action);
      if(!isMasterAdd)
        dsCorp.goToInternalRow(masterRow);
      if(!isDetailAdd)
      {
        dsCorpMan.goToRow(Integer.parseInt(data.getParameter("rownum")));
        detailRow = dsCorpMan.getInternalRow();
      }
      initRowInfo(false, isDetailAdd, true);
      initRowInfo(true, isMasterAdd, true);
      //m_RowInfo.put(data.getRequest());
      String[] lxs = data.getParameterValues("ywlx");
      listCorpType.clear();
      for(int i=0; lxs!=null &&i<lxs.length; i++)
        listCorpType.add(lxs[i]);
      data.setMessage(showJavaScript("toDetail();"));
    }
  }

  /**
   * 部门变更操作的触发类
   */
  final class DeptChange implements Obactioner
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
      m_RowInfo.put(data.getRequest());
    }
  }

  /**
   * 从表（联系人）的删除操作的触发类
   */
  final class DetailDelete implements Obactioner
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
      m_RowInfo.put(data.getRequest());
      String[] lxs = data.getParameterValues("ywlx");
      listCorpType.clear();
      for(int i=0; lxs!=null &&i<lxs.length; i++)
        listCorpType.add(lxs[i]);

      dsCorpMan.goToRow(Integer.parseInt(data.getParameter("rownum")));
      dsCorpMan.deleteRow();
    }
  }


  /**
   * 从表（联系人）的保存操作的触发类
   */
  final class DetailPost implements Obactioner
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
      d_RowInfo.put(data.getRequest());
      String dwtxid = m_RowInfo.get("dwtxid");
      String xm = d_RowInfo.get("xm");
      String loginName = d_RowInfo.get("loginName");
      String loginPassword = d_RowInfo.get("loginPassword");
      String ch = d_RowInfo.get("ch");
      String sex = d_RowInfo.get("sex");
      String zw = d_RowInfo.get("zw");
      String bgdh = d_RowInfo.get("bgdh");
      String cz = d_RowInfo.get("cz");
      String yddh = d_RowInfo.get("yddh");
      String chhm = d_RowInfo.get("chhm");
      String jtjh = d_RowInfo.get("jtjh");
      String isMarry = d_RowInfo.get("isMarry");
      String sr = d_RowInfo.get("sr");
      String sjID = d_RowInfo.get("sjID");
      String xxdz = d_RowInfo.get("xxdz");
      String dzyj = d_RowInfo.get("dzyj");
      String yzbm = d_RowInfo.get("yzbm");
      String bz = d_RowInfo.get("bz");
      //数据的有效性校验
      if(xm.equals(""))
      {
        data.setMessage(showJavaScript("alert('姓名不能为空！')"));
        return;
      }
      //
      if(isDetailAdd){
        dsCorpMan.insertRow(false);
        dsCorpMan.setValue("lxrid","-1");
      }
      else
        dsCorpMan.goToInternalRow(detailRow);

      dsCorpMan.setValue("dwtxid",dwtxid);
      dsCorpMan.setValue("xm",xm);
      dsCorpMan.setValue("loginName",loginName);
      dsCorpMan.setValue("loginPassword",loginPassword);
      dsCorpMan.setValue("ch",ch);
      dsCorpMan.setValue("sex",sex);
      dsCorpMan.setValue("zw",zw);
      dsCorpMan.setValue("bgdh",bgdh);
      dsCorpMan.setValue("cz",cz);
      dsCorpMan.setValue("yddh",yddh);
      dsCorpMan.setValue("chhm",chhm);
      dsCorpMan.setValue("jtjh",jtjh);
      dsCorpMan.setValue("isMarry",isMarry);
      dsCorpMan.setValue("sr",sr);
      dsCorpMan.setValue("sjID",sjID);
      dsCorpMan.setValue("xxdz",xxdz);
      dsCorpMan.setValue("dzyj",dzyj);
      dsCorpMan.setValue("yzbm",yzbm);
      dsCorpMan.setValue("bz",bz);
      dsCorpMan.post();//提交到内存，不提交到数据库,主表更新是才提交到数据库中

      if(String.valueOf(DETAIL_RE_ADD).equals(action))
      {
        isDetailAdd = true;
        initRowInfo(false, true, true);
      }
      else if(String.valueOf(DETAIL_POST).equals(action))
        data.setMessage(showJavaScript("backList();"));
    }
  }

  /**
   * 得到主表的行信息
   * @return 主表的行信息
   */
  public RowMap getRowInfo()
  {
    return m_RowInfo;
  }

  /**
   * 初始化信息
   * @param isMaster 是否主表
   * @param isAdd 是否添加
   * @param isInit 是否重新初始化行信息
   * @throws Exception 异常
   */
  private void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit) throws Exception{
    if(isMaster)
    {
      if(isAdd){
        if(isInit && m_RowInfo.size()>0)
          m_RowInfo.clear();
        String htbh = dataSetProvider.getSequence("SELECT pck_base.billNextCode('dwtx','dwdm')dwdm FROM dual");
        m_RowInfo.put("dwdm", htbh);
        m_RowInfo.put("deptid", currUser.getDeptId());
      }
      else
        m_RowInfo.put(dsCorp);
    }
    else
    {
      if(isAdd)
      {
        if(isInit && d_RowInfo.size()>0)
          d_RowInfo.clear();

        d_RowInfo.put("sex", "1");
        d_RowInfo.put("ismarry", "1");
      }
      else
        d_RowInfo.put(dsCorpMan);
    }
  }

  /**
   * 得到dwtx对应的EngineDataSet
   */
  public  EngineDataSet getMaterTable()
  {
    return dsCorp;
  }

  /**
   * 得到dwtx_lxr对应的EngineDataSet
   */
  public  EngineDataSet getDetailTable()
  {
    return dsCorpMan;
  }

  public EngineDataSet getDataSetLXR()
  {
    return dsCorpMan;
  }

  /**
   * 得到主表的一行
   */
  public  RowMap getMasterRowinfo()
  {
    return m_RowInfo;
  }

  /**
   * 得到从表的一行
   */
  public  RowMap getDetailRowinfo()
  {
    return d_RowInfo;
  }

  /**
   * 得到联系人的列表
   * @param id 人员id
   * @return 返回联系人列表网页信息
   * @throws Exception 异常
   */
  public String getLinkmanList(String id) throws Exception
  {
    return BaseAction.dataSetToOption(dsCorpMan, "lxrid", "xm", id, null, null);
  }
}