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

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sube.beans.CashierProvider;
import com.sube.beans.LegalPerson;
import com.sube.daos.mysql.CashierProviderDao;
import com.sube.daos.mysql.LegalPersonDao;
import com.sube.exceptions.person.InvalidCashierProviderException;
import com.sube.exceptions.person.InvalidLegalPersonException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/sube/resources/mysql/mysqlContext.xml","classpath:applicationContext.xml" })
public class CashierProviderDaoTest extends TestCase{
	@Autowired
	private CashierProviderDao cashierProviderDao;
	@Autowired
	private LegalPersonDao legalPersonDao;
	
	@Test
	public void testCashierProviderDao() throws InvalidCashierProviderException, InvalidLegalPersonException{
		CashierProvider cashierProvider = new CashierProvider();
		cashierProvider.setCashierName("Beto");
		cashierProvider.setLocation("Lugano");
		LegalPerson legalPerson = new LegalPerson(); 
		legalPerson.setCuit(123456l);
		legalPerson.setFantasyName("Sube Card Fantasy Name");
		legalPerson.setLegalLocation("Brick Avenue 123");
		legalPerson.setLegalName("Sube Cashier Provider SRL");
		legalPerson.setId(legalPersonDao.createLegalPerson(legalPerson));
		cashierProvider.setLegalPerson(legalPerson);
		cashierProvider.setId(cashierProviderDao.createCashierProvider(cashierProvider));
		CashierProvider sameCashierProvider = cashierProviderDao.getCashierProvider(cashierProvider.getId());
		assertEquals("Id not the same", cashierProvider.getId(), sameCashierProvider.getId());
		assertEquals("Location not the same", cashierProvider.getLocation(), sameCashierProvider.getLocation());
		assertEquals("Name not the same", cashierProvider.getCashierName(), sameCashierProvider.getCashierName());
		assertEquals("Legal Person not the same", cashierProvider.getLegalPerson(), sameCashierProvider.getLegalPerson());
		legalPerson.setCuit(456789l);
		legalPerson.setLegalLocation("Wolf Avenue 123");
		legalPerson.setLegalName("Very Legal SA");
		legalPersonDao.modifyLegalPerson(legalPerson);
		cashierProvider.setLegalPerson(legalPerson);
		cashierProvider.setCashierName("Carlo");
		cashierProvider.setLocation("Barracas");
		cashierProviderDao.modifyCashierProvider(cashierProvider);
		List<CashierProvider> cashierProviders = cashierProviderDao.getCashierProviderByName("Car");
		assertEquals("None or more than one result", Integer.valueOf(1), Integer.valueOf(cashierProviders.size()));
		assertEquals("Id not the same", cashierProvider.getId(), cashierProviders.get(0).getId());
		assertEquals("Location not the same", cashierProvider.getLocation(), cashierProviders.get(0).getLocation());
		assertEquals("Name not the same", cashierProvider.getCashierName(), cashierProviders.get(0).getCashierName());
		assertEquals("Legal Person not the same", cashierProvider.getLegalPerson(), cashierProviders.get(0).getLegalPerson());
		cashierProviderDao.deleteCashierProvider(cashierProvider.getId());
		assertNull("Cashier provider still exists", cashierProviderDao.getCashierProvider(cashierProvider.getId()));
		legalPersonDao.deleteLegalPerson(legalPerson.getId());
	}

	public void setCashierProviderDao(CashierProviderDao cashierProviderDao) {
		this.cashierProviderDao = cashierProviderDao;
	}

	public void setLegalPersonDao(LegalPersonDao legalPersonDao) {
		this.legalPersonDao = legalPersonDao;
	}
}
