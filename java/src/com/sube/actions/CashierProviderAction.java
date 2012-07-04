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
import com.sube.beans.CashierProvider;
import com.sube.beans.SubeCard;
import com.sube.daos.mysql.SubeCardUsageDao;
import com.sube.exceptions.card.InvalidSubeCardException;
import com.sube.exceptions.money.InvalidMoneyException;
import com.sube.exceptions.person.InvalidLegalPersonException;

public class CashierProviderAction implements FailureAwareAction{
	private SubeCardUsageDao subeCardUsageDao;
	private SubeCard subeCard;
	private String reason;
	private Double money;
	private CashierProvider provider;
	
	public String performCashierAction(){
		try {
			if(money > 0){
				subeCardUsageDao.chargeMoney(provider, subeCard, money);
			}else{
				subeCardUsageDao.refundMoney(provider, subeCard, money);
			}
		} catch (InvalidSubeCardException e) {
			//Here I could assign a I18n text
			reason = e.getMessage();
			return ActionSupport.ERROR;
		} catch (InvalidMoneyException e) {
			//Here I could assign a I18n text
			reason = e.getMessage();
			return ActionSupport.ERROR;
		} catch (InvalidLegalPersonException e) {
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

	public void setProvider(CashierProvider provider) {
		this.provider = provider;
	}

	public void setSubeCardUsageDao(SubeCardUsageDao subeCardUsageDao) {
		this.subeCardUsageDao = subeCardUsageDao;
	}

	public void setSubeCard(SubeCard subeCard) {
		this.subeCard = subeCard;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public SubeCard getSubeCard() {
		return subeCard;
	}

	public Double getMoney() {
		return money;
	}
}
