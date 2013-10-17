package engine.dataset.database;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.borland.dx.dataset.DataSetException;

import engine.dataset.DebugSql;
import engine.dataset.EngineDataSet;
import engine.dataset.ProvideInfo;
import engine.dataset.ResolveInfo;
import engine.util.EngineRuntimeException;
import engine.util.SimplePool;
import engine.util.log.Log;
/**
 * <p>Title: 数据库管理的助手</p>
 * <p>Description: 数据库管理的助手
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author jac
 * @version 1.0
 */

public final class DatabaseHelper
{
  private static final String lineSeparator = System.getProperty("line.separator");

  //private static final String ORA ="ORA-";
  private static final String dbConfigFile ="/dbconfig.properties";
  private static transient ConnectDescriptor connDesc      = null;
  private static           SimplePool        dbProvidePool = null;

  private static Log log = new Log("engine.dataset.DAO");

  private static oracle.jdbc.pool.OracleConnectionCacheImpl cacheImpl = null;
  private static DBConnectionPool connPool                            = null;

  private DatabaseHelper(){}

  /**
   * 初始化数据库连接池连接属性
   * @param connDesc 连接描述类型
   */
  public synchronized static void init(Map config)
  {
    DatabaseHelper.connDesc = new ConnectDescriptor(config);
    int max = connDesc.getMax();
    dbProvidePool = new SimplePool(max);
    cacheImpl = null;
    connPool = null;
  }

  //自己的初始化
  private synchronized static void selfInit(){
    InputStream is = null;
    PipedInputStream pis = null;
    try{
      if(cacheImpl == null && connPool == null)
      {
        try{
          String configFile = engine.EngineServlet.getDatabaseConfigFile();
          is = new FileInputStream(configFile);
        }
        catch(EngineRuntimeException ex){
          is = EngineDataSet.class.getResourceAsStream(dbConfigFile);
        }

        if(is == null)
          throw new EngineRuntimeException("the file of dbconfig.properties not found");

        pis = new PipedInputStream();

        new engine.encrypt.SimpleEncrypt().decryptStream(is, new PipedOutputStream(pis));
        Properties config = new Properties();
        config.load(pis);
        

        

        
        String url      = (String)config.get("url");
        String user     = (String)config.get("username");
        String password = (String)config.get("password");
        String driver   = (String)config.get("driver");
        int max = 20;
        int min = 10;
        try{
          max = Integer.parseInt((String)config.get("maxnumber"));
        }
        catch(Exception ex){}
        try{
          min = Integer.parseInt((String)config.get("minnumber"));
        }
        catch(Exception ex){}

        ApplicationContext context = new ClassPathXmlApplicationContext("/spring/spring-sanbo.xml");
        //ComboPooledDataSource dataSource = (ComboPooledDataSource)context.getBean("dataSource");
        BasicDataSource dataSource = (BasicDataSource)context.getBean("dataSource");
        
//        url = dataSource.getJdbcUrl();
//        user =  dataSource.getUser();
//        password =  dataSource.getPassword();
//        driver =  dataSource.getDriverClass();
//        min =  dataSource.getMinPoolSize();
//        max =  dataSource.getMaxPoolSize();
        
      url = dataSource.getUrl();
      user =  dataSource.getUsername();
      password =  dataSource.getPassword();
      driver =  dataSource.getDriverClassName();
      max =  dataSource.getMaxActive();
//        
        dbProvidePool = new SimplePool();
        boolean isOracle = engine.EngineServlet.isOracleDatabase();
        if(isOracle){

          cacheImpl = new oracle.jdbc.pool.OracleConnectionCacheImpl();
          cacheImpl.setURL(url);
          cacheImpl.setUser(user);
          cacheImpl.setPassword(password);

          cacheImpl.setMaxLimit(max);
          if(min > 0)
            cacheImpl.setMinLimit(min);
        }
        else{
          connPool = new DBConnectionPool(driver, url, user, password, max);
        }
      }
    }
    catch(ClassNotFoundException cex){
      throw new EngineRuntimeException("not found jdbc driver class", cex);
    }
    catch(SQLException sex){
      throw new EngineRuntimeException("read dbconfig.properties file error", sex);
    }
    catch(IOException iex){
      throw new EngineRuntimeException("get jdbc connection", iex);
    }
    finally{
      try{
        if(is != null)
          is.close();
      }
      catch(IOException ex){}
      try{
        if(pis != null)
          pis.close();
      }
      catch(IOException ex){}
    }
  }

