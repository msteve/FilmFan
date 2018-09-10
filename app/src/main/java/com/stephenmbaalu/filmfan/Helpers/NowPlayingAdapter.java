package com.stephenmbaalu.filmfan.Helpers;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.stephenmbaalu.filmfan.Models.MoviePlaying;
import com.stephenmbaalu.filmfan.MovieFullDetails;
import com.stephenmbaalu.filmfan.R;


public class NowPlayingAdapter extends  RecyclerView.Adapter<NowPlayingAdapter.MovieViewHolder> {

   // Typeface font;
    private MoviePlaying [] moviePlayings;
    private Context context;


    public class MovieViewHolder extends RecyclerView.ViewHolder {
        public TextView textTitle, textReleaseDate,textVoting;
        public ImageView imageView;

        public MovieViewHolder(View view) {
            super(view);
            textTitle = (TextView) view.findViewById(R.id.textTitle);
            textReleaseDate = (TextView) view.findViewById(R.id.textReleasedate);
            textVoting= (TextView) view.findViewById(R.id.textVoteAver);
            imageView=view.findViewById(R.id.imagePoster);

        }
    }


    public NowPlayingAdapter(MoviePlaying [] moviePlayings, Context context) {
        this.moviePlayings = moviePlayings;
        this.context=context;
       // font=Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf" );
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.nowplaying_item, parent, false);

        return new MovieViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

       final MoviePlaying movie =moviePlayings[position];

        holder.textTitle.setText(movie.getTitle());
        holder.textReleaseDate.setText(movie.getRelease_date());
        holder.textVoting.setText(String.valueOf(movie.getVote_average()));

        // loading album cover using Glide library
        //
        String url=AppUtil.getfullImageUrl(AppConstants.Poster_Sizes.w154.toString(),movie.getPoster_path());
        Glide.with(context).load(url).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Clicked <>>", Toast.LENGTH_SHORT).show();

                String id=String.valueOf(movie.getId());

                Log.d("ID",id);

              //  viewFullDetails(movie);

                Intent intent=new Intent(context, MovieFullDetails.class);
                intent.putExtra("id_",id);
                context.startActivity(intent);

            }
        });

    }




    @Override
    public int getItemCount() {
        return moviePlayings.length;
    }


}

