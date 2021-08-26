package by.musicwaves.resource;

import java.util.Locale;
import java.util.ResourceBundle;

public enum LocalizationBundle
{
    DEFAULT(),
    BELARUSIAN(new Locale("be", "by")),
    RUSSIAN(new Locale("ru", "ru")),
    ENGLISH(new Locale("en", "gb"));

     
    private final ResourceBundle resourceBundle;
    
    private LocalizationBundle(Locale locale) 
    {
        resourceBundle = ResourceBundle.getBundle("resources.forms", locale);
    }
    
    private LocalizationBundle() 
    {
        resourceBundle = ResourceBundle.getBundle("resources.forms");
    }

    public String getProperty(String key)
    {
        return resourceBundle.getString(key);
    }
    
    // TODO -> make handy pick language bundle method using language and country params
    public static LocalizationBundle getLocalizationBundle(Locale locale)
    {
        throw new UnsupportedOperationException("will do some other day");
    }

}

