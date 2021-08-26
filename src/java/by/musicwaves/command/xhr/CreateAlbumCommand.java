package by.musicwaves.command.xhr;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.AlbumDao;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import by.musicwaves.entity.ancillary.EntityNameValueRestraints;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to create and save new Album instance. <br><br>
 * Passed album name will be validated. <br><br>
 * Access restrictions: ADMINISTRATOR and CURATOR level users only.
 */
public class CreateAlbumCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_INSUFFICIENT_RIGHTS = 3;
    
    private final static String PARAM_NAME_ARTIST_NAME = "name";
    private final static String PARAM_NAME_YEAR = "year";
    private final static String PARAM_NAME_ARTIST = "artist";
    private final static String PARAM_NAME_ACTIVITY_STATE = "active";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("CREATE ALBUM command reached");

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
            String albumName = request.getParameter(PARAM_NAME_ARTIST_NAME);
            int albumYear = Integer.parseInt(request.getParameter(PARAM_NAME_YEAR));
            int albumArtist = Integer.parseInt(request.getParameter(PARAM_NAME_ARTIST));
            boolean albumActivityState = Boolean.parseBoolean(request.getParameter(PARAM_NAME_ACTIVITY_STATE));

            if (!EntityNameValueRestraints.ALBUM.validateName(albumName))
            {
                throw new IllegalArgumentException();
            }

            try (AlbumDao dao = new AlbumDao())
            {
                // we don't check if new album name is unique
                // this is not really relevant and for some mad reasons
                // artist may want to have several albums with the very same name

                Album albumBeingCreated = new Album();
                albumBeingCreated.setName(albumName);
                albumBeingCreated.setYear(albumYear);
                albumBeingCreated.setArtist(albumArtist);
                albumBeingCreated.setActive(albumActivityState);

                dao.create(albumBeingCreated);

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("CREATE ALBUM command, error occured", ex);
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
