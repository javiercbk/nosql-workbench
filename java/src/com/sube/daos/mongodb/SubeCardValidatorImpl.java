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

import com.sube.beans.SubeCard;

public class SubeCardValidatorImpl implements SubeCardValidator {

	@Override
	public boolean isValid(SubeCard subeCard) {
		//Data Entry and User should have a better validation
		if (subeCard.getBalance() < 0 || subeCard.getCreatedBy() == null
				|| subeCard.getNumber() < 0 || subeCard.getUser() == null) {
			return false;
		}
		return true;
	}

}
