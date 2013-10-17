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
 * <p>Title: 基础信息－杂项维护</p>
 * <p>Description: 基础信息－杂项维护</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public class B_MixItem extends SingleOperate
{
  public static final int TYPE_CHANGE    = 101;//辅助信息类型的更改
  //表字段lx的各种类型
  public static final int ORDER_TYPE     = 1;//销售订单类型
  public static final int LANDING_TYPE   = 2;//提单业务类型
  public static final int SEND_PRODUCT   = 3;//提单发货方式
  public static final int BUY_TYPE       = 4;//采购订单类型
  public static final int SALE_CHANCE_TYPE =5;//销售机会来源
  public static final int MARKET_ACT_TYPE= 6;//市场活动类型
  public static final int QUOTE_BUY_TYPE = 7;//(报价单)购买方式
  public int     lx = ORDER_TYPE;//人员辅助信息的类型

  private EngineRow locateRow = null;

  public B_MixItem()
  {
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
    setDataSetProperty(dsOneTable, "SELECT * FROM zxwh");

    dsOneTable.setSort(new SortDescriptor("", new String[]{"lx","dm"}, new boolean[]{false,false}, null, 0));
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
    String dm = rowInfo.get("dm");
    String mc = rowInfo.get("mc");
    if(dm.equals(""))
      return LoginBean.showJavaScript("alert('编码不能为空！');");
    if(mc.equals(""))
      return LoginBean.showJavaScript("alert('名称不能为空！');");

    if(!isAdd)
      ds.goToInternalRow(editrow);

    if(isAdd || !dm.equals(ds.getValue("dm")))
    {
      String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('zxwh','dm','"+dm+"','lx="+lx+"') from dual");
      if(!count.equals("0"))
      {
        if(isAdd)
          initRowInfo(true, false);
        return LoginBean.showJavaScript("alert('编码("+ dm +")已经存在!');");
      }
    }

    if(isAdd)
    {
      ds.insertRow(false);
      ds.setValue("lxid", "-1");
      ds.setValue("lx", String.valueOf(lx));
    }
    ds.setValue("dm", dm);
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
      case TYPE_CHANGE:
        try{
          lx = Integer.parseInt(request.getParameter("lx"));
        }
        catch(Exception e){
          return "";
        }
        if(lx < ORDER_TYPE || lx > QUOTE_BUY_TYPE)
          lx = ORDER_TYPE;
        break;
    }
    return "";
  }

  /**
   * 初始化列信息 Implement this engine.project.OperateCommon abstract method
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  protected final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否时添加操作
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();

    if(isAdd){
      String code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('zxwh','dm','','lx="+lx+"',6) from dual");
      rowInfo.put("dm", code);
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