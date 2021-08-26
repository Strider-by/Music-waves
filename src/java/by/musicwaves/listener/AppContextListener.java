package by.musicwaves.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 * This thing is up to setting context path attribute to session so we can work
 * with it in our jsp pages.
 */
@WebListener
public class AppContextListener implements ServletContextListener
{
    @Override
    public void contextInitialized(ServletContextEvent event)
    {
        ServletContext sc = event.getServletContext();
        sc.setAttribute("contextPath", sc.getContextPath());
    }

    @Override
    public void contextDestroyed(ServletContextEvent event)
    {
    }
}
