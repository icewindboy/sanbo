package engine.erp.baseinfo;

import engine.project.*;
import engine.dataset.*;
import engine.common.LoginBean;
import engine.util.StringUtils;

import javax.servlet.http.*;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 基础信息维护--费用列表</p>
 * <p>Description: 基础信息维护--费用列表</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public final class B_FeeRate extends SingleOperate
{
  private static final String DETAIL_SQL = "SELECT * FROM jc_fyfl WHERE fyflID=";//
  private EngineRow locateRow = null;
  public  String retuUrl = null;
  public  static final int OPERATEITEM = 1000;
  public  int    fydl = 1;


  /**
   * 构造函数
   */
  public B_FeeRate()
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
  protected void jbInit() throws java.lang.Exception
  {
    setDataSetProperty(dsOneTable, "SELECT * FROM jc_fyfl");
  //  dsOneTable.setSort(new SortDescriptor("", new String[]{"ckmc"}, new boolean[]{false}, null, 0));
    dsOneTable.setSequence(new SequenceDescriptor(new String[]{"fyflID"}, new String[]{"s_jc_fyfl"}));
  }

  /**
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
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName()
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
    String lxid = rowInfo.get("lxid");       //费用名称
    String fl = rowInfo.get("fl");           //费率
    String storeid = rowInfo.get("storeid"); //仓库名
    String qydd =rowInfo.get("qydd");        //起运地点
    String pz = rowInfo.get("pz");           //品种
    String mddd =rowInfo.get("mddd");        //目的地点
    String djsm = rowInfo.get("djsm");       //定价说明
    String jglx = rowInfo.get("jglx");       //加工类型
    String dwtxid = rowInfo.get("dwtxid");   //结算单位
    String dqh  = rowInfo.get("dqh");       //地区号
    String dwdm = rowInfo.get("dwdm");      //单位代码
    String temp = null;



  /**  if(lxid.equals(""))
      return LoginBean.showJavaScript("alert('费用名称不能为空！');");
  **/

    if(fl.equals(""))
     fl = "0";    //不输入默认为零
    else{
     temp = checkNumber(fl,"费率");
     if(temp!= null)
       return LoginBean.showJavaScript("alert('费率只能为数字！');");
       }

       boolean isShowStore  = true;
       boolean isChangeName = true;
       switch(fydl){
       case 1:{
              isShowStore = true;
              isChangeName = false;
              break;
             }
       case 2:{
             isShowStore  = false;
             isChangeName = true;
             break;
              }
       case 3:{
             isShowStore = true;
             isChangeName = false;
             break;
            }
       }


    if(djsm.equals(""))
      return LoginBean.showJavaScript("alert('定价说明不能为空！');");


    if(!isAdd)
      ds.goToInternalRow(editrow);
    if(isAdd || !lxid.equals(ds.getValue("lxid")))
    {
      String count = dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('jc_fyfl','lxid','"+lxid+"','fydl="+fydl+"') from dual");
      if(!count.equals("0"))
      {
        return LoginBean.showJavaScript("alert('费用名称("+ lxid +")已经存在!');");
      }
    }

    if(isAdd)
    {
      ds.insertRow(false);
      ds.setValue("fyflid", "-1");
    }

    ds.setValue("fydl",new Integer(fydl).toString());//费用名称 or:String.valueOf(fydl)
    ds.setValue("fl", fl);                           //费率
    if(isShowStore) ds.setValue("storeid", storeid); //仓库名
    ds.setValue("pz",pz);                            //品种
    ds.setValue("qydd",qydd);                        //起运地点
    ds.setValue("jglx",jglx);                        //加工类型
    ds.setValue("mddd",mddd);                        //目的地点
    ds.setValue("djsm",djsm);                        //定价说明
    ds.setValue("dwtxid",dwtxid);                    //结算单位
    ds.setValue("lxid", lxid);


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
     case OPERATEITEM:
       try{
         fydl = Integer.parseInt(request.getParameter("fydl"));
       }
       catch(Exception e)
       {}
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
  public final RowMap getRowinfo() {    return rowInfo;  }

  /**
   * 得到杂项列表的页面的<select>控件的<option></option>的字符串
   * @param mixId 初始化选中的杂项ID
   * @param lx 杂项类型
   * @return 杂项列表
   */
  public synchronized final String getFeeRateForOption(String fyflID)
  {
    return dataSetToOption(getOneTable(), "fyflID", "lxid", fyflID, null, null);
  }
  /**
   * 根据杂项ID得到杂项名称
   * @param bankId 初始化选中的杂项ID
   * @return 杂项名称
   */
  public synchronized final String getStoreFeeRateName(String fyflID)
  {
    if(locateRow == null)
      locateRow = new EngineRow(getOneTable(), "fyflID");
    locateRow.setValue(0, fyflID);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      return getOneTable().getValue("lxid");
    else
      return "";
  }
}
