package engine.erp.baseinfo;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineRow;
import engine.dataset.SequenceDescriptor;
import engine.dataset.RowMap;
import engine.action.BaseAction;
import engine.action.Operate;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;
import engine.project.*;
import engine.html.*;
import engine.common.*;
import engine.util.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.Date;
import java.util.Hashtable;
import java.util.Enumeration;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSession;

import com.borland.dx.dataset.*;

/**
 * <p>Title: 基础类</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author 李建华
 * @version 1.0
 */

public abstract class BasePublicClass extends BaseAction
{
  public BasePublicClass()
  {
  }
  /**
   * @param ds 传递进来的数据集
   * @param fieldname 该数据集中所要判断的字段
   * @return 返回该数据集中该字段是否有记录，如果有返回false.没有返回true;
   */
  public static final boolean isRework(EngineDataSet ds, String fieldname)
  {
    int rowcount = ds.getRowCount();
    ds.first();
    for(int i=0; i<rowcount; i++)
    {
      if(ds.getBigDecimal(fieldname).doubleValue() !=0)
        return false;
      ds.next();
    }
    return true;
  }
  /**
   * @param ds 传递进来的数据集
   * @param fieldname 该数据集中所要判断的字段
   * @param fieldname 该数据集中第几条记录
   * @return 返回该数据集中第row行纪录该字段是否有记录，如果有返回false.没有返回true;
   */
  public static final boolean isRevamp(EngineDataSet ds, String fieldname, int row)
  {
    ds.goToRow(row);
    if(ds.getBigDecimal(fieldname).doubleValue() != 0)
      return false;
    return true;
  }
  /**
   *
   *@return 返回boolean型,根据入参判断单据是否能被title例取消审批，强制完成;
   */
   public static final boolean checkstate(String zt, String Loginid, String sprid, String title, boolean hasLimits)
   {
     if(zt.equals("1") && sprid.equals(Loginid) && title.equals("取消审批"))
       return true;
     if(!zt.equals("0") && !zt.equals("9") && !zt.equals("8") && title.equals("强制完成") && hasLimits)
       return true;
     return false;
   }

   private static ArrayList keys = new ArrayList();
   private static Hashtable table = new Hashtable();
   /**
    * 分割字符串s,用sep分割成一个字符串数组.并保存到HashTable中
    * 返回字符串，是Hash表中key为field的值

   public synchronized static final String parseEspecialString(String s, String field, String sep)
   {
   }
   */
   /**
    * 分割字符串s,用sep分割成一个字符串数组.并保存到HashTable中
    * 返回字符串，是Hash表中key为field的值
    */
   public synchronized static final String parseEspecialString(String s, String field, String sep)
   {
     if(s==null || s.equals("") || field.equals("") || field==null)
       return "1";
     String[] code = parseString(s, sep);
     String key=null, value = null;
     keys.clear();
     table.clear();
     for(int i=0; i<code.length; i++)
     {
       if(i%2 > 0){
         value = code[i];
         table.put(key, value);//把类别属性如（宽度，厂家）作为HashTable的key,而（英捷公司）这则为value
       }
       else{
         key = code[i].trim();
         keys.add(key);
       }
     }
     if(!keys.contains(field))
       return "0";
     else{
       String temp = table.get(field).toString();
       temp = temp.equals("") ? "0" : temp;
       return temp;
     }
   }
   /**
    *  参数为一个存有数据的table
    *  返回一个字符串，table中所有元素的最小值
    */
   public static final String getMinString(Hashtable table)
   {
     int num = table.size();
     if(num<1)
       return null;
     String val = null;//val为HashTable中的某一元素值
     BigDecimal value = null; //val为HashTable中的某一元素值，转换为BigDecemial类型
     BigDecimal minValue= null;//temp为临时字符串保存HashTable中的某两个值中小的元素的值
     int isMin = 0;// 两个BigDecimal值比较返回Int型，-1为小于，0为等于，1为大于
     Enumeration values = table.elements();//HASHTABLE中所有元素的Value
     while(values.hasMoreElements())//枚举值的元素从第一个开始比较到小
     {
       value= (BigDecimal)values.nextElement();
       if(minValue == null)
         minValue = value;
       else{
         isMin = minValue.compareTo(value);
         if(isMin > 0)//如果临时数据小于或则等于这个value,临时数据值不变
           minValue = value;
       }
     }
     if(minValue == null)
       return null;
     else
       return minValue.toString();
   }
   /**
    * 校验属性值是否匹配需要查询的表达式
    * @param searchExps 查询的表达式
    * @param sxz 数据集中的属性值
    * @return 返回是否匹配
    */
   public static final boolean matchPropertyValue(Hashtable searchExps, String sxz, boolean isInit)
   {
     //保存属性值拆分的key和value
     Hashtable mapSxz = new Hashtable();
     if(mapSxz.size() > 0)
       mapSxz.clear();
     //分解属性值
     String[] sxzs = engine.util.StringUtils.parseString(sxz, "()");
     String key = null,  value = null;
     for(int i=0; i<sxzs.length; i++)
     {
       if(i%2 == 0)
         key = sxzs[i].trim();
       else
         mapSxz.put(key, sxzs[i].trim());
     }
     if(mapSxz.size() == 0)
       return false;
     //判断属性值
     Map.Entry[] entrys = (Map.Entry[])searchExps.entrySet().toArray(new Map.Entry[searchExps.size()]);
     for(int i=0; i<entrys.length; i++)
     {
       Object searchKey = entrys[i].getKey();//固定为"宽度"
       Object searchValue = entrys[i].getValue();//得到HashTable中key为宽度的value
       //查询的条件是否是字符串的
       if(searchValue instanceof String)//得到的HashTable中的value是否是字符串
       {
         if(!searchValue.equals(mapSxz.get(searchKey)))
           return false;
       }
       else
       {
         String sxzValue = (String)mapSxz.get(searchKey);
         if(sxzValue ==null || sxzValue.length() == 0 || !isDouble(sxzValue))
           return false;
         BigDecimal bdSxzValue = new BigDecimal(sxzValue);
         if(!isInit){//查询时
           BigDecimal[] values = (BigDecimal[])searchValue;
           //若小于最小值剔除
           if(values[0] != null && bdSxzValue.compareTo(values[0]) < 0)
             return false;
           //若大于最大值剔除
           if(values[1] != null && bdSxzValue.compareTo(values[1]) > 0)
             return false;
         }
         else{//初始化
           BigDecimal values = (BigDecimal)searchValue;
           if(values !=null && bdSxzValue.compareTo(values) < 0)
             return false;
         }
       }
     }
     return true;
   }
}


