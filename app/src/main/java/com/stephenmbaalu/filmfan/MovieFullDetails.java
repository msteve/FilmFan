package com.stephenmbaalu.filmfan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.stephenmbaalu.filmfan.Helpers.AppConstants;
import com.stephenmbaalu.filmfan.Helpers.AppUtil;
import com.stephenmbaalu.filmfan.Helpers.GetSessionIdAsync;
import com.stephenmbaalu.filmfan.Helpers.HttpRequestHandler;
import com.stephenmbaalu.filmfan.Helpers.NowPlayingAdapter;
import com.stephenmbaalu.filmfan.Models.Fullmovie;
import com.stephenmbaalu.filmfan.Models.Genres;
import com.stephenmbaalu.filmfan.Models.MoviePlaying;
import com.stephenmbaalu.filmfan.Models.Similar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class MovieFullDetails extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener {

    private String movie_id;
    private ImageView imagePosterMain;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView recycler_viewRelated;
    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_full_details_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        movie_id=getIntent().getStringExtra("id_");
        sharedpreferences = getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);

        imagePosterMain=findViewById(R.id.imagePosterMain);
        recycler_viewRelated=findViewById(R.id.recycler_viewRelated);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler_viewRelated.setLayoutManager(layoutManager);
        recycler_viewRelated.setItemAnimator(new DefaultItemAnimator());

       // recycler_viewRelated.setAdapter(imagesAdapter);




         collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

       // collapsingToolbarLayout.setTitle("Some title");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header=navigationView.getHeaderView(0);
        //TextView textViewSideName= (TextView) header.findViewById(R.id.textViewSideName);
       // TextView textViewSidePhone= (TextView) header.findViewById(R.id.textViewSidePhone);


        RatingBar ratingBar=findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {

                final String rating=String.valueOf(v);

                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(MovieFullDetails.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(MovieFullDetails.this);
                }
                builder.setTitle("Submit rating")
                        .setMessage("Are you sure you want to submit yo rating?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Log.d("AMH","subimit rating");
                                //Do rating
                                //check if Logged In
                                if(sharedpreferences.contains(AppConstants.HAS_SESSION_ID)){

                                    if(sharedpreferences.getBoolean(AppConstants.HAS_SESSION_ID,false)){

                                        new DoRatingAsync(MovieFullDetails.this).execute(rating);

                                    }else {
                                        Toast.makeText(MovieFullDetails.this, "Login required", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    Toast.makeText(MovieFullDetails.this, "Login required", Toast.LENGTH_SHORT).show();
                                }


                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                                Log.d("AMH","Cancel rating ");

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
        });



        new LoadFullDetailsAsync(this).execute();
    }

    public void addToMyFavorites(View vv){
        //Add to favorites

        if(sharedpreferences.contains(AppConstants.EXPIRES)){

            String expires=sharedpreferences.getString(AppConstants.EXPIRES,null);

            if(!AppUtil.hasTokenExpired(expires)){

                //check if we have session_id   editor.putBoolean(AppConstants.HAS_SESSION_ID, false);
                if(sharedpreferences.contains(AppConstants.HAS_SESSION_ID)){

                    if(sharedpreferences.getBoolean(AppConstants.HAS_SESSION_ID,false)){


                        //Add to Favorites


                    }else{
                        //Get session ID
                        if (AppUtil.checkInternetConenction(this)){
                            new GetSessionIdAsync(this).execute();
                        }else {

                            Toast.makeText(this, "Check connection", Toast.LENGTH_SHORT).show();

                        }
                    }

                }else{
                    //Get Session Id
                    if (AppUtil.checkInternetConenction(this)) {
                        new GetSessionIdAsync(this).execute();
                    }else {

                        Toast.makeText(this, "Check connection", Toast.LENGTH_SHORT).show();

                    }

                }


            }else {

                Toast.makeText(this, "Login is required", Toast.LENGTH_SHORT).show();
            }

            // String request_token=sharedpreferences.getString(AppConstants.REQUEST_TOKEN,null);

        }else {
            Toast.makeText(this, "Login is required", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            // Handle Login
            Intent intent=new Intent(this,Login.class);
            startActivity(intent);

        } else if (id == R.id.nav_favorites) {

            //check If Logged in


            if(sharedpreferences.contains(AppConstants.EXPIRES)){

                String expires=sharedpreferences.getString(AppConstants.EXPIRES,null);

                if(!AppUtil.hasTokenExpired(expires)){

                    //check if we have session_id   editor.putBoolean(AppConstants.HAS_SESSION_ID, false);
                    if(sharedpreferences.contains(AppConstants.HAS_SESSION_ID)){

                        if(sharedpreferences.getBoolean(AppConstants.HAS_SESSION_ID,false)){

                            //Go to to my favourities page else,
                            Intent intent=new Intent(this,MyFavorites.class);
                            intent.putExtra(AppConstants.SESSION_ID,sharedpreferences.getString(AppConstants.SESSION_ID,null));
                            startActivity(intent);
                        }else{
                            //Get session ID
                            new GetSessionIdAsync(this).execute();
                        }

                    }else{
                        //Get Session Id
                        new GetSessionIdAsync(this).execute();

                    }


                }else {

                    Toast.makeText(this, "Login is required", Toast.LENGTH_SHORT).show();
                }

                // String request_token=sharedpreferences.getString(AppConstants.REQUEST_TOKEN,null);

            }else {
                Toast.makeText(this, "Login is required", Toast.LENGTH_SHORT).show();
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class DoRatingAsync extends AsyncTask<String, String, JSONObject> {

        private Context context;
        // private String selectedItem;
        private ProgressDialog progressDialog;

        public DoRatingAsync(Context context) {
            this.context = context;
            // this.selectedItem = selectedItem;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Loading..");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            // progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {

            try {
                HttpRequestHandler httpRequestHandler = new HttpRequestHandler();
                //movie/{movie_id}/rating
                String session_id = sharedpreferences.getString(AppConstants.SESSION_ID, null);
                String url = AppConstants.APIBASE_URL + "movie/" + movie_id + "/rating?api_key=" + AppConstants.APIKEY_V3 + "&session_id=" + session_id;
                HashMap<String, String> params = new HashMap<>();
                //params.put("api_key", AppConstants.APIKEY_V3);
                params.put("value", strings[0]);
                String data = new Gson().toJson(params);
                JSONObject jsonObject = httpRequestHandler.makeHttpRequestPOST(url, data);
                Log.d("Json", jsonObject != null ? jsonObject.toString() : "Null ");
                return jsonObject;
            }catch (Exception cc){
                cc.printStackTrace();
            }
            return  null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            if (jsonObject != null){
                // Log.d("MSG","Some Data "+jsonObject.toString());

                try {
                    int status_code=jsonObject.getInt("status_code");

                    if (status_code==1){
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                    }else {

                        Toast.makeText(context, "Sorry, Something went wrong", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Sorry, Something went wrong", Toast.LENGTH_SHORT).show();
                }

            }else {
                Log.d("ERROR","No data returned");
                Toast.makeText(context, "Sorry, Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class LoadFullDetailsAsync extends AsyncTask<String, String, JSONObject> {

        private Context context;
        // private String selectedItem;
        private ProgressDialog progressDialog;

        public LoadFullDetailsAsync(Context context) {
            this.context = context;
            // this.selectedItem = selectedItem;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog= new ProgressDialog(context);
            progressDialog.setIndeterminate(true);
            progressDialog.setTitle("Loading..");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            // progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... strings) {
            HttpRequestHandler httpRequestHandler = new HttpRequestHandler();
            String url= AppConstants.APIBASE_URL+"movie/"+movie_id;
            HashMap<String, String> params = new HashMap<>();
            params.put("api_key", AppConstants.APIKEY_V3);
            params.put("append_to_response", "similar");
            JSONObject jsonObject = httpRequestHandler.makeHttpRequest(url, "GET", params);
            Log.d("Json", jsonObject != null ? jsonObject.toString() : "Null ");
            return jsonObject;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            if (jsonObject != null){
                // Log.d("MSG","Some Data "+jsonObject.toString());
                try {

                    Fullmovie fullmovie=new Gson().fromJson(jsonObject.toString(),Fullmovie.class);

                    String url= AppUtil.getfullImageUrl(AppConstants.Poster_Sizes.w154.toString(),fullmovie.getPoster_path());
                    Glide.with(context).load(url).into(imagePosterMain);

                    collapsingToolbarLayout.setTitle(fullmovie.getTitle());
                    TextView textRatingAndDate=findViewById(R.id.textRatingAndDate);
                    textRatingAndDate.setText("Vote: "+fullmovie.getVote_count()+" | Released: "+fullmovie.getRelease_date());

                    TextView textFullDetails=findViewById(R.id.textFullDetails);
                    textFullDetails.setText(fullmovie.getOverview());
                    //textGenre
                   TextView textGenre= findViewById(R.id.textGenre);

                   String genreSt="";
                   for (Genres genre:fullmovie.getGenres()){
                       genreSt=genre.getName()+", ";
                   }
                   textGenre.setText(genreSt.substring(0,genreSt.lastIndexOf(",")));

                    Similar similar=fullmovie.getSimilar();

                    //USe the Now playing Adapter

                    NowPlayingAdapter nowPlayingAdapter=new NowPlayingAdapter(similar.getResults(),context);
                    recycler_viewRelated.setAdapter(nowPlayingAdapter);





                } catch (Exception e) {
                    e.printStackTrace();
                }


            }else {
                Log.d("ERROR","No data returned");
            }
        }
    }

}
