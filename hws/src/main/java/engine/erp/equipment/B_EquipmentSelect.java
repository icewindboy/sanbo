package engine.erp.equipment;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.*;
import engine.project.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import com.borland.dx.dataset.*;
/**
 * <p>Title: 设备管理--设备履历卡（引设备名称）</p>
 * <p>Description: 设备管理--设备履历卡（引设备名称）</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: </p>
 * @author why
 * @version 1.0
 */
public final class B_EquipmentSelect extends BaseAction implements Operate
{
  private  static   final   String mastersql="select * from sb_recordcard where 1<>1";

  private static final String Quality_SQL="select * from sb_recordcard";
  //private static final String Select_SQL="select * from sb_recordcard";

  /**
   * 建立检验类型列表信息的数据集
   */
  private EngineDataSet dsmaster=new  EngineDataSet();
  private EngineDataSet dsSelect=new  EngineDataSet();
  /**
   * 用于定位数据集
   */
  private EngineRow locateRow = null;
  /**
   * 保存用户输入的信息
   */
  private RowMap rowInfo = new RowMap();
  /**
   * 是否在添加状态
   */
  public  boolean isAdd = true;
  /**
   * 保存修改操作的行记录指针
   */
  private long  editrow = 0;
  /**
   * 点击返回按钮的URL
   */
  public  String retuUrl = null;
  public String curID=null;
  public String equipmentID=null;
  /**
   * 得到检验类型信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回收发单据信息的实例
   */
  public static B_EquipmentSelect getInstance(HttpServletRequest request)
  {
    B_EquipmentSelect b_EquipmentSelectBean = null;
    HttpSession session = request.getSession(true);
    synchronized(session)
    {
      String beanName = "b_EquipmentSelectBean";
      b_EquipmentSelectBean = (B_EquipmentSelect)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_EquipmentSelectBean == null)
      {
        b_EquipmentSelectBean = new B_EquipmentSelect();
        session.setAttribute(beanName, b_EquipmentSelectBean);
      }
    }
    return b_EquipmentSelectBean;
  }
  /**
   * 构造函数
   */
  public B_EquipmentSelect()
  {
    try {
      jbInit();
    }
    catch (Exception ex) {
      log.error("jbInit", ex);
    }
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected void jbInit() throws Exception
  {
    setDataSetProperty(dsmaster, mastersql);
    //添加操作的触发对象
    setDataSetProperty(dsSelect, mastersql);
    addObactioner(String.valueOf(INIT), new B_WorkProcedure_Init());
  }
  //----Implementation of the BaseAction abstract class
  /**
   * JSP调用的函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      String operate = request.getParameter(OPERATE_KEY);
      if(operate != null && operate.trim().length() > 0)
      {
        RunData data = notifyObactioners(operate, request, response, null);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsmaster.isOpen() && dsmaster.changesPending())
        dsmaster.reset();
      if(dsSelect.isOpen() && dsSelect.changesPending())
        dsSelect.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  public String code_name_Change(String code,String name,String operate){
    String SelectSQL=null;
    String Selectfield="equipmentid";
    String SelectColumn="";
    String SelectColumnName="";
    if(operate.equals("53")){
      SelectColumn=code;
      SelectColumnName="equipment_code";
    }
    if(operate.equals("54")){
      SelectColumn=name;
      SelectColumnName="equipment_name";
    }
    SelectSQL=Quality_SQL +" where "+SelectColumnName+" like '%"+SelectColumn+"%' order by equipment_code";
    dsSelect.setQueryString(SelectSQL);
    openSelectTable();
    dsSelect.setRowMax(null);
    if(dsSelect.getRowCount()>1){
      return "more";
    }
    if(dsSelect.getRowCount()<1) return "none";
    return dsSelect.getValue("equipmentid");
  }
  //----Implementation of the BaseAction abstract class
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsmaster != null){
      dsmaster.close();
      dsmaster = null;
    }
    if(dsSelect != null){
      dsSelect.close();
      dsSelect = null;
    }
    log = null;
    rowInfo = null;
    locateRow = null;
  }
  //----Implementation of the BaseAction abstract class
  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
  {
    return getClass();
  }
  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
    if(!isAdd)
      rowInfo.put(getOneTable());
  }

  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsmaster.isOpen())
      dsmaster.open();
    return dsmaster;
  }
    /*得到选择表对象*/
  public final EngineDataSet getSelectTable()
  {
    if(!dsSelect.isOpen())
      dsSelect.open();
    return dsSelect;
  }
      /*打开清理或报废记录表*/
  public final void openSelectTable()
  {
    //dsSelect.setQueryString(SQL);
    if(!dsSelect.isOpen())
      dsSelect.open();
    else
      dsSelect.refresh();
  }
  /*得到一列的信息*/
  public final RowMap getRowinfo() {return rowInfo;  }
  /**
   * 初始化操作的触发类
   */
  class B_WorkProcedure_Init implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发初始化操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      try{
        String SQL=null;
        String code=data.getParameter("code");
        String name=data.getParameter("name");
        if(!code.equals("")&&!code.equals("null")&&code!=null)
          SQL=Quality_SQL+" where equipment_code like '%"+code+"%' order by equipment_code";
        else if(!name.equals("")&&!name.equals("null")&&name!=null)
          SQL=Quality_SQL+" where equipment_name like '%"+name+"%' order by equipment_code";
        else SQL=Quality_SQL;
        dsmaster.setQueryString(SQL);
        dsmaster.setRowMax(null);
        retuUrl = data.getParameter("src");
        retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      }
      catch(Exception e1){
      }
    }
  }
}
