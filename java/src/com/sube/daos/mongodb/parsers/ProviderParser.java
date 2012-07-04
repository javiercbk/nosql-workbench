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
package com.sube.daos.mongodb.parsers;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.sube.beans.CashierProvider;
import com.sube.beans.LegalPerson;
import com.sube.beans.Provider;
import com.sube.beans.ProviderType;
import com.sube.beans.ServiceProvider;

public class ProviderParser implements DBObjectParser<Provider> {
	private DBObjectParser<LegalPerson> legalPersonParser;

	@Override
	public Provider parse(DBObject toParse) {
		Provider provider = null;
		ProviderType providerType = ProviderType.getByTypeName(toParse.get("providerType").toString());
		if(ProviderType.CashierProvider.equals(providerType)){
			provider = new CashierProvider();
		}else{
			provider = new ServiceProvider();
		}
		provider.setMongoId((ObjectId)toParse.get("_id"));
		provider.setLocation(toParse.get("location").toString());
		provider.setProviderName(toParse.get("providerName").toString());
		provider.setLegalPerson(legalPersonParser.parse((DBObject)toParse.get("legalPerson")));
		return provider;
	}

	public void setLegalPersonParser(DBObjectParser<LegalPerson> legalPersonParser) {
		this.legalPersonParser = legalPersonParser;
	}
}
