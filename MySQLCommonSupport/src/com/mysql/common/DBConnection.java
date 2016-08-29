package com.mysql.common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.mysql.util.PropertiesReader;

public class DBConnection {
	static Properties prop = PropertiesReader.read("/config.properties");
	static String classForName = prop.getProperty("db.driver.class.name");
	static String databaseURL = prop.getProperty("db.url");
	static String username = prop.getProperty("db.username");
	static String password = prop.getProperty("db.password");

	public static Connection getConnection() throws ClassNotFoundException, SQLException {
	
		Class.forName(classForName);
		Connection connection =  DriverManager.getConnection(databaseURL, username, password);
		return connection;
		
	}

}
