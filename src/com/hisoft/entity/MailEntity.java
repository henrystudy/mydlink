package com.hisoft.entity;

import java.util.ArrayList;
import java.util.List;

public class MailEntity
{
  private String from;
  private String fromPersonal;
  private String fromAddress;
  private String subject;
  private String content;
  private String contentText;
  private String SentDate;
  private String mailTo;
  private boolean hasRead;
  
  public String getMailTo()
  {
    return this.mailTo;
  }
  
  public void setMailTo(String mailTo)
  {
    this.mailTo = mailTo;
  }
  
  public String getFrom()
  {
    return this.from;
  }
  
  public void setFrom(String from)
  {
    this.from = from;
  }
  
  public boolean isHasRead()
  {
    return this.hasRead;
  }
  
  public void setHasRead(boolean hasRead)
  {
    this.hasRead = hasRead;
  }
  
  public String getSentDate()
  {
    return this.SentDate;
  }
  
  public void setSentDate(String sentDate)
  {
    this.SentDate = sentDate;
  }
  
  public String getFromPersonal()
  {
    return this.fromPersonal;
  }
  
  public void setFromPersonal(String fromPersonal)
  {
    this.fromPersonal = fromPersonal;
  }
  
  public String getFromAddress()
  {
    return this.fromAddress;
  }
  
  public void setFromAddress(String fromAddress)
  {
    this.fromAddress = fromAddress;
  }
  
  public String getSubject()
  {
    return this.subject;
  }
  
  public void setSubject(String subject)
  {
    this.subject = subject;
  }
  
  public String getContent()
  {
    return this.content;
  }
  
  public void setContent(String content)
  {
    this.content = content;
  }
  
  public void setContentText(String content)
  {
    this.contentText = content;
  }
  
  public String getContentText()
  {
    if ((this.contentText == null) || (this.contentText.isEmpty()))
    {
      List<String> tempList = new ArrayList();
      String contentText = getContent();
      int endPlace = 0;
      int startPlace = 0;
      for (;;)
      {
        startPlace = getContent().indexOf("<", endPlace);
        endPlace = getContent().indexOf(">", endPlace) + 1;
        if ((startPlace == -1) || (endPlace == -1)) {
          break;
        }
        tempList.add(new String(getContent().substring(startPlace, endPlace)));
      }
      for (String i : tempList) {
        contentText = contentText.replace(i, "");
      }
      contentText = contentText.replaceAll("\r\n", "\n");
      contentText = contentText.replaceAll("\r \n", "\n");
      return contentText;
    }
    return this.contentText;
  }
  
  public String getFirstLinksContent()
  {
    String URL = null;
    int temp;
    URL = this.content.substring(temp = this.content.toLowerCase().indexOf("<a href=") + 9, this.content.toLowerCase().indexOf("\">", temp));
    if (URL.contains("http")) {
      return URL;
    }
    return "";
  }
  
  public String getRedContent()
  {
    String redString = null;
    String tempFlag = "<font color=\"red\">";
    int temp = this.content.toLowerCase().indexOf(tempFlag) + tempFlag.length();
    if (temp == 17) {
      return "";
    }
    redString = this.content.substring(temp, this.content.toLowerCase().indexOf("</font>", temp));
    return redString;
  }
}
