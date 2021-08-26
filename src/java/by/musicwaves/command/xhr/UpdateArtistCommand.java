package by.musicwaves.command.xhr;

import by.musicwaves.dao.ArtistDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.Artist;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.EntityNameValueRestraints;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to update existing Artist instance. <br><br>
 * Passed artist name will be validated. <br><br>
 * Access restrictions: ADMINISTRATOR and CURATOR level users only.
 */
public class UpdateArtistCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_NAME_ALREADY_IN_USE = 3;
    private final static int STATE_INSUFFICIENT_RIGHTS = 4;
    
    private final static String PARAM_NAME_ID = "id";
    private final static String PARAM_NAME_NAME = "name";
    private final static String PARAM_NAME_ACTIVITY_STATE = "active";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("UPDATE ARTIST command reached");

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
            int artistId = Integer.parseInt(request.getParameter(PARAM_NAME_ID));
            String artistName = request.getParameter(PARAM_NAME_NAME);
            boolean artistActivityState = Boolean.parseBoolean(request.getParameter(PARAM_NAME_ACTIVITY_STATE));

            if (!EntityNameValueRestraints.ARTIST.validateName(artistName))
            {
                throw new IllegalArgumentException();
            }

            try (ArtistDao dao = new ArtistDao())
            {
                List<Artist> foundByName = dao.findByName(artistName, 1, 0);

                if (!foundByName.isEmpty() && foundByName.get(0).getId() != artistId)
                {
                    LOGGER.debug("The name we a trying to set is already in use by another instance");
                    json.appendResponceCode(STATE_NAME_ALREADY_IN_USE);
                    json.closeJson();
                    return json.toString();
                }

                Artist artistBeingUpdated = dao.findById(artistId);
                if (artistBeingUpdated == null)
                {
                    throw new DaoException("failed to find user with given id");
                }

                artistBeingUpdated.setId(artistId);
                artistBeingUpdated.setName(artistName);
                artistBeingUpdated.setActive(artistActivityState);

                dao.update(artistBeingUpdated);

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("UPDATE ARTIST command, error occured", ex);
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
