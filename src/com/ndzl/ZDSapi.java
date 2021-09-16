package com.ndzl;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;

public class ZDSapi {
    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("ZDS MANAGEMENT APIs");

        //CODE FROM POSTMAN (Java - OkHttp)
        //MAVEN: https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp/4.9.1

        // 1.-GET AUTH KEY   https://api.zebra.com/v2/oauth/token/access
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url("https://api.zebra.com/v2/oauth/token/access")
                .method("GET", null)
                .addHeader("client_id", Secrets.client_id)
                .addHeader("client_secret", Secrets.client_secret)
                .build();
        Response response = client.newCall(request).execute();
        String strResp=response.body().string();
        System.out.println(strResp);
        /*SAMPLE RESULT
        *{
        "access_token":"0ykMRRCoUmO1NSNG1cIYHGEava9N",
        "expires_in":"3599"
        }
        *
        * */
        JSONObject jsonAuth = new JSONObject(strResp);
        String zdsAuthToken= jsonAuth.getString("access_token");
        String zdsAuthExpire= jsonAuth.getString("expires_in");


        // 2.- WHAT FIRMWARE VERSIONS ARE AVAILABLE ?
        request = new Request.Builder()
                .url("https://api.zebra.com/v2/devices/readers/FX7500801791/os")
                .method("GET", null)
                .addHeader("apikey", Secrets.client_id)
                .addHeader("Authorization", "Bearer "+zdsAuthToken)
                .build();
        response = client.newCall(request).execute();
        strResp=response.body().string();
        System.out.println("AVAILABLE FW "+ strResp);


        // 3.- CURRENT FW VERSION ON READER
        request = new Request.Builder()
                .url("https://api.zebra.com/v2/devices/readers/FX7500801791/version")
                .method("GET", null)
                .addHeader("apikey", Secrets.client_id)
                .addHeader("Authorization", "Bearer "+zdsAuthToken)
                .build();
        try {
            response = client.newCall(request).execute();
        } catch (java.net.SocketTimeoutException stoe) {
            System.out.println("TIMEOUT ERROR: "+response.header("Status-Code"));
        }
        strResp=response.body().string();
        JSONObject jsonFW = new JSONObject(strResp);
        System.out.println("FW READER APPLICATION  "+ jsonFW.getJSONObject("value").getString("readerApplication"));
    }



}
