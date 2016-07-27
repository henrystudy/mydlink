package com.hisoft.entity;

public class AnnotationEntity
{
  private String caseName;
  private String description;
  private String creater;
  private String createDate;
  private String updater;
  private String updateDate;
  private String recordAccountName;
  private String recordDeviceIndex;
  private AccountEntity recordAccountEntity;
  private DeviceEntity recordDeviceEntity;
  public static final String ANNOTATION = "annotation";
  public static final String TESTCASENAME = "testCaseName";
  public static final String DESCRIPTION = "description";
  public static final String CREATED = "created";
  public static final String LASTUPDATED = "lastUpdated";
  public static final String AUTHOR = "author";
  public static final String DATE = "date";
  public static final String RECORDINFO = "recordInfo";
  public static final String RECORDACCOUNT = "account";
  public static final String RECORDEVICEINDEX = "deviceIndex";
  
  public AnnotationEntity() {}
  
  public AnnotationEntity(String description, String creater, String createDate, String updater, String updateDate)
  {
    this.description = description;
    this.creater = creater;
    this.createDate = createDate;
    this.updater = updater;
    this.updateDate = updateDate;
  }
  
  public String getCaseName()
  {
    return this.caseName;
  }
  
  public void setCaseName(String caseName)
  {
    this.caseName = caseName;
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public String getCreater()
  {
    return this.creater;
  }
  
  public String getCreateDate()
  {
    return this.createDate;
  }
  
  public String getUpdater()
  {
    return this.updater;
  }
  
  public String getUpdateDate()
  {
    return this.updateDate;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public void setCreater(String creater)
  {
    this.creater = creater;
  }
  
  public void setCreateDate(String createDate)
  {
    this.createDate = createDate;
  }
  
  public void setUpdater(String updater)
  {
    this.updater = updater;
  }
  
  public void setUpdateDate(String updateDate)
  {
    this.updateDate = updateDate;
  }
  
  public String getRecordAccountName()
  {
    return this.recordAccountName;
  }
  
  public void setRecordAccountName(String recordAccount)
  {
    this.recordAccountName = recordAccount;
  }
  
  public String getRecordDeviceIndex()
  {
    return this.recordDeviceIndex;
  }
  
  public void setRecordDeviceIndex(String recordDeviceIndex)
  {
    this.recordDeviceIndex = recordDeviceIndex;
  }
  
  public AccountEntity getRecordAccountEntity()
  {
    return this.recordAccountEntity;
  }
  
  public void setRecordAccountEntity(AccountEntity recordAccountEntity)
  {
    this.recordAccountEntity = recordAccountEntity;
  }
  
  public DeviceEntity getRecordDeviceEntity()
  {
    return this.recordDeviceEntity;
  }
  
  public void setRecordDeviceEntity(DeviceEntity recordDeviceEntity)
  {
    this.recordDeviceEntity = recordDeviceEntity;
  }
}
