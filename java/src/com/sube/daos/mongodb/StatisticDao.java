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

import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import com.sube.beans.Provider;
import com.sube.beans.SubeCard;

public interface StatisticDao {
	public List<Entry<Date,Long>> getUsagesByDates(Date from, Date to);
	public List<SubeCard> getMostTravelers(int limit);
	public List<SubeCard> getMostExpenders(int limit);
	public List<Provider> getMostProfitable(int limit);
	public List<Provider> getMoreErrorProne(int limit);
}
