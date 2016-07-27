package com.hisoft.entity;

import com.hisoft.type.ResultEnum;
import java.util.List;
import org.apache.log4j.Logger;

public class Import
  extends ABaseTag
{
  Logger logger = Logger.getLogger(Import.class);
  private String scr;
  private List<ABaseTag> importXML;
  
  public List<ABaseTag> getImportXML()
  {
    return this.importXML;
  }
  
  public void setImportXML(List<ABaseTag> importXML)
  {
    this.importXML = importXML;
  }
  
  public String getScr()
  {
    return this.scr;
  }
  
  public void setScr(String scr)
  {
    this.scr = scr;
  }
  
  public Import()
  {
    append(getClass().getName());
  }
  
  public String toString()
  {
    String str = "Import ";
    for (Object o : this.importXML) {
      str = str + o.toString() + "\n";
    }
    return str;
  }
  
  public String excute()
  {
    beforeExcute();
    this.logger.info(toString());
    boolean flag = true;
    for (ABaseTag temp : this.importXML) {
      flag &= temp.excute().contains(ResultEnum.PASS);
    }
    if (flag)
    {
      setResult(ResultEnum.PASS);
      return ResultEnum.PASS;
    }
    return flag;
  }
}
