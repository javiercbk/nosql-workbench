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

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.mongodb.MapReduceCommand;
import com.mongodb.MapReduceCommand.OutputType;
import com.sube.beans.MongoCollection;
import com.sube.beans.Provider;
import com.sube.beans.ProviderType;
import com.sube.beans.SubeCard;
import com.sube.daos.mongodb.parsers.DBObjectParser;

public class StatisticDaoMongoImpl implements StatisticDao {
	private DB db;
	private DBObjectParser<SubeCard> subeCardParser;
	private DBObjectParser<Provider> providerParser;

	@Override
	public List<Entry<Date,Long>> getUsagesByDates(Date from, Date to) {
		StringBuffer map = new StringBuffer("function() {");
		map.append("emit(this.datetime,{count:1});");
		map.append("}");
		StringBuffer reduce = new StringBuffer("function(key, values) {");
		reduce.append("var result = 0");
		reduce.append("values.forEach(function(value) {");
		reduce.append("result += value.count;");
		reduce.append("});");
		reduce.append("return result;");
		reduce.append("}");
		DBObject query = new BasicDBObject("datetime", new BasicDBObject("$gte", from).append("$lte",to));
		MapReduceCommand mapReduce = new MapReduceCommand(
				getCardUsagesCollection(), map.toString(), reduce.toString(),
				MongoCollection.UsagesByDates.name, OutputType.REPLACE, query);
		getCardUsagesCollection().mapReduce(mapReduce);
		DBCursor dbCursor = getUsagesByDateCollection().find().sort(new BasicDBObject("_id", 1));
		return parseUsagesByDatesResult(dbCursor);
	}

	@Override
	public List<SubeCard> getMostTravelers(int limit) {
		StringBuffer map = new StringBuffer("function() {");
		map.append("emit(this.subeCard,{count:1});");
		map.append("}");
		StringBuffer reduce = new StringBuffer("function(key, values) {");
		reduce.append("var result = 0");
		reduce.append("values.forEach(function(value) {");
		reduce.append("result += value.count;");
		reduce.append("});");
		reduce.append("return result;");
		reduce.append("}");
		MapReduceCommand mapReduce = new MapReduceCommand(
				getCardUsagesCollection(), map.toString(), reduce.toString(),
				MongoCollection.MostTravelers.name, OutputType.REPLACE, null);
		getCardUsagesCollection().mapReduce(mapReduce);
		DBCursor dbCursor = getMostTravelerCollection().find().sort(new BasicDBObject("_id", 1)).limit(limit);
		return parseSubeCardResult(dbCursor);
	}

	@Override
	public List<SubeCard> getMostExpenders(int limit) {
		StringBuffer map = new StringBuffer("function() {");
		map.append("emit(this.subeCard,{money:this.money});");
		map.append("}");
		StringBuffer reduce = new StringBuffer("function(key, values) {");
		reduce.append("var result = 0");
		reduce.append("values.forEach(function(value) {");
		reduce.append("result += value.money;");
		reduce.append("});");
		reduce.append("return result;");
		reduce.append("}");
		MapReduceCommand mapReduce = new MapReduceCommand(
				getCardUsagesCollection(), map.toString(), reduce.toString(),
				MongoCollection.MostExpenders.name, OutputType.REPLACE, null);
		getCardUsagesCollection().mapReduce(mapReduce);
		DBCursor dbCursor = getMostExpendersCollection().find().sort(new BasicDBObject("value", -1)).limit(limit);
		return parseSubeCardResult(dbCursor);
	}

	@Override
	public List<Provider> getMostProfitable(int limit) {
		StringBuffer map = new StringBuffer("function() {");
		map.append("emit(this.provider.ref,{money:this.money});");
		map.append("}");
		StringBuffer reduce = new StringBuffer("function(key, values) {");
		reduce.append("var result = 0");
		reduce.append("values.forEach(function(value) {");
		reduce.append("result += value.money;");
		reduce.append("});");
		reduce.append("return result;");
		reduce.append("}");
		BasicDBObject cashierPositive = new BasicDBObject("this.provider.type", ProviderType.CashierProvider.name());
		cashierPositive.append("money", new BasicDBObject("$gt", 0));
		DBObject andCashierPositive = new BasicDBObject("$and", cashierPositive);
		BasicDBObject serviceNegative = new BasicDBObject("this.provider.type", ProviderType.ServiceProvider.name());
		serviceNegative.append("money", new BasicDBObject("$lt", 0));
		DBObject andServiceNegative = new BasicDBObject("$and", serviceNegative);
		DBObject query = new BasicDBObject().append("$or", andCashierPositive).append("$or", andServiceNegative);
		MapReduceCommand mapReduce = new MapReduceCommand(
				getCardUsagesCollection(), map.toString(), reduce.toString(),
				MongoCollection.MostExpenders.name, OutputType.REPLACE, query);
		getCardUsagesCollection().mapReduce(mapReduce);
		DBCursor dbCursor = getMostProfitableCollection().find().sort(new BasicDBObject("value", -1)).limit(limit);
		return parseProviderResult(dbCursor);
	}

