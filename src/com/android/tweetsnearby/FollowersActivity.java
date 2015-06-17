package com.android.tweetsnearby;

import java.io.IOException;
import java.util.List;

import retrofit.client.Response;
import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.LoadCallback;
import com.twitter.sdk.android.tweetui.TweetViewFetchAdapter;

public class FollowersActivity extends ListActivity {
	private App app;
	private Handler handler;
	final TweetViewFetchAdapter<CompactTweetView> adapter = new TweetViewFetchAdapter<CompactTweetView>(
			FollowersActivity.this) {
		@Override
		public CompactTweetView getTweetView(Context context, Tweet tweet) {
			return new CompactTweetView(context, tweet,
					R.style.CustomTweetStyle);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler();
		Log.v("breakpoint", "entered follower");
		app = (App) getApplication();
		setContentView(R.layout.tweet_list);
		MyTwitterApiClient mtac = new MyTwitterApiClient(TwitterCore
				.getInstance().getSessionManager().getActiveSession());
		for (long i : app.getFollowerlist()) {
			Log.v("breakpoint", "followerid:" + i);
			mtac.getTService().usrtml(i, 5, new Callback<Response>() {

				@Override
				public void success(Result<Response> result) {
					String fstring;
					try {
						fstring = ResToString.getString(result.data.getBody()
								.in());
						Gson gson = new Gson();
						Tweet[] fresult = gson.fromJson(fstring, Tweet[].class);
						Log.v("breakpoint", "gson got");
						for (Tweet i : fresult) {
							if ((i.coordinates != null)) {
								Log.v("breakpoint", i.idStr);
								if ((CalcDistance.getDist(app.getLat(),
										app.getLon(),
										i.coordinates.getLatitude(),
										i.coordinates.getLongitude()) <= app
										.getDist())
										&& (CalcTime.getTimeDist(i.createdAt) <= app
												.getTime())) {
									app.addFollower(Long.parseLong(i.idStr));
									Log.v("breakpoint", "id added");
								}
							}
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void failure(TwitterException arg0) {
					// TODO Auto-generated method stub
					Log.v("breakpoint", "follower failed");

				}
			});

		}

		setUpBack();
		Runnable run = new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (app.getFollower().isEmpty()) {
					Toast.makeText(
							getApplicationContext(),
							"Nothing to show, try again or\nchange the distance and time.",
							Toast.LENGTH_SHORT).show();
					FollowersActivity.this.onBackPressed();
				} else
					setupList();
			}

		};
		handler.postDelayed(run, 2000);

	}

	private void setUpBack() {
		final ImageView back = (ImageView) findViewById(R.id.back);
		final TextView tv = (TextView) findViewById(R.id.tweetlist);
		tv.setText("Followers Nearby");
		back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
	}

	private void setupList() {
		setListAdapter(adapter);
		Log.v("breakpoint", "adapter set");
		app.followersort();
		adapter.setTweetIds(app.getFollower(),
				new LoadCallback<List<Tweet>>() {
					@Override
					public void success(List<Tweet> tweets) {
						Log.v("breakpoint", adapter.getCount() + "List Created");

						adapter.notifyDataSetChanged();
					}

					@Override
					public void failure(TwitterException exception) {
						Toast.makeText(getApplicationContext(),
								"Fail to get the list...", Toast.LENGTH_SHORT)
								.show();
					}
				});

	}

}