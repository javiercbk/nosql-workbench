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
import com.sube.beans.Provider;
import com.sube.beans.SubeCard;
import com.sube.beans.SubeCardUsage;

public class SubeCardUsageGenerator implements DBObjectGenerator<SubeCardUsage> {
	private DBRefGenerator<SubeCard> subeCardRefGenerator;
	private DBObjectGenerator<Provider> provideRefTypeGenerator;
	
	@Override
	public DBObject generate(SubeCardUsage toGenerate) {
		BasicDBObject subeCardUsageDBObject = new BasicDBObject();
		ObjectId id = new ObjectId();
		subeCardUsageDBObject.put("_id", id);
		subeCardUsageDBObject.put("subeCard", subeCardRefGenerator.generateDBRef(toGenerate.getCard()));
		subeCardUsageDBObject.put("provider", provideRefTypeGenerator.generate(toGenerate.getPerformer()));
		subeCardUsageDBObject.put("datetime", toGenerate.getDatetime());
		subeCardUsageDBObject.put("money", toGenerate.getMoney());
		return subeCardUsageDBObject;
	}

	public void setSubeCardRefGenerator(
			DBRefGenerator<SubeCard> subeCardRefGenerator) {
		this.subeCardRefGenerator = subeCardRefGenerator;
	}

	public void setProvideRefTypeGenerator(
			DBObjectGenerator<Provider> provideRefTypeGenerator) {
		this.provideRefTypeGenerator = provideRefTypeGenerator;
	}
	
	
}
