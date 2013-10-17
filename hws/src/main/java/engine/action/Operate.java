package engine.action;

/**
 * <p>Title: 操作的类型（小于1000的数字）</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 江海岛
 * @version 1.0
 */

public interface Operate extends java.io.Serializable
{
  public static final int INIT         = 0; //初始化
  public static final int ADD          = 11;//添加
  public static final int EDIT         = 12;//浏览或编辑
  public static final int BROWS        = 121;//浏览
  public static final int DEL          = 13;//删除
  public static final int POST         = 14;//保存返回

  public static final int MASTER_SEARCH= 15;//查找
  public static final int ORDERBY      = 151;//排序
  public static final int MASTER_APPROVE= 16;//通过审核
  public static final int MASTER_CLEAR = 17;//还原未审核状态
  public static final int FIXED_SEARCH = 18;//固定查询条件的查询
  public static final int POST_CONTINUE= 19;//保存继续

  public static final int DETAIL_ADD   = 21;//从表添加
  public static final int DETAIL_EDIT  = 22;//从表浏览或编辑
  public static final int DETAIL_DEL   = 23;//从表删除
  public static final int DETAIL_POST  = 24;//从表保存
  public static final int DETAIL_RE_ADD= 25;//从表保存后再添加
  public static final int DETAIL_REFRESH_LIST = 26;//从表提交后的刷新列表的操作
  public static final int DETAIL_DIRECT_EDIT = 28; //双击列表直接倒从表的修改状态
  //public static final int DETAIL_DIRECT_POST = 29; //从表的直接提交

  public static final int PROD_MULTI_SELECT  = 41;//多选产品
  public static final int CUST_MULTI_SELECT  = 43;//多选往来单位

  public static final int DEPT_CHANGE  = 51;//部门列表更改
  public static final int CUST_CHANGE  = 52;//往来单位更改
  public static final int CUST_NAME_CHANGE  = 55;//往来单位名称更改
  public static final int PROD_CHANGE  = 53;//产品更改
  public static final int PROD_NAME_CHANGE  = 54;//产品名称更改
  public static final int PROD_PROP_NAME_CHANGE = 58;//产品规格属性的名称变化的模糊查询

  public static final int APPROVE = 61;//审批
  public static final int ADD_APPROVE = 62;//添加在审核列表中去

  public static final int SHOW_PDF        = 71;//显示PDF报表
  public static final int PRINT_BILL      = 72;//打印单据
  public static final int PRINT_PRECISION = 73;//套打单据

  public static final String OPERATE_KEY="operate"; //操作的常量
}