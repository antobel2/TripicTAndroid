package com.club.coolkids.clientandroid.services;

import com.club.coolkids.clientandroid.models.dtos.ActivityDTO;
import com.club.coolkids.clientandroid.models.dtos.CreateActivityDTO;
import com.club.coolkids.clientandroid.models.dtos.CreatePictureDTO;
import com.club.coolkids.clientandroid.models.dtos.CreatePostDTO;
import com.club.coolkids.clientandroid.models.dtos.InviteUserToTripDTO;
import com.club.coolkids.clientandroid.models.dtos.PictureDTO;
import com.club.coolkids.clientandroid.models.dtos.PostDTO;

import java.util.List;
import com.club.coolkids.clientandroid.models.dtos.CreateTripDTO;
import com.club.coolkids.clientandroid.models.dtos.SignedInUserDTO;
import com.club.coolkids.clientandroid.models.dtos.TripDTO;
import com.club.coolkids.clientandroid.models.dtos.UserSearchResultDTO;
import com.club.coolkids.clientandroid.models.LogInfo;
import com.club.coolkids.clientandroid.models.Token;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface IDataService {

    //String endpoint = "http://e1-test.projet.college-em.info:8080";
    //String endpoint = "http://e1-dev.projet.college-em.info:8080";
    String endpoint = "http://10.0.2.2:52090";

    @GET("/api/values/")
    Call<String> getAllTest();

    @POST("/api/Trips/CreateTrip")
    Call<Void> createTrip(@Header("Authorization") String authorization, @Body CreateTripDTO createTripDTO);

    @GET("/api/Trips/getTripsForUser")
    Call<List<TripDTO>> getTrips(@Header("Authorization") String authorization);

    @POST("/api/Posts/CreatePost")
    Call<Integer> createPost(@Header("Authorization") String authorization, @Body CreatePostDTO post);

    @POST("/api/Activities/CreateActivity")
    Call<Void> createActivity(@Header("Authorization") String authorization, @Body CreateActivityDTO createActivityDTO);

    @GET("/api/Activities/getActivitiesForTrip/{id}")
    Call<List<ActivityDTO>> getActivities(@Header("Authorization") String authorization, @Path("id") int id);

    @GET("/api/Posts")
    Call<List<PostDTO>> getPosts();

    @POST("/api/Pictures/CreatePicture")
    Call<Void> createPicture(@Header("Authorization") String authorization, @Body CreatePictureDTO post);

    @POST("/api/Account/Register")
    Call<Void> register(@Body LogInfo user);

    @FormUrlEncoded
    @POST("/api/Token")
    Call<Token> signin(@Header("Content-Type") String contentType, @Field("username") String username, @Field("password") String password, @Field("grant_type") String grantType);

    @POST("/api/Account/Logout")
    Call<Void> logout(@Header("Authorization") String authorization);


    @GET("/api/Pictures/GetPictureFromId/{id}")
    Call<PictureDTO> getPictureFromId(@Header("Authorization") String authorization, @Path("id") int id);

    @GET("/api/Posts/GetPostsForActivity/{id}")
    Call<List<PostDTO>> getPostForActivity(@Header("Authorization") String authorization, @Path("id") int id);

    @GET("/api/Trips/GetUsersForTrip/{id}")
    Call<List<SignedInUserDTO>> getUsersForTrip(@Header("Authorization") String authorization, @Path("id") int id);

    @GET("api/Account/FindUsers/{tripId}/{searchParams}")
    Call<List<UserSearchResultDTO>> findUsers(@Header("Authorization") String authorization, @Path("tripId") int tripId, @Path("searchParams") String searchParams);

    @POST("api/Trips/InviteUserToTrip")
    Call<Void> inviteUserToTrip(@Header("Authorization") String authorization, @Body InviteUserToTripDTO inviteUserToTripDTO);

    @GET("api/Account/CurrentUser")
    Call<SignedInUserDTO> getCurrentUser(@Header("Authorization") String authorization);

     /*
    @GET("/pokemons/{id}/{uid}/details/")
    Call<PokDetails> getPokemonDetails(@Path("id") int pokId, @Path("uid") String userId);

    @POST("/users/pokemons/create/")
    Call<Boolean> createPokemon(@Body PokCreate pokC);

    @GET("/users/signout")
    Call<Boolean> logoutUser();

    @POST("/users/create/")
    Call<TLoggedUser> createUser(@Body AttemptUser u);

    @POST("/users/login/")
    Call<TLoggedUser> login(@Body AttemptUser u);

    @POST("/users/create/")
    Call<Token> createUser(@Body AttemptUser u);

    @POST("/users/login/")
    Call<Token> login(@Body AttemptUser u);
    */
}
