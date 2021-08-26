package by.musicwaves.util;

import java.util.HashMap;
import java.util.Map;

public class HtmlStringEscapeTool 
{
    static 
    {
        CHARACTERS_TO_ESCAPE_LIST = new HashMap<>();
        HtmlStringEscapeTool.CHARACTERS_TO_ESCAPE_LIST.put(' ', "&nbsp;");
        HtmlStringEscapeTool.CHARACTERS_TO_ESCAPE_LIST.put('<', "&lt;");
        HtmlStringEscapeTool.CHARACTERS_TO_ESCAPE_LIST.put('>', "&gt;");
        HtmlStringEscapeTool.CHARACTERS_TO_ESCAPE_LIST.put('&', "&amp;");
        HtmlStringEscapeTool.CHARACTERS_TO_ESCAPE_LIST.put('"', "&quot;");
        HtmlStringEscapeTool.CHARACTERS_TO_ESCAPE_LIST.put('\'', "&apos;");
    }
    
    private final static Map<Character, String> CHARACTERS_TO_ESCAPE_LIST;

    public String escape(String text)
    {
        StringBuilder sb = new StringBuilder();
        String replacer;
        
        for(char symbol : text.toCharArray())
        {
            if((replacer = CHARACTERS_TO_ESCAPE_LIST.get(symbol)) != null)
            {
                sb.append(replacer);
            }
            else
            {
                sb.append(symbol);
            }
        }
        
        return sb.toString();
    }
}
