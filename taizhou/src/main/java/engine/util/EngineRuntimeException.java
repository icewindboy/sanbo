package engine.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * <p>Title: This is a base class of runtime exeptions thrown by Engine</p>
 * <p>Description: This class represents a non-checked type exception (see
 * {@see java.lang.RuntimeException}). It has the nested stack trace
 * functionality found in the {@see TurbineException} class.
 *
 * It's sad that this class is a straight copy/paste of Turbine exception.
 * I wish that Java supported NonCheckedException marker interface...</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: ENGINE</p>
 * @author hukn
 * @version 1.0
 */

public class EngineRuntimeException extends RuntimeException
{
    /**
     * Holds the reference to the exception or error that caused
     * this exception to be thrown.
     */
    private Throwable nested = null;

    /**
     * Constructs a new <code>EngineRuntimeException</code> without specified
     * detail message.
     */
    public EngineRuntimeException()
    {
        super();
    }

    /**
     * Constructs a new <code>EngineRuntimeException</code> with specified
     * detail message.
     *
     * @param msg the error message.
     */
    public EngineRuntimeException(String msg)
    {
        super(msg);
    }

    /**
     * Constructs a new <code>EngineRuntimeException</code> with specified
     * nested <code>Throwable</code>.
     *
     * @param nested the exception or error that caused this exception
     *               to be thrown.
     */
    public EngineRuntimeException(Throwable nested)
    {
        super();
        this.nested = nested;
    }

    /**
     * Constructs a new <code>EngineRuntimeException</code> with specified
     * detail message and nested <code>Throwable</code>.
     *
     * @param msg the error message.
     * @param nested the exception or error that caused this exception
     *               to be thrown.
     */
    public EngineRuntimeException(String msg, Throwable nested)
    {
        super(msg);
        this.nested = nested;
    }

    /**
     * Prints the stack trace of this exception the the standar error
     * stream.
     */
    public void printStackTrace()
    {
        synchronized(System.err)
        {
            printStackTrace(System.err);
        }
    }

    /**
     * Prints the stack trace of this exception to the specified print stream.
     *
     * @param out <code>PrintStream</code> to use for output
     */
    public void printStackTrace(PrintStream out)
    {
        synchronized(out)
        {
            PrintWriter pw=new PrintWriter(out, false);
            printStackTrace(pw);
            // flush the PrintWriter before it's GCed
            pw.flush();
        }
    }

    /**
     * Prints the stack trace of this exception to the specified print writer.
     *
     * @param out <code>PrintWriter</code> to use for output.
     */
    public void printStackTrace(PrintWriter out)
    {
        synchronized(out)
        {
            printStackTrace(out, 0);
        }
    }

    /**
     * Prints the stack trace of this exception skiping a specified number
     * of stack frames.
     *
     * @param out <code>PrintWriter</code> to use for output.
     * @param skip the numbere of stack frames to skip.
     */
    public void printStackTrace(PrintWriter out, int skip)
    {
        String[] st = captureStackTrace();
        if(nested != null)
        {
            if(nested instanceof EngineRuntimeException)
            {
                ((EngineRuntimeException)nested).printStackTrace(out, st.length - 2);
            }
            else if(nested instanceof EngineException)
            {
                ((EngineException)nested).printStackTrace(out, st.length - 2);
            }
            else
            {
                String[] nst = captureStackTrace(nested);
                for(int i = 0; i<nst.length - st.length + 2; i++)
                {
                    out.println(nst[i]);
                }
            }
            out.print("rethrown as ");
        }
        for(int i=0; i<st.length - skip; i++)
        {
            out.println(st[i]);
        }
    }

    /**
     * Captures the stack trace associated with this exception.
     *
     * @return an array of Strings describing stack frames.
     */
    private String[] captureStackTrace()
    {
        StringWriter sw = new StringWriter();
        super.printStackTrace(new PrintWriter(sw, true));
        return splitStackTrace(sw.getBuffer().toString());
    }

    /**
     * Captures the stack trace associated with a <code>Throwable</code>
     * object.
     *
     * @param t the <code>Throwable</code>.
     * @return an array of Strings describing stack frames.
     */
    private String[] captureStackTrace(Throwable t)
    {
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw, true));
        return splitStackTrace(sw.getBuffer().toString());
    }

    /**
     * Splits the stack trace given as a newline separated string
     * into an array of stack frames.
     *
     * @param stackTrace the stack trace.
     * @return an array of Strings describing stack frames.
     */
    private String[] splitStackTrace(String stackTrace)
    {
        String linebreak = System.getProperty("line.separator");
        StringTokenizer st = new StringTokenizer(stackTrace, linebreak);
        LinkedList list = new LinkedList();
        while(st.hasMoreTokens())
        {
            list.add(st.nextToken());
        }
        return (String [])list.toArray(new String[] {});
    }
}

