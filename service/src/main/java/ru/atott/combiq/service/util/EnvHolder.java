package ru.atott.combiq.service.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EnvHolder {

    @Autowired
    @Qualifier("serviceProperties")
    private Properties serviceProperties;

    public String getNode() {
        return StringUtils.defaultString(System.getProperty("node"), "local");
    }

    public String getEnv() {
        return System.getProperty("env");
    }

    public InputStream getEnvResourceAsStream(String resourceFileName) {
        return EnvHolder.class.getResourceAsStream("/env/" + getEnv() + "/" + resourceFileName);
    }

    public Set<String> getDeclaredClusterNodes() {
        return serviceProperties.stringPropertyNames().stream()
                .filter(propertyName -> propertyName.startsWith("service.cluster."))
                .map(propertyName -> StringUtils.substringBetween(propertyName, "service.cluster.", "."))
                .collect(Collectors.toSet());
    }
}
