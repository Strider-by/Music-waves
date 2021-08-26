package by.musicwaves.logic;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class SearchLogic
{
    private final static Logger LOGGER;
    
    static
    {
        LOGGER = LogManager.getLogger();
    }

    public enum SearchType
    {
        CONTAINS,
        STRICT;

        public static SearchType parse(String val)
        {
            try
            {
                int ordinal = Integer.parseInt(val);
                int variants = SearchType.values().length;

                SearchType result = ordinal <= variants && ordinal >= 0 ? SearchType.values()[ordinal] : null;
                LOGGER.debug("search type parse result: " + result);
                return result;
                
            } catch (NumberFormatException | NullPointerException ex)
            {
                return null;
            }
        }
    }

    public enum Activity
    {
        ALL,
        ACTIVE_ONLY,
        INACTIVE_ONLY;

        public static Activity parse(String val)
        {
            try
            {
                int ordinal = Integer.parseInt(val);
                int variants = Activity.values().length;

                Activity result = ordinal <= variants && ordinal >= 0 ? Activity.values()[ordinal] : null;
                return result;
                
            } catch (NumberFormatException | NullPointerException ex)
            {
                return null;
            }
        }
    }

}
