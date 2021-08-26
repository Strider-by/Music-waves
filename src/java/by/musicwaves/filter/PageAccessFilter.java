package by.musicwaves.filter;

import by.musicwaves.resource.AppResourceRequest;
import by.musicwaves.resource.ApplicationPage;
import by.musicwaves.util.URLWorker;
import java.io.IOException;
import java.util.EnumSet;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.catalina.tribes.util.Arrays;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

@WebFilter(urlPatterns = {"/*"})
public class PageAccessFilter implements Filter
{
    private final static EnumSet<AppResourceRequest> SHOULD_BE_SKIPPED
            = EnumSet.of(
                    AppResourceRequest.AJAX,
                    AppResourceRequest.CSS,
                    AppResourceRequest.IMAGE,
                    AppResourceRequest.JS,
                    AppResourceRequest.MAIN_SERVLET,
                    AppResourceRequest.STATIC);

    private final static Logger LOGGER = LogManager.getLogger();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException
    {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (!skipWithoutFiltering(httpServletRequest))
        {
            ApplicationPage targetPage = parsePageRequest(httpServletRequest);

            LOGGER.debug("URI filter is used.");
            LOGGER.debug("URI we will use: " + targetPage);

            if (targetPage != null)
            {
                // Access system may be modified the way user will be checked
                // to have rights to get proper page right here, and if the rights
                // are not sufficient - error 403 will be returned.
                // So far access control is on each jsp page duty.
                RequestDispatcher dispatcher = request.getServletContext()
                        .getRequestDispatcher(targetPage.getPagePath());
                dispatcher.forward(request, response);
                
            } else
            {
                // error 404
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;
                httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } else
        {
            LOGGER.debug("We skip filtering with URI filter");
            chain.doFilter(request, response);
        }
    }

    private static boolean skipWithoutFiltering(HttpServletRequest request)
    {
        String path = URLWorker.getUriExcludingContextPath(request);

        for (AppResourceRequest item : SHOULD_BE_SKIPPED)
        {
            if (path.startsWith("/" + item.getValue() + "/")
                    || path.equals("/" + item.getValue()))
            {
                return true;
            }
        }

        return false;
    }

    private static ApplicationPage parsePageRequest(HttpServletRequest httpServletRequest)
    {
        String uriExcludingContextPath = URLWorker.getUriExcludingContextPath(httpServletRequest);
        String[] uriParts = splitURI(uriExcludingContextPath);
        ApplicationPage referToPage;

        LOGGER.debug("cleaned URI: " + uriExcludingContextPath);
        LOGGER.debug("parts: " + Arrays.toString(uriParts));

        if (uriParts.length > 0)
        {
            String alias = uriParts[0];
            LOGGER.debug("alias: " + alias);

            referToPage = ApplicationPage.getPageByAlias(alias);
            if (referToPage == null)
            {
                LOGGER.info("user tried to reach page using an inappropriate alias [" + alias + "]");
                referToPage = null;
            }
        } else
        {
            referToPage = ApplicationPage.INDEX;
        }

        LOGGER.debug("what we got as result: " + referToPage);
        return referToPage;
    }

    public static String[] splitURI(String address)
    {
        String[] result = address.split("/");
        if (result.length > 0 && result[0].isEmpty())
        {
            String[] tmp = new String[result.length - 1];
            for (int i = 0 ; i < tmp.length ; i++)
            {
                tmp[i] = result[i + 1];
            }
            result = tmp;
        }

        return result;
    }
}
