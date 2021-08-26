package by.musicwaves.command.xhr;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.UserDao;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to get users list (to use in proper users 
 * adminitration form) and return it as JSON-object to the requesting side. <br><br>
 * Access restrictions: ADMINISTRATOR level users only
 */
public class GetUsersListCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_INSUFFICIENT_RIGHTS = 3;
    
    private final static String PARAM_NAME_ID = "id";
    private final static String PARAM_NAME_DATE = "date";
    private final static String PARAM_NAME_EMAIL = "email";
    private final static String PARAM_NAME_NICKNAME = "nickname";
    private final static String PARAM_NAME_ROLE = "role";
    private final static String PARAM_NAME_OFFSET = "search_offset";
    private final static String PARAM_NAME_LIMIT = "search_limit";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("GET USERS LIST command reached");

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
            // We pass null to request in case if user id wasn't set in our web-form
            // so we don't actually filter our users using it
            Integer id = null;
            String idStringValue = request.getParameter(PARAM_NAME_ID);
            if (idStringValue != null && !idStringValue.equals(""))
            {
                id = Integer.parseInt(idStringValue);
            }
            String date = request.getParameter(PARAM_NAME_DATE);
            String email = request.getParameter(PARAM_NAME_EMAIL);
            String nickname = request.getParameter(PARAM_NAME_NICKNAME);
            int roleId = Integer.parseInt(request.getParameter(PARAM_NAME_ROLE));

            int offset = Integer.parseInt(request.getParameter(PARAM_NAME_OFFSET));
            int limit = Integer.parseInt(request.getParameter(PARAM_NAME_LIMIT));

            if (offset < 0 || limit < 0)
            {
                throw new IllegalArgumentException("Invalid limit or offset parameter");
            }

            try (UserDao dao = new UserDao())
            {
                List<User> searchResult = dao.filterAndFind(date, email, nickname, id, roleId, limit, offset);

                json.appendResponceCode(STATE_SUCCESS);
                wrapSearchResult(json, searchResult, "users");
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("GET USERS LIST command, error occured", ex);
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

    private void wrapSearchResult(JsonSelfWrapper json, List<User> list, String arrName)
    {
        json.openArray(arrName);

        for (int i = 0 ; i < list.size() ; i++)
        {
            User user = list.get(i);

            json.openObject();
            json.appendNumber("id", user.getId());
            json.appendString("email", user.getEmail());
            json.appendString("nickname", user.getNickname());
            json.appendNumber("role", user.getRole().getDatabaseEquivalent());
            json.appendString("registerDate", user.getRegisterDate().toString());
            json.closeObject();
        }

        json.closeArray();
    }
}
