package by.musicwaves.command.xhr;

import by.musicwaves.command.CommandException;
import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.PlaylistDao;
import by.musicwaves.entity.User;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import by.musicwaves.entity.ancillary.EntityNameValueRestraints;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to rename single Playlist instance. <br>
 * Playlist will be renamed only if it belongs to the user who requests this processor. <br><br> 
 * Access restrictions: any level users, login required
 */
public class RenamePlaylistCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    
    private final static String PARAM_NAME_NAME = "name";
    private final static String PARAM_NAME_ID = "id";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("RENAME PLAYLIST command reached");

        JsonSelfWrapper json = new JsonSelfWrapper();
        json.openJson();

        User user = (User) request.getSession().getAttribute("user");
        if (user == null)
        {
            LOGGER.debug("Someone tries to run this command without being authorized.");
            json.appendResponceCode(STATE_USER_NOT_LOGGED_IN);
            json.closeJson();
            return json.toString();
        }

        try
        {
            String playlistName = request.getParameter(PARAM_NAME_NAME);
            int playlistId = Integer.parseInt(request.getParameter(PARAM_NAME_ID));

            if (EntityNameValueRestraints.PLAYLIST.validateName(playlistName))
            {
                throw new IllegalArgumentException();
            }

            try (PlaylistDao dao = new PlaylistDao())
            {
                boolean success = dao.rename(user.getId(), playlistId, playlistName);
                if (!success)
                {
                    throw new CommandException("No rows were affected");
                }

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException | CommandException ex)
            {
                LOGGER.error("RENAME PLAYLIST command, error occured: ", ex);
                json.appendResponceCode(STATE_ERROR_OCCURED);
                json.closeJson();
                return json.toString();
            }
        } catch (IllegalArgumentException | NullPointerException ex)
        {
            LOGGER.debug("Data did not pass inner verification", ex);
            json.appendResponceCode(STATE_INVALID_DATA);
            json.closeJson();
            return json.toString();
        }
    }
}
