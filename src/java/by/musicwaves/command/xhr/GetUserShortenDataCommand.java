package by.musicwaves.command.xhr;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.UserDao;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to get user shorten data (to use in proper users 
 * adminitration form) and return it as JSON-object to the requesting side. <br><br>
 * Access restrictions: ADMINISTRATOR level users only
 */
public class GetUserShortenDataCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
	
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_INSUFFICIENT_RIGHTS = 3;
    private final static int STATE_USER_NOT_FOUND = 4;
    
    private final static String PARAM_NAME_ID = "id";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("GET USER SHORTEN DATA command reached");

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

        if (user.getRole() != Role.ADMINISTRATOR)
        {
            LOGGER.info("User is logged in but has no rights to execute this command");
            LOGGER.info("User: " + user.toString());
            json.appendResponceCode(STATE_INSUFFICIENT_RIGHTS);
            json.closeJson();
            return json.toString();
        }

        try
        {
            int id = Integer.parseInt(request.getParameter(PARAM_NAME_ID));

            try (UserDao dao = new UserDao())
            {
                User searchResult = dao.findById(id);

                if (searchResult == null)
                {
                    json.appendResponceCode(STATE_USER_NOT_FOUND);
                    json.closeJson();
                    return json.toString();
                }

                wrapSearchResult(json, searchResult);
                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("GET USER SHORTEN DATA command, error occured", ex);
                json.appendResponceCode(STATE_ERROR_OCCURED);
                json.closeJson();
                return json.toString();
            }
        } catch (IllegalArgumentException | NullPointerException ex)
        {
            LOGGER.error("Data did not pass inner verification", ex);
            json.appendResponceCode(STATE_INVALID_DATA);
            json.closeJson();
            return json.toString();
        }
    }

    private void wrapSearchResult(JsonSelfWrapper json, User user)
    {
        json.openObject("user");
        json.appendNumber("id", user.getId());
        json.appendString("email", user.getEmail());
        json.appendString("nickname", user.getNickname());
        json.appendNumber("role", user.getRole().getDatabaseEquivalent());
        json.appendString("registerDate", user.getRegisterDate().toString());
        json.appendString("first_name", user.getFirstName());
        json.appendString("last_name", user.getLastName());
        json.appendString("avatar", user.getAvatarFileName());
        json.closeObject();
    }
}
