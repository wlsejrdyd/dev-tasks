package tasks.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);                    // 수신자
            message.setSubject(subject);          // 제목
            message.setText(text);                // 내용
            message.setFrom("wlsejrdyd@gmail.com"); // 보내는 사람 이메일 (Gmail 계정)

            mailSender.send(message);
            log.info("✅ 이메일 전송 성공 → to: {}", to);
        } catch (Exception e) {
            log.error("❌ 이메일 전송 실패: {}", e.getMessage(), e);
        }
    }
}
