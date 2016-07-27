package com.hisoft.entity;

import com.hisoft.exception.AssertException;
import com.hisoft.type.AssertVerifyType;
import com.hisoft.type.ResultEnum;
import com.hisoft.utility.DataProviderTool;
import org.apache.log4j.Logger;

public class Assert
  extends ABaseTag
{
  Logger logger = Logger.getLogger(Assert.class);
  private String excepted = "";
  private ABaseTag verify;
  private AssertVerifyType avt = AssertVerifyType.EQUALS;
  
  public String getTag()
  {
    return "Assert";
  }
  
  public AssertVerifyType getAvt()
  {
    return this.avt;
  }
  
  public void setAvt(AssertVerifyType avt)
  {
    this.avt = avt;
  }
  
  public ABaseTag getVerify()
  {
    return this.verify;
  }
  
  public void setVerify(ABaseTag verify)
  {
    this.verify = verify;
  }
  
  public String getExcepted()
  {
    return this.excepted;
  }
  
  public void setExcepted(String excepted)
  {
    this.excepted = excepted;
  }
  
  private void replaceProperty()
  {
    this.excepted = DataProviderTool.getProperty(this.excepted);
    if (this.excepted.contains("#$password$#")) {
      this.excepted = this.excepted.replace("#$password$#", DataProviderTool.getProperty("${newPassword}"));
    }
  }
  
  public String excute()
  {
    beforeExcute();
    replaceProperty();
    this.logger.info(toString());
    String temp = this.verify.excute();
    String returnStr = Processor(this.excepted, temp);
    return returnStr;
  }
  
  public String Processor(String excepted, String saw)
  {
    String info = "";
    switch (this.avt)
    {
    case EQUALS: 
      if (excepted.equals(saw)) {
        info = ResultEnum.PASS + ": " + saw;
      } else {
        info = "Expected equals'" + excepted + "' but saw '" + saw + "' instead.";
      }
      break;
    case CONTAINS: 
      if (saw.contains(excepted)) {
        info = ResultEnum.PASS + ": " + saw;
      } else {
        info = "Expected '" + saw + "' contains '" + excepted + "', but not";
      }
      break;
    case NOTIN: 
      if (excepted.contains(saw)) {
        info = ResultEnum.PASS + ": " + saw;
      } else {
        info = "Expected '" + saw + "' in '" + excepted + "', but not";
      }
      break;
    case NOTEQUALS: 
      if (saw.equals(excepted)) {
        info = ResultEnum.PASS + ": " + saw;
      } else {
        info = "Expected result'" + excepted + "' but saw '" + saw + "' instead.";
      }
      break;
    }
    if (info.contains("Expected "))
    {
      setResult(ResultEnum.FAIL);
      append(info);
      this.logger.error(info);
      throw new AssertException(info);
    }
    append(info);
    setResult(ResultEnum.PASS);
    
    return info;
  }
  
  public String toString()
  {
    return "Verify " + this.avt.toString().toLowerCase() + " | " + this.excepted + " | " + this.verify.toString();
  }
}
