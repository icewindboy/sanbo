package engine.erp.jit;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;

import engine.dataset.*;
import engine.util.StringUtils;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.common.LoginBean;
import engine.project.*;
import engine.html.HtmlTableProducer;
import engine.erp.baseinfo.HtmlTree;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import com.borland.dx.dataset.*;

/**
 * Title:        基础信息--类别信息维护
 * Description:
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author  江海岛
 * @version 1.0
 */
//2004.9.17 1.一次性展开BOM树, 不分此展开 2.添加上级规格属性ID，规格属性ID
public final class B_Bom extends BaseAction implements Operate
{
  //各种操作的静态变量
  public static final String NODE_ADD_CHILD = "1001";//添加子结点
  public static final String NODE_EDIT_VIEW = "1002";//浏览或编辑类别结点
  public static final String NODE_DELETE    = "1003";//删除产品类别结点
  public static final String NODE_EXPAND    = "1004";//展开节点
  //与明细类别类别有关的操作
  public static final String NODE_EDIT_POST = "2001";//提交
  public static final String NODE_PASTE     = "2002";//粘贴产品类别结点
  public static final String NODE_REFRESH   = "2003";//刷新
  public static final String NODE_COPY_ADD  = "2004";//复制添加
  //
  public static final String CHILD_SECTION_INI  = "3001";//BOM工段初始化
  public static final String CHILD_SECTION_ADD  = "3002";//BOM工段添加
  public static final String CHILD_SECTION_POST = "3003";//BOM工段保存到内存
  public static final String CHILD_SECTION_DEL  = "3004";//BOM工段删除
  //
  public static final String SEARCH_INIT        = "4001";//查询初始化
  public static final String SEARCH_PATH        = "4002";//查询路径

