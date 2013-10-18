package engine.erp.sale.shengyu;
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
    import engine.common.*;
    import java.util.*;
    import java.text.*;
    import java.text.SimpleDateFormat;
    import java.math.BigDecimal;
    import javax.servlet.http.HttpServletRequest;
    import javax.servlet.http.HttpServletResponse;
    import javax.servlet.http.HttpSessionBindingEvent;
    import javax.servlet.http.HttpSession;

    import com.borland.dx.dataset.*;

    /**
     * <p>Title: 新产品发货登记表</p>
     * <p>Copyright: right reserved (c) 2004</p>
     * <p>Company: ENGINE</p>
     * <p>Author: 胡康宁</p>
     * @version 1.0
     */

    public final class B_CustomerUnitiveDiscount extends BaseAction implements Operate
    {
      private static final String CPFHDJ_SQL = "SELECT * FROM xs_khtyzk order by cplx   ";

      private EngineDataSet dsCpfhdjTable = new EngineDataSet();//数据集
      private RowMap rowInfo = new RowMap();
      public  boolean isAdd = false;
      private long    editrow = 0;
      public  String retuUrl = null;

      //public  HtmlTableProducer masterProducer = new HtmlTableProducer(dsCpfhdjTable, "xs_khtyzk");
      //public  HtmlTableProducer table = new HtmlTableProducer(dsCpfhdjTable, "xs_khtyzk", "a");//查询得到数据库中配置的字段

      public  String loginid = "";   //登录员工的ID
      public  String loginCode = ""; //登陆员工的编码
      public  String loginName = ""; //登录员工的姓名
      private String fgsid = null;   //分公司ID
      public boolean isReport = false;
      public  boolean isApprove = false;     //是否在审批状态
      private String cpfhdjid = null;
      public  static final String CANCER_APPROVE           = "9002";
      public  static final String OVER                     = "9003";          //完成
      public  static final String SALE_CANCER              = "9004";          //作废
      public  static final String SELECT_PRODUCT           = "9005";
      public  static final String SXZ_CHANGE               = "9006";
      public  static final String AFTER_SELECT_PRODUCT     = "9006";
      public  static final String AFTER_POST               = "9007";
      public  static final String DWTXID_CHANGE   = "1007";
      public  static final String REPORT                   = "9008";      //报表追踪
      private QueryFixedItem fixedQuery = new QueryFixedItem();//定义固定查询类
      public boolean submitType;//用于判断true=仅制定人可提交,false=有权限人可提交
      private boolean isInitQuery = false;
      public String dwdm="";
      public String dwmc="";
      private User user = null;//登陆用户（设置用户部门权限）
      /**
       * 从会话中得到银行信用卡信息的实例
       * @param request jsp请求
       * @return 返回银行信用卡信息的实例
       */

      public static B_CustomerUnitiveDiscount getInstance(HttpServletRequest request)
      {
        B_CustomerUnitiveDiscount b_CustomerUnitiveDiscountBean = null;
        HttpSession session = request.getSession(true);
        synchronized (session)
        {
          String beanName = "b_CustomerUnitiveDiscountBean";
          b_CustomerUnitiveDiscountBean = (B_CustomerUnitiveDiscount)session.getAttribute(beanName);
          //判断该session是否有该bean的实例
          if(b_CustomerUnitiveDiscountBean == null)
          {
            LoginBean loginBean = LoginBean.getInstance(request);
            b_CustomerUnitiveDiscountBean = new B_CustomerUnitiveDiscount();
            b_CustomerUnitiveDiscountBean.fgsid = loginBean.getFirstDeptID();
            b_CustomerUnitiveDiscountBean.loginid = loginBean.getUserID();
            b_CustomerUnitiveDiscountBean.user = loginBean.getUser();
            b_CustomerUnitiveDiscountBean.loginName = loginBean.getUserName();
            session.setAttribute(beanName, b_CustomerUnitiveDiscountBean);//加入到session中
          }
        }
        return b_CustomerUnitiveDiscountBean;
      }
      /**
       * 构造函数
       */
      private B_CustomerUnitiveDiscount()
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
        setDataSetProperty(dsCpfhdjTable, combineSQL(CPFHDJ_SQL,"?",new String[]{fgsid,""}));                        //提取出全部数据
        //dsCpfhdjTable.setSequence(new SequenceDescriptor(new String[]{"cpfhdjid"}, new String[]{"s_xs_khtyzk"})); //设置主健的sequence
        //dsCpfhdjTable.setSequence(new SequenceDescriptor(new String[]{"djh"}, new String[]{"SELECT pck_base.billNextCode('xs_khtyzk','djh') from dual"}));
        dsCpfhdjTable.setTableName("xs_khtyzk");
        //dsCpfhdjTable.setSort(new SortDescriptor("", new String[]{"cpfhdjid"}, new boolean[]{false}, null, 0));
        //添加操作的触发对象
        Cpfhdj_Add_Edit add_edit = new Cpfhdj_Add_Edit();
        addObactioner(String.valueOf(ADD), add_edit);                  //新增
        addObactioner(String.valueOf(EDIT), add_edit);                 //修改
        addObactioner(String.valueOf(INIT), new Cpfhdj_Init());  //初始化 operate=0
        addObactioner(String.valueOf(POST), new Cpfhdj_Post());  //保存
        addObactioner(String.valueOf(DEL), new Cpfhdj_Del()); //删除

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
          if(dsCpfhdjTable.isOpen() && dsCpfhdjTable.changesPending())
            dsCpfhdjTable.reset();
          log.error("doService", ex);
          return showMessage(ex.getMessage(), true);
        }
      }
      /**
       * jvm要调的函数,类似于析构函数
       */
      public void valueUnbound(HttpSessionBindingEvent event)
      {
        if(dsCpfhdjTable != null){
          dsCpfhdjTable.close();
          dsCpfhdjTable = null;
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
       * 得到固定查询的用户输入的值
       * @param col 查询项名称
       * @return 用户输入的值
       */
      public final String getFixedQueryValue(String col)
      {
        return fixedQuery.getSearchRow().get(col);
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
          String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
          rowInfo.put("czrq", today);//制单日期
          rowInfo.put("czy", loginName);//操作员
          rowInfo.put("czyid", loginid);
          rowInfo.put("zt", "0");
        }
      }
      /*得到表对象*/
      public final EngineDataSet getOneTable()
      {
        //if(!dsCpfhdjTable.isOpen())
          //dsCpfhdjTable.open();
        return dsCpfhdjTable;
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
      class Cpfhdj_Init implements Obactioner
      {
        public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
        {
          dwdm ="";
          dwmc ="";
          isReport = false;
          isApprove = false;
          HttpServletRequest request = data.getRequest();
          retuUrl = data.getParameter("src");
          retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
          fixedQuery.getSearchRow().clear();

          String code = dataSetProvider.getSequence("select value from systemparam where code='SYS_APPROVE_ONLY_SELF' ");
          if(code.equals("1"))
            submitType=true;
          else
            submitType=false;

          String MSQL =  combineSQL(CPFHDJ_SQL, "?", new String[]{fgsid, ""});
          dsCpfhdjTable.setQueryString(MSQL);
          dsCpfhdjTable.setRowMax(null);
        }
      }
/**
 * 添加或修改操作的触发类
 */
class Cpfhdj_Add_Edit implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    isReport = false;
    isApprove = false;
    isAdd = action.equals(String.valueOf(ADD));
    if(!isAdd)
    {
      dsCpfhdjTable.goToRow(Integer.parseInt(data.getParameter("rownum")));
      editrow = dsCpfhdjTable.getInternalRow();
    }
    initRowInfo(isAdd, true);
  }
}
/**
 * 保存操作的触发类
 */
