package by.musicwaves.command.xhr;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.PlaylistDao;
import by.musicwaves.entity.Playlist;
import by.musicwaves.entity.User;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import by.musicwaves.entity.ancillary.EntityNameValueRestraints;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to create and save new Playlist instance. <br>
 * Playlist is bound to the user who creates it. <br>
 * Passed playlist name will be validated. <br><br>
 * Access restrictions: any level users, login required.
 */
public class CreatePlaylistCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    
    private final static String PARAM_NAME_PLAYLIST_NAME = "name";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("CREATE PLAYLIST command reached");

        JsonSelfWrapper json = new JsonSelfWrapper();
        json.openJson();

        User user = (User) request.getSession().getAttribute("user");
        if (user == null)
        {
            LOGGER.debug("Someone tries to run this command without being authorized");
            json.appendResponceCode(STATE_USER_NOT_LOGGED_IN);
            json.closeJson();

            return json.toString();
        }

        try
        {
            String playlistName = request.getParameter(PARAM_NAME_PLAYLIST_NAME);

            if (!EntityNameValueRestraints.PLAYLIST.validateName(playlistName))
            {
                throw new IllegalArgumentException();
            }

            try (PlaylistDao dao = new PlaylistDao())
            {
                Playlist playlist = new Playlist();
                playlist.setUserID(user.getId());
                playlist.setName(playlistName);
                dao.create(playlist);

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("CREATE PLAYLIST command, error occured", ex);
                json.appendResponceCode(STATE_ERROR_OCCURED);
                json.closeJson();
                return json.toString();
            }
        } catch (IllegalArgumentException ex)
        {
            LOGGER.debug("Data did not pass inner verification", ex);
            json.appendResponceCode(STATE_INVALID_DATA);
            json.closeJson();
            return json.toString();
        }
    }
}
