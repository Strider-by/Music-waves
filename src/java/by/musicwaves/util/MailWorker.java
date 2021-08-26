package by.musicwaves.util;

import java.util.Properties;
import java.util.ResourceBundle;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class MailWorker
{
    private final Properties props;
    private final ResourceBundle resourceBundle;
    private final Session session;
    private final Message message;
    private final static Logger LOGGER = LogManager.getLogger();

    
    {
        resourceBundle = ResourceBundle.getBundle("resources.email");
        props = new Properties();
        props.put("mail.smtp.host", resourceBundle.getString("mail.smtp.host"));
        props.put("mail.smtp.auth", resourceBundle.getString("mail.smtp.auth"));
        props.put("mail.smtp.port", resourceBundle.getString("mail.smtp.port"));
        props.put("mail.smtp.socketFactory.port", resourceBundle.getString("mail.smtp.socketFactory.port"));
        props.put("mail.smtp.socketFactory.class", resourceBundle.getString("mail.smtp.socketFactory.class"));

        session = Session.getInstance(props, new javax.mail.Authenticator()
        {
            @Override
            protected PasswordAuthentication getPasswordAuthentication()
            {
                return new PasswordAuthentication(
                        resourceBundle.getString("mail.user.login"),
                        resourceBundle.getString("mail.user.password"));
            }
        });

        message = new MimeMessage(session);
    }

    public boolean sendMessage(String recipientAddr, String subject, String text)
    {
        try
        {
            message.setFrom(new InternetAddress(resourceBundle.getString("mail.user.address")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientAddr));
            message.setSubject(subject);
            message.setText(text);

            Transport.send(message);
            return true;

        } catch (MessagingException ex)
        {
            LOGGER.error("Mail worker has failed in his task", ex);
            return false;
        }
    }

    public boolean sendCode(String recipientAddr, String code, String logoFilePath)
    {
        try
        {
            message.setFrom(new InternetAddress(resourceBundle.getString("mail.user.address")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientAddr));
            message.setSubject("Music waves: registration code");

            MimeMultipart multipart = new MimeMultipart("related");

            BodyPart messageBodyPart = new MimeBodyPart();

            String htmlText;
            htmlText = "<html>\n"
                    + " <head>\n"
                    + "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n"
                    + " </head>\n"
                    + " <body>\n"
                    + "  <table style = \"border:1px rgba(0, 0, 0, 0.3) solid; padding: 10px;\">\n"
                    + "   <tr>\n"
                    + "    <th><img src=\"cid:image\"></th>\n"
                    + "   </tr>\n"
                    + "   <tr>\n"
                    + "    <td style = \"text-align:center\">Your code is:</td>\n"
                    + "  </tr>\n"
                    + "     <tr style = \"height: 10px;\"></tr>\n"
                    + "  <tr>\n"
                    + "    <td style = \"text-align:center\">\n"
                    + "    <div style=\"border:solid 1px rgba(0, 0, 0, 0.3);\">\n"
                    + "    <font size=\"+3\" face=\"serif, sans-serif, fantasy, monospace\" color=\"#001a00\">"
                    + code
                    + "    </font>\n"
                    + "    </div>\n"
                    + "    </td>\n"
                    + "  </tr>\n"
                    + "  <tr style = \"height: 30px;\"></tr>\n"
                    + " </table>\n"
                    + " </body>\n"
                    + "</html>";

            messageBodyPart.setContent(htmlText, "text/html");
            multipart.addBodyPart(messageBodyPart);

            messageBodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource(logoFilePath);

            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");

            multipart.addBodyPart(messageBodyPart);

            message.setContent(multipart);
            Transport.send(message);

            return true;
            
        } catch (MessagingException ex)
        {
            LOGGER.error("Mail worker has failed in his task", ex);
            return false;
        }
    }
}
