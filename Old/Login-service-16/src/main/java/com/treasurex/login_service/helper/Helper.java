package com.treasurex.login_service.helper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.treasurex.login_service.service.EmailService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Helper {

	private final EmailService emailService;
	private final TemplateEngine templateEngine;

	/*
	 * Generates a numeric OTP of given length.
	 */
	public String generateOtp(int length) {
		StringBuilder otp = new StringBuilder();
		SecureRandom random = new SecureRandom();

		for (int i = 0; i < length; i++) {
			otp.append(random.nextInt(10));
		}
		return otp.toString();
	}

	/*
	 * Hashes a security answer using SHA-256 and Base64 encoding for secure
	 */
	public String hashAnswer(String answer) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(answer.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(hash);
		} catch (Exception e) {
			throw new RuntimeException("Error hashing answer", e);
		}
	}

	/*
	 * Sends OTP email for account verification.
	 */
	public void sendUserVerificationOtpToEmail(String mail, String otp, String name) {
		String subject = "Account Verification - OTP Code";

		Context context = new Context();
		context.setVariable("name", name);
		context.setVariable("otp", otp);

		String body = templateEngine.process("verification-otp.html", context);

		emailService.sendEmail(mail, subject, body);
	}

	/*
	 * Sends OTP email for password reset.
	 */
	public void sendPasswordResetOtpEmail(String mail, String otp, String name) {
		String subject = "Password Reset - OTP Code";

		Context context = new Context();
		context.setVariable("name", name);
		context.setVariable("otp", otp);

		String body = templateEngine.process("password-reset.html", context);

		emailService.sendEmail(mail, subject, body);
	}
}
