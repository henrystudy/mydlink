package com.hisoft.selenium;

import com.hisoft.entity.Environment;
import com.hisoft.entity.SheduleEntity;
import com.hisoft.entity.VariableMap;
import com.hisoft.type.ResultEnum;
import com.hisoft.utility.Base64;
import com.hisoft.utility.CommonTool;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

public class XmlDriveSelenium
{
  protected ScheduledThreadPoolExecutor executor = null;
  Logger logger = Logger.getLogger(XmlDriveSelenium.class);
  public Selenium selenium = SeleniumX.getInstance();
  public Environment environment = Environment.getInstance();
  public String timeout = this.environment.getTimeout();
  protected int g_windowID = 2012;
  
  public String open(String url)
  {
    onlyOpen(url);
    waitForPageToLoad();
    return ResultEnum.PASS;
  }
  
  public String onlyOpen(String url)
  {
    this.selenium.open(url);
    return ResultEnum.PASS;
  }
  
  public String type(String operate, String value)
  {
    this.selenium.type(operate, value);
    return ResultEnum.PASS;
  }
  
  public String clickAndWait(String locator)
  {
    click(locator);
    waitForPageToLoad();
    return ResultEnum.PASS;
  }
  
  public String click(String locator)
  {
    if (this.environment.getBrowser().contains("safari"))
    {
      String script1 = getAttributeV2(locator + "/@onclick");
      String type = getAttributeV2(locator + "/@type");
      if ((!CommonTool.isEmpty(script1)) && (!type.equals("checkbox")))
      {
        runScriptV2(script1);
      }
      else
      {
        String script2 = getAttributeV2(locator + "/@id");
        if (!CommonTool.isEmpty(script2)) {
          runScriptV2("window.jQuery('#" + script2 + "').click()");
        }
      }
    }
    else
    {
      this.selenium.click(locator);
    }
    return ResultEnum.PASS;
  }
  
  public String waitForPageToLoad()
  {
    this.selenium.waitForPageToLoad(this.timeout);
    return ResultEnum.PASS;
  }
  
  public boolean isElementPresent(String locator)
  {
    return this.selenium.isElementPresent(locator);
  }
  
  public boolean isTextPresent(String pattern)
  {
    return this.selenium.isTextPresent(pattern);
  }
  
  public boolean isVisible(String locator)
  {
    return this.selenium.isVisible(locator);
  }
  
  public boolean isVisibleV2(String locator)
  {
    boolean flag = false;
    try
    {
      flag = this.selenium.isVisible(locator);
    }
    catch (SeleniumException e)
    {
      flag = false;
    }
    return flag;
  }
  
  public String check(String str)
  {
    this.selenium.check(str);
    return ResultEnum.PASS;
  }
  
  public String uncheck(String str)
  {
    this.selenium.uncheck(str);
    return ResultEnum.PASS;
  }
  
  public String windowFocus()
  {
    this.selenium.windowFocus();
    return ResultEnum.PASS;
  }
  
  public String runScript(String script)
  {
    this.selenium.runScript(script);
    return ResultEnum.PASS;
  }
  
  public String runScriptV2(String script)
  {
    try
    {
      runScript(script);
    }
    catch (SeleniumException localSeleniumException) {}
    return ResultEnum.PASS;
  }
  
  public String getEval(String script)
  {
    return this.selenium.getEval(script);
  }
  
  public String getEvalV2(String script)
  {
    String str = "";
    try
    {
      str = this.selenium.getEval(script);
    }
    catch (SeleniumException localSeleniumException) {}
    return str;
  }
  
  public String goBack()
  {
    this.selenium.goBack();
    waitForPageToLoad();
    return ResultEnum.PASS;
  }
  
  public String getValue(String xpath)
  {
    String returnString = "";
    if (xpath.contains("/@")) {
      returnString = getAttribute(xpath);
    } else if (xpath.contains("window.")) {
      returnString = CommonTool.threeToSixBit(getEval(xpath));
    } else if (isEditableV2(xpath)) {
      try
      {
        returnString = getValueT(xpath);
      }
      catch (SeleniumException e)
      {
        returnString = getTextV2(xpath);
      }
    } else {
      returnString = getTextV2(xpath);
    }
    return returnString;
  }
  
  public String getValueT(String xpath)
  {
    return this.selenium.getValue(xpath);
  }
  
  public boolean isEditable(String locator)
  {
    return this.selenium.isEditable(locator);
  }
  
