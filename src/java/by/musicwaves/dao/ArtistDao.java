package by.musicwaves.dao;

import by.musicwaves.dao.util.PreparedStatementContainer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import by.musicwaves.dao.util.SQLRequestHandler;
import by.musicwaves.entity.Artist;

public class ArtistDao implements Dao<Integer, Artist>
{
    public final static String SQL_SELECT_ALL
            = "SELECT * FROM artists";

    public final static String SQL_CREATE_INSTANCE
            = "INSERT INTO artists (name, active, image) VALUES (?, ?, ?)";
    public final static String SQL_UPDATE_INSTANCE
            = "UPDATE artists SET name = ?, active = ?, image = ?";
    public final static String SQL_DELETE_INSTANCE
            = "DELETE FROM artists";

    public final static String SQL_POSTFIX_SELECT_BY_ID
            = " WHERE artists.id = ?";
    public final static String SQL_POSTFIX_SELECT_BY_NAME
            = " WHERE artists.name = ?";
    public final static String SQL_POSTFIX_SELECT_BY_NAME_PATTERN
            = " WHERE artists.name LIKE ?";

    public final static String SQL_POSTFIX_AND_CONDITION_ACTIVITY
            = " AND artists.active = ?";
    public final static String SQL_POSTFIX_LIMIT
            = " LIMIT ?";
    public final static String SQL_POSTFIX_LIMIT_OFFSET
            = " LIMIT ? OFFSET ?";

    // somehow without artists.active in SELECT part, "WHERE artists.active = TRUE" does not work
    // though the very same SQL works perfectly while being used in Workbench.
    public final static String SQL_FIND_FAVOURITE_ARTISTS
            = "SELECT artists.id, artists.name, artists.image, artists.active "
            + "FROM artists LEFT JOIN favorite_artists ON artists.id = favorite_artists.artist_id "
            + "WHERE artists.active = TRUE and favorite_artists.user_id = ? AND artists.name LIKE ? "
            + "ORDER BY artists.name LIMIT ? OFFSET ?;";
    
    private final SQLRequestHandler requestHandler = new SQLRequestHandler();

    @Override
    public List<Artist> findAll() throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL,
                Artist::new,
                this::initArtist,
                null);
    }

    @Override
    public Artist findById(Integer id) throws DaoException
    {
        return requestHandler.processSingleResultSelectRequest(
                SQL_SELECT_ALL + SQL_POSTFIX_SELECT_BY_ID,
                Artist::new,
                this::initArtist,
                (statement) -> statement.setNextInt(id));
    }

    @Override
    public Integer create(Artist instance) throws DaoException
    {
        return requestHandler.processCreateRequest(
                instance,
                SQL_CREATE_INSTANCE,
                this::initCreationStatement);
    }

    @Override
    public Artist update(Artist instance) throws DaoException
    {
        Artist responce = requestHandler.processUpdateRequest(
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
    public boolean delete(Artist instance) throws DaoException
    {
        boolean result = requestHandler.processDeleteRequest(
                instance,
                SQL_DELETE_INSTANCE + SQL_POSTFIX_SELECT_BY_ID,
                (inst, statement) -> statement.setNextInt(inst.getId()));

        return result;
    }

    public List<Artist> findByNamePattern(String namePattern, int limit, int offset) throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL
                + SQL_POSTFIX_SELECT_BY_NAME_PATTERN
                + SQL_POSTFIX_LIMIT_OFFSET,
                Artist::new,
                this::initArtist,
                (statement) ->
        {
            statement.setNextString(namePattern);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<Artist> findByNamePattern(String namePattern, boolean activityState, int limit, int offset) throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL
                + SQL_POSTFIX_SELECT_BY_NAME_PATTERN
                + SQL_POSTFIX_AND_CONDITION_ACTIVITY
                + SQL_POSTFIX_LIMIT_OFFSET,
                Artist::new,
                this::initArtist,
                (statement) ->
        {
            statement.setNextString(namePattern);
            statement.setNextBoolean(activityState);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<Artist> findByName(String name, int limit, int offset) throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL
                + SQL_POSTFIX_SELECT_BY_NAME
                + SQL_POSTFIX_LIMIT_OFFSET,
                Artist::new,
                this::initArtist,
                (statement) ->
        {
            statement.setNextString(name);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<Artist> findByName(String name, boolean activityState, int limit, int offset) throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL
                + SQL_POSTFIX_SELECT_BY_NAME
                + SQL_POSTFIX_AND_CONDITION_ACTIVITY
                + SQL_POSTFIX_LIMIT_OFFSET,
                Artist::new,
                this::initArtist,
                (statement) ->
        {
            statement.setNextString(name);
            statement.setNextBoolean(activityState);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<Artist> findFavouriteArtists(int userId, String namePattern, int limit, int offset) throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_FIND_FAVOURITE_ARTISTS,
                Artist::new,
                this::initArtist,
                (statement) ->
        {
            statement.setNextInt(userId);
            statement.setNextString(namePattern);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    private void initArtist(Artist artist, ResultSet resultSet) throws SQLException
    {
        artist.setId(resultSet.getInt("id"));
        artist.setName(resultSet.getString("name"));
        artist.setActive(resultSet.getBoolean("active"));
        artist.setImageFileName(resultSet.getString("image"));
    }
    
    public void initCreationStatement(Artist artist, PreparedStatementContainer statement) throws SQLException
    {
        statement.setNextString(artist.getName());
        statement.setNextBoolean(artist.isActive());
        statement.setNextString(artist.getImageFileName());
    }

    @Override
    public void close()
    {
        requestHandler.close();
    }
}
