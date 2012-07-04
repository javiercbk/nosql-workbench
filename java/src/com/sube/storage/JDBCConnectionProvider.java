/*******************************************************************************
 * Copyright 2012 Javier Ignacio Lecuona
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.sube.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class JDBCConnectionProvider {
	private static JDBCConnectionProvider instance;
	private String driverUrl;
	private String jdbcDatabaseUrl;
	private Properties connectionProperties;
	
	private JDBCConnectionProvider(){
		
	}
	
	public static JDBCConnectionProvider getInstance(){
		if(instance == null){
			instance = new JDBCConnectionProvider();
		}
		return instance;
	}
	
	public Connection getConnection() throws Exception{
		Class.forName(driverUrl).newInstance();
		Connection connection = DriverManager.getConnection(jdbcDatabaseUrl,connectionProperties);
		connection.setAutoCommit(false);
		return connection;
	}

	public void setJdbcDatabaseUrl(String jdbcDatabaseUrl) {
		this.jdbcDatabaseUrl = jdbcDatabaseUrl;
	}

	public void setConnectionProperties(Properties connectionProperties) {
		this.connectionProperties = connectionProperties;
	}

	public void setDriverUrl(String driverUrl) {
		this.driverUrl = driverUrl;
	}
}
