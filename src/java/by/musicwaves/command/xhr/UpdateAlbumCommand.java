package by.musicwaves.command.xhr;

import by.musicwaves.dao.AlbumDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.EntityNameValueRestraints;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to update existing Album instance. <br><br>
 * Passed album name will be validated. <br><br>
 * Access restrictions: ADMINISTRATOR and CURATOR level users only.
 */
public class UpdateAlbumCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_INSUFFICIENT_RIGHTS = 4;
    
    private final static String PARAM_NAME_ID = "id";
    private final static String PARAM_NAME_NAME = "name";
    private final static String PARAM_NAME_YEAR = "year";
    private final static String PARAM_NAME_ARTIST = "artist";
    private final static String PARAM_NAME_ACTIVITY_STATE = "active";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("UPDATE ALBUM command reached");

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

        if (user.getRole() != Role.CURATOR && user.getRole() != Role.ADMINISTRATOR)
        {
            LOGGER.debug("User is logged in but has no rights to execute this command");
            LOGGER.debug("User role: " + user.getRole());
            json.appendResponceCode(STATE_INSUFFICIENT_RIGHTS);
            json.closeJson();
            return json.toString();
        }

        try
        {
            String instanceName = request.getParameter(PARAM_NAME_NAME);
            int albumYear = Integer.parseInt(request.getParameter(PARAM_NAME_YEAR));
            int albumArtist = Integer.parseInt(request.getParameter(PARAM_NAME_ARTIST));
            int albumId = Integer.parseInt(request.getParameter(PARAM_NAME_ID));
            boolean albumActivityState = Boolean.parseBoolean(request.getParameter(PARAM_NAME_ACTIVITY_STATE));

            if (!EntityNameValueRestraints.ALBUM.validateName(instanceName))
            {
                throw new IllegalArgumentException();
            }

            try (AlbumDao dao = new AlbumDao())
            {
                Album albumBeingUpdated = dao.findById(albumId);
                if (albumBeingUpdated == null)
                {
                    throw new DaoException("failed to find album with given id");
                }

                albumBeingUpdated.setId(albumId);
                albumBeingUpdated.setName(instanceName);
                albumBeingUpdated.setActive(albumActivityState);
                albumBeingUpdated.setYear(albumYear);
                albumBeingUpdated.setArtist(albumArtist);

                dao.update(albumBeingUpdated);

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("UPDATE ALBUM command, error occured", ex);
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
