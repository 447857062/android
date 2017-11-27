package com.deplink.sdk.android.sdk.rest.ConverterFactory;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by Administrator on 2017/7/29.
 */
public class PngResponseBodyConverter implements Converter<ResponseBody, Bitmap> {
    private  static final String TAG="PngConverter";
    @Override
    public Bitmap convert(ResponseBody value) throws IOException {
        try {
            return getBitmapFromByte(value.bytes());
        } finally {
            Log.i(TAG,"getBitmapFromByte close");
            value.close();
        }
    }


    public Bitmap getBitmapFromByte(byte[] temp){
        if(temp != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(temp, 0, temp.length);
            return bitmap;
        }else{
            return null;
        }
    }


}
