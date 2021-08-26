package by.musicwaves.command.xhr;

import by.musicwaves.dao.AlbumDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to get single album data and return it as JSON-object 
 * to the requesting side. <br><br>
 * Data activity state: ALL <br>
 * Access restrictions: ADMINISTRATOR and CURATOR level users only
 */
public class GetAlbumByIdCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_INSUFFICIENT_RIGHTS = 3;
    private final static int STATE_NOT_FOUND = 4;
    
    private final static String PARAM_NAME_ID = "id";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("GET ALBUM BY ID command reached");

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
            int id = Integer.parseInt(request.getParameter(PARAM_NAME_ID));

            try (AlbumDao dao = new AlbumDao())
            {
                Album album = dao.findById(id);

                if (album == null)
                {
                    LOGGER.debug("We failed to find such an album");
                    json.appendResponceCode(STATE_NOT_FOUND);
                    json.closeJson();
                    return json.toString();
                }

                json.appendResponceCode(STATE_SUCCESS);
                json.openObject("album");
                json.appendNumber("id", album.getId());
                json.appendString("name", album.getName());
                json.appendNumber("year", album.getYear());
                json.appendString("image", album.getImageFileName());
                json.appendBoolean("active", album.isActive());
                json.closeObject();
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("GET ALBUM BY ID, error occured", ex);
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
