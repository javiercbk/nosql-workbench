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

import com.sube.beans.LegalPerson;
import com.sube.daos.mysql.LegalPersonDao;
import com.sube.exceptions.person.InvalidLegalPersonException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/sube/resources/mysql/mysqlContext.xml","classpath:applicationContext.xml" })
public class LegalPersonDaoTest extends TestCase{
	@Autowired
	private LegalPersonDao legalPersonDao;
	
	@Test
	public void testLegalPersonDao() throws InvalidLegalPersonException{
		LegalPerson legalPerson = new LegalPerson(); 
		legalPerson.setCuit(123456l);
		legalPerson.setFantasyName("Sube Card Fantasy Name");
		legalPerson.setLegalLocation("Brick Avenue 123");
		legalPerson.setLegalName("Sube Cashier Provider SRL");
		legalPerson.setId(legalPersonDao.createLegalPerson(legalPerson));
		assertNotNull("Legal Person created", legalPerson.getId());
		LegalPerson sameLegalPerson = legalPersonDao.getLegalPerson(legalPerson.getId());
		assertEquals("Cuit is not the same", legalPerson.getCuit(), sameLegalPerson.getCuit());
		assertEquals("Fantasy Name is not the same", legalPerson.getFantasyName(), sameLegalPerson.getFantasyName());
		assertEquals("Legal Name is not the same", legalPerson.getLegalName(), sameLegalPerson.getLegalName());
		assertEquals("Legal Location is not the same", legalPerson.getLegalLocation(), sameLegalPerson.getLegalLocation());
		assertEquals("Id is not the same", legalPerson.getId(), sameLegalPerson.getId());
		legalPerson.setCuit(789l);
		legalPerson.setFantasyName("New Fantasy Name");
		legalPerson.setLegalLocation("Right Avenue 456");
		legalPerson.setLegalName("Other Provider SRL");
		legalPersonDao.modifyLegalPerson(legalPerson);
		sameLegalPerson = legalPersonDao.getLegalPerson(legalPerson.getId());
		assertEquals("Cuit is not the same", legalPerson.getCuit(), sameLegalPerson.getCuit());
		assertEquals("Fantasy Name is not the same", legalPerson.getFantasyName(), sameLegalPerson.getFantasyName());
		assertEquals("Legal Name is not the same", legalPerson.getLegalName(), sameLegalPerson.getLegalName());
		assertEquals("Legal Location is not the same", legalPerson.getLegalLocation(), sameLegalPerson.getLegalLocation());
		assertEquals("Id is not the same", legalPerson.getId(), sameLegalPerson.getId());
		List<LegalPerson> legalPersons = legalPersonDao.getLegalPersonByName("New");
		List<LegalPerson> sameLegalPersons = legalPersonDao.getLegalPersonByName("Other");
		assertEquals("None or more than one result", Integer.valueOf(1), Integer.valueOf(legalPersons.size()));
		assertEquals("None or more than one result", Integer.valueOf(1), Integer.valueOf(sameLegalPersons.size()));
		assertEquals("Id is not the same", legalPerson.getId(), legalPersons.get(0).getId());
		assertEquals("Id is not the same", legalPerson.getId(), sameLegalPersons.get(0).getId());
		Integer deleted = legalPersonDao.deleteLegalPerson(sameLegalPerson.getId());
		assertEquals("Deleted Rows is not the same", Integer.valueOf(1), deleted);
		LegalPerson nullLegalPerson = legalPersonDao.getLegalPerson(legalPerson.getId());
		assertNull("Deleted But then found", nullLegalPerson);
	}

	public void setLegalPersonDao(LegalPersonDao legalPersonDao) {
		this.legalPersonDao = legalPersonDao;
	}
}
