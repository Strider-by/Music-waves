package by.musicwaves.command.xhr;

import by.musicwaves.dao.AlbumDao;
import by.musicwaves.dao.DaoException;
import by.musicwaves.entity.Album;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to search albums bound to chosen artist, 
 * filtering them according to the given album name and album year values. <br> 
 * Search result will be returned to the requesting side packed in JSON object. <br><br>
 * Data activity state: ACTIVE ONLY
 * Access restrictions: any level users, login required
 */
public class SearchAlbumsCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_INSUFFICIENT_RIGHTS = 3;
    
    private final static String PARAM_NAME_ARTIST_ID = "artist_id";
    private final static String PARAM_NAME_ALBUM_NAME = "name";
    private final static String PARAM_NAME_ALBUM_YEAR = "year";
    private final static String PARAM_NAME_OFFSET = "search_offset";
    private final static String PARAM_NAME_LIMIT = "search_limit";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("SEARCH ALBUMS command reached");

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
            String albumName = request.getParameter(PARAM_NAME_ALBUM_NAME);
            String albumYear = request.getParameter(PARAM_NAME_ALBUM_YEAR);

            int offset = Integer.parseInt(request.getParameter(PARAM_NAME_OFFSET));
            int limit = Integer.parseInt(request.getParameter(PARAM_NAME_LIMIT));
            int artistId = Integer.parseInt(request.getParameter(PARAM_NAME_ARTIST_ID));

            if (offset < 0 || limit < 0)
            {
                throw new IllegalArgumentException();
            }

            try (AlbumDao dao = new AlbumDao())
            {
                List<Album> searchResult = dao.findByArtistIdAndAlbumNamePatternAndYearPattern(artistId, "%" + albumName + "%", albumYear + "%", limit, offset);

                wrapSearchResult(json, searchResult, "albums");
                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException ex)
            {
                LOGGER.debug("SEARCH ALBUMS command, error occured", ex);
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

    private void wrapSearchResult(JsonSelfWrapper json, List<Album> list, String arrName)
    {
        json.openArray(arrName);

        for (int i = 0 ; i < list.size() ; i++)
        {
            Album album = list.get(i);

            json.openObject();
            json.appendNumber("id", album.getId());
            json.appendString("name", album.getName());
            json.appendBoolean("active", album.isActive());
            json.appendString("image", album.getImageFileName());
            json.appendNumber("year", album.getYear());
            json.closeObject();
        }

        json.closeArray();
    }
}
