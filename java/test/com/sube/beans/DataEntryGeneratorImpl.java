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

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import com.sube.utils.PasswordEncoder;

public class DataEntryGeneratorImpl implements DataEntryTestDataGenerator {
	private PasswordEncoder passwordEncoder;

	@Override
	public DataEntry generateDataEntry(PhysicalPerson physicalPerson) {
		DataEntry dataEntry = new DataEntry();
		dataEntry.setPhysicalPerson(physicalPerson);
		try {
			dataEntry.setPassword(passwordEncoder.encodePassword(UUID.randomUUID().toString()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return dataEntry;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
}
