package uk.co.whatsa.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base class to all persistent types. All domain model classes extend this class, which provides
 * an {@code ID} instance used to identify the instance in the database.
 * 
 * @see ID
 */
public abstract class Persistent {
	protected static Logger LOG = LoggerFactory.getLogger(Persistent.class);

	private ID id;

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

	public boolean isPersistent() {
		return id != null && id.isSet();
	}
}
