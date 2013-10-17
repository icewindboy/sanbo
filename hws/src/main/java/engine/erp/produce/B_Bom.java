package engine.erp.produce;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import com.borland.dx.dataset.Locate;
import com.borland.dx.dataset.MasterLinkDescriptor;
import com.borland.dx.dataset.SortDescriptor;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.common.LoginBean;
import engine.dataset.DataSetUtils;
import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.RowMap;
import engine.erp.baseinfo.HtmlTree;
import engine.project.LookUp;
import engine.project.LookupBeanFacade;
import engine.project.SysConstant;
import engine.util.StringUtils;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;

/**
 * Title:        基础信息--类别信息维护
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author  江海岛
 * @version 1.0
 */

public final class B_Bom extends BaseAction implements Operate
{
  //各种操作的静态变量
  public static final int NODE_ADD_CHILD = 1001;//添加子结点
  public static final int NODE_EDIT_VIEW = 1002;//浏览或编辑类别结点
  public static final int NODE_DELETE    = 1003;//删除产品类别结点
  public static final int NODE_EXPAND    = 1004;//展开节点
  //与明细类别类别有关的操作
  public static final int NODE_EDIT_POST = 2001;//提交
  public static final int NODE_PASTE     = 2002;//粘贴产品类别结点
  public static final int NODE_REFRESH   = 2003;//刷新
  //
  public static final String CHILD_SECTION_INI  = "3001";//BOM工段初始化
  public static final String CHILD_SECTION_ADD  = "3002";//BOM工段添加
  public static final String CHILD_SECTION_POST = "3003";//BOM工段保存到内存
  public static final String CHILD_SECTION_DEL  = "3004";//BOM工段删除

  private static final String BOM_FIRST_NODE_SQL = "{CALL PCK_PRODUCE.getBomTreeData(?,0)}";//提取顶节点的SQL语句
  private static final String BOM_TREE_SQL = "{CALL PCK_PRODUCE.getBomTreeData(?,@)}";//BOM树的SQL
  private static final String NODE_CAPTION_SQL = "SELECT PCK_PRODUCE.getBomNodeCaption(@) FROM dual";//得到节点的显示标题
  private static final String NODE_FATHERPATH_SQL = "SELECT PCK_PRODUCE.getBomNodePath(@) FROM dual";//父节点的路径，用逗号分割每个父节点ID
  private static final String BOM_DATA_SQL = "SELECT * FROM vw_sc_bom WHERE sjcpid=? ORDER BY cpbm";//BOM的一条记录
  private static final String BOM_DATA_STRUCT_SQL = "SELECT * FROM vw_sc_bom WHERE 1<>1";//BOM表的机构
  private static final String BOM_DATA_DEL_SQL
      = "SELECT bomid, cpid, sjcpid, sl, shl, zjlx, xgr, wgcl, '' cpbm FROM sc_bom WHERE sjcpid='?'";
  private static final String COPY_DATA_SQL = "{CALL PCK_PRODUCE.getBomCopyData(?,@,@)}";//复值配料
  //产品更新总提前期
  private static final String BOM_TOTAL_TIME_SQL = "{CALL PCK_PRODUCE.updateBomProduceTime(@)}";
  //private static final String CHILD_COUNT_SQL = "SELECT COUNT(*) FROM sc_bom WHERE sjcpid=";
  //BOM领料工段
  private static final String BOM_SECTION_SQL = "SELECT * FROM sc_bomsection WHERE bomID IN(@)";
  //BOM领料工段结构
  private static final String BOM_SECTION_STRUCT_SQL = "SELECT * FROM sc_bomsection WHERE 1<>1";

  private static final String NODEID    = "cpid";   //树节点ID字名称
  private static final String PARENTID  = "sjcpid"; //父节点ID字段
  private static final String NODECODE  = null;     //树节点编码字段
  private static final String NODENAME  = "mc";     //树节点名称字段
  private static final String CHILDCOUNT= "childcount"; //子节点记录数量
  //private static final String NODELEVEL = "";       //树节点的层次

