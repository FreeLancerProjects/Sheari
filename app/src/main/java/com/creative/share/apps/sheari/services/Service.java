package com.creative.share.apps.sheari.services;


import com.creative.share.apps.sheari.models.CategoryDataModel;
import com.creative.share.apps.sheari.models.LocationDataModel;
import com.creative.share.apps.sheari.models.PlaceGeocodeData;
import com.creative.share.apps.sheari.models.PlaceMapDetailsData;
import com.creative.share.apps.sheari.models.ProvidersDataModel;
import com.creative.share.apps.sheari.models.SignUpDataModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {

    @GET("place/findplacefromtext/json")
    Call<PlaceMapDetailsData> searchOnMap(@Query(value = "inputtype") String inputtype,
                                          @Query(value = "input") String input,
                                          @Query(value = "fields") String fields,
                                          @Query(value = "language") String language,
                                          @Query(value = "key") String key
    );

    @GET("geocode/json")
    Call<PlaceGeocodeData> getGeoData(@Query(value = "latlng") String latlng,
                                      @Query(value = "language") String language,
                                      @Query(value = "key") String key);


    @FormUrlEncoded
    @POST("api/auth/register/client")
    Call<SignUpDataModel> clientSignUp(@Header("X-localization") String lang,
                                       @Field("name") String name,
                                       @Field("email") String email,
                                       @Field("phone") String phone,
                                       @Field("password") String password
    );


    @GET("api/categories")
    Call<CategoryDataModel> getCategory(@Header("X-localization") String lang);

    @GET("api/sub_categories/{category_id}")
    Call<CategoryDataModel> getSubCategory(@Header("X-localization") String lang,
                                           @Path("category_id") int category_id
    );

    @GET("api/locations/countries")
    Call<LocationDataModel> getCountry(@Header("X-localization") String lang);

    @GET("api/locations/cities/{country_id}")
    Call<LocationDataModel> getCityByCountry(@Header("X-localization") String lang,
                                             @Path("country_id") int country_id
    );

    @GET("api/locations/regions/{city_id}")
    Call<LocationDataModel> getRegionByCity(@Header("X-localization") String lang,
                                            @Path("city_id") int city_id
    );


    @GET("api/providers/{sub_cat_id}")
    Call<ProvidersDataModel> getProvidersBySubCategory(@Header("X-localization") String lang,
                                                       @Path("sub_cat_id") int city_id,
                                                       @Query("page") int page
    );


}


