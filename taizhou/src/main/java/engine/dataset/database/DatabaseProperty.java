package engine.dataset.database;

import java.sql.*;
import java.util.HashMap;
/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author hukn
 * @version 1.0
 */

public final class DatabaseProperty
{
  public   static final int    DB_UNKNOWN         = 0x0000;
  public   static final int    DB_INTERBASE       = 0x0001;
  public   static final int    DB_ORACLE          = 0x0002;
  public   static final int    DB_SYBASE          = 0x0003;
  public   static final int    DB_DB2             = 0x0004;
  public   static final int    DB_DBASE           = 0x0005;
  public   static final int    DB_PARADOX         = 0x0006;

  private  static  HashMap hm = new HashMap();

  /**
   * 得到数据库产品名称
   * @param connect 数据库连接实例
   * @param connectName 连接的产量名称
   * @return
   */
  public synchronized static int getDatabaseProductName(Connection connect, String connectName)
  {
    int dialect = DB_UNKNOWN;
    Object obj = hm.get(connectName);
    if(obj != null)
    {
      try{
        dialect = Integer.parseInt((String)obj);
        return dialect;
      }
      catch(Exception ex){}
    }
    dialect = analyzeSqlDialect(connect);
    hm.put(connectName, String.valueOf(dialect));
    return dialect;
  }


  /**
   * 分析数据库的类型
   * @param connect 数据库连接实例
   * @return
   */
  private static int analyzeSqlDialect(Connection connect)
  {
    int sqlDialect;
    try {
      String productName = connect.getMetaData().getDatabaseProductName();
      if      (productName.equalsIgnoreCase("Interbase") ||   // Visigenic ODBC
               productName.equalsIgnoreCase("INTRBASE"))      // BorlandBridge
        sqlDialect = DB_INTERBASE;
      else if (productName.equalsIgnoreCase("Oracle") ||      // Visigenic ODBC
               productName.equalsIgnoreCase("Oracle7"))       // Oracle ODBC
        sqlDialect = DB_ORACLE;
      else if (productName.equalsIgnoreCase("DBASE"))         // BorlandBridge
        sqlDialect = DB_DBASE;
      else if (productName.equalsIgnoreCase("PARADOX"))       // BorlandBridge
        sqlDialect = DB_PARADOX;
      else if (productName.startsWith("DB2"))                 // DB2
        sqlDialect = DB_DB2;
      else if (productName.length() >= 6 && productName.substring(0,6).equalsIgnoreCase("SYBASE"))
        sqlDialect = DB_SYBASE;
      else
        sqlDialect = DB_UNKNOWN;
    }
    catch (Throwable ex) {
      sqlDialect = DB_UNKNOWN;
    }
    return sqlDialect;
  }
}