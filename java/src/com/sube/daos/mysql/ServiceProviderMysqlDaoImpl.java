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

import com.sube.beans.ServiceProvider;
import com.sube.exceptions.person.InvalidLegalPersonException;
import com.sube.exceptions.person.InvalidServiceProviderException;

public class ServiceProviderMysqlDaoImpl implements ServiceProviderDao{
	private MysqlUpdateStatementProxy<Long> createServiceProviderProxy;
	private MysqlUpdateStatementProxy<Integer> modifyServiceProviderProxy;
	private MysqlUpdateStatementProxy<Integer> deleteServiceProviderProxy;
	private MysqlStatementProxy<ServiceProvider> getServiceProviderProxy;
	private MysqlStatementProxy<List<ServiceProvider>> getServiceProviderByNameProxy;
	private LegalPersonDao legalPersonDao;

	@Override
	public Long createServiceProvider(final ServiceProvider provider)
			throws InvalidServiceProviderException {
		// id, name, location, id_person
		try {
			return createServiceProviderProxy.execute(new MysqlTransactionProxy<Long>(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setNull(1, Types.BIGINT);
					statement.setString(2, provider.getProviderName());
					statement.setString(3, provider.getLocation());
					statement.setLong(4, provider.getLegalPerson().getId());
				}

				@Override
				public Long parseResult(ResultSet resultSet) throws SQLException {
					// TODO Auto-generated method stub
					return resultSet.getLong(1);
				}
				
			});
		} catch (SQLException e) {
			throw new InvalidServiceProviderException();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Integer modifyServiceProvider(final ServiceProvider provider)
			throws InvalidServiceProviderException {
		try {
			legalPersonDao.modifyLegalPerson(provider.getLegalPerson());
			modifyServiceProviderProxy.execute(new MysqlTransactionProxy(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					//name = ?, location = ?, id_person = ? WHERE id = ?
					statement.setString(1, provider.getProviderName());
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
			throw new InvalidServiceProviderException();
		} catch (SQLException e) {
			throw new InvalidServiceProviderException();
		}
		return modifyServiceProviderProxy.getRowsModified();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Integer deleteServiceProvider(final Long id)
			throws InvalidServiceProviderException {
		try {
			deleteServiceProviderProxy.execute(new MysqlTransactionProxy(){

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
			throw new InvalidServiceProviderException();
		}
		return deleteServiceProviderProxy.getRowsModified();
	}

	@Override
	public ServiceProvider getServiceProvider(final Long id) {
		ServiceProvider ServiceProvider = null;
		try {
			ServiceProvider = getServiceProviderProxy.execute(new MysqlTransactionProxy<ServiceProvider>(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setLong(1, id);
				}

				@Override
				public ServiceProvider parseResult(ResultSet resultSet)
						throws SQLException {
					ServiceProvider ServiceProvider = null;
					if(resultSet.next()){
						//id, name, location, id_person
						ServiceProvider = parseServiceProvider(resultSet);
					}
					return ServiceProvider;
				}
				
			});
		} catch (SQLException e) {
			//nothing to do
		}
		return ServiceProvider;
	}

	@Override
	public List<ServiceProvider> getServiceProviderByName(final String name) {
		List<ServiceProvider> ServiceProviders = null;
		try {
			ServiceProviders = getServiceProviderByNameProxy.execute(new MysqlTransactionProxy<List<ServiceProvider>>(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setString(1, name);
				}

				@Override
				public List<ServiceProvider> parseResult(ResultSet resultSet)
						throws SQLException {
					List<ServiceProvider> ServiceProviders = new ArrayList<ServiceProvider>();
					while(resultSet.next()){
						ServiceProviders.add(parseServiceProvider(resultSet));
					}
					return ServiceProviders;
				}
				
			});
		} catch (SQLException e) {
			//nothing to do
		}
		return ServiceProviders;
	}
	
	private ServiceProvider parseServiceProvider(ResultSet resultSet)
			throws SQLException {
		ServiceProvider serviceProvider;
		serviceProvider = new ServiceProvider();
		serviceProvider.setId(resultSet.getLong(1));
		serviceProvider.setProviderName(resultSet.getString(2));
		serviceProvider.setLocation(resultSet.getString(3));
		serviceProvider.setLegalPerson(legalPersonDao.getLegalPerson(resultSet.getLong(4)));
		return serviceProvider;
	}

	public void setCreateServiceProviderProxy(
			MysqlUpdateStatementProxy<Long> createServiceProviderProxy) {
		this.createServiceProviderProxy = createServiceProviderProxy;
	}

	public void setModifyServiceProviderProxy(
			MysqlUpdateStatementProxy<Integer> modifyServiceProviderProxy) {
		this.modifyServiceProviderProxy = modifyServiceProviderProxy;
	}

	public void setDeleteServiceProviderProxy(
			MysqlUpdateStatementProxy<Integer> deleteServiceProviderProxy) {
		this.deleteServiceProviderProxy = deleteServiceProviderProxy;
	}

	public void setGetServiceProviderProxy(
			MysqlStatementProxy<ServiceProvider> getServiceProviderProxy) {
		this.getServiceProviderProxy = getServiceProviderProxy;
	}

	public void setGetServiceProviderByNameProxy(
			MysqlStatementProxy<List<ServiceProvider>> getServiceProviderByNameProxy) {
		this.getServiceProviderByNameProxy = getServiceProviderByNameProxy;
	}

	public void setLegalPersonDao(LegalPersonDao legalPersonDao) {
		this.legalPersonDao = legalPersonDao;
	}
}
