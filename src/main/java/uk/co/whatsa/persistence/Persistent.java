package uk.co.whatsa.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base class to all persistent types. All domain model classes extend this class, which provides
 * an {@code ID} instance used to identify the instance in the database.
 * 
 * @see ID
 */
public class Persistent {
    /** Logger. */
	private static final Logger LOG = LoggerFactory.getLogger(Persistent.class);

	/** The persistent class's identifier in the database. */
	private ID id;

	/**
	 * Ctor.
	 */
	protected Persistent() {
	    // Empty
	}
	
	/**
	 * 
	 * @return {@link #id}
	 */
	public final ID getId() {
		return id;
	}

	/**
	 * @param id {@link #id}
	 */
	public final void setId(final ID id) {
		this.id = id;
	}

	/**
	 * @return {@link LOG}
	 */
	protected final Logger getLogger() {
	    return LOG;
	}

//	public boolean isPersistent() {
//		return id != null && id.isSet();
//	}
}
