package by.musicwaves.dao;

import java.util.List;
import by.musicwaves.dao.util.SQLRequestHandler;
import by.musicwaves.dao.util.SelfDependentStatementInitializer;
import java.util.Map;

public class CrossEntityDao implements AutoCloseable
{
    public final static String SQL__MUSIC_SEARCH__GET_SEARCH_RESULTS_QUANTITY
            = "SET @search_string := ?; "
            + "SELECT "
            + "(SELECT COUNT(artists.id) FROM artists "
            + "WHERE artists.name LIKE @search_string AND artists.active = TRUE) AS artists_count, "
            + "(SELECT COUNT(albums.id) FROM albums LEFT JOIN artists ON albums.artist = artists.id "
            + "WHERE albums.name LIKE @search_string AND artists.active = TRUE AND albums.active = TRUE) AS albums_count, "
            + "(SELECT COUNT(audio_tracks.id) FROM audio_tracks LEFT JOIN albums ON audio_tracks.album = albums.id LEFT JOIN artists ON albums.artist = artists.id "
            + "WHERE audio_tracks.name LIKE @search_string AND artists.active = TRUE AND albums.active = TRUE AND audio_tracks.active = TRUE) AS tracks_count; ";

    public final static String SQL__MUSIC_SEARCH__GET_ARTISTS_LIST
            = "SET @user_id := ?; "
            + "SELECT artists.id AS artist_id, artists.name AS artist_name, artists.image AS artist_image, "
            + "(SELECT COUNT(id) FROM albums WHERE albums.artist = artist_id AND active = TRUE) AS albums_count_artist_has, "
            + "((SELECT COUNT(id) FROM favorite_artists WHERE artists.id = favorite_artists.artist_id AND user_id = @user_id) > 0) AS favourite"
            + " FROM artists WHERE artists.name LIKE @search_string AND active = TRUE ORDER BY artist_name LIMIT ? OFFSET ?; ";

    public final static String SQL__MUSIC_SEARCH__GET_ALBUMS_LIST
            = "SET @user_id := ?; "
            + "SELECT artists.id AS artist_id, artists.name AS artist_name, artists.image AS artist_image, "
            + "albums.id AS album_id, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year, "
            + "(SELECT COUNT(id) FROM audio_tracks WHERE audio_tracks.album = album_id AND audio_tracks.active = TRUE) AS tracks_count_album_has, "
            + "((SELECT COUNT(id) FROM favorite_albums WHERE albums.id = favorite_albums.album_id AND user_id = @user_id) > 0) AS favourite "
            + "FROM albums LEFT JOIN artists ON albums.artist = artists.id WHERE albums.name LIKE @search_string AND artists.active = TRUE AND albums.active = TRUE "
            + "ORDER BY album_name, artist_name, album_year LIMIT ? OFFSET ?;";

    public final static String SQL__MUSIC_SEARCH__GET_TRACKS_LIST
            = "SET @user_id := ?; "
            + "SELECT artists.id AS artist_id, artists.name AS artist_name, artists.image AS artist_image, "
            + "albums.id AS album_id, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year, "
            + "audio_tracks.id AS track_id, audio_tracks.name AS track_name, audio_tracks.file AS track_file, audio_tracks.track_number AS track_number, "
            + "(SELECT CASE WHEN genres.active = TRUE THEN genres.name ELSE NULL END) AS track_genre, "
            + "((SELECT COUNT(id) FROM favorite_tracks WHERE audio_tracks.id = favorite_tracks.track_id AND user_id = @user_id) > 0) AS favourite "
            + "FROM audio_tracks LEFT JOIN albums ON audio_tracks.album = albums.id LEFT JOIN artists ON albums.artist = artists.id LEFT JOIN genres ON audio_tracks.genre = genres.id"
            + " WHERE audio_tracks.name LIKE @search_string AND audio_tracks.active = TRUE AND artists.active = TRUE AND albums.active = TRUE "
            + " ORDER BY track_name, album_name, artist_name, album_year LIMIT ? OFFSET ?;";

