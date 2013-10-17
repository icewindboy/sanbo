package engine.erp.person;


import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import java.util.*;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;
/**
 * <p>Title:人力资源--定义款项公式</p>
 * <p>Description: 人力资源--定义款项公式</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 张新文
 * @version 1.0
 */
public final class B_WageFieldEexpression extends BaseAction implements Operate
{
  private static final String GZKXSZ_SQL = "SELECT * FROM rl_gzkxsz ";//
  private EngineDataSet dsRl_gzkxsz = new EngineDataSet();
  private EngineRow locateRow = null;
  private RowMap rowInfo = new RowMap();
  public  boolean isAdd = true;
  private long  editrow = 0;
  public  String retuUrl = null;
  public static B_WageFieldEexpression getInstance(HttpServletRequest request)
  {
    B_WageFieldEexpression b_WageFieldEexpressionBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "b_WageFieldEexpressionBean";
      b_WageFieldEexpressionBean = (B_WageFieldEexpression)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(b_WageFieldEexpressionBean == null)
      {
        b_WageFieldEexpressionBean = new B_WageFieldEexpression();
        session.setAttribute(beanName, b_WageFieldEexpressionBean);
      }
    }
    return b_WageFieldEexpressionBean;
  }
  /**
   * 构造函数
   */
  private B_WageFieldEexpression()
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
    setDataSetProperty(dsRl_gzkxsz, GZKXSZ_SQL);
    dsRl_gzkxsz.setSort(new SortDescriptor("", new String[]{"pxh"}, new boolean[]{false}, null, 0));
    dsRl_gzkxsz.setSequence(new SequenceDescriptor(new String[]{"gzkxszID"}, new String[]{"S_RL_GZKXSZ"}));
    addObactioner(String.valueOf(INIT), new B_WorkProcedure_Init());
    addObactioner(String.valueOf(POST), new B_WorkProcedure_Post());
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
      if(dsRl_gzkxsz.isOpen() && dsRl_gzkxsz.changesPending())
        dsRl_gzkxsz.reset();
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }
  /**
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    if(dsRl_gzkxsz != null){
      dsRl_gzkxsz.close();
      dsRl_gzkxsz = null;
    }
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
    if(!dsRl_gzkxsz.isOpen())
      dsRl_gzkxsz.open();
    return dsRl_gzkxsz;
  }
  /*得到一列的信息*/
  public final RowMap getRowinfo() {    return rowInfo;  }
  /**
   * 初始化操作的触发类
   */
  class B_WorkProcedure_Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      retuUrl = data.getParameter("src");
      retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
      dsRl_gzkxsz.refresh();
    }
  }
  /**
   * 保存操作的触发类
   */
  class B_WorkProcedure_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      EngineDataSet ds = getOneTable();
      //校验数据
      ArrayList fieldkey=new ArrayList();
      ArrayList jsgsvalue=new ArrayList();
      StringBuffer gs=new StringBuffer();
      rowInfo.put(data.getRequest());
      ds.first();
      for(int i=0;i<ds.getRowCount();i++)
      {
       String jsgs=rowInfo.get("jsgs_"+i);
       if(!jsgs.equals(""))
       {
         String field=ds.getValue("dyzdm");//公式字段
         if(jsgs.indexOf(field)>-1)
         {
           data.setMessage(showJavaScript("alert('款项中"+ds.getValue("mc")+"公式错误!')"));
           return;
         }
         ds.setValue("jsgs",jsgs);
         fieldkey.add(field);
         jsgsvalue.add(jsgs);
         gs.append(jsgs+",");
       }
       else
         ds.setValue("jsgs",jsgs);
       ds.next();
      }
      ds.saveChanges();
    }
   //public String
  }
}


