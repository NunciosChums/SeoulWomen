package kr.susemi99.seoulwomen.networks;

import kr.susemi99.seoulwomen.models.SeochoItem;
import retrofit2.Call;
import retrofit2.http.GET;

public final class WomenService extends BaseService
{
  public static ListAPI api()
  {
    return (ListAPI) retrofit(ListAPI.class);
  }

  public interface ListAPI
  {
    @GET("SeoulSeochoWomenResourcesClass/1/2")
    Call<SeochoItem> seocho();
  }
}
