package com.hisoft.utility;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class Base64$OutputStream
  extends FilterOutputStream
{
  private boolean encode;
  private int position;
  private byte[] buffer;
  private int bufferLength;
  private int lineLength;
  private boolean breakLines;
  private byte[] b4;
  private boolean suspendEncoding;
  private int options;
  private byte[] decodabet;
  
  public Base64$OutputStream(OutputStream out)
  {
    this(out, 1);
  }
  
  public Base64$OutputStream(OutputStream out, int options)
  {
    super(out);
    this.breakLines = ((options & 0x8) != 0);
    this.encode = ((options & 0x1) != 0);
    this.bufferLength = (this.encode ? 3 : 4);
    this.buffer = new byte[this.bufferLength];
    this.position = 0;
    this.lineLength = 0;
    this.suspendEncoding = false;
    this.b4 = new byte[4];
    this.options = options;
    this.decodabet = Base64.access$0(options);
  }
  
  public void write(int theByte)
    throws IOException
  {
    if (this.suspendEncoding)
    {
      this.out.write(theByte);
      return;
    }
    if (this.encode)
    {
      this.buffer[(this.position++)] = ((byte)theByte);
      if (this.position >= this.bufferLength)
      {
        this.out.write(Base64.access$3(this.b4, this.buffer, this.bufferLength, this.options));
        
        this.lineLength += 4;
        if ((this.breakLines) && (this.lineLength >= 76))
        {
          this.out.write(10);
          this.lineLength = 0;
        }
        this.position = 0;
      }
    }
    else if (this.decodabet[(theByte & 0x7F)] > -5)
    {
      this.buffer[(this.position++)] = ((byte)theByte);
      if (this.position >= this.bufferLength)
      {
        int len = Base64.access$2(this.buffer, 0, this.b4, 0, this.options);
        this.out.write(this.b4, 0, len);
        this.position = 0;
      }
    }
    else if (this.decodabet[(theByte & 0x7F)] != -5)
    {
      throw new IOException("Invalid character in Base64 data.");
    }
  }
  
  public void write(byte[] theBytes, int off, int len)
    throws IOException
  {
    if (this.suspendEncoding)
    {
      this.out.write(theBytes, off, len);
      return;
    }
    for (int i = 0; i < len; i++) {
      write(theBytes[(off + i)]);
    }
  }
  
  public void flushBase64()
    throws IOException
  {
    if (this.position > 0) {
      if (this.encode)
      {
        this.out.write(Base64.access$3(this.b4, this.buffer, this.position, this.options));
        this.position = 0;
      }
      else
      {
        throw new IOException("Base64 input not properly padded.");
      }
    }
  }
  
  public void close()
    throws IOException
  {
    flushBase64();
    
    super.close();
    
    this.buffer = null;
    this.out = null;
  }
  
  public void suspendEncoding()
    throws IOException
  {
    flushBase64();
    this.suspendEncoding = true;
  }
  
  public void resumeEncoding()
  {
    this.suspendEncoding = false;
  }
}
