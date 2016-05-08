package ru.atott.combiq.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PropertiesHolder {

    @Value("${web.production}")
    private boolean production;

    @Value("${web.production.domain}")
    private String productionDomain;

    public boolean isProduction() {
        return production;
    }

    public String getProductionDomain() {
        return productionDomain;
    }
}
