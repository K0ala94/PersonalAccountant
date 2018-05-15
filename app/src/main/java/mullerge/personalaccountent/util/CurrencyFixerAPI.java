package mullerge.personalaccountent.util;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CurrencyFixerAPI {

    @GET("latest?access_key=51489afc1da53411f2ba16e2ca5767b4" +
            "&base=EUR" +
            "&symbols=GBP,USD,HRK,HUF")
    public Call<ResponseBody> getCurrencyValues();
}
