package by.musicwaves.resource;

import java.util.ResourceBundle;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public enum ApplicationPage 
{
    INDEX ("index", "index"),
    
    CHANGE_PASSWORD("change-password", "change_password"),
    
    PERSONAL_DATA("personal-data", "personal_data"),
    
    GENRES("genres", "genres"),
    
    ARTISTS("artists", "artists"),
    
    ALBUMS("albums", "albums"),
    
    MUSIC_COMPOUND("music-compound", "music_compound"),
    
    MUSIC_SEARCH("music-search", "music_search"),
    
    USER_PLAYLISTS("playlists", "playlists"),
    
    LISTEN("listen", "listen"),
    
    USERS("users", "users"),
    
    LOGIN ("login", "login"),
    
    REGISTER ("register", "register"),
    
    UNKNOWN_PAGE (null, "error404"), // can't be reached from outside
    
    ACCESS_FORBIDDEN (null, "error403"), // can't be reached from outside
    
    INTERNAL_ERROR (null, "error500"); // can't be reached from outside
    
    private final String alias;
    private final String propertyKey;
    
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("resources.pages");
    
    ApplicationPage(String alias, String propertyKey)
    {   
        this.alias = alias;
        this.propertyKey = propertyKey;
    }
    
    private static String getPropertyKey(String alias)
    {
        String responce = null;
        
        for(ApplicationPage instance : ApplicationPage.values())
        {
            if(instance.alias != null && instance.alias.equalsIgnoreCase(alias))
            {
                responce = instance.propertyKey;
                break;
            }
        }
        
        return responce;
    }
    
    public static ApplicationPage getPageByAlias(String alias)
    {
        for(ApplicationPage instance : ApplicationPage.values())
        {
            if(instance.alias != null && instance.alias.equalsIgnoreCase(alias))
            {
                return instance;
            }
        }
        
        return null;
    }
    
    public static String getPagePath(String alias)
    {
        LOGGER.debug("alias: " + alias);
        String propertyKey = getPropertyKey(alias);
        LOGGER.debug("propertyKey: " + propertyKey);
        String pagePath = RESOURCE_BUNDLE.getString(propertyKey);
        LOGGER.debug("found page path: " + pagePath);
        return pagePath;
    }
    
    public String getPageAlias()
    {
        return this.alias;
    }
    
    public String getPagePath()
    {
        return RESOURCE_BUNDLE.getString(propertyKey);
    }
}
