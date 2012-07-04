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

import com.sube.beans.PhysicalPerson;
import com.sube.beans.SubeCard;
import com.sube.beans.User;
import com.sube.exceptions.card.InvalidSubeCardException;
import com.sube.exceptions.person.InvalidPhysicalPersonException;
import com.sube.exceptions.person.InvalidUserException;

public class UserMysqlDaoImpl implements UserDao {
	private MysqlUpdateStatementProxy<Long> createUserProxy;
	private MysqlUpdateStatementProxy<Integer> deleteUserProxy;
	private MysqlStatementProxy<User> getUserProxy;
	private PhysicalPersonDao physicalPersonDao;

	@Override
	public Long createUser(final User user, final SubeCard subeCard)
			throws InvalidSubeCardException, InvalidUserException {
		final PhysicalPerson physicalPerson = findOrCreatePhysicalPerson(user);
		try {
			return createUserProxy.execute(new MysqlTransactionProxy<Long>(){
				@Override
				public void executeQuery(PreparedStatement statement) throws SQLException {
					statement.setNull(0, Types.BIGINT);
					statement.setLong(1, subeCard.getNumber());
					statement.setLong(2, physicalPerson.getId());
					statement.execute();
					statement.executeUpdate();
					
				}

				@Override
				public Long parseResult(ResultSet resultSet)
						throws SQLException {
					return resultSet.getLong(0);
				}
				
			});
		} catch (SQLException e) {
			//if InvalidSubeCardException, InvalidUserException
			throw new InvalidSubeCardException();
		}
	}

	private PhysicalPerson findOrCreatePhysicalPerson(final User user) throws InvalidUserException {
		PhysicalPerson  physicalPerson = physicalPersonDao.getPhysicalPersonByIdNumber(user.getPhysicalPerson().getIdNumber(), user.getPhysicalPerson().getDocumentType());
		if(!(physicalPerson == null)){
			try {
				physicalPerson.setId(physicalPersonDao.createPhysicalPerson(user.getPhysicalPerson()));
			} catch (InvalidPhysicalPersonException e) {
				throw new InvalidUserException();
			}
		}
		return physicalPerson;
	}

	@Override
	public Integer deleteUser(final User user) throws InvalidUserException {
		try {
			deleteUserProxy.execute(new MysqlTransactionProxy<Integer>(){

				@Override
				public void executeQuery(PreparedStatement statement) throws SQLException {
					statement.setLong(0, user.getId());
				}

				@Override
				public Integer parseResult(ResultSet resultSet)
						throws SQLException {
					return null;
				}
				
			});
		} catch (SQLException e) {
			throw new InvalidUserException();
		}
		return deleteUserProxy.getRowsModified();
	}

	@Override
	public User getUser(final Long id){
		User user = null;
		try {
			user = getUserProxy.execute(new MysqlTransactionProxy<User>(){
				@Override
				public void executeQuery(PreparedStatement statement) throws SQLException {
					statement.setLong(0, id);
				}

				@Override
				public User parseResult(ResultSet resultSet)
						throws SQLException {
					User found = null;
					if(resultSet.next()){
						found = new User();
						found.setId(resultSet.getLong(0));
						SubeCard subeCard = new SubeCard();
						subeCard.setNumber(resultSet.getLong(1));
						found.setSubeCard(subeCard);
						found.setPhysicalPerson(physicalPersonDao.getPhysicalPerson(resultSet.getLong(2)));
					}
					return found;
				}
			});
		} catch (SQLException e) {
			//nothing to do
		}
		return user;
	}

	public void setCreateUserProxy(MysqlUpdateStatementProxy<Long> createUserProxy) {
		this.createUserProxy = createUserProxy;
	}

	public void setDeleteUserProxy(MysqlUpdateStatementProxy<Integer> deleteUserProxy) {
		this.deleteUserProxy = deleteUserProxy;
	}

	public void setGetUserProxy(MysqlStatementProxy<User> getUserProxy) {
		this.getUserProxy = getUserProxy;
	}

	public void setPhysicalPersonDao(PhysicalPersonDao physicalPersonDao) {
		this.physicalPersonDao = physicalPersonDao;
	}
	
}
