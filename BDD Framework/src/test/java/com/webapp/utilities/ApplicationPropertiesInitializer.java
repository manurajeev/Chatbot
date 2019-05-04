package com.webapp.utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApplicationPropertiesInitializer {

	private Properties object = null;

	public ApplicationPropertiesInitializer() {
		objectProperties();
	}

	/**
	 * @return properties object
	 */
	public Properties getApplicationPropertiesObject() {
		return object;
	}
    
	/**
	 * To load properties from application.properties file
	 * @param propFilePath
	 * @return
	 */
	public Properties loadPropertiesFile(String propFilePath) {
		Properties properties = null;
		try {
			properties = new Properties();
			InputStream fis = new FileInputStream(propFilePath);
			properties.load(fis);
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	/**
	 * To intialize properties object
	 * @return properties object
	 */
	public Properties objectProperties() {
		String objectFileName = "src/test/resources/config/config.properties";
		object = loadPropertiesFile(objectFileName);
		return object;
	}

}
