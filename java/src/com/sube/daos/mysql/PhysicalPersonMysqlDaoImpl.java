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

import com.sube.beans.DocumentType;
import com.sube.beans.PhysicalPerson;
import com.sube.exceptions.person.InvalidPhysicalPersonException;

public class PhysicalPersonMysqlDaoImpl implements PhysicalPersonDao {
	private MysqlUpdateStatementProxy<Long> createPhysicalPersonProxy;
	private MysqlUpdateStatementProxy<Integer> modifyPhysicalPersonProxy;
	private MysqlStatementProxy<Integer> getDocumentTypeIdProxy;
	private MysqlStatementProxy<String> getDocumentTypeProxy;
	private MysqlStatementProxy<PhysicalPerson> getPhysicalPersonByIdNumberProxy;
	private MysqlUpdateStatementProxy<Integer> deletePhysicalPersonProxy;
	private MysqlStatementProxy<PhysicalPerson> getPhysicalPersonProxy;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Integer modifyPhysicalPerson(final PhysicalPerson person) throws InvalidPhysicalPersonException{
		try {
			final Integer documentTypeId = getDocumentTypeId(person.getDocumentType());
			modifyPhysicalPersonProxy.execute(new MysqlTransactionProxy(){
				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					//first_name = ?, last_name = ?, doc_num = ?, id_doc_type = ? WHERE id = ?
					statement.setString(1, person.getFirstName());
					statement.setString(2, person.getLastName());
					statement.setInt(3, person.getIdNumber());
					statement.setInt(4, documentTypeId);
					statement.setLong(5, person.getId());
				}

				@Override
				public Object parseResult(ResultSet resultSet) throws SQLException {
					// TODO Auto-generated method stub
					return null;
				}
			});
		} catch (SQLException e) {
			throw new InvalidPhysicalPersonException();
		}
		return modifyPhysicalPersonProxy.getRowsModified();
	}

	@Override
	public Long createPhysicalPerson(final PhysicalPerson physicalPerson) throws InvalidPhysicalPersonException{
		try {
			final Integer documentTypeId = getDocumentTypeId(physicalPerson.getDocumentType());
			return createPhysicalPersonProxy.execute(new MysqlTransactionProxy<Long>(){
				@Override
				public void executeQuery(PreparedStatement statement) throws SQLException {
					//id, first_name, last_name, doc_num, id_doc_type
					statement.setNull(1, Types.BIGINT);
					statement.setString(2, physicalPerson.getFirstName());
					statement.setString(3, physicalPerson.getLastName());
					statement.setInt(4, physicalPerson.getIdNumber());
					statement.setInt(5, documentTypeId);
				}

				@Override
				public Long parseResult(ResultSet resultSet)
						throws SQLException {
					return resultSet.getLong(1);
				}
			});
		} catch (SQLException e) {
			throw new InvalidPhysicalPersonException();
		}
	}
	
	@Override
	public Integer getDocumentTypeId(final DocumentType documentType)
			throws SQLException {
		final Integer documentTypeId = getDocumentTypeIdProxy.execute(new MysqlTransactionProxy<Integer>(){
			@Override
			public void executeQuery(PreparedStatement statement) throws SQLException {
				statement.setString(1, documentType.type);				
			}

			@Override
			public Integer parseResult(ResultSet resultSet) throws SQLException {
				resultSet.next();
				return resultSet.getInt(1);
			}
		});
		return documentTypeId;
	}
	
	public DocumentType getDocumentType(final Integer documentTypeId){
		String documentType = null;
		try {
			documentType = getDocumentTypeProxy.execute(new MysqlTransactionProxy<String>(){
				@Override
				public void executeQuery(PreparedStatement statement) throws SQLException {
					statement.setInt(1, documentTypeId);				
				}

				@Override
				public String parseResult(ResultSet resultSet) throws SQLException {
					resultSet.next();
					return resultSet.getString(1);
				}
			});
		} catch (SQLException e) {
			//nothing to do
		}
		return DocumentType.getTypeByName(documentType);
	}

	@Override
	public Integer deletePhysicalPerson(final Long id) throws InvalidPhysicalPersonException{
		try {
			deletePhysicalPersonProxy.execute(new MysqlTransactionProxy<Integer>(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setLong(1, id);
				}

				@Override
				public Integer parseResult(ResultSet resultSet) throws SQLException {
					// TODO Auto-generated method stub
					return null;
				}
				
			});
		} catch (SQLException e) {
			throw new InvalidPhysicalPersonException();
		}
		return deletePhysicalPersonProxy.getRowsModified();
	}
	
	@Override
	public PhysicalPerson getPhysicalPerson(final Long id){
		PhysicalPerson physicalPerson = null;
		try {
			physicalPerson = getPhysicalPersonProxy.execute(new MysqlTransactionProxy<PhysicalPerson>(){

				@Override
				public void executeQuery(PreparedStatement statement) throws SQLException {
					statement.setLong(1, id);
				}

				@Override
				public PhysicalPerson parseResult(ResultSet resultSet)
						throws SQLException {
					//id, first_name, last_name, doc_num, id_doc_type
					PhysicalPerson found = null;
					if(resultSet.next()){
						found = parsePhysicalPerson(resultSet);
					}
					return found;
				}
				
			});
		} catch (SQLException e) {
			//nothing to do
		}
		return physicalPerson;
	}

	@Override
	public PhysicalPerson getPhysicalPersonByIdNumber(final Integer idNumber, final DocumentType documentType) {
		PhysicalPerson physicalPerson = null;
		try {
			final Integer documentTypeId = getDocumentTypeId(documentType);
			physicalPerson = getPhysicalPersonByIdNumberProxy.execute(new MysqlTransactionProxy<PhysicalPerson>(){

				@Override
				public void executeQuery(PreparedStatement statement) throws SQLException {
					statement.setInt(1, idNumber);
					statement.setInt(2, documentTypeId);
				}

				@Override
				public PhysicalPerson parseResult(ResultSet resultSet)
						throws SQLException {
					PhysicalPerson found = null;
					if(resultSet.next()){
						found = parsePhysicalPerson(resultSet);
					}
					return found;
				}
				
			});
		} catch (SQLException e) {
			//nothing to do
		}
		return physicalPerson;
	}
	
	private PhysicalPerson parsePhysicalPerson(ResultSet resultSet)
			throws SQLException {
		PhysicalPerson found;
		found = new PhysicalPerson();
		found.setId(resultSet.getLong(1));
		found.setFirstName(resultSet.getString(2));
		found.setLastName(resultSet.getString(3));
		found.setIdNumber(resultSet.getInt(4));
		found.setDocumentType(getDocumentType(resultSet.getInt(5)));
		return found;
	}

	public void setCreatePhysicalPersonProxy(
			MysqlUpdateStatementProxy<Long> createPhysicalPersonProxy) {
		this.createPhysicalPersonProxy = createPhysicalPersonProxy;
	}

	public void setModifyPhysicalPersonProxy(
			MysqlUpdateStatementProxy<Integer> modifyPhysicalPersonProxy) {
		this.modifyPhysicalPersonProxy = modifyPhysicalPersonProxy;
	}

	public void setGetDocumentTypeIdProxy(
			MysqlStatementProxy<Integer> getDocumentTypeIdProxy) {
		this.getDocumentTypeIdProxy = getDocumentTypeIdProxy;
	}

	public void setGetDocumentTypeProxy(
			MysqlStatementProxy<String> getDocumentTypeProxy) {
		this.getDocumentTypeProxy = getDocumentTypeProxy;
	}

	public void setGetPhysicalPersonByIdNumberProxy(
			MysqlStatementProxy<PhysicalPerson> getPhysicalPersonByIdNumberProxy) {
		this.getPhysicalPersonByIdNumberProxy = getPhysicalPersonByIdNumberProxy;
	}

	public void setDeletePhysicalPersonProxy(
			MysqlUpdateStatementProxy<Integer> deletePhysicalPersonProxy) {
		this.deletePhysicalPersonProxy = deletePhysicalPersonProxy;
	}

	public void setGetPhysicalPersonProxy(
			MysqlStatementProxy<PhysicalPerson> getPhysicalPersonProxy) {
		this.getPhysicalPersonProxy = getPhysicalPersonProxy;
	}
}
