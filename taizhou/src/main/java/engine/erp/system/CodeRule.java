package engine.erp.system;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.util.StringUtils;
import engine.common.LoginBean;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title: 基础信息维护--编码规则</p>
 * <p>Description: 基础信息维护--编码规则</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public final class CodeRule extends BaseAction implements Operate
{
  public static final String PRODUCT_CODE_POST = "1001";
  public static final String PRODUCT_CODE_DEL  = "1002";
  /**
   * 提取编码规则信息的SQL语句
   */
  private static final String CODERULE_SQL = "SELECT * FROM jc_coderule_cont WHERE coderule=";//

  private static final String CODERULE = "SELECT coderule, rulename, maxlen FROM jc_coderule";

  private static final String FIRST_SEP  = ",";
  private static final String SECOND_SEP = "$";

  /**
   * 保存编码规则信息的数据集
   */
  private EngineDataSet dsCodeRuleList = new EngineDataSet();

  private EngineDataSet dsCodeRule = new EngineDataSet();

  /**
   * 保存拆分的字符串
   */
  public ArrayList typeList = null;

  public ArrayList numberList = null;

  /**
   * 编码总长度
   */
  public int totallen;
  /**
   * 保存用户输入的信息
   */
  //private RowMap rowInfo = new RowMap();

  /**
   * 点击返回按钮的URL
   */
  public  String retuUrl = null;

  //用于产品编码
  //private ArrayList productList = new ArrayList();

  private String codeRule = "";//SELECT * FROM kc_kw WHERE coderule='salebill'
  private String ruleName = "";
  public  int maxLen;
  /**
   * 得到编码规则的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回编码规则信息的实例
   */
  public static CodeRule getInstance(HttpServletRequest request)
  {
    CodeRule codeRuleBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "codeRuleBean";
      codeRuleBean = (CodeRule)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(codeRuleBean == null)
      {
        codeRuleBean = new CodeRule();
        session.setAttribute(beanName, codeRuleBean);
      }
    }
    return codeRuleBean;
  }
  /**
   * 构造函数
   */
  private CodeRule()
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
    setDataSetProperty(dsCodeRule, null);
    setDataSetProperty(dsCodeRuleList, CODERULE);
    dsCodeRuleList.setSort(new SortDescriptor("", new String[]{"rulename"}, new boolean[]{false}, null, 0));
    //dsCodeRule.setSequence(new SequenceDescriptor(new String[]{"coderule"}, new String[]{"jc_coderule"}));
    //添加操作的触发对象
    CodeRule_ProductPost productCodePost = new CodeRule_ProductPost();
    addObactioner(String.valueOf(INIT), new CodeRule_Init());
    addObactioner(String.valueOf(EDIT), new CodeRule_Edit());
    addObactioner(String.valueOf(POST), new CodeRule_Post());
    addObactioner(PRODUCT_CODE_POST, productCodePost);
    addObactioner(PRODUCT_CODE_DEL,  productCodePost);
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
      String opearate = request.getParameter(OPERATE_KEY);
      if(opearate != null && opearate.trim().length() > 0)
      {
        RunData data = notifyObactioners(opearate, request, response, null);
        if(data.hasMessage())
          return data.getMessage();
      }
      return "";
    }
    catch(Exception ex){
      if(dsCodeRule.isOpen() && dsCodeRule.changesPending())
        dsCodeRule.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  //----Implementation of the BaseAction abstract class
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsCodeRule != null){
      dsCodeRule.closeDataSet();
      dsCodeRule = null;
    }
    if(dsCodeRuleList != null){
      dsCodeRuleList.closeDataSet();
      dsCodeRuleList = null;
    }
    log = null;
    //rowInfo = null;
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
   * 初始化行信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   *
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    //是否时添加操作
    if(!isAdd)
      rowInfo.put(getOneTable());
    if(isInit && rowInfo.size() > 0)
      rowInfo.clear();
  }*/

  /*得到表对象*/
  public final EngineDataSet getCodeRuleList()
  {
    return dsCodeRuleList;
  }

  /*得到表对象*/
  public final EngineDataSet getOneTable()
  {
    return dsCodeRule;
  }
  /*得到传入的codeRule的值并将它赋值*/
  public final String getCodeRuleCaption()
  {
    return ruleName;
  }
    /*String temp = null;
    if(codeRule.equals("xs_ht"))
      temp = "销售合同编码规则";
    else if(codeRule.equals("xs_td.t"))
      temp ="销售退货单编码规则";
    else if(codeRule.equals("cg_ht"))
      temp ="采购合同编码规则";
    else if(codeRule.equals("cg_htjhd.t"))
      temp ="采购退货单编码规则";
    else if(codeRule.equals("kc_sfdj.b"))
      temp ="出库单编码规则";
    else if(codeRule.equals("kc_sfdj.a"))
      temp ="入库单编码规则";
    else if(codeRule.equals("sc_drawmaterial.a"))
      temp ="外加工发料单编码规则";
    else if(codeRule.equals("sc_receiveprod.a"))
      temp ="外加工入库单编码规则";
    else if(codeRule.equals("cw_wjgjs"))
      temp ="外加工结算编码规则";
    /**
    else if(codeRule.equals("kc_sfdj.e"))
      temp ="生产领料单编码规则";
    else if(codeRule.equals("kc_sfdj.f"))
      temp ="自制收货单编码规则";

    else if(codeRule.equals("sc_drawmaterial"))
      temp ="生产领料单编码规则";
    else if(codeRule.equals("sc_receiveprod"))
      temp ="自制收货单编码规则";
    else if(codeRule.equals("kc_sfdj.g"))
      temp ="移库单编码规则";
    else if(codeRule.equals("kc_sfdj.h"))
      temp ="损溢单编码规则";
    else if(codeRule.equals("kc_sfdj.i"))
      temp ="其它入库单编码规则";
    else if(codeRule.equals("cg_sqd"))
      temp ="采购申请单编码规则";
    else if(codeRule.equals("sc_jh"))
      temp ="生产计划编码规则";
    else if(codeRule.equals("sc_wlxqjh"))
      temp ="物料需求编码规则";
    else if(codeRule.equals("sc_rwd"))
      temp ="生产任务单编码规则";
    else if(codeRule.equals("sc_jgd"))
      temp ="生产加工单编码规则";
    else if(codeRule.equals("sc_jgd.a"))
      temp ="委外加工单编码规则";
    else if(codeRule.equals("product"))
      temp ="产品编码规则";
    else if(codeRule.equals("xs_td"))
      temp ="销售提货单编码规则";
    else if(codeRule.equals("cg_htjhd"))
      temp ="采购进货单编码规则";
    else if(codeRule.equals("cw_cgfp"))
      temp ="采购发票编码规则";
    else if(codeRule.equals("cw_cgjs"))
      temp ="采购结算编码规则";
    else if(codeRule.equals("cw_xsfp"))
      temp ="销售发票编码规则";
    else if(codeRule.equals("cg_sqd"))
      temp ="采购申请单编码规则";
    else if(codeRule.equals("sc_gzzgzl"))
      temp ="(工作组)工人工作量编码规则";
    else if(codeRule.equals("sc_bmzgzl"))
      temp ="(部门)工人工作量编码规则";
    else if(codeRule.equals("kc_pd"))
      temp ="库存盘点编码规则";
    else if(codeRule.equals("zl_buycheck"))
      temp = "采购入库单检验";
    else if(codeRule.equals("cw_cgyfk"))
      temp = "采购预付款";
      return temp;
  }
  */
  /**一个得到产品编码列表数组的方法
   *用特定的逗号分割符分割字符串segformat
   * 例如：3$n,3$u,3$uc表示每层的编码长度为3
   * @return
   */
  private String[][] getProductList()
  {
    return StringUtils.getArrays(dsCodeRule.getValue("segformat"), FIRST_SEP, SECOND_SEP);
  }
  //------------------------------------------
  //操作实现的类
  //------------------------------------------
  /**
   * 初始化操作的触发类
   */
  class CodeRule_Init implements Obactioner
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
      if(dsCodeRuleList.isOpen())
        dsCodeRuleList.refresh();
      else
        dsCodeRuleList.open();
    }
  }

  /**
   * 初始化操作的触发类
   */
  class CodeRule_Edit implements Obactioner
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
      String row = data.getParameter("type", "");
      if(row.length() == 0)
        return;

      dsCodeRuleList.goToRow(Integer.parseInt(row));
      codeRule = dsCodeRuleList.getValue("coderule");
      maxLen = dsCodeRuleList.getBigDecimal("maxlen").intValue();
      ruleName= dsCodeRuleList.getValue("rulename");

      dsCodeRule.setQueryString(CODERULE_SQL +"'"+ codeRule +"'");
      if(dsCodeRule.isOpen())
        dsCodeRule.refresh();
      else
        dsCodeRule.openDataSet();

      if(codeRule.equals("product"))//是否是产品编码界面初始化总长度
      {
        String[][] segments = getProductList();
        if(typeList == null)
          typeList = new ArrayList();
        else
          typeList.clear();
        if(numberList == null)
          numberList = new ArrayList();
        else
          numberList.clear();

        totallen = 0;
        for(int i=0; i<segments[0].length; i++)
        {
          String type = segments[0][i];
          numberList.add(segments[1][i]);
          typeList.add(type.length() ==0 ? "n" : type);
          totallen += Integer.parseInt(segments[1][i]);
        }
        try{
          totallen += Integer.parseInt(dsCodeRule.getValue("autolen"));
        }
        catch(Exception ex){
        }
      }
    }
  }

  /**
   * 保存操作的触发类
   */
  class CodeRule_Post implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发保存操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
      //rowInfo.put(data.getRequest());
      String codeprefix= data.getParameter("codeprefix", "");
      String dateformat= data.getParameter("dateformat", "");
      String autolen  = data.getParameter("autolen", "");
      //String rulename = rowInfo.get("rulename");
      if(autolen.equals("")){
        data.setMessage(showJavaScript("alert('顺序号长度不能为空！');"));
        return;
      }
      try{
        Integer.parseInt(autolen);
      }
      catch(Exception ex){
        data.setMessage(showJavaScript("alert('输入的顺序号长度不是整数类型！');"));
        return;
      }
      if(ds.getRowCount() == 0)
      {
        ds.insertRow(false);
        ds.setValue("coderule", codeRule);
      }
      ds.setValue("codeprefix", codeprefix);
      ds.setValue("dateformat", dateformat);
      ds.setValue("autolen", autolen);
      //ds.setValue("rulename", rulename);
      ds.post();
      ds.saveChanges();
      data.setMessage(showJavaScript("backList();"));
    }
  }
  /**
   * 保存操作的触发类
   */
  class CodeRule_ProductPost implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发保存操作
     * @param  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      boolean isDelete = PRODUCT_CODE_DEL.equals(action);//判断是否为删除操作
      EngineDataSet ds = getOneTable();
      //校验数据
      //rowInfo.clear();
      //rowInfo.put(data.getRequest());
      String autolen_s  = data.getParameter("autolen", "");
      String newsegment_s = data.getParameter("newsegment", "");
      String type = data.getParameter("type", "n");
      String alert = null;
      if((alert = checkInt(autolen_s, "顺序号位数")) != null)
      {
        data.setMessage(alert);
        return;
      }
      int autolen = Integer.parseInt(autolen_s);
      if(autolen<1)
      {
        data.setMessage(showJavaScript("alert('顺序号位数不能小于1！');"));
        return;
      }
      //校验新增位数
      if(newsegment_s.length() > 0 && (alert =checkInt(newsegment_s,"新增位数")) !=null)
      {
        data.setMessage(alert);
        return;
      }
      boolean newsegmentIsNull = newsegment_s.length() == 0;
      int newsegment = newsegmentIsNull ? 0 : Integer.parseInt(newsegment_s);
      if(!newsegmentIsNull && newsegment<1)
      {
        data.setMessage(showJavaScript("alert('新增位数不能小于1！');"));
        return;
      }
      //处理删除的分隔
      if(isDelete)
      {
        String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM kc_dm WHERE isdelete=0");
        if(!count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('已有产品存在不能删除!');"));
          return;
        }
        int row = Integer.parseInt(data.getParameter("rownum"));
        typeList.remove(row);
        numberList.remove(row);
      }

      //分级层数
      if(numberList.size() > 10)
      {
        data.setMessage(showJavaScript("alert('大于总级数！');"));
        return;
      }
      //计算编码总长度和得到分级格式segforment
      int newTotalLen = 0;
      StringBuffer buf = new StringBuffer();
      for(int i=0; i<numberList.size(); i++)
      {
        String number = (String)numberList.get(i);
        buf.append(typeList.get(i)).append(SECOND_SEP).append(number);
        buf.append(i==numberList.size()-1 ? "" : FIRST_SEP);
        newTotalLen += Integer.parseInt(number);
      }
      newTotalLen += autolen;
      int segmentnum = numberList.size();
      if(newsegment > 0)
      {
        newTotalLen += newsegment;
        buf.append(FIRST_SEP).append(type).append(SECOND_SEP).append(newsegment);
        segmentnum++;
        //
        numberList.add(newsegment_s);
        typeList.add(type);
      }
      String segformat = buf.toString();
      if(newTotalLen > maxLen)
      {
        data.setMessage(showJavaScript("alert('大于编码总长度！');"));
        return;
      }
      totallen = newTotalLen;
      //提交数据
      if(ds.getRowCount() == 0)
      {
        ds.insertRow(false);
        ds.setValue("coderule", codeRule);
        //ds.setValue("maxlen", "32");
      }
      ds.setValue("segmentnum", String.valueOf(segmentnum));
      ds.setValue("segformat", segformat);
      ds.setValue("autolen", String.valueOf(autolen));
      //ds.setValue("rulename", rowInfo.get("rulename"));
      ds.post();
      ds.saveChanges();
      //data.setMessage(showJavaScript("backList();"));
    }
  }
}
