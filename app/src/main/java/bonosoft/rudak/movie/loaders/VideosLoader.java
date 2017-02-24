package bonosoft.rudak.movie.loaders;


import android.support.v4.content.AsyncTaskLoader;
import android.content.Context;
import java.io.IOException;
import java.util.List;
import bonosoft.rudak.movie.util.Constants;
import bonosoft.rudak.movie.util.JsonParser;
import bonosoft.rudak.movie.retrofit.MoviesService;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class VideosLoader extends AsyncTaskLoader<List<String>> {
    List<String> videos;
    String movieId;

    public VideosLoader(Context context, String id) {
        super(context);
        movieId = id;
    }

    @Override
    protected void onStartLoading() {
        if (videos != null) {
            deliverResult(videos);
        } else {
            forceLoad();
        }
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
    }

    @Override
    public List<String> loadInBackground() {
        try {
            return apiCall();
        } catch (Exception e) {
           return null;
        }
    }

    @Override
    public void deliverResult(List<String> data) {
        if(videos == null)
            videos = data;
        else {
            videos.clear();
            videos.addAll(data);
        }
        super.deliverResult(data);
    }

    private List<String> apiCall() throws IOException {
        Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl("https://api.themoviedb.org/3/movie/").build();

        MoviesService service = retrofit.create(MoviesService.class);
        Call<String> call = service.getMovieVideos(movieId,Constants.API_KEY);
        String str = call.execute().body().toString();
        return JsonParser.JsonToListVideos(str);
    }
}
