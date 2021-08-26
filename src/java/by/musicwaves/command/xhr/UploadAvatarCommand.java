package by.musicwaves.command.xhr;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.UserDao;
import by.musicwaves.entity.User;
import by.musicwaves.logic.UserImageLoader;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to handle user's avatar uploading. <br>
 * Passed image will be validated. Old user's avatar file will be deleted. <br><br>
 * Access restrictions: any level users, login required.
 */
public class UploadAvatarCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_IMAGE = 2;

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("UPLOAD AVATAR command reached");

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

        UserImageLoader avatarWorker = new UserImageLoader();
        String newAvatarFileName = avatarWorker.upload(request, user.getId());
        if (newAvatarFileName == null)
        {
            LOGGER.debug("Something went wrong, avatar will not be updates");
            json.appendResponceCode(STATE_INVALID_IMAGE);
            json.closeJson();
            return json.toString();
        }

        // setting new image, holding old image just in case for now
        String oldAvatarFileName = user.getAvatarFileName();
        user.setAvatarFileName(newAvatarFileName);

        try (UserDao userDao = new UserDao())
        {
            userDao.update(user);

            // success
            avatarWorker.deleteImageFile(oldAvatarFileName);
            json.appendResponceCode(STATE_SUCCESS);
            json.appendString("avatarFile", newAvatarFileName);
            json.closeJson();
            return json.toString();

        } catch (DaoException ex)
        {
            LOGGER.error("UPLOAD AVATAR command, error occured", ex);
            user.setAvatarFileName(oldAvatarFileName);

            json.appendResponceCode(STATE_ERROR_OCCURED);
            json.closeJson();
            return json.toString();
        }
    }
}
