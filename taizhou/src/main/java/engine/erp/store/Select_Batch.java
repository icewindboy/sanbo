package engine.erp.store;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.project.*;
import engine.html.*;
import engine.common.LoginBean;
import engine.util.log.LogHelper;
import engine.util.log.Log;

import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;

/**
 * <p>Title: 库存管理_销售出库单选择批号</p>
 * <p>Description: 库存管理_销售出库单选择批号</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */
/**
 * 2004-4-28 12:18 修改 如果调用页面传进来的dmsxid为空的话，那么就认为它(调用页面)将不使用dmsxid来查找批号资料,而是用sxz来使用查找. yjg
 *                 但,这会引起一个问题就是:带回调用页面(如:销售出库单)的dmsxid就会改变.这会不会造成产生一些错误.
 * 03:29 18:20 新增 用于执行配批号页面上新增的批号输入框以实现类似规格属性输入框类似的功能. yjg
 */

public final class Select_Batch extends BaseAction implements Operate
{
  private static final String  SELECT_BATCH_STRUCT_SQL = " SELECT a.wzmxid, a.cpid, a.dmsxid, a.storeid, a.kwid, a.zl, a.ph, a.fgsid, b.sxz FROM kc_wzmx a, kc_dmsx b "
                                                       +  "  WHERE 1<>1";
  private static final String  SELECT_BATCH_SQL
    = "SELECT a.wzmxid, a.cpid, a.dmsxid, a.storeid, a.kwid, a.zl, a.ph, a.fgsid, b.sxz FROM kc_wzmx a, kc_dmsx b "
      + " WHERE a.zl<>0 and a.fgsid=? and a.cpid=? and a.dmsxid = b.dmsxid(+) and a.storeid=? ? ?";

  public String SHORTCUTOP = "58";//03:29 18:20 新增 用于执行配批号页面上新增的批号输入框以实现类似规格属性输入框类似的功能. yjg
  public String cpid       = "";
  public String storeid    = "";
  public String dmsxid     = "";
  public String widthSxz   = "";
  public String lengthSxz  = "";
  public String sxz        = "";
  public String sxz2       = "";
  public String ph         = "";
  public boolean  isMultiSelect = false;
  public String srcFrm = "";
  public String widthSyskey = "";
  public String lengthSyskey = "";
  public String multiIdInput = "";
  public String methodName = "";
  public String row = "";//用来保存如果是单选择调用的话,用来得调用时的当前行数,用以在返回时给sl_onchange()提供行数据参数.
  public String isIconFirst = "";
  public String isFirstComeIn = "";
  public String caller = "";
  public String[] inputName = null;
  public String[] fieldName = null;

  private EngineDataSet dsBatch  = new EngineDataSet();
  private EngineRow locateResult = null;
  public  String retuUrl         = null;
  private String fgsid           = null;   //分公司ID
  private String qtyFormat       = null;

