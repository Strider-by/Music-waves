package by.musicwaves.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import by.musicwaves.resource.ConfigurationManager;
import by.musicwaves.util.URLWorker;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@WebServlet("/static/*")
public class StaticResourcesController extends HttpServlet
{
    private final static Logger LOGGER = LogManager.getLogger();
    private final static String STATIC_RESOURCES_PATH = ConfigurationManager.getProperty("application.data.static");

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException
    {
        LOGGER.debug("We have reached STATIC RESOURCES controller");
        LOGGER.debug("URL: " + request.getRequestURL().toString());
        LOGGER.debug("URI: " + request.getRequestURI());

        String uriExcludingContextPath = URLWorker.getUriExcludingContextPath(request);

        String requestedFile = convertRequestUriToFilePath(uriExcludingContextPath);
        File sourceFile = new File(requestedFile);

        LOGGER.debug("absolute file path we try to reach is: " + sourceFile.getAbsolutePath());
        if (sourceFile.exists() && sourceFile.isFile())
        {
            try (InputStream is = new FileInputStream(sourceFile); OutputStream os = response.getOutputStream())
            {
                int read;
                byte bytes[] = new byte[1024];

                while ((read = is.read(bytes)) != -1)
                {
                    os.write(bytes, 0, read);
                }
                os.flush();
            }

        } else
        {
            // 404, resource not found
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private static String convertRequestUriToFilePath(String uri)
    {
        uri = uri.replaceFirst("/static", "");
        return STATIC_RESOURCES_PATH + uri.replaceAll("/", Matcher.quoteReplacement(File.separator));
    }
}
