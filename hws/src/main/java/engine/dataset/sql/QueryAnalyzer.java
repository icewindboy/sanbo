package engine.dataset.sql;

import java.lang.*;
import java.util.*;
import java.sql.*;

import engine.dataset.database.RuntimeMetaData;
import engine.dataset.database.Database;

class QueryAnalyzer implements java.io.Serializable
{
  private   String            query;                   // setup in constructor
            Database          db;                      // setup in constructor
            char              quoteCharacter;          //    "
            boolean           adjustIdentifiers;       //    "
  private   boolean           isCaseInsensitive;       //    "
  private   boolean           mkUpper;                 //    "
  private   boolean           isCaseInsensitiveQuoted; //    "
  private   boolean           mkUpperQuoted;           //    "
  private   boolean           couldParse;
            String            userName;                // setup in init() from database
            SimpleParser      parser;                  // setup in parse()

  QueryAnalyzer(Database db, String query) {
    this.db = db;
    this.query = query;
  }

  // Scan token list from parser to determine number of tables
  // in SELECT clause. Returns a Vector of strings. Each table
  // is represented by 4 strings: tableName, schemaName,
  // databaseName, and aliasName.
  //
  Vector getTables()
  {
    if (parser == null)
      parse();

    QueryParseToken   tokens = parser.getParsedTokens();
    String            tableName, schemaName, aliasName;
    Vector            result = new Vector(1);

    //Diagnostic.trace(Trace.QueryAnalyze,"Get table names from parser");

    while (tokens != null) {

      if (tokens.isTable())
      {
        SQLElement element = new SQLElement(tokens);
        element.unquoteStrings(this);
        result.addElement(element);
      }
      tokens = tokens.getNextToken();
    }

    return result;
  }


  Vector getColumns() /*-throws DataSetException-*/ {
    if (parser == null)
      parse();

    QueryParseToken   tokens = parser.getParsedTokens();
    String            columnName, tableName, aliasName;
    Vector            result = new Vector(10);

    while (tokens != null && !tokens.isWhere()) {

      if (tokens.isField()) {
        SQLElement element = new SQLElement(tokens);
        element.unquoteStrings(this);
        result.addElement(element);
      }
      else if (tokens.isExpression()) {
        result.addElement(null);
      }
      tokens = tokens.getNextToken();
    }

    return result;
  }

  Vector getParameters() /*-throws DataSetException-*/ {
    if (parser == null)
      parse();

    QueryParseToken   tokens = parser.getParsedTokens();
    String            name;
    Vector            result = new Vector(10);

    while (tokens != null) {

      if (tokens.isParameter()) {
        name = tokens.getName();
        name = unquoteString(name);
        result.addElement(name);
      }

      tokens = tokens.getNextToken();
    }

    return result;
  }

  String adjustCase(String dbString) {
    //Diagnostic.check(adjustIdentifiers);
    if (quoteCharacter != '\0') {
      if (mkUpperQuoted)
        return dbString.toUpperCase();
      else
        return dbString.toLowerCase();
    }
    else {
      if (mkUpper)
        return dbString.toUpperCase();
      else
        return dbString.toLowerCase();
    }
  }

  String unquoteString(String dbString)
  {
    if (dbString == null || dbString.length() == 0)
      return null;

    if (dbString.charAt(0) != quoteCharacter) {
      if (isCaseInsensitive) {
        if (mkUpper)
          dbString = dbString.toUpperCase();
        else
          dbString = dbString.toLowerCase();
      }
    }
    else {
      dbString = dbString.substring(1,dbString.length()-1);

      if (isCaseInsensitiveQuoted) {
        if (mkUpperQuoted)
          dbString = dbString.toUpperCase();
        else
          dbString = dbString.toLowerCase();
      }
    }
    return dbString;
  }

  boolean parse() /*-throws DataSetException-*/ {
    //Diagnostic.trace(Trace.QueryAnalyze, "Invoking parser");
    init();
    parser = new SimpleParser(query, quoteCharacter);
    Object tokens = parser.getParsedTokens();
    couldParse = (tokens != null);
    return couldParse;
  }

  boolean couldParse() {
    return couldParse;
  }

  void init()
  {
    RuntimeMetaData runtimeMetaData = db.getRuntimeMetaData();
    isCaseInsensitive = !runtimeMetaData.isUseCaseSensitiveId();
    mkUpper = isCaseInsensitive && !runtimeMetaData.storesLowerCaseId();
    adjustIdentifiers = isCaseInsensitive;

    quoteCharacter = runtimeMetaData.getIdentifierQuoteChar();
    if (quoteCharacter != '\0') {
      isCaseInsensitiveQuoted = !runtimeMetaData.isUseCaseSensitiveQuotedId();
      mkUpperQuoted = isCaseInsensitiveQuoted && !runtimeMetaData.storesLowerCaseId();
      adjustIdentifiers = isCaseInsensitiveQuoted;
    }

    try {
      userName = runtimeMetaData.getMetaData().getUserName();
      userName = unquoteString(userName);
    }
    catch (Exception ex) {
      ex.printStackTrace();
      userName = null;
    }
  }
}
