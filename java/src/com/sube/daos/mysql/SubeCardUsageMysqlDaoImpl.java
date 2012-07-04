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

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Calendar;
import java.util.List;

import com.sube.beans.CashierProvider;
import com.sube.beans.LegalPerson;
import com.sube.beans.ProviderType;
import com.sube.beans.ServiceProvider;
import com.sube.beans.SubeCard;
import com.sube.beans.SubeCardUsage;
import com.sube.beans.User;
import com.sube.exceptions.card.InvalidSubeCardException;
import com.sube.exceptions.money.InvalidMoneyException;
import com.sube.exceptions.money.MoneyException;
import com.sube.exceptions.money.NotEnoughMoneyException;
import com.sube.exceptions.person.InvalidLegalPersonException;

public class SubeCardUsageMysqlDaoImpl implements SubeCardUsageDao {
	private MysqlUpdateStatementProxy<Integer> genericChargeRefundProxy;
	private MysqlUpdateStatementProxy<Integer> cleanSubeCardProxy;
	private SubeCardDao subeCardDao;

	@Override
	public List<SubeCardUsage> listLastUsage(User user, SubeCard subeCard,
			int limit) throws InvalidSubeCardException {
		// TODO Auto-generated method stub
		return null;
	}

	private Double genericChargeRefund(final LegalPerson legalPerson,
			final ProviderType providerType, final SubeCard subeCard,
			final Double money, final Long idResponsable) throws InvalidSubeCardException,
			MoneyException, InvalidLegalPersonException{
		try {
			Double balance = subeCardDao.getBalance(subeCard);
			if(balance < -money){
				throw new NotEnoughMoneyException(balance);
			}
			genericChargeRefundProxy.execute(new MysqlTransactionProxy<Integer>() {

				@Override
				public void executeQuery(PreparedStatement statement) throws SQLException {
					// id, id_provider, provider_type, money, id_card, datetime
					statement.setNull(1, Types.BIGINT);
					statement.setLong(2, legalPerson.getId());
					statement.setInt(3, providerType.type);
					statement.setDouble(4, money);
					statement.setLong(5, subeCard.getNumber());
					statement.setDate(6, new Date(Calendar.getInstance()
							.getTimeInMillis()));
					statement.setLong(7, idResponsable);
				}

				@Override
				public Integer parseResult(ResultSet resultSet)
						throws SQLException {
					return null;
				}

			});
			Double newBalance = balance + money.doubleValue();
			subeCardDao.addToBalance(subeCard, money);
			return newBalance;
		} catch (SQLException e) {
			// if InvalidSubeCardException or InvalidMoneyException or
			// InvalidLegalPersonException
			throw new InvalidSubeCardException();
		}
	}

	@Override
	public Double chargeMoney(final CashierProvider provider,
			final SubeCard subeCard, final Double money)
			throws InvalidSubeCardException, InvalidMoneyException,
			InvalidLegalPersonException {
		try {
			return genericChargeRefund(provider.getLegalPerson(), ProviderType.CashierProvider,
					subeCard, money, provider.getId());
		} catch (MoneyException e) {
			throw new InvalidMoneyException(e);
		}
	}

	@Override
	public Double refundMoney(final CashierProvider provider,
			final SubeCard subeCard, final Double money)
			throws InvalidSubeCardException, InvalidMoneyException,
			InvalidLegalPersonException {
		// Execute Validations
		try {
			return genericChargeRefund(provider.getLegalPerson(), ProviderType.CashierProvider,
					subeCard, -money, provider.getId());
		} catch (MoneyException e) {
			throw new InvalidMoneyException(e);
		}
	}

	@Override
	public Double chargeService(final ServiceProvider provider,
			final SubeCard subeCard, final Double money)
			throws InvalidSubeCardException, NotEnoughMoneyException,
			InvalidLegalPersonException {
		try {
			return genericChargeRefund(provider.getLegalPerson(), ProviderType.ServiceProvider,
					subeCard, -money, provider.getId());
		} catch (MoneyException e) {
			throw (NotEnoughMoneyException) e;
		}
		

	}

	@Override
	public Double refundService(final ServiceProvider provider,
			final SubeCard subeCard, final Double money)
			throws InvalidSubeCardException, NotEnoughMoneyException,
			InvalidLegalPersonException {
		try {
			return genericChargeRefund(provider.getLegalPerson(), ProviderType.ServiceProvider,
					subeCard, money, provider.getId());
		} catch (MoneyException e) {
			throw (NotEnoughMoneyException) e;
		}

	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Integer cleanCardUsages(final Long id) {
		try {
			cleanSubeCardProxy.execute(new MysqlTransactionProxy(){

				@Override
				public void executeQuery(PreparedStatement statement)
						throws SQLException {
					statement.setLong(1, id);
					
				}

				@Override
				public Object parseResult(ResultSet resultSet) throws SQLException {
					// nothing to do
					return null;
				}
				
			});
		} catch (SQLException e) {
			//Nothing to do
		}
		return cleanSubeCardProxy.getRowsModified();
	}

	public void setGenericChargeRefundProxy(
			MysqlUpdateStatementProxy<Integer> genericChargeRefundProxy) {
		this.genericChargeRefundProxy = genericChargeRefundProxy;
	}

	public void setSubeCardDao(SubeCardDao subeCardDao) {
		this.subeCardDao = subeCardDao;
	}

	public void setCleanSubeCardProxy(
			MysqlUpdateStatementProxy<Integer> cleanSubeCardProxy) {
		this.cleanSubeCardProxy = cleanSubeCardProxy;
	}
}
