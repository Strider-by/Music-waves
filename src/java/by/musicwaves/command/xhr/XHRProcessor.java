package by.musicwaves.command.xhr;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface XHRProcessor
{
    public final static int STATE_ERROR_OCCURED = 520;

    String execute(HttpServletRequest request, HttpServletResponse response);
}
