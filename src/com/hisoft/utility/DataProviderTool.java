package com.hisoft.utility;

import com.hisoft.constant.Xpath;
import com.hisoft.entity.AccountEntity;
import com.hisoft.entity.DeviceEntity;
import com.hisoft.entity.Environment;
import com.hisoft.entity.VariableMap;
import com.hisoft.exception.XpathNotFoundException;
import com.hisoft.xml.Dom4JXMLHelper;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.dom4j.Node;

public class DataProviderTool
{
  static Logger logger = Logger.getLogger(DataProviderTool.class);
  
  public static String getData(String str)
  {
    String returnStr = "";
    if (str.contains("xml("))
    {
      if (str.contains(").xpath.")) {
        returnStr = xpath(getName(str, ").xpath."), getXMLPath(str));
      } else if (str.contains(").message.")) {
        returnStr = message(getName(str, ").message."), getXMLPath(str));
      } else if (str.contains(").js.")) {
        returnStr = js(getName(str, ").js."), getXMLPath(str));
      } else if (str.contains(").image.")) {
        returnStr = image(getName(str, ").image."), getXMLPath(str));
      } else if (str.contains(").color.")) {
        returnStr = color(getName(str, ").color."), getXMLPath(str));
      } else if (str.contains(").pagePath.")) {
        returnStr = pagePath(getName(str, ").pagePath."), getXMLPath(str));
      } else if (str.contains(").font.")) {
        returnStr = font(getName(str, ").font."), getXMLPath(str));
      } else {
        returnStr = str;
      }
    }
    else if (str.contains("xpath(")) {
      returnStr = xpath(str.split("\\.")[1], getXMLPath(str));
    } else if (str.contains("url(")) {
      returnStr = pagePath(str.split("\\.")[1], getXMLPath(str));
    } else if (str.contains("js(")) {
      returnStr = js(str.split("\\.")[1], getXMLPath(str));
    } else if (str.contains("message(")) {
      returnStr = message(str.split("\\.")[1], getXMLPath(str));
    } else if (str.contains("image(")) {
      returnStr = image(str.split("\\.")[1], getXMLPath(str));
    } else if (str.contains("account(")) {
      returnStr = AccountInfo(str.substring(str.indexOf("account(") + "account(".length(), str.indexOf(").")), str);
    } else if (str.contains("email(")) {
      returnStr = getEmailInfo(str.substring(str.indexOf("email(") + "email(".length(), str.indexOf(").")), str);
    } else {
      returnStr = str;
    }
    return returnStr;
  }
  
  private static String getEmailInfo(String name, String str)
  {
    String returnStr = "";
    String xpath = "/info/emailEntitys/emailEntity[@name='${&}']".replace("${&}", name) + "/";
    String xpath2 = str.split("\\.")[1];
    String language = "message[@name='" + xpath2 + "']/content[@language='" + Environment.getInstance().getLanguage() + "']";
    try
    {
      returnStr = Dom4JXMLHelper.readSingleNode(xpath + language, Environment.getInstance().getEmailInfoFilePath()).getText();
    }
    catch (XpathNotFoundException e)
    {
      returnStr = Dom4JXMLHelper.readSingleNode(xpath + xpath2, Environment.getInstance().getEmailInfoFilePath()).getText();
    }
    returnStr = returnStr.replace("#$firstName$#", getData("account(" + name + ").firstName")).replace("#$DNSName$#", Environment.getInstance().getBaseUrl().split("/")[2]);
    return returnStr;
  }
  
  public static String getProperty(String str)
  {
    String returnStr = str;
    if (str == null) {
      returnStr = "";
    } else if (str.contains("property(")) {
      returnStr = getValue(str.substring(str.indexOf("property(") + "property(".length(), str.indexOf(")")));
    } else if (str.contains("${")) {
      returnStr = getValue(str.substring(str.indexOf("${") + "${".length(), str.indexOf("}")));
    }
    return returnStr;
  }
  
  private static String getValue(String key)
  {
    return VariableMap.getInstance().mapGetValue(key);
  }
  
