package engine.web.observer;

import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * Title: 观察者模式－触发者
 * </p>
 * <p>
 * Description: 一个触发者对象可以有一个或多个执行者对象。 一个执行者必须实现<tt>Obactioner</tt>接口。
 * 触发者(Obationable)实例将调用执行者(<code>Obactioner</code>)的
 * <code>notifyObactioners</code>方法 此时将调用所有执行者的<code>update</code>方法.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: JAC
 * </p>
 * 
 * @author hukn
 * @version 1.0
 */

@SuppressWarnings("serial")
public class Obationable implements java.io.Serializable {
	@SuppressWarnings("rawtypes")
	private Hashtable obs = new Hashtable();

	/**
	 * 新增一个对应action操作的Obactioner对象集的Obactioner， 若该对象已经在对象集中存在将不做处理
	 * 
	 * @param action
	 *            以后将会触发的操作.
	 * @param o
	 *            要被添加的Obactioner.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public final void addObactioner(String action, Obactioner o) {
		synchronized (obs) {
			ArrayList obactioners = (ArrayList) obs.get(action);
			if (obactioners == null) {
				obactioners = new ArrayList();
				obactioners.add(o);
				obs.put(action, obactioners);
			} else if (!obactioners.contains(o))
				obactioners.add(o);
		}
	}

	/**
	 * 删除一个对应action操作的Obactioner对象集中的Obactioner
	 * 
	 * @param o
	 *            要被删除的Obactioner.
	 */
	@SuppressWarnings("rawtypes")
	public final void deleteObactioner(String action, Obactioner o) {
		synchronized (obs) {
			ArrayList obactioners = (ArrayList) obs.get(action);
			if (obactioners != null)
				obactioners.remove(o);
		}
	}

	/**
	 * 触发相应的action行为所注册的所有执行者对象
	 * 
	 * @param action
	 *            操作行为
	 */
	public final RunData notifyObactioners(String action, HttpServletRequest req, HttpServletResponse res)
			throws Exception {
		return notifyObactioners(action, req, res, null);
	}

	/**
	 * 触发相应的action行为所注册的所有执行者对象
	 * 
	 * @param action
	 *            操作行为
	 * @param arg
	 *            传递给执行者对象的参数
	 */
	@SuppressWarnings("rawtypes")
	public final RunData notifyObactioners(String action, HttpServletRequest req, HttpServletResponse res, Object arg)
			throws Exception {
		if (action == null)
			return null;

		synchronized (obs) {
			ArrayList obactioners = (ArrayList) obs.get(action);
			if (obactioners == null)
				return null;

			RunData data = new RunData();
			data.setRequest(req);
			data.setResponse(res);
			for (int i = 0; i < obactioners.size(); i++)
				((Obactioner) obactioners.get(i)).execute(action, this, data, arg);

			return data;
		}
	}

	/**
	 * 清除所有的Obactioner对象
	 */
	public synchronized void deleteObservers() {
		obs.clear();
	}

	/**
	 * 返回所有的Obactioner对象的数量
	 * 
	 * @return 所有Obactioner对象的数量
	 */
	@SuppressWarnings("rawtypes")
	public synchronized int countObactioners() {
		int count = 0;
		if (obs.size() == 0)
			return count;
		Integer size = obs.size();
		Collection collect = obs.values();
		Iterator it = collect.iterator();
		while (it.hasNext()) {
			ArrayList obactioners = (ArrayList) it.next();
			if (obactioners != null)
				count += obactioners.size();
		}
		return count;
	}
}