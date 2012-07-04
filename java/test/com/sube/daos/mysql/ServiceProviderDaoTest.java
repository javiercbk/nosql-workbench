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

import com.sube.beans.ServiceProvider;
import com.sube.beans.LegalPerson;
import com.sube.daos.mysql.LegalPersonDao;
import com.sube.daos.mysql.ServiceProviderDao;
import com.sube.exceptions.person.InvalidServiceProviderException;
import com.sube.exceptions.person.InvalidLegalPersonException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:com/sube/resources/mysql/mysqlContext.xml","classpath:applicationContext.xml" })
public class ServiceProviderDaoTest extends TestCase{
	@Autowired
	private ServiceProviderDao serviceProviderDao;
	@Autowired
	private LegalPersonDao legalPersonDao;
	
	@Test
	public void testServiceProviderDao() throws InvalidServiceProviderException, InvalidLegalPersonException{
		ServiceProvider serviceProvider = new ServiceProvider();
		serviceProvider.setProviderName("Beto");
		serviceProvider.setLocation("Lugano");
		LegalPerson legalPerson = new LegalPerson(); 
		legalPerson.setCuit(123456l);
		legalPerson.setFantasyName("Sube Card Fantasy Name");
		legalPerson.setLegalLocation("Brick Avenue 123");
		legalPerson.setLegalName("Sube Service Provider SRL");
		legalPerson.setId(legalPersonDao.createLegalPerson(legalPerson));
		serviceProvider.setLegalPerson(legalPerson);
		serviceProvider.setId(serviceProviderDao.createServiceProvider(serviceProvider));
		ServiceProvider sameServiceProvider = serviceProviderDao.getServiceProvider(serviceProvider.getId());
		assertEquals("Id not the same", serviceProvider.getId(), sameServiceProvider.getId());
		assertEquals("Location not the same", serviceProvider.getLocation(), sameServiceProvider.getLocation());
		assertEquals("Name not the same", serviceProvider.getProviderName(), sameServiceProvider.getProviderName());
		assertEquals("Legal Person not the same", serviceProvider.getLegalPerson(), sameServiceProvider.getLegalPerson());
		legalPerson.setCuit(456789l);
		legalPerson.setLegalLocation("Wolf Avenue 123");
		legalPerson.setLegalName("Very Legal SA");
		legalPersonDao.modifyLegalPerson(legalPerson);
		serviceProvider.setLegalPerson(legalPerson);
		serviceProvider.setProviderName("Carlo");
		serviceProvider.setLocation("Barracas");
		serviceProviderDao.modifyServiceProvider(serviceProvider);
		List<ServiceProvider> serviceProviders = serviceProviderDao.getServiceProviderByName("Car");
		assertEquals("None or more than one result", Integer.valueOf(1), Integer.valueOf(serviceProviders.size()));
		assertEquals("Id not the same", serviceProvider.getId(), serviceProviders.get(0).getId());
		assertEquals("Location not the same", serviceProvider.getLocation(), serviceProviders.get(0).getLocation());
		assertEquals("Name not the same", serviceProvider.getProviderName(), serviceProviders.get(0).getProviderName());
		assertEquals("Legal Person not the same", serviceProvider.getLegalPerson(), serviceProviders.get(0).getLegalPerson());
		serviceProviderDao.deleteServiceProvider(serviceProvider.getId());
		assertNull("Service provider still exists", serviceProviderDao.getServiceProvider(serviceProvider.getId()));
		legalPersonDao.deleteLegalPerson(legalPerson.getId());
	}
	public void setServiceProviderDao(ServiceProviderDao serviceProviderDao) {
		this.serviceProviderDao = serviceProviderDao;
	}
	public void setLegalPersonDao(LegalPersonDao legalPersonDao) {
		this.legalPersonDao = legalPersonDao;
	}
}
