package by.musicwaves.command.action;

import by.musicwaves.entity.ancillary.Role;
import by.musicwaves.entity.User;
import javax.servlet.http.HttpServletRequest;
import by.musicwaves.resource.ApplicationPage;
import by.musicwaves.servlet.TransitType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This command redirects user after he has logged in to proper page. If user has
 * finished his registration process, he will be sent to index page, otherwise - 
 * to his personal data page.
 */
public class GetAfterloginPage implements ActionCommand
{
    private TransitType transitType;
    private ApplicationPage targetPage;

    private final static Logger LOGGER = LogManager.getLogger();

    @Override
    public void execute(HttpServletRequest request)
    {
        LOGGER.debug("Executing login command...");

        User user = (User) request.getSession().getAttribute("user");
        if (user != null && user.isAccountActivated())
        {
            // If user has completed his registration already (== he is not NOT_REGISTERED_USER)
            // - he will be sent to site map page. Otherwise - to personal data page 
            // so he can finish registration.
            if (user.getRole() != Role.NOT_REGISTERED_USER)
            {
                targetPage = ApplicationPage.INDEX;
            } else
            {
                targetPage = ApplicationPage.PERSONAL_DATA;
            }

            transitType = TransitType.REDIRECT;
            
        } else
        {
            // Since current login mechanism works via preliminarily login, 
            // this command can't be run if user isn'r logged in.
            // But just in case someone too curious will break front-end part and run this thing directly,
            // he will be returned to login page since session contains no "user" object.
            targetPage = ApplicationPage.LOGIN;
            transitType = TransitType.REDIRECT;
        }
    }

    @Override
    public ApplicationPage getTargetPage()
    {
        return targetPage;
    }

    @Override
    public TransitType getTransitType()
    {
        return transitType;
    }
}
