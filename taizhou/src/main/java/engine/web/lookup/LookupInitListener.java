package engine.web.lookup;

import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;
/**
 * <p>Title: Lookup对象初始化监听器</p>
 * <p>Description: 处理Lookup对象的where子句的参数和过滤条件的参数</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */
public interface LookupInitListener extends Serializable
{

  /**
   * 处理Lookup对象的where子句的参数
   * @param whereParams where子句的参数
   * @param req WEB请求
   */
  public void processWhereParams(LookupParam[] whereParams, HttpServletRequest req);

  /**
   * 处理Lookup对象的过滤条件的参数
   * @param filterParams 过滤条件的参数
   * @param req WEB请求
   */
  public void processFilterParams(LookupParam[] filterParams, HttpServletRequest req);
}