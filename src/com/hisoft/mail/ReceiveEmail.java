package com.hisoft.mail;

import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

public class ReceiveEmail
{
  private MimeMessage mimeMessage = null;
  private String saveAttachPath = "";
  private StringBuffer bodytext = new StringBuffer();
  private StringBuffer bodyHtmltext = new StringBuffer();
  private String dateformat = "yy-MM-dd HH:mm";
  
  public ReceiveEmail(MimeMessage mimeMessage)
  {
    this.mimeMessage = mimeMessage;
  }
  
  public void setMimeMessage(MimeMessage mimeMessage)
  {
    this.mimeMessage = mimeMessage;
  }
  
  public String getFrom()
    throws Exception
  {
    return getFromPersonal() + "," + getFromAddress();
  }
  
  public String getFromAddress()
    throws Exception
  {
    InternetAddress[] address = (InternetAddress[])this.mimeMessage.getFrom();
    String from = address[0].getAddress();
    if (from == null) {
      from = "";
    }
    return from;
  }
  
  public String getFromPersonal()
    throws Exception
  {
    InternetAddress[] address = (InternetAddress[])this.mimeMessage.getFrom();
    String personal = address[0].getPersonal();
    if (personal == null) {
      personal = "";
    }
    return personal;
  }
  
  public String getMailAddress(String type)
    throws Exception
  {
    String mailaddr = "";
    String addtype = type.toUpperCase();
    InternetAddress[] address = (InternetAddress[])null;
    if ((addtype.equals("TO")) || (addtype.equals("CC")) || (addtype.equals("BCC")))
    {
      if (addtype.equals("TO")) {
        address = (InternetAddress[])this.mimeMessage.getRecipients(Message.RecipientType.TO);
      } else if (addtype.equals("CC")) {
        address = (InternetAddress[])this.mimeMessage.getRecipients(Message.RecipientType.CC);
      } else {
        address = (InternetAddress[])this.mimeMessage.getRecipients(Message.RecipientType.BCC);
      }
      if (address != null)
      {
        for (int i = 0; i < address.length; i++)
        {
          String email = address[i].getAddress();
          if (email == null) {
            email = "";
          } else {
            email = MimeUtility.decodeText(email);
          }
          String personal = address[i].getPersonal();
          if (personal == null) {
            personal = "";
          } else {
            personal = MimeUtility.decodeText(personal);
          }
          String compositeto = personal + email;
          mailaddr = mailaddr + "," + compositeto;
        }
        mailaddr = mailaddr.substring(1);
      }
    }
    else
    {
      throw new Exception("Error emailaddr type!");
    }
    return mailaddr;
  }
  
  public String getSubject()
    throws MessagingException
  {
    String subject = "";
    try
    {
      subject = MimeUtility.decodeText(this.mimeMessage.getSubject());
      if (subject == null) {
        subject = "";
      }
    }
    catch (Exception localException) {}
    return subject;
  }
  
  public String getSentDate()
    throws Exception
  {
    Date sentdate = this.mimeMessage.getSentDate();
    SimpleDateFormat format = new SimpleDateFormat(this.dateformat);
    return format.format(sentdate);
  }
  
  public List<String> getBodyText()
  {
    List<String> list = new ArrayList();
    list.add(this.bodytext.toString());
    list.add(this.bodyHtmltext.toString());
    return list;
  }
  
  public void getMailContent(Part part)
    throws Exception
  {
    String contenttype = part.getContentType();
    int nameindex = contenttype.indexOf("name");
    boolean conname = false;
    if (nameindex != -1) {
      conname = true;
    }
    if ((part.isMimeType("text/plain")) && (!conname))
    {
      this.bodytext.append((String)part.getContent());
    }
    else if ((part.isMimeType("text/html")) && (!conname))
    {
      this.bodyHtmltext.append((String)part.getContent());
    }
    else if (part.isMimeType("multipart/*"))
    {
      Multipart multipart = (Multipart)part.getContent();
      int counts = multipart.getCount();
      for (int i = 0; i < counts; i++) {
        getMailContent(multipart.getBodyPart(i));
      }
    }
    else if (part.isMimeType("message/rfc822"))
    {
      getMailContent((Part)part.getContent());
    }
  }
  
