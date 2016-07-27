package com.hisoft.start;

import com.hisoft.entity.Environment;
import com.hisoft.entity.TestCase;
import com.hisoft.processor.ReportProcessor;
import com.hisoft.processor.TestCaseProcessor;
import com.hisoft.type.XMLType;
import com.hisoft.xml.SchemaChecker;
import com.hisoft.xml.XML2TestCase;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import org.apache.log4j.Logger;

public class Starter
{
  static Logger logger = Logger.getLogger(Starter.class);
  
  public static void main(String[] args)
  {
    logger.info("program start...");
    if (args.length != 0)
    {
      File configFile = new File(args[0]);
      if (!configFile.exists())
      {
        FileNotFoundException ffe = new FileNotFoundException(configFile.getPath());
        logger.error(ffe.getMessage(), ffe);
      }
      Environment.setCONFIGFILEPATH(args[0]);
    }
    else
    {
      logger.info("use default config file 'config.xml'.");
    }
    Environment environment = Environment.getInstance();
    if ((SchemaChecker.validateXml(environment.getTestCaseList(), XMLType.TESTCASE)) && (SchemaChecker.validateXml(environment.getPageDataPath(), XMLType.WEBSITE)))
    {
      List<File> fileList = Environment.getInstance().getTestCaseList();
      List<TestCase> l = XML2TestCase.xml2TestCaseObject(fileList);
      l = new TestCaseProcessor().Processor(l);
      new ReportProcessor().XMLAndHTMLReport(l);
    }
    else
    {
      logger.error("validate xml fail. please check xml file.");
    }
    logger.info("program exit...");
    System.exit(0);
  }
}
