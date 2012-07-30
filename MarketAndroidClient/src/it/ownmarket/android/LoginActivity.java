package it.ownmarket.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import it.ownmarket.android.actions.ThreadSimpleLogin;
import it.ownmarket.android.dto.User;
import it.ownmarket.android.util.Util;

public class LoginActivity extends Activity implements
		ThreadSimpleLogin.OnEndLogin, OnClickListener {

	private EditText editUsername;
	private EditText editPassword;
	private Button buttonLogin;
	private Button buttonCancel;
	private Handler handler;
	private ThreadSimpleLogin thread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		handler=new Handler();
		editUsername = (EditText) findViewById(R.id.edit_email);
		editPassword = (EditText) findViewById(R.id.edit_password);
		buttonLogin = (Button) findViewById(R.id.button_save);
		buttonCancel = (Button) findViewById(R.id.button_cancel);
		buttonLogin.setOnClickListener(this);
		buttonCancel.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_save:
			if (Util.isEmpty(editUsername.getText().toString())
					|| Util.isEmpty(editPassword.getText().toString())) {
				Toast.makeText(
						this,
						this.getResources().getString(
								R.string.error_login_required),
						Toast.LENGTH_LONG).show();
			} else {
				User u = new User(editUsername.getText().toString(),
						editPassword.getText().toString());
				thread = new ThreadSimpleLogin(this, u, this);
				thread.start();
			}
			break;
		case R.id.button_cancel:
			finish();
			break;
		default:
			break;
		}

	}

	@Override
	public void onLogin(boolean success) {
		if(success)
		{
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					Intent ii=new Intent(LoginActivity.this,MainActivity.class);
					startActivity(ii);
					
				}
			});
			
		}
		else
handler.post(new Runnable() {
				
				@Override
				public void run() {
					Toast.makeText(
							LoginActivity.this,
							LoginActivity.this.getResources().getString(
									R.string.error_login_failed),
							Toast.LENGTH_LONG).show();
					
				}
			});
			

	}
}
