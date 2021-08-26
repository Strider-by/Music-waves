package by.musicwaves.command.xhr;

import by.musicwaves.dao.AlbumDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.logic.AlbumImageLoader;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to handle album image uploading. <br>
 * Passed image will be validated. Old album image will be deleted. <br><br>
 * Access restrictions: ADMINISTRATOR and CURATOR level users only.
 */
public class UploadAlbumImageCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INSUFFICIENT_RIGHTS = 2;
    private final static int STATE_INVALID_IMAGE = 3;
    private final static int STATE_INVALID_DATA = 4;
    
    private final static String PARAM_NAME_ID = "id";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("UPLOAD ALBUM IMAGE command reached");

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

        if (user.getRole() != Role.CURATOR && user.getRole() != Role.ADMINISTRATOR)
        {
            LOGGER.debug("User is logged in but has no rights to execute this command");
            LOGGER.debug("User role: " + user.getRole());
            json.appendResponceCode(STATE_INSUFFICIENT_RIGHTS);
            json.closeJson();
            return json.toString();
        }

        try
        {
            int albumId = Integer.parseInt(request.getParameter(PARAM_NAME_ID));

            // downloading image
            AlbumImageLoader imageLoader = new AlbumImageLoader();
            String newImageFileName = imageLoader.upload(request, albumId);
            if (newImageFileName == null)
            {
                LOGGER.debug("Something is wrong with the passed image");
                json.appendResponceCode(STATE_INVALID_IMAGE);
                json.closeJson();
                return json.toString();
            }

            try (AlbumDao dao = new AlbumDao())
            {
                Album album = dao.findById(albumId);
                if (album == null)
                {
                    LOGGER.debug("We failed to find album using provided id");
                    json.appendResponceCode(STATE_INVALID_DATA);
                    json.closeJson();
                    imageLoader.deleteImageFile(newImageFileName);
                    return json.toString();
                }

                // setting new image, holding old image just in case for now
                String oldImageFileName = album.getImageFileName();
                album.setImageFileName(newImageFileName);

                dao.update(album);

                // success
                imageLoader.deleteImageFile(oldImageFileName);
                json.appendResponceCode(STATE_SUCCESS);
                json.appendString("albumImage", newImageFileName);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("UPLOAD ALBUM IMAGE command, error occured", ex);
                imageLoader.deleteImageFile(newImageFileName);
                json.appendResponceCode(STATE_ERROR_OCCURED);
                json.closeJson();
                return json.toString();
            }
        } catch (IllegalArgumentException | NullPointerException ex)
        {
            LOGGER.debug("Data did not pass inner verification", ex);
            json.appendResponceCode(STATE_INVALID_DATA);
            json.closeJson();
            return json.toString();
        }
    }
}
