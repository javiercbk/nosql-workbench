package com.sube.beans;

import java.util.UUID;

public class ProviderGeneratorImpl implements ProviderTestDataGenerator {

	@Override
	public Provider generateProvider(ProviderType type, LegalPerson legalPerson) {
		Provider provider = null;
		if(type.equals(ProviderType.CashierProvider)){
			CashierProvider cashierProvider = new CashierProvider();
			cashierProvider.setLegalPerson(legalPerson);
			cashierProvider.setLocation(UUID.randomUUID().toString());
			cashierProvider.setProviderName(UUID.randomUUID().toString());
			cashierProvider.setCashierName(UUID.randomUUID().toString());
			provider = cashierProvider;
		}else{
			ServiceProvider serviceProvider = new ServiceProvider();
			serviceProvider.setLegalPerson(legalPerson);
			serviceProvider.setLocation(UUID.randomUUID().toString());
			serviceProvider.setProviderName(UUID.randomUUID().toString());
			provider = serviceProvider;
		}
		return provider;
	}
}
