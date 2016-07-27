package com.hisoft.selenium;

import com.hisoft.entity.Environment;
import com.thoughtworks.selenium.BrowserConfigurationOptions;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import org.apache.log4j.Logger;

public class SeleniumX
{
  private static boolean isSeleniumStart = false;
  static Logger logger = Logger.getLogger(SeleniumX.class);
  private static Selenium selenium = null;
  private static Environment environment = Environment.getInstance();
  
  public static Selenium getInstance()
  {
    if (selenium == null) {
      selenium = new DefaultSelenium(environment.getHost(), environment.getPort(), environment.getBrowser(), environment.getBaseUrl());
    }
    return selenium;
  }
  
  public static void startSelenium()
  {
    if (selenium == null) {
      getInstance();
    }
    logger.info(">>> new selenium session start.");
    BrowserConfigurationOptions bco = new BrowserConfigurationOptions();
    selenium.start(bco.setCommandLineFlags("--disable-web-security"));
    selenium.setTimeout("90000");
    selenium.windowMaximize();
    selenium.windowFocus();
    selenium.useXpathLibrary("javascript-xpath");
    isSeleniumStart = true;
  }
  
  public static void stopSelenium()
  {
    if (isSeleniumStart)
    {
      logger.info("<<< stop the selenium session.");
      selenium.close();
      selenium.stop();
      isSeleniumStart = false;
    }
  }
}