  /**
   * 得到数据库连接
   * @return 返回数据库连接对象
   */
  private synchronized static Connection getConnection()// throws SQLException, IOException
  {
    try{
      if(connDesc == null)
        selfInit();
      else if(engine.EngineServlet.isOracleDatabase()){
        if(cacheImpl == null)
          cacheImpl = new oracle.jdbc.pool.OracleConnectionCacheImpl();

        cacheImpl.setURL(connDesc.getUrl());
        cacheImpl.setUser(connDesc.getUsername());
        cacheImpl.setPassword(connDesc.getPassword());
        //
        cacheImpl.setMaxLimit(connDesc.getMax());
        cacheImpl.setMinLimit(connDesc.getMin());
      }
      else if(connPool == null){
        connPool = new DBConnectionPool(connDesc.getDriver(), connDesc.getUrl(),
                                        connDesc.getUrl(), connDesc.getPassword(),
                                        connDesc.getMax());
        //cacheImpl = new oracle.jdbc.pool.OracleConnectionCacheImpl();
      }
    }
    catch(ClassNotFoundException ex){
      log.fatal("JDBC驱动类文件没有发现", ex);
      throw new EngineRuntimeException("JDBC driver class not found", ex);
    }
    catch(SQLException ex){
      log.fatal("设置数据库连接失败", ex);
      throw new EngineRuntimeException("设置数据库连接失败", ex);
    }

    try{
      return cacheImpl != null ? cacheImpl.getConnection() : connPool.getConnection();
    }
    catch(SQLException ex){
      throw new EngineRuntimeException("创建数据库连接失败", ex);
    }
  }

  /**
   * 得到提供数据和更新提交数据的类
   * @return 返回提供数据和更新提交数据的类
   */
  private synchronized static DatabaseProvider getDBProvider()
  {
    if(connDesc == null)
      selfInit();

    DatabaseProvider dbProvider = (DatabaseProvider)dbProvidePool.get();
    if(dbProvider == null)
      dbProvider = new DatabaseProvider(getConnection());
    else
      dbProvider.setConnection(getConnection());

    return dbProvider;
  }

  private static void releaseDatabaseProvider(DatabaseProvider dbProvider)
  {
    Connection conn = dbProvider.getConnection();
    if(conn != null){
      if(cacheImpl != null){ //is oracle
        try{
          conn.close();
        }
        catch(SQLException ex){
          DataSetException.SQLException(ex);
        }
      }
      else
        connPool.freeConnection(conn);
    }
    dbProvider.close();
    dbProvidePool.put(dbProvider);
  }

  /**
   * 打印日志
   * @param MethodName 执行的方法名称
   * @param SQL SQL语句数组
   * @param ex 异常信息
   */
  private synchronized static void printLog(String MethodName, String[] SQL, Exception ex)
  {
    boolean isDebug = log.isDebugEnabled();
    StringBuffer buf = null;
    if(ex!=null || isDebug){
      buf = new StringBuffer("CALL "+MethodName);
    }

    if(SQL!=null && (ex != null || isDebug))
    {
      for(int i=0; i<SQL.length; i++)
      {
        buf.append(lineSeparator);
        buf.append("  SQL[");
        buf.append(i);
        buf.append("]: ");
        buf.append(SQL[i]);
      }
    }
    if(ex!=null)
      log.error(buf, ex);
    else if(isDebug)
      log.debug(buf);
  }

  /**
   * 打印日志
   * @param MethodName 执行的方法名称
   * @param SQL SQL语句数组
   * @param ex 异常信息
   */
  private synchronized static void printLog(String MethodName, DebugSql[] SQL, Exception ex)
  {
    boolean isDebug = log.isDebugEnabled();
    StringBuffer buf = null;
    if(ex!=null || isDebug){
      buf = new StringBuffer("CALL "+MethodName);
    }

    if(SQL!=null && (ex != null || isDebug))
    {
      for(int i=0; i<SQL.length; i++)
      {
        buf.append(lineSeparator);
        buf.append("  SQL[");
        buf.append(i);
        buf.append("]: ");
        buf.append(SQL[i].getQueryString());
      }
    }
    if(ex!=null)
      log.error(buf, ex);
    else if(isDebug)
      log.debug(buf);
  }

