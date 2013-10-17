package engine.util.context;

import java.util.Hashtable;

/**
 * <p>Title: 默认实现保存上下文对象的接口</p>
 * <p>Description:
 * General purpose implemention of the application Context
 * interface for general application use.  This class should
 * be used in place of the original Context class.
 *
 * This implementation uses a Hashtable  (@see java.util.Hashtable )
 * for data storage.
 *
 * This context implementation can be shared between threads access between them
 * </p>
 *
 * @see engine.context.Context
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public class DefaultContext implements Context
{
    Context innerContext;

    /**
     *  storage for key/value pairs
     */
    private Hashtable context = new Hashtable();

    /**
     * default contructor, does nothing
     * interesting
     */
    public DefaultContext()
    {
    }

    /**
     * Allow chained contexts.
     */
    public DefaultContext(Context context)
    {
        super();

        //!! I realize this is not the most efficient
        // way to do this, but I'm not sure if chained
        // contexts can work with templating solutions
        // other than velocity. I don't see why not right
        // of the bat, but this will work for now.

        Object[] keys = context.getKeys();

        for (int i = 0; i < keys.length; i++)
        {
          put((String) keys[i], context.get((String)keys[i]));
        }
    }

    public void put(String key, Object value)
    {
        context.put(key, value);
    }

    public Object get(String key)
    {
        return context.get(key);
    }

    public Object remove(Object key)
    {
        return context.remove(key);
    }

    public boolean containsKey(Object key)
    {
        return context.containsKey(key);
    }

    public Object[] getKeys()
    {
        return context.keySet().toArray();
    }

    public void clear()
    {
      context.clear();
    }
}
