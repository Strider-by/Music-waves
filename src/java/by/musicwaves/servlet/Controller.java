package by.musicwaves.servlet;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import by.musicwaves.command.action.ActionCommand;
import by.musicwaves.command.factory.ActionFactory;
import by.musicwaves.resource.ApplicationPage;
import java.util.Enumeration;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@WebServlet("/controller")
public class Controller extends HttpServlet
{
    private final static Logger LOGGER = LogManager.getLogger();

    @Override
    protected void service(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException
    {
        LOGGER.debug("We have reached main controller");
        LOGGER.debug("URL: " + request.getRequestURL().toString());
        LOGGER.debug("URI: " + request.getRequestURI());

//           -----------------    DEBUG BLOCK START    -----------------      
        Enumeration<String> parameterNames = request.getParameterNames();

        StringBuilder sb = new StringBuilder();
        while (parameterNames.hasMoreElements())
        {
            String paramName = parameterNames.nextElement();
            sb.append(paramName);

            String[] paramValues = request.getParameterValues(paramName);
            for (String paramValue : paramValues)
            {
                sb.append("\t").append(paramValue);
            }
        }
        LOGGER.debug("request parameters: " + sb.toString());
//           -----------------    DEBUG BLOCK END    -----------------        

        ApplicationPage page;
        ActionFactory actionFactory = new ActionFactory();
        ActionCommand command = actionFactory.defineCommand(request);

        if (command != null)
        {
            command.execute(request);
            page = command.getTargetPage();
            TransitType transitType = command.getTransitType();

            switch (transitType)
            {
                case FORWARD:
                    RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(page.getPagePath());
                    dispatcher.forward(request, response);
                    break;

                case REDIRECT:
                    response.sendRedirect(page.getPageAlias());
                    break;
            }
        }
        else
        {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
