package com.app.idfcscraper.api;
import com.app.idfcscraper.response.GetUpiStatusResponse;
import retrofit2.Call;;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("GetUpiStatus")
    Call<GetUpiStatusResponse> getUpiStatus(@Query("upiId") String upiId);


}