  public boolean getReplySign()
    throws MessagingException
  {
    boolean replysign = false;
    String[] needreply = this.mimeMessage.getHeader("Disposition-Notification-To");
    if (needreply != null) {
      replysign = true;
    }
    return replysign;
  }
  
  public String getMessageId()
    throws MessagingException
  {
    return this.mimeMessage.getMessageID();
  }
  
  public boolean isNew()
    throws MessagingException
  {
    boolean isnew = false;
    Flags flags = this.mimeMessage.getFlags();
    Flags.Flag[] flag = flags.getSystemFlags();
    for (int i = 0; i < flag.length; i++) {
      if (flag[i] == Flags.Flag.SEEN)
      {
        isnew = true;
        System.out.println("seen Message.......");
        break;
      }
    }
    Date sentDate = this.mimeMessage.getSentDate();
    
    long times = new Date().getTime() - sentDate.getTime();
    if (times > 10800000L) {
      isnew = true;
    }
    return isnew;
  }
  
  public void isRead()
    throws MessagingException
  {}
  
  public boolean isContainAttach(Part part)
    throws Exception
  {
    boolean attachflag = false;
    if (part.isMimeType("multipart/*"))
    {
      Multipart mp = (Multipart)part.getContent();
      for (int i = 0; i < mp.getCount(); i++)
      {
        BodyPart mpart = mp.getBodyPart(i);
        String disposition = mpart.getDisposition();
        if ((disposition != null) && ((disposition.equals("attachment")) || (disposition.equals("inline"))))
        {
          attachflag = true;
        }
        else if (mpart.isMimeType("multipart/*"))
        {
          attachflag = isContainAttach(mpart);
        }
        else
        {
          String contype = mpart.getContentType();
          if (contype.toLowerCase().indexOf("application") != -1) {
            attachflag = true;
          }
          if (contype.toLowerCase().indexOf("name") != -1) {
            attachflag = true;
          }
        }
      }
    }
    else if (part.isMimeType("message/rfc822"))
    {
      attachflag = isContainAttach((Part)part.getContent());
    }
    return attachflag;
  }
  
  public void saveAttachMent(Part part)
    throws Exception
  {
    String fileName = "";
    if (part.isMimeType("multipart/*"))
    {
      Multipart mp = (Multipart)part.getContent();
      for (int i = 0; i < mp.getCount(); i++)
      {
        BodyPart mpart = mp.getBodyPart(i);
        String disposition = mpart.getDisposition();
        if ((disposition != null) && ((disposition.equals("attachment")) || (disposition.equals("inline"))))
        {
          fileName = mpart.getFileName();
          if (fileName.toLowerCase().indexOf("gb2312") != -1) {
            fileName = MimeUtility.decodeText(fileName);
          }
          saveFile(fileName, mpart.getInputStream());
        }
        else if (mpart.isMimeType("multipart/*"))
        {
          saveAttachMent(mpart);
        }
        else
        {
          fileName = mpart.getFileName();
          if ((fileName != null) && (fileName.toLowerCase().indexOf("GB2312") != -1))
          {
            fileName = MimeUtility.decodeText(fileName);
            saveFile(fileName, mpart.getInputStream());
          }
        }
      }
    }
    else if (part.isMimeType("message/rfc822"))
    {
      saveAttachMent((Part)part.getContent());
    }
  }
  
  public void setAttachPath(String attachpath)
  {
    this.saveAttachPath = attachpath;
  }
  
  public void setDateFormat(String format)
    throws Exception
  {
    this.dateformat = format;
  }
  
  public String getAttachPath()
  {
    return this.saveAttachPath;
  }
  
