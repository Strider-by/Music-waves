package by.musicwaves.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.dao.util.SQLRequestHandler;
import by.musicwaves.entity.Genre;

public class GenreDao implements Dao<Integer, Genre>
{
    public final static String SQL_SELECT_ALL
            = "SELECT * FROM genres";
    public final static String SQL_CREATE_INSTANCE
            = "INSERT INTO genres (name, active) VALUES (?, ?)";
    public final static String SQL_UPDATE_INSTANCE
            = "UPDATE genres SET name = ?, active = ?";
    public final static String SQL_DELETE_INSTANCE
            = "DELETE FROM genres";

    public final static String SQL_POSTFIX_SELECT_BY_ID
            = " WHERE genres.id = ?";
    public final static String SQL_POSTFIX_SELECT_BY_NAME
            = " WHERE genres.name = ?";
    public final static String SQL_POSTFIX_SELECT_BY_NAME_PATTERN
            = " WHERE genres.name LIKE ?";

    public final static String SQL_POSTFIX_CONDITION_ACTIVITY
            = " AND genres.active = ?";

    private final SQLRequestHandler requestHandler = new SQLRequestHandler();

    @Override
    public List<Genre> findAll() throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL,
                Genre::new,
                this::initGenre,
                null);
    }

    @Override
    public Genre findById(Integer id) throws DaoException
    {
        return requestHandler.processSingleResultSelectRequest(
                SQL_SELECT_ALL + SQL_POSTFIX_SELECT_BY_ID,
                Genre::new,
                this::initGenre,
                (statement) -> statement.setNextInt(id));
    }

    @Override
    public Integer create(Genre instance) throws DaoException
    {
        return requestHandler.processCreateRequest(
                instance,
                SQL_CREATE_INSTANCE,
                this::initCreationStatement);
    }

    @Override
    public Genre update(Genre instance) throws DaoException
    {
        Genre responce = requestHandler.processUpdateRequest(
                instance,
                SQL_UPDATE_INSTANCE + SQL_POSTFIX_SELECT_BY_ID,
                this::initCreationStatement,
                (statement) -> statement.setNextInt(instance.getId()));
        return responce;
    }

    @Override
    public boolean delete(Integer id) throws DaoException
    {
        boolean result = requestHandler.processDeleteRequest(id, SQL_DELETE_INSTANCE + SQL_POSTFIX_SELECT_BY_ID);

        return result;
    }

    @Override
    public boolean delete(Genre instance) throws DaoException
    {
        boolean result = requestHandler.processDeleteRequest(
                instance,
                SQL_DELETE_INSTANCE + SQL_POSTFIX_SELECT_BY_ID,
                (inst, statement) -> statement.setNextInt(inst.getId()));

        return result;
    }

    public List<Genre> findByNamePattern(String namePattern, int limit, int offset) throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL
                + SQL_POSTFIX_SELECT_BY_NAME_PATTERN
                + SQL_LIMIT_OFFSET,
                Genre::new,
                this::initGenre,
                (statement) ->
        {
            statement.setNextString(namePattern);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<Genre> findByNamePattern(String namePattern, boolean activityState, int limit, int offset) throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL
                + SQL_POSTFIX_SELECT_BY_NAME_PATTERN
                + SQL_POSTFIX_CONDITION_ACTIVITY
                + SQL_LIMIT_OFFSET,
                Genre::new,
                this::initGenre,
                (statement) ->
        {
            statement.setNextString(namePattern);
            statement.setNextBoolean(activityState);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<Genre> findByName(String name, int limit, int offset) throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL
                + SQL_POSTFIX_SELECT_BY_NAME
                + SQL_LIMIT_OFFSET,
                Genre::new,
                this::initGenre,
                (statement) ->
        {
            statement.setNextString(name);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<Genre> findByName(String name, boolean activityState, int limit, int offset) throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL
                + SQL_POSTFIX_SELECT_BY_NAME
                + SQL_POSTFIX_CONDITION_ACTIVITY
                + SQL_LIMIT_OFFSET,
                Genre::new,
                this::initGenre,
                (statement) ->
        {
            statement.setNextString(name);
            statement.setNextBoolean(activityState);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    private void initGenre(Genre genre, ResultSet resultSet) throws SQLException
    {
        genre.setId(resultSet.getInt("id"));
        genre.setName(resultSet.getString("name"));
        genre.setActive(resultSet.getBoolean("active"));
    }

    private void initCreationStatement(Genre genre, PreparedStatementContainer statement) throws SQLException
    {
        statement.setNextString(genre.getName());
        statement.setNextBoolean(genre.isActive());
    }

    @Override
    public void close()
    {
        requestHandler.close();
    }
}
