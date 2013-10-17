package engine.project;

import java.util.Hashtable;

import engine.dataset.EngineDataSet;
import engine.dataset.EngineDataSetProvider;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author hukn
 * @version 1.0
 */

class OtherDataSetPool implements java.io.Serializable {
	private static Hashtable htPool = new Hashtable();

	// private static DataSetPool dataSetPool = new DataSetPool();

	/**
	 * 从数据集池中得到数据集
	 * 
	 * @param beanName
	 *            name of bean
	 * @return 返回数据集
	 */
	public static EngineDataSet getDataSet(String beanName) {
		EngineDataSet eds = null;
		if (beanName == null)
			return eds;
		if (!OtherLookupHelperPool.isOneOpen(beanName))
			return eds;

		synchronized (htPool) {
			eds = (EngineDataSet) htPool.get(beanName);
			if (eds == null) {
				eds = new EngineDataSet();
				eds.setProvider(new EngineDataSetProvider());
				// eds.setResolver(new EngineDataSetResolver());
				htPool.put(beanName, eds);
			}
		}
		return eds;
	}

	/**
	 * 从数据集池中移去数据集
	 * 
	 * @param beanName
	 *            name of bean
	 */
	static void removeDataSet(String beanName) {
		if (beanName == null)
			return;
		synchronized (htPool) {
			htPool.remove(beanName);
		}
	}

	/**
	 * 刷新数据集以同步数据
	 * 
	 * @param beanName
	 *            name of bean
	 */
	public static void refreshDataSet(String beanName) {
		synchronized (htPool) {
			if (!OtherLookupHelperPool.isOneOpen(beanName))
				return;
			EngineDataSet eds = getDataSet(beanName);
			if (eds == null)
				return;
			eds.closeDataSet();
		}
	}
}