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
import com.mongodb.DBRef;
import com.sube.beans.CardStatus;
import com.sube.beans.DataEntry;
import com.sube.beans.SubeCard;
import com.sube.beans.User;

public class SubeCardParser implements DBObjectParser<SubeCard> {
	private DBObjectParser<User> userParser;
	private DBObjectParser<DataEntry> dataEntryParser;

	@Override
	public SubeCard parse(DBObject toParse) {
		SubeCard subeCard = new SubeCard();
		subeCard.setMongoId((ObjectId)toParse.get("_id"));
		subeCard.setBalance((Double) toParse.get("balance"));
		subeCard.setNumber((Long) toParse.get("number"));
		subeCard.setCreatedBy(dataEntryParser.parse(((DBRef)toParse.get("dataEntry")).fetch()));
		subeCard.setUser(userParser.parse((DBObject)toParse.get("user")));
		if(toParse.get("status") != null){
			subeCard.setStatus(CardStatus.getStatus(toParse.get("status").toString()));
		}
		subeCard.getUser().setSubeCard(subeCard);
		return subeCard;
	}

	public void setUserParser(DBObjectParser<User> userParser) {
		this.userParser = userParser;
	}

	public void setDataEntryParser(DBObjectParser<DataEntry> dataEntryParser) {
		this.dataEntryParser = dataEntryParser;
	}
}
