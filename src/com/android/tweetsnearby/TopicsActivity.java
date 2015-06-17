package com.android.tweetsnearby;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Search;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.LoadCallback;
import com.twitter.sdk.android.tweetui.TweetViewFetchAdapter;

public class TopicsActivity extends ListActivity {

	private App app;
	private Handler handler;
	TweetViewFetchAdapter<CompactTweetView> adapter = new TweetViewFetchAdapter<CompactTweetView>(
			TopicsActivity.this) {
		@Override
		public CompactTweetView getTweetView(Context context, Tweet tweet) {
			return new CompactTweetView(context, tweet,
					R.style.CustomTweetStyle);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v("breakpoint", "entered Friend");
		handler = new Handler();
		app = (App) getApplication();
		setContentView(R.layout.tweet_list);
		Log.v("breakpoint", "view set");
		setUpBack();
		Log.v("breakpoint", "Backuped");
		String query = "";
		query = app.gettagstr();
		Log.v("breakpoint", query);
		String geo = String.valueOf(app.getLat()) + ","
				+ String.valueOf(app.getLon()) + ","
				+ String.valueOf(app.getDist() / 1000) + "km";
		Log.v("breakpoint", geo);
		MyTwitterApiClient mtac = new MyTwitterApiClient(TwitterCore
				.getInstance().getSessionManager().getActiveSession());
		Log.v("breakout", "mtac set");
		mtac.getTService().topic(query, geo, new Callback<Search>() {
			@Override
			public void success(Result<Search> result) {
				List<Tweet> topicresult = result.data.tweets;
				Log.v("breakpoint", "topic gson got");
				for (Tweet i : topicresult) {
					long td = CalcTime.getTimeDist(i.createdAt);
					Log.v("breakout",
							String.valueOf(td) + "VS"
									+ String.valueOf(app.getTime()));
					if (td <= app.getTime()) {
						Log.v("breakpoint", i.idStr);
						app.addtopic(Long.parseLong(i.idStr));
						Log.v("breakpoint", "id added");
					}
				}

			}

			@Override
			public void failure(TwitterException arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		Runnable run = new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (app.getTopic().isEmpty()) {
					Toast.makeText(
							getApplicationContext(),
							"Nothing to show, try again or\nchange the distance and time.",
							Toast.LENGTH_SHORT).show();
					TopicsActivity.this.onBackPressed();
				} else
					setupList();
			}
			
		};

		handler.postDelayed(run, 2000);
		

	}

	private void setUpBack() {
		final ImageView back = (ImageView) findViewById(R.id.back);
		final TextView tv = (TextView) findViewById(R.id.tweetlist);
		tv.setText("Topics Nearby");
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
		app.topicsort();
		adapter.setTweetIds(app.getTopic(), new LoadCallback<List<Tweet>>() {
			@Override
			public void success(List<Tweet> tweets) {
				Log.v("breakpoint", adapter.getCount() + "List Created");
			}

			@Override
			public void failure(TwitterException exception) {
				Toast.makeText(getApplicationContext(),
						"Fail to get the list...", Toast.LENGTH_SHORT).show();
			}
		});

	}

}