  private EngineDataSet dsBomTree = new EngineDataSet();//保存BOM树状信息的数据集
  public  EngineDataSet dsBomData = new EngineDataSet();//保存需要修改记录的数据集
  public  EngineDataSet dsBomSection = new EngineDataSet();//保留BOM的领料工段
  private EngineDataSet dsTempTree = null;//临时保存树的数据
  private EngineDataSet dsTempPaste = null;//保存临时数据

  private HtmlTree bomTree = null;     //保存打印物资类别树网页的对象

  private int     tempOperate = NODE_EDIT_VIEW;//临时保存上次的操作类型
  //private int     nodelevel;                 //保存当前结点的层数
  private String  parentId    = null;          //保存父结点ID
  //private String  nodeId      = null;        //保存当前结点
  private String  pathCode    = null;          //保存树展开的路径
  private List    pathList    = null;          //保存节点的各个父节点的ID
  public  boolean isAdd       = true;          //是否在添加状态

  public  ArrayList   d_RowInfos   = null;

  public  String loginId = "";   //登录员工的ID
  public  String loginCode = ""; //登陆员工的编码
  public  String loginName = ""; //登录员工的姓名
  private String qtyFormat = null, priceFormat = null, sumFormat = null;
  private int s_bomid = 0;

  private LookUp prodBean  = null;//产品信息BEAN
  /**
   * 得到权限维护的实例
   * @param request jsp请求
   * @param isApproveStat 是否在审批状态
   * @return 返回权限维护的实例
   */
  public static B_Bom getInstance(HttpServletRequest request)
  {
    B_Bom bomBean = null;
    HttpSession session = request.getSession(true);
    synchronized (session)
    {
      String beanName = "bomBean";
      bomBean = (B_Bom)session.getAttribute(beanName);
      //判断该session是否有该bean的实例
      if(bomBean == null)
      {
        //引用LoginBean
        LoginBean loginBean = LoginBean.getInstance(request);

        bomBean = new B_Bom();
        bomBean.qtyFormat = loginBean.getQtyFormat();
        bomBean.priceFormat = loginBean.getPriceFormat();
        bomBean.sumFormat = loginBean.getSumFormat();

        //saleOrderBean.fgsid = loginBean.getFirstDeptID();
        bomBean.loginId = loginBean.getUserID();
        bomBean.loginName = loginBean.getUserName();

        bomBean.dsBomData.setColumnFormat("sl", bomBean.qtyFormat);
        session.setAttribute(beanName, bomBean);
      }
    }
    return bomBean;
  }


