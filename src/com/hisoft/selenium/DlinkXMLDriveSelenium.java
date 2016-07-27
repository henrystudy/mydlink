package com.hisoft.selenium;

import com.hisoft.entity.Environment;
import com.hisoft.entity.MailEntity;
import com.hisoft.entity.SheduleEntity;
import com.hisoft.entity.VariableMap;
import com.hisoft.mail.ReceiveEmailHelper;
import com.hisoft.type.ResultEnum;
import com.hisoft.utility.CommonTool;
import com.hisoft.utility.DataProviderTool;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import org.apache.log4j.Logger;

public class DlinkXMLDriveSelenium
  extends XmlDriveSelenium
{
  private static DlinkXMLDriveSelenium dlinkXMLDriveSelenium = null;
  
  public static DlinkXMLDriveSelenium getInstance()
  {
    if (dlinkXMLDriveSelenium == null) {
      dlinkXMLDriveSelenium = new DlinkXMLDriveSelenium();
    }
    return dlinkXMLDriveSelenium;
  }
  
  public String setLanguage()
  {
    if (this.environment.getOs() == null) {
      this.environment.setOs(getEval("(function(){var ua=navigator.userAgent;var system;if(ua.indexOf('Windows NT 5.1')>-1){system='Windows XP';}else if(ua.indexOf('Windows NT 6.0')>-1){system='Windows Vista';}else if(ua.indexOf('Windows NT 6.1')>-1){system='Windows 7';}else if(ua.indexOf('Ubuntu')>-1){system='Ubuntu';}else if(ua.indexOf('Mac')>-1){system='Mac';}else{system='Others';}return system;})()"));
    }
    if (this.environment.getBrowser().contains("*")) {
      this.environment.setBrowser(getEval("(function(){var Sys={};var ua=navigator.userAgent.toLowerCase();var s;var broswer;(s=ua.match(/msie ([\\d.]+)/))?Sys.ie=s[1]:(s=ua.match(/firefox\\/([\\d.]+)/))?Sys.firefox=s[1]:(s=ua.match(/chrome\\/([\\d.]+)/))?Sys.chrome=s[1]:(s=ua.match(/opera.([\\d.]+)/))?Sys.opera=s[1]:(s=ua.match(/version\\/([\\d.]+).*safari/))?Sys.safari=s[1]:0;if(Sys.ie){broswer='IE'}else if(Sys.firefox){broswer='Firefox: '+Sys.firefox}else if(Sys.chrome){broswer='GoogleChrome: '+Sys.chrome}else if(Sys.opera){broswer='Opera: '+Sys.opera}else if(Sys.safari){broswer='Safari: '+Sys.safari}else{broswer='unknow'}return broswer;})()"));
    }
    open("/signout");
    return open("/entrance?lang=" + this.environment.getLanguage());
  }
  
  public String getConnectionType(String time)
  {
    String thisConnType = "R.";
    for (int looptimes = 0; (looptimes < this.environment.getRetryTimes()) && (thisConnType.contains("R.")); looptimes++)
    {
      thisConnType = getConnectionType(CommonTool.String2Int(time));
      if ((looptimes + 1 < this.environment.getRetryTimes()) && (thisConnType.contains("R.")))
      {
        runScriptV2("loadIndex();");
        this.logger.info("waitForLiveView: retry");
      }
    }
    return thisConnType;
  }
  
  public String getAllConnectionType(String time)
  {
    String returnStr = "";
    String[] sns = getAllDeviceSNs().split(",");
    if (sns.length < 1) {
      return "have no device.";
    }
    for (int i = 0; i < sns.length; i++)
    {
      selectDevice(sns[i]);
      returnStr = returnStr + sns[i] + ": " + getConnectionType(time) + "<br/>";
    }
    return returnStr;
  }
  
  protected String getAllDeviceSNs()
  {
    int numb = CommonTool.String2Int(getEval("window.jQuery('tr[id]').length"));
    String returnStr = "";
    String temp = "device_list_";
    for (int i = 0; i < numb; i++)
    {
      temp = getEval("window.jQuery('tr[id]')[" + i + "].id");
      if (temp.contains("device_list_")) {
        returnStr = returnStr + temp.replace("device_list_", "") + ",";
      }
    }
    if (returnStr.endsWith(",")) {
      returnStr = returnStr.substring(0, returnStr.length() - 1);
    }
    return returnStr;
  }
  
  protected String getConnectionType(int timer)
  {
    sleep(5000);
    String info = "";
    SheduleEntity entity = new SheduleEntity(timer);
    iAmShedule(entity);
    int tempTime = 3000;
    while (!entity.isTimeout())
    {
      sleep(tempTime);
      info = getEval("window.jQuery('#mydlink\\\\:debugWin_log_msg').val();");
      if (ConnectionType(info) != null)
      {
        this.executor.shutdownNow();
        return ConnectionType(info);
      }
    }
    return "R. wait for live view " + this.environment.getRetryTimes() + " time(s) timeout! ";
  }
  
  protected String ConnectionType(String info)
  {
    if (isVisibleV2("//div[@id='div_fwUpgrade']"))
    {
      click("//div[@id='div_fwUpgrade']/div[@class='btnw floatl']");
    }
    else
    {
      if (isVisibleV2("//div[@id='win_ConnError']")) {
        return "R. Unable to access your camera's Live Video.";
      }
      if (isVisibleV2("//div[@id='win_content']/div/h3")) {
        return "E. Your camera is offline.";
      }
    }
    info = info.split("newLine")[(info.split("newLine").length - 1)];
    if (info.contains("$mDconnector:connected"))
    {
      if (info.contains("$mDconnector:connected[local]")) {
        return "Direct";
      }
      if (info.contains("$mDconnector:connected[upnp]")) {
        return "Port";
      }
      if (info.contains("$mDconnector:connected[stunt]")) {
        return "Relay";
      }
      if (info.contains("$mDconnector:connected[relay]")) {
        return "Relay";
      }
      return null;
    }
    return null;
  }
  
  public String selectDevice(String SNValue)
  {
    getEvalV2("window.jQuery('#mydlink\\\\:debugWin_log_msg').append('newLine');");
    
    open("/device#" + SNValue);
    return ResultEnum.PASS;
  }
  
  public String waitForLiveView(String time)
  {
    String thisConnType = "R.";
    for (int looptimes = 0; (looptimes < this.environment.getRetryTimes()) && (thisConnType.contains("R.")); looptimes++)
    {
      thisConnType = getConnectionType(CommonTool.String2Int(time));
      if ((looptimes + 1 < this.environment.getRetryTimes()) && (thisConnType.contains("R.")))
      {
        runScriptV2("loadIndex();");
        this.logger.info("waitForLiveView: retry");
      }
    }
    if ((thisConnType.contains("R.")) || (thisConnType.contains("E."))) {
      return ResultEnum.PASS;
    }
    return ResultEnum.PASS + ": " + thisConnType;
  }
  
  public String getEmailInfo(String accountName)
    throws NoSuchProviderException, MessagingException
  {
    String subject = DataProviderTool.getData("email(" + accountName + ").subject");
    MailEntity f = ReceiveEmailHelper.getFirstMailBySubject(DataProviderTool.getData("account(" + accountName + ").email"), DataProviderTool.getData("account(" + accountName + ").emailPwd"), true, subject, true);
    if (f == null) {
      return ResultEnum.FAIL + ": email with the subject '" + subject + "' is null.";
    }
    VariableMap map = VariableMap.getInstance();
    map.mapPutKV("contentText", f.getContentText());
    map.mapPutKV("link", f.getFirstLinksContent());
    map.mapPutKV("mailFromPersonal", f.getFrom().split(",")[0]);
    map.mapPutKV("mailFrom", f.getFrom().split(",")[1]);
    map.mapPutKV("from", f.getFrom());
    map.mapPutKV("mailTo", f.getMailTo());
    map.mapPutKV("subject", f.getSubject());
    map.mapPutKV("sendDate", f.getSentDate());
    map.mapPutKV("fromPersonal", f.getFromPersonal());
    map.mapPutKV("newPassword", f.getRedContent());
    return ResultEnum.PASS;
  }
  
  public String openDevicePage(String sn, String tag)
  {
    if (tag.toLowerCase().contains("settings")) {
      openWindow(this.environment.getBaseUrl().replace("http://", new StringBuilder("http://").append(sn).append(".").toString()) + "/?settings");
    } else {
      openWindow(this.environment.getBaseUrl().replace("http://", new StringBuilder("http://").append(sn).append(".").toString()) + "/?maintenance");
    }
    return ResultEnum.PASS;
  }
}
