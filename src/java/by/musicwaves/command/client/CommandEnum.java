package by.musicwaves.command.client;

import by.musicwaves.command.action.ActionCommand;
import by.musicwaves.command.action.GoToPageCommand;
import by.musicwaves.command.action.GetAfterloginPage;
import by.musicwaves.command.action.GoToDefaultPageCommand;
import by.musicwaves.command.action.LogoutCommand;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public enum CommandEnum
{
    LOGIN (new GetAfterloginPage(), "login"),
    
    LOGOUT (new LogoutCommand(), "logout"),
    
    GO_TO_PAGE (new GoToPageCommand(), "go_to_page"),
   
    GO_TO_DEFAULT_PAGE (new GoToDefaultPageCommand(), "");
    
    private final static Logger LOGGER = LogManager.getLogger();
    
    private final ActionCommand command;
    private final String alias;
    
    private CommandEnum(ActionCommand command, String alias)
    {
        this.command = command;
        this.alias = alias;
    }

    public ActionCommand getCommand()
    {
        return command;
    }
    
    public static ActionCommand getCommandByAlias(String alias)
    {
        LOGGER.debug("Action command requested requested, alias is: " + alias);
        
        for(CommandEnum ce : CommandEnum.values())
        {
            if(ce.alias != null && ce.alias.equals(alias))
            {
                return ce.command;
            }
        }
        
        // if nothing suitable was found
        LOGGER.info("unknown page was requested, alias is [" + alias + "]");
        return null;
    }
}
