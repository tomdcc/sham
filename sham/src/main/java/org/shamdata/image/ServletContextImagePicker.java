package org.shamdata.image;


import javax.servlet.ServletContext;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class ServletContextImagePicker implements ImagePicker {
    private Random random;
    private ServletContext servletContext;
    private String baseDir;

    private List<URL> files;
    private List<String> dirs;

    public URL nextImage() {
        return files.get(random.nextInt(files.size()));
    }

    public Map<String, URL> nextImageSet() {
        return buildImageSet(dirs.get(random.nextInt(dirs.size())));
    }

    private Map<String, URL> buildImageSet(String dirName) {
        Map<String, URL> imageSet = new HashMap<String, URL>();
        for(String path: listFiles(dirName)) {
            imageSet.put(getFilenameBase(path), toURL(path));
        }
        return imageSet;
    }

    private String getFilenameBase(String path) {
        String filename = path.substring(path.lastIndexOf('/') + 1);
        int dotPlace = filename.lastIndexOf('.');
        if(dotPlace != -1) {
            return filename.substring(0, dotPlace);
        } else {
            return filename;
        }
    }


    public void init() {
        if(servletContext == null) {
            throw new IllegalArgumentException("Servlet context required");
        }
        if(random == null) {
            random = new Random();
        }
        if(baseDir == null) {
            baseDir = "/";
        }

        scanFiles();
    }

    @SuppressWarnings("unchecked")
    Set<String> listFiles(String dirName) {
        return (Set<String>) servletContext.getResourcePaths(dirName);
    }

    private void scanFiles() {
        files = new ArrayList<URL>();
        dirs = new ArrayList<String>();
        Collection<String> fileNames = new LinkedHashSet<String>(listFiles(baseDir));
        fileNames.remove(baseDir.endsWith("/") ? baseDir : baseDir + "/");
        for(String filename : fileNames) {
            if(filename.endsWith("/")) {
                dirs.add(filename);
            } else {
                files.add(toURL(filename));
            }
        }
    }

    private URL toURL(String filename) {
        try {
            return servletContext.getResource(filename);
        } catch(MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
