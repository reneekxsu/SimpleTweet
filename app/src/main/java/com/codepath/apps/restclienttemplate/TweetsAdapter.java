package com.codepath.apps.restclienttemplate;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codepath.apps.restclienttemplate.models.Tweet;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class TweetsAdapter extends RecyclerView.Adapter<TweetsAdapter.ViewHolder>{

    Context context;
    List<Tweet> tweets;

    // pass in context and list of tweets
    public TweetsAdapter(Context context, List<Tweet> tweets){
        this.context = context;
        this.tweets = tweets;
    }

    @NonNull
    @NotNull
    @Override
    // for each row, inflate layout for tweet
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet,parent,false);
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

    public void clear(){
        tweets.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Tweet> list) {
        tweets.addAll(list);
        notifyDataSetChanged();
    }


    // define viewholder
    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivProfileImage;
        TextView tvBody;
        TextView tvScreenName;
        TextView timeStamp;
        ImageView ivFirstEmbeddedImage;
        TextView tvName;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvScreenName = itemView.findViewById(R.id.tvScreenName);
            timeStamp = itemView.findViewById(R.id.timeStamp);
            ivFirstEmbeddedImage = itemView.findViewById(R.id.ivFirstEmbeddedImage);
            tvName = itemView.findViewById(R.id.tvName);
        }

        public void bind(Tweet tweet) {
            tvBody.setText(tweet.body);
            tvScreenName.setText("@" + tweet.user.screenName);
            timeStamp.setText(tweet.getRelativeTimeAgo(tweet.createdAt));
            tvName.setText(tweet.user.name);
            Glide.with(context).load(tweet.user.profileImageUrl).into(ivProfileImage);
            if (tweet.hasMedia){
                ivFirstEmbeddedImage.setVisibility(View.VISIBLE);
                Log.i("TweetsAdapter","tweet has media");
                Glide.with(context).load(tweet.firstEmbeddedImage).centerCrop().transform(new RoundedCornersTransformation(20, 5)).into(ivFirstEmbeddedImage);
            } else {
                ivFirstEmbeddedImage.setVisibility(View.GONE);
            }
        }
    }
}
