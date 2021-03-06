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
package com.sube.daos.mongodb;

import java.util.Date;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sube.beans.CashierProvider;
import com.sube.beans.DataEntry;
import com.sube.beans.DataEntryTestDataGenerator;
import com.sube.beans.LegalPerson;
import com.sube.beans.LegalPersonTestDataGenerator;
import com.sube.beans.PhysicalPerson;
import com.sube.beans.PhysicalPersonTestDataGenerator;
import com.sube.beans.ProviderTestDataGenerator;
import com.sube.beans.ProviderType;
import com.sube.beans.ServiceProvider;
import com.sube.beans.SubeCard;
import com.sube.beans.SubeCardTestDataGenerator;
import com.sube.beans.SubeCardUsage;
import com.sube.beans.User;
import com.sube.beans.UserTestDataGenerator;
import com.sube.exceptions.card.InvalidSubeCardException;
import com.sube.exceptions.person.InvalidDataEntryException;
import com.sube.exceptions.person.InvalidProviderException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/sube/resources/mongodb/mongodbContext.xml","classpath:com/sube/resources/testContext.xml","classpath:applicationContext.xml" })
public class CardUsagesDaoTest extends TestCase{
	@Autowired
	private CardDao cardMongoDao;
	@Autowired
	private EntryDao entryDao;
	@Autowired
	private SubeCardTestDataGenerator subeCardTestDataGenerator;
	@Autowired
	private PhysicalPersonTestDataGenerator physicalPersonTestDataGenerator;
	@Autowired
	private DataEntryTestDataGenerator dataEntryTestDataGenerator;
	@Autowired
	private UserTestDataGenerator userTestDataGenerator;
	@Autowired
	private ProviderTestDataGenerator providerTestDataGenerator;
	@Autowired
	private LegalPersonTestDataGenerator legalPersonTestDataGenerator;
	@Autowired
	private CardUsagesDao cardUsagesDao;
	@Autowired
	private ProviderDao providerDao;
	private PhysicalPerson dataEntryPhysicalPerson;
	private DataEntry dataEntry;
	private SubeCard subeCard;
	private PhysicalPerson userPhysicalPerson;
	private User user;
	private CashierProvider cashierProvider;
	private ServiceProvider serviceProvider;
	private LegalPerson legalPerson;
	
	@Before
	public void setUp() throws Exception{
		super.setUp();
		dataEntryPhysicalPerson = physicalPersonTestDataGenerator.generatePhysicalPerson();
		dataEntry = dataEntryTestDataGenerator.generateDataEntry(dataEntryPhysicalPerson);
		subeCard = subeCardTestDataGenerator.generateSubeCard(dataEntry);
		userPhysicalPerson = physicalPersonTestDataGenerator.generatePhysicalPerson();
		user = userTestDataGenerator.generate(subeCard, userPhysicalPerson);
		subeCard.setCreatedBy(dataEntry);
		subeCard.setUser(user);
		legalPerson = legalPersonTestDataGenerator.generateLegalPerson();
		cashierProvider = (CashierProvider) providerTestDataGenerator.generateProvider(ProviderType.CashierProvider, legalPerson);
		serviceProvider = (ServiceProvider) providerTestDataGenerator.generateProvider(ProviderType.ServiceProvider, legalPerson);
		providerDao.registerProvider(cashierProvider);
		providerDao.registerProvider(serviceProvider);
		entryDao.createDataEntry(dataEntry);
		subeCard.setCreatedBy(dataEntry);
		cardMongoDao.storeSubeCard(subeCard);
	}
	
	@Test
	public void testLoadUsages() throws InvalidDataEntryException, InvalidSubeCardException, InvalidProviderException{
		SubeCardUsage chargeMoney = new SubeCardUsage();
		chargeMoney.setCard(subeCard);
		chargeMoney.setDatetime(new Date());
		chargeMoney.setMoney(1.5d);
		chargeMoney.setPerformer(cashierProvider);
		chargeMoney.setCard(subeCard);
		cardUsagesDao.chargeMoney(chargeMoney);
		Double balance = cardMongoDao.getBalance(subeCard);
		assertEquals("Balance should match", Double.valueOf(1.5d), balance);
		SubeCardUsage chargeService = new SubeCardUsage();
		chargeService.setCard(subeCard);
		chargeService.setDatetime(new Date());
		chargeService.setMoney(-1.5d);
		chargeService.setPerformer(serviceProvider);
		chargeService.setCard(subeCard);
		cardUsagesDao.chargeService(chargeService);
		balance = cardMongoDao.getBalance(subeCard);
		assertEquals("Balance should match", Double.valueOf(0), balance);
		SubeCardUsage refundService = new SubeCardUsage();
		refundService.setCard(subeCard);
		refundService.setDatetime(new Date());
		refundService.setMoney(1.5d);
		refundService.setPerformer(serviceProvider);
		refundService.setCard(subeCard);
		cardUsagesDao.refundService(refundService);
		balance = cardMongoDao.getBalance(subeCard);
		assertEquals("Balance should match", Double.valueOf(1.5d), balance);
		SubeCardUsage refundMoney = new SubeCardUsage();
		refundMoney.setCard(subeCard);
		refundMoney.setDatetime(new Date());
		refundMoney.setMoney(-1.5d);
		refundMoney.setPerformer(cashierProvider);
		refundMoney.setCard(subeCard);
		cardUsagesDao.refundMoney(refundMoney);
		balance = cardMongoDao.getBalance(subeCard);
		assertEquals("Balance should match", Double.valueOf(0), balance);
		assertEquals("Count should match", Integer.valueOf(4), cardUsagesDao.count());
	}
	
	@After
	public void tearDown(){
		cardMongoDao.removeAll();
		entryDao.removeAll();
		providerDao.removeAll();
		cardUsagesDao.removeAll();
	}
	
	public void setCardMongoDao(CardDao cardMongoDao) {
		this.cardMongoDao = cardMongoDao;
	}
	public void setEntryDao(EntryDao entryDao) {
		this.entryDao = entryDao;
	}
	public void setSubeCardTestDataGenerator(
			SubeCardTestDataGenerator subeCardTestDataGenerator) {
		this.subeCardTestDataGenerator = subeCardTestDataGenerator;
	}
	public void setPhysicalPersonTestDataGenerator(
			PhysicalPersonTestDataGenerator physicalPersonTestDataGenerator) {
		this.physicalPersonTestDataGenerator = physicalPersonTestDataGenerator;
	}
	public void setDataEntryTestDataGenerator(
			DataEntryTestDataGenerator dataEntryTestDataGenerator) {
		this.dataEntryTestDataGenerator = dataEntryTestDataGenerator;
	}
	public void setUserTestDataGenerator(UserTestDataGenerator userTestDataGenerator) {
		this.userTestDataGenerator = userTestDataGenerator;
	}
	public void setCardUsagesDao(CardUsagesDao cardUsagesDao) {
		this.cardUsagesDao = cardUsagesDao;
	}

	public void setProviderDao(ProviderDao providerDao) {
		this.providerDao = providerDao;
	}
	
	public void setProviderTestDataGenerator(
			ProviderTestDataGenerator providerTestDataGenerator) {
		this.providerTestDataGenerator = providerTestDataGenerator;
	}
	
	public void setLegalPersonTestDataGenerator(
			LegalPersonTestDataGenerator legalPersonTestDataGenerator) {
		this.legalPersonTestDataGenerator = legalPersonTestDataGenerator;
	}
}
