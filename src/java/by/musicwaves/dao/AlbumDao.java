package by.musicwaves.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.dao.util.SQLRequestHandler;
import by.musicwaves.entity.Album;

public class AlbumDao implements Dao<Integer, Album>
{
    public final static String SQL_SELECT_ALL
            = "SELECT * FROM albums";
    public final static String SQL_CREATE_INSTANCE
            = "INSERT INTO albums (name, year, artist, image, active) VALUES (?, ?, ?, ?, ?)";
    public final static String SQL_UPDATE_INSTANCE
            = "UPDATE albums SET name = ?, year = ?, artist = ?, image = ?, active = ?";
    public final static String SQL_DELETE_INSTANCE
            = "DELETE FROM music_waves.albums";

    public final static String SQL_POSTFIX_SELECT_BY_ID
            = " WHERE albums.id = ?";
    public final static String SQL_POSTFIX_SELECT_BY_NAME
            = " WHERE albums.name = ?";
    public final static String SQL_POSTFIX_SELECT_BY_NAME_PATTERN
            = " WHERE albums.name LIKE ?";

    public final static String SQL_NAME_LIKE
            = "albums.name LIKE ?";
    public final static String SQL_ACTIVITY_EQUALS
            = "albums.active = ?";
    public final static String SQL_ARTIST_ID_EQUALS
            = "albums.artist = ?";
    public final static String SQL_YEAR_LIKE
            = "albums.year LIKE ?";

    private final SQLRequestHandler requestHandler = new SQLRequestHandler();

    @Override
    public List<Album> findAll() throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL,
                Album::new,
                this::initAlbum,
                null);
    }

    @Override
    public Album findById(Integer id) throws DaoException
    {
        return requestHandler.processSingleResultSelectRequest(
                SQL_SELECT_ALL + SQL_POSTFIX_SELECT_BY_ID,
                Album::new,
                this::initAlbum,
                (statement) -> statement.setNextInt(id));
    }

    @Override
    public Integer create(Album instance) throws DaoException
    {
        return requestHandler.processCreateRequest(
                instance,
                SQL_CREATE_INSTANCE,
                this::initCreationStatement);
    }

    @Override
    public Album update(Album instance) throws DaoException
    {
        Album responce = requestHandler.processUpdateRequest(
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
    public boolean delete(Album instance) throws DaoException
    {
        boolean result = requestHandler.processDeleteRequest(
                instance,
                SQL_DELETE_INSTANCE + SQL_POSTFIX_SELECT_BY_ID,
                (inst, statement) -> statement.setNextInt(inst.getId()));

        return result;
    }

    public List<Album> findByNamePatternAndYearPattern(String namePattern, String year, int limit, int offset) throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL
                + SQL_POSTFIX_SELECT_BY_NAME_PATTERN + SQL_AND
                + SQL_YEAR_LIKE
                + SQL_LIMIT_OFFSET,
                Album::new,
                this::initAlbum,
                (statement) ->
        {
            statement.setNextString(namePattern);
            statement.setNextString(year);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<Album> findByArtistIdAndAlbumNamePatternAndYearPattern(int artistId, String namePattern, String yearPattern, int limit, int offset) throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL + SQL_WHERE
                + SQL_ARTIST_ID_EQUALS + SQL_AND
                + SQL_NAME_LIKE + SQL_AND
                + SQL_YEAR_LIKE
                + SQL_LIMIT_OFFSET,
                Album::new,
                this::initAlbum,
                (statement) ->
        {
            statement.setNextInt(artistId);
            statement.setNextString(namePattern);
            statement.setNextString(yearPattern);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<Album> findByArtistIdAndAlbumNamePattern(int artistId, String namePattern, int limit, int offset) throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL + SQL_WHERE
                + SQL_ARTIST_ID_EQUALS + SQL_AND
                + SQL_NAME_LIKE
                + SQL_LIMIT_OFFSET,
                Album::new,
                this::initAlbum,
                (statement) ->
        {
            statement.setNextInt(artistId);
            statement.setNextString(namePattern);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });

    }

    private void initAlbum(Album album, ResultSet resultSet) throws SQLException
    {
        album.setId(resultSet.getInt("id"));
        album.setName(resultSet.getString("name"));
        album.setYear(resultSet.getInt("year"));
        album.setArtist(resultSet.getInt("artist"));
        album.setImageFileName(resultSet.getString("image"));
        album.setActive(resultSet.getBoolean("active"));
    }

    private void initCreationStatement(Album album, PreparedStatementContainer statement) throws SQLException
    {
        statement.setNextString(album.getName());
        statement.setNextInt(album.getYear());
        statement.setNextInt(album.getArtist());
        statement.setNextString(album.getImageFileName());
        statement.setNextBoolean(album.isActive());
    }

    @Override
    public void close()
    {
        requestHandler.close();
    }
}
