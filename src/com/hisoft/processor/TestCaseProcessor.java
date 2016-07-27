package com.hisoft.processor;

import com.hisoft.entity.Assert;
import com.hisoft.entity.Choose;
import com.hisoft.entity.Function;
import com.hisoft.entity.Html;
import com.hisoft.entity.Import;
import com.hisoft.entity.TestCase;
import com.hisoft.entity.Verify;
import com.hisoft.exception.AssertException;
import com.hisoft.selenium.SeleniumX;
import java.util.List;
import org.apache.log4j.Logger;

public class TestCaseProcessor
{
  Logger logger = Logger.getLogger(TestCaseProcessor.class);
  
  public List<TestCase> Processor(List<TestCase> testcaseList)
  {
    for (TestCase tc : testcaseList)
    {
      SeleniumX.startSelenium();
      this.logger.info("TestCase start.");
      long startTime = System.currentTimeMillis();
      try
      {
        for (Object o : tc.getOperateList()) {
          if ((o instanceof Html)) {
            ((Html)o).excute();
          } else if ((o instanceof Verify)) {
            ((Verify)o).excute();
          } else if ((o instanceof Assert)) {
            try
            {
              ((Assert)o).excute();
            }
            catch (AssertException e)
            {
              break;
            }
          } else if ((o instanceof Choose)) {
            ((Choose)o).excute();
          } else if ((o instanceof Function)) {
            ((Function)o).excute();
          } else if ((o instanceof Import)) {
            ((Import)o).excute();
          } else {
            this.logger.error("if you see this, please check com.adm.processor.TestCaseProcessor.Processor().");
          }
        }
      }
      catch (Exception e)
      {
        this.logger.error(e.getMessage(), e);
      }
      tc.setTime(System.currentTimeMillis() - startTime);
      this.logger.info("TestCase end.");
      SeleniumX.stopSelenium();
    }
    return testcaseList;
  }
}
