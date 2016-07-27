package com.hisoft.exception;

public class AssertException
  extends RuntimeException
{
  private static final long serialVersionUID = 1424123412341341231L;
  String messasge = "";
  
  public AssertException(String messasge)
  {
    this.messasge = messasge;
  }
  
  public String getMessage()
  {
    return this.messasge;
  }
}
