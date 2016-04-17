package uk.co.whatsa.persistence.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.ResultSetIdentifierConsumer;
import org.hibernate.usertype.UserType;

import uk.co.whatsa.persistence.ID;

/**
 * A Hibernate {@link UserType} for mapping {@link ID} objects to/from
 * a database.
 */
public class IDUserType implements UserType, ResultSetIdentifierConsumer {
    /** The types of the fields persisted by this user type. */
    private static final int[] TYPES = { Types.INTEGER };

    /**
     * {@inheritDoc}
     */
    @Override
    public final int[] sqlTypes() {
        return TYPES.clone();
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("rawtypes")
    @Override
    public final Class returnedClass() {
        return ID.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean equals(final Object x, final Object y) throws HibernateException {
        if (x == null) {
            return y == null;
        }
        return x.equals(y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int hashCode(final Object x) throws HibernateException {
        return x.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object nullSafeGet(final ResultSet rs, final String[] names, final SessionImplementor session,
            final Object owner) throws HibernateException, SQLException {
        if (rs.wasNull()) {
            return null;
        }
        final int i = rs.getInt(names[0]);
        return ID.valueOf(i);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void nullSafeSet(final PreparedStatement st, final Object value, final int index,
            final SessionImplementor session) throws HibernateException, SQLException {
        if (value == null) {
            st.setNull(index, TYPES[0]);
        } else {
            st.setInt(index, ((ID) value).getIntValue());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object deepCopy(final Object value) throws HibernateException {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final boolean isMutable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Serializable disassemble(final Object value) throws HibernateException {
        return (Serializable) value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object assemble(final Serializable cached, final Object owner) throws HibernateException {
        return cached;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Object replace(final Object original, final Object target, final Object owner)
            throws HibernateException {
        return original;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final Serializable consumeIdentifier(final ResultSet resultSet) {
        try {
            return ID.valueOf(resultSet.getInt(1));
        } catch (SQLException e) {
            throw new HibernateException(e);
        }
    }
}
