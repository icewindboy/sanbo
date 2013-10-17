package engine.erp.baseinfo;

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
import engine.util.StringUtils;
import engine.common.LoginBean;
import engine.project.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;

/**
 * Title:        基础信息--类别信息维护
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author  江海岛
 * @version 1.0
 */

public final class B_ProductKind extends BaseAction implements Operate
{
  //各种操作的静态变量
  public static final int NODE_ADD_CHILD = 1000;//添加子结点
  public static final int NODE_EDIT_VIEW = 1001;//浏览或编辑类别结点
  public static final int NODE_PRODUCT   = 1002;//类别下的产品列表
  public static final int NODE_DELETE    = 1003;//删除产品类别结点
  public static final int NODE_PASTE     = 1004;//粘贴产品类别结点
  //与明细类别类别有关的操作
  public static final int NODE_EDIT_POST = 2002;//提交
  //大类属性页面
  public static final int NODE_PROPERTY = 3001;//

  private static final String PRODUCT_KIND_TREE_SQL
      = "SELECT wzlbid, bm, parentid, mc FROM kc_dmlb WHERE isdelete=0";
  private static final String PRODUCT_KIND_DATA_SQL
      = "SELECT wzlbid, bm, parentid, mc, cs, isdelete FROM kc_dmlb WHERE isdelete=0 AND wzlbid=";
  private static final String CODE_COUNT_SQL
      = "SELECT COUNT(*) FROM kc_dmlb WHERE isdelete=0 AND bm='?'";
  private static final String NAME_COUNT_SQL
      = "SELECT COUNT(*) FROM kc_dmlb WHERE mc='?' AND bm LIKE '?%' AND isdelete=0";
  private static final String CHILD_COUNT_SQL
      = "SELECT COUNT(*) FROM kc_dmlb WHERE isdelete=0 AND parentid=";
  private static final String PRODUCT_COUNT_SQL
      = "SELECT COUNT(*) FROM kc_dm WHERE isdelete=0 AND wzlbid=";
  private static final String PRODUCT_CODE_RULE
      = "SELECT segformat FROM jc_coderule_cont WHERE coderule='product'";

  private static final String NODEID    = "wzlbid";   //树节点ID字名称
  private static final String PARENTID  = "parentid"; //父节点ID字段
  private static final String NODECODE  = "bm";       //树节点编码字段
  private static final String NODENAME  = "mc";       //树节点名称字段
  private static final String NODELEVEL = "cs";       //树节点的层数

  private EngineDataSet dsProductKindTree = new EngineDataSet();//保存类别树状信息的数据集
  public  EngineDataSet dsProductKindData = new EngineDataSet();//保存需要修改记录的数据集

  private HtmlTree productKindTree = null;     //保存打印物资类别树网页的对象
  private int[]   segments = null;             //各个树层的编码长度
  private String[] segtypes = null;        //各个树层的编码类型

  private int     tempOperate = NODE_EDIT_VIEW;//临时保存上次的操作类型
  private int     nodelevel;                   //保存当前结点的层数
  private String  parentId    = null;          //保存父结点ID
  private String  nodeId      = null;          //保存当前结点
  private String  pathCode    = null;          //保存树展开的路径
  public  boolean isAdd       = true;          //是否在添加状态

