package com.crisnello.moviedb.util;


import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.SimpleEmail;

import java.util.Properties;

import javax.mail.AuthenticationFailedException;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class SendEmail {

    private String mailSMTPServer;
    private String mailSMTPServerPort;
    private String pUser, pPass;
    public SendEmail() { //Para o GMAIL
        mailSMTPServer = "smtp.gmail.com";
        mailSMTPServerPort = "587";
    }
    SendEmail(String mailSMTPServer, String mailSMTPServerPort) { //Para outro Servidor
        this.mailSMTPServer = mailSMTPServer;
        this.mailSMTPServerPort = mailSMTPServerPort;
    }
    public void sendMail(String from,String pSenhaFrom, Object[] to, String subject, String message) throws AuthenticationFailedException, Exception {
    	pUser = from;
    	pPass = pSenhaFrom;
    	sendMail(from, to, subject, message);
    }
    public void sendMail(String from, Object[] tos, String subject, String message)throws AuthenticationFailedException,Exception {
    	
		//FUNCIONA
		
    	SimpleEmail email = new SimpleEmail();
    	String authuser = pUser;
    	String authpwd = pPass;
    	
//    	System.out.println("User "+pUser+" Password "+pPass);

    	email.setSmtpPort(587);
    	email.setAuthenticator(new DefaultAuthenticator(authuser, authpwd));
    	email.setDebug(true);
    	email.setHostName("smtp.gmail.com");
    	email.getMailSession().getProperties().put("mail.smtps.auth", "true");
    	email.getMailSession().getProperties().put("mail.debug", "true");
    	email.getMailSession().getProperties().put("mail.smtps.port", "587");
    	email.getMailSession().getProperties().put("mail.smtps.socketFactory.port", "587");
    	email.getMailSession().getProperties().put("mail.smtps.socketFactory.class",   "javax.net.ssl.SSLSocketFactory");
    	email.getMailSession().getProperties().put("mail.smtps.socketFactory.fallback", "false");
    	email.getMailSession().getProperties().put("mail.smtp.starttls.enable", "true");
    	email.setFrom(pUser, "Moviedb");
    	email.setSubject("[ Moviedb ]");
    	email.setMsg("Sua senha é : ");
    	//email.addTo("crisnello@gmail.com", "crisnello");
		for(int i=0;i<tos.length;i++){
			email.addTo(tos[i].toString(), "moviedb");
		}
    	//email.setTLS(true);
    	email.send();
		
    }
	public String getPPass() {
		return pPass;
	}
	public void setPPass(String pass) {
		pPass = pass;
	}
	public String getPUser() {
		return pUser;
	}
	public void setPUser(String user) {
		pUser = user;
	}
}



//clase que retorna uma autenticacao para ser enviada e verificada pelo servidor smtp
class SimpleAuth extends Authenticator {
  public String username = null;
  public String password = null;


  public SimpleAuth(String user, String pwd) {
      username = user;
      password = pwd;
  }

  protected PasswordAuthentication getPasswordAuthentication() {
      return new PasswordAuthentication (username,password);
  }
}

