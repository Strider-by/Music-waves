package by.musicwaves.dao;

import java.util.List;
import by.musicwaves.entity.Entity;

public interface Dao<K, T extends Entity> extends AutoCloseable
{

    List<T> findAll() throws DaoException;
    public abstract T findById(K id) throws DaoException;
    public abstract Integer create(T instance) throws DaoException;
    public abstract T update(T instance) throws DaoException;
    public abstract boolean delete(K id) throws DaoException;
    public abstract boolean delete(T instance) throws DaoException;

    // SQL parts we can use to build our SQL expression
    public final static String SQL_WHERE = " WHERE ";
    public final static String SQL_AND = " AND ";
    public final static String SQL_LIKE = " LIKE ?";
    public final static String SQL_EQUALS = " = ?";
    public final static String SQL_LIMIT_OFFSET = " LIMIT ? OFFSET ?";
    public final static String SQL_LIMIT = " LIMIT ?";

}
