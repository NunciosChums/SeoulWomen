package kr.susemi99.seoulwomen.networks;

import kr.susemi99.seoulwomen.R;
import kr.susemi99.seoulwomen.application.MyApp;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

public final class WomenService
{
  public static ListAPI api()
  {
    String host = "http://openapi.seoul.go.kr:8088/" + MyApp.context().getString(R.string.my_key) + "/json/";
    Retrofit retrofit = new Retrofit.Builder().baseUrl(host).build();
    return retrofit.create(ListAPI.class);
  }

  public interface ListAPI
  {
    @GET("{name}/1/2")
    Call<ResponseBody> list(@Path("name") String name);
  }
}
