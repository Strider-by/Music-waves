package by.musicwaves.logic;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationWorker 
{
    private final static Pattern EMAIL_PATTERN;
    private final static int EMAIL_MAX_LENGTH;
    private final static int PASSWORD_MAX_LENGTH;
    
    static
    {
        // there is no real point in strict checking of the entered email since
        // non-existing email that fits strict pattern isn't any better than invalid email
        EMAIL_PATTERN = Pattern.compile("^[^@]+@[^@]+[.][^@]{2,}$");
        EMAIL_MAX_LENGTH = 45;
        PASSWORD_MAX_LENGTH = 45;
    }
    
    public  String generateRegisterCode() 
    {
        Random rnd = new Random();
        String result = "";
        result += (char) (rnd.nextInt('K' - 'A') + 'A');
        result += rnd.nextInt(90_000) + 10_000;
        result += (char) (rnd.nextInt('Z' - 'L') + 'L');
        
        return result;
    }
    
    public  boolean validatePassword(String password)
    {
        // We don't really care so the only thing we check here is the password isn't empty
        // and isn't too long
        return !password.isEmpty() && password.length() <= PASSWORD_MAX_LENGTH;
    }
    
    public  boolean validateEmail(String email)
    {
        Matcher mat = EMAIL_PATTERN.matcher(email);
        
        return mat.find() && mat.group().length() <= EMAIL_MAX_LENGTH;
    }
}
