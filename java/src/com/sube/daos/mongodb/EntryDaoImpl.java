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

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.sube.beans.DataEntry;
import com.sube.beans.MongoCollection;
import com.sube.beans.PhysicalPersonStatus;
import com.sube.daos.mongodb.generators.DBObjectGenerator;
import com.sube.exceptions.person.InvalidDataEntryException;

public class EntryDaoImpl implements EntryDao{
	private DB db;
	private DBObjectGenerator<DataEntry> dataEntryGenerator;

	@Override
	public void createDataEntry(DataEntry dataEntry)
			throws InvalidDataEntryException {
		DBCollection collection = getCollection();
		DBObject dataEntryDBObject = dataEntryGenerator.generate(dataEntry);
		collection.ensureIndex(new BasicDBObject("docNum", 1));
		collection.ensureIndex(new BasicDBObject("docNum", 1).append("docType", 1), new BasicDBObject("unique", true));
		collection.insert(dataEntryDBObject);
	}

	@Override
	public void markDeleted(DataEntry dataEntry)
			throws InvalidDataEntryException {
		markAs(dataEntry, PhysicalPersonStatus.Inactive.status);
	}

	@Override
	public void markActive(DataEntry dataEntry)
			throws InvalidDataEntryException {
		markAs(dataEntry, PhysicalPersonStatus.Active.status);
	}
	
	private void markAs(DataEntry dataEntry, String status) {
		DBObject query = getQuery(dataEntry);
		DBCollection collection = getCollection();
		DBObject update = dataEntryGenerator.generate(dataEntry);
		update.put("status", status);
		collection.findAndModify(query, update);
	}
	
	private DBObject getQuery(DataEntry dataEntry){
		BasicDBObject query = new BasicDBObject();
		if(dataEntry.getMongoId() == null){
			query.put("docNum", dataEntry.getPhysicalPerson().getIdNumber());
			query.put("docType", dataEntry.getPhysicalPerson().getDocumentType().type);
		}else{
			query.put("_id", dataEntry.getMongoId());
		}
		return query;
	}
	
	private DBCollection getCollection(){
		return db.getCollection(MongoCollection.DataEntries.name);
	}
	
	public void setDb(DB db) {
		this.db = db;
	}

	@Override
	public void deleteDataEntry(DataEntry dataEntry) {
		getCollection().find(getQuery(dataEntry)).remove();
	}
}
