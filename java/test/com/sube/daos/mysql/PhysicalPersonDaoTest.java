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

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sube.beans.DocumentType;
import com.sube.beans.PhysicalPerson;
import com.sube.daos.mysql.PhysicalPersonDao;
import com.sube.exceptions.person.InvalidPhysicalPersonException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/sube/resources/mysql/mysqlContext.xml","classpath:applicationContext.xml" })
public class PhysicalPersonDaoTest extends TestCase{
	@Autowired
	private PhysicalPersonDao physicalPersonDao;
	
	@Test
	public void testPhysicalPersonDao() throws InvalidPhysicalPersonException{
		PhysicalPerson physicalPerson = new PhysicalPerson();
		physicalPerson.setDocumentType(DocumentType.DNI);
		physicalPerson.setFirstName("Hetor");
		physicalPerson.setLastName("Lopez");
		physicalPerson.setIdNumber(12345678);
		physicalPerson.setId(physicalPersonDao.createPhysicalPerson(physicalPerson));
		PhysicalPerson samePhysicalPerson = physicalPersonDao.getPhysicalPerson(physicalPerson.getId());
		assertEquals("First Name not the same", physicalPerson.getFirstName(), samePhysicalPerson.getFirstName());
		assertEquals("Last Name not the same", physicalPerson.getLastName(), samePhysicalPerson.getLastName());
		assertEquals("Document Type not the same", physicalPerson.getDocumentType(), samePhysicalPerson.getDocumentType());
		assertEquals("Id Number not the same", physicalPerson.getIdNumber(), samePhysicalPerson.getIdNumber());
		assertEquals("Id not the same", physicalPerson.getId(), samePhysicalPerson.getId());
		physicalPerson.setDocumentType(DocumentType.LE);
		physicalPerson.setFirstName("Vitor");
		physicalPerson.setLastName("Gomez");
		physicalPerson.setIdNumber(3456789);
		physicalPersonDao.modifyPhysicalPerson(physicalPerson);
		samePhysicalPerson = physicalPersonDao.getPhysicalPersonByIdNumber(3456789, DocumentType.LE);
		assertEquals("First Name not the same", physicalPerson.getFirstName(), samePhysicalPerson.getFirstName());
		assertEquals("Last Name not the same", physicalPerson.getLastName(), samePhysicalPerson.getLastName());
		assertEquals("Document Type not the same", physicalPerson.getDocumentType(), samePhysicalPerson.getDocumentType());
		assertEquals("Id Number not the same", physicalPerson.getIdNumber(), samePhysicalPerson.getIdNumber());
		assertEquals("Id not the same", physicalPerson.getId(), samePhysicalPerson.getId());
		physicalPersonDao.deletePhysicalPerson(physicalPerson.getId());
		assertNull("Physical Person still exists", physicalPersonDao.getPhysicalPerson(physicalPerson.getId()));
	}

	public void setPhysicalPersonDao(PhysicalPersonDao physicalPersonDao) {
		this.physicalPersonDao = physicalPersonDao;
	}
}
