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
package com.sube.daos.mongodb.parsers;

import com.mongodb.DBObject;
import com.sube.beans.DocumentType;
import com.sube.beans.PhysicalPerson;
import com.sube.beans.User;

public class UserParser implements DBObjectParser<User> {

	@Override
	public User parse(DBObject toParse) {
		User user = new User();
		PhysicalPerson physicalPerson = new PhysicalPerson();
		physicalPerson.setDocumentType(DocumentType.getTypeByName(toParse.get("docType").toString()));
		physicalPerson.setFirstName(toParse.get("firstName").toString());
		physicalPerson.setLastName(toParse.get("lastName").toString());
		physicalPerson.setIdNumber((Integer)toParse.get("docNum"));
		user.setPhysicalPerson(physicalPerson);
		return user;
	}

}
