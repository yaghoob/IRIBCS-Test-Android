package ir.iribcs.sso;

import android.content.Context;
import android.os.AsyncTask;
import android.os.NetworkOnMainThreadException;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

//Yaghoob Siahmargooei Was Here
public class INIT {

    public static String data="";
    public static String SERVER_API_URL="http://1gs.ir/iribcs/api/";
    public static class OkHttp_API_GetUserInfo extends AsyncTask<String, Void, Void> {

        private OnAsyncTaskListener<String> mCallBack;
        private Context mContext;
        public String mException="";


        public OkHttp_API_GetUserInfo(Context context, OnAsyncTaskListener callback) {
            mCallBack = callback;
            mContext = context;
            mException="";
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(String... params) {
            Log.i("4Yaghoob>>>>", "Async Task >> send to server!");
            Log.i("4Yaghoob>>>>", "Async Task >> TOken IS : " + params[1]);
            data = "";
            mException="";
            if (!params[0].startsWith("http://") && !params[0].startsWith("https://")) {
                params[0] = "http://" + params[0];
            }
            try {
                final OkHttpClient client = new OkHttpClient.Builder()
                        .build();
                OkHttpClient httpClient = client.newBuilder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .retryOnConnectionFailure(true)
                        .build();
                RequestBody body;
                body = new FormBody.Builder()
                        .add("token", params[1])
                        .build();
                Request req = new Request.Builder().url(params[0]).post(body).build();
                data = httpClient.newCall(req).execute().body().string();
            } catch (IOException e) {
                Log.i("4Yaghoob>>>>> Failed1:", e.toString());
                //  if(INIT.DEBUG_MODE) Toast.makeText(getBaseContext(),"IO ERROR" ,Toast.LENGTH_SHORT).show();
                data = "ERROR:CATCH IO ex...";
                mException = e.getMessage();
            } catch (NetworkOnMainThreadException e) {
                Log.i("4Yaghoob>>>>> Failed2: ", e.toString());
                //  if(INIT.DEBUG_MODE) Toast.makeText(getBaseContext(),"NETWORK ERROR" ,Toast.LENGTH_SHORT).show();
                data = "ERROR:CATCH Network ex...";
                mException = e.getMessage();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i("4Yaghoob>>>> response", data);

            if(data.contains("OK") && data.startsWith("OK"))
            {
            }

            if (mCallBack != null) {
                if (mException.equals("")) {
                    if(isJSONValid(data))
                    mCallBack.onSuccess(data);
                    else
                        mCallBack.onFailure("Server Error! > "+ data);
                } else {
                    mCallBack.onFailure("Server Unreachable! > "+mException);
                }
            }
        }
    }



    static boolean isJSONValid(String test)
    {
        try {
            new JSONObject(test);
            Log.i("4Yaghoob","Json checker function: OK JSON :)");

            return true;
        } catch(JSONException ex) {
            Log.i("4Yaghoob","Json checker function: NOT JSON :(" + ex.getMessage());

            return false;
        }
    }


}
