package com.mediconnect.backend.feature.doctorembedding.converter;

import com.pgvector.PGvector;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Arrays;
import org.hibernate.type.SqlTypes;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

public class PGvectorUserType implements UserType<PGvector> {

    @Override
    public int getSqlType() {
        return SqlTypes.OTHER;
    }

    @Override
    public Class<PGvector> returnedClass() {
        return PGvector.class;
    }

    @Override
    public boolean equals(PGvector x, PGvector y) {
        if (x == y) return true;
        if (x == null || y == null) return false;
        return Arrays.equals(x.toArray(), y.toArray());
    }

    @Override
    public int hashCode(PGvector x) {
        return x == null ? 0 : Arrays.hashCode(x.toArray());
    }

    @Override
    public PGvector nullSafeGet(ResultSet rs, int position, WrapperOptions options) throws SQLException {
        Object val = rs.getObject(position);
        if (val == null) {
            return null;
        }
        if (val instanceof PGvector v) {
            return v;
        }
        if (val instanceof PGobject pgo) {
            return new PGvector(pgo.getValue());
        }
        return new PGvector(val.toString());
    }

    @Override
    public void nullSafeSet(PreparedStatement st, PGvector value, int index, WrapperOptions options) throws SQLException {
        if (value == null) {
            st.setNull(index, Types.OTHER);
        } else {
            st.setObject(index, value, Types.OTHER);
        }
    }

    @Override
    public PGvector deepCopy(PGvector value) {
        if (value == null) return null;
        return new PGvector(value.toArray());
    }

    @Override
    public boolean isMutable() {
        return false;
    }

    @Override
    public Serializable disassemble(PGvector value) {
        return value == null ? null : value.getValue();
    }

    @Override
    public PGvector assemble(Serializable cached, Object owner) {
        if (cached == null) return null;
        try {
            return new PGvector((String) cached);
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