  public boolean isEditableV2(String locator)
  {
    boolean flag = false;
    try
    {
      flag = this.selenium.isEditable(locator);
    }
    catch (SeleniumException e)
    {
      flag = false;
    }
    return flag;
  }
  
  public String refresh()
  {
    this.selenium.refresh();
    waitForPageToLoad();
    return ResultEnum.PASS;
  }
  
  public String sleep(int millis)
  {
    try
    {
      Thread.sleep(millis);
    }
    catch (InterruptedException e)
    {
      this.logger.error(e.getMessage(), e);
      return ResultEnum.FAIL;
    }
    return ResultEnum.PASS;
  }
  
  public boolean sleep(String millis)
  {
    sleep(CommonTool.String2Int(millis) * 1000);
    return true;
  }
  
  public String close()
  {
    this.selenium.close();
    return ResultEnum.PASS;
  }
  
  public String getLocation()
  {
    return this.selenium.getLocation();
  }
  
  public String getAttribute(String attributeLocator)
  {
    return this.selenium.getAttribute(attributeLocator);
  }
  
  public String attribute(String attributeLocator, String attribute)
  {
    if (CommonTool.isEmpty(attribute)) {
      return getAttribute(attributeLocator);
    }
    return getAttributeV2(attributeLocator + "/@" + attribute);
  }
  
  public String getAttributeV2(String attributeLocator)
  {
    String returnString = "";
    try
    {
      returnString = this.selenium.getAttribute(attributeLocator);
    }
    catch (SeleniumException se)
    {
      returnString = "";
    }
    return returnString;
  }
  
  public int getXpathCount(String attributeLocator)
  {
    return ((Integer)this.selenium.getXpathCount(attributeLocator)).intValue();
  }
  
  public String[] getSelectOptions(String selectLocator)
  {
    return this.selenium.getSelectOptions(selectLocator);
  }
  
  public String getText(String locator)
  {
    return this.selenium.getText(locator).replaceAll("\n ", "").replaceAll("\n", "").trim();
  }
  
  public String getTextV2(String locator)
  {
    String returnStr = "";
    try
    {
      returnStr = getText(locator);
    }
    catch (SeleniumException e)
    {
      returnStr = "";
    }
    return returnStr;
  }
  
  public String getSelectedLabel(String locator)
  {
    return this.selenium.getSelectedLabel(locator);
  }
  
  public String selectFrame(String locator)
  {
    this.selenium.selectFrame(locator);
    return ResultEnum.PASS;
  }
  
  public String getOS()
  {
    return getEval("(function(){var ua=navigator.userAgent;var system;if(ua.indexOf('Windows NT 5.1')>-1){system='Windows XP';}else if(ua.indexOf('Windows NT 6.0')>-1){system='Windows Vista';}else if(ua.indexOf('Windows NT 6.1')>-1){system='Windows 7';}else if(ua.indexOf('Ubuntu')>-1){system='Ubuntu';}else if(ua.indexOf('Mac')>-1){system='Mac';}else{system='Others';}return system;})()");
  }
  
  public String fireEvent(String locator, String eventName)
  {
    this.selenium.fireEvent(locator, eventName);
    return ResultEnum.PASS;
  }
  
  public String focus(String locator)
  {
    this.selenium.focus(locator);
    return ResultEnum.PASS;
  }
  
  protected void iAmShedule(SheduleEntity shedule)
  {
    this.executor = new ScheduledThreadPoolExecutor(1);
    this.executor.schedule(new XmlDriveSelenium.1(this, shedule), 
    
      shedule.getTempTime(), TimeUnit.SECONDS);
  }
  
  public boolean waitForElementDisplay(String xpath, String time)
  {
    SheduleEntity entity = new SheduleEntity(CommonTool.String2Int(time));
    iAmShedule(entity);
    int tempTime = 2000;
    while (!entity.isTimeout())
    {
      if (isVisibleV2(xpath))
      {
        shutdownAndAwaitTermination(this.executor);
        return true;
      }
      sleep(tempTime);
    }
    throw new SeleniumException("ERROR: wait for '" + xpath + "' " + time + "s time out!");
  }
  
  protected void shutdownAndAwaitTermination(ExecutorService pool)
  {
    pool.shutdown();
    try
    {
      if (!pool.awaitTermination(5L, TimeUnit.SECONDS))
      {
        pool.shutdownNow();
        if (!pool.awaitTermination(5L, TimeUnit.SECONDS)) {
          System.err.println("Pool did not terminate");
        }
      }
    }
    catch (InterruptedException ie)
    {
      pool.shutdownNow();
      
      Thread.currentThread().interrupt();
    }
  }
  
