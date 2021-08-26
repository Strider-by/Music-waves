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
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import java.util.List;

/**
 * This XHRProcessor is designed to search artists using passed search string value. 
 * Found data will be returned as JSON-object to the requesting side. <br><br>
 * Data activity state: ACTIVE ONLY <br>
 * Access restrictions: users that haven't completed registration 
 * (NOT_REGISTERED_USER level users) are not permitted to use it
 */
public class MusicSearchGetFoundArtistsListCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_INSUFFICIENT_RIGHTS = 3;
    
    private final static String PARAM_NAME_SEARCH_STRING = "search_string";
    private final static String PARAM_NAME_OFFSET = "offset";
    private final static String PARAM_NAME_LIMIT = "limit";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("Music Search: get found artists command reached");

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
            String searchString = request.getParameter(PARAM_NAME_SEARCH_STRING);
            int offset = Integer.parseInt(request.getParameter(PARAM_NAME_OFFSET));
            int limit = Integer.parseInt(request.getParameter(PARAM_NAME_LIMIT));

            if (offset < 0 || limit < 0)
            {
                throw new IllegalArgumentException("Invalid limit or offset parameter");
            }

            try (CrossEntityDao dao = new CrossEntityDao())
            {
                List<List<Map<String, String>>> searchResult
                        = dao.processMusicSearchFindArtists("%" + searchString + "%", user.getId(), limit, offset);

                if (searchResult == null || searchResult.size() != 2)
                {
                    throw new CommandException("query result does not meet expectations");
                }

                parseSearchResultsQuantity(json, searchResult.get(0));
                parseFoundArtists(json, searchResult.get(1));

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException | CommandException ex)
            {
                LOGGER.error("Music Search: get artists search results quantity command, error occured", ex);
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

    private void parseSearchResultsQuantity(JsonSelfWrapper json, List<Map<String, String>> rows) throws CommandException
    {
        int artistsCount, albumsCount, tracksCount;

        for (Map<String, String> row : rows)
        {
            try
            {
                artistsCount = Integer.parseInt(row.get("artists_count"));
                albumsCount = Integer.parseInt(row.get("albums_count"));
                tracksCount = Integer.parseInt(row.get("tracks_count"));

                json.openObject("results_quantity");
                json.appendNumber("artists", artistsCount);
                json.appendNumber("albums", albumsCount);
                json.appendNumber("tracks", tracksCount);
                json.closeObject();

            } catch (NumberFormatException | NullPointerException ex)
            {
                throw new CommandException("failed to process gotten request result values", ex);
            }
        }
    }

    private void parseFoundArtists(JsonSelfWrapper json, List<Map<String, String>> rows) throws CommandException
    {
        json.openArray("artists");

        for (Map<String, String> row : rows)
        {
            try
            {
                int artistId = Integer.parseInt(row.get("artist_id"));
                String artistName = row.get("artist_name");
                String artistImage = row.get("artist_image");
                // since there is no real boolean in MySQL, 1 is local "true", 0 is local false
                boolean favourite = row.get("favourite").equals("1");
                int albumsQuantityArtistHas = Integer.parseInt(row.get("albums_count_artist_has"));

                json.openObject();
                json.appendNumber("artist_id", artistId);
                json.appendString("artist_name", artistName);
                json.appendString("artist_image", artistImage);
                json.appendBoolean("favourite", favourite);
                json.appendNumber("albums_count_artist_has", albumsQuantityArtistHas);
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
