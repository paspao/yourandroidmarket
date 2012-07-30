package it.ownmarket.android.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import android.util.Log;


public class Util {
	


	public static boolean isEmpty(String str)
	{
		boolean result=true;
		if(str==null);
		else 
		{
			str=str.trim();
			if(str.length()>0)
				result=false;
		}
			
		return result;
	}
	
	public static boolean isEmpty(Collection<?> collection)
	{
		boolean result=true;
		if(collection==null)
			result=true;
		else if(collection.isEmpty())
			result=true;
		else
			result=false;
			
		return result;
	}
	
	public static String formatDate(String date)
	{
		String result=" ";
		if(isEmpty(date)) {
			return result;
		} else if (date.contains("9999") || date.contains("0000")) {
		  return result;
		} else {
		  result=date.substring(6, 8)+"/"+date.substring(4, 6)+"/"+ date.substring(0, 4);
		}
		return result;
	}
	
	public static String formatString(String in, int nOfChar) {
	  String result = in;
	  if (isEmpty(result)) {
		return result;
	  } else if (result.length() <= nOfChar) {
		return result;
	  } else {
		result = result.substring(0, nOfChar) + "\n" + result.substring(nOfChar);
	  }
	  return result;
	}
	
	public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);
 
        // Get the size of the file
        long length = file.length();
 
        if (length > Integer.MAX_VALUE) {
            // File is too large
            
            Log.w("Get Bytes function", file.getPath()+" is too big");
        }
 
        // Create the byte array to hold the data
        byte[] bytes = new byte[(int)length];
 
        // debug - init array
        for (int i = 0; i < length; i++){
            bytes[i] = 0x0;
        }
 
        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }
 
        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+file.getName());
        }
 
        // Close the input stream and return bytes
        is.close();
        return bytes;
    }
	
	
}
