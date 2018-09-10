package com.stephenmbaalu.filmfan;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.stephenmbaalu.filmfan.Helpers.HttpRequestHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MyFavorites extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_favorites);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Load the account Id from the sesssion Id
        new GetAccountIDAsync(this).execute();


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
        getMenuInflater().inflate(R.menu.my_favorites, menu);
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
            // Handle the camera action
        } else if (id == R.id.nav_favorites) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class GetAccountIDAsync extends AsyncTask<String, String, String> {

        private Context context;
        private ProgressDialog progressDialog;

        public GetAccountIDAsync(Context context) {
            this.context = context;
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

            try {

                HttpRequestHandler httpRequestHandler = new HttpRequestHandler();
                //Request Token First
                String charset = "UTF-8";
                String url= AppConstants.APIBASE_URL+"account";//?api_key="+ AppConstants.APIKEY_V3;
                HashMap<String, String> params = new HashMap<>();
                params.put("api_key", AppConstants.APIKEY_V3);


                JSONObject jsonObject = httpRequestHandler.makeHttpRequest(url, "GET",params);
                Log.d("Json", jsonObject != null ? jsonObject.toString() : "Null ");

                String id=jsonObject.getString("id");
                String username=jsonObject.getString("username");


                    SharedPreferences sharedpreferences = getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(AppConstants.ACCOUNT_ID, id);
                    editor.putString(AppConstants.USERNAME, username);
                    editor.commit();

                    //Then Try gettting favourites



                    return  String.valueOf(true);



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

               /* Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                // get menu from navigationView
                Menu menu = navigationView.getMenu();
                // find MenuItem you want to change
                MenuItem nav_login = menu.findItem(R.id.nav_login);
                nav_login.setTitle("Logged In");*/

            }else {
                Toast.makeText(context, "Access denied", Toast.LENGTH_SHORT).show();
                Log.d("ERROR","Acess denied");
            }
        }
    }

}
