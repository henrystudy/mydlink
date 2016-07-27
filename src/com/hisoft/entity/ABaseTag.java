package com.hisoft.entity;

import com.hisoft.selenium.DlinkXMLDriveSelenium;
import com.hisoft.type.ResultEnum;
import com.hisoft.utility.CommonTool;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.log4j.Logger;

public abstract class ABaseTag
{
  Object xmlDriveSelenium = DlinkXMLDriveSelenium.getInstance();
  private StringBuffer sb = new StringBuffer();
  private int lineNumb = 0;
  private ResultEnum result = ResultEnum.NOTRUN;
  Logger logger = Logger.getLogger(ABaseTag.class);
  private String screenshot = "";
  
  public String getScreenshot()
  {
    return this.screenshot;
  }
  
  public void setScreenshot(String screenshot)
  {
    this.logger.info("screenshot: " + screenshot);
    this.screenshot = screenshot;
  }
  
  public String getTag()
  {
    return "ABaseTag";
  }
  
  public ResultEnum getResult()
  {
    return this.result;
  }
  
  public void setResult(ResultEnum result)
  {
    if (result.equals(ResultEnum.FAIL)) {
      try
      {
        setScreenshot(reflectMethod("captureScreenshotPath", "", ""));
      }
      catch (SecurityException e)
      {
        this.logger.error(e.getMessage(), e);
      }
      catch (IllegalArgumentException e)
      {
        this.logger.error(e.getMessage(), e);
      }
      catch (NoSuchMethodException e)
      {
        this.logger.error(e.getMessage(), e);
      }
      catch (IllegalAccessException e)
      {
        this.logger.error(e.getMessage(), e);
      }
      catch (InvocationTargetException e)
      {
        this.logger.error(e.getMessage(), e);
      }
    }
    this.result = result;
  }
  
  public void beforeExcute()
  {
    this.result = ResultEnum.FAIL;
  }
  
  public int getLineNumb()
  {
    return this.lineNumb;
  }
  
  public void setLineNumb(int lineNumb)
  {
    this.lineNumb = lineNumb;
  }
  
  public String getReportInfo()
  {
    return this.sb.toString();
  }
  
  public void append(String str)
  {
    this.sb.append(str);
  }
  
  public String excute()
  {
    return null;
  }
  
  public String reflectMethod(String command, String target, String value)
    throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException
  {
    String returnStr = ResultEnum.FAIL;
    try
    {
      Method f = this.xmlDriveSelenium.getClass().getMethod(command, new Class[] { String.class });
      returnStr = CommonTool.any2String(f.invoke(this.xmlDriveSelenium, new Object[] { target }));
    }
    catch (NoSuchMethodException e)
    {
      try
      {
        Method f = this.xmlDriveSelenium.getClass().getMethod(command, new Class[] { String.class, String.class });
        returnStr = CommonTool.any2String(f.invoke(this.xmlDriveSelenium, new Object[] { target, value }));
      }
      catch (NoSuchMethodException e1)
      {
        Method f = this.xmlDriveSelenium.getClass().getMethod(command, new Class[0]);
        returnStr = CommonTool.any2String(f.invoke(this.xmlDriveSelenium, new Object[0]));
      }
    }
    return returnStr;
  }
}
