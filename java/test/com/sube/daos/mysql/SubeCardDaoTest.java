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

import com.sube.beans.DataEntry;
import com.sube.beans.DocumentType;
import com.sube.beans.PhysicalPerson;
import com.sube.beans.SubeCard;
import com.sube.exceptions.card.DuplicatedSubeCardException;
import com.sube.exceptions.card.InvalidSubeCardException;
import com.sube.exceptions.person.InvalidDataEntryException;
import com.sube.exceptions.person.InvalidPhysicalPersonException;
import com.sube.utils.PasswordEncoder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/sube/resources/mysql/mysqlContext.xml","classpath:applicationContext.xml" })
public class SubeCardDaoTest extends TestCase{
	@Autowired
	private SubeCardDao subeCardDao;
	@Autowired
	private DataEntryDao dataEntryDao;
	@Autowired
	private PhysicalPersonDao physicalPersonDao;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Test
	public void subeCardDaoTest() throws NoSuchAlgorithmException,
			InvalidPhysicalPersonException, InvalidDataEntryException,
			DuplicatedSubeCardException, InvalidSubeCardException {
		DataEntry dataEntry = new DataEntry();
		dataEntry.setPassword(passwordEncoder.encodePassword("checreto"));
		PhysicalPerson physicalPerson = new PhysicalPerson();
		physicalPerson.setDocumentType(DocumentType.DNI);
		physicalPerson.setFirstName("Hetor");
		physicalPerson.setLastName("Lopez");
		physicalPerson.setIdNumber(12345678);
		physicalPerson.setId(physicalPersonDao.createPhysicalPerson(physicalPerson));
		dataEntry.setPhysicalPerson(physicalPerson);
		dataEntry.setId(dataEntryDao.createDataEntry(dataEntry));
		SubeCard subeCard = new SubeCard();
		subeCard.setBalance(Double.valueOf(0.0d));
		subeCard.setCreatedBy(dataEntry);
		subeCard.setNumber(subeCardDao.createCard(subeCard, dataEntry));
		SubeCard sameSubeCard = subeCardDao.getSubeCard(subeCard.getNumber());
		assertNull("User must be null", sameSubeCard.getUser());
		assertNull("Data Entry must be null", sameSubeCard.getCreatedBy());
		assertEquals("Balance not the same", subeCard.getBalance(), sameSubeCard.getBalance());
		assertEquals("Number not the same", subeCard.getNumber(), sameSubeCard.getNumber());
		subeCardDao.addToBalance(subeCard, 3.98d);
		subeCardDao.addToBalance(subeCard, 0.02d);
		subeCard.setBalance(4.00d);
		sameSubeCard = subeCardDao.getSubeCard(subeCard.getNumber());
		assertNull("User must be null", sameSubeCard.getUser());
		assertNull("Data Entry must be null", sameSubeCard.getCreatedBy());
		assertEquals("Balance not the same", subeCard.getBalance(), sameSubeCard.getBalance());
		assertEquals("Number not the same", subeCard.getNumber(), sameSubeCard.getNumber());
		subeCardDao.deleteCard(subeCard);
		assertNull("Sube Card still exists", subeCardDao.getSubeCard(subeCard.getNumber()));
		dataEntryDao.deleteDataEntry(dataEntry.getId());
		physicalPersonDao.deletePhysicalPerson(physicalPerson.getId());
	}

	public void setSubeCardDao(SubeCardDao subeCardDao) {
		this.subeCardDao = subeCardDao;
	}

	public void setDataEntryDao(DataEntryDao dataEntryDao) {
		this.dataEntryDao = dataEntryDao;
	}

	public void setPhysicalPersonDao(PhysicalPersonDao physicalPersonDao) {
		this.physicalPersonDao = physicalPersonDao;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
}
