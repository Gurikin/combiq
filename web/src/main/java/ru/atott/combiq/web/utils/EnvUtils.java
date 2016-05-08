package ru.atott.combiq.web.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import ru.atott.combiq.service.util.ApplicationContextHolder;
import ru.atott.combiq.web.config.PropertiesHolder;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

public final class EnvUtils {

    private static Boolean production;

    private static String productionDomain;

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

    public static boolean isProduction() {
        if (production == null) {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            PropertiesHolder propertiesHolder = applicationContext.getBean(PropertiesHolder.class);
            production = propertiesHolder.isProduction();
        }

        return production;
    }

    public static boolean isProductionClusterNodeRequest(HttpServletRequest request) {
        return isProduction() && !isProductionRequest(request);
    }

    public static boolean isProductionRequest(HttpServletRequest request) {
        if (!isProduction()) {
            return false;
        }

        if (productionDomain == null) {
            ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
            PropertiesHolder propertiesHolder = applicationContext.getBean(PropertiesHolder.class);
            productionDomain = propertiesHolder.getProductionDomain();
        }

        return StringUtils.equalsIgnoreCase(request.getServerName(), productionDomain);
    }
}
