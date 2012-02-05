package org.shamdata.test

class TestUtil {
    static String getProjectBaseDir() {
        new File("sham").exists() ? "sham" : "."
    }
}
