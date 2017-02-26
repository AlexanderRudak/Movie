package bonosoft.rudak.movie.ui;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import bonosoft.rudak.movie.util.Constants;
import bonosoft.rudak.movie.R;
import bonosoft.rudak.movie.adapters.RecyclerMovieAdapter;
import bonosoft.rudak.movie.loaders.MoviesLoader;
import bonosoft.rudak.movie.models.Movie;
import bonosoft.rudak.movie.models.Response;

import java.util.ArrayList;
import java.util.List;


public class ItemListActivity extends AppCompatActivity
        implements RecyclerMovieAdapter.OnItemClickListener,
         LoaderManager.LoaderCallbacks<Response> {

    private boolean twoPane;
    private List<Movie> movies = new ArrayList<Movie>();
    private RecyclerView recyclerView;
    private RecyclerMovieAdapter adapter;
    private GridLayoutManager recyclerViewLayoutManager;
    private Bundle bundle;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private LoaderManager.LoaderCallbacks<Response> loaderCallbacks;
    private OnLoadMoreListener onLoadMoreListener;
    int page;
    int total_pages;
    boolean loading;
    String sort="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        findView();
        setupAdapter();
        addListener();
        setupLoader();
    }


    void findView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView = (RecyclerView) findViewById(R.id.item_list);
        progressBar = (ProgressBar)  findViewById(R.id.progressBar);

        if (findViewById(R.id.item_detail_container) != null) {
            twoPane = true;
        }
    }

    void setupAdapter(){
        int col = twoPane ? 3 : 2;

        recyclerViewLayoutManager = new GridLayoutManager(this, col);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        adapter = new RecyclerMovieAdapter(this, movies);
        adapter.SetOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    void setupLoader(){
        bundle = new Bundle();
        bundle.putString(Constants.SORT, Constants.SORT_POPULAR);
        page=0;
        bundle.putString(Constants.PAGE, String.valueOf(page+1));
        progressBar.setVisibility(View.VISIBLE);
        getLoaderManager().initLoader(R.id.movies_loader, bundle, this);
    }

    void addListener(){
        loaderCallbacks = this;

        onLoadMoreListener = new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
            if(page<total_pages){
                bundle = new Bundle();
                bundle.putString(Constants.SORT, sort);
                bundle.putString(Constants.PAGE, String.valueOf(page+1));
                progressBar.setVisibility(View.VISIBLE);

                Loader<Response> loader = getLoaderManager().getLoader(R.id.movies_loader);
                ((MoviesLoader)loader).setArg(bundle);
                loader.onContentChanged();
            }
            }
        };

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleThreshold = 4;
            int totalItemCount = recyclerViewLayoutManager.getItemCount();
            int lastVisibleItem = recyclerViewLayoutManager.findLastVisibleItemPosition();

            if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                loading = true;
                if (onLoadMoreListener != null) {
                    onLoadMoreListener.onLoadMore();
                }
            }
            }
        });
    }

    public void setLoaded() {
        loading = false;
    }

    interface OnLoadMoreListener {
        void onLoadMore();
    }


    @Override
    public void onItemClick(View view, int position) {
        if (twoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(ItemDetailFragment.ARG_ITEM_ID, movies.get(position).getId());
            arguments.putString(ItemDetailFragment.ARG_ITEM_TITLE, movies.get(position).getOriginalTitle());

            ItemDetailFragment fragment = new ItemDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.item_detail_container, fragment)
                    .commit();
        } else {
            Context context = view.getContext();
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, movies.get(position).getId());
            intent.putExtra(ItemDetailFragment.ARG_ITEM_TITLE, movies.get(position).getOriginalTitle());
            context.startActivity(intent);
        }
    }


    @Override
    public Loader<Response> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case R.id.movies_loader:
                return (Loader) new MoviesLoader(this, args);

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Response> loader, Response data) {
        progressBar.setVisibility(View.GONE);
        setLoaded();
        int id = loader.getId();
        Response response = ((MoviesLoader)loader).getLastResponse();
        if (id == R.id.movies_loader) {
            if(page < data.getPage() || !sort.equalsIgnoreCase(data.getSort())){
                if(!sort.equalsIgnoreCase(data.getSort())) {
                    sort = data.getSort();
                    movies.clear();
                }

                if(page==0)
                    movies.addAll(response.getMovies());
                else
                    movies.addAll(data.getMovies());

                page = data.getPage();
                total_pages = data.getTotal_pages();

                adapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onLoaderReset(Loader<Response> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list_film, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.sort_popular:
                sortPopular();
                break;
            case R.id.sort_rating:
                sortRating();
                break;
            case R.id.sort_favorite:
                sortFavorite();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sortPopular(){
        toolbar.setTitle(R.string.title_sort_popular);
        bundle.putString(Constants.SORT, Constants.SORT_POPULAR);
        page=0;
        bundle.putString(Constants.PAGE, String.valueOf(page+1));
        progressBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(R.id.movies_loader, bundle, loaderCallbacks);
    }

    public void sortRating(){
        toolbar.setTitle(R.string.title_sort_rating);
        bundle.putString(Constants.SORT, Constants.SORT_RATING);
        page=0;
        bundle.putString(Constants.PAGE, String.valueOf(page+1));
        progressBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(R.id.movies_loader, bundle, loaderCallbacks);
    }

    public void sortFavorite(){
        toolbar.setTitle(R.string.title_sort_favorite);
        bundle.putString(Constants.SORT, Constants.SORT_FAVORITE);
        page=0;
        getLoaderManager().restartLoader(R.id.movies_loader, bundle, this);
    }
}