    public final static String SQL__MUSIC_SEARCH__GET_CHOSEN_ARTIST
            = "SET @user_id := ?; SET @artist_id := ?; "
            + "SELECT artists.name AS artist_name, artists.image AS artist_image FROM artists WHERE artists.id = @artist_id; "
            + "SELECT albums.id AS album_id, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year, "
            + "(SELECT COUNT(id) FROM audio_tracks WHERE audio_tracks.album = album_id AND audio_tracks.active = TRUE) AS tracks_count_album_has, "
            + "((SELECT COUNT(id) FROM favorite_albums WHERE albums.id = favorite_albums.album_id AND user_id = @user_id) > 0) AS favourite "
            + "FROM albums LEFT JOIN artists ON albums.artist = artists.id WHERE artists.id = @artist_id AND artists.active = TRUE AND albums.active = TRUE "
            + "ORDER BY album_year, album_name LIMIT ? OFFSET ?;";

    public final static String SQL__MUSIC_SEARCH__GET_CHOSEN_ALBUM
            = "SET @user_id := ?; SET @album_id := ?; "
            + "SELECT artists.name AS artist_name, artists.image AS artist_image, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year "
            + "FROM artists LEFT JOIN albums ON albums.artist = artists.id WHERE albums.id = @album_id; "
            + "SELECT audio_tracks.id AS track_id, audio_tracks.name AS track_name, audio_tracks.file AS track_file, "
            + "((SELECT COUNT(id) FROM favorite_tracks WHERE audio_tracks.id = favorite_tracks.track_id AND user_id = @user_id) > 0) AS favourite "
            + "FROM audio_tracks LEFT JOIN albums ON audio_tracks.album = albums.id LEFT JOIN artists ON albums.artist = artists.id "
            + "WHERE albums.id = @album_id AND audio_tracks.active = TRUE AND albums.active = TRUE AND artists.active = TRUE "
            + "ORDER BY audio_tracks.track_number LIMIT ? OFFSET ?;";

    public final static String SQL_SET_ARTIST_AS_FAVOURITE
            = "INSERT INTO music_waves.favorite_artists (user_id, artist_id) VALUES (?, ?)";

    public final static String SQL_UNSET_ARTIST_AS_FAVOURITE
            = "DELETE FROM music_waves.favorite_artists WHERE user_id = ? AND artist_id = ?;";

    public final static String SQL_SET_ALBUM_AS_FAVOURITE
            = "INSERT INTO music_waves.favorite_albums (user_id, album_id) VALUES (?, ?)";

    public final static String SQL_UNSET_ALBUM_AS_FAVOURITE
            = "DELETE FROM music_waves.favorite_albums WHERE user_id = ? AND album_id = ?;";

    public final static String SQL_SET_TRACK_AS_FAVOURITE
            = "INSERT INTO music_waves.favorite_tracks (user_id, track_id) VALUES (?, ?)";

    public final static String SQL_UNSET_TRACK_AS_FAVOURITE
            = "DELETE FROM music_waves.favorite_tracks WHERE user_id = ? AND track_id = ?;";

    public final static String SQL_CHECK_IF_PLAYLIST_EXISTS_AND_BELONGS_TO_USER
            = "SELECT (CASE WHEN EXISTS "
            + "(SELECT playlists.user "
            + "FROM playlists LEFT JOIN users "
            + "ON playlists.user = users.user_id WHERE users.user_id = ? AND playlists.id = ?) "
            + "THEN TRUE ELSE FALSE END) AS result;";

    public final static String SQL_GET_PLAYLIST_TRACKS
            = "SELECT playlist_items.id AS item_id, playlist_items.track_id AS track_id, audio_tracks.name AS track_name, "
            + "(SELECT audio_tracks.active = TRUE AND albums.active = TRUE AND artists.active = TRUE) AS is_active_item "
            + "FROM playlist_items "
            + "LEFT JOIN playlists ON playlist_items.playlist_id = playlists.id "
            + "LEFT JOIN audio_tracks ON playlist_items.track_id = audio_tracks.id "
            + "LEFT JOIN albums ON audio_tracks.album = albums.id "
            + "LEFT JOIN artists ON albums.artist = artists.id "
            + "WHERE playlists.user = ? AND playlists.id = ?";