  /* Error */
  private void saveFile(String fileName, java.io.InputStream in)
    throws Exception
  {
    // Byte code:
    //   0: ldc_w 344
    //   3: invokestatic 346	java/lang/System:getProperty	(Ljava/lang/String;)Ljava/lang/String;
    //   6: astore_3
    //   7: aload_0
    //   8: invokevirtual 349	com/hisoft/mail/ReceiveEmail:getAttachPath	()Ljava/lang/String;
    //   11: astore 4
    //   13: ldc 21
    //   15: astore 5
    //   17: aload_3
    //   18: ifnonnull +6 -> 24
    //   21: ldc 21
    //   23: astore_3
    //   24: aload_3
    //   25: invokevirtual 310	java/lang/String:toLowerCase	()Ljava/lang/String;
    //   28: ldc_w 351
    //   31: invokevirtual 184	java/lang/String:indexOf	(Ljava/lang/String;)I
    //   34: iconst_m1
    //   35: if_icmpeq +31 -> 66
    //   38: ldc_w 353
    //   41: astore 5
    //   43: aload 4
    //   45: ifnull +13 -> 58
    //   48: aload 4
    //   50: ldc 21
    //   52: invokevirtual 98	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   55: ifeq +21 -> 76
    //   58: ldc_w 355
    //   61: astore 4
    //   63: goto +13 -> 76
    //   66: ldc_w 357
    //   69: astore 5
    //   71: ldc_w 359
    //   74: astore 4
    //   76: new 361	java/io/File
    //   79: dup
    //   80: new 46	java/lang/StringBuilder
    //   83: dup
    //   84: aload 4
    //   86: invokestatic 51	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   89: invokespecial 57	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   92: aload 5
    //   94: invokevirtual 62	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   97: aload_1
    //   98: invokevirtual 62	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   101: invokevirtual 69	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   104: invokespecial 363	java/io/File:<init>	(Ljava/lang/String;)V
    //   107: astore 6
    //   109: getstatic 263	java/lang/System:out	Ljava/io/PrintStream;
    //   112: new 46	java/lang/StringBuilder
    //   115: dup
    //   116: ldc_w 364
    //   119: invokespecial 57	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   122: aload 6
    //   124: invokevirtual 366	java/io/File:toString	()Ljava/lang/String;
    //   127: invokevirtual 62	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   130: invokevirtual 69	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   133: invokevirtual 271	java/io/PrintStream:println	(Ljava/lang/String;)V
    //   136: aconst_null
    //   137: astore 7
    //   139: aconst_null
    //   140: astore 8
    //   142: new 367	java/io/BufferedOutputStream
    //   145: dup
    //   146: new 369	java/io/FileOutputStream
    //   149: dup
    //   150: aload 6
    //   152: invokespecial 371	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   155: invokespecial 374	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   158: astore 7
    //   160: new 377	java/io/BufferedInputStream
    //   163: dup
    //   164: aload_2
    //   165: invokespecial 379	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   168: astore 8
    //   170: goto +15 -> 185
    //   173: aload 7
    //   175: iload 9
    //   177: invokevirtual 382	java/io/BufferedOutputStream:write	(I)V
    //   180: aload 7
    //   182: invokevirtual 386	java/io/BufferedOutputStream:flush	()V
    //   185: aload 8
    //   187: invokevirtual 389	java/io/BufferedInputStream:read	()I
    //   190: dup
    //   191: istore 9
    //   193: iconst_m1
    //   194: if_icmpne -21 -> 173
    //   197: goto +36 -> 233
    //   200: astore 9
    //   202: aload 9
    //   204: invokevirtual 392	java/lang/Exception:printStackTrace	()V
    //   207: new 44	java/lang/Exception
    //   210: dup
    //   211: ldc_w 395
    //   214: invokespecial 130	java/lang/Exception:<init>	(Ljava/lang/String;)V
    //   217: athrow
    //   218: astore 10
    //   220: aload 7
    //   222: invokevirtual 397	java/io/BufferedOutputStream:close	()V
    //   225: aload 8
    //   227: invokevirtual 400	java/io/BufferedInputStream:close	()V
    //   230: aload 10
    //   232: athrow
    //   233: aload 7
    //   235: invokevirtual 397	java/io/BufferedOutputStream:close	()V
    //   238: aload 8
    //   240: invokevirtual 400	java/io/BufferedInputStream:close	()V
    //   243: return
    // Line number table:
    //   Java source line #311	-> byte code offset #0
    //   Java source line #312	-> byte code offset #7
    //   Java source line #313	-> byte code offset #13
    //   Java source line #314	-> byte code offset #17
    //   Java source line #315	-> byte code offset #21
    //   Java source line #316	-> byte code offset #24
    //   Java source line #317	-> byte code offset #38
    //   Java source line #318	-> byte code offset #43
    //   Java source line #319	-> byte code offset #58
    //   Java source line #321	-> byte code offset #66
    //   Java source line #322	-> byte code offset #71
    //   Java source line #324	-> byte code offset #76
    //   Java source line #325	-> byte code offset #109
    //   Java source line #329	-> byte code offset #136
    //   Java source line #330	-> byte code offset #139
    //   Java source line #332	-> byte code offset #142
    //   Java source line #333	-> byte code offset #160
    //   Java source line #335	-> byte code offset #170
    //   Java source line #336	-> byte code offset #173
    //   Java source line #337	-> byte code offset #180
    //   Java source line #335	-> byte code offset #185
    //   Java source line #339	-> byte code offset #200
    //   Java source line #340	-> byte code offset #202
    //   Java source line #341	-> byte code offset #207
    //   Java source line #342	-> byte code offset #218
    //   Java source line #343	-> byte code offset #220
    //   Java source line #344	-> byte code offset #225
    //   Java source line #345	-> byte code offset #230
    //   Java source line #343	-> byte code offset #233
    //   Java source line #344	-> byte code offset #238
    //   Java source line #346	-> byte code offset #243
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	244	0	this	ReceiveEmail
    //   0	244	1	fileName	String
    //   0	244	2	in	java.io.InputStream
    //   6	19	3	osName	String
    //   11	74	4	storedir	String
    //   15	78	5	separator	String
    //   107	44	6	storefile	java.io.File
    //   137	97	7	bos	java.io.BufferedOutputStream
    //   140	99	8	bis	java.io.BufferedInputStream
    //   173	3	9	c	int
    //   191	3	9	c	int
    //   200	3	9	exception	Exception
    //   218	13	10	localObject	Object
    // Exception table:
    //   from	to	target	type
    //   142	197	200	java/lang/Exception
    //   142	218	218	finally
  }
  
