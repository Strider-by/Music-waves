package by.musicwaves.entity.ancillary;

import by.musicwaves.entity.User;
import java.util.regex.Pattern;

public class PersonalDataValueRestraints
{
    private final static String NICKNAME_PATTERN_STRING = "[^`'\"#!@\\\\/|&$?]{3,15}";
    private final static String NAME_PATTERN_STRING = ".{0,40}";
    private final static Pattern NICKNAME_PATTERN;
    private final static Pattern NAME_PATTERN;

    static
    {
        NICKNAME_PATTERN = Pattern.compile(NICKNAME_PATTERN_STRING);
        NAME_PATTERN = Pattern.compile(NAME_PATTERN_STRING);
    }

    public boolean validateNickname(String nickname)
    {
        return nickname != null && NICKNAME_PATTERN.matcher(nickname).matches();
    }

    public boolean validateName(String name)
    {
        return name != null && NAME_PATTERN.matcher(name).matches();
    }

    public boolean validateSex(String sexParamValue)
    {
        try
        {
            int sexId = Integer.parseInt(sexParamValue);
            User.Sex[] values = User.Sex.values();
            return sexId >= 0 && sexId < values.length;
            
        } catch (NumberFormatException | NullPointerException ex)
        {
            return false;
        }
    }

    public boolean validateCountry(String countryParamValue)
    {
        try
        {
            int countryId = Integer.parseInt(countryParamValue);
            Country[] values = Country.values();
            return countryId >= 0 && countryId < values.length;
            
        } catch (NumberFormatException | NullPointerException ex)
        {
            return false;
        }
    }

    public boolean validateLanguage(String languageParamValue)
    {
        try
        {
            int languageId = Integer.parseInt(languageParamValue);
            Language[] values = Language.values();
            return languageId >= 0 && languageId < values.length;
            
        } catch (NumberFormatException | NullPointerException ex)
        {
            return false;
        }
    }

}
