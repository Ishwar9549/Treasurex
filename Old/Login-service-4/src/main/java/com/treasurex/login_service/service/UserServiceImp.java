package com.treasurex.login_service.service;

import java.util.Random;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.treasurex.login_service.dto.ChangePasswordRequest;
import com.treasurex.login_service.dto.UserLoginRequest;
import com.treasurex.login_service.dto.UserMapper;
import com.treasurex.login_service.dto.UserRegisterRequest;
import com.treasurex.login_service.dto.VerifyUserRequest;
import com.treasurex.login_service.entity.User;
import com.treasurex.login_service.exception.InvalidCredentialsException;
import com.treasurex.login_service.exception.ResourceNotFoundException;
import com.treasurex.login_service.repository.UserRepository;
import com.treasurex.login_service.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

	private final UserRepository userRepository;
	private final UserMapper userMapper;
	private final PasswordEncoder passwordEncoder;
	private final EmailService emailService;
	private final JwtUtil jwtUtil;
	private String otp = "in-valid";

	@Override
	@Transactional
	public String register(UserRegisterRequest userRegisterRequest) {

		log.info("Attempting to register user with email: {}", userRegisterRequest.getEmail());

		if (userRepository.findByEmail(userRegisterRequest.getEmail()).isPresent()) {
			log.warn("Registration failed - email {} already exists", userRegisterRequest.getEmail());
			throw new InvalidCredentialsException("Email already in use: " + userRegisterRequest.getEmail());
		}

		User user = userMapper.dtoToEntity(userRegisterRequest);
		log.debug("Mapped UserRegisterRequest to User entity: {}", user);
		user.setVerified(false);
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		User savedUser = userRepository.save(user);
		String verifyOtp = generateOtp(4);
		sendVerifyUserMail(savedUser.getEmail(), verifyOtp, savedUser.getName());
		log.info("User registered successfully with id: {}", savedUser.getId());

		return "User registered successfully with id:" + savedUser.getEmail() + " before login verify user from mail";
	}

	@Override
	public String verifyUser(VerifyUserRequest verifyUserRequest) {

		log.info("Attempting to verify User with email: {}", verifyUserRequest.getEmail());

		User user = userRepository.findByEmail(verifyUserRequest.getEmail()).orElseThrow(
				() -> new ResourceNotFoundException("User not found with email: " + verifyUserRequest.getEmail()));

		if (user.getVerified()) {
			throw new RuntimeException("user is already verified..");
		}
		if (!this.otp.equals(verifyUserRequest.getOtp())) {
			throw new InvalidCredentialsException("Code is not correct...");
		}
		user.setVerified(true);
		userRepository.save(user);
		this.otp = "in-valid";
		log.info("User verified successfully..");

		return "User " + user.getEmail() + " is verified successfully now you can login";
	}

	@Override
	public String login(UserLoginRequest userLoginRequest) {

		log.info("Attempting login for email: {}", userLoginRequest.getEmail());

		User user = userRepository.findByEmail(userLoginRequest.getEmail()).orElseThrow(() -> {
			log.warn("Login failed - user not found with email: {}", userLoginRequest.getEmail());
			return new ResourceNotFoundException("User not found with email: " + userLoginRequest.getEmail());
		});
		if (!user.getVerified()) {
			throw new RuntimeException("User is not verified. Please verify your account before logging in.");
		}
		if (!passwordEncoder.matches(userLoginRequest.getPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("Invalid password");
		}

		log.info("Login successful for email: {}", userLoginRequest.getEmail());
		return jwtUtil.generateToken(user.getEmail());
	}

	@Override
	public String forgotPassword(String email) {
		log.info("Forgot password service method executed for email: {}", email);

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
		
		if (!user.getVerified()) {
			throw new RuntimeException("User is not verified. Please verify your account.");
		}
		String otp = generateOtp(4);
		sendOtpMail(email, otp, user.getName());

		log.debug("Generated password reset code for {}: {}", email, otp);

		return "OTP is sent to your mail id";
	}

	@Override
	public String resetPassword(String code, String email, String newPassword) {
		log.info("Reset password service method executed for email: {}", email);

		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
		
		if (!user.getVerified()) {
			throw new RuntimeException("User is not verified. Please verify your account before logging in.");
		}

		if (this.otp.equals(code)) {
			user.setPassword(passwordEncoder.encode(newPassword));
			userRepository.save(user);
			this.otp = "Fail";
			log.debug("Password updated successfully for email: {}", email);
		} else {
			throw new InvalidCredentialsException("Code is not correct...");
		}
		return "Password updated successfully";
	}

	@Override
	public String changePassword(ChangePasswordRequest changePasswordRequest) {
		log.info("change password service method executed");

		User user = userRepository.findByEmail(changePasswordRequest.getEmail()).orElseThrow(
				() -> new ResourceNotFoundException("User not found with email: " + changePasswordRequest.getEmail()));
		
		if (!user.getVerified()) {
			throw new RuntimeException("User is not verified. Please verify your account before logging in.");
		}
		if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
			throw new InvalidCredentialsException("old password is not correct..");
		}
		user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
		userRepository.save(user);
		log.debug("Password changed successfully");
		
		return "Password changed successfully";
	}

	private String generateOtp(int length) {
		StringBuilder otp = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			otp.append(random.nextInt(10));
		}
		this.otp = otp.toString();
		return otp.toString();
	}

	private void sendVerifyUserMail(String mail, String otp, String name) {
		String toMail = mail;
		String subject = "Account Verification - OTP Code";

		String body = "<html>"
				+ "<body style='font-family: Arial, sans-serif; background-color:#f9f9f9; padding:20px;'>"
				+ "<div style='max-width:600px; margin:auto; background:#fff; padding:20px; border-radius:10px; "
				+ "box-shadow:0 2px 8px rgba(0,0,0,0.1);'>" + "<h2 style='color:#333;'>Account Verification - OTP Code</h2>"
				+ "<p>Dear " + name + ",</p>" + "<p>Thank you for registering with us.</p>"
				+ "<p>Your One-Time Password (OTP) for account verification is: "
				+ "<b style='color:#2563eb; font-size:18px;'>" + otp + "</b></p>"
				+ "<p>Please enter this code in the app to verify your email address.</p>"
				+ "<p style='color:red;'><i>Do not share this OTP with anyone for security reasons.</i></p>"
				+ "<br><p>Regards,<br><b>Treasurex Team</b></p>" + "</div>" + "</body>" + "</html>";

		emailService.sendEmail(toMail, subject, body);
	}

	private void sendOtpMail(String mail, String otp, String name) {
		String toMail = mail;
		String subject = "Password Reset - OTP Code";

		String body = "<html>"
				+ "<body style='font-family: Arial, sans-serif; background-color:#f9f9f9; padding:20px;'>"
				+ "<div style='max-width:600px; margin:auto; background:#fff; padding:20px; border-radius:10px; "
				+ "box-shadow:0 2px 8px rgba(0,0,0,0.1);'>" + "<h2 style='color:#333;'>Password Reset Request</h2>"
				+ "<p>Dear " + name + ",</p>" + "<p>Your One-Time Password (OTP) for password reset is: "
				+ "<b style='color:#2563eb; font-size:18px;'>" + otp + "</b></p>"
				+ "<p>Please use this OTP to reset your account password.</p>"
				+ "<p style='color:red;'><i>Do not share this OTP with anyone for security reasons.</i></p>"
				+ "<br><p>Regards,<br><b>Treasurex Team</b></p>" + "</div>" + "</body>" + "</html>";

		emailService.sendEmail(toMail, subject, body);
	}

}