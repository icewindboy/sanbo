package engine.erp.person;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.web.observer.Obactioner;
import engine.common.LoginBean;

import engine.project.SysConstant;
import engine.project.LookupBeanFacade;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;

/**
 * <p>Title: 银行信用卡列表</p>
 * <p>Copyright: right reserved (c) 2003</p>
 * <p>Company: ENGINE</p>
 * <p>Author: 胡康宁</p>
 * @version 1.0
 */

public final class B_CreditCard extends BaseAction implements Operate
{
  private static final String YHXYK_SQL = "SELECT * FROM rl_yhxyk WHERE 1=1 ? order by xykbh";
  private EngineDataSet dsRl_yhxykTable = new EngineDataSet();//数据集
  private RowMap rowInfo = new RowMap();                      //HashTable
  public  boolean isAdd = true;
  private long    editrow = 0;
  public  String retuUrl = null;

  /**
   * 从会话中得到银行信用卡信息的实例
   * @param request jsp请求
   * @return 返回银行信用卡信息的实例
   */

  public static B_CreditCard getInstance(HttpServletRequest request)
  {
    B_CreditCard b_creditcardBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_creditcardBean";
      b_creditcardBean = (B_CreditCard)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_creditcardBean == null)
      {
        b_creditcardBean = new B_CreditCard();
        session.setAttribute(beanName, b_creditcardBean);//加入到session中
      }
    }
    return b_creditcardBean;
  }
  /**
   * 构造函数
   */
  private B_CreditCard()
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
    setDataSetProperty(dsRl_yhxykTable, combineSQL(YHXYK_SQL,"?",new String[]{""}));                        //提取出全部数据
    dsRl_yhxykTable.setSequence(new SequenceDescriptor(new String[]{"xykID"}, new String[]{"s_rl_yhxyk"})); //设置主健的sequence
    //添加操作的触发对象
    B_CreditCard_Add_Edit add_edit = new B_CreditCard_Add_Edit();
    addObactioner(String.valueOf(ADD), add_edit);                  //新增
    addObactioner(String.valueOf(EDIT), add_edit);                 //修改
    addObactioner(String.valueOf(INIT), new B_CreditCard_Init());  //初始化 operate=0
    addObactioner(String.valueOf(POST), new B_CreditCard_Post());  //保存
    addObactioner(String.valueOf(DEL), new B_CreditCard_Delete()); //删除
  }
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
      String operate = request.getParameter("operate");
      if(operate != null && operate.trim().length() > 0)
      {
        RunData data = notifyObactioners(operate, request, response, null);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsRl_yhxykTable.isOpen() && dsRl_yhxykTable.changesPending())
        dsRl_yhxykTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsRl_yhxykTable != null){
      dsRl_yhxykTable.close();
      dsRl_yhxykTable = null;
    }
    log = null;
    rowInfo = null;
  }
  /**
   * 得到子类的类名
   * 实现BaseAction中的抽象方法
   * 日志中调用
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
    else
      {
      //调用存贮过程
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('rl_yhxyk','xykbh','','',3) from dual");
      rowInfo.put("xykbh", code);
      }
  }
  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    if(!dsRl_yhxykTable.isOpen())
      dsRl_yhxykTable.open();
    return dsRl_yhxykTable;
  }
  /**
   *得到表的一行信息
   * */
  public final RowMap getRowinfo() {return rowInfo;}


  //------------------------------------------
  //操作实现的类:初始化;新增,修改,删除
  //------------------------------------------
  /**
   * 初始化操作的触发类
   */
  class B_CreditCard_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
    }
  }
  /**
   * 添加或修改操作的触发类
   */
  class B_CreditCard_Add_Edit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsRl_yhxykTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsRl_yhxykTable.getInternalRow();
      }
      initRowInfo(isAdd, true);
    }
  }
  /**
   * 保存操作的触发类
   */
  class B_CreditCard_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
      rowInfo.put(data.getRequest());
      String xykbh = rowInfo.get("xykbh");
      String xykmc = rowInfo.get("xykmc");
      String xykhcd = rowInfo.get("xykhcd");
      if(xykbh.equals("")){
        data.setMessage(showJavaScript("alert('信用卡编号不能为空！');"));
        return;
      }
      if(xykmc.equals("")){
        data.setMessage(showJavaScript("alert('信用卡名称不能为空！');"));
        return;
      }
      if(xykhcd.equals("")){
        data.setMessage(showJavaScript("alert('信用卡号长度不能为空！');"));
        return;
      }
      String checkxykcd = checkNumber(xykhcd,"信用卡号长度");
      if(!(checkxykcd==null))
      {
        data.setMessage(checkxykcd);
        return;
      }
      String count=dataSetProvider.getSequence("SELECT COUNT(*) from rl_yhxyk where xykmc='"+xykmc+"'");
      if(!count.equals("0")&&isAdd)
      {
        data.setMessage(showJavaScript("alert('此银行信用卡已被使用,请从新输入!')"));
        return;
      }
      if(!isAdd)
        ds.goToInternalRow(editrow);
      if(isAdd)
      {
        ds.insertRow(false);
        ds.setValue("xykID", "-1");
      }
      ds.setValue("xykbh", xykbh);
      ds.setValue("xykmc", xykmc);
      ds.setValue("xykhcd", xykhcd);
      ds.post();
      ds.saveChanges();
      //刷新lookup的数据集，保持数据的同步.入参是相应的lookup名称
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_BANK_CREDIT_CARD);
      data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }
  /**
   * 删除操作的触发类
   */
  class B_CreditCard_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {

        EngineDataSet ds = getOneTable();
        ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
        String count = dataSetProvider.getSequence("select count(*) from rl_ygxykh where xykID="+ds.getValue("xykID"));
        if(count!=null&&!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('已被引用,不能删除!');"));
          return;
        }
        ds.deleteRow();
        ds.saveChanges();

      //刷新lookup的数据集，保持数据的同步.入参是相应的lookup名称
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_BANK_CREDIT_CARD);
    }
  }
}
