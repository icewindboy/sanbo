package engine.dataset;

import java.io.Serializable;

/**
 * Title:        自定义的Beans
 * Description:  1.客户端TableDatSet的Provide和Resolve
 * 2.服务端更新提交的数据到数据库
 * Copyright:    Copyright (c) 2002
 * Company:      JAC
 * @author 江海岛
 * @version 1.0
 */

public class Res extends java.util.ListResourceBundle implements Serializable {
  static final Object[][] contents = {
     {"RS_CantFindProvide", "Can't Find the Provide Data Method at SesionBean!"},
     {"RS_CantFindResolve", "Can't Find the Resolve Data Method at SesionBean!"},
     {"RS_NoDataSource", "None DataSource! Must set a valid DataBaseConnection!"},
     {"RS_SQLisNone", "The SQL Text Is None!"},
     {"RS_MethodIsNone", "Remote Or Local Method Doesnot Set!"},
     {"RS_SeverIsNone", "The SeesionBeanConnection Doesnot Set!"},
     {"RS_CantInvoke", "Couldn't Invoke Remote Server Mothod!"},
     {"RS_ProviderErr", "Provider 必须是 EngineDataSetProvider！"},
     {"RS_ResolverErr", "Resolver 必须是 EngineDataSetResolver！"},
     {"RS_NoneResolver", "没有设置Resolver！"},
     {"RS_ParamLength", "更新的数据集的数量与其相对应的SQL不相等"},
     {"RS_NotEngineDataSet", "更新的数据集数组至少有一个不是EngineDataSet的实例"},
  };

  public Object[][] getContents() {
    return contents;
  }
}