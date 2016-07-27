package com.hisoft.utility;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Base64$InputStream
  extends FilterInputStream
{
  private boolean encode;
  private int position;
  private byte[] buffer;
  private int bufferLength;
  private int numSigBytes;
  private int lineLength;
  private boolean breakLines;
  private int options;
  private byte[] decodabet;
  
  public Base64$InputStream(InputStream in)
  {
    this(in, 0);
  }
  
  public Base64$InputStream(InputStream in, int options)
  {
    super(in);
    this.options = options;
    this.breakLines = ((options & 0x8) > 0);
    this.encode = ((options & 0x1) > 0);
    this.bufferLength = (this.encode ? 4 : 3);
    this.buffer = new byte[this.bufferLength];
    this.position = -1;
    this.lineLength = 0;
    this.decodabet = Base64.access$0(options);
  }
  
  public int read()
    throws IOException
  {
    if (this.position < 0) {
      if (this.encode)
      {
        byte[] b3 = new byte[3];
        int numBinaryBytes = 0;
        for (int i = 0; i < 3; i++)
        {
          int b = this.in.read();
          if (b < 0) {
            break;
          }
          b3[i] = ((byte)b);
          numBinaryBytes++;
        }
        if (numBinaryBytes > 0)
        {
          Base64.access$1(b3, 0, numBinaryBytes, this.buffer, 0, this.options);
          this.position = 0;
          this.numSigBytes = 4;
        }
        else
        {
          return -1;
        }
      }
      else
      {
        byte[] b4 = new byte[4];
        int i = 0;
        for (i = 0; i < 4; i++)
        {
          int b = 0;
          do
          {
            b = this.in.read();
          } while ((b >= 0) && (this.decodabet[(b & 0x7F)] <= -5));
          if (b < 0) {
            break;
          }
          b4[i] = ((byte)b);
        }
        if (i == 4)
        {
          this.numSigBytes = Base64.access$2(b4, 0, this.buffer, 0, this.options);
          this.position = 0;
        }
        else
        {
          if (i == 0) {
            return -1;
          }
          throw new IOException("Improperly padded Base64 input.");
        }
      }
    }
    if (this.position >= 0)
    {
      if (this.position >= this.numSigBytes) {
        return -1;
      }
      if ((this.encode) && (this.breakLines) && (this.lineLength >= 76))
      {
        this.lineLength = 0;
        return 10;
      }
      this.lineLength += 1;
      
      int b = this.buffer[(this.position++)];
      if (this.position >= this.bufferLength) {
        this.position = -1;
      }
      return b & 0xFF;
    }
    throw new IOException("Error in Base64 code reading stream.");
  }
  
  public int read(byte[] dest, int off, int len)
    throws IOException
  {
    for (int i = 0; i < len; i++)
    {
      int b = read();
      if (b >= 0)
      {
        dest[(off + i)] = ((byte)b);
      }
      else
      {
        if (i != 0) {
          break;
        }
        return -1;
      }
    }
    return i;
  }
}
