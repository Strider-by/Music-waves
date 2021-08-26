package by.musicwaves.command.xhr;

import by.musicwaves.dao.AudioTrackDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.AudioTrack;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.logic.AudioTrackFileLoader;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to handle audiotrack file uploading. <br>
 * Passed file will be validated. Old audiofile will be deleted. <br><br>
 * Access restrictions: ADMINISTRATOR and CURATOR level users only.
 */
public class UploadAudioTrackFileCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INSUFFICIENT_RIGHTS = 2;
    private final static int STATE_INVALID_FILE = 3;
    private final static int STATE_INVALID_DATA = 4;
    
    private final static String PARAM_NAME_ID = "id";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {

        LOGGER.debug("UPLOAD AUDIO TRACK command reached");

        JsonSelfWrapper json = new JsonSelfWrapper();
        json.openJson();

        User user = (User) request.getSession().getAttribute("user");
        if (user == null)
        {
            LOGGER.debug("Someone tries to run this command without being authorized. Maybe session is expired?");
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
            int trackId = Integer.parseInt(request.getParameter(PARAM_NAME_ID));
            AudioTrackFileLoader loader = new AudioTrackFileLoader();
            String newFileName = null;

            try (AudioTrackDao dao = new AudioTrackDao())
            {
                AudioTrack track = dao.findById(trackId);
                if (track == null)
                {
                    LOGGER.debug("We failed to find instance using provided id");
                    json.appendResponceCode(STATE_INVALID_DATA);
                    json.closeJson();
                    return json.toString();
                }

                // downloading file
                newFileName = loader.upload(request, trackId);
                if (newFileName == null)
                {
                    LOGGER.debug("Something is wrong with the passed file");
                    json.appendResponceCode(STATE_INVALID_FILE);
                    json.closeJson();
                    return json.toString();
                }

                String oldFileName = track.getFileName();
                track.setFileName(newFileName);

                dao.update(track);

                // success
                loader.deleteTrackFile(oldFileName);
                json.appendResponceCode(STATE_SUCCESS);
                json.appendString("trackFileName", newFileName);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("UPLOAD AUDIO TRACK command, error occured", ex);
                loader.deleteTrackFile(newFileName);
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
