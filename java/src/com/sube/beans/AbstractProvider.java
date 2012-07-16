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

public abstract class AbstractProvider implements Provider{
	protected LegalPerson legalPerson;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getProviderName() == null) ? 0 : getProviderName().hashCode());
		result = prime * result
				+ ((legalPerson == null) ? 0 : legalPerson.hashCode());
		result = prime * result
				+ ((getProviderType() == null) ? 0 : getProviderType().hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractProvider other = (AbstractProvider) obj;
		if (getProviderName() == null) {
			if (other.getProviderName() != null)
				return false;
		} else if (!getProviderName().equals(other.getProviderName()))
			return false;
		if (legalPerson == null) {
			if (other.getLegalPerson() != null)
				return false;
		} else if (!legalPerson.equals(other.getLegalPerson()))
			return false;
		if (getProviderType() != other.getProviderType())
			return false;
		return true;
	}
	
	public LegalPerson getLegalPerson() {
		return legalPerson;
	}
	public void setLegalPerson(LegalPerson legalPerson) {
		this.legalPerson = legalPerson;
	}
}
