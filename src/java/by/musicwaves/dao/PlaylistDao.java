package by.musicwaves.dao;

import by.musicwaves.dao.util.PreparedStatementContainer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import by.musicwaves.dao.util.SQLRequestHandler;
import by.musicwaves.dao.util.SelfDependentStatementInitializer;
import by.musicwaves.entity.Playlist;

public class PlaylistDao implements Dao<Integer, Playlist>
{
    public final static String SQL_SELECT_ALL
            = "SELECT * FROM playlists";
    public final static String SQL_CREATE_INSTANCE
            = "INSERT INTO playlists (name, user) VALUES (?, ?)";
    public final static String SQL_UPDATE_INSTANCE
            = "UPDATE playlists SET name = ?, user = ?";
    public final static String SQL_DELETE_INSTANCE
            = "DELETE FROM playlists";

    public final static String SQL_POSTFIX_SELECT_BY_ID
            = " WHERE playlists.id = ?";
    public final static String SQL_POSTFIX_SELECT_BY_USER_ID
            = " WHERE playlists.user = ?";
    public final static String SQL_POSTFIX_SELECT_BY_NAME
            = " WHERE playlists.name = ?";
    public final static String SQL_ORDER_BY_ID_ASC
            = " ORDER BY playlists.id";
    public final static String SQL_ORDER_BY_NAME_ASC
            = " ORDER BY playlists.name";

    public final static String SQL_RENAME_PLAYLIST
            = "UPDATE playlists SET name = ? WHERE user = ? AND id = ?";

    public final static String SQL_DELETE_PLAYLIST_BY_USER_AND_PLAYLIST_ID
            = "DELETE FROM playlists WHERE user = ? AND id = ?";

    private final SQLRequestHandler requestHandler = new SQLRequestHandler();

    @Override
    public List<Playlist> findAll() throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL,
                Playlist::new,
                this::initPlaylist,
                null);
    }

    @Override
    public Playlist findById(Integer id) throws DaoException
    {
        return requestHandler.processSingleResultSelectRequest(
                SQL_SELECT_ALL + SQL_POSTFIX_SELECT_BY_ID,
                Playlist::new,
                this::initPlaylist,
                (statement) -> statement.setNextInt(id));
    }

    public List<Playlist> findByUserId(Integer id) throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL + SQL_POSTFIX_SELECT_BY_USER_ID + SQL_ORDER_BY_NAME_ASC,
                Playlist::new,
                this::initPlaylist,
                (statement) -> statement.setNextInt(id));
    }

    @Override
    public Integer create(Playlist instance) throws DaoException
    {
        return requestHandler.processCreateRequest(
                instance,
                SQL_CREATE_INSTANCE,
                this::initCreationStatement);
    }

    @Override
    public Playlist update(Playlist instance) throws DaoException
    {
        Playlist responce = requestHandler.processUpdateRequest(
                instance,
                SQL_UPDATE_INSTANCE + SQL_POSTFIX_SELECT_BY_ID,
                this::initCreationStatement,
                (statement) -> statement.setNextInt(instance.getId()));
        return responce;
    }

    public boolean rename(int userId, int playlistId, String playlistName) throws DaoException
    {
        int rowsAffected = requestHandler.processCustomUpdateRequest(
                SQL_RENAME_PLAYLIST,
                (statement) ->
        {
            statement.setNextString(playlistName);
            statement.setNextInt(userId);
            statement.setNextInt(playlistId);
        });

        return rowsAffected == 1;
    }

    @Override
    public boolean delete(Integer id) throws DaoException
    {
        boolean result = requestHandler.processDeleteRequest(id, SQL_DELETE_INSTANCE + SQL_POSTFIX_SELECT_BY_ID);

        return result;
    }

    @Override
    public boolean delete(Playlist instance) throws DaoException
    {
        boolean result = requestHandler.processDeleteRequest(
                instance,
                SQL_DELETE_INSTANCE + SQL_POSTFIX_SELECT_BY_ID,
                (inst, statement) -> statement.setNextInt(inst.getId()));

        return result;
    }

    public void delete(int userId, int... playlistsId) throws DaoException
    {

        SelfDependentStatementInitializer[] initializers = new SelfDependentStatementInitializer[playlistsId.length];
        for (int i = 0 ; i < initializers.length ; i++)
        {
            int playlistId = playlistsId[i];
            initializers[i] = (statement) ->
            {
                statement.setNextInt(userId);
                statement.setNextInt(playlistId);
            };
        }

        requestHandler.processBatchRequest(
                SQL_DELETE_PLAYLIST_BY_USER_AND_PLAYLIST_ID,
                initializers);
    }

    private void initPlaylist(Playlist playlist, ResultSet resultSet) throws SQLException
    {
        playlist.setId(resultSet.getInt("id"));
        playlist.setName(resultSet.getString("name"));
        playlist.setUserID(resultSet.getInt("user"));
    }

    private void initCreationStatement(Playlist playlist, PreparedStatementContainer statement) throws SQLException
    {
        statement.setNextString(playlist.getName());
        statement.setNextInt(playlist.getUserID());
    }

    @Override
    public void close()
    {
        requestHandler.close();
    }
}