    public final static String SQL_DELETE_PLAYLIST_ITEMS
            = "DELETE FROM music_waves.playlist_items WHERE playlist_items.playlist_id = ?";

    public final static String SQL_RECORD_PLAYLIST_ITEM
            = "INSERT INTO music_waves.playlist_items (playlist_id, track_id) VALUES (?, ?);";

    public final static String SQL_GET_ACTIVE_TRACK_DATA
            = "SELECT artists.id AS artist_id, artists.name AS artist_name, artists.image AS artist_image, "
            + "albums.id AS album_id, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year, "
            + "audio_tracks.id AS track_id, audio_tracks.name AS track_name, audio_tracks.file AS track_file, genres.name AS track_genre "
            + "FROM artists LEFT JOIN albums ON albums.artist = artists.id LEFT JOIN audio_tracks ON audio_tracks.album = albums.id "
            + "LEFT JOIN genres ON audio_tracks.genre = genres.id "
            + "WHERE audio_tracks.id = ? AND audio_tracks.active = TRUE AND albums.active = TRUE AND artists.active = TRUE;";

    public final static String SQL_GET_FAVOURITE_ALBUMS_DATA
            = "SET @user_id := ?; SET @search_pattern := ?; "
            + "SELECT artists.id AS artist_id, artists.name AS artist_name, artists.image AS artist_image, "
            + "albums.id AS album_id, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year "
            + "FROM artists LEFT JOIN albums ON albums.artist = artists.id  "
            + "LEFT JOIN favorite_albums ON favorite_albums.album_id = albums.id "
            + "WHERE favorite_albums.user_id = @user_id "
            + "AND (artists.name LIKE @search_pattern OR albums.name LIKE @search_pattern) "
            + "AND albums.active = TRUE AND artists.active = TRUE "
            + "ORDER BY artist_name, album_name LIMIT ? OFFSET ?;";

    public final static String SQL_GET_FAVOURITE_TRACKS_DATA
            = "SET @user_id := ?; SET @search_pattern := ?; "
            + "SELECT artists.id AS artist_id, artists.name AS artist_name, artists.image AS artist_image, "
            + "albums.id AS album_id, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year, "
            + "audio_tracks.id AS track_id, audio_tracks.name AS track_name, audio_tracks.file AS track_file "
            + "FROM artists LEFT JOIN albums ON albums.artist = artists.id LEFT JOIN audio_tracks ON audio_tracks.album = albums.id "
            + "LEFT JOIN favorite_tracks ON favorite_tracks.track_id = audio_tracks.id "
            + "WHERE favorite_tracks.user_id = @user_id "
            + "AND (audio_tracks.name LIKE @search_pattern OR artists.name LIKE @search_pattern OR albums.name LIKE @search_pattern) "
            + "AND albums.active = TRUE AND artists.active = TRUE AND audio_tracks.active = TRUE "
            + "ORDER BY artist_name, album_name, track_name LIMIT ? OFFSET ?;";

    public final static String SQL_GET_ARTIST_ALBUMS_DATA
            = "SELECT artists.id AS artist_id, artists.name AS artist_name, artists.image AS artist_image, "
            + "albums.id AS album_id, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year "
            + "FROM artists LEFT JOIN albums ON albums.artist = artists.id "
            + "WHERE artists.id = ? AND albums.name LIKE ? AND albums.active = TRUE AND artists.active = TRUE "
            + "ORDER BY artist_name, album_name LIMIT ? OFFSET ?;";

    public final static String SQL_GET_ALBUM_TRACKS_DATA
            = "SELECT artists.id AS artist_id, artists.name AS artist_name, artists.image AS artist_image, "
            + "albums.id AS album_id, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year, "
            + "audio_tracks.id AS track_id, audio_tracks.name AS track_name, audio_tracks.file AS track_file "
            + "FROM artists LEFT JOIN albums ON albums.artist = artists.id LEFT JOIN audio_tracks ON audio_tracks.album = albums.id "
            + "WHERE albums.id = ? AND audio_tracks.name LIKE ? "
            + "AND albums.active = TRUE AND artists.active = TRUE AND audio_tracks.active = TRUE "
            + "ORDER BY audio_tracks.track_number;";

