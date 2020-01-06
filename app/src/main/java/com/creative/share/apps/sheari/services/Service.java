package com.creative.share.apps.sheari.services;


import com.creative.share.apps.sheari.models.CategoryDataModel;
import com.creative.share.apps.sheari.models.CommentDataModel;
import com.creative.share.apps.sheari.models.CommentRespons;
import com.creative.share.apps.sheari.models.LocationDataModel;
import com.creative.share.apps.sheari.models.MessageDataModel;
import com.creative.share.apps.sheari.models.MyOrderDataModel;
import com.creative.share.apps.sheari.models.OfferDataModel;
import com.creative.share.apps.sheari.models.PlaceGeocodeData;
import com.creative.share.apps.sheari.models.PlaceMapDetailsData;
import com.creative.share.apps.sheari.models.ProjectDataModel;
import com.creative.share.apps.sheari.models.ProvidersDataModel;
import com.creative.share.apps.sheari.models.ResponseActiveUser;
import com.creative.share.apps.sheari.models.SingleMessageDataModel;
import com.creative.share.apps.sheari.models.SingleProjectDataModel;
import com.creative.share.apps.sheari.models.SliderDataModel;
import com.creative.share.apps.sheari.models.TermsModel;
import com.creative.share.apps.sheari.models.UserModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
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
                                 @Field("region_id") int region_id,
                                 @Field("password") String password
    );


    @Multipart
    @POST("api/auth/register/provider")
    Call<UserModel> clientProviderSignUpWithOutImage(@Header("X-localization") String lang,
                                                     @Part("name") RequestBody name,
                                                     @Part("email") RequestBody email,
                                                     @Part("phone") RequestBody phone,
                                                     @Part("region_id") RequestBody region_id,
                                                     @Part("password") RequestBody password,
                                                     @Part("bio") RequestBody bio,
                                                     @Part("map") RequestBody map,
                                                     @Part("delivery") RequestBody delivery,
                                                     @Part("charitable") RequestBody charitable,
                                                     @Part("provider_type") RequestBody provider_type,
                                                     @Part("ads_category") RequestBody ads_category,
                                                     @Part("job") RequestBody job,
                                                     @Part("lat") RequestBody lat,
                                                     @Part("lng") RequestBody lng,
                                                     @Part("sub_categories[]") List<RequestBody> ids);


    @Multipart
    @POST("api/auth/register/provider")
    Call<UserModel> clientProviderSignUpWithImage(@Header("X-localization") String lang,
                                                  @Part("name") RequestBody name,
                                                  @Part("email") RequestBody email,
                                                  @Part("phone") RequestBody phone,
                                                  @Part("region_id") RequestBody region_id,
                                                  @Part("password") RequestBody password,
                                                  @Part("bio") RequestBody bio,
                                                  @Part("map") RequestBody map,
                                                  @Part("delivery") RequestBody delivery,
                                                  @Part("charitable") RequestBody charitable,
                                                  @Part("provider_type") RequestBody provider_type,
                                                  @Part("ads_category") RequestBody ads_category,
                                                  @Part("job") RequestBody job,
                                                  @Part("lat") RequestBody lat,
                                                  @Part("lng") RequestBody lng,
                                                  @Part("sub_categories[]") List<RequestBody> ids,
                                                  @Part MultipartBody.Part image
    );


    @Multipart
    @POST("api/auth/register/provider")
    Call<UserModel> companyProviderSignUpWithOutImage(@Header("X-localization") String lang,
                                                      @Part("name") RequestBody name,
                                                      @Part("email") RequestBody email,
                                                      @Part("phone") RequestBody phone,
                                                      @Part("region_id") RequestBody region_id,
                                                      @Part("password") RequestBody password,
                                                      @Part("bio") RequestBody bio,
                                                      @Part("map") RequestBody map,
                                                      @Part("delivery") RequestBody delivery,
                                                      @Part("charitable") RequestBody charitable,
                                                      @Part("provider_type") RequestBody provider_type,
                                                      @Part("ads_category") RequestBody ads_category,
                                                      @Part("job") RequestBody job,
                                                      @Part("lat") RequestBody lat,
                                                      @Part("lng") RequestBody lng,
                                                      @Part("emp_no") RequestBody emp_no,
                                                      @Part("creation_year") RequestBody creation_year,
                                                      @Part("commerical_no") RequestBody commerical_no,
                                                      @Part("provider_company_type") RequestBody provider_company_type,
                                                      @Part("sub_categories[]") List<RequestBody> ids
    );


    @Multipart
    @POST("api/auth/register/provider")
    Call<UserModel> companyProviderSignUpWithImage(@Header("X-localization") String lang,
                                                   @Part("name") RequestBody name,
                                                   @Part("email") RequestBody email,
                                                   @Part("phone") RequestBody phone,
                                                   @Part("region_id") RequestBody region_id,
                                                   @Part("password") RequestBody password,
                                                   @Part("bio") RequestBody bio,
                                                   @Part("map") RequestBody map,
                                                   @Part("delivery") RequestBody delivery,
                                                   @Part("charitable") RequestBody charitable,
                                                   @Part("provider_type") RequestBody provider_type,
                                                   @Part("ads_category") RequestBody ads_category,
                                                   @Part("job") RequestBody job,
                                                   @Part("lat") RequestBody lat,
                                                   @Part("lng") RequestBody lng,
                                                   @Part("emp_no") RequestBody emp_no,
                                                   @Part("creation_year") RequestBody creation_year,
                                                   @Part("commerical_no") RequestBody commerical_no,
                                                   @Part("provider_company_type") RequestBody provider_company_type,
                                                   @Part("sub_categories[]") List<RequestBody> ids,
                                                   @Part MultipartBody.Part image
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
                                    @Path("id") int id,
                                    @Field("comment") String comment
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

    @FormUrlEncoded
    @POST("api/inbox")
    Call<MessageDataModel> getRoomMessages(@Header("Authorization") String user_token,
                                           @Field("receiver_id") int receiver_id,
                                           @Field("order_id") int order_id,
                                           @Field("page") int page
    );


    @FormUrlEncoded
    @POST("api/send/message")
    Call<SingleMessageDataModel> sendChatMessage(@Header("Authorization") String user_token,
                                                 @Field("receiver_id") int receiver_id,
                                                 @Field("order_id") int order_id,
                                                 @Field("message") String message

    );


    @FormUrlEncoded
    @POST("api/auth/activate")
    Call<ResponseActiveUser> activeUserSmsCode(@Header("Authorization") String user_token,
                                               @Field("verification_code") String verification_code,
                                               @Field("phone") String phone
    );

    @FormUrlEncoded
    @POST("api/auth/resend_code")
    Call<ResponseActiveUser> reSendSmsCode(@Field("phone") String phone);


    @Multipart
    @POST("api/provider/projects/new")
    Call<SingleProjectDataModel> addProject(@Header("Authorization") String user_token,
                                            @Part("title") RequestBody title,
                                            @Part("description") RequestBody description,
                                            @Part MultipartBody.Part file);


    @GET("api/provider/projects/provider-projects")
    Call<ProjectDataModel> getAllProjects(@Header("Authorization") String user_token);


    @FormUrlEncoded
    @POST("api/auth/forget")
    Call<ResponseActiveUser> forgotPassword(@Field("phone") String phone);


    @FormUrlEncoded
    @POST("api/auth/reset")
    Call<ResponseActiveUser> sendNewPassword(@Field("reset_code") String reset_code,
                                             @Field("phone") String phone,
                                             @Field("password") String password,
                                             @Field("password_confirmation") String password_confirmation
    );


    @GET("api/market/single/{id}")
    Call<CommentDataModel> getAllComments(@Header("Authorization") String user_token,
                                          @Path("id") int id
    );


    @FormUrlEncoded
    @POST("api/client/ordered/send")
    Call<ResponseActiveUser> sendOrder(@Header("X-localization") String lang,
                                       @Header("Authorization") String token,
                                       @Field("title") String title,
                                       @Field("details") String details,
                                       @Field("lat") double lat,
                                       @Field("lng") double lng,
                                       @Field("time") String time,
                                       @Field("date") String date,
                                       @Field("provider_id") int provider_id,
                                       @Field("important") String important


    );


    @GET("api/client/ordered/pending")
    Call<MyOrderDataModel> getClientPendingOrder(@Header("X-localization") String lang,
                                                 @Header("Authorization") String user_token,
                                                 @Query("page") int page
    );

    @GET("api/client/ordered/accepted")
    Call<MyOrderDataModel> getClientCurrentOrder(@Header("X-localization") String lang,
                                                 @Header("Authorization") String user_token,
                                                 @Query("page") int page
    );

    @GET("api/client/ordered/finished")
    Call<MyOrderDataModel> getClientPreviousOrder(@Header("X-localization") String lang,
                                                  @Header("Authorization") String user_token,
                                                  @Query("page") int page
    );


    @GET("api/provider/ordered/pending")
    Call<MyOrderDataModel> getProviderPendingOrder(@Header("X-localization") String lang,
                                                   @Header("Authorization") String user_token,
                                                   @Query("page") int page
    );

    @GET("api/provider/ordered/accepted")
    Call<MyOrderDataModel> getProviderCurrentOrder(@Header("X-localization") String lang,
                                                   @Header("Authorization") String user_token,
                                                   @Query("page") int page
    );

    @GET("api/provider/ordered/finished")
    Call<MyOrderDataModel> getProviderPreviousOrder(@Header("X-localization") String lang,
                                                    @Header("Authorization") String user_token,
                                                    @Query("page") int page
    );


}


