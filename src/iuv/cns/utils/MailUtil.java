package iuv.cns.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MailUtil {
	private final static Log LOG = LogFactory.getLog(MailUtil.class);

	/**
	 * 发送邮件
	 * 
	 * @param subject
	 * @param content
	 * @return
	 */
	public static boolean sendMail(String subject, String content) {
		LOG.info("发送邮件了！主题：【"+subject+"】正文：【"+content+"】");
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", Constants.Mail_SMPTHOST);
		properties.put("mail.smtp.auth", "true");
		Session session = Session.getDefaultInstance(properties, new Authenticator() {
			@Override
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(Constants.Mail_USER, Constants.Mail_PASSWD); // 发件人邮件用户名、密码
			}
		});
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(Constants.Mail_FROM));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(Constants.Mail_TO));
			message.setSubject(subject);
			message.setText(content);
			Transport.send(message);
		} catch (MessagingException e) {
			LOG.error("发送邮件失败了", e);
			return false;
		}
		return true;
	}

	// public static void main(String[] args) {
	// MailUtil.sendMail("test", "aaa");
	// }
}
