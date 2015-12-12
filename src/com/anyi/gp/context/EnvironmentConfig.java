package com.anyi.gp.context;

import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.springframework.core.io.Resource;

public class EnvironmentConfig {

  private static final Logger logger = Logger.getLogger(EnvironmentConfig.class);

  Properties props = new Properties();

  public EnvironmentConfig(Resource configFile) {
    try {
      props.load(configFile.getInputStream());
    } catch (IOException e) {
      logger.error("could not read environment.properties file from classpath.", e);
      throw new RuntimeException(e);
    }
  }

  public String get(String key) {
    return props.getProperty(key);
  }

}
