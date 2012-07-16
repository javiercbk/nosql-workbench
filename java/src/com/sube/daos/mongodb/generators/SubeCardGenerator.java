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
import com.sube.beans.DataEntry;
import com.sube.beans.SubeCard;
import com.sube.beans.User;

public class SubeCardGenerator implements DBObjectGenerator<SubeCard> {
	private DBRefGenerator<DataEntry> dataEntryRefGenerator;
	private DBObjectGenerator<User> userGenerator;

	@Override
	public DBObject generate(SubeCard toGenerate) {
		BasicDBObject subeCardDBObject = new BasicDBObject();
		//ObjectId id = new ObjectId();
		//subeCardDBObject.put("_id", id);
		subeCardDBObject.put("balance", toGenerate.getBalance());
		subeCardDBObject.put("number", toGenerate.getNumber());
		subeCardDBObject.put("user", userGenerator.generate(toGenerate.getUser()));
		subeCardDBObject.put("dataEntry", dataEntryRefGenerator.generateDBRef(toGenerate.getCreatedBy()));
		return subeCardDBObject;
	}

	public void setDataEntryRefGenerator(DBRefGenerator<DataEntry> dataEntryRefGenerator) {
		this.dataEntryRefGenerator = dataEntryRefGenerator;
	}

	public void setUserGenerator(DBObjectGenerator<User> userGenerator) {
		this.userGenerator = userGenerator;
	}
}
