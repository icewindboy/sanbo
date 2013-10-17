package engine.report.event;

import java.io.Serializable;
import java.util.Map;
import javax.servlet.ServletRequest;

/**
 * <p>Title: 报表装载时得到替换模板特殊字符串的属性信息监听器</p>
 * <p>Description: 报表装载时得到替换模板特殊字符串的属性信息监听器</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: engine</p>
 * @author hukn
 * @version 1.0
 */

public interface TempletLoadListener extends Serializable
{

  /**
   * 得到初始化的用于替换模板特殊字符串的属性信息。key:被替换的字符串, value：替换的字符串
   * @param reqest ServletRequest实例
   * @return 返回用于替换模板特殊字符串的属性信息。
   */
  public Map getInitConstant(ServletRequest reqest);
}