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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import junit.framework.TestCase;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sube.beans.CashierProvider;
import com.sube.beans.DataEntry;
import com.sube.beans.DataEntryTestDataGenerator;
import com.sube.beans.LegalPerson;
import com.sube.beans.LegalPersonTestDataGenerator;
import com.sube.beans.PhysicalPerson;
import com.sube.beans.PhysicalPersonTestDataGenerator;
import com.sube.beans.Provider;
import com.sube.beans.ProviderTestDataGenerator;
import com.sube.beans.ProviderType;
import com.sube.beans.ServiceProvider;
import com.sube.beans.SubeCard;
import com.sube.beans.SubeCardTestDataGenerator;
import com.sube.beans.SubeCardUsage;
import com.sube.beans.User;
import com.sube.beans.UserTestDataGenerator;
import com.sube.exceptions.card.InvalidSubeCardException;
import com.sube.exceptions.person.InvalidDataEntryException;
import com.sube.exceptions.person.InvalidProviderException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:com/sube/resources/mongodb/mongodbContext.xml",
		"classpath:com/sube/resources/testContext.xml",
		"classpath:applicationContext.xml" })
public class StatisticDaoTest extends TestCase {
	private static final int MIN_USAGES = 100;
	private static final int MAX_USAGES = 500;
	private static final int MIN_CARDS = 50;
	private static final int MAX_CARDS = 60;
	private static final int MIN_PROVIDERS = 5;
	private static final int MAX_PROVIDERS = 10;
	private static final double MONEY_LAMBDA = 2.20;
	private static final int DATE_DIFF = -8;
	private static final int DATE_DIFF1 = -3;
	private static final int DATE_DIFF2 = 4;
	private final Date FROM = DateUtils.addDays(new Date(), DATE_DIFF);
	private final Date TO = new Date(); 
	private final Date TO_DIFF = truncateDate(DateUtils.addDays(TO, DATE_DIFF1));
	private final Date FROM_DIFF = truncateDate(DateUtils.addDays(FROM, DATE_DIFF2));
	@Autowired
	private CardDao cardMongoDao;
	@Autowired
	private EntryDao entryDao;
	@Autowired
	private SubeCardTestDataGenerator subeCardTestDataGenerator;
	@Autowired
	private PhysicalPersonTestDataGenerator physicalPersonTestDataGenerator;
	@Autowired
	private DataEntryTestDataGenerator dataEntryTestDataGenerator;
	@Autowired
	private UserTestDataGenerator userTestDataGenerator;
	@Autowired
	private ProviderTestDataGenerator providerTestDataGenerator;
	@Autowired
	private LegalPersonTestDataGenerator legalPersonTestDataGenerator;
	@Autowired
	private CardUsagesDao cardUsagesDao;
	@Autowired
	private ProviderDao providerDao;
	@Autowired
	private StatisticDao statisticDao;
	private Map<Provider, Double> refunds;
	private Map<Provider, Double> usages;
	private List<SubeCard> cards;
	private List<ServiceProvider> serviceProviders;
	private List<CashierProvider> cashierProviders;
	private int cardsSize;
	private int providersSize;
	private int refundsSize;
	private int usagesSize;
	private Random random;
	private Map<Date, Long> usages1;
	private Map<Date, Long> usages2;
	private Map<SubeCard, Long> travels;
	private Map<SubeCard, Double> expended;

	@Before
	public void setUp() throws Exception {
		super.setUp();
		init();
		cardsSize = random.nextInt(MAX_CARDS) + MIN_CARDS;
		providersSize = random.nextInt(MAX_PROVIDERS) + MIN_PROVIDERS;
		refundsSize = random.nextInt(MAX_USAGES) + MIN_USAGES;
		usagesSize = random.nextInt(MAX_USAGES) + MIN_USAGES;
		generateCards();
		generateCashierProviders();
		generateServiceProviders();
		generateUsages();
		generateRefunds();
	}