    ////////////////////////////////////////////////////////////////////////////
    //// GET MULTIPLE TRACKs DATA BY GIVEN IDs
    ////////////////////////////////////////////////////////////////////////////
    public final static String SQL_GET_TRACKS_DATA_PREFIX
            = "SELECT artists.id AS artist_id, artists.name AS artist_name, artists.image AS artist_image, "
            + "albums.id AS album_id, albums.name AS album_name, albums.image AS album_image, albums.year AS album_year, "
            + "audio_tracks.id AS track_id, audio_tracks.name AS track_name, audio_tracks.file AS track_file "
            + "FROM artists LEFT JOIN albums ON albums.artist = artists.id LEFT JOIN audio_tracks ON audio_tracks.album = albums.id "
            + "WHERE albums.active = TRUE AND artists.active = TRUE AND audio_tracks.active = TRUE AND (";

    public final static String SQL_GET_TRACKS_DATA_TRACK_ID_CLAUSE = "audio_tracks.id = ?";

    public final static String SQL_OR = " OR ";

    public final static String SQL_GET_TRACKS_DATA_POSTFIX = ") ORDER BY artist_name, album_name, track_name;";

    ///////////////////////////////////////////////////////////////////////////////////////////////////
    //// DELETING ARTISTS, ALBUMS, TRACKS AND GETTING TRACKs FILE NAMEs THAT ARE IN NEED TO BE DELETED
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public final static String SQL_DELETE_ARTIST_AND_GET_FILES_BEING_DELETED_NAMES
            = "SET @artist_id := ?; "
            // getting artist img
            + "SELECT artists.image FROM artists "
            + "WHERE artists.id = @artist_id; "
            // getting albums img
            + "SELECT albums.image FROM albums "
            + "LEFT JOIN artists ON albums.artist = artists.id "
            + "WHERE artists.id = @artist_id; "
            // getting tracks files
            + "SELECT audio_tracks.file FROM audio_tracks "
            + "LEFT JOIN albums ON audio_tracks.album = albums.id "
            + "LEFT JOIN artists ON albums.artist = artists.id "
            + "WHERE artists.id = @artist_id; "
            // deleting records
            + "DELETE artists, albums, audio_tracks "
            + "FROM artists "
            + "LEFT JOIN albums ON albums.artist = artists.id "
            + "LEFT JOIN audio_tracks ON audio_tracks.album = albums.id "
            + "WHERE artists.id = @artist_id;";

    public final static String SQL_DELETE_ALBUM_AND_GET_FILES_BEING_DELETED_NAMES
            = "SET @album_id := ?; "
            // getting album img
            + "SELECT albums.image FROM albums "
            + "LEFT JOIN artists ON albums.artist = artists.id "
            + "WHERE albums.id = @album_id; "
            // getting tracks files
            + "SELECT audio_tracks.file FROM audio_tracks "
            + "LEFT JOIN albums ON audio_tracks.album = albums.id "
            + "WHERE albums.id = @album_id; "
            // deleting records
            + "DELETE albums, audio_tracks "
            + "FROM albums "
            + "LEFT JOIN audio_tracks ON audio_tracks.album = albums.id "
            + "WHERE albums.id = @album_id;";

    public final static String SQL_DELETE_TRACK_AND_GET_FILE_BEING_DELETED_NAME
            = "SET @track_id := ?; "
            // getting track file
            + "SELECT audio_tracks.file FROM audio_tracks "
            + "WHERE audio_tracks.id = @track_id; "
            // deleting records
            + "DELETE audio_tracks "
            + "FROM audio_tracks "
            + "WHERE audio_tracks.id = @track_id;";

    ////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    private final SQLRequestHandler requestHandler = new SQLRequestHandler();

