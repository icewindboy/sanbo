package engine.util.context;

/**
 * <p>Title: 保存上下文对象的接口</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public interface Context extends java.io.Serializable
{
    /**
     * Adds a name/value pair to the context.
     *
     * @param key   The name to key the provided value with.
     * @param value The corresponding value.
     */
    public void put(String key, Object value);

    /**
     * Gets the value corresponding to the provided key from the context.
     *
     * @param key The name of the desired value.
     * @return    The value corresponding to the provided key.
     */
    public Object get(String key);

    /**
     * Indicates whether the specified key is in the context.
     *
     * @param key The key to look for.
     * @return    Whether the key is in the context.
     */
    public boolean containsKey(Object key);

    /**
     * Get all the keys for the values in the context
     */
    public Object[] getKeys();

    /**
     * Removes the value associated with the specified key from the context.
     *
     * @param key The name of the value to remove.
     * @return    The value that the key was mapped to, or <code>null</code>
     *            if unmapped.
     */
    public Object remove(Object key);

    /**
     * Removes all object of the context.
     */
    public void clear();
}