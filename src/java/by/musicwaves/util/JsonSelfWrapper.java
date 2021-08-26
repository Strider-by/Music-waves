package by.musicwaves.util;

public class JsonSelfWrapper 
{
    private final StringBuilder text = new StringBuilder();
    
    public void openJson()
    {
        text.append("{");
    }
    
    public void appendString(String value)
    {
        text.append("\n");
        if(value != null)
        {
            text.append("\"").append( encloseJsonString(value) ).append("\"");
        }
        else
        {
            text.append("null");
        }
        text.append(",");
    }

    public void appendString(String key, String value)
    {
        text.append("\n\"").append(key).append("\": ");
        if(value != null)
        {
            text.append("\"").append( encloseJsonString(value) ).append("\"");
        }
        else
        {
            text.append("null");
        }
        text.append(",");
        
    }
    
    public void appendNumber(Number value)
    {
        text.append("\n").append(value).append(",");
    }
    
    public void appendNumber(String key, Number value)
    {
        text.append("\n\"").append(key).append("\": ")
                .append(value).append(",");
    }
    
    public void appendBoolean(boolean value)
    {
        text.append("\n").append(value).append(",");
    }
    
    public void appendBoolean(String key, boolean value)
    {
        text.append("\n\"").append(key).append("\": ")
                .append(value).append(",");
    }
    
    public void openObject(String name)
    {
        text.append("\n\"").append(name).append("\": {");
    }

    public void openObject()
    {
        text.append("\n{");
    }
    
    public void closeObject()
    {
        trimExtraComma();
        text.append("\n},");
    }
    
    public void openArray(String name)
    {
        text.append("\n\"").append(name).append("\": [");
    }
    
    public void closeArray()
    {
        trimExtraComma();
        text.append("\n],");
    }
    
    public void closeJson()
    {
        trimExtraComma();
        text.append("\n}");
    }
    
    private String encloseJsonString(String value)
    {
        return value.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\"");
    }
    
    private void trimExtraComma()
    {
        if(text.charAt(text.length() - 1) == ',')
        {
            text.deleteCharAt(text.length() - 1);
        }
    }
    
    // my application xhr-command specific method
    public void appendResponceCode(int code)
    {
        appendNumber("appResponseCode", code);
    }
    
    @Override
    public String toString()
    {
        return text.toString();
    }
}


