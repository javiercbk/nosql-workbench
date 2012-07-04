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

import java.security.NoSuchAlgorithmException;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sube.beans.CashierProvider;
import com.sube.beans.DataEntry;
import com.sube.beans.DocumentType;
import com.sube.beans.LegalPerson;
import com.sube.beans.PhysicalPerson;
import com.sube.beans.ServiceProvider;
import com.sube.beans.SubeCard;
import com.sube.daos.mysql.CashierProviderDao;
import com.sube.daos.mysql.DataEntryDao;
import com.sube.daos.mysql.LegalPersonDao;
import com.sube.daos.mysql.PhysicalPersonDao;
import com.sube.daos.mysql.ServiceProviderDao;
import com.sube.daos.mysql.SubeCardDao;
import com.sube.daos.mysql.SubeCardUsageDao;
import com.sube.exceptions.card.DuplicatedSubeCardException;
import com.sube.exceptions.card.InvalidSubeCardException;
import com.sube.exceptions.person.InvalidCashierProviderException;
import com.sube.exceptions.person.InvalidDataEntryException;
import com.sube.exceptions.person.InvalidLegalPersonException;
import com.sube.exceptions.person.InvalidPhysicalPersonException;
import com.sube.exceptions.person.InvalidServiceProviderException;
import com.sube.utils.PasswordEncoder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/sube/resources/mysql/mysqlContext.xml","classpath:applicationContext.xml" })
public class SubeCardUsageDaoTest extends TestCase{
	@Autowired
	private SubeCardDao subeCardDao;
	@Autowired
	private PhysicalPersonDao physicalPersonDao;
	@Autowired
	private LegalPersonDao legalPersonDao;
	@Autowired
	private DataEntryDao dataEntryDao;
	@Autowired
	private SubeCardUsageDao subeCardUsageDao;
	@Autowired
	private CashierProviderDao cashierProviderDao;
	@Autowired
	private ServiceProviderDao serviceProviderDao;
	@Autowired
	private PasswordEncoder passwordEncoder;
	private ServiceProvider serviceProvider;
	private LegalPerson serviceProviderLegalPerson;
	private CashierProvider cashierProvider;
	private LegalPerson cashierProvicerLegalPerson;
	private DataEntry dataEntry;
	private PhysicalPerson physicalPersonDataEntry;
	private SubeCard subeCard;
	
	@Test
	public void testSubeCardUsageDao() throws Exception{
		createServiceProvider();
		createCashierProvider();
		createSubeCard();
		try{
		subeCardUsageDao.chargeMoney(cashierProvider, subeCard, 1.25);
		assertEquals("Sube Card Balance does not match", Double.valueOf(1.25d), subeCardDao.getBalance(subeCard));
		subeCardUsageDao.chargeMoney(cashierProvider, subeCard, 1.25);
		assertEquals("Sube Card Balance does not match", Double.valueOf(2.50d), subeCardDao.getBalance(subeCard));
		subeCardUsageDao.refundMoney(cashierProvider, subeCard, 1.25);
		assertEquals("Sube Card Balance does not match", Double.valueOf(1.25d), subeCardDao.getBalance(subeCard));
		subeCardUsageDao.chargeService(serviceProvider, subeCard, 1.25d);
		assertEquals("Sube Card Balance does not match", Double.valueOf(0d), subeCardDao.getBalance(subeCard));
		subeCardUsageDao.refundService(serviceProvider, subeCard, 1.25d);
		assertEquals("Sube Card Balance does not match", Double.valueOf(1.25d), subeCardDao.getBalance(subeCard));
		}catch(Exception e){
			//nothing to do
			e.printStackTrace();
		}finally{
			deleteAllObject();
		}
	}
	
	private void deleteAllObject() throws InvalidLegalPersonException,
			InvalidPhysicalPersonException, InvalidSubeCardException,
			InvalidCashierProviderException, InvalidServiceProviderException {
		cashierProviderDao.deleteCashierProvider(cashierProvider.getId());
		serviceProviderDao.deleteServiceProvider(serviceProvider.getId());
		legalPersonDao.deleteLegalPerson(cashierProvicerLegalPerson.getId());
		legalPersonDao.deleteLegalPerson(serviceProviderLegalPerson.getId());
		subeCardUsageDao.cleanCardUsages(subeCard.getNumber());
		subeCardDao.deleteCard(subeCard);
		dataEntryDao.deleteDataEntry(dataEntry.getId());
		physicalPersonDao.deletePhysicalPerson(physicalPersonDataEntry.getId());
	}

