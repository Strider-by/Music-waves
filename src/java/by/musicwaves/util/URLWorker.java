package by.musicwaves.util;

import javax.servlet.http.HttpServletRequest;

public class URLWorker 
{
    public static String getUriExcludingContextPath(HttpServletRequest request)
    {
        String contextPath = request.getServletContext().getContextPath();
        int baseUriFullLength = contextPath.length();
        String path = request.getRequestURI().substring(baseUriFullLength);
        
        return path;
    }
    
    public static String getUriExcludingContextPath(String requestURI, String contextPath)
    {
        int baseUriFullLength = contextPath.length();
        String path = requestURI.substring(baseUriFullLength);
        
        return path;
    }
    
    public static String getRealFilePath(HttpServletRequest request, String filePath)
    {
        return request.getServletContext().getRealPath(filePath);
    }
}
