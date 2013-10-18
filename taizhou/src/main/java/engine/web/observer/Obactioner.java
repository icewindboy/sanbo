package engine.web.observer;

/**
 * <p>Title: 观察者模式－执行者</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: JAC</p>
 * @author hukn
 * @version 1.0
 */

public interface Obactioner extends java.io.Serializable
{
  /**
   * 这个方法当触发者对象调用<tt>Obactionable</tt>的<code>notifyObactioners</code>方法
   * 将调用所有执行者(Obactioner)的这个方法.
   * @parma  action 触发执行的参数（键值）
   * @param  o      触发者对象
   * @param  data   传递的信息的类
   * @param  arg    触发者对象调用<code>notifyObactioners</code>方法传递的参数
   */
   void execute(String action, Obationable o, RunData data, Object arg) throws Exception;
}