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
package com.sube.beans;

import java.util.UUID;

public class ProviderGeneratorImpl implements ProviderTestDataGenerator {

	@Override
	public Provider generateProvider(ProviderType type, LegalPerson legalPerson) {
		Provider provider = null;
		if(type.equals(ProviderType.CashierProvider)){
			CashierProvider cashierProvider = new CashierProvider();
			cashierProvider.setLegalPerson(legalPerson);
			cashierProvider.setLocation(UUID.randomUUID().toString());
			cashierProvider.setProviderName(UUID.randomUUID().toString());
			cashierProvider.setCashierName(UUID.randomUUID().toString());
			provider = cashierProvider;
		}else{
			ServiceProvider serviceProvider = new ServiceProvider();
			serviceProvider.setLegalPerson(legalPerson);
			serviceProvider.setLocation(UUID.randomUUID().toString());
			serviceProvider.setProviderName(UUID.randomUUID().toString());
			provider = serviceProvider;
		}
		return provider;
	}
}
