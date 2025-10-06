package sistem_pengaduan.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceLogin {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendOTPLogin(String toEmail, String otp, String nama) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        String htmlMsg = "<html><body style='font-family: Arial, sans-serif; color: #333333;'>"
                + "<div style='margin: auto; max-width: 600px; padding: 20px; text-align: center;'>"
                + "<h1 style='color: #333333;'>odyhub</h1>"
                + "<h2 style='color: #333333;'>Let's Log In</h2>"
                + "<p style='color: #666666;'>Hi, " + nama + "!</p>"
                + "<p style='color: #666666;'>Use this code to Log In confidentially.</p>"
                + "<p style='color: #666666;'>It will expire in 1 minute.</p>"
                + "<h2 style='color: #333333; letter-spacing: 4px; margin: 50px 0;'>" + otp.charAt(0) + " "
                + otp.charAt(1) + " " + otp.charAt(2) + " " + otp.charAt(3) + "</h2>"
                + "<p style='color: #666666;'>This code will securely.</p>"
                + "<p style='font-size: 80%; color: #999999;'>If you didn't request this email, you can ignore it.</p>"
                + "</div>"
                + "</body></html>";
        try {
            helper.setTo(toEmail);
            helper.setSubject("Verification your OTP code for Login");
            helper.setText(htmlMsg, true); // true parameter indicates HTML content
            javaMailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            // Handle exception
        }
    }
}
