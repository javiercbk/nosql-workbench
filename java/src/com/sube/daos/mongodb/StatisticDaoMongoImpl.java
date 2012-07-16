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

import java.io.IOException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

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
import com.sube.daos.mongodb.mapreduce.MapReduce;
import com.sube.daos.mongodb.mapreduce.MapReduceGenerator;
import com.sube.daos.mongodb.parsers.DBObjectParser;

public class StatisticDaoMongoImpl implements StatisticDao {
	private DB db;
	private DBObjectParser<SubeCard> subeCardParser;
	private DBObjectParser<Provider> providerParser;
	private MapReduceGenerator usagesByDatesMapReduce;
	private MapReduceGenerator mostTravelersMapReduce;
	private MapReduceGenerator mostExpendersMapReduce;
	private MapReduceGenerator mostProfitableMapReduce;
	private MapReduceGenerator errorProneMapReduce;


	@Override
	public List<Entry<Date,Long>> getUsagesByDates(Date from, Date to) {
		MapReduce mapReduce = null;
		try {
			mapReduce = usagesByDatesMapReduce.generateMapReduce();
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<Entry<Date,Long>>();
		}
		DBObject query = new BasicDBObject("datetime", new BasicDBObject("$gte", from).append("$lte",to));
		executeMapReduce(mapReduce, query, MongoCollection.UsagesByDates);
		DBCursor dbCursor = getUsagesByDateCollection().find().sort(new BasicDBObject("_id", 1));
		return parseUsagesByDatesResult(dbCursor);
	}

	@Override
	public List<Entry<SubeCard, Long>> getMostTravelers(int limit) {
		MapReduce mapReduce = null;
		try {
			mapReduce = mostTravelersMapReduce.generateMapReduce();
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<Entry<SubeCard, Long>>();
		}
		DBObject[] andServiceNegativeArray = {new BasicDBObject("provider.type", ProviderType.ServiceProvider.name()), new BasicDBObject("money", new BasicDBObject("$lt", 0))}; 
		DBObject query = new BasicDBObject("$and", andServiceNegativeArray);
		executeMapReduce(mapReduce, query, MongoCollection.MostTravelers);
		DBCursor dbCursor = getMostTravelerCollection().find().sort(new BasicDBObject("value", -1)).limit(limit);
		return parseSubeCardCount(dbCursor);
	}

	@Override
	public List<Entry<SubeCard, Double>> getMostExpenders(int limit) {
		MapReduce mapReduce = null;
		try {
			mapReduce = mostExpendersMapReduce.generateMapReduce();
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<Entry<SubeCard, Double>>();
		}
		executeMapReduce(mapReduce, null, MongoCollection.MostExpenders);
		DBCursor dbCursor = getMostExpendersCollection().find().sort(new BasicDBObject("value", -1)).limit(limit);
		return parseSubeCardMoney(dbCursor);
	}

	@Override
	public List<Entry<Provider, Double>> getMostProfitable(int limit) {
		MapReduce mapReduce = null;
		try {
			mapReduce = mostProfitableMapReduce.generateMapReduce();
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<Entry<Provider, Double>>();
		}
		DBObject[] andCashierPositiveArray = {new BasicDBObject("provider.type", ProviderType.CashierProvider.name()), new BasicDBObject("money", new BasicDBObject("$gt", 0))};
		DBObject andCashierPositive = new BasicDBObject("$and", andCashierPositiveArray);
		DBObject[] andServiceNegativeArray = {new BasicDBObject("provider.type", ProviderType.ServiceProvider.name()), new BasicDBObject("money", new BasicDBObject("$lt", 0))}; 
		DBObject andServiceNegative = new BasicDBObject("$and", andServiceNegativeArray);
		DBObject[] orQuery = {andCashierPositive, andServiceNegative};
		DBObject query = new BasicDBObject("$or", orQuery);
		executeMapReduce(mapReduce, query, MongoCollection.MostProfitable);
		DBCursor dbCursor = getMostProfitableCollection().find().sort(new BasicDBObject("value", -1)).limit(limit);
		return parseProviderResult(dbCursor);
	}