	@Test
	public void testStatistics() {
		Double amount = getMaxRefunds();
		Entry<Provider,Double> moreErrorProne = statisticDao.getMoreErrorProne(1).get(0);
		assertEquals("Money should be the same", roundTwoDecimals(amount), roundTwoDecimals(moreErrorProne.getValue()));
		amount = getMostUsed();
		Entry<Provider,Double> mostProfitable = statisticDao.getMostProfitable(1).get(0);
		assertEquals("Money should be the same", roundTwoDecimals(amount), roundTwoDecimals(mostProfitable.getValue()));
		List<Entry<Date, Long>> usagesByDates = statisticDao.getUsagesByDates(TO_DIFF, TO);
		assertAllElementsMatch(usagesByDates, usages1);
		usagesByDates= statisticDao.getUsagesByDates(FROM, FROM_DIFF);
		assertAllElementsMatch(usagesByDates, usages2);
		Long mostTraveler = getMostTraveler();
		Entry<SubeCard, Long> result = statisticDao.getMostTravelers(1).get(0);
		assertEquals("Travels should be the same", mostTraveler, result.getValue());
		Double mostExpender = getMostExpender();
		Entry<SubeCard, Double> resultMoney = statisticDao.getMostExpenders(1).get(0);
		assertEquals("Money should be the same", roundTwoDecimals(mostExpender), roundTwoDecimals(resultMoney.getValue()));
	}

	private void assertAllElementsMatch(List<Entry<Date, Long>> usagesByDates, Map<Date,Long> usagesMap) {
		for(Entry<Date, Long> entry : usagesByDates){
			Long count = usagesMap.get(entry.getKey());
			assertEquals("Count should be the same", count, entry.getValue());
		}
	}

	@After
	public void tearDown() {
		cardMongoDao.removeAll();
		entryDao.removeAll();
		providerDao.removeAll();
		cardUsagesDao.removeAll();
	}

	private Double getMaxRefunds() {
		Entry<Provider, Double> maxRefunder = null;
		for (Entry<Provider, Double> entry : refunds.entrySet()) {
			if (maxRefunder == null
					|| Math.abs(maxRefunder.getValue()) < Math.abs(entry
							.getValue())) {
				maxRefunder = entry;
			}
		}
		return maxRefunder.getValue();
	}

	private Double getMostUsed() {
		Entry<Provider, Double> mostUsed = null;
		for (Entry<Provider, Double> entry : usages.entrySet()) {
			if (mostUsed == null
					|| Math.abs(mostUsed.getValue()) < Math.abs(entry
							.getValue())) {
				mostUsed = entry;
			}
		}
		return mostUsed.getValue();
	}
	
	private Long getMostTraveler() {
		Long current = Long.valueOf(-1l);
		for(Entry<SubeCard, Long> entry : travels.entrySet()){
			if(entry.getValue() > current){
				current = entry.getValue();
			}
		}
		return current;
	}
	
	private Double getMostExpender(){
		Double current = null;
		for(Entry<SubeCard, Double> entry : expended.entrySet()){
			if(current == null || entry.getValue() > current){
				current = entry.getValue();
			}
		}
		return current;
	}

