package org.fonal.ndkplugin.helpers;

public final class OsHelper
{
   private static String OS = null;
   
   public static String getOsName()
   {
      if(OS == null) { OS = System.getProperty("os.name"); }
      return OS;
   }
   
   public static boolean isWindows()
   {
      return getOsName().startsWith("Windows");
   }

}

