package by.musicwaves.command.xhr;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.UserDao;
import by.musicwaves.entity.User;
import by.musicwaves.logic.RegistrationWorker;
import by.musicwaves.util.PasswordWorker;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to process password changing. <br><br>
 * It checks if old password sent by user matches the old one held in
 * application database (hashed values are compared). If everything is all
 * right, new password will be validated and - if it fine - set as account
 * password.
 */
public class ChangeAccountPasswordCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_PASSWORD_DOES_NOT_MEET_REQUIRMENTS = 1;
    private final static int STATE_PASSWORDS_DO_NOT_MATCH = 2;
    private final static int STATE_USER_IS_NOT_LOGGED_IN = 3;
    
    private final static String PARAM_NAME_PASSWORD_OLD = "old_password";
    private final static String PARAM_NAME_PASSWORD_NEW = "new_password";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("UPDATE ACCOUNT PASSWORD command reached");

        String passwordOld = request.getParameter(PARAM_NAME_PASSWORD_OLD);
        String passwordNew = request.getParameter(PARAM_NAME_PASSWORD_NEW);

        User user = (User) request.getSession().getAttribute("user");
        if (user == null)
        {
            LOGGER.debug("Someone tries to run this command without being authorized");
            return String.valueOf(STATE_USER_IS_NOT_LOGGED_IN);
        }

        String passwordOldHashed = PasswordWorker.processPasswordHashing(passwordOld);
        if (!user.getHashedPassword().equals(passwordOldHashed))
        {
            LOGGER.debug("Old password sent by user doesn't match one holden in database");
            return String.valueOf(STATE_PASSWORDS_DO_NOT_MATCH);
        }

        RegistrationWorker registrationWorker = new RegistrationWorker();
        if (!registrationWorker.validatePassword(passwordNew))
        {
            LOGGER.debug("New password doesn't match requirements");
            return String.valueOf(STATE_PASSWORD_DOES_NOT_MEET_REQUIRMENTS);
        }

        LOGGER.debug("Entered data is all right, updating password");
        String passwordNewHashed = PasswordWorker.processPasswordHashing(passwordNew);
        user.setHashedPassword(passwordNewHashed);

        try (UserDao userDao = new UserDao())
        {
            userDao.update(user);
            return String.valueOf(STATE_SUCCESS);

        } catch (DaoException ex)
        {
            LOGGER.error("UPDATE ACCOUNT PASSWORD command, exception occured", ex);
            user.setHashedPassword(passwordOldHashed); // setting back old password to the user object holden in session
            return String.valueOf(STATE_ERROR_OCCURED);
        }
    }
}
