package com.hisoft.constant;

public final class EvalJS
{
  public static final String EVALGETOSJS = "(function(){var ua=navigator.userAgent;var system;if(ua.indexOf('Windows NT 5.1')>-1){system='Windows XP';}else if(ua.indexOf('Windows NT 6.0')>-1){system='Windows Vista';}else if(ua.indexOf('Windows NT 6.1')>-1){system='Windows 7';}else if(ua.indexOf('Ubuntu')>-1){system='Ubuntu';}else if(ua.indexOf('Mac')>-1){system='Mac';}else{system='Others';}return system;})()";
  public static final String EVALGETBROWSERJS = "(function(){var Sys={};var ua=navigator.userAgent.toLowerCase();var s;var broswer;(s=ua.match(/msie ([\\d.]+)/))?Sys.ie=s[1]:(s=ua.match(/firefox\\/([\\d.]+)/))?Sys.firefox=s[1]:(s=ua.match(/chrome\\/([\\d.]+)/))?Sys.chrome=s[1]:(s=ua.match(/opera.([\\d.]+)/))?Sys.opera=s[1]:(s=ua.match(/version\\/([\\d.]+).*safari/))?Sys.safari=s[1]:0;if(Sys.ie){broswer='IE'}else if(Sys.firefox){broswer='Firefox: '+Sys.firefox}else if(Sys.chrome){broswer='GoogleChrome: '+Sys.chrome}else if(Sys.opera){broswer='Opera: '+Sys.opera}else if(Sys.safari){broswer='Safari: '+Sys.safari}else{broswer='unknow'}return broswer;})()";
  public static final String JQUERYJS = "(function(){return navigator.userAgent;var system;})()";
  public static final String SKIPFIRMWAREJS = "loadIndex();";
  public static final String CONNECTIONINFOJS = "window.jQuery('#mydlink\\\\:debugWin_log_msg').val();";
  public static final String CLEANCONNECTIONINFOJS = "window.jQuery('#mydlink\\\\:debugWin_log_msg').append('newLine');";
}
