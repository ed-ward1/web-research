package uk.co.whatsa.persistence;

import java.io.Serializable;

/**
 * An ID used by all persistent classes. The ID class abstracts away a
 * specific type from any code that makes use of database IDs and
 * clearly shows when a database ID is being used.
 */
@SuppressWarnings("PMD.ShortClassName")
public final class ID implements Serializable, Comparable<ID> {
    private static final long serialVersionUID = -3417715711491938142L;

    /** The encapsulated identifier. */
    private int id;

    /**
     * @param id {@link #id}
     */
    private ID(final int id) {
        this.id = id;
    }

    /**
     * @return {@link #id}
     */
    protected int getId() {
        return id;
    }

    /**
     * @param id {@link #id}
     */
    protected void setId(final int id) {
        this.id = id;
    }

    /**
     * @return true if the object contains a valid identifier
     */
    public boolean isSet() {
        return id != 0;
    }

    /**
     * @return this identifier represented as an int value
     */
    public int getIntValue() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Integer.toString(getId());
    }

    /**
     * Converts an int into an ID instance.
     * 
     * @param id an identifier represented as an {@code int}
     * @return the newly instantiated ID instance
     */
    public static ID valueOf(final int id) {
        return new ID(id);
    }

    /**
     * Converts a {@code String} into an ID instance.
     * 
     * @param id an identifier represented as a {@code String}
     * @return the newly instantiated ID instance
     */
    public static ID valueOf(final String id) {
        return new ID(Integer.parseInt(id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(final Object o) {
        if (o == null || !getClass().equals(o.getClass())) {
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

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(final ID o) {
        return getIntValue() - o.getIntValue();
    }
}
