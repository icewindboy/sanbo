package engine.dataset.database;

import java.io.Serializable;

import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.sql.ResultSet;

import com.borland.dx.dataset.Column;
import com.borland.dx.dataset.ColumnList;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.MetaDataUpdate;
import com.borland.dx.dataset.Variant;
import com.borland.dx.text.Alignment;
/**
 * <p>Title: 数据库运行信息类</p>
 * <p>Description: 数据库运行信息类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */
public final class RuntimeMetaData implements Serializable
{
  public  static final int    INVLILID_DB        = 0xffffFFFF;
  public  static final int    DB_UNKNOWN         = 0x0000;
  public  static final int    DB_INTERBASE       = 0x0001;
  public  static final int    DB_ORACLE          = 0x0002;
  public  static final int    DB_SYBASE          = 0x0003;
  public  static final int    DB_DB2             = 0x0004;
  public  static final int    DB_DBASE           = 0x0005;
  public  static final int    DB_PARADOX         = 0x0006;

  public  static final int    INVALID_DRIVER     = 0xffffFFFF;
  public  static final int    UNKNOWN_DRIVER     = 0x0000;
  public  static final int    ODBC_UNKNOWN       = 0x1000;
  public  static final int    ODBC_VISIGENIC     = 0x1100;
  public  static final int    ODBC_ORACLE        = 0x1200;

  public  static final int    JDBC4_DATAGATEWAY  = 0x2000;
  public  static final int    JDBC4_ORACLE       = 0x2100;
  public  static final int    JDBC4_DB2          = 0x2200;
  public  static final int    JDBC4_INTERCLIENT  = 0x2300;
  public  static final int    JDBC4_JCONNECT     = 0x2400;
  public  static final int    JDBC4_JDATASTORE   = 0x2500;

  public  static final int    JDBC2_DATAGATEWAY  = 0x3000;
  public  static final int    JDBC2_ORACLE       = 0x3100;
  public  static final int    JDBC2_DB2          = 0x3200;

  private static final String defUserName        = "<DEF>";
  private String              userName           = defUserName;
  private static final int    INVALID_QUOTE_CHAR = 0xffFFFF;
  private int                 quoteCharacter     = INVALID_QUOTE_CHAR;

  private int                 sqlDialect  = -1;
  private int                 sqlDriver   = INVALID_DRIVER;
  private Boolean storesLowerCaseId       = null;
  private Boolean storesUpperCaseId       = null;
  private Boolean storesLowerCaseQuotedId = null;
  private Boolean storesUpperCaseQuotedId = null;

  private transient Database         db = null;
  private transient DatabaseMetaData metaData = null;

  public RuntimeMetaData(){}

  public RuntimeMetaData(Database db)
  {
    setDatabase(db);
  }

  /**
   * 得到标识引号的字符
   * @return 返回标识引号的字符
   */
  public final char getIdentifierQuoteChar()
  {
    if (quoteCharacter == INVALID_QUOTE_CHAR) {
      quoteCharacter = '\0';
      try{
        String string = getMetaData().getIdentifierQuoteString();
        if (string.length() > 0)
          quoteCharacter = string.charAt(0);

        if (quoteCharacter == ' ')
          quoteCharacter = '\0';
      }
      catch (Throwable ex) {
        quoteCharacter  = '\0';
      }
    }
    return (char)quoteCharacter;
  }


  public synchronized final DatabaseMetaData getMetaData() {
    if (metaData == null) {
      try {
        metaData  = getConnection().getMetaData();
      }
      catch (SQLException sex) {
        DataSetException.SQLException(sex);
      }
    }
    return metaData;
  }

  /**
   * 得到数据库的连接实例
   * @return 返回数据库的连接实例
   */
  private Connection getConnection()
  {
    return this.db.getConnection();
  }

  public Database getDatabase()
  {
    return this.db;
  }

  /**
   * 设置数据库的连接实例
   * @param connDescriptor 数据库的连接描述
   */
  public void setDatabase(Database db)
  {
    //throw new DataSetException(DataSetException.CONNECTION_DESCRIPTOR_NOT_SET, "connection is null");
    this.db = db;
  }

  public void close()
  {
    this.db = null;
    metaData = null;
  }

