package com.hisoft.entity;

import java.util.ArrayList;
import java.util.List;

public class TestCase
{
  private List<ABaseTag> operateList = new ArrayList();
  private long time = 0L;
  private String fileName = "";
  private String testCaseName = "";
  private String testCaseVersion = "";
  private String description = "";
  private String createdAuthor = "";
  private String createdDate = "";
  private String lastUpdatedAuthor = "";
  private String lastUpdatedDate = "";
  private String recordInfoAccount = "";
  private String recordInfoIndex = "";
  
  public long getTime()
  {
    return this.time;
  }
  
  public void setTime(long time)
  {
    this.time = time;
  }
  
  public String getFileName()
  {
    return this.fileName;
  }
  
  public void setFileName(String fileName)
  {
    this.fileName = fileName;
  }
  
  public String getTestCaseVersion()
  {
    return this.testCaseVersion;
  }
  
  public void setTestCaseVersion(String testCaseVersion)
  {
    this.testCaseVersion = testCaseVersion;
  }
  
  public String getTestCaseName()
  {
    return this.testCaseName;
  }
  
  public void setTestCaseName(String testCaseName)
  {
    this.testCaseName = testCaseName;
  }
  
  public String getDescription()
  {
    return this.description;
  }
  
  public void setDescription(String description)
  {
    this.description = description;
  }
  
  public String getCreatedAuthor()
  {
    return this.createdAuthor;
  }
  
  public void setCreatedAuthor(String createdAuthor)
  {
    this.createdAuthor = createdAuthor;
  }
  
  public String getCreatedDate()
  {
    return this.createdDate;
  }
  
  public void setCreatedDate(String createdDate)
  {
    this.createdDate = createdDate;
  }
  
  public String getLastUpdatedAuthor()
  {
    return this.lastUpdatedAuthor;
  }
  
  public void setLastUpdatedAuthor(String lastUpdatedAuthor)
  {
    this.lastUpdatedAuthor = lastUpdatedAuthor;
  }
  
  public String getLastUpdatedDate()
  {
    return this.lastUpdatedDate;
  }
  
  public void setLastUpdatedDate(String lastUpdatedDate)
  {
    this.lastUpdatedDate = lastUpdatedDate;
  }
  
  public String getRecordInfoAccount()
  {
    return this.recordInfoAccount;
  }
  
  public void setRecordInfoAccount(String recordInfoAccount)
  {
    this.recordInfoAccount = recordInfoAccount;
  }
  
  public String getRecordInfoIndex()
  {
    return this.recordInfoIndex;
  }
  
  public void setRecordInfoIndex(String recordInfoIndex)
  {
    this.recordInfoIndex = recordInfoIndex;
  }
  
  public List<ABaseTag> getOperateList()
  {
    return this.operateList;
  }
  
  public void setOperateList(List<ABaseTag> operateList)
  {
    this.operateList = operateList;
  }
  
  public boolean addOperate(ABaseTag i)
  {
    return this.operateList.add(i);
  }
  
  public void excute()
  {
    for (ABaseTag i : this.operateList) {
      i.excute();
    }
  }
}
