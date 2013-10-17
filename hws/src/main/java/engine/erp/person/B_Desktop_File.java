package engine.erp.person;

import engine.action.*;
import engine.dataset.*;
import engine.web.observer.*;
import javax.servlet.http.*;
import engine.web.upload.*;
import engine.common.LoginBean;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 审批情况表</p>
 * <p>Description: 基础管理--审批情况表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李强
 * @version 1.0
 */

public final class B_Desktop_File extends BaseAction implements Operate
{
  /**
   * 提取审批历史记录主表所有信息的SQL语句
   */
  private static final String file_SQL
      = "  select distinct a.*,b.personid,b.isread "
      +"   from rl_document_move a,rl_receive_person b"
      +"   where a.document_id=b.document_id and b.isread =0 ? ";
  private static final String file_str_sql
      = "  select distinct a.* ,b.personid,b.isread "
      +"   from rl_document_move a,rl_receive_person b where 1<>1";
  private static final String fileadd_SQL    = "SELECT * FROM rl_annex_file  WHERE document_id= ";

  private static final String person_SQL    = "SELECT * FROM rl_receive_person  WHERE document_id= ";
  private static final String FILE_STRUT_SQL   = "SELECT * FROM rl_annex_file  WHERE 1<>1" ;
  private static final String PERSON_STRUT_SQL  = "SELECT * FROM rl_receive_person  WHERE 1<>1" ;
  private static final String edit_SQL
     = "  select * from rl_document_move where 1=1 and document_id= ";
  private static final String edit_STRUT_SQL
     = "  select * from rl_document_move where 1<>1";
  /**
   * 保存审批历史记录主表信息的数据集
   */
  private EngineDataSet dsFileTable = new EngineDataSet();
  private EngineDataSet dsFilefile = new EngineDataSet();
  private EngineDataSet dsFileperson = new EngineDataSet();
  private EngineDataSet dsEditFile = new EngineDataSet();
  private RowMap  m_RowInfo    = new RowMap();
  private transient File file = null;
  String document_id=null;
  String RECEIVE_PERSON_ID=null;
  /**
   * 点击返回按钮的URL
   */
  public  String loginId = "";   //登录员工的ID
  public static final String VIEW_DETAIL = "105475";   //主从明细
  public  String loginName = ""; //登录员工的姓名
  public  String retuUrl = null;
  public  String SMPOST = "12334456";
  public  static final String SHOW_FILE="111233543455";
  /**
   * 得到收发单据信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_Desktop_File getInstance(HttpServletRequest request)
  {
    B_Desktop_File b_FileBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_FileBean";
      b_FileBean = (B_Desktop_File)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_FileBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        b_FileBean = new B_Desktop_File();
        b_FileBean.loginId = loginBean.getUserID();
        b_FileBean.loginName = loginBean.getUserName();
        session.setAttribute(beanName, b_FileBean);
      }
    }
    return b_FileBean;
  }
  class showFile implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {

   int rownum=Integer.parseInt(data.getRequest().getParameter("rownum"));
   if(!dsFilefile.isOpen())
     return;
   if(file != null){
     file.download(data.getResponse(), null);
    }
    else{
     dsFilefile.goToRow(rownum);
     String mime_type = dsFilefile.getValue("fileformat");
     FileUpload upload = new FileUpload();
     upload.downloadField(data.getResponse(), dsFilefile, "mainfile", mime_type, null);
    }

  }
  }
  /**
   * 构造函数
   */
  private B_Desktop_File()
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
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsFileTable,file_str_sql);
    setDataSetProperty(dsFilefile,FILE_STRUT_SQL);
    setDataSetProperty(dsFileperson,PERSON_STRUT_SQL);
    setDataSetProperty(dsEditFile,edit_STRUT_SQL);


    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(VIEW_DETAIL),new Master_Add_Edit());//修改主表,及其对应的从表
    addObactioner(String.valueOf(SMPOST), new SM_POST());//修改主表,及其对应的从表
    addObactioner(String.valueOf(SMPOST), new SM_POST());//修改主表,及其对应的从表
    addObactioner(String.valueOf(SHOW_FILE), new showFile());//修改主表,及其对应的从表

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
      if(operate == null)
        operate="0";
      if(operate != null && operate.trim().length() > 0)
      {
        RunData data = notifyObactioners(operate, request, response, null);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsFileTable.isOpen() && dsFileTable.changesPending())
        dsFileTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * 主表添加或修改操作的触发类
   */
  class Master_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {

      if(m_RowInfo.size() > 0)
        m_RowInfo.clear();
        long masterRow = Long.parseLong(data.getParameter("rownum"));
        dsFileTable.goToInternalRow(masterRow+1);

      document_id=dsFileTable.getValue("document_id");
      dsEditFile.setQueryString(edit_SQL + document_id);
      if(dsEditFile.isOpen())
       dsEditFile.refresh();
     else
       dsEditFile.open();
       dsEditFile.setValue("inout_type","1");
       dsEditFile.post();
       dsEditFile.saveChanges();

      /*附件情况*/
     dsFilefile.setQueryString(fileadd_SQL + document_id);
     if(dsFilefile.isOpen())
       dsFilefile.refresh();
     else
       dsFilefile.open();
     /*接收人员情况*/
     dsFileperson.setQueryString(person_SQL +  document_id+"  and personid="+loginId );
     if(dsFileperson.isOpen())
       dsFileperson.refresh();
     else
      dsFileperson.open();
      RECEIVE_PERSON_ID=dsFileperson.getValue("RECEIVE_PERSON_ID");
      dsFileperson.setValue("isread","1");
      dsFileperson.post();
      dsFileperson.saveChanges();

      //打开从表
      data.setMessage(showJavaScript("toDetail();"));
    }
  }
  //----Implementation of the BaseAction abstract class
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsFileTable != null){
      dsFileTable.close();
      dsFileTable = null;
    }
    if(dsFilefile != null){
      dsFilefile.close();
      dsFilefile = null;
    }
    if(dsFileperson != null){
     dsFileperson.close();
     dsFileperson = null;
    }
    if(dsEditFile != null){
    dsEditFile.close();
    dsEditFile = null;
    }
    log = null;
    m_RowInfo = null;
  }
  //----Implementation of the BaseAction abstract class
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  /*得到主表一行的信息*/
  public final RowMap getMasterRowinfo() { return m_RowInfo; }
  protected Class childClassName()
  {
    return getClass();
  }

  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsFileTable.isOpen())
      dsFileTable.open();
    return dsFileTable;
  }
   /*得到表对象*/
  public final EngineDataSet getFilefileTable()
  {
    if(!dsFilefile.isOpen())
      dsFilefile.open();
    return dsFilefile;
  }
   /*得到表对象*/
  public final EngineDataSet getFilepersonTable()
  {
    if(!dsFileperson.isOpen())
      dsFileperson.open();
    return dsFileperson;
  }
  public final EngineDataSet getEditTable()
 {
   if(!dsEditFile.isOpen())
     dsEditFile.open();
   return dsEditFile;
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

     if(dsFileTable.isOpen() && dsFileTable.getRowCount() > 0)
      dsFileTable.empty();
    dsFileTable.setQueryString(combineSQL(file_SQL,"?",new String[]{" and b.personid="+loginId }));
   dsFileTable.setRowMax(null);
   dsFileTable.refresh();

   }
  }
  /**
    * 主表保存操作的触发类
     */
  class SM_POST  implements Obactioner
    {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {


        dsFileperson.setValue("sm",data.getRequest().getParameter("sm"));



        dsFileperson.post();
        dsFileperson.saveChanges();
        if(dsFileTable.isOpen() && dsFileTable.getRowCount() > 0)
        dsFileTable.empty();
        dsFileTable.setQueryString(combineSQL(file_SQL,"?",new String[]{" and b.personid="+loginId }));
        dsFileTable.setRowMax(null);
    }
    }


}
