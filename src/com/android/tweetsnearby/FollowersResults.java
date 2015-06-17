package com.android.tweetsnearby;

import java.util.List;

import com.google.gson.annotations.SerializedName;

//follower search results
public class FollowersResults {

	@SerializedName("ids")
	private List<Long> ids;
	
	@SerializedName("next_cursor")
	private int next_cursor;
	
	@SerializedName("previous_cursor")
	private int previous_cursor;
	
	@SerializedName("next_cursor_str")
	private String next_cursor_str;
	
	@SerializedName("previous_cursor_str")
	private String previous_cursor_str;


	public List<Long> getids() {
		return ids;
	}

}