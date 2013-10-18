package engine.erp.common;

import engine.action.*;
import engine.dataset.*;
import engine.web.observer.*;
import javax.servlet.http.*;

import com.borland.dx.dataset.*;

/**
 * <p>
 * Title: 审批情况表
 * </p>
 * <p>
 * Description: 基础管理--审批情况表
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author 李强
 * @version 1.0
 */

public final class B_ApproveResult extends BaseAction implements Operate {
	/**
	 * 提取审批历史记录主表所有信息的SQL语句
	 */
	private static final String APPROVE_HISTORY_SQL = "select * from( "
			+ " SELECT l.spjlid, l.tjsprq, l.tjbm, l.tjr,l.spxmbm, l.zjid, NULL deptid FROM sp_jl_ls l WHERE l.spxmbm='@' AND l.zjid='@' "
			+ " union all " + " select a.spjlid, a.tjsprq, b.mc, c.xm, a.spxmbm, a.zjid, a.deptid from sp_jl a, bm b, emp c "
			+ " where a.deptid=b.deptid and a.personid=c.personid(+) AND a.spxmbm='@' AND a.zjid='@') " + " ORDER BY spjlid";//

	/**
	 * 提取审批历史记录从表所有信息的SQL语句
	 */
	private static final String APPROVE_HISTORY_DETAIL_SQL = "select * from( "
			+ " SELECT m.spjlid, m.spmc, m.sftg, m.spr, m.sprq, m.spyj, m.spdj, l.spxmbm, l.zjid, NULL spxmmxid FROM sp_jlmx_ls m, sp_jl_ls l "
			+ " WHERE m.spjlid = l.spjlid and l.spxmbm='@' AND l.zjid='@'" + " union all "
			+ " select a.spjlid, a.spmc, a.sftg, b.xm, a.sprq, a.spyj, a.spdj, c.spxmbm, c.zjid, a.spxmmxid "
			+ " from sp_jlmx a, sp_jl c, emp b where a.personid=b.personid(+) and a.spjlid=c.spjlid and c.spxmbm='@' AND c.zjid='@') "
			+ " ORDER BY spjlid, spdj";
	// 在审批中时显示审批人
	private static final String APPROVE_PERSON = " SELECT DISTINCT e.xm, r.spxmmxid FROM sp_spr r, emp e "
			+ " WHERE r.personid = e.personid  " + " AND r.spxmmxid IN(@) AND r.deptid = '@'";

	/**
	 * 保存审批历史记录主表信息的数据集
	 */
	private EngineDataSet dsApproveHis = new EngineDataSet();

	/**
	 * 保存审批历史记录从表信息的数据集
	 */
	private EngineDataSet dsApproveHisDetail = new EngineDataSet();
	/**
	 * 审批中时显示审批人的数据集
	 */
	private EngineDataSet dsPerson = new EngineDataSet();
	/**
	 * 点击返回按钮的URL
	 */
	public String retuUrl = null;

	/**
	 * 得到收发单据信息的实例
	 * 
	 * @param request
	 *            jsp请求
	 * @param isApproveStat
	 *            是否在审批状态
	 * @return 返回收发单据信息的实例
	 */
	public static B_ApproveResult getInstance(HttpServletRequest request) {
		B_ApproveResult b_ApproveResult = null;
		HttpSession session = request.getSession(true);
		synchronized (session) {
			String beanName = "b_ApproveResult";
			b_ApproveResult = (B_ApproveResult) session.getAttribute(beanName);
			// 判断该session是否有该bean的实例
			if (b_ApproveResult == null) {
				b_ApproveResult = new B_ApproveResult();
				session.setAttribute(beanName, b_ApproveResult);
			}
		}
		return b_ApproveResult;
	}

	/**
	 * 构造函数
	 */
	private B_ApproveResult() {
		try {
			jbInit();
		} catch (Exception ex) {
			log.error("jbInit", ex);
		}
	}

	/**
	 * 初始化函数
	 * 
	 * @throws Exception
	 *             异常信息
	 */
	protected void jbInit() throws Exception {
		setDataSetProperty(dsApproveHis, null);
		setDataSetProperty(dsApproveHisDetail, null);
		setDataSetProperty(dsPerson, null);

		dsApproveHisDetail.setMasterLink(new MasterLinkDescriptor(dsApproveHis, new String[] { "spjlid" }, new String[] { "spjlid" },
				false, false, false));
		dsPerson.setMasterLink(new MasterLinkDescriptor(dsApproveHisDetail, new String[] { "spxmmxid" }, new String[] { "spxmmxid" },
				false, false, false));
		// dsApproveHis.setSort(new SortDescriptor("", new String[]{"SFDJLBID"},
		// new boolean[]{false}, null, 0));
		// 添加操作的触发对象
		addObactioner(String.valueOf(INIT), new Init());
	}

