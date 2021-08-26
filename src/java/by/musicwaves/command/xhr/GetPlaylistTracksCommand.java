package by.musicwaves.command.xhr;

import by.musicwaves.command.CommandException;
import by.musicwaves.dao.CrossEntityDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.User;
import by.musicwaves.util.HtmlStringEscapeTool;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.List;
import java.util.Map;

/**
 * This XHRProcessor is designed to get list of tracks in playlist (shorten data,
 * enough only to build playlist representation) 
 * and return it as JSON-object to the requesting side. <br><br>
 * Data activity state: ALL
 * Access restrictions: any level users, login required.
 */
public class GetPlaylistTracksCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    
    private final static String PARAM_NAME_ID = "playlist_id";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("GET PLAYLIST TRACKS command reached");

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

        try
        {
            int playlistId = Integer.parseInt(request.getParameter(PARAM_NAME_ID));
            try (CrossEntityDao dao = new CrossEntityDao())
            {
                List<List<Map<String, String>>> searchResult = dao.getPlaylistTracks(user.getId(), playlistId);

                if (searchResult == null || searchResult.size() != 1)
                {
                    throw new CommandException("query result does not meet expectations");
                }

                parsePlaylistData(json, searchResult.get(0));

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException | CommandException ex)
            {
                LOGGER.error("GET PLAYLIST TRACKS command, error occured", ex);
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

    private void parsePlaylistData(JsonSelfWrapper json, List<Map<String, String>> rows) throws CommandException
    {
        HtmlStringEscapeTool escapeTool = new HtmlStringEscapeTool();
        json.openArray("playlist_items");

        for (Map<String, String> row : rows)
        {
            try
            {
                int itemId = Integer.parseInt(row.get("item_id"));
                int trackId = Integer.parseInt(row.get("track_id"));
                String trackName = escapeTool.escape(row.get("track_name"));
                // since there is no real boolean in MySQL, 1 is local "true", 0 is local false
                boolean active = row.get("is_active_item").equals("1");

                json.openObject();
                json.appendNumber("item_id", itemId);
                json.appendNumber("track_id", trackId);
                json.appendString("track_name", trackName);
                json.appendBoolean("active", active);
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
