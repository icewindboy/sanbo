package engine.erp.person;

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
import engine.web.upload.*;
import com.borland.dx.dataset.*;
import javax.swing.*;

public final class B_Document_move extends BaseAction implements Operate
{
  private static final String MASTER_STRUT_SQL = "SELECT * FROM rl_document_move WHERE 1<>1";   //取主表的结构
  private static final String MASTER_SQL    = "SELECT distinct a.* FROM rl_document_move a,rl_receive_person b where b.document_id=a.document_id  ? order by a.senddate DESC";    //对主表的参数化查询
  /*以下是对从表的条件查询,需提供ypxxID参数.*/
  private static final String file_SQL    = "SELECT * FROM rl_annex_file  WHERE document_id= ";
  private static final String person_SQL    = "SELECT * FROM rl_receive_person  WHERE document_id= ";
  private static final String FILE_STRUT_SQL   = "SELECT * FROM rl_annex_file  WHERE 1<>1" ;
  private static final String PERSON_STRUT_SQL  = "SELECT * FROM rl_receive_person  WHERE 1<>1" ;
  private static final String reciveperson_SQL    = "SELECT * FROM rl_receive_person  WHERE document_id= ";
  //操作

  public static final String FILE_ADD    = "9000";    //应聘培训课程新增
   public static final String  PERSON_ADD="156333389";
  public static final String FILE_DEL    = "9001";    //应聘培训课程删除操作
  public  static final String INVOICE_OVER = "3423513";//完成
  public  static final String OVER = "34235313";
  public static final String VIEW_DETAIL = "1055";   //主从明细
  public static final String OPERATE_SEARCH = "1066";//主表查询操作
  public static final String DELETE_RETURN = "1067"; //主从删除操作
  public static final String FILE_UPLOAD   = "90231114";//上传照片
  public  static final String TURNPAGE = "99911111126";
   public  static final String PERSON_DEL="111233455";
   public  static final String ShowHistry="526234";
  public  String loginId = "";   //登录员工的ID
   public  String VIEW_PERSON="558991";
  public  String loginName = ""; //登录员工的姓名
  public  String logindeptid = ""; //登录员工的部门
  /*主从表的数据集*/
  private EngineDataSet dsMasterTable  = new EngineDataSet();//主表
  private EngineDataSet dsDetailTable  = new EngineDataSet();//从表培训课程
  private EngineDataSet dsDetailperson  = new EngineDataSet();//从表培训课程
  private EngineDataSet dsreciveperson  = new EngineDataSet();//从表培训课程
  public boolean isMasterAdd = false;                          //主表是否在添加状态
  private long    masterRow = 0;                               //保存主表修改操作的行记录指针
  private RowMap  m_RowInfo    = new RowMap();                 //主表添加行或修改行的引用
  /*把从表数据放在ArrayList中,这样可以保存多行信息来对应主表*/
  public ArrayList arraylist_rl_file  = null;//从表应聘人员工作经历
  private boolean isInitQuery = false;                         //是否已经初始化查询条件
  private QueryBasic fixedQuery = new QueryFixedItem();
  public  String retuUrl = null;
  private String document_id = null;
  public int s_car_id=0;
  private LookUp personBean = null;
  public boolean issendperson = false;
  public boolean isreciveperson = false;
  public boolean isdetaillook = false;
  public boolean isHistory = false;
  public String multiIdInputs="";
   public String[] personids=null;

