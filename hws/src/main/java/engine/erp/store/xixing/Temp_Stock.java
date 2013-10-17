package engine.erp.store.xixing;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.dataset.EngineDataSet;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.common.User;
import engine.web.observer.Obationable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import engine.web.observer.Obactioner;



/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author wlc
 * @version 1.0
 */


public final class Temp_Stock extends BaseAction implements Operate{
  String SQL = "SELECT * FROM fgsid = '?',storeid = '?',cpid = '?'"; //表

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  public  String loginDept = ""; //登录员工的部门
  public  String bjfs = ""; //系统的报价方式



  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private String fgsid = null;   //分公司ID
  public String isHandwork = null; //是否允许手工录入自制入库单
  private User user = null;
  public String SYS_APPROVE_ONLY_SELF = null;//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
  public String SC_DRAW_MATERIAL = null;//生产领料配批是否以库存数量为准,1=以库存数量为准,0=没有变化
  public String SC_STORE_UNIT_STYLE = null;//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
  public String KC_PRODUCE_UNIT_STYLE = null;//计量单位和生产单位换算方式1=强制换算,0=仅空值时换算
  public String drawType = null;//单据类型1是领料-1是退料
  public String SYS_PRODUCT_SPEC_PROP = null;//生产用位换算的相关规格属性名称 得到的值为“宽度”……
  public String SC_OUTSTORE_SHOW_ADD_FIELD = null;//是否显示生产领料单的附加字段



  private EngineDataSet dsMasterTable = new EngineDataSet();

  private Temp_Stock(){
    try{
      jbInit();
    }catch(Exception ex){
      log.error("jbInit",ex);
    }
  }
  private final void  jbInit() throws Exception{
    setDataSetProperty(dsMasterTable, SQL);

    addObactioner(String.valueOf(ADD),new Master_Add_Edit());
  }

  public static Temp_Stock getInstence(HttpServletRequest request){
    Temp_Stock tempStockBean = null;
    HttpSession session = request.getSession(true);
    synchronized(session){
      String beanName = "tempStockBean";
      tempStockBean = (Temp_Stock)session.getAttribute(beanName);
      if(tempStockBean == null){
        LoginBean loginBean = LoginBean.getInstance(request);
        tempStockBean = new Temp_Stock();

        tempStockBean.qtyFormat = loginBean.getQtyFormat();

        tempStockBean.fgsid = loginBean.getFirstDeptID();
        tempStockBean.loginId = loginBean.getUserID();
        tempStockBean.loginName = loginBean.getUserName();
        tempStockBean.loginDept = loginBean.getDeptID();
        tempStockBean.bjfs = loginBean.getSystemParam("BUY_PRICLE_METHOD");
        tempStockBean.isHandwork = loginBean.getSystemParam("KC_HANDIN_STOCK_BILL");//是否可以手工添加系统参数1=允许手工输入,0=不允许手工输入
        tempStockBean.SYS_APPROVE_ONLY_SELF = loginBean.getSystemParam("SYS_APPROVE_ONLY_SELF");//提交审批是否只有制单人可以提交,1=仅制单人可提交,0=有权限人可提交
        tempStockBean.SC_DRAW_MATERIAL = loginBean.getSystemParam("SC_DRAW_MATERIAL");//生产领料配批是否以库存数量为准,1=以库存数量为准,0=没有变化
        tempStockBean.SC_STORE_UNIT_STYLE = loginBean.getSystemParam("SC_STORE_UNIT_STYLE");//计量单位和辅单位换算方式1=强制换算,0=仅空值时换算
        tempStockBean.KC_PRODUCE_UNIT_STYLE = loginBean.getSystemParam("KC_PRODUCE_UNIT_STYLE");//计量单位和生产单位换算方式1=强制换算,0=仅空值时换算
        tempStockBean.SYS_PRODUCT_SPEC_PROP = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");//生产用位换算的相关规格属性名称 得到的值为“宽度”……
        tempStockBean.SC_OUTSTORE_SHOW_ADD_FIELD = loginBean.getSystemParam("SC_OUTSTORE_SHOW_ADD_FIELD");//是否显示生产领料单的附加字段
        tempStockBean.user = loginBean.getUser();

        session.setAttribute(beanName, tempStockBean);
      }
    }
    return tempStockBean;
  }

  //----Implementation of the BaseAction abstract class
/**
 * JSP调用的函数
 * @param request 网页的请求对象
 * @param response 网页的响应对象
 * @return 返回HTML或javascipt的语句
 * @throws Exception 异常
 */

  public final String doService(HttpServletRequest request, HttpServletResponse response ){
    try{
      String operate = request.getParameter(OPERATE_KEY);
      if(operate != null && operate.trim().length() > 0){
        RunData data = notifyObactioners(operate,request,response,null);
        if(data == null)
          return showMessage("无效操作",false);
        if(data.hasMessage())
          return data.getMessage();
      }
       return "";
     }
     catch(Exception ex){
      if(dsMasterTable.isOpen() && dsMasterTable.changesPending())
        dsMasterTable.reset();
        log.error("doService", ex);
        return showMessage(ex.getMessage(), true);
      }
  }
  public String adoService(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      String opearate = request.getParameter(OPERATE_KEY);
      if(opearate != null && opearate.trim().length() > 0){
        RunData data = notifyObactioners(opearate, request, response, null);
        if(data == null)
          return showMessage("无效操作", false);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsMasterTable.isOpen() && dsMasterTable.changesPending())
        dsMasterTable.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }


  public final void valueUnbound(HttpSessionBindingEvent enent){
    if(dsMasterTable != null){
      dsMasterTable.close();
      dsMasterTable = null;
    }
  }
  public final Class childClassName(){
    return getClass();
  }
 class Master_Add_Edit implements Obactioner{
   public void execute(String action, Obationable o, RunData data, Object arg) throws Exception{
     showMessage(action,true);
   }
 }

}
