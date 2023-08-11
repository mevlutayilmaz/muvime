package com.example.muvime.service;

import com.example.muvime.model.Cast;
import com.example.muvime.response.CastResponse;
import com.example.muvime.response.CrewResponse;
import com.example.muvime.response.MovieResponse;
import com.example.muvime.model.Movie;
import com.google.gson.annotations.SerializedName;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MovieApiService {
    @GET("movie/popular")
    Observable <MovieResponse> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/top_rated")
    Observable<MovieResponse> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/{movie_id}")
    Observable<Movie> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("search/movie")
    Observable<MovieResponse> getMovieSearch(
            @Query("query") String query,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/credits")
    Observable<CastResponse > getCreditsCast(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}/credits")
    Observable<CrewResponse> getCrewDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("person/{person_id}")
    Observable<Cast> getCastDetails(
            @Path("person_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("person/{person_id}/movie_credits")
    Observable<CastResponse> getCastMovieCredits(
            @Path("person_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("authentication/guest_session/new")
    Call<GuestSessionResponse> getGuestSessionId(@Query("api_key") String apiKey);


    public class GuestSessionResponse {
        private boolean success;
        private String guest_session_id;
        private String expires_at;

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        public String getGuestSessionId() {
            return guest_session_id;
        }

        public void setGuestSessionId(String guest_session_id) {
            this.guest_session_id = guest_session_id;
        }

        public String getExpiresAt() {
            return expires_at;
        }

        public void setExpiresAt(String expires_at) {
            this.expires_at = expires_at;
        }

        public GuestSessionResponse(boolean success, String guest_session_id, String expires_at) {
            this.success = success;
            this.guest_session_id = guest_session_id;
            this.expires_at = expires_at;
        }

        public  GuestSessionResponse(){}
    }
}

