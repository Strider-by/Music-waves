package by.musicwaves.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import by.musicwaves.dao.util.PreparedStatementContainer;
import by.musicwaves.dao.util.SQLRequestHandler;
import by.musicwaves.entity.AudioTrack;

public class AudioTrackDao implements Dao<Integer, AudioTrack>
{
    public final static String SQL_SELECT_ALL
            = "SELECT * FROM music_waves.audio_tracks";
    public final static String SQL_CREATE_INSTANCE
            = "INSERT INTO music_waves.audio_tracks (name, genre, album, file, active, track_number) VALUES (?, ?, ?, ?, ?, ?)";
    public final static String SQL_UPDATE_INSTANCE
            = "UPDATE music_waves.audio_tracks SET name = ?, genre = ?, album = ?, file = ?, active = ?, track_number = ?";
    public final static String SQL_DELETE_INSTANCE
            = "DELETE FROM music_waves.audio_tracks";

    public final static String SQL_CREATE_TRACK_GENERATING_TRACK_NUMBER
            = "INSERT INTO music_waves.audio_tracks (genre, album, name, active, track_number) \n"
            + "SELECT ?, ?, ?, ?, COUNT(id) + 1 FROM music_waves.audio_tracks WHERE music_waves.audio_tracks.album = ?";

    public final static String SQL_REARRANGE_ALBUM_TRACKS_NUMBERS
            = "SET @row_base = 0; "
            + "UPDATE audio_tracks "
            + "SET track_number = (@row_base := @row_base + 1) "
            + "WHERE album = ?"
            + "ORDER BY track_number; ";

    public final static String SQL_SHIFT_TRACK_NUMBER_UP_OLD
            = "SELECT @current_number := track_number, @current_album := album "
            + "FROM audio_tracks "
            + "WHERE id = ?; "
            + "UPDATE audio_tracks "
            + "SET track_number = IF(track_number = @current_number - 1, @current_number, @current_number - 1) "
            + "WHERE @current_number > 1 AND track_number IN (@current_number - 1, @current_number) AND album = @current_album;";

    public final static String SQL_SHIFT_TRACK_NUMBER_UP
            = "SELECT @current_number := track_number, @current_album := album FROM audio_tracks WHERE id = ?; "
            + "UPDATE audio_tracks "
            + "	SET track_number = "
            + " (CASE "
            + "     WHEN track_number = @current_number - 1 THEN @current_number "
            + "     ELSE @current_number - 1 "
            + " END) "
            + "WHERE @current_number > 1 AND track_number IN (@current_number - 1, @current_number) AND album = @current_album;";

    public final static String SQL_SHIFT_TRACK_NUMBER_DOWN
            = "SELECT @current_number := track_number, @current_album := album "
            + "FROM audio_tracks "
            + "WHERE id = ?; "
            + "SELECT @tracks_in_album := COUNT(id) "
            + "FROM audio_tracks "
            + "WHERE album = @current_album; "
            + "UPDATE audio_tracks "
            + "SET track_number = IF(track_number = @current_number, @current_number + 1, @current_number) "
            + "WHERE @current_number < @tracks_in_album "
            + "AND track_number IN (@current_number, @current_number + 1) "
            + "AND album = @current_album;";

    public final static String SQL_POSTFIX_SELECT_BY_ID
            = " WHERE music_waves.audio_tracks.id = ?";
    public final static String SQL_POSTFIX_SELECT_BY_NAME
            = " WHERE music_waves.audio_tracks.name = ?";
    public final static String SQL_POSTFIX_SELECT_BY_ALBUM_ID
            = " WHERE music_waves.audio_tracks.album = ?";

    private final SQLRequestHandler requestHandler = new SQLRequestHandler();

