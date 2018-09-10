package com.stephenmbaalu.filmfan.Helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.stephenmbaalu.filmfan.MyFavorites;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class GetSessionIdAsync extends AsyncTask<String, String, String> {

    private Context context;
    private ProgressDialog progressDialog;
    private  SharedPreferences sharedpreferences;

    public GetSessionIdAsync(Context context) {
        this.context = context;
        this.sharedpreferences = context.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
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
            context.startActivity(intent);

        }else {


            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(AppConstants.HAS_SESSION_ID, false);
            editor.commit();
            Toast.makeText(context, "Access denied", Toast.LENGTH_SHORT).show();
            Log.d("ERROR","Acess denied");
        }
    }
}
