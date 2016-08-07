package ru.atott.combiq.web.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import ru.atott.combiq.service.util.ApplicationContextHolder;
import ru.atott.combiq.service.util.EnvHolder;
import ru.atott.combiq.web.config.PropertiesHolder;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;

public final class WebUtils {

    private static Boolean production;

    private static String productionDomain;

    private WebUtils() { }

    public static String getNode() {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        return applicationContext.getBean(EnvHolder.class).getNode();
    }

    public static String getEnv() {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        return applicationContext.getBean(EnvHolder.class).getEnv();
    }

    public static InputStream getEnvResourceAsStream(String resourceFileName) {
        ApplicationContext applicationContext = ApplicationContextHolder.getApplicationContext();
        return applicationContext.getBean(EnvHolder.class).getEnvResourceAsStream(resourceFileName);
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
