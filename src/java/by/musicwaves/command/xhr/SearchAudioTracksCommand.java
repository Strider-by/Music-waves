package by.musicwaves.command.xhr;

import by.musicwaves.dao.AudioTrackDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.AudioTrack;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to get tracks from a single album. <br> 
 * Filtering is not supported, but search result to be returned depends on passed
 * limit and offset parameters. <br>
 * Search result will be returned to the requesting side packed in JSON object. <br><br>
 * Data activity state: ALL <br>
 * Access restrictions: ADMINISTRATOR and CURATOR level users only
 */
public class SearchAudioTracksCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_INSUFFICIENT_RIGHTS = 3;
    
    private final static String PARAM_NAME_ALBUM_ID = "album_id";
    private final static String PARAM_NAME_OFFSET = "search_offset";
    private final static String PARAM_NAME_LIMIT = "search_limit";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("SEARCH AUDIO TRACKS command reached");

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
            int albumId = Integer.parseInt(request.getParameter(PARAM_NAME_ALBUM_ID));
            int offset = Integer.parseInt(request.getParameter(PARAM_NAME_OFFSET));
            int limit = Integer.parseInt(request.getParameter(PARAM_NAME_LIMIT));

            if (offset < 0 || limit < 0)
            {
                throw new IllegalArgumentException();
            }

            try (AudioTrackDao dao = new AudioTrackDao())
            {
                List<AudioTrack> searchResult = dao.findByAlbumId(albumId);

                wrapSearchResult(json, searchResult, "audiotracks");
                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("SEARCH AUDIO TRACKS command, error occured", ex);
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

    private void wrapSearchResult(JsonSelfWrapper json, List<AudioTrack> list, String arrName)
    {
        json.openArray(arrName);

        for (int i = 0 ; i < list.size() ; i++)
        {
            AudioTrack track = list.get(i);

            json.openObject();
            json.appendNumber("id", track.getId());
            json.appendString("name", track.getName());
            json.appendString("fileName", track.getFileName());
            json.appendNumber("number", track.getTrackNumber());
            json.closeObject();
        }

        json.closeArray();
    }
}
