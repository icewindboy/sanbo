package engine.web.html;

import java.util.List;
import java.util.ArrayList;
import engine.util.StringUtils;
/**
 * <p>Title: 字段信息</p>
 * <p>Description: 字段信息</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public final class FieldInfo implements java.io.Serializable
{

  public static final int VIRTUAL_TYPE = 0;

  public static final int INPUT_TYPE = 1;

  public static final int MEMO_TYPE = 2;

  public static final int SELECT_TYPE = 3;

  private String fieldcode = null;

  private String fieldname = null;

  private String linktable = null;

  private String[] showFieldCodes = null;

  private String[] showFieldNames = null;

  private String url = null;

  private int type;

  private List[] enumvalues = null;

  private boolean isBak = false;

  private boolean isShow = false;

  private boolean isNull = false;

  /**
   * 字段信息的构造函数
   * @param fieldcode 字段名
   * @param fieldname 字段中文名称
   * @param linktable 关联表
   * @param inputType 输入类型
   * @param enumvalues 枚举值
   * @param lookup lookup实例
   * @param showField 关联表需要显示的字段
   * @param url 关联的url
   * @param isBak 是否是备用字段
   * @param isShow 是否在列表中显示该字段
   */
  public FieldInfo(String fieldcode, String fieldname, String linktable, String inputType,
                   String enumvalues, String showField, String url, boolean isBak, boolean isShow)
  {
    this.fieldcode = fieldcode == null ? null : fieldcode.toLowerCase();
    this.fieldname = fieldname == null ? null : fieldname.length() > 0 ? fieldname : null;
    this.linktable = linktable == null ? null : linktable.length() > 0 ? linktable : null;
    this.url = url == null ? null : url.length() > 0 ? url : null;
    this.isBak = isBak;
    this.isShow = isShow;
    try{
      this.type = Integer.parseInt(inputType);
    }
    catch(Exception ex){
      this.type = INPUT_TYPE;
    }
    if(type == SELECT_TYPE)
      this.enumvalues = StringUtils.getEnumValues(enumvalues);
    //
    if(showField != null && showField.length() >0)
    {
      String[][] lists = StringUtils.getArrays(showField);
      this.showFieldCodes = lists[0];
      this.showFieldNames = lists[1];
    }

    //if(urlParam != null && urlParam.length() > 0)
      //this.urlParams = BaseAction.parseString(urlParam, ",");
  }

  /**
   * 得到枚举值的数组
   * @return 返回枚举值的数组
   */
  public List[] getEnumvalues() {
    return enumvalues;
  }

  /**
   * 得到字段名
   * @return 返回字段名
   */
  public String getFieldcode() {
    return fieldcode;
  }

  /**
   * 得到字段名中文名
   * @return 返回字段名中文名
   */
  public String getFieldname() {
    return fieldname;
  }

  /**
   * 得到字段输入类型
   * @return 返回字段输入类型
   */
  public int getType() {
    return type;
  }

  /**
   * 得到关联表名
   * @return 返回关联表名
   */
  public String getLinktable() {
    return linktable;
  }

  /**
   * 是否是备用字段
   * @return 是否是备用字段
   */
  public boolean isBak() {
    return isBak;
  }

  /**
   * 该字段是否在列表需要显示
   * @return 该字段是否在列表需要显示
   */
  public boolean isShow() {
    return isShow;
  }

  /**
   * 该字段是否允许未空
   * @return 该字段是否允许未空
   */
  public boolean isNull() {
    return isNull;
  }

  /**
   * 设置该字段是否允许未空
   * @param isNull 是否允许为空
   */
  public void setIsNull(boolean isNull)
  {
    this.isNull = isNull;
  }

  /**
   * 得到关联表需要显示字段的数组
   * @return 返回关联表需要显示字段的数组
   */
  public String[] getShowFields() {
    return showFieldCodes;
  }

  /**
   * 得到关联表需要显示的字段的对应的标题数组
   * @return 关联表需要显示的字段的对应的标题数组
   */
  public String[] getShowFieldNames() {
    return showFieldNames;
  }

  /**
   * 得到关联表需要显示的字段的对应的标题
   * @param i 顺序
   * @return 关联表需要显示的字段的对应的标题
   */
  public String getShowFieldName(int i) {
    return showFieldNames[i];
  }
  /**
   * 得到相关的url
   * @return 返回相关的url
   */
  public String getUrl() {
    return url;
  }
}