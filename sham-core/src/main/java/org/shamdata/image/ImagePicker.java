package org.shamdata.image;

import org.shamdata.ShamGenerator;

import java.net.URL;
import java.util.Map;

/**
 * A class which will pick a random image or set of images.
 */
public interface ImagePicker extends ShamGenerator {

    /**
     * Returns a {@link URL} to a random image.
     *
     * @return a URL to a randomly selected image.
     */
    URL nextImage();

    /**
     * Returns a random image set. The set consists of a {@link Map} of
     * strings to URLs to the actual image, ith the image name (probably
     * derived from the image file name) as the key.
     *
     * @return the randomly selected image set
     */
    Map<String,URL> nextImageSet();

    /**
     * Sets the base directory for the image picker. The exact meaning will
     * be implementation specific. This should be set before {@link #init}
     * is called.
     *
     * @param baseDir the base directory to look in for images.
     */
    void setBaseDir(String baseDir);

    /**
     * Initialises the image picker. While implementation specific, this
     * will probably involve scanning the base directory previously set
     * by {@link #setBaseDir}
     */
    void init();
}
