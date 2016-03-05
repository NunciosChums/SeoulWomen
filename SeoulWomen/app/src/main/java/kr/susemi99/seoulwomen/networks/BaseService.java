package kr.susemi99.seoulwomen.networks;

import kr.susemi99.seoulwomen.application.MyApp;
import kr.susemi99.seoulwomen.R;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BaseService
{
  protected static Object retrofit(Class<?> className)
  {
    String host = "http://openapi.seoul.go.kr:8088/" + MyApp.context().getString(R.string.my_key) + "/json/";
    Retrofit retrofit = new Retrofit.Builder().baseUrl(host).addConverterFactory(GsonConverterFactory.create()).build();
    return retrofit.create(className);
  }
}
