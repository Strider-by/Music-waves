package by.musicwaves.dao.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import by.musicwaves.connection.ConnectionPool;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.Entity;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SQLRequestHandler
{
    private final static Logger LOGGER = LogManager.getLogger();
    private final Connection connection = ConnectionPool.INSTANCE.getConnection();
    private boolean possiblyBadConnection = false;

    public void close()
    {
        if (!possiblyBadConnection)
        {
            ConnectionPool.INSTANCE.returnConnection(connection);
        }
        else
        {
            ConnectionPool.INSTANCE.returnBadConnection(connection);
        }
    }

    public <T extends Entity> List<T> processMultipleResultsSelectRequest(
            String sql,
            EntityFactory<T> factory,
            EntityInitializer<T> eInitializer,
            SelfDependentStatementInitializer sInitializer) throws DaoException
    {

        List<T> list = new ArrayList<>();
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        try
        {
            statementContainer.wrap(connection.prepareStatement(sql));
            if (sInitializer != null)
            {
                sInitializer.init(statementContainer);
            }
            LOGGER.debug("filled sql: " + statementContainer);
            try (ResultSet resultSet = statementContainer.executeQuery())
            {
                while (resultSet.next())
                {
                    T instance = factory.createInstance();
                    eInitializer.init(instance, resultSet);
                    list.add(instance);
                }
            }

            LOGGER.debug("SQL performed: " + statementContainer);
            return list;
        }
        catch (SQLException ex)
        {
            throw new DaoException("Failed to execute request", ex);
        }
        finally
        {
            try
            {
                if (statementContainer.getInnerStatement() != null)
                {
                    statementContainer.close();
                }
            }
            catch (SQLException ex)
            {
                LOGGER.error("Failed to close prepared statement", ex);
            }
        }
    }

    public <T extends Entity> T processSingleResultSelectRequest(
            String sql, EntityFactory<T> factory,
            EntityInitializer<T> eInitializer,
            SelfDependentStatementInitializer sInitializer) throws DaoException
    {
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        try
        {
            statementContainer.wrap(connection.prepareStatement(sql));
            if (sInitializer != null)
            {
                sInitializer.init(statementContainer);
            }
            try (ResultSet resultSet = statementContainer.executeQuery())
            {
                if (resultSet.next())
                {
                    T instance = factory.createInstance();
                    eInitializer.init(instance, resultSet);
                    return instance;
                }
                else
                {
                    resultSet.close();
                    return null;
                }
            }
        }
        catch (SQLException ex)
        {
            throw new DaoException("Failed to execute request", ex);
        }
        finally
        {
            try
            {
                if (statementContainer.getInnerStatement() != null)
                {
                    statementContainer.close();
                }
            }
            catch (SQLException ex)
            {
                possiblyBadConnection = true;
                LOGGER.error("Failed to close prepared statement", ex);
            }
        }
    }

    public <T extends Entity> Integer processCreateRequest(
            T instance,
            String sql,
            EntityDependentStatementInitializer<T> initializer) throws DaoException
    {

        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Integer result = null;

        try
        {
            statementContainer.wrap(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS));
            initializer.init(instance, statementContainer);

            int affectedRows = statementContainer.executeUpdate();
            if (affectedRows == 0)
            {
                throw new SQLException("Creation failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statementContainer.getGeneratedKeys())
            {
                if (generatedKeys.next())
                {
                    result = generatedKeys.getInt(1);
                }
                else
                {
                    connection.rollback();
                    throw new SQLException("Creation failed, no ID obtained.");
                }
            }
            return result;
        }
        catch (SQLException ex)
        {
            throw new DaoException("Failed to execute request", ex);
        }
        finally
        {
            try
            {
                if (statementContainer.getInnerStatement() != null)
                {
                    statementContainer.close();
                }
            }
            catch (SQLException ex)
            {
                possiblyBadConnection = true;
                LOGGER.error("Failed to close statement", ex);
            }
        }
    }

    public Integer processCustomCreateRequest(
            String sql,
            SelfDependentStatementInitializer initializer) throws DaoException
    {

        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        Integer result = null;

        try
        {
            statementContainer.wrap(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS));
            if (initializer != null)
            {
                initializer.init(statementContainer);
            }

            int affectedRows = statementContainer.executeUpdate();
            if (affectedRows == 0)
            {
                throw new SQLException("Creation failed, no rows affected.");
            }

            try (ResultSet generatedKeys = statementContainer.getGeneratedKeys())
            {
                if (generatedKeys.next())
                {
                    result = generatedKeys.getInt(1);
                }
                else
                {
                    throw new SQLException("Creation failed, no ID obtained.");
                }
            }
            return result;
        }
        catch (SQLException ex)
        {
            throw new DaoException("Failed to execute request", ex);
        }
        finally
        {
            try
            {
                if (statementContainer.getInnerStatement() != null)
                {
                    statementContainer.close();
                }
            }
            catch (SQLException ex)
            {
                possiblyBadConnection = true;
                LOGGER.error("Failed to close statement", ex);
            }
        }
    }

    public <T extends Entity> T processUpdateRequest(
            T instance, String sql,
            EntityDependentStatementInitializer<T> edsInitializer,
            SelfDependentStatementInitializer sdsInitializer) throws DaoException
    {
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();

        try
        {
            statementContainer.wrap(connection.prepareStatement(sql));

            edsInitializer.init(instance, statementContainer);
            if (sdsInitializer != null)
            {
                sdsInitializer.init(statementContainer);
            }

            LOGGER.debug("update SQL request is: " + statementContainer);
            int affectedRows = statementContainer.executeUpdate();
            if (affectedRows == 0)
            {
                throw new SQLException("Update failed, no rows affected.");
            }

            return instance;
        }
        catch (SQLException ex)
        {
            throw new DaoException("Failed to execute request", ex);
        }
        finally
        {
            try
            {
                if (statementContainer.getInnerStatement() != null)
                {
                    statementContainer.close();
                }
            }
            catch (SQLException ex)
            {
                possiblyBadConnection = true;
                LOGGER.error("Failed to close statement", ex);
            }
        }
    }

    public int processCustomUpdateRequest(String sql, SelfDependentStatementInitializer sdsInitializer) throws DaoException
    {
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();

        try
        {
            statementContainer.wrap(connection.prepareStatement(sql));
            if (sdsInitializer != null)
            {
                sdsInitializer.init(statementContainer);
            }

            LOGGER.debug("update SQL request is: " + statementContainer);
            int affectedRows = statementContainer.executeUpdate();
            return affectedRows;
        }
        catch (SQLException ex)
        {
            throw new DaoException("Failed to execute SQL update request", ex);
        }
        finally
        {
            try
            {
                if (statementContainer.getInnerStatement() != null)
                {
                    statementContainer.close();
                }
            }
            catch (SQLException ex)
            {
                possiblyBadConnection = true;
                LOGGER.error("Failed to close statement", ex);
            }
        }
    }

    public <T extends Entity> void processMultipleUpdateRequest(
            T[] instances,
            String sql,
            EntityDependentStatementInitializer<T> edsInitializer1,
            EntityDependentStatementInitializer<T> edsInitializer2) throws DaoException
    {
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        try
        {
            connection.setAutoCommit(false);
            statementContainer.wrap(connection.prepareStatement(sql));

            for (T instance : instances)
            {
                edsInitializer1.init(instance, statementContainer);
                if (edsInitializer2 != null)
                {
                    edsInitializer2.init(instance, statementContainer);
                }
                statementContainer.addBatch();
            }

            LOGGER.debug("update SQL request is: " + statementContainer);
            statementContainer.executeBatch();
            connection.commit();

        }
        catch (SQLException ex)
        {
            possiblyBadConnection = true;
            try
            {
                connection.rollback();
            }
            catch (SQLException rollbackEx)
            {
                possiblyBadConnection = true;
                LOGGER.error("failed to rollback operation", rollbackEx);
            }
            throw new DaoException("Failed to execute request", ex);
        }
        finally
        {
            try
            {
                connection.setAutoCommit(true);
            }
            catch (SQLException ex)
            {
                LOGGER.error("Failed to set autocommit true", ex);
                possiblyBadConnection = true;
            }

            try
            {
                if (statementContainer.getInnerStatement() != null)
                {
                    statementContainer.close();
                }
            }
            catch (SQLException ex)
            {
                possiblyBadConnection = true;
                LOGGER.error("Failed to close statement", ex);
            }
        }
    }

    public void processBatchRequest(String sql, SelfDependentStatementInitializer... initializers) throws DaoException
    {
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        try
        {
            connection.setAutoCommit(false);
            statementContainer.wrap(connection.prepareStatement(sql));

            for (SelfDependentStatementInitializer initializer : initializers)
            {
                initializer.init(statementContainer);
                statementContainer.addBatch();
            }

            LOGGER.debug("batch SQL request is: " + statementContainer);
            statementContainer.executeBatch();
            connection.commit();

        }
        catch (SQLException ex)
        {
            try
            {
                connection.rollback();
            }
            catch (SQLException rollbackEx)
            {
                possiblyBadConnection = true;
                LOGGER.error("failed to rollback operation", rollbackEx);
            }
            throw new DaoException("Failed to execute  request", ex);
        }
        finally
        {
            try
            {
                connection.setAutoCommit(true);
            }
            catch (SQLException ex)
            {
                possiblyBadConnection = true;
                LOGGER.error("Failed to set autocommit true", ex);
            }

            try
            {
                if (statementContainer.getInnerStatement() != null)
                {
                    statementContainer.close();
                }
            }
            catch (SQLException ex)
            {
                possiblyBadConnection = true;
                LOGGER.error("Failed to close statement", ex);
            }
        }
    }

    public boolean processDeleteRequest(Integer id, String sql) throws DaoException
    {
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        boolean result;

        try
        {
            statementContainer.wrap(connection.prepareStatement(sql));
            statementContainer.setNextInt(id);
            result = statementContainer.executeUpdate() > 0;

            return result;
        }
        catch (SQLException ex)
        {
            throw new DaoException("Failed to execute request", ex);
        }
        finally
        {
            try
            {
                if (statementContainer.getInnerStatement() != null)
                {
                    statementContainer.close();
                }
            }
            catch (SQLException ex)
            {
                possiblyBadConnection = true;
                LOGGER.error("Failed to close statement", ex);
            }
        }
    }

    public <T extends Entity> boolean processDeleteRequest(T instance, String sql,
            EntityDependentStatementInitializer<T> eInitializer) throws DaoException
    {
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        boolean result;

        try
        {
            statementContainer.wrap(connection.prepareStatement(sql));
            eInitializer.init(instance, statementContainer);
            result = statementContainer.executeUpdate() > 0;

            return result;
        }
        catch (SQLException ex)
        {
            throw new DaoException("Failed to execute request", ex);
        }
        finally
        {
            try
            {
                if (statementContainer.getInnerStatement() != null)
                {
                    statementContainer.close();
                }
            }
            catch (SQLException ex)
            {
                possiblyBadConnection = true;
                LOGGER.error("Failed to close statement", ex);
            }
        }
    }

    public void processCustomRequest(String sql, SelfDependentStatementInitializer... sdsInitializers) throws DaoException
    {
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();

        try
        {
            statementContainer.wrap(connection.prepareStatement(sql));
            for (SelfDependentStatementInitializer initializer : sdsInitializers)
            {
                initializer.init(statementContainer);
            }

            statementContainer.execute();
        }
        catch (SQLException ex)
        {
            throw new DaoException("Failed to execute custom SQL request", ex);
        }
        finally
        {
            try
            {
                if (statementContainer.getInnerStatement() != null)
                {
                    statementContainer.close();
                }
            }
            catch (SQLException ex)
            {
                possiblyBadConnection = true;
                LOGGER.error("Failed to close statement", ex);
            }
        }
    }

    public boolean processBooleanResultRequest(String sql, SelfDependentStatementInitializer initializer) throws DaoException
    {
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();

        try
        {
            statementContainer.wrap(connection.prepareStatement(sql));
            initializer.init(statementContainer);
            ResultSet queryResult = statementContainer.executeQuery();
            queryResult.next();
            return queryResult.getBoolean(1);
        }
        catch (SQLException ex)
        {
            throw new DaoException("Failed to execute SQL request", ex);
        }
        finally
        {
            try
            {
                if (statementContainer.getInnerStatement() != null)
                {
                    statementContainer.close();
                }
            }
            catch (SQLException ex)
            {
                possiblyBadConnection = true;
                LOGGER.error("Failed to close statement", ex);
            }
        }
    }

    public List<List<Map<String, String>>> processCustomSelectRequest(
            String sql,
            SelfDependentStatementInitializer sInitializer) throws DaoException
    {

        List<List<Map<String, String>>> globalResultData = new ArrayList<>();
        PreparedStatementContainer statementContainer = new PreparedStatementContainer();
        try
        {
            statementContainer.wrap(connection.prepareStatement(sql));
            if (sInitializer != null)
            {
                sInitializer.init(statementContainer);
            }
            LOGGER.debug("filled sql: " + statementContainer);
            boolean isResultSet = statementContainer.execute();

            // parsing result sets we got as a result of our request(s) execution
            // each result set is represented by a separate list (currentResultSetData)
            // each row from single result set is being held as Map<String, String>,
            // where key is column label and value is cell content
            while (true)
            {
                if (isResultSet)
                {
                    List<Map<String, String>> currentResultSetData = new ArrayList<>();
                    try (ResultSet resultSet = statementContainer.getResultSet())
                    {

                        while (resultSet.next())
                        {
                            Map<String, String> rowData = new HashMap<>();
                            ResultSetMetaData metaData = resultSet.getMetaData();
                            for (int i = 1 ; i <= metaData.getColumnCount() ; i++)
                            {
                                String name = metaData.getColumnLabel(i);
                                String value = resultSet.getString(i);
                                rowData.put(name, value);
                            }

                            currentResultSetData.add(rowData);
                        }
                    }

                    globalResultData.add(currentResultSetData);
                }
                else
                {
                    if (statementContainer.getUpdateCount() == -1)
                    {
                        break;
                    }
                }

                isResultSet = statementContainer.getMoreResults();
            }

            LOGGER.debug("rows read: " + globalResultData.size());
            return globalResultData;
        }
        catch (SQLException ex)
        {
            throw new DaoException("Failed to execute request", ex);
        }
        finally
        {
            try
            {
                if (statementContainer.getInnerStatement() != null)
                {
                    statementContainer.close();
                }
            }
            catch (SQLException ex)
            {
                possiblyBadConnection = true;
                LOGGER.error("Failed to close prepared statement", ex);
            }
        }
    }
}
