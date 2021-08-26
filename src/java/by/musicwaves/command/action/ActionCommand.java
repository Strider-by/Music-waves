package by.musicwaves.command.action;

import by.musicwaves.resource.ApplicationPage;
import by.musicwaves.servlet.TransitType;
import javax.servlet.http.HttpServletRequest;

public interface ActionCommand
{
    void execute(HttpServletRequest request);
    ApplicationPage getTargetPage();
    TransitType getTransitType();
}
