package com.hisoft.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

public class CommonTool
{
  static Logger logger = Logger.getLogger(CommonTool.class);
  
  public static String getExtensionName(File file)
  {
    String[] strings = file.getName().split("\\.");
    int length = strings.length;
    return strings[(length - 1)];
  }
  
  public static String any2String(Object o)
  {
    if ((o instanceof String)) {
      return (String)o;
    }
    if ((o instanceof Boolean)) {
      return (Boolean)o;
    }
    if ((o instanceof Number)) {
      return (Number)o;
    }
    logger.error("if you see this, please check method 'CommonTool.any2String()'. " + 
      o);
    return null;
  }
  
  public static int String2Int(String str)
  {
    int intTime = 12;
    try
    {
      intTime = Integer.valueOf(str).intValue();
    }
    catch (NumberFormatException e)
    {
      logger.warn(e.getMessage() + ", use default one: 12.");
    }
    return intTime;
  }
  
  public static List<File> readXmlFileList(String path)
  {
    return readFileListAccordingToExtensionName(path, "xml", 
      new ArrayList());
  }
  
  public static List<File> readFileListAccordingToExtensionName(String path, String extensionName, List<File> caseList)
  {
    File file = new File(path);
    if (file.isFile())
    {
      caseList.add(file);
      return caseList;
    }
    File[] files = new File(path).listFiles();
    if (files == null) {
      throw new RuntimeException("The path " + path + 
        " in config is wrong.");
    }
    File[] arrayOfFile1;
    int j = (arrayOfFile1 = files).length;
    for (int i = 0; i < j; i++)
    {
      File testCase = arrayOfFile1[i];
      if (testCase.isDirectory()) {
        readFileListAccordingToExtensionName(testCase.getPath(), 
          extensionName, caseList);
      }
      if (getExtensionName(testCase).equals(extensionName)) {
        caseList.add(testCase);
      }
    }
    return caseList;
  }
  
  public static boolean isEmpty(String str)
  {
    if ((str == null) || (str.isEmpty())) {
      return true;
    }
    return false;
  }
  
  public static String threeToSixBit(String color)
  {
    String expressionThreeBit = "^#[0-f]{3}$";
    
    String rgbResult = color;
    if (matcherColor(expressionThreeBit, color))
    {
      String redBuffer = String.valueOf(color.charAt(1));
      redBuffer = redBuffer + redBuffer;
      String greenBuffer = String.valueOf(color.charAt(2));
      greenBuffer = greenBuffer + greenBuffer;
      String blueBuffer = String.valueOf(color.charAt(3));
      blueBuffer = blueBuffer + blueBuffer;
      rgbResult = "#" + redBuffer + greenBuffer + blueBuffer;
    }
    else if (color.equals("white"))
    {
      rgbResult = "#ffffff";
    }
    else if (color.equals("black"))
    {
      rgbResult = "#000000";
    }
    else if (color.equals("green"))
    {
      rgbResult = "#008000";
    }
    return rgbResult;
  }
  
  public static boolean matcherColor(String expression, String color)
  {
    Pattern p = Pattern.compile(expression, 2);
    Matcher m = p.matcher(color);
    return m.matches();
  }
}
