package by.musicwaves.entity;

import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.ancillary.Country;
import by.musicwaves.entity.ancillary.Language;
import java.time.LocalDate;

public class User implements Entity
{
    private int id;

    public enum Sex
    {
        UNKNOWN,
        MALE,
        FEMALE;

        public int getDatabaseEquivalent()
        {
            return this.ordinal();
        }

        public static Sex parseDatabaseEquivalent(int param)
        {
            return Sex.values()[param];
        }
    }

    private String email;
    private String hashedPassword;
    private String nickname;
    private String firstName;
    private String lastName;
    private String avatarFileName;
    private Country country;
    private Sex sex;
    private Role role;
    private LocalDate registerDate;
    private Language language;
    private String confCode;
    private boolean accountActivated;
    
    public User()
    {
        this.language = Language.UNKNOWN;
        this.country = Country.UNKNOWN;
        this.sex = Sex.UNKNOWN;
        this.registerDate = LocalDate.now();
        this.role = Role.NOT_REGISTERED_USER;
        this.avatarFileName = "";
        this.firstName = "";
        this.lastName = "";
        this.nickname = "";
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public Language getLanguage()
    {
        return language;
    }

    public void setLanguage(Language language)
    {
        this.language = language;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getHashedPassword()
    {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword)
    {
        this.hashedPassword = hashedPassword;
    }

    public String getNickname()
    {
        return nickname;
    }

    public void setNickname(String nickname)
    {
        this.nickname = nickname;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public Country getCountry()
    {
        return country;
    }

    public void setCountry(Country country)
    {
        this.country = country;
    }

    public Sex getSex()
    {
        return sex;
    }

    public void setSex(Sex sex)
    {
        this.sex = sex;
    }

    public boolean isAccountActivated()
    {
        return accountActivated;
    }

    public void setAccountActivated(boolean accountActivated)
    {
        this.accountActivated = accountActivated;
    }

    public String getConfCode()
    {
        return confCode;
    }

    public void setConfCode(String confCode)
    {
        this.confCode = confCode;
    }

    public String getAvatarFileName()
    {
        return avatarFileName;
    }

    public void setAvatarFileName(String avatarFileName)
    {
        this.avatarFileName = avatarFileName;
    }

    public Role getRole()
    {
        return role;
    }

    public void setRole(Role role)
    {
        this.role = role;
    }

    public LocalDate getRegisterDate()
    {
        return registerDate;
    }

    public void setRegisterDate(LocalDate registerDate)
    {
        this.registerDate = registerDate;
    }

    public void parseAndSetRegisterDate(String dtbDate)
    {
        this.registerDate = LocalDate.parse(dtbDate);
    }
}
