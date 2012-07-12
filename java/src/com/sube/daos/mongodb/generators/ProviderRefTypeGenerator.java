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
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.sube.beans.Provider;

public class ProviderRefTypeGenerator implements DBObjectGenerator<Provider>{
	private DB db;
	private String collection;

	@Override
	public DBObject generate(Provider toGenerate) {
		DBCollection providers = db.getCollection(collection);
		//FIXME poner un generador
		DBRef dbRef = new DBRef(db, providers.findOne(toGenerate));
		DBObject dbObject = new BasicDBObject("type", toGenerate.getProviderType().typeName).append("ref", dbRef);
		return dbObject;
	}

	public void setDb(DB db) {
		this.db = db;
	}

	public void setCollection(String collection) {
		this.collection = collection;
	}
}
