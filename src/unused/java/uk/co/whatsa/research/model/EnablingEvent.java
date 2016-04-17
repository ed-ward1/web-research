package uk.co.whatsa.research.model;

import org.joda.time.DateTime;

import uk.co.whatsa.persistence.Persistent;

public class EnablingEvent extends Persistent {
	private DateTime date;
	private String country;
	private String city;

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
