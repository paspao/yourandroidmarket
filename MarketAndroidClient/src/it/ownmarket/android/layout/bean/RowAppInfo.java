package it.ownmarket.android.layout.bean;

import java.io.Serializable;

import android.graphics.drawable.BitmapDrawable;

public class RowAppInfo implements Serializable{

	private static final long serialVersionUID = -5348880638645600987L;

	private String title;
	private BitmapDrawable icon;
	private String iconUrl;
	private String version;
	private String status;
	
	public RowAppInfo() {
		// TODO Auto-generated constructor stub
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public BitmapDrawable getIcon() {
		return icon;
	}

	public void setIcon(BitmapDrawable icon) {
		this.icon = icon;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	public String getIconUrl() {
		return iconUrl;
	}
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	
	
}
