package by.musicwaves.command.factory;

import javax.servlet.http.HttpServletRequest;
import by.musicwaves.command.client.XHRProcessorEnum;
import by.musicwaves.command.xhr.XHRProcessor;

public class XHRProcessorFactory
{
    public final static String PARAM_NAME_COMMAND = "command";
    
    public XHRProcessor defineProcessor(HttpServletRequest request)
    {
        // get command name from request
        String commandParamValue = request.getParameter(PARAM_NAME_COMMAND);
        // get proper command by gotten alias;
        // if alis is unknown or invalid, null will be returned
        XHRProcessor processor = XHRProcessorEnum.getProcessorByAlias(commandParamValue);

        return processor;
    }
}
