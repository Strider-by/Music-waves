package by.musicwaves.command.action;

import javax.servlet.http.HttpServletRequest;
import by.musicwaves.resource.ApplicationPage;
import by.musicwaves.servlet.TransitType;

/**
 * This command processes user logout via session invalidation. <br>
 * User will be redirected to login page.
 */
public class LogoutCommand implements ActionCommand
{
    private TransitType transitType;
    private ApplicationPage targetPage;
    
    @Override
    public void execute(HttpServletRequest request)
    {
        request.getSession().invalidate();
        
        targetPage = ApplicationPage.LOGIN;
        transitType = TransitType.REDIRECT;
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
