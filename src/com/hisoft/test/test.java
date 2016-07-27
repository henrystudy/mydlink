package com.hisoft.test;

import com.hisoft.selenium.SeleniumX;
import com.thoughtworks.selenium.Selenium;

public class test
{
  public static void main(String[] args)
  {
    SeleniumX.startSelenium();
    Selenium se = SeleniumX.getInstance();
    se.open("/");
    SeleniumX.stopSelenium();
  }
}
