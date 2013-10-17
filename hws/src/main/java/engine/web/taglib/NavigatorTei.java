package engine.web.taglib;

import javax.servlet.jsp.tagext.TagExtraInfo;
import javax.servlet.jsp.tagext.TagData;
import javax.servlet.jsp.tagext.VariableInfo;

/**
 * <p>Title: 自定义的Beans</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public class NavigatorTei extends TagExtraInfo
{
  //NESTED ==> variable is visible only within the start/end tags
  //AT_BEGIN ==> variable is visible after start tag
  //AT_END ==> variable is visible after end tag
  public VariableInfo[] getVariableInfo( TagData data ) {
    String id = data.getId();
    if ( id != null ) {
      return new VariableInfo[] {
        new VariableInfo( id, Navigator.class.getName(), true, VariableInfo.AT_BEGIN ),
      };
    }
    else {
      return super.getVariableInfo( data );
    }
  }
}