	private void generateUsages() throws InvalidSubeCardException,
			InvalidProviderException {
		for (int i = 0; i < usagesSize; i++) {
			SubeCardUsage usage = new SubeCardUsage();
			usage.setCard((SubeCard) getRandom(cards));
			Date randomDate = DateUtils.round(DateUtils.round(DateUtils.round(getRandomDate(FROM, TO), Calendar.SECOND), Calendar.MINUTE), Calendar.HOUR);
			usage.setDatetime(randomDate);
			if((randomDate.after(TO_DIFF) || DateUtils.isSameDay(randomDate, TO_DIFF)) &&
					(randomDate.before(TO) ||DateUtils.isSameDay(randomDate, TO) )){
				Long count = usages1.get(randomDate);
				if(count != null){
					count++;
				}else{
					count = Long.valueOf(1l);
				}
				usages1.put(randomDate, count);
			}else if((randomDate.after(FROM) || DateUtils.isSameDay(randomDate, FROM)) &&
					(randomDate.before(FROM_DIFF) || DateUtils.isSameDay(randomDate, FROM_DIFF))){
				Long count = usages2.get(randomDate);
				if(count != null){
					count++;
				}else{
					count = Long.valueOf(1l);
				}
				usages2.put(randomDate, count);
			}
			if (random.nextInt() % 2 == 0) {
				//Charge Money
				usage.setMoney((random.nextDouble() + 0.1d) * MONEY_LAMBDA); // 3.1
																				// max,
																				// min
																				// 0.1
				usage.setPerformer((Provider) getRandom(cashierProviders));
				cardUsagesDao.chargeMoney(usage);
			} else {
				//Charge Service
				usage.setMoney((random.nextDouble() + 0.1d) * -MONEY_LAMBDA); // -3.1
																				// min,
																				// max
																				// -0.1
				usage.setPerformer((Provider) getRandom(serviceProviders));
				cardUsagesDao.chargeService(usage);
				Long travelsCount = travels.get(usage.getCard());
				if(travelsCount == null){
					travelsCount = Long.valueOf(1l);
				}else{
					travelsCount++;
				}
				travels.put(usage.getCard(), travelsCount);
			}
			Double totalMoney = usages.get(usage.getPerformer());
			if (totalMoney == null) {
				usages.put(usage.getPerformer(), Math.abs(usage.getMoney()));
			} else {
				totalMoney += Math.abs(usage.getMoney());
				usages.put(usage.getPerformer(), totalMoney);
			}
			registerMoneyExpended(usage);
		}
	}
	
	private void registerMoneyExpended(SubeCardUsage usage){
		Double totalExpended = expended.get(usage.getCard());
		if(totalExpended == null){
			totalExpended = Double.valueOf(usage.getMoney());
		}else{
			totalExpended = totalExpended + usage.getMoney();
		}
		expended.put(usage.getCard(), totalExpended);
	}

	private void generateRefunds() throws InvalidSubeCardException,
			InvalidProviderException {
		for (int i = 0; i < refundsSize; i++) {
			SubeCardUsage usage = new SubeCardUsage();
			usage.setCard((SubeCard) getRandom(cards));
			usage.setDatetime(new Date());
			if (random.nextInt() % 2 == 0) {
				usage.setMoney((random.nextDouble() + 0.1d) * MONEY_LAMBDA); // 3.1
																				// max,
																				// min
																				// 0.1
				usage.setPerformer((Provider) getRandom(serviceProviders));
				cardUsagesDao.refundService(usage);
			} else {
				usage.setMoney((random.nextDouble() + 0.1d) * -MONEY_LAMBDA); // -3.1
																				// min,
																				// max
																				// -0.1
				usage.setPerformer((Provider) getRandom(cashierProviders));
				cardUsagesDao.refundMoney(usage);
			}
			Double totalRefunds = refunds.get(usage.getPerformer());
			if (totalRefunds == null) {
				refunds.put(usage.getPerformer(), Math.abs(usage.getMoney()));
			} else {
				totalRefunds += Math.abs(usage.getMoney());
				refunds.put(usage.getPerformer(), totalRefunds);
			}
			registerMoneyExpended(usage);
		}
	}

	private void generateCashierProviders() throws InvalidProviderException {
		for (int i = 0; i < providersSize; i++) {
			LegalPerson legalPerson = legalPersonTestDataGenerator
					.generateLegalPerson();
			CashierProvider cashierProvider = (CashierProvider) providerTestDataGenerator
					.generateProvider(ProviderType.CashierProvider, legalPerson);
			providerDao.registerProvider(cashierProvider);
			cashierProviders.add(cashierProvider);
		}
	}

	private void generateServiceProviders() throws InvalidProviderException {
		for (int i = 0; i < providersSize; i++) {
			LegalPerson legalPerson = legalPersonTestDataGenerator
					.generateLegalPerson();
			ServiceProvider serviceProvider = (ServiceProvider) providerTestDataGenerator
					.generateProvider(ProviderType.ServiceProvider, legalPerson);
			providerDao.registerProvider(serviceProvider);
			serviceProviders.add(serviceProvider);
		}
	}

	@SuppressWarnings("rawtypes")
	private Object getRandom(List objects) {
		return objects.get(random.nextInt(objects.size()));
	}

