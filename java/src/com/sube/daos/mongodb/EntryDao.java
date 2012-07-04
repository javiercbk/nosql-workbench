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

import com.sube.beans.DataEntry;
import com.sube.exceptions.person.InvalidDataEntryException;

public interface EntryDao {
	public void createDataEntry(DataEntry dataEntry) throws InvalidDataEntryException;
	public void markDeleted(DataEntry dataEntry) throws InvalidDataEntryException;
	public void markActive(DataEntry dataEntry) throws InvalidDataEntryException;
	public void deleteDataEntry(DataEntry dataEntry);
}