  private static String font(String name, String xmlPath)
  {
    String xpath = "/info/fonts/font[@name='${&}']".replace("${&}", name);
    if (Environment.getInstance().getBrowser().toLowerCase().contains("ie")) {
      xpath = xpath + "/ie";
    } else {
      xpath = xpath + "/notIe";
    }
    return Dom4JXMLHelper.readSingleNode(xpath, xmlPath).getText();
  }
  
  private static String color(String name, String xmlPath)
  {
    String xpath = "/info/colors/color[@name='${&}']".replace("${&}", name);
    if (Environment.getInstance().getBrowser().toLowerCase().contains("ie")) {
      xpath = xpath + "/ie";
    } else {
      xpath = xpath + "/notIe";
    }
    return Dom4JXMLHelper.readSingleNode(xpath, xmlPath).getText();
  }
  
  private static String getName(String str, String afterString)
  {
    return str.substring(str.indexOf(afterString) + afterString.length());
  }
  
  private static String getXMLPath(String str)
  {
    return Environment.getInstance().getPageDataPath() + (Environment.getInstance().getPageDataPath().endsWith("/") ? "" : "/") + str.substring(str.indexOf("(") + "(".length(), str.indexOf(")")) + ".xml";
  }
  
  private static String xpath(String name, String xmlpath)
  {
    return Dom4JXMLHelper.readSingleNode("/info/targets/xpath[@name='${&}']".replace("${&}", name), xmlpath).getText();
  }
  
  private static String pagePath(String name, String xmlpath)
  {
    return Dom4JXMLHelper.readSingleNode("/info/targets/pagePath[@name='${&}']".replace("${&}", name), xmlpath).getText();
  }
  
  private static String js(String name, String xmlpath)
  {
    return Dom4JXMLHelper.readSingleNode("/info/targets/js[@name='${&}']".replace("${&}", name), xmlpath).getText();
  }
  
  private static String message(String name, String xmlpath)
  {
    return Dom4JXMLHelper.readSingleNode(Xpath.MESSAGE_XPATH.replace("${&}", name), xmlpath).getText();
  }
  
  private static String image(String name, String xmlpath)
  {
    return Dom4JXMLHelper.readSingleNode("/info/images/image[@name='${&}']".replace("${&}", name), xmlpath).getText();
  }
  
  public static String AccountInfo(String name, String str)
  {
    AccountEntity account = (AccountEntity)Environment.getInstance().getAccountMap().get(name);
    if (account == null) {
      throw new XpathNotFoundException("account named: " + name, "");
    }
    String type = str.split("\\.")[1];
    switch (com.hisoft.type.AccountType.valueOf(type))
    {
    case changedLink: 
      return account.getEmail();
    case device: 
      return account.getPassword();
    case deviceList: 
      return account.getEmailPassword();
    case email: 
      return account.getEmailLink();
    case expiredLink: 
      return account.getFirstName();
    case firstName: 
      return account.getLastName();
    case pwd: 
      String[] strings = str.split("\\.");
      int deviceNo = Integer.parseInt(strings[2]) - 1;
      try
      {
        device = (DeviceEntity)account.getDeviceList().get(deviceNo);
      }
      catch (IndexOutOfBoundsException e)
      {
        DeviceEntity device;
        throw new XpathNotFoundException("Index " + deviceNo + " device in account named: " + name, "");
      }
      DeviceEntity device;
      if (strings.length == 4) {
        return getDeviceInfo(strings[3], device);
      }
      return "";
    }
    return null;
  }
  
  public static String getDeviceInfo(String infoName, DeviceEntity device)
  {
    switch (DataProviderTool.DeviceInfo.valueOf(infoName))
    {
    case mac: 
      return device.getConnectType();
    case connectionType: 
      return device.getDeviceName();
    case passwd: 
      return device.getMac();
    case deviceName: 
      return device.getModelName();
    case state: 
      return device.getPassWord();
    case modelName: 
      return device.getSn();
    case sn: 
      return device.getUserName();
    }
    return "";
  }
}
