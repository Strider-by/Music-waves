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
 * This XHRProcessor is designed to search among artists, albums and audio tracks 
 * using passed search string value. Found objects will be counted and ONLY THEIR QUANTITY 
 * will be returned as JSON-object to the requesting side. <br><br>
 * Data activity state: ACTIVE ONLY <br>
 * Access restrictions: users that haven't completed registration 
 * (NOT_REGISTERED_USER level users) are not permitted to use it
 */
public class MusicSearchGetSearchResultsQuantityCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_INSUFFICIENT_RIGHTS = 3;
    
    private final static String PARAM_NAME_SEARCH_STRING = "search_string";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("Music Search: get search results quantity command reached");

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
            String searchString = request.getParameter(PARAM_NAME_SEARCH_STRING);

            try (CrossEntityDao dao = new CrossEntityDao())
            {
                List<List<Map<String, String>>> searchResult = dao.getMusicSearchResultsQuantity("%" + searchString + "%");
                if (searchResult == null || searchResult.size() != 1)
                {
                    throw new CommandException("query result does not meet expectations");
                }

                parseAndWrapSearchResultsQuantity(json, searchResult.get(0));

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException | CommandException ex)
            {
                LOGGER.error("Music Search: get search results quantity command, error occured", ex);
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

    private void parseAndWrapSearchResultsQuantity(JsonSelfWrapper json, List<Map<String, String>> rows) throws CommandException
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
}
