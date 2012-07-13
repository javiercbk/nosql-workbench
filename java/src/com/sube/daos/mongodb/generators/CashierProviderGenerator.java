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

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sube.beans.CashierProvider;
import com.sube.beans.LegalPerson;

public class CashierProviderGenerator implements
		DBObjectGenerator<CashierProvider> {
	private DBObjectGenerator<LegalPerson> legalPersonGenerator;

	@Override
	public DBObject generate(CashierProvider toGenerate) {
		BasicDBObject cashierProviderDBObject = new BasicDBObject();
		cashierProviderDBObject.put("location", toGenerate.getLocation());
		cashierProviderDBObject.put("providerType", toGenerate.getProviderType().typeName);
		cashierProviderDBObject.put("providerName", toGenerate.getCashierName());
		cashierProviderDBObject.put("legalPerson", legalPersonGenerator.generate(toGenerate.getLegalPerson()));
		return cashierProviderDBObject;
	}

	public void setLegalPersonGenerator(
			DBObjectGenerator<LegalPerson> legalPersonGenerator) {
		this.legalPersonGenerator = legalPersonGenerator;
	}
}
