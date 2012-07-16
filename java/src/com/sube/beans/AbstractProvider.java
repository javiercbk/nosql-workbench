package com.sube.beans;

public abstract class AbstractProvider implements Provider{
	protected LegalPerson legalPerson;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getProviderName() == null) ? 0 : getProviderName().hashCode());
		result = prime * result
				+ ((legalPerson == null) ? 0 : legalPerson.hashCode());
		result = prime * result
				+ ((getProviderType() == null) ? 0 : getProviderType().hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractProvider other = (AbstractProvider) obj;
		if (getProviderName() == null) {
			if (other.getProviderName() != null)
				return false;
		} else if (!getProviderName().equals(other.getProviderName()))
			return false;
		if (legalPerson == null) {
			if (other.getLegalPerson() != null)
				return false;
		} else if (!legalPerson.equals(other.getLegalPerson()))
			return false;
		if (getProviderType() != other.getProviderType())
			return false;
		return true;
	}
	
	public LegalPerson getLegalPerson() {
		return legalPerson;
	}
	public void setLegalPerson(LegalPerson legalPerson) {
		this.legalPerson = legalPerson;
	}
}
