package com.waterR8.server.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.codemonkey.simplejavamail.Email;
import org.codemonkey.simplejavamail.Mailer;
import org.codemonkey.simplejavamail.TransportStrategy;

public class MailManager {

	// ygreco/#Jack2012
	// 310350/Brooks1967
	public void sendEmail(String emailToSendTo) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(
								"caseyrodgers@gmail.com", "kcbr1962_B");
					}
				});
		
		try {
		
			String msgBody = "...";
			try {
			    Message msg = new MimeMessage(session);
			    msg.setFrom(new InternetAddress("casey@caseyrodgers.com", "Example.com Admin"));
			    msg.addRecipient(Message.RecipientType.TO,    new InternetAddress("caseyrodgers@gmail.com", "Mr. User"));
			    msg.setSubject("Your Example.com account has been activated");
			    msg.setText(msgBody);
			    Transport.send(msg);
	
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		try {

			String to = "sonoojaiswal1987@gmail.com";// change accordingly

			// Get the session object


			// compose message
			try {
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress("caseyrodgers@gmail.com"));// change
																				// accordingly
				message.addRecipient(Message.RecipientType.TO,new InternetAddress("caseyrodgers@gmail.com"));
				message.setSubject("Hello");
				message.setText("Testing.......");

				// send message
				Transport.send(message);

				System.out.println("message sent successfully");

			} catch (MessagingException e) {
				throw new RuntimeException(e);
			}

			final Email email = new Email();

			// smtp: new Mailer("smtp.host.com", 25, "username", "password")
			// smtps: new Mailer("smtp.host.com", 25, "username", "password",
			// TransportStrategy.SMTP_SSL)
			// smtps tls: new Mailer("mail.smtp.port", 465, "username",
			// "password", TransportStrategy.SMTP_TLS)

			email.setFromAddress("casey", "caseyrodgers@gmail.com");
			email.setSubject("hey");
			email.addRecipient("Casey Rodgers", "caseyrodgers@gmail.com",
					RecipientType.TO);
			email.setText("This is a test!");

			new Mailer("smtp.gmail.com", 465, "caseyrodgers@gmail.com",
					"kcbr1962_B", TransportStrategy.SMTP_SSL).sendMail(email);

			email.setFromAddress("steve", "ssmith@s2tek.com");
			email.setSubject("hey");
			email.addRecipient("Casey Rodgers", "caseyrodgers@gmail.com",
					RecipientType.TO);
			email.setText("This is a test!");

			new Mailer("smtp.secureserver.net", 25, "ssmith@s2tek.com",
					"1rotor").sendMail(email);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	static public void main(String as[]) {
		try {
			new MailManager().sendEmail("casey@hotmath.com");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
