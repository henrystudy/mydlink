package com.hisoft.entity;

public class SheduleEntity
{
  private boolean isTimeout = false;
  private int tempTime;
  
  public SheduleEntity(int tempTime)
  {
    this.tempTime = tempTime;
  }
  
  public void setTimeout(boolean isTimeout)
  {
    this.isTimeout = isTimeout;
  }
  
  public boolean isTimeout()
  {
    return this.isTimeout;
  }
  
  public int getTempTime()
  {
    return this.tempTime;
  }
}
