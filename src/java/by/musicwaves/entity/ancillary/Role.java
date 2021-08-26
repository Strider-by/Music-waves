package by.musicwaves.entity.ancillary;

public enum Role
{
    NOT_REGISTERED_USER ("unregistered"),
    USER ("user"),
    CURATOR ("curator"),
    ADMINISTRATOR ("administrator");

    private String propertyKey;
    
    Role(String propertyKey)
    {
        this.propertyKey = propertyKey;
    }
    
    public static Role parseDatabaseEquivalent(int value) 
    { 
        return Role.values()[value];
    }

    public int getDatabaseEquivalent() 
    {
        return this.ordinal();
    }

    public String getPropertyKey()
    {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey)
    {
        this.propertyKey = propertyKey;
    }
}
