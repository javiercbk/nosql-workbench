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
package com.sube.daos.mongodb;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.sube.beans.MongoCollection;
import com.sube.beans.Provider;
import com.sube.beans.ProviderStatus;
import com.sube.daos.mongodb.generators.DBObjectGenerator;
import com.sube.daos.mongodb.parsers.DBObjectParser;
import com.sube.exceptions.person.InvalidProviderException;

public class GenericProviderDaoImpl implements ProviderDao {
	private DB db;
	private DBObjectGenerator<Provider> providerGenerator;
	private DBObjectParser<Provider> providerParser;

	@Override
	public void registerProvider(Provider provider)
			throws InvalidProviderException {
		DBCollection collection = getProvidersCollection();
		collection.ensureIndex(new BasicDBObject("providerName", 1));
		collection.ensureIndex(new BasicDBObject("legalPerson.legalName", 1), new BasicDBObject("unique", true));
		collection.ensureIndex(new BasicDBObject("legalPerson.cuit", 1), new BasicDBObject("unique", true));
		collection.ensureIndex(new BasicDBObject("legalPerson.fantasyName", 1));
		collection.insert(providerGenerator.generate(provider));
	}

	@Override
	public void markInactive(Provider provider) throws InvalidProviderException {
		markAs(provider, ProviderStatus.Inactive.status);
	}

	@Override
	public void markActive(Provider provider) throws InvalidProviderException {
		markAs(provider, ProviderStatus.Active.status);
	}

	@Override
	public List<Provider> getProviderByName(String name, int limit) {
		List<Provider> providers = new ArrayList<Provider>();
		DBCollection collection = getProvidersCollection();
		BasicDBObject query = new BasicDBObject();
		BasicDBObject legalPerson = new BasicDBObject();
		legalPerson.put("fantasyName", name);
		legalPerson.put("legalName", name);
		query.put("providerName", name);
		DBCursor cursor = collection.find(query);
		int i = 0;
		while(cursor.hasNext() && i < limit){
			providers.add(providerParser.parse(cursor.next()));
			i++;
		}
		return providers;
	}
	
	private void markAs(Provider provider, String status){
		DBCollection collection = getProvidersCollection();
		BasicDBObject query = getProviderQuery(provider);
		BasicDBObject update = new BasicDBObject();
		update.put("status", status);
		collection.findAndModify(query, update);
	}
	
	private DBCollection getProvidersCollection(){
		return db.getCollection(MongoCollection.Providers.name);
	}
	
	private BasicDBObject getProviderQuery(Provider provider) {
		BasicDBObject query = new BasicDBObject();
		if(provider.getMongoId() == null){
			BasicDBObject legalPerson = new BasicDBObject();
			legalPerson.put("cuit", provider.getLegalPerson().getCuit());
			query.put("providerType", provider.getProviderType().typeName);
			query.put("legalPerson", legalPerson);
		}else{
			query.put("_id", provider.getMongoId());
		}
		return query;
	}

	public void setDb(DB db) {
		this.db = db;
	}

	public void setProviderGenerator(
			DBObjectGenerator<Provider> providerGenerator) {
		this.providerGenerator = providerGenerator;
	}

	public void setProviderParser(DBObjectParser<Provider> providerParser) {
		this.providerParser = providerParser;
	}
}
