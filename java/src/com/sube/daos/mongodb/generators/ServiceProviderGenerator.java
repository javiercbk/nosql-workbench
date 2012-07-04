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

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sube.beans.LegalPerson;
import com.sube.beans.ServiceProvider;

public class ServiceProviderGenerator implements
		DBObjectGenerator<ServiceProvider> {
	private DBObjectGenerator<LegalPerson> legalPersonGenerator;

	@Override
	public DBObject generate(ServiceProvider toGenerate) {
		BasicDBObject serviceProviderDBObject = new BasicDBObject();
		serviceProviderDBObject.put("location", toGenerate.getLocation());
		serviceProviderDBObject.put("providerType", toGenerate.getProviderType().typeName);
		serviceProviderDBObject.put("providerName", toGenerate.getProviderName());
		serviceProviderDBObject.put("legalPerson", legalPersonGenerator.generate(toGenerate.getLegalPerson()));
		return serviceProviderDBObject;
	}

	public void setLegalPersonGenerator(
			DBObjectGenerator<LegalPerson> legalPersonGenerator) {
		this.legalPersonGenerator = legalPersonGenerator;
	}
}
