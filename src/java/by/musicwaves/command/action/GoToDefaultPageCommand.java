package by.musicwaves.command.action;

import javax.servlet.http.HttpServletRequest;
import by.musicwaves.resource.ApplicationPage;
import by.musicwaves.servlet.TransitType;

/**
 * This command forwards user to the main application page.
 */
public class GoToDefaultPageCommand implements ActionCommand
{
    private TransitType transitType;
    private ApplicationPage targetPage;
    
    @Override
    public void execute(HttpServletRequest request)
    {
        targetPage = ApplicationPage.INDEX;
        transitType = TransitType.FORWARD;
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
