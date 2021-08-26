package by.musicwaves.tag;

import by.musicwaves.resource.LocalizationBundle;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import static javax.servlet.jsp.tagext.Tag.SKIP_BODY;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class LocalizationTag  extends TagSupport
{
    private String property;
    private final static Logger LOGGER = LogManager.getLogger();
    
    public void setProperty(String property)
    {
        this.property = property;
        LOGGER.debug("property set: " + property);
    }
    
    @Override
    public int doStartTag() throws JspException
    {
        try
        {
            LocalizationBundle lb = (LocalizationBundle) pageContext.getSession().getAttribute("localizationBundle");
            if(lb == null)
            {
                lb = LocalizationBundle.DEFAULT;
            }
            
            LOGGER.debug("localization bundle: " + lb);
            JspWriter writer = pageContext.getOut();
            String value = lb.getProperty(property);
            writer.write(value);
            
        } catch (IOException e)
        {
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }
}
