package by.musicwaves.command.xhr;

import by.musicwaves.dao.ArtistDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.User;
import by.musicwaves.entity.Artist;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.logic.ArtistImageLoader;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to handle artist image uploading. <br>
 * Passed image will be validated. Old artist image will be deleted. <br><br>
 * Access restrictions: ADMINISTRATOR and CURATOR level users only.
 */
public class UploadArtistImageCommand implements XHRProcessor
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

        LOGGER.debug("UPLOAD ARTIST IMAGE command reached");

        JsonSelfWrapper json = new JsonSelfWrapper();
        json.openJson();

        User user = (User) request.getSession().getAttribute("user");
        if (user == null)
        {
            LOGGER.debug("Someone tries to run this command without being authorized.");
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
            int artistId = Integer.parseInt(request.getParameter(PARAM_NAME_ID));

            // downloading image
            ArtistImageLoader imageLoader = new ArtistImageLoader();
            String newImageFileName = imageLoader.upload(request, artistId);
            if (newImageFileName == null)
            {
                LOGGER.debug("Something is wrong with the passed image");
                json.appendResponceCode(STATE_INVALID_IMAGE);
                json.closeJson();
                return json.toString();
            }

            try (ArtistDao dao = new ArtistDao())
            {
                Artist artist = dao.findById(artistId);
                if (artist == null)
                {
                    LOGGER.debug("We failed to find artist using provided id");
                    json.appendResponceCode(STATE_INVALID_DATA);
                    json.closeJson();
                    imageLoader.deleteImageFile(newImageFileName);
                    return json.toString();
                }

                // setting new image, holding old image just in case for now
                String oldImageFileName = artist.getImageFileName();
                artist.setImageFileName(newImageFileName);

                dao.update(artist);

                // success
                imageLoader.deleteImageFile(oldImageFileName);
                json.appendResponceCode(STATE_SUCCESS);
                json.appendString("artistImage", newImageFileName);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.debug("UPLOAD ARTIST IMAGE command, error occured", ex);
                imageLoader.deleteImageFile(newImageFileName);
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
