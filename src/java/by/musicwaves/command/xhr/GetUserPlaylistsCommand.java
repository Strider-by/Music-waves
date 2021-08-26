package by.musicwaves.command.xhr;

import by.musicwaves.command.CommandException;
import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.PlaylistDao;
import by.musicwaves.entity.Playlist;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.util.HtmlStringEscapeTool;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to get user playlists list and return it 
 * as JSON-object to the requesting side. <br><br>
 * Access restrictions: any level users, login required
 */
public class GetUserPlaylistsCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INSUFFICIENT_RIGHTS = 2;
    
    private final static String PARAM_NAME_DO_ESCAPE = "escape";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("GET USER PLAYLISTS command reached");

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
            json.appendResponceCode(STATE_INSUFFICIENT_RIGHTS);
            json.closeJson();
            return json.toString();
        }

        try (PlaylistDao dao = new PlaylistDao())
        {
            List<Playlist> searchResult = dao.findByUserId(user.getId());

            // finding out if certain symbols should be escaped in the json we pass to client,
            // default behaviour (in case if this parameter was not passed) is - TO ESCAPE
            String escapeStringParam = request.getParameter(PARAM_NAME_DO_ESCAPE);
            // parseBoolean takes null as FALSE so we don't need additional check here
            boolean escape = Boolean.parseBoolean(escapeStringParam);

            parsePlaylistsData(json, searchResult, escape);

            json.appendResponceCode(STATE_SUCCESS);
            json.closeJson();
            return json.toString();

        } catch (DaoException | CommandException ex)
        {
            LOGGER.error("GET USER PLAYLISTS command, error occured", ex);
            json.appendResponceCode(STATE_ERROR_OCCURED);
            json.closeJson();
            return json.toString();
        }
    }

    private void parsePlaylistsData(JsonSelfWrapper json, List<Playlist> playlists, boolean escape) throws CommandException
    {
        HtmlStringEscapeTool escapeTool = new HtmlStringEscapeTool();
        json.openArray("playlists");

        for (Playlist playlist : playlists)
        {
            json.openObject();
            json.appendNumber("id", playlist.getId());
            json.appendString("name", escape ? escapeTool.escape(playlist.getName()) : playlist.getName());
            json.closeObject();
        }

        json.closeArray();
    }
}
