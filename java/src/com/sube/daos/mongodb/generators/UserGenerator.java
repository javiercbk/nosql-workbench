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
package com.sube.daos.mongodb.generators;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.sube.beans.User;

public class UserGenerator implements DBObjectGenerator<User> {

	@Override
	public DBObject generate(User toGenerate) {
		BasicDBObject userDBObject = new BasicDBObject();
		userDBObject.put("firstName", toGenerate.getPhysicalPerson().getFirstName());
		userDBObject.put("lastName", toGenerate.getPhysicalPerson().getLastName());
		userDBObject.put("docType", toGenerate.getPhysicalPerson().getDocumentType().type);
		userDBObject.put("docNum", toGenerate.getPhysicalPerson().getIdNumber());
		return userDBObject;
	}

}
