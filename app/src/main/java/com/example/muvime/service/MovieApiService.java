package com.example.muvime.service;

import com.example.muvime.model.Cast;
import com.example.muvime.response.CastResponse;
import com.example.muvime.response.CrewResponse;
import com.example.muvime.response.MovieResponse;
import com.example.muvime.model.Movie;
import com.example.muvime.response.WatchListResponse;
import com.google.gson.annotations.SerializedName;

import io.reactivex.Observable;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApiService {
    @GET("movie/popular")
    Observable <MovieResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language);

    @GET("movie/top_rated")
    Observable<MovieResponse> getTopRatedMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language);

    @GET("movie/{movie_id}")
    Observable<Movie> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("search/movie")
    Observable<MovieResponse> getMovieSearch(
            @Query("query") String query,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("movie/{movie_id}/credits")
    Observable<CastResponse > getCreditsCast(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("movie/{movie_id}/credits")
    Observable<CrewResponse> getCrewDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("person/{person_id}")
    Observable<Cast> getCastDetails(
            @Path("person_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("person/{person_id}/movie_credits")
    Observable<CastResponse> getCastMovieCredits(
            @Path("person_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @GET("authentication/token/new")
    Call<RequestResponse> getCreateRequest(@Query("api_key") String apiKey);

    @POST("authentication/session/new")
    Call<SessionResponse> getCreateSession(
            @Query("api_key") String apiKey,
            @Body RequestBody body);

    @POST("list")
    Observable<ListResponse> getCreateList(
            @Query("session_id") String session_id,
            @Body RequestBody body,
            @Query("api_key") String apiKey
    );

    @POST("list/{list_id}/add_item")
    Call<ResponseBody> getAddMovieToList(
            @Path("list_id") int listId,
            @Query("api_key") String apiKey,
            @Query("session_id") String sessionId,
            @Body RequestBody body
    );

    @GET("list/{list_id}")
    Call<WatchListResponse> getListDetails(
            @Path("list_id") int listId,
            @Query("api_key") String apiKey,
            @Query("language") String language
    );

    @POST("list/{list_id}/remove_item")
    Observable<ResponseBody> getRemoveMovieToList(
            @Path("list_id") int listId,
            @Query("api_key") String apiKey,
            @Query("session_id") String sessionId,
            @Body RequestBody body
    );


    public class RequestResponse{
        private boolean success;
        private String expires_at;
        private String request_token;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getExpires_at() {
            return expires_at;
        }

        public void setExpires_at(String expires_at) {
            this.expires_at = expires_at;
        }

        public String getRequest_token() {
            return request_token;
        }

        public void setRequest_token(String request_token) {
            this.request_token = request_token;
        }

        public RequestResponse(boolean success, String expires_at, String request_token) {
            this.success = success;
            this.expires_at = expires_at;
            this.request_token = request_token;
        }

        public RequestResponse(){}
    }

    public class SessionResponse {
        private boolean success;
        private String session_id;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getSession_id() {
            return session_id;
        }

        public void setSession_id(String session_id) {
            this.session_id = session_id;
        }

        public SessionResponse(boolean success, String session_id) {
            this.success = success;
            this.session_id = session_id;
        }

        public SessionResponse(){}
    }

    public class ListResponse{
        private String status_message;
        private boolean success;
        private int status_code;
        private int list_id;

        public String getStatus_message() {
            return status_message;
        }

        public void setStatus_message(String status_message) {
            this.status_message = status_message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public int getStatus_code() {
            return status_code;
        }

        public void setStatus_code(int status_code) {
            this.status_code = status_code;
        }

        public int getList_id() {
            return list_id;
        }

        public void setList_id(int list_id) {
            this.list_id = list_id;
        }

        public ListResponse(String status_message, boolean success, int status_code, int list_id) {
            this.status_message = status_message;
            this.success = success;
            this.status_code = status_code;
            this.list_id = list_id;
        }
    }
}

