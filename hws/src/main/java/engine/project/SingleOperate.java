package engine.project;

import javax.servlet.http.*;
import javax.servlet.jsp.*;

import com.borland.dx.dataset.*;
import engine.dataset.*;
import engine.util.log.Log;
/**
 * <p>Title: 单表操作类</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author hukn
 * @version 1.0
 */

public abstract class SingleOperate extends CommonClass
{
  public    boolean isAdd = true;    //是否在添加状态
  protected long    editrow = 0;     //保存修改操作的行记录指针
  //protected long    editrow_detail = 0;  //保存从表修改操作的行记录指针

  protected EngineDataSet dsOneTable  = new EngineDataSet();

  protected RowMap rowInfo    = new RowMap(); //添加行或修改行的引用

  /**
   * JSP调用的函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  public String doService(HttpServletRequest request, HttpServletResponse response)
  {
    //设置浏览器没有缓存
    response.setHeader("Pragma","no-cache");
    response.setHeader("Cache-Control","no-cache");
    response.setDateHeader("Expires", 0);

    String sRe = "";
    if(request.getParameter("operate") == null || request.getParameter("operate").equals(""))
      return sRe;
    int operate = -1;
    try{
      operate = Integer.parseInt(request.getParameter("operate"));
    }
    catch(Exception ex){
      return sRe;
    }

    try{
      sRe = doService(request, response, operate);
    }
    catch(Exception ex){
      if(dsOneTable.isOpen() && dsOneTable.changesPending())
        dsOneTable.reset();
      sRe = showMessage(ex.getMessage(), true);
      log.error("doService", ex);
    }

    return sRe;
  }
  /**
   * BEAN的逻辑部分
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @param operate 网页的操作类型
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
 private String doService(HttpServletRequest request, HttpServletResponse response, int operate) throws Exception
  {
    String sRe= "";
    switch(operate)
    {
      //
      case Operate.ADD:
      case Operate.EDIT:
        isAdd = operate == Operate.ADD;
        if(!isAdd)
        {
          dsOneTable.goToRow(Integer.parseInt(request.getParameter("rownum")));
          editrow = dsOneTable.getInternalRow();
        }
        initRowInfo(isAdd, true);
        sRe = otherOperate(request, response, operate);
        break;
      //
      case Operate.POST:
        sRe = postOperate(request, response);
        break;
      //
      case Operate.DEL:
        sRe = deleteOperate(request, response);
        break;
      default:
        sRe = otherOperate(request, response, operate);
    }
    return sRe;
  }

  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  protected abstract void jbInit() throws Exception;

  /*提交操作*/
  protected abstract String postOperate(HttpServletRequest request, HttpServletResponse response) throws Exception;

  /*删除操作*/
  protected abstract String deleteOperate(HttpServletRequest request, HttpServletResponse response) throws Exception;

  /*其他操作*/
  protected abstract String otherOperate(HttpServletRequest request, HttpServletResponse response, int operate) throws Exception;

  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   */
  protected abstract void initRowInfo(boolean isAdd, boolean isInit) throws Exception;

  /*得到表对象*/
  public abstract EngineDataSet getOneTable();

  /*得到一列的信息*/
  public abstract RowMap getRowinfo();
}