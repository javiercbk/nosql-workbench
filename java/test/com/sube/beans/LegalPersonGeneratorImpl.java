package com.sube.beans;

import java.util.Random;
import java.util.UUID;

public class LegalPersonGeneratorImpl implements LegalPersonTestDataGenerator {

	@Override
	public LegalPerson generateLegalPerson() {
		LegalPerson legalPerson = new LegalPerson();
		legalPerson.setCuit(new Random().nextLong());
		legalPerson.setFantasyName(UUID.randomUUID().toString());
		legalPerson.setLegalLocation(UUID.randomUUID().toString());
		legalPerson.setLegalName(UUID.randomUUID().toString());
		return legalPerson;
	}

}
