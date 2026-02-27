package com.treasurex.userservice.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to send emails using JavaMailSender.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender javaMailSender;

	/**
	 * Sends an email with HTML content.
	 *
	 * @param to      recipient email
	 * @param subject email subject
	 * @param body    email body (HTML)
	 */
	public void sendEmail(String to, String subject, String body) {
		log.info("sendEmail function called for recipient: {}", to);
		try {
			MimeMessage message = javaMailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(body, true);

			javaMailSender.send(message);

			log.info("Email sent successfully to {} with subject '{}'", to, subject);
		} catch (Exception e) {
			log.error("Exception while sending email to {}: {}", to, e.getMessage(), e);
		}
	}
}
//END 