package engine.erp.system;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineDataView;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.erp.baseinfo.HtmlTree;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;

/**
 * Title:        基础信息--界面信息维护
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author  江海岛
 * @version 1.0
 */

public class B_NodeInfo extends BaseAction implements Operate
{
  //各种操作的静态变量
  public static final int NODE_ADD_CHILD = 1000;//添加子结点
  public static final int NODE_EDIT_VIEW = 1001;//浏览或编辑界面结点
  public static final int NODE_PERSON    = 1002;//界面下的人员列表
  public static final int NODE_DELETE    = 1003;//删除界面结点
  public static final int NODE_PASTE     = 1004;//粘贴界面结点
  public static final int NODE_OPEN_PRIVILIGE = 1005;//打开界面权限

  public static final int NODE_TREE_POST = 2001;//界面树的被动提交
  //与nodeinfoedit.jsp有关的操作
  public static final int NODE_EDIT_POST = 2002;//提交
  //

  private EngineDataSet dsNodeInfo = new EngineDataSet();//保存界面树状信息的数据集
  public  EngineDataSet dsNodeData = new EngineDataSet();//保存需要修改的数据集
  public  EngineDataSet dsNodePrivilige = new EngineDataSet();//界面权限的数据集

  private HtmlTree deptTree = null;//保存打印界面树的对象

  public  int tempOperate = NODE_EDIT_VIEW;//临时保存上次的操作类型
  private String parentId = null;          //保存父结点ID
  private String parentCode = "";          //保存父结点的编码
  private String nodeId   = null;          //保存当前结点
  private static final String deptSQL  = "SELECT * FROM nodeInfo WHERE isdelete=0 AND nodeId=";
  private static final String codeSQL  = "SELECT count(*) FROM nodeInfo WHERE isdelete=0 AND nodeCode='";
  private static final String CHILD_COUNT_SQL = "SELECT COUNT(*) FROM nodeInfo WHERE isdelete=0 AND parentNodeId=";

  private static final String NODE_PRIVILIGE_SQL = "SELECT * FROM limitList WHERE isdelete=0 AND nodeId=";

  public  boolean isDeptAdd = true;//界面的设置是否处在添加状态
  private String  pathCode = null; //保存树展开的路径

  public    boolean isAdd = true;    //是否在添加状态
  protected long    editrow = 0;     //保存修改操作的行记录指针

  private String isExecute = "0";
  private String nodeType = "1";
  public RowMap rowInfo = new RowMap();

