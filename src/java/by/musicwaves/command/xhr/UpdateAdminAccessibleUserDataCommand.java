package by.musicwaves.command.xhr;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.UserDao;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.PersonalDataValueRestraints;
import by.musicwaves.logic.RegistrationWorker;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to update user's data by administrator. <br>
 * Some fields are locked from changing, but administrator has access to change
 * user's role. <br><br>
 * Access restrictions: ADMINISTRATOR level users only
 */
public class UpdateAdminAccessibleUserDataCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_INSUFFICIENT_RIGHTS = 3;
    private final static int STATE_USER_NOT_FOUND = 4;
    
    private final static String PARAM_NAME_ID = "id";
    private final static String PARAM_NAME_EMAIL = "email";
    private final static String PARAM_NAME_NICKNAME = "nickname";
    private final static String PARAM_NAME_ROLE = "role";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("UPDATE USER DATA BY ADMIN command reached");

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
            int role = Integer.parseInt(request.getParameter(PARAM_NAME_ROLE));
            String email = request.getParameter(PARAM_NAME_EMAIL);
            String nickname = request.getParameter(PARAM_NAME_NICKNAME);

            if (role < 0 || role > Role.values().length - 1)
            {
                // invalid Role
                throw new IllegalArgumentException("invalid role parameter");
            }

            if (!new PersonalDataValueRestraints().validateNickname(nickname))
            {
                // invalid nickname
                throw new IllegalArgumentException("invalid nickname parameter");
            }

            RegistrationWorker registrationWorker = new RegistrationWorker();
            if (!registrationWorker.validateEmail(email))
            {
                // invalid email
                throw new IllegalArgumentException("invalid email parameter");
            }

            try (UserDao dao = new UserDao())
            {
                boolean success = dao.updateAdminAccessibleUserData(id, nickname, email, role);
                if (!success)
                {
                    json.appendResponceCode(STATE_USER_NOT_FOUND);
                    json.closeJson();
                    return json.toString();
                }

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("UPDATE USER DATA BY ADMIN command, error occured", ex);
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
}
