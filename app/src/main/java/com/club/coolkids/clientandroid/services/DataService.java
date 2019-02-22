package com.club.coolkids.clientandroid.services;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataService {

    private static final DataService instance = new DataService();
    public IDataService service;


    //private constructor to avoid client applications to use constructor
    private DataService(){
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        Retrofit retro = new Retrofit.Builder()
                .baseUrl(IDataService.endpoint)
                .client(getClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        service = retro.create(IDataService.class);

    }

    public static DataService getInstance(){
        return instance;
    }

    public static class MyCookieJar implements CookieJar {

        private List<Cookie> cookies;

        @Override
        public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
            this.cookies =  cookies;
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl url) {
            List<Cookie> res = new ArrayList<>();
            if (cookies != null){
                for(Cookie c : cookies){
                    if (c.expiresAt() > System.currentTimeMillis()) res.add(c);
                }
            }
            return res;
        }
    }

    public static OkHttpClient getClient(){
        try {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.interceptors().add(interceptor);
            // Sets the cookie Jar to automatically handles incoming and outgoing cookies
            CookieJar cookieJar =
                    new MyCookieJar();
            builder = builder.cookieJar(cookieJar);

            // Adds logging capability to see http exchanges on Android Monitor
//            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
//            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//            builder = builder.addInterceptor(interceptor);
            return builder.build();
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }


}