  /**
   * 执行无返回值的SQL(需要将此方法导出到Remote接口)
   * @param SQL 提取数据的SQL语句，如果客户端没有设置SQL,可以在此设置,比如主键
   * @throws Exception 抛出异常
   */
  public static void ExecuteSQL(String[] SQL) //throws Exception
  {
    DatabaseProvider databaseProvider = getDBProvider();
    synchronized(databaseProvider)//锁定对象
    {
      try{
        databaseProvider.execSQL(SQL);
        databaseProvider.commit();
        printLog("ExecuteSQL", SQL, null);
      }
      catch (DataSetException ex)
      {
        databaseProvider.rollback();
        printLog("ExecuteSQL", SQL, ex);
        throw ex;
      }
      finally{
        releaseDatabaseProvider(databaseProvider);
      }
    }
  }

  /**
   * 提取数据(需要将此方法导出到Remote接口)
   * @param SQL 提取数据的SQL语句，如果客户端没有设置SQL,可以在此设置,比如主键
   * @param hmArray 其他相关参数
   * @return 返回客户断的数据
   * @throws Exception 抛出异常
   */
  public static ProvideInfo[] provideData(ProvideInfo[] infos) throws Exception
  {
    DatabaseProvider databaseProvider = getDBProvider();
    synchronized(databaseProvider)//锁定对象
    {
      ProvideInfo[] data = null;
      try{
        data = databaseProvider.provideData(infos);
        databaseProvider.commit();
        printLog("provideData", infos, null);
      }
      catch (Exception ex)
      {
        databaseProvider.rollback();
        printLog("provideData", infos, ex);
        throw ex;
      }
      finally{
        releaseDatabaseProvider(databaseProvider);
      }
      return data;
    }
  }

  /**
   * 更新表数据(需要将此方法导出到Remote接口)
   * @param changes 客户端更改后的数据，你可以提交到数据库之前更改里面的数据
   * @param SQL SQL语句
   * @param hmRelation 其他相关参数
   * @return SQL SQL语句
   * @throws Exception 抛出异常
   */
  public static ResolveInfo[] resovleData(ResolveInfo[] changeInfos) throws Exception
  {
    DatabaseProvider databaseProvider = getDBProvider();
    synchronized(databaseProvider)
    {
      ResolveInfo[] infos = null;
      try{
        infos = databaseProvider.resolveData(changeInfos);
        databaseProvider.commit();
        printLog("resovleData", changeInfos, null);
        return infos;
      }
      catch (Exception ex)
      {
        databaseProvider.rollback();
        printLog("resovleData", changeInfos, ex);
        throw ex;
      }
      finally{
        releaseDatabaseProvider(databaseProvider);
      }
    }
  }

  /**
   * 设置要提交数据集的一些必须的元素（包括 PrimaryKeys, SchemaName, TableName）
   * @param infos 需要被设置属性的数据集的对象
   * @return 返回的信息（PrimaryKeys, SchemaName, TableName）
   * @throws DataSetException SQL异常
   */
  public static ResolveInfo[] processElement(ResolveInfo[] infos) //throws SQLException
  {
    DatabaseProvider databaseProvider = getDBProvider();
    synchronized(databaseProvider)//锁定对象
    {
      try{
        databaseProvider.processDataSetElements(infos);
        return infos;
      }
      catch (RuntimeException ex)
      {
        printLog("processElement", infos, ex);
        throw ex;
      }
      finally{
        releaseDatabaseProvider(databaseProvider);
      }
    }
  }

  /**
   * 提取SQL语句的总记录数量
   * @param queryString SQL语句
   * @return 总记录
   */
  public static int getRowCount(String queryString)
  {
    DatabaseProvider databaseProvider = getDBProvider();
    synchronized(databaseProvider)//锁定对象
    {
      try{
        return databaseProvider.getRowCount(queryString);
      }
      catch (RuntimeException ex)
      {
        printLog("getRowCount", new String[]{queryString}, ex);
        throw ex;
      }
      finally{
        releaseDatabaseProvider(databaseProvider);
      }
    }
  }

  /**
   * 提取序列的值
   * @param sequenceName 序列名称
   * @return 返回序列的值
   */
  public static String getSequence(String sequenceName)
  {
    DatabaseProvider databaseProvider = getDBProvider();
    synchronized(databaseProvider)//锁定对象
    {
      try{
        String value = databaseProvider.getSequence(sequenceName);
        databaseProvider.commit();
        return value;
      }
      catch (RuntimeException ex)
      {
        databaseProvider.rollback();
        printLog("getSequence", new String[]{sequenceName}, ex);
        throw ex;
      }
      finally{
        releaseDatabaseProvider(databaseProvider);
      }
    }
  }

}