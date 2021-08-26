package by.musicwaves.dao.util;

import java.sql.SQLException;

public interface SelfDependentStatementInitializer
{
    void init(PreparedStatementContainer statement) throws SQLException;
}
