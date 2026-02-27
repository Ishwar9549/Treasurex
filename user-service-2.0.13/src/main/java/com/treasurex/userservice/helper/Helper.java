package com.treasurex.userservice.helper;

import java.security.SecureRandom;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.treasurex.userservice.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility/helper class for generating OTPs and sending them via phone or email.
 * Uses Thymeleaf templates for email content.
 * 
 * In development, OTPs are logged; in production, integrate SMS/email provider.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class Helper {

	private final EmailService emailService;
	private final TemplateEngine templateEngine;

	private static final int DEFAULT_OTP_LENGTH = 4; // default OTP length

	/**
	 * Generates a numeric OTP of default length (4 digits).
	 */
	public String generateOtp() {
		return generateOtp(DEFAULT_OTP_LENGTH);
	}

	/**
	 * Generates numeric OTP with specified length.
	 */
	public String generateOtp(int length) {
		if (length <= 0)
			throw new IllegalArgumentException("OTP length must be > 0");

		StringBuilder otp = new StringBuilder();
		SecureRandom random = new SecureRandom();
		for (int i = 0; i < length; i++) {
			otp.append(random.nextInt(10));
		}
		return otp.toString();
	}

	/**
	 * Sends OTP to phone number. WARNING: Currently only logs OTP for development.
	 * Integrate SMS provider in production.
	 */
	public void sendVerificationOtpToPhone(String phoneNumber, String otp) {
		log.info("Phone OTP for {}: {}", phoneNumber, otp);
		System.err.println("Phone " + phoneNumber + " OTP: " + otp); // dev only
	}

	/**
	 * Sends OTP email using specified Thymeleaf template and subject.
	 */
	public void sendVerificationsOtpToEmails(String mail, String otp, String name, String subject) {

		Context context = new Context();
		context.setVariable("name", name);
		context.setVariable("otp", otp);

		String body = templateEngine.process("verification-otp.html", context);

		emailService.sendEmail(mail, subject, body);
		log.info("Sent OTP email to {}", mail);
	}

	/**
	 * Sends OTP email Testing. above method remove s at last
	 */
	public void sendVerificationsOtpToEmail(String mail, String otp, String name, String subject) {
		sendVerificationOtpToPhone(mail, otp);
	}
}
//END