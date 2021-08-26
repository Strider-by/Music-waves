package by.musicwaves.util;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ResourceBundle;

public class PasswordWorker
{
    private final static char[] HEX_ARRAY;
    private final static String SALT_1;
    private final static String SALT_2;
    private final static String SALT;
    private final static int ITERATIONS;
    private final static int KEY_LENGTH;

    static
    {
        HEX_ARRAY = "0123456789ABCDEF".toCharArray();
        SALT_1 = "8661A3E41EAC1433D77E3DF37003F300";

        ResourceBundle settings = ResourceBundle.getBundle("resources.password_processing");
        SALT_2 = settings.getString("salt2");
        SALT = SALT_1 + SALT_2;
        ITERATIONS = Integer.parseInt(settings.getString("iterations"));
        KEY_LENGTH = Integer.parseInt(settings.getString("keyLength"));
    }

    public static String processPasswordHashing(String password)
    {
        char[] passwordChars = password.toCharArray();
        byte[] saltBytes = SALT.getBytes();

        byte[] hashedBytes = hashPassword(passwordChars, saltBytes);
        String hashedString = bytesToHex(hashedBytes);

        return hashedString;
    }

    private static byte[] hashPassword(char[] password, byte[] salt)
    {
        try
        {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return res;
            
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    private static String bytesToHex(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0 ; j < bytes.length ; j++)
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        
        return new String(hexChars);
    }
}
