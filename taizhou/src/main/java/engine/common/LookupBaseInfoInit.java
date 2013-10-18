package engine.common;

import engine.web.lookup.LookupParam;
import javax.servlet.http.HttpServletRequest;
import engine.web.lookup.LookupInitListener;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author 江海岛
 * @version 1.0
 */

public class LookupBaseInfoInit implements LookupInitListener {

  /**
   * 处理Lookup对象的where子句的参数
   * @param whereParams where子句的参数
   * @param req WEB请求
   */
  public void processWhereParams(LookupParam[] whereParams, HttpServletRequest req) {
    processParams(whereParams, req);
  }

  /**
   * 处理Lookup对象的过滤条件的参数
   * @param filterParams 过滤条件的参数
   * @param req WEB请求
   */
  public void processFilterParams(LookupParam[] filterParams, HttpServletRequest req) {
    processParams(filterParams, req);
  }

  private void processParams(LookupParam[] params, HttpServletRequest req){
    //得到登陆Bean
    LoginBean login = LoginBean.getInstance(req);
    for(int i=0; i<params.length; i++){
      String key = params[i].getKey();
      if("fgsid".equalsIgnoreCase(key))
        params[i].setValues(new String[]{login.getFirstDeptID()});
      else if("deptid".equalsIgnoreCase(key))
        params[i].setValues(login.getUser().getHandleDepts());
      else if("storeid".equalsIgnoreCase(key))
        params[i].setValues(login.getUser().getHandleStores());
    }
  }
}