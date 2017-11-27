package com.deplink.sdk.android.sdk.rest.ConverterFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/7/29.
 */
public class PngConvertFactory  extends Converter.Factory{
    public static PngConvertFactory create() {
        return new PngConvertFactory();
    }

    private PngConvertFactory() {

    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return new PngResponseBodyConverter();
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
    }
}

