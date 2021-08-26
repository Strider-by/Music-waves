package by.musicwaves.command.xhr;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.UserDao;
import by.musicwaves.entity.User;
import by.musicwaves.util.MailWorker;
import by.musicwaves.util.URLWorker;
import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to send confirmation code on user request.
 */
public class SendConfirmatonRegCodeCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_EMAIL_NOT_FOUND = 1;
    private final static int STATE_ACCOUNT_ALREADY_ACTIVATED = 2;
    private final static int STATE_FAILED_TO_SEND_EMAIL = 3;
    
    private final static String PARAM_NAME_EMAIL = "email";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        String email = request.getParameter(PARAM_NAME_EMAIL);

        try (UserDao userDao = new UserDao())
        {
            LOGGER.debug("SEND CONFIRMATION CODE command reached");
            
            User foundUser = userDao.findByEmail(email);

            LOGGER.debug("checking user in database...");
            LOGGER.debug("user found: " + foundUser != null);

            if (foundUser == null)
            {
                LOGGER.debug("response: e-mail wasn't found");
                return String.valueOf(STATE_EMAIL_NOT_FOUND);
            }

            if (foundUser.isAccountActivated())
            {
                LOGGER.debug("response: account is alredy activated");
                return String.valueOf(STATE_ACCOUNT_ALREADY_ACTIVATED);
            }

            String confCode = foundUser.getConfCode();

            // sending conf. code
            String logoRelFilePath = ResourceBundle.getBundle("resources.email").getString("mail.body.picture");
            String logoRealFilePath = URLWorker.getRealFilePath(request, logoRelFilePath);
            MailWorker mailWorker = new MailWorker();
            boolean mailSent = mailWorker.sendCode(email, confCode, logoRealFilePath);
            if (!mailSent)
            {
                LOGGER.error("SEND CONFIRMATION CODE command, failed to send email");
                return String.valueOf(STATE_FAILED_TO_SEND_EMAIL);
            }

            LOGGER.debug("success");
            return String.valueOf(STATE_SUCCESS);

        } catch (DaoException ex)
        {
            LOGGER.error("SEND CONFIRMATION CODE command, error occured: ", ex);
            return String.valueOf(STATE_ERROR_OCCURED);
        }
    }
}
