package by.musicwaves.command.xhr;

import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.AudioTrackDao;
import by.musicwaves.entity.AudioTrack;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import by.musicwaves.entity.ancillary.EntityNameValueRestraints;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to create and save new AudioTrack instance. <br><br>
 * Passed track name will be validated. <br><br>
 * Access restrictions: ADMINISTRATOR and CURATOR level users only.
 */
public class CreateAudioTrackCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_INSUFFICIENT_RIGHTS = 3;
    
    private final static String PARAM_NAME_ALBUM_ID = "album_id";
    private final static String PARAM_NAME_ACTIVITY_STATE = "active";
    private final static String PARAM_NAME_TRACK_NAME = "name";
    private final static String PARAM_NAME_GENRE_ID = "genre_id";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("CREATE AUDIOTRACK command reached");

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
            String trackName = request.getParameter(PARAM_NAME_TRACK_NAME);
            int albumId = Integer.parseInt(request.getParameter(PARAM_NAME_ALBUM_ID));
            int genreId = Integer.parseInt(request.getParameter(PARAM_NAME_GENRE_ID));
            boolean trackActivityState = Boolean.parseBoolean(request.getParameter(PARAM_NAME_ACTIVITY_STATE));

            if (!EntityNameValueRestraints.AUDIOTRACK.validateName(trackName))
            {
                throw new IllegalArgumentException();
            }

            try (AudioTrackDao dao = new AudioTrackDao())
            {
                AudioTrack trackBeingCreated = new AudioTrack();
                trackBeingCreated.setName(trackName);
                trackBeingCreated.setAlbumId(albumId);
                trackBeingCreated.setGenreId(genreId);
                trackBeingCreated.setActive(trackActivityState);

                dao.createGeneratingTrackNumber(trackBeingCreated);

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("CREATE AUDIOTRACK command, error occured", ex);
                json.appendResponceCode(STATE_ERROR_OCCURED);
                json.closeJson();
                return json.toString();
            }
        } catch (IllegalArgumentException ex)
        {
            LOGGER.debug("Data did not pass inner verification", ex);
            json.appendResponceCode(STATE_INVALID_DATA);
            json.closeJson();
            return json.toString();
        }
    }
}
