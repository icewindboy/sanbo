package engine.common;

import java.io.Serializable;

/**
 * <p>Title: 审批项目明细监听器(指特殊审批)</p>
 * <p>Description: 审批项目明细监听器(指特殊审批)</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author 江海岛
 * @version 1.0
 */

public interface ApproveListener extends Serializable
{
  public void processApprove(ApproveResponse[] reponses) throws Exception;
}