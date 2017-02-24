package bonosoft.rudak.movie.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;
import java.util.List;


import bonosoft.rudak.movie.R;
import bonosoft.rudak.movie.adapters.SimpleDividerItemDecoration;
import bonosoft.rudak.movie.adapters.SimpleItemRecyclerViewAdapter;
import bonosoft.rudak.movie.database.MoviesTable;
import bonosoft.rudak.movie.loaders.DetailLoader;
import bonosoft.rudak.movie.loaders.ReviewsLoader;
import bonosoft.rudak.movie.loaders.VideosLoader;
import bonosoft.rudak.movie.models.Movie;


public class ItemDetailFragment extends Fragment
        implements SimpleItemRecyclerViewAdapter.OnItemClickListener,
        LoaderManager.LoaderCallbacks<List<String>> {

    TextView tvItemTitle;
    TextView tvReleaseDate;
    TextView tvRuntime;
    TextView tvVote;
    TextView tvOverview;
    Button btFavorite;

    Picasso picasso;
    ImageView iv_movie_poster;
    Movie movie;
    RecyclerView recyclerView;
    SimpleItemRecyclerViewAdapter adapterVideos;
    RecyclerView.LayoutManager layoutManager;
    SimpleItemRecyclerViewAdapter adapterReviews;
    RecyclerView rvReviews;
    RecyclerView.LayoutManager lmReviews;

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_ITEM_TITLE = "item_title";

    private String movieId;
    private String movieTitle;

    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            movieId = getArguments().getString(ARG_ITEM_ID);
        }
        if (getArguments().containsKey(ARG_ITEM_TITLE)) {
            movieTitle = getArguments().getString(ARG_ITEM_TITLE);
        }
        String s = "";


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.movie_detail, container, false);

        movie = new Movie();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_video);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        adapterVideos = new SimpleItemRecyclerViewAdapter(movie.getTrailers(),R.layout.video_item);
        adapterVideos.SetOnItemClickListener(this);
        recyclerView.setAdapter(adapterVideos);

        rvReviews = (RecyclerView) rootView.findViewById(R.id.rv_review);
        lmReviews = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvReviews.setLayoutManager(lmReviews);
        rvReviews.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        adapterReviews = new SimpleItemRecyclerViewAdapter(movie.getReviews(),R.layout.review_item);
        rvReviews.setAdapter(adapterReviews);

        picasso = Picasso.with(getContext());

        tvItemTitle = (TextView) rootView.findViewById(R.id.tvItemTitle);
        tvItemTitle.setText(movieTitle);
        tvReleaseDate = (TextView) rootView.findViewById(R.id.tvReleaseDate);
        tvRuntime = (TextView) rootView.findViewById(R.id.tvRuntime);
        tvVote = (TextView) rootView.findViewById(R.id.tvVote);
        tvOverview = (TextView) rootView.findViewById(R.id.tvOverview);
        btFavorite = (Button)  rootView.findViewById(R.id.btFavorite);
        iv_movie_poster = (ImageView) rootView.findViewById(R.id.ivMoviePoster);

        btFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLocal();
            }
        });

        getLoaderManager().initLoader(R.id.videos_loader, Bundle.EMPTY, this);
        getLoaderManager().initLoader(R.id.reviews_loader, Bundle.EMPTY, this);
        getLoaderManager().initLoader(R.id.detail_loader, Bundle.EMPTY, callbacks);

        return rootView;
    }

    LoaderManager.LoaderCallbacks<Movie> callbacks = new LoaderManager.LoaderCallbacks<Movie>(){

        @Override
        public Loader<Movie> onCreateLoader(int id, Bundle bundle) {
            switch (id) {
                case R.id.detail_loader:
                    return (Loader) new DetailLoader(getContext(), movieId);
                default:
                    return null;
            }
        }

        @Override
        public void onLoadFinished(Loader<Movie> loader, Movie data) {
            int id = loader.getId();
            if (id == R.id.detail_loader) {
                movie.setId(data.getId());
                movie.setOriginalTitle(data.getOriginalTitle());
                movie.setPosterPath(data.getPosterPath());
                movie.setVoteCount(data.getVoteCount());
                movie.setVoteAverage(data.getVoteAverage());
                movie.setReleaseDate(data.getReleaseDate());
                movie.setOverview(data.getOverview());
                movie.setRuntime(data.getRuntime());

                tvReleaseDate.setText(data.getReleaseDate());
                tvRuntime.setText(data.getRuntime()+"min");
                tvVote.setText(data.getVoteAverage()+"/"+data.getVoteCount());
                tvOverview.setText(data.getOverview());

                picasso.load(movie.getPosterPath()).into(iv_movie_poster);
            }
        }

        @Override
        public void onLoaderReset(Loader<Movie> loader) {

        }
    };

    @Override
    public void onItemClick(View view, int position) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movie.getTrailers().get(position)));
        startActivity(browserIntent);
    }

    public void saveLocal(){

        MoviesTable.saveMovie(getContext(), movie);
        MoviesTable.saveTrailers(getContext(), movie);
        MoviesTable.saveReviews(getContext(), movie);
    }


    @Override
    public Loader<List<String>> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case R.id.videos_loader:
                return (Loader) new VideosLoader(getContext(), movieId);
            case R.id.reviews_loader:
                return (Loader) new ReviewsLoader(getContext(), movieId);
            default:
                return null;
        }
    }
    @Override
    public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
        int id = loader.getId();
        if (id == R.id.videos_loader) {
            movie.getTrailers().clear();
            movie.getTrailers().addAll(data);
            adapterVideos.notifyDataSetChanged();
        } else
        if (id == R.id.reviews_loader) {
            movie.getReviews().clear();
            movie.getReviews ().addAll(data);
            adapterReviews.notifyDataSetChanged();
        }

    }

    @Override
    public void onLoaderReset(Loader<List<String>> loader) {

    }


}
