package by.musicwaves.command.xhr;

import by.musicwaves.command.CommandException;
import by.musicwaves.dao.DaoException;
import by.musicwaves.dao.CrossEntityDao;
import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import by.musicwaves.logic.AlbumImageLoader;
import by.musicwaves.logic.ArtistImageLoader;
import by.musicwaves.util.JsonSelfWrapper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import by.musicwaves.logic.AudioTrackFileLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * This XHRProcessor is designed to delete existing Artist instance. <br><br>
 * All dependent data, both database-held records (albums records, 
 * audiotracks records, favourite audiotrack records, favourite albums records, 
 * favourite artist records, playlist item records) and files (artist image, 
 * albums images, audiotracks files) will be deleted as well. <br><br>
 * Access restrictions: ADMINISTRATOR and CURATOR level users only.
 */
public class DeleteArtistCommand implements XHRProcessor
{
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static int STATE_SUCCESS = 0;
    private final static int STATE_USER_NOT_LOGGED_IN = 1;
    private final static int STATE_INVALID_DATA = 2;
    private final static int STATE_INSUFFICIENT_RIGHTS = 3;
    
    private final static String PARAM_NAME_ARTIST_ID = "id";

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response)
    {
        LOGGER.debug("DELETE ARTIST command reached");

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
            String idString = request.getParameter(PARAM_NAME_ARTIST_ID);
            int id = Integer.parseInt(idString);

            try (CrossEntityDao crossEntityDao = new CrossEntityDao())
            {
                // getting tracks being deleted file names, artist and albums images,
                // [cascading] delete artist, his albums and tracks
                // deleting tracks files, album and artist images
                // deleting faved items and playlist items

                List<List<Map<String, String>>> queryResult = crossEntityDao.deleteArtist(id);
                if (queryResult == null || queryResult.size() != 3)
                {
                    throw new CommandException("query result does not meet expectations");
                }
                List<String> artistImagesNeededToBeDeletedFileNames = getImagesNames(queryResult.get(0));
                List<String> albumsImagesNeededToBeDeletedFileNames = getImagesNames(queryResult.get(1));
                List<String> tracksNeededToBeDeletedFileNames = getTracksNames(queryResult.get(2));

                ArtistImageLoader artistImageLoader = new ArtistImageLoader();
                AlbumImageLoader albumImageLoader = new AlbumImageLoader();
                AudioTrackFileLoader trackLoader = new AudioTrackFileLoader();

                for (String image : artistImagesNeededToBeDeletedFileNames)
                {
                    artistImageLoader.deleteImageFile(image);
                }
                for (String image : albumsImagesNeededToBeDeletedFileNames)
                {
                    albumImageLoader.deleteImageFile(image);
                }
                for (String fileName : tracksNeededToBeDeletedFileNames)
                {
                    trackLoader.deleteTrackFile(fileName);
                }

                json.appendResponceCode(STATE_SUCCESS);
                json.closeJson();
                return json.toString();

            } catch (DaoException | CommandException ex)
            {
                LOGGER.error("DELETE ARTIST command, error occured", ex);
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

    private List<String> getImagesNames(List<Map<String, String>> data) throws CommandException
    {
        ArrayList<String> result = new ArrayList<>();

        for (Map<String, String> row : data)
        {
            if (!row.containsKey("image"))
            {
                throw new CommandException("failed to process gotten request result values");
            }

            result.add(row.get("image"));
        }

        return result;
    }

    private List<String> getTracksNames(List<Map<String, String>> data) throws CommandException
    {
        ArrayList<String> result = new ArrayList<>();

        for (Map<String, String> row : data)
        {
            if (!row.containsKey("file"))
            {
                throw new CommandException("failed to process gotten request result values");
            }

            result.add(row.get("file"));
        }

        return result;
    }
}
