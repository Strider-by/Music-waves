package by.musicwaves.command.xhr;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.PlaylistDao;
import by.musicwaves.entity.User;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to delete Playlist instances (one or several). <br><br>
 * Only playlists bound to the user who runs this processor can be deleted. <br><br> 
 * Access restrictions: any level users, login required
 */
public class DeleteMultiplePlaylistsCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    
    private final static String PARAM_NAME_ID = "id[]";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("DELETE MULTIPLE PLAYLISTS command reached");

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
            String[] playlistsIdPostStrings = request.getParameterValues(PARAM_NAME_ID);
            if (playlistsIdPostStrings == null)
            {
                throw new IllegalArgumentException("failed to find expected post parameter");
            }

            int[] playlistsId = new int[playlistsIdPostStrings.length];
            for (int i = 0 ; i < playlistsId.length ; i++)
            {
                playlistsId[i] = Integer.parseInt(playlistsIdPostStrings[i]);
            }

            try (PlaylistDao dao = new PlaylistDao())
            {
                // in case playlist id sent in this command does not belong to this user,
                // it will not be deleted; that's why we need to pass user id parameter
                dao.delete(user.getId(), playlistsId);

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("DELETE MULTIPLE PLAYLISTS command, error occured: ", ex);
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
