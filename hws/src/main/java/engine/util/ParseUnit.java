package engine.util;

/**
 * Title:        HTML分析单元
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      JAC
 * @author hukn
 * @version 1.0
 */

import java.io.*;
import java.util.*;

public class ParseUnit implements java.io.Serializable{
  private String rawText;
  private int flags;
  private TagDetail tagDetail;

  public ParseUnit( String chars, int flag ) {
    rawText = chars;
    if ( flag == FLAG_SCRIPT || flag == FLAG_STYLE ) {
      flags = flag;
    }
    else {
      determineType();
    }
    if ( isTag() ){
      if ( isEndTag() ) {
        tagDetail = new TagDetail( rawText.substring( 2 ) );
      }
      else if ( isEmptyTag() ) {
        tagDetail = new TagDetail( rawText.substring( 1, rawText.length() - 2 ) );
      }
      else {
        tagDetail = new TagDetail( rawText.substring( 1, rawText.length() - 1 ) );
      }
    }
  }

  public boolean isTag()         { return (flags & FLAG_TAG) != 0; }
  public boolean isMainTag()     { return (flags & (FLAG_TAG | FLAG_END)) == FLAG_TAG; }
  public boolean isStartTag()    { return (flags == FLAG_TAG); }
  public boolean isEndTag()      { return (flags & FLAG_END) != 0; }
  public boolean isEmptyTag()    { return (flags & FLAG_EMPTY) != 0; }
  public boolean isComment()     { return (flags & FLAG_COMMENT) != 0; }
  public boolean isScriptBody()  { return (flags & FLAG_SCRIPT) != 0; }
  public boolean isStyleBody()   { return (flags & FLAG_STYLE) != 0; }
  public boolean isJsp()         { return (flags & FLAG_JSP) != 0; }
  public boolean isDeclaration() { return (flags & FLAG_DECL) != 0; }

  public String toString() {
    return rawText;
  }

  public String getTagName() {
    if ( isTag() ) {
      return tagDetail.getElementName();
    }
    else {
      return null;
    }
  }

  public String getIdAttribute() {
    if ( isTag() ) {
      return tagDetail.getIdAttribute();
    }
    else {
      return null;
    }
  }

  public String getNameAttribute() {
    if ( isTag() ) {
      return tagDetail.getNameAttribute();
    }
    else {
      return null;
    }
  }

  /**
   * Attribute names are always lowercase.
   */
  public String getAttribute( String name ) {
    if ( isTag() ) {
      return tagDetail.getAttribute( name );
    }
    else {
      return null;
    }
  }

  public Map getAttributes() {
    return tagDetail.getAttributes();
  }

  public int getFormNumber() {
    if ( isTag() ) {
      return tagDetail.getFormNumber();
    }
    else {
      return -1;
    }
  }

  public void setFormNumber( int number ) {
    if ( isTag() ) {
      tagDetail.setFormNumber( number );
    }
  }

  public String getFormName() {
    if ( isTag() ) {
      return tagDetail.getFormName();
    }
    else {
      return null;
    }
  }

  public void setFormName( String name ) {
    if ( isTag() ) {
      tagDetail.setFormName( name );
    }
  }


  public static final int FLAG_TAG     = 0x01;
  public static final int FLAG_END     = 0x02;
  public static final int FLAG_EMPTY   = 0x04;
  public static final int FLAG_COMMENT = 0x08;
  public static final int FLAG_SCRIPT  = 0x10;
  public static final int FLAG_STYLE   = 0x20;
  public static final int FLAG_DECL    = 0x40;
  public static final int FLAG_JSP     = 0x80;

  static final char   START_TAG         = '<';
  static final char   START_ENDTAG      = '/';
  static final String END_EMPTYTAG      = "/>";
  static final String START_COMMENT     = "!--";
  static final char   START_JSP         = '%';
  static final String START_JSP_COMMENT = "%--";
  static final char   START_DECL        = '!';
  static final char   START_DECL_XML    = '?';