    @Override
    public List<AudioTrack> findAll() throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL,
                AudioTrack::new,
                this::initAudioTrack,
                null);
    }

    @Override
    public AudioTrack findById(Integer id) throws DaoException
    {
        return requestHandler.processSingleResultSelectRequest(
                SQL_SELECT_ALL + SQL_POSTFIX_SELECT_BY_ID,
                AudioTrack::new,
                this::initAudioTrack,
                (statement) -> statement.setNextInt(id));
    }

    @Override
    public Integer create(AudioTrack instance) throws DaoException
    {
        return requestHandler.processCreateRequest(
                instance,
                SQL_CREATE_INSTANCE,
                this::initCreationStatement);
    }

    public Integer createGeneratingTrackNumber(AudioTrack instance) throws DaoException
    {
        return requestHandler.processCreateRequest(instance,
                SQL_CREATE_TRACK_GENERATING_TRACK_NUMBER,
                (inst, statement) ->
        {
            statement.setNextInt(inst.getGenreId());
            statement.setNextInt(inst.getAlbumId());
            statement.setNextString(inst.getName());
            statement.setNextBoolean(inst.isActive());
            statement.setNextInt(inst.getAlbumId()); // setting album id for condition parameter
        });
    }

    @Override
    public AudioTrack update(AudioTrack instance) throws DaoException
    {
        AudioTrack responce = requestHandler.processUpdateRequest(
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
    public boolean delete(AudioTrack instance) throws DaoException
    {
        boolean result = requestHandler.processDeleteRequest(
                instance,
                SQL_DELETE_INSTANCE + SQL_POSTFIX_SELECT_BY_ID,
                (inst, statement) -> statement.setNextInt(inst.getId()));

        return result;
    }

    public List<AudioTrack> findByAlbumId(int id) throws DaoException
    {
        return requestHandler.processMultipleResultsSelectRequest(
                SQL_SELECT_ALL + SQL_POSTFIX_SELECT_BY_ALBUM_ID,
                AudioTrack::new,
                this::initAudioTrack,
                (statement) -> statement.setNextInt(id));
    }

    public void update(AudioTrack[] instances) throws DaoException
    {
        requestHandler.processMultipleUpdateRequest(
                instances,
                SQL_UPDATE_INSTANCE + SQL_POSTFIX_SELECT_BY_ID,
                this::initCreationStatement,
                (instance, statement) -> statement.setNextInt(((AudioTrack) instance).getId()));
    }

    public void rearrangeAlbumTracksNumbers(int albumId) throws DaoException
    {
        requestHandler.processCustomRequest(SQL_REARRANGE_ALBUM_TRACKS_NUMBERS,
                statement -> statement.setNextInt(albumId));
    }

    public void shiftTrackNumberUp(int trackId) throws DaoException
    {
        requestHandler.processCustomRequest(
                SQL_SHIFT_TRACK_NUMBER_UP,
                statement -> statement.setNextInt(trackId));
    }

    public void shiftTrackNumberDown(int trackId) throws DaoException
    {
        requestHandler.processCustomRequest(
                        SQL_SHIFT_TRACK_NUMBER_DOWN,
                        statement -> statement.setNextInt(trackId));
    }
    
    private void initAudioTrack(AudioTrack track, ResultSet resultSet) throws SQLException
    {
        track.setId(resultSet.getInt("id"));
        track.setGenreId(resultSet.getInt("genre"));
        track.setAlbumId(resultSet.getInt("album"));
        track.setName(resultSet.getString("name"));
        track.setActive(resultSet.getBoolean("active"));
        track.setFileName(resultSet.getString("file"));
        track.setTrackNumber(resultSet.getInt("track_number"));
    }

    private void initCreationStatement(AudioTrack track, PreparedStatementContainer statementCont) throws SQLException
    {
        statementCont.setNextString(track.getName());
        statementCont.setNextInt(track.getGenreId());
        statementCont.setNextInt(track.getAlbumId());
        statementCont.setNextString(track.getFileName());
        statementCont.setNextBoolean(track.isActive());
        statementCont.setNextInt(track.getTrackNumber());
    }

    @Override
    public void close()
    {
        requestHandler.close();
    }

}
