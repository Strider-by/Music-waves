package by.musicwaves.command.xhr;

import by.musicwaves.command.CommandException;
import by.musicwaves.dao.CrossEntityDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.util.JsonSelfWrapper;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to get list of tracks being content in a particular album
 * + artist and album bound data,
 * and return it as JSON-object to the requesting side. <br><br>
 * Data activity state: ACTIVE ONLY <br>
 * Access restrictions: users that haven't completed registration 
 * (NOT_REGISTERED_USER level users) are not permitted to use it
 */
public class MusicSearchGetChosenAlbumTracksCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_INSUFFICIENT_RIGHTS = 3;
    
    private final static String PARAM_NAME_ALBUM_ID = "album_id";
    private final static String PARAM_NAME_OFFSET = "offset";
    private final static String PARAM_NAME_LIMIT = "limit";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("Music Search: GET CHOSEN ALBUM command reached");

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

        if (user.getRole() == Role.NOT_REGISTERED_USER)
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
                throw new IllegalArgumentException("Invalid limit or offset parameter");
            }

            try (CrossEntityDao dao = new CrossEntityDao())
            {
                List<List<Map<String, String>>> searchResult
                        = dao.processMusicSearchGetChosenAlbumTracksList(user.getId(), albumId, limit, offset);

                if (searchResult == null || searchResult.size() != 2)
                {
                    throw new CommandException("query result does not meet expectations");
                }

                parseAlbumData(json, searchResult.get(0));
                parseTracksData(json, searchResult.get(1));

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException | CommandException ex)
            {
                LOGGER.error("Music Search: get album search results quantity command, error occured", ex);
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

    private void parseAlbumData(JsonSelfWrapper json, List<Map<String, String>> rows) throws CommandException
    {
        for (Map<String, String> row : rows)
        {
            try
            {
                String artistName = row.get("artist_name");
                String artistImage = row.get("artist_image");
                String albumName = row.get("album_name");
                String albumImage = row.get("album_image");
                int albumYear = Integer.parseInt(row.get("album_year"));

                json.openObject("artist");
                json.appendString("name", artistName);
                json.appendString("image", artistImage);
                json.closeObject();

                json.openObject("album");
                json.appendString("name", albumName);
                json.appendString("image", albumImage);
                json.appendNumber("year", albumYear);
                json.closeObject();

            } catch (NumberFormatException | NullPointerException ex)
            {
                json.closeArray();
                throw new CommandException("failed to process gotten request result values", ex);
            }
        }
    }

    private void parseTracksData(JsonSelfWrapper json, List<Map<String, String>> rows) throws CommandException
    {
        json.openArray("tracks");

        for (Map<String, String> row : rows)
        {
            try
            {
                int trackId = Integer.parseInt(row.get("track_id"));
                String trackName = row.get("track_name");
                String fileName = row.get("track_file");
                // since there is no real boolean in MySQL, 1 is local "true", 0 is local false
                boolean favourite = row.get("favourite").equals("1");

                json.openObject();
                json.appendNumber("id", trackId);
                json.appendString("name", trackName);
                json.appendString("file", fileName);
                json.appendBoolean("favourite", favourite);
                json.closeObject();

            } catch (NumberFormatException | NullPointerException ex)
            {
                json.closeArray();
                throw new CommandException("failed to process gotten request result values", ex);
            }
        }

        json.closeArray();
    }
}
