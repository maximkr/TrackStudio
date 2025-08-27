package com.trackstudio.exception;

import java.util.Map;

import com.trackstudio.app.session.SessionContext;

public class AccessDeniedException extends GranException {

    public AccessDeniedException(Class className, String methodName, SessionContext sc, String why, String where, String id)
            throws GranException {
        super("AccessDeniedException: className=" + className.getName() + ", method=" + methodName + "\n logged user: " + sc.getUser().getLogin() + "\n why: " + why + "\n where: " + where + "\n id: " + id);
        //printStackTraceForAllThreads();
    }

    public AccessDeniedException(Class className, String methodName, SessionContext sc, String why, String id)
            throws GranException {
        super("AccessDeniedException: className=" + className.getName() + ", method=" + methodName + "\n logged user: " + sc.getUser().getLogin() + "\n condition: " + why+ "\n id: " + id);
        //printStackTraceForAllThreads();
    }

    void printStackTraceForAllThreads()
    {
        System.out.println("**************** Dump started ****************");
        Map<Thread, StackTraceElement[]> st = Thread.getAllStackTraces();
        for (Thread t : st.keySet()) {
            StackTraceElement[] trace = st.get(t);
            for (int i=0;i<trace.length;i++)
                System.out.println(t.getId()+ " " + trace[i].getClassName() + " " + trace[i].getMethodName() + " " + trace[i].getLineNumber());
            System.out.println();
        }
        System.out.println("**************** Dump finished ****************");


    }

}
