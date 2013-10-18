package engine.erp.common.install;

import engine.dataset.*;
import engine.action.*;
import engine.action.Operate;
//import engine.mo.observer.*;
import engine.encrypt.SimpleEncrypt;
import engine.util.MessageFormat;
import engine.util.StringUtils;
import engine.util.version.Version;
import engine.report.util.TempletData;
import engine.report.util.ReportTempletParser;
import engine.util.log.LogHelper;

import engine.web.observer.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.sql.*;


/**
 * <p>Title: ERP数据库安装程序.</p>
 * <p>Description:
 *处理流程:
 *点击安装后执行过程
 *1.	生成加密文件
 *2.	用Sys用户连接数据库创建表空间和索引和用户.
 *3.	用创建用户连接数据库
 *4.	执行Sql(Sql语句从文件中取出来.
 *		其中:从sql文件取出sql时
 *		要把{tablespace}及{tableindex}这两个中括号中的字串替换为
 *		用户在上面界面相应文本框中输入的名称)
 *5.	dbconfig.properties加密文件里面是我们默认的用户名等信息.
 *		当这个程序运行后,可以在界面上修改ERP用户名, 密码等.
 *		然后须要再写回dbconfig.properties中.
 * </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public final class InstallSystem extends BaseAction implements Operate
{
  public static final String ONLY_CONFIG = "10001";
  //加密文件名称
  public final static String DBCONFIGNAME = "dbconfig.properties";
  //创建表空间的SQL
  private final static String CREATE_TABLESPACE_SQL
      = "CREATE TABLESPACE {tablespace} LOGGING DATAFILE '{tablespace}.ora' SIZE 50M "
      + "AUTOEXTEND ON NEXT 10M MAXSIZE UNLIMITED EXTENT MANAGEMENT LOCAL SEGMENT SPACE MANAGEMENT AUTO";
  //初始化数据库信息的SQL
  private final static String[] INIT_DBINFO_SQL = new String[]{
    //创建用分配表空间权限和限额（无限制）
    "CREATE USER {username} PROFILE DEFAULT IDENTIFIED BY \"{password}\" DEFAULT TABLESPACE {tablespace} "+
    "QUOTA UNLIMITED ON {tableindex} QUOTA UNLIMITED ON {tablespace} ACCOUNT UNLOCK",
    //分配创建存储过程的权限
    "GRANT CREATE PROCEDURE TO {username}",
    //分配创建触发器的权限
    "GRANT CREATE TRIGGER TO {username}",
    //分配连接的权限
    "GRANT \"CONNECT\" TO {username}"
  };
  //主要用于保存从文件中读取的配置信息.
  private transient Properties props = null;
  private String webinfPath = null;
  private String filePath = null;
  public  boolean isRemote = false;

  /**
   * 得到安装系统的实例
   * @param request jsp请求
   * @return 返回安装的实例
   */
  public static InstallSystem getInstance(HttpServletRequest request)
  {
    InstallSystem installSystemBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      installSystemBean = (InstallSystem)session.getAttribute("installSystemBean");
      if(installSystemBean == null)
      {
        installSystemBean = new InstallSystem();
        session.setAttribute("installSystemBean", installSystemBean);
      }
    }
    return installSystemBean;
  }

  /**
   * 构造函数
   */
  private InstallSystem() {
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
    InstallPost installPost = new InstallPost();
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(ONLY_CONFIG), installPost);
    addObactioner(String.valueOf(POST), installPost);
    addObactioner(String.valueOf(POST_CONTINUE), new InstallContinue());
  }

  /**
   * Implement this engine.project.BaseAction abstract method
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName() {
    return getClass();
  }

  //----Implementation of the BaseAction abstract class
  /**
   * JSP调用的函数. 业务操作的入口函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  public String doService(HttpServletRequest request, HttpServletResponse response) throws java.lang.Exception
  {
     try{
       String operate = request.getParameter(OPERATE_KEY);
       if(operate == null || operate.trim().length()== 0)
         operate = String.valueOf(INIT);
       //{
         RunData data = notifyObactioners(operate, request, response, null);
         if(data == null)
           return showMessage("无效操作", false);
         if(data.hasMessage())
           return data.getMessage();
       //}
       return "";
     }
     catch(Exception ex){
       //if(dsMasterTable.isOpen() && dsMasterTable.changesPending())
         //dsMasterTable.reset();
       log.error("doService", ex);
       return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * 判断是否是本机安装并设置保存数据库连接文件的目录路径
   * @param request WEB请求
   * @param webinfpath WEB-INF目录的路径
   * @return 返回是否是本机安装
   * @throws IOException IO异常
   */
  public boolean isLocalHost(HttpServletRequest request, String webinfpath)
      throws IOException
  {
    if(!request.getRemoteAddr().equals("127.0.0.1"))
      return false;

    webinfPath = webinfpath;
    filePath = webinfpath + File.separator + "classes" + File.separator +"conf" + File.separator + DBCONFIGNAME;
    return true;
  }

  /**
   * 数据库连接的属性文件的值
   * @param key 键
   * @return 返回值
   */
  public String getPropertyValue(String key)
  {
    if(props==null)
      return "";
    String s = (String)props.get(key);
    return s == null ? "" : s;
  }

  /**
   * session失效是触发的事件
   * @param event session绑定事件
   */
  public void valueUnbound(HttpSessionBindingEvent event) {
    ;
  }

  /**
   * @param fullFileName 传入要读取的文件的完整文件名.
   * @return 返回文件读取后的属性对象
   * @throws IOException
   */
  static Properties getConfigInfo(String fullFileName) throws IOException
  {
    //存放从被解密出来的文件中取出的资料
    PipedInputStream pis = null ;
    FileInputStream is = null;
    try
    {
      pis = new PipedInputStream();
      is = new FileInputStream(fullFileName);
      new SimpleEncrypt().decryptStream(is, new PipedOutputStream(pis));
      Properties properties = new Properties();
      properties.load(pis);
      return properties;
    }
    finally
    {
      try{
        if(pis != null)
          pis.close();
      }
      catch(IOException ex){}
      try{
        if(is != null)
          is.close();
      }
      catch(IOException ex){}
    }
  }

  /**
   *  取得用户界面上的修改过的信息,重新加密.
   * (加密信息有可能是已经修改过的.用户在界面上可以修改配置信息.如erp用户名密码等.)
   * @para ps 用户在界面上修改过的配置信息.此参数保存了收集来的界面信息.如用户名等
   * @para 文件全名.
   * @throws Exception
   */
  public static void updateConfigInfo(Properties ps, String fullFileName) throws Exception{
    //建立两个管道文件操作对象.一个输入,一个输出
    PipedInputStream pis = null ;
    FileOutputStream os = null;
    try
    {
      pis = new PipedInputStream();
      //因为管道对象须要依附于它的相反的对象.即I依附于
      PipedOutputStream pos = new PipedOutputStream(pis);
      try{
        ps.store(pos, "engine");
      }
      finally
      {
        try{
          if(pos != null)
            pos.close();
        }
        catch(Exception ex){
          ex.printStackTrace();
        }
      }
      os = new FileOutputStream(fullFileName);
      new SimpleEncrypt().encryptStream(pis, os);
    }
    finally{
      try{
        if(pis != null)
          pis.close();
      }
      catch(Exception ex){}
      try{
        if(os != null)
          os.close();
      }
      catch(Exception ex){}
    }
  }

  /**
   * 初始化操作的触发类
   * 读取加密文件,取出数据,提供给网页使其能显示出来在网页上.
   */
  class Init implements Obactioner
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
      //取加密文件中的资料.存入数据集,供页面使用.
      props = getConfigInfo(filePath);
    }
  }

  /**
   * 安装操作要做:
   * 1.得到页面上用户输入的要用来用SYS连接数据库的资料.如url,driver,表空间,索引空间.
   *   下面的操作要用此来做db的数据库的最初操作.
   * 2.用上出得到的SYS连接到db后,建立表空间,索引空间,及用户输入的ERP用户名.断开链接.
   * 3.用建立的ERPUse再连接到DB,从文件中取出sql执行.
   */
  class InstallPost implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      boolean isOnlyConfig = action.equals(ONLY_CONFIG);
      initDataBase(data);
      if(data.hasMessage())
        return;
      //保存数据库连接的配置文件信息
      updateConfigInfo(props, filePath);

      if(isOnlyConfig)
      {
        data.setMessage(showJavaScript("alert('配置成功！');"));
        return;
      }
      //
      if(install())
        data.setMessage(showJavaScript("errorinfo();"));
      else
        data.setMessage(showJavaScript("alert('安装成功！');"));
    }

    /**
     * -----------------------------------------------------------------------------
     * The following class provides an example of using JDBC to connect to an
     * Oracle database using several advanced options. For example, one of the most
     * used option is connecting to a database as SYSDBA. The following table
     * contains
     *
     * Connection Properties Recognized by Oracle JDBC Drivers
     * -------------------------------------------------------
     * Name                Short Name Type     Description
     * ------------------- ---------- -------- -----------------------------------
     * user                n/a        String   The user name for logging into the
     *                                         database.
     * password            n/a        String   The password for logging into the
     *                                         database.
     * database            server     String   The connect string for the database.
     * internal_logon      n/a        String   A role, such as SYSDBA or SYSOPER,
     *                                         that allows you to log on as SYS.
     * defaultRowPrefetch  prefetch   String   (containing integer value)
     *                                         The default number of rows to
     *                                         prefetch from the server.
     *                                         (default value is "10")
     * remarksReporting    remarks    String   (containing boolean value)
     *                                         "true" if getTables() and
     *                                         getColumns() should report
     *                                         TABLE_REMARKS; equivalent to using
     *                                         setRemarksReporting().
     *                                         (default value is "false")
     * defaultBatchValue   batchvalue String   (containing integer value)
     *                                         The default batch value that triggers
     *                                         an execution request.
     *                                         (default value is "10")
     * includeSynonyms     synonyms   String   (containing boolean value)
     *                                         "true" to include column information
     *                                         from predefined "synonym" SQL
     *                                         entities when you execute a
     *                                         DataBaseMetaData getColumns() call;
     *                                         equivalent to connection
     *                                         setIncludeSynonyms() call.
     *                                         (default value is "false")
     * -----------------------------------------------------------------------------
     */
    /**
     * 此访法执行序列紧接在在用户更新界面信息后按安装后,执行加密操作之后.<br>
     * 然后SYS连接数据库,做数据库操作.建立表空间,索引空间.及ERP用户.<br>
     * (加密信息有可能是已经修改过的.用户在界面上可以修改配置信息.)<br>
     * @throws Exception 异常
     */
    private void initDataBase(RunData data)
        throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException
    {
      String sysuser = data.getParameter("sysuser", "").toUpperCase();
      if(sysuser.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请输入SYS用户名');"));
        return;
      }

      String syspassword = data.getParameter("syspassword", "");
      //
      String driver = data.getParameter("driver", "");//(String)props.get("driver");
      String url = data.getParameter("url", "");
      if(url.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请输入数据库连接的URL');"));
        return;
      }
      String username = data.getParameter("username", "").toUpperCase();
      if(username.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请输入连接数据库的用户名');"));
        return;
      }
      //
      String password = data.getParameter("password", "");
      if(password.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请输入连接数据库的用户密码');"));
        return;
      }
      //表空间名称
      String tablespace = data.getParameter("tablespace", "").toUpperCase();
      if(tablespace.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请输入表空间名称');"));
        return;
      }
      //索引空间名称
      String tableindex = data.getParameter("tableindex", "").toUpperCase();
      if(tableindex.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请输入索引空间名称');"));
        return;
      }
      //
      String minnumber = data.getParameter("minnumber", "");//
      String min = checkInt(minnumber, "连接池最小连接数");
      if(min != null)
      {
        data.setMessage(min);
        return;
      }

      String maxnumber = data.getParameter("maxnumber", "");//
      String max = checkInt(maxnumber, "连接池最大连接数");
      if(max != null)
      {
        data.setMessage(max);
        return;
      }
      //保存配值到内存
      if(props==null)
        props = new Properties();
      props.put("url", url);
      props.put("username", username);
      props.put("password", password);
      props.put("tablespace", tablespace);
      props.put("tableindex", tableindex);
      props.put("maxnumber", maxnumber);
      props.put("minnumber", minnumber);

      Properties conProps = new Properties();
      conProps.put("user", sysuser);
      conProps.put("password", syspassword);
      if(sysuser.equals("SYS"))
        conProps.put("internal_logon", "sysdba");
      //tablespace, tableindex, username, password,
      /*
      Class.forName(driver).newInstance();
      Connection conn = null;
      Statement stmt = null;
      StringBuffer result = new StringBuffer();
      try{
        conn = DriverManager.getConnection(url, conProps);
        //SYS连接上数据库后后,即建立表空间,索引空间.
        stmt = conn.createStatement();
        //创建表空间
        String sql = StringUtils.replace(CREATE_TABLESPACE_SQL, "{tablespace}", tablespace);
        String msg = "创建表空间失败";
        getExecuteUpdateMessage(stmt, sql, msg, result);
        //创建索引空间
        if(!tablespace.equals(tableindex))
        {
          msg = "创建索引空间失败，请确认是否已经存在该表空间！";
          sql = StringUtils.replace(CREATE_TABLESPACE_SQL, "{tablespace}", tableindex);
          getExecuteUpdateMessage(stmt, sql, msg, result);
        }
        //执行其他的SQL
        MessageFormat format = new MessageFormat();
        for(int i=0; i<INIT_DBINFO_SQL.length; i++)
        {
          msg = i==0 ? "创建用户失败，请确认是否已经存在该用户！" : (i+1)+".赋于权限失败！";
          format.applyPattern(INIT_DBINFO_SQL[i]);
          sql = format.format(props);
          getExecuteUpdateMessage(stmt, sql, msg, result);
        }
        //
        if(result.length() > 0)
          data.setMessage(showMessage(result.toString(), false) + showJavaScript("continueDlg();"));
      }
      finally{
        try{
          if(conn != null)
            conn.close();
        }catch(SQLException ex){}
        try{
          if(stmt != null)
            stmt.close();
        }catch(SQLException ex){}
      }

      */
    }

    /**
     * 得到执行是否成功SQL的信息。
     * @param stmt 执行SQL预警机的声明
     * @param sql 语句
     * @param title 执行失败的标题语句
     * @param result 失败信息
     * @return 返回失败信息
     */
    private StringBuffer getExecuteUpdateMessage(Statement stmt, String sql, String title, StringBuffer result)
    {
      if(result == null)
        result = new StringBuffer();
      try{
        stmt.executeUpdate(sql);
      }
      catch(SQLException ex){
        log.warn(title, ex);
        result.append(title).append(":").append(ex.getMessage()).append("\n");
      }
      return result;
    }
  }

  /**
   * 安装系统
   * @return 返回是否有出错信息
   */
  private boolean install() throws Exception
  {
    //属性提供者和更新者属性
    setProviderResolver();
    //更新数据库的必要信息, 从sql文件中读取sql语句然后执行, 在更新基础数据
    String installFile = webinfPath + File.separator + "sql" + File.separator + "install.properties";
    //取得数据库的连接
    String driver = (String)props.get("driver");
    String url = (String)props.get("url");
    String username = (String)props.get("username");
    String password = (String)props.get("password");
    Class.forName(driver).newInstance();
    Connection conn = DriverManager.getConnection(url, username, password);
    //
    try{
      return resolveDataBase(installFile, props, conn, dataSetProvider, dataSetResolver);
    }
    finally
    {
      try{
        if(conn != null)
          conn.close();
      }
      catch(SQLException ex){}
    }
  }

  /**
   * 创建系统要用的表空间,索引空间,用户时，出现错误，排除错误信息后继续安装
   * 从文件中取出sql执行.
   */
  class InstallContinue implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //保存数据库连接的配置文件信息
      updateConfigInfo(props, filePath);
      if(install())
        data.setMessage(showJavaScript("errorinfo();"));
      else
        data.setMessage(showJavaScript("alert('安装成功');"));
    }
  }

  /**
   * 从属性文件中读取文件列表，并执行SQL语句或更新基础数据
   * @param propertyFile 属性文件名称
   * @param conn 数据库的连接
   * @throws FileNotFoundException 文件没有发现的异常
   * @throws UnsupportedEncodingException 文件解码异常
   * @throws IOException IO异常
   * @return 返回是否有出错
   */
  static boolean resolveDataBase(String sqlPropertyFile, Properties configProps, Connection conn,
                                 EngineDataSetProvider dataSetProvider,
                                 EngineDataSetResolver dataSetResolver)
      throws FileNotFoundException, UnsupportedEncodingException, IOException, SQLException
  {
    boolean hasError = false;
    FileInputStream isProperty = null;
    File file = new File(sqlPropertyFile);
    String parent = file.getParent();
    try
    {
      isProperty = new FileInputStream(file);
      Properties properties = new Properties();
      properties.load(isProperty);
      //执行sql语句
      String objectStr = (String)properties.get("sqls");
      if(objectStr != null && objectStr.trim().length() > 0)
      {
        String[] objects = StringUtils.parseString(objectStr.trim(), ",");
        for(int i=0; i<objects.length; i++)
        {
          String filepath = parent + File.separator + objects[i].trim();
          boolean result = executeSQL(filepath, configProps, false, conn);
          hasError = hasError || result;
        }
        System.gc();
      }
      //执行升级系统的SQL语句
      objectStr = (String)properties.get("updates");
      if(objectStr != null)
      {
        File parentFile = file.getParentFile();
        File[] updateFiles = parentFile.listFiles();
        String parentPath = parentFile.getAbsolutePath();
        Version curVer = null;
        ArrayList vers = new ArrayList();
        for(int i=0; i<updateFiles.length; i++)
        {
          String name = updateFiles[i].getName();
          //是否是文件,是否是sql结尾的文件
          if(!updateFiles[i].isFile() || !name.startsWith(objectStr) || !name.endsWith(".sql"))
            continue;

          name = name.substring(objectStr.length(), name.length()-4);
          if(curVer == null)
            curVer = getSoftVersion(conn);
          //是否比当前的版本大
          Version newVersion = new Version(name);
          if(newVersion.compareTo(curVer) > 0)
            vers.add(newVersion);
        }
        //升级
        Version[] sortedVers = sortVersions(vers);
        StringBuffer buf = new StringBuffer();
        for(int j=sortedVers.length-1; j>=0; j--)
        {
          buf.setLength(0);
          buf.append(parentPath).append(File.separator);
          buf.append(objectStr).append(sortedVers[j].toString()).append(".sql");
          String path = buf.toString();
          boolean result = executeSQL(path, configProps, false, conn);
          hasError = hasError || result;
        }
        System.gc();
      }
      //执行创建对象的SQL语句
      objectStr = (String)properties.get("objects");
      if(objectStr != null && objectStr.trim().length() > 0)
      {
        String[] objects = StringUtils.parseString(objectStr.trim(), ",");
        for(int i=0; i<objects.length; i++)
        {
          String filepath = parent + File.separator + objects[i].trim();
          boolean result = executeSQL(filepath, configProps, true, conn);
          hasError = hasError || result;
        }
        System.gc();
      }
      //更行基础数据
      String dataStr = (String)properties.get("datas");
      if(dataStr != null && dataStr.trim().length() > 0)
      {
        String[] datas = StringUtils.parseString(dataStr.trim(), ",");
        for(int i=0; i<datas.length; i++)
        {
          String filepath = parent + File.separator + datas[i].trim();
          TempletData data = ReportTempletParser.createTemplet(filepath);
          boolean result = TableDataFacade.updateTablesData(dataSetProvider, dataSetResolver, data);
          hasError = hasError || result;
        }
        System.gc();
      }
    }
    finally
    {
      try{
        if(isProperty != null)
          isProperty.close();
      }
      catch(IOException ex){}
    }
    return hasError;
  }

  /**
   * 将版本号按降序排序
   * @param versions 需要版本号数组
   * @return 返回排序后的数组
   */
  private static Version[] sortVersions(List versions)
  {
    List list = new ArrayList(versions.size());
    for(int i=0; i<versions.size(); i++)
    {
      boolean isInsert = false;
      for(int j=0; j<list.size(); j++)
      {
        Version temp = (Version)list.get(j);
        if(temp.compareTo(versions.get(i)) > 0)
          continue;
        isInsert = true;
        //如果可插入
        if(isInsert)
        {
          list.add(j, versions.get(i));
          break;
        }
      }
      if(!isInsert)
        list.add(versions.get(i));
    }
    return (Version[])list.toArray(new Version[list.size()]);
  }

  /**
   * 得到软件的版本号
   * @param conn 数据库连接对象
   * @return 返回软件的版本号
   */
  private static Version getSoftVersion(Connection conn) throws SQLException
  {
    String sql = "SELECT VALUE FROM SYSTEMPARAM WHERE CODE='SYS_SOFT_VERSION'";
    Statement stmt = conn.createStatement();
    ResultSet result = null;
    try{
      result = stmt.executeQuery(sql);
      if(result.next())
      {
        String ver = result.getString(1);
        return new Version(ver);
      }
      else
      {
        try{
          String init = "INSERT INTO SYSTEMPARAM(paramid, code, name, value, deptid, isshow) "
                      + "VALUES(13, 'SYS_SOFT_VERSION','软件版本', '1.0.0.0', 0, 0)";
          stmt.executeUpdate(init);
        }
        catch(SQLException sex){}
        return new Version("1.0.0.0");
      }
    }
    finally{
      if(result != null)
        result.close();
      try{
        if(stmt != null)
          stmt.close();
      }
      catch(Exception ex1){}
    }
  }

  /**
   * 执行一些SQL语句
   * @param filename SQL文件路径名称
   * @param isOnlySlash 是否只用"/"拆分SQL
   * @return 返回是否有出错
   * @throws FileNotFoundException 文件没有发现的异常
   * @throws UnsupportedEncodingException 文件解码异常
   * @throws IOException IO异常
   */
  private static boolean executeSQL(String filename, Properties spaceConfig,
                                    boolean isOnlySlash, Connection conn)
      throws FileNotFoundException, UnsupportedEncodingException, IOException
  {
    //读取文件
    List sqls = readFile(filename, isOnlySlash);
    //
    boolean hasError = false;
    MessageFormat format = new MessageFormat();
    PreparedStatement stmt = null;
    for(int i=0; i<sqls.size(); i++)
    {
      String sql = (String)sqls.get(i);
      //{tablespace}及{tableindex}这两个中括号中的字串替换为
      format.applyPattern(sql);
      sql = format.format(spaceConfig);
      //剥离并剔除与回车符号有关的非法字符
      //sql = StringUtils.stripEnterSymbol(sql);
      try{
        stmt = conn.prepareStatement(sql);
        stmt.executeUpdate();
        conn.commit();
        //dataSetResolver.updateQuery(new String[]{sql});
      }
      catch(Exception ex){
        LogHelper.getLogHelper("更新数据库").error(sql, ex);
        //ex.printStackTrace();
        hasError = true;
      }
      finally{
        try{
          stmt.close();
        }
        catch(Exception ex1){}
      }
    }
    return hasError;
  }

  /**
   * 以特定的格式"/"或;号分段SQL
   * @param filename 文件路径名称
   * @param isOnlySlash 是否只用"/"拆分SQL
   * @throws FileNotFoundException 文件没有发现的异常
   * @throws UnsupportedEncodingException 文件解码异常
   * @throws IOException IO异常
   */
  private static List readFile(String filename, boolean isOnlySlash)
      throws FileNotFoundException, UnsupportedEncodingException, IOException
  {
    String s = null;
    BufferedReader in = new BufferedReader( new InputStreamReader( new FileInputStream( filename ),"GBK"));
    ArrayList list = new ArrayList();
    StringBuffer buf = new StringBuffer();
    while((s = in.readLine()) != null)
    {
      //"/"表示一段SQL语句的结束
      if(s.startsWith("/") && !s.startsWith("/*"))
      {
        list.add(buf.toString());
        buf.setLength(0);
        continue;
      }
      //";"SQL语句的结束, 并需要剔除该字符
      else if(!isOnlySlash && s.endsWith(";"))
      {
        buf.append(s.substring(0, s.length()-1));
        list.add(buf.toString());
        buf.setLength(0);
        continue;
      }
      //提示信息,一些PL/SQL Developer的特殊标志
      else if(s.startsWith("prompt") || s.startsWith("spool")
              || (s.startsWith("--") && s.endsWith("--")) || s.length() == 0)
        continue;
      else
        buf.append(s).append("\n");
    }
    return list;
  }
}