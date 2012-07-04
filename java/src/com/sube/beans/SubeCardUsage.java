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
package com.sube.beans;

import java.util.Date;

import org.bson.types.ObjectId;

public class SubeCardUsage implements MongoBean{
	private Provider provider;
	private Double money;
	private SubeCard card;
	private Date datetime;
	private ObjectId mongoId;
	
	public Provider getPerformer() {
		return provider;
	}
	public void setPerformer(Provider performer) {
		this.provider = performer;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public SubeCard getCard() {
		return card;
	}
	public void setCard(SubeCard card) {
		this.card = card;
	}
	public Date getDatetime() {
		return datetime;
	}
	public void setDatetime(Date datetime) {
		this.datetime = datetime;
	}
	public ObjectId getMongoId() {
		return mongoId;
	}
	public void setMongoId(ObjectId mongoId) {
		this.mongoId = mongoId;
	}
	
}
