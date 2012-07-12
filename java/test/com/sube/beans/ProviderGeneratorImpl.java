package com.sube.beans;

public class ProviderGeneratorImpl implements ProviderTestDataGenerator {

	@Override
	public Provider generateProvider(ProviderType type, LegalPerson legalPerson) {
		Provider provider = null;
		if(type.equals(ProviderType.CashierProvider)){
			CashierProvider cashierProvider = new CashierProvider();
			cashierProvider.setLegalPerson(legalPerson);
			cashierProvider.setLocation("Some Cashier Location");
			cashierProvider.setProviderName("Some Cashier Provider name");
			cashierProvider.setCashierName("Some Cashier Name");
			provider = cashierProvider;
		}else{
			ServiceProvider serviceProvider = new ServiceProvider();
			serviceProvider.setLegalPerson(legalPerson);
			serviceProvider.setLocation("Some Service Location");
			serviceProvider.setProviderName("Some Service Provider name");
			provider = serviceProvider;
		}
		return provider;
	}
}
