package engine.web.lookup;

import java.util.Map;
import java.util.Hashtable;
import java.util.List;
import java.io.Serializable;
import java.io.File;
//import javax.servlet.jsp.PageContext;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import engine.EngineConfigGlobal;
import engine.util.log.Log;
import engine.util.StringUtils;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

final class LookupHelperPool implements Serializable, EngineConfigGlobal
{
  private static Map hashSql = new Hashtable(100, 1);

  private static Log log = new Log("LookupHelperPool");

  private static boolean isInit = false;

  /**
   * 现在的框架还是比较粗糙的。有空需要整体的考虑各个服务的问题。可参考turbine的Service
   */
  private synchronized static void init(){
    File file = engine.EngineServlet.getLookupConfigFile();
    if(file == null)
      return;

    SAXBuilder builder = new SAXBuilder(false);
    Document doc = null;
    try
    {
      doc = builder.build(file);
    }
    catch (Exception ex)
    {
      log.fatal("parse Lookup config error!", ex);
      //throw new Exception("解析配置文件出错！", ex);
    }
    Element element = doc.getRootElement();
    if(element == null)
      return;
    parseLookupBeans(element);

    parseTableMappings(element);
  }


  private static void parseTableMappings(Element rootElement)
  {
    TableMappingPool.clear();
    List lookupMappingList  = rootElement.getChildren(TABLE_LOOKUP_MAPINGS);
    for(int i=0; lookupMappingList!=null && i<lookupMappingList.size(); i++)
    {
      Element lookupMapping = (Element)lookupMappingList.get(i);
      List mappingList = lookupMapping.getChildren(MAPPING);
      for(int j=0; j<mappingList.size(); j++)
      {
        Element mapping = (Element)mappingList.get(j);
        String table = mapping.getAttributeValue(MAPPING_TABLE);
        String lookup = mapping.getAttributeValue(MAPPING_LOOKUP);
        if(table !=null && lookup != null && table.length() > 0 && lookup.length() > 0)
          TableMappingPool.put(table, lookup);
      }
    }
  }

  private static void parseLookupBeans(Element rootElement)
  {
    hashSql.clear();
    LookupPool.clear();
    List lookupbeansList  = rootElement.getChildren(LOOKUP_BEANS);
    for(int i=0; lookupbeansList!=null && i<lookupbeansList.size(); i++)
    {
      Element lookupbeans = (Element)lookupbeansList.get(i);
      List lookupbeanList = lookupbeans.getChildren(LOOKUP_BEAN);
      for(int j=0; lookupbeanList!=null && j<lookupbeanList.size(); j++)
      {
        Element lookupbean = (Element)lookupbeanList.get(j);
        LookupHelper helper = putLookupHelper(lookupbean);
        if(helper == null)
          return;
        List whereList = lookupbean.getChildren(WHERE);
        if(whereList!=null)
        {
          LookupParam[] whereParams = new LookupParam[whereList.size()];
          for(int k=0;  k<whereList.size(); k++){
            Element whereElement = (Element)whereList.get(k);
            String key = whereElement.getAttributeValue(WHERE_KEY);
            String valuesStr = whereElement.getAttributeValue(WHERE_VALUES);
            String[] values = valuesStr!=null && valuesStr.length()>0 ? StringUtils.parseString(valuesStr, ",") : null;
            whereParams[k] = new LookupParam(key, values);
          }
          helper.setWheres(whereParams);
        }
        List filterList = lookupbean.getChildren(FILTER);
        if(filterList!=null)
        {
          LookupParam[] filterParams = new LookupParam[filterList.size()];
          for(int k=0;  k<filterList.size(); k++){
            Element filterElement = (Element)filterList.get(k);
            String key = filterElement.getAttributeValue(FILTER_KEY);
            String valuesStr = filterElement.getAttributeValue(FILTER_VALUES);
            String[] values = valuesStr!=null && valuesStr.length()>0 ? StringUtils.parseString(valuesStr, ",") : null;
            filterParams[k] = new LookupParam(key, values);
          }
          helper.setFilters(filterParams);
        }
        List lookupListenerList = lookupbean.getChildren(LISTENER);
        for(int k=0; lookupListenerList!=null && k<lookupListenerList.size(); k++)
        {
          Element lookupListener = (Element)lookupListenerList.get(k);
          if(LISTENER_TYPE_INIT.equals(lookupListener.getAttributeValue(LISTENER_TYPE)))
          {
            helper.setInitListenerClass(lookupListener.getAttributeValue(LISTENER_CLASSNAME));
            break;
          }
        }
      }
    }
  }

  /**
   * 根据xml解析的元素对象生成LookupHelper对象
   * @param element LookupHelper对象对应的Element元素
   * @return 返回生成LookupHelper对象
   */
  private static LookupHelper putLookupHelper(Element element)
  {
    String name = element.getAttributeValue(LOOKUP_NAME);
    if(name==null || name.length() == 0)
      return null;
    String scope     = element.getAttributeValue(LOOKUP_SCOPE);
    String statement = element.getAttributeValue(LOOKUP_STATEMENT);
    String orderbys  = element.getAttributeValue(LOOKUP_ORDERBYS);
    String keys      = element.getAttributeValue(LOOKUP_KEYS);
    String regkeys   = element.getAttributeValue(LOOKUP_REGKEYS);
    String captions  = element.getAttributeValue(LOOKUP_CAPTIONS);
    String treefield = element.getAttributeValue(LOOKUP_TREEFIELD);
    String type      = element.getAttributeValue(LOOKUP_TYPE);

    String[] keyArray = keys!=null && keys.length()>0 ? StringUtils.parseString(keys, ",") : null;
    String[] regkeyArray = regkeys!=null && regkeys.length()>0 ? StringUtils.parseString(regkeys, ",") : null;
    String[] capArray = captions!=null && captions.length()>0 ? StringUtils.parseString(captions, ",") : null;
    LookupHelper helper = new LookupHelper(statement, orderbys, keyArray, regkeyArray, capArray,
        LOOKUP_SCOPE_APPLICATION.equalsIgnoreCase(scope),
        LOOKUP_TYPE_LIST.equalsIgnoreCase(type), treefield);
    hashSql.put(name, helper);
    return helper;
  }



  /**
   * 在缓冲池中得到LookupHelper对象
   * @return
   */
  public synchronized static LookupHelper getLookupHelper(String name)
  {
    init();
    return (LookupHelper)hashSql.get(name);
  }

  /**
   * LookUp bean 是否是一次性打开的
   * @param beanName name of bean
   * @return true 是
   */
  public synchronized static boolean isOneOpen(String beanName)
  {
    LookupHelper help = getLookupHelper(beanName);
    return help == null ? false : help.isOneOpen();
  }

  public static void main(String args[]){
    init();
  }
}