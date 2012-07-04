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
package com.sube.daos.mysql;

import com.sube.beans.DataEntry;
import com.sube.beans.SubeCard;
import com.sube.beans.User;
import com.sube.exceptions.card.DuplicatedSubeCardException;
import com.sube.exceptions.card.InvalidSubeCardException;

public interface SubeCardDao {
	public Long createCard(SubeCard subeCard, DataEntry dataEntry, User user) throws DuplicatedSubeCardException;
	public Long createCard(SubeCard subeCard, DataEntry dataEntry) throws DuplicatedSubeCardException;
	public Integer deleteCard(SubeCard subeCard) throws InvalidSubeCardException;
	public Double addToBalance(SubeCard subeCard, Double money) throws InvalidSubeCardException;
	public Double getBalance(SubeCard subeCard) throws InvalidSubeCardException;
	public SubeCard getSubeCard(Long id);
}
