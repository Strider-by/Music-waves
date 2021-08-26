package by.musicwaves.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import by.musicwaves.command.factory.XHRProcessorFactory;
import by.musicwaves.command.xhr.XHRProcessor;
import javax.servlet.annotation.MultipartConfig;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@WebServlet("/ajax")
@MultipartConfig(maxRequestSize = 20 * 1024 * 1024) // 20 MB
public class AjaxController extends HttpServlet
{
    private final static Logger LOGGER = LogManager.getLogger();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        XHRProcessorFactory factory = new XHRProcessorFactory();
        XHRProcessor xhrp = factory.defineProcessor(request);

        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        if (xhrp != null)
        {
            String result = xhrp.execute(request, response);
            response.getWriter().write(result);
            
        } else
        {
            String msg = "someone has requested xhr-processor using unknown or invalid alias, ";
            msg += "[" + request.getParameter(XHRProcessorFactory.PARAM_NAME_COMMAND) + "]";
            
            LOGGER.info(msg);
        }
    }
}
