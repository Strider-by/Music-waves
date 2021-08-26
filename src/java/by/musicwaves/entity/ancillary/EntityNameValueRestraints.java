package by.musicwaves.entity.ancillary;

import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public enum EntityNameValueRestraints
{
    GENRE(".{1,45}"),
    ARTIST(".{1,45}"),
    ALBUM(".{1,45}"),
    AUDIOTRACK(".{1,45}"),
    PLAYLIST(".{1,45}");

    private final static Logger LOGGER = LogManager.getLogger();
    private final Pattern namePattern;

    private EntityNameValueRestraints(String namePatternString)
    {
        this.namePattern = Pattern.compile(namePatternString);
    }

    public boolean validateName(String name)
    {
        LOGGER.debug("validating " + this.toString() + " name value " + name);
        boolean result = name != null && namePattern.matcher(name).matches();
        LOGGER.debug("validation result: " + result);

        return result;
    }

}
