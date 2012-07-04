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
import com.sube.daos.mysql.DataEntryDao;
import com.sube.daos.mysql.PhysicalPersonDao;
import com.sube.exceptions.person.InvalidDataEntryException;
import com.sube.exceptions.person.InvalidPhysicalPersonException;
import com.sube.utils.PasswordEncoder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/sube/resources/mysql/mysqlContext.xml","classpath:applicationContext.xml" })
public class DataEntryDaoTest extends TestCase{
	private static final String PASSWORD = "ilNono";
	@Autowired
	private DataEntryDao dataEntryDao;
	@Autowired
	private PhysicalPersonDao physicalPersonDao;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Test
	public void testDataEntryDao() throws NoSuchAlgorithmException, InvalidDataEntryException, InvalidPhysicalPersonException{
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
		DataEntry sameDataEntry = dataEntryDao.getDataEntry(dataEntry.getId());
		assertEquals("Physical Person not the same", dataEntry.getPhysicalPerson(), sameDataEntry.getPhysicalPerson());
		assertEquals("Id not the same", dataEntry.getId(), sameDataEntry.getId());
		physicalPerson.setDocumentType(DocumentType.LE);
		physicalPerson.setFirstName("Vitor");
		physicalPerson.setLastName("Gomez");
		physicalPerson.setIdNumber(234567);
		dataEntry.setPassword(passwordEncoder.encodePassword(PASSWORD));
		dataEntry.setPhysicalPerson(physicalPerson);
		dataEntryDao.modifyDataEntry(dataEntry);
		assertNull("Password not Encoded", dataEntryDao.getDataEntry(dataEntry.getId(), PASSWORD));
		sameDataEntry = dataEntryDao.getDataEntry(dataEntry.getId(), passwordEncoder.encodePassword(PASSWORD));
		assertEquals("Physical Person not the same", dataEntry.getPhysicalPerson(), sameDataEntry.getPhysicalPerson());
		assertEquals("Id not the same", dataEntry.getId(), sameDataEntry.getId());
		dataEntryDao.deleteDataEntry(dataEntry.getId());
		assertNull("Data Entry still exists", dataEntryDao.getDataEntry(dataEntry.getId()));
		physicalPersonDao.deletePhysicalPerson(physicalPerson.getId());
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
