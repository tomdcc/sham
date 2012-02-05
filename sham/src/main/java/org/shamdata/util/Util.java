package org.shamdata.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Util {
    public static boolean runningInServletContainer() {
        try {
            new InitialContext().lookup("java:comp/env");
            return true;
        } catch(NamingException e) {
            return false;
        }
    }
}
