package com.trabajo.carlos.somefoodserver.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Carlos Prieto on 18/12/2017.
 */

public class FCMRetrofitClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient(String baseUrl){

        if (retrofit == null){

            retrofit = new Retrofit.Builder().baseUrl(baseUrl).addConverterFactory(GsonConverterFactory.create()).build();

        }

        return retrofit;

    }

}
