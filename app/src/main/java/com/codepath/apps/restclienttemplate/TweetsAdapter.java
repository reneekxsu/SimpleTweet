package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder> {

    Context context;
    List<Tweet> tweets;
    TwitterClient client;


    // pass in context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets, TwitterClient client) {
        this.context = context;
        this.tweets = tweets;
        this.client = client;
    }

    @NonNull
    @NotNull
    @Override
    // for each row, inflate layout for tweet
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    // bind values based on position of element
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        // get the data at position
        Tweet tweet = tweets.get(position);

        // bind the tweet with view holder
        holder.bind(tweet);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }

    public void clear() {
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }


    // define viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView timeStamp;
        ImageView ivFirstEmbeddedImage;
        TextView tvName;
        TextView tvFavCount;
        Button btnFav;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            timeStamp = itemView.findViewById(R.id.timeStamp);
            ivFirstEmbeddedImage = itemView.findViewById(R.id.ivFirstEmbeddedImage);
            tvName = itemView.findViewById(R.id.tvName);
            tvFavCount = itemView.findViewById(R.id.tvFavCount);
            btnFav = itemView.findViewById(R.id.btnFavorite);

            itemView.setOnClickListener(this);
        }

        public void bind(final Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText("@" + tweet.user.screenName);
            timeStamp.setText(tweet.getRelativeTimeAgo(tweet.createdAt));
            tvName.setText(tweet.user.name);
            tvFavCount.setText(Long.toString(tweet.favCount));
            Glide.with(context).load(tweet.user.profileImageUrl).circleCrop().into(ivProfileImage);
            if (tweet.hasMedia) {
                ivFirstEmbeddedImage.setVisibility(View.VISIBLE);
                Log.i("TweetsAdapter", "tweet has media");
                Glide.with(context).load(tweet.firstEmbeddedImage).centerCrop().transform(new RoundedCornersTransformation(20, 5)).into(ivFirstEmbeddedImage);
            } else {
                ivFirstEmbeddedImage.setVisibility(View.GONE);
            }
            if (tweet.favorite){
                btnFav.setBackground(context.getDrawable(R.drawable.ic_vector_heart));
            } else {
                btnFav.setBackground(context.getDrawable(R.drawable.ic_vector_heart_stroke));
            }
            btnFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tweet.favorite){
                        // unlike tweet
                        client.unFavTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("TweetsAdapter","onSuccess to favorite tweet");
                                btnFav.setBackground(context.getDrawable(R.drawable.ic_vector_heart_stroke));
                                tweet.favCount--;
                                tvFavCount.setText(tweet.favCount+"");
                                tweet.favorite = false;
                                try {
                                    Tweet tweet = Tweet.fromJson(json.jsonObject);
                                    Log.i("TweetsAdapter","Unfavorited tweet says: " + tweet.body);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("TweetsAdapter","onFailure to unfavorite tweet",throwable);
                            }
                        });
                    } else {
                        // like tweet
                        client.favTweet(tweet.id, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                Log.i("TweetsAdapter","onSuccess to favorite tweet");
                                btnFav.setBackground(context.getDrawable(R.drawable.ic_vector_heart));
                                tweet.favCount++;
                                tvFavCount.setText(tweet.favCount + "");
                                tweet.favorite = true;
                                try {
                                    Tweet tweet = Tweet.fromJson(json.jsonObject);
                                    Log.i("TweetsAdapter","Favorited tweet says: " + tweet.body);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                Log.e("TweetsAdapter","onFailure to favorite tweet",throwable);
                            }
                        });
                    }
                }
            });
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Tweet tweet = tweets.get(position);
                Intent intent = new Intent(context, TweetDetailsActivity.class);
                intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
                context.startActivity(intent);
            }
        }
    }
}
