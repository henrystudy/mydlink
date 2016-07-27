package com.hisoft.xml;

import com.hisoft.entity.ABaseTag;
import com.hisoft.entity.Assert;
import com.hisoft.entity.Choose;
import com.hisoft.entity.Function;
import com.hisoft.entity.Html;
import com.hisoft.entity.Verify;
import com.hisoft.exception.XpathNotFoundException;
import com.hisoft.type.AssertVerifyType;
import com.hisoft.utility.CommonTool;
import com.hisoft.utility.DataProviderTool;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.log4j.Logger;

public class XML2Import
{
  private static Logger logger = Logger.getLogger(XML2Import.class);
  private static XMLInputFactory factory = XMLInputFactory.newInstance();
  private static XMLStreamReader reader;
  private static String fileName;
  private static final String srcPath = "import/";
  
  public static List<ABaseTag> xml2Import(String str)
  {
    File file = new File("import/" + str);
    if (!file.exists())
    {
      FileNotFoundException ffe = new FileNotFoundException(file.getPath());
      logger.error(ffe.getMessage(), ffe);
    }
    return xml2Import(file);
  }
  
  private static List<ABaseTag> xml2Import(File file)
  {
    List<ABaseTag> importList = null;
    try
    {
      reader = factory.createXMLStreamReader(new FileReader(file));
      fileName = file.getName();
      importList = new ArrayList();
      while (reader.hasNext())
      {
        switch (reader.getEventType())
        {
        case 1: 
          String name = reader.getLocalName();
          if (name.equals("Html")) {
            importList.add(xml2Html());
          } else if (name.equals("Function")) {
            importList.add(xml2Function());
          } else if (!name.equals("ImportXML")) {
            if (name.equals("Verify")) {
              importList.add(xml2Verify());
            } else if (!name.equals("Assert")) {
              if (!name.equals("Choose")) {
                if (!name.equals("Loop")) {
                  if (!name.equals("Test")) {
                    if (!name.equals("TestCase")) {
                      if (!name.equals("testCaseName")) {
                        if (!name.equals("annotation")) {
                          if (!name.equals("description")) {
                            if (!name.equals("created")) {
                              if (!name.equals("lastUpdated")) {
                                if (!name.equals("recordInfo")) {
                                  logger.debug("xml2TestCaseObject unhandle tag: " + name);
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          break;
        case 2: 
          break;
        }
        if (reader.hasNext()) {
          reader.next();
        }
      }
    }
    catch (FileNotFoundException e)
    {
      logger.error(e.getMessage(), e);
    }
    catch (XMLStreamException e)
    {
      logger.error(e.getMessage(), e);
    }
    return importList;
  }
  
  private static Html xml2Html()
  {
    Html html = new Html();
    html.setCommand(reader.getAttributeValue(null, "command"));
    try
    {
      html.setTarget(DataProviderTool.getData(reader.getAttributeValue(null, "target")));
      html.setValue(DataProviderTool.getData(reader.getAttributeValue(null, "value")));
    }
    catch (XpathNotFoundException e)
    {
      logger.error("file: " + fileName + " line: " + reader.getLocation().getLineNumber() + " " + e.getMessage(), e);
    }
    html.setLineNumb(reader.getLocation().getLineNumber());
    return html;
  }
  
  private static Function xml2Function()
  {
    Function function = new Function();
    function.setCommand(reader.getAttributeValue(null, "command"));
    try
    {
      function.setTarget(DataProviderTool.getData(reader.getAttributeValue(null, "target")));
      function.setValue(DataProviderTool.getData(reader.getAttributeValue(null, "value")));
    }
    catch (XpathNotFoundException e)
    {
      logger.error("file: " + fileName + " line: " + reader.getLocation().getLineNumber() + " " + e.getMessage(), e);
    }
    function.setLineNumb(reader.getLocation().getLineNumber());
    return function;
  }
  
  private static AssertVerifyType getAVT(String str)
  {
    AssertVerifyType avt = AssertVerifyType.EQUALS;
    if (!CommonTool.isEmpty(str)) {
      try
      {
        avt = AssertVerifyType.valueOf(str.toUpperCase());
      }
      catch (IllegalArgumentException e)
      {
        logger.error(e.getMessage() + " change to default");
        avt = AssertVerifyType.CONTAINS;
      }
    }
    return avt;
  }
  
  private static Verify xml2Verify()
    throws XMLStreamException
  {
    Verify verify = new Verify();
    try
    {
      verify.setExcepted(DataProviderTool.getData(reader.getAttributeValue(null, "expected")));
    }
    catch (XpathNotFoundException e)
    {
      logger.error("file: " + fileName + " line: " + reader.getLocation().getLineNumber() + " " + e.getMessage(), e);
    }
    verify.setAvt(getAVT(reader.getAttributeValue(null, "type")));
    Boolean flag = Boolean.valueOf(true);
    while ((reader.hasNext()) && (flag.booleanValue()))
    {
      switch (reader.getEventType())
      {
      case 1: 
        verify.setLineNumb(reader.getLocation().getLineNumber());
        String name = reader.getLocalName();
        if (name.equals("Html")) {
          verify.setVerify(xml2Html());
        } else if (name.equals("Function")) {
          verify.setVerify(xml2Function());
        } else if (!name.equals("Verify")) {
          logger.debug("xml2Verify unhandle tag: " + name);
        }
        break;
      case 2: 
        if (reader.getLocalName().equals("Verify")) {
          flag = Boolean.valueOf(false);
        }
        break;
      }
      if (reader.hasNext()) {
        reader.next();
      }
    }
    return verify;
  }
  
  private static Assert xml2Assert()
    throws XMLStreamException
  {
    Assert assert1 = new Assert();
    try
    {
      assert1.setExcepted(DataProviderTool.getData(reader.getAttributeValue(null, "expected")));
    }
    catch (XpathNotFoundException e)
    {
      logger.error("file: " + fileName + " line: " + reader.getLocation().getLineNumber() + " " + e.getMessage(), e);
    }
    Boolean flag = Boolean.valueOf(true);
    while ((reader.hasNext()) && (flag.booleanValue()))
    {
      switch (reader.getEventType())
      {
      case 1: 
        String name = reader.getLocalName();
        if (name.equals("Html")) {
          assert1.setVerify(xml2Html());
        } else if (name.equals("Function")) {
          assert1.setVerify(xml2Function());
        } else if (!name.equals("Assert")) {
          logger.debug("xml2Assert unhandle tag: " + name);
        }
        break;
      case 2: 
        if (reader.getLocalName().equals("Assert")) {
          flag = Boolean.valueOf(false);
        }
        break;
      }
      if (reader.hasNext()) {
        reader.next();
      }
    }
    return assert1;
  }
  
  private static Choose xml2Choose()
    throws XMLStreamException
  {
    Choose choose = new Choose();
    choose.setLineNumb(reader.getLocation().getLineNumber());
    Boolean flag = Boolean.valueOf(true);
    while ((reader.hasNext()) && (flag.booleanValue()))
    {
      switch (reader.getEventType())
      {
      case 1: 
        String name = reader.getLocalName();
        if (name.equals("When")) {
          choose.setWhen(xml2When());
        } else if (name.equals("Do")) {
          choose.setDo(xml2Do());
        } else if (name.equals("Otherwise")) {
          choose.setOtherwise(xml2Otherwise());
        }
        break;
      case 2: 
        if (reader.getLocalName().equals("Choose")) {
          flag = Boolean.valueOf(false);
        }
        break;
      }
      if (reader.hasNext()) {
        reader.next();
      }
    }
    return choose;
  }
  
  private static ABaseTag xml2When()
    throws XMLStreamException
  {
    ABaseTag i = null;
    Boolean flag = Boolean.valueOf(true);
    while ((reader.hasNext()) && (flag.booleanValue()))
    {
      switch (reader.getEventType())
      {
      case 1: 
        String name = reader.getLocalName();
        if (name.equals("Verify"))
        {
          i = xml2Verify();
          flag = Boolean.valueOf(false);
        }
        else if (name.equals("Assert"))
        {
          i = xml2Assert();
          flag = Boolean.valueOf(false);
        }
        break;
      case 2: 
        if (reader.getLocalName().equals("When")) {
          flag = Boolean.valueOf(false);
        }
        break;
      }
      reader.next();
    }
    return i;
  }
  
  private static List<ABaseTag> xml2Do()
    throws XMLStreamException
  {
    List<ABaseTag> chooseDos = new ArrayList();
    Boolean flag = Boolean.valueOf(true);
    while ((reader.hasNext()) && (flag.booleanValue()))
    {
      switch (reader.getEventType())
      {
      case 1: 
        String name = reader.getLocalName();
        if (name.equals("Html")) {
          chooseDos.add(xml2Html());
        } else if (name.equals("Verify")) {
          chooseDos.add(xml2Verify());
        } else if (name.equals("Assert")) {
          chooseDos.add(xml2Assert());
        }
        break;
      case 2: 
        if (reader.getLocalName().equals("Do")) {
          flag = Boolean.valueOf(false);
        }
        break;
      }
      reader.next();
    }
    return chooseDos;
  }
  
  private static List<ABaseTag> xml2Otherwise()
    throws XMLStreamException
  {
    List<ABaseTag> otherWise = new ArrayList();
    Boolean flag = Boolean.valueOf(true);
    while ((reader.hasNext()) && (flag.booleanValue()))
    {
      switch (reader.getEventType())
      {
      case 1: 
        String name = reader.getLocalName();
        if (name.equals("Html")) {
          otherWise.add(xml2Html());
        } else if (name.equals("Verify")) {
          otherWise.add(xml2Verify());
        } else if (name.equals("Assert")) {
          otherWise.add(xml2Assert());
        }
        break;
      case 2: 
        if (reader.getLocalName().equals("Otherwise")) {
          flag = Boolean.valueOf(false);
        }
        break;
      }
      reader.next();
    }
    return otherWise;
  }
}
