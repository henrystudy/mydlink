package com.hisoft.mail;

import com.hisoft.entity.MailEntity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.MimeMessage;
import org.apache.log4j.Logger;

public class ReceiveEmailHelper
{
  static Logger logger = Logger.getLogger(ReceiveEmailHelper.class);
  private String reciveServerAddrss = "pop.sina.cn";
  private String protocal = "pop3";
  private int mailNum = 5;
  private boolean readNewOnly = true;
  private final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
  
  public ReceiveEmailHelper() {}
  
  public ReceiveEmailHelper(boolean readNewOnly)
  {
    this.readNewOnly = readNewOnly;
  }
  
  public ReceiveEmailHelper(int mailNum)
  {
    this.mailNum = mailNum;
  }
  
  public ReceiveEmailHelper(int mailNum, boolean readNewOnly)
  {
    this.mailNum = mailNum;
    this.readNewOnly = readNewOnly;
  }
  
  public ReceiveEmailHelper(String reciveServerAddrss, String protocal, int mailNum, boolean readNewOnly)
  {
    this.reciveServerAddrss = reciveServerAddrss;
    this.protocal = protocal;
    this.mailNum = mailNum;
    this.readNewOnly = readNewOnly;
  }
  
  public String getReciveServerAddrss()
  {
    return this.reciveServerAddrss;
  }
  
  public void setReciveServerAddrss(String reciveServerAddrss)
  {
    this.reciveServerAddrss = reciveServerAddrss;
  }
  
  public String getProtocal()
  {
    return this.protocal;
  }
  
  public void setProtocal(String protocal)
  {
    this.protocal = protocal;
  }
  
  public int getMailNum()
  {
    return this.mailNum;
  }
  
  public void setMailNum(int mailNum)
  {
    this.mailNum = mailNum;
  }
  
  public boolean isReadNewOnly()
  {
    return this.readNewOnly;
  }
  
  public void setReadNewOnly(boolean readNewOnly)
  {
    this.readNewOnly = readNewOnly;
  }
  
  public static MailEntity getFirstMail(String email, String password, boolean readNewOnly, boolean needWait)
    throws NoSuchProviderException, MessagingException, Exception
  {
    ReceiveEmailHelper receiveEmailHelper = new ReceiveEmailHelper(readNewOnly);
    List<MailEntity> tempList = receiveEmailHelper.reciveMailWithCheck(email, password, needWait);
    MailEntity mailEntity = (MailEntity)tempList.get(0);
    
    return mailEntity;
  }
  
  public static MailEntity getFirstMailBySubject(String email, String password, boolean readNewOnly, String subject, boolean needWait)
    throws NoSuchProviderException, MessagingException
  {
    ReceiveEmailHelper receiveEmailHelper = new ReceiveEmailHelper(readNewOnly);
    List<MailEntity> tempList = receiveEmailHelper.reciveMailWithCheck(email, password, needWait);
    for (int i = 0; i < tempList.size(); i++)
    {
      MailEntity mailEntity = (MailEntity)tempList.get(i);
      if ((mailEntity.getSubject().equals(subject)) && (mailEntity.getMailTo().contains(email))) {
        return mailEntity;
      }
    }
    return null;
  }
  
  public static MailEntity getFirstMailBySubjectAndmailTo(String email, String password, boolean readNewOnly, String subject, String mailTo, boolean needWait)
    throws NoSuchProviderException, MessagingException
  {
    ReceiveEmailHelper receiveEmailHelper = new ReceiveEmailHelper(readNewOnly);
    List<MailEntity> tempList = receiveEmailHelper.reciveMailWithCheck(email, password, needWait);
    for (int i = 0; i < tempList.size(); i++)
    {
      MailEntity mailEntity = (MailEntity)tempList.get(i);
      if ((mailEntity.getSubject().equals(subject)) && (mailEntity.getMailTo().contains(email))) {
        return mailEntity;
      }
    }
    return null;
  }
  
  public static List<MailEntity> getTwoMailBySubject(String email, String password, boolean readNewOnly, String subject, boolean needWait)
    throws NoSuchProviderException, MessagingException, Exception
  {
    ReceiveEmailHelper receiveEmailHelper = new ReceiveEmailHelper(readNewOnly);
    List<MailEntity> tempList = receiveEmailHelper.reciveMailWithCheck(email, password, needWait);
    
    List<MailEntity> mailEntityList = new ArrayList();
    for (int i = 0; (i < tempList.size()) && (i < 2); i++)
    {
      MailEntity mailEntity = (MailEntity)tempList.get(i);
      if (mailEntity.getSubject().equals(subject)) {
        mailEntityList.add(mailEntity);
      }
    }
    if (new Date(((MailEntity)mailEntityList.get(0)).getSentDate()).compareTo(new Date(((MailEntity)mailEntityList.get(1)).getSentDate())) < 0) {
      mailEntityList.add(0, (MailEntity)mailEntityList.remove(1));
    }
    return mailEntityList;
  }
  