	@Override
	public List<Entry<Provider, Double>> getMoreErrorProne(int limit) {
		MapReduce mapReduce = null;
		try {
			mapReduce = errorProneMapReduce.generateMapReduce();
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<Entry<Provider, Double>>();
		}
		DBObject[] andCashierNegativeArray = {new BasicDBObject("provider.type", ProviderType.CashierProvider.name()), new BasicDBObject("money", new BasicDBObject("$lt", 0))};
		DBObject andCashierNegative = new BasicDBObject("$and", andCashierNegativeArray);
		DBObject[] andServicePositiveArray = {new BasicDBObject("provider.type", ProviderType.ServiceProvider.name()), new BasicDBObject("money", new BasicDBObject("$gt", 0))}; 
		DBObject andServicePositive = new BasicDBObject("$and", andServicePositiveArray);
		DBObject[] orQuery = {andCashierNegative, andServicePositive};
		DBObject query = new BasicDBObject("$or", orQuery);
		executeMapReduce(mapReduce, query, MongoCollection.MoreErrorProne);
		DBCursor dbCursor = getMoreErrorProneCollection().find().sort(new BasicDBObject("value", -1)).limit(limit);
		return parseProviderResult(dbCursor);
	}
	
	private void executeMapReduce(MapReduce mapReduce, DBObject query, MongoCollection mongoCollection){
		MapReduceCommand mapReduceCommand = new MapReduceCommand(
				getCardUsagesCollection(), mapReduce.getMapExpression(), mapReduce.getReduceExpression(),
				mongoCollection.name, OutputType.REPLACE, query);
		getCardUsagesCollection().mapReduce(mapReduceCommand);
	}
	
	private List<Entry<Date,Long>> parseUsagesByDatesResult(DBCursor dbCursor) {
		List<Entry<Date,Long>> usagesByDates = new ArrayList<Entry<Date,Long>>();
		while(dbCursor.hasNext()){
			DBObject next = dbCursor.next();
			Date date = (Date) next.get("_id");
			Long usages = ((Double) next.get("value")).longValue();
			usagesByDates.add(new SimpleEntry<Date,Long>(date, usages));
		}
		return usagesByDates;
	}
	
	private List<Entry<SubeCard, Long>> parseSubeCardCount(DBCursor dbCursor) {
		List<Entry<SubeCard, Long>> subeCardResults = new ArrayList<Entry<SubeCard, Long>>(); 
		while(dbCursor.hasNext()){
			DBObject next = dbCursor.next();
			DBRef subeCardRef= (DBRef) (next.get("_id"));
			Long usages = ((Double) next.get("value")).longValue();
			subeCardResults.add(new SimpleEntry<SubeCard,Long>(subeCardParser.parse(subeCardRef.fetch()), usages));
		}
		return subeCardResults;
	}
	
	private List<Entry<SubeCard, Double>> parseSubeCardMoney(DBCursor dbCursor) {
		List<Entry<SubeCard, Double>> subeCardResults = new ArrayList<Entry<SubeCard, Double>>(); 
		while(dbCursor.hasNext()){
			DBObject next = dbCursor.next();
			DBRef subeCardRef= (DBRef) (next.get("_id"));
			Double usages = (Double) next.get("value");
			subeCardResults.add(new SimpleEntry<SubeCard,Double>(subeCardParser.parse(subeCardRef.fetch()), usages));
		}
		return subeCardResults;
	}
	
	private List<Entry<Provider, Double>> parseProviderResult(DBCursor dbCursor) {
		List<Entry<Provider, Double>> providersResults = new ArrayList<Entry<Provider, Double>>(); 
		while(dbCursor.hasNext()){
			DBObject next = dbCursor.next();
			DBRef providerRef= (DBRef) (next.get("_id"));
			Double usages = (Double) next.get("value");
			providersResults.add(new SimpleEntry<Provider,Double>(providerParser.parse(providerRef.fetch()),usages));
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

	public void setUsagesByDatesMapReduce(MapReduceGenerator usagesByDatesMapReduce) {
		this.usagesByDatesMapReduce = usagesByDatesMapReduce;
	}

	public void setMostTravelersMapReduce(MapReduceGenerator mostTravelersMapReduce) {
		this.mostTravelersMapReduce = mostTravelersMapReduce;
	}

	public void setMostExpendersMapReduce(MapReduceGenerator mostExpendersMapReduce) {
		this.mostExpendersMapReduce = mostExpendersMapReduce;
	}

	public void setMostProfitableMapReduce(
			MapReduceGenerator mostProfitableMapReduce) {
		this.mostProfitableMapReduce = mostProfitableMapReduce;
	}

	public void setErrorProneMapReduce(MapReduceGenerator errorProneMapReduce) {
		this.errorProneMapReduce = errorProneMapReduce;
	}
}
