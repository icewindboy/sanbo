package engine.web.taglib;

import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;

import com.borland.dx.dataset.DataSet;
import engine.dataset.RowMap;
import engine.web.lookup.LookupParam;
import engine.web.lookup.LookupHelper;
import engine.web.lookup.Lookup;
import engine.web.lookup.LookupFacade;

/**
 * <p>Title: lookupBean对应的类型</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public class LookupTag extends TagSupport
{

  private String name = null;
  private String id = null;
  private Lookup lookupObj = null;

  public void setId( String newId ) {
    id = newId;
  }

  public String getId() {
    return id;
  }

  public void setName( String newName ) {
    name = newName;
  }

  public String getName() {
    return name;
  }

  /*
  public void regData(DataSet ds, String idColumnName)
  {
    this.lookupObj.regData(ds, idColumnName);
  }

  public void regData(DataSet ds, String[] idColumnNames)
  {
    this.lookupObj.regData(ds, idColumnNames);
  }

  public void regData(String[] idValues)
  {
    this.lookupObj.regData(idValues);
  }

  public void regData(LookupParam[] regInfos)
  {
    this.lookupObj.regData(regInfos);
  }

  public String getLookupName(String idValue)
  {
    return this.lookupObj.getLookupName(idValue);
  }

  public String getLookupName(String[] idValues)
  {
    return this.lookupObj.getLookupName(idValues);
  }

  public RowMap getLookupRow(String idValue)
  {
    return this.lookupObj.getLookupRow(idValue);
  }

  public RowMap getLookupRow(String[] idValues)
  {
    return this.lookupObj.getLookupRow(idValues);
  }*/

  /**
   * 标签的开始函数
   */
  public int doStartTag() throws JspTagException
  {
    lookupObj = LookupFacade.getInstance((HttpServletRequest)pageContext.getRequest(), getName());
    pageContext.setAttribute( id, lookupObj );
    return SKIP_BODY;
  }
}