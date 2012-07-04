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
import java.util.ArrayList;
import java.util.List;

import com.sube.beans.LegalPerson;
import com.sube.exceptions.person.InvalidLegalPersonException;

public class LegalPersonMysqlDaoImpl implements LegalPersonDao {
	private MysqlUpdateStatementProxy<Long> createLegalPersonProxy;
	private MysqlUpdateStatementProxy<Integer> deleteLegalPersonProxy;
	private MysqlUpdateStatementProxy<Integer> modifyLegalPersonProxy;
	private MysqlStatementProxy<LegalPerson> getLegalPersonProxy;
	private MysqlStatementProxy<List<LegalPerson>> getLegalPersonByNameProxy;

	@Override
	public Long createLegalPerson(final LegalPerson legalPerson)
			throws InvalidLegalPersonException {
		try {
			return createLegalPersonProxy.execute(new MysqlTransactionProxy<Long>(){
				@Override
				public void executeQuery(PreparedStatement statement) throws SQLException{
					statement.setNull(1, Types.BIGINT);
					statement.setString(2, legalPerson.getLegalName());
					statement.setString(3, legalPerson.getFantasyName());
					statement.setLong(4, legalPerson.getCuit());
					statement.setString(5, legalPerson.getLegalLocation());
				}

				@Override
				public Long parseResult(ResultSet resultSet)
						throws SQLException {
					return resultSet.getLong(1);
				}
			});
		} catch (SQLException e) {
			InvalidLegalPersonException invalidLegalPersonException = new InvalidLegalPersonException(e.getMessage());
			invalidLegalPersonException.setCausedBy(legalPerson);
			throw invalidLegalPersonException;
		}
	}

	public void setCreateLegalPersonProxy(
			MysqlUpdateStatementProxy<Long> createLegalPersonProxy) {
		this.createLegalPersonProxy = createLegalPersonProxy;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Integer deleteLegalPerson(final Long id)
			throws InvalidLegalPersonException {
		try {
			deleteLegalPersonProxy.execute(new MysqlTransactionProxy(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setLong(1, id);
				}

				@Override
				public Object parseResult(ResultSet resultSet) throws SQLException {
					//Nothing to do really
					return null;
				}
				
			});
		} catch (SQLException e) {
			throw new InvalidLegalPersonException(e.getMessage());
		}
		return deleteLegalPersonProxy.getRowsModified();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Integer modifyLegalPerson(final LegalPerson legalPerson)
			throws InvalidLegalPersonException {
		//legal_name=?, fantasy_name=?, cuit=?, location=? WHERE id = ?"
		try {
			modifyLegalPersonProxy.execute(new MysqlTransactionProxy(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setString(1, legalPerson.getLegalName());
					statement.setString(2, legalPerson.getFantasyName());
					statement.setLong(3, legalPerson.getCuit());
					statement.setString(4, legalPerson.getLegalLocation());
					statement.setLong(5, legalPerson.getId());
				}

				@Override
				public Object parseResult(ResultSet resultSet) throws SQLException {
					// TODO Auto-generated method stub
					return null;
				}
				
			});
		} catch (SQLException e) {
			throw new InvalidLegalPersonException(e.getMessage());
		}
		return modifyLegalPersonProxy.getRowsModified();
	}

	@Override
	public LegalPerson getLegalPerson(final Long id) {
		// id, legal_name, fantasy_name, cuit, location
		LegalPerson legalPerson = null;
		try {
			legalPerson = getLegalPersonProxy.execute(new MysqlTransactionProxy<LegalPerson>(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setLong(1, id);
				}

				@Override
				public LegalPerson parseResult(ResultSet resultSet)
						throws SQLException {
					LegalPerson found = null;
					if(resultSet.next()){
						found = parseLegalPerson(resultSet);
					}
					return found;
				}
				
			});
		} catch (SQLException e) {
			//Nothing to do
			e.printStackTrace();
		}
		return legalPerson;
	}
	
	@Override
	public List<LegalPerson> getLegalPersonByName(final String name) {
		List<LegalPerson> legalPersons = null;
		try {
			legalPersons = getLegalPersonByNameProxy.execute(new MysqlTransactionProxy<List<LegalPerson>>(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setString(1, name);
					statement.setString(2, name);
				}

				@Override
				public List<LegalPerson> parseResult(ResultSet resultSet)
						throws SQLException {
					List<LegalPerson> legalPersons = new ArrayList<LegalPerson>();
					while(resultSet.next()){
						legalPersons.add(parseLegalPerson(resultSet));
					}
					return legalPersons;
				}
				
			});
		} catch (SQLException e) {
			//nothing to do
		}
		return legalPersons;
	}
	
	private LegalPerson parseLegalPerson(ResultSet resultSet)
			throws SQLException {
		LegalPerson found = new LegalPerson();
		found.setId(resultSet.getLong(1));
		found.setLegalName(resultSet.getString(2));
		found.setFantasyName(resultSet.getString(3));
		found.setCuit(resultSet.getLong(4));
		found.setLegalLocation(resultSet.getString(5));
		return found;
	}

	public void setDeleteLegalPersonProxy(
			MysqlUpdateStatementProxy<Integer> deleteLegalPersonProxy) {
		this.deleteLegalPersonProxy = deleteLegalPersonProxy;
	}

	public void setModifyLegalPersonProxy(
			MysqlUpdateStatementProxy<Integer> modifyLegalPersonProxy) {
		this.modifyLegalPersonProxy = modifyLegalPersonProxy;
	}

	public void setGetLegalPersonProxy(
			MysqlStatementProxy<LegalPerson> getLegalPersonProxy) {
		this.getLegalPersonProxy = getLegalPersonProxy;
	}

	public void setGetLegalPersonByNameProxy(
			MysqlStatementProxy<List<LegalPerson>> getLegalPersonByNameProxy) {
		this.getLegalPersonByNameProxy = getLegalPersonByNameProxy;
	}
	
}