  private void determineType() {
    flags = 0;
    if ( rawText.length() >= 3 && !rawText.equals( "</>" ) ) {
      if ( rawText.charAt( 0 ) == START_TAG ) {
        char c2 = rawText.charAt( 1 );
        if ( c2 == START_ENDTAG ) {
          flags |= FLAG_TAG | FLAG_END;
        }
        else if ( rawText.endsWith( END_EMPTYTAG ) ) {
          flags |= FLAG_TAG | FLAG_EMPTY;
        }
        else if ( rawText.startsWith( START_COMMENT, 1 ) ) {
          flags |= FLAG_COMMENT;
        }
        else if ( rawText.startsWith( START_JSP_COMMENT, 1 ) ) {
          flags |= FLAG_COMMENT | FLAG_JSP;
        }
        else if ( c2 == START_JSP ) {
          flags |= FLAG_JSP;
        }
        else if ( c2 == START_DECL || c2 == START_DECL_XML ) {
          flags |= FLAG_DECL;
        }
        else {
          flags |= FLAG_TAG;
        }
      }
    }
  }

}


class TagDetail {
  private String elementName;
  private Map attributes = new HashMap();
  private int formNum = -1;
  private String formName = null;

  public TagDetail( String tagContent ) {
    parseTag( tagContent );
  }

  public Map getAttributes() {
    return attributes;
  }

  public String getElementName() {
    return elementName;
  }

  public String getIdAttribute() {
    return (String)attributes.get( "id" );
  }

  public String getNameAttribute(){
    return (String)attributes.get( "name" );
  }

  public int getFormNumber() {
    return formNum;
  }

  public void setFormNumber( int number ) {
    formNum = number;
    if ( formName == null ) {
      formName = "";
    }
  }

  public String getFormName() {
    return formName;
  }

  public void setFormName( String name ) {
    formName = name;
  }

  /**
   * Returns value of named attribute
   * @param name attribute name, must be lowercase
   * @return value of attribute, or null if not defined
   */
  public String getAttribute( String name ){
    return (String)attributes.get( name );
  }

  public String toString(){
    StringBuffer ret = new StringBuffer( elementName );
    Set keys = attributes.keySet();
    Iterator i = keys.iterator();
    String key;
    while ( i.hasNext() ) {
      key = (String)i.next();
      ret.append( ' ' );
      ret.append( key );
      ret.append( "=\"" );
      ret.append( attributes.get( key ) );
      ret.append( '"' );
    }
    return ret.toString();
  }


  public void parseTag( String tagContent ){
    tagContent = StringUtils.stripEnterSymbol(tagContent);
    StreamTokenizer st = new StreamTokenizer( new StringReader( tagContent ) );
    boolean gotName = false;
    boolean expectValue = false;
    String attribute = null;

    st.slashSlashComments( true );
    st.slashStarComments( false );
    st.ordinaryChars( '-', '9' );
    st.wordChars( '0', '9' );
    st.wordChars( '-', '.' );
    st.ordinaryChar( '%' );
    st.wordChars( '%', '%' );
    st.ordinaryChar( ':' );
    st.wordChars( ':', ':' );

    try {
      int ttype = st.nextToken();
      while ( ttype != st.TT_EOF ){
        switch ( ttype ){
          case '=':
            expectValue = ( attribute != null );
            break;
          case StreamTokenizer.TT_WORD:
            if ( !gotName ) {
              elementName = st.sval.toLowerCase();
              gotName = true;
              break;
            }
          case '\'':
          case '\"':
            if ( expectValue ) {
              attributes.put( attribute, st.sval );
              expectValue = false;
              attribute = null;
            }
            else {
              if ( attribute != null ) {
                attributes.put( attribute, attribute );
              }
              attribute = st.sval.toLowerCase();
            }
            break;
        }
        ttype = st.nextToken();
      }
    }
    catch( IOException iox ) {
      iox.printStackTrace();
    }
  }

}