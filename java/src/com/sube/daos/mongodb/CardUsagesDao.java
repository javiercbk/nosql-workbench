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

import java.util.List;

import com.sube.beans.SubeCard;
import com.sube.beans.SubeCardUsage;
import com.sube.exceptions.card.InvalidSubeCardException;
import com.sube.exceptions.person.InvalidProviderException;

public interface CardUsagesDao {
	public void chargeService(SubeCardUsage cardUsage) throws InvalidSubeCardException, InvalidProviderException;
	public void refundService(SubeCardUsage cardUsage) throws InvalidSubeCardException, InvalidProviderException;
	public void chargeMoney(SubeCardUsage cardUsage) throws InvalidSubeCardException, InvalidProviderException;
	public void refundMoney(SubeCardUsage cardUsage) throws InvalidSubeCardException, InvalidProviderException;
	public List<SubeCardUsage> listLastUsage(SubeCard subeCard, int limit) throws InvalidSubeCardException;
	//only for testing purposes
	public void removeAll();
	public Integer count();
}
