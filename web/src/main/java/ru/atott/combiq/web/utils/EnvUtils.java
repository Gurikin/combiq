package ru.atott.combiq.web.utils;

import java.io.InputStream;

public final class EnvUtils {

    private EnvUtils() { }

    public static String getNode() {
        return System.getProperty("node");
    }

    public static String getEnv() {
        return System.getProperty("env");
    }

    public static InputStream getEnvResourceAsStream(String resourceFileName) {
        return EnvUtils.class.getResourceAsStream("/env/" + getEnv() + "/" + resourceFileName);
    }
}
