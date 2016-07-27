package com.hisoft.entity;

import java.util.ArrayList;
import java.util.List;

public class AccountEntity
{
  public String email;
  public String password;
  public String emailPassword;
  public String firstName;
  public String lastName;
  public String emailLink;
  public List<DeviceEntity> devices;
  
  public AccountEntity() {}
  
  public AccountEntity(String email, String password)
  {
    this.email = email;
    this.password = password;
    this.devices = new ArrayList();
  }
  
  public AccountEntity(String email, String password, String emailPassword)
  {
    this.email = email;
    this.password = password;
    this.emailPassword = emailPassword;
    this.devices = new ArrayList();
  }
  
  public AccountEntity(String email, String password, List<DeviceEntity> devices)
  {
    this.email = email;
    this.password = password;
    this.devices = devices;
  }
  
  public AccountEntity(String email, String password, String emailPassword, String firstName, String lastName, List<DeviceEntity> devices)
  {
    this.email = email;
    this.password = password;
    this.emailPassword = emailPassword;
    this.firstName = firstName;
    this.lastName = lastName;
    this.devices = devices;
  }
  
  public List<DeviceEntity> getDeviceList()
  {
    return this.devices;
  }
  
  public String getEmail()
  {
    return this.email;
  }
  
  public void setEmail(String email)
  {
    this.email = email;
  }
  
  public String getPassword()
  {
    return this.password;
  }
  
  public void setPassword(String password)
  {
    this.password = password;
  }
  
  public String getEmailPassword()
  {
    return this.emailPassword;
  }
  
  public void setEmailPassword(String emailPassword)
  {
    this.emailPassword = emailPassword;
  }
  
  public String getLastName()
  {
    return this.lastName;
  }
  
  public void setLastName(String lastName)
  {
    this.lastName = lastName;
  }
  
  public String getFirstName()
  {
    return this.firstName;
  }
  
  public void setFirstName(String firstName)
  {
    this.firstName = firstName;
  }
  
  public DeviceEntity getDeviceForList(String sn)
  {
    for (DeviceEntity device : this.devices) {
      if (device.getSn().equals(sn)) {
        return device;
      }
    }
    return null;
  }
  
  public String getEmailLink()
  {
    return this.emailLink;
  }
  
  public void setEmailLink(String emailLink)
  {
    this.emailLink = emailLink;
  }
}
