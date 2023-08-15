package toy.board.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    public void sendMail(String toEMail, String title, String text) {
        SimpleMailMessage emailForm = createEmailForm(toEMail, title, text);

        try {
            mailSender.send(emailForm);
        } catch (MailException e) {
            log.debug("MailService.sendEmail exception occur toEmail: {}, " +
                    "title: {}, text: {}", toEMail, title, text);
            throw new BusinessException(ExceptionCode.UNABLE_TO_SEND_EMAIL);
        }

    }

    private SimpleMailMessage createEmailForm(String toEmail, String title, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        return message;
    }

}