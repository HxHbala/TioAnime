package com.axiel7.tioanime.rest;

import com.axiel7.tioanime.model.AnimeResponse;
import com.axiel7.tioanime.model.EpisodeResponse;
import com.axiel7.tioanime.model.GenreResponse;
import com.axiel7.tioanime.model.JikanResponse;
import com.axiel7.tioanime.model.LatestAnimesResponse;
import com.axiel7.tioanime.model.LatestEpisodesResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface AnimeApiService {
    @GET("episodes")
    Call<LatestEpisodesResponse> getLatestEpisodes(@Query("auth") String apiKey);
    @GET("animes")
    Call<LatestAnimesResponse> getLatestAnimes(@Query("sort") String recent, @Query("auth") String apiKey);
    @GET("tv")
    Call<LatestAnimesResponse> getTV(@Query("page") int page, @Query("sort") String recent, @Query("status") Integer status, @Query("year") String years, @Query("genres") List<CharSequence> genres, @Query("auth") String apiKey);
    @GET("animes")
    Call<LatestAnimesResponse> getEmission(@Query("status") int status, @Query("page") int page, @Query("auth") String apiKey);
    @GET("movies")
    Call<LatestAnimesResponse> getMovie(@Query("page") int page, @Query("sort") String recent, @Query("status") Integer status, @Query("year") String years, @Query("genres") List<CharSequence> genres, @Query("auth") String apiKey);
    @GET("ovas")
    Call<LatestAnimesResponse> getOvas(@Query("page") int page, @Query("sort") String recent, @Query("status") Integer status, @Query("year") String years, @Query("genres") List<CharSequence> genres, @Query("auth") String apiKey);
    @GET("specials")
    Call<LatestAnimesResponse> getSpecials(@Query("page") int page, @Query("sort") String recent, @Query("status") Integer status, @Query("year") String years, @Query("genres") List<CharSequence> genres, @Query("auth") String apiKey);
    @GET("genres")
    Call<GenreResponse> getGenres(@Query("auth") String apiKey);
    @GET("browser")
    Call<LatestAnimesResponse> getBrowser(@Query("q") String search, @Query("page") int page, @Query("auth") String apiKey);
    @GET
    Call<AnimeResponse> getAnime(@Url String url, @Query("auth") String apiKey);
    @GET
    Call<EpisodeResponse> getEpisode(@Url String url, @Query("auth") String apiKey);
    @GET
    Call<JikanResponse> getMalData(@Url String url);
}
