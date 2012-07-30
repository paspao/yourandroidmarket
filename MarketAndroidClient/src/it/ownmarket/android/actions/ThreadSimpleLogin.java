package it.ownmarket.android.actions;

import android.content.Context;
import it.ownmarket.android.dto.User;
import it.ownmarket.android.exception.MarketAndroidException;
import it.ownmarket.android.rest.service.ServiceSimpleLogin;

public class ThreadSimpleLogin extends Thread{

	private User user;
	private OnEndLogin eventEmitter;
	private Context context;
	private ServiceSimpleLogin simple;
	
	public ThreadSimpleLogin(Context context,User user,OnEndLogin listner) {
		this.eventEmitter=listner;
		this.user=user;
		this.context=context;
	}
	
	@Override
	public void run() {
		boolean result=false;
		simple=new ServiceSimpleLogin(context);
		try {
			result=simple.login(user.getEmail(), user.getPassword());
		} catch (MarketAndroidException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			eventEmitter.onLogin(result);
		}
		
	}
	
	public interface OnEndLogin{
		public void onLogin(boolean success);
	}
}
