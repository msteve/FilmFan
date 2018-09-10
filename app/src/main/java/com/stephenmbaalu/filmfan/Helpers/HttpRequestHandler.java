package com.stephenmbaalu.filmfan.Helpers;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class HttpRequestHandler {

    private String charset = "UTF-8";
    private HttpURLConnection conn;
    private DataOutputStream wr;
    private StringBuilder result;
    private URL urlObj;
    private JSONObject jObj = null;
    private StringBuilder sbParams;
    private String paramsString;
    ///$json = file_get_contents('php://input'); URL: http://10.0.2.2/*
    //http://localhost/eiffel/get_categories.php?categories=all

    public JSONObject makeHttpRequestPOST(String url,
                                      String postData) {

            // request method is POST
        System.out.println(url);
            try {

              //  String urlData=URLEncoder.encode(postData, charset);

                urlObj = new URL(url);

                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setDoOutput(true);

                conn.setRequestMethod("POST");

                conn.setRequestProperty("content-type","application/json;charset=utf-8");
               // conn.setRequestProperty("Accept-Charset", charset);

                //.header("content-type", "application/json")

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.connect();

                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception c) {
                c.printStackTrace();
            }


        try {
            //Receive the response from the server
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Log.d("JSON Parser", "result: " + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception c) {
            c.printStackTrace();
        }

        conn.disconnect();

        // try parse the string to a JSON object
        try {
            System.out.println(result.toString());
            // System.out.println(" >>>> ");
            jObj = new JSONObject(result.toString());
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        } catch (Exception c) {
            Log.e("Exception", "Error " + c.toString());
        }

        // return JSON Object
        return jObj;
    }


    public JSONObject makeHttpRequest(String url, String method,
                                      HashMap<String, String> params) {

        sbParams = new StringBuilder();
        int i = 0;
        for (String key : params.keySet()) {
            try {
                if (i != 0) {
                    sbParams.append("&");
                }
                sbParams.append(key).append("=")
                        .append(URLEncoder.encode(params.get(key), charset));

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i++;
        }

        if (method.equals("POST")) {
            // request method is POST
            try {
                urlObj = new URL(url);

                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setDoOutput(true);

                conn.setRequestMethod("POST");

                conn.setRequestProperty("Accept-Charset", charset);

                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);

                conn.connect();

                paramsString = sbParams.toString();

                wr = new DataOutputStream(conn.getOutputStream());
                wr.writeBytes(paramsString);
                wr.flush();
                wr.close();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception c) {
                c.printStackTrace();
            }
        } else if (method.equals("GET")) {
            // request method is GET

            if (sbParams.length() != 0) {
                url += "?" + sbParams.toString();
            }

          //  System.out.println(url);

            try {
                urlObj = new URL(url);

                conn = (HttpURLConnection) urlObj.openConnection();

                conn.setDoOutput(false);

                conn.setRequestMethod("GET");

                conn.setRequestProperty("Accept-Charset", charset);

                conn.setConnectTimeout(15000);

                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception c) {
                c.printStackTrace();
            }

        }

        try {
            //Receive the response from the server
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            Log.d("JSON Parser", "result: " + result.toString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception c) {
            c.printStackTrace();
        }

        conn.disconnect();

        // try parse the string to a JSON object
        try {
            System.out.println(result.toString());
            // System.out.println(" >>>> ");
            jObj = new JSONObject(result.toString());
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        } catch (Exception c) {
            Log.e("Exception", "Error " + c.toString());
        }

        // return JSON Object
        return jObj;
    }

}
