//package com.beautymeongdang.domain.notification.service;
//
//import jakarta.mail.MessagingException;
//import jakarta.mail.internet.MimeMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//import org.thymeleaf.TemplateEngine;
//import org.thymeleaf.context.Context;
//
//import java.util.Map;
//
//@Service
//public class NotificationEmailService {
//
//    private final JavaMailSender mailSender;
//    private final TemplateEngine templateEngine;
//
//    public NotificationEmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
//        this.mailSender = mailSender;
//        this.templateEngine = templateEngine;
//    }
//
//    public void sendEmail(String to, String subject, String templateName, Map<String, Object> variables) {
//        MimeMessage message = mailSender.createMimeMessage();
//        try {
//            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
//            helper.setTo(to);
//            helper.setSubject(subject);
//
//            Context context = new Context();
//            context.setVariables(variables);
//            String htmlContent = templateEngine.process(templateName, context);
//
//            helper.setText(htmlContent, true);
//            mailSender.send(message);
//        } catch (MessagingException e) {
//            throw new RuntimeException("이메일 전송 실패: " + to, e);
//        }
//    }
//}
