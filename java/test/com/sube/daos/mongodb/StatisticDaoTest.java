package com.sube.daos.mongodb;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import junit.framework.TestCase;

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
@ContextConfiguration(locations = { "classpath:com/sube/resources/mongodb/mongodbContext.xml","classpath:com/sube/resources/testContext.xml","classpath:applicationContext.xml" })
public class StatisticDaoTest extends TestCase{
	private static final int MIN_USAGES = 100;
	private static final int MAX_USAGES = 500;
	private static final int MIN_CARDS = 50;
	private static final int MAX_CARDS = 60;
	private static final int MIN_PROVIDERS = 5;
	private static final int MAX_PROVIDERS = 10;
	private static final double MONEY_LAMBDA = 2.20;
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
	
	@Before
	public void setUp() throws Exception{
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
	public void testStatistics(){
		Provider provider = getMaxRefunds();
		Provider moreErrorProne = statisticDao.getMoreErrorProne(1).get(0);
		assertEquals("Provider should be the same", provider, moreErrorProne);
		provider = getMostUsed();
		Provider mostProfitable = statisticDao.getMostProfitable(1).get(0);
		assertEquals("Provider should be the same", provider, mostProfitable);
	}
	
	@After
	public void tearDown(){
		cardMongoDao.removeAll();
		entryDao.removeAll();
		providerDao.removeAll();
		cardUsagesDao.removeAll();
	}
	
	private Provider getMaxRefunds(){
		Entry<Provider, Double> maxRefunder = null;
		for(Entry<Provider, Double> entry : refunds.entrySet()){
			if(maxRefunder == null || Math.abs(maxRefunder.getValue()) < Math.abs(entry.getValue())){
				maxRefunder = entry;
			}
		}
		return maxRefunder.getKey();
	}
	
	private Provider getMostUsed(){
		Entry<Provider, Double> mostUsed = null;
		for(Entry<Provider, Double> entry : usages.entrySet()){
			if(mostUsed == null || Math.abs(mostUsed.getValue()) < Math.abs(entry.getValue())){
				mostUsed = entry;
			}
		}
		return mostUsed.getKey();
	}

	private void generateUsages() throws InvalidSubeCardException, InvalidProviderException {
		for(int i=0; i < usagesSize; i++){
			SubeCardUsage usage = new SubeCardUsage();
			usage.setCard((SubeCard) getRandom(cards));
			usage.setDatetime(new Date());
			if(random.nextInt() % 2 == 0){
				usage.setMoney((random.nextDouble() + 0.1d) * MONEY_LAMBDA); // 3.1 max, min 0.1
				usage.setPerformer((Provider) getRandom(cashierProviders));
				cardUsagesDao.chargeMoney(usage);
			}else{
				usage.setMoney((random.nextDouble() + 0.1d) * -MONEY_LAMBDA); // -3.1 min, max -0.1
				usage.setPerformer((Provider) getRandom(serviceProviders));
				cardUsagesDao.chargeService(usage);
			}
			Double totalMoney = usages.get(usage.getPerformer());
			if(totalMoney == null){
				usages.put(usage.getPerformer(), usage.getMoney());
			}else{
				totalMoney += usage.getMoney();
				usages.put(usage.getPerformer(), totalMoney);
			}
		}
	}

	private void generateRefunds() throws InvalidSubeCardException, InvalidProviderException {
		for(int i=0; i < refundsSize; i++){
			SubeCardUsage usage = new SubeCardUsage();
			usage.setCard((SubeCard) getRandom(cards));
			usage.setDatetime(new Date());
			if(random.nextInt() % 2 == 0){
				usage.setMoney((random.nextDouble() + 0.1d) * MONEY_LAMBDA); // 3.1 max, min 0.1
				usage.setPerformer((Provider) getRandom(serviceProviders));
				cardUsagesDao.refundService(usage);
			}else{
				usage.setMoney((random.nextDouble() + 0.1d) * -MONEY_LAMBDA); // -3.1 min, max -0.1
				usage.setPerformer((Provider) getRandom(cashierProviders));
				cardUsagesDao.refundMoney(usage);
			}
			Double totalRefunds = refunds.get(usage.getPerformer());
			if(totalRefunds == null){
				refunds.put(usage.getPerformer(), usage.getMoney());
			}else{
				totalRefunds += usage.getMoney();
				refunds.put(usage.getPerformer(), totalRefunds);
			}
		}
	}

	private void generateCashierProviders() throws InvalidProviderException {
		for(int i=0; i < providersSize; i++){
			LegalPerson legalPerson = legalPersonTestDataGenerator.generateLegalPerson();
			CashierProvider cashierProvider = (CashierProvider) providerTestDataGenerator.generateProvider(ProviderType.CashierProvider, legalPerson);
			providerDao.registerProvider(cashierProvider);
			cashierProviders.add(cashierProvider);
		}
	}

	private void generateServiceProviders() throws InvalidProviderException {
		for(int i=0; i < providersSize; i++){
			LegalPerson legalPerson = legalPersonTestDataGenerator.generateLegalPerson();
			ServiceProvider serviceProvider = (ServiceProvider) providerTestDataGenerator.generateProvider(ProviderType.ServiceProvider, legalPerson);
			providerDao.registerProvider(serviceProvider);
			serviceProviders.add(serviceProvider);
		}
	}
	
	@SuppressWarnings("rawtypes") 
	private Object getRandom(List objects){
		return objects.get(random.nextInt(objects.size()));
	}

	private void generateCards() throws InvalidDataEntryException,
			InvalidSubeCardException {
		for(int i=0; i < cardsSize; i++){
			PhysicalPerson dataEntryPhysicalPerson = physicalPersonTestDataGenerator.generatePhysicalPerson();
			DataEntry dataEntry = dataEntryTestDataGenerator.generateDataEntry(dataEntryPhysicalPerson);
			SubeCard subeCard = subeCardTestDataGenerator.generateSubeCard(dataEntry);
			PhysicalPerson userPhysicalPerson = physicalPersonTestDataGenerator.generatePhysicalPerson();
			User user = userTestDataGenerator.generate(subeCard, userPhysicalPerson);
			subeCard.setCreatedBy(dataEntry);
			subeCard.setUser(user);
			entryDao.createDataEntry(dataEntry);
			subeCard.setCreatedBy(dataEntry);
			cardMongoDao.storeSubeCard(subeCard);
			cards.add(subeCard);
		}
	}

	private void init() {
		random = new Random();
		cards = new ArrayList<SubeCard>();
		serviceProviders = new ArrayList<ServiceProvider>();
		cashierProviders = new ArrayList<CashierProvider>();
		refunds = new HashMap<Provider, Double>();
		usages = new HashMap<Provider, Double>();
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

	public void setUserTestDataGenerator(UserTestDataGenerator userTestDataGenerator) {
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
