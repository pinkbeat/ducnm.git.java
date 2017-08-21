package com.vsc.util;
/*
 * make up & decorated by ducnm
 */
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class MailUtil {

    public static final int MAX_MESSAGES_PER_TRANSPORT = 200;

    private static Log log = LogFactory.getLog(MailUtil.class);

    private MailUtil() {// prevent instantiation
    }
    public static void main(String[] asd){
    	MailUtil mailUtil = new MailUtil();
    	try {
			//mailUtil.sendMail(null, "ducnm@vnpt.vn", "pinkbeat@gmail.com", "duc33.vn@gmail.com", "Mail sent by java", "Xin chào đồng chí");
    		String[] fileAttach = {"E:\\dothilap.xls","E:\\vcredist.bmp"};
			mailUtil.sendMail(null, "ducnm@vnpt.vn", "pinkbeat@gmail.com", "duc33.vn@gmail.com", "Mail sent by java", "Xin chào đồng chí",fileAttach);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    //private static MailOptions mailOption = new MailOptions();

    public static String getEmailUsername(String email) {
        if (email == null) return "";
        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            return "";
        }
        return email.substring(0, atIndex);
    }


    public static String getEmailDomain(String email) {
        if (email == null) return "";
        int atIndex = email.indexOf('@');
        if (atIndex == -1) {
            return "";
        }
        return email.substring(atIndex + 1);
    }


    public static void checkEmailFormat(String input) throws BadInputException {
        if (input == null) throw new BadInputException("Sorry, null string is not a good email.");//@todo : localize me
        int atIndex = input.indexOf('@');
        int dotIndex = input.lastIndexOf('.');
        if ((atIndex == -1) || (dotIndex == -1) || (atIndex >= dotIndex)) {
            //@todo : localize me
            throw new BadInputException("Error: '" + input + "' is not a valid email value. Please try again.");
        }

        // now check for content of the string
        int length = input.length();
        char c = 0;

        for (int i = 0; i < length; i++) {
            c = input.charAt(i);
            if ((c >= 'a') && (c <= 'z')) {
                // lower char
            } else if ((c >= 'A') && (c <= 'Z')) {
                // upper char
            } else if ((c >= '0') && (c <= '9')/* && (i != 0)*/) {
                // as of 31 Jan 2004, i relax the email checking
                // so that the email can start with an numeric char
                // hopefully it does not introduce a security bug
                // because this value will be inserted into sql script
                // numeric char
            } else if ( ( (c=='_') || (c=='-') || (c=='.') || (c=='@') ) && (i != 0) ) {
                // _ char
            } else {
                // not good char, throw an BadInputException
                //@todo : localize me
                throw new BadInputException(input + " is not a valid email. Reason: character '" + c + "' is not accepted in an email.");
            }
        }// for

        // last check
        try {
            new javax.mail.internet.InternetAddress(input);
        } catch (Exception ex) {
            log.error("Error when running checkEmailFormat", ex);
            throw new BadInputException("Assertion: dont want to occur in Util.checkEmailFormat");
        }
    }


    public static void sendMail(String from, String to, String cc, String bcc, String subject, String message, String[] files)
        throws MessagingException, BadInputException, UnsupportedEncodingException {

        MailMessage mailItem = new MailMessage();
        mailItem.setFrom(from);
        mailItem.setTo(to);
        mailItem.setCc(cc);
        mailItem.setBcc(bcc);
        mailItem.setSubject(subject);
        mailItem.setMessage(message);
        mailItem.setFileAttach(files);
        
        ArrayList mailList = new ArrayList(1);
        mailList.add(mailItem);
        try {
	        if(files != null && files.length!=0){
	        	sendMailAttachment(mailList);
	        }else{
	        	sendMail(mailList);
	        }
        } catch (MessagingException mex) {
            System.out.println(mex.getMessage());
            log.debug("MessagingException has occured. Detail info:");
            log.debug("from = " + mailItem.getFrom());
            log.debug("to = " + mailItem.getTo());
            log.debug("cc = " + mailItem.getCc());
            log.debug("bcc = " + mailItem.getBcc());
            log.debug("subject = " + mailItem.getSubject());
            log.debug("message = " + mailItem.getMessage());
            throw mex;// this may look redundant, but it is not :-)
        }
    }

    public static void sendMail(Collection mailStructCollection)
        throws MessagingException, BadInputException, UnsupportedEncodingException {

        Session session = null;
        Transport transport = null;
        int totalEmails = mailStructCollection.size();
        int count = 0;
        int sendFailedExceptionCount = 0;
        try {
            for (Iterator iter = mailStructCollection.iterator(); iter.hasNext(); ) {
                count++;

                if ((transport == null) || (session == null)) {
                    Properties props = new Properties();

                    String server = MailConfig.getMailServer();
                    String port = MailConfig.getMailServerPort();
                    String userName = MailConfig.getMailUserName();
                    String password = MailConfig.getMailPassword(); 

                    props.put("mail.smtp.host", server);
                    props.put("mail.smtp.port", port);
                    if ( (userName != null) && (userName.length() > 0) ) {
                        props.put("mail.smtp.auth", "true");
                    }
                    props.put("mail.debug", "true");
                    session = Session.getDefaultInstance(props, null);
                    transport = session.getTransport("smtp");
                    if ((userName != null) && (userName.length() > 0)) {
                        transport.connect(server, userName, password);
                    } else {
                        transport.connect();
                    }
                }

                MailMessage mailItem = (MailMessage)iter.next();

                String from = mailItem.getFrom();
                String to = mailItem.getTo();
                String cc = mailItem.getCc();
                String bcc = mailItem.getBcc();
                String subject = mailItem.getSubject();
                String message = mailItem.getMessage();

                //if (from == null) from = mailOption.defaultMailFrom;
                if (from == null) from = MailConfig.getDefaultMailFrom();

                try {
                    // this will also check for email error
                    checkEmailFormat(from);
                    InternetAddress fromAddress = new InternetAddress(from);
                    InternetAddress[] toAddress = getInternetAddressEmails(to);
                    InternetAddress[] ccAddress = getInternetAddressEmails(cc);
                    InternetAddress[] bccAddress = getInternetAddressEmails(bcc);
                    if ((toAddress == null) && (ccAddress == null) && (bccAddress == null)) {
                        //@todo : localize me
                        throw new BadInputException("Cannot send mail since all To, Cc, Bcc addresses are empty.");
                    }
                    // create a message
                    MimeMessage msg = new MimeMessage(session);
                    msg.setSentDate(new Date());
                    msg.setFrom(fromAddress);

                    if (toAddress != null) {
                        msg.setRecipients(Message.RecipientType.TO, toAddress);
                    }
                    if (ccAddress != null) {
                        msg.setRecipients(Message.RecipientType.CC, ccAddress);
                    }
                    if (bccAddress != null) {
                        msg.setRecipients(Message.RecipientType.BCC, bccAddress);
                    }

                    msg.setSubject(subject,"UTF-8");
                    msg.setText(message,"UTF-8");
                    msg.saveChanges();

                    transport.sendMessage(msg, msg.getAllRecipients());
//                    transport.send(msg, msg.getAllRecipients());

                    // now check if sent 200 emails, then close connection (transport)
                    if ((count % MAX_MESSAGES_PER_TRANSPORT) == 0) {
                        try {
                            if (transport != null) transport.close();
                        } catch (MessagingException ex) {}
                        transport = null;
                        session = null;
                    }
                } catch (SendFailedException ex) {
                    sendFailedExceptionCount++;
                    log.error("SendFailedException has occured.", ex);
                    log.warn("SendFailedException has occured. Detail info:");
                    log.warn("from = " + from);
                    log.warn("to = " + to);
                    log.warn("cc = " + cc);
                    log.warn("bcc = " + bcc);
                    log.warn("subject = " + subject);
                    log.info("message = " + message);
                    if ((totalEmails != 1) && (sendFailedExceptionCount > 10)) {
                        throw ex;// this may look redundant, but it is not :-)
                    }
                } catch (MessagingException mex) {
                    log.error("MessagingException has occured.", mex);
                    log.warn("MessagingException has occured. Detail info:");
                    log.warn("from = " + from);
                    log.warn("to = " + to);
                    log.warn("cc = " + cc);
                    log.warn("bcc = " + bcc);
                    log.warn("subject = " + subject);
                    log.info("message = " + message);
                    throw mex;// this may look redundant, but it is not :-)
                }
            }
        } finally {
            try {
                if (transport != null) transport.close();
            } catch (MessagingException ex) { }
            if (totalEmails != 1) {
                log.info("sendMail: totalEmails = " + totalEmails + " sent count = " + count);
            }
        }
    }
    public static void sendMailAttachment(Collection mailStructCollection)
            throws MessagingException, BadInputException, UnsupportedEncodingException {

            Session session = null;
            Transport transport = null;
            int totalEmails = mailStructCollection.size();
            int count = 0;
            int sendFailedExceptionCount = 0;
            try {
                for (Iterator iter = mailStructCollection.iterator(); iter.hasNext(); ) {
                    count++;

                    if ((transport == null) || (session == null)) {
                        Properties props = new Properties();

                        String server = MailConfig.getMailServer();
                        String port = MailConfig.getMailServerPort();
                        String userName = MailConfig.getMailUserName();
                        String password = MailConfig.getMailPassword(); 

                        props.put("mail.smtp.host", server);
                        props.put("mail.smtp.port", port);
                        if ( (userName != null) && (userName.length() > 0) ) {
                            props.put("mail.smtp.auth", "true");
                        }
                        props.put("mail.debug", "true");
                        session = Session.getDefaultInstance(props, null);
                        transport = session.getTransport("smtp");
                        if ((userName != null) && (userName.length() > 0)) {
                            transport.connect(server, userName, password);
                        } else {
                            transport.connect();
                        }
                    }

                    MailMessage mailItem = (MailMessage)iter.next();

                    String from = mailItem.getFrom();
                    String to = mailItem.getTo();
                    String cc = mailItem.getCc();
                    String bcc = mailItem.getBcc();
                    String subject = mailItem.getSubject();
                    String message = mailItem.getMessage();
                    String files[] = mailItem.getFileAttach();

                    //if (from == null) from = mailOption.defaultMailFrom;
                    if (from == null) from = MailConfig.getDefaultMailFrom();

                    try {
                        // this will also check for email error
                        checkEmailFormat(from);
                        InternetAddress fromAddress = new InternetAddress(from);
                        InternetAddress[] toAddress = getInternetAddressEmails(to);
                        InternetAddress[] ccAddress = getInternetAddressEmails(cc);
                        InternetAddress[] bccAddress = getInternetAddressEmails(bcc);
                        if ((toAddress == null) && (ccAddress == null) && (bccAddress == null)) {
                            //@todo : localize me
                            throw new BadInputException("Cannot send mail since all To, Cc, Bcc addresses are empty.");
                        }
                        // create a message
                        MimeMessage msg = new MimeMessage(session);
                        msg.setSentDate(new Date());
                        msg.setFrom(fromAddress);

                        if (toAddress != null) {
                            msg.setRecipients(Message.RecipientType.TO, toAddress);
                        }
                        if (ccAddress != null) {
                            msg.setRecipients(Message.RecipientType.CC, ccAddress);
                        }
                        if (bccAddress != null) {
                            msg.setRecipients(Message.RecipientType.BCC, bccAddress);
                        }

                        msg.setSubject(subject,"UTF-8");
//                        msg.setText(message,"UTF-8");
//                        msg.saveChanges();
                        
                        MimeBodyPart mbp1 = new MimeBodyPart();
                	    mbp1.setText(message,"UTF-8");
                	    Multipart mp = new MimeMultipart();
                	    mp.addBodyPart(mbp1);
                	    // create the second message part
                	    MimeBodyPart mbp2 = null;

                	    // attach the files to the message
                	    for(int i=0;i<files.length;i++){
                	    	mbp2 = new MimeBodyPart();
                	    	mbp2.attachFile(files[i]);
                	    	mp.addBodyPart(mbp2);
                	    }
                	    
                	    
                	    // add the Multipart to the message
                	    msg.setContent(mp);
                	    // set the Date: header
                	    msg.setSentDate(new Date());
                	    
                        transport.sendMessage(msg, msg.getAllRecipients());
//                        transport.send(msg, msg.getAllRecipients());

                        // now check if sent 200 emails, then close connection (transport)
                        if ((count % MAX_MESSAGES_PER_TRANSPORT) == 0) {
                            try {
                                if (transport != null) transport.close();
                            } catch (MessagingException ex) {}
                            transport = null;
                            session = null;
                        }
                    } catch (SendFailedException ex) {
                        sendFailedExceptionCount++;
                        log.error("SendFailedException has occured.", ex);
                        log.warn("SendFailedException has occured. Detail info:");
                        log.warn("from = " + from);
                        log.warn("to = " + to);
                        log.warn("cc = " + cc);
                        log.warn("bcc = " + bcc);
                        log.warn("subject = " + subject);
                        log.info("message = " + message);
                        if ((totalEmails != 1) && (sendFailedExceptionCount > 10)) {
                            throw ex;// this may look redundant, but it is not :-)
                        }
                    } catch (MessagingException mex) {
                        log.error("MessagingException has occured.", mex);
                        log.warn("MessagingException has occured. Detail info:");
                        log.warn("from = " + from);
                        log.warn("to = " + to);
                        log.warn("cc = " + cc);
                        log.warn("bcc = " + bcc);
                        log.warn("subject = " + subject);
                        log.info("message = " + message);
                        throw mex;// this may look redundant, but it is not :-)
                    } catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            } finally {
                try {
                    if (transport != null) transport.close();
                } catch (MessagingException ex) { }
                if (totalEmails != 1) {
                    log.info("sendMail: totalEmails = " + totalEmails + " sent count = " + count);
                }
            }
        }

 
    public static String[] getEmails(String email) throws BadInputException {
        if (email == null) email = "";
        email = email.trim();// very important
        email = email.replace(',', ';');// replace all occurrence of ',' to ';'
        StringTokenizer t = new StringTokenizer(email, ";");
        String[] ret = new String[t.countTokens()];
        int index = 0;
        while(t.hasMoreTokens()) {
            String mail = t.nextToken().trim();
            checkEmailFormat(mail);
            ret[index] = mail;
            //log.debug(ret[index]);
            index++;
        }
        return ret;
    }

    public static String[] getEmails(String to, String cc, String bcc) throws BadInputException {
        String[] toMail = getEmails(to);
        String[] ccMail = getEmails(cc);
        String[] bccMail= getEmails(bcc);
        String[] ret = new String[toMail.length + ccMail.length + bccMail.length];
        int index = 0;
        for (int i = 0; i < toMail.length; i++) {
            ret[index] = toMail[i];
            index++;
        }
        for (int i = 0; i < ccMail.length; i++) {
            ret[index] = ccMail[i];
            index++;
        }
        for (int i = 0; i < bccMail.length; i++) {
            ret[index] = bccMail[i];
            index++;
        }
        return ret;
    }


    private static InternetAddress[] getInternetAddressEmails(String email)
        throws BadInputException, AddressException {
        String[] mails = getEmails(email);
        if (mails.length == 0) return null;// must return null, not empty array

        //log.debug("to = " + mails);
        InternetAddress[] address = new InternetAddress[mails.length];
        for (int i = 0; i < mails.length; i++) {
            address[i] = new InternetAddress(mails[i]);
            //log.debug("to each element = " + mails[i]);
        }
        return address;
    }

}

class  MailMessage {

    private String from;
    private String to;
    private String cc;
    private String bcc;
    private String subject;
    private String message;
    private String[] fileAttach;

    public MailMessage() {
    }
    protected String getFrom() {
        return from;
    }
    protected void setFrom(String from) {
        this.from = from;
    }
    protected String getTo() {
        return to;
    }
    protected void setTo(String to) {
        this.to = to;
    }
    protected String getCc() {
        return cc;
    }
    protected void setCc(String cc) {
        this.cc = cc;
    }
    protected String getBcc() {
        return bcc;
    }
    protected void setBcc(String bcc) {
        this.bcc = bcc;
    }
    protected String getSubject() {
        return subject;
    }
    protected void setSubject(String subject) {
        this.subject = subject;
    }
    protected String getMessage() {
        return message;
    }
    protected void setMessage(String message) {
        this.message = message;
    }
    protected String[] getFileAttach() {
        return fileAttach;
    }
    protected void setFileAttach(String[] files) {
        this.fileAttach = files;
    }
}
class BadInputException extends Exception {
   public BadInputException(String msg) {
      super(msg);
   }
}

class MailConfig {
    private static Log log = LogFactory.getLog(MailConfig.class);
    private static Properties prop = null;
    private static String filename = "mail.properties";
    private static String mailServer = "";
    private static String defaultMailFrom = "";
    private static String mailServerPort = "";
    private static String mailUserName = "";
    private static String mailPassword = "";


    public static String getMailServer() {
        return mailServer;
    }
    public static String getMailServerPort() {
        return mailServerPort;
    }
    public static String getDefaultMailFrom() {
        return defaultMailFrom;
    }
    public static String getMailUserName() {
        return mailUserName;
    }
    public static String getMailPassword() {
        return mailPassword;
    }

    
    static {
        load();
    }

    public static void load() {
        reload();
    }
    
    private static Properties getResource() {
    	prop = new Properties();
    	InputStream input = null;    
    	try{
			input = MailConfig.class.getResourceAsStream(filename);	
			prop.load(input);
    	} catch (IOException e) {
            String message = "Can't read the configuration file: '" + filename + "'. Make sure the file is in your CLASSPATH";    		          
    		log.error(message, e);
        }		
		return prop;
    }

    private static void reload() {
            /* <Mail Options> */
        	prop = getResource();
            mailServer = prop.getProperty("mail.server");
            defaultMailFrom = prop.getProperty("mail.default_from");
            mailUserName = prop.getProperty("mail.username");
            mailPassword = prop.getProperty("mail.password");
            mailServerPort = prop.getProperty("mail.port");
    }
}