package com.hisoft.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

class Base64$1
  extends ObjectInputStream
{
  Base64$1(InputStream $anonymous0, ClassLoader paramClassLoader)
    throws IOException
  {
    super($anonymous0);
  }
  
  public Class<?> resolveClass(ObjectStreamClass streamClass)
    throws IOException, ClassNotFoundException
  {
    Class<?> c = Class.forName(streamClass.getName(), false, this.val$loader);
    if (c == null) {
      return super.resolveClass(streamClass);
    }
    return c;
  }
}
