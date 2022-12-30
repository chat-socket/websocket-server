package com.mtvu.websocketserver.utility;

import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

/**
 * @author mvu
 * @project chat-socket
 **/
public class PropertiesExtractor {
    private final static Properties properties;
    static {
        YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
        yaml.setResources(new ClassPathResource("application.yml"));
        properties = yaml.getObject();
    }

    public static String getProperty(String key){
        return properties.getProperty(key);
    }
}