	@Override
	public List<Provider> getMoreErrorProne(int limit) {
		StringBuffer map = new StringBuffer("function() {");
		map.append("emit(this.provider.ref,{money:this.money});");
		map.append("}");
		StringBuffer reduce = new StringBuffer("function(key, values) {");
		reduce.append("var result = 0");
		reduce.append("values.forEach(function(value) {");
		reduce.append("result += value.money;");
		reduce.append("});");
		reduce.append("return result;");
		reduce.append("}");
		BasicDBObject cashierPositive = new BasicDBObject("this.provider.type", ProviderType.CashierProvider.name());
		cashierPositive.append("money", new BasicDBObject("$lt", 0));
		DBObject andCashierPositive = new BasicDBObject("$and", cashierPositive);
		BasicDBObject serviceNegative = new BasicDBObject("this.provider.type", ProviderType.ServiceProvider.name());
		serviceNegative.append("money", new BasicDBObject("$gt", 0));
		DBObject andServiceNegative = new BasicDBObject("$and", serviceNegative);
		DBObject query = new BasicDBObject().append("$or", andCashierPositive).append("$or", andServiceNegative);
		MapReduceCommand mapReduce = new MapReduceCommand(
				getCardUsagesCollection(), map.toString(), reduce.toString(),
				MongoCollection.MostExpenders.name, OutputType.REPLACE, query);
		getCardUsagesCollection().mapReduce(mapReduce);
		DBCursor dbCursor = getMoreErrorProneCollection().find().sort(new BasicDBObject("value", -1)).limit(limit);
		return parseProviderResult(dbCursor);
	}
	
	private List<Entry<Date,Long>> parseUsagesByDatesResult(DBCursor dbCursor) {
		List<Entry<Date,Long>> usagesByDates = new ArrayList<Entry<Date,Long>>();
		while(dbCursor.hasNext()){
			DBObject next = dbCursor.next();
			Date date = (Date) next.get("_id");
			Long usages = (Long) next.get("value");
			usagesByDates.add(new SimpleEntry<Date,Long>(date, usages));
		}
		return usagesByDates;
	}
	
	private List<SubeCard> parseSubeCardResult(DBCursor dbCursor) {
		List<SubeCard> subeCardResults = new ArrayList<SubeCard>(); 
		while(dbCursor.hasNext()){
			DBObject next = dbCursor.next();
			ObjectId subeCardId = (ObjectId) next.get("_id");
			DBRef subeCardRef = new DBRef(db, new BasicDBObject("$ref",MongoCollection.Cards.name).append("$id", subeCardId));
			subeCardResults.add(subeCardParser.parse(subeCardRef.fetch()));
		}
		return subeCardResults;
	}
	
	private List<Provider> parseProviderResult(DBCursor dbCursor) {
		List<Provider> providersResults = new ArrayList<Provider>(); 
		while(dbCursor.hasNext()){
			DBObject next = dbCursor.next();
			ObjectId providerId = (ObjectId) next.get("_id");
			DBRef providerRef = new DBRef(db, new BasicDBObject("$ref",MongoCollection.Providers.name).append("$id", providerId));
			providersResults.add(providerParser.parse(providerRef.fetch()));
		}
		return providersResults;
	}
	
	private DBCollection getCardUsagesCollection() {
		return db.getCollection(MongoCollection.CardUsages.name);
	}
	
	private DBCollection getUsagesByDateCollection() {
		return db.getCollection(MongoCollection.UsagesByDates.name);
	}
	
	private DBCollection getMostTravelerCollection() {
		return db.getCollection(MongoCollection.MostTravelers.name);
	}
	
	private DBCollection getMostExpendersCollection() {
		return db.getCollection(MongoCollection.MostExpenders.name);
	}
	
	private DBCollection getMostProfitableCollection() {
		return db.getCollection(MongoCollection.MostProfitable.name);
	}
	
	private DBCollection getMoreErrorProneCollection() {
		return db.getCollection(MongoCollection.MoreErrorProne.name);
	}

	public void setDb(DB db) {
		this.db = db;
	}
	
	public void setSubeCardParser(DBObjectParser<SubeCard> subeCardParser) {
		this.subeCardParser = subeCardParser;
	}
	
	public void setProviderParser(DBObjectParser<Provider> providerParser) {
		this.providerParser = providerParser;
	}
}
