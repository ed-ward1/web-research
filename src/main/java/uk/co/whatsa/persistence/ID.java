package uk.co.whatsa.persistence;

import java.io.Serializable;

/**
 * An ID used by all persistent classes. The ID class abstracts away a specific type from any code
 * that makes use of database IDs and clearly shows when a database ID is being used.
 */
public class ID implements Serializable, Comparable<ID> {
	private static final long serialVersionUID = -3417715711491938142L;

	private int id;

	private ID(int id) {
		this.id = id;
	}

	protected int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
	}

	public boolean isSet() {
		return id != 0;
	}
	
	public int getIntValue() {
		return id;
	}
	
	public String toString() {
		return Integer.toString(getId());
	}
	
	public static ID valueOf(int id) {
		return new ID(id);
	}
	
	public static ID valueOf(String id) {
		return new ID(Integer.parseInt(id));
	}
	
	@Override
	public boolean equals(final Object o) {
		if (o == null || ! getClass().equals(o.getClass())) {
			return false;
		}
		if (this == o) {
			return true;
		}
		final ID other = (ID) o;
		if (this.getId() == other.id) {
			return true;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return id;
	}

	@Override
	public int compareTo(ID o) {
		return getIntValue() - o.getIntValue();
	}
}
