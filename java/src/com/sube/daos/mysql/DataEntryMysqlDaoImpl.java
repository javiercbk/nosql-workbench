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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import com.sube.beans.DataEntry;
import com.sube.exceptions.person.InvalidDataEntryException;
import com.sube.exceptions.person.InvalidPhysicalPersonException;

public class DataEntryMysqlDaoImpl implements DataEntryDao {
	private MysqlUpdateStatementProxy<Integer> createDataEntryProxy;
	private MysqlUpdateStatementProxy<Integer> modifyDataEntryProxy;
	private MysqlUpdateStatementProxy<Integer> deleteDataEntryProxy;
	private MysqlStatementProxy<DataEntry> getDataEntryProxy;
	private MysqlStatementProxy<DataEntry> getDataEntryByPasswordProxy;
	private PhysicalPersonDao physicalPersonDao;
	

	@Override
	public Integer createDataEntry(final DataEntry dataEntry)
			throws InvalidDataEntryException {
		try {
			return createDataEntryProxy.execute(new MysqlTransactionProxy<Integer>(){
				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					//id, password, id_person
					statement.setNull(1, Types.INTEGER);
					statement.setString(2, dataEntry.getPassword());
					statement.setLong(3, dataEntry.getPhysicalPerson().getId());
				}

				@Override
				public Integer parseResult(ResultSet resultSet) throws SQLException {
					return resultSet.getInt(1);
				}
				
			});
		} catch (SQLException e) {
			throw new InvalidDataEntryException();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Integer modifyDataEntry(final DataEntry dataEntry)
			throws InvalidDataEntryException {
		try {
			physicalPersonDao.modifyPhysicalPerson(dataEntry.getPhysicalPerson());
			modifyDataEntryProxy.execute(new MysqlTransactionProxy(){
				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setString(1, dataEntry.getPassword());
					statement.setInt(2, dataEntry.getId());
				}

				@Override
				public Object parseResult(ResultSet resultSet) throws SQLException {
					return null;
				}
				
			});
		} catch (SQLException e) {
			throw new InvalidDataEntryException();
		} catch (InvalidPhysicalPersonException e) {
			throw new InvalidDataEntryException();
		}
		return modifyDataEntryProxy.getRowsModified();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Integer deleteDataEntry(final Integer id) {
		try {
			deleteDataEntryProxy.execute(new MysqlTransactionProxy(){
				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setInt(1, id);
				}

				@Override
				public Object parseResult(ResultSet resultSet) throws SQLException {
					return null;
				}
				
			});
		} catch (SQLException e) {
			//nothing to do
		}
		return modifyDataEntryProxy.getRowsModified();
	}

	@Override
	public DataEntry getDataEntry(final Integer id) {
		DataEntry dataEntry = null;
		try {
			dataEntry = getDataEntryProxy.execute(new MysqlTransactionProxy<DataEntry>(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setInt(1, id);
				}

				@Override
				public DataEntry parseResult(ResultSet resultSet)
						throws SQLException {
					DataEntry found = null;
					if(resultSet.next()){
						found = parseDataEntry(resultSet);
					}
					return found;
				}
				
			});
		} catch (SQLException e) {
			//Nothing to do
		}
		return dataEntry;
	}

	@Override
	public DataEntry getDataEntry(final Integer id, final String password) {
		DataEntry dataEntry = null;
		try {
			dataEntry = getDataEntryByPasswordProxy.execute(new MysqlTransactionProxy<DataEntry>(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setInt(1, id);
					statement.setString(2, password);
				}

				@Override
				public DataEntry parseResult(ResultSet resultSet)
						throws SQLException {
					DataEntry found = null;
					if(resultSet.next()){
						found = parseDataEntry(resultSet);
					}
					return found;
				}
				
			});
		} catch (SQLException e) {
			//Nothing to do
		}
		return dataEntry;
	}
	
	private DataEntry parseDataEntry(ResultSet resultSet)
			throws SQLException {
		DataEntry found;
		found = new DataEntry();
		found.setId(resultSet.getInt(1));
		found.setPhysicalPerson(physicalPersonDao.getPhysicalPerson(resultSet.getLong(2)));
		return found;
	}

	public void setCreateDataEntryProxy(
			MysqlUpdateStatementProxy<Integer> createDataEntryProxy) {
		this.createDataEntryProxy = createDataEntryProxy;
	}

	public void setModifyDataEntryProxy(
			MysqlUpdateStatementProxy<Integer> modifyDataEntryProxy) {
		this.modifyDataEntryProxy = modifyDataEntryProxy;
	}

	public void setGetDataEntryProxy(
			MysqlStatementProxy<DataEntry> getDataEntryProxy) {
		this.getDataEntryProxy = getDataEntryProxy;
	}

	public void setGetDataEntryByPasswordProxy(
			MysqlStatementProxy<DataEntry> getDataEntryByPasswordProxy) {
		this.getDataEntryByPasswordProxy = getDataEntryByPasswordProxy;
	}

	public void setPhysicalPersonDao(PhysicalPersonDao physicalPersonDao) {
		this.physicalPersonDao = physicalPersonDao;
	}

	public void setDeleteDataEntryProxy(
			MysqlUpdateStatementProxy<Integer> deleteDataEntryProxy) {
		this.deleteDataEntryProxy = deleteDataEntryProxy;
	}
}
