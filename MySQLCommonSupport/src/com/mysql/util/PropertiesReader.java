package com.mysql.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesReader {

	public static Properties read(String path) {
		Properties prop = new Properties();
		InputStream in = PropertiesReader.class.getResourceAsStream(path);
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return prop;
	}

}
