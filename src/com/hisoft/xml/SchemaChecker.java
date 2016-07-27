package com.hisoft.xml;

import com.hisoft.type.XMLType;
import com.hisoft.utility.CommonTool;
import java.io.File;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXParseException;

public class SchemaChecker
{
  static Logger logger = Logger.getLogger(SchemaChecker.class);
  private static final String CONFIG_SCHEMA = "schema/config.xsd";
  private static final String TESTCASE_SCHEMA = "schema/testcase.xsd";
  private static final String WEBSITE_SCHEMA = "schema/website.xsd";
  
  private static boolean validateSingleSchema(File xml, File xsd)
  {
    boolean legal = false;
    try
    {
      if ((fileExists(xml)) && (fileExists(xsd)))
      {
        SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = sf.newSchema(xsd);
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xml));
        legal = true;
      }
    }
    catch (SAXParseException spe)
    {
      logger.error("file '" + xml.getPath() + "' " + spe.getMessage(), spe);
    }
    catch (Exception e)
    {
      logger.error(e.getMessage(), e);
    }
    return legal;
  }
  
  private boolean validateSingleSchemaV2(File xml, File xsd)
  {
    boolean legal = false;
    try
    {
      if ((fileExists(xml)) && (fileExists(xsd)))
      {
        SchemaFactory sf = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        Schema schema = sf.newSchema(xsd);
        Validator validator = schema.newValidator();
        validator.validate(new StreamSource(xml));
        legal = true;
      }
    }
    catch (SAXParseException spe)
    {
      legal = false;
    }
    catch (Exception e)
    {
      legal = false;
    }
    return legal;
  }
  
  public static boolean validateOnlyXml(File xmlFile)
  {
    boolean legal = false;
    try
    {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(xmlFile);
      doc.getDocumentElement().normalize();
      legal = true;
    }
    catch (SAXParseException spe)
    {
      logger.error(spe.getMessage(), spe);
    }
    catch (Exception e)
    {
      logger.error(e.getMessage(), e);
    }
    return legal;
  }
  
  public static boolean fileExists(File file)
  {
    file.exists();
    
    return file.exists();
  }
  
  public static boolean validateXml(File xmlFile, XMLType xt)
  {
    return validateSingleSchema(xmlFile, getXsdFile(xt));
  }
  
  public boolean validateXmlV2(File xmlFile, XMLType xt)
  {
    return validateSingleSchemaV2(xmlFile, getXsdFile(xt));
  }
  
  public static boolean validateXml(String xmlFilePath, XMLType xt)
  {
    File tempFile = new File(xmlFilePath);
    if (tempFile.exists())
    {
      if (tempFile.isDirectory()) {
        return validateXml(CommonTool.readXmlFileList(xmlFilePath), xt);
      }
      return validateXml(new File(xmlFilePath), xt);
    }
    logger.info("the path: '" + xmlFilePath + "'  does not exist!");
    return true;
  }
  
  public static boolean validateXml(List<File> files, XMLType xt)
  {
    boolean legal = true;
    switch (xt)
    {
    case CONFIG: 
      logger.info("check the CONFIG XML...");
      break;
    case WEBSITE: 
      logger.info("check the TESTCASE XML...");
      break;
    case TESTCASE: 
      logger.info("check the WEBSITE XML...");
      break;
    default: 
      logger.info("if you see this, XML type has been changed. please fix your code.");
    }
    for (int i = 0; i < files.size(); i++) {
      legal = (validateXml((File)files.get(i), xt)) && (legal);
    }
    return legal;
  }
  
  private static File getXsdFile(XMLType xt)
  {
    switch (xt)
    {
    case CONFIG: 
      return new File("schema/config.xsd");
    case WEBSITE: 
      return new File("schema/testcase.xsd");
    case TESTCASE: 
      return new File("schema/website.xsd");
    }
    return null;
  }
}