  private B_Bom() {
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
    if(dsBomTree != null){
      dsBomTree.close();
      dsBomTree = null;
    }
    if(dsBomData != null){
      dsBomData.close();
      dsBomData = null;
    }
    if(dsTempPaste != null) {
      dsTempPaste.close();
      dsTempPaste = null;
    }
    if(dsTempTree != null){
      dsTempTree.close();
      dsTempTree = null;
    }
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private void jbInit() throws Exception {
    setDataSetProperty(dsBomTree, null);
    setDataSetProperty(dsBomData, null);
    setDataSetProperty(dsBomSection, BOM_SECTION_STRUCT_SQL);

    dsBomTree.setSort(new SortDescriptor("", new String[]{"sjcpid", "mc"}, new boolean[]{false, false}, null, 0));
    //dsBomData.setSort(new SortDescriptor("", new String[]{"cpbm"}, new boolean[]{false}, null, 0));
    dsBomData.setTableName("sc_bom");
    //dsBomData.setSequence(new SequenceDescriptor(new String[]{"bomid"}, new String[]{"s_sc_bom"}));
    //设置主从关系，设置级联更新，级联删除
    dsBomSection.setMasterLink(new MasterLinkDescriptor(dsBomData, new String[]{"bomid"},
        new String[]{"bomid"}, false, true, true));
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
    //添加子件
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    //删除子件
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    //展开节点
    addObactioner(String.valueOf(NODE_EXPAND), new Node_Expand());
    //刷行节点
    addObactioner(String.valueOf(NODE_REFRESH), new Node_Refresh());
    //初始化
    addObactioner(String.valueOf(INIT), new Init());
    //BOM工序
    addObactioner(CHILD_SECTION_INI, new ChildSectionInit());
    addObactioner(CHILD_SECTION_ADD, new ChildSectionAdd());
    addObactioner(CHILD_SECTION_DEL, new ChildSectionDel());
    addObactioner(CHILD_SECTION_POST, new ChildSectionPost());
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
   * 得到产品类别树的HTML文本的字符串
   * @return 返回产品类别树的HTML字符串
   */
  public String getBomTreeHTML()
  {
    if(!dsBomTree.isOpen())
      dsBomTree.open();
    if(bomTree == null)
    {
      bomTree = new HtmlTree();
      //bomTree.setExpandNodeFuction("NodeExpand(?)");
      bomTree.setTreeProperty(dsBomTree, "物资清单(BOM)表", NODEID, NODECODE, PARENTID, NODENAME, null, true, CHILDCOUNT);
    }
    return bomTree.printHtmlTree(pathCode);
  }

  /**
   * 从表保存操作
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

    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//产品id
      //detailRow.put("gymcid", rowInfo.get("gymcid_"+i));//工艺id
      detailRow.put("cpbm", rowInfo.get("cpbm_"+i));//
      detailRow.put("sl", formatNumber(rowInfo.get("sl_"+i), qtyFormat));//
      detailRow.put("shl", rowInfo.get("shl_"+i));//损耗率
      detailRow.put("zjlx", rowInfo.get("zjlx_"+i));//子件类型
      //保存用户自定义的字段
      /*FieldInfo[] fields = detailProducer.getBakFieldCodes();
      for(int j=0; j<fields.length; j++)
      {
        String fieldCode = fields[j].getFieldcode();
        detailRow.put(fieldCode, rowInfo.get(fieldCode + "_" + i));
      }*/
    }
  }

