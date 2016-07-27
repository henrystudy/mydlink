package com.hisoft.exception;

public class XpathNotFoundException
  extends RuntimeException
{
  private static final long serialVersionUID = 454211223122342341L;
  String filePath = "";
  String xpath = "";
  
  public XpathNotFoundException(String xpath, String filePath)
  {
    this.xpath = xpath;
    this.filePath = filePath;
  }
  
  public String getMessage()
  {
    return this.xpath + " in file " + this.filePath + " is not found!";
  }
}
