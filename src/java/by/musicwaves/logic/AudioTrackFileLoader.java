package by.musicwaves.logic;

import by.musicwaves.resource.ConfigurationManager;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class AudioTrackFileLoader
{
    private final static Logger LOGGER;
    private final static String[] ALLOWED_FILE_TYPES;
    private final static int NAME_BASE = 46655; // magic number
    private final static long SIMPLIFIED_TIMESTAMP_SUBSTRACT_BASE = 1_500_000_000_000L;
    private final static long MAX_ALLOWED_FILE_SIZE = 1024 * 1024 * 10; // 10Mb
    private final static String STATIC_DATA_DIRECTORY = ConfigurationManager.getProperty("application.data.static");
    private final static String AUDIOTRACK_FILES_SUBDIR = "tracks" + File.separator;

    static
    {
        LOGGER = LogManager.getLogger();
        ALLOWED_FILE_TYPES = new String[]
        {
            "audio/mpeg",
            "audio/mp3"
        };
    }

    public String upload(HttpServletRequest request, int trackId)
    {
        File fileFromRequest = getFileFromRequest(request, generateFileName(trackId));

        return fileFromRequest != null ? fileFromRequest.getName() : null;
    }

    private File getFileFromRequest(HttpServletRequest request, String fileName)
    {
        try
        {
            // constructs path of the directory to save uploaded file
            String uploadDirPath = STATIC_DATA_DIRECTORY + File.separator + AUDIOTRACK_FILES_SUBDIR;
            File uploadedFile = new File(uploadDirPath + fileName);

            // creates upload folder if it does not exists
            File uploadFolder = new File(uploadDirPath);
            if (!uploadFolder.exists())
            {
                uploadFolder.mkdirs();
            }

            // write file in folder
            for (Part part : request.getParts())
            {
                if (part != null && part.getSize() > 0)
                {
                    String contentType = part.getContentType();

                    if (!checkContentType(contentType))
                    {
                        continue;
                    }
                    part.write(uploadDirPath + File.separator + fileName);
                }
            }

            if (uploadedFile.exists())
            {
                if (uploadedFile.length() > 0 && uploadedFile.length() <= MAX_ALLOWED_FILE_SIZE)
                {
                    return uploadedFile;
                } else
                {
                    uploadedFile.delete();
                    return null;
                }
            } else
            {
                return null;
            }
        } catch (IOException | ServletException ex)
        {
            LOGGER.error("failed to download audiotrack file", ex);
            return null;
        }
    }

    public void deleteTrackFile(String fileName)
    {
        File oldFile = new File(STATIC_DATA_DIRECTORY + File.separator + AUDIOTRACK_FILES_SUBDIR + fileName);
        LOGGER.debug("trying to delete old file");
        LOGGER.debug("filepath is: " + oldFile.getAbsolutePath());
        LOGGER.debug("file exists: " + (oldFile.exists() && oldFile.isFile()));

        if (oldFile.exists() && oldFile.isFile())
        {
            try
            {
                Files.delete(oldFile.toPath());
            } catch (IOException | SecurityException ex)
            {
                LOGGER.debug("failed to delete file right away", ex);
                oldFile.deleteOnExit();
            }
        }
    }

    private String generateFileName(int id)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toString(id + NAME_BASE, Character.MAX_RADIX).toUpperCase());
        LOGGER.debug("sb:" + sb.toString());
        sb.append("_");
        sb.append(generateSimplifiedTimestamp());

        return sb.toString();
    }

    private String generateSimplifiedTimestamp()
    {
        Long simplifiedTimestamp = System.currentTimeMillis() - SIMPLIFIED_TIMESTAMP_SUBSTRACT_BASE;
        simplifiedTimestamp /= 1000;
        String responce = Long.toString(simplifiedTimestamp, Character.MAX_RADIX);
        LOGGER.debug("simpl. timestamp: " + responce);

        return responce;
    }

    private boolean checkContentType(String contentType)
    {
        for (String allowedType : ALLOWED_FILE_TYPES)
        {
            if (allowedType.equalsIgnoreCase(contentType))
            {
                return true;
            }
        }

        return false;
    }
}
