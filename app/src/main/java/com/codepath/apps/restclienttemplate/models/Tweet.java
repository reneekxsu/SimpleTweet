package com.codepath.apps.restclienttemplate.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Tweet {
    public String body;
    public String createdAt;
    public User user;
    public boolean hasMedia;
    public ArrayList<String> embeddedImages;
    public String firstEmbeddedImage;
    public long id;
    public long favCount;
    public int retweetCount;
    public boolean favorite;
    public boolean retweet;

    // empty constructor for parcelable library
    public Tweet(){}

    public static Tweet fromJson(JSONObject jsonObject) throws JSONException {
        Tweet tweet = new Tweet();
        tweet.body = jsonObject.getString("full_text");
        tweet.createdAt = jsonObject.getString("created_at");
        tweet.user = User.fromJson(jsonObject.getJSONObject("user"));
        tweet.id = jsonObject.getLong("id");
        JSONObject entities = jsonObject.getJSONObject("entities");
        tweet.favCount = jsonObject.getLong("favorite_count");
        Log.i("Tweet", "favorite count: " + tweet.favCount);
        tweet.retweetCount = jsonObject.getInt("retweet_count");
        tweet.favorite = jsonObject.getBoolean("favorited");
        tweet.retweet = jsonObject.getBoolean("retweeted");

        if (entities.has("media")){
            JSONArray media = entities.getJSONArray("media");
            tweet.hasMedia = true;
            tweet.embeddedImages = new ArrayList<>();
            for (int i=0; i<media.length(); i++){
                tweet.embeddedImages.add(media.getJSONObject(i).getString("media_url_https"));
            }
            tweet.firstEmbeddedImage = tweet.embeddedImages.get(0);
            Log.i("Tweet", "firstEmbeddedImage: " + tweet.firstEmbeddedImage);
        } else {
            tweet.hasMedia = false;
            tweet.embeddedImages = new ArrayList<>();
        }
        return tweet;
    }

    public static List<Tweet> fromJsonArray(JSONArray jsonArray) throws JSONException {
        List<Tweet> tweets = new ArrayList<>();
        for (int i=0; i<jsonArray.length(); i++){
            tweets.add(fromJson(jsonArray.getJSONObject(i)));
        }
        return tweets;
    }

    public String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.i("Tweet","time as string: " + relativeDate);
        return relativeDate;
    }

}
