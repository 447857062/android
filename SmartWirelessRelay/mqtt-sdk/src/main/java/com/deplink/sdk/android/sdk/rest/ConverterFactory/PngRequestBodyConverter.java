package com.deplink.sdk.android.sdk.rest.ConverterFactory;

import java.io.IOException;

import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * Created by Administrator on 2017/7/29.
 */
public class PngRequestBodyConverter  implements Converter<String, RequestBody> {
    @Override
    public RequestBody convert(String value) throws IOException {
        return null;
    }
}
