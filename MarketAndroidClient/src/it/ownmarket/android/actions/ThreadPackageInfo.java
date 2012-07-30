package it.ownmarket.android.actions;

import it.ownmarket.android.R;
import it.ownmarket.android.dto.Applicazione;
import it.ownmarket.android.dto.User;
import it.ownmarket.android.layout.bean.RowAppInfo;
import it.ownmarket.android.util.PropertiesLoader;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ThreadPackageInfo extends Thread {

	private static final String TAG = "ThreadPackageInfo";
	private OnPackageInfo eventEmitter;
	private Context context;
	private Applicazione[] remoteApp;
	private User userLogged;

	public ThreadPackageInfo(Context cxt, Applicazione[] apps, User userLogged,
			OnPackageInfo ev) {
		this.eventEmitter = ev;
		this.context = cxt;
		this.userLogged = userLogged;
		this.remoteApp = apps;
	}

	@Override
	public void run() {
		RowAppInfo[] result = null;
		try {
			List<RowAppInfo> lista = getPackages(remoteApp, userLogged);
			result = lista.toArray(new RowAppInfo[1]);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			eventEmitter.onPackageInfoLoaded(result);
		}
	}

	class PInfo {
		private String appname = "";
		private String pname = "";
		private String versionName = "";
		private int versionCode = 0;
		private Drawable icon;

		private void prettyPrint() {
			Log.v(TAG, appname + "\t" + pname + "\t" + versionName + "\t"
					+ versionCode);
		}
	}

	private List<RowAppInfo> getPackages(Applicazione[] remoteApps,
			User userLogged) {
		ArrayList<PInfo> apps = getInstalledApps(false);
		List<RowAppInfo> result = new ArrayList<RowAppInfo>();
		String pathIcon = PropertiesLoader.getInstance(context.getResources())
				.getValue("getappicon.path");
		String loginParam = PropertiesLoader
				.getInstance(context.getResources()).getValue("rest.user");
		String passParam = PropertiesLoader.getInstance(context.getResources())
				.getValue("rest.password");

		for (int i = 0; i < remoteApps.length; i++) {
			Applicazione current = remoteApps[i];
			String url = PropertiesLoader.getInstance(context.getResources())
					.getValue("rest.url");
			RowAppInfo row = new RowAppInfo();
			result.add(row);
			row.setTitle(remoteApps[i].getName());
			row.setVersion(remoteApps[i].getVersion_name());
			String name = null;
			try {
				name = URLEncoder.encode(remoteApps[i].getName(), "UTF-8")
						.replace("+", "%20");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			url += pathIcon + name + "?" + loginParam + "="
					+ userLogged.getEmail() + "&" + passParam + "="
					+ userLogged.getPassword();
			row.setIconUrl(url);
			row.setStatus(context.getResources().getString(
					R.string.install));
			for (PInfo app : apps) {

				if (app.pname.equals(remoteApps[i].getPackage_())
						&& app.appname.equals(remoteApps[i].getName())) {
					if (app.versionCode < remoteApps[i].getVersion_code())
						row.setStatus(context.getResources().getString(
								R.string.update));
					else
						row.setStatus(context.getResources().getString(
								R.string.installed));
				} 
					

			}
		}

		/* false = no system packages */
		/*
		 * final int max = apps.size(); for (int i=0; i<max; i++) {
		 * apps.get(i).prettyPrint(); }
		 */
		return result;
	}

	private ArrayList<PInfo> getInstalledApps(boolean getSysPackages) {
		ArrayList<PInfo> res = new ArrayList<PInfo>();
		List<PackageInfo> packs = context.getPackageManager()
				.getInstalledPackages(0);
		for (int i = 0; i < packs.size(); i++) {
			PackageInfo p = packs.get(i);
			if ((!getSysPackages) && (p.versionName == null)) {
				continue;
			}
			PInfo newInfo = new PInfo();
			newInfo.appname = p.applicationInfo.loadLabel(
					context.getPackageManager()).toString();
			newInfo.pname = p.packageName;
			newInfo.versionName = p.versionName;
			newInfo.versionCode = p.versionCode;
			newInfo.icon = p.applicationInfo.loadIcon(context
					.getPackageManager());
			res.add(newInfo);
		}
		return res;
	}

	public interface OnPackageInfo {
		public void onPackageInfoLoaded(RowAppInfo[] rows);
	}
}
