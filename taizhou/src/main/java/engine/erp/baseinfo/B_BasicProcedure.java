package engine.erp.baseinfo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;

import engine.action.BaseAction;
import engine.action.Operate;
import engine.dataset.EngineDataSet;
import engine.dataset.RowMap;
import engine.dataset.SequenceDescriptor;
import engine.project.LookupBeanFacade;
import engine.project.SysConstant;
import engine.web.observer.Obactioner;
import engine.web.observer.Obationable;
import engine.web.observer.RunData;

public final class B_BasicProcedure extends BaseAction implements Operate {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5717662691416025118L;

	/**
	 * 保存工序分段信息的数据集
	 */
	private EngineDataSet dsBasicProcedure = new EngineDataSet();

	/**
	 * 保存用户输入的信息
	 */
	private RowMap rowInfo = new RowMap();

	/**
	 * 是否在添加状态
	 */
	public boolean isAdd = true;

	/**
	 * 保存修改操作的行记录指针
	 */
	private long editrow = 0;

	/**
	 * 点击返回按钮的URL
	 */
	public String retuUrl = null;

	/**
	 * 得到工序分段信息的实例
	 * 
	 * @param request
	 *            jsp请求
	 * @param isApproveStat
	 *            是否在审批状态
	 * @return 返回仓库信息的实例
	 */
	public static B_BasicProcedure getInstance(HttpServletRequest request) {
		B_BasicProcedure b_BasicProcedureBean = null;
		HttpSession session = request.getSession(true);
		synchronized (session) {
			String beanName = "b_BasicProcedureBean";
			b_BasicProcedureBean = (B_BasicProcedure) session.getAttribute(beanName);
			// 判断该session是否有该bean的实例
			if (b_BasicProcedureBean == null) {
				b_BasicProcedureBean = new B_BasicProcedure();
				session.setAttribute(beanName, b_BasicProcedureBean);
			}
		}
		return b_BasicProcedureBean;
	}

