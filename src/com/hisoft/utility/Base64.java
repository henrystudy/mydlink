package com.hisoft.utility;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class Base64
{
  public static final int NO_OPTIONS = 0;
  public static final int ENCODE = 1;
  public static final int DECODE = 0;
  public static final int GZIP = 2;
  public static final int DONT_GUNZIP = 4;
  public static final int DO_BREAK_LINES = 8;
  public static final int URL_SAFE = 16;
  public static final int ORDERED = 32;
  private static final int MAX_LINE_LENGTH = 76;
  private static final byte EQUALS_SIGN = 61;
  private static final byte NEW_LINE = 10;
  private static final String PREFERRED_ENCODING = "US-ASCII";
  private static final byte WHITE_SPACE_ENC = -5;
  private static final byte EQUALS_SIGN_ENC = -1;
  private static final byte[] _STANDARD_ALPHABET = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 
    80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 
    105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 
    49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };
  private static final byte[] _STANDARD_DECODABET = { -9, -9, -9, -9, -9, -9, -9, -9, -9, 
  
    -5, -5, 
    -9, -9, 
    -5, 
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, 
    -5, 
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    62, 
    -9, -9, -9, 
    63, 
    52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 
    -9, -9, -9, 
    -1, 
    -9, -9, -9, 
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 
    
    14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 
    
    -9, -9, -9, -9, -9, -9, 
    26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
    
    39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 
    
    -9, -9, -9, -9, -9, 
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9 };
  private static final byte[] _URL_SAFE_ALPHABET = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 
    80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 
    105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 
    49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95 };
  private static final byte[] _URL_SAFE_DECODABET = { -9, -9, -9, -9, -9, -9, -9, -9, -9, 
  
    -5, -5, 
    -9, -9, 
    -5, 
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, 
    -5, 
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    -9, 
    -9, 
    62, 
    -9, 
    -9, 
    52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 
    -9, -9, -9, 
    -1, 
    -9, -9, -9, 
    0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 
    
    14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 
    
    -9, -9, -9, -9, 
    63, 
    -9, 
    26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 
    
    39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 
    
    -9, -9, -9, -9, -9, 
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9 };
  private static final byte[] _ORDERED_ALPHABET = { 45, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 
    69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 
    88, 89, 90, 95, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 
    112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122 };
  private static final byte[] _ORDERED_DECODABET = { -9, -9, -9, -9, -9, -9, -9, -9, -9, 
  
    -5, -5, 
    -9, -9, 
    -5, 
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, 
    -5, 
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    -9, 
    -9, 
    
    0, -9, 
    -9, 
    1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 
    -9, -9, -9, 
    -1, 
    -9, -9, -9, 
    11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 
    
    24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 
    
    -9, -9, -9, -9, 
    37, 
    -9, 
    38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 
    
    51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 
    
    -9, -9, -9, -9, -9, 
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 
    
    -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9 };
  
  private static final byte[] getAlphabet(int options)
  {
    if ((options & 0x10) == 16) {
      return _URL_SAFE_ALPHABET;
    }
    if ((options & 0x20) == 32) {
      return _ORDERED_ALPHABET;
    }
    return _STANDARD_ALPHABET;
  }
  
  private static final byte[] getDecodabet(int options)
  {
    if ((options & 0x10) == 16) {
      return _URL_SAFE_DECODABET;
    }
    if ((options & 0x20) == 32) {
      return _ORDERED_DECODABET;
    }
    return _STANDARD_DECODABET;
  }
  
  private static byte[] encode3to4(byte[] b4, byte[] threeBytes, int numSigBytes, int options)
  {
    encode3to4(threeBytes, 0, numSigBytes, b4, 0, options);
    return b4;
  }
  
  private static byte[] encode3to4(byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset, int options)
  {
    byte[] ALPHABET = getAlphabet(options);
    
    int inBuff = (numSigBytes > 0 ? source[srcOffset] << 24 >>> 8 : 0) | (numSigBytes > 1 ? source[(srcOffset + 1)] << 24 >>> 16 : 0) | (numSigBytes > 2 ? source[(srcOffset + 2)] << 24 >>> 24 : 0);
    switch (numSigBytes)
    {
    case 3: 
      destination[destOffset] = ALPHABET[(inBuff >>> 18)];
      destination[(destOffset + 1)] = ALPHABET[(inBuff >>> 12 & 0x3F)];
      destination[(destOffset + 2)] = ALPHABET[(inBuff >>> 6 & 0x3F)];
      destination[(destOffset + 3)] = ALPHABET[(inBuff & 0x3F)];
      return destination;
    case 2: 
      destination[destOffset] = ALPHABET[(inBuff >>> 18)];
      destination[(destOffset + 1)] = ALPHABET[(inBuff >>> 12 & 0x3F)];
      destination[(destOffset + 2)] = ALPHABET[(inBuff >>> 6 & 0x3F)];
      destination[(destOffset + 3)] = 61;
      return destination;
    case 1: 
      destination[destOffset] = ALPHABET[(inBuff >>> 18)];
      destination[(destOffset + 1)] = ALPHABET[(inBuff >>> 12 & 0x3F)];
      destination[(destOffset + 2)] = 61;
      destination[(destOffset + 3)] = 61;
      return destination;
    }
    return destination;
  }
  
  public static void encode(ByteBuffer raw, ByteBuffer encoded)
  {
    byte[] raw3 = new byte[3];
    byte[] enc4 = new byte[4];
    while (raw.hasRemaining())
    {
      int rem = Math.min(3, raw.remaining());
      raw.get(raw3, 0, rem);
      encode3to4(enc4, raw3, rem, 0);
      encoded.put(enc4);
    }
  }
  
  public static void encode(ByteBuffer raw, CharBuffer encoded)
  {
    byte[] raw3 = new byte[3];
    byte[] enc4 = new byte[4];
    int i;
    for (; raw.hasRemaining(); i < 4)
    {
      int rem = Math.min(3, raw.remaining());
      raw.get(raw3, 0, rem);
      encode3to4(enc4, raw3, rem, 0);
      i = 0; continue;
      encoded.put((char)(enc4[i] & 0xFF));i++;
    }
  }
  
  public static String encodeObject(Serializable serializableObject)
    throws IOException
  {
    return encodeObject(serializableObject, 0);
  }
  
  /* Error */
  public static String encodeObject(Serializable serializableObject, int options)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +13 -> 14
    //   4: new 149	java/lang/NullPointerException
    //   7: dup
    //   8: ldc -105
    //   10: invokespecial 153	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
    //   13: athrow
    //   14: aconst_null
    //   15: astore_2
    //   16: aconst_null
    //   17: astore_3
    //   18: aconst_null
    //   19: astore 4
    //   21: aconst_null
    //   22: astore 5
    //   24: new 156	java/io/ByteArrayOutputStream
    //   27: dup
    //   28: invokespecial 158	java/io/ByteArrayOutputStream:<init>	()V
    //   31: astore_2
    //   32: new 159	com/hisoft/utility/Base64$OutputStream
    //   35: dup
    //   36: aload_2
    //   37: iconst_1
    //   38: iload_1
    //   39: ior
    //   40: invokespecial 161	com/hisoft/utility/Base64$OutputStream:<init>	(Ljava/io/OutputStream;I)V
    //   43: astore_3
    //   44: iload_1
    //   45: iconst_2
    //   46: iand
    //   47: ifeq +27 -> 74
    //   50: new 164	java/util/zip/GZIPOutputStream
    //   53: dup
    //   54: aload_3
    //   55: invokespecial 166	java/util/zip/GZIPOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   58: astore 4
    //   60: new 169	java/io/ObjectOutputStream
    //   63: dup
    //   64: aload 4
    //   66: invokespecial 171	java/io/ObjectOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   69: astore 5
    //   71: goto +13 -> 84
    //   74: new 169	java/io/ObjectOutputStream
    //   77: dup
    //   78: aload_3
    //   79: invokespecial 171	java/io/ObjectOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   82: astore 5
    //   84: aload 5
    //   86: aload_0
    //   87: invokevirtual 172	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
    //   90: goto +51 -> 141
    //   93: astore 6
    //   95: aload 6
    //   97: athrow
    //   98: astore 7
    //   100: aload 5
    //   102: invokevirtual 176	java/io/ObjectOutputStream:close	()V
    //   105: goto +5 -> 110
    //   108: astore 8
    //   110: aload 4
    //   112: invokevirtual 179	java/util/zip/GZIPOutputStream:close	()V
    //   115: goto +5 -> 120
    //   118: astore 8
    //   120: aload_3
    //   121: invokevirtual 180	java/io/OutputStream:close	()V
    //   124: goto +5 -> 129
    //   127: astore 8
    //   129: aload_2
    //   130: invokevirtual 183	java/io/ByteArrayOutputStream:close	()V
    //   133: goto +5 -> 138
    //   136: astore 8
    //   138: aload 7
    //   140: athrow
    //   141: aload 5
    //   143: invokevirtual 176	java/io/ObjectOutputStream:close	()V
    //   146: goto +5 -> 151
    //   149: astore 8
    //   151: aload 4
    //   153: invokevirtual 179	java/util/zip/GZIPOutputStream:close	()V
    //   156: goto +5 -> 161
    //   159: astore 8
    //   161: aload_3
    //   162: invokevirtual 180	java/io/OutputStream:close	()V
    //   165: goto +5 -> 170
    //   168: astore 8
    //   170: aload_2
    //   171: invokevirtual 183	java/io/ByteArrayOutputStream:close	()V
    //   174: goto +5 -> 179
    //   177: astore 8
    //   179: new 184	java/lang/String
    //   182: dup
    //   183: aload_2
    //   184: invokevirtual 186	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   187: ldc 31
    //   189: invokespecial 190	java/lang/String:<init>	([BLjava/lang/String;)V
    //   192: areturn
    //   193: astore 6
    //   195: new 184	java/lang/String
    //   198: dup
    //   199: aload_2
    //   200: invokevirtual 186	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   203: invokespecial 193	java/lang/String:<init>	([B)V
    //   206: areturn
    // Line number table:
    //   Java source line #691	-> byte code offset #0
    //   Java source line #692	-> byte code offset #4
    //   Java source line #696	-> byte code offset #14
    //   Java source line #697	-> byte code offset #16
    //   Java source line #698	-> byte code offset #18
    //   Java source line #699	-> byte code offset #21
    //   Java source line #703	-> byte code offset #24
    //   Java source line #704	-> byte code offset #32
    //   Java source line #705	-> byte code offset #44
    //   Java source line #707	-> byte code offset #50
    //   Java source line #708	-> byte code offset #60
    //   Java source line #711	-> byte code offset #74
    //   Java source line #713	-> byte code offset #84
    //   Java source line #715	-> byte code offset #93
    //   Java source line #718	-> byte code offset #95
    //   Java source line #720	-> byte code offset #98
    //   Java source line #722	-> byte code offset #100
    //   Java source line #723	-> byte code offset #108
    //   Java source line #726	-> byte code offset #110
    //   Java source line #727	-> byte code offset #118
    //   Java source line #730	-> byte code offset #120
    //   Java source line #731	-> byte code offset #127
    //   Java source line #734	-> byte code offset #129
    //   Java source line #735	-> byte code offset #136
    //   Java source line #737	-> byte code offset #138
    //   Java source line #722	-> byte code offset #141
    //   Java source line #723	-> byte code offset #149
    //   Java source line #726	-> byte code offset #151
    //   Java source line #727	-> byte code offset #159
    //   Java source line #730	-> byte code offset #161
    //   Java source line #731	-> byte code offset #168
    //   Java source line #734	-> byte code offset #170
    //   Java source line #735	-> byte code offset #177
    //   Java source line #741	-> byte code offset #179
    //   Java source line #743	-> byte code offset #193
    //   Java source line #745	-> byte code offset #195
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	207	0	serializableObject	Serializable
    //   0	207	1	options	int
    //   15	185	2	baos	java.io.ByteArrayOutputStream
    //   17	145	3	b64os	java.io.OutputStream
    //   19	133	4	gzos	java.util.zip.GZIPOutputStream
    //   22	120	5	oos	java.io.ObjectOutputStream
    //   93	3	6	e	IOException
    //   193	3	6	uue	UnsupportedEncodingException
    //   98	41	7	localObject	Object
    //   108	1	8	localException	Exception
    //   118	1	8	localException1	Exception
    //   127	1	8	localException2	Exception
    //   136	1	8	localException3	Exception
    //   149	1	8	localException4	Exception
    //   159	1	8	localException5	Exception
    //   168	1	8	localException6	Exception
    //   177	1	8	localException7	Exception
    // Exception table:
    //   from	to	target	type
    //   24	90	93	java/io/IOException
    //   24	98	98	finally
    //   100	105	108	java/lang/Exception
    //   110	115	118	java/lang/Exception
    //   120	124	127	java/lang/Exception
    //   129	133	136	java/lang/Exception
    //   141	146	149	java/lang/Exception
    //   151	156	159	java/lang/Exception
    //   161	165	168	java/lang/Exception
    //   170	174	177	java/lang/Exception
    //   179	192	193	java/io/UnsupportedEncodingException
  }
  
  public static String encodeBytes(byte[] source)
  {
    String encoded = null;
    try
    {
      encoded = encodeBytes(source, 0, source.length, 0);
    }
    catch (IOException ex)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError(ex.getMessage());
      }
    }
    assert (encoded != null);
    return encoded;
  }
  
  public static String encodeBytes(byte[] source, int options)
    throws IOException
  {
    return encodeBytes(source, 0, source.length, options);
  }
  
  public static String encodeBytes(byte[] source, int off, int len)
  {
    String encoded = null;
    try
    {
      encoded = encodeBytes(source, off, len, 0);
    }
    catch (IOException ex)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError(ex.getMessage());
      }
    }
    assert (encoded != null);
    return encoded;
  }
  
  public static String encodeBytes(byte[] source, int off, int len, int options)
    throws IOException
  {
    byte[] encoded = encodeBytesToBytes(source, off, len, options);
    try
    {
      return new String(encoded, "US-ASCII");
    }
    catch (UnsupportedEncodingException uue) {}
    return new String(encoded);
  }
  
  public static byte[] encodeBytesToBytes(byte[] source)
  {
    byte[] encoded = (byte[])null;
    try
    {
      encoded = encodeBytesToBytes(source, 0, source.length, 0);
    }
    catch (IOException ex)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError("IOExceptions only come from GZipping, which is turned off: " + ex.getMessage());
      }
    }
    return encoded;
  }
  
  /* Error */
  public static byte[] encodeBytesToBytes(byte[] source, int off, int len, int options)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +13 -> 14
    //   4: new 149	java/lang/NullPointerException
    //   7: dup
    //   8: ldc -4
    //   10: invokespecial 153	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
    //   13: athrow
    //   14: iload_1
    //   15: ifge +28 -> 43
    //   18: new 254	java/lang/IllegalArgumentException
    //   21: dup
    //   22: new 240	java/lang/StringBuilder
    //   25: dup
    //   26: ldc_w 256
    //   29: invokespecial 244	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   32: iload_1
    //   33: invokevirtual 258	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   36: invokevirtual 249	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   39: invokespecial 261	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   42: athrow
    //   43: iload_2
    //   44: ifge +28 -> 72
    //   47: new 254	java/lang/IllegalArgumentException
    //   50: dup
    //   51: new 240	java/lang/StringBuilder
    //   54: dup
    //   55: ldc_w 262
    //   58: invokespecial 244	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   61: iload_2
    //   62: invokevirtual 258	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   65: invokevirtual 249	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   68: invokespecial 261	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   71: athrow
    //   72: iload_1
    //   73: iload_2
    //   74: iadd
    //   75: aload_0
    //   76: arraylength
    //   77: if_icmple +43 -> 120
    //   80: new 254	java/lang/IllegalArgumentException
    //   83: dup
    //   84: ldc_w 264
    //   87: iconst_3
    //   88: anewarray 3	java/lang/Object
    //   91: dup
    //   92: iconst_0
    //   93: iload_1
    //   94: invokestatic 266	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   97: aastore
    //   98: dup
    //   99: iconst_1
    //   100: iload_2
    //   101: invokestatic 266	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   104: aastore
    //   105: dup
    //   106: iconst_2
    //   107: aload_0
    //   108: arraylength
    //   109: invokestatic 266	java/lang/Integer:valueOf	(I)Ljava/lang/Integer;
    //   112: aastore
    //   113: invokestatic 272	java/lang/String:format	(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;
    //   116: invokespecial 261	java/lang/IllegalArgumentException:<init>	(Ljava/lang/String;)V
    //   119: athrow
    //   120: iload_3
    //   121: iconst_2
    //   122: iand
    //   123: ifeq +138 -> 261
    //   126: aconst_null
    //   127: astore 4
    //   129: aconst_null
    //   130: astore 5
    //   132: aconst_null
    //   133: astore 6
    //   135: new 156	java/io/ByteArrayOutputStream
    //   138: dup
    //   139: invokespecial 158	java/io/ByteArrayOutputStream:<init>	()V
    //   142: astore 4
    //   144: new 159	com/hisoft/utility/Base64$OutputStream
    //   147: dup
    //   148: aload 4
    //   150: iconst_1
    //   151: iload_3
    //   152: ior
    //   153: invokespecial 161	com/hisoft/utility/Base64$OutputStream:<init>	(Ljava/io/OutputStream;I)V
    //   156: astore 6
    //   158: new 164	java/util/zip/GZIPOutputStream
    //   161: dup
    //   162: aload 6
    //   164: invokespecial 166	java/util/zip/GZIPOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   167: astore 5
    //   169: aload 5
    //   171: aload_0
    //   172: iload_1
    //   173: iload_2
    //   174: invokevirtual 276	java/util/zip/GZIPOutputStream:write	([BII)V
    //   177: aload 5
    //   179: invokevirtual 179	java/util/zip/GZIPOutputStream:close	()V
    //   182: goto +43 -> 225
    //   185: astore 7
    //   187: aload 7
    //   189: athrow
    //   190: astore 8
    //   192: aload 5
    //   194: invokevirtual 179	java/util/zip/GZIPOutputStream:close	()V
    //   197: goto +5 -> 202
    //   200: astore 9
    //   202: aload 6
    //   204: invokevirtual 280	com/hisoft/utility/Base64$OutputStream:close	()V
    //   207: goto +5 -> 212
    //   210: astore 9
    //   212: aload 4
    //   214: invokevirtual 183	java/io/ByteArrayOutputStream:close	()V
    //   217: goto +5 -> 222
    //   220: astore 9
    //   222: aload 8
    //   224: athrow
    //   225: aload 5
    //   227: invokevirtual 179	java/util/zip/GZIPOutputStream:close	()V
    //   230: goto +5 -> 235
    //   233: astore 9
    //   235: aload 6
    //   237: invokevirtual 280	com/hisoft/utility/Base64$OutputStream:close	()V
    //   240: goto +5 -> 245
    //   243: astore 9
    //   245: aload 4
    //   247: invokevirtual 183	java/io/ByteArrayOutputStream:close	()V
    //   250: goto +5 -> 255
    //   253: astore 9
    //   255: aload 4
    //   257: invokevirtual 186	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   260: areturn
    //   261: iload_3
    //   262: bipush 8
    //   264: iand
    //   265: ifeq +7 -> 272
    //   268: iconst_1
    //   269: goto +4 -> 273
    //   272: iconst_0
    //   273: istore 4
    //   275: iload_2
    //   276: iconst_3
    //   277: idiv
    //   278: iconst_4
    //   279: imul
    //   280: iload_2
    //   281: iconst_3
    //   282: irem
    //   283: ifle +7 -> 290
    //   286: iconst_4
    //   287: goto +4 -> 291
    //   290: iconst_0
    //   291: iadd
    //   292: istore 5
    //   294: iload 4
    //   296: ifeq +13 -> 309
    //   299: iload 5
    //   301: iload 5
    //   303: bipush 76
    //   305: idiv
    //   306: iadd
    //   307: istore 5
    //   309: iload 5
    //   311: newarray <illegal type>
    //   313: astore 6
    //   315: iconst_0
    //   316: istore 7
    //   318: iconst_0
    //   319: istore 8
    //   321: iload_2
    //   322: iconst_2
    //   323: isub
    //   324: istore 9
    //   326: iconst_0
    //   327: istore 10
    //   329: goto +54 -> 383
    //   332: aload_0
    //   333: iload 7
    //   335: iload_1
    //   336: iadd
    //   337: iconst_3
    //   338: aload 6
    //   340: iload 8
    //   342: iload_3
    //   343: invokestatic 83	com/hisoft/utility/Base64:encode3to4	([BII[BII)[B
    //   346: pop
    //   347: iinc 10 4
    //   350: iload 4
    //   352: ifeq +25 -> 377
    //   355: iload 10
    //   357: bipush 76
    //   359: if_icmplt +18 -> 377
    //   362: aload 6
    //   364: iload 8
    //   366: iconst_4
    //   367: iadd
    //   368: bipush 10
    //   370: bastore
    //   371: iinc 8 1
    //   374: iconst_0
    //   375: istore 10
    //   377: iinc 7 3
    //   380: iinc 8 4
    //   383: iload 7
    //   385: iload 9
    //   387: if_icmplt -55 -> 332
    //   390: iload 7
    //   392: iload_2
    //   393: if_icmpge +24 -> 417
    //   396: aload_0
    //   397: iload 7
    //   399: iload_1
    //   400: iadd
    //   401: iload_2
    //   402: iload 7
    //   404: isub
    //   405: aload 6
    //   407: iload 8
    //   409: iload_3
    //   410: invokestatic 83	com/hisoft/utility/Base64:encode3to4	([BII[BII)[B
    //   413: pop
    //   414: iinc 8 4
    //   417: iload 8
    //   419: aload 6
    //   421: arraylength
    //   422: iconst_1
    //   423: isub
    //   424: if_icmpgt +23 -> 447
    //   427: iload 8
    //   429: newarray <illegal type>
    //   431: astore 11
    //   433: aload 6
    //   435: iconst_0
    //   436: aload 11
    //   438: iconst_0
    //   439: iload 8
    //   441: invokestatic 281	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   444: aload 11
    //   446: areturn
    //   447: aload 6
    //   449: areturn
    // Line number table:
    //   Java source line #961	-> byte code offset #0
    //   Java source line #962	-> byte code offset #4
    //   Java source line #965	-> byte code offset #14
    //   Java source line #966	-> byte code offset #18
    //   Java source line #969	-> byte code offset #43
    //   Java source line #970	-> byte code offset #47
    //   Java source line #973	-> byte code offset #72
    //   Java source line #974	-> byte code offset #80
    //   Java source line #978	-> byte code offset #120
    //   Java source line #979	-> byte code offset #126
    //   Java source line #980	-> byte code offset #129
    //   Java source line #981	-> byte code offset #132
    //   Java source line #985	-> byte code offset #135
    //   Java source line #986	-> byte code offset #144
    //   Java source line #987	-> byte code offset #158
    //   Java source line #989	-> byte code offset #169
    //   Java source line #990	-> byte code offset #177
    //   Java source line #992	-> byte code offset #185
    //   Java source line #995	-> byte code offset #187
    //   Java source line #997	-> byte code offset #190
    //   Java source line #999	-> byte code offset #192
    //   Java source line #1000	-> byte code offset #200
    //   Java source line #1003	-> byte code offset #202
    //   Java source line #1004	-> byte code offset #210
    //   Java source line #1007	-> byte code offset #212
    //   Java source line #1008	-> byte code offset #220
    //   Java source line #1010	-> byte code offset #222
    //   Java source line #999	-> byte code offset #225
    //   Java source line #1000	-> byte code offset #233
    //   Java source line #1003	-> byte code offset #235
    //   Java source line #1004	-> byte code offset #243
    //   Java source line #1007	-> byte code offset #245
    //   Java source line #1008	-> byte code offset #253
    //   Java source line #1012	-> byte code offset #255
    //   Java source line #1017	-> byte code offset #261
    //   Java source line #1026	-> byte code offset #275
    //   Java source line #1029	-> byte code offset #294
    //   Java source line #1030	-> byte code offset #299
    //   Java source line #1033	-> byte code offset #309
    //   Java source line #1035	-> byte code offset #315
    //   Java source line #1036	-> byte code offset #318
    //   Java source line #1037	-> byte code offset #321
    //   Java source line #1038	-> byte code offset #326
    //   Java source line #1039	-> byte code offset #329
    //   Java source line #1040	-> byte code offset #332
    //   Java source line #1042	-> byte code offset #347
    //   Java source line #1043	-> byte code offset #350
    //   Java source line #1044	-> byte code offset #362
    //   Java source line #1045	-> byte code offset #371
    //   Java source line #1046	-> byte code offset #374
    //   Java source line #1039	-> byte code offset #377
    //   Java source line #1050	-> byte code offset #390
    //   Java source line #1051	-> byte code offset #396
    //   Java source line #1052	-> byte code offset #414
    //   Java source line #1056	-> byte code offset #417
    //   Java source line #1061	-> byte code offset #427
    //   Java source line #1062	-> byte code offset #433
    //   Java source line #1065	-> byte code offset #444
    //   Java source line #1068	-> byte code offset #447
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	450	0	source	byte[]
    //   0	450	1	off	int
    //   0	450	2	len	int
    //   0	450	3	options	int
    //   127	129	4	baos	java.io.ByteArrayOutputStream
    //   273	78	4	breakLines	boolean
    //   130	96	5	gzos	java.util.zip.GZIPOutputStream
    //   292	18	5	encLen	int
    //   133	103	6	b64os	Base64.OutputStream
    //   313	135	6	outBuff	byte[]
    //   185	3	7	e	IOException
    //   316	87	7	d	int
    //   190	33	8	localObject	Object
    //   319	121	8	e	int
    //   200	1	9	localException	Exception
    //   210	1	9	localException1	Exception
    //   220	1	9	localException2	Exception
    //   233	1	9	localException3	Exception
    //   243	1	9	localException4	Exception
    //   253	1	9	localException5	Exception
    //   324	62	9	len2	int
    //   327	49	10	lineLength	int
    //   431	14	11	finalOut	byte[]
    // Exception table:
    //   from	to	target	type
    //   135	182	185	java/io/IOException
    //   135	190	190	finally
    //   192	197	200	java/lang/Exception
    //   202	207	210	java/lang/Exception
    //   212	217	220	java/lang/Exception
    //   225	230	233	java/lang/Exception
    //   235	240	243	java/lang/Exception
    //   245	250	253	java/lang/Exception
  }
  
  private static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset, int options)
  {
    if (source == null) {
      throw new NullPointerException("Source array was null.");
    }
    if (destination == null) {
      throw new NullPointerException("Destination array was null.");
    }
    if ((srcOffset < 0) || (srcOffset + 3 >= source.length)) {
      throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and still process four bytes.", new Object[] { Integer.valueOf(source.length), Integer.valueOf(srcOffset) }));
    }
    if ((destOffset < 0) || (destOffset + 2 >= destination.length)) {
      throw new IllegalArgumentException(String.format("Destination array with length %d cannot have offset of %d and still store three bytes.", new Object[] { Integer.valueOf(destination.length), Integer.valueOf(destOffset) }));
    }
    byte[] DECODABET = getDecodabet(options);
    if (source[(srcOffset + 2)] == 61)
    {
      int outBuff = (DECODABET[source[srcOffset]] & 0xFF) << 18 | (DECODABET[source[(srcOffset + 1)]] & 0xFF) << 12;
      
      destination[destOffset] = ((byte)(outBuff >>> 16));
      return 1;
    }
    if (source[(srcOffset + 3)] == 61)
    {
      int outBuff = (DECODABET[source[srcOffset]] & 0xFF) << 18 | (DECODABET[source[(srcOffset + 1)]] & 0xFF) << 12 | (DECODABET[source[(srcOffset + 2)]] & 0xFF) << 6;
      
      destination[destOffset] = ((byte)(outBuff >>> 16));
      destination[(destOffset + 1)] = ((byte)(outBuff >>> 8));
      return 2;
    }
    int outBuff = (DECODABET[source[srcOffset]] & 0xFF) << 18 | (DECODABET[source[(srcOffset + 1)]] & 0xFF) << 12 | (DECODABET[source[(srcOffset + 2)]] & 0xFF) << 6 | DECODABET[source[(srcOffset + 3)]] & 0xFF;
    
    destination[destOffset] = ((byte)(outBuff >> 16));
    destination[(destOffset + 1)] = ((byte)(outBuff >> 8));
    destination[(destOffset + 2)] = ((byte)outBuff);
    
    return 3;
  }
  
  public static byte[] decode(byte[] source)
    throws IOException
  {
    byte[] decoded = (byte[])null;
    
    decoded = decode(source, 0, source.length, 0);
    
    return decoded;
  }
  
  public static byte[] decode(byte[] source, int off, int len, int options)
    throws IOException
  {
    if (source == null) {
      throw new NullPointerException("Cannot decode null source array.");
    }
    if ((off < 0) || (off + len > source.length)) {
      throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and process %d bytes.", new Object[] { Integer.valueOf(source.length), Integer.valueOf(off), Integer.valueOf(len) }));
    }
    if (len == 0) {
      return new byte[0];
    }
    if (len < 4) {
      throw new IllegalArgumentException("Base64-encoded string must have at least four characters, but length specified was " + len);
    }
    byte[] DECODABET = getDecodabet(options);
    
    int len34 = len * 3 / 4;
    byte[] outBuff = new byte[len34];
    int outBuffPosn = 0;
    
    byte[] b4 = new byte[4];
    
    int b4Posn = 0;
    int i = 0;
    byte sbiDecode = 0;
    for (i = off; i < off + len; i++)
    {
      sbiDecode = DECODABET[(source[i] & 0xFF)];
      if (sbiDecode >= -5)
      {
        if (sbiDecode >= -1)
        {
          b4[(b4Posn++)] = source[i];
          if (b4Posn > 3)
          {
            outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn, options);
            b4Posn = 0;
            if (source[i] == 61) {
              break;
            }
          }
        }
      }
      else {
        throw new IOException(String.format("Bad Base64 input character decimal %d in array position %d", new Object[] { Integer.valueOf(source[i] & 0xFF), Integer.valueOf(i) }));
      }
    }
    byte[] out = new byte[outBuffPosn];
    System.arraycopy(outBuff, 0, out, 0, outBuffPosn);
    return out;
  }
  
  public static byte[] decode(String s)
    throws IOException
  {
    return decode(s, 0);
  }
  
  /* Error */
  public static byte[] decode(String s, int options)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +14 -> 15
    //   4: new 149	java/lang/NullPointerException
    //   7: dup
    //   8: ldc_w 333
    //   11: invokespecial 153	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: aload_0
    //   16: ldc 31
    //   18: invokevirtual 335	java/lang/String:getBytes	(Ljava/lang/String;)[B
    //   21: astore_2
    //   22: goto +9 -> 31
    //   25: astore_3
    //   26: aload_0
    //   27: invokevirtual 338	java/lang/String:getBytes	()[B
    //   30: astore_2
    //   31: aload_2
    //   32: iconst_0
    //   33: aload_2
    //   34: arraylength
    //   35: iload_1
    //   36: invokestatic 309	com/hisoft/utility/Base64:decode	([BIII)[B
    //   39: astore_2
    //   40: iload_1
    //   41: iconst_4
    //   42: iand
    //   43: ifeq +7 -> 50
    //   46: iconst_1
    //   47: goto +4 -> 51
    //   50: iconst_0
    //   51: istore_3
    //   52: aload_2
    //   53: ifnull +230 -> 283
    //   56: aload_2
    //   57: arraylength
    //   58: iconst_4
    //   59: if_icmplt +224 -> 283
    //   62: iload_3
    //   63: ifne +220 -> 283
    //   66: aload_2
    //   67: iconst_0
    //   68: baload
    //   69: sipush 255
    //   72: iand
    //   73: aload_2
    //   74: iconst_1
    //   75: baload
    //   76: bipush 8
    //   78: ishl
    //   79: ldc_w 340
    //   82: iand
    //   83: ior
    //   84: istore 4
    //   86: ldc_w 341
    //   89: iload 4
    //   91: if_icmpne +192 -> 283
    //   94: aconst_null
    //   95: astore 5
    //   97: aconst_null
    //   98: astore 6
    //   100: aconst_null
    //   101: astore 7
    //   103: sipush 2048
    //   106: newarray <illegal type>
    //   108: astore 8
    //   110: iconst_0
    //   111: istore 9
    //   113: new 156	java/io/ByteArrayOutputStream
    //   116: dup
    //   117: invokespecial 158	java/io/ByteArrayOutputStream:<init>	()V
    //   120: astore 7
    //   122: new 342	java/io/ByteArrayInputStream
    //   125: dup
    //   126: aload_2
    //   127: invokespecial 344	java/io/ByteArrayInputStream:<init>	([B)V
    //   130: astore 5
    //   132: new 345	java/util/zip/GZIPInputStream
    //   135: dup
    //   136: aload 5
    //   138: invokespecial 347	java/util/zip/GZIPInputStream:<init>	(Ljava/io/InputStream;)V
    //   141: astore 6
    //   143: goto +13 -> 156
    //   146: aload 7
    //   148: aload 8
    //   150: iconst_0
    //   151: iload 9
    //   153: invokevirtual 350	java/io/ByteArrayOutputStream:write	([BII)V
    //   156: aload 6
    //   158: aload 8
    //   160: invokevirtual 351	java/util/zip/GZIPInputStream:read	([B)I
    //   163: dup
    //   164: istore 9
    //   166: ifge -20 -> 146
    //   169: aload 7
    //   171: invokevirtual 186	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   174: astore_2
    //   175: goto +78 -> 253
    //   178: astore 10
    //   180: aload 10
    //   182: invokevirtual 355	java/io/IOException:printStackTrace	()V
    //   185: aload 7
    //   187: invokevirtual 183	java/io/ByteArrayOutputStream:close	()V
    //   190: goto +5 -> 195
    //   193: astore 12
    //   195: aload 6
    //   197: invokevirtual 358	java/util/zip/GZIPInputStream:close	()V
    //   200: goto +5 -> 205
    //   203: astore 12
    //   205: aload 5
    //   207: invokevirtual 359	java/io/ByteArrayInputStream:close	()V
    //   210: goto +73 -> 283
    //   213: astore 12
    //   215: goto +68 -> 283
    //   218: astore 11
    //   220: aload 7
    //   222: invokevirtual 183	java/io/ByteArrayOutputStream:close	()V
    //   225: goto +5 -> 230
    //   228: astore 12
    //   230: aload 6
    //   232: invokevirtual 358	java/util/zip/GZIPInputStream:close	()V
    //   235: goto +5 -> 240
    //   238: astore 12
    //   240: aload 5
    //   242: invokevirtual 359	java/io/ByteArrayInputStream:close	()V
    //   245: goto +5 -> 250
    //   248: astore 12
    //   250: aload 11
    //   252: athrow
    //   253: aload 7
    //   255: invokevirtual 183	java/io/ByteArrayOutputStream:close	()V
    //   258: goto +5 -> 263
    //   261: astore 12
    //   263: aload 6
    //   265: invokevirtual 358	java/util/zip/GZIPInputStream:close	()V
    //   268: goto +5 -> 273
    //   271: astore 12
    //   273: aload 5
    //   275: invokevirtual 359	java/io/ByteArrayInputStream:close	()V
    //   278: goto +5 -> 283
    //   281: astore 12
    //   283: aload_2
    //   284: areturn
    // Line number table:
    //   Java source line #1312	-> byte code offset #0
    //   Java source line #1313	-> byte code offset #4
    //   Java source line #1318	-> byte code offset #15
    //   Java source line #1320	-> byte code offset #25
    //   Java source line #1321	-> byte code offset #26
    //   Java source line #1326	-> byte code offset #31
    //   Java source line #1330	-> byte code offset #40
    //   Java source line #1331	-> byte code offset #52
    //   Java source line #1333	-> byte code offset #66
    //   Java source line #1334	-> byte code offset #86
    //   Java source line #1335	-> byte code offset #94
    //   Java source line #1336	-> byte code offset #97
    //   Java source line #1337	-> byte code offset #100
    //   Java source line #1338	-> byte code offset #103
    //   Java source line #1339	-> byte code offset #110
    //   Java source line #1342	-> byte code offset #113
    //   Java source line #1343	-> byte code offset #122
    //   Java source line #1344	-> byte code offset #132
    //   Java source line #1346	-> byte code offset #143
    //   Java source line #1347	-> byte code offset #146
    //   Java source line #1346	-> byte code offset #156
    //   Java source line #1351	-> byte code offset #169
    //   Java source line #1354	-> byte code offset #178
    //   Java source line #1355	-> byte code offset #180
    //   Java source line #1360	-> byte code offset #185
    //   Java source line #1361	-> byte code offset #193
    //   Java source line #1364	-> byte code offset #195
    //   Java source line #1365	-> byte code offset #203
    //   Java source line #1368	-> byte code offset #205
    //   Java source line #1369	-> byte code offset #213
    //   Java source line #1358	-> byte code offset #218
    //   Java source line #1360	-> byte code offset #220
    //   Java source line #1361	-> byte code offset #228
    //   Java source line #1364	-> byte code offset #230
    //   Java source line #1365	-> byte code offset #238
    //   Java source line #1368	-> byte code offset #240
    //   Java source line #1369	-> byte code offset #248
    //   Java source line #1371	-> byte code offset #250
    //   Java source line #1360	-> byte code offset #253
    //   Java source line #1361	-> byte code offset #261
    //   Java source line #1364	-> byte code offset #263
    //   Java source line #1365	-> byte code offset #271
    //   Java source line #1368	-> byte code offset #273
    //   Java source line #1369	-> byte code offset #281
    //   Java source line #1376	-> byte code offset #283
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	285	0	s	String
    //   0	285	1	options	int
    //   21	2	2	bytes	byte[]
    //   30	254	2	bytes	byte[]
    //   25	2	3	uee	UnsupportedEncodingException
    //   51	12	3	dontGunzip	boolean
    //   84	6	4	head	int
    //   95	179	5	bais	java.io.ByteArrayInputStream
    //   98	166	6	gzis	java.util.zip.GZIPInputStream
    //   101	153	7	baos	java.io.ByteArrayOutputStream
    //   108	51	8	buffer	byte[]
    //   111	54	9	length	int
    //   178	3	10	e	IOException
    //   218	33	11	localObject	Object
    //   193	1	12	localException	Exception
    //   203	1	12	localException1	Exception
    //   213	1	12	localException2	Exception
    //   228	1	12	localException3	Exception
    //   238	1	12	localException4	Exception
    //   248	1	12	localException5	Exception
    //   261	1	12	localException6	Exception
    //   271	1	12	localException7	Exception
    //   281	1	12	localException8	Exception
    // Exception table:
    //   from	to	target	type
    //   15	22	25	java/io/UnsupportedEncodingException
    //   113	175	178	java/io/IOException
    //   185	190	193	java/lang/Exception
    //   195	200	203	java/lang/Exception
    //   205	210	213	java/lang/Exception
    //   113	185	218	finally
    //   220	225	228	java/lang/Exception
    //   230	235	238	java/lang/Exception
    //   240	245	248	java/lang/Exception
    //   253	258	261	java/lang/Exception
    //   263	268	271	java/lang/Exception
    //   273	278	281	java/lang/Exception
  }
  
  public static Object decodeToObject(String encodedObject)
    throws IOException, ClassNotFoundException
  {
    return decodeToObject(encodedObject, 0, null);
  }
  
  /* Error */
  public static Object decodeToObject(String encodedObject, int options, ClassLoader loader)
    throws IOException, ClassNotFoundException
  {
    // Byte code:
    //   0: aload_0
    //   1: iload_1
    //   2: invokestatic 329	com/hisoft/utility/Base64:decode	(Ljava/lang/String;I)[B
    //   5: astore_3
    //   6: aconst_null
    //   7: astore 4
    //   9: aconst_null
    //   10: astore 5
    //   12: aconst_null
    //   13: astore 6
    //   15: new 342	java/io/ByteArrayInputStream
    //   18: dup
    //   19: aload_3
    //   20: invokespecial 344	java/io/ByteArrayInputStream:<init>	([B)V
    //   23: astore 4
    //   25: aload_2
    //   26: ifnonnull +17 -> 43
    //   29: new 378	java/io/ObjectInputStream
    //   32: dup
    //   33: aload 4
    //   35: invokespecial 380	java/io/ObjectInputStream:<init>	(Ljava/io/InputStream;)V
    //   38: astore 5
    //   40: goto +15 -> 55
    //   43: new 381	com/hisoft/utility/Base64$1
    //   46: dup
    //   47: aload 4
    //   49: aload_2
    //   50: invokespecial 383	com/hisoft/utility/Base64$1:<init>	(Ljava/io/InputStream;Ljava/lang/ClassLoader;)V
    //   53: astore 5
    //   55: aload 5
    //   57: invokevirtual 386	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   60: astore 6
    //   62: goto +38 -> 100
    //   65: astore 7
    //   67: aload 7
    //   69: athrow
    //   70: astore 7
    //   72: aload 7
    //   74: athrow
    //   75: astore 8
    //   77: aload 4
    //   79: invokevirtual 359	java/io/ByteArrayInputStream:close	()V
    //   82: goto +5 -> 87
    //   85: astore 9
    //   87: aload 5
    //   89: invokevirtual 390	java/io/ObjectInputStream:close	()V
    //   92: goto +5 -> 97
    //   95: astore 9
    //   97: aload 8
    //   99: athrow
    //   100: aload 4
    //   102: invokevirtual 359	java/io/ByteArrayInputStream:close	()V
    //   105: goto +5 -> 110
    //   108: astore 9
    //   110: aload 5
    //   112: invokevirtual 390	java/io/ObjectInputStream:close	()V
    //   115: goto +5 -> 120
    //   118: astore 9
    //   120: aload 6
    //   122: areturn
    // Line number table:
    //   Java source line #1423	-> byte code offset #0
    //   Java source line #1425	-> byte code offset #6
    //   Java source line #1426	-> byte code offset #9
    //   Java source line #1427	-> byte code offset #12
    //   Java source line #1430	-> byte code offset #15
    //   Java source line #1433	-> byte code offset #25
    //   Java source line #1434	-> byte code offset #29
    //   Java source line #1440	-> byte code offset #43
    //   Java source line #1453	-> byte code offset #55
    //   Java source line #1455	-> byte code offset #65
    //   Java source line #1456	-> byte code offset #67
    //   Java source line #1458	-> byte code offset #70
    //   Java source line #1459	-> byte code offset #72
    //   Java source line #1461	-> byte code offset #75
    //   Java source line #1463	-> byte code offset #77
    //   Java source line #1464	-> byte code offset #85
    //   Java source line #1467	-> byte code offset #87
    //   Java source line #1468	-> byte code offset #95
    //   Java source line #1470	-> byte code offset #97
    //   Java source line #1463	-> byte code offset #100
    //   Java source line #1464	-> byte code offset #108
    //   Java source line #1467	-> byte code offset #110
    //   Java source line #1468	-> byte code offset #118
    //   Java source line #1472	-> byte code offset #120
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	123	0	encodedObject	String
    //   0	123	1	options	int
    //   0	123	2	loader	ClassLoader
    //   5	15	3	objBytes	byte[]
    //   7	94	4	bais	java.io.ByteArrayInputStream
    //   10	101	5	ois	java.io.ObjectInputStream
    //   13	108	6	obj	Object
    //   65	3	7	e	IOException
    //   70	3	7	e	ClassNotFoundException
    //   75	23	8	localObject1	Object
    //   85	1	9	localException	Exception
    //   95	1	9	localException1	Exception
    //   108	1	9	localException2	Exception
    //   118	1	9	localException3	Exception
    // Exception table:
    //   from	to	target	type
    //   15	62	65	java/io/IOException
    //   15	62	70	java/lang/ClassNotFoundException
    //   15	75	75	finally
    //   77	82	85	java/lang/Exception
    //   87	92	95	java/lang/Exception
    //   100	105	108	java/lang/Exception
    //   110	115	118	java/lang/Exception
  }
  
  /* Error */
  public static void encodeToFile(byte[] dataToEncode, String filename)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +14 -> 15
    //   4: new 149	java/lang/NullPointerException
    //   7: dup
    //   8: ldc_w 402
    //   11: invokespecial 153	java/lang/NullPointerException:<init>	(Ljava/lang/String;)V
    //   14: athrow
    //   15: aconst_null
    //   16: astore_2
    //   17: new 159	com/hisoft/utility/Base64$OutputStream
    //   20: dup
    //   21: new 404	java/io/FileOutputStream
    //   24: dup
    //   25: aload_1
    //   26: invokespecial 406	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   29: iconst_1
    //   30: invokespecial 161	com/hisoft/utility/Base64$OutputStream:<init>	(Ljava/io/OutputStream;I)V
    //   33: astore_2
    //   34: aload_2
    //   35: aload_0
    //   36: invokevirtual 407	com/hisoft/utility/Base64$OutputStream:write	([B)V
    //   39: goto +20 -> 59
    //   42: astore_3
    //   43: aload_3
    //   44: athrow
    //   45: astore 4
    //   47: aload_2
    //   48: invokevirtual 280	com/hisoft/utility/Base64$OutputStream:close	()V
    //   51: goto +5 -> 56
    //   54: astore 5
    //   56: aload 4
    //   58: athrow
    //   59: aload_2
    //   60: invokevirtual 280	com/hisoft/utility/Base64$OutputStream:close	()V
    //   63: goto +5 -> 68
    //   66: astore 5
    //   68: return
    // Line number table:
    //   Java source line #1497	-> byte code offset #0
    //   Java source line #1498	-> byte code offset #4
    //   Java source line #1501	-> byte code offset #15
    //   Java source line #1503	-> byte code offset #17
    //   Java source line #1504	-> byte code offset #34
    //   Java source line #1506	-> byte code offset #42
    //   Java source line #1507	-> byte code offset #43
    //   Java source line #1509	-> byte code offset #45
    //   Java source line #1511	-> byte code offset #47
    //   Java source line #1512	-> byte code offset #54
    //   Java source line #1514	-> byte code offset #56
    //   Java source line #1511	-> byte code offset #59
    //   Java source line #1512	-> byte code offset #66
    //   Java source line #1516	-> byte code offset #68
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	69	0	dataToEncode	byte[]
    //   0	69	1	filename	String
    //   16	44	2	bos	Base64.OutputStream
    //   42	2	3	e	IOException
    //   45	12	4	localObject	Object
    //   54	1	5	localException	Exception
    //   66	1	5	localException1	Exception
    // Exception table:
    //   from	to	target	type
    //   17	39	42	java/io/IOException
    //   17	45	45	finally
    //   47	51	54	java/lang/Exception
    //   59	63	66	java/lang/Exception
  }
  
  /* Error */
  public static void decodeToFile(String dataToDecode, String filename)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_2
    //   2: new 159	com/hisoft/utility/Base64$OutputStream
    //   5: dup
    //   6: new 404	java/io/FileOutputStream
    //   9: dup
    //   10: aload_1
    //   11: invokespecial 406	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   14: iconst_0
    //   15: invokespecial 161	com/hisoft/utility/Base64$OutputStream:<init>	(Ljava/io/OutputStream;I)V
    //   18: astore_2
    //   19: aload_2
    //   20: aload_0
    //   21: ldc 31
    //   23: invokevirtual 335	java/lang/String:getBytes	(Ljava/lang/String;)[B
    //   26: invokevirtual 407	com/hisoft/utility/Base64$OutputStream:write	([B)V
    //   29: goto +20 -> 49
    //   32: astore_3
    //   33: aload_3
    //   34: athrow
    //   35: astore 4
    //   37: aload_2
    //   38: invokevirtual 280	com/hisoft/utility/Base64$OutputStream:close	()V
    //   41: goto +5 -> 46
    //   44: astore 5
    //   46: aload 4
    //   48: athrow
    //   49: aload_2
    //   50: invokevirtual 280	com/hisoft/utility/Base64$OutputStream:close	()V
    //   53: goto +5 -> 58
    //   56: astore 5
    //   58: return
    // Line number table:
    //   Java source line #1538	-> byte code offset #0
    //   Java source line #1540	-> byte code offset #2
    //   Java source line #1541	-> byte code offset #19
    //   Java source line #1543	-> byte code offset #32
    //   Java source line #1544	-> byte code offset #33
    //   Java source line #1546	-> byte code offset #35
    //   Java source line #1548	-> byte code offset #37
    //   Java source line #1549	-> byte code offset #44
    //   Java source line #1551	-> byte code offset #46
    //   Java source line #1548	-> byte code offset #49
    //   Java source line #1549	-> byte code offset #56
    //   Java source line #1553	-> byte code offset #58
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	59	0	dataToDecode	String
    //   0	59	1	filename	String
    //   1	49	2	bos	Base64.OutputStream
    //   32	2	3	e	IOException
    //   35	12	4	localObject	Object
    //   44	1	5	localException	Exception
    //   56	1	5	localException1	Exception
    // Exception table:
    //   from	to	target	type
    //   2	29	32	java/io/IOException
    //   2	35	35	finally
    //   37	41	44	java/lang/Exception
    //   49	53	56	java/lang/Exception
  }
  
  /* Error */
  public static byte[] decodeFromFile(String filename)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: checkcast 97	[B
    //   4: astore_1
    //   5: aconst_null
    //   6: astore_2
    //   7: new 416	java/io/File
    //   10: dup
    //   11: aload_0
    //   12: invokespecial 418	java/io/File:<init>	(Ljava/lang/String;)V
    //   15: astore_3
    //   16: aconst_null
    //   17: checkcast 97	[B
    //   20: astore 4
    //   22: iconst_0
    //   23: istore 5
    //   25: iconst_0
    //   26: istore 6
    //   28: aload_3
    //   29: invokevirtual 419	java/io/File:length	()J
    //   32: ldc2_w 422
    //   35: lcmp
    //   36: ifle +37 -> 73
    //   39: new 142	java/io/IOException
    //   42: dup
    //   43: new 240	java/lang/StringBuilder
    //   46: dup
    //   47: ldc_w 424
    //   50: invokespecial 244	java/lang/StringBuilder:<init>	(Ljava/lang/String;)V
    //   53: aload_3
    //   54: invokevirtual 419	java/io/File:length	()J
    //   57: invokevirtual 426	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   60: ldc_w 429
    //   63: invokevirtual 245	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   66: invokevirtual 249	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   69: invokespecial 322	java/io/IOException:<init>	(Ljava/lang/String;)V
    //   72: athrow
    //   73: aload_3
    //   74: invokevirtual 419	java/io/File:length	()J
    //   77: l2i
    //   78: newarray <illegal type>
    //   80: astore 4
    //   82: new 431	com/hisoft/utility/Base64$InputStream
    //   85: dup
    //   86: new 433	java/io/BufferedInputStream
    //   89: dup
    //   90: new 435	java/io/FileInputStream
    //   93: dup
    //   94: aload_3
    //   95: invokespecial 437	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   98: invokespecial 440	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   101: iconst_0
    //   102: invokespecial 441	com/hisoft/utility/Base64$InputStream:<init>	(Ljava/io/InputStream;I)V
    //   105: astore_2
    //   106: goto +10 -> 116
    //   109: iload 5
    //   111: iload 6
    //   113: iadd
    //   114: istore 5
    //   116: aload_2
    //   117: aload 4
    //   119: iload 5
    //   121: sipush 4096
    //   124: invokevirtual 444	com/hisoft/utility/Base64$InputStream:read	([BII)I
    //   127: dup
    //   128: istore 6
    //   130: ifge -21 -> 109
    //   133: iload 5
    //   135: newarray <illegal type>
    //   137: astore_1
    //   138: aload 4
    //   140: iconst_0
    //   141: aload_1
    //   142: iconst_0
    //   143: iload 5
    //   145: invokestatic 281	java/lang/System:arraycopy	(Ljava/lang/Object;ILjava/lang/Object;II)V
    //   148: goto +20 -> 168
    //   151: astore_3
    //   152: aload_3
    //   153: athrow
    //   154: astore 7
    //   156: aload_2
    //   157: invokevirtual 447	com/hisoft/utility/Base64$InputStream:close	()V
    //   160: goto +5 -> 165
    //   163: astore 8
    //   165: aload 7
    //   167: athrow
    //   168: aload_2
    //   169: invokevirtual 447	com/hisoft/utility/Base64$InputStream:close	()V
    //   172: goto +5 -> 177
    //   175: astore 8
    //   177: aload_1
    //   178: areturn
    // Line number table:
    //   Java source line #1574	-> byte code offset #0
    //   Java source line #1575	-> byte code offset #5
    //   Java source line #1578	-> byte code offset #7
    //   Java source line #1579	-> byte code offset #16
    //   Java source line #1580	-> byte code offset #22
    //   Java source line #1581	-> byte code offset #25
    //   Java source line #1584	-> byte code offset #28
    //   Java source line #1585	-> byte code offset #39
    //   Java source line #1587	-> byte code offset #73
    //   Java source line #1590	-> byte code offset #82
    //   Java source line #1593	-> byte code offset #106
    //   Java source line #1594	-> byte code offset #109
    //   Java source line #1593	-> byte code offset #116
    //   Java source line #1598	-> byte code offset #133
    //   Java source line #1599	-> byte code offset #138
    //   Java source line #1602	-> byte code offset #151
    //   Java source line #1603	-> byte code offset #152
    //   Java source line #1605	-> byte code offset #154
    //   Java source line #1607	-> byte code offset #156
    //   Java source line #1608	-> byte code offset #163
    //   Java source line #1610	-> byte code offset #165
    //   Java source line #1607	-> byte code offset #168
    //   Java source line #1608	-> byte code offset #175
    //   Java source line #1612	-> byte code offset #177
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	179	0	filename	String
    //   4	174	1	decodedData	byte[]
    //   6	163	2	bis	Base64.InputStream
    //   15	80	3	file	java.io.File
    //   151	2	3	e	IOException
    //   20	119	4	buffer	byte[]
    //   23	121	5	length	int
    //   26	103	6	numBytes	int
    //   154	12	7	localObject	Object
    //   163	1	8	localException	Exception
    //   175	1	8	localException1	Exception
    // Exception table:
    //   from	to	target	type
    //   7	148	151	java/io/IOException
    //   7	154	154	finally
    //   156	160	163	java/lang/Exception
    //   168	172	175	java/lang/Exception
  }
  
  /* Error */
  public static String encodeFromFile(String filename)
    throws IOException
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_1
    //   2: aconst_null
    //   3: astore_2
    //   4: new 416	java/io/File
    //   7: dup
    //   8: aload_0
    //   9: invokespecial 418	java/io/File:<init>	(Ljava/lang/String;)V
    //   12: astore_3
    //   13: aload_3
    //   14: invokevirtual 419	java/io/File:length	()J
    //   17: l2d
    //   18: ldc2_w 456
    //   21: dmul
    //   22: dconst_1
    //   23: dadd
    //   24: d2i
    //   25: bipush 40
    //   27: invokestatic 458	java/lang/Math:max	(II)I
    //   30: newarray <illegal type>
    //   32: astore 4
    //   34: iconst_0
    //   35: istore 5
    //   37: iconst_0
    //   38: istore 6
    //   40: new 431	com/hisoft/utility/Base64$InputStream
    //   43: dup
    //   44: new 433	java/io/BufferedInputStream
    //   47: dup
    //   48: new 435	java/io/FileInputStream
    //   51: dup
    //   52: aload_3
    //   53: invokespecial 437	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   56: invokespecial 440	java/io/BufferedInputStream:<init>	(Ljava/io/InputStream;)V
    //   59: iconst_1
    //   60: invokespecial 441	com/hisoft/utility/Base64$InputStream:<init>	(Ljava/io/InputStream;I)V
    //   63: astore_2
    //   64: goto +10 -> 74
    //   67: iload 5
    //   69: iload 6
    //   71: iadd
    //   72: istore 5
    //   74: aload_2
    //   75: aload 4
    //   77: iload 5
    //   79: sipush 4096
    //   82: invokevirtual 444	com/hisoft/utility/Base64$InputStream:read	([BII)I
    //   85: dup
    //   86: istore 6
    //   88: ifge -21 -> 67
    //   91: new 184	java/lang/String
    //   94: dup
    //   95: aload 4
    //   97: iconst_0
    //   98: iload 5
    //   100: ldc 31
    //   102: invokespecial 461	java/lang/String:<init>	([BIILjava/lang/String;)V
    //   105: astore_1
    //   106: goto +20 -> 126
    //   109: astore_3
    //   110: aload_3
    //   111: athrow
    //   112: astore 7
    //   114: aload_2
    //   115: invokevirtual 447	com/hisoft/utility/Base64$InputStream:close	()V
    //   118: goto +5 -> 123
    //   121: astore 8
    //   123: aload 7
    //   125: athrow
    //   126: aload_2
    //   127: invokevirtual 447	com/hisoft/utility/Base64$InputStream:close	()V
    //   130: goto +5 -> 135
    //   133: astore 8
    //   135: aload_1
    //   136: areturn
    // Line number table:
    //   Java source line #1634	-> byte code offset #0
    //   Java source line #1635	-> byte code offset #2
    //   Java source line #1638	-> byte code offset #4
    //   Java source line #1639	-> byte code offset #13
    //   Java source line #1655	-> byte code offset #34
    //   Java source line #1656	-> byte code offset #37
    //   Java source line #1659	-> byte code offset #40
    //   Java source line #1662	-> byte code offset #64
    //   Java source line #1663	-> byte code offset #67
    //   Java source line #1662	-> byte code offset #74
    //   Java source line #1667	-> byte code offset #91
    //   Java source line #1670	-> byte code offset #109
    //   Java source line #1671	-> byte code offset #110
    //   Java source line #1673	-> byte code offset #112
    //   Java source line #1675	-> byte code offset #114
    //   Java source line #1676	-> byte code offset #121
    //   Java source line #1678	-> byte code offset #123
    //   Java source line #1675	-> byte code offset #126
    //   Java source line #1676	-> byte code offset #133
    //   Java source line #1680	-> byte code offset #135
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	137	0	filename	String
    //   1	135	1	encodedData	String
    //   3	124	2	bis	Base64.InputStream
    //   12	41	3	file	java.io.File
    //   109	2	3	e	IOException
    //   32	64	4	buffer	byte[]
    //   35	64	5	length	int
    //   38	49	6	numBytes	int
    //   112	12	7	localObject	Object
    //   121	1	8	localException	Exception
    //   133	1	8	localException1	Exception
    // Exception table:
    //   from	to	target	type
    //   4	106	109	java/io/IOException
    //   4	112	112	finally
    //   114	118	121	java/lang/Exception
    //   126	130	133	java/lang/Exception
  }
  
  /* Error */
  public static void encodeFileToFile(String infile, String outfile)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokestatic 466	com/hisoft/utility/Base64:encodeFromFile	(Ljava/lang/String;)Ljava/lang/String;
    //   4: astore_2
    //   5: aconst_null
    //   6: astore_3
    //   7: new 468	java/io/BufferedOutputStream
    //   10: dup
    //   11: new 404	java/io/FileOutputStream
    //   14: dup
    //   15: aload_1
    //   16: invokespecial 406	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   19: invokespecial 470	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   22: astore_3
    //   23: aload_3
    //   24: aload_2
    //   25: ldc 31
    //   27: invokevirtual 335	java/lang/String:getBytes	(Ljava/lang/String;)[B
    //   30: invokevirtual 471	java/io/OutputStream:write	([B)V
    //   33: goto +22 -> 55
    //   36: astore 4
    //   38: aload 4
    //   40: athrow
    //   41: astore 5
    //   43: aload_3
    //   44: invokevirtual 180	java/io/OutputStream:close	()V
    //   47: goto +5 -> 52
    //   50: astore 6
    //   52: aload 5
    //   54: athrow
    //   55: aload_3
    //   56: invokevirtual 180	java/io/OutputStream:close	()V
    //   59: goto +5 -> 64
    //   62: astore 6
    //   64: return
    // Line number table:
    //   Java source line #1696	-> byte code offset #0
    //   Java source line #1697	-> byte code offset #5
    //   Java source line #1699	-> byte code offset #7
    //   Java source line #1700	-> byte code offset #23
    //   Java source line #1702	-> byte code offset #36
    //   Java source line #1703	-> byte code offset #38
    //   Java source line #1705	-> byte code offset #41
    //   Java source line #1707	-> byte code offset #43
    //   Java source line #1708	-> byte code offset #50
    //   Java source line #1710	-> byte code offset #52
    //   Java source line #1707	-> byte code offset #55
    //   Java source line #1708	-> byte code offset #62
    //   Java source line #1711	-> byte code offset #64
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	65	0	infile	String
    //   0	65	1	outfile	String
    //   4	21	2	encoded	String
    //   6	50	3	out	java.io.OutputStream
    //   36	3	4	e	IOException
    //   41	12	5	localObject	Object
    //   50	1	6	localException	Exception
    //   62	1	6	localException1	Exception
    // Exception table:
    //   from	to	target	type
    //   7	33	36	java/io/IOException
    //   7	41	41	finally
    //   43	47	50	java/lang/Exception
    //   55	59	62	java/lang/Exception
  }
  
  /* Error */
  public static void decodeFileToFile(String infile, String outfile)
    throws IOException
  {
    // Byte code:
    //   0: aload_0
    //   1: invokestatic 475	com/hisoft/utility/Base64:decodeFromFile	(Ljava/lang/String;)[B
    //   4: astore_2
    //   5: aconst_null
    //   6: astore_3
    //   7: new 468	java/io/BufferedOutputStream
    //   10: dup
    //   11: new 404	java/io/FileOutputStream
    //   14: dup
    //   15: aload_1
    //   16: invokespecial 406	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   19: invokespecial 470	java/io/BufferedOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   22: astore_3
    //   23: aload_3
    //   24: aload_2
    //   25: invokevirtual 471	java/io/OutputStream:write	([B)V
    //   28: goto +22 -> 50
    //   31: astore 4
    //   33: aload 4
    //   35: athrow
    //   36: astore 5
    //   38: aload_3
    //   39: invokevirtual 180	java/io/OutputStream:close	()V
    //   42: goto +5 -> 47
    //   45: astore 6
    //   47: aload 5
    //   49: athrow
    //   50: aload_3
    //   51: invokevirtual 180	java/io/OutputStream:close	()V
    //   54: goto +5 -> 59
    //   57: astore 6
    //   59: return
    // Line number table:
    //   Java source line #1726	-> byte code offset #0
    //   Java source line #1727	-> byte code offset #5
    //   Java source line #1729	-> byte code offset #7
    //   Java source line #1730	-> byte code offset #23
    //   Java source line #1732	-> byte code offset #31
    //   Java source line #1733	-> byte code offset #33
    //   Java source line #1735	-> byte code offset #36
    //   Java source line #1737	-> byte code offset #38
    //   Java source line #1738	-> byte code offset #45
    //   Java source line #1740	-> byte code offset #47
    //   Java source line #1737	-> byte code offset #50
    //   Java source line #1738	-> byte code offset #57
    //   Java source line #1741	-> byte code offset #59
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	60	0	infile	String
    //   0	60	1	outfile	String
    //   4	21	2	decoded	byte[]
    //   6	45	3	out	java.io.OutputStream
    //   31	3	4	e	IOException
    //   36	12	5	localObject	Object
    //   45	1	6	localException	Exception
    //   57	1	6	localException1	Exception
    // Exception table:
    //   from	to	target	type
    //   7	28	31	java/io/IOException
    //   7	36	36	finally
    //   38	42	45	java/lang/Exception
    //   50	54	57	java/lang/Exception
  }
}
