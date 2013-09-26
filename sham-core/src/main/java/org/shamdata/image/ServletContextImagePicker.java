package org.shamdata.image;


import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * An image picker which will scan for images in a directory inside a
 * servlet context. The specified {@link #baseDir} will be treated as
 * relative to the root of the web application.
 */
public class ServletContextImagePicker extends BaseImagePicker {
    private ServletContext servletContext;

    /**
     * Initialises the image picker and scans for files. This requires that
     * the servlet context has already bee set using {@link #setServletContext setServletContext()}.
     */
    public void init() {
        if(servletContext == null) {
            throw new IllegalArgumentException("Servlet context required");
        }
        if(baseDir == null) {
            baseDir = "/";
        }

        super.init();
    }

    @SuppressWarnings("unchecked")
    protected Set<String> listFiles(String dirName) {
        return (Set<String>) servletContext.getResourcePaths(dirName);
    }

    protected URL toURL(String filename) {
        try {
            return servletContext.getResource(filename);
        } catch(MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Gives the picker a handle on the servlet context, so that it can can
     * for images in the context.
     *
     * @param servletContext the current servlet context
     */
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