	/**
	 * 构造函数
	 */
	private B_BasicProcedure() {
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
		setDataSetProperty(dsBasicProcedure, "SELECT * FROM jc_basic_technics ORDER BY code");
		// dsBasicProcedure.setSort(new SortDescriptor("", new String[] { "gdbh"
		// }, new boolean[] { false }, null, 0));
		dsBasicProcedure.setSequence(new SequenceDescriptor(new String[] { "id" },
				new String[] { "s_jc_basic_technics" }));
		// 添加操作的触发对象
		B_BasicProcedure_Add_Edit add_edit = new B_BasicProcedure_Add_Edit();
		addObactioner(String.valueOf(INIT), new B_BasicProcedure_Init());
		addObactioner(String.valueOf(ADD), add_edit);
		addObactioner(String.valueOf(EDIT), add_edit);
		addObactioner(String.valueOf(POST), new B_BasicProcedure_Post());
		addObactioner(String.valueOf(DEL), new B_BasicProcedure_Delete());
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
			String operate = request.getParameter(OPERATE_KEY);
			if (operate != null && operate.trim().length() > 0) {
				RunData data = notifyObactioners(operate, request, response, null);
				if (data.hasMessage())
					return data.getMessage();
			}
			return "";
		} catch (Exception ex) {
			if (dsBasicProcedure.isOpen() && dsBasicProcedure.changesPending())
				dsBasicProcedure.reset();
			log.error("doService", ex);
			return showMessage(ex.getMessage(), true);
		}
	}

	// ----Implementation of the BaseAction abstract class
	/**
	 * jvm要调的函数,类似于析构函数
	 */
	public void valueUnbound(HttpSessionBindingEvent event) {
		if (dsBasicProcedure != null) {
			dsBasicProcedure.close();
			dsBasicProcedure = null;
		}
		log = null;
		rowInfo = null;
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

	/**
	 * 初始化列信息
	 * 
	 * @param isAdd
	 *            是否时添加
	 * @param isInit
	 *            是否从新初始化
	 * @throws java.lang.Exception
	 *             异常
	 */
	private final void initRowInfo(boolean isAdd, boolean isInit) throws java.lang.Exception {
		// 是否时添加操作
		if (isInit && rowInfo.size() > 0)
			rowInfo.clear();
		if (!isAdd)
			rowInfo.put(getOneTable());
		else {
			String code = dataSetProvider
					.getSequence("SELECT pck_base.fieldNextCode('jc_basic_technics','code','','',3) from dual");
			rowInfo.put("code", code);
		}
	}

	/* 得到表对象 */
	public final EngineDataSet getOneTable() {
		if (!dsBasicProcedure.isOpen())
			dsBasicProcedure.open();
		return dsBasicProcedure;
	}

	/* 得到一列的信息 */
	public final RowMap getRowinfo() {
		return rowInfo;
	}

	/**
	 * 初始化操作的触发类
	 */
	class B_BasicProcedure_Init implements Obactioner {
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
		}
	}

	/**
	 * 添加或修改操作的触发类
	 */
	class B_BasicProcedure_Add_Edit implements Obactioner {
		// ----Implementation of the Obactioner interface
		/**
		 * 添加或修改的触发操作
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
			isAdd = action.equals(String.valueOf(ADD));
			if (!isAdd) {
				dsBasicProcedure.goToRow(Integer.parseInt(data.getParameter("rownum")));
				editrow = dsBasicProcedure.getInternalRow();
			}
			initRowInfo(isAdd, true);
		}
	}

	/**
	 * 保存操作的触发类
	 */
	class B_BasicProcedure_Post implements Obactioner {
		// ----Implementation of the Obactioner interface
		/**
		 * 触发保存操作
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
			EngineDataSet ds = getOneTable();
			// 校验数据
			rowInfo.put(data.getRequest());
			String code = rowInfo.get("code");
			String name = rowInfo.get("name");
			// String gdms = rowInfo.get("gdms");
			String deptid = rowInfo.get("deptid");
			String description = rowInfo.get("description");
			if (name.equals("")) {
				data.setMessage(showJavaScript("alert('工艺名称不能为空！');"));
				return;
			}
			if (deptid.equals("")) {
				data.setMessage(showJavaScript("alert('所属车间不能为空！');"));
				return;
			}
			if (!isAdd)
				ds.goToInternalRow(editrow);

			// if (isAdd || !gdmc.equals(ds.getValue("gdmc"))) {
			// String count =
			// dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('sc_gxfd','gdmc','"
			// + gdmc
			// + "') from dual");
			// if (!count.equals("0")) {
			// data.setMessage(showJavaScript("alert('工段名称(" + gdmc +
			// ")已经存在!');"));
			// return;
			// }
			// }
			// if (isAdd || !gdbh.equals(ds.getValue("gdbh"))) {
			// String count =
			// dataSetProvider.getSequence("SELECT pck_base.fieldCodeCount('sc_gxfd','gdbh','"
			// + gdbh
			// + "') from dual");
			// if (!count.equals("0")) {
			// data.setMessage(showJavaScript("alert('工段编号(" + gdbh +
			// ")已经存在!');"));
			// return;
			// }
			// }

			if (isAdd) {
				ds.insertRow(false);
				ds.setValue("id", "-1");
			}
			ds.setValue("code", code);
			ds.setValue("name", name);
			// ds.setValue("gdms", gdms);
			ds.setValue("deptid", deptid);
			ds.setValue("description", description);
			ds.post();
			ds.saveChanges();
			LookupBeanFacade.refreshLookup(SysConstant.BEAN_WORK_PROCEDURE);
			data.setMessage(showJavaScript("parent.hideInterFrame();"));
		}
	}

	/**
	 * 删除操作的触发类
	 */
	class B_BasicProcedure_Delete implements Obactioner {
		// ----Implementation of the Obactioner interface
		/**
		 * 触发删除操作
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
			EngineDataSet ds = getOneTable();
			ds.goToRow(Integer.parseInt(data.getParameter("rownum")));

			ds.deleteRow();
			ds.saveChanges();

		}
	}
}