package tornaco.apps.shortx.services.v;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import tornaco.apps.shortx.core.BuildProp;
import tornaco.apps.shortx.core.CommonApiResWrapper;

// Workaround, make return type to Call.
// Caused by: java.lang.IllegalArgumentException: Could not locate call adapter for class java.lang.Object.
// Tried:
//  * retrofit2.CompletableFutureCallAdapterFactory
//  * retrofit2.DefaultCallAdapterFactory
//  at retrofit2.Retrofit.nextCallAdapter(Unknown Source:146)
//  at retrofit2.Retrofit.callAdapter(Unknown Source:1)
//  at retrofit2.HttpServiceMethod.createCallAdapter(Unknown Source:0)

public interface Api {
    @GET("verifyCodeBinding")
    Call<CommonApiResWrapper> verify(
            @Query("uuid") String uuid,
            @Query("deviceId") String deviceId
    );

    class Factory {
        private static final String BASE_URL = BuildProp.API_BASE_URL;

        public static Api create() {
            return new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient.Builder().build())
                    .build()
                    .create(Api.class);
        }
    }
}