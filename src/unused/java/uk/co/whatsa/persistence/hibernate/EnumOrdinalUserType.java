package uk.co.whatsa.persistence.hibernate;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

/**
 * A Hibernate {@link UserType} for mapping {@link Enum} objects based on their ordinal values.
 * The type/class of the enum is passed to {@code EnumOrdinalUserType} via a parameter "{@code enumType}".
 * 
 * <pre>
 * eg.
 * 
 * {@code
 * <property name="type" column="type">
 *   <type name="uk.co.whatsa.persistence.hibernate.EnumOrdinalUserType">
 *     <param name="enumType">uk.co.whatsa.research.model.WebPageResourceType</param>
 *   </type>
 * </property>
 * }
 * </pre>
 * 
 * The {@code UserType} can also be defined as a {@code <typedef>} and referenced by name in the mapping.
 * <pre>
 * eg.
 * 
 * {@code
 * <typedef name="enumordinal" class="uk.co.whatsa.persistence.hibernate.EnumOrdinalUserType">
 *   <param name="enumType">uk.co.whatsa.research.model.WebPageResourceType</param>
 * </typedef>
 * 
 * <class ...>
 *    <property name="theEnum" type="enumordinal"/>
 * </class> 
 * }
 * </pre>
 */
//TODO make this configurable - to use either ordinals or the text of the enum value.
@SuppressWarnings("rawtypes")
public class EnumOrdinalUserType implements UserType, ParameterizedType {
	private static int[] sqlTypes = { Types.TINYINT };
	
	private Class<Enum> enumType;

	@SuppressWarnings("unchecked")
	@Override
	public void setParameterValues(Properties parameters) {
		final String enumTypeName = parameters.getProperty("enumType");
		if (enumTypeName == null) {
			throw new MappingException("property 'enumType' is not configured");
		}
		try {
			enumType = (Class<Enum>) Class.forName(enumTypeName);
		} catch (ClassNotFoundException e) {
			throw new MappingException("configured enumType not found", e);
		} catch (Exception e) {
			throw new MappingException(e);
		}
	}

	@Override
	public int[] sqlTypes() {
		return sqlTypes;
	}

	@Override
	public Class returnedClass() {
		return enumType;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return x.equals(y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		if (rs.wasNull()) {
			return null;
		}
		int i = rs.getInt(names[0]);
		return getEnumFromOrdinal(i);
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
			throws HibernateException, SQLException {
		if (value == null) {
			st.setNull(index, sqlTypes[0]);
		} else {
			st.setInt(index, ((Enum) value).ordinal());
		}
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return original;
	}

	private Enum getEnumFromOrdinal(final int ordinal) {
		try {
			Method valuesMethod = enumType.getMethod("values");
			Enum[] values = (Enum[]) valuesMethod.invoke(null);

			if (ordinal >= 0 && ordinal < values.length) {
				return values[ordinal];
			}
			return values[0];
		} catch (Exception e) {
			throw new MappingException(e);
		}
	}
}
