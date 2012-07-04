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

public enum ProviderType {
	ServiceProvider(com.sube.beans.ServiceProvider.class, 1, "service"),
	CashierProvider(com.sube.beans.CashierProvider.class, 2, "cashier");
	
	public final Class<?> providerClass;
	public final int type;
	public final String typeName;
	
	ProviderType(Class<?> providerClass, int type, String typeName){
		this.providerClass = providerClass;
		this.type = type;
		this.typeName = typeName;
	}
	
	public static ProviderType getByType(int type){
		for(ProviderType providerType : values()){
			if(providerType.type == type){
				return providerType; 
			}
		}
		return null;
	}

	public static ProviderType getByTypeName(String typeName) {
		for(ProviderType providerType : values()){
			if(providerType.typeName.equals(typeName)){
				return providerType; 
			}
		}
		return null;
	}
}