	private void generateCards() throws InvalidDataEntryException,
			InvalidSubeCardException {
		for (int i = 0; i < cardsSize; i++) {
			PhysicalPerson dataEntryPhysicalPerson = physicalPersonTestDataGenerator
					.generatePhysicalPerson();
			DataEntry dataEntry = dataEntryTestDataGenerator
					.generateDataEntry(dataEntryPhysicalPerson);
			SubeCard subeCard = subeCardTestDataGenerator
					.generateSubeCard(dataEntry);
			PhysicalPerson userPhysicalPerson = physicalPersonTestDataGenerator
					.generatePhysicalPerson();
			User user = userTestDataGenerator.generate(subeCard,
					userPhysicalPerson);
			subeCard.setCreatedBy(dataEntry);
			subeCard.setUser(user);
			entryDao.createDataEntry(dataEntry);
			subeCard.setCreatedBy(dataEntry);
			cardMongoDao.storeSubeCard(subeCard);
			cards.add(subeCard);
		}
	}
	
	private double roundTwoDecimals(double d) {
    	DecimalFormat twoDForm = new DecimalFormat("#.##");
    	return Double.valueOf(twoDForm.format(d));
	}

	private Date getRandomDate(Date from, Date to) {
		Random random = new Random();
		long newTime = nextLong(random, Math.abs(to.getTime() - from.getTime()))
				+ Math.min(to.getTime(), from.getTime());
		return new Date(newTime);
	}

	private long nextLong(Random rng, long n) {
		long bits, val;
		do {
			bits = (rng.nextLong() << 1) >>> 1;
			val = bits % n;
		} while (bits - val + (n - 1) < 0L);
		return val;
	}
	
	private Date truncateDate(Date date){
		Calendar cal = Calendar.getInstance(); // locale-specific
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		return new Date(cal.getTimeInMillis());
	}

	private void init() {
		random = new Random();
		cards = new ArrayList<SubeCard>();
		serviceProviders = new ArrayList<ServiceProvider>();
		cashierProviders = new ArrayList<CashierProvider>();
		refunds = new HashMap<Provider, Double>();
		usages = new HashMap<Provider, Double>();
		usages1 = new HashMap<Date, Long>();
		usages2 = new HashMap<Date, Long>();
		travels = new HashMap<SubeCard, Long>();
		expended = new HashMap<SubeCard, Double>();
	}

	public void setCardMongoDao(CardDao cardMongoDao) {
		this.cardMongoDao = cardMongoDao;
	}

	public void setEntryDao(EntryDao entryDao) {
		this.entryDao = entryDao;
	}

	public void setSubeCardTestDataGenerator(
			SubeCardTestDataGenerator subeCardTestDataGenerator) {
		this.subeCardTestDataGenerator = subeCardTestDataGenerator;
	}

	public void setPhysicalPersonTestDataGenerator(
			PhysicalPersonTestDataGenerator physicalPersonTestDataGenerator) {
		this.physicalPersonTestDataGenerator = physicalPersonTestDataGenerator;
	}

	public void setDataEntryTestDataGenerator(
			DataEntryTestDataGenerator dataEntryTestDataGenerator) {
		this.dataEntryTestDataGenerator = dataEntryTestDataGenerator;
	}

	public void setUserTestDataGenerator(
			UserTestDataGenerator userTestDataGenerator) {
		this.userTestDataGenerator = userTestDataGenerator;
	}

	public void setProviderTestDataGenerator(
			ProviderTestDataGenerator providerTestDataGenerator) {
		this.providerTestDataGenerator = providerTestDataGenerator;
	}

	public void setLegalPersonTestDataGenerator(
			LegalPersonTestDataGenerator legalPersonTestDataGenerator) {
		this.legalPersonTestDataGenerator = legalPersonTestDataGenerator;
	}

	public void setCardUsagesDao(CardUsagesDao cardUsagesDao) {
		this.cardUsagesDao = cardUsagesDao;
	}

	public void setProviderDao(ProviderDao providerDao) {
		this.providerDao = providerDao;
	}
}