  private synchronized void analyzeSqlDialect()
  {
    try {
      String url = getMetaData().getURL();
      if      (url.startsWith("jdbc:borland:ds"))   //NORES
        sqlDriver = JDBC4_JDATASTORE;
      if      (url.startsWith("jdbc:BorlandBridge:"))   //NORES
        sqlDriver = JDBC2_DATAGATEWAY;
      else if (url.startsWith("jdbc:BorlandBroker:"))   //NORES
        sqlDriver = JDBC4_DATAGATEWAY;
      else if (url.startsWith("jdbc:oracle:oci"))       //NORES
        sqlDriver = JDBC2_ORACLE;
      else if (url.startsWith("jdbc:oracle:thin:"))     //NORES
        sqlDriver = JDBC4_ORACLE;
      else if (url.startsWith("jdbc:db2://"))           //NORES
        sqlDriver = JDBC4_DB2;
      else if (url.startsWith("jdbc:db2:"))             //NORES
        sqlDriver = JDBC2_DB2;
      else if (url.startsWith("jdbc:interbase:"))       //NORES
        sqlDriver = JDBC4_INTERCLIENT;
      else if (url.startsWith("jdbc:sybase:Tds:"))      //NORES
        sqlDriver = JDBC4_JCONNECT;
      else if (url.startsWith("jdbc:odbc:")) {          //NORES
        String driverName = getMetaData().getDriverName();
        String part = driverName.substring(17);
        if (!driverName.substring(0,17).equalsIgnoreCase("JDBC-ODBC Bridge ")) //NORES
          sqlDriver = UNKNOWN_DRIVER;
        else if (part.equalsIgnoreCase("(iscdrv32.DLL)") ||  // Interbase      //NORES
                 part.equalsIgnoreCase("(VSORAC32.DLL)") ||  // Oracle         //NORES
                 part.equalsIgnoreCase("(vssyb32.DLL)"))     // Sybase         //NORES
          sqlDriver = ODBC_VISIGENIC;
        else if (part.equalsIgnoreCase("(SQO32_73.DLL)"))                      //NORES
          sqlDriver = ODBC_ORACLE;
        else
          sqlDriver = ODBC_UNKNOWN;
      }
      else
        sqlDriver = UNKNOWN_DRIVER;
    }
    catch (Throwable ex) {
      sqlDriver = UNKNOWN_DRIVER;
    }

    // Find the database product:
    try {
      String productName = getMetaData().getDatabaseProductName();
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
  }

  /**
   * 得到连接数据库的用户名
   * @return 返回连接数据库的用户名
   */
  String getUserName()
  {
    if (userName == defUserName)
    {
      try {
        userName = getMetaData().getUserName();
      }
      catch (Throwable ex) {
        userName = null;
      }
    }
    return userName;
  }

  public boolean storesUpperCaseId()
  {
    try{
      if(storesUpperCaseId == null)
        storesUpperCaseId = new Boolean(getMetaData().storesUpperCaseIdentifiers());
    }
    catch(SQLException ex){
      DataSetException.SQLException(ex);
    }
    return storesUpperCaseId.booleanValue();
  }

  public boolean storesLowerCaseId()
  {
    try{
      if(storesLowerCaseId == null)
        storesLowerCaseId = new Boolean(getMetaData().storesLowerCaseIdentifiers());
    }
    catch(SQLException ex){
      DataSetException.SQLException(ex);
    }
    return storesLowerCaseId.booleanValue();
  }

  public boolean storesLowerCaseQuotedId()
  {
    try{
      if(storesLowerCaseQuotedId == null)
        storesLowerCaseQuotedId = new Boolean(getMetaData().storesLowerCaseQuotedIdentifiers());
    }
    catch(SQLException ex){
      DataSetException.SQLException(ex);
    }
    return storesLowerCaseQuotedId.booleanValue();
  }

  public boolean storesUpperCaseQuotedId()
  {
    try{
      if(storesUpperCaseQuotedId == null)
        storesUpperCaseQuotedId = new Boolean(getMetaData().storesUpperCaseQuotedIdentifiers());
    }
    catch(SQLException ex){
      DataSetException.SQLException(ex);
    }
    return storesUpperCaseQuotedId.booleanValue();
  }

  public boolean isUseCaseSensitiveId()
  {
    return !(storesLowerCaseId() || storesUpperCaseId());
  }

  public boolean isUseCaseSensitiveQuotedId()
  {
    return !(storesLowerCaseQuotedId() || storesUpperCaseQuotedId());
  }

  /**
   * 得到不同的数据库不用类型的特定的sql类型
   * @return 返回不同的数据库不用类型的特定的sql类型
   */
  public int getSQLDialect()
  {
    if(sqlDialect == INVLILID_DB)
      analyzeSqlDialect();
    return sqlDialect;
  }

  /**
   * 得到数据库的类型
   * @return 返回数据库的类型
   */
  public int getSQLDriver()
  {
    if(sqlDriver == INVALID_DRIVER)
      analyzeSqlDialect();
    return sqlDriver;
  }

  public boolean isUseOracleRowID()
  {
    return getSQLDialect() == DB_ORACLE;
  }

  /**
   * 当前数据库是否需要用到方案名
   * @return 返回是否需要用到方案名
   */
  public boolean isUseSchemaName()
  {
    int sqlDriver = getSQLDriver();
    return sqlDriver == JDBC2_DB2 || sqlDriver == JDBC4_DB2;
  }

  /**
   * 当前数据库是否需要用到表名
   * @return 返回是否需要用到表名
   */
  public boolean isUseTableName()
  {
    int sqlDriver = getSQLDriver();
    int sqlDialect = getSQLDialect();
    return sqlDriver == JDBC2_DATAGATEWAY && sqlDriver == JDBC4_DATAGATEWAY
           && (sqlDialect == DB_DBASE || sqlDialect == DB_PARADOX);
  }


  public boolean isUseSpacePadding()
  {
    return false;
  }

  public boolean isReuseSaveStatements()
  {
    return true;
  }

  /**
   * Use the setObject call instead of setString if possible.
   * Some drivers need to know the sqlType to successfully store strings.
   * (VARCHAR or CHAR).
   * @return 是否将设置对象为字符串
   */
  public boolean isUseSetObjectForStrings()
  {
    return false;
  }

  /**
   * Use the setObject call instead of setBinaryStream if possible.
   * Some drivers need to know the sqlType to successfully store blobs.
   * However the SUN ODBC driver has a bug in setObject for blobs, where
   * the blobs are chopped off to 2000 bytes.
   * @return 是否将设置对象为流
   */
  public boolean isUseSetObjectForStreams()
  {
    int sqlDriver = getSQLDriver();
    return sqlDriver == ODBC_ORACLE || sqlDriver == ODBC_UNKNOWN || sqlDriver == ODBC_VISIGENIC;
  }

  /**
   * 处理ResultSet的各个列的名称和属性，并返回Column数组给StroageDataSet
   * @param db Database对象实例
   * @param metaDataUpdate metaDataUpdate
   * @param result ResultSet类
   * @param isUseString 所有字段是否全部使用String类型,不管数据库字段类型
   * @return 返回Column数组给StroageDataSet
   * @throws SQLException 抛出SQLException异常
   */
  synchronized Column[] processMetaData(int metaDataUpdate, ResultSet result, boolean isUseString)
      throws SQLException
  {
    ResultSetMetaData metaResult = result.getMetaData();
    //数据库的类型
    boolean isInterclient = sqlDriver == JDBC4_INTERCLIENT;

    int resultColumns = metaResult.getColumnCount();
    ColumnList columnList = new ColumnList(resultColumns);

    int sqlType, precision, displaySize, scale;
    String name, label;
    boolean search;

    for (int index=1; index<=resultColumns; index++) {
      search  = true;
      sqlType  = metaResult.getColumnType(index);
      name     = metaResult.getColumnName(index);
      label    = name;

      try {
        label = metaResult.getColumnLabel(index);
      }
      catch (SQLException se) {};

      try{
        precision  = metaResult.getPrecision(index);
      }
      catch(NumberFormatException e){
        precision  = Integer.MAX_VALUE;
      }

      displaySize = metaResult.getColumnDisplaySize(index);
      scale       = metaResult.getScale(index);

      if (sqlType == java.sql.Types.LONGVARBINARY || sqlType == java.sql.Types.LONGVARCHAR)
        search = false;
      else if ((metaDataUpdate&MetaDataUpdate.SEARCHABLE) != 0) {
        try { search = metaResult.isSearchable(index); }
        catch (SQLException se) { };
      }

      if (scale == -127 && sqlType == java.sql.Types.NUMERIC && sqlDriver == JDBC4_ORACLE) {
        sqlType = java.sql.Types.DOUBLE;
        scale   = 0;
        search  = false;
      }

      if (isInterclient)
        name = label;

      if (name == null)
        name = "";

      int count = 1;
      int initialLength = name.length();

      if (initialLength == 0) {
        name = "CALC_1";
        initialLength = 5;
        count = 2;
      }

      while(columnList.findOrdinal(name) != -1) {
        name  = name.substring(0,initialLength)+count;
        ++count;
      }

      columnList.addColumn(
          createColumn(name, label, sqlType, precision, displaySize, scale, search, isUseString)
          );
    }
    return columnList.getColumnsArray();
  }

  /**
   * 创建Column对象
   * @param name Column名称
   * @param label Column标签
   * @param sqlType 数据类型
   * @param precision 精度
   * @param displaySize 显示的长度
   * @param scale scale
   * @param search 是否可作为WHERE子句的条件
   * @param isUseStringType 是否数据集的Column对象全部用String类型代替
   * @return 返回创建的Column对象
   */
  private static Column createColumn(String name, String label, int sqlType, int precision,
                                     int displaySize, int scale, boolean search, boolean isUseStringType)
  {
    Column column = new Column();
    column.setColumnName(name);
    column.setServerColumnName(name);
    column.setCaption(label);
    column.setSearchable(search);
    getTypeInfo(column, sqlType, precision, displaySize, scale, isUseStringType);
    return column;
  }

  /**
   * 设置Column对象的sqlType, precision, displaySize, scale并返回StroageDataSet表示的数据类型
   * @param column Column对象
   * @param sqlType SQL数据类型
   * @param precision 精度,即字段的有效长度
   * @param displaySize 显示的长度
   * @param scale scale 刻度
   * @param isUseStringType 是否数据集的Column对象全部用String类型代替
   * @return 返回StroageDataSet表示的数据类型
   */
  private static int getTypeInfo(Column column, int sqlType, int precision,
                                 int displaySize, int scale, boolean isUseStringType)
  {
    /*没有用道的
    public final static int NULL		=   0;
    public final static int DISTINCT            = 2001;
    public final static int STRUCT              = 2002;
    public final static int ARRAY               = 2003;
    */

    int type      = Variant.STRING;
    int alignment = Alignment.RIGHT | Alignment.MIDDLE;

    switch (sqlType) {
      case java.sql.Types.CLOB:
      case java.sql.Types.LONGVARCHAR:
        type        = Variant.STRING;
        alignment   = Alignment.LEFT | Alignment.MIDDLE;
        precision   = -1;
        scale       = -1;
        break;

      case -8:
        precision   = -1;
      case java.sql.Types.CHAR:
      case java.sql.Types.VARCHAR:
        type        = Variant.STRING;
        alignment   = Alignment.LEFT | Alignment.MIDDLE;
        scale       = -1;
        if (precision == 0 && displaySize > 0)
          precision = displaySize;
        break;

      case java.sql.Types.NUMERIC:
      case java.sql.Types.DECIMAL:
        type        = isUseStringType ? Variant.STRING : Variant.BIGDECIMAL;

        if (scale == 0 && precision == 0)
          scale = precision = -1;
        if (scale < 0)
          scale = 0;
        break;

      case java.sql.Types.BIT:
        type      = isUseStringType ? Variant.STRING : Variant.BOOLEAN;
        precision = scale = -1;
        break;

      case java.sql.Types.TINYINT:
        type      =  isUseStringType ? Variant.STRING : Variant.BYTE;
        precision = scale = -1;
        break;

      case java.sql.Types.SMALLINT:
        type      =  isUseStringType ? Variant.STRING : Variant.SHORT;
        precision = scale = -1;
        break;

      case java.sql.Types.INTEGER:
        type      =  isUseStringType ? Variant.STRING : Variant.INT;
        precision = scale = -1;
        break;

      case java.sql.Types.BIGINT:
        type      =  isUseStringType ? Variant.STRING : Variant.LONG;
        precision = scale = -1;
        break;

      case java.sql.Types.REAL:
        type      =  isUseStringType ? Variant.STRING : Variant.FLOAT;
        precision = scale = -1;
        break;

      case java.sql.Types.FLOAT:
      case java.sql.Types.DOUBLE:
        type      =  isUseStringType ? Variant.STRING : Variant.DOUBLE;
        precision = scale = -1;
        break;

      case java.sql.Types.BLOB:
      case java.sql.Types.VARBINARY:
      case java.sql.Types.LONGVARBINARY:
        precision   = -1;
      case java.sql.Types.BINARY:
        type        = Variant.INPUTSTREAM;
        alignment   = Alignment.LEFT | Alignment.MIDDLE;
        scale       = -1;
        break;

      case java.sql.Types.DATE:
        type        = isUseStringType ? Variant.STRING : Variant.DATE;
        precision   = scale = -1;
        break;

      case java.sql.Types.TIME:
        type        = isUseStringType ? Variant.STRING : Variant.TIME;
        precision   = scale = -1;
        break;

      case java.sql.Types.TIMESTAMP:
        type        =  isUseStringType ? Variant.STRING : Variant.TIMESTAMP;
        precision   = scale = -1;
        break;

      case java.sql.Types.OTHER:
      case java.sql.Types.JAVA_OBJECT:
        type        =  Variant.OBJECT;
        precision   = scale = -1;
        break;

      default:
        //Diagnostic.fail();
        break;
    }
    if (column != null) {
      column.setDataType(type);
      column.setScale(scale);
      column.setPrecision(precision);
      column.setAlignment(alignment);
      column.setSqlType(sqlType);
    }
    return type;
  }
}