package com.creative.share.apps.sheari.services;


import com.creative.share.apps.sheari.models.CategoryDataModel;
import com.creative.share.apps.sheari.models.CommentRespons;
import com.creative.share.apps.sheari.models.LocationDataModel;
import com.creative.share.apps.sheari.models.OfferDataModel;
import com.creative.share.apps.sheari.models.PlaceGeocodeData;
import com.creative.share.apps.sheari.models.PlaceMapDetailsData;
import com.creative.share.apps.sheari.models.ProvidersDataModel;
import com.creative.share.apps.sheari.models.SliderDataModel;
import com.creative.share.apps.sheari.models.TermsModel;
import com.creative.share.apps.sheari.models.UserModel;

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
    Call<UserModel> clientSignUp(@Header("X-localization") String lang,
                                 @Field("name") String name,
                                 @Field("email") String email,
                                 @Field("phone") String phone,
                                 @Field("region_id") String region_id,
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
                                                       @Path("sub_cat_id") int sub_cat_id,
                                                       @Query("page") int page
    );

    @FormUrlEncoded
    @POST("api/search")
    Call<ProvidersDataModel> getProvidersSearch(@Field("category_id") int sub_category_id,
                                                @Field("country_id") int country_id,
                                                @Field("city_id") int city_id,
                                                @Query("page") int page
    );


    @GET("api/get-ads-categories")
    Call<CategoryDataModel> getAds(@Header("X-l") String lang);

    @GET("api/sub-categories")
    Call<CategoryDataModel> getAllSubCategory(@Header("X-localization") String lang);

    @GET("api/service/ads/provider/{cat_id}")
    Call<ProvidersDataModel> getAdsProvidersBySubCategory(@Path("cat_id") int cat_id,
                                                          @Query("page") int page
    );

    @GET("api/market/offers")
    Call<OfferDataModel> getOffers(@Query("page") int page);

    @GET("api/market/requests")
    Call<OfferDataModel> getOrder(@Query("page") int page);


    @FormUrlEncoded
    @POST("api/market/add/comment/{id}")
    Call<CommentRespons> addComment(@Header("Authorization") String token,
                                    @Path("id") int id
    );


    @GET("api/terms")
    Call<TermsModel> getAppTerms(@Header("X-localization") String lang);

    @GET("api/terms_user")
    Call<TermsModel> getClientTerms(@Header("X-localization") String lang,
                                    @Header("Authorization") String token
    );

    @GET("api/terms_provider")
    Call<TermsModel> getProviderTerms(@Header("X-localization") String lang,
                                      @Header("Authorization") String token);

    @GET("api/sliders")
    Call<SliderDataModel> getSlider();

    @FormUrlEncoded
    @POST("api/auth/login")
    Call<UserModel> login(@Header("X-localization") String lang,
                          @Field("email") String email,
                          @Field("password") String password
    );

    @FormUrlEncoded
    @POST("api/user/update/profile")
    Call<UserModel> updateClientProfile(@Header("Authorization") String user_token,
                                        @Field("name") String name,
                                        @Field("email") String email,
                                        @Field("phone") String phone
    );

}


