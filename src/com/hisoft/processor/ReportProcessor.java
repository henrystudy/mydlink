package com.hisoft.processor;

import com.hisoft.entity.ABaseTag;
import com.hisoft.entity.Assert;
import com.hisoft.entity.Choose;
import com.hisoft.entity.Environment;
import com.hisoft.entity.Function;
import com.hisoft.entity.Html;
import com.hisoft.entity.Import;
import com.hisoft.entity.TestCase;
import com.hisoft.entity.Verify;
import com.hisoft.type.ResultEnum;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class ReportProcessor
{
  Logger logger = Logger.getLogger(ReportProcessor.class);
  private static String htmlHead = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">";
  
  public void XMLAndHTMLReport(List<TestCase> testcaseList)
  {
    writeHtmlFile(XMLReport(testcaseList), "schema/StyleXml.xsl");
  }
  
  private String XMLReport(List<TestCase> testcaseList)
  {
    int pass = 0;
    int fail = 0;
    int skip = 0;
    long time = 0L;
    long totalTime = 0L;
    int totalPass = 0;
    int totalFail = 0;
    int totalSkip = 0;
    int casePassed = 0;
    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String pathXml = "output/" + df.format(new Date()) + ".xml";
    this.logger.info("create XML report('" + pathXml + "').");
    Environment environment = Environment.getInstance();
    try
    {
      if (!new File("~/output").isDirectory()) {
        new File("output").mkdir();
      }
    }
    catch (SecurityException e)
    {
      this.logger.error(e.getMessage(), e);
    }
    try
    {
      PrintWriter writerXml = new PrintWriter(new OutputStreamWriter(
        new FileOutputStream(pathXml), "utf-8"));
      XMLOutputFactory xof = XMLOutputFactory.newInstance();
      XMLStreamWriter xmlsw = xof.createXMLStreamWriter(writerXml);
      xmlsw.writeStartDocument("utf-8", "1.0");
      
      xmlsw.writeStartElement("Report");
      Environment2XML(xmlsw, environment);
      xmlsw.writeStartElement("TestCases");
      for (TestCase tc : testcaseList)
      {
        ResultEnum re = null;
        xmlsw.writeStartElement("TestCase");
        xmlsw.writeAttribute("fileName", tc.getFileName());
        xmlsw.writeAttribute("testCaseName", 
          tc.getTestCaseName());
        xmlsw.writeAttribute("testCaseVersion", 
          tc.getTestCaseVersion());
        xmlsw.writeAttribute("author", 
          tc.getLastUpdatedAuthor());
        xmlsw.writeAttribute("date", 
          tc.getLastUpdatedDate());
        for (Object o : tc.getOperateList())
        {
          if ((o instanceof Html))
          {
            Html2XML(xmlsw, (Html)o);
            re = ((Html)o).getResult();
          }
          else if ((o instanceof Function))
          {
            Function2XML(xmlsw, (Function)o);
            re = ((Function)o).getResult();
          }
          else if ((o instanceof Verify))
          {
            Verify2XML(xmlsw, (Verify)o);
            re = ((Verify)o).getResult();
          }
          else if ((o instanceof Assert))
          {
            Assert2XML(xmlsw, (Assert)o);
            re = ((Assert)o).getResult();
          }
          else if ((o instanceof Choose))
          {
            Choose2XML(xmlsw, (Choose)o);
            re = ((Choose)o).getResult();
          }
          else if ((o instanceof Import))
          {
            Import2XML(xmlsw, (Import)o);
            re = ((Import)o).getResult();
          }
          if (re.equals(ResultEnum.PASS)) {
            pass++;
          } else if (re.equals(ResultEnum.FAIL)) {
            fail++;
          } else {
            skip++;
          }
        }
        time = tc.getTime() / 1000L;
        totalPass += pass;
        totalFail += fail;
        totalSkip += skip;
        totalTime += time;
        
        xmlsw.writeStartElement("TestcaseResult");
        xmlsw.writeAttribute("fileName", tc.getFileName());
        xmlsw.writeAttribute("passed", pass);
        xmlsw.writeAttribute("failed", fail);
        xmlsw.writeAttribute("skipped", skip);
        xmlsw.writeAttribute("totaltime", time);
        xmlsw.writeEndElement();
        casePassed += pass / tc.getOperateList().size();
        pass = 0;
        fail = 0;
        skip = 0;
        time = 0L;
        xmlsw.writeEndElement();
      }
      xmlsw.writeEndElement();
      xmlsw.writeStartElement("TestcaseResult");
      xmlsw.writeAttribute("passed", totalPass);
      xmlsw.writeAttribute("failed", totalFail);
      xmlsw.writeAttribute("skipped", totalSkip);
      xmlsw.writeAttribute("totaltime", totalTime);
      xmlsw.writeAttribute("result", casePassed + " / " + 
        testcaseList.size());
      xmlsw.writeEndElement();
      xmlsw.writeEndElement();
      xmlsw.flush();
      xmlsw.close();
    }
    catch (UnsupportedEncodingException e)
    {
      this.logger.error(e.getMessage(), e);
    }
    catch (FileNotFoundException e)
    {
      this.logger.error(e.getMessage(), e);
    }
    catch (FactoryConfigurationError e)
    {
      this.logger.error(e.getMessage(), e);
    }
    catch (XMLStreamException e)
    {
      this.logger.error(e.getMessage(), e);
    }
    formatXml(pathXml);
    return pathXml;
  }
  
  public void formatXml(String pathXml)
  {
    this.logger.info("format XML report('" + pathXml + "').");
    try
    {
      SAXReader saxReader = new SAXReader();
      
      Document doc = saxReader
        .read(new FileInputStream(new File(pathXml)));
      
      OutputFormat format = OutputFormat.createPrettyPrint();
      
      format.setEncoding("UTF-8");
      
      XMLWriter writer = new XMLWriter(new FileWriter(new File(pathXml)), 
        format);
      
      writer.write(doc);
      writer.close();
    }
    catch (Exception ex)
    {
      this.logger.error(ex.getMessage(), ex);
    }
  }
  
  /* Error */
  public void writeHtmlFile(String xmlpath, String sxlpath)
  {
    // Byte code:
    //   0: new 93	java/io/File
    //   3: dup
    //   4: aload_1
    //   5: ldc 73
    //   7: ldc_w 409
    //   10: invokevirtual 411	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   13: invokespecial 97	java/io/File:<init>	(Ljava/lang/String;)V
    //   16: astore_3
    //   17: aload_0
    //   18: getfield 27	com/hisoft/processor/ReportProcessor:logger	Lorg/apache/log4j/Logger;
    //   21: new 57	java/lang/StringBuilder
    //   24: dup
    //   25: ldc_w 415
    //   28: invokespecial 61	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   31: aload_3
    //   32: invokevirtual 417	java/io/File:getPath	()Ljava/lang/String;
    //   35: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   38: ldc_w 420
    //   41: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   44: invokevirtual 75	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   47: invokevirtual 83	org/apache/log4j/Logger:info	(Ljava/lang/Object;)V
    //   50: new 93	java/io/File
    //   53: dup
    //   54: aload_1
    //   55: invokespecial 97	java/io/File:<init>	(Ljava/lang/String;)V
    //   58: astore 4
    //   60: new 93	java/io/File
    //   63: dup
    //   64: aload_2
    //   65: invokespecial 97	java/io/File:<init>	(Ljava/lang/String;)V
    //   68: astore 5
    //   70: aconst_null
    //   71: astore 6
    //   73: aconst_null
    //   74: astore 7
    //   76: aload 4
    //   78: invokestatic 422	com/hisoft/xml/SchemaChecker:fileExists	(Ljava/io/File;)Z
    //   81: ifeq +555 -> 636
    //   84: aload 5
    //   86: invokestatic 422	com/hisoft/xml/SchemaChecker:fileExists	(Ljava/io/File;)Z
    //   89: ifeq +547 -> 636
    //   92: aload 4
    //   94: invokestatic 428	com/hisoft/xml/SchemaChecker:validateOnlyXml	(Ljava/io/File;)Z
    //   97: ifeq +539 -> 636
    //   100: invokestatic 431	javax/xml/parsers/DocumentBuilderFactory:newInstance	()Ljavax/xml/parsers/DocumentBuilderFactory;
    //   103: astore 8
    //   105: aload 8
    //   107: invokevirtual 436	javax/xml/parsers/DocumentBuilderFactory:newDocumentBuilder	()Ljavax/xml/parsers/DocumentBuilder;
    //   110: astore 9
    //   112: aload 9
    //   114: aload_1
    //   115: invokevirtual 440	javax/xml/parsers/DocumentBuilder:parse	(Ljava/lang/String;)Lorg/w3c/dom/Document;
    //   118: astore 10
    //   120: new 446	javax/xml/transform/dom/DOMSource
    //   123: dup
    //   124: aload 10
    //   126: invokespecial 448	javax/xml/transform/dom/DOMSource:<init>	(Lorg/w3c/dom/Node;)V
    //   129: astore 11
    //   131: new 451	java/io/StringWriter
    //   134: dup
    //   135: invokespecial 453	java/io/StringWriter:<init>	()V
    //   138: astore 7
    //   140: new 454	javax/xml/transform/stream/StreamResult
    //   143: dup
    //   144: aload 7
    //   146: invokespecial 456	javax/xml/transform/stream/StreamResult:<init>	(Ljava/io/Writer;)V
    //   149: astore 12
    //   151: new 457	javax/xml/transform/stream/StreamSource
    //   154: dup
    //   155: aload_2
    //   156: invokespecial 459	javax/xml/transform/stream/StreamSource:<init>	(Ljava/lang/String;)V
    //   159: astore 13
    //   161: invokestatic 460	javax/xml/transform/TransformerFactory:newInstance	()Ljavax/xml/transform/TransformerFactory;
    //   164: astore 14
    //   166: aload 14
    //   168: aload 13
    //   170: invokevirtual 465	javax/xml/transform/TransformerFactory:newTransformer	(Ljavax/xml/transform/Source;)Ljavax/xml/transform/Transformer;
    //   173: astore 15
    //   175: aload 15
    //   177: ldc_w 469
    //   180: ldc 123
    //   182: invokevirtual 471	javax/xml/transform/Transformer:setOutputProperty	(Ljava/lang/String;Ljava/lang/String;)V
    //   185: aload 15
    //   187: aload 11
    //   189: aload 12
    //   191: invokevirtual 476	javax/xml/transform/Transformer:transform	(Ljavax/xml/transform/Source;Ljavax/xml/transform/Result;)V
    //   194: new 116	java/io/PrintWriter
    //   197: dup
    //   198: aload_3
    //   199: invokespecial 480	java/io/PrintWriter:<init>	(Ljava/io/File;)V
    //   202: astore 6
    //   204: aload 6
    //   206: new 57	java/lang/StringBuilder
    //   209: dup
    //   210: getstatic 14	com/hisoft/processor/ReportProcessor:htmlHead	Ljava/lang/String;
    //   213: invokestatic 481	java/lang/String:valueOf	(Ljava/lang/Object;)Ljava/lang/String;
    //   216: invokespecial 61	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   219: aload 7
    //   221: invokevirtual 485	java/io/StringWriter:toString	()Ljava/lang/String;
    //   224: ldc_w 486
    //   227: ldc_w 488
    //   230: invokevirtual 490	java/lang/String:replaceAll	(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
    //   233: ldc_w 494
    //   236: ldc_w 496
    //   239: invokevirtual 411	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   242: ldc_w 498
    //   245: ldc_w 500
    //   248: invokevirtual 411	java/lang/String:replace	(Ljava/lang/CharSequence;Ljava/lang/CharSequence;)Ljava/lang/String;
    //   251: invokevirtual 69	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   254: invokevirtual 75	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   257: invokevirtual 502	java/io/PrintWriter:write	(Ljava/lang/String;)V
    //   260: goto +337 -> 597
    //   263: astore 9
    //   265: aload_0
    //   266: getfield 27	com/hisoft/processor/ReportProcessor:logger	Lorg/apache/log4j/Logger;
    //   269: aload 9
    //   271: invokevirtual 504	javax/xml/parsers/ParserConfigurationException:getMessage	()Ljava/lang/String;
    //   274: aload 9
    //   276: invokevirtual 112	org/apache/log4j/Logger:error	(Ljava/lang/Object;Ljava/lang/Throwable;)V
    //   279: aload 6
    //   281: ifnull +8 -> 289
    //   284: aload 6
    //   286: invokevirtual 507	java/io/PrintWriter:close	()V
    //   289: aload 7
    //   291: ifnull +345 -> 636
    //   294: aload 7
    //   296: invokevirtual 508	java/io/StringWriter:close	()V
    //   299: goto +337 -> 636
    //   302: astore 17
    //   304: aload_0
    //   305: getfield 27	com/hisoft/processor/ReportProcessor:logger	Lorg/apache/log4j/Logger;
    //   308: aload 17
    //   310: invokevirtual 509	java/io/IOException:getMessage	()Ljava/lang/String;
    //   313: aload 17
    //   315: invokevirtual 112	org/apache/log4j/Logger:error	(Ljava/lang/Object;Ljava/lang/Throwable;)V
    //   318: goto +318 -> 636
    //   321: astore 9
    //   323: aload_0
    //   324: getfield 27	com/hisoft/processor/ReportProcessor:logger	Lorg/apache/log4j/Logger;
    //   327: aload 9
    //   329: invokevirtual 512	org/xml/sax/SAXException:getMessage	()Ljava/lang/String;
    //   332: aload 9
    //   334: invokevirtual 112	org/apache/log4j/Logger:error	(Ljava/lang/Object;Ljava/lang/Throwable;)V
    //   337: aload 6
    //   339: ifnull +8 -> 347
    //   342: aload 6
    //   344: invokevirtual 507	java/io/PrintWriter:close	()V
    //   347: aload 7
    //   349: ifnull +287 -> 636
    //   352: aload 7
    //   354: invokevirtual 508	java/io/StringWriter:close	()V
    //   357: goto +279 -> 636
    //   360: astore 17
    //   362: aload_0
    //   363: getfield 27	com/hisoft/processor/ReportProcessor:logger	Lorg/apache/log4j/Logger;
    //   366: aload 17
    //   368: invokevirtual 509	java/io/IOException:getMessage	()Ljava/lang/String;
    //   371: aload 17
    //   373: invokevirtual 112	org/apache/log4j/Logger:error	(Ljava/lang/Object;Ljava/lang/Throwable;)V
    //   376: goto +260 -> 636
    //   379: astore 9
    //   381: aload_0
    //   382: getfield 27	com/hisoft/processor/ReportProcessor:logger	Lorg/apache/log4j/Logger;
    //   385: aload 9
    //   387: invokevirtual 509	java/io/IOException:getMessage	()Ljava/lang/String;
    //   390: aload 9
    //   392: invokevirtual 112	org/apache/log4j/Logger:error	(Ljava/lang/Object;Ljava/lang/Throwable;)V
    //   395: aload 6
    //   397: ifnull +8 -> 405
    //   400: aload 6
    //   402: invokevirtual 507	java/io/PrintWriter:close	()V
    //   405: aload 7
    //   407: ifnull +229 -> 636
    //   410: aload 7
    //   412: invokevirtual 508	java/io/StringWriter:close	()V
    //   415: goto +221 -> 636
    //   418: astore 17
    //   420: aload_0
    //   421: getfield 27	com/hisoft/processor/ReportProcessor:logger	Lorg/apache/log4j/Logger;
    //   424: aload 17
    //   426: invokevirtual 509	java/io/IOException:getMessage	()Ljava/lang/String;
    //   429: aload 17
    //   431: invokevirtual 112	org/apache/log4j/Logger:error	(Ljava/lang/Object;Ljava/lang/Throwable;)V
    //   434: goto +202 -> 636
    //   437: astore 9
    //   439: aload_0
    //   440: getfield 27	com/hisoft/processor/ReportProcessor:logger	Lorg/apache/log4j/Logger;
    //   443: aload 9
    //   445: invokevirtual 515	javax/xml/transform/TransformerConfigurationException:getMessage	()Ljava/lang/String;
    //   448: aload 9
    //   450: invokevirtual 112	org/apache/log4j/Logger:error	(Ljava/lang/Object;Ljava/lang/Throwable;)V
    //   453: aload 6
    //   455: ifnull +8 -> 463
    //   458: aload 6
    //   460: invokevirtual 507	java/io/PrintWriter:close	()V
    //   463: aload 7
    //   465: ifnull +171 -> 636
    //   468: aload 7
    //   470: invokevirtual 508	java/io/StringWriter:close	()V
    //   473: goto +163 -> 636
    //   476: astore 17
    //   478: aload_0
    //   479: getfield 27	com/hisoft/processor/ReportProcessor:logger	Lorg/apache/log4j/Logger;
    //   482: aload 17
    //   484: invokevirtual 509	java/io/IOException:getMessage	()Ljava/lang/String;
    //   487: aload 17
    //   489: invokevirtual 112	org/apache/log4j/Logger:error	(Ljava/lang/Object;Ljava/lang/Throwable;)V
    //   492: goto +144 -> 636
    //   495: astore 9
    //   497: aload_0
    //   498: getfield 27	com/hisoft/processor/ReportProcessor:logger	Lorg/apache/log4j/Logger;
    //   501: aload 9
    //   503: invokevirtual 518	javax/xml/transform/TransformerException:getMessage	()Ljava/lang/String;
    //   506: aload 9
    //   508: invokevirtual 112	org/apache/log4j/Logger:error	(Ljava/lang/Object;Ljava/lang/Throwable;)V
    //   511: aload 6
    //   513: ifnull +8 -> 521
    //   516: aload 6
    //   518: invokevirtual 507	java/io/PrintWriter:close	()V
    //   521: aload 7
    //   523: ifnull +113 -> 636
    //   526: aload 7
    //   528: invokevirtual 508	java/io/StringWriter:close	()V
    //   531: goto +105 -> 636
    //   534: astore 17
    //   536: aload_0
    //   537: getfield 27	com/hisoft/processor/ReportProcessor:logger	Lorg/apache/log4j/Logger;
    //   540: aload 17
    //   542: invokevirtual 509	java/io/IOException:getMessage	()Ljava/lang/String;
    //   545: aload 17
    //   547: invokevirtual 112	org/apache/log4j/Logger:error	(Ljava/lang/Object;Ljava/lang/Throwable;)V
    //   550: goto +86 -> 636
    //   553: astore 16
    //   555: aload 6
    //   557: ifnull +8 -> 565
    //   560: aload 6
    //   562: invokevirtual 507	java/io/PrintWriter:close	()V
    //   565: aload 7
    //   567: ifnull +27 -> 594
    //   570: aload 7
    //   572: invokevirtual 508	java/io/StringWriter:close	()V
    //   575: goto +19 -> 594
    //   578: astore 17
    //   580: aload_0
    //   581: getfield 27	com/hisoft/processor/ReportProcessor:logger	Lorg/apache/log4j/Logger;
    //   584: aload 17
    //   586: invokevirtual 509	java/io/IOException:getMessage	()Ljava/lang/String;
    //   589: aload 17
    //   591: invokevirtual 112	org/apache/log4j/Logger:error	(Ljava/lang/Object;Ljava/lang/Throwable;)V
    //   594: aload 16
    //   596: athrow
    //   597: aload 6
    //   599: ifnull +8 -> 607
    //   602: aload 6
    //   604: invokevirtual 507	java/io/PrintWriter:close	()V
    //   607: aload 7
    //   609: ifnull +27 -> 636
    //   612: aload 7
    //   614: invokevirtual 508	java/io/StringWriter:close	()V
    //   617: goto +19 -> 636
    //   620: astore 17
    //   622: aload_0
    //   623: getfield 27	com/hisoft/processor/ReportProcessor:logger	Lorg/apache/log4j/Logger;
    //   626: aload 17
    //   628: invokevirtual 509	java/io/IOException:getMessage	()Ljava/lang/String;
    //   631: aload 17
    //   633: invokevirtual 112	org/apache/log4j/Logger:error	(Ljava/lang/Object;Ljava/lang/Throwable;)V
    //   636: return
    // Line number table:
    //   Java source line #231	-> byte code offset #0
    //   Java source line #232	-> byte code offset #17
    //   Java source line #233	-> byte code offset #50
    //   Java source line #234	-> byte code offset #60
    //   Java source line #235	-> byte code offset #70
    //   Java source line #236	-> byte code offset #73
    //   Java source line #237	-> byte code offset #76
    //   Java source line #238	-> byte code offset #84
    //   Java source line #239	-> byte code offset #92
    //   Java source line #240	-> byte code offset #100
    //   Java source line #243	-> byte code offset #105
    //   Java source line #244	-> byte code offset #112
    //   Java source line #245	-> byte code offset #120
    //   Java source line #246	-> byte code offset #131
    //   Java source line #247	-> byte code offset #140
    //   Java source line #249	-> byte code offset #151
    //   Java source line #250	-> byte code offset #161
    //   Java source line #251	-> byte code offset #166
    //   Java source line #252	-> byte code offset #175
    //   Java source line #254	-> byte code offset #185
    //   Java source line #256	-> byte code offset #194
    //   Java source line #258	-> byte code offset #204
    //   Java source line #259	-> byte code offset #219
    //   Java source line #260	-> byte code offset #233
    //   Java source line #259	-> byte code offset #251
    //   Java source line #258	-> byte code offset #257
    //   Java source line #261	-> byte code offset #263
    //   Java source line #262	-> byte code offset #265
    //   Java source line #273	-> byte code offset #279
    //   Java source line #274	-> byte code offset #284
    //   Java source line #276	-> byte code offset #289
    //   Java source line #277	-> byte code offset #294
    //   Java source line #279	-> byte code offset #302
    //   Java source line #280	-> byte code offset #304
    //   Java source line #263	-> byte code offset #321
    //   Java source line #264	-> byte code offset #323
    //   Java source line #273	-> byte code offset #337
    //   Java source line #274	-> byte code offset #342
    //   Java source line #276	-> byte code offset #347
    //   Java source line #277	-> byte code offset #352
    //   Java source line #279	-> byte code offset #360
    //   Java source line #280	-> byte code offset #362
    //   Java source line #265	-> byte code offset #379
    //   Java source line #266	-> byte code offset #381
    //   Java source line #273	-> byte code offset #395
    //   Java source line #274	-> byte code offset #400
    //   Java source line #276	-> byte code offset #405
    //   Java source line #277	-> byte code offset #410
    //   Java source line #279	-> byte code offset #418
    //   Java source line #280	-> byte code offset #420
    //   Java source line #267	-> byte code offset #437
    //   Java source line #268	-> byte code offset #439
    //   Java source line #273	-> byte code offset #453
    //   Java source line #274	-> byte code offset #458
    //   Java source line #276	-> byte code offset #463
    //   Java source line #277	-> byte code offset #468
    //   Java source line #279	-> byte code offset #476
    //   Java source line #280	-> byte code offset #478
    //   Java source line #269	-> byte code offset #495
    //   Java source line #270	-> byte code offset #497
    //   Java source line #273	-> byte code offset #511
    //   Java source line #274	-> byte code offset #516
    //   Java source line #276	-> byte code offset #521
    //   Java source line #277	-> byte code offset #526
    //   Java source line #279	-> byte code offset #534
    //   Java source line #280	-> byte code offset #536
    //   Java source line #271	-> byte code offset #553
    //   Java source line #273	-> byte code offset #555
    //   Java source line #274	-> byte code offset #560
    //   Java source line #276	-> byte code offset #565
    //   Java source line #277	-> byte code offset #570
    //   Java source line #279	-> byte code offset #578
    //   Java source line #280	-> byte code offset #580
    //   Java source line #282	-> byte code offset #594
    //   Java source line #273	-> byte code offset #597
    //   Java source line #274	-> byte code offset #602
    //   Java source line #276	-> byte code offset #607
    //   Java source line #277	-> byte code offset #612
    //   Java source line #279	-> byte code offset #620
    //   Java source line #280	-> byte code offset #622
    //   Java source line #284	-> byte code offset #636
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	637	0	this	ReportProcessor
    //   0	637	1	xmlpath	String
    //   0	637	2	sxlpath	String
    //   16	183	3	html	File
    //   58	35	4	xmlFile	File
    //   68	17	5	sxlFile	File
    //   71	532	6	pw	PrintWriter
    //   74	539	7	sw	java.io.StringWriter
    //   103	3	8	dbf	javax.xml.parsers.DocumentBuilderFactory
    //   110	3	9	db	javax.xml.parsers.DocumentBuilder
    //   263	12	9	e	javax.xml.parsers.ParserConfigurationException
    //   321	12	9	e	org.xml.sax.SAXException
    //   379	12	9	e	java.io.IOException
    //   437	12	9	e	javax.xml.transform.TransformerConfigurationException
    //   495	12	9	e	javax.xml.transform.TransformerException
    //   118	7	10	doc	org.w3c.dom.Document
    //   129	59	11	source	javax.xml.transform.dom.DOMSource
    //   149	41	12	resulted	javax.xml.transform.Result
    //   159	10	13	ss	javax.xml.transform.stream.StreamSource
    //   164	3	14	tff	javax.xml.transform.TransformerFactory
    //   173	13	15	tf	javax.xml.transform.Transformer
    //   553	42	16	localObject	Object
    //   302	12	17	e	java.io.IOException
    //   360	12	17	e	java.io.IOException
    //   418	12	17	e	java.io.IOException
    //   476	12	17	e	java.io.IOException
    //   534	12	17	e	java.io.IOException
    //   578	12	17	e	java.io.IOException
    //   620	12	17	e	java.io.IOException
    // Exception table:
    //   from	to	target	type
    //   105	260	263	javax/xml/parsers/ParserConfigurationException
    //   279	299	302	java/io/IOException
    //   105	260	321	org/xml/sax/SAXException
    //   337	357	360	java/io/IOException
    //   105	260	379	java/io/IOException
    //   395	415	418	java/io/IOException
    //   105	260	437	javax/xml/transform/TransformerConfigurationException
    //   453	473	476	java/io/IOException
    //   105	260	495	javax/xml/transform/TransformerException
    //   511	531	534	java/io/IOException
    //   105	279	553	finally
    //   321	337	553	finally
    //   379	395	553	finally
    //   437	453	553	finally
    //   495	511	553	finally
    //   555	575	578	java/io/IOException
    //   597	617	620	java/io/IOException
  }
  
  private void Environment2XML(XMLStreamWriter xmlsw, Environment environment)
    throws XMLStreamException
  {
    SimpleDateFormat tempDate = new SimpleDateFormat("yyyy/MM/dd a", 
      Locale.ENGLISH);
    String datetime = tempDate.format(new Date());
    xmlsw.writeStartElement("Configurations");
    xmlsw.writeStartElement("Time");
    xmlsw.writeCharacters("(GMT+8) " + datetime);
    xmlsw.writeEndElement();
    xmlsw.writeStartElement("OS");
    xmlsw.writeCharacters(environment.getOs());
    xmlsw.writeEndElement();
    xmlsw.writeStartElement("Host");
    xmlsw.writeCharacters(environment.getHost());
    xmlsw.writeEndElement();
    xmlsw.writeStartElement("Port");
    xmlsw.writeCharacters(environment.getPort());
    xmlsw.writeEndElement();
    xmlsw.writeStartElement("BaseUrl");
    xmlsw.writeCharacters(environment.getBaseUrl());
    xmlsw.writeEndElement();
    xmlsw.writeStartElement("Browser");
    xmlsw.writeCharacters(environment.getBrowser());
    xmlsw.writeEndElement();
    xmlsw.writeStartElement("Language");
    xmlsw.writeCharacters(environment.getLanguage());
    xmlsw.writeEndElement();
    xmlsw.writeStartElement("Timeout");
    xmlsw.writeCharacters(environment.getTimeout());
    xmlsw.writeEndElement();
    xmlsw.writeStartElement("RetryTimes");
    xmlsw.writeCharacters(environment.getRetryTimes());
    xmlsw.writeEndElement();
    xmlsw.writeStartElement("PageDataPath");
    xmlsw.writeCharacters(environment.getPageDataPath());
    xmlsw.writeEndElement();
    xmlsw.writeStartElement("TestCasePath");
    xmlsw.writeCharacters(environment.getTestCasePath());
    xmlsw.writeEndElement();
    xmlsw.writeEndElement();
  }
  
  private void Html2XML(XMLStreamWriter xmlsw, Html html)
    throws XMLStreamException
  {
    xmlsw.writeStartElement("Tag");
    xmlsw.writeAttribute("Count", "1");
    xmlsw.writeAttribute("name", "Html");
    xmlsw.writeAttribute("lineNumber", html.getLineNumb());
    xmlsw.writeStartElement("OperateInfo");
    xmlsw.writeAttribute("name", "Html");
    xmlsw.writeAttribute("operate", html.toString());
    xmlsw.writeAttribute("result", html.getResult());
    xmlsw.writeAttribute("resultInfo", html.getReportInfo());
    xmlsw.writeAttribute("screenshot", html.getScreenshot());
    xmlsw.writeEndElement();
    xmlsw.writeEndElement();
  }
  
  private void Function2XML(XMLStreamWriter xmlsw, Function function)
    throws XMLStreamException
  {
    xmlsw.writeStartElement("Tag");
    xmlsw.writeAttribute("Count", "1");
    xmlsw.writeAttribute("name", "Function");
    xmlsw.writeAttribute("lineNumber", 
      function.getLineNumb());
    xmlsw.writeStartElement("OperateInfo");
    xmlsw.writeAttribute("name", "Function");
    xmlsw.writeAttribute("operate", function.toString());
    xmlsw.writeAttribute("result", function.getResult());
    xmlsw.writeAttribute("resultInfo", 
      function.getReportInfo());
    xmlsw.writeAttribute("screenshot", 
      function.getScreenshot());
    xmlsw.writeEndElement();
    xmlsw.writeEndElement();
  }
  
  private void Verify2XML(XMLStreamWriter xmlsw, Verify verify)
    throws XMLStreamException
  {
    xmlsw.writeStartElement("Tag");
    xmlsw.writeAttribute("Count", "1");
    xmlsw.writeAttribute("name", "Verify");
    xmlsw.writeAttribute("lineNumber", 
      verify.getLineNumb());
    xmlsw.writeStartElement("OperateInfo");
    xmlsw.writeAttribute("name", "Verify");
    xmlsw.writeAttribute("operate", verify.toString());
    xmlsw.writeAttribute("result", verify.getResult());
    xmlsw.writeAttribute("resultInfo", verify.getReportInfo());
    xmlsw.writeAttribute("screenshot", verify.getScreenshot());
    xmlsw.writeEndElement();
    xmlsw.writeEndElement();
  }
  
  private void Assert2XML(XMLStreamWriter xmlsw, Assert as)
    throws XMLStreamException
  {
    xmlsw.writeStartElement("Tag");
    xmlsw.writeAttribute("Count", "1");
    xmlsw.writeAttribute("name", "Assert");
    xmlsw.writeAttribute("lineNumber", as.getLineNumb());
    xmlsw.writeStartElement("OperateInfo");
    xmlsw.writeAttribute("name", "Assert");
    xmlsw.writeAttribute("operate", as.toString());
    xmlsw.writeAttribute("result", as.getResult());
    xmlsw.writeAttribute("resultInfo", as.getReportInfo());
    xmlsw.writeAttribute("screenshot", as.getScreenshot());
    xmlsw.writeEndElement();
    xmlsw.writeEndElement();
  }
  
  private void Do2XML(XMLStreamWriter xmlsw, List<ABaseTag> list)
    throws XMLStreamException
  {
    for (ABaseTag abt : list)
    {
      xmlsw.writeStartElement("OperateInfo");
      xmlsw.writeAttribute("name", "Do");
      xmlsw.writeAttribute("operate", abt.toString());
      xmlsw.writeAttribute("result", abt.getResult());
      xmlsw.writeAttribute("lineNumber", 
        abt.getLineNumb());
      xmlsw.writeAttribute("resultInfo", abt.getReportInfo());
      xmlsw.writeAttribute("screenshot", abt.getScreenshot());
      xmlsw.writeEndElement();
    }
  }
  
  private void Otherwise2XML(XMLStreamWriter xmlsw, List<ABaseTag> list)
    throws XMLStreamException
  {
    for (ABaseTag abt : list)
    {
      xmlsw.writeStartElement("OperateInfo");
      xmlsw.writeAttribute("name", "Otherwise");
      xmlsw.writeAttribute("operate", abt.toString());
      xmlsw.writeAttribute("result", abt.getResult());
      xmlsw.writeAttribute("lineNumber", 
        abt.getLineNumb());
      xmlsw.writeAttribute("resultInfo", abt.getReportInfo());
      xmlsw.writeAttribute("screenshot", abt.getScreenshot());
      xmlsw.writeEndElement();
    }
  }
  
  private void When2XML(XMLStreamWriter xmlsw, ABaseTag abt)
    throws XMLStreamException
  {
    xmlsw.writeStartElement("OperateInfo");
    xmlsw.writeAttribute("name", "When");
    xmlsw.writeAttribute("operate", abt.toString());
    xmlsw.writeAttribute("result", abt.getResult());
    xmlsw.writeAttribute("lineNumber", abt.getLineNumb());
    xmlsw.writeAttribute("resultInfo", abt.getReportInfo());
    xmlsw.writeAttribute("screenshot", abt.getScreenshot());
    xmlsw.writeEndElement();
  }
  
  private void Choose2XML(XMLStreamWriter xmlsw, Choose choose)
    throws XMLStreamException
  {
    xmlsw.writeStartElement("Tag");
    xmlsw.writeAttribute("Count", 
      1 + choose.getDo().size() + choose.getOtherwise().size());
    xmlsw.writeAttribute("name", "Choose");
    xmlsw.writeAttribute("lineNumber", 
      choose.getLineNumb());
    When2XML(xmlsw, choose.getWhen());
    Do2XML(xmlsw, choose.getDo());
    Otherwise2XML(xmlsw, choose.getOtherwise());
    xmlsw.writeEndElement();
  }
  
  private void Import2XML(XMLStreamWriter xmlsw, Import o)
    throws XMLStreamException
  {
    xmlsw.writeStartElement("Tag");
    xmlsw.writeAttribute("Count", 
      o.getImportXML().size());
    xmlsw.writeAttribute("name", "ImportXML");
    xmlsw.writeAttribute("lineNumber", o.getLineNumb());
    for (ABaseTag abt : o.getImportXML())
    {
      xmlsw.writeStartElement("OperateInfo");
      if ((abt instanceof Html)) {
        xmlsw.writeAttribute("name", "Html");
      } else if ((abt instanceof Verify)) {
        xmlsw.writeAttribute("name", "Verify");
      } else if ((abt instanceof Assert)) {
        xmlsw.writeAttribute("name", "Assert");
      }
      xmlsw.writeAttribute("operate", abt.toString());
      xmlsw.writeAttribute("result", abt.getResult());
      xmlsw.writeAttribute("lineNumber", 
        abt.getLineNumb());
      xmlsw.writeAttribute("resultInfo", abt.getReportInfo());
      xmlsw.writeAttribute("screenshot", abt.getScreenshot());
      xmlsw.writeEndElement();
    }
    xmlsw.writeEndElement();
  }
}
