package engine.erp.common.install;

import javax.servlet.http.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.io.FileOutputStream;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.*;
import engine.dataset.EngineDataSetProvider;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: engine</p>
 * @author 江海岛
 * @version 1.0
 */

public class ExportTableData extends BaseAction implements Operate
{
  /**
   * 构造函数
   */
  public ExportTableData() {
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
  private final void jbInit() throws java.lang.Exception
  {
    //addObactioner(String.valueOf(INIT), new Init());
    addObactioner(String.valueOf(POST), new Export());
  }

  /**
   * Implement this engine.project.BaseAction abstract method
   * 得到子类的类名
   * @return 返回子类的类名
   */
  protected Class childClassName() {
    return getClass();
  }

  //----Implementation of the BaseAction abstract class
  /**
   * JSP调用的函数. 业务操作的入口函数
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  public String doService(HttpServletRequest request, HttpServletResponse response) throws java.lang.Exception
  {
     try{
       String operate = request.getParameter(OPERATE_KEY);
       if(operate != null && operate.length()> 0)
       {
         RunData data = notifyObactioners(operate, request, response, null);
         if(data == null)
           return showMessage("无效操作", false);
         if(data.hasMessage())
           return data.getMessage();
       }
       return "";
     }
     catch(Exception ex){
       //if(dsMasterTable.isOpen() && dsMasterTable.changesPending())
         //dsMasterTable.reset();
       log.error("doService", ex);
       return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * session失效是触发的事件
   * @param event session绑定事件
   */
  public void valueUnbound(HttpSessionBindingEvent event) {
    ;
  }

  /**
   * 得到当前日期
   * @return
   */
  public String getCurrentDate()
  {
    return new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
  }

  /**
   * 导出表数据的触发类
   * 更新网页提交的需要导出的表,到数据库中提取数据,并写在文件中
   */
  class Export implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 添加或修改的触发操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String filename = data.getParameter("filename", "");
      if(filename.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请输入文件路径！')"));
        return;
      }
      String[] tableNames = data.getParameterValues("tablename");
      if(tableNames == null || tableNames.length == 0)
      {
        data.setMessage(showJavaScript("alert('请选择需要导出表名！')"));
        return;
      }
      if(dataSetProvider == null)
        dataSetProvider = new EngineDataSetProvider();
      TableDataFacade.bakTablesData(dataSetProvider, tableNames, new FileOutputStream(filename));
      data.setMessage(showJavaScript("alert('导出数据成功,请查看相应的文件！')"));
    }
  }
}