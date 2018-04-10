package com.trabajo.carlos.somefoodserver.Remote;

import com.trabajo.carlos.somefoodserver.Model.MyResponse;
import com.trabajo.carlos.somefoodserver.Model.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by Carlos Prieto on 17/12/2017.
 */

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAUvRuKGU:APA91bEGTyqY5W_A_Jze3rCygMiulgwE4uJAyOOwoVCXXTvn5p2pMdD7u5T95cT4CSU8yf0xBS45z1N2heOWSkWoT-GuAwgOCWciwHBCFQ7aUwQ_eALCNX8H6bgwNCrhxDumBZ3fEX4i"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
