package com.hisoft.entity;

import com.hisoft.exception.XpathNotFoundException;
import com.hisoft.type.DeviceState;
import com.hisoft.type.XMLType;
import com.hisoft.utility.CommonTool;
import com.hisoft.xml.Dom4JXMLHelper;
import com.hisoft.xml.SchemaChecker;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class Environment
{
  Logger logger = Logger.getLogger(Environment.class);
  private static String CONFIGFILEPATH = "config.xml";
  private static final String CONFIGXPATH = "/configs/";
  private static final String ACCOUNTBASEXPATH = "/info/accounts/account";
  private static final String TESTCASEPATH = "testCasePath";
  private String host;
  private int port;
  private String baseUrl;
  private String browser;
  private String language;
  private String timeout;
  private String os = null;
  private List<File> testCaseList;
  private String testCasePath;
  private String pageDataPath;
  private String accountFilePath;
  private String setemailInfoFilePath;
  private int retryTimes;
  private Map<String, AccountEntity> accountMap;
  
  public String getEmailInfoFilePath()
  {
    return this.setemailInfoFilePath;
  }
  
  public void setEmailInfoFilePath(String emailInfoFilePath)
  {
    this.setemailInfoFilePath = emailInfoFilePath;
  }
  
  private static Environment environment = null;
  
  private Environment()
  {
    this.logger.info("initializing Environment ... ");
    SAXReader saxReader = new SAXReader();
    if (SchemaChecker.validateXml(CONFIGFILEPATH, XMLType.CONFIG)) {
      try
      {
        Document document = saxReader.read(new File(CONFIGFILEPATH));
        
        setHost(document.selectSingleNode("/configs/host").getText());
        
        setPort(Integer.parseInt(document.selectSingleNode("/configs/port").getText()));
        
        setBaseUrl(document.selectSingleNode("/configs/baseUrl").getText());
        
        setBrowser(document.selectSingleNode("/configs/browser").getText());
        
        setLanguage(document.selectSingleNode("/configs/language").getText());
        
        setTimeout(document.selectSingleNode("/configs/timeout").getText());
        
        setRetryTimes(Integer.parseInt(document.selectSingleNode("/configs/retryTimes").getText()));
        
        setPageDataPath(document.selectSingleNode("/configs/pageDataPath").getText());
        
        setAccountFilePath(document.selectSingleNode("/configs/accountFile").getText());
        setEmailInfoFilePath(document.selectSingleNode("/configs/emailInfoFile").getText());
        
        setAccountMap(ReadAccountMap(this.accountFilePath));
        
        setTestCaseList(getTestCaseList(document));
        Node node = document.selectSingleNode("/configs/testCasePath");
        
        setTestCasePath(node.valueOf("@name"));
      }
      catch (DocumentException ex)
      {
        this.logger.error(ex.getMessage(), ex);
      }
      catch (XpathNotFoundException e)
      {
        this.logger.error(e.getMessage(), e);
      }
    } else {
      this.logger.error("There some unknow error in file'" + CONFIGFILEPATH + "'");
    }
    this.logger.info("initializing Environment ... OK");
  }
  
  public static Environment getInstance()
  {
    if (environment == null) {
      environment = new Environment();
    }
    return environment;
  }
  
  private List<File> getTestCaseList(Document document)
    throws XpathNotFoundException
  {
    List<File> allList = new ArrayList();
    String baseXpath = "/configs/testCasePath";
    List<?> objList = document.selectNodes(baseXpath);
    for (Object obj : objList) {
      allList.addAll(getTestCaseListForOneNode((Node)obj, baseXpath));
    }
    return allList;
  }
  
  private List<File> getTestCaseListForOneNode(Node node, String baseXpath)
    throws XpathNotFoundException
  {
    String type = node.valueOf("@type");
    String path = node.valueOf("@name");
    List<File> lists = new ArrayList();
    List<File> tempList;
    switch (Environment.TestCasePathType.valueOf(type))
    {
    case exclude: 
      lists = CommonTool.readXmlFileList(path);
      break;
    case include: 
      String excludeXpath = baseXpath + "/exclude";
      lists = CommonTool.readXmlFileList(path);
      List<File> excludeFileList = new ArrayList();
      List<String> excludeTextList = Dom4JXMLHelper.getTextList(excludeXpath, CONFIGFILEPATH);
      for (String aPath : excludeTextList)
      {
        File file = new File(aPath);
        if (!file.exists()) {
          throw new RuntimeException("The path: " + aPath + " in the config file is not exist!");
        }
        if (file.isDirectory())
        {
          tempList = CommonTool.readXmlFileList(aPath);
          for (File tempFile : tempList) {
            excludeFileList.add(tempFile);
          }
        }
        else
        {
          excludeFileList.add(file);
        }
      }
      for (File excludeFile : excludeFileList) {
        for (File file : lists) {
          if (excludeFile.getPath().equals(file.getPath()))
          {
            lists.remove(file);
            break;
          }
        }
      }
      break;
    case file: 
      String includeXpath = baseXpath + "/include";
      Object includeList = Dom4JXMLHelper.readNodeList(includeXpath, CONFIGFILEPATH);
      for (Object obj : (List)includeList)
      {
        String aPath = ((Node)obj).getText();
        File file = new File(aPath);
        if (!file.exists()) {
          throw new RuntimeException("The path: " + aPath + " in the config file is not exist!");
        }
        if (file.isDirectory())
        {
          List<File> tempList = CommonTool.readXmlFileList(aPath);
          for (File tempFile : tempList) {
            lists.add(tempFile);
          }
        }
        else
        {
          lists.add(file);
        }
      }
      break;
    default: 
      throw new RuntimeException();
    }
    return lists;
  }
  
  public Map<String, AccountEntity> ReadAccountMap(String filePath)
    throws XpathNotFoundException, DocumentException
  {
    List<?> accountList = Dom4JXMLHelper.readNodeList("/info/accounts/account", filePath);
    int accountCount = accountList.size();
    Map<String, AccountEntity> accountMap = new HashMap();
    for (int i = 0; i < accountCount; i++)
    {
      String accountType = ((Node)accountList.get(i)).valueOf("@type");
      accountMap.put(accountType, ReadAccount(filePath, accountType));
    }
    return accountMap;
  }
  
  public AccountEntity ReadAccount(String filePath, String accountType)
    throws XpathNotFoundException
  {
    String xPath = "/info/accounts/account[@type='" + accountType + "']";
    
    String email = Dom4JXMLHelper.readSingleNode(xPath + "/email", filePath).getText();
    String passwd = Dom4JXMLHelper.readSingleNode(xPath + "/password", filePath).getText();
    String emailPasswd = Dom4JXMLHelper.readSingleNode(xPath + "/emailPassword", filePath).getText();
    String firstName = Dom4JXMLHelper.readSingleNode(xPath + "/firstName", filePath).getText();
    String lastName = Dom4JXMLHelper.readSingleNode(xPath + "/lastName", filePath).getText();
    
    List<DeviceEntity> devices = new ArrayList();
    int deviceCount = 0;
    try
    {
      deviceCount = Dom4JXMLHelper.readNodeList(xPath + "/devices/device", filePath).size();
    }
    catch (XpathNotFoundException localXpathNotFoundException) {}
    for (int i = 0; i < deviceCount; i++)
    {
      DeviceEntity device = new DeviceEntity();
      device.setDeviceName(((Node)Dom4JXMLHelper.readNodeList(xPath + "/devices/device/DeviceName", filePath).get(i)).getText());
      device.setModelName(((Node)Dom4JXMLHelper.readNodeList(xPath + "/devices/device/ModelName", filePath).get(i)).getText());
      device.setConnectType(((Node)Dom4JXMLHelper.readNodeList(xPath + "/devices/device/ConnectionType", filePath).get(i)).getText());
      device.setSn(((Node)Dom4JXMLHelper.readNodeList(xPath + "/devices/device/SN", filePath).get(i)).getText());
      device.setMac(((Node)Dom4JXMLHelper.readNodeList(xPath + "/devices/device/Mac", filePath).get(i)).getText());
      device.setUserName(((Node)Dom4JXMLHelper.readNodeList(xPath + "/devices/device/UserName", filePath).get(i)).getText());
      device.setPassWord(((Node)Dom4JXMLHelper.readNodeList(xPath + "/devices/device/Passwd", filePath).get(i)).getText());
      String state = ((Node)Dom4JXMLHelper.readNodeList(xPath + "/devices/device/state", filePath).get(i)).getText();
      device.setState(DeviceState.valueOf(state));
      device.setFw(((Node)Dom4JXMLHelper.readNodeList(xPath + "/devices/device/fw", filePath).get(i)).getText());
      devices.add(device);
    }
    return new AccountEntity(email, passwd, emailPasswd, firstName, lastName, devices);
  }
  
  public String getHost()
  {
    return this.host;
  }
  
  public void setHost(String host)
  {
    this.host = host;
    VariableMap.getInstance().mapPutKV("host", host);
  }
  
  public int getPort()
  {
    return this.port;
  }
  
  public void setPort(int port)
  {
    this.port = port;
    VariableMap.getInstance().mapPutKV("port", Integer.valueOf(port));
  }
  
  public String getBaseUrl()
  {
    return this.baseUrl;
  }
  
  public void setBaseUrl(String baseUrl)
  {
    this.baseUrl = baseUrl;
    VariableMap.getInstance().mapPutKV("baseUrl", baseUrl);
  }
  
  public String getBrowser()
  {
    return this.browser;
  }
  
  public void setBrowser(String browser)
  {
    this.browser = browser;
    VariableMap.getInstance().mapPutKV("browser", browser);
  }
  
  public String getLanguage()
  {
    return this.language;
  }
  
  public void setLanguage(String language)
  {
    this.language = language;
    VariableMap.getInstance().mapPutKV("language", language);
  }
  
  public String getTimeout()
  {
    return this.timeout;
  }
  
  public void setTimeout(String timeout)
  {
    this.timeout = timeout;
    VariableMap.getInstance().mapPutKV("timeout", timeout);
  }
  
  public String getOs()
  {
    return this.os;
  }
  
  public void setOs(String os)
  {
    this.os = os;
    VariableMap.getInstance().mapPutKV("os", os);
  }
  
  public List<File> getTestCaseList()
  {
    return this.testCaseList;
  }
  
  public void setTestCaseList(List<File> testCaseList)
  {
    this.testCaseList = testCaseList;
  }
  
  public String getTestCasePath()
  {
    return this.testCasePath;
  }
  
  public void setTestCasePath(String testCasePath)
  {
    this.testCasePath = testCasePath;
    VariableMap.getInstance().mapPutKV("testCasePath", testCasePath);
  }
  
  public String getPageDataPath()
  {
    return this.pageDataPath;
  }
  
  public void setPageDataPath(String pageDataPath)
  {
    this.pageDataPath = pageDataPath;
    VariableMap.getInstance().mapPutKV("pageDataPath", pageDataPath);
  }
  
  public String getAccountFilePath()
  {
    return this.accountFilePath;
  }
  
  public void setAccountFilePath(String accountFilePath)
  {
    this.accountFilePath = accountFilePath;
    VariableMap.getInstance().mapPutKV("accountFilePath", accountFilePath);
  }
  
  public int getRetryTimes()
  {
    return this.retryTimes;
  }
  
  public void setRetryTimes(int retryTimes)
  {
    this.retryTimes = retryTimes;
    VariableMap.getInstance().mapPutKV("retryTimes", Integer.valueOf(retryTimes));
  }
  
  public Map<String, AccountEntity> getAccountMap()
  {
    return this.accountMap;
  }
  
  public void setAccountMap(Map<String, AccountEntity> accountMap)
  {
    this.accountMap = accountMap;
  }
  
  public static void setCONFIGFILEPATH(String cONFIGFILEPATH)
  {
    CONFIGFILEPATH = cONFIGFILEPATH;
  }
}
