package by.musicwaves.command.xhr;

import by.musicwaves.dao.ArtistDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.Artist;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import static by.musicwaves.logic.SearchLogic.Activity;
import static by.musicwaves.logic.SearchLogic.SearchType;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to search artists, 
 * filtering them according to the given search string value and their activity state. <br>
 * Strict and non-strict search modes are supported. <br>
 * Search result will be returned to the requesting side packed in JSON object. <br><br>
 * Data activity state: ALL <br>
 * Access restrictions: ADMINISTRATOR and CURATOR level users only
 */
public class SearchArtistsCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_INSUFFICIENT_RIGHTS = 3;
    
    private final static String PARAM_NAME_SEARCH_TYPE = "search_type";
    private final static String PARAM_NAME_SEARCH_ACTIVITY = "search_activity";
    private final static String PARAM_NAME_SEARCH_STRING = "search_string";
    private final static String PARAM_NAME_OFFSET = "search_offset";
    private final static String PARAM_NAME_LIMIT = "search_limit";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("SEARCH ARTIST command reached");

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
            String searchString = request.getParameter(PARAM_NAME_SEARCH_STRING);
            SearchType searchType = SearchType.parse(request.getParameter(PARAM_NAME_SEARCH_TYPE));
            Activity activity = Activity.parse(request.getParameter(PARAM_NAME_SEARCH_ACTIVITY));
            int offset = Integer.parseInt(request.getParameter(PARAM_NAME_OFFSET));
            int limit = Integer.parseInt(request.getParameter(PARAM_NAME_LIMIT));

            if (searchType == null || activity == null || offset < 0 || limit < 0)
            {
                throw new IllegalArgumentException();
            }

            try (ArtistDao dao = new ArtistDao())
            {
                List<Artist> searchResult = null;

                switch (searchType)
                {
                    case STRICT:
                        switch (activity)
                        {
                            case ALL:
                                searchResult = dao.findByName(searchString, limit, offset);
                                break;
                            case ACTIVE_ONLY:
                                searchResult = dao.findByName(searchString, true, limit, offset);
                                break;
                            case INACTIVE_ONLY:
                                searchResult = dao.findByName(searchString, false, limit, offset);
                                break;
                        }
                        break;

                    case CONTAINS:
                        searchString = "%" + searchString + "%";
                        switch (activity)
                        {
                            case ALL:
                                searchResult = dao.findByNamePattern(searchString, limit, offset);
                                break;
                            case ACTIVE_ONLY:
                                searchResult = dao.findByNamePattern(searchString, true, limit, offset);
                                break;
                            case INACTIVE_ONLY:
                                searchResult = dao.findByNamePattern(searchString, false, limit, offset);
                                break;
                        }
                        break;
                }

                wrapSearchResult(json, searchResult, "artists");
                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.error("SEARCH ARTIST command, error occured", ex);
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

    private void wrapSearchResult(JsonSelfWrapper json, List<Artist> list, String arrName)
    {
        json.openArray(arrName);

        for (int i = 0 ; i < list.size() ; i++)
        {
            Artist artist = list.get(i);

            json.openObject();
            json.appendNumber("id", artist.getId());
            json.appendString("name", artist.getName());
            json.appendBoolean("active", artist.isActive());
            json.appendString("image", artist.getImageFileName());
            json.closeObject();
        }

        json.closeArray();
    }
}
