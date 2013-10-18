package engine.dataset.database;

import java.io.*;
import java.sql.*;
import java.util.*;

/**
 * <p>Title: 数据库连接池管理</p>
 * <p>Description: 数据库连接池管理</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author jac
 * @version 1.0
 */

public final class DBConnectionPool
{
  private int checkedOut;
  private Vector freeConnections;
  private int maxConn;

  private String driver = null;
  private String URL    = null;
  private String user   = null;
  private String password = null;

  public synchronized void freeConnection(Connection connection)
  {
    freeConnections.addElement(connection);
    checkedOut--;
    notifyAll();
  }

  public synchronized Connection getConnection() throws SQLException
  {
    Connection connection = null;
    if(freeConnections.size() > 0)
    {
      connection = (Connection)freeConnections.firstElement();
      freeConnections.removeElementAt(0);
      try
      {
        if(connection.isClosed())
        {
          connection = getConnection();
        }
      }
      catch(SQLException sqlexception)
      {
        connection = getConnection();
      }
    }
    else if(maxConn == 0 || checkedOut < maxConn)
      connection = newConnection();
    if(connection != null)
      checkedOut++;
    return connection;
  }

  public synchronized Connection getConnection(long waitTime) throws SQLException
  {
    long l1 = (new java.util.Date()).getTime();
    Connection connection;
    while((connection = getConnection()) == null)
    {
      try{
        wait(waitTime);
      }
      catch(InterruptedException interruptedexception) {}

      if((new java.util.Date()).getTime() - l1 >= waitTime)
        return null;
    }
    return connection;
  }

  public synchronized void release()
  {
    for(Enumeration enumeration = freeConnections.elements(); enumeration.hasMoreElements();)
    {
      Connection connection = (Connection)enumeration.nextElement();
      try
      {
        connection.close();
        //log("关闭连接池" + name + "中的一个连接");
      }
      catch(SQLException sqlexception){
        //log(sqlexception, "无法关闭连接池" + name + "中的连接");
      }
    }

    freeConnections.removeAllElements();
  }

  private Connection newConnection() throws SQLException
  {
    return user == null ? DriverManager.getConnection(URL) :
                 DriverManager.getConnection(URL, user, password);
  }

  public DBConnectionPool(String driver,
                          String url,
                          String user,
                          String password,
                          int maxConnection) throws ClassNotFoundException
  {
    this.freeConnections = new Vector();
    this.driver = driver;
    Class.forName(driver);
    this.URL = url;
    this.user = user;
    this.password = password;
    this.maxConn = maxConnection;
  }
}
