package by.musicwaves.dao.util;

import java.sql.SQLException;
import by.musicwaves.entity.Entity;

public interface EntityDependentStatementInitializer<T extends Entity> 
{
    void init(T instance, PreparedStatementContainer statement) throws SQLException;
}
