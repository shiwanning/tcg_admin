package com.tcg.admin.service.mail;
import java.util.Date;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * 简单邮件（不带附件的邮件）发送器
 */
public class SimpleMailSender {
    /**
     * 以文本格式发送邮件
     *
     * @param mailInfo 待发送的邮件的信息
     *
     * @throws MessagingException
     */
    public void sendTextMail(MailSenderInfo mailInfo) throws MessagingException {

        EmailAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate()) {
            authenticator = new EmailAuthenticator(mailInfo.getUserName(),
                                                   mailInfo.getPassword());
        }

        Session sendMailSession = Session.getInstance(pro, authenticator);
        sendMailSession.setDebug(true);

        MimeMessage mailMessage = new MimeMessage(sendMailSession);
        Address from = new InternetAddress(mailInfo.getFromAddress());
        mailMessage.setFrom(from);
        Address to = new InternetAddress(mailInfo.getToAddress());
        mailMessage.setRecipient(Message.RecipientType.TO, to);
        mailMessage.setSubject(mailInfo.getSubject(), "UTF-8");
        mailMessage.setSentDate(new Date());
        String mailContent = mailInfo.getContent();
        mailMessage.setText(mailContent, "UTF-8");

        Transport.send(mailMessage);
    }

    /**
     * 以HTML格式发送邮件
     *
     * @param mailInfo 待发送的邮件信息
     *
     * @throws MessagingException
     */
    public void sendHtmlMail(MailSenderInfo mailInfo) throws MessagingException {
        // 判断是否需要身份认证
        EmailAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        // 如果需要身份认证，则创建一个密码验证器
        if (mailInfo.isValidate()) {
            authenticator = new EmailAuthenticator(mailInfo.getUserName(),
                                                   mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getInstance(pro, authenticator);
        // 根据session创建一个邮件消息
        MimeMessage mailMessage = new MimeMessage(sendMailSession);
        // 创建邮件发送者地址
        Address from = new InternetAddress(mailInfo.getFromAddress());
        // 设置邮件消息的发送者
        mailMessage.setFrom(from);
        // 创建邮件的接收者地址，并设置到邮件消息中
        Address to = new InternetAddress(mailInfo.getToAddress());
        // Message.RecipientType.TO属性表示接收者的类型为TO
        mailMessage.setRecipient(Message.RecipientType.TO, to);
        // 设置邮件消息的主题
        mailMessage.setSubject(mailInfo.getSubject(), "UTF-8");
        // 设置邮件消息发送的时间
        mailMessage.setSentDate(new Date());
        // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
        Multipart mainPart = new MimeMultipart();
        // 创建一个包含HTML内容的MimeBodyPart
        BodyPart html = new MimeBodyPart();
        // 设置HTML内容
        html.setContent(mailInfo.getContent(), "text/html; charset=UTF-8");
        mainPart.addBodyPart(html);
        // 将MiniMultipart对象设置为邮件内容
        mailMessage.setContent(mainPart);
        // 发送邮件
        Transport.send(mailMessage);
    }
}