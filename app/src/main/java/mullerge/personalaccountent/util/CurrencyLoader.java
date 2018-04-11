package mullerge.personalaccountent.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import mullerge.personalaccountent.R;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CurrencyLoader {

    private final long milisInADay = 1000*60*60*24;

    private Context context;
    private Map<String,Currency> loadedCurrencies;
    private static long lastCurrencyQuery = 0;

    private String baseURL;
    private Retrofit retrofit;
    private CurrencyFixerAPI currencyAPI;

    private static CurrencyLoader instance;

    private CurrencyLoader(Context context){
        this.context = context;
        loadedCurrencies = new HashMap<>();
        baseURL = context.getResources().getString(R.string.base_url);

        final SharedPreferences sp = context.getSharedPreferences("PERSONAL_ACCOUNTENT", Context.MODE_PRIVATE);
        lastCurrencyQuery = sp.getLong("last_currency_query", 0);

        retrofit = new Retrofit.Builder().baseUrl(baseURL)
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create()).build();
        currencyAPI = retrofit.create(CurrencyFixerAPI.class);
    }

    public static CurrencyLoader getIntance(Context context){
        if(instance != null){
            instance.context = context;
        }
        else {
            instance = new CurrencyLoader(context);
        }

        return instance;
    }


    public Currency getCurrencyWithName(final String currencyName) throws Exception{
        return loadedCurrencies.get(currencyName);
    }

    public void loadCurrencies(){

        final SharedPreferences sp = context.getSharedPreferences("PERSONAL_ACCOUNTENT", Context.MODE_PRIVATE);
        long timeSinceLastQuery = (new Date()).getTime() - lastCurrencyQuery;

        if(timeSinceLastQuery > milisInADay ){

            Call<ResponseBody> result = currencyAPI.getCurrencyValues();
            result.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        JSONObject jsonObj = new JSONObject(response.body().string());

                        System.out.println(jsonObj.toString());

                        JSONObject ratesObj = jsonObj.getJSONObject("rates");
                        double euroInHUF = Double.parseDouble((ratesObj.getString(Currency.HUF)));
                        double usdInHuf = 1 / Double.parseDouble(ratesObj.getString(Currency.USD))*euroInHUF;
                        double gbpInHuf = 1 /Double.parseDouble(ratesObj.getString(Currency.GBP)) *euroInHUF;

                        loadedCurrencies.clear();
                        loadedCurrencies.put(Currency.EURO,new Currency(euroInHUF, Currency.EURO));
                        loadedCurrencies.put(Currency.HUF,new Currency(1, Currency.HUF));
                        loadedCurrencies.put(Currency.USD,new Currency(usdInHuf , Currency.USD));
                        loadedCurrencies.put(Currency.GBP,new Currency(gbpInHuf, Currency.GBP));

                        sp.edit().putFloat(Currency.EURO, (float)euroInHUF).apply();
                        sp.edit().putFloat(Currency.USD, (float)usdInHuf).apply();
                        sp.edit().putFloat(Currency.GBP,(float)gbpInHuf).apply();

                        lastCurrencyQuery = new Date().getTime();
                        sp.edit().putLong("last_currency_query", lastCurrencyQuery).apply();

                        System.out.println("EUR: " + euroInHUF + " GBP: " + gbpInHuf);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        loadCurrenciesFromMemory();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    Toast.makeText(context, "Network error", Toast.LENGTH_LONG);
                    loadCurrenciesFromMemory();
                }
            });
        }
        else {
            loadCurrenciesFromMemory();
        }
    }

    public void loadCurrenciesFromMemory(){
        final SharedPreferences sp = context.getSharedPreferences("PERSONAL_ACCOUNTENT", Context.MODE_PRIVATE);

        loadedCurrencies.put(Currency.HUF,new Currency(1.0, Currency.HUF));
        loadedCurrencies.put(Currency.EURO,new Currency(sp.getFloat(Currency.EURO, 0.0f), Currency.EURO));
        loadedCurrencies.put(Currency.USD,new Currency(sp.getFloat(Currency.USD, 0.0f), Currency.USD));
        loadedCurrencies.put(Currency.GBP,new Currency(sp.getFloat(Currency.GBP, 0.0f), Currency.GBP));
    }

    public Map<String, Currency> getLoadedCurrencies() {
        return loadedCurrencies;
    }
}
