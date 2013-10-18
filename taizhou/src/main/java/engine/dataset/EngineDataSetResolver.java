package engine.dataset;

import java.lang.reflect.*;
import java.util.*;
import java.math.BigDecimal;
import javax.ejb.*;

import engine.dataset.database.*;
import engine.util.log.Log;

import com.borland.dx.dataset.*;
/**
 * Title:        自定义的Beans
 * Description:  1.客户端TableDatSet的Provide和Resolve
 * 2.服务端更新提交的数据到数据库
 * Copyright:    Copyright (c) 2002
 * Company:      JAC
 * @author 江海岛
 * @version 1.0
 */
public class EngineDataSetResolver extends Resolver
{
  private Log log = new Log(getClass());
  private transient ResourceBundle res = Res.getBundle("engine.dataset.Res");
  private String methodName = null;
  private EJBObject sessionBeanRemote;

  public EngineDataSetResolver()
  {
  }

  /**
   * resolveData
   * @param dataSet 提交更新的数据集
   * @throws DataSetException 抛出异常
   */
  public void resolveData(DataSet dataSet) throws DataSetException
  {
    String resolveMethodName = null;
    EngineDataSet ds = null;
    if(dataSet instanceof EngineDataSet)
    {
      ds = ((EngineDataSet)dataSet);
      resolveMethodName = ds.getResolveMethodName();
    }
    else
      return;

    try {
      ProviderHelp.startResolution(dataSet.getStorageDataSet(), true);
      saveDataSets(new EngineDataSet[]{ds}, resolveMethodName);
    }
    finally{
      ProviderHelp.endResolution(dataSet.getStorageDataSet());
    }
  }

  /**
   * 数据集提交以后的处理
   * @param resolvedDataSets 提交后的数据集
   * @param resolvedInfo 提交后，服务段返回的信息
   * @throws Exception 异常
   */
  private void afterResolve(DataSet[] resolvedDataSets, ResolveInfo[] resolvedInfo)
  {
    if(resolvedInfo == null)
      return;
    StorageDataSet dataSet    = null;

    handleSequences(resolvedDataSets, resolvedInfo);

    for(int i=0; i<resolvedDataSets.length; i++)
    {
      dataSet = resolvedDataSets[i].getStorageDataSet();

      String tableName = resolvedInfo[i].getTableName();
      String sechmaName = resolvedInfo[i].getSchemaName();
      if(tableName != null)
        dataSet.setTableName(tableName);
      if(sechmaName != null)
        dataSet.setSchemaName(sechmaName);
      //
      if(dataSet instanceof EngineDataSet)
        ((EngineDataSet)dataSet).resolveInfo = resolvedInfo[i];
      else
      {
        List rowIds = resolvedInfo[i].getRowIds();
        for(int j=0; rowIds!= null && j<rowIds.size(); j++)
        {
          String rowid = (String)rowIds.get(j);
          if(rowid != null)
            dataSet.setRowId(rowid, true);
        }
      }
    }
  }

  /**
   * 运用递归的方法得到相应的参数。按主从从表顺序添加数据集
   * @param masterDataSet 主表的数据集
   * @param dsVector 保存主从从表顺序的各个数据集
   * @param masterLink 保存相应dsVector中的数据集的连接主表的列名
   */
  private void getAllDataSet(DataSet masterDataSet, Vector dsVector, Vector masterLink/*, int masterNum*/)
  {
    DataSet[] dsTempArray = masterDataSet.getDetails();
    dsVector.addElement(masterDataSet);
    HashMap hm = new HashMap();
    if(masterDataSet.getMasterLink()!=null/*masterNum != -1*/)
    {
      ;
    }
    masterLink.addElement(hm);

    if(dsTempArray != null)
    {
      for(int i=0; i<dsTempArray.length; i++)
        getAllDataSet(dsTempArray[i], dsVector, masterLink);
    }
  }

  /**
   * 提交主表，同时提交其下的各个从表, 并同时执行没有返回值的SQL语句
   * @param masterEngineDataSet 主表数据集
   * @throws DataSetException 抛出异常
   */
  public void saveDataSets(EngineDataSet masterEngineDataSet) throws DataSetException
  {
    if(masterEngineDataSet == null)
      throw new DataSetException("masterEngineDataSet must not null!");

    Vector dsVector = new Vector();
    Vector masterLinkVector = new Vector();
    this.getAllDataSet(masterEngineDataSet, dsVector, masterLinkVector);

    DataSet[] dsArray = new DataSet[dsVector.size()];
    dsVector.copyInto(dsArray);
    HashMap[] hmRelation = new HashMap[masterLinkVector.size()];
    masterLinkVector.copyInto(hmRelation);

    for(int i=0;i<dsArray.length;i++)
    {
      DataSet[] dsTempArray = dsArray[i].getDetails();
      if(dsTempArray != null)
      {
        int[] detailNum = new int[dsTempArray.length];
        String[] masterColumns = new String[dsTempArray.length];
        String[] detailColumns = new String[dsTempArray.length];
        for(int j=0; j<dsTempArray.length; j++)
        {
          for(int k=0; k<dsArray.length; k++)
          {
            boolean isEquals= false;
            try{
              isEquals = dsTempArray[j].equals(dsArray[k]);
            }
            catch(DataSetException ex){
              isEquals = false;
            }
            if(isEquals)
            {
              detailNum[j] = k;
              masterColumns[j] = dsArray[k].getMasterLink().getMasterLinkColumns()[0];
              detailColumns[j] = dsArray[k].getMasterLink().getDetailLinkColumns()[0];
              break;
            }
          }
        }
      }
    }

    String[] SQLArrray = new String[dsArray.length];
  }