  public static void main(String[] args)
    throws Exception
  {
    String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    Properties props = System.getProperties();
    props.setProperty("mail.pop3.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    props.setProperty("mail.pop3.socketFactory.fallback", "false");
    props.setProperty("mail.pop3.port", "995");
    props.setProperty("mail.pop3.socketFactory.port", "995");
    
    String host = "pop.gmail.com";
    
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.auth", "true");
    Session session = Session.getDefaultInstance(props, null);
    URLName urln = new URLName("pop3", "pop.gmail.com", 995, null, "default.twqa", "dlink123");
    Store store = session.getStore(urln.getProtocol());
    store.connect(host, "default.twqa", "dlink123");
    Folder folder = store.getFolder("INBOX");
    folder.open(1);
    Message[] message = folder.getMessages();
    System.out.println("Messages's length: " + message.length);
    ReceiveEmail pmm = null;
    for (int i = 0; i < message.length; i++)
    {
      System.out.println("======================");
      pmm = new ReceiveEmail((MimeMessage)message[i]);
      System.out.println("Message " + i + " subject: " + pmm.getSubject());
      System.out.println("Message " + i + " sentdate: " + pmm.getSentDate());
      System.out.println("Message " + i + " replysign: " + pmm.getReplySign());
      System.out.println("Message " + i + " hasRead: " + pmm.isNew());
      System.out.println("Message " + i + "  containAttachment: " + pmm.isContainAttach(message[i]));
      System.out.println("Message " + i + " form: " + pmm.getFrom());
      System.out.println("Message " + i + " to: " + pmm.getMailAddress("to"));
      System.out.println("Message " + i + " cc: " + pmm.getMailAddress("cc"));
      System.out.println("Message " + i + " bcc: " + pmm.getMailAddress("bcc"));
      pmm.setDateFormat("yyyy年MM月dd日 HH:mm");
      System.out.println("Message " + i + " sentdate: " + pmm.getSentDate());
      System.out.println("Message " + i + " Message-ID: " + pmm.getMessageId());
      
      pmm.getMailContent(message[i]);
      System.out.println("Message " + i + " bodycontent: \r\n" + pmm.getBodyText());
    }
    store.close();
  }
}
