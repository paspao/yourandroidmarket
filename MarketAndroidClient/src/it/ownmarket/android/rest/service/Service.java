package it.ownmarket.android.rest.service;

import android.content.Context;

public abstract class Service {

	protected Context context;
	
	
	public Service(Context context) {
		this.context=context;
	}
}
