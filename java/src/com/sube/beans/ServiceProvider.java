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

public class ServiceProvider implements Provider{
	private String providerName;
	private String location;
	private Long id;
	private LegalPerson legalPerson;
	private ObjectId mongoId;
	private final ProviderType providerType = ProviderType.ServiceProvider;
	
	public String getProviderName() {
		return providerName;
	}
	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public LegalPerson getLegalPerson() {
		return legalPerson;
	}
	public void setLegalPerson(LegalPerson legalPerson) {
		this.legalPerson = legalPerson;
	}
	@Override
	public ProviderType getProviderType() {
		return providerType;
	}
	public ObjectId getMongoId() {
		return mongoId;
	}
	public void setMongoId(ObjectId mongoId) {
		this.mongoId = mongoId;
	}
}
