package com.deplink.sdk.android.sdk.rest.ConverterFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by Administrator on 2017/7/29.
 */
public class StringResponseBodyConverter implements Converter<ResponseBody, String> {
    private  static final String TAG="StrResponseConverter";
    @Override
    public String convert(ResponseBody value) throws IOException {
        try {
            StringBuilder str=new StringBuilder();
            InputStreamReader is=(InputStreamReader) value.charStream();

            BufferedReader bufferedReader = new BufferedReader(is);
            String lineTXT;
            while ((lineTXT = bufferedReader.readLine()) != null) {
                 str.append(lineTXT) ;
            }
            return str.toString();
        }

        finally {

            value.close();
        }
    }




}
