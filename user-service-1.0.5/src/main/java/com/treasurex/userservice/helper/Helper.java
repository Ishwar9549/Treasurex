package com.treasurex.userservice.helper;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.treasurex.userservice.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class Helper {

	// Service to send emails
	private final EmailService emailService;

	// Thymeleaf template engine for email body rendering
	private final TemplateEngine templateEngine;

	private static final int DEFAULT_OTP_LENGTH = 4; // default OTP length

	/**
	 * Generates a numeric OTP of given length. If no length provided, default is 4
	 * digits.
	 */
	public String generateOtp() {
		return generateOtp(DEFAULT_OTP_LENGTH);
	}

	/**
	 * Generates numeric OTP with specified length.
	 */
	public String generateOtp(int length) {
		StringBuilder otp = new StringBuilder();
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < length; i++) {
			otp.append(random.nextInt(10));
		}
		return otp.toString();
	}

	/**
	 * Sends OTP to phone number. Currently logs OTP; in future, integrate SMS
	 * provider.
	 */
	public void sendVerificationOtpToPhone(String phoneNumber, String otp) {
		log.info("Phone OTP: {}", otp);
		System.err.println("Phone "+phoneNumber+" OTP: "+otp);
		// TODO: integrate SMS provider for production
	}

	/**
	 * Sends OTP email for verification using Thymeleaf template.
	 */
	public void sendOtpForEmailVerification(String mail, String otp, String name) {
		//sendOtpForEmailVerification(mail, otp, name, "verification-otp.html", "Account Verification - OTP Code");
		sendVerificationOtpToPhone(name, otp);
	}

	/**
	 * Flexible email sender for OTP with configurable template and subject
	 *
	 * @param mail         recipient email
	 * @param otp          OTP code
	 * @param name         recipient name
	 * @param templateName Thymeleaf template file
	 * @param subject      email subject
	 */
	public void sendOtpForEmailVerification(String mail, String otp, String name, String templateName, String subject) {
		Context context = new Context();
		context.setVariable("name", name);
		context.setVariable("otp", otp);

		String body = templateEngine.process(templateName, context);

		emailService.sendEmail(mail, subject, body);
		log.info("Sent OTP email to {}", mail);

	}
}
//END