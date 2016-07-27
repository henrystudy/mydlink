package com.hisoft.entity;

import com.hisoft.type.ResultEnum;
import java.util.List;
import org.apache.log4j.Logger;

public class Choose
  extends ABaseTag
{
  Logger logger = Logger.getLogger(Choose.class);
  private ABaseTag when;
  private List<ABaseTag> chooseDo;
  private List<ABaseTag> otherwise;
  
  public ABaseTag getWhen()
  {
    return this.when;
  }
  
  public void setWhen(ABaseTag when)
  {
    this.when = when;
  }
  
  public List<ABaseTag> getDo()
  {
    return this.chooseDo;
  }
  
  public void setDo(List<ABaseTag> do1)
  {
    this.chooseDo = do1;
  }
  
  public List<ABaseTag> getOtherwise()
  {
    return this.otherwise;
  }
  
  public void setOtherwise(List<ABaseTag> otherwise)
  {
    this.otherwise = otherwise;
  }
  
  public String excute()
  {
    beforeExcute();
    this.logger.info(toString());
    this.logger.info(WhenToString());
    boolean flag = true;
    this.when.excute();
    if (this.when.getResult().equals(ResultEnum.PASS))
    {
      this.logger.info(ChooseDoToString());
      if ((this.chooseDo != null) && (!this.chooseDo.isEmpty())) {
        for (ABaseTag temp : this.chooseDo) {
          flag &= temp.excute().contains(ResultEnum.PASS);
        }
      }
    }
    else
    {
      this.logger.info(OtherwiseToString());
      if ((this.otherwise != null) && (!this.otherwise.isEmpty())) {
        for (ABaseTag temp : this.otherwise) {
          flag &= temp.excute().contains(ResultEnum.PASS);
        }
      }
    }
    if (flag)
    {
      setResult(ResultEnum.PASS);
      return ResultEnum.PASS;
    }
    return ResultEnum.FAIL;
  }
  
  public String toString()
  {
    return "[ " + WhenToString() + " ]\n[ " + ChooseDoToString() + " ]\n[ " + OtherwiseToString() + " ]";
  }
  
  public String WhenToString()
  {
    return "When  | " + this.when.toString();
  }
  
  public String ChooseDoToString()
  {
    String str = "Do ";
    for (Object o : this.chooseDo) {
      str = str + o.toString() + "\n";
    }
    return str;
  }
  
  public String OtherwiseToString()
  {
    String str = "Otherwise ";
    for (Object o : this.otherwise) {
      str = str + o.toString() + "\n";
    }
    return str;
  }
}
