package com.haina.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/*
 * 加载jdbc驱动类
 * 
 */
public class ConnectionManager {
	
	
	
	//获取jdbc驱动的方法
	public static Connection getConnection(){
	//1.创建properties对象
	Properties pro = new Properties();
	//2.获取jdbc.properties的输入流
	InputStream in = ConnectionManager.class.getClassLoader()
			.getResourceAsStream("jdbc.properties");
	//3.加载对应输出流
	
	 Connection conn = null;
	//4. 加载数据库驱动程序(对应的 Driver 实现类中有注册驱动的静态代码块.)
	try {
		pro.load(in);
		String user = pro.getProperty("user");
		String password = pro.getProperty("password");
		String jdbcUrl = pro.getProperty("jdbcUrl");
		String driver = pro.getProperty("driver");
		Class.forName(driver);
       conn = DriverManager.getConnection(jdbcUrl, user, password);
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

		return conn;
	
	}
	public static void releaseDB(ResultSet resultSet, Statement statement,
			Connection connection) {

		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	

}