  //提取顶节点的SQL语句 //1 childcount
  private static final String BOM_FIRST_NODE_SQL
      = "SELECT (t.cpid||'_'||t.dmsxid) node, (t.sjcpid||'_'||t.sjdmsxid) parentnode,"
      + " (b.cpbm||' '||b.product||decode(d.sxz, NULL, '', ' '||d.sxz)) mc, 1 childcount "
      + "FROM ( "
      + " SELECT a.cpid, a.dmsxid, 0 sjcpid, 0 sjdmsxid "
      + " FROM  (SELECT DISTINCT sjcpid cpid, sjdmsxid dmsxid FROM sc_bom) a, sc_bom b "
      + " WHERE a.dmsxid=b.dmsxid(+) AND a.cpid=b.cpid(+) "
      + " GROUP BY a.cpid, a.dmsxid "
      + " HAVING COUNT(b.cpid)=0 " //剔除非根节点
      + ") t, vw_kc_dmsx_exist d, vw_kc_dm_exist b "
      + "WHERE t.dmsxid=d.dmsxid AND t.cpid=b.cpid "
      + "ORDER BY b.cpbm";
  //    "{CALL PCK_PRODUCE.getBomTreeData(?,0)}";
  //BOM树的SQL
  /*
  private static final String BOM_TREE_SQL
      = "SELECT (t.cpid||'_'||t.dmsxid) node, (t.sjcpid||'_'||t.sjdmsxid) parentnode,"
      + " (b.cpbm||' '||b.product||decode(d.sxz, NULL, '', ' '||d.sxz)) mc "
      + "FROM sc_bom t, vw_kc_dmsx_exist d, vw_kc_dm_exist b,  "
      + "WHERE t.dmsxid=d.dmsxid AND t.cpid = b.cpid "
      + "ORDER BY b.cpbm ";
  */
  //2004.11.2 添加分段下载数据 childcount
  private static final String BOM_CHILDS_SQL
      = "SELECT (t.cpid||'_'||t.dmsxid) node, (t.sjcpid||'_'||t.sjdmsxid) parentnode,"
      + " (b.cpbm||' '||b.product||decode(d.sxz, NULL, '', ' '||d.sxz)) mc, childcount "
      + "FROM ( "
      + " SELECT a.cpid, a.dmsxid, a.sjcpid, a.sjdmsxid, COUNT(b.cpid) childcount "
      + " FROM sc_bom a, sc_bom b "
      + " WHERE a.cpid=b.sjcpid(+) AND a.dmsxid=b.sjdmsxid(+) AND a.sjcpid='@' AND a.sjdmsxid='@'"
      + " GROUP BY a.cpid, a.dmsxid, a.sjcpid, a.sjdmsxid"
      + ") t, vw_kc_dmsx_exist d, vw_kc_dm_exist b "
      + "WHERE t.dmsxid=d.dmsxid AND t.cpid=b.cpid "
      + "ORDER BY b.cpbm ";
  //"{CALL PCK_PRODUCE.getBomTreeData(?,@)}";//BOM树的SQL
  //private static final String NODE_CAPTION_SQL = "SELECT PCK_PRODUCE.getBomNodeCaption(@) FROM dual";//得到节点的显示标题
  //**得到节点的显示标题
  private static final String NODE_CAPTION_SQL
      = "SELECT cpbm||' '||product||' '||(SELECT sxz FROM vw_kc_dmsx_exist WHERE dmsxid='@') FROM vw_kc_dm_exist WHERE cpid='@'";
  //**父节点的路径， 用逗号分割每个父节点ID, 格式cpid_1_dmsxid_1,cpid_2_dmsxid_2
  private static final String NODE_FATHERPATH_SQL = "SELECT PCK_PRODUCE.getBomNodePath(@,'@') FROM dual";
  //**BOM的一条记录
  private static final String BOM_DATA_SQL
      = "SELECT * FROM vw_sc_bom WHERE sjcpid='@' AND sjdmsxid='@' ORDER BY cpbm";
  private static final String BOM_DATA_STRUCT_SQL = "SELECT * FROM vw_sc_bom WHERE 1<>1";//BOM表的机构
  //**删除BOM的一条记录(包括打上删除标记的物资)
  private static final String BOM_DATA_DEL_SQL
      = "SELECT bomid, cpid, sjcpid, dmsxid, sjdmsxid, sl, shl, zjlx, xgr, wgcl, '' cpbm "
      + "FROM sc_bom WHERE sjcpid='@' AND sjdmsxid='@'";
  //**复值配料
  private static final String COPY_DATA_SQL = "{CALL PCK_PRODUCE.getBomCopyData(?,@,@,@,@)}";
  //复制配料并添加
  private static final String COPY_ADD_SQL
      = "SELECT bomid, cpid, sjcpid, dmsxid, sjdmsxid, sl, shl, zjlx, xgr, wgcl, cpbm "
      + "FROM vw_sc_bom WHERE sjcpid='@' AND sjdmsxid='@'";
  //**产品更新总提前期
  private static final String BOM_TOTAL_TIME_SQL = "{CALL PCK_PRODUCE.updateBomAheadTime(@, @)}";
  //产品提前期数据
  private static final String BOM_AHEAD_TIME = "SELECT * FROM sc_prod_time WHERE cpid='@' AND dmsxid='@'";
  //private static final String CHILD_COUNT_SQL = "SELECT COUNT(*) FROM sc_bom WHERE sjcpid=";
  //BOM领料工段
  private static final String BOM_SECTION_SQL = "SELECT * FROM sc_bomsection WHERE bomID IN(@)";
  //BOM领料工段结构
  private static final String BOM_SECTION_STRUCT_SQL = "SELECT * FROM sc_bomsection WHERE 1<>1";
  //
  private static final String BOM_PARENT_COUNT
      = "SELECT COUNT(*) FROM vw_sc_bom WHERE sjcpid='@' AND sjdmsxid='@'";
  //得到BOM父件数据
  private static final String BOM_PARENT_NODE
      = "SELECT (sjcpid||'_'||sjdmsxid) node FROM vw_sc_bom "
      + "WHERE ROWNUM<2 AND cpid='@' AND dmsxid='@'";
  //BOM查询SQL
  private static final String BOM_SEARCH
  = "SELECT t.*, a.cpbm, a.product, e.sxz, a.jldw, a.wzlbid, a.chxz "
  + "FROM ( "
  + "  SELECT b.cpbm sjcpbm, b.product sjproduct, d.sxz sjsxz, c.sjcpid, c.sjdmsxid, c.cpid, c.dmsxid "
  + "  FROM  vw_kc_dm_exist b, sc_bom c, vw_kc_dmsx_exist d "
  + "  WHERE b.cpid=c.sjcpid AND c.sjdmsxid=d.dmsxid "
  + "  UNION ALL "
  + "  SELECT NULL, NULL, NULL, 0 sjcpid, 0 sjdmsxid, a.cpid, a.dmsxid "
  + "  FROM  (SELECT DISTINCT sjcpid cpid, sjdmsxid dmsxid FROM sc_bom) a, sc_bom b "
  + "  WHERE a.dmsxid=b.dmsxid(+) AND a.cpid=b.cpid(+) "
  + "  GROUP BY a.cpid, a.dmsxid "
  + "  HAVING COUNT(b.cpid)=0 "
  + ") t, vw_kc_dm_exist a, vw_kc_dmsx_exist e "
  + "WHERE t.cpid=a.cpid AND t.dmsxid=e.dmsxid AND @ ";//ORDER BY a.cpbm

  private static final String NODEID    = "node";   //树节点ID字名称//cpid
  private static final String PARENTID  = "parentnode"; //父节点ID字段//sjcpid
  private static final String NODECODE  = null;     //树节点编码字段
  private static final String NODENAME  = "mc";     //树节点名称字段
  //2004。9。17 全部一次打开
  private static final String CHILDCOUNT= "childcount";     //全部一次打开 //"childcount"; //子节点记录数量
  //private static final String NODELEVEL = "";       //树节点的层次

  private EngineDataSet dsBomTree = new EngineDataSet();//保存BOM树状信息的数据集
  public  EngineDataSet dsBomData = new EngineDataSet();//保存需要修改记录的数据集
  public  EngineDataSet dsBomSection = new EngineDataSet();//保留BOM的领料工段
  //private EngineDataSet dsTempTree = null;//临时保存树的数据
  private EngineDataSet dsTempPaste = null;//保存临时数据
  public  EngineDataSet dsBomSearch = new EngineDataSet(); //保存查询的数据
  public  HtmlTableProducer table = new HtmlTableProducer(dsBomSearch, "kc_dm_select", "a");
  //@time public  EngineDataSet dsAheadTime = new EngineDataSet();//提前期

