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

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sube.beans.CardStatus;
import com.sube.beans.DataEntry;
import com.sube.beans.DataEntryTestDataGenerator;
import com.sube.beans.PhysicalPerson;
import com.sube.beans.PhysicalPersonTestDataGenerator;
import com.sube.beans.SubeCard;
import com.sube.beans.SubeCardTestDataGenerator;
import com.sube.beans.User;
import com.sube.beans.UserTestDataGenerator;
import com.sube.exceptions.card.InvalidSubeCardException;
import com.sube.exceptions.person.InvalidDataEntryException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/sube/resources/mongodb/mongodbContext.xml","classpath:com/sube/resources/testContext.xml","classpath:applicationContext.xml" })
public class CardDaoTest extends TestCase{
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
	private PhysicalPerson dataEntryPhysicalPerson;
	private DataEntry dataEntry;
	private SubeCard subeCard;
	private User user;
	private PhysicalPerson userPhysicalPerson;
	
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
	}
	
	@Test
	public void testCreate() throws InvalidSubeCardException, InvalidDataEntryException{
		entryDao.createDataEntry(dataEntry);
		subeCard.setCreatedBy(dataEntry);
		cardMongoDao.storeSubeCard(subeCard);
		SubeCard sameCard = null;
		assertEquals("Balance should be the same", subeCard.getBalance(), cardMongoDao.getBalance(subeCard));
		cardMongoDao.markStolen(subeCard);
		sameCard = cardMongoDao.getCards(subeCard).get(0);
		assertEquals("Cards should be the same", subeCard, sameCard);
		assertEquals("Card should be marked as stolen", CardStatus.STOLEN, sameCard.getStatus());
		cardMongoDao.markDeleted(subeCard);
		sameCard = cardMongoDao.getCards(subeCard).get(0);
		assertEquals("Cards should be the same", subeCard, sameCard);
		assertEquals("Card should be marked as deleted", CardStatus.DELETED, sameCard.getStatus());
		cardMongoDao.markLost(subeCard);
		sameCard = cardMongoDao.getCards(subeCard).get(0);
		assertEquals("Cards should be the same", subeCard, sameCard);
		assertEquals("Card should be marked as lost", CardStatus.LOST, sameCard.getStatus());
		cardMongoDao.markActive(subeCard);
		sameCard = cardMongoDao.getCards(subeCard).get(0);
		assertEquals("Cards should be the same", subeCard, sameCard);
		assertEquals("Card should be marked as active", CardStatus.ACTIVE, sameCard.getStatus());
		cardMongoDao.addToBalance(subeCard, 1.50d);
		assertEquals("Balance should be the same", 1.50d, cardMongoDao.getBalance(subeCard));
		cardMongoDao.addToBalance(subeCard, 1.50d);
		assertEquals("Balance should be the same", 3.00d, cardMongoDao.getBalance(subeCard));
	}
	
	@After
	public void tearDown() throws Exception{
		cardMongoDao.removeAll();
		entryDao.removeAll();
	}

	public void setCardMongoDao(CardDao cardMongoDao) {
		this.cardMongoDao = cardMongoDao;
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

	public void setEntryDao(EntryDao entryDao) {
		this.entryDao = entryDao;
	}
}
