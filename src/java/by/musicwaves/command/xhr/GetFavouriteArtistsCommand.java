package by.musicwaves.command.xhr;

import by.musicwaves.dao.ArtistDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.Artist;
import by.musicwaves.entity.User;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to get list of artists set by user as favourite 
 * and return it as JSON-object to the requesting side. <br><br>
 * Data activity state: ACTIVE ONLY <br>
 * Access restrictions: any level users, login required
 */
public class GetFavouriteArtistsCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    
    private final static String PARAM_NAME_SEARCH_STRING = "search_string";
    private final static String PARAM_NAME_LIMIT = "limit";
    private final static String PARAM_NAME_OFFSET = "offset";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("GET FAVOURITE ARTISTS command reached");

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
            int limit = Integer.parseInt(request.getParameter(PARAM_NAME_LIMIT));
            int offset = Integer.parseInt(request.getParameter(PARAM_NAME_OFFSET));

            if (limit < 0 || offset < 0)
            {
                throw new IllegalArgumentException("Invalid search parameters");
            }

            try (ArtistDao dao = new ArtistDao())
            {
                List<Artist> artists = dao.findFavouriteArtists(user.getId(), "%" + searchString + "%", limit, offset);

                parseAndWrapSearchResult(json, artists);

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("GET FAVOURITE ARTISTS command, error occured", ex);
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

    private void parseAndWrapSearchResult(JsonSelfWrapper json, List<Artist> artists)
    {
        json.openArray("artists");

        for (Artist artist : artists)
        {
            json.openObject();
            json.appendNumber("id", artist.getId());
            json.appendString("name", artist.getName());
            json.appendString("image", artist.getImageFileName());
            json.closeObject();
        }

        json.closeArray();
    }
}
