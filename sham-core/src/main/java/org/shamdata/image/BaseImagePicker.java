package org.shamdata.image;


import java.net.URL;
import java.util.*;

/**
 * Base class for image pickers. Subclasses only have to implement
 * {@link #listFiles listFiles} and {@link #toURL toURL} to get a
 * fully functional image picker.
 *
 * <p>Subclassses of this picker will ignore files starting with a dot,
 * to avoid accidetally picking up certain version control hidden
 * files. This behaviour can be canged by calling
 * {@link #setFilenameIgnorePatterns setFilenameIgnorePatterns}.</p>
 */
public abstract class BaseImagePicker implements ImagePicker {

    /**
     * Default regular expression patterns for filenames to ignore.
     * Default is files starting with a dot.
     */
    public static final Collection<String> DEFAULT_FILE_IGNORE_PATTERNS = Collections.singleton("^\\..*");

    private Random random;

    /**
     * Base directory name.
     */
    protected String baseDir;

    private List<URL> files;
    private List<String> dirs;
    private Collection<String> filenameIgnorePatterns = DEFAULT_FILE_IGNORE_PATTERNS;

    /**
     * Returns a random image found in the base directory.
     *
     * @return a random image URL
     */
    public URL nextImage() {
        return files.get(random.nextInt(files.size()));
    }

    /**
     * Returns a random image set found as a subdirectory of the base directory.
     *
     * @return random image set
     */
    public Map<String, URL> nextImageSet() {
        return buildImageSet(dirs.get(random.nextInt(dirs.size())));
    }

    /**
     * Lists all files in the given base directory. Directory names should
     * be returned with a trailing forward slash to mark them as such.
     *
     * @param dirName the directory relative to the base directory to scan for files
     * @return set of file names
     */
    protected abstract Set<String> listFiles(String dirName);

    /**
     * Converts the given relative file name into a URL, from which the
     * image's byte stream can then be read.
     *
     * @param filename the file name
     * @return a URL pointing to that file from which the image's data can be read
     */
    protected abstract URL toURL(String filename);

    private Map<String, URL> buildImageSet(String dirName) {
        Map<String, URL> imageSet = new HashMap<String, URL>();
        for(String path: filterFilenames(listFiles(dirName))) {
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


    /**
     * Scans the given base directory for images and directories which
     * represent image sets. Images returned by this picker will be files
     * found in the given directory, and image sets returned will be directories
     * found in the given directory.
     */
    public void init() {
        if(random == null) {
            random = new Random();
        }
        scanFiles();
    }

    private Set<String> filterFilenames(Set<String> filePaths) {
        Set<String> filtered = new LinkedHashSet<String>();
        if(filePaths != null) {
            filenameLoop:
            for(String path : filePaths) {
                String strippedPath = path.endsWith("/") ? path.substring(0, path.length() - 1) : path;
                String filename = strippedPath.substring(strippedPath.lastIndexOf('/') + 1);
                for(String pattern : filenameIgnorePatterns) {
                    if(filename.matches(pattern)) {
                        continue filenameLoop;
                    }
                }
                filtered.add(path);
            }
        }
        return filtered;
    }

    private void scanFiles() {
        files = new ArrayList<URL>();
        dirs = new ArrayList<String>();
        Collection<String> fileNames = new LinkedHashSet<String>(filterFilenames(listFiles(baseDir)));
        fileNames.remove(baseDir.endsWith("/") ? baseDir : baseDir + "/");
        for(String filename : fileNames) {
            if(filename.endsWith("/")) {
                dirs.add(filename);
            } else {
                files.add(toURL(filename));
            }
        }
    }

    public void setRandom(Random random) {
        this.random = random;
    }

    /**
     * Sets the base directory that this picker will scan for images in. Exactly how this
     * gets used is implementation dependent.
     *
     * @param baseDir the base directory to look in for images.
     */
    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    /**
     * Sets the patterns to use when excluding files. Default value is images that start with a dot.
     * @param patterns
     */
    public void setFilenameIgnorePatterns(Collection<String> patterns) {
        filenameIgnorePatterns = patterns;
    }
}
