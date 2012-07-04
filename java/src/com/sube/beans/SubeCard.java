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

import org.bson.types.ObjectId;


public class SubeCard implements MongoBean{
	private Long number;
	private Double balance;
	private DataEntry createdBy;
	private User user;
	private ObjectId mongoId;
	
	public Long getNumber() {
		return number;
	}
	public void setNumber(Long number) {
		this.number = number;
	}
	public Double getBalance() {
		return balance;
	}
	public void setBalance(Double balance) {
		this.balance = balance;
	}
	public DataEntry getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(DataEntry createdBy) {
		this.createdBy = createdBy;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public ObjectId getMongoId() {
		return mongoId;
	}
	public void setMongoId(ObjectId mongoId) {
		this.mongoId = mongoId;
	}
}