    public List<List<Map<String, String>>> getMusicSearchResultsQuantity(String searchString) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL__MUSIC_SEARCH__GET_SEARCH_RESULTS_QUANTITY,
                (statement) ->
        {
            statement.setNextString(searchString);
        });
    }

    public List<List<Map<String, String>>> processMusicSearchFindArtists(String searchString, int userId, int limit, int offset) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL__MUSIC_SEARCH__GET_SEARCH_RESULTS_QUANTITY + SQL__MUSIC_SEARCH__GET_ARTISTS_LIST,
                (statement) ->
        {
            statement.setNextString(searchString);
            statement.setNextInt(userId);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<List<Map<String, String>>> processMusicSearchFindAlbums(String searchString, int userId, int limit, int offset) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL__MUSIC_SEARCH__GET_SEARCH_RESULTS_QUANTITY + SQL__MUSIC_SEARCH__GET_ALBUMS_LIST,
                (statement) ->
        {
            statement.setNextString(searchString);
            statement.setNextInt(userId);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<List<Map<String, String>>> processMusicSearchFindAudioTracks(String searchString, int userId, int limit, int offset) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL__MUSIC_SEARCH__GET_SEARCH_RESULTS_QUANTITY + SQL__MUSIC_SEARCH__GET_TRACKS_LIST,
                (statement) ->
        {
            statement.setNextString(searchString);
            statement.setNextInt(userId);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public boolean setArtistAsFavourite(int userId, int artistId) throws DaoException
    {
        return requestHandler.processCustomCreateRequest(
                SQL_SET_ARTIST_AS_FAVOURITE,
                (statement) ->
        {
            statement.setNextInt(userId);
            statement.setNextInt(artistId);
        }) != null;
    }

    public void unsetArtistAsFavourite(int userId, int artistId) throws DaoException
    {
        requestHandler.processCustomRequest(
                SQL_UNSET_ARTIST_AS_FAVOURITE,
                (statement) ->
        {
            statement.setNextInt(userId);
            statement.setNextInt(artistId);
        });
    }

    public boolean setAlbumAsFavourite(int userId, int albumId) throws DaoException
    {
        return requestHandler.processCustomCreateRequest(
                SQL_SET_ALBUM_AS_FAVOURITE,
                (statement) ->
        {
            statement.setNextInt(userId);
            statement.setNextInt(albumId);
        }) != null;
    }

    public void unsetAlbumAsFavourite(int userId, int albumId) throws DaoException
    {
        requestHandler.processCustomRequest(
                SQL_UNSET_ALBUM_AS_FAVOURITE,
                (statement) ->
        {
            statement.setNextInt(userId);
            statement.setNextInt(albumId);
        });
    }

    public boolean setTrackAsFavourite(int userId, int trackId) throws DaoException
    {
        return requestHandler.processCustomCreateRequest(
                SQL_SET_TRACK_AS_FAVOURITE,
                (statement) ->
        {
            statement.setNextInt(userId);
            statement.setNextInt(trackId);
        }) != null;
    }

    public void unsetTrackAsFavourite(int userId, int trackId) throws DaoException
    {
        requestHandler.processCustomRequest(
                SQL_UNSET_TRACK_AS_FAVOURITE,
                (statement) ->
        {
            statement.setNextInt(userId);
            statement.setNextInt(trackId);
        });
    }

    public List<List<Map<String, String>>> processMusicSearchGetChosenArtistAlbumsList(int userId, int artistId, int limit, int offset) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL__MUSIC_SEARCH__GET_CHOSEN_ARTIST,
                (statement) ->
        {
            statement.setNextInt(userId);
            statement.setNextInt(artistId);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<List<Map<String, String>>> processMusicSearchGetChosenAlbumTracksList(int userId, int albumId, int limit, int offset) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL__MUSIC_SEARCH__GET_CHOSEN_ALBUM,
                (statement) ->
        {
            statement.setNextInt(userId);
            statement.setNextInt(albumId);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<List<Map<String, String>>> getPlaylistTracks(int userId, int playlistId) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL_GET_PLAYLIST_TRACKS,
                (statement) ->
        {
            statement.setNextInt(userId);
            statement.setNextInt(playlistId);
        });
    }

    public void recordPlaylistItems(int userId, int playlistId, int... tracksId) throws DaoException
    {
        // checkin if given id playlist exists and actually belongs to the person who requested it's rewrite
        boolean playlistBelongsToUser = requestHandler.processBooleanResultRequest(
                SQL_CHECK_IF_PLAYLIST_EXISTS_AND_BELONGS_TO_USER,
                (statement) ->
        {
            statement.setNextInt(userId);
            statement.setNextInt(playlistId);
        });

        if (playlistBelongsToUser)
        {
            // deleting old items
            requestHandler.processDeleteRequest(playlistId, SQL_DELETE_PLAYLIST_ITEMS);

            // if there is need to write new items (e.g. playlist isn't empty)
            if (tracksId != null && tracksId.length > 0)
            {
                // configuring initializers for INSERT request
                SelfDependentStatementInitializer[] initializers = new SelfDependentStatementInitializer[tracksId.length];
                for (int i = 0 ; i < initializers.length ; i++)
                {
                    int trackId = tracksId[i];
                    initializers[i] = (statement) ->
                    {
                        statement.setNextInt(playlistId);
                        statement.setNextInt(trackId);
                        //statement.resetBatchParameterIndex();
                    };
                }

                // recording new items
                requestHandler.processBatchRequest(SQL_RECORD_PLAYLIST_ITEM, initializers);
            }
        }
    }

    public List<List<Map<String, String>>> getActiveAudioTrackData(int trackId) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL_GET_ACTIVE_TRACK_DATA,
                (statement) ->
        {
            statement.setNextInt(trackId);
        });
    }

    public List<List<Map<String, String>>> getFavouriteAlbumsData(int userId, String searchPattern, int limit, int offset) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL_GET_FAVOURITE_ALBUMS_DATA,
                (statement) ->
        {
            statement.setNextInt(userId);
            statement.setNextString(searchPattern);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<List<Map<String, String>>> getFavouriteTracksData(int userId, String searchPattern, int limit, int offset) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL_GET_FAVOURITE_TRACKS_DATA,
                (statement) ->
        {
            statement.setNextInt(userId);
            statement.setNextString(searchPattern);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<List<Map<String, String>>> getArtistAlbumsData(int artistId, String searchPattern, int limit, int offset) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL_GET_ARTIST_ALBUMS_DATA,
                (statement) ->
        {
            statement.setNextInt(artistId);
            statement.setNextString(searchPattern);
            statement.setNextInt(limit);
            statement.setNextInt(offset);
        });
    }

    public List<List<Map<String, String>>> getAlbumTracksData(int albumId, String searchPattern) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL_GET_ALBUM_TRACKS_DATA,
                (statement) ->
        {
            statement.setNextInt(albumId);
            statement.setNextString(searchPattern);
        });
    }

    /**
     * Requires at least 1 track id as a parameter
     */
    public List<List<Map<String, String>>> getTracksData(int... tracksId) throws DaoException
    {
        if (tracksId == null || tracksId.length < 1)
        {
            throw new DaoException("Invalid parameters passed");
        }

        StringBuilder sql = new StringBuilder();
        sql.append(SQL_GET_TRACKS_DATA_PREFIX);

        for (int i = 0 ; i < tracksId.length - 1 ; i++)
        {
            sql.append(SQL_GET_TRACKS_DATA_TRACK_ID_CLAUSE);
            sql.append(SQL_OR);
        }
        sql.append(SQL_GET_TRACKS_DATA_TRACK_ID_CLAUSE);
        sql.append(SQL_GET_TRACKS_DATA_POSTFIX);

        return requestHandler.processCustomSelectRequest(
                sql.toString(),
                (statement) ->
        {
            for (int trackId : tracksId)
            {
                statement.setNextInt(trackId);
            }
        });
    }

    public List<List<Map<String, String>>> deleteArtist(int artistId) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL_DELETE_ARTIST_AND_GET_FILES_BEING_DELETED_NAMES,
                (statement) ->
        {
            statement.setNextInt(artistId);
        });
    }

    public List<List<Map<String, String>>> deleteAlbum(int albumId) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL_DELETE_ALBUM_AND_GET_FILES_BEING_DELETED_NAMES,
                (statement) ->
        {
            statement.setNextInt(albumId);
        });
    }

    public List<List<Map<String, String>>> deleteAudioTrack(int trackId) throws DaoException
    {
        return requestHandler.processCustomSelectRequest(
                SQL_DELETE_TRACK_AND_GET_FILE_BEING_DELETED_NAME,
                (statement) ->
        {
            statement.setNextInt(trackId);
        });
    }

    @Override
    public void close()
    {
        requestHandler.close();
    }

}
