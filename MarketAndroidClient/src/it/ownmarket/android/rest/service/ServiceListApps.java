package it.ownmarket.android.rest.service;

import it.ownmarket.android.dto.Applicazione;
import it.ownmarket.android.dto.User;
import it.ownmarket.android.exception.MarketAndroidException;
import it.ownmarket.android.rest.RequestMethod;
import it.ownmarket.android.rest.RestClient;
import it.ownmarket.android.util.PropertiesLoader;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;

import com.google.gson.Gson;

import android.content.Context;

public class ServiceListApps extends Service {
	
	
	public ServiceListApps(Context cnt) {
		super(cnt);
	}
	
	
	public Applicazione[] getApps(User user) throws MarketAndroidException{
		Applicazione[] result=null;
		
		String url=PropertiesLoader.getInstance(context.getResources()).getValue("rest.url");
		String path=PropertiesLoader.getInstance(context.getResources()).getValue("listapps.path");
		String loginParam=PropertiesLoader.getInstance(context.getResources()).getValue("rest.user");
		String passwordParam=PropertiesLoader.getInstance(context.getResources()).getValue("rest.password");
		url+=path+"?"+loginParam+"="+user.getEmail()+"&"+passwordParam+"="+user.getPassword();
		RestClient client=new RestClient(url);
		try{
			client.execute(RequestMethod.GET);
			if(client.getResponseCode()==HttpStatus.SC_OK)
			{
				Gson gson=new Gson();
				//Type collectionType = new TypeToken<Collection<Integer>>(){}.getType();
				//Applicazione[].class
				result=gson.fromJson(client.getResponse(),Applicazione[].class);
				
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new MarketAndroidException(e.getMessage());
		}
		
		
		return result;
	}

}