	// ----Implementation of the BaseAction abstract class
	/**
	 * JSP调用的函数
	 * 
	 * @param request
	 *            网页的请求对象
	 * @param response
	 *            网页的响应对象
	 * @return 返回HTML或javascipt的语句
	 * @throws Exception
	 *             异常
	 */
	public String doService(HttpServletRequest request, HttpServletResponse response) {
		try {
			String opearate = request.getParameter(OPERATE_KEY);
			if (opearate != null && opearate.trim().length() > 0) {
				RunData data = notifyObactioners(opearate, request, response, null);
				if (data.hasMessage())
					return data.getMessage();
			}
			return "";
		} catch (Exception ex) {
			if (dsApproveHis.isOpen() && dsApproveHis.changesPending())
				dsApproveHis.reset();
			log.error("doService", ex);
			return showMessage(ex.getMessage(), true);
		}
	}

	// ----Implementation of the BaseAction abstract class
	/**
	 * jvm要调的函数,类似于析构函数
	 */
	public void valueUnbound(HttpSessionBindingEvent event) {
		if (dsApproveHis != null) {
			dsApproveHis.close();
			dsApproveHis = null;
		}
		log = null;
	}

	// ----Implementation of the BaseAction abstract class
	/**
	 * 得到子类的类名
	 * 
	 * @return 返回子类的类名
	 */
	protected Class childClassName() {
		return getClass();
	}

	/* 得到表对象 */
	public final EngineDataSet getOneTable() {
		if (!dsApproveHis.isOpen())
			dsApproveHis.open();
		return dsApproveHis;
	}

	/* 得到从表对象 */
	public final EngineDataSet getDetailTable() {
		if (!dsApproveHisDetail.isOpen())
			dsApproveHisDetail.open();
		return dsApproveHisDetail;
	}

	/* 得到审批人数据集 */
	public final void openPersonTable(String s) {
		EngineDataSet ds = getDetailTable();
		ds.first();
		int count = ds.getRowCount();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < ds.getRowCount(); i++) {
			String spxmmxid = ds.getValue("spxmmxid");
			if (i == count - 1)
				buf = buf.append("'").append(spxmmxid).append("'");
			else
				buf = buf.append("'").append(spxmmxid).append("',");
			ds.next();
		}
		String sql = buf.toString();
		dsPerson.setQueryString(combineSQL(APPROVE_PERSON, "@", new String[] { sql, s }));
		if (dsPerson.isOpen())
			dsPerson.refresh();
		else
			dsPerson.open();
	}

	/**
	 * 初始化操作的触发类
	 */
	private class Init implements Obactioner {
		// ----Implementation of the Obactioner interface
		/**
		 * 触发初始化操作
		 * 
		 * @parma action 触发执行的参数（键值）
		 * @param o
		 *            触发者对象
		 * @param data
		 *            传递的信息的类
		 * @param arg
		 *            触发者对象调用<code>notifyObactioners</code>方法传递的参数
		 */
		public void execute(String action, Obationable o, RunData data, Object arg) throws Exception {
			retuUrl = data.getParameter("src");
			retuUrl = retuUrl != null ? retuUrl.trim() : retuUrl;
			String spxmbm = data.getParameter("project");
			String zjid = data.getParameter("id");
			//
			String SQL = combineSQL(APPROVE_HISTORY_SQL, "@", new String[] { spxmbm, zjid, spxmbm, zjid });
			String DETAIL_SQL = combineSQL(APPROVE_HISTORY_DETAIL_SQL, "@", new String[] { spxmbm, zjid, spxmbm, zjid });
			dsApproveHis.setQueryString(SQL);
			dsApproveHisDetail.setQueryString(DETAIL_SQL);
			dsApproveHis.setRowMax(null);
			if (dsApproveHis.isOpen())
				dsApproveHis.refresh();
			else
				dsApproveHis.open();
			if (dsApproveHisDetail.isOpen())
				dsApproveHisDetail.refresh();
			else
				dsApproveHisDetail.open();
		}
	}

	/* 得到审批人数据集 */
	public final String getPersonString() {
		dsPerson.first();
		StringBuffer personbuf = new StringBuffer();
		int row = dsPerson.getRowCount();
		for (int i = 0; i < dsPerson.getRowCount(); i++) {
			String xm = dsPerson.getValue("xm");
			if (i == row - 1)
				personbuf = personbuf.append(xm);
			else
				personbuf = personbuf.append(xm).append(",");
			dsPerson.next();
		}
		String personArray = personbuf.toString();
		if (personbuf != null)
			return personArray;
		else
			return "";
	}
}
