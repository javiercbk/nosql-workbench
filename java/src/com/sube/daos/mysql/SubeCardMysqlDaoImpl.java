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
import com.sube.beans.SubeCard;
import com.sube.beans.User;
import com.sube.exceptions.card.DuplicatedSubeCardException;
import com.sube.exceptions.card.InvalidSubeCardException;

public class SubeCardMysqlDaoImpl implements SubeCardDao {
	private MysqlUpdateStatementProxy<Long> createCardProxy;
	private MysqlUpdateStatementProxy<Long> createUserlessCardProxy;
	private MysqlUpdateStatementProxy<Integer> updateBalanceProxy;
	private MysqlUpdateStatementProxy<Integer> deleteCardProxy;
	private MysqlStatementProxy<Double> queryBalanceProxy;
	private MysqlStatementProxy<SubeCard> getSubeCardProxy;

	@Override
	public Long createCard(final SubeCard subeCard, final DataEntry dataEntry, final User user)
			throws DuplicatedSubeCardException {
		try {
		return createCardProxy.execute(new MysqlTransactionProxy<Long>(){
			@Override
			public void executeQuery(PreparedStatement statement) throws SQLException {
				statement.setNull(1, Types.BIGINT);
				statement.setDouble(2, subeCard.getBalance());
				statement.setLong(3, dataEntry.getId());
				statement.setLong(4, user.getId());
			}

			@Override
			public Long parseResult(ResultSet resultSet) throws SQLException {
				return resultSet.getLong(1);
			}
		});			
		} catch (SQLException e) {
			//Should check if duplicated key but let's not tell anyone
			DuplicatedSubeCardException duplicatedSubeCardException = new DuplicatedSubeCardException(subeCard, e.getMessage());
			throw duplicatedSubeCardException;
		}
	}
	
	@Override
	public Long createCard(final SubeCard subeCard, final DataEntry dataEntry)
			throws DuplicatedSubeCardException {
		try {
			return createUserlessCardProxy.execute(new MysqlTransactionProxy<Long>(){
				@Override
				public void executeQuery(PreparedStatement statement) throws SQLException {
					statement.setNull(1, Types.BIGINT);
					statement.setDouble(2, subeCard.getBalance());
					statement.setLong(3, dataEntry.getId());
				}

				@Override
				public Long parseResult(ResultSet resultSet) throws SQLException {
					return resultSet.getLong(1);
				}
			});			
			} catch (SQLException e) {
				//Should check if duplicated key but let's not tell anyone
				DuplicatedSubeCardException duplicatedSubeCardException = new DuplicatedSubeCardException(subeCard, e.getMessage());
				throw duplicatedSubeCardException;
			}
	}

	@Override
	public Integer deleteCard(final SubeCard subeCard)
			throws InvalidSubeCardException {
		try {
			deleteCardProxy.execute(new MysqlTransactionProxy<Integer>(){
				@Override
				public void executeQuery(PreparedStatement statement) throws SQLException {
					statement.setLong(1, subeCard.getNumber());
				}

				@Override
				public Integer parseResult(ResultSet resultSet)
						throws SQLException {
					return null;
				}
				
			});
		} catch (SQLException e) {
			throw new InvalidSubeCardException();
		}
		return deleteCardProxy.getRowsModified();
	}
	
	@Override
	public Double getBalance(final SubeCard subeCard)
			throws InvalidSubeCardException {
		try {
			return queryBalanceProxy.execute(new MysqlTransactionProxy<Double>(){
				@Override
				public void executeQuery(PreparedStatement statement) throws SQLException {
					statement.setLong(1, subeCard.getNumber());
				}

				@Override
				public Double parseResult(ResultSet resultSet)
						throws SQLException {
					resultSet.next();
					return resultSet.getDouble(1);
				}
				
			});
		} catch (SQLException e) {
			throw new InvalidSubeCardException();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Double addToBalance(final SubeCard subeCard, final Double money)
			throws InvalidSubeCardException {
		try {
			final Double newBalance = getBalance(subeCard) + money;
			updateBalanceProxy.execute(new MysqlTransactionProxy(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setDouble(1, newBalance);
					statement.setLong(2, subeCard.getNumber());
				}

				@Override
				public Object parseResult(ResultSet resultSet) throws SQLException {
					return null;
				}
				
			});
			return newBalance;
		} catch (SQLException e) {
			throw new InvalidSubeCardException();
		}
	}
	
	@Override
	public SubeCard getSubeCard(final Long id) {
		SubeCard subeCard = null;
		try {
			subeCard = getSubeCardProxy.execute(new MysqlTransactionProxy<SubeCard>(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setLong(1, id);
				}

				@Override
				public SubeCard parseResult(ResultSet resultSet)
						throws SQLException {
					SubeCard found = null;
					if(resultSet.next()){
						found = new SubeCard();
						found.setNumber(resultSet.getLong(1));
						found.setBalance(resultSet.getDouble(2));
					}
					return found;
				}
			});
		} catch (SQLException e) {
			//Nothing to do
		}
		return subeCard;
	}

	public void setCreateCardProxy(MysqlUpdateStatementProxy<Long> createCardProxy) {
		this.createCardProxy = createCardProxy;
	}

	public void setDeleteCardProxy(MysqlUpdateStatementProxy<Integer> deleteCardProxy) {
		this.deleteCardProxy = deleteCardProxy;
	}

	public void setQueryBalanceProxy(MysqlStatementProxy<Double> queryBalanceProxy) {
		this.queryBalanceProxy = queryBalanceProxy;
	}

	public void setCreateUserlessCardProxy(
			MysqlUpdateStatementProxy<Long> createUserlessCardProxy) {
		this.createUserlessCardProxy = createUserlessCardProxy;
	}

	public void setUpdateBalanceProxy(
			MysqlUpdateStatementProxy<Integer> updateBalanceProxy) {
		this.updateBalanceProxy = updateBalanceProxy;
	}

	public void setGetSubeCardProxy(MysqlStatementProxy<SubeCard> getSubeCardProxy) {
		this.getSubeCardProxy = getSubeCardProxy;
	}
}
