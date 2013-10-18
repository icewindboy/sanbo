package engine.dataset;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javax.ejb.EJBObject;

import com.borland.dx.dataset.DataSetData;
import com.borland.dx.dataset.DataSetException;
import com.borland.dx.dataset.Provider;
import com.borland.dx.dataset.StorageDataSet;

import engine.dataset.database.DatabaseHelper;
import engine.util.EngineRuntimeException;
/**
 * Title:        自定义的Beans
 * Description:  1.客户端TableDatSet的Provide和Resolve
 * 2.服务端更新提交的数据到数据库
 * Copyright:    Copyright (c) 2002
 * Company:      JAC
 * @author 江海岛
 * @version 1.0
 */

public class EngineDataSetProvider extends Provider {
  private transient ResourceBundle res = Res.getBundle("engine.dataset.Res");
  private EJBObject sessionBeanRemote;
  private String methodName;

  public EngineDataSetProvider()
  {
  }

  /**
   * provideData
   * @param dataSet StorageDataSet接收到的数据.
   * @throws DataSetException 抛出异常
   */
  public void provideData(StorageDataSet dataSet, boolean toOpen) throws DataSetException {
    try {
      if(dataSet instanceof EngineDataSet)
      {
        EngineDataSet ds  = (EngineDataSet)dataSet;
        openDataSets(new EngineDataSet[]{ds}, ds.getProvideMethodName());
      }
    }
    catch (DataSetException ex) {
      throw ex;
    }
    catch (SQLException ex){
      DataSetException.SQLException(ex);
    }
    catch (Exception ex) {
      throw new EngineRuntimeException(ex);
    }
  }

  /**
   * 根据条件打开特定（EngineDataSet）各个数据集
   * @param clientdataSets 想应的数据集
   */
  public void openDataSets(EngineDataSet[] clientdataSets) throws Exception
  {
    String remoteMethod = null;
    if(clientdataSets != null && clientdataSets.length > 0)
      remoteMethod = clientdataSets[0].getProvideMethodName();
    openDataSets(clientdataSets, remoteMethod);
  }
  /**
   * 根据条件打开特定（EngineDataSet）各个数据集
   * @param clientdataSets 想应的数据集
   * @param remoteMethod 远程方法的名称，如果不用ejb，可以为null
   */
  public void openDataSets(EngineDataSet[] clientdataSets, String remoteMethod) throws Exception
  {
    String[] SQLArrray = new String [clientdataSets.length];

    ProvideInfo[] infos = new ProvideInfo[clientdataSets.length];
    for(int i=0; i<clientdataSets.length; i++)
      infos[i] = clientdataSets[i].provideInfo;

    openDataSets(clientdataSets, infos, remoteMethod);
  }
  /**
   * 根据条件打开各个数据集
   * @param dataSets 想应的数据集
   * @param SQL 提取数据的SQL语句数组
   * @param remoteMethod 远程方法的名称，如果不用ejb，可以为null
   * @throws DataSetException 抛出异常
   */
  public void openDataSets(StorageDataSet[] dataSets, String[] SQL, String remoteMethod) throws Exception
  {
    ProvideInfo[] infos = new ProvideInfo[SQL.length];
    for(int i=0; i<SQL.length; i++)
    {
      infos[i] = new ProvideInfo();
      infos[i].setQueryString(SQL[i]);
    }
    openDataSets(dataSets, infos, remoteMethod);
  }

  /**
   * 根据条件打开各个数据集
   * @param dataSets 想应的数据集
   * @param SQL 提取数据的SQL语句数组
   * @param remoteMethod 远程方法的名称，如果不用ejb，可以为null
   * @param hmRelation 各种需要传递的参数数组
   * @throws 抛出异常
   */
  public void openDataSets(StorageDataSet[] dataSets, ProvideInfo[] infos, String remoteMethod) throws Exception
  {
    if(dataSets.length != infos.length)
      throw new engine.util.EngineRuntimeException("length of DataSet is not euqals length of ProvideInfo array");

    try{
      for(int i=0; i<infos.length; i++)
      {
        if(infos[i].isLoadDataUseSelf())
          infos[i].setProvideDataSet(dataSets[i]);
      }

      ProvideInfo[] datas = getServerData(infos, remoteMethod);

      for(int i=0; i<infos.length; i++)
      {
        if(!infos[i].isLoadDataUseSelf()) {
          dataSets[i].empty();
          if(datas[i].getProvideData() != null)
            datas[i].getProvideData().loadDataSet(dataSets[i]);
        }
      }
    }
    catch(Exception ex){
      throw ex;
    }
    finally
    {
      for(int i=0; infos!=null && i<infos.length; i++)
        if(infos[i] != null)
          infos[i].clearTemp();
    }
  }
  /**
   * 从服务端提取数据
   * @param infos 提取数据的信息类（包含SQL语句）数组
   * @return 返回提取到的数据和其他的参数
   * @throws Exception 抛出异常
   */
  public ProvideInfo[] getDataSetData_info(ProvideInfo[] infos) throws Exception
  {
    return getServerData(infos, getMethodName());
  }
  /**
   * 从服务端提取数据
   * @param infos 提取数据的信息类（包含SQL语句）数组
   * @return 返回提取到的数据和其他的参数
   * @throws Exception 抛出异常
   */
  public DataSetData[] getDataSetData(ProvideInfo[] infos) throws Exception
  {
    ProvideInfo[] datas = getServerData(infos, getMethodName());
    DataSetData[] dsds = new DataSetData[datas.length];
    for(int i=0; i<datas.length; i++)
      dsds[i] = datas[i].getProvideData();
    datas = null;
    return dsds;
  }