  /**
   * 查找节点的所有父节点ID(用于递归算法)
   */
  private void lookUpPath(List pathList, String nodeid) throws Exception
  {
    String SQL = combineSQL(NODE_FATHERPATH_SQL, "@", new String[]{nodeid});
    String path = dataSetProvider.getSequence(SQL);
    if(path == null)
      return;
    String[] paths = parseString(path, ",");
    for(int i=0; i<paths.length; i++)
      pathList.add(paths[i]);
    /*层数
    int level = 0;
    EngineRow row = new EngineRow(dsBomTree, NODEID);
    row.setValue(0, nodeid);
    while(dsBomTree.locate(row, Locate.FIRST))
    {
      if(++level > 20)//最大20层
        break;
      String tempId = dsBomTree.getValue(PARENTID);
      if(tempId.equals("0"))//表示到根节点了
        break;

      pathList.add(tempId);
      row.setValue(0, tempId);
    }*/
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
      //重新打开树数据
      dsBomTree.setQueryString(BOM_FIRST_NODE_SQL);
      

      if(dsBomTree.isOpen())
        dsBomTree.refresh();
      else
        dsBomTree.open();
      pathCode = null;
      s_bomid = 0;
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
      String tempNodeID = data.getParameter("nodeID").replaceAll(",", "");
      if(tempNodeID == null || tempNodeID.trim().equals(""))
      {
        data.setMessage(showJavaScript("location.href='../blank.htm';"));
        return;
      }
      tempNodeID = tempNodeID.trim();

      isAdd = action.equals(String.valueOf(NODE_ADD_CHILD));
      /*查找记录， 提取当前操作的节点的数据*/
      if(isAdd){
        if(dsBomData.isOpen())
          dsBomData.empty();
        else
        {
          dsBomData.setQueryString(BOM_DATA_STRUCT_SQL);
          dsBomData.openDataSet();
        }
        //
        if(dsBomSection.isOpen())
          dsBomSection.empty();
        else
          dsBomSection.openDataSet();
      }
      else{
        String sql = StringUtils.combine(BOM_DATA_SQL, "?", new String[]{tempNodeID});
        dsBomData.setQueryString(sql);
        if(dsBomData.isOpen())
          dsBomData.refresh();
        else
          dsBomData.openDataSet();
        //
        String[] bomids = DataSetUtils.rowsToArray(dsBomData, "bomid", false, false);
        sql = StringUtils.getArrayValue(bomids, ",");
        if(sql.length() == 0)
          sql = "-100";
        sql = StringUtils.combine(BOM_SECTION_SQL, "@", new String[]{sql});
        dsBomSection.setQueryString(sql);
        if(dsBomSection.isOpen())
          dsBomSection.refresh();
        else
          dsBomSection.openDataSet();
      }
      parentId = isAdd ? "" : tempNodeID;

      initRowInfo(isAdd, true);
    }
  }

  /**
   * 初始化列信息
   * @param isAdd 是否时添加
   * @param isInit 是否从新初始化
   * @throws java.lang.Exception 异常
   */
  private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception
  {
    EngineDataSet dsDetail = dsBomData;
    if(d_RowInfos == null)
      d_RowInfos = new ArrayList(dsDetail.getRowCount());
    else if(isInit)
      d_RowInfos.clear();

    dsDetail.first();
    for(int i=0; i<dsDetail.getRowCount(); i++)
    {
      RowMap row = new RowMap(dsDetail);
      d_RowInfos.add(row);
      dsDetail.next();
    }
  }


  //
  private ArrayList sectionRows = null;
  private String bomId = "";

  public ArrayList getSectionRows(){
    return sectionRows;
  }

  /**
   * 初始化工序分段行信息
   */
  private final void initSectionRowInfo()
  {
    EngineDataSet dsDetail = dsBomSection;
    if(sectionRows == null)
      sectionRows = new ArrayList(dsDetail.getRowCount());
    else //if(isInit)
      sectionRows.clear();

    dsDetail.first();
    for(int i=0; i<dsDetail.getRowCount(); i++)
    {
      RowMap row = new RowMap(dsDetail);
      sectionRows.add(row);
      dsDetail.next();
    }
  }

  /**
   * BOM子件领料工段初始化
   */
  final class ChildSectionInit implements Obactioner
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
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      dsBomData.goToRow(rownum);
      bomId = dsBomData.getValue("bomid");
      initSectionRowInfo();
    }
  }

  /**
   * BOM子件领料工段添加
   */
  final class ChildSectionAdd implements Obactioner
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
      putSectionInfo(data);
      RowMap row = new RowMap();
      row.put("bomid", bomId);
      sectionRows.add(row);
    }
  }

  /**
   * BOM子件领料工段添加
   */
  final class ChildSectionDel implements Obactioner
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
      putSectionInfo(data);
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      sectionRows.remove(rownum);
    }
  }

  /**
   * BOM子件领料工段数据保存到内存
   */
  final class ChildSectionPost implements Obactioner
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
      putSectionInfo(data);
      //校验表单数据
      String temp = checkInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      dsBomSection.deleteAllRows();
      for(int i=0; i<sectionRows.size(); i++)
      {
        RowMap row = (RowMap)sectionRows.get(i);
        dsBomSection.insertRow(false);
        dsBomSection.setValue("bomid", row.get("bomid"));
        dsBomSection.setValue("gxfdid", row.get("gxfdid"));
        dsBomSection.post();
      }
      data.setMessage(showJavaScript("hide();"));
    }

    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkInfo() throws Exception
    {
      String temp = null;
      RowMap detailrow = null;
      ArrayList gxfdids = new ArrayList();
      for(int i=0; i<sectionRows.size(); i++)
      {
        detailrow = (RowMap)sectionRows.get(i);
        String gxfdid = detailrow.get("gxfdid");
        if(gxfdid.length() == 0)
          return showJavaScript("alert('第"+ (i+1) + "行不能为空！')");
        if(gxfdids.contains(gxfdid))
          return showJavaScript("alert('第"+ (i+1) + "行与前面行重复！')");
        gxfdids.add(gxfdid);
      }
      return null;
    }
  }

  /**
   * 从表保存操作
   * @param request 网页的请求对象
   * @param response 网页的响应对象
   * @return 返回HTML或javascipt的语句
   * @throws Exception 异常
   */
  private final void putSectionInfo(RunData data)
  {
    int rownum = sectionRows.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)sectionRows.get(i);
      detailRow.put("gxfdid", data.getParameter("gxfdid_"+i));//产品id
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
      if(!isAdd)
      {
        if(parentId == null || parentId.length() == 0){
          data.setMessage(showJavaScript("alert('请选择需要修改的物料！'); location.href='../blank.htm';"));
          return;
        }
      }
      putDetailInfo(data.getRequest());
      if(isAdd)
      {
        parentId = data.getParameter(PARENTID);
        if(parentId == null)
          parentId = "";
        if(parentId.length() == 0) {
          data.setMessage(showJavaScript("alert('请选择父件！');"));
          return;
        }
        if(d_RowInfos.size() < 1)
        {
          data.setMessage(showJavaScript("alert('请输入配料明细！')"));
          return;
        }
      }
      //校验表单数据
      String temp = checkDetailInfo();
      if(temp != null)
      {
        data.setMessage(temp);
        return;
      }
      //设置数据
      RowMap detailrow = null;
      dsBomData.first();
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        if(isAdd)
          dsBomData.setValue(PARENTID, parentId);

        if(dsBomData.getBigDecimal("bomid").intValue() < 0)
          dsBomData.setValue("bomid", dataSetProvider.getSequence("s_sc_bom"));

        dsBomData.setValue(NODEID, detailrow.get(NODEID));
        dsBomData.setValue("sl", detailrow.get("sl"));
        dsBomData.setValue("shl", detailrow.get("shl"));
        dsBomData.setValue("wgcl", detailrow.get("wgcl"));
        dsBomData.setValue("zjlx", detailrow.get("zjlx"));
        //dsBomData.setValue("gymcid", detailrow.get("gymcid"));
        dsBomData.setValue("cpbm",  detailrow.get("cpbm"));
        dsBomData.setValue("xgr", loginName);
        dsBomData.post();
        dsBomData.next();
      }
      dsBomData.getColumn("cpbm").setResolvable(false);
      String totalSQL = combineSQL(BOM_TOTAL_TIME_SQL, "@", new String[]{parentId});
      dsBomData.setAfterResolvedSQL(new String[]{totalSQL});
      dsBomData.saveDataSets(new EngineDataSet[]{dsBomData, dsBomSection});
      /*
      dsBomData.first();
      for(int i=0; i<dsBomData.getRowCount(); i++){
        System.out.println("cpid:"+dsBomData.getValue("cpid")+"sjcpid:"+dsBomData.getValue("sjcpid"));
        dsBomData.next();
      }*/
      //同步树的记录
      EngineRow firstrow = new EngineRow(dsBomTree, new String[]{NODEID, PARENTID});
      firstrow.setValue(0, parentId);
      firstrow.setValue(1, "0");
      if(isAdd)
      {
        //将状态改变修改状态
        isAdd = false;
        //更新树的第一节点数据
        if(!dsBomTree.locate(firstrow, Locate.FIRST))
        {
          dsBomTree.insertRow(false);
          dsBomTree.setValue(NODEID, parentId);
          dsBomTree.setValue(PARENTID, "0");
          dsBomTree.setValue(CHILDCOUNT, "1");
          //从数据库中提取节点的显示的标题
          String SQL = combineSQL(NODE_CAPTION_SQL, "@", new String[]{parentId});
          String name = dataSetProvider.getSequence(SQL);
          dsBomTree.setValue(NODENAME, name);
          dsBomTree.post();
        }
        //展开树数据
        emptyChilds(parentId);
        expandChilds(parentId);
      }
      //修改时,将数据全部记录删除,此时需要删除顶节点记录,在树上不显示
      else if(dsBomData.getRowCount() == 0)
      {
        emptyChilds(parentId);
        if(dsBomTree.locate(firstrow, Locate.FIRST))
          dsBomTree.emptyRow();
      }
      //提取所有子件
      else
      {
        emptyChilds(parentId);
        expandChilds(parentId);
      }

      data.setMessage(showJavaScript("submitTree();"));
    }

    /**
     * 校验从表表单信息从表输入的信息的正确性
     * @return null 表示没有信息
     */
    private String checkDetailInfo() throws Exception
    {
      ArrayList cpidList = d_RowInfos.size() > 0 ? new ArrayList() : null;
      ArrayList path     = d_RowInfos.size() > 0 ? new ArrayList() : null;
      String temp = null;
      RowMap detailrow = null;
      for(int i=0; i<d_RowInfos.size(); i++)
      {
        detailrow = (RowMap)d_RowInfos.get(i);
        String cpid = detailrow.get("cpid");
        if(cpid.equals(parentId))
          return showJavaScript("alert('配料单中子件物资不能与父件的物资相同');");
        //检查重复
        if(cpidList.contains(cpid))
          return showJavaScript("alert('配料单中子件物资重复(编码:"+detailrow.get("cpbm")+")')");
        else
          cpidList.add(cpid);
        //提取父件的路径信息
        lookUpPath(path, parentId);

        if(path.indexOf(cpid) > -1)
          return showJavaScript("alert('材料代码("+ detailrow.get("cpbm")+") 是非法子件！');");
        //
        String sl = detailrow.get("sl");
        if((temp = checkNumber(sl, "数量")) != null)
          return temp;
        String zk = detailrow.get("shl");
        if((temp = checkNumber(sl, "损耗率")) != null)
          return temp;
        String zjlx = detailrow.get("zjlx");
        if(zjlx.length() == 0)
          return showJavaScript("alert('请选择子件类型！')");
      }
      return null;
    }

    /**
     * 清空子件记录
     * @param parentId 父件ID
     * @throws Exception 异常
     */
    private void emptyChilds(String parentId)
    {
      EngineRow row = new EngineRow(dsBomTree, PARENTID);
      row.setValue(0, parentId);
      int locate = Locate.FIRST;
      dsBomTree.first();
      while(dsBomTree.locate(row, locate))
      {
        dsBomTree.emptyRow();
      }
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
      /*查找记录*/
      String sql = StringUtils.combine(BOM_DATA_DEL_SQL, "?", new String[]{deleteId});
      dsBomData.setQueryString(sql);
      if(dsBomData.isOpen())
        dsBomData.refresh();
      else
        dsBomData.openDataSet();

      dsBomData.deleteAllRows();
      dsBomData.getColumn("cpbm").setResolvable(false);
      dsBomData.saveDataSets(new EngineDataSet[]{dsBomData, dsBomSection});
      //dsBomData.saveChanges();
      //同步树的数据
      EngineRow row = new EngineRow(dsBomTree, new String[]{NODEID, PARENTID});
      row.setValue(0, deleteId);
      row.setValue(1, "0");
      if(dsBomTree.locate(row, Locate.FIRST))
        dsBomTree.deleteRow();

      //dsBomTree.first();
      pathCode = data.getParameter("pathcode");
    }
  }

  /**
   * 配料表添加物资
   */
  final class Detail_Add implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 配料表添加物资
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      HttpServletRequest req = data.getRequest();
      //保存输入的明细信息
      putDetailInfo(req);
      if(isAdd)
        parentId = data.getParameter(PARENTID, "");
      dsBomData.insertRow(false);
      dsBomData.setValue("bomid", String.valueOf(--s_bomid));
      dsBomData.setValue(PARENTID, parentId);
      dsBomData.post();
      //创建一个与用户相对应的行
      d_RowInfos.add(new RowMap(dsBomData));
      /*
      String multiIdInput = req.getParameter("multiIdInput");
      if(multiIdInput==null || multiIdInput.length() == 0)
        return;
      //实例化查找数据集的类
      EngineRow locateGoodsRow = new EngineRow(dsBomData, NODEID);
      String[] cpids = parseString(multiIdInput,",");
      LookUp productBean = getProductBean(req);
      if(cpids.length > 0)
        productBean.regData(cpids);
      for(int i=0; i < cpids.length; i++)
      {
        if(cpids[i].equals("-1"))
          continue;

        RowMap detailrow = null;
        locateGoodsRow.setValue(0, cpids[i]);
        if(!dsBomData.locate(locateGoodsRow, Locate.FIRST))
        {
          RowMap prodRow = productBean.getLookupRow(cpids[i]);
          dsBomData.insertRow(false);
          dsBomData.setValue("bomid", "-1");
          dsBomData.setValue(PARENTID, parentId);
          dsBomData.setValue(NODEID, cpids[i]);
          dsBomData.setValue("cpbm", prodRow.get("cpbm"));
          dsBomData.post();
          //创建一个与用户相对应的行
          detailrow = new RowMap(dsBomData);
          d_RowInfos.add(detailrow);
        }
      }
      */
    }
  }

  /**
   * 配料表删除物资
   */
  final class Detail_Delete implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      putDetailInfo(data.getRequest());
      EngineDataSet ds = dsBomData;
      int rownum = Integer.parseInt(data.getParameter("rownum"));
      //删除临时数组的一列数据
      d_RowInfos.remove(rownum);
      ds.goToRow(rownum);
      ds.deleteRow();
    }
  }
  /**
   * 配料表添加物资
   */
  final class Node_Expand implements Obactioner
  {
    //----Implementation of the Obactioner interface
    /**
     * 配料表添加物资
     * @parma  action 触发执行的参数（键值）
     * @param  o      触发者对象
     * @param  data   传递的信息的类
     * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
     */
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      String nodeId = data.getParameter("curNodeID");
      if(nodeId == null || nodeId.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请选择需要展开的物资！');"));
        return;
      }
      //展开树数据
      expandChilds(nodeId);

      pathCode = data.getParameter("pathcode");
    }
  }

  /**
   * 展开父件下的所有子件，即提取所有子件数据
   * @param SQL 提取所有子件数据的SQL语句
   * @throws Exception
   */
  private void expandChilds(String parentId) throws Exception
  {
    String SQL = combineSQL(BOM_TREE_SQL, "@", new String[]{parentId});
    if(dsTempTree == null)
    {
      dsTempTree = new EngineDataSet();
      setDataSetProperty(dsTempTree, null);
    }
    dsTempTree.setQueryString(SQL);
    if(dsTempTree.isOpen())
      dsTempTree.refresh();
    else
      dsTempTree.open();
    //增加提取的数据? bug: loadDataSet(), 居然会清空数据
    if(dsTempTree.getRowCount() < 1)
      return;
    EngineRow row = new EngineRow(dsBomTree);
    dsTempTree.first();
    for(int i=0; i<dsTempTree.getRowCount(); i++)
    {
//      dsTempTree.copyTo(row);
      dsBomTree.addRow(row);
      dsTempTree.next();
    }
    dsTempTree.empty();
  }
  /**
   * 节点刷新
   */
  final class Node_Refresh implements Obactioner
  {
    public void execute(String action, Obationable o, RunData data, Object arg) throws Exception
    {
      pathCode = data.getParameter("pathcode");
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
      String copyId = data.getParameter("cutNodeID");
      String pasteId = data.getParameter("curNodeID");
      if(copyId == null || copyId.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请选择需要复制物资！');"));
        return;
      }
      if(pasteId == null || pasteId.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请选择粘贴的目标物资！');"));
        return;
      }
      /*判断合理性
      if(!pasteId.equals("0"))
      {
        ArrayList path = new ArrayList();
        lookUpPath(path, pasteId);
        if(path.indexOf(copyId) > -1)
        {
          data.setMessage(showJavaScript("alert('不能将父件粘贴到子件下！')"));
          return;
        }
      }*/
      /*查找记录*/
      parentId = pasteId.equals("0") ? "" : pasteId;
      if(dsTempPaste == null)
      {
        dsTempPaste = new EngineDataSet();
        setDataSetProperty(dsTempPaste, null);
      }
      String SQL = combineSQL(COPY_DATA_SQL, "@", new String[]{copyId, pasteId});
      dsTempPaste.setQueryString(SQL);
      if(dsTempPaste.isOpen())
        dsTempPaste.refresh();
      else
        dsTempPaste.open();
      //是否是根节点
      String sql = pasteId.equals("0") ? BOM_DATA_STRUCT_SQL :
                   StringUtils.combine(BOM_DATA_SQL, "?", new String[]{pasteId});
      dsBomData.setQueryString(sql);
      if(dsBomData.isOpen())
        dsBomData.refresh();
      else
        dsBomData.openDataSet();

      EngineRow row = new EngineRow(dsBomData, NODEID);
      dsTempPaste.first();
      for(int i=0; i<dsTempPaste.getRowCount(); i++)
      {
        row.setValue(0, dsTempPaste.getValue(NODEID));
        if(!dsBomData.locate(row, Locate.FIRST))
        {
          dsBomData.insertRow(false);
          dsBomData.setValue("bomid", String.valueOf(--s_bomid));
          dsBomData.setValue(NODEID, dsTempPaste.getValue(NODEID));
          //dsBomData.setValue("gymcid", dsTempPaste.getValue("gymcid"));
          dsBomData.setValue("sl", dsTempPaste.getValue("sl"));
          dsBomData.setValue("shl", dsTempPaste.getValue("shl"));
          dsBomData.setValue("zjlx", dsTempPaste.getValue("zjlx"));
          dsBomData.setValue("wgcl", dsTempPaste.getValue("wgcl"));
          dsBomData.setValue("cpbm", dsTempPaste.getValue("cpbm"));
          if(parentId.length() > 0)
            dsBomData.setValue(PARENTID, parentId);
          dsBomData.post();
        }
        dsTempPaste.next();
      }
      dsTempPaste.empty();
      //改变状态:粘贴在根节点下将状态改为添加状态表示可以修改父件信息，添加状态不可以修改
      isAdd = pasteId.equals("0");
      initRowInfo(isAdd, true);
      pathCode = data.getParameter("pathcode");
      //pathCode = pasteId;
    }
  }

  /**
   * 得到用于查找产品信息的bean
   * @param req WEB的请求
   * @return 返回用于查找产品信息的bean
   */
  public LookUp getProductBean(HttpServletRequest req)
  {
    if(prodBean == null)
      prodBean = LookupBeanFacade.getInstance(req, SysConstant.BEAN_PRODUCT);
    return prodBean;
  }

  /*得到从表多列的信息*/
  public final RowMap[] getDetailRowinfos() {
    RowMap[] rows = new RowMap[d_RowInfos.size()];
    d_RowInfos.toArray(rows);
    return rows;
  }

  public String getParentId()
  {
    return parentId;
  }

  public String getPathCode(){
    return this.pathCode == null ? "" : this.pathCode;
  }
}
