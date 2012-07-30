package it.ownmarket.android;

import it.ownmarket.android.util.Const;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Intent ii = null;
		SharedPreferences pref = getSharedPreferences(Const.SHARED_YOUR_MARKET,
				MODE_WORLD_READABLE);
		if (pref.contains(Const.USER_LOGIN))
			ii = new Intent(this, MainActivity.class);
		else
			ii = new Intent(this, LoginActivity.class);
		startActivity(ii);

	}

}
