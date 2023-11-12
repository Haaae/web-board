package toy.board.service.mail;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import toy.board.exception.BusinessException;
import toy.board.exception.ExceptionCode;
import toy.board.service.member.MemberService;
import toy.board.service.redis.RedisService;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MailService {

    private final JavaMailSender mailSender;
    private final RedisService redisService;
    private final MemberService memberService;

    @Value("${spring.mail.properties.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;
    private final String REDIS_PREFIX = "AuthCode";
    private final String EMAIL_TITLE = "My Poker Hand History 이메일 인증 번호";

    @Transactional
    public void sendCodeToEmail(final String email) {
        memberService.checkUsernameDuplication(email);
        String authCode = createAuthCode();
        sendMail(email, EMAIL_TITLE, authCode);
        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisService.setValues(REDIS_PREFIX + email, authCode, authCodeExpirationMillis);
    }

    @Transactional
    public boolean verifiedCode(final String email, final String authCode) {
        memberService.checkUsernameDuplication(email);
        return redisService.deleteIfValueExistAndEqualTo(REDIS_PREFIX + email, authCode);
    }

    private String createAuthCode() {
        int length = 6;
        try {
            Random random = SecureRandom.getInstanceStrong();
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < length; i++) {
                builder.append(random.nextInt(10));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException(ExceptionCode.INTERNAL_SERVER_ERROR);
        }
    }

    private void sendMail(final String toEMail, final String title, final String text) {
        SimpleMailMessage emailForm = createEmailForm(toEMail, title, text);

        try {
            mailSender.send(emailForm);
        } catch (MailException e) {
            throw new BusinessException(ExceptionCode.UNABLE_TO_SEND_EMAIL);
        }
    }

    private SimpleMailMessage createEmailForm(final String toEmail, final String title, final String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(text);

        return message;
    }

}