  /**
   * 设置要提交数据集的一些必须的元素（包括 PrimaryKeys, SchemaName, TableName）
   * @param engineDataSets 需要被设置属性的数据集数组
   * @throws DataSetException SQL异常
   */
  public void processElement(EngineDataSet[] engineDataSets) //throws SQLException
  {
    ResolveInfo[] resolveInfos = new ResolveInfo[engineDataSets.length];
    for(int i=0; i<resolveInfos.length; i++)
    {
      resolveInfos[i] = engineDataSets[i].resolveInfo;
      resolveInfos[i].setTableName(null);
      resolveInfos[i].setSchemaName(null);
      if(resolveInfos[i].getRowIds() != null)
        resolveInfos[i].getRowIds().clear();
      //设置SQL，以便解析SQL语句
      resolveInfos[i].setQueryString(engineDataSets[i].provideInfo.getQueryString());
    }
    //
    resolveInfos = DatabaseHelper.processElement(resolveInfos);
    //
    for(int i=0; i<resolveInfos.length; i++)
    {
      engineDataSets[i].resolveInfo = resolveInfos[i];
      engineDataSets[i].resolveInfo.setQueryString(null);
      List rowids = resolveInfos[i].getRowIds();
      for(int j=0; rowids != null && j<rowids.size(); j++)
      {
        String name = (String)rowids.get(j);
        try{
          engineDataSets[i].setRowId(name, true);
        }
        catch(Exception ex){
          rowids.remove(j);
          j--;
        }
      }
      engineDataSets[i].setTableName(resolveInfos[i].getTableName());
      engineDataSets[i].setSchemaName(resolveInfos[i].getSchemaName());
    }
  }

  /**
   * 从服务端提取数据
   * @param SQLArray 提取数据的SQL语句数组
   * @param remoteMethod 远程方法的名称，如果不用ejb，可以为null
   * @param hmArray 各种需要传递的参数数组
   * @return 返回提取到的数据和其他的参数
   * @throws Exception 抛出异常
   */
  private ProvideInfo[] getServerData(ProvideInfo[] infos, String remoteMethod) throws Exception
  {
    if (sessionBeanRemote != null && remoteMethod == null)
      throw new DataSetException("EngineDataSetProvider [getServerData]:"+ res.getString("RS_MethodIsNone"));

    ProvideInfo[] provideData = null;
    if(sessionBeanRemote == null)
      provideData = DatabaseHelper.provideData(infos);
    else
    {
      Method provideMethod = null;
      Method [] methodArray = sessionBeanRemote.getClass().getMethods();
      for(int i = 0; i < methodArray.length; i++)
      {
        Method method = methodArray[i];
        if(method.getName().equals(remoteMethod) && method.getParameterTypes().length == 2 ) {
          provideMethod = method;
          break;
        }
      }
      if ( provideMethod != null)
        provideData = (ProvideInfo[])provideMethod.invoke(sessionBeanRemote, new Object[]{infos});
      else
        throw new DataSetException(res.getString("RS_CantFindProvide"));
    }
    return provideData;
  }
  /**
   * 提取数据库总实际的总记录数量
   * @param queryString SQL语句
   * @return 返回总记录数量
   * @throws Exception 抛出异常
   */
  public int getTrueCount(String queryString)
  {
    int value = 0;
    if(sessionBeanRemote == null)
      value = DatabaseHelper.getRowCount(queryString);
    else
      ;
    return value;
  }
  /**
   * 提取序列的值
   * @param sequenceName 序列名称
   * @return 返回序列的值
   */
  public String getSequence(String sequenceName)
  {
    String value=null;
    if(sessionBeanRemote == null)
      value = DatabaseHelper.getSequence(sequenceName);
    else
      ;
    return value;
  }
  /**
   * 设置SessionBeanRemote的引用，以便远程调用SessionBean的方法
   * @param sessionBeanRemote SessionBean的远程对象
   */
  public void setSessionBeanRemote(EJBObject sessionBeanRemote)
  {
    this.sessionBeanRemote = sessionBeanRemote;
  }
  /**
   * 得到SessionBeanRemote
   * @return 返回SessionBeanRemote的引用
   */
  public EJBObject getSessionBeanRemote()
  {
    return this.sessionBeanRemote;
  }
  /**
   * 设置提取数据的远程方法的名称
   * @param methodName 远程方法的名称
   */
  public void setMethodName(String methodName)
  {
    this.methodName = methodName;
  }
  /**
   * 得到提取数据的远程方法的名称
   * @return 返回远程方法的名称
   */
  public String getMethodName()
  {
    return methodName;
  }
}