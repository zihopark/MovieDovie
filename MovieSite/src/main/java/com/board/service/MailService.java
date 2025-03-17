package com.board.service;


import java.io.UnsupportedEncodingException;

import org.springframework.messaging.MessagingException;

import jakarta.mail.internet.MimeMessage;

public interface MailService {
	
	// 메일 내용 작성
	public MimeMessage createEmailForm(String email) throws MessagingException, UnsupportedEncodingException, jakarta.mail.MessagingException;
	
	// 메일 발송
	public String sendEmail(String email) throws MessagingException, UnsupportedEncodingException, jakarta.mail.MessagingException;
	
	// 랜덤 인증 코드 랜덤 생성
	public void createCode();
}
