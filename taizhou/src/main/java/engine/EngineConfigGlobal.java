package engine;

/**
 * <p>Title: engine配置文件常量</p>
 * <p>Description: engine配置文件常量</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public interface EngineConfigGlobal
{
  public static final String ENGINE_CONFIG = "engine-config";

  public static final String LOOKUP_BEANS = "lookup-beans";

  public static final String LOOKUP_BEAN = "lookup-bean";

  //---------------------------------------------------------
  //--lookup-bean 元素
  //---------------------------------------------------------
  public final static String LOOKUP_NAME = "name";

  public final static String LOOKUP_SCOPE = "scope";

  public final static String LOOKUP_SCOPE_APPLICATION = "application";

  public final static String LOOKUP_SCOPE_SESSION = "session";

  public final static String LOOKUP_SCOPE_PAGE = "page";

  public final static String LOOKUP_SCOPE_REQUEST = "request";

  public final static String LOOKUP_STATEMENT = "statement";

  public final static String LOOKUP_ORDERBYS = "orderbys";

  public final static String LOOKUP_KEYS = "keys";

  public final static String LOOKUP_REGKEYS = "regkeys";

  public final static String LOOKUP_CAPTIONS = "captions";

  public final static String LOOKUP_TREEFIELD = "treefield";

  public final static String LOOKUP_TYPE = "type";

  public final static String LOOKUP_TYPE_LIST = "list"; //LOOKUP_TYPE的其中一个值

  //lookup-bean的子元素: where子句元素
  public final static String WHERE = "where";
  //过滤元素和where子句元素的属性
  public final static String WHERE_KEY = "key";

  public final static String WHERE_VALUES = "values";

  //lookup-bean的子元素: 过滤元素
  public final static String FILTER = "filter";
  //过滤元素和where子句元素的属性
  public final static String FILTER_KEY = "key";

  public final static String FILTER_VALUES = "values";

  //lookup-bean的子元素: 监听器元素
  public static final String LISTENER = "listener";

  public final static String LISTENER_TYPE = "type";

  public final static String LISTENER_TYPE_INIT = "init"; //type 其中一个值。初始化监听器
  //监听器的类名
  public final static String LISTENER_CLASSNAME = "className";
  //---------------------------------------------------------
  //--table-lookup-mappings 元素。表更新触发lookupbean刷新
  //---------------------------------------------------------
  public final static String TABLE_LOOKUP_MAPINGS = "table-lookup-mappings";

  public final static String MAPPING = "mapping";

  //lookup的名称属性
  public final static String MAPPING_TABLE = "table";

  public final static String MAPPING_LOOKUP = "lookup";

}