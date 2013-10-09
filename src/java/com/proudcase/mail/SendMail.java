package com.proudcase.mail;

import com.proudcase.exclogger.ExceptionLogger;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Copyright © 17.07.2013 Michel Vocks This file is part of proudcase.
 *
 * proudcase is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * proudcase is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * proudcase. If not, see <http://www.gnu.org/licenses/>.
 *
 * /
 *
 **
 * @Author: Michel Vocks
 *
 * @Date: 17.07.2013
 *
 * @Encoding: UTF-8
 */
public final class SendMail {

    private static final String SENDERHOST = "smtp.udag.de";
    private static final String SENDEREMAIL = "noreply@proudcase.com";
    private static final String SENDPORT = "587";
    private static final String USERNAME = "proudcasecom-0001";
    private static final String USERPASS = "michel11437400";
    private static final String CONTENT = "text/html";
    
    // Authenticator
    private static final Authenticator authenticator =
            new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(USERNAME, USERPASS);
                }
            };

    public SendMail() {
    }

    public static void sendMail(String destination, String subject, String message) throws ExceptionLogger {
        // Generate properties and set the settings
        Properties props = new Properties();
        props.put("mail.smtp.host", SENDERHOST);
        props.put("mail.from", SENDEREMAIL);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.port", SENDPORT);
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        
        // get the session
        Session session = Session.getDefaultInstance(props, authenticator);
        
        try {
            // Create a new mimemessage
            Message emailMessage = new MimeMessage(session);
            
            // From
            emailMessage.setFrom(new InternetAddress(SENDEREMAIL, SENDEREMAIL));
            
            // To
            emailMessage.setRecipient(Message.RecipientType.TO, 
                    new InternetAddress(destination, destination));
            
            // Subject
            emailMessage.setSubject(subject);
            
            // Content
            emailMessage.setContent(message, CONTENT);
            
            // Send the message!
            Transport.send(emailMessage);
        } catch (MessagingException | UnsupportedEncodingException ex) {
            // when the user types an invalid email then we are here
            // so we just do nothing
        }
    }
}
