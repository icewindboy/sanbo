package engine.util;

import java.io.*;
import java.util.*;
import java.net.URL;
import engine.util.StringUtils;
/**
 * Title:        HTML类
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:      JAC
 * @author hukn
 * @version 1.0
 */

public class HtmlParser implements Serializable {

  private static Map parseCache = new HashMap();
  private ParseUnit[] units;
  private List unitList = new ArrayList();

  private static class ParsePackage {
    public long timeStamp;
    public ParseUnit[] units;
    public ParsePackage( long timeStamp, ParseUnit[] units ) {
      this.timeStamp = timeStamp;
      this.units = units;
    }
  }

  public ParseUnit[] parse( String filename ) throws IOException {
    return parse(filename, null, null);
  }

  public ParseUnit[] parse( String filename, String charsetName)
      throws IOException {
    return parse(filename, charsetName, null);
  }

  public ParseUnit[] parse( String filename, Map replaces)
      throws IOException {
    return parse(filename, null, replaces);
  }

  public ParseUnit[] parse( String filename, String charsetName, Map replaces)
      throws IOException
  {
    File file = new File( filename );
    filename = file.getCanonicalPath();
    long timeStamp = file.lastModified();
    ParsePackage cachedParse = (ParsePackage)parseCache.get( filename );
    if ( cachedParse != null ) {
      if ( timeStamp == cachedParse.timeStamp ) {
        return cachedParse.units;
      }
    }

    if(charsetName == null)
      charsetName = "UTF-8";
    BufferedReader in = new BufferedReader(
        new InputStreamReader(new FileInputStream( filename ), charsetName)
        );
    parseStream( in, replaces);

    synchronized ( parseCache ) {
      parseCache.put( filename, new ParsePackage( timeStamp, units ) );
    }
    return units;
  }

  public ParseUnit[] parseString( String buffer) throws IOException {
    return parseString(buffer, null);
  }

  public ParseUnit[] parseString( String buffer, Map replaces ) throws IOException {
    BufferedReader in = new BufferedReader( new StringReader( buffer ));
    parseStream( in, replaces );
    return units;
  }

  public ParseUnit[] parseUrl( URL url) throws IOException {
    return parseUrl(url, null);
  }

  public ParseUnit[] parseUrl( URL url, Map replaces ) throws IOException {
    BufferedReader in = new BufferedReader( new InputStreamReader( url.openStream() ));
    parseStream( in, replaces );
    return units;
  }

  public ParseUnit[] parseFile(InputStream io, String charsetName)
      throws IOException
  {
    return parseFile(io, charsetName, null);
  }

  public ParseUnit[] parseFile(InputStream io, Map replaces)
      throws IOException
  {
    return parseFile(io, null, replaces);
  }

  public ParseUnit[] parseFile(InputStream io, String charsetName, Map replaces)
      throws IOException
  {
    if(charsetName == null||charsetName.equals(""))
      charsetName = "UTF-8";
    BufferedReader in = new BufferedReader(new InputStreamReader(io, charsetName));
    parseStream( in, replaces );
    return units;
  }

  private static final int NORMAL      = 0;
  private static final int COMMENT     = ParseUnit.FLAG_COMMENT;
  private static final int SCRIPT_BODY = ParseUnit.FLAG_SCRIPT;
  private static final int STYLE_BODY  = ParseUnit.FLAG_STYLE;
  private static final int JSP         = ParseUnit.FLAG_JSP;
  private static final int JSP_COMMENT = ParseUnit.FLAG_JSP | ParseUnit.FLAG_COMMENT;

  private int mode;
  private StringBuffer buf;
  private String test;

  private void parseStream( BufferedReader in, Map replaces) throws IOException {
    int c;
    ParseUnit unit;

    unitList.clear();
    mode = NORMAL;
    buf = new StringBuffer();

    c = in.read();
    while( c >= 0 ){
      switch ( c ) {
        case '<':
          if ( mode == NORMAL && buf.length() > 0 ) {
            test = buf.toString();
            if ( test.startsWith( "<!--" ) ) {
              mode = COMMENT;
            }
            else if ( test.startsWith( "<%--" ) ) {
              mode = JSP_COMMENT;
            }
            else if ( test.startsWith( "<%" ) ) {
              mode = JSP;
            }
            else {
              if(replaces != null)
                test = StringUtils.replaceStrings(test, replaces);
              //test = StringUtils.stripEnterSymbol(test);
              unitList.add( new ParseUnit( test, mode ));
              buf.setLength( 0 );
            }
          }
          buf.append( (char)c );
          break;
        case '>':
          buf.append( (char)c );
          test = buf.toString();
          if ( mode == NORMAL ) {
            if ( test.startsWith( "<!--" ) ) {
              mode = COMMENT;
            }
            else if ( test.startsWith( "<%--" ) ) {
              mode = JSP_COMMENT;
            }
            else if ( test.startsWith( "<%" ) ) {
              mode = JSP;
            }
          }
          switch ( mode ) {
            case COMMENT:
              if ( !test.endsWith( "-->" ) ) {
                test = null;
              }
              break;
            case JSP_COMMENT:
              if ( !test.endsWith( "--%>" ) ) {
                test = null;
              }
              break;
            case JSP:
              if ( !test.endsWith( "%>" ) ) {
                test = null;
              }
              break;
            case SCRIPT_BODY:
              checkBodyEnd( "</SCRIPT>", replaces );
              break;
            case STYLE_BODY:
              checkBodyEnd( "</STYLE>", replaces );
              break;
          }

          if ( test != null ) {
            //
            if(replaces != null)
              test = StringUtils.replaceStrings(test, replaces);

            unit = new ParseUnit( test, mode );
            unitList.add( unit );
            buf.setLength( 0 );
            mode = NORMAL;
            if ( unit.isStartTag() ) {
              test = unit.getTagName();
              if ( test.equals( "script" ) ) {
                mode = SCRIPT_BODY;
              }
              else if ( test.equals( "style" ) ) {
                mode = STYLE_BODY;
              }
            }
          }
          break;
        default:
          buf.append( (char)c );
      }
      c = in.read();
    }
    if ( buf.length() > 0 ) {
      String temp = buf.toString();
      if(replaces != null)
        temp = StringUtils.replaceStrings(temp, replaces);

      unitList.add( new ParseUnit( temp, mode ));
    }
    units = new ParseUnit[ unitList.size() ];
    unitList.toArray( units );
  }


  private void checkBodyEnd( String endTag, Map replaces) {
    int index = test.length() - endTag.length();
    if ( index >= 0 ) {
      test = test.substring( index );
      if ( test.equalsIgnoreCase( endTag ) ) {
        if ( index > 0 ) {

          buf.setLength( index );
          String temp = buf.toString();
          if(replaces != null)
            temp = StringUtils.replaceStrings(temp, replaces);

          unitList.add( new ParseUnit( temp, mode ));
        }
        mode = NORMAL;
      }
      else {
        test = null;
      }
    }
    else {
      test = null;
    }
  }
}
