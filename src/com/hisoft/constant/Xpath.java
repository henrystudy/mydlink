package com.hisoft.constant;

import com.hisoft.entity.Environment;

public final class Xpath
{
  public static final String FW_TITLE = "//div[@id='div_fwUpgrade']";
  public static final String FW_BUTTON = "//div[@id='div_fwUpgrade']/div[@class='btnw floatl']";
  public static final String CONNECTION_ERROR = "//div[@id='win_ConnError']";
  public static final String OFFLINE = "//div[@id='win_content']/div/h3";
  public static final String MYDLINK_TABBOX = "//div[@id='mydlinkTabBox']";
  public static final String REPLACE = "${&}";
  public static final String JS_XPATH = "/info/targets/js[@name='${&}']";
  public static final String XPATH_XPATH = "/info/targets/xpath[@name='${&}']";
  public static final String PAGEPATH_XPATH = "/info/targets/pagePath[@name='${&}']";
  public static final String IMAGE_XPATH = "/info/images/image[@name='${&}']";
  public static final String MESSAGE_XPATH = "/info/messages/message[@name='${&}']/content[@language='" + Environment.getInstance().getLanguage() + "']";
  public static final String COLOR_XPATH = "/info/colors/color[@name='${&}']";
  public static final String FONT_XPATH = "/info/fonts/font[@name='${&}']";
  public static final String EMAILINFO_XPATH = "/info/emailEntitys/emailEntity[@name='${&}']";
  public static final String PWD_VERIFICATION = "//div[@id='win_content']/div[@style]/p[@style]";
}
