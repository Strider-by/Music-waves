package by.musicwaves.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum ConnectionPool
{
    INSTANCE;

    private final LinkedBlockingQueue<Connection> freeConnections = new LinkedBlockingQueue<>();
    private final LinkedBlockingQueue<Connection> connectionsInUse = new LinkedBlockingQueue<>();
    private final ConnectionWorker connectionWorker = new ConnectionWorker();
    private final int poolInitialSize = 50;

    private final Logger LOGGER = LogManager.getLogger();

    
    {
        try
        {
            connectionWorker.init();
        } catch (SQLException ex)
        {
            LOGGER.error("Connection initialization has failed to perform", ex);
            throw new RuntimeException();
        }

        initPool();
    }

    public void initPool()
    {
        try
        {
            for (int i = 0 ; i < poolInitialSize ; i++)
            {
                freeConnections.add(connectionWorker.openConnection());
            }
        } catch (SQLException ex)
        {
            LOGGER.error("Connection pool initialization operation, connection worker has failed to open new connection", ex);
            throw new RuntimeException();
        }
    }

    public Connection getConnection()
    {
        try
        {
            Connection con = freeConnections.take();
            connectionsInUse.add(con);
            return con;
        } catch (InterruptedException ex)
        {
            LOGGER.error("Get connection from pool operation, error occured", ex);
            throw new RuntimeException();
        }
    }

    public void returnConnection(Connection connection)
    {
        if (connectionsInUse.contains(connection))
        {
            connectionsInUse.remove(connection);
            try
            {
                if (!connection.isClosed())
                {
                    connection.setAutoCommit(true);
                    freeConnections.add(connection);
                } else
                {
                    // if somehow we get back closed connection - let us create 
                    // new connection instead corrupted one
                    freeConnections.add(connectionWorker.openConnection());
                }
            } catch (SQLException ex)
            {
                LOGGER.error("Connection pool error, return connection operation has failed", ex);
                throw new RuntimeException();
            }
        }
    }

    public void returnBadConnection(Connection connection)
    {
        if (connectionsInUse.contains(connection))
        {
            connectionsInUse.remove(connection);
            try
            {
                freeConnections.add(connectionWorker.openConnection());
            } catch (SQLException ex)
            {
                LOGGER.error("Connection pool error, return connection operation has failed", ex);
                throw new RuntimeException();
            }
        }
    }

    public void closeAllConnections()
    {
        for (Connection connection : freeConnections)
        {
            try
            {
                if (connection != null && !connection.isClosed())
                {
                    connection.close();
                }
            } catch (SQLException ex)
            {
                LOGGER.error("Connection closing operation, error occured", ex);
            }
        }
    }

}
