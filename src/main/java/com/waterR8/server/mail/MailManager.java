package com.waterR8.server.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailManager {
	
	static MailManager __instance;
	public static MailManager getInstance() {
		if(__instance == null) {
			__instance = new MailManager();
		}
		return __instance;
	}

	// ygreco/#Jack2012
	// 310350/Brooks1967
	public void sendEmail(String emailToSendTo, String from, String subject,  String messageText) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "localhost");
		props.put("mail.smtp.auth", "false");
		props.put("mail.smtp.port", "25");
		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(
								"user", "password");
					}
				});
		try {
			try {
			    Message msg = new MimeMessage(session);
			    msg.setFrom(new InternetAddress(from, "WaterR8 Admin"));
			    msg.addRecipient(Message.RecipientType.TO,    new InternetAddress(emailToSendTo, "WaterR8 Client"));
			    msg.setSubject(subject);
			    msg.setText(messageText);
			    Transport.send(msg);
	
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	static public void main(String as[]) {
		try {
			new MailManager().sendEmail("casey@hotmath.com","me@test.com", "My Test Email", "The is the sample email text");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
