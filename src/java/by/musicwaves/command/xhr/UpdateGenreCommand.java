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
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to update existing Genre instance. <br><br>
 * Passed genre name will be validated. <br><br>
 * Access restrictions: ADMINISTRATOR and CURATOR level users only.
 */
public class UpdateGenreCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_NAME_ALREADY_IN_USE = 3;
    private final static int STATE_INSUFFICIENT_RIGHTS = 4;
    
    private final static String PARAM_NAME_GENRE_ID = "id";
    private final static String PARAM_NAME_GENRE_NAME = "name";
    private final static String PARAM_NAME_GENRE_ACTIVITY_STATE = "active";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("UPDATE GENRE command reached");

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
            int genreId = Integer.parseInt(request.getParameter(PARAM_NAME_GENRE_ID));
            String genreName = request.getParameter(PARAM_NAME_GENRE_NAME);
            boolean genreIsActive = Boolean.parseBoolean(request.getParameter(PARAM_NAME_GENRE_ACTIVITY_STATE));

            if (!EntityNameValueRestraints.GENRE.validateName(genreName) || genreId < 0)
            {
                throw new IllegalArgumentException();
            }

            try (GenreDao genreDao = new GenreDao())
            {
                List<Genre> foundByName = genreDao.findByName(genreName, 1, 0);

                if (!foundByName.isEmpty() && foundByName.get(0).getId() != genreId)
                {
                    LOGGER.debug("The name we a trying to set is already in use by another genre");
                    json.appendResponceCode(STATE_NAME_ALREADY_IN_USE);
                    json.closeJson();
                    return json.toString();
                }

                Genre genreBeingUpdated = new Genre();

                genreBeingUpdated.setId(genreId);
                genreBeingUpdated.setName(genreName);
                genreBeingUpdated.setActive(genreIsActive);

                genreDao.update(genreBeingUpdated);

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("UPDATE GENRE command, error occured", ex);
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
