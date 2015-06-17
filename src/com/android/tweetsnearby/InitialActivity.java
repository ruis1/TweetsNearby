package com.android.tweetsnearby;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.twitter.sdk.android.Twitter;
import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.core.Session;


public class InitialActivity extends Activity {

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v("breakpoint","initial");
        final Session activeSession = SessionRecorder.recordInitialSessionState(
                Twitter.getSessionManager().getActiveSession(),
                Digits.getSessionManager().getActiveSession()
        );

        if (activeSession != null) {
            startChooseActivity();
        } else {
            startLoginActivity();
        }
    }

    private void startChooseActivity() {
        startActivity(new Intent(this, ChooseActivity.class));
    }

    private void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }
}