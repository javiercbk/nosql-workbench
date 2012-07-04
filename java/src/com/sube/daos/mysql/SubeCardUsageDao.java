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

import java.util.List;

import com.sube.beans.CashierProvider;
import com.sube.beans.ServiceProvider;
import com.sube.beans.SubeCard;
import com.sube.beans.SubeCardUsage;
import com.sube.beans.User;
import com.sube.exceptions.card.InvalidSubeCardException;
import com.sube.exceptions.money.InvalidMoneyException;
import com.sube.exceptions.money.NotEnoughMoneyException;
import com.sube.exceptions.person.InvalidLegalPersonException;

public interface SubeCardUsageDao {
	public List<SubeCardUsage> listLastUsage(User user, SubeCard subeCard, int limit) throws InvalidSubeCardException;
	public Double chargeMoney(CashierProvider provider, SubeCard subeCard, Double money) throws InvalidSubeCardException, InvalidMoneyException, InvalidLegalPersonException;
	public Double refundMoney(CashierProvider provider, SubeCard subeCard, Double money) throws InvalidSubeCardException, InvalidMoneyException, InvalidLegalPersonException;
	public Double chargeService(ServiceProvider provider, SubeCard subeCard, Double money) throws InvalidSubeCardException, NotEnoughMoneyException, InvalidLegalPersonException;
	public Double refundService(ServiceProvider provider, SubeCard subeCard, Double money) throws InvalidSubeCardException, NotEnoughMoneyException, InvalidLegalPersonException;
	public Integer cleanCardUsages(Long id);
}
