package by.musicwaves.command.xhr;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.UserDao;
import by.musicwaves.entity.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * User must enter code sent by e-mail to activate account. This XHRProcessor
 * checks if the code user sends us and the code we have sent him earlier match.
 * If they do - account will be activated.
 */
public class ActivateUserAccountCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();

    private final static int STATE_SUCCESS = 0;
    private final static int STATE_WRONG_CODE = 1;
    private final static int STATE_EMAIL_NOT_FOUND = 2;
    private final static int STATE_ACCOUNT_ALREADY_ACTIVATED = 3;

    private final static String PARAM_NAME_EMAIL = "email";
    private final static String PARAM_NAME_CONF_CODE = "confirmation_code";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("ACTIVATE USER ACCOUNT command reached");

        String email = request.getParameter(PARAM_NAME_EMAIL);
        String confCode = request.getParameter(PARAM_NAME_CONF_CODE);
        LOGGER.debug("email: " + email);
        LOGGER.debug("code: " + confCode);

        try (UserDao userDao = new UserDao())
        {
            User foundUser = userDao.findByEmail(email);

            LOGGER.debug("checking user in database...");
            LOGGER.debug("user is: " + foundUser);

            if (foundUser == null)
            {
                LOGGER.debug("response: we found not such an e-mail");
                return String.valueOf(STATE_EMAIL_NOT_FOUND);
            }

            if (foundUser.isAccountActivated())
            {
                LOGGER.debug("response: account is active already");
                return String.valueOf(STATE_ACCOUNT_ALREADY_ACTIVATED);
            }

            if (!foundUser.getConfCode().equals(confCode))
            {
                LOGGER.debug("response: codes do not match");
                return String.valueOf(STATE_WRONG_CODE);
            }

            LOGGER.debug("state: now we can activate account");

            foundUser.setAccountActivated(true);
            foundUser.setConfCode(""); // we don't need to store this code any longer

            userDao.update(foundUser);

            LOGGER.debug("success");
            return String.valueOf(STATE_SUCCESS);

        } catch (DaoException ex)
        {
            LOGGER.error("ACTIVATE USER ACCOUNT command, error occured", ex);
            return String.valueOf(STATE_ERROR_OCCURED);
        }
    }
}
