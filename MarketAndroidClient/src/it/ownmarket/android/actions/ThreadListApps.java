package it.ownmarket.android.actions;

import it.ownmarket.android.dto.Applicazione;
import it.ownmarket.android.dto.User;
import it.ownmarket.android.exception.MarketAndroidException;
import it.ownmarket.android.rest.service.ServiceListApps;
import android.content.Context;

public class ThreadListApps extends Thread{

	private User user;
	private OnListApps eventEmitter;
	private Context context;
	
	public ThreadListApps(Context context,User user,OnListApps listner) {
		this.eventEmitter=listner;
		this.user=user;
		this.context=context;
	}
	
	@Override
	public void run() {
		ServiceListApps listapps=new ServiceListApps(context);
		Applicazione[] result=null;
		try{
			result=listapps.getApps(user);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally{
			eventEmitter.onResult(result);
		}
	}
	
	public interface OnListApps{
		public void onResult(Applicazione[] apps);
	}
}
