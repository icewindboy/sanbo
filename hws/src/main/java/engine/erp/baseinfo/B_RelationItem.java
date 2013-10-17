package engine.erp.baseinfo;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.*;

import com.borland.dx.dataset.*;
import engine.dataset.*;
import engine.util.*;
import engine.util.log.LogHelper;
import engine.project.*;
import engine.common.LoginBean;
/**
 * <p>Title: 基础信息－物资相关维护</p>
 * <p>Description: 基础信息－物资相关维护</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛_徐锋
 * @version 1.0
 */

public class B_RelationItem extends SingleOperate
{
  //实例对象的参数的入参的静态变量
  public static final int RELA_MATIARIAL  = 1001;//物资相关
  /**实例对象的参数的入参的静态变量: 采购相关*/
  public static final int RELA_BUY        = 1002;//采购相关
  /**实例对象的参数的入参的静态变量: 加工相关*/
  public static final int RELA_PROCESS    = 1003;//加工相关
  /**实例对象的参数的入参的静态变量: 财务相关*/
  public static final int RELA_FINANCE    = 1004;//财务相关
  /**实例对象的参数的入参的静态变量: 销售相关*/
  public static final int RELA_SALE       = 1005;//销售相关

  public static final int TYPE_CHANGE    = 101;//辅助信息类型的更改
  //表字段lx的各种类型
  //物资相关维护(没用的)
  public static final int LX_PRODUCT_NAME  = 11;//品名
  public static final int LX_MATIARIAL     = 12;//材质
  public static final int LX_SPEC          = 13;//规格
  public static final int LX_AREA          = 14;//产地

  //采购相关维护
  /**采购相关维护: 采购费用设置*/
  public static final int LX_BUY_SPENDS    = 21;//采购费用设置
  /**采购相关维护: 发货方式*/
  public static final int LX_SEND_PRODUCT  = 22;//发货方式

  /**加工相关维护: 加工类型*/
  public static final int LX_PROCESS       = 31;//加工类型

  /**财务相关设置: 费用名称设置*/
  public static final int LX_SPENDS_NAME   = 41;//费用名称设置
  /**财务相关设置: 收支来源用途设置*/
  public static final int LX_IN_OUT        = 42;//收支来源用途设置
  /**财务相关设置: 常用摘要设置*/
  public static final int LX_SUMMERISE     = 43;//常用摘要设置


  /**销售相关设置: 开单地址*/
  public static final int LX_LIST_ADDRESS  = 51;//开单地址


  public int    lx = 0;
  private int   htmlChange = 0;

  private EngineRow locateRow = null;
  public  String retuUrl = null;

  /**
   * 得到实例
   * @param request
   * @param htmlChange 改变页面 RELA_BUY:采购相关 RELA_PROCESS:加工相关
   * RELA_FINANCE:财务相关 RELA_SALE:销售相关
   *
   * @return 返回实例
   */
  public static B_RelationItem getInstance(HttpServletRequest request, int htmlChange)
  {
    HttpSession session = request.getSession(true);
    B_RelationItem relationBean = null;
    synchronized (session)
    {
      relationBean = (B_RelationItem)session.getAttribute("relationitemBean_"+htmlChange);
      if(relationBean == null)
      {
        relationBean = new B_RelationItem(htmlChange);
        session.setAttribute("relationitemBean_"+htmlChange, relationBean);
      }
    }
    return relationBean;
  }

