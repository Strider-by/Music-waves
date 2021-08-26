package by.musicwaves.command.action;

import javax.servlet.http.HttpServletRequest;
import by.musicwaves.resource.ApplicationPage;
import by.musicwaves.servlet.TransitType;

/**
 * This command forwards user to the page requested in "page" parameter.
 */
// This isn't used in my project so far. Can be useful though.
public class GoToPageCommand implements ActionCommand
{
    private final static String PARAM_NAME_REQUESTED_PAGE = "page";
    private TransitType transitType;
    private ApplicationPage targetPage;
    

    @Override
    public void execute(HttpServletRequest request)
    {
        String requestedPageAlias = request.getParameter(PARAM_NAME_REQUESTED_PAGE);
        
        if ((targetPage = ApplicationPage.getPageByAlias(requestedPageAlias)) != null)
        {
            transitType = TransitType.FORWARD;
        } 
        else
        {
            targetPage = ApplicationPage.UNKNOWN_PAGE;
            transitType = TransitType.FORWARD;
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
