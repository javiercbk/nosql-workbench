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
import com.sube.exceptions.card.InvalidSubeCardException;

public interface CardDao {
	public void storeSubeCard(SubeCard subeCard) throws InvalidSubeCardException;
	public void markStolen(SubeCard subeCard) throws InvalidSubeCardException;
	public void markDeleted(SubeCard subeCard) throws InvalidSubeCardException;
	public void markLost(SubeCard subeCard) throws InvalidSubeCardException;
	public void markActive(SubeCard subeCard) throws InvalidSubeCardException;
	public Double addToBalance(SubeCard subeCard, Double money) throws InvalidSubeCardException;
	public Double getBalance(SubeCard subeCard) throws InvalidSubeCardException;
	public List<SubeCard> getCards(SubeCard subeCard) throws InvalidSubeCardException;
	public void removeAll();
}