  public RowMap searchConditionRowInfo = new RowMap();
  /**
   * 得到批号信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回批号信息的实例
   */
  public static Select_Batch getInstance(HttpServletRequest request)
  {
    Select_Batch selectBatchBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "selectBatchBean";
      selectBatchBean = (Select_Batch)session.getAttribute(beanName);
      if(selectBatchBean == null)
      {
        LoginBean loginBean = LoginBean.getInstance(request);
        selectBatchBean = new Select_Batch();
        selectBatchBean.qtyFormat = loginBean.getQtyFormat();
        selectBatchBean.fgsid = loginBean.getFirstDeptID();
        selectBatchBean.widthSyskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_PROP");
        selectBatchBean.lengthSyskey = loginBean.getSystemParam("SYS_PRODUCT_SPEC_LENGTH");
        session.setAttribute(beanName, selectBatchBean);
      }
    }
    return selectBatchBean;
  }

  /**
   * 构造函数
   */
  private Select_Batch()
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
    setDataSetProperty(dsBatch, SELECT_BATCH_STRUCT_SQL);
    addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(SHORTCUTOP), new Init());
    //addObactioner(String.valueOf(FIXED_SEARCH), new Search());
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
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * Session失效时，调用的函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsBatch != null){
      dsBatch.close();
      dsBatch = null;
    }
    log = null;
    searchConditionRowInfo=null;
  }

  /**
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected final Class childClassName()
  {
    return getClass();
  }

  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest request = data.getRequest();
      putDetailInfo(request);
      retuUrl = request.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      String temp = request.getParameter("isMultiSelect")==null?"":request.getParameter("isMultiSelect");
      //1表示是要多选按钮形式checkbox
      isMultiSelect = temp.equals("1")?true:false;
      srcFrm = request.getParameter("srcFrm")==null?srcFrm:request.getParameter("srcFrm");//
      methodName = request.getParameter("methodName")==null?methodName:request.getParameter("methodName");//
      multiIdInput = request.getParameter("srcVar")==null?multiIdInput:request.getParameter("srcVar");//
      //isIconFirst = request.getParameter("isIconFirst")==null?isIconFirst:"1";//request.getParameter("isIconFirst");
      //如果不是多选按钮形式的话,那么就须要取inputName和fieldName了.
      //因为单选情况下页面上要用js直接返回值给调用页面.
      if ( !isMultiSelect )
      {
        inputName = request.getParameterValues("srcVarTwo")==null?inputName:request.getParameterValues("srcVarTwo");
        fieldName = request.getParameterValues("fieldVar")==null?fieldName:request.getParameterValues("fieldVar");
        row = request.getParameter("row")==null?row:request.getParameter("row");
      }
      //isIconFirst等于1则说明不是图标事件第一次进来的.
      isIconFirst = "1";
      isFirstComeIn = "1";
      if (request.getParameter("isIconFirst")==null)
        isIconFirst = "0";
      if (request.getParameter("isFirstComeIn")==null)
      {
        isFirstComeIn = "0";
      }
      //初始化查询项目和内容
      //initQueryItem(request);
      //fixedQuery.getSearchRow().clear();
      //替换可变字符串，组装SQL
      cpid = request.getParameter("cpid")==null?"":request.getParameter("cpid");
      if (cpid.equals("") || cpid == null )  { data.setMessage("showJavaScript('alert('产品不能为空！');')"); return;}
      storeid = request.getParameter("storeid")==null?"":request.getParameter("storeid");
      if (storeid.equals("") || storeid == null )  { data.setMessage("showJavaScript('alert('库房不能为空！');')"); return;}
      dmsxid = request.getParameter("dmsxid")==null?"":request.getParameter("dmsxid");
      sxz = request.getParameter("sxz")==null?"":request.getParameter("sxz");
      //解析传进来的sxz字串,变成如:%宽度(600)%长度(500)%
      if (!sxz.equals(""))
      {
        widthSxz = parseEspecialString(sxz, "()", false);
        lengthSxz = parseEspecialString(sxz, "()", true);
        widthSxz  = widthSxz.equals("")?"": widthSyskey + "(" + widthSxz + ")";
        lengthSxz = lengthSxz.equals("")?"": lengthSyskey + "(" + lengthSxz + ")";

        sxz = widthSxz.equals("")?"":"%"+widthSxz+"%";
        sxz = sxz + (sxz.equals("")?(lengthSxz.equals("")?"":"%"+lengthSxz+"%"):(lengthSxz.equals("")?"":lengthSxz+"%"));
        sxz2 = lengthSxz.equals("")?"":"%"+lengthSxz+"%";
        sxz2 = sxz2 + (sxz2.equals("")?(widthSxz.equals("")?"":"%"+widthSxz+"%"):(widthSxz.equals("")?"":widthSxz+"%"));
      }
      String sql = "";
      //2004-4-28 12:18 修改 如果调用页面传进来的dmsxid为空的话，那么就认为它(调用页面)将不使用dmsxid来查找批号资料,而是用sxz来使用查找. yjg
      //但,这会引起一个问题就是:带回调用页面(如:销售出库单)的dmsxid就会改变.这会不会造成产生一些错误.
      if ( !dmsxid.equals("") )
        sql = " and a.dmsxid = " + dmsxid;
      else if ( !sxz.equals("") )
         sql = "and ( b.sxz like '" + sxz + "' or b.sxz like '" + sxz2 + "' )";
      ph = request.getParameter("ph")==null?"":request.getParameter("ph");
      if ( !ph.equals("") )
        sql += " and a.ph like '%" + ph + "%'";
      String SQL = combineSQL(SELECT_BATCH_SQL, "?", new String[]{fgsid,cpid,storeid, sql});
      dsBatch.setQueryString(SQL);
      if (!dsBatch.isOpen())
        dsBatch.openDataSet();
      else
        dsBatch.readyRefresh();
        dsBatch.refresh();
        //dsBatch.setRowMax(null);
    }
  }
  /*
  *得到一行信息
  */

  public final RowMap getLookupRow(String wzmxid)
  {
    RowMap row = new RowMap();
    if(wzmxid == null || wzmxid.equals(""))
      return row;
    EngineRow locateRow = new EngineRow(dsBatch, "wzmxid");
    locateRow.setValue(0, wzmxid);
    if(getOneTable().locate(locateRow, Locate.FIRST))
      row.put(getOneTable());
    return row;
  }
  public final EngineDataSet getOneTable()
  {
    return dsBatch;
  }
  /**
   * 保存查询条件操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putDetailInfo(HttpServletRequest request)
  {
    RowMap rowInfo = new RowMap();
    //保存网页的所有信息
    rowInfo.put(request);
    searchConditionRowInfo.put("cpid", rowInfo.get("cpid"));
    searchConditionRowInfo.put("storeid", rowInfo.get("storeid"));
    searchConditionRowInfo.put("dmsxid", rowInfo.get("dmsxid"));
    searchConditionRowInfo.put("ph", rowInfo.get("ph"));
    searchConditionRowInfo.put("srcFrm", rowInfo.get("srcFrm"));
    searchConditionRowInfo.put("srcVar", rowInfo.get("srcVar"));
    searchConditionRowInfo.put("isMultiSelect", rowInfo.get("isMultiSelect"));
    searchConditionRowInfo.put("sxz", rowInfo.get("sxz"));
    searchConditionRowInfo.put("row", rowInfo.get("row"));
    searchConditionRowInfo.put("methodName", rowInfo.get("methodName"));
  }
  public Log getLog(){
    return super.log;
  }
  /**
   * 分割字符串s,用sep分割成一个字符串数组.并保存到HashTable中
   * @para s 源串
   * @para sep 分割符
   * @para isGetLength 解析出长度.true则解析长度,false则解析宽度
   * @return 返回字符串，是Hash表中key为field的值
   */
  public final String parseEspecialString(String s, String sep, boolean isGetLength)
  {
    //保存返回的串值.
    String returnS = "";
    if(s==null || s.equals(""))
      return "0";
    String[] code = parseString(s, sep);
    //宽度的键及值.
    String key=null, value = null;
    //长度的键及值.
      //String lengthKey=null, lengthValue = null;
    //取宽度
    int valueCoordinate = 0;
    int KeyCoordinate   = 0;
    for(int i=0; i<code.length; i++)
    {
      if(i%2 > 0){
        value = code[i];
        valueCoordinate = i;
      }
      else{
        key = code[i].trim();
        KeyCoordinate = i;
      }
      if(value==null)
        continue;
      if(key.equals(isGetLength?lengthSyskey:widthSyskey))
      {
        if ( (valueCoordinate < KeyCoordinate) && (KeyCoordinate++ < code.length) )
          return code[KeyCoordinate++];
        else
          return value;
      }
    }
    return "";
  }
}



