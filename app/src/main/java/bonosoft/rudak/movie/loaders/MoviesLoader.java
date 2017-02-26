package bonosoft.rudak.movie.loaders;


import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import java.io.IOException;

import bonosoft.rudak.movie.util.Constants;
import bonosoft.rudak.movie.util.JsonParser;
import bonosoft.rudak.movie.database.MoviesTable;
import bonosoft.rudak.movie.models.Response;
import bonosoft.rudak.movie.retrofit.MoviesService;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MoviesLoader extends AsyncTaskLoader<Response> {

    private Response response = new Response();
    private String sort;
    private String sort_old;
    private String page;

    public MoviesLoader(Context context, Bundle args) {
        super(context);
        setArg(args);
    }

    @Override
    protected void onStartLoading() {
        if(response.getMovies()==null || response.getMovies().isEmpty()
                || !sort.equalsIgnoreCase(sort_old)){
            forceLoad();
        } else {
            deliverResult(response);
        }
    }

    public void setArg(Bundle args){
        if (args != null) {
            sort_old = sort;
            sort = args.getString(Constants.SORT);
            page = args.getString(Constants.PAGE);
        }
    }

    @Override
    public void forceLoad() {
        super.forceLoad();
    }

    @Override
    public Response loadInBackground() {
        try {
            return apiCall();
        } catch (Exception e) {
            return null;
        }
    }

    public Response getLastResponse() {
        return response;
    }

    @Override
    public void deliverResult(Response data) {
        if(response == null)
            response = new Response();
        if(!sort.equalsIgnoreCase(sort_old)) {
            sort_old = sort;
            response.getMovies().clear();
        }
        if(response.getPage() != data.getPage()) {
            response.getMovies().addAll(data.getMovies());
            response.setSort(data.getSort());
            response.setPage(data.getPage());
            response.setTotal_pages(data.getTotal_pages());
        }
        super.deliverResult(data);
    }

    @Nullable
    private Response apiCall()  throws IOException {
        Response result = new Response();
        if(sort.equalsIgnoreCase(Constants.SORT_FAVORITE)) {
            Cursor cursor = getContext().getContentResolver().query(MoviesTable.URI_MOVIES,
                    null, null, null, null);
            if(cursor==null){
                return result;
            }
            result = MoviesTable.listMoviesFromCursor(cursor);
            result.setPage(1);
            result.setSort(sort);
            return result;
        }

        Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .baseUrl("https://api.themoviedb.org/3/movie/").build();

        MoviesService service = retrofit.create(MoviesService.class);
        Call<String> call = null;

        switch (sort) {
            case Constants.SORT_POPULAR:
                call = service.getMoviePopular(Constants.API_KEY, page);
                break;
            case Constants.SORT_RATING:
                call = service.getMovieTopRated(Constants.API_KEY, page);
                break;
            default:
                call = service.getMoviePopular(Constants.API_KEY, page);
                break;
        }

        String str = call.execute().body().toString();
        result = JsonParser.JsonToListMovie(str);
        result.setSort(sort);
        return result;
    }


}
