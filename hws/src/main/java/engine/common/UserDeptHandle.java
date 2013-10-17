package engine.common;

import java.io.Serializable;
/**
 * <p>Title: 用户部门权限的处理类</p>
 * <p>Description: 用户部门权限的处理类</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author 江海岛
 * @version 1.0
 */

public final class UserDeptHandle implements Serializable
{
  //是否有该部门的所有权限
  private boolean isDept = false;
  //当前用户
  private User user = null;

  /**
   * 构造函数，当前用户是否具有部门权限
   * @param user 当前用户
   * @param isDept 该部门的所有权限
   */
  public UserDeptHandle(User user, boolean isDept)
  {
    this.user = user;
    this.isDept = isDept;
  }

  /**
   * 是否有该部门的所有权限
   * @return 返回是否有该部门的所有权限
   */
  public boolean isDept()
  {
    return this.isDept;
  }

  /**
   * 是否处理该人员单据的权限
   * @param userId 人员id
   * @return 返回是否处理该人员单据的权限
   */
  public boolean isHandle(String userId)
  {
    return isDept ? true : user.getUserId().equals(userId);
  }

}