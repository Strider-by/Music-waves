package by.musicwaves.entity.ancillary;

import by.musicwaves.resource.LocalizationBundle;

public enum Language
{
    UNKNOWN("Unknown", "unknown", LocalizationBundle.DEFAULT),
    ENGLISH("English", "eng", LocalizationBundle.ENGLISH),
    BELARUSIAN("Беларуская", "by", LocalizationBundle.BELARUSIAN),
    RUSSIAN("Русский", "ru", LocalizationBundle.RUSSIAN);

    private String name;
    private String shortName;
    private LocalizationBundle localizationBundle;

    Language(String name, String shortName, LocalizationBundle localizationBundle)
    {
        this.name = name;
        this.shortName = shortName;
        this.localizationBundle = localizationBundle;
    }

    public static Language parseDatabaseEquivalent(int id)
    {
        return Language.values()[id];
    }

    public int getDatabaseEquivalent()
    {
        return this.ordinal();
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getShortName()
    {
        return shortName;
    }

    public void setShortName(String shortName)
    {
        this.shortName = shortName;
    }

    public LocalizationBundle getLocalizationBundle()
    {
        return localizationBundle;
    }

    public void setLocalizationBundle(LocalizationBundle localizationBundle)
    {
        this.localizationBundle = localizationBundle;
    }
}
