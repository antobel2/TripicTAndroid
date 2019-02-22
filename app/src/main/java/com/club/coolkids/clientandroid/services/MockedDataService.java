package com.club.coolkids.clientandroid.services;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.mock.BehaviorDelegate;
import retrofit2.mock.MockRetrofit;
import retrofit2.mock.NetworkBehavior;

public class MockedDataService{

    private BehaviorDelegate<IDataService> delegate;

    private static final MockedDataService instance = new MockedDataService();

    //private constructor to avoid client applications to use constructor
    private MockedDataService(){
        Retrofit retro = new Retrofit.Builder()
                .baseUrl("https://www.coolkidsclub.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        NetworkBehavior networkBehavior = NetworkBehavior.create();
        networkBehavior.setDelay(1000, TimeUnit.MILLISECONDS);
        networkBehavior.setVariancePercent(90);

        MockRetrofit mock = new MockRetrofit.Builder(retro)
                .networkBehavior(networkBehavior)
                .build();
        delegate = mock.create(IDataService.class);

    }

    public static MockedDataService getInstance(){
        return instance;
    }


}
