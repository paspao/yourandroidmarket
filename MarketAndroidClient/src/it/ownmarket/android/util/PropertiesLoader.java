package it.ownmarket.android.util;


import it.ownmarket.android.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

public class PropertiesLoader {
	
	private static PropertiesLoader instance;
	private Properties properties;

	private PropertiesLoader(Resources resources) {
		try {
		    InputStream rawResource = resources.openRawResource(R.raw.conf);
		    properties = new Properties();
		    properties.load(rawResource);
		    Log.d("Properties","The properties are now loaded");
		    Log.d("Properties","properties: " + properties);
		} catch (NotFoundException e) {
			Log.e("Properties","Did not find raw resource: "+e);
		} catch (IOException e) {
			Log.e("Properties","Failed to open microlog property file");
		}

	}
	public static PropertiesLoader getInstance(Resources res){
		if(instance==null)
			instance=new PropertiesLoader(res);
		return instance;
	}
	
	public String getValue(String key)
	{
		String result=null;
		if(properties!=null)
			result=properties.getProperty(key);
		return result;
	}
}
