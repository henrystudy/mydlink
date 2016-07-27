package com.hisoft.entity;

import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

public class VariableMap
{
  static Logger logger = Logger.getLogger(VariableMap.class);
  private Map<String, String> map = new HashMap();
  private static VariableMap variableMap = null;
  
  public static VariableMap getInstance()
  {
    if (variableMap == null)
    {
      logger.info("initializing VariableMap ...");
      variableMap = new VariableMap();
    }
    return variableMap;
  }
  
  public boolean mapPutKV(String key, String value)
  {
    logger.debug("map.put(" + key + "," + value + ")");
    try
    {
      this.map.put(key, value);
    }
    catch (Exception e)
    {
      logger.error(e.getMessage());
      return false;
    }
    return true;
  }
  
  public boolean mapPutKV(String key, Number value)
  {
    logger.debug("map.put(" + key + "," + value + ")");
    try
    {
      this.map.put(key, value);
    }
    catch (Exception e)
    {
      logger.error(e.getMessage());
      return false;
    }
    return true;
  }
  
  public String mapGetValue(String key)
  {
    if (this.map.containsKey(key)) {
      return (String)this.map.get(key);
    }
    logger.info("map does not contain key '" + key + "', return ''.");
    return "";
  }
}
