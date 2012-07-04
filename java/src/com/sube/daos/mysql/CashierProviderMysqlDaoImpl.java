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

import com.sube.beans.CashierProvider;
import com.sube.exceptions.person.InvalidCashierProviderException;
import com.sube.exceptions.person.InvalidLegalPersonException;

public class CashierProviderMysqlDaoImpl implements CashierProviderDao {
	private MysqlUpdateStatementProxy<Long> createCashierProviderProxy;
	private MysqlUpdateStatementProxy<Integer> modifyCashierProviderProxy;
	private MysqlUpdateStatementProxy<Integer> deleteCashierProviderProxy;
	private MysqlStatementProxy<CashierProvider> getCashierProviderProxy;
	private MysqlStatementProxy<List<CashierProvider>> getCashierProviderByNameProxy;
	private LegalPersonDao legalPersonDao;

	@Override
	public Long createCashierProvider(final CashierProvider provider)
			throws InvalidCashierProviderException {
		// id, name, location, id_person
		try {
			return createCashierProviderProxy.execute(new MysqlTransactionProxy<Long>(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setNull(1, Types.BIGINT);
					statement.setString(2, provider.getCashierName());
					statement.setString(3, provider.getLocation());
					statement.setLong(4, provider.getLegalPerson().getId());
				}

				@Override
				public Long parseResult(ResultSet resultSet) throws SQLException {
					return resultSet.getLong(1);
				}
				
			});
		} catch (SQLException e) {
			throw new InvalidCashierProviderException();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Integer modifyCashierProvider(final CashierProvider provider)
			throws InvalidCashierProviderException {
		try {
			legalPersonDao.modifyLegalPerson(provider.getLegalPerson());
			modifyCashierProviderProxy.execute(new MysqlTransactionProxy(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					//name = ?, location = ?, id_person = ? WHERE id = ?
					statement.setString(1, provider.getCashierName());
					statement.setString(2, provider.getLocation());
					statement.setLong(3, provider.getLegalPerson().getId());
					statement.setLong(4, provider.getId());
				}

				@Override
				public Object parseResult(ResultSet resultSet)
						throws SQLException {
					//nothing to do
					return null;
				}
				
			});
		} catch (InvalidLegalPersonException e) {
			throw new InvalidCashierProviderException();
		} catch (SQLException e) {
			throw new InvalidCashierProviderException();
		}
		return modifyCashierProviderProxy.getRowsModified();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Integer deleteCashierProvider(final Long id)
			throws InvalidCashierProviderException {
		try {
			deleteCashierProviderProxy.execute(new MysqlTransactionProxy(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setLong(1, id);
				}

				@Override
				public Object parseResult(ResultSet resultSet) throws SQLException {
					// TODO Auto-generated method stub
					return null;
				}
			});
		} catch (SQLException e) {
			throw new InvalidCashierProviderException();
		}
		return deleteCashierProviderProxy.getRowsModified();
	}

	@Override
	public CashierProvider getCashierProvider(final Long id) {
		CashierProvider cashierProvider = null;
		try {
			cashierProvider = getCashierProviderProxy.execute(new MysqlTransactionProxy<CashierProvider>(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setLong(1, id);
				}

				@Override
				public CashierProvider parseResult(ResultSet resultSet)
						throws SQLException {
					CashierProvider cashierProvider = null;
					if(resultSet.next()){
						//id, name, location, id_person
						cashierProvider = parseCashierProvider(resultSet);
					}
					return cashierProvider;
				}
				
			});
		} catch (SQLException e) {
			//nothing to do
		}
		return cashierProvider;
	}

	@Override
	public List<CashierProvider> getCashierProviderByName(final String name) {
		List<CashierProvider> cashierProviders = null;
		try {
			cashierProviders = getCashierProviderByNameProxy.execute(new MysqlTransactionProxy<List<CashierProvider>>(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setString(1, name);
				}

				@Override
				public List<CashierProvider> parseResult(ResultSet resultSet)
						throws SQLException {
					List<CashierProvider> cashierProviders = new ArrayList<CashierProvider>();
					while(resultSet.next()){
						cashierProviders.add(parseCashierProvider(resultSet));
					}
					return cashierProviders;
				}
				
			});
		} catch (SQLException e) {
			//nothing to do
		}
		return cashierProviders;
	}
	
	private CashierProvider parseCashierProvider(ResultSet resultSet)
			throws SQLException {
		CashierProvider cashierProvider;
		cashierProvider = new CashierProvider();
		cashierProvider.setId(resultSet.getLong(1));
		cashierProvider.setCashierName(resultSet.getString(2));
		cashierProvider.setLocation(resultSet.getString(3));
		cashierProvider.setLegalPerson(legalPersonDao.getLegalPerson(resultSet.getLong(4)));
		return cashierProvider;
	}

	public void setCreateCashierProviderProxy(
			MysqlUpdateStatementProxy<Long> createCashierProviderProxy) {
		this.createCashierProviderProxy = createCashierProviderProxy;
	}

	public void setModifyCashierProviderProxy(
			MysqlUpdateStatementProxy<Integer> modifyCashierProviderProxy) {
		this.modifyCashierProviderProxy = modifyCashierProviderProxy;
	}

	public void setDeleteCashierProviderProxy(
			MysqlUpdateStatementProxy<Integer> deleteCashierProviderProxy) {
		this.deleteCashierProviderProxy = deleteCashierProviderProxy;
	}

	public void setLegalPersonDao(LegalPersonDao legalPersonDao) {
		this.legalPersonDao = legalPersonDao;
	}

	public void setGetCashierProviderProxy(
			MysqlStatementProxy<CashierProvider> getCashierProviderProxy) {
		this.getCashierProviderProxy = getCashierProviderProxy;
	}

	public void setGetCashierProviderByNameProxy(
			MysqlStatementProxy<List<CashierProvider>> getCashierProviderByNameProxy) {
		this.getCashierProviderByNameProxy = getCashierProviderByNameProxy;
	}
}
