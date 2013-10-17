package engine.project;

import java.math.BigDecimal;
import java.util.StringTokenizer;

import javax.ejb.EJBObject;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.borland.dx.dataset.DataRow;
import com.borland.dx.dataset.DataSet;
import com.borland.dx.dataset.ReadRow;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineDataSetProvider;
import engine.dataset.EngineDataSetResolver;
import engine.dataset.LocateUtil;
import engine.dataset.ProvideInfo;
import engine.util.Format;
import engine.util.StringUtils;
import engine.util.log.Log;

/**
 * Title: 自定义的Beans Description: 1.客户端TableDatSet的Provide和Resolve
 * 2.服务端更新提交的数据到数据库
 * 
 * Copyright: Copyright (c) 2002 Company: ENGINE
 * 
 * @author hukn
 * @version 1.0
 */

public abstract class CommonClass implements HttpSessionBindingListener,
		java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7472334113572322879L;
	protected EJBObject sessionBeanRemote; // SessionBean的连接的类
	protected EngineDataSetProvider dataSetProvider; //
	protected EngineDataSetResolver dataSetResolver;
	protected EngineDataSet cdsTemp; // 临时的数据集
	protected String provideMethodName = "provideData";// 提供数据的方法名称
	protected String resolveMethodName = "resovleData";// 提交数据的方法名称

	protected Log log = new Log(childClassName());// 日志对象

	/**
	 * 得到子类的类名
	 * 
	 * @return 返回子类的类名
	 */
	protected abstract Class childClassName();

	public void valueBound(HttpSessionBindingEvent event) {
		return;
	};

	/**
	 * 确却的查找并定位数据集（在本地的数据集中找不到的情况下，到数据库中去找，找到后加到入参的数据集中）
	 * 
	 * @param ds
	 *            数据集
	 * @param fieldName
	 *            字段名
	 * @param value
	 *            查找字段的值
	 * @param locatedb_sql
	 *            本地的数据集中找不到的情况下到数据库查找的SQL
	 * @return 是否找到
	 */
	protected boolean sureLocateData(EngineDataSet cds, ReadRow locateRow,
			String locatedb_sql) throws Exception {
		if (cds == null)
			return false;
		// 如果数据集没有打开直接打开
		if (!cds.isOpen()) {
			cds.setQueryString(locatedb_sql);
			cds.open();
			return cds.getRowCount() > 0;
		}

		if (locateRow == null)
			return false;

		// 查找记录
		if (cds.locate(locateRow, LocateUtil.FIRST))
			return true;
		else {
			// 当前数据集找不到，到数据库找
			if (locatedb_sql == null)
				return false;
			// 到数据库查找
			locateFromDB(cds, locatedb_sql);
			return cds.locate(locateRow, LocateUtil.FIRST);
		}
	}

	/**
	 * 在本地的数据集中找不到的情况下，到数据库中去找，找到后加到入参的数据集中
	 * 
	 * @param cds
	 *            需要添加的数据集
	 * @param sql
	 *            SQL语句
	 */
	protected void locateFromDB(EngineDataSet cds, String sql) throws Exception {
		ProvideInfo[] provider = new ProvideInfo[] { new ProvideInfo(sql) };
		provider = dataSetProvider.getDataSetData_info(provider);
		provider[0].getProvideData().loadDataSet(cds);
		provider = null;
	}

	/**
	 * 设置数据提供和提交对象属性
	 */
	private void setProviderResolver() {
		// if(dataSetProvider == null)
		dataSetProvider = new EngineDataSetProvider();
		if (sessionBeanRemote != null
				&& dataSetProvider.getSessionBeanRemote() == null)
			dataSetProvider.setSessionBeanRemote(sessionBeanRemote);

		// if(dataSetResolver == null)
		dataSetResolver = new EngineDataSetResolver();
		if (sessionBeanRemote != null
				&& dataSetResolver.getSessionBeanRemote() == null)
			dataSetResolver.setSessionBeanRemote(sessionBeanRemote);
	}

	/**
	 * 为数据集设置与SessionBean连接的属性
	 * 
	 * @param cds
	 *            数据集
	 * @param sql
	 *            SQL语句
	 */
	protected void setDataSetProperty(EngineDataSet cds, String sql) {
		if (cds == null)
			throw new RuntimeException("the EngineDataSet is null");
		// 设置数据提供和提交对象
		setProviderResolver();

		cds.setProvider(dataSetProvider);
		cds.setResolver(dataSetResolver);
		// 设置数据提供和提交方法名称
		cds.setProvideMethodName(provideMethodName);
		cds.setResolveMethodName(resolveMethodName);

		if (sql != null)
			cds.setQueryString(sql);
	}

	/**
	 * 用特定的分割符分割字符窜, 返回字符串数组
	 * 
	 * @param s
	 *            要分割的字符串
	 * @param sep
	 *            分割符
	 * @return 分割后的字符串数组
	 */
	public static String[] parseString(String s, String sep) {
		StringTokenizer st = new StringTokenizer(s, sep);
		String result[] = new String[st.countTokens()];
		for (int i = 0; i < result.length; i++) {
			result[i] = st.nextToken();
		}
		return result;
	}

	/**
	 * 查找并定位数据集<br>
	 * 列如:locateDataSet(dsvDept,"nhxh",tbzsjyxmData.format("vcbm"),Locate.FIRST)
	 * 
	 * @param ds
	 *            数据集
	 * @param FieldName
	 *            字段名
	 * @param value
	 *            查找的值
	 * @param option
	 *            查找的条件
	 * @return 是否找到
	 */
	public synchronized static boolean locateDataSet(DataSet ds,
			String FieldName, String value, int option) {
		if (FieldName.equalsIgnoreCase("") || FieldName == null)
			return false;

		DataRow rowLocate = new DataRow(ds, FieldName);
		if (ds.getColumn(FieldName).getDataType() == com.borland.dx.dataset.Variant.BIGDECIMAL) {
			if (value.equalsIgnoreCase("") || value == null)
				return false;
			rowLocate.setBigDecimal(FieldName, new BigDecimal(value));
		} else if (ds.getColumn(FieldName).getDataType() == com.borland.dx.dataset.Variant.STRING)
			rowLocate.setString(FieldName, value);

		boolean isLoacte = ds.locate(rowLocate, option);
		rowLocate = null;
		return isLoacte;
	}

	/**
	 * 将BigDecimal格式成指定的格式的字符串
	 * 
	 * @param bg
	 *            要格式的BigDecimal
	 * @param pattern
	 *            格式化的字符串，如#,##0.00
	 * @return 返回格式化过的字符串
	 */
	public synchronized static String formatNumber(Object obj, String pattern) {
//		System.out.println(pattern);
		return Format.formatNumber(obj, pattern);
	}

	/**
	 * 将float格式成指定的格式的字符串
	 * 
	 * @param f
	 *            要格式的float
	 * @param pattern
	 *            格式化的字符串，如#,##0.00
	 * @return 返回格式化过的字符串
	 */
	public synchronized static String formatNumber(float f, String pattern) {
//		System.out.println(pattern);
		return Format.formatNumber(f, pattern);
	}

	/**
	 * 将double格式成指定的格式的字符串
	 * 
	 * @param d
	 *            要格式的double
	 * @param pattern
	 *            格式化的字符串，如#,##0.00
	 * @return 返回格式化过的字符串
	 */
	public synchronized static String formatNumber(double d, String pattern)// throws													// Exception
	{
		System.out.println(pattern);
		return Format.formatNumber(d, pattern);
	}

	/**
	 * 将数据集转化为网页的下拉框的option的内容
	 * 
	 * @param ds
	 *            数据集
	 * @param idColumn
	 *            id列名,即要得到的值
	 * @param capColumn
	 *            显示的名称
	 * @param selectIdValue
	 *            初始化option时，默认选中的id列的值
	 * @param existColumn
	 *            过滤的列名，即只打印以该列名为条件的值，若＝null，则不起作用
	 * @param existValue
	 *            过滤的列名的值
	 * @return 包含option的字符串
	 */
	public synchronized static String dataSetToOption(DataSet ds,
			String idColumn, String capColumn, String selectIdValue,
			String existColumn, String existValue) {
		StringBuffer buf = new StringBuffer();
		ds.first();
		for (int i = 0; i < ds.getRowCount(); i++) {
			if (existColumn != null
					&& !EngineDataSet.getValue(ds,
							ds.getColumn(existColumn).getOrdinal()).equals(
							existValue)) {
				ds.next();
				continue;
			}
			String id = EngineDataSet.getValue(ds, ds.getColumn(idColumn)
					.getOrdinal());
			buf.append("<option value='");
			buf.append(id);
			if (id.equals(selectIdValue))
				buf.append("' selected>");
			else
				buf.append("'>");

			buf.append(EngineDataSet.getValue(ds, ds.getColumn(capColumn)
					.getOrdinal()));
			buf.append("</option>");
			ds.next();
		}
		return buf.toString();
	}

	/**
	 * 显示客户端的JavaScript
	 * 
	 * @param script
	 *            JavaScript语句的内容
	 * @return 返回那容<script>+ script +</script>
	 */
	public static String showJavaScript(String script) {
		return "<script>" + script + "</script>";
	}

	/**
	 * 显示HTMl的信息给客户端
	 * 
	 * @param value
	 *            需要信息的值
	 * @param isError
	 *            是否是错误信息
	 * @return 返回HTML格式的字符串
	 */
	public synchronized static String showMessage(String value, boolean isError) {
		/*
		 * <table align='center' cellspacing='1' cellpadding='0'>
		 * <tr><td><fieldset> <legend align='center'>信息</legend> <div
		 * align='left' style='color: red'>&nbsp;123&nbsp;</div> </fieldset>
		 * </td></tr></table>
		 */
		String table = "<table align='center' cellspacing='1' cellpadding='0'><tr><td><fieldset><legend align='center'>"
				+ (isError ? "错误" : "信息")
				+ "</legend><div align='left'"
				+ (isError ? " style='color: red'" : "") + ">&nbsp;";
		table += StringUtils.replaceInvalid(value);
		return table + "</td></tr></table>";
	}

	/**
	 * 检测字符串是否是数字型的
	 * 
	 * @param value
	 *            需要检测的字符串
	 * @param caption
	 *            javascipt需要显示的标题
	 * @return 若返回null表示是数字，非null为javasrcip语句
	 */
	public static String checkNumber(String value, String caption) {
		if (value.equals(""))
			return showJavaScript("alert('" + caption + " 不能为空！');");
		try {
			Double.parseDouble(value);
		} catch (Exception ex) {
			return showJavaScript("alert('非法 " + caption + "！');");
		}
		return null;
	}

	/**
	 * 检测字符串是否是数字型的
	 * 
	 * @param value
	 *            需要检测的字符串
	 * @param caption
	 *            javascipt需要显示的标题
	 * @return 若返回null表示是数字，非null为javasrcip语句
	 */
	public static String checkNumber(String value, String caption,
			boolean canZero) {
		if (value.equals(""))
			return showJavaScript("alert('" + caption + " 不能为空！');");
		try {
			double d = Double.parseDouble(value);
			if (!canZero && d == 0)
				return showJavaScript("alert('" + caption + "不能为零');");
		} catch (Exception ex) {
			return showJavaScript("alert('非法 " + caption + "！');");
		}
		return null;
	}

	/**
	 * 检测字符串是否是数字型的
	 * 
	 * @param value
	 *            需要检测的字符串
	 * @param caption
	 *            javascipt需要显示的标题
	 * @param min
	 *            最小值
	 * @param max
	 *            最大值
	 * @return 若返回null表示是数字，非null为javasrcip语句
	 */
	public static String checkNumber(String value, String caption, double min,
			double max) {
		if (value.equals(""))
			return showJavaScript("alert('" + caption + " 不能为空！');");
		double dValue;
		try {
			dValue = Double.parseDouble(value);
		} catch (Exception ex) {
			return showJavaScript("alert('非法 " + caption + "！');");
		}
		if (dValue < min)
			return showJavaScript("alert('" + caption + " 值太小！');");
		if (dValue > max)
			return showJavaScript("alert('" + caption + " 值太大！');");

		return null;
	}

	/**
	 * 检测字符串是否是整型的
	 * 
	 * @param value
	 *            需要检测的字符串
	 * @param caption
	 *            javascipt需要显示的标题
	 * @return 若返回null表示是整型，非null为javasrcip语句
	 */
	public static String checkInt(String value, String caption) {
		if ("".equals(value))
			return showJavaScript("alert('" + caption + " 不能为空！');");
		try {
			Integer.parseInt(value);
		} catch (Exception ex) {
			return showJavaScript("alert('非法 " + caption + "！');");
		}
		return null;
	}

	/**
	 * 检测字符串是否是数字型的
	 * 
	 * @param value
	 *            需要检测的字符串
	 * @return 返回是否是Double型的
	 */
	public static boolean isDouble(String value) {
		try {
			Double.parseDouble(value);
		} catch (Exception ex) {
			return false;
		}
		return true;
	}
}