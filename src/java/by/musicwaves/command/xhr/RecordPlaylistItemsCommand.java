package by.musicwaves.command.xhr;

import by.musicwaves.dao.CrossEntityDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.User;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to record user playlist.
 * All playlist items that were in this playlist before will be deleted. <br><br>
 * Access restrictions: any level users, login required
 */
public class RecordPlaylistItemsCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    
    private final static String PARAM_NAME_PLAYLIST_ID = "playlist_id";
    private final static String PARAM_NAME_TRACKS_ID = "tracks_id[]";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("RECORD PLAYLIST ITEMS command reached");

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
            int playlistId = Integer.parseInt(request.getParameter(PARAM_NAME_PLAYLIST_ID));
            String[] tracksIdStrings = request.getParameterValues(PARAM_NAME_TRACKS_ID);

            int[] tracksId = null;
            if (tracksIdStrings != null)
            {
                tracksId = new int[tracksIdStrings.length];
                for (int i = 0 ; i < tracksId.length ; i++)
                {
                    tracksId[i] = Integer.parseInt(tracksIdStrings[i]);
                }
            }

            try (CrossEntityDao dao = new CrossEntityDao())
            {
                dao.recordPlaylistItems(user.getId(), playlistId, tracksId);

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("RECORD PLAYLIST ITEMS command, error occured", ex);
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
