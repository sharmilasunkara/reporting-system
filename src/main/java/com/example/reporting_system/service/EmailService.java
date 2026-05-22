package com.example.reporting_system.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmailWithAttachment(
            String toEmail,
            String subject,
            String body,
            String filePath) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true);

            helper.setTo(toEmail);

            helper.setSubject(subject);

            helper.setText(body);

            FileSystemResource file =
                    new FileSystemResource(
                            new File(filePath)
                    );

            helper.addAttachment(
                    file.getFilename(),
                    file
            );

            mailSender.send(message);

            log.info("Email sent successfully");

        } catch (Exception ex) {

            log.error("Failed to send email", ex);
        }
    }
}