	private void createSubeCard() throws NoSuchAlgorithmException,
			InvalidPhysicalPersonException, InvalidDataEntryException,
			DuplicatedSubeCardException {
		dataEntry = new DataEntry();
		dataEntry.setPassword(passwordEncoder.encodePassword("checreto"));
		physicalPersonDataEntry = new PhysicalPerson();
		physicalPersonDataEntry.setDocumentType(DocumentType.DNI);
		physicalPersonDataEntry.setFirstName("Hetor");
		physicalPersonDataEntry.setLastName("Lopez");
		physicalPersonDataEntry.setIdNumber(12345678);
		physicalPersonDataEntry.setId(physicalPersonDao.createPhysicalPerson(physicalPersonDataEntry));
		dataEntry.setPhysicalPerson(physicalPersonDataEntry);
		dataEntry.setId(dataEntryDao.createDataEntry(dataEntry));
		subeCard = new SubeCard();
		subeCard.setBalance(Double.valueOf(0.0d));
		subeCard.setCreatedBy(dataEntry);
		subeCard.setNumber(subeCardDao.createCard(subeCard, dataEntry));
	}

	private void createServiceProvider() throws InvalidLegalPersonException, InvalidServiceProviderException{
		serviceProvider = new ServiceProvider();
		serviceProvider.setProviderName("Beto");
		serviceProvider.setLocation("Lugano");
		serviceProviderLegalPerson = new LegalPerson(); 
		serviceProviderLegalPerson.setCuit(123456l);
		serviceProviderLegalPerson.setFantasyName("Sube Card Fantasy Name");
		serviceProviderLegalPerson.setLegalLocation("Brick Avenue 123");
		serviceProviderLegalPerson.setLegalName("Sube Service Provider SRL");
		serviceProviderLegalPerson.setId(legalPersonDao.createLegalPerson(serviceProviderLegalPerson));
		serviceProvider.setLegalPerson(serviceProviderLegalPerson);
		serviceProvider.setId(serviceProviderDao.createServiceProvider(serviceProvider));
	}
	
	private void createCashierProvider() throws InvalidLegalPersonException, InvalidCashierProviderException{
		cashierProvider = new CashierProvider();
		cashierProvider.setCashierName("Beto");
		cashierProvider.setLocation("Lugano");
		cashierProvicerLegalPerson = new LegalPerson(); 
		cashierProvicerLegalPerson.setCuit(2345678l);
		cashierProvicerLegalPerson.setFantasyName("Sube Card Fantasy Name");
		cashierProvicerLegalPerson.setLegalLocation("Brick Avenue 123");
		cashierProvicerLegalPerson.setLegalName("Sube Cashier Provider SRL");
		cashierProvicerLegalPerson.setId(legalPersonDao.createLegalPerson(cashierProvicerLegalPerson));
		cashierProvider.setLegalPerson(cashierProvicerLegalPerson);
		cashierProvider.setId(cashierProviderDao.createCashierProvider(cashierProvider));
	}

	public void setSubeCardDao(SubeCardDao subeCardDao) {
		this.subeCardDao = subeCardDao;
	}

	public void setPhysicalPersonDao(PhysicalPersonDao physicalPersonDao) {
		this.physicalPersonDao = physicalPersonDao;
	}

	public void setLegalPersonDao(LegalPersonDao legalPersonDao) {
		this.legalPersonDao = legalPersonDao;
	}

	public void setDataEntryDao(DataEntryDao dataEntryDao) {
		this.dataEntryDao = dataEntryDao;
	}

	public void setSubeCardUsageDao(SubeCardUsageDao subeCardUsageDao) {
		this.subeCardUsageDao = subeCardUsageDao;
	}

	public void setCashierProviderDao(CashierProviderDao cashierProviderDao) {
		this.cashierProviderDao = cashierProviderDao;
	}

	public void setServiceProviderDao(ServiceProviderDao serviceProviderDao) {
		this.serviceProviderDao = serviceProviderDao;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
}