class Cpfhdj_Post implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    EngineDataSet ds = getOneTable();
    //校验数据
    //t.wzdjid,t.tdhwid,t.deptid,t.dwtxid,t.xs__tdhwid,t.cpid,t.dj,t.fkqk,t.dmsxid
    //需判断数据表中是否有相同的往来单位，产品，规格属性，部门，分公司
    rowInfo.put(data.getRequest());
    String xydj = rowInfo.get("xydj");
    String cplx = rowInfo.get("cplx");
    String djlx = rowInfo.get("djlx");
    String zk = rowInfo.get("zk");

    String temp = "";
    if(xydj.equals(""))
    {
      data.setMessage(showJavaScript("alert('请输入信誉等级!')"));
      return;
    }
    if(cplx.equals(""))
    {
      data.setMessage(showJavaScript("alert('请输入产品类别!')"));
      return;
    }
    if(djlx.equals(""))
    {
      data.setMessage(showJavaScript("alert('请输入定价类型!')"));
      return;
    }
    if(zk.equals(""))
    {
      data.setMessage(showJavaScript("alert('请输入折扣!')"));
      return;
    }
    if((temp = checkNumber(zk, "折扣")) != null)
    {
      data.setMessage(temp);
      return;
    }
    String count = dataSetProvider.getSequence("SELECT COUNT(*) FROM xs_khtyzk t WHERE t.xydj='"+xydj+"' AND t.cplx='"+cplx+"' AND t.djlx='"+djlx+"'");
    if(count!=null&&!count.equals("0"))
    {
      if(isAdd||!count.equals("1"))
      {
        data.setMessage(showJavaScript("alert('信誉等级，产品类别，定价类型不能重复!')"));
        return;
      }
    }
    if(isAdd)
    {
      String ncount = dataSetProvider.getSequence("SELECT COUNT(*) FROM xs_khtyzk t WHERE t.xydj='"+xydj+"' AND t.cplx='"+cplx+"' AND t.djlx='"+djlx+"'");
      if(ncount!=null&&ncount.equals("1"))
      {
        data.setMessage(showJavaScript("alert('信誉等级，产品类别，定价类型不能重复!')"));
        return;
      }
      ds.insertRow(false);
      isAdd=false;
    }
    else
      ds.goToInternalRow(editrow);
    ds.setValue("xydj", xydj);
    ds.setValue("cplx", cplx);
    ds.setValue("djlx", djlx);
    ds.setValue("zk", zk);
    ds.post();
    ds.saveChanges();
    editrow = ds.getInternalRow();
  }
}
/**
 * 删除操作的触发类
 */
class Cpfhdj_Del implements Obactioner
{
  public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
  {
    EngineDataSet ds = getOneTable();
    ds.goToRow(Integer.parseInt(data.getParameter("rownum")));
    ds.deleteRow();
    ds.saveChanges();
  }
}
}



