package com.android.tweetsnearby;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class LoginActivity extends Activity {

	private TwitterLoginButton loginButton;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		setUpTwitterButton();
	}

    private void setUpTwitterButton() {
    	loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
            	SessionRecorder.recordSessionActive("Login: twitter account active", result.data);
            	startChoose();
            }

            @Override
            public void failure(TwitterException exception) {
            	Toast.makeText(LoginActivity.this, "Authentification Failed", Toast.LENGTH_SHORT).show(); 
            }
        });
    }
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginButton.onActivityResult(requestCode, resultCode, data);
    }
	
	private void startChoose() {
        final Intent Intent = new Intent(LoginActivity.this, ChooseActivity.class);
        startActivity(Intent);
        LoginActivity.this.finish();
    }
	
	public void onBackPressed() {
		super.onBackPressed();
	}

}
