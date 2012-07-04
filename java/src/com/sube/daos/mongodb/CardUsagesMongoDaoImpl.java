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
import com.mongodb.DBObject;
import com.sube.beans.MongoCollection;
import com.sube.beans.ProviderType;
import com.sube.beans.SubeCard;
import com.sube.beans.SubeCardUsage;
import com.sube.daos.mongodb.generators.DBObjectGenerator;
import com.sube.daos.mongodb.generators.DBRefGenerator;
import com.sube.daos.mongodb.parsers.DBObjectParser;
import com.sube.exceptions.card.InvalidSubeCardException;
import com.sube.exceptions.person.InvalidProviderException;

public class CardUsagesMongoDaoImpl implements CardUsagesDao{
	private DB db;
	private DBObjectGenerator<SubeCardUsage> subeCardUsageGenerator;
	private DBRefGenerator<SubeCard> subeCardRefGenerator;
	private DBObjectParser<SubeCardUsage> subeCardUsageParser;
	private CardDao cardDao;

	@Override
	public void chargeService(SubeCardUsage cardUsage) throws InvalidSubeCardException, InvalidProviderException {
		if(!ProviderType.ServiceProvider.equals(cardUsage.getPerformer().getProviderType())){
			throw new InvalidProviderException("Expected Service Provider");
		}
		if(cardUsage.getCard().getNumber() == null || cardUsage.getCard().getUser() == null){
			throw new InvalidSubeCardException();
		}
		genericUsageInsertion(cardUsage);
	}

	@Override
	public void refundService(SubeCardUsage cardUsage) throws InvalidSubeCardException, InvalidProviderException {
		if(!ProviderType.ServiceProvider.equals(cardUsage.getPerformer().getProviderType())){
			throw new InvalidProviderException("Expected Service Provider");
		}
		if(cardUsage.getCard().getNumber() == null || cardUsage.getCard().getUser() == null){
			throw new InvalidSubeCardException();
		}
		genericUsageInsertion(cardUsage);
	}

	@Override
	public void chargeMoney(SubeCardUsage cardUsage) throws InvalidSubeCardException, InvalidProviderException {
		if(!ProviderType.CashierProvider.equals(cardUsage.getPerformer().getProviderType())){
			throw new InvalidProviderException("Expected Cashier Provider");
		}
		if(cardUsage.getCard().getNumber() == null || cardUsage.getCard().getUser() == null){
			throw new InvalidSubeCardException();
		}
		genericUsageInsertion(cardUsage);
	}

	@Override
	public void refundMoney(SubeCardUsage cardUsage) throws InvalidSubeCardException, InvalidProviderException {
		if(!ProviderType.CashierProvider.equals(cardUsage.getPerformer().getProviderType())){
			throw new InvalidProviderException("Expected Cashier Provider");
		}
		if(cardUsage.getCard().getNumber() == null || cardUsage.getCard().getUser() == null){
			throw new InvalidSubeCardException();
		}
		genericUsageInsertion(cardUsage);
	}

	@Override
	public List<SubeCardUsage> listLastUsage(SubeCard subeCard, int limit)
			throws InvalidSubeCardException {
		DBCollection cardUsagesCollection = getCardUsagesCollection();
		BasicDBObject query = new BasicDBObject();
		query.put("subeCard", subeCardRefGenerator.generateDBRef(subeCard));
		DBCursor dbCursor = cardUsagesCollection.find(query);
		int i = 0;
		List<SubeCardUsage> subeCardUsages = new ArrayList<SubeCardUsage>();
		while(dbCursor.hasNext() && i < limit){
			DBObject next = dbCursor.next();
			subeCardUsages.add(subeCardUsageParser.parse(next));
			i++;
		}
		return subeCardUsages;
	}

	private void genericUsageInsertion(SubeCardUsage cardUsage) throws InvalidSubeCardException {
		DBCollection cardUsagesCollection = getCardUsagesCollection();
		cardUsagesCollection.ensureIndex(new BasicDBObject("subeCard", 1));
		cardUsagesCollection.ensureIndex(new BasicDBObject("provider", 1));
		cardUsagesCollection.ensureIndex(new BasicDBObject("datetime", 1));
		cardUsagesCollection.ensureIndex(new BasicDBObject("money", 1));
		DBObject subeCardUsageDBObject = subeCardUsageGenerator.generate(cardUsage);
		cardDao.addToBalance(cardUsage.getCard(), cardUsage.getMoney());
		cardUsagesCollection.insert(subeCardUsageDBObject);
	}
	
	private DBCollection getCardUsagesCollection() {
		DBCollection collection = db.getCollection(MongoCollection.CardUsages.name);
		return collection;
	}

	public void setDb(DB db) {
		this.db = db;
	}

	public void setSubeCardUsageGenerator(DBObjectGenerator<SubeCardUsage> subeCardUsageGenerator) {
		this.subeCardUsageGenerator = subeCardUsageGenerator;
	}

	public void setSubeCardRefGenerator(DBRefGenerator<SubeCard> subeCardRefGenerator) {
		this.subeCardRefGenerator = subeCardRefGenerator;
	}

	public void setSubeCardUsageParser(
			DBObjectParser<SubeCardUsage> subeCardUsageParser) {
		this.subeCardUsageParser = subeCardUsageParser;
	}

	public void setCardDao(CardDao cardDao) {
		this.cardDao = cardDao;
	}
}
