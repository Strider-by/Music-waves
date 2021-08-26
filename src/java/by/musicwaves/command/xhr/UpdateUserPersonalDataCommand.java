package by.musicwaves.command.xhr;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.UserDao;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Country;
import by.musicwaves.entity.ancillary.Language;
import by.musicwaves.entity.ancillary.PersonalDataValueRestraints;
import by.musicwaves.resource.LocalizationBundle;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to update user's data by himself. <br>
 * Some fields are locked from changing. <br>
 * Passed values will be validated. <br>
 * After user has set his nickname for the first time, his registration is completed
 * and his role is to be changed from NOT_REGISTERED_USER to USER. <br><br>
 * Access restrictions: any level users, login required
 */
public class UpdateUserPersonalDataCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    
    private final static String PARAM_NAME_NICKNAME = "nickname";
    private final static String PARAM_NAME_FIRST_NAME = "first_name";
    private final static String PARAM_NAME_LAST_NAME = "last_name";
    private final static String PARAM_NAME_SEX = "sex";
    private final static String PARAM_NAME_COUNTRY = "country";
    private final static String PARAM_NAME_LANGUAGE = "language";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("UPDATE PERSONAL DATA command reached");

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

        PersonalDataValueRestraints personalDataWorker = new PersonalDataValueRestraints();
        String nickname = request.getParameter(PARAM_NAME_NICKNAME);
        String firstName = request.getParameter(PARAM_NAME_FIRST_NAME);
        String lastName = request.getParameter(PARAM_NAME_LAST_NAME);
        String sex = request.getParameter(PARAM_NAME_SEX);
        String country = request.getParameter(PARAM_NAME_COUNTRY);
        String language = request.getParameter(PARAM_NAME_LANGUAGE);

        if (!personalDataWorker.validateNickname(nickname)
                || !personalDataWorker.validateName(firstName)
                || !personalDataWorker.validateName(lastName)
                || !personalDataWorker.validateSex(sex)
                || !personalDataWorker.validateCountry(country)
                || !personalDataWorker.validateLanguage(language))
        {
            LOGGER.debug("Something is wrong with passed data, new personal data values won't be saved");
            json.appendResponceCode(STATE_INVALID_DATA);
            json.closeJson();
            return json.toString();
        }

        // we keep this in case of error to be able to roll everything back
        Role oldRole = user.getRole();
        HttpSession session = request.getSession();
        LocalizationBundle oldLocalizationBundle = user.getLanguage().getLocalizationBundle();

        user.setNickname(nickname);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setSex(User.Sex.parseDatabaseEquivalent(Integer.parseInt(sex)));
        user.setCountry(Country.parseDatabaseEquivalent(Integer.parseInt(country)));
        user.setLanguage(Language.parseDatabaseEquivalent(Integer.parseInt(language)));

        // after user has set valid nickname he can be counted as registered user so let's change his role
        if (user.getRole() == Role.NOT_REGISTERED_USER)
        {
            user.setRole(Role.USER);
        }

        session.setAttribute("localizationBundle", user.getLanguage().getLocalizationBundle());

        try (UserDao userDao = new UserDao())
        {
            userDao.update(user);

            json.appendResponceCode(STATE_SUCCESS);
            json.closeJson();
            return json.toString();

        } catch (DaoException ex)
        {
            LOGGER.error("UPDATE PERSONAL DATA command, error occured", ex);
            user.setRole(oldRole);
            session.setAttribute("localizationBundle", oldLocalizationBundle);
            json.appendResponceCode(STATE_ERROR_OCCURED);
            json.closeJson();
            return json.toString();
        }
    }
}