  /**
   *运单列表的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回销售合同列表的实例
   */
  public static B_Document_move getInstance(HttpServletRequest request)
  {
    B_Document_move documentBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "documentBean";
      documentBean = (B_Document_move)session.getAttribute(beanName);
      if(documentBean == null)
      {
      //引用LoginBean
      LoginBean loginBean = LoginBean.getInstance(request);
      documentBean = new B_Document_move();
      documentBean.loginId = loginBean.getUserID();
      documentBean.loginName = loginBean.getUserName();
      documentBean.logindeptid = loginBean.getDeptID();
      session.setAttribute(beanName, documentBean);
      }
    }
    return documentBean;
  }

  /**
   * 构造函数
   */
  private B_Document_move()
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
    setDataSetProperty(dsDetailTable, FILE_STRUT_SQL);
    setDataSetProperty(dsDetailperson, PERSON_STRUT_SQL);
    setDataSetProperty(dsreciveperson, PERSON_STRUT_SQL);
    dsMasterTable.setTableName("rl_document_move");


    dsDetailTable.setSequence(new SequenceDescriptor(new String[]{"fileid"}, new String[]{"S_RL_ANNEX_FILE"}));
    dsDetailperson.setSequence(new SequenceDescriptor(new String[]{"receive_person_id"}, new String[]{"S_RL_RECEIVE_PERSON"}));
    Master_Add_Edit masterAddEdit = new Master_Add_Edit();
    Master_Post masterPost = new Master_Post();
    addObactioner(String.valueOf(INIT), new Init());//初始化
    addObactioner(String.valueOf(OPERATE_SEARCH), new Master_Search());//定制查询
    addObactioner(String.valueOf(FILE_ADD), new Rl_File_adddel());//新增删除操作
    addObactioner(String.valueOf(FILE_DEL), new Rl_File_adddel());//新增删除操作
    addObactioner(String.valueOf(VIEW_DETAIL), masterAddEdit);//修改主表,及其对应的从表
    addObactioner(String.valueOf(DELETE_RETURN), new Master_Delete());//删除主表某一行,及其对应的从表
    addObactioner(String.valueOf(ADD), masterAddEdit);
    addObactioner(String.valueOf(EDIT), masterAddEdit);
    addObactioner(String.valueOf(INVOICE_OVER), new Invoice_Over());//完成
    addObactioner(String.valueOf(DEL), new Master_Delete());
    addObactioner(String.valueOf(POST), masterPost);
    addObactioner(String.valueOf(PERSON_ADD), new Person_add());
    addObactioner(String.valueOf(POST_CONTINUE), masterPost);
    addObactioner(FILE_UPLOAD, new UploadFile());
    addObactioner(PERSON_DEL, new Rl_person_delete());
    addObactioner(VIEW_PERSON, new view_person());
     addObactioner(ShowHistry, new Show_Histry());


    //addObactioner(TURNPAGE, new Turn_Page());//翻页事件
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
    if(dsDetailperson != null){
      dsDetailperson.close();
      dsDetailperson = null;
    }
    if(dsreciveperson != null){
    dsreciveperson.close();
    dsreciveperson = null;
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

         m_RowInfo.put("senddate", today);//制单日期
         m_RowInfo.put("sendperson", loginName);
         m_RowInfo.put("inout_type", "0");
      }
    }
    else{
      openDetailTable();
      arraylist_rl_file=putDetailToArraylist(dsDetailTable,arraylist_rl_file);

    }
  }
  //插入收件人信息
  class Person_add implements Obactioner
   {
     public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
     {
       HttpServletRequest req = data.getRequest();
       //保存输入的明细信息
       putDetailInfo(data.getRequest());
       String multiIdInput = m_RowInfo.get("multiIdInput");
       if(multiIdInputs.equals(""))
       multiIdInputs=multiIdInput;
       else
       multiIdInputs=multiIdInputs+","+multiIdInput;
       if(multiIdInput.length() == 0)
         return;
       //实例化查找数据集的类
       if(dsDetailperson.getRowCount()>0)
       {
       dsDetailperson.deleteAllRows();
       dsDetailperson.saveChanges();
       }
        personids = parseString(multiIdInputs,",");
      EngineRow row = new EngineRow(dsDetailperson, "personid");
       for(int i=0; i < personids.length; i++)
       {
         if(personids[i].equals("-1"))
           continue;
           row.setValue(0, personids[i]);
           if(!dsDetailperson.locate(row, Locate.FIRST)&&!personids[i].equals(loginId))
           {
           dsDetailperson.insertRow(false);
           dsDetailperson.setValue("receive_person_id", "-1");

           dsDetailperson.setValue("personid", personids[i]);

           dsDetailperson.setValue("isread","0");

           dsDetailperson.post();
           }
           //创建一个与用户相对应的行


       }
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
  public final void showFile(HttpServletRequest request, HttpServletResponse response)
     throws java.io.IOException
 {
    putDetailInfo(request);

   int rownum=Integer.parseInt(request.getParameter("i"));
   if(!dsDetailTable.isOpen())
     return;

     dsDetailTable.goToRow(rownum);
     String mime_type = dsDetailTable.getValue("fileformat");
     String mime_name = dsDetailTable.getValue("filename");

     FileUpload upload = new FileUpload();
     upload.downloadField(response, dsDetailTable, "mainfile", mime_type, mime_name);


  }

  /**
   * 从表保存操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  //上传图片
  class UploadFile implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      File file = null;
      file=null;
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
      file = upload.getFiles().getFile(0);
      String a=file.getContentType();
      dsDetailTable.insertRow(false);
      dsDetailTable.setValue("fileid","-1");
      file.saveAs(dsDetailTable, "mainfile");
      dsDetailTable.setValue("fileformat", file.getContentType());
      dsDetailTable.setValue("filename", file_name);
      arraylist_rl_file.clear();
      arraylist_rl_file=putDetailToArraylist(dsDetailTable,arraylist_rl_file);
      dsDetailTable.post();
      data.setMessage(showJavaScript("hidewin();"));
      file=null;
    }
  }
  private final void putDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = getMasterRowinfo();
    rowInfo.put(request);

  }
  /*得到表对象*/
  public final EngineDataSet getMaterTable()
  {
    return dsMasterTable;
  }

  /*得到从表表对象*/
  public final EngineDataSet getFileTable(){return dsDetailTable;}
  public final EngineDataSet getPersonTable(){return dsDetailperson;}
  public final EngineDataSet getrecivePersonTable(){return dsreciveperson;}
/*打开从表*/
  public final void openDetailTable()
  {

    String document_id  = dsMasterTable.getValue("document_id");
     /*附件情况*/
     dsDetailTable.setQueryString(file_SQL + (isMasterAdd ? "-1" : document_id));
     if(dsDetailTable.isOpen())
       dsDetailTable.refresh();
     else
       dsDetailTable.open();
     /*接收人员情况*/
     dsDetailperson.setQueryString(person_SQL + (isMasterAdd ? "-1" : document_id));
     if(dsDetailperson.isOpen())
       dsDetailperson.refresh();
     else
      dsDetailperson.open();
     if(!issendperson){
     dsreciveperson.setQueryString(reciveperson_SQL + (isMasterAdd ? "-1" : document_id+"  and personid="+loginId ));

    if(dsreciveperson.isOpen())
      dsreciveperson.refresh();
    else
      dsreciveperson.open();
     if(!isMasterAdd){
      dsreciveperson.setValue("isread","1");
      dsreciveperson.post();
      dsreciveperson.saveChanges();
     }
     }
  }
  class Show_Histry implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {
     if(dsMasterTable.isOpen() && dsMasterTable.getRowCount() > 0)
      dsMasterTable.empty();
      dsMasterTable.setQueryString(combineSQL(MASTER_SQL,"?",new String[]{" and b.personid="+loginId+" and b.isdelete is null and b.isread=1 " }));
      if(!dsMasterTable.isOpen())
       dsMasterTable.open();
      else
       dsMasterTable.refresh();

      isHistory=true;
   }
  }
  class view_person implements Obactioner
 {
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
   {

     if(m_RowInfo.size() > 0)
       m_RowInfo.clear();
    dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
    String sendpersonid=dsMasterTable.getValue("sendpersonid");
    if(!sendpersonid.equals(loginId)){
      data.setMessage(showJavaScript("alert('只有发件人才有权限查看!')"));
      return;
    }
    String document_id=dsMasterTable.getValue("document_id");

    /*接收人员情况*/
      dsDetailperson.setQueryString(person_SQL+document_id);
    if(dsDetailperson.isOpen())
      dsDetailperson.refresh();
    else
     dsDetailperson.open();
     isdetaillook=false;

     //打开从表
     data.setMessage(showJavaScript("location.href='reveciveperson.jsp'"));
   }
  }
  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }

  /*得到附件多行信息*/
  public final RowMap[] getFileRowinfos() {
    RowMap[] rows = new RowMap[arraylist_rl_file.size()];
    arraylist_rl_file.toArray(rows);
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
    if(arraylist_rl_file!=null){
      if(arraylist_rl_file.size()>0)
      {
        arraylist_rl_file.clear();
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
      if(!dsDetailperson.isOpen())
        dsDetailperson.open();
      if(dsDetailperson.getRowCount()>0)
        {
        dsDetailperson.deleteAllRows();
        dsDetailperson.post();
        }
      multiIdInputs="";
      personids=null;
      isHistory = false;
      isdetaillook = false;
      issendperson = false;
      isreciveperson = false;
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      HttpServletRequest request = data.getRequest();

      //初始化查询项目和内容
      RowMap row = fixedQuery.getSearchRow();
      row.clear();
      //初始化时清空数据集
      if(dsMasterTable.isOpen() && dsMasterTable.getRowCount() > 0)
      dsMasterTable.empty();
      dsMasterTable.setQueryString(combineSQL(MASTER_SQL,"?",new String[]{" and a.sendpersonid ="+loginId+" and a.isdelete is null " }));
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
       personids=null;
       multiIdInputs="";
      isMasterAdd = String.valueOf(ADD).equals(action);
      if(!isMasterAdd)
      {
        dsMasterTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        masterRow = dsMasterTable.getInternalRow();
        String sendpersonid=dsMasterTable.getValue("sendpersonid");
        if(sendpersonid.equals(loginId))
        issendperson=true;
        else
        isreciveperson=true;
        initRowInfo(true, false, true);
        initRowInfo(false, false, true);
      }
      if(isMasterAdd){
        if(dsDetailperson.getRowCount()>0)
        {
        dsDetailperson.deleteAllRows();
        dsDetailperson.post();
        }

        m_RowInfo.clear();
        initArrayList();
        initRowInfo(false, isMasterAdd, true);
        initRowInfo(true, isMasterAdd, true);
        issendperson=true;
      }
      //打开从表
      isdetaillook=true;
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
      EngineDataSet detail_rl_file = getFileTable();
       EngineDataSet detail_rl_person = getPersonTable();
      //校验主表
      if (rowInfo.get("file_type").equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择文件类型!')"));
        return;
      }
      if (rowInfo.get("filelevel").equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择优先级!')"));
        return;
      }
      if (rowInfo.get("topic").equals(""))
     {
       data.setMessage(showJavaScript("alert('请填写主题!')"));
       return;
      }



      if(!isMasterAdd)
        ds.goToInternalRow(masterRow);
      //得到主表主键值
      if(isMasterAdd){
        ds.insertRow(false);
        document_id = dataSetProvider.getSequence("S_RL_DOCUMENT_MOVE") ;
        ds.setValue("document_id", document_id);
      }else
        document_id = ds.getValue("document_id");
      //保存从表的数据

      detail_rl_file.first();
      for(int i=0; i<detail_rl_file.getRowCount(); i++)
      {
       detail_rl_file.setValue("document_id",document_id);
       detail_rl_file.post();
       detail_rl_file.next();
      }
      if(detail_rl_person.getRowCount()==0)
      {
        data.setMessage(showJavaScript("alert('您没有添加联系人!')"));
         return;
      }
      detail_rl_person.first();

      for(int i=0; i<detail_rl_person.getRowCount(); i++)
     {
      detail_rl_person.setValue("document_id",document_id);
      detail_rl_person.post();
      detail_rl_person.next();
      }
      //保存主表数据

      ds.setValue("deptid", rowInfo.get("deptid"));//
      ds.setValue("personid", rowInfo.get("personid"));//
      ds.setValue("inout_type", "0");//
      ds.setValue("topic", rowInfo.get("topic"));//

      ds.setValue("file_type", rowInfo.get("file_type"));//

      ds.setValue("filelevel", rowInfo.get("filelevel"));//
      ds.setValue("deptid",logindeptid );//
      ds.setValue("caption", rowInfo.get("caption"));//
      ds.setValue("maintext", rowInfo.get("maintext"));//
      ds.setValue("sendpersonid",loginId );//
      ds.setValue("sendperson", loginName);//
      ds.setValue("senddate", rowInfo.get("senddate"));//
      ds.post();
      ds.saveDataSets(new EngineDataSet[]{ds, detail_rl_file,detail_rl_person}, null);
      issendperson = false;
      isreciveperson = false;
      isMasterAdd= false;
      personids=null;
      multiIdInputs="";
      if(dsDetailperson.getRowCount()>0)
        {
        dsDetailperson.deleteAllRows();
        dsDetailperson.post();
        }
      //刷新lookup的数据集，保持数据的同步.入参是相应的lookup名称
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_AREA_CAR);
       LookupBeanFacade.refreshLookup(SysConstant.BEAN_SALE_CAR);
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
      dsDetailTable.closeDataSet();
      if(dsDetailperson.isOpen())
      dsDetailperson.closeDataSet();

      document_id = dsMasterTable.getValue("document_id");
      setDataSetProperty(dsDetailTable,file_SQL+document_id);
      setDataSetProperty(dsDetailperson,person_SQL+document_id);
      dsDetailTable.open();
      dsDetailperson.open();

      String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM rl_receive_person WHERE isdelete is null and document_id="+document_id);
      String countsentperson = dataSetProvider.getSequence("SELECT COUNT(*) FROM rl_document_move WHERE isdelete is null and document_id="+document_id);
      if(Double.parseDouble(count)+Double.parseDouble(countsentperson)==1)
      {
      dsDetailTable.deleteAllRows();

      dsDetailperson.deleteAllRows();



      ds.deleteRow();
      ds.post();dsDetailperson.post();dsDetailTable.post();
      ds.saveDataSets(new EngineDataSet[]{ds, dsDetailTable,dsDetailperson}, null);

      }
      else
      {
        if(loginId.equals(ds.getValue("sendpersonid")))
        {
        ds.setValue("isdelete","1");
        ds.post();
        ds.saveChanges();
        if(ds.isOpen() && ds.getRowCount() > 0)
        ds.empty();
         ds.setQueryString(combineSQL(MASTER_SQL,"?",new String[]{" and a.sendpersonid ="+loginId+" and a.isdelete is null " }));
        ds.refresh();

        }
        else
        {
          EngineRow row = new EngineRow(dsDetailperson, "personid");
          row.setValue(0, loginId);
          if(dsDetailperson.locate(row, Locate.FIRST))
          dsDetailperson.setValue("isdelete","1");
          dsDetailperson.post();
          dsDetailperson.saveChanges();
          if(dsMasterTable.isOpen() && dsMasterTable.getRowCount() > 0)
           ds.empty();
          ds.setQueryString(combineSQL(MASTER_SQL,"?",new String[]{" and b.personid="+loginId+" and b.isdelete is null and b.isread=1 " }));
          ds.setRowMax(null);
        }


      }


      //ds.saveChanges();

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
      SQL = combineSQL(MASTER_SQL, "?", new String[]{ " and ((a.sendpersonid ="+loginId+" and a.isdelete is null) or (b.personid="+loginId+" and b.isdelete is null )) "+SQL});


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
        new QueryColumn(master.getColumn("inout_type"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("topic"), null, null, null, null, "like"),
        new QueryColumn(master.getColumn("file_type"), null, null, null, null, "="),
        new QueryColumn(master.getColumn("caption"), null, null, null, null, "like") ,
        new QueryColumn(master.getColumn("filelevel"), null, null, null, null, "＝"),
        new QueryColumn(master.getColumn("sendpersonid"), null, null, null, null, "＝")
      });
       isInitQuery = true;
    }
  }
  /**
   * 0号
   *
   *
   * */
  class Rl_File_adddel  implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(action.equals(FILE_ADD))
      {
        putDetailInfo(data.getRequest());
      }
      if(action.equals(FILE_DEL))
      {
          putDetailInfo(data.getRequest());
          int rownum=Integer.parseInt(data.getRequest().getParameter("rownum"));
          arraylist_rl_file.remove(rownum);
          dsDetailTable.goToRow(rownum);
          dsDetailTable.deleteRow();

      }

    }
  }
  class Rl_person_delete  implements Obactioner
    {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      int rownum=Integer.parseInt(data.getRequest().getParameter("rownum"));
      dsDetailperson.goToRow(rownum);
      dsDetailperson.deleteRow();
      dsDetailperson.post();
      dsDetailperson.saveChanges();
    }
    }
}


