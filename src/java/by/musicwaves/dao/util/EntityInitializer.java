package by.musicwaves.dao.util;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface EntityInitializer<T> 
{
    void init(T instance, ResultSet resultSet) throws SQLException;
}
