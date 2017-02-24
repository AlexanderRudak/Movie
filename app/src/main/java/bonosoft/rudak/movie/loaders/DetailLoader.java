package bonosoft.rudak.movie.loaders;


import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import bonosoft.rudak.movie.util.Constants;
import bonosoft.rudak.movie.util.JsonParser;
import bonosoft.rudak.movie.models.Movie;
import bonosoft.rudak.movie.retrofit.MoviesService;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class DetailLoader extends AsyncTaskLoader<Movie> {
    Movie movie;
    String movieId;

    public DetailLoader(Context context, String id) {
        super(context);
        movieId = id;
    }

    @Override
    protected void onStartLoading() {
        if (movie != null) {
            deliverResult(movie);
        } else {
            forceLoad();
        }
    }

    @Override
    public void forceLoad() {
            super.forceLoad();
    }

    @Override
    public Movie loadInBackground() {
        try {
            return apiCall();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void deliverResult(Movie data) {
        if (movie == null)
            movie = data;
        super.deliverResult(data);
    }

    private Movie apiCall() {
        try {
            Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(ScalarsConverterFactory.create())
                .baseUrl("https://api.themoviedb.org/3/movie/").build();

            MoviesService service = retrofit.create(MoviesService.class);
            Call<String> call = service.getMovieDetails(movieId, Constants.API_KEY);

            String str = call.execute().body().toString();
            return JsonParser.JsonToDetail(str);
        } catch (Exception e) {
            String s = e.getMessage();
        }
        return null;
    }
}