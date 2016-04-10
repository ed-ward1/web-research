package uk.co.whatsa.persistence.mybatis;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import uk.co.whatsa.persistence.ID;

/**
 * A MyBatis implementation class used to map database id column(s) to/from an instance of the type {@link ID}.
 */
public class IdTypeHandler extends BaseTypeHandler<ID> {

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, ID parameter, JdbcType jdbcType) throws SQLException {
		ps.setInt(i, parameter.getIntValue());
	}

	@Override
	public ID getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return ID.valueOf(rs.getInt(columnName));
	}

	@Override
	public ID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return ID.valueOf(rs.getInt(columnIndex));
	}

	@Override
	public ID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return ID.valueOf(cs.getInt(columnIndex));
	}

}
