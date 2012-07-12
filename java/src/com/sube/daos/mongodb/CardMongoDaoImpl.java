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
import java.util.Iterator;
import java.util.List;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.sube.beans.CardStatus;
import com.sube.beans.MongoCollection;
import com.sube.beans.SubeCard;
import com.sube.daos.mongodb.generators.DBObjectGenerator;
import com.sube.daos.mongodb.parsers.DBObjectParser;
import com.sube.exceptions.card.InvalidSubeCardException;

public class CardMongoDaoImpl implements CardDao {
	private DB db;
	private SubeCardValidator subeCardValidator;
	private DBObjectGenerator<SubeCard> subeCardGenerator;
	private DBObjectParser<SubeCard> subeCardParser;

	@Override
	public void storeSubeCard(SubeCard subeCard)
			throws InvalidSubeCardException {
		if(!subeCardValidator.isValid(subeCard)){
			throw new InvalidSubeCardException();
		}
		DBCollection collection = getCardCollection();
		collection.ensureIndex(new BasicDBObject("number", 1), new BasicDBObject("unique", true));
		collection.ensureIndex(new BasicDBObject("user.docNum", 1));
		collection.ensureIndex(new BasicDBObject("user.docNum", 1).append("user.docType", 1), new BasicDBObject("unique", true));
		DBObject subeCardDBObject = subeCardGenerator.generate(subeCard);
		collection.insert(subeCardDBObject);
		subeCard.setMongoId((ObjectId) subeCardDBObject.get("_id"));
	}

	@Override
	public void markStolen(SubeCard subeCard) throws InvalidSubeCardException {
		markAs(subeCard, CardStatus.STOLEN.status);
	}

	@Override
	public void markDeleted(SubeCard subeCard) throws InvalidSubeCardException {
		markAs(subeCard, CardStatus.DELETED.status);
	}

	@Override
	public void markLost(SubeCard subeCard) throws InvalidSubeCardException {
		markAs(subeCard, CardStatus.LOST.status);
	}

	@Override
	public void markActive(SubeCard subeCard) throws InvalidSubeCardException {
		markAs(subeCard, CardStatus.ACTIVE.status);
	}

	@Override
	public Double addToBalance(SubeCard subeCard, Double money)
			throws InvalidSubeCardException {
		DBCollection collection = getCardCollection();
		BasicDBObject query = getCardQuery(subeCard);
		Double balance = getBalanceByQuery(query);
		Double newBalance = balance.doubleValue() + money;
		BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("balance", newBalance));
		collection.update(query, update);
		return newBalance;
	}

	@Override
	public Double getBalance(SubeCard subeCard) throws InvalidSubeCardException {
		return getBalanceByQuery(getCardQuery(subeCard));
	}
	
	@Override
	public List<SubeCard> getCards(SubeCard subeCard) throws InvalidSubeCardException {
		DBCollection collection = getCardCollection();
		DBCursor cursor = collection.find(getCardQuery(subeCard));
		Iterator<DBObject> iterator = cursor.iterator();
		List<SubeCard> cards = new ArrayList<SubeCard>();
		while(iterator.hasNext()){
			DBObject dbObject = iterator.next();
			cards.add(subeCardParser.parse(dbObject));
		}
		return cards;
	}
	
	@Override
	public void removeAll() {
		DBCollection collection = getCardCollection();
		collection.remove(new BasicDBObject());
	}
	
	private Double getBalanceByQuery(DBObject query){
		DBCollection collection = getCardCollection();
		DBObject subeCardDBObject = collection.findOne(query);
		return (Double) subeCardDBObject.get("balance");
	}
	
	private void markAs(SubeCard subeCard, String status){
		DBCollection collection = getCardCollection();
		BasicDBObject query = getCardQuery(subeCard);
		BasicDBObject update = new BasicDBObject("$set", new BasicDBObject("status", status));
		collection.findAndModify(query, update);
	}

	private BasicDBObject getCardQuery(SubeCard subeCard) {
		BasicDBObject query = new BasicDBObject();
		if(subeCard.getMongoId() == null){
			query.put("number", subeCard.getNumber());
		}else{
			query.put("_id", subeCard.getMongoId());
		}
		return query;
	}
	
	private DBCollection getCardCollection() {
		DBCollection collection = db.getCollection(MongoCollection.Cards.name);
		return collection;
	}

	public void setDb(DB db) {
		this.db = db;
	}

	public void setSubeCardGenerator(DBObjectGenerator<SubeCard> subeCardGenerator) {
		this.subeCardGenerator = subeCardGenerator;
	}

	public void setSubeCardValidator(SubeCardValidator subeCardValidator) {
		this.subeCardValidator = subeCardValidator;
	}

	public void setSubeCardParser(DBObjectParser<SubeCard> subeCardParser) {
		this.subeCardParser = subeCardParser;
	}
}