  /**
   * 得到权限维护的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回权限维护的实例
   */
  public static B_NodeInfo getInstance(HttpServletRequest request)
  {
    B_NodeInfo nodeInfoBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "nodeInfoBean";
      nodeInfoBean = (B_NodeInfo)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(nodeInfoBean == null)
      {
        nodeInfoBean = new B_NodeInfo();
        session.setAttribute(beanName, nodeInfoBean);
      }
    }
    return nodeInfoBean;
  }


  public B_NodeInfo() {
    try {
      jbInit();
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
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
   * jvm要调的函数,类似于析构函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    dsNodeInfo.close();
    dsNodeInfo = null;
    dsNodeData.close();
    dsNodeData = null;
    dsNodePrivilige.close();
    dsNodePrivilige = null;
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private void jbInit() throws Exception {
    setDataSetProperty(dsNodeInfo, "SELECT nodeId, nodeCode, parentNodeId, nodeName FROM nodeInfo WHERE isdelete=0");
    setDataSetProperty(dsNodeData, "SELECT * FROM nodeInfo WHERE 1<>1");
    setDataSetProperty(dsNodePrivilige, null);

    dsNodeInfo.setSort(new SortDescriptor("", new String[]{"nodeCode"}, new boolean[]{false}, null, 0));
    dsNodeData.setSequence(new SequenceDescriptor(new String[]{"nodeId"}, new String[]{"s_nodeInfo"}));
    dsNodePrivilige.setSequence(new SequenceDescriptor(new String[]{"limitId"}, new String[]{"s_limitList"}));
    //初始化
    addObactioner(String.valueOf(INIT), new Init());
    //执行者
    Node_AddChild_Edit node_AddChild_Edit = new Node_AddChild_Edit();
    //添加 编辑的执行者
    addObactioner(String.valueOf(NODE_EDIT_VIEW), node_AddChild_Edit);
    //添加 新增子节点的执行者
    addObactioner(String.valueOf(NODE_ADD_CHILD), node_AddChild_Edit);
    //添加 节点提交的执行者
    addObactioner(String.valueOf(NODE_EDIT_POST), new Node_Post());
    //添加 节点删除的执行者
    addObactioner(String.valueOf(NODE_DELETE), new Node_Delete());
    //添加 节点粘贴的执行者
    addObactioner(String.valueOf(NODE_PASTE), new Node_Paste());
    //添加 刷新树的执行者
    addObactioner(String.valueOf(NODE_TREE_POST), new Node_Refresh());
    //添加 打开节点权限的执行者
    addObactioner(String.valueOf(NODE_OPEN_PRIVILIGE), new Node_OpenPrivilige());
    //添加 添加权限的执行者
    Node_Privilige_AddEdit node_Privilige_AddEdit = new Node_Privilige_AddEdit();
    addObactioner(String.valueOf(ADD), node_Privilige_AddEdit);
    //添加 修改权限操作的执行者
    addObactioner(String.valueOf(EDIT), node_Privilige_AddEdit);
    //添加 提交权限的执行者
    addObactioner(String.valueOf(POST), new Node_Privilige_Post());
    //添加 删除权限的执行者
    addObactioner(String.valueOf(DEL), new Node_Privilige_Delete());
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
      log.error("doService", ex);
      return showMessage(ex.getMessage(), true);
    }
  }

  /**
   * 设置默认的操作
   * @param operate 当前操作
   */
  private void setDefaultOperate(int operate)
  {
    tempOperate = operate;
    if(tempOperate < NODE_ADD_CHILD || tempOperate > NODE_PERSON)
      tempOperate = NODE_EDIT_VIEW;
  }
  /**
   * 得到界面树的HTML文本的字符串
   * @return 返回界面树的HTML字符串
   */
  public String getDeptTree()
  {
    if(!dsNodeInfo.isOpen())
      dsNodeInfo.open();
    if(deptTree == null)
    {
      deptTree = new HtmlTree();
      deptTree.setTreeProperty(dsNodeInfo, "界面名称", "nodeId", "nodeCode", "parentNodeId", "nodeName");
    }
    return deptTree.printHtmlTree(pathCode);
  }

  /**
   * 提取添加界面结点的代码
   * @return 返回界面编码
   * @throws Exception 异常
   */
  public String[] getDeptCode() throws Exception
  {
    String code = null;
    if(isDeptAdd)
      code = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('nodeInfo','nodeCode','"
           + parentCode +"','isdelete=0') from dual");
    else
      code = dsNodeData.getValue("nodeCode");
    int leng = code.length()-2;
    return new String[]{code.substring(0, leng), code.substring(leng)};
  }

  /**
   * 得到所有的界面信息列表
   * @return 界面信息列表
   */
  public synchronized EngineDataView getAllDept()
  {
    if(!dsNodeInfo.isOpen())
      dsNodeInfo.open();
    return dsNodeInfo.cloneEngineDataView();
  }
  /**
   * 得到第一行的界面ID
   * @return 界面信息列表
   */
  public final String getFisrtRowDept()
  {
    EngineDataView ds = getAllDept();
    String nodeId = ds.getValue("nodeId");
    ds.close();
    return nodeId;
  }
  /**
   * 根据界面ID提取界面名称
   * @param nodeId 界面序号
   * @return 界面名称
   */
  public synchronized String getDeptName(String nodeId)
  {
    if(nodeId == null || nodeId.equals(""))
      return "";
    String deptName= null;
    EngineDataView ds = getAllDept();
    if(locateDataSet(ds, "nodeId", nodeId, Locate.FIRST))
      deptName = ds.getValue("nodeName");
    else
      deptName = "";
    ds.close();
    ds = null;
    return deptName;
  }

  /**
   * 得到界面列表的页面的<select>控件的<option></option>的字符串
   * @param currnodeId 当前界面ID，即是需要选定的界面ID
   * @return 界面信息列表
   */
  public synchronized String getDeptForOption(String currnodeId)
  {
    StringBuffer buf = new StringBuffer();
    EngineDataView ds = getAllDept();
    ds.first();
    while(ds.inBounds())
    {
      String deptcode = ds.getValue("nodeCode");
      createDeptOptionTree(ds, deptcode, 0, currnodeId, buf);
    }
    ds.close();
    ds = null;
    return buf.toString();
  }

  /**
   * 创建各个树结点
   * @param fatherCode 父结点编号
   * @param level 层
   * @return 返回是否还有子结点
   */
  private boolean createDeptOptionTree(EngineDataView ds, String fatherCode, int level,
                                       String currnodeId, StringBuffer buf)
  {
    String nodeId    = ds.getValue("nodeId");
    String deptcode  = ds.getValue("nodeCode");
    String deptname  = ds.getValue("nodeName");
    boolean hasChild = false;//是否还有子结点
    boolean isLeaf = false;

    if(ds.next())
    {
      String nextCode = ds.getValue("nodeCode");
      isLeaf = !nextCode.startsWith(deptcode);
      hasChild = nextCode.startsWith(fatherCode);
    }
    else
    {
      isLeaf = true;
      hasChild = false;
    }

    buf.append("<option value=");
    buf.append(nodeId);
    if(nodeId.equals(currnodeId))
      buf.append(" selected");
    buf.append(">");
    for(int i=0; i<level; i++)
      buf.append("&nbsp;");

    buf.append(deptname);
    buf.append("</option>");

    if(!isLeaf)
    {
      //打印子结点
      while(true)
      {
        boolean isHasChild = createDeptOptionTree(ds, deptcode, level+1, currnodeId, buf);
        if(!isHasChild)
          break;
      }

      hasChild = ds.inBounds() && ds.getValue("nodeCode").startsWith(fatherCode);
    }
    return hasChild;
  }

  /**
   * 初始化操作的触发类
   */
  class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(dsNodeInfo.isOpen())
        dsNodeInfo.refresh();
      else
        dsNodeData.open();
    }
  }
  //------------------------------------------
  //操作实现的类
  //------------------------------------------
  /**
   * 添加子节点或查看编辑记录节点操作的触发类
   */
  class Node_AddChild_Edit implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发添加子节点或查看编辑记录节点操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String nodeID = data.getParameter("nodeID");
      if(nodeID == null || nodeID.trim().equals(""))
      {
        data.setMessage(showJavaScript("location.href='../blank.htm';"));
        return;
      }

      isDeptAdd = action.equals(String.valueOf(NODE_ADD_CHILD));
      if(isDeptAdd)
      {
        parentId = nodeID.trim();
        isExecute = data.getParameter("isexecute");
        if(isExecute == null || isExecute.equals(""))
        {
          data.setMessage(showJavaScript("alert('非法操作！');"));
          return;
        }
      }
      else
        nodeId = nodeID.trim();
      /*查找记录*/
      dsNodeData.setQueryString(deptSQL+ (isDeptAdd ? parentId : nodeId));
      if(dsNodeData.isOpen())
        dsNodeData.refresh();
      else
        dsNodeData.open();

      if(dsNodeData.getRowCount() < 1)//没有记录,不是根结点
        if(!isDeptAdd || !parentId.equals("0"))
        {
          data.setMessage(showJavaScript("alert('没有该条记录, 可能已经被别的用户更改！'); location.href='../blank.htm';"));
          return;
        }

      if(isDeptAdd)
        parentCode = parentId.equals("0") ? "" : dsNodeData.getValue("nodeCode");
    }
  }

  /**
   * 节点提交操作的触发类
   */
  class Node_Post implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发节点提交操作 nodeinfodit.jsp提交动作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      if(isDeptAdd){
        if(parentId == null || parentId.trim().equals("")){
          data.setMessage(showJavaScript("alert('请选择要添加下级界面的界面！'); location.href='../blank.htm';"));
          return;
        }
      }
      else
      {
        if(nodeId == null || nodeId.trim().equals("")){
          data.setMessage(showJavaScript("alert('请选择需要修改的界面！'); location.href='../blank.htm';"));
          return;
        }
      }
      //判断编码是否已经存在
      String count = null;
      String deptcode = null, deptoldcode = null;
      deptcode = data.getParameter("prefix_code") + data.getParameter("self_code");
      if(isDeptAdd)
        count = dataSetProvider.getSequence(codeSQL + deptcode + "'");
      else{
        deptoldcode = dsNodeData.getValue("nodeCode");
        if(!deptoldcode.equals(deptcode))
          count = dataSetProvider.getSequence(codeSQL + deptcode + "'");
      }
      if(count != null && !count.equals("0"))
      {
        data.setMessage(showJavaScript("alert('该编码已经存在!');"));
        return;
      }

      if(isDeptAdd)
      {
        dsNodeData.insertRow(false);
        dsNodeData.setValue("nodeId", "-1");
        dsNodeData.setValue("parentNodeId", parentId);
        dsNodeData.setValue("isExecute", isExecute);
        dsNodeData.setValue("nodeType", nodeType);
      }
      dsNodeData.setValue("nodeCode", deptcode);

      RowMap row = new RowMap();
      row.put(data.getRequest());
      dsNodeData.setValue("nodeName", row.get("nodeName"));
      //dsNodeData.setValue("nodeCode",  row.get("nodeCode"));
      dsNodeData.setValue("url",   row.get("url"));
      dsNodeData.setValue("interCode", row.get("interCode"));
      dsNodeData.setValue("isjit", row.get("isjit"));
      dsNodeData.setValue("isdelete", "0");
      dsNodeData.post();
      //同步树的记录
      if(isDeptAdd)
      {
        dsNodeData.saveChanges();
        dsNodeInfo.insertRow(false);
        dsNodeInfo.setValue("nodeId", dsNodeData.getValue("nodeId"));
        dsNodeInfo.setValue("parentNodeId", dsNodeData.getValue("parentNodeId"));
        dsNodeInfo.setValue("nodeCode", dsNodeData.getValue("nodeCode"));
        dsNodeInfo.setValue("nodeName", dsNodeData.getValue("nodeName"));
        dsNodeInfo.post();
      }
      //更改界面编码
      else if(!deptoldcode.equals(deptcode))
      {
        dsNodeData.saveChanges(new String[]{"CALL pck_base.updateChildCode('nodeInfo','nodeCode','"+deptoldcode+"','"+deptcode+"','isdelete=0')"});
        dsNodeInfo.refresh();
      }
      else
      {
        dsNodeData.saveChanges();
        if(locateDataSet(dsNodeInfo, "nodeId", nodeId, Locate.FIRST))
        {
          dsNodeInfo.setValue("nodeCode", dsNodeData.getValue("nodeCode"));
          dsNodeInfo.setValue("nodeName", dsNodeData.getValue("nodeName"));
          dsNodeInfo.post();
        }
      }
      pathCode = deptcode;
      data.setMessage(showJavaScript("submitTree();"));
    }
  }

  /**
   * 节点删除操作的触发类
   */
  class Node_Delete implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发节点提交操作 nodeinfodit.jsp提交动作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String deleteId = data.getParameter("curNodeID");
      if(deleteId == null || deleteId.trim().equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择界面！');"));
        return;
      }
      if(!dataSetProvider.getSequence(CHILD_COUNT_SQL + deleteId).equals("0"))
      {
        data.setMessage(showJavaScript("alert('该界面下有子界面！');"));
        return;
      }
      /*查找记录*/
      dsNodeData.setQueryString(deptSQL+ deleteId.trim());
      if(dsNodeData.isOpen())
        dsNodeData.refresh();
      else
        dsNodeData.openDataSet();

      if(dsNodeData.getRowCount() < 1)
      {
        data.setMessage(showJavaScript("alert('没有该条记录, 可能已经被别的用户更改！');"));
        return;
      }

      setDefaultOperate(Integer.parseInt(action));

      dsNodeData.setValue("isdelete", "1");
      dsNodeData.post();
      dsNodeData.saveChanges();
      //同步树的数据
      if(locateDataSet(dsNodeInfo, "nodeId", deleteId, Locate.FIRST))
        dsNodeInfo.deleteRow();

      dsNodeInfo.prior();
      pathCode = dsNodeInfo.getValue("nodeCode");
    }
  }

  /**
   * 节点粘贴操作的触发类
   */
  class Node_Paste implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 触发节点粘贴操作
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String cutId = data.getParameter("cutNodeID");
      String pasteId = data.getParameter("curNodeID");
      if(cutId == null || cutId.trim().equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择需要剪切界面！');"));
        return;
      }
      if(pasteId == null || pasteId.trim().equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择粘贴的目标界面！');"));
        return;
      }
      cutId = cutId.trim();
      pasteId = pasteId.trim();
      /*查找记录*/
      String deptcode = null, deptoldcode = null;
      if(!pasteId.equals("0")){//是否是根节点
        dsNodeData.setQueryString(deptSQL+ pasteId);
        if(dsNodeData.isOpen())
          dsNodeData.refresh();
        else
          dsNodeData.openDataSet();

        if(dsNodeData.getRowCount() < 1)
        {
          data.setMessage(showJavaScript("alert('没有粘贴界面的记录, 可能已经被别的用户更改！');"));
          return;
        }
        deptcode = dsNodeData.getValue("nodeCode");
      }
      else
        deptcode = "";

      dsNodeData.setQueryString(deptSQL+ cutId);
      if(dsNodeData.isOpen())
        dsNodeData.refresh();
      else
        dsNodeData.openDataSet();
      if(dsNodeData.getRowCount() < 1)
      {
        data.setMessage(showJavaScript("alert('没有剪切界面的记录, 可能已经被别的用户更改！');"));
        return;
      }

      deptoldcode = dsNodeData.getValue("nodeCode");//剪切界面的编号
      if(!pasteId.equals("0")){
        if(deptcode.startsWith(deptoldcode)){
          data.setMessage(showJavaScript("alert('不能将父界面粘贴到子界面下！')"));
          return;
        }
      }

      deptcode = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('nodeInfo','nodeCode','"
        + deptcode +"','isdelete=0') from dual");
      dsNodeData.setValue("nodeCode",deptcode);
      dsNodeData.setValue("parentNodeId", pasteId);
      dsNodeData.saveChanges(new String[]{"CALL pck_base.updateChildCode('nodeInfo','nodeCode','"+deptoldcode+"','"+deptcode+"','isdelete=0')"});

      setDefaultOperate(Integer.parseInt(action));
      //同步树的数据
      dsNodeInfo.refresh();
      pathCode = deptcode;
    }
  }

  /**
   * 刷新树操作的触发类
   */
  class Node_Refresh implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      setDefaultOperate(Integer.parseInt(action));
    }
  }

  /**
   * 刷新树操作的触发类
   */
  class Node_OpenPrivilige implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String nodeID = data.getParameter("nodeID");
      if(nodeID == null || nodeID.trim().equals(""))
      {
        data.setMessage(showJavaScript("location.href='../blank.htm';"));
        return;
      }
      nodeId = nodeID;
      dsNodePrivilige.setQueryString(NODE_PRIVILIGE_SQL + nodeID.trim());
      if(dsNodePrivilige.isOpen())
        dsNodePrivilige.refresh();
      else
        dsNodePrivilige.open();
    }
  }

  /**
   * 添加或修改权限的触发类
   */
  class Node_Privilige_AddEdit implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      isAdd = action.equals(String.valueOf(ADD));
      if(!isAdd)
      {
        dsNodePrivilige.goToRow(Integer.parseInt(data.getParameter("rownum")));
        editrow = dsNodePrivilige.getInternalRow();
        rowInfo.put(dsNodePrivilige);
      }
      else
        rowInfo.clear();

      data.setMessage(showJavaScript("showFixedQuery();"));
    }
  }

  /**
   * 提交权限的触发类
   */
  class Node_Privilige_Post implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //校验数据
      rowInfo.put(data.getRequest());
      String priviligeId = rowInfo.get("priviligeId");
      if(!isAdd)
        dsNodePrivilige.goToInternalRow(editrow);
      if(isAdd)
      {
        dsNodePrivilige.insertRow(false);
        dsNodePrivilige.setValue("limitId", "-1");
        dsNodePrivilige.setValue("isDelete", "0");
      }
      dsNodePrivilige.setValue("priviligeId", priviligeId);
      dsNodePrivilige.setValue("nodeId", nodeId);
      dsNodePrivilige.post();
      try{
        dsNodePrivilige.saveChanges();
      }
      catch(Exception ex)
      {
        if(dsNodePrivilige.changesPending())
          dsNodePrivilige.reset();
        throw ex;
      }
      //data.setMessage(showJavaScript("parent.hideInterFrame();"));
    }
  }

  /**
   * 删除权限的触发类
   */
  class Node_Privilige_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      dsNodePrivilige.goToRow(Integer.parseInt(data.getParameter("rownum")));
      dsNodePrivilige.setValue("isdelete", "1");
      dsNodePrivilige.saveChanges();
      dsNodePrivilige.deleteRow();
      dsNodePrivilige.resetPendingStatus(true);
    }
  }
}
