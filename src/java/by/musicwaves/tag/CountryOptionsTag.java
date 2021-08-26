package by.musicwaves.tag;

import by.musicwaves.entity.ancillary.Country;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import static javax.servlet.jsp.tagext.Tag.SKIP_BODY;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class CountryOptionsTag  extends TagSupport
{
    private int selectedOptionId;
    
    private final static Logger LOGGER = LogManager.getLogger();
    
    public void setProperty(int selectedOptionId)
    {
        this.setSelectedOptionId(selectedOptionId);
        LOGGER.debug("selected value: " + selectedOptionId);
    }
    
    @Override
    public int doStartTag() throws JspException
    {
        try
        {
            Country[] countries = Country.values();
            StringBuilder sb = new StringBuilder();
            
            for(int i = 1; i < countries.length; i++)
            {
                sb.append("<option value=\"");
                sb.append(i);
                sb.append("\" ");
                if(selectedOptionId == i)
                {
                    sb.append("selected");
                }
                sb.append(">");
                sb.append(countries[i].getName());
                sb.append("</option>");
            }
            
            JspWriter writer = pageContext.getOut();
            writer.write(sb.toString());
            
        } catch (IOException e)
        {
            throw new JspException(e.getMessage());
        }
        return SKIP_BODY;
    }

    public int getSelectedOptionId()
    {
        return selectedOptionId;
    }

    public void setSelectedOptionId(int selectedOptionId)
    {
        this.selectedOptionId = selectedOptionId;
    }
}
