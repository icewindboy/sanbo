package engine.web.lookup;

import java.util.*;
import javax.servlet.http.*;
import engine.web.taglib.Select;
import engine.dataset.*;
import engine.util.log.Log;
import engine.util.StringUtils;
import engine.util.EngineRuntimeException;

//import com.borland.dx.dataset.DataSet;
//import com.borland.dx.dataset.Locate;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author hukn
 * @version 1.0
 */

public final class LookupFacade implements java.io.Serializable, HttpSessionBindingListener
{
  private final static String LookupFacadeKey = "engine.web.lookup.LookupFacade";

  private Log log = new Log(this.getClass());

  private Hashtable lookupTable = new Hashtable();

  private EngineDataSetProvider provider = new EngineDataSetProvider();
  /**
   * 构造函数
   */
  private LookupFacade() {
  }

  /**
   * 得到基础信息的实例，用于查询一行信息，得到仓库,库位等信息
   * @param request web请求
   * @param beanName baen的名称，各个bean的名称见类engine.project.SysConstant(如仓库,入参为SysConstant.BEAN_STORE)
   * @return 返回对象的引用
   */
  public synchronized static Lookup getInstance(HttpServletRequest request, String beanName)
  {
    //设置SQL语句的缓存
    LookupHelper help = LookupHelperPool.getLookupHelper(beanName);
    if(help == null)
      throw new LookupNotFoundException("not found the lookup config info in config file!");

    Lookup lookupBean = null;
    if(help.isOneOpen())
    {
      lookupBean = (Lookup)LookupPool.getLookup(beanName);
      if(lookupBean == null)
      {
        lookupBean = createLookup(beanName, help, new EngineDataSetProvider(), request);
        LookupPool.put(beanName, lookupBean);
      }
    }
    else
    {
      HttpSession session = request.getSession(true);
      LookupFacade lookupFacade = (LookupFacade)session.getAttribute(LookupFacadeKey);
      if(lookupFacade == null)
      {
        lookupFacade = new LookupFacade();
        session.setAttribute(LookupFacadeKey, lookupFacade);
      }
      Map lookupTable = lookupFacade.lookupTable;
      lookupBean = (Lookup)lookupTable.get(beanName);
      if(lookupBean == null)
      {
        lookupBean = createLookup(beanName, help, lookupFacade.provider, request);
        lookupTable.put(beanName, lookupBean);
      }
    }
    return lookupBean;
  }


  private static Lookup createLookup(String beanName, LookupHelper help,
                                     EngineDataSetProvider provider, HttpServletRequest request)
  {
    EngineDataSet ds = new EngineDataSet();
    ds.setProvider(provider);
    Lookup lookupBean = new LookupImpl();
    lookupBean.init(help, ds);
    LookupParam[] whereParams = help.getWheres();
    LookupParam[] filterParams = help.getFilters();
    if(whereParams != null || filterParams != null)
    {
      LookupInitListener initListener = help.getInitListener();
      if(whereParams != null)
        initListener.processWhereParams(whereParams, request);
      if(filterParams != null)
        initListener.processFilterParams(filterParams, request);
    }

    return lookupBean;
  }

  /**
   * 绑定bean实例到session中
   * @param event session绑定事件
   */
  public void valueBound(HttpSessionBindingEvent event) {
  }

  /**
   * 将bean实例在session中解除绑定
   * @param event session绑定事件
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(lookupTable!=null && lookupTable.size() > 0)
    {
      Iterator iterator = lookupTable.values().iterator();
      while(iterator.hasNext()){
        Lookup lookup = (Lookup)iterator.next();
        lookup.release();
      }
      lookupTable.clear();
    }
    lookupTable = null;
    this.provider = null;
  }
}