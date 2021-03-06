package bonosoft.rudak.movie.retrofit;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MoviesService {

    @GET("popular")
    Call<String> getMoviePopular(@Query("api_key") String key, @Query("page") String page);

    @GET("top_rated")
    Call<String> getMovieTopRated(@Query("api_key") String key, @Query("page") String page);

    @GET("{movie_id}")
    Call<String> getMovieDetails(@Path("movie_id") String id, @Query("api_key") String key);

    @GET("{movie_id}/reviews")
    Call<String> getMovieReviews(@Path("movie_id") String id, @Query("api_key") String key);

    @GET("{movie_id}/videos")
    Call<String> getMovieVideos(@Path("movie_id") String id, @Query("api_key") String key);

}
