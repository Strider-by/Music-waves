package by.musicwaves.command.xhr;

import by.musicwaves.dao.AudioTrackDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.AudioTrack;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.entity.ancillary.EntityNameValueRestraints;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to update existing AudioTrack instance. <br><br>
 * Passed track name will be validated. <br><br>
 * Access restrictions: ADMINISTRATOR and CURATOR level users only.
 */
public class UpdateAudioTrackCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_INSUFFICIENT_RIGHTS = 3;
    
    private final static String PARAM_NAME_ID = "id";
    private final static String PARAM_NAME_NAME = "name";
    private final static String PARAM_NAME_GENRE = "genre";
    private final static String PARAM_NAME_ACTIVITY_STATE = "active";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("UPDATE AUDIO TRACK command reached");

        JsonSelfWrapper json = new JsonSelfWrapper();
        json.openJson();

        User user = (User) request.getSession().getAttribute("user");
        if (user == null)
        {
            LOGGER.info("Someone tries to run this command without being authorized");
            json.appendResponceCode(STATE_USER_NOT_LOGGED_IN);
            json.closeJson();
            return json.toString();
        }

        if (user.getRole() != Role.CURATOR && user.getRole() != Role.ADMINISTRATOR)
        {
            LOGGER.info("User is logged in but has no rights to execute this command");
            LOGGER.info("User: " + user.getId() + " | " + user.getRole());
            json.appendResponceCode(STATE_INSUFFICIENT_RIGHTS);
            json.closeJson();
            return json.toString();
        }

        try
        {
            String trackName = request.getParameter(PARAM_NAME_NAME);
            int trackGenre = Integer.parseInt(request.getParameter(PARAM_NAME_GENRE));
            int trackId = Integer.parseInt(request.getParameter(PARAM_NAME_ID));
            boolean trackActivityState = Boolean.parseBoolean(request.getParameter(PARAM_NAME_ACTIVITY_STATE));

            if (!EntityNameValueRestraints.AUDIOTRACK.validateName(trackName))
            {
                throw new IllegalArgumentException();
            }

            try (AudioTrackDao dao = new AudioTrackDao())
            {
                AudioTrack trackBeingUpdated = dao.findById(trackId);
                if (trackBeingUpdated == null)
                {
                    throw new DaoException("failed to find audiotrack with given id");
                }

                trackBeingUpdated.setId(trackId);
                trackBeingUpdated.setName(trackName);
                trackBeingUpdated.setActive(trackActivityState);
                trackBeingUpdated.setGenreId(trackGenre);

                dao.update(trackBeingUpdated);

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("UPDATE AUDIO TRACK command, error occured", ex);
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
