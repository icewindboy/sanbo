package engine.common;

import java.io.Serializable;

/**
 * <p>Title: 审批项目明细的响应</p>
 * <p>Description: 审批项目明细的响应</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author 江海岛
 * @version 1.0
 */

public final class ApproveResponse implements Serializable
{
  String projectFlowId = null;    //审批项目明细id
  String projectFlowCode = null;  //审批项目明细编码
  String projectFlowValue = null; //审批项目明细值

  boolean response = true; //是否加入审批

  ApproveResponse(String projectFlowId, String projectFlowCode, String projectFlowValue)
  {
    this.projectFlowId = projectFlowId;
    this.projectFlowCode = projectFlowCode;
    this.projectFlowValue = projectFlowValue;
  }

  /**
   * 调用这个方法表示允许该审批明细
   */
  public final void add() { response = true; }

  /**
   * 调用这个方法表示跳过这审批明细
   */
  public final void skip() { response = false;}

  /**
   * 得到审批项目明细id
   * @return 返回审批项目明细id
   */
  public String getProjectFlowId()
  {
    return projectFlowId;
  }

  /**
   * 得到审批项目的编码
   * @return 返回审批项目的编码
   */
  public String getProjectFlowCode()
  {
    return projectFlowCode;
  }

  /**
   * 得到审批项目的特殊值
   * @return 返回审批项目的特殊值
   */
  public String getProjectFlowValue()
  {
    return projectFlowValue;
  }
}