  private HtmlTree bomTree = null;     //保存打印物资类别树网页的对象

  //private int     tempOperate = NODE_EDIT_VIEW;//临时保存上次的操作类型
  //private int     nodelevel;                 //保存当前结点的层数
  private String  parentId     = "";          //保存父结点ID
  private String  parentPropId = "0";         //保存父结点属性ID
  //private String  nodeId     = null;        //保存当前结点
  private String  pathCode     = null;          //保存树展开的路径
  public  boolean isAdd        = true;          //是否在添加状态
  //private List    pathList   = null;          //保存节点的各个父节点的ID
  public  String  aheadtime    = "";

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

        //bomBean.dsBomData.setColumnFormat("sl", bomBean.qtyFormat);
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
      dsBomTree.closeDataSet();
      //dsBomTree = null;
    }
    if(dsBomData != null){
      dsBomData.closeDataSet();
      //dsBomData = null;
    }
    if(dsTempPaste != null) {
      dsTempPaste.closeDataSet();
      //dsTempPaste = null;
    }
    if(dsBomSearch != null){
      dsBomSearch.closeDataSet();
    }
  }
  /**
   * 初始化函数
   * @throws Exception 异常信息
   */
  private void jbInit() throws Exception {
    setDataSetProperty(dsBomTree, null);
    setDataSetProperty(dsBomData, null);
    //dsBomData.setLoadDataUseSelf(false);
    setDataSetProperty(dsBomSection, BOM_SECTION_STRUCT_SQL);
    setDataSetProperty(dsBomSearch, StringUtils.combine(BOM_SEARCH, "@", new String[]{"1<>1"}));
    //dsBomSection.setLoadDataUseSelf(false);
    //@time setDataSetProperty(dsAheadTime, null);
    dsBomSearch.setSort(new SortDescriptor("", new String[]{"cpbm"}, new boolean[]{false, false}, null, 0));
    dsBomTree.setSort(new SortDescriptor("", new String[]{PARENTID, NODENAME}, new boolean[]{false, false}, null, 0));
    //dsBomData.setSort(new SortDescriptor("", new String[]{"cpbm"}, new boolean[]{false}, null, 0));
    dsBomData.setTableName("sc_bom");
    //dsBomData.setSequence(new SequenceDescriptor(new String[]{"bomid"}, new String[]{"s_sc_bom"}));
    //设置主从关系，设置级联更新，级联删除
    dsBomSection.setMasterLink(new MasterLinkDescriptor(dsBomData, new String[]{"bomid"},
        new String[]{"bomid"}, false, true, true));
    //
    NodeChild_Add_Edit nodeChild_Add_Edit = new NodeChild_Add_Edit();
    //添加 编辑的执行者
    addObactioner(NODE_EDIT_VIEW, nodeChild_Add_Edit);
    //添加 新增子节点的执行者
    addObactioner(NODE_ADD_CHILD, nodeChild_Add_Edit);
    //添加 节点提交的执行者
    addObactioner(NODE_EDIT_POST, new Node_Post());
    //添加 节点删除的执行者
    addObactioner(NODE_DELETE, new Node_Delete());
    //添加 节点粘贴的执行者
    Node_Paste paste = new Node_Paste();
    addObactioner(NODE_PASTE, paste);
    //复制配料添加
    addObactioner(NODE_COPY_ADD, paste);
    //添加子件
    addObactioner(String.valueOf(DETAIL_ADD), new Detail_Add());
    //删除子件
    addObactioner(String.valueOf(DETAIL_DEL), new Detail_Delete());
    //展开节点
    addObactioner(NODE_EXPAND, new Node_Expand());
    //刷行节点
    addObactioner(NODE_REFRESH, new Node_Refresh());
    //初始化
    addObactioner(String.valueOf(INIT), new Init());
    //BOM工段初始化
    addObactioner(CHILD_SECTION_INI, new ChildSectionInit());
    //BOM工段添加
    addObactioner(CHILD_SECTION_ADD, new ChildSectionAdd());
    //BOM工段删除
    addObactioner(CHILD_SECTION_DEL, new ChildSectionDel());
    //BOM工段
    addObactioner(CHILD_SECTION_POST, new ChildSectionPost());
    //BOM查询
    Search search = new Search();
    addObactioner(String.valueOf(MASTER_SEARCH), search);
    addObactioner(SEARCH_INIT, search);
    //查询路径
    addObactioner(SEARCH_PATH, new SearchPath());
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
  public void getBomTreeHTML(PageContext pageContext)
  {
    //if(!dsBomTree.isOpen())
    //  dsBomTree.openDataSet();
    if(bomTree == null)
    {
      bomTree = new HtmlTree();
      //bomTree.setExpandNodeFuction("NodeExpand(?)");
      bomTree.setTreeProperty(dsBomTree, "物资清单(BOM)表", NODEID, NODECODE, PARENTID, NODENAME,
                              null, true, CHILDCOUNT, "0_0");
    }
    bomTree.printHtmlTree(pageContext, pathCode);
  }

  public void getChildrenHTML(PageContext pageContext, String parentId, String parentPath){
    if(bomTree == null)
    {
      bomTree = new HtmlTree();
      //bomTree.setExpandNodeFuction("NodeExpand(?)");
      bomTree.setTreeProperty(dsBomTree, "物资清单(BOM)表", NODEID, NODECODE, PARENTID, NODENAME,
                              null, true, CHILDCOUNT, "0_0");
    }
    bomTree.printChildrenHTML(pageContext, parentId, parentPath);
  }

  public void getNodeHTML(PageContext pageContext, String nodeId, String nodePath){
    if(bomTree == null)
    {
      bomTree = new HtmlTree();
      //bomTree.setExpandNodeFuction("NodeExpand(?)");
      bomTree.setTreeProperty(dsBomTree, "物资清单(BOM)表", NODEID, NODECODE, PARENTID, NODENAME,
                              null, true, CHILDCOUNT, "0_0");
    }
    bomTree.printNodeHTML(pageContext, nodeId, "", nodePath);
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
    aheadtime = rowInfo.get("ahead_time");

    int rownum = d_RowInfos.size();
    RowMap detailRow = null;
    for(int i=0; i<rownum; i++)
    {
      detailRow = (RowMap)d_RowInfos.get(i);
      detailRow.put("cpid", rowInfo.get("cpid_"+i));//产品id
      String dmsxid = rowInfo.get("dmsxid_"+i);
      detailRow.put("dmsxid", dmsxid.length() == 0 ? "0" : dmsxid);//产品id
      //detailRow.put("gymcid", rowInfo.get("gymcid_"+i));//工艺id
      detailRow.put("cpbm", rowInfo.get("cpbm_"+i));//
      detailRow.put("sl", rowInfo.get("sl_"+i));//
      detailRow.put("shl", rowInfo.get("shl_"+i));//损耗率
      detailRow.put("zjlx", rowInfo.get("zjlx_"+i));//子件类型
      detailRow.put("wgcl", rowInfo.get("wgcl_"+i));//完工处理
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
  private void lookUpPath(List pathList, String nodeid, String nodepropid) throws Exception
  {
    String SQL = combineSQL(NODE_FATHERPATH_SQL, "@", new String[]{nodeid, nodepropid});
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

  /**
   * 打开工段领料信息表
   * @param dsBomData BOM数据的数据集
   * @param dsBomSection BOM领料工段信息
   */
  private void openBomSection(EngineDataSet dsBomData, EngineDataSet dsBomSection)
  {
    String[] bomids = DataSetUtils.rowsToArray(dsBomData, "bomid", false, false);
    String sql = StringUtils.getArrayValue(bomids, ",");
    if(sql.length() == 0)
      sql = "-100";
    sql = StringUtils.combine(BOM_SECTION_SQL, "@", new String[]{sql});
    dsBomSection.setQueryString(sql);
    if(!dsBomSection.isOpen())
      dsBomSection.openDataSet();
    else
      dsBomSection.refresh();
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
      //提取跟节点的数据
      dsBomTree.setQueryString(BOM_FIRST_NODE_SQL);
      if(dsBomTree.isOpen())
        dsBomTree.refresh();
      else
        dsBomTree.openDataSet();
      dsBomTree.getColumn("parentnode").setPrecision(100);
      //System.out.println(dsBomTree.getColumn("parentnode").getPrecision());
      pathCode = null;
      s_bomid = 0;
    }

    /**
     * 展开所有子件
     * @throws Exception
     *
    private void getFirstNodeData() throws Exception
    {
      //提取跟节点数据
      String SQL = BOM_FIRST_NODE_SQL;
      EngineDataSet dsTempTree = new EngineDataSet();
      setDataSetProperty(dsTempTree, null);
      dsTempTree.setQueryString(SQL);
      dsTempTree.openDataSet();
      //增加提取的数据? bug: loadDataSet(), 居然会清空数据
      if(dsTempTree.getRowCount() < 1)
        return;
      EngineRow row = new EngineRow(dsBomTree);
      dsTempTree.first();
      for(int i=0; i<dsTempTree.getRowCount(); i++)
      {
        dsTempTree.copyTo(row);
        dsBomTree.addRow(row);
        dsTempTree.next();
      }
      dsTempTree.closeDataSet();
    }*/
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
      String _tempNodeID = data.getParameter("nodeID", "").trim();
      if(_tempNodeID.length() == 0 || _tempNodeID.equals("_"))
      {
        data.setMessage(showJavaScript("location.href='../blank.htm';"));
        return;
      }
      String[] tempNodeIDs = StringUtils.parseString(_tempNodeID, "_");
      //
      pathCode = data.getParameter("pathcode", "");

      isAdd = action.equals(String.valueOf(NODE_ADD_CHILD));
      /*查找记录， 提取当前操作的节点的数据*/
      if(isAdd){
        dsBomData.setQueryString(BOM_DATA_STRUCT_SQL);
        if(dsBomData.isOpen())
          dsBomData.refresh();
        else
          dsBomData.openDataSet();

        //
        if(dsBomSection.isOpen())
          dsBomSection.empty();
        else
          dsBomSection.openDataSet();
      }
      else
        openDetails(tempNodeIDs[0], tempNodeIDs[1]);

      parentId = isAdd ? "" : tempNodeIDs[0];
      parentPropId = isAdd ? "0" : tempNodeIDs[1];

      initRowInfo(isAdd, true);
      //@time initAheadTime();
    }
  }

  /**
   * 查询
   */
  final class Search implements Obactioner
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
      boolean isInit = SEARCH_INIT.equals(action);
      String SQL = null;
      if(isInit){
        SQL = StringUtils.combine(BOM_SEARCH, "@", new String[]{"1<>1"});
        table.getWhereInfo().clearWhereValues();
      }
      else
      {
        table.getWhereInfo().setWhereValues(data.getRequest());
        SQL = table.getWhereInfo().getWhereQuery();
        SQL = StringUtils.combine(BOM_SEARCH, "@", new String[]{SQL.length()==0 ? "1=1" : SQL});
      }
      dsBomSearch.setQueryString(SQL);
      dsBomSearch.readyRefresh();
    }
  }

  /**
   * 查询
   */
  final class SearchPath implements Obactioner
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
      String nodeidStr = data.getParameter("curNodeID", "").trim();
      if(nodeidStr.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请选择类别！');"));
        return;
      }
      String[] nodeAndParent = StringUtils.parseString(nodeidStr, "$");
      String[] nodeids = StringUtils.parseString(nodeAndParent[0], "_");
      String cpid = nodeids[0] == null ? "" : nodeids[0];
      String propid = nodeids[1] == null && nodeids[1].length() == 0 ? "" : nodeids[1];
      String parentId = null, parentPropId = null;
      if(!nodeAndParent[1].equals("0_0"))
      {
        String[] temps = StringUtils.parseString(nodeAndParent[1], "_");
        parentId = temps[0];
        parentPropId = temps[1];
      }
      //展开树路径
      pathCode = getNodePathDatas(cpid, propid, parentId, parentPropId);
      //data.setMessage(showJavaScript("showSelected();"));
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

  /**
   * //@time 初始化提前期数据
   *
  private final void initAheadTime()
  {
    String sql = StringUtils.combine(BOM_AHEAD_TIME, "@", new String[]{parentId, parentPropId});
    dsAheadTime.setQueryString(sql);
    if(dsAheadTime.isOpen())
      dsAheadTime.refresh();
    else
      dsAheadTime.openDataSet();

    aheadtime = dsAheadTime.getRowCount() == 0? "" : dsAheadTime.getValue("ahead_time");
  }*/

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
      /*if(!isDouble(aheadtime))
      {
        data.setMessage(showJavaScript("alert('输入的提前期非法！');"));
        return;
      }*/
      if(isAdd)
      {
        parentId = data.getParameter("sjcpid", "");
        parentPropId = data.getParameter("sjdmsxid", "0");
        if(parentPropId.length() == 0)
          parentPropId = "0";
        if(parentId.length() == 0) {
          data.setMessage(showJavaScript("alert('请选择父件！');"));
          return;
        }
        if(d_RowInfos.size() < 1)
        {
          data.setMessage(showJavaScript("alert('请输入配料明细！')"));
          return;
        }
        String sql = StringUtils.combine(BOM_PARENT_COUNT, "@", new String[]{parentId, parentPropId});
        sql = dataSetProvider.getSequence(sql);
        if(!sql.equals("0"))
        {
          data.setMessage(showJavaScript("alert('该父件已经存在！')"));
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
        {
          dsBomData.setValue("sjcpid", parentId);
          dsBomData.setValue("sjdmsxid", parentPropId);
        }
        if(dsBomData.getBigDecimal("bomid").intValue() < 0)
          dsBomData.setValue("bomid", dataSetProvider.getSequence("s_sc_bom"));

        dsBomData.setValue("cpid", detailrow.get("cpid"));
        dsBomData.setValue("dmsxid", detailrow.get("dmsxid"));
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
      /* //@time
      if(dsAheadTime.getRowCount() == 0)
      {
        dsAheadTime.insertRow(false);
        dsAheadTime.setValue("cpid", parentId);
        dsAheadTime.setValue("dmsxid", parentPropId);
      }
      dsAheadTime.setValue("ahead_time", aheadtime);
      dsAheadTime.post();
      */
      dsBomData.getColumn("cpbm").setResolvable(false);
      //更新总提前期
      String totalSQL = combineSQL(BOM_TOTAL_TIME_SQL, "@", new String[]{parentId, parentPropId});
      dsBomData.setAfterResolvedSQL(new String[]{totalSQL});
      dsBomData.saveDataSets(new EngineDataSet[]{dsBomData, dsBomSection}); //dsAheadTime
      /*
      dsBomData.first();
      for(int i=0; i<dsBomData.getRowCount(); i++){
        System.out.println("cpid:"+dsBomData.getValue("cpid")+"sjcpid:"+dsBomData.getValue("sjcpid"));
        dsBomData.next();
      }*/
      String parentStr = parentId+"_"+parentPropId;
      //同步树的记录
      String[] pathNodeids = StringUtils.parseString(pathCode, "$");
      //当前父节点的父节点
      String pa_parentId = isAdd || pathNodeids.length == 1 ? "0_0" : pathNodeids[pathNodeids.length-2];
      //
      EngineRow row = new EngineRow(dsBomTree, new String[]{NODEID, PARENTID});
      row.setValue(0, parentStr);
      row.setValue(1, pa_parentId);//0_0
      if(isAdd)
      {
        //更新树的第一节点数据
        if(!dsBomTree.locate(row, Locate.FIRST))
        {
          dsBomTree.insertRow(false);
          dsBomTree.setValue(NODEID, parentStr);
          dsBomTree.setValue(PARENTID, pa_parentId);
          dsBomTree.setValue(CHILDCOUNT, "1");//添加的节点肯定有子节点
          //从数据库中提取节点的显示的标题
          String SQL = combineSQL(NODE_CAPTION_SQL, "@", new String[]{parentPropId, parentId});
          String name = dataSetProvider.getSequence(SQL);
          dsBomTree.setValue(NODENAME, name);
          dsBomTree.post();
        }
        //展开树数据
        emptyChilds(parentStr);
        expandChilds(parentStr, pathCode);
      }
      //如果修改时,将数据全部记录删除,再保存。此时需要删除顶节点记录,在树上不显示
      else if(dsBomData.getRowCount() == 0)
      {
        emptyChilds(parentStr);
        if(dsBomTree.locate(row, Locate.FIRST))
        {
          if(pathNodeids.length == 1)
            dsBomTree.emptyRow();
          else
            dsBomTree.setValue(CHILDCOUNT, "0");
        }
      }
      //提取所有子件
      else
      {
        emptyChilds(parentStr);
        if(dsBomTree.locate(row, Locate.FIRST))
          dsBomTree.setValue(CHILDCOUNT, "1");
        expandChilds(parentStr, pathCode);
      }
      //
      //@time initAheadTime();
      data.setMessage(showJavaScript(isAdd ? "submitTree();" : "refreshNode();"));
      //将状态改变修改状态
      if(isAdd)
        isAdd = false;
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
        String dmsxid = detailrow.get("dmsxid");
        if(cpid.equals(parentId) && dmsxid.equals(parentPropId))
          return showJavaScript("alert('配料单中子件物资不能与父件的物资相同');");
        if((temp = checkNumber(detailrow.get("sl"), "子件数量", 0, 999999999999999d)) != null)
          return temp;
        //检查重复
        String cpid_propid = cpid + "_" + dmsxid;
        if(cpidList.contains(cpid_propid))
          return showJavaScript("alert('配料单中子件物资重复(编码:"+detailrow.get("cpbm")+")')");
        else
          cpidList.add(cpid_propid);
        //提取父件的路径信息
        lookUpPath(path, parentId, parentPropId);

        if(path.indexOf(cpid_propid) > -1)
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
      String _deleteId = data.getParameter("curNodeID","").trim();
      if(_deleteId.length() == 0)
      {
        data.setMessage(showJavaScript("alert('请选择类别！');"));
        return;
      }
      /*查找记录*/
      String[] deleteIds = StringUtils.parseString(_deleteId, "_");
      String sql = StringUtils.combine(BOM_DATA_DEL_SQL, "@", deleteIds);
      dsBomData.setQueryString(sql);
      if(dsBomData.isOpen())
        dsBomData.refresh();
      else
        dsBomData.openDataSet();
      //打开工段信息
      openBomSection(dsBomData, dsBomSection);

      while(dsBomData.getRowCount() > 0){
        dsBomData.deleteRow();
      }

      dsBomData.getColumn("cpbm").setResolvable(false);
      dsBomData.saveDataSets(new EngineDataSet[]{dsBomData, dsBomSection});
      //dsBomData.saveChanges();
      pathCode = data.getParameter("pathcode", "");
      String[] pathNodeids = StringUtils.parseString(pathCode, "$");
      String parentId = pathNodeids.length == 1 ? "0_0" : pathNodeids[pathNodeids.length-2];
      //同步树的数据(主要是根节点)
      EngineRow row = new EngineRow(dsBomTree, new String[]{NODEID, PARENTID});
      row.setValue(0, _deleteId);
      row.setValue(1, parentId);//0_0
      if(dsBomTree.locate(row, Locate.FIRST))
        dsBomTree.deleteRow();
      //若删除的不是顶节点, 需更新其子节点的数量
      if(pathNodeids.length > 1){
        String countsql = StringUtils.combine(BOM_PARENT_COUNT, "@", StringUtils.parseString(parentId, "_"));
        String count = dataSetProvider.getSequence(countsql);
        String pa_parentId = pathNodeids.length == 2 ? "0_0" : pathNodeids[pathNodeids.length-3];
        row.setValue(0, parentId);
        row.setValue(1, pa_parentId);//0_0
        if(dsBomTree.locate(row, Locate.FIRST))
          dsBomTree.setValue(CHILDCOUNT, count);
      }
      //dsBomTree.first();
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
      {
        parentId = data.getParameter("sjcpid", "");
        parentPropId = data.getParameter("sjdmsxid", "0");
        if(parentPropId.length() == 0)
          parentPropId = "0";
        if(d_RowInfos.size() == 0){
          String sql = StringUtils.combine(BOM_PARENT_COUNT, "@", new String[]{parentId, parentPropId});
          sql = dataSetProvider.getSequence(sql);
          //如果该父件已经存在，就提取子件数据和展开树路径
          if(!sql.equals("0"))
          {
            isAdd = false;
            //提取子件数据
            openDetails(parentId, parentPropId);
            //展开树路径
            pathCode = getNodePathDatas(parentId, parentPropId);
            //初始化界面显示数组
            initRowInfo(isAdd, true);
            //
            data.setMessage(showJavaScript("refreshTree();"));
            return;
          }
        }
      }
      //String[] parentIds = StringUtils.parseString(parentId, "_");
      dsBomData.insertRow(false);
      dsBomData.setValue("bomid", String.valueOf(--s_bomid));
      dsBomData.setValue("sjcpid", parentId);
      dsBomData.setValue("sjdmsxid", parentPropId);
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
      String nodeId = data.getParameter("curNodeID", "").trim();
      String path = data.getParameter("pathcode");
      String temp = null;
      if((temp = expandChilds(nodeId, path)) != null)
        data.setMessage(temp);
    }
  }

  /**
   * 展开父件下的所有子件，即提取所有子件数据
   * @return 提取所有子件数据的SQL语句
   * @throws Exception
   */
  public String expandChilds(String nodeId, String path) throws Exception
  {
    if(nodeId.length() == 0 || nodeId.equals("_"))
    {
      return showJavaScript("alert('请选择需要展开的物资！');");
    }
    String[] tempNodeIDs = StringUtils.parseString(nodeId, "_");
    //pathCode = path;
    //展开节点
    expandChilds(null, tempNodeIDs[0], tempNodeIDs[1]);
    //
    return null;
  }

  /**
   * 打开子件列表和工序分段列表
   * @param cpid 父件产品id
   * @param propid 父件产品规格属性id
   */
  private void openDetails(String cpid, String propid){
    String sql = StringUtils.combine(BOM_DATA_SQL, "@", new String[]{cpid, propid});
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

  private String getNodePathDatas(String cpid, String propid){
    return getNodePathDatas(cpid, propid, null, null);
  }
  /**
   * 展开树路径, 包括提取数据, 并返回树路径
   * @param cpid 父件产品id
   * @param propid 父件产品规格属性id
   */
  private String getNodePathDatas(String cpid, String propid, String parentid, String parentPropid)
  {
    String path = new StringBuffer(cpid).append("_").append(propid).toString();
    EngineDataSet ds = new EngineDataSet();
    expandChilds(ds, cpid, propid);
    int level = 0;
    while(level < 20){
      if(level == 0 && parentid != null && parentPropid != null){
        //组合树路径
        path = new StringBuffer(parentid).append("_").append(parentPropid).append("$").append(path).toString();
        expandChilds(ds, parentid, parentPropid);
        cpid = parentid;
        propid = parentPropid;
      }
      else{
        String parentnode = StringUtils.combine(BOM_PARENT_NODE, "@", new String[]{cpid, propid});
        parentnode = dataSetProvider.getSequence(parentnode);
        if(parentnode == null || parentnode.length() == 0)
          break;
        //组合树路径
        path = new StringBuffer(parentnode).append("$").append(path).toString();
        String[] parentIDs = StringUtils.parseString(parentnode, "_");
        expandChilds(ds, parentIDs[0], parentIDs[1]);
        cpid = parentIDs[0];
        propid = parentIDs[1];
      }
      level++;
    }
    return path;
  }

  /**
   * 展开节点数据
   * @param dsTempTree 临时树数据用数据集
   * @param cpid 父件产品id
   * @param propid 父件规格属性id
   */
  private void expandChilds(EngineDataSet dsTempTree, String cpid, String propid)
  {
    //提取跟节点数据
    String SQL = combineSQL(BOM_CHILDS_SQL, "@", new String[]{cpid, propid});
    if(dsTempTree == null){
      dsTempTree = new EngineDataSet();
    }
    if(dsTempTree.getProvider() == null)
      setDataSetProperty(dsTempTree, null);

    dsTempTree.setQueryString(SQL);
    if(dsTempTree.isOpen())
      dsTempTree.refresh();
    else
      dsTempTree.openDataSet();
    //增加提取的数据? bug: loadDataSet(), 居然会清空数据
    if(dsTempTree.getRowCount() < 1)
      return;

    //EngineRow row = new EngineRow(dsBomTree);
    int count = dsBomTree.getColumnCount();
    EngineRow row = new EngineRow(dsBomTree, new String[]{"node", "parentnode"});
    dsTempTree.first();
    for(int i=0; i<dsTempTree.getRowCount(); i++)
    {
      row.setValue(0, dsTempTree.getValue("node"));
      row.setValue(1, dsTempTree.getValue("parentnode"));
      if(!dsBomTree.locate(row, Locate.FIRST))
      {
        dsBomTree.insertRow(false);
        for(int j=0; j<count; j++)
          dsBomTree.setValue(j, dsTempTree.getValue(j));
        dsBomTree.post();
      }
      dsTempTree.next();
    }
    dsTempTree.closeDataSet();
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
      String _copyId = data.getParameter("cutNodeID", "");
      String _pasteId = data.getParameter("curNodeID", "");
      boolean isCopyAdd = action.equals(NODE_COPY_ADD);
      if(_copyId.length() == 0 || _copyId.equals("_"))
      {
        data.setMessage(showJavaScript("alert('请选择需要复制物资！');"));
        return;
      }
      //粘贴操作需要校验
      if(!isCopyAdd && (_pasteId.length() == 0 || _pasteId.equals("_")))
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
      /*查找记录, 得到父节点信息*/
      String[] copyIds  = StringUtils.parseString(_copyId, "_");
      String[] pasteIds = isCopyAdd ? null : StringUtils.parseString(_pasteId, "_");
      if(isCopyAdd)
      {
        parentId = "";
        parentPropId = "0";
      }
      else
      {
        parentId = pasteIds[0].equals("0") ? "" : pasteIds[0];
        parentPropId = pasteIds[1].length()==0 ? "0" : pasteIds[1];
      }
      if(dsTempPaste == null)
      {
        dsTempPaste = new EngineDataSet();
        setDataSetProperty(dsTempPaste, null);
      }
      //得到拷贝子节点的记录
      String SQL = isCopyAdd ? combineSQL(COPY_ADD_SQL, "@", new String[]{copyIds[0], copyIds[1]})
                   : combineSQL(COPY_DATA_SQL, "@", new String[]{copyIds[0], copyIds[1], pasteIds[0], pasteIds[1]});
      //得到拷贝子节点的记录
      dsTempPaste.setQueryString(SQL);
      if(dsTempPaste.isOpen())
        dsTempPaste.refresh();
      else
        dsTempPaste.openDataSet();
      //是否是根节点, 如果是跟节点或是复制添加的, 只提取结构
      String sql = isCopyAdd || pasteIds[0].equals("0") ? BOM_DATA_STRUCT_SQL :
                   StringUtils.combine(BOM_DATA_SQL, "@", pasteIds);
      dsBomData.setQueryString(sql);
      if(dsBomData.isOpen())
        dsBomData.refresh();
      else
        dsBomData.openDataSet();
      //打开工段领料信息表
      openBomSection(dsBomData, dsBomSection);
      //插入粘贴的配料信息
      EngineDataSet dsTempPasteSection = new EngineDataSet();
      setDataSetProperty(dsTempPasteSection, null);
      EngineRow row = new EngineRow(dsBomData, new String[]{"cpid", "dmsxid"});
      dsTempPaste.first();
      for(int i=0; i<dsTempPaste.getRowCount(); i++)
      {
        row.setValue(0, dsTempPaste.getValue("cpid"));
        row.setValue(1, dsTempPaste.getValue("dmsxid"));
        if(!dsBomData.locate(row, Locate.FIRST))
        {
          dsBomData.insertRow(false);
          String newBomid = String.valueOf(--s_bomid);
          dsBomData.setValue("bomid", newBomid);
          dsBomData.setValue("cpid", dsTempPaste.getValue("cpid"));
          dsBomData.setValue("dmsxid", dsTempPaste.getValue("dmsxid"));
          //dsBomData.setValue("gymcid", dsTempPaste.getValue("gymcid"));
          dsBomData.setValue("sl", dsTempPaste.getValue("sl"));
          dsBomData.setValue("shl", dsTempPaste.getValue("shl"));
          dsBomData.setValue("zjlx", dsTempPaste.getValue("zjlx"));
          dsBomData.setValue("wgcl", dsTempPaste.getValue("wgcl"));
          dsBomData.setValue("cpbm", dsTempPaste.getValue("cpbm"));
          //
          if(parentId.length() > 0)
          {
            dsBomData.setValue("sjcpid", parentId);
            dsBomData.setValue("sjdmsxid", parentPropId);
          }
          dsBomData.post();
          //插入复制的工段信息数据
          String pasBomid = dsTempPaste.getValue("bomid");
          String pasSQL = StringUtils.combine(BOM_SECTION_SQL, "@", new String[]{pasBomid});
          dsTempPasteSection.setQueryString(pasSQL);
          if(dsTempPasteSection.isOpen())
            dsTempPasteSection.refresh();
          else
            dsTempPasteSection.openDataSet();
          //
          dsTempPasteSection.first();
          for(int j=0; j<dsTempPasteSection.getRowCount(); j++)
          {
            dsBomSection.insertRow(false);
            dsBomSection.setValue("bomid", newBomid);
            dsBomSection.setValue("gxfdid", dsTempPasteSection.getValue("gxfdid"));
            dsTempPasteSection.next();
          }//end for
        }
        dsTempPaste.next();
      }//end paste
      dsTempPaste.closeDataSet();
      dsTempPasteSection.closeDataSet();
      //改变状态:粘贴在根节点下将状态改为添加状态表示可以修改父件信息，添加状态不可以修改
      isAdd = parentId.equals("");//isCopyAdd || pasteIds[0].equals("0");

      initRowInfo(isAdd, true);
      //@time initAheadTime();
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

  public String getParentPropId()
  {
    return this.parentPropId;
  }

  public String getPathCode(){
    return this.pathCode == null ? "" : this.pathCode;
  }
}
