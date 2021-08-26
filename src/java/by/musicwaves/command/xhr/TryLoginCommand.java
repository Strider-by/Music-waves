package by.musicwaves.command.xhr;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.UserDao;
import by.musicwaves.entity.User;
import by.musicwaves.util.PasswordWorker;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to check login credentials and if everything 
 * is all right (account with given email exists and is active, passed password 
 * matches one holden in database) perform login. <br>
 * After login, User-type object with our user data and proper localization bundle
 * will be stored in newly opened session.
 */
public class TryLoginCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_USER_EXISTS_AND_ACTIVE = 0;
    private final static int STATE_USER_EXISTS_BUT_INACTIVE = 1;
    private final static int STATE_USER_DOES_NOT_EXIST = 2;
    
    private final static String PARAM_NAME_EMAIL = "email";
    private final static String PARAM_NAME_PASSWORD = "password";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        String email = request.getParameter(PARAM_NAME_EMAIL);
        String password = request.getParameter(PARAM_NAME_PASSWORD);
        String hashedPassword = PasswordWorker.processPasswordHashing(password);
        LOGGER.debug("hashed password is: " + hashedPassword);

        try
        {
            User user = getUser(email, hashedPassword);
            if (user == null)
            {
                LOGGER.debug("response: we found not such a user");
                return String.valueOf(STATE_USER_DOES_NOT_EXIST);
            }

            if (!user.isAccountActivated())
            {
                LOGGER.debug("response: account is inactive");
                return String.valueOf(STATE_USER_EXISTS_BUT_INACTIVE);
            }

            LOGGER.debug("response: we can log in");

            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            session.setAttribute("localizationBundle", user.getLanguage().getLocalizationBundle());

            return String.valueOf(STATE_USER_EXISTS_AND_ACTIVE);

        } catch (DaoException ex)
        {
            LOGGER.error("Check login credentials command, error ocured", ex);
            return String.valueOf(STATE_ERROR_OCCURED);
        }
    }

    private User getUser(String email, String hashedPassword) throws DaoException
    {
        try (UserDao userDao = new UserDao())
        {
            User user = userDao.findByEmail(email.toLowerCase());
            return user != null && user.getHashedPassword().equals(hashedPassword) ? user : null;
        }
    }
}
