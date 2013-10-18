<%--JavaScript函数--%>
<%@ page contentType="text/html; charset=UTF-8"%><%@ include file="../pub/init.jsp"%>
<html>
<head>
<title></title>
<link rel="stylesheet" href="../scripts/public.css" type="text/css">
</head>
<script LANGUAGE="javascript">
/**
 * sxz 要解析的字符串
 * s    第一次分割索引，分隔掉s第一次出现的位置前面的字符
 * t    第二次分割的索引，分割第一次分割后的字符串，把第一次出现t后面的字符隔掉
 * y    最后一次分割的索引，分割第二次分割后的字符串，分隔掉y第一次出现的位置前面的字符
 */
  function parseString(sxz,s,t,y){
     if(sxz=='' || s=='' || t=='' || y=='')
        return '';
     else{
       leng = sxz.length;//得到字符串长度
       start= sxz.indexOf(s);//得到sxz第一个s出现的位置
       startValue = sxz.substring(start, leng);//截掉第一个出现位置前面的字符
       end = startValue.indexOf(t);
       temp = startValue.substring(0,end);
       cur= temp.indexOf(y);
       value = temp.substring(cur+1, temp.length);
       return value;
     }
  }
  function addDate(sdate,sl)
  {
    var datestr = sdate.replace(/-/gi, "/");
    var dt = new Date(datestr);
    var dt2 = new Date(dt.getYear() + "/" + (dt.getMonth() + 1) + "/" + (dt.getDate()+sl));
    var obj = dt2.getYear() + "-" + (dt2.getMonth() + 1) + "-" + dt2.getDate();
    return obj;
  }
</script>
<BODY>
</body>
</html>
