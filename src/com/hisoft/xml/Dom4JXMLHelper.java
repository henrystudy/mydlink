package com.hisoft.xml;

import com.hisoft.exception.XpathNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class Dom4JXMLHelper
{
  static Logger logger = Logger.getLogger(Dom4JXMLHelper.class);
  private static SAXReader saxReader = new SAXReader();
  
  private static Document read(String filePath)
  {
    try
    {
      return saxReader.read(filePath);
    }
    catch (DocumentException e)
    {
      logger.error(e.getMessage(), e);
    }
    return null;
  }
  
  public static List<?> readNodeList(String xpath, String filePath)
    throws XpathNotFoundException
  {
    List<?> list = read(filePath).selectNodes(xpath);
    if (list.size() == 0) {
      throw new XpathNotFoundException(xpath, filePath);
    }
    return list;
  }
  
  public static List<String> getTextList(String xpath, String filePath)
    throws XpathNotFoundException
  {
    List<?> nodeList = readNodeList(xpath, filePath);
    List<String> msgList = new ArrayList();
    for (Object node : nodeList) {
      msgList.add(((Node)node).getText());
    }
    return msgList;
  }
  
  public static Node readSingleNode(String xpath, String filePath)
  {
    Node node = read(filePath).selectSingleNode(xpath);
    if (node == null)
    {
      XpathNotFoundException e = new XpathNotFoundException(xpath, filePath);
      throw e;
    }
    return node;
  }
}
