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

import java.util.List;

import com.opensymphony.xwork2.ActionSupport;
import com.sube.beans.SubeCard;
import com.sube.beans.SubeCardUsage;
import com.sube.beans.User;
import com.sube.daos.mysql.SubeCardDao;
import com.sube.daos.mysql.SubeCardUsageDao;
import com.sube.exceptions.card.InvalidSubeCardException;

public class SubeCardUsageAction implements FailureAwareAction {
	private SubeCardDao subeCardDao;
	private SubeCardUsageDao subeCardUsageDao;
	private User user;
	private SubeCard subeCard;
	private String reason;
	private int limit;
	private List<SubeCardUsage> lastUsage;
	private Double balance;
	
	public String queryBalance(){
		try {
			balance = subeCardDao.getBalance(subeCard);
		} catch (InvalidSubeCardException e) {
			//Here I could assign a I18n text
			reason = e.getMessage();
			return ActionSupport.ERROR;
		}
		return ActionSupport.SUCCESS;
	}
	
	public String listLastUsage(){
		try {
			lastUsage = subeCardUsageDao.listLastUsage(user, subeCard, limit);
		} catch (InvalidSubeCardException e) {
			//Here I could assign a I18n text
			reason = e.getMessage();
			return ActionSupport.ERROR;
		}
		return ActionSupport.SUCCESS;
	}

	@Override
	public String getReason() {
		return reason;
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
	
	public Double getBalance() {
		return balance;
	}

	public void setSubeCard(SubeCard subeCard) {
		this.subeCard = subeCard;
	}

	public void setSubeCardUsageDao(SubeCardDao subeCardDao) {
		this.subeCardDao = subeCardDao;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public List<SubeCardUsage> getLastUsage() {
		return lastUsage;
	}

	public void setSubeCardUsageDao(SubeCardUsageDao subeCardUsageDao) {
		this.subeCardUsageDao = subeCardUsageDao;
	}
}
