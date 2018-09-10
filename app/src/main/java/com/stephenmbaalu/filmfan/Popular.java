package com.stephenmbaalu.filmfan;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.stephenmbaalu.filmfan.Helpers.AppConstants;
import com.stephenmbaalu.filmfan.Helpers.AppUtil;
import com.stephenmbaalu.filmfan.Helpers.GridSpacingItemDecoration;
import com.stephenmbaalu.filmfan.Helpers.HttpRequestHandler;
import com.stephenmbaalu.filmfan.Helpers.NowPlayingAdapter;
import com.stephenmbaalu.filmfan.Models.MoviePlaying;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class Popular extends Fragment {


    private NowPlayingAdapter nowPlayingAdapter;
    private RecyclerView recyclerView;

    public Popular() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_popular, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        //
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, AppUtil.dpToPx(10,getActivity()), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //  recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        if(AppUtil.checkInternetConenction(getActivity())){
            new LoadPopularAys(getActivity()).execute();
        }else {
            Toast.makeText(getActivity(), "Check Connection", Toast.LENGTH_SHORT).show();
        }


        return view;
    }

    private class LoadPopularAys extends AsyncTask<String, String, JSONObject> {

        private Activity context;
        // private String selectedItem;
        private ProgressDialog progressDialog;

        public LoadPopularAys(Activity context) {
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
            String url= AppConstants.APIBASE_URL+"movie/popular";
            HashMap<String, String> params = new HashMap<>();
            params.put("api_key", AppConstants.APIKEY_V3);
            params.put("language", AppConstants.DEFAULT_LANG);//page=1
            params.put("page", "1");
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
                    JSONArray resultsArray=jsonObject.getJSONArray("results");

                    //new TypeToken<ArrayList<MoviePlaying>>() {}.getType()
                    MoviePlaying[]listMovies=new Gson().fromJson(resultsArray.toString(),
                            MoviePlaying[].class);

                    Arrays.sort(listMovies, new Comparator<MoviePlaying>() {
                        @Override
                        public int compare(MoviePlaying moviePlaying, MoviePlaying t1) {
                            return moviePlaying.getTitle().compareTo(t1.getTitle());
                        }
                    });

                    nowPlayingAdapter = new NowPlayingAdapter(listMovies, context);
                    recyclerView.setAdapter(nowPlayingAdapter);



                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }else {
                Log.d("ERROR","No data returned");
            }
        }
    }



}