  /**
   * 同时更新多个数据集，从表必须跟在主表后面，并同时执行没有返回值的SQL语句
   * @param clientDataSets 需要更新的数据集
   * @param remoteMethod 远程方法的名称，如果不用ejb，可以为null
   * @throws DataSetException 抛出异常
   */
  public void saveDataSets(EngineDataSet[] clientDataSets, String remoteMethod)
      throws DataSetException
  {
    ResolveInfo[] infos = new ResolveInfo[clientDataSets.length];
    for(int i =0; i<infos.length; i++)
    {
      infos[i] = clientDataSets[i].resolveInfo;
      infos[i].setQueryString(clientDataSets[i].getQueryString());
    }

    saveDataSets(clientDataSets, infos, remoteMethod);
  }

  /**
   * 提交更新过的数据, 从表必须跟在主表后面
   * @param dataSets 更新过的数据集
   * @param infos 提交数据的相关信息
   * @param remoteMethod 要执行的远程方法
   * @throws DataSetException 抛出异常
   */
  public void saveDataSets(StorageDataSet[] dataSets, ResolveInfo[] infos, String remoteMethod) throws DataSetException
  {
    if(dataSets.length != infos.length)
      throw new engine.util.EngineRuntimeException("length of DataSet is not euqals length of ProvideInfo array");

    if (sessionBeanRemote != null && remoteMethod == null)
      throw new DataSetException("EngineDataSetResolver [saveDataSets]:"+ res.getString("RS_MethodIsNone"));

    try {
      for(int i=0; i<dataSets.length; i++)
      {
        if(infos[i].isSaveDataUseSelf())
          infos[i].setChangedDataSet(dataSets[i]);
        else
          infos[i].setChangedData(DataSetData.extractDataSetChanges(dataSets[i]));
      }

      ResolveInfo[] retuInfos = resolveChanges(infos, remoteMethod);

      afterResolve(dataSets, retuInfos);

      for(int i=0; i<dataSets.length; i++)
        dataSets[i].resetPendingStatus(true);
    }
    catch (DataSetException ex)
    {
      for(int i=0; i<dataSets.length; i++)
        dataSets[i].resetPendingStatus(false);

      throw ex;
    }
    catch (Exception ex)
    {
      for(int i=0; i<dataSets.length; i++)
        dataSets[i].resetPendingStatus(false);

      String error = ex.getMessage();
      if(error == null)
        error = "EngineDataSetResolver [resolveData]:" + res.getString("RS_CantInvoke");
      DataSetException.throwExceptionChain(ex);
      throw new DataSetException(error);
    }
    finally{
      for(int i=0; infos!=null && i<infos.length; i++)
      {
        if(infos[i]!=null)
          infos[i].clearTemp();
      }
    }
  }

  /**
   * 提交更改过的数据
   * @param changeInfos 更新过的数据的信息
   * @return 返回的有关信息.
   * @throws DataSetException
   */
  public ResolveInfo[] saveDataSetDatas(ResolveInfo[] changeInfos) throws Exception
  {
    return resolveChanges(changeInfos, getMethodName());
  }

