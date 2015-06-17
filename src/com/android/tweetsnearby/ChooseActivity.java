package com.android.tweetsnearby;

import java.io.IOException;

import retrofit.client.Response;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;

public class ChooseActivity extends Activity implements ConnectionCallbacks,
		OnConnectionFailedListener {
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
	private Location mLastLocation;
	private GoogleApiClient mGoogleApiClient;
	private TextView lblLocation;
	private App app;
	private int dist, time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App) getApplication();
		setContentView(R.layout.activity_choose);
		setupSpin();
		setupTNearby();
		setupFNearby();
		setupRNearby();
		setupSignOut();
		fetchdata();
		lblLocation = (TextView) findViewById(R.id.locationshow);
		if (checkPlayServices()) {
			buildGoogleApiClient();
		}
		displayLocation();
	}

	private void displayLocation() {

		mLastLocation = LocationServices.FusedLocationApi
				.getLastLocation(mGoogleApiClient);
		if (mLastLocation != null) {
			double latitude = mLastLocation.getLatitude();
			double longitude = mLastLocation.getLongitude();
			app.setLat(latitude);
			app.setLon(longitude);
			lblLocation.setText("Your Position: " + latitude + "," + longitude);
		} else {
			lblLocation.setText("Waiting for the location...");
		}
	}

	protected synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this)
				.addApi(LocationServices.API).build();
	}

	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Toast.makeText(getApplicationContext(),
						"This device is not supported.", Toast.LENGTH_LONG)
						.show();
				finish();
			}
			return false;
		}
		return true;
	}

	private void fetchdata() {
		MyTwitterApiClient mtac = new MyTwitterApiClient(TwitterCore
				.getInstance().getSessionManager().getActiveSession());
		Log.v("breakout", "mtac set");
		mtac.getTService().usrtml(
				Twitter.getSessionManager().getActiveSession().getUserId(), 10,
				new Callback<Response>() {
					@Override
					public void success(Result<Response> result) {
						String tstring;
						Log.v("breakpoint", "get topic success");
						try {
							tstring = ResToString.getString(result.data
									.getBody().in());
							Gson gson = new Gson();
							Log.v("breakpoint", "GSON got");
							Tweet[] topicresult = gson.fromJson(tstring,
									Tweet[].class);
							for (Tweet i : topicresult) {
								if (!(i.entities.hashtags.isEmpty())) {
									Log.v("breakpoint",
											String.valueOf(i.entities.hashtags
													.size()));
									int j = i.entities.hashtags.size();
									for (int k = 0; k < j; k++) {
										Log.v("breakpoint", String.valueOf(k));
										String tag = i.entities.hashtags.get(k).text;

										app.addtags("#" + tag);
									}

								}
							}

						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void failure(TwitterException exception) {
						Log.v("breakpoint", "api error", exception);
					}
				});
		mtac.getTService().retweets(50, new Callback<Response>() {
			@Override
			public void success(Result<Response> result) {
				String retweetstring;
				try {
					retweetstring = ResToString.getString(result.data.getBody()
							.in());
					Gson gson = new Gson();
					Tweet[] retweetresult = gson.fromJson(retweetstring,
							Tweet[].class);
					Log.v("breakpoint", "gson got");
					for (Tweet i : retweetresult) {
						if ((i.coordinates != null)) {
							Log.v("breakpoint", i.idStr);
							app.addRetweet(Long.parseLong(i.idStr));
							Log.v("breakpoint", "id added");
						}
					}
					Log.v("breakpoint", "retweet got");

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@Override
			public void failure(TwitterException arg0) {
				// TODO Auto-generated method stub

			}
		});
		mtac.getTService().fid(Twitter.getSessionManager().getActiveSession().getUserId(),false,new Callback<Response>() {
    		@Override
    		public void success(Result<Response> result) {
            	String fstring;
            	Log.v("breakpoint","get fid success");
				try {
					fstring = ResToString.getString(result.data.getBody().in());
					Log.v("breakpoint",fstring);
                Gson gson=new Gson();
                final FollowersResults fresult=gson.fromJson(fstring, FollowersResults.class);
                app.setfollowerlist(fresult.getids());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            }
           
            @Override
            public void failure(TwitterException exception) {
                Log.v("breakpoint", "api error", exception);
            }
        });
	}

	@SuppressWarnings("rawtypes")
	private void setupSpin() {
		Spinner dspin = (Spinner) findViewById(R.id.destspin);
		Spinner tspin = (Spinner) findViewById(R.id.timespin);
		ArrayAdapter dadapter = ArrayAdapter.createFromResource(this,
				R.array.distance, R.layout.my_spinner_textview);
		ArrayAdapter tadapter = ArrayAdapter.createFromResource(this,
				R.array.time, R.layout.my_spinner_textview);
		dadapter.setDropDownViewResource(R.layout.my_spinner_textview);
		tadapter.setDropDownViewResource(R.layout.my_spinner_textview);
		dspin.setAdapter(dadapter);
		tspin.setAdapter(tadapter);
		dspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				switch (pos) {
				case 0:
					dist = 5000;
					break;
				case 1:
					dist = 3000;
					break;
				case 2:
					dist = 1000;
					break;
				case 3:
					dist = 500;
					break;
				}
				app.setDist(dist);
			}

			public void onNothingSelected(AdapterView<?> parent) {
				Log.v("breakpoint", "dNothingSelected");
			}
		});
		tspin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				switch (pos) {
				case 0:
					time = 259200;
					break;
				case 1:
					time = 86400;
					break;
				case 2:
					time = 43200;
					break;
				case 3:
					time = 3600;
					break;
				}
				app.setTime(time);
			}

			public void onNothingSelected(AdapterView<?> parent) {
				Log.v("breakpoint", "tNothingSelected");
			}
		});

	}

	private void setupTNearby() {
		final Button tnb = (Button) findViewById(R.id.TNearby);
		tnb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent TopicIntent = new Intent(ChooseActivity.this,
						TopicsActivity.class);
				startActivity(TopicIntent);
			}
		});
	}

	private void setupFNearby() {
		final Button fnb = (Button) findViewById(R.id.FNearby);
		fnb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent FollowerIntent = new Intent(ChooseActivity.this,
						FollowersActivity.class);
				startActivity(FollowerIntent);

			}
		});
	}

	private void setupRNearby() {
		final Button rnb = (Button) findViewById(R.id.RNearby);
		rnb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final Intent RetweetIntent = new Intent(ChooseActivity.this,
						RetweetsActivity.class);
				startActivity(RetweetIntent);
			}
		});
	}

	private void setupSignOut() {
		final Button logoutbt = (Button) findViewById(R.id.Logout);
		logoutbt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Twitter.getSessionManager().clearActiveSession();
				SessionRecorder
						.recordSessionInactive("About: accounts deactivated");
				Toast.makeText(getApplicationContext(), "Logged Out",
						Toast.LENGTH_SHORT).show();
				ChooseActivity.this.finish();
			}
		});
	}

	public void onBackPressed() {
		new AlertDialog.Builder(this).setTitle("Really Exit?")
				.setMessage("Are you sure you want to exit?")
				.setNegativeButton(android.R.string.no, null)
				.setPositiveButton(android.R.string.yes, new OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						ChooseActivity.super.onBackPressed();
					}
				}).create().show();
	}

	protected void onStart() {
		super.onStart();
		if (mGoogleApiClient != null) {
			mGoogleApiClient.connect();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		checkPlayServices();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.i("breakpoint",
				"Connection failed: ConnectionResult.getErrorCode() = "
						+ result.getErrorCode());
	}

	@Override
	public void onConnected(Bundle arg0) {
		displayLocation();
	}

	@Override
	public void onConnectionSuspended(int arg0) {
		mGoogleApiClient.connect();
	}
}
