package it.ownmarket.android.rest.service;

import it.ownmarket.android.dto.User;
import it.ownmarket.android.exception.MarketAndroidException;
import it.ownmarket.android.rest.RequestMethod;
import it.ownmarket.android.rest.RestClient;
import it.ownmarket.android.util.Const;
import it.ownmarket.android.util.PropertiesLoader;

import org.apache.http.HttpStatus;

import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ServiceSimpleLogin extends Service{

	public ServiceSimpleLogin(Context context) {
		super(context);
	}
	
	public boolean login(String email,String password)throws MarketAndroidException{
		boolean result=false;
		String url=PropertiesLoader.getInstance(context.getResources()).getValue("rest.url");
		String path=PropertiesLoader.getInstance(context.getResources()).getValue("simplelogin.path");
		String userParam=PropertiesLoader.getInstance(context.getResources()).getValue("rest.user");
		String passwdParam=PropertiesLoader.getInstance(context.getResources()).getValue("rest.password");
		url+=path+"?"+userParam+"="+email+"&"+passwdParam+"="+password;
		try
		{
			RestClient client =new RestClient(url);
			client.execute(RequestMethod.GET);
			if(HttpStatus.SC_OK==client.getResponseCode())
			{
				result=true;
				User uu=new User(email,password);
				Gson gg=new Gson();
				String userJson=gg.toJson(uu);
				SharedPreferences.Editor pref=context.getSharedPreferences(Const.SHARED_YOUR_MARKET, context.MODE_WORLD_WRITEABLE).edit();
				pref.putString(Const.USER_LOGIN, userJson);
				pref.commit();
				
			}
		}
		catch(Exception e)
		{
			throw new MarketAndroidException(e.getMessage());
		}
		
		return result;
	}
	
}
