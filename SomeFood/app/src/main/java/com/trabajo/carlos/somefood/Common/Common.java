package com.trabajo.carlos.somefood.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.trabajo.carlos.somefood.Remote.APIService;
import com.trabajo.carlos.somefood.Remote.RetrofitClient;
import com.trabajo.carlos.somefood.Utilidad.User;

/**
 * Created by Carlos Prieto on 06/09/2017.
 */

public class Common {

    public static User currentUser;

    private static final String BASE_URL = "https://fcm.googleapis.com/";

    public static APIService getFCMService(){

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);

    }

    public static String converCodeToStatus(String status){

        if (status.equals("0"))
            return "Pedido";
        else  if (status.equals("1"))
            return "Por el camino";
        else
            return "Enviado";

    }

    public static final String DELETE = "Borrar";
    public static final String USER_KEY = "User";
    public static final String PWD_KEY = "Password";

    public static boolean isConnectedToInternet(Context context){

        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){

            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

            if (info != null){

                for (int i = 0; i < info.length; i++){

                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;

                }

            }

        }

        return false;

    }

}
