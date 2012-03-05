package org.shamdata.util;

import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * Utilities class. Intended for internal Sham use only.
 */
public class Util {

    /**
     * Rough and ready detection of servlet container. Most definitely not guaranteed to work.
     *
     * @return best guess at whether we're in a servlet container or not
     */
    public static boolean runningInServletContainer() {
        try {
            new InitialContext().lookup("java:comp/env");
            return true;
        } catch(NamingException e) {
            return false;
        }
    }
}
