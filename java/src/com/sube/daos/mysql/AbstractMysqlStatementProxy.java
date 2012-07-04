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
package com.sube.daos.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.sube.storage.JDBCConnectionProvider;

public abstract class AbstractMysqlStatementProxy<T> {
	protected Connection connection;
	protected ResultSet result;
	protected PreparedStatement statement;
	protected String query;
	private JDBCConnectionProvider jdbcConnectionProvider;
	
	public abstract T execute(MysqlTransactionProxy<T> proxy) throws SQLException;
	
	protected void getNewConnection(){
		try {
			connection = jdbcConnectionProvider.getConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void closeConnection() throws SQLException {
		if(result !=null){
			result.close();
		}
		if(statement !=null){
			statement.close();
		}
		if(connection != null){
			connection.close();
		}
	}
	
	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public void setJdbcConnectionProvider(
			JDBCConnectionProvider jdbcConnectionProvider) {
		this.jdbcConnectionProvider = jdbcConnectionProvider;
	}
}
