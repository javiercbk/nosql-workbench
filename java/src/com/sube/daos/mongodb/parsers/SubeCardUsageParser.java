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

import java.util.Date;

import org.bson.types.ObjectId;

import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.sube.beans.Provider;
import com.sube.beans.SubeCard;
import com.sube.beans.SubeCardUsage;

public class SubeCardUsageParser implements DBObjectParser<SubeCardUsage> {
	private DBObjectParser<SubeCard> subeCardParser;
	private DBObjectParser<Provider> providerParser;

	@Override
	public SubeCardUsage parse(DBObject toParse) {
		SubeCardUsage usage = new SubeCardUsage();
		usage.setMongoId((ObjectId)toParse.get("_id"));
		DBObject subeCardDBObject = ((DBRef) toParse.get("subeCard")).fetch();
		DBObject providerDBObject = ((DBRef) toParse.get("provider")).fetch();
		usage.setCard(subeCardParser.parse(subeCardDBObject));
		usage.setPerformer(providerParser.parse(providerDBObject));
		usage.setDatetime((Date) toParse.get("datetime"));
		usage.setMoney((Double) toParse.get("money"));
		return usage;
	}

	public void setSubeCardParser(DBObjectParser<SubeCard> subeCardParser) {
		this.subeCardParser = subeCardParser;
	}

	public void setProviderParser(DBObjectParser<Provider> providerParser) {
		this.providerParser = providerParser;
	}
}
