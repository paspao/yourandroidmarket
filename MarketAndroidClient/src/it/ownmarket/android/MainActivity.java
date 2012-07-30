package it.ownmarket.android;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import it.ownmarket.android.actions.ThreadListApps;
import it.ownmarket.android.actions.ThreadPackageInfo;
import it.ownmarket.android.adapter.MySimpleArrayAdapter;
import it.ownmarket.android.dto.Applicazione;
import it.ownmarket.android.dto.User;
import it.ownmarket.android.layout.bean.RowAppInfo;
import it.ownmarket.android.util.Const;
import it.ownmarket.android.util.DownloadFile;
import it.ownmarket.android.util.PropertiesLoader;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends ListActivity implements
		ThreadListApps.OnListApps, ThreadPackageInfo.OnPackageInfo,
		OnItemClickListener, DownloadFile.OnDownloadFinished {

	private Handler handler;
	private ProgressDialog progress;
	private User userLogged;
	private Applicazione[] currentApps;
	private static final int REQUEST_CODE_INSTALL = 11;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		RowAppInfo[] values = null;
		handler = new Handler();
		SharedPreferences pref = getSharedPreferences(Const.SHARED_YOUR_MARKET,
				MODE_WORLD_READABLE);
		String usrJson = pref.getString(Const.USER_LOGIN, "");
		Gson gg = new Gson();
		userLogged = gg.fromJson(usrJson, User.class);
		// First paramenter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, android.R.id.text1, values);

		getListView().setOnItemClickListener(this);

		progress = ProgressDialog.show(this,
				getResources().getText(R.string.list_apps), getResources()
						.getText(R.string.loading), true);
		ThreadListApps listAppsService = new ThreadListApps(this, userLogged,
				this);
		listAppsService.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_logout:
			SharedPreferences.Editor pref = getSharedPreferences(
					Const.SHARED_YOUR_MARKET, MODE_WORLD_WRITEABLE).edit();
			pref.clear();
			pref.commit();
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	public void onResult(final Applicazione[] apps) {
		currentApps = apps;
		ThreadPackageInfo pkg = new ThreadPackageInfo(this, apps, userLogged,
				this);
		pkg.start();
	}

	@Override
	public void onPackageInfoLoaded(final RowAppInfo[] rows) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				if (progress != null)
					progress.dismiss();

				if (rows != null) {

					MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(
							MainActivity.this, rows);
					// Assign adapter to ListView
					getListView().setAdapter(adapter);
					
				}

			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> view, View arg1, int position,
			long id) {
		ProgressDialog mProgressDialog;
		String url = null;
		url = PropertiesLoader.getInstance(getResources()).getValue("rest.url");
		String path = PropertiesLoader.getInstance(getResources()).getValue(
				"downloadapp.path");
		String userParam = PropertiesLoader.getInstance(getResources())
				.getValue("rest.user");
		String passParam = PropertiesLoader.getInstance(getResources())
				.getValue("rest.password");
		String appid = currentApps[position].getName();
		try {
			appid = URLEncoder.encode(appid, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		url += path + appid + "?" + userParam + "=" + userLogged.getEmail()
				+ "&" + passParam + "=" + userLogged.getPassword();
		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage(this.getResources().getString(R.string.downloading));
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		DownloadFile dd = new DownloadFile(mProgressDialog,
				currentApps[position], this);
		dd.execute(url);

	}

	@Override
	public void downloaded(String url) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		File apkFile = new File(url);
		intent.setDataAndType(Uri.fromFile(apkFile),
				"application/vnd.android.package-archive");
		startActivityForResult(intent, REQUEST_CODE_INSTALL);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUEST_CODE_INSTALL:
			progress = ProgressDialog.show(this,
					getResources().getText(R.string.update_list_apps), getResources()
							.getText(R.string.loading), true);
			ThreadPackageInfo pkg = new ThreadPackageInfo(this, currentApps, userLogged,
					this);
			pkg.start();
			break;
		default:
			break;
		}
		
	}
	@Override
	public void onBackPressed() {
		
		super.onBackPressed();
		finish();
	}
}
