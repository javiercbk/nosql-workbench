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

import java.sql.SQLException;
import java.sql.Statement;

public class MysqlUpdateStatementProxy<T> extends AbstractMysqlStatementProxy<T> {
	private Integer rowsModified;
	
	@Override
	public T execute(MysqlTransactionProxy<T> proxy) throws SQLException {
		if(connection.isClosed()){
			getNewConnection();
		}
		statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		T executionResult = null;
		try{
			proxy.executeQuery(statement);
			rowsModified = statement.executeUpdate();
			result = statement.getGeneratedKeys();
			result.next();
			executionResult = proxy.parseResult(result);
			connection.commit();
		}catch(SQLException e){
			connection.rollback();
			throw e;
		}
		closeConnection();
		return executionResult;
	}
	
	public Integer getRowsModified() {
		return rowsModified;
	}
}