  private B_RelationItem(int type)
  {
    htmlChange = type;
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  /**
   * Implement this engine.project.OperateCommon abstract method
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected final void jbInit() throws java.lang.Exception
  {
    switch(htmlChange)
    {
      case RELA_MATIARIAL:
        setDataSetProperty(dsOneTable, "SELECT * FROM zxwh WHERE lx>10 AND lx<20");
        lx = LX_PRODUCT_NAME;
        break;
      case RELA_BUY:
        setDataSetProperty(dsOneTable, "SELECT * FROM zxwh WHERE lx>20 AND lx<30");
        lx = LX_BUY_SPENDS;
        break;
      case RELA_PROCESS:
        setDataSetProperty(dsOneTable, "SELECT * FROM zxwh WHERE lx>30 AND lx<40");
        lx = LX_PROCESS;
        break;
      case RELA_FINANCE:
        setDataSetProperty(dsOneTable, "SELECT * FROM zxwh WHERE lx>40 AND lx<50");
        lx = LX_SPENDS_NAME;
        break;
      case RELA_SALE:
        setDataSetProperty(dsOneTable, "SELECT * FROM zxwh WHERE lx>50 AND lx<60");
        lx = LX_LIST_ADDRESS;
        break;
    }

    dsOneTable.setSort(new SortDescriptor("", new String[]{"lx","pxh"}, new boolean[]{false,false}, null, 0));
    dsOneTable.setSequence(new SequenceDescriptor(new String[]{"lxid"}, new String[]{"s_zxwh"}));
  }

  /**
   * Implement this engine.project.OperateCommon abstract method
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    dsOneTable.close();
    dsOneTable = null;
    log = null;
    rowInfo = null;
    locateRow = null;
  }

  /**
   * Implement this engine.project.OperateCommon abstract method
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }
  /**
   * 保存操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws java.lang.Exception 异常
   */
  protected final String postOperate(HttpServletRequest request, HttpServletResponse response)
      throws Exception
  {
    EngineDataSet ds = getOneTable();
    //校验数据
    rowInfo.put(request);
    String pxh = rowInfo.get("pxh");
    String mc = rowInfo.get("mc");
    String temp = null;
    temp = checkNumber(pxh, "排序号");
    if(temp != null)
      return temp;
    if(mc.equals(""))
      return LoginBean.showJavaScript("alert('名称不能为空！');");

    if(!isAdd)
      ds.goToInternalRow(editrow);

    if(isAdd)
    {
      ds.insertRow(false);
      ds.setValue("lxid", "-1");
      ds.setValue("lx", String.valueOf(lx));
    }
    ds.setValue("pxh", pxh);
    ds.setValue("mc", mc);
    ds.post();
    ds.saveChanges();
    return LoginBean.showJavaScript("parent.hideInterFrame();");
  }
  /**
   * 删除操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   */
  protected final String deleteOperate(HttpServletRequest request, HttpServletResponse response)
  {
    try{
      lx = Integer.parseInt(request.getParameter("lx"));
    }
    catch(Exception e){
      return "";
    }

    EngineDataSet ds = getOneTable();
    ds.goToRow(Integer.parseInt(request.getParameter("rownum")));
    ds.deleteRow();
    ds.saveChanges();
    return "";
  }
  /**
   * 其他操作，父类未定义的操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @param operate 网页的操作类型
   * @return 返回HTML或javascipt的语句
   */
  protected final String otherOperate(HttpServletRequest request, HttpServletResponse response, int operate)
  {
    switch(operate)
    {
      //类型更改时
      case Operate.ADD:
      case Operate.EDIT:
        try{
          lx = Integer.parseInt(request.getParameter("lx"));
        }
        catch(Exception e){
        }
        break;
        //初始化
      case Operate.INIT:
        retuUrl = request.getParameter("src");
        retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
        break;
    }
    return "";
  }

  /**
   * 初始化列信息 Implement this engine.project.OperateCommon abstract method
   * @param isAdd 是否添加
   * @param isInit 是否重新初始化
   * @throws java.lang.Exception 异常
   */
  protected final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();

    if(isAdd){
      ;
    }
    else
      rowInfo.put(getOneTable());
  }

  /*得到表对象，Implement this engine.project.OperateCommon abstract method*/
  public final EngineDataSet getOneTable()
  {
    if(!dsOneTable.isOpen())
      dsOneTable.open();
    return dsOneTable;
  }

  /*得到一列的信息，Implement this engine.project.OperateCommon abstract method*/
  public final RowMap getRowinfo() {    return rowInfo;  }

  /**
   * 得到杂项列表的页面的<select>控件的<option></option>的字符串
   * @param mixId 初始化选中的杂项ID
   * @param lx 杂项类型
   * @return 杂项列表
   */
  public synchronized final String getMixItemForOption(String mixId, int lx)
  {
    return dataSetToOption(getOneTable(), "lxid", "mc", mixId, "lx", String.valueOf(lx));
  }
  /**
   * 根据杂项ID得到杂项名称
   * @param bankId 初始化选中的杂项ID
   * @return 杂项名称
   */
  public synchronized final String getMixItemName(String mixId)
  {
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "lxid");
    locateRow.setValue(0, mixId);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      return getOneTable().getValue("mc");
    else
      return "";
  }
}
