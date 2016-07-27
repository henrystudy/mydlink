package com.hisoft.entity;

import com.hisoft.type.DeviceState;

public class DeviceEntity
{
  private String deviceName;
  private String modelName;
  private String connectType;
  private String sn;
  private String userName;
  private String mac;
  private String passWord;
  private String fw;
  public DeviceState state;
  
  public String getFw()
  {
    return this.fw;
  }
  
  public void setFw(String fw)
  {
    this.fw = fw;
  }
  
  public DeviceState getState()
  {
    return this.state;
  }
  
  public void setState(DeviceState state)
  {
    this.state = state;
  }
  
  public String getDeviceName()
  {
    return this.deviceName;
  }
  
  public void setDeviceName(String deviceName)
  {
    this.deviceName = deviceName;
  }
  
  public String getModelName()
  {
    return this.modelName;
  }
  
  public void setModelName(String modelName)
  {
    this.modelName = modelName;
  }
  
  public String getConnectType()
  {
    return this.connectType;
  }
  
  public void setConnectType(String connectType)
  {
    this.connectType = connectType;
  }
  
  public String getSn()
  {
    return this.sn;
  }
  
  public void setSn(String sn)
  {
    this.sn = sn;
  }
  
  public String getUserName()
  {
    return this.userName;
  }
  
  public void setUserName(String userName)
  {
    this.userName = userName;
  }
  
  public String getMac()
  {
    return this.mac;
  }
  
  public void setMac(String mac)
  {
    this.mac = mac;
  }
  
  public String getPassWord()
  {
    return this.passWord;
  }
  
  public void setPassWord(String passWord)
  {
    this.passWord = passWord;
  }
}
