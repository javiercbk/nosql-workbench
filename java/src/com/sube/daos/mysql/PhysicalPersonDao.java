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

import java.sql.SQLException;

import com.sube.beans.DocumentType;
import com.sube.beans.PhysicalPerson;
import com.sube.exceptions.person.InvalidPhysicalPersonException;

public interface PhysicalPersonDao {
	public Integer modifyPhysicalPerson(PhysicalPerson person) throws InvalidPhysicalPersonException;
	public Integer deletePhysicalPerson(Long id) throws InvalidPhysicalPersonException;
	public Long createPhysicalPerson(PhysicalPerson physicalPerson) throws InvalidPhysicalPersonException;
	public Integer getDocumentTypeId(DocumentType documentType)
			throws SQLException;
	public PhysicalPerson getPhysicalPerson(Long id);
	public PhysicalPerson getPhysicalPersonByIdNumber(Integer idNumber, DocumentType documentType);
}
