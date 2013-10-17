package engine.dataset.database;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
/**
 * <p>Title: 数据库接口</p>
 * <p>Description: 数据库接口：与数据库连接有关的接口</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public interface Database extends Serializable
{
  /**
   * 回滚数据
   */
  public void rollback();

  /**
   * 设置类里的数据库组件提交到数据库
   */
  public void commit();

  /**
   * 设置类里的数据库组件是否自动提交
   * @param isAutoCommit  is true执行完SQL就提交；is false需要Commit才能提交，rollback就可以回滚
   */
  public void setAutoCommit(boolean isAutoCommit);

  /**
   * 得到类里的数据库组件是否自动提交
   * @return 返回类里的数据库是否自动提交
   */
  public boolean getAutoCommit();

  /**
   * 得到数据库的连接实例
   * @return 返回数据库的连接实例
   */
  public Connection getConnection();

  /**
   * 设置数据库的连接实例
   * @param connDescriptor 数据库的连接描述
   */
  public void setConnection(Connection conn);

  /**
   * 得到运行的数据库实例信息
   * @return 返回运行的数据库实例信息
   */
  public RuntimeMetaData getRuntimeMetaData();

  /**
   * 设置运行的数据库实例信息
   * @param runtimeMetaData 运行的数据库实例信息
   *
  public void setRuntimeMetaData(RuntimeMetaData runtimeMetaData);

  /**
   * 得到数据库信息
   * @return 返回数据库信息
   */
  public DatabaseMetaData getMetaData();

  /**
   * 关闭数据库的连接
   */
  public void close();
}