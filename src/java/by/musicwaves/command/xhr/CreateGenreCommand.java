package by.musicwaves.command.xhr;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.GenreDao;
import by.musicwaves.entity.Genre;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.EntityNameValueRestraints;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.List;

/**
 * This XHRProcessor is designed to create and save new Genre instance. <br>
 * Passed name value will be validated. <br><br>
 * Access restrictions: ADMINISTRATOR and CURATOR level users only.
 */
public class CreateGenreCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_NAME_ALREADY_IN_USE = 3;
    private final static int STATE_INSUFFICIENT_RIGHTS = 4;
    
    private final static String PARAM_NAME_GENRE_NAME = "name";
    private final static String PARAM_NAME_GENRE_ACTIVITY_STATE = "active";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("CREATE GENRE command reached");

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
            String genreName = request.getParameter(PARAM_NAME_GENRE_NAME);
            boolean genreIsActive = Boolean.parseBoolean(request.getParameter(PARAM_NAME_GENRE_ACTIVITY_STATE));

            if (!EntityNameValueRestraints.GENRE.validateName(genreName))
            {
                throw new IllegalArgumentException();
            }

            try (GenreDao genreDao = new GenreDao())
            {
                List<Genre> foundByName = genreDao.findByName(genreName, 1, 0);

                if (!foundByName.isEmpty())
                {
                    LOGGER.debug("The name we are trying to set is already in use by another genre");
                    json.appendResponceCode(STATE_NAME_ALREADY_IN_USE);
                    json.closeJson();
                    return json.toString();
                }

                Genre genreBeingCreated = new Genre();
                genreBeingCreated.setName(genreName);
                genreBeingCreated.setActive(genreIsActive);

                genreDao.create(genreBeingCreated);

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("CREATE GENRE command, error occured", ex);
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
