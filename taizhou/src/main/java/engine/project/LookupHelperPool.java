package engine.project;

import java.util.Map;
import java.util.Hashtable;
import engine.util.log.Log;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: ENGINE
 * </p>
 * 
 * @author hukn
 * @version 1.0
 */

public abstract class LookupHelperPool {
	protected static LookupHelperPool pool = null;

	private static Log log = new Log(LookupHelperPool.class);

	protected static String getChildName() {
		return "engine.project.LookupHelperPoolImpl";
	}

	static {
		try {
			pool = (LookupHelperPool) Class.forName(getChildName()).newInstance();
		} catch (ClassNotFoundException ex) {
			log.fatal("Not found the class of " + getChildName(), ex);
		} catch (ClassCastException ex) {
			log.fatal("Incorrect Cast Class LookupHelperPool for class " + getChildName(), ex);
		} catch (Exception ex) {
			log.fatal("Exception for get instance of class " + getChildName(), ex);
		}
	}

	/**
	 * 在缓冲池中得到LookupLookupHelperer对象
	 * 
	 * @return
	 */
	public static LookupHelper getLookupHelper(String name) {
		return (LookupHelper) pool.getLookupConfig(name);
	}

	/**
	 * LookUp bean 是否是一次性打开的
	 * 
	 * @param beanName
	 *            name of bean
	 * @return true 是
	 */
	public static boolean isOneOpen(String beanName) {
		LookupHelper help = getLookupHelper(beanName);
		return help == null ? false : help.isOneOpen();
	}

	protected abstract LookupHelper getLookupConfig(String name);
}