  public RowMap   rowInfo     = new RowMap();
  /**
   * 得到权限维护的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回权限维护的实例
   */
  public static B_ProductKind getInstance(HttpServletRequest request)
  {
    B_ProductKind productKindBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "productKindBean";
      productKindBean = (B_ProductKind)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(productKindBean == null)
      {
        productKindBean = new B_ProductKind();
        session.setAttribute(beanName, productKindBean);
      }
    }
    return productKindBean;
  }


  private B_ProductKind() {
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
   * Session失效时，调用的函数
   */
  public void valueUnbound(HttpSessionBindingEvent event)
  {
    dsProductKindTree.close();
    dsProductKindTree = null;
    dsProductKindData.close();
    dsProductKindData = null;
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private void jbInit() throws Exception {
    setDataSetProperty(dsProductKindTree, PRODUCT_KIND_TREE_SQL);
    setDataSetProperty(dsProductKindData, null);

    dsProductKindTree.setSort(new SortDescriptor("", new String[]{"bm"}, new boolean[]{false}, null, 0));
    dsProductKindData.setSequence(new SequenceDescriptor(new String[]{"wzlbid"}, new String[]{"s_kc_dmlb"}));
    //
    NodeChild_Add_Edit nodeChild_Add_Edit = new NodeChild_Add_Edit();
    //添加 编辑的执行者
    addObactioner(String.valueOf(NODE_EDIT_VIEW), nodeChild_Add_Edit);
    //添加 新增子节点的执行者
    addObactioner(String.valueOf(NODE_ADD_CHILD), nodeChild_Add_Edit);
    //添加 节点提交的执行者
    addObactioner(String.valueOf(NODE_EDIT_POST), new Node_Post());
    //添加 节点删除的执行者
    addObactioner(String.valueOf(NODE_DELETE), new Node_Delete());
    //添加 节点粘贴的执行者
    addObactioner(String.valueOf(NODE_PASTE), new Node_Paste());
    //初始化
    addObactioner(String.valueOf(INIT), new Init());
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
   * 得到的操作
   * @return 返回上次保留的操作
   */
  public int getDefaultOperate()
  {
    if(tempOperate < NODE_ADD_CHILD || tempOperate > NODE_PRODUCT || tempOperate != NODE_PROPERTY)
      tempOperate = NODE_EDIT_VIEW;
    return tempOperate;
  }

  /**
   * 得到产品类别树的HTML文本的字符串
   * @return 返回产品类别树的HTML字符串
   */
  public String getProductKindTreeHTML()
  {
    if(!dsProductKindTree.isOpen())
      dsProductKindTree.open();

    if(productKindTree == null)
    {
      productKindTree = new HtmlTree();
      productKindTree.setTreeProperty(dsProductKindTree, "物资类别", "wzlbid", "bm", "parentid", "mc");
    }
    return productKindTree.printHtmlTree(pathCode);
  }

  /**
   * 得到产品类别树的HTML文本的字符串
   * @return 返回产品类别树的HTML字符串
   */
  public String getStaticProductKindTreeHTML()
  {
    EngineDataSet ds = LookupBeanFacade.getLookupDataSet(SysConstant.BEAN_PRODUCT_KIND);
    if(ds == null)
      ds = dsProductKindTree;
    synchronized(ds)
    {
      if(!ds.isOpen())
        ds.open();
      if(productKindTree == null)
      {
        productKindTree = new HtmlTree();
        productKindTree.setTreeProperty(ds, "物资类别", "wzlbid", "bm", "parentid", "mc");
      }
    }
    return productKindTree.printHtmlTree(null);
  }

  /**
   * 得到当前层的长度
   * @return 得到当前层的长度
   */
  public int getNodeLevelLength()
  {
    return segments[nodelevel];
  }
  //------------------------------------------
  //操作实现的类
  //------------------------------------------
  /**
   * 初始化操作的触发类
   */
  final class Init implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      //得到产品结构的各个层数的编码长度
      String segformat = dataSetProvider.getSequence(PRODUCT_CODE_RULE);
      String[][] segs = StringUtils.getArrays(segformat, ",", "$");
      segtypes = segs[0];
      segments = new int[segs[1].length];
      for(int i=0; i<segs[1].length; i++)
        segments[i] = Integer.parseInt(segs[1][i]);
      //重新打开树数据
      dsProductKindTree.setQueryString(PRODUCT_KIND_TREE_SQL);
      if(dsProductKindTree.isOpen())
        dsProductKindTree.refresh();
      else
        dsProductKindTree.open();
    }
  }
  /**
   * 添加子节点或查看编辑记录节点操作的触发类
   */
  final class NodeChild_Add_Edit implements Obactioner
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
      String tempNodeID = data.getParameter("nodeID");
      if(tempNodeID == null || tempNodeID.trim().equals(""))
      {
        data.setMessage(showJavaScript("location.href='../blank.htm';"));
        return;
      }
      tempNodeID = tempNodeID.trim();

      isAdd = action.equals(String.valueOf(NODE_ADD_CHILD));
      /*查找记录， 提取当前操作的节点的数据*/
      dsProductKindData.setQueryString(PRODUCT_KIND_DATA_SQL + tempNodeID);
      if(dsProductKindData.isOpen())
        dsProductKindData.refresh();
      else
        dsProductKindData.open();

      if(dsProductKindData.getRowCount() < 1)//没有记录,不是根结点
      {
        if(!isAdd || !tempNodeID.equals("0"))
        {
          data.setMessage(showJavaScript("alert('没有该条记录, 可能已经被别的用户更改！'); location.href='../blank.htm';"));
          return;
        }
      }
      else if(isAdd && dsProductKindData.getBigDecimal(NODELEVEL).intValue()+1 >= segments.length)
      {
        data.setMessage(showJavaScript("alert('已经到定义的最大层数, 不能添加子类别！'); location.href='../blank.htm';"));
        parentId = null;
        return;
      }

      if(isAdd)
        parentId = tempNodeID;
      else
        nodeId = tempNodeID;

      initRowInfo(true, isAdd, true);
    }

    /**
     * 初始化列信息
     * @param isAdd 是否时添加
     * @param isInit 是否从新初始化
     * @throws java.lang.Exception 异常
     */
    private final void initRowInfo(boolean isMaster, boolean isAdd, boolean isInit) throws java.lang.Exception
    {
      if(isInit && rowInfo.size() > 0)
        rowInfo.clear();

      int level;
      String parentCode = null;
      if(isAdd){
        boolean isFirstNode = parentId.equals("0");
        parentCode = isFirstNode ? "" : dsProductKindData.getValue("bm");
        level = isFirstNode ? 0 : dsProductKindData.getBigDecimal(NODELEVEL).intValue()+1;
        rowInfo.put("parentCode", parentCode);
        String self = segtypes[level].length()>0 && !segtypes[level].equals("n") ? "" :
                      dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('kc_dmlb','bm','"
                      + parentCode +"','isdelete=0',"+ segments[level] +",0) from dual");
        rowInfo.put("self_code", self);
        rowInfo.put(NODELEVEL, String.valueOf(level));
      }
      else{
        level = dsProductKindData.getBigDecimal(NODELEVEL).intValue();
        rowInfo.put(dsProductKindData);
        parentCode = dsProductKindData.getValue("bm");
        int length = parentCode.length() - segments[level];
        rowInfo.put("parentCode", parentCode.substring(0, length));
        rowInfo.put("self_code", parentCode.substring(length));
      }
      nodelevel = level;
    }
  }

  /**
   * 节点提交操作的触发类
   */
  final class Node_Post implements Obactioner
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
      if(isAdd){
        if(parentId == null || parentId.length() ==0){
          data.setMessage(showJavaScript("alert('请选择要添加下级的类别！'); location.href='../blank.htm';"));
          return;
        }
      }
      else
      {
        if(nodeId == null || nodeId.length() == 0){
          data.setMessage(showJavaScript("alert('请选择需要修改的类别！'); location.href='../blank.htm';"));
          return;
        }
      }
      rowInfo.put(data.getRequest());
      String self_code = rowInfo.get("self_code");
      if(self_code.length() != segments[nodelevel])
      {
        data.setMessage(showJavaScript("alert('编码的长度不够！');"));
        return;
      }
      String type = segtypes[nodelevel];
      //类型是数字型或空串
      if(type.length()==0 || type.equals("n")){
        String temp = null;
        if((temp = checkInt(self_code, "编码")) != null)
        {
          data.setMessage(temp);
          return;
        }
      }
      else if(type.equals("uc")){
        self_code = self_code.toUpperCase();
        rowInfo.put("self_code", self_code);
      }
      else if(type.equals("lc"))
      {
        self_code = self_code.toLowerCase();
        rowInfo.put("self_code", self_code);
      }
      //判断编码是否已经存在
      String count = null;
      String newcode = null, oldcode = null, oldname = null;
      String parentCode = rowInfo.get("parentCode");
      newcode = parentCode + self_code;
      String newname = rowInfo.get(NODENAME);
      //提取旧编码和名称
      if(!isAdd){
        oldcode = dsProductKindData.getValue(NODECODE);
        oldname = dsProductKindData.getValue(NODENAME);
      }
      //添加状态或修改状态旧编码不等于新编码
      if(isAdd || !oldcode.equals(newcode)){
        count = dataSetProvider.getSequence(combineSQL(CODE_COUNT_SQL, "?", new String[]{newcode}));
        if(count != null && !count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('该编码已经存在!');"));
          return;
        }
      }
      if(isAdd || !oldname.equals(newname)){
        count = dataSetProvider.getSequence(combineSQL(NAME_COUNT_SQL, "?", new String[]{newname, parentCode}));
        if(count != null && !count.equals("0"))
        {
          data.setMessage(showJavaScript("alert('该名称已经存在!');"));
          return;
        }
      }
      //设置数据
      if(isAdd)
      {
        dsProductKindData.empty();
        dsProductKindData.insertRow(false);
        dsProductKindData.setValue(NODEID, "-1");
        dsProductKindData.setValue(PARENTID, parentId);
        dsProductKindData.setValue(NODELEVEL, rowInfo.get(NODELEVEL));
      }
      dsProductKindData.setValue(NODECODE, newcode);
      dsProductKindData.setValue(NODENAME, newname);
      dsProductKindData.setValue("isdelete", "0");
      dsProductKindData.post();
      //同步树的记录
      if(isAdd)
      {
        dsProductKindData.saveChanges();
        //将状态改变修改状态
        isAdd = false;
        nodeId = dsProductKindData.getValue(NODEID);
        //更新树数据
        dsProductKindTree.insertRow(false);
        dsProductKindTree.setValue(NODEID, dsProductKindData.getValue(NODEID));
        dsProductKindTree.setValue(PARENTID, dsProductKindData.getValue(PARENTID));
        dsProductKindTree.setValue(NODECODE, dsProductKindData.getValue(NODECODE));
        dsProductKindTree.setValue(NODENAME, dsProductKindData.getValue(NODENAME));
        dsProductKindTree.post();
      }
      //更改类别编码
      else if(!oldcode.equals(newcode))
      {
        //更新期下子节点和产品的所有编码
        dsProductKindData.saveChanges(new String[]{
          "CALL pck_base.updateChildCode('kc_dmlb','bm','"+oldcode+"','"+newcode+"','isdelete=0')",
          "CALL pck_base.updateChildCode('kc_dm','cpbm','"+oldcode+"','"+newcode+"','isdelete=0')"
        });
        dsProductKindTree.refresh();
      }
      else
      {
        dsProductKindData.saveChanges();
        if(locateDataSet(dsProductKindTree, NODEID, nodeId, Locate.FIRST))
        {
          dsProductKindTree.setValue(NODECODE, dsProductKindData.getValue(NODECODE));
          dsProductKindTree.setValue(NODENAME, dsProductKindData.getValue(NODENAME));
          dsProductKindTree.post();
        }
      }
      //
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCT_KIND);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCT_FIRST_KIND);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCT_FIRST_KIND_CODE);
      pathCode = newcode;
      data.setMessage(showJavaScript("submitTree();"));
    }
  }

  /**
   * 节点删除操作的触发类
   */
  final class Node_Delete implements Obactioner
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
        data.setMessage(showJavaScript("alert('请选择类别！');"));
        return;
      }
      if(!dataSetProvider.getSequence(CHILD_COUNT_SQL + deleteId).equals("0"))
      {
        data.setMessage(showJavaScript("alert('该类别下有子类别！');"));
        return;
      }
      else if(!dataSetProvider.getSequence(PRODUCT_COUNT_SQL + deleteId).equals("0"))
      {
        data.setMessage(showJavaScript("alert('该类别下有物资！');"));
        return;
      }
      /*查找记录*/
      dsProductKindData.setQueryString(PRODUCT_KIND_DATA_SQL+ deleteId);
      if(dsProductKindData.isOpen())
        dsProductKindData.refresh();
      else
        dsProductKindData.open();

      if(dsProductKindData.getRowCount() < 1)
      {
        data.setMessage(showJavaScript("alert('没有该条记录, 可能已经被别的用户更改！');"));
        return;
      }

      tempOperate = Integer.parseInt(action);

      dsProductKindData.setValue("isdelete", "1");
      dsProductKindData.post();
      dsProductKindData.saveChanges();
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCT_KIND);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCT_FIRST_KIND);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCT_FIRST_KIND_CODE);
      //同步树的数据
      if(locateDataSet(dsProductKindTree, NODEID, deleteId, Locate.FIRST))
        dsProductKindTree.deleteRow();

      dsProductKindTree.prior();
      pathCode = dsProductKindTree.getValue(NODECODE);
    }
  }

  /**
   * 节点粘贴操作的触发类
   */
  final class Node_Paste implements Obactioner
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
        data.setMessage(showJavaScript("alert('请选择需要剪切类别！');"));
        return;
      }
      if(pasteId == null || pasteId.trim().equals(""))
      {
        data.setMessage(showJavaScript("alert('请选择粘贴的目标类别！');"));
        return;
      }
      cutId = cutId.trim();
      pasteId = pasteId.trim();
      /*查找记录*/
      int level = 0; //粘贴的下一层
      String newcode = null, oldcode = null;
      if(!pasteId.equals("0")){//是否是根节点
        dsProductKindData.setQueryString(PRODUCT_KIND_DATA_SQL+ pasteId);
        if(dsProductKindData.isOpen())
          dsProductKindData.refresh();
        else
          dsProductKindData.open();
        if(dsProductKindData.getRowCount() < 1)
        {
          data.setMessage(showJavaScript("alert('没有粘贴类别的记录, 可能已经被别的用户更改！');"));
          return;
        }
        newcode = dsProductKindData.getValue(NODECODE);
        level = dsProductKindData.getBigDecimal(NODELEVEL).intValue()+1;
      }
      else
        newcode = "";

      dsProductKindData.setQueryString(PRODUCT_KIND_DATA_SQL+ cutId);
      if(dsProductKindData.isOpen())
        dsProductKindData.refresh();
      else
        dsProductKindData.open();
      if(dsProductKindData.getRowCount() < 1)
      {
        data.setMessage(showJavaScript("alert('没有剪切类别的记录, 可能已经被别的用户更改！');"));
        return;
      }

      oldcode = dsProductKindData.getValue(NODECODE);//剪切类别的编号
      if(!pasteId.equals("0") && newcode.startsWith(oldcode)){
        data.setMessage(showJavaScript("alert('不能将父类别粘贴到子类别下！')"));
        return;
      }

      int maxlevel = newcode.length() + segments[level];
      newcode = dataSetProvider.getSequence("SELECT pck_base.fieldNextCode('kc_dmlb','bm','"
        + newcode +"','isdelete=0',"+ segments[level] +") from dual");
      if(newcode.length() > maxlevel){
        data.setMessage(showJavaScript("alert('已经到最大编码！')"));
        return;
      }

      dsProductKindData.setValue(NODECODE, newcode);
      dsProductKindData.setValue(PARENTID, pasteId);
      dsProductKindData.setValue(NODELEVEL, String.valueOf(level));
      dsProductKindData.post();
      dsProductKindData.saveChanges(new String[]{
          "CALL pck_base.updateChildCode('kc_dmlb','bm','"+oldcode+"','"+newcode+"','isdelete=0')",
          "CALL pck_base.updateChildCode('kc_dm','cpbm','"+oldcode+"','"+newcode+"','isdelete=0')"
      });
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCT_KIND);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCT_FIRST_KIND);
      LookupBeanFacade.refreshLookup(SysConstant.BEAN_PRODUCT_FIRST_KIND_CODE);

      tempOperate = Integer.parseInt(action);
      //同步树的数据
      dsProductKindTree.refresh();
      pathCode = newcode;
    }
  }
}