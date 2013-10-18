package engine.util;

/**
 * <p>Title: 简单缓冲池对象</p>
 * <p>Description: 基于线程池。该缓冲池将忽略堆栈溢出的问题，如果为空返回null</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */
public class SimplePool  {

  private Object pool[];

  private int max;
  private int last;
  private int current=-1;

  private transient Object lock;
  public  static final int DEFAULT_SIZE=32;
  static  final int debug=0;

  public SimplePool() {
    this(DEFAULT_SIZE,DEFAULT_SIZE);
  }

  public SimplePool(int size) {
    this(size, size);
  }

  public SimplePool(int size, int max) {
    if(max < 0)
      max = DEFAULT_SIZE;
    if(size > max)
      size = max;
    else if(size < 0)
      size = DEFAULT_SIZE;

    this.max=max;
    pool=new Object[size];
    this.last=size-1;
    lock=new Object();
  }

  public  void set(Object o) {
    put(o);
  }

  /**
   * Add the object to the pool, silent nothing if the pool is full
   */
  public  void put(Object o) {
    synchronized( lock ) {
      //
      if( current < last )
      {
        current++;
        pool[current] = o;
      }
      else if( current < max )
      {
        int newSize=pool.length*2;
        if( newSize > max ) newSize=max+1;
        Object tmp[]=new Object[newSize];
        last=newSize-1;
        System.arraycopy( pool, 0, tmp, 0, pool.length);
        pool=tmp;
        current++;
        pool[current] = o;
      }
      if( debug > 0 )
        log("put " + o + " " + current + " " + max );
    }
  }

  /**
   * Get an object from the pool, null if the pool is empty.
   */
  public  Object get() {
    Object item = null;
    synchronized( lock )
    {
      if( current >= 0 ) {
        item = pool[current];
        pool[current] = null;
        current -= 1;
      }
      if( debug > 0 )
        log("get " + item + " " + current + " " + max);
    }
    return item;
  }

  /**
   * Return the size of the pool
   */
  public int getMax() {
    return max;
  }

  /**
   * Number of object in the pool
   */
  public int getCount() {
    return current+1;
  }


  public void shutdown() {
  }

  private void log( String s ) {
    System.out.println("SimplePool: " + s );
  }
}