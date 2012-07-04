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
package com.sube.actions;

import com.opensymphony.xwork2.ActionSupport;
import com.sube.beans.SubeCard;
import com.sube.beans.User;
import com.sube.daos.mysql.UserDao;
import com.sube.exceptions.card.InvalidSubeCardException;
import com.sube.exceptions.person.InvalidUserException;

public class UserAction implements FailureAwareAction{
	private UserDao userDao;
	private User user;
	private SubeCard subeCard;
	private String reason;
	
	public String createUser(){
		try {
			userDao.createUser(user, subeCard);
		} catch (InvalidSubeCardException e) {
			//Here I could assign a I18n text
			reason = e.getMessage();
			return ActionSupport.ERROR;
		} catch (InvalidUserException e) {
			//Here I could assign a I18n text
			reason = e.getMessage();
			return ActionSupport.ERROR;
		}
		return ActionSupport.SUCCESS;
	}
	
	public String deleteUser(){
		try {
			userDao.deleteUser(user);
		} catch (InvalidUserException e) {
			//Here I could assign a I18n text
			reason = e.getMessage();
			return ActionSupport.ERROR;
		}
		return ActionSupport.SUCCESS;
	}

	public UserDao getUserDao() {
		return userDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public SubeCard getSubeCard() {
		return subeCard;
	}

	public void setSubeCard(SubeCard subeCard) {
		this.subeCard = subeCard;
	}

	@Override
	public String getReason() {
		return reason;
	}
}
