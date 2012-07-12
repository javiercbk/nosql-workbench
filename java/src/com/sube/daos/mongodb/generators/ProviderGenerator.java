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
package com.sube.daos.mongodb.generators;

import com.mongodb.DBObject;
import com.sube.beans.CashierProvider;
import com.sube.beans.Provider;
import com.sube.beans.ProviderType;
import com.sube.beans.ServiceProvider;

public class ProviderGenerator implements DBObjectGenerator<Provider> {
	private DBObjectGenerator<CashierProvider> cashierProviderGenerator;
	private DBObjectGenerator<ServiceProvider> serviceProviderGenerator;
	
	@Override
	public DBObject generate(Provider toGenerate) {
		if(ProviderType.CashierProvider.equals(toGenerate.getProviderType())){
			return cashierProviderGenerator.generate((CashierProvider) toGenerate);
		}
		return serviceProviderGenerator.generate((ServiceProvider) toGenerate);
	}

	public void setCashierProviderGenerator(
			DBObjectGenerator<CashierProvider> cashierProviderGenerator) {
		this.cashierProviderGenerator = cashierProviderGenerator;
	}

	public void setServiceProviderGenerator(
			DBObjectGenerator<ServiceProvider> serviceProviderGenerator) {
		this.serviceProviderGenerator = serviceProviderGenerator;
	}
}
