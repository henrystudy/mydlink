package com.hisoft.entity;

import com.hisoft.type.ResultEnum;
import com.thoughtworks.selenium.SeleniumException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.apache.log4j.Logger;

public class Function
  extends ABaseTag
{
  Logger logger = Logger.getLogger(Function.class);
  private String command;
  private String target;
  private String value;
  
  public String getCommand()
  {
    return this.command;
  }
  
  public void setCommand(String command)
  {
    this.command = command;
  }
  
  public String getTarget()
  {
    return this.target;
  }
  
  public void setTarget(String target)
  {
    this.target = target;
  }
  
  public String getValue()
  {
    return this.value;
  }
  
  public void setValue(String value)
  {
    this.value = value;
  }
  
  public String excute()
  {
    beforeExcute();
    String returnStr = ResultEnum.FAIL;
    this.logger.info(toString());
    try
    {
      try
      {
        Method f = this.xmlDriveSelenium.getClass().getMethod(this.command, new Class[] { String.class });
        returnStr = (String)f.invoke(this.xmlDriveSelenium, new Object[] { this.target });
      }
      catch (NoSuchMethodException e)
      {
        try
        {
          Method f = this.xmlDriveSelenium.getClass().getMethod(this.command, new Class[] { String.class, String.class });
          returnStr = (String)f.invoke(this.xmlDriveSelenium, new Object[] { this.target, this.value });
        }
        catch (NoSuchMethodException e1)
        {
          Method f = this.xmlDriveSelenium.getClass().getMethod(this.command, new Class[0]);
          returnStr = (String)f.invoke(this.xmlDriveSelenium, new Object[0]);
        }
      }
      append(returnStr);
      setResult(ResultEnum.PASS);
    }
    catch (SecurityException e)
    {
      this.logger.error(e.getMessage(), e);
    }
    catch (IllegalArgumentException e)
    {
      this.logger.error(e.getMessage(), e);
    }
    catch (IllegalAccessException e)
    {
      this.logger.error(e.getMessage(), e);
    }
    catch (InvocationTargetException e)
    {
      Throwable target = e.getTargetException();
      if ((target instanceof SeleniumException))
      {
        returnStr = "LINE: " + getLineNumb() + ", " + ((SeleniumException)target).getMessage();
        this.logger.error(returnStr);
        append(returnStr);
        setResult(ResultEnum.FAIL);
      }
      else
      {
        this.logger.error(e.getMessage(), e);
      }
    }
    catch (NoSuchMethodException e)
    {
      this.logger.error(e.getMessage(), e);
    }
    return returnStr;
  }
  
  public String toString()
  {
    return this.command + " | " + this.target + " | " + this.value;
  }
}
