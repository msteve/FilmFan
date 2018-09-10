package com.stephenmbaalu.filmfan.Helpers;

public class AppConstants {

    public static String APIKEY_V3="f2e45fc8a7ec280a11455f994a786f91";
    public static String APIKEY_V4="eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJmMmU0NWZjOGE3ZWMyODBhMTE0NTVmOTk0YTc4NmY5MSIsInN1YiI6IjViOTM2YjAzMGUwYTI2MTk3NTAwMzlhNyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.zKrOj0_FxXezLMXU74TzRDVXi1bYss_-jO-DGuSN-ys";

    public static String APIBASE_URL="https://api.themoviedb.org/3/";
    public  static String DEFAULT_LANG="en-US";

    public static String FILE_BASEURL="https://image.tmdb.org/t/p/";

    public static String PREF_NAME="com.steve.moviedb";

    public static String REQUEST_TOKEN="request_token";
    public static String EXPIRES="expires_at";
    public static String SESSION_ID="session_id";//HAS_SESSION_ID
    public static String HAS_SESSION_ID="has_session_id";
    public static String USERNAME="username_";
    public static String ACCOUNT_ID="account_id";

    public static enum BackDrop_Sizes{
        w300,w780,w1280,original

    }

    public static enum Logo_Sizes{
        w45,w92, w154, w185, w300, w500, original

    }
    public static enum Poster_Sizes{
        w92,w154, w185, w300, w500,w780, original

    }
    public static enum Profile_Sizes{
        w45, w185, h632, original

    }
    public static enum Still_Sizes{
        w92, w185, w300, original

    }


}
