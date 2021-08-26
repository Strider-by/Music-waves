package by.musicwaves.command.xhr;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.UserDao;
import by.musicwaves.entity.User;
import by.musicwaves.logic.RegistrationWorker;
import by.musicwaves.util.MailWorker;
import by.musicwaves.util.PasswordWorker;
import by.musicwaves.util.URLWorker;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to create new user account using passed email and
 * password. If the email we get here already presents in our database, account will not
 * be created. Passed email and password will be validated. If account was successfully created,
 * a confirmational code that is needed to activate account will be sent.
 */
public class RegisterUserAccountCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_PASSWORD_DOESNT_MATCH_REQUIRMENTS = 1;
    private final static int STATE_PASSWORDS_DO_NOT_MATCH = 2;
    private final static int STATE_EMAIL_IS_ALREADY_IN_USE = 3;
    private final static int STATE_INVALID_EMAIL = 4;
    private final static int STATE_FAILED_TO_SEND_EMAIL = 5;
    
    private final static String PARAM_NAME_EMAIL = "email";
    private final static String PARAM_NAME_PASSWORD = "password";
    private final static String PARAM_NAME_CONF_PASSWORD = "password2";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {

        String email = request.getParameter(PARAM_NAME_EMAIL);
        String password1 = request.getParameter(PARAM_NAME_PASSWORD);
        String password2 = request.getParameter(PARAM_NAME_CONF_PASSWORD);

        LOGGER.debug("email: " + email);
        LOGGER.debug("password: " + password1);
        LOGGER.debug("password (repeat): " + password2);

        try (UserDao userDao = new UserDao())
        {
            User foundUser = userDao.findByEmail(email);

            LOGGER.debug("checking user in database...");
            LOGGER.debug("user is: " + foundUser);

            if (foundUser != null)
            {
                LOGGER.debug("response: this e-mail is already in use!");
                return String.valueOf(STATE_EMAIL_IS_ALREADY_IN_USE);
            }

            RegistrationWorker registrationWorker = new RegistrationWorker();
            if (email == null || !registrationWorker.validateEmail(email))
            {
                LOGGER.debug("response: this isn't valid e-mail");
                return String.valueOf(STATE_INVALID_EMAIL);
            }

            if (password1 == null || !registrationWorker.validatePassword(password1))
            {
                LOGGER.debug("response: invalid password");
                return String.valueOf(STATE_PASSWORD_DOESNT_MATCH_REQUIRMENTS);
            }

            if (!password1.equals(password2))
            {
                LOGGER.debug("response: passwords don't match each other");
                return String.valueOf(STATE_PASSWORDS_DO_NOT_MATCH);
            }

            // creating conf. code and hashed password
            String confCode = registrationWorker.generateRegisterCode();
            String hashedPassword = PasswordWorker.processPasswordHashing(password1);

            // adding user to database
            User user = new User();
            user.setHashedPassword(hashedPassword);
            user.setEmail(email);
            user.setConfCode(confCode);

            Integer userId = userDao.create(user);

            if (userId == null) // == somehow creation failed. This kinda isn't possible, but...
            {
                LOGGER.debug("We failed in new user creation. Shame.");
                return String.valueOf(STATE_ERROR_OCCURED);
            }

            // sending conf. code
            String logoRelFilePath = ResourceBundle.getBundle("resources.email").getString("mail.body.picture");
            String logoRealFilePath = URLWorker.getRealFilePath(request, logoRelFilePath);

            MailWorker mailWorker = new MailWorker();
            boolean mailSent = mailWorker.sendCode(email, confCode, logoRealFilePath);
            if (!mailSent)
            {
                LOGGER.debug("We failed to send email. There may be various reasons, invalid email is among them.");
                return String.valueOf(STATE_FAILED_TO_SEND_EMAIL);
            }

            LOGGER.debug("It seems there was no errors, so... Success?");
            return String.valueOf(STATE_SUCCESS);

        } catch (DaoException ex)
        {
            LOGGER.error("Register user account command, error occured", ex);
            return String.valueOf(STATE_ERROR_OCCURED);
        }
    }
}
