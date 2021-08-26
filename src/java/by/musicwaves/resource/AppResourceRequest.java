package by.musicwaves.resource;

public enum AppResourceRequest
{
    IMAGE("images"),
    CSS("css"),
    JS("js"),
    MAIN_SERVLET(ConfigurationManager.getProperty("application.naming.mainservlet")),
    AJAX(ConfigurationManager.getProperty("application.naming.ajax")),
    STATIC(ConfigurationManager.getProperty("application.naming.static"));

    String value;

    AppResourceRequest(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }
}
