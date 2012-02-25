package org.shamdata.image;


import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class ServletContextImagePicker extends BaseImagePicker {
    private ServletContext servletContext;

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
    Set<String> listFiles(String dirName) {
        return (Set<String>) servletContext.getResourcePaths(dirName);
    }

    protected URL toURL(String filename) {
        try {
            return servletContext.getResource(filename);
        } catch(MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}
