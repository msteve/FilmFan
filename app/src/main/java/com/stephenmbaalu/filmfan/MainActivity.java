package com.stephenmbaalu.filmfan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.stephenmbaalu.filmfan.Helpers.AppConstants;
import com.stephenmbaalu.filmfan.Helpers.AppUtil;
import com.stephenmbaalu.filmfan.Helpers.GetSessionIdAsync;
import com.stephenmbaalu.filmfan.Helpers.HttpRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    private ViewPager viewPager;
    private TabLayout tabLayout;
    private  SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedpreferences = getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        //textUserName


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);



    }

    private void setupViewPager(ViewPager viewPager) {
        MainctivityPageAdapter adapter = new MainctivityPageAdapter(getSupportFragmentManager());
       NowPlaying nowplaying = new NowPlaying();
        Popular popular= new Popular();
        TopRated topRated = new TopRated();
        AllMovies allMovies = new AllMovies();
        adapter.addFragment(nowplaying, "Now Playing");
        adapter.addFragment(popular, "Popular");
        adapter.addFragment(topRated, "Top Rated");
        adapter.addFragment(allMovies, "Upcoming");


        viewPager.setAdapter(adapter);
       // int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
       // viewPager.setOffscreenPageLimit(limit);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    class MainctivityPageAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public MainctivityPageAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
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
            //Go to Favourites Page

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


  /*  private class GetSessionIdAsyn extends AsyncTask<String, String, String> {

        private Context context;
        private ProgressDialog progressDialog;

        public GetSessionIdAsyn(Context context) {
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
        protected String doInBackground(String... strings) {


            //JSONObject jsonObject=null;
            try {
                String request_token=sharedpreferences.getString(AppConstants.REQUEST_TOKEN,null);

                HttpRequestHandler httpRequestHandler = new HttpRequestHandler();
                //Request Token First
                String charset = "UTF-8";
                String url= AppConstants.APIBASE_URL+"authentication/session/new?api_key="+ AppConstants.APIKEY_V3;
                HashMap<String, String> params = new HashMap<>();
                params.put("request_token", request_token);
                String postData=new Gson().toJson(params);
                System.out.println(postData);

                JSONObject jsonObject = httpRequestHandler.makeHttpRequestPOST(url, postData);
                Log.d("Json", jsonObject != null ? jsonObject.toString() : "Null ");


                boolean success=jsonObject.getBoolean("success");

                if(success){
                    //save Token ans expires to shared Prefered

                    String session_id=jsonObject.getString("session_id");
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(AppConstants.SESSION_ID, session_id);
                    editor.putBoolean(AppConstants.HAS_SESSION_ID, true);
                    editor.commit();
                    return  String.valueOf(true);


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }catch (Exception cc){

                cc.printStackTrace();
            }

            return String.valueOf(false);
        }

        @Override
        protected void onPostExecute(String isSucess) {
            super.onPostExecute(isSucess);
            progressDialog.dismiss();
            if (Boolean.valueOf(isSucess)){

                //Go to to my favourities page else,
                Intent intent=new Intent(context,MyFavorites.class);
                intent.putExtra(AppConstants.SESSION_ID,sharedpreferences.getString(AppConstants.SESSION_ID,null));
                startActivity(intent);

            }else {


                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(AppConstants.HAS_SESSION_ID, false);
                editor.commit();
                Toast.makeText(context, "Access denied", Toast.LENGTH_SHORT).show();
                Log.d("ERROR","Acess denied");
            }
        }
    }*/

}
