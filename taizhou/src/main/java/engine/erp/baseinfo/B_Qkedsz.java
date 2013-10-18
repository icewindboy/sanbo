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
 * <p>Title: 基础信息－购货单位维护</p>
 * <p>Description: 基础信息－购货单位维护</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public class B_Qkedsz extends SingleOperate
{
  private static final String DETAIL_SQL = "SELECT * FROM xs_qkedsz WHERE rkedID=";//
  private EngineRow locateRow = null;
  public  String retuUrl = null;
  /**
   * 得到往来单位信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回往来单位信息的实例
   */
  public static B_Qkedsz getInstance(HttpServletRequest request)
  {
    B_Qkedsz qkedszBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "qkedszBean";
      qkedszBean = (B_Qkedsz)session.getAttribute(beanName);
      if(qkedszBean == null)
      {
        qkedszBean = new B_Qkedsz();
        session.setAttribute(beanName, qkedszBean);
      }
    }
    return qkedszBean;
  }

  /**
   * 构造函数
   */
  private  B_Qkedsz()
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
    setDataSetProperty(dsOneTable, "SELECT * FROM xs_qkedsz ");
    dsOneTable.setSequence(new SequenceDescriptor(new String[]{"rkedID"}, new String[]{"s_xs_qkedsz"}));
  }

  /**
   * Implement this engine.project.OperateCommon abstract method
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsOneTable != null){
      dsOneTable.close();
      dsOneTable = null;
    }
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
   * @throws java.lang.Exception异常
   */
  protected final String postOperate(HttpServletRequest request, HttpServletResponse response)
      throws Exception
  {
    EngineDataSet ds = getOneTable();
    //校验数据
    rowInfo.put(request);
    String dwtxid = rowInfo.get("dwtxid");          //购货单位
    String qsrq   = rowInfo.get("qsrq");            //起始日期
    String zzrq   = rowInfo.get("zzrq");            //终止日期
    String kqked  = rowInfo.get("kqked");           //可欠款额度
    String personid = rowInfo.get("personid");      //批准人
    String bz   = rowInfo.get("bz");                //备注
    String dqh  = rowInfo.get("dqh");               //地区号
    String dwdm = rowInfo.get("dwdm");              //单位代码
    String temp = null;

    if(dwtxid.equals(""))
      return LoginBean.showJavaScript("alert('购货单位不能为空！');");
    if(qsrq.equals(""))
      return LoginBean.showJavaScript("alert('起始日期不能为空！');");
    if(zzrq.equals(""))
      return LoginBean.showJavaScript("alert('终止日期不能为空！');");

    if(kqked.equals(""))
      kqked = "0";
    else{
      temp = checkNumber(kqked,"可欠款额度");
      if(temp!= null)
        return LoginBean.showJavaScript("alert('可欠款额度只能为数字！');");
       }

    if(!isAdd)
      ds.goToInternalRow(editrow);
    if(isAdd || !dwtxid.equals(ds.getValue("dwtxid")))
    {
      String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('xs_qkedsz','dwtxid','"+dwtxid+"') from dual");
      if(!count.equals("0"))
      {
        if(isAdd)
          initRowInfo(true, false);
        return LoginBean.showJavaScript("alert('购货单位("+ dwtxid +")已经存在!');");
      }
    }

    if(isAdd)
    {
      ds.insertRow(false);
      ds.setValue("rkedID","-1");
    }

    ds.setValue("dwtxid",dwtxid);                     //购货单位
    ds.setValue("qsrq",qsrq);                         //起始日期
    ds.setValue("zzrq",zzrq);                         //终止日期
    ds.setValue("kqked",kqked);                       //可欠款额度
    ds.setValue("personid",personid);                 //批准人
    ds.setValue("bz",bz);                             //bz
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
    switch(operate){
    case Operate.INIT:
       retuUrl = request.getParameter("src");
       retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
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
  public final RowMap getRowinfo() {
      return rowInfo;
     }

  /**
   * 得到购货单位列表的页面的<select>控件的<option></option>的字符串
   * @param mixId 初始化选中的购货单位ID
   * @param  购货单位类型
   * @return 购货单位列表
   */
  public synchronized final String getQkedszForOption(String rkedID)
  {
    return dataSetToOption(getOneTable(), "rkedID", "dwxlid", rkedID, null, null);
  }
  /**
   * 根据购货单位ID得到购货单位名称
   * @param bankId 初始化选中的购货单位ID
   * @return 购货单位名称
   */
  public synchronized final String getQkedszName(String rkedID)
  {
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(),"rkedID");
      locateRow.setValue(0, rkedID);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      return getOneTable().getValue("dwtxid");
    else
      return null;
  }
}