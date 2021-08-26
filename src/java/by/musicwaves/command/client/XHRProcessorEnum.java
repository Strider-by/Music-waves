package by.musicwaves.command.client;

import by.musicwaves.command.xhr.ActivateUserAccountCommand;
import by.musicwaves.command.xhr.ChangeAccountPasswordCommand;
import by.musicwaves.command.xhr.TryLoginCommand;
import by.musicwaves.command.xhr.GetArtistByIdCommand;
import by.musicwaves.command.xhr.GetAlbumByIdCommand;
import by.musicwaves.command.xhr.GetTrackByIdCommand;
import by.musicwaves.command.xhr.SearchArtistsCommand;
import by.musicwaves.command.xhr.CreateArtistCommand;
import by.musicwaves.command.xhr.UpdateArtistCommand;
import by.musicwaves.command.xhr.SearchAlbumsCommand;
import by.musicwaves.command.xhr.UpdateAlbumCommand;
import by.musicwaves.command.xhr.CreateAlbumCommand;
import by.musicwaves.command.xhr.CreateAudioTrackCommand;
import by.musicwaves.command.xhr.CreateGenreCommand;
import by.musicwaves.command.xhr.UpdateGenreCommand;
import by.musicwaves.command.xhr.RegisterUserAccountCommand;
import by.musicwaves.command.xhr.SendConfirmatonRegCodeCommand;
import by.musicwaves.command.xhr.UpdateUserPersonalDataCommand;
import by.musicwaves.command.xhr.UploadAvatarCommand;
import by.musicwaves.command.xhr.SearchGenresCommand;
import by.musicwaves.command.xhr.UploadArtistImageCommand;
import by.musicwaves.command.xhr.UploadAlbumImageCommand;
import by.musicwaves.command.xhr.UploadAudioTrackFileCommand;
import by.musicwaves.command.xhr.GetAlbumTracksCommand;
import by.musicwaves.command.xhr.ShiftUpAudioTrackNumber;
import by.musicwaves.command.xhr.ShiftDownAudioTrackNumber;
import by.musicwaves.command.xhr.UpdateAudioTrackCommand;
import by.musicwaves.command.xhr.GetUsersListCommand;
import by.musicwaves.command.xhr.GetUserShortenDataCommand;
import by.musicwaves.command.xhr.MusicSearchGetChosenAlbumTracksCommand;
import by.musicwaves.command.xhr.MusicSearchGetChosenArtistAlbumsCommand;
import by.musicwaves.command.xhr.MusicSearchGetFoundAlbumsListCommand;
import by.musicwaves.command.xhr.MusicSearchGetFoundArtistsListCommand;
import by.musicwaves.command.xhr.MusicSearchGetFoundAudioTracksListCommand;
import by.musicwaves.command.xhr.UpdateAdminAccessibleUserDataCommand;
import by.musicwaves.command.xhr.MusicSearchGetSearchResultsQuantityCommand;
import by.musicwaves.command.xhr.SetAlbumAsFavouriteCommand;
import by.musicwaves.command.xhr.SetArtistAsFavouriteCommand;
import by.musicwaves.command.xhr.SetAudiotrackAsFavouriteCommand;
import by.musicwaves.command.xhr.UnsetAlbumAsFavouriteCommand;
import by.musicwaves.command.xhr.UnsetArtistAsFavouriteCommand;
import by.musicwaves.command.xhr.UnsetTrackAsFavouriteCommand;
import by.musicwaves.command.xhr.GetUserPlaylistsCommand;
import by.musicwaves.command.xhr.CreatePlaylistCommand;
import by.musicwaves.command.xhr.GetPlaylistTracksCommand;
import by.musicwaves.command.xhr.RecordPlaylistItemsCommand;
import by.musicwaves.command.xhr.RenamePlaylistCommand;
import by.musicwaves.command.xhr.DeleteMultiplePlaylistsCommand;
import by.musicwaves.command.xhr.GetFavouriteArtistsCommand;
import by.musicwaves.command.xhr.GetFavouriteAlbumsCommand;
import by.musicwaves.command.xhr.GetActiveAudiotrackDataCommand;
import by.musicwaves.command.xhr.SearchAlbumTracksCommand;
import by.musicwaves.command.xhr.GetArtistAlbumsDataCommand;
import by.musicwaves.command.xhr.GetFavouriteTracksCommand;
import by.musicwaves.command.xhr.GetTracksDataByTracksIdCommand;
import by.musicwaves.command.xhr.DeleteArtistCommand;
import by.musicwaves.command.xhr.DeleteAlbumCommand;
import by.musicwaves.command.xhr.DeleteAudioTrackCommand;
import by.musicwaves.command.xhr.XHRProcessor;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public enum XHRProcessorEnum
{
    REGISTER_USER_ACCOUNT (new RegisterUserAccountCommand(), "register"), // [register.js, register.jsp]
    
    ACTIVATE_USER_ACCOUNT (new ActivateUserAccountCommand(), "activate_account"), // [register.js, register.jsp]
    
    SEND_CONFIRMATION_REG_CODE (new SendConfirmatonRegCodeCommand(), "send_conf_code"), // [register.js, register.jsp]
    
    CHECK_LOGIN_PASSWORD_PAIR (new TryLoginCommand(), "check_login_credentials"), // [login.js, login,jsp]
    
    CHANGE_ACCOUNT_PASSWORD (new ChangeAccountPasswordCommand(), "change_password"), // [change_password.js, change_password.jsp]
    
    CHANGE_USER_AVATAR (new UploadAvatarCommand(), "change_avatar"), // [personal_data.js, personal_data.jsp]
    
    UPDATE_PERSONAL_DATA (new UpdateUserPersonalDataCommand(), "update_personal_data"), // [personal_data.js, personal_data.jsp]
    
    GET_GENRES_LIST (new SearchGenresCommand(), "get_genres_list"), // [genres.js, genres.jsp], [music_compound_tracks.js, music_compound.jsp]
    
    UPDATE_GENRE (new UpdateGenreCommand(), "update_genre"), // [genres,js, genres.jsp]
    
    CREATE_GENRE (new CreateGenreCommand(), "create_genre"), // [genres,js, genres.jsp]
    
    GET_ARTISTS_LIST (new SearchArtistsCommand(), "get_artists_list"), // [music_compound_artists.js, music_compound.jsp], [music_search.js, music_search.jsp]
    
    GET_ARTIST_BY_ID (new GetArtistByIdCommand(), "get_artist_by_id"), // [music_compound_artists.js, music_compound.jsp]
    
    UPDATE_ARTIST (new UpdateArtistCommand(), "update_artist"), // [music_compound_artists.js, music_compound.jsp]
    
    CREATE_ARTIST (new CreateArtistCommand(), "create_artist"), // [music_compound_artists.js, music_compound.jsp]
    
    UPLOAD_ARTIST_IMAGE (new UploadArtistImageCommand(), "upload_artist_image"), // [music_compound_artists.js, music_compound.jsp]
    
    GET_ALBUMS_LIST (new SearchAlbumsCommand(), "get_albums_list"), // [music_compound_albums.js, music_compound.jsp]
    
    GET_ALBUM_BY_ID (new GetAlbumByIdCommand(), "get_album_by_id"), // [music_compound_albums.js, music_compound.jsp]
    
    CREATE_ALBUM (new CreateAlbumCommand(), "create_album"), // [music_compound_albums.js, music_compound.jsp]
    
    UPDATE_ALBUM (new UpdateAlbumCommand(), "update_album"), // [music_compound_albums.js, music_compound.jsp]
    
    UPLOAD_ALBUM_IMAGE (new UploadAlbumImageCommand(), "upload_album_image"), // [music_compound_albums.js, music_compound.jsp]
    
    GET_ALBUM_TRACKS (new GetAlbumTracksCommand(), "get_album_tracks"), // [music_compound_tracks.js, music_compound.jsp]
    
    CREATE_TRACK (new CreateAudioTrackCommand(), "create_track"), // [music_compound_tracks.js, music_compound.jsp]
    
    GET_TRACK_BY_ID (new GetTrackByIdCommand(), "get_track_by_id"), // [music_compound_tracks.js, music_compound.jsp]
    
    UPLOAD_TRACK_FILE (new UploadAudioTrackFileCommand(), "upload_track_file"), // [music_compound_tracks.js, music_compound.jsp]
    
    SHIFT_UP_TRACK_NUMBER (new ShiftUpAudioTrackNumber(), "shift_up_track_number"), // [music_compound_tracks.js, music_compound.jsp]
    
    SHIFT_DOWN_TRACK_NUMBER (new ShiftDownAudioTrackNumber(), "shift_down_track_number"), // [music_compound_tracks.js, music_compound.jsp]
    
    UPDATE_TRACK (new UpdateAudioTrackCommand(), "update_track"), // [music_compound_tracks.js, music_compound.jsp]
    
    GET_USERS_LIST (new GetUsersListCommand(), "get_users_list"), // [users.js, users.jsp]
    
    GET_USER_SHORTEN_DATA (new GetUserShortenDataCommand(), "get_user_data"), // [users.js, users.jsp]
    
    UPDATE_USER_DATA_BY_ADMIN(new UpdateAdminAccessibleUserDataCommand(), "update_user_data_by_admin"), // [users.js, users.jsp]
    
    MUSIC_SEARCH_GET_RESULTS_QUANTITY(new MusicSearchGetSearchResultsQuantityCommand(), "music_search_get_results_quantity"), // [music_search.js, music_search.jsp]
    
    MUSIC_SEARCH_GET_ARTISTS_LIST(new MusicSearchGetFoundArtistsListCommand(), "music_search_get_artists_list"), // [music_search.js, music_search.jsp]
    
    MUSIC_SEARCH_GET_ALBUMS_LIST(new MusicSearchGetFoundAlbumsListCommand(), "music_search_get_albums_list"), // [music_search.js, music_search.jsp]
    
    MUSIC_SEARCH_GET_TRACKS_LIST(new MusicSearchGetFoundAudioTracksListCommand(), "music_search_get_tracks_list"), // [music_search.js, music_search.jsp]
    
    SET_ARTIST_AS_FAVOURITE(new SetArtistAsFavouriteCommand(), "set_favourite_artist"), // [music_search.js, music_search.jsp]
    
    UNSET_ARTIST_AS_FAVOURITE(new UnsetArtistAsFavouriteCommand(), "unset_favourite_artist"), // [music_search.js, music_search.jsp], [playlists.js, playlists.jsp]
    
    SET_ALBUM_AS_FAVOURITE(new SetAlbumAsFavouriteCommand(), "set_favourite_album"), // [music_search.js, music_search.jsp]
    
    UNSET_ALBUM_AS_FAVOURITE(new UnsetAlbumAsFavouriteCommand(), "unset_favourite_album"), // [music_search.js, music_search.jsp], [playlists.js, playlists.jsp]
    
    SET_TRACK_AS_FAVOURITE(new SetAudiotrackAsFavouriteCommand(), "set_favourite_track"), // [music_search.js, music_search.jsp]
    
    UNSET_TRACK_AS_FAVOURITE(new UnsetTrackAsFavouriteCommand(), "unset_favourite_track"), // [music_search.js, music_search.jsp], [playlists.js, playlists.jsp]
    
    MUSIC_SEARCH_GET_CHOSEN_ARTIST_ALBUMS(new MusicSearchGetChosenArtistAlbumsCommand(), "music_search_get_chosen_artist_albums"), // [music_search.js, music_search.jsp]
    
    MUSIC_SEARCH_GET_CHOSEN_ALBUM_TRACKS(new MusicSearchGetChosenAlbumTracksCommand(), "music_search_get_chosen_album_tracks"), // [music_search.js, music_search.jsp], [playlists.js, playlists.jsp]
    
    GET_USER_PLAYLISTS(new GetUserPlaylistsCommand(), "get_user_playlists"), // [music_search.js, music_search.jsp], [playlists.js, playlists.jsp], [listen.js, listen.jsp]
    
    CREATE_NEW_PLAYLIST(new CreatePlaylistCommand(), "create_new_playlist"), // [music_search.js, music_search.jsp], [playlists.js, playlists.jsp]
    
    RENAME_PLAYLIST(new RenamePlaylistCommand(), "rename_playlist"), // [playlists.js, playlists.jsp]
    
    DELETE_PLAYLISTS(new DeleteMultiplePlaylistsCommand(), "delete_playlists"), // [playlists.js, playlists.jsp]
    
    GET_PLAYLIST_TRACKS(new GetPlaylistTracksCommand(), "get_playlist_tracks"), // [music_search.js, music_search.jsp], [playlists.js, playlists.jsp], [listen.js, listen.jsp]
    
    RECORD_PLAYLIST(new RecordPlaylistItemsCommand(), "record_playlist"), // [music_search.js, music_search.jsp], [playlists.js, playlists.jsp]
    
    GET_ACTIVE_TRACK_DATA(new GetActiveAudiotrackDataCommand(), "get_active_track_data"), // [music_search.js, music_search.jsp]
        
    GET_FAVOURITE_ARTISTS(new GetFavouriteArtistsCommand(), "get_favourite_artists"), // [playlists.js, playlists.jsp]
    
    GET_FAVOURITE_ALBUMS(new GetFavouriteAlbumsCommand(), "get_favourite_albums"), // [playlists.js, playlists.jsp]
    
    GET_FAVOURITE_TRACKS(new GetFavouriteTracksCommand(), "get_favourite_tracks"), // [playlists.js, playlists.jsp]
    
    GET_ARTIST_ALBUMS_DATA(new GetArtistAlbumsDataCommand(), "get_artist_albums_data"), // [playlists.js, playlists.jsp]
    
    GET_ALBUM_TRACKS_DATA(new SearchAlbumTracksCommand(), "search_album_tracks"), // [playlists.js, playlists.jsp]
    
    GET_TRACKS_DATA_BY_TRACKS_ID(new GetTracksDataByTracksIdCommand(), "get_tracks_data_by_tracks_id"), // [playlists.js, playlists.jsp], [listen.js, listen.jsp]
    
    DELETE_ARTIST_CASCADING(new DeleteArtistCommand(), "delete_artist"), // [music_compound_artists.js, music_compound.jsp]
    
    DELETE_ALBUM_CASCADING(new DeleteAlbumCommand(), "delete_album"), // [music_compound_albums.js, music_compound.jsp]
    
    DELETE_AUDIO_TRACK(new DeleteAudioTrackCommand(), "delete_track"); // [music_compound_tracks.js, music_compound.jsp]

    
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final XHRProcessor processor;
    private final String alias;
    
    private XHRProcessorEnum(XHRProcessor processor, String alias)
    {
        this.processor = processor;
        this.alias = alias;
    }

    public static XHRProcessor getProcessorByAlias(String alias)
    {
        LOGGER.debug("XHR processor requested, alias is: " + alias);
        
        for(XHRProcessorEnum xhrpe : XHRProcessorEnum.values())
        {
            if(xhrpe.alias.equals(alias))
            {
                return xhrpe.getProcessor();
            }
        }
        
        LOGGER.debug("no processor was found for the given alias");
        return null;
    }
    
    public XHRProcessor getProcessor()
    {
        return processor;
    }
}
