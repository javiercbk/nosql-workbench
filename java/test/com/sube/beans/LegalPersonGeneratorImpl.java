package com.sube.beans;

public class LegalPersonGeneratorImpl implements LegalPersonTestDataGenerator {

	@Override
	public LegalPerson generateLegalPerson() {
		LegalPerson legalPerson = new LegalPerson();
		legalPerson.setCuit(1297213l);
		legalPerson.setFantasyName("Legal Fantasy Name");
		legalPerson.setLegalLocation("Some legal location");
		legalPerson.setLegalName("Some Legal Name");
		return legalPerson;
	}

}
