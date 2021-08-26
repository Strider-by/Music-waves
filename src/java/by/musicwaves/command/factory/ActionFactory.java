package by.musicwaves.command.factory;

import javax.servlet.http.HttpServletRequest;
import by.musicwaves.command.action.ActionCommand;
import by.musicwaves.command.client.CommandEnum;

public class ActionFactory
{
    private final static String PARAM_NAME_COMMAND = "command";
    
    public ActionCommand defineCommand(HttpServletRequest request)
    {
        // get command name from request
        String commandParamValue = request.getParameter(PARAM_NAME_COMMAND);
        // get proper command by gotten alias;
        // if alis is unknown or invalid, null will be returned
        ActionCommand command = CommandEnum.getCommandByAlias(commandParamValue);

        return command;
    }

}
