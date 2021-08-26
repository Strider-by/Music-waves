package by.musicwaves.command.xhr;

import by.musicwaves.command.CommandException;
import by.musicwaves.dao.CrossEntityDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.User;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to get single artist data and bound albums data 
 * and return it as JSON-object to the requesting side. <br><br>
 * Data activity state: ALL <br>
 * Access restrictions: ADMINISTRATOR and CURATOR level users only
 */
public class GetArtistAlbumsDataCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    
    private final static String PARAM_NAME_ARTIST_ID = "artist_id";
    private final static String PARAM_NAME_SEARCH_STRING = "search_string";
    private final static String PARAM_NAME_LIMIT = "limit";
    private final static String PARAM_NAME_OFFSET = "offset";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("GET ARTIST ALBUMS command reached");

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
            String searchString = request.getParameter(PARAM_NAME_SEARCH_STRING);
            int artistId = Integer.parseInt(request.getParameter(PARAM_NAME_ARTIST_ID));
            int limit = Integer.parseInt(request.getParameter(PARAM_NAME_LIMIT));
            int offset = Integer.parseInt(request.getParameter(PARAM_NAME_OFFSET));

            if (searchString == null || limit < 0 || offset < 0)
            {
                throw new IllegalArgumentException("Invalid search parameters");
            }

            try (CrossEntityDao dao = new CrossEntityDao())
            {
                List<List<Map<String, String>>> queryResult = dao.getArtistAlbumsData(artistId, "%" + searchString + "%", limit, offset);

                if (queryResult == null || queryResult.size() != 1)
                {
                    throw new CommandException("query result does not meet expectations");
                }

                parseAndWrapSearchResult(json, queryResult.get(0));

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException | CommandException ex)
            {
                LOGGER.error("GET ARTIST ALBUMS command, error occured", ex);
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

    private void parseAndWrapSearchResult(JsonSelfWrapper json, List<Map<String, String>> data) throws CommandException
    {
        json.openArray("albums");

        for (Map<String, String> row : data)
        {
            try
            {
                int artistId = Integer.parseInt(row.get("artist_id"));
                int albumId = Integer.parseInt(row.get("album_id"));
                String artistName = row.get("artist_name");
                String albumName = row.get("album_name");
                String artistImage = row.get("artist_image");
                String albumImage = row.get("album_image");
                int albumYear = Integer.parseInt(row.get("album_year"));

                json.openObject();

                json.openObject("artist");
                json.appendNumber("id", artistId);
                json.appendString("name", artistName);
                json.appendString("image", artistImage);
                json.closeObject();

                json.openObject("album");
                json.appendNumber("id", albumId);
                json.appendString("name", albumName);
                json.appendString("image", albumImage);
                json.appendNumber("year", albumYear);
                json.closeObject();

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
