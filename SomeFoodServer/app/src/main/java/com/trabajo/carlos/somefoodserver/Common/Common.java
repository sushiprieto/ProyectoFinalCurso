package com.trabajo.carlos.somefoodserver.Common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.trabajo.carlos.somefoodserver.Model.Request;
import com.trabajo.carlos.somefoodserver.Model.User;
import com.trabajo.carlos.somefoodserver.Remote.APIService;
import com.trabajo.carlos.somefoodserver.Remote.FCMRetrofitClient;
import com.trabajo.carlos.somefoodserver.Remote.IGeoCoordinates;
import com.trabajo.carlos.somefoodserver.Remote.RetrofitClient;

/**
 * Created by Carlos Prieto on 27/09/2017.
 */

public class Common {

    public static User currentUser;
    public static Request currentRequest;

    public static final String UPDATE = "Actualizar";
    public static final String DELETE = "Borrar";

    public static final int PICK_IMAGE_REQUEST = 71;

    public static final String baseUrl = "https://maps.googleapis.com";

    public static final String fcmUrl = "https://fcm.googleapis.com/";

    public static String convertCodeToStatus(String code){

        if (code.equals("0"))
            return "Pedido";
        else if (code.equals("1"))
            return "En el camino";
        else
            return "Enviado";

    }

    public static APIService getFCMClient(){
        return FCMRetrofitClient.getClient(fcmUrl).create(APIService.class);
    }

    public static IGeoCoordinates getGeoCodeService(){

        return RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);

    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight){

        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float)bitmap.getWidth();
        float scaleY = newHeight/ (float)bitmap.getHeight();
        float pivotX = 0, pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }

}
