package com.thinkgem.jeesite.open.send;

import com.squareup.okhttp.*;

public class OkHttpClientUtil {


    public static String doPost(String url,String md5,String param){
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/octet-stream");
        RequestBody body = RequestBody.create(mediaType, param);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("md5", md5)
                .addHeader("appid", "selfService")
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
