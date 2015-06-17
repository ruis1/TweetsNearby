package com.android.tweetsnearby;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Session;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.models.Search;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public class MyTwitterApiClient extends TwitterApiClient {

    public MyTwitterApiClient(Session session) {
        super(session);
    }
    public TService getTService() {
        return getService(TService.class);
    }
}

interface TService {
    @GET("/1.1/followers/ids.json")
    void fid(@Query("user_id") long userid,@Query("stringify_ids") boolean stringify, Callback<Response> cb);
    @GET("/1.1/statuses/retweets_of_me.json")
    void retweets(@Query("count") int count,Callback<Response> cb);
    @GET("/1.1/statuses/retweets/{id}.json")
    void retweetid(@Path("id") String id, Callback<Response> cb);
    @GET("/1.1/statuses/user_timeline.json")
    void usrtml(@Query("user_id") long userid, @Query("count") int count, Callback<Response> cb);
    @GET("/1.1/search/tweets.json")
    void topic(@Query("q") String query, @Query("geocode") String geo, Callback<Search> cb);
}
