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
package com.sube.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncoder {
	private static PasswordEncoder instance;
	
	private PasswordEncoder(){
		
	}
	
	public static PasswordEncoder getInstance(){
		if(instance == null){
			instance = new PasswordEncoder();
		}
		return instance;
	}
	
	public String encodePassword(String password) throws NoSuchAlgorithmException{
		MessageDigest mdEnc = MessageDigest.getInstance("MD5");
		mdEnc.update(password.getBytes(), 0, password.length());
		String md5 = new BigInteger(1, mdEnc.digest()).toString(16); 
		return md5;
	}
}