  public List<MailEntity> reciveMailWithCheck(String email, String password, boolean needWait)
    throws NoSuchProviderException, MessagingException
  {
    boolean flag = true;
    int reConnectCount = 0;
    List<MailEntity> mailEntityList = null;
    try
    {
      logger.info("Waiting for email to arrive, please wait...");
      if (needWait) {
        Thread.sleep(60000L);
      }
      logger.info("Receiving email, please wait...");
    }
    catch (InterruptedException e1)
    {
      logger.error(e1.getMessage());
    }
    while (flag) {
      try
      {
        mailEntityList = reciveMail(email, password);
        flag = false;
      }
      catch (MessagingException e)
      {
        flag = true;
        if (reConnectCount++ > 3)
        {
          flag = false;
          throw new MessagingException("Cannot connect email server,please check your email server");
        }
      }
      catch (Exception e)
      {
        logger.error(e.getMessage());
      }
    }
    if ((mailEntityList == null) || (mailEntityList.isEmpty()))
    {
      logger.error("The mail is null.");
      throw new MessagingException("The mail is null.");
    }
    return mailEntityList;
  }
  
  public List<MailEntity> reciveMail(String email, String password)
    throws NoSuchProviderException, MessagingException, Exception
  {
    if (email.indexOf("+") != -1) {
      email = email.substring(0, email.indexOf("+")) + email.substring(email.indexOf("@"));
    }
    boolean reciveFlag = false;
    ReceiveEmail pmm = null;
    List<MailEntity> mailEntityList = new ArrayList();
    
    Store store = chooseMailServer(email, password);
    for (int times = 0; (times < 3) && (!reciveFlag); times++)
    {
      Folder folder = store.getFolder("INBOX");
      folder.open(1);
      Message[] message = folder.getMessages();
      if (message.length > 0)
      {
        int i = message.length - 1;
        do
        {
          pmm = new ReceiveEmail((MimeMessage)message[i]);
          if ((!this.readNewOnly) || (!pmm.isNew()))
          {
            MailEntity mailEntity = new MailEntity();
            mailEntity.setSubject(pmm.getSubject());
            mailEntity.setSentDate(pmm.getSentDate());
            mailEntity.setHasRead(pmm.isNew());
            mailEntity.setFrom(pmm.getFrom());
            mailEntity.setFromPersonal(pmm.getFromPersonal());
            mailEntity.setFromAddress(pmm.getFromAddress());
            mailEntity.setMailTo(pmm.getMailAddress("to"));
            
            pmm.getMailContent(message[i]);
            mailEntity.setContentText((String)pmm.getBodyText().get(0));
            mailEntity.setContent((String)pmm.getBodyText().get(1));
            
            mailEntityList.add(mailEntity);
            
            reciveFlag = true;
          }
          i--;
          if (i <= message.length - this.mailNum - 1) {
            break;
          }
        } while (i >= 0);
      }
      else
      {
        Thread.sleep(30000L);
      }
      folder.close(false);
    }
    store.close();
    return mailEntityList;
  }
  
  public Store chooseMailServer(String email, String password)
    throws NoSuchProviderException, MessagingException, Exception
  {
    String temp = email.substring(email.indexOf("@") + 1, email.lastIndexOf("."));
    if (temp.equals("gmail")) {
      return setGmailFieldAndConnect(email, password);
    }
    if (temp.equals("sina")) {
      return setFieldAndConnect(email, password);
    }
    logger.error("does support the email type, only support gmail and sina.");
    return null;
  }
  
  private Store setGmailFieldAndConnect(String email, String password)
    throws NoSuchProviderException, MessagingException, Exception
  {
    String temp = email.substring(email.indexOf("@") + 1);
    Properties props = System.getProperties();
    props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.setProperty("mail.pop3.socketFactory.fallback", "false");
    props.setProperty("mail.pop3.port", "995");
    props.setProperty("mail.pop3.socketFactory.port", "995");
    props.put("mail.smtp.host", "pop." + temp);
    props.put("mail.smtp.auth", "true");
    Session session = Session.getDefaultInstance(props, null);
    URLName urln = new URLName(this.protocal, "pop." + temp, 995, null, email.substring(0, email.indexOf("@")), password);
    Store store = session.getStore(urln.getProtocol());
    store.connect("pop." + temp, email.substring(0, email.indexOf("@")), password);
    return store;
  }
  
  private Store setFieldAndConnect(String email, String password)
    throws NoSuchProviderException, MessagingException, Exception
  {
    String temp = email.substring(email.indexOf("@") + 1);
    Properties props = System.getProperties();
    props.put("mail.smtp.host", "pop." + temp);
    props.put("mail.smtp.auth", "true");
    Session session = Session.getDefaultInstance(props, null);
    URLName urln = new URLName(this.protocal, "pop." + temp, 110, null, email.substring(0, email.indexOf("@")), password);
    Store store = session.getStore(urln.getProtocol());
    store.connect("pop." + temp, email.substring(0, email.indexOf("@")), password);
    return store;
  }
}
