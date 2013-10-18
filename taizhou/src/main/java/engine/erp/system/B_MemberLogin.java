package engine.erp.system;

import javax.servlet.http.*;
import javax.servlet.jsp.*;
import java.util.*;

import com.borland.dx.dataset.*;
import engine.dataset.*;
import engine.util.*;
import engine.util.log.LogHelper;
import engine.project.*;
/**
 * <p>Title: 基础维护－会员登录管理</p>
 * <p>Description: 基础维护－会员登录管理<br>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public class B_MemberLogin extends SingleOperate
{
  public static final int OPERATE_CLEAR  = 10001;//清楚密码

  private static final String MEMBER_STRUCT_SQL = "SELECT personid, bm, xm, username, userpass, isuse FROM emp WHERE 1<>1";
  private static final String MEMBER_SQL = "SELECT personid, bm, xm, username, userpass, isuse FROM emp "
      + "WHERE isdelete=0 AND deptid IN (SELECT deptid FROM bm WHERE ismember=1 AND isdelete=0)";

  private QueryBasic fixedQuery = null;
  public String retuUrl = null;

  /**
   * 得到往来单位信息的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回往来单位信息的实例
   */
  public static B_MemberLogin getInstance(HttpServletRequest request)
  {
    B_MemberLogin memberLoginBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "memberLoginBean";
      memberLoginBean = (B_MemberLogin)session.getAttribute(beanName);
      if(memberLoginBean == null)
      {
        memberLoginBean = new B_MemberLogin();
        session.setAttribute(beanName, memberLoginBean);
      }
    }
    return memberLoginBean;
  }

  private B_MemberLogin()
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
    setDataSetProperty(dsOneTable, MEMBER_STRUCT_SQL);
    //dsOneTable.setSort(new SortDescriptor("", new String[]{"bm"}, new boolean[]{false}, null, 0));
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
    ds.first();
    for(int i=0; i<ds.getRowCount(); i++)
    {
      String username = request.getParameter("username_"+ds.getRow());
      if(username != null && !ds.getValue("username").equals(username))
      {
        ds.setValue("username", username);
        ds.post();
      }
      String isuse = request.getParameter("isuse_"+ds.getRow());
      if(isuse != null && !ds.getValue("isuse").equals(isuse))
      {
        ds.setValue("isuse", isuse);
        ds.post();
      }
      ds.next();
    }
    if(ds.changesPending())
      ds.saveChanges();

    return "";
  }
  /**
   * 删除操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   */
  protected final String deleteOperate(HttpServletRequest request, HttpServletResponse response)
  {
    //清除密码
    dsOneTable.goToRow(Integer.parseInt(request.getParameter("rownum")));
    dsOneTable.setValue("userpass", "");
    dsOneTable.post();
    dsOneTable.saveChanges();
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
      //初始化
      case Operate.INIT:
        retuUrl = request.getParameter("src");
        retuUrl = retuUrl!= null ? retuUrl.trim() : retuUrl;
        //初始化查询项目和内容
        initQueryItem(request);
        RowMap row = fixedQuery.getSearchRow();
        row.clear();
        dsOneTable.setQueryString(MEMBER_STRUCT_SQL);
        dsOneTable.setRowMax(null);
        return showJavaScript("showFixedQuery();");
      //查询
      case Operate.FIXED_SEARCH:
        QueryBasic queryBasic = fixedQuery;
        queryBasic.setSearchValue(request);
        String SQL = queryBasic.getWhereQuery();
        SQL = MEMBER_SQL + (SQL.equals("")? "" : (" AND " + SQL)) +" ORDER BY bm";
        if(!dsOneTable.getQueryString().equals(SQL))
        {
          dsOneTable.setQueryString(SQL);
          dsOneTable.setRowMax(null);
        }
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
  }

  /*得到表对象，Implement this engine.project.OperateCommon abstract method*/
  public final EngineDataSet getOneTable()
  {
    return dsOneTable;
  }

  /*得到一列的信息，Implement this engine.project.OperateCommon abstract method*/
  public final RowMap getRowinfo() {    return rowInfo;  }

  /**
   * 取得分段的往来单位数据
   * @param rowMin 分段的最小行标
   * @param rowMax 分段的最大行标
   */
  public void fetchData(int rowMin, int rowMax, HttpServletRequest request)
  {
    if(dsOneTable.getRowMin() != rowMin || dsOneTable.getRowMax() != rowMax)
    {
      dsOneTable.setRowMin(rowMin);
      dsOneTable.setRowMax(rowMax);
      if(dsOneTable.isOpen())
        dsOneTable.refresh();
      else
        dsOneTable.open();
    }

    initQueryItem(request);
  }

  /**
   * 初始化查询的各个列
   * @param request web请求对象
   */
  private void initQueryItem(HttpServletRequest request)
  {
    if(fixedQuery != null)
      return;
    EngineDataSet master = dsOneTable;
    if(!master.isOpen())
      master.open();

    //初始化固定的查询项目
    fixedQuery = new QueryFixedItem();
    fixedQuery.addShowColumn("", new QueryColumn[]{
      new QueryColumn(master.getColumn("bm"), null, null, null),
      new QueryColumn(master.getColumn("xm"), null, null, null),
      new QueryColumn(master.getColumn("isuse"), null, null, null, null, "=")
    });
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
}