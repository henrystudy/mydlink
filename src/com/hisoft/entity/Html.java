package com.hisoft.entity;

import com.hisoft.type.ResultEnum;
import com.hisoft.utility.DataProviderTool;
import com.thoughtworks.selenium.SeleniumException;
import java.lang.reflect.InvocationTargetException;
import org.apache.log4j.Logger;

public class Html
  extends ABaseTag
{
  Logger logger = Logger.getLogger(Html.class);
  private String command;
  private String target;
  private String value;
  
  public String getTag()
  {
    return "Html";
  }
  
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
  
  private void replaceProperty()
  {
    this.target = DataProviderTool.getProperty(this.target);
    this.value = DataProviderTool.getProperty(this.value);
  }
  
  public String excute()
  {
    beforeExcute();
    replaceProperty();
    String returnStr = ResultEnum.FAIL;
    this.logger.info(toString());
    try
    {
      returnStr = reflectMethod(this.command, this.target, this.value);
      setResult(ResultEnum.PASS);
      append(returnStr);
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
        returnStr = ((SeleniumException)target).getMessage();
        this.logger.error(returnStr, (SeleniumException)target);
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