  /**
   * 提交更新过的数据
   * @param changeInfos 更新过的数据的信息
   * sequences:该的sequence名称.
   * @param remoteMethod 要执行的远程方法
   * @return 返回的有关信息. errordatasets: 错误的DataSetData数组(用于单表的提交,多表是null)
   * @throws DataSetException 抛出DataSet异常
   * @throws Exception 抛出异常
   */
  private ResolveInfo[] resolveChanges(ResolveInfo[] changeInfos, String remoteMethod)
    throws DataSetException, Exception
  {
    if (sessionBeanRemote != null && remoteMethod == null)
      throw new DataSetException("EngineDataSetResolver [resolveChanges]:"+ res.getString("RS_MethodIsNone"));

    ResolveInfo[] returnInfo = null;
    try
    {
      //sessionBeanRemote = sessionBeanConn.getSessionBeanRemote();
      if(sessionBeanRemote == null)
      {
        returnInfo = DatabaseHelper.resovleData(changeInfos);
      }
      else
      {
        Method resolveMethod = null;
        Method[] methodArray = sessionBeanRemote.getClass().getMethods();
        for(int i = 0; i < methodArray.length; i++)
        {
          Method method = methodArray[i];
          if(method.getName().equals(remoteMethod) && method.getParameterTypes().length == 3)
          {
            resolveMethod = method;
            break;
          }
        }
        if (resolveMethod != null )
          returnInfo = (ResolveInfo[])resolveMethod.invoke(sessionBeanRemote, new Object[]{changeInfos});
        else
          throw new DataSetException("EngineDataSetResolver [resolveData]:"+ res.getString("RS_CantFindResolve"));
      }
    }
    catch (DataSetException ex) {
      throw ex;
    }
    catch (Exception ex) {
      throw ex;
    }
    return returnInfo;
  }

  /**
   * 执行没有返回数据集的SQL
   * @param updateSQL 没有返回数据集的SQL
   * @throws DataSetException 抛出DataSet异常
   */
  public void updateQuery(String[] updateSQL) throws DataSetException
  {
    try
    {
      if(sessionBeanRemote == null)
        DatabaseHelper.ExecuteSQL(updateSQL);
    }
    catch (DataSetException ex) {
      throw ex;
    }
    catch (Exception ex) {
      //ex.printStackTrace();
      throw new DataSetException(ex.getMessage());
    }
  }

  /**
   * 处理提交后的序列的回填
   * @param dataSet 提交更新的数据集
   * @param sequence  提交完毕后返回的序列记录的DataSetData
   */
  private void handleSequences(DataSet[] dataSet, ResolveInfo[] resolvedInfo)
  {
    TableDataSet[] keyDataSets = new TableDataSet[dataSet.length];
    DataSetView[] dsvDataSets = new DataSetView[dataSet.length];
    for(int num=0; num<dataSet.length; num++)
    {
      DataSetData sequence = resolvedInfo[num].isSaveDataUseSelf() ? null :
                resolvedInfo[num].getResolveData();
      if(sequence!=null)
      {
        keyDataSets[num] = new TableDataSet();
        sequence.loadDataSet(keyDataSets[num]);
        dsvDataSets[num] = dataSet[num].cloneDataSetView();
      }
    }
    //
    for(int num=0; num<dataSet.length; num++)
    {
      if(keyDataSets[num]==null)
        continue;

      keyDataSets[num].first();
      for(int i=0;i<keyDataSets[num].getRowCount();i++)
      {
        dsvDataSets[num].goToInternalRow(keyDataSets[num].getLong("INTERNALROW"));
        for(int j=0;j<keyDataSets[num].getColumnCount()-1;j++)
        {
          String changedValue = keyDataSets[num].getString(j);
          if(changedValue == null || changedValue.equals(""))
            continue;
          String columnName = keyDataSets[num].getColumn(j).getColumnName();
          int dataType = dsvDataSets[num].getColumn(columnName).getDataType();
          if(dataType == Variant.BIGDECIMAL)
            dsvDataSets[num].setBigDecimal(columnName, new BigDecimal(changedValue));
          else if(dataType == Variant.STRING)
            dsvDataSets[num].setString(columnName, changedValue);

          dsvDataSets[num].post();
        }
        keyDataSets[num].next();
      }
    }
  }


  /**
   * 处理错误的数据集
   * @param dataSet 提交更新的数据集
   * @param errors  提交完毕后的出错记录的DataSetData
   * @throws DataSetException 抛出异常
   */
  private void handleErrors(DataSet dataSet, DataSetData errors)
    throws DataSetException
  {
    TableDataSet errorDataSet = new TableDataSet();
    errors.loadDataSet(errorDataSet);
    long internalRow;
    errorDataSet.first();
    do {
      internalRow = errorDataSet.getLong(0);
      dataSet.resetPendingStatus(internalRow,false);
    } while (errorDataSet.next());

    dataSet.resetPendingStatus(true);

    errorDataSet.first();
    internalRow = errorDataSet.getLong(0);
    dataSet.goToInternalRow(internalRow);
    DataSetException ex = (DataSetException)errorDataSet.getObject(1);
    throw ex;
  }
  /**
   * 得到SessionBean的RemoteInterface的远程方法名称
   * @return 返回SessionBean的RemoteInterface的远程方法名称
   */
  public String getMethodName()
  {
    return methodName;
  }
  /**
   * 设置SessionBean的RemoteInterface的远程方法名称
   * @param methodName SessionBean的RemoteInterface的远程方法名称
   */
  public void setMethodName(String methodName)
  {
    this.methodName = methodName;
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
}