  public String getTarget(String target)
  {
    return target;
  }
  
  public boolean openWindow(String str)
  {
    if (isElementPresent(str))
    {
      String onClick = getAttributeV2(str + "/@onclick");
      if (!CommonTool.isEmpty(onClick))
      {
        String url = onClick.split("'")[1];
        openNewWindow(url);
        return true;
      }
      String href = getAttributeV2(str + "/@href");
      if (!CommonTool.isEmpty(href))
      {
        openNewWindow(href);
        return true;
      }
    }
    else
    {
      openNewWindow(str);
      return true;
    }
    return false;
  }
  
  protected void openNewWindow(String url)
  {
    String windowID = getWindowID();
    this.selenium.openWindow(url, windowID);
    this.selenium.waitForPopUp(windowID, this.timeout);
    this.selenium.selectWindow(windowID);
  }
  
  protected String getWindowID()
  {
    this.g_windowID += 1;
    return this.g_windowID;
  }
  
  public String selectWindow(String str)
  {
    int i = CommonTool.String2Int(str) - 1;
    if (i == 0)
    {
      this.selenium.selectWindow("null");
      windowFocus();
    }
    else if ((i < getWindowCount()) && (i > 0))
    {
      this.selenium.selectWindow(this.selenium.getAllWindowNames()[i]);
      windowFocus();
    }
    else
    {
      this.logger.warn("select default Window.");
      this.selenium.selectWindow("null");
      windowFocus();
    }
    return ResultEnum.PASS;
  }
  
  protected int getWindowCount()
  {
    return this.selenium.getAllWindowNames().length;
  }
  
  public String addCustomRequestHeader(String userName, String password)
  {
    String authHeader = Base64.encodeBytes((userName + ":" + password).getBytes());
    this.selenium.addCustomRequestHeader("Authorization", "Basic " + authHeader);
    return ResultEnum.PASS;
  }
  
  public String captureScreenshotPath()
  {
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String pngPath = "output/screenshot/" + df.format(new Date()) + ".png";
    try
    {
      if (!new File("output").isDirectory()) {
        new File("output").mkdir();
      }
      if (!new File("output/screenshot").isDirectory()) {
        new File("output/screenshot").mkdir();
      }
    }
    catch (SecurityException e)
    {
      this.logger.error(e.getMessage(), e);
    }
    try
    {
      String pngBase64 = captureScreenshot();
      Base64.decodeToFile(pngBase64, pngPath);
    }
    catch (IOException e)
    {
      this.logger.error(e.getMessage(), e);
    }
    sleep(1000);
    return pngPath.replace("output/", "");
  }
  
  public String captureScreenshot()
  {
    this.selenium.windowFocus();
    this.selenium.windowMaximize();
    if ((this.environment.getBrowser().toLowerCase().contains("firefox")) && (this.environment.getHost().equals("127.0.0.1"))) {
      return this.selenium.captureEntirePageScreenshotToString("");
    }
    return this.selenium.captureScreenshotToString();
  }
  
  public String saveCaptureScreenshot(String pngPath)
  {
    try
    {
      Base64.decodeToFile(captureScreenshot(), pngPath);
    }
    catch (IOException e)
    {
      this.logger.error(e.getMessage(), e);
    }
    return pngPath;
  }
  
  public String setProperty(String key, String value)
  {
    VariableMap.getInstance().mapPutKV(key, value);
    return ResultEnum.PASS;
  }
  
  public String mouseDown(String str)
  {
    this.selenium.mouseDown(str);
    return ResultEnum.PASS;
  }
  
  public String mouseUp(String str)
  {
    this.selenium.mouseUp(str);
    return ResultEnum.PASS;
  }
  
  public String screenshot(String screenShotname)
  {
    try
    {
      if (!new File("output").isDirectory()) {
        new File("output").mkdir();
      }
      if (!new File("output/local_" + this.environment.getLanguage()).isDirectory()) {
        new File("output/local_" + this.environment.getLanguage()).mkdir();
      }
    }
    catch (SecurityException e)
    {
      this.logger.error(e.getMessage());
    }
    saveCaptureScreenshot("output/local_" + this.environment.getLanguage() + "/" + screenShotname + ".png");
    return ResultEnum.PASS;
  }
}
