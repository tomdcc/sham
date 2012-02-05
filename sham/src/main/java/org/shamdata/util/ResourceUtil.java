package org.shamdata.util;

import java.io.InputStream;
import java.util.Locale;

public class ResourceUtil {
    public static InputStream readResource(Class base, String baseName, String type) {
        return readResource(base, baseName, type, Locale.getDefault());
    }
    public static InputStream readResource(Class base, String baseName, String type, Locale locale) {
        String resourceName = baseName + "_en." + type;
        return base.getResourceAsStream(resourceName);
    }
}
