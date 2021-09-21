package com.ndzl;

import kong.unirest.Unirest;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.*;

import java.io.*;
import java.net.*;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

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
                .addHeader("Authorization", "Bearer " + zdsAuthToken)
                .build();
        response = client.newCall(request).execute();
        strResp = response.body().string();
        response.close();
        System.out.println("AVAILABLE FW " + strResp);


        //3b.- same as 3 with UNIREST library
        //according to the postman doc, unirest is Unirest in Java: Simplified, lightweight HTTP client library.
        //kong.github.io/unirest-java/

        try {
            Unirest.config()
                    .connectTimeout(20000); //IMPORTANTE PER OTTENERE IL BODY ANCHE IN CASO DI TIMEOUT
            kong.unirest.HttpResponse<String> responseunirest =
                    Unirest
                            .get("https://api.zebra.com/v2/devices/readers/FX7500801791/version")
                            .header("accept", "application/json")
                            .header("Authorization", "Bearer " + zdsAuthToken)
                            .asString();
            JSONObject jsonUNIREST_FW = new JSONObject(responseunirest.getBody());
            if (responseunirest.getStatus() == 200)
                System.out.println("UNIREST - FW READER APPLICATION  " + jsonUNIREST_FW.getString("readerApplication"));
            else
                System.out.println("UNIREST - ERROR READING: " + responseunirest.getBody());

        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }




    }



}

class ParameterStringBuilder {
    public static String getParamsString(Map<String, String> params)
            throws UnsupportedEncodingException{
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            result.append("&");
        }

        String resultString = result.toString();
        return resultString.length() > 0
                ? resultString.substring(0, resultString.length() - 1)
                : resultString;
    }
}
