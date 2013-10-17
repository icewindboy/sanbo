package engine.project;

import com.borland.dx.dataset.Column;
import com.borland.jb.util.FastStringBuffer;
import engine.dataset.RowMap;
import engine.dataset.sql.QueryWhereField;
import engine.util.*;
import java.sql.Date;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


public class QueryForm extends QueryBasic
{

    private FastStringBuffer itemBuf;
    private FastStringBuffer opsignBuf;
    private FastStringBuffer linkBuf;
    private String postUrl;

    private QueryForm()
    {
        itemBuf = new FastStringBuffer();
        opsignBuf = new FastStringBuffer();
        linkBuf = new FastStringBuffer();
        postUrl = null;
    }

    public void addShowColumn(String s, QueryWhereField aquerywherefield[])
    {
        for(int i = 0; i < aquerywherefield.length; i++)
        {
            boolean flag = aquerywherefield[i].getLinkTable() != null;
            boolean flag1 = aquerywherefield[i].getExtendName() != null;
            String s3 = "/table/".concat(String.valueOf(String.valueOf(flag ? ((Object) (aquerywherefield[i].getLinkTable())) : ((Object) (s)))));
            String s1 = aquerywherefield[i].getColumn().getColumnName().toUpperCase();
            String s4;
            if(flag)
                s4 = flag1 ? aquerywherefield[i].getExtendName().toUpperCase() : aquerywherefield[i].getQueryColumn().toUpperCase();
            else
                s4 = flag1 ? aquerywherefield[i].getExtendName().toUpperCase() : s1;
            String s2 = PropertyManager.getPropertyManager(s3, true, "UTF-8").getProp(s4);
            if(s2 == null)
                s2 = s4;
            columnNames.add(String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(s.toUpperCase())))).append("_").append(s1))));
            columnCaptions.add(s2);
            columnsArray.add(aquerywherefield[i]);
        }

    }

    public String getItemOption(String s)
    {
        String s1 = searchRow.get(s);
        if(itemBuf.length() > 0)
            itemBuf.empty();
        for(int i = 0; i < columnNames.size(); i++)
        {
            itemBuf.append("<option value='");
            itemBuf.append(columnNames.get(i));
            itemBuf.append("'");
            if(columnNames.get(i).equals(s1))
                itemBuf.append(" selected");
            itemBuf.append(">");
            itemBuf.append(columnCaptions.get(i));
            itemBuf.append("</option>");
        }

        return itemBuf.toString();
    }

    public String getOpsignOption(String s)
    {
        String s1 = searchRow.get(s);
        if(opsignBuf.length() > 0)
            opsignBuf.empty();
        opsignBuf.append(String.valueOf(String.valueOf((new StringBuffer("<option value='='")).append(s1.equals("=") ? " selected" : "").append(">等于</option>"))));
        opsignBuf.append(String.valueOf(String.valueOf((new StringBuffer("<option value='>'")).append(s1.equals(">") ? " selected" : "").append(">大于</option>"))));
        opsignBuf.append(String.valueOf(String.valueOf((new StringBuffer("<option value='<'")).append(s1.equals("<") ? " selected" : "").append(">小于</option>"))));
        opsignBuf.append(String.valueOf(String.valueOf((new StringBuffer("<option value='>='")).append(s1.equals(">=") ? " selected" : "").append(">大于等于</option>"))));
        opsignBuf.append(String.valueOf(String.valueOf((new StringBuffer("<option value='<='")).append(s1.equals("<=") ? " selected" : "").append(">小于等于</option>"))));
        opsignBuf.append(String.valueOf(String.valueOf((new StringBuffer("<option value='<>'")).append(s1.equals("<>") ? " selected" : "").append(">不等于</option>"))));
        opsignBuf.append(String.valueOf(String.valueOf((new StringBuffer("<option value='LIKE'")).append(s1.equals("LIKE") ? " selected" : "").append(">包含</option>"))));
        return opsignBuf.toString();
    }

    public String getLinkOption(String s)
    {
        String s1 = searchRow.get(s);
        if(linkBuf.length() > 0)
            linkBuf.empty();
        linkBuf.append(String.valueOf(String.valueOf((new StringBuffer("<option value='AND'")).append(s1.equals("AND") ? " selected" : "").append(">并且</option>"))));
        linkBuf.append(String.valueOf(String.valueOf((new StringBuffer("<option value='OR'")).append(s1.equals("OR") ? " selected" : "").append(">或者</option>"))));
        return linkBuf.toString();
    }

    public void setSearchValue(HttpServletRequest httpservletrequest)
    {
        searchRow.put(httpservletrequest);
    }

    public static synchronized QueryForm getInstance(HttpServletRequest httpservletrequest)
    {
        HttpSession httpsession = httpservletrequest.getSession(true);
        StringBuffer stringbuffer = httpservletrequest.getRequestURL();
        String s = stringbuffer.toString();
        String s1 = stringbuffer.append("_SearchFrom").toString();
        QueryForm queryform = (QueryForm)httpsession.getAttribute(s1);
        if(queryform == null)
        {
            queryform = new QueryForm();
            queryform.postUrl = s;
            httpsession.setAttribute(s1, queryform);
        }
        return queryform;
    }

    public String getPostUrl()
    {
        return postUrl;
    }

    public String getWhereQuery()
    {
        FastStringBuffer faststringbuffer = new FastStringBuffer();
        Object obj = null;
        Object obj1 = null;
        Object obj2 = null;
        for(int i = 1; i <= 6; i++)
        {
            String s = searchRow.get("value".concat(String.valueOf(String.valueOf(i))));
            if(s == null || s.equals(""))
                continue;
            String s1 = searchRow.get("item".concat(String.valueOf(String.valueOf(i))));
            int j = columnNames.indexOf(s1);
            if(j < 0)
                continue;
            String s2 = searchRow.get("opsign".concat(String.valueOf(String.valueOf(i))));
            boolean flag = s2.equals("LIKE");
            QueryColumn querycolumn = (QueryColumn)columnsArray.get(j);
            faststringbuffer.append(" ");
            faststringbuffer.append(s1);
            if(querycolumn.getLinkTable() == null)
            {
                int k = querycolumn.getColumn().getDataType();
                boolean flag1 = false;//k == 13 || k == 15;胡康宁
                if(flag1)
                    try
                    {
                        Date.valueOf(s);
                    }
                    catch(Exception exception)
                    {
                        continue;
                    }
                faststringbuffer.append(" ");
                if(!flag1)
                    faststringbuffer.append(s2);
                else   if(flag)
                {
                    faststringbuffer.append("=to_date(");
                } else
                {
                    faststringbuffer.append(s2);
                    faststringbuffer.append("to_date(");
                }
                faststringbuffer.append(flag1 ? "'" : flag ? "'%" : "'");
                faststringbuffer.append(StringUtils.replaceInvalid(s));
                faststringbuffer.append(flag1 ? "','YYYY-MM-DD') " : flag ? "%' " : "' ");
            } else
            {
                faststringbuffer.append(" IN (SELECT ");
                faststringbuffer.append(querycolumn.getLinkColumn());
                faststringbuffer.append(" FROM ");
                faststringbuffer.append(querycolumn.getLinkTable());
                faststringbuffer.append(" WHERE ");
                faststringbuffer.append(querycolumn.getQueryColumn());
                faststringbuffer.append(" ");
                faststringbuffer.append(s2);
                faststringbuffer.append(flag ? "'%" : "'");
                faststringbuffer.append(StringUtils.replaceInvalid(s));
                faststringbuffer.append(flag ? "%') " : "') ");
            }
            if(i != 6)
                faststringbuffer.append(searchRow.get("link".concat(String.valueOf(String.valueOf(i)))));
        }

        String s3 = faststringbuffer.toString();
        faststringbuffer = null;
        if(s3.endsWith("AND"))
            s3 = s3.substring(0, s3.length() - 3);
        else
        if(s3.endsWith("OR"))
            s3 = s3.substring(0, s3.length() - 2);
        return s3;
    }
}
