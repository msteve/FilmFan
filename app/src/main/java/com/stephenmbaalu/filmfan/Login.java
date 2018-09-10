package com.stephenmbaalu.filmfan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.stephenmbaalu.filmfan.Models.Similar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class Login extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private  SharedPreferences sharedpreferences;
    private int REQUESTCODE=200;
    private   NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_drawer);
         sharedpreferences = getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       // get

       DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

         navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(AppUtil.checkInternetConenction(this)){

            new GetRequestTokenAsync(this).execute();
        }else {
            Toast.makeText(this, "Please check connection", Toast.LENGTH_SHORT).show();
        }




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



            //Go to Favourites Page

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public  void completeLogin(View view){

       String request_token=sharedpreferences.getString(AppConstants.REQUEST_TOKEN,null);
        String url= "https://www.themoviedb.org/authenticate/"+request_token;
        //HashMap<String, String> params = new HashMap<>();
        //params.put("api_key", AppConstants.APIKEY_V3);

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));

        PackageManager packageManager = this.getPackageManager();
        if (i.resolveActivity(packageManager) != null) {
            startActivityForResult(i,REQUESTCODE);
        } else {
            Toast.makeText(this, "Sorry Action can not be completed", Toast.LENGTH_SHORT).show();
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUESTCODE){
            //try to get the session ID,
            new DoLoginAsync(this).execute();

        }else {
            Log.d("MSG","Not known request");
        }

    }

    private class GetRequestTokenAsync extends AsyncTask<String, String, JSONObject> {

        private Context context;
        // private String selectedItem;
        private ProgressDialog progressDialog;

        public GetRequestTokenAsync(Context context) {
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

           // String username=strings[0],password=strings[1];
            HttpRequestHandler httpRequestHandler = new HttpRequestHandler();
            //Request Token First
            String url= AppConstants.APIBASE_URL+"authentication/token/new";
           HashMap<String, String> params = new HashMap<>();
            params.put("api_key", AppConstants.APIKEY_V3);

            JSONObject jsonObject = httpRequestHandler.makeHttpRequest(url, "GET", params);
            Log.d("Json", jsonObject != null ? jsonObject.toString() : "Null ");


            try {
                boolean success=jsonObject.getBoolean("success");

                if(success){
                    //save Token ans expires to shared Prefered
                    String expires=jsonObject.getString("expires_at");
                    String request_token=jsonObject.getString("request_token");

                    //change Expires to EAT
                //    boolean hasTimeExpired=AppUtil.hasTokenExpired(expires);
                   // Log.d("MSG","DATA "+hasTimeExpired);



                    //sharedpreferences.contains(AppConstants.KEY_PHONE)
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(AppConstants.REQUEST_TOKEN, request_token);
                    editor.putString(AppConstants.EXPIRES, expires);

                    editor.commit();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            //If successful the do login



            return jsonObject;
        }


        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            progressDialog.dismiss();
            if (jsonObject != null){
                // Log.d("MSG","Some Data "+jsonObject.toString());


            }else {
                Log.d("ERROR","No data returned");
            }
        }
    }


    private class DoLoginAsync extends AsyncTask<String, String, String> {

        private Context context;
        // private String selectedItem;
        private ProgressDialog progressDialog;

        public DoLoginAsync(Context context) {
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

                Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                // get menu from navigationView
                Menu menu = navigationView.getMenu();
                // find MenuItem you want to change
                MenuItem nav_login = menu.findItem(R.id.nav_login);
                nav_login.setTitle("Logged In");

            }else {


                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(AppConstants.HAS_SESSION_ID, false);
                editor.commit();
                Toast.makeText(context, "Access denied", Toast.LENGTH_SHORT).show();
                Log.d("ERROR","Acess denied");
            }
        }
    }


}
