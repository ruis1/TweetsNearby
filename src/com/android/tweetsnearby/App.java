package com.android.tweetsnearby;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import android.app.Application;
import android.util.Log;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

public class App extends Application {
	private static App singleton;
	private TwitterAuthConfig authConfig;
	private static final String TWITTER_KEY = "kQ0sqziPn8zMX8raUsZ9o1Npm";
	private static final String TWITTER_SECRET = "kPgL2saVtOERNAcEQC1PwMBZOislA8sJETm8rjkCo6iTPFUJD6";
	private double lat, lon;
	private int dist, time;
	private List<Long> retweet,retweetid, follower, followerlist, topic; 
	private List<String> tags;
	private String tagstr=null;

	public static App getInstance() {
		return singleton;
	}

	public List<Long> getRetweet() {
		return retweet;
	}

	public List<Long> getRetweetID() {
		return retweetid;
	}

	public List<Long> getFollower() {
		return follower;
	}
	
	public List<Long> getFollowerlist() {
		return followerlist;
	}

	public List<Long> getTopic() {
		return topic;
	}
	public List<String> getTags() {
		return tags;
	}
	
	public String getTagtext(int i){
		return tags.get(i);
	}
	
	public void addRetweet(long rt) {
		if (!retweet.contains(rt))
			this.retweet.add(rt);
	}
	
	public void addRetweetID(long rtid) {
		if (!retweetid.contains(rtid))
			this.retweetid.add(rtid);
	}

	public void addFollower(long ft) {
		if (!follower.contains(ft))
			this.follower.add(ft);
	}
	
	public void addFollowerlist(long ftl) {
		if (!followerlist.contains(ftl))
			this.followerlist.add(ftl);
	}

	public void addtopic(long tt) {
		if (!topic.contains(tt))
			this.topic.add(tt);
	}
	
	public void addtags(String tag) {
		if ((!tags.contains(tag))&&(tags.size()<3))
		{
			if (tags.isEmpty())
				tagstr = tag;
			else tagstr = tagstr +" OR "+tag;
			this.tags.add(tag);
			Log.v("breakpoint","#"+tag+" got");
			Log.v("breakpoint",tagstr);
		}
	}
	
	public void setfollowerlist(List<Long> followerlst){
		this.followerlist=followerlst;
	}

	public String gettagstr(){
		return tagstr;
	}
	
	public int getDist() {
		return dist;
	}

	public int getTime() {
		return time;
	}

	public void setDist(int distance) {
		this.dist = distance;
		follower.clear();
		topic.clear();
		Log.v("breakpoint","dist set to "+String.valueOf(distance));
	}

	public void setTime(int timex) {
		this.time = timex;
		follower.clear();
		topic.clear();
		Log.v("breakpoint","time set to "+String.valueOf(timex));
	}
	
	public void retsort(){
		Collections.sort(retweet,Collections.reverseOrder());
	}
	
	public void followersort(){
		Collections.sort(follower,Collections.reverseOrder());
	}
	
	public void topicsort(){
		Collections.sort(topic,Collections.reverseOrder());
	}

	public double getLat() {
		return lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLat(double latitude) {
		this.lat = latitude;
	}

	public void setLon(double longitude) {
		this.lon = longitude;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		singleton = this;
		authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
		Fabric.with(this, new Twitter(authConfig));
		retweet = new ArrayList<Long>();
		retweetid = new ArrayList<Long>();
		follower = new ArrayList<Long>();
		followerlist = new ArrayList<Long>();
		topic = new ArrayList<Long>();
		tags = new ArrayList<String>();
		tagstr=null;
	}
}
