package bonosoft.rudak.movie.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import java.util.ArrayList;
import java.util.List;

import bonosoft.rudak.movie.models.Movie;
import bonosoft.rudak.movie.models.Response;


public class MoviesTable {

    public static final Uri URI_MOVIES = SQLiteHelper.BASE_CONTENT_URI.buildUpon().appendPath(Requests.MOVIES_TABLE_NAME).build();
    public static final Uri URI_TRAILERS = SQLiteHelper.BASE_CONTENT_URI.buildUpon().appendPath(Requests.TRAILERS_TABLE_NAME).build();
    public static final Uri URI_REVIEWS = SQLiteHelper.BASE_CONTENT_URI.buildUpon().appendPath(Requests.REVIEWS_TABLE_NAME).build();


    public static void saveMovie(Context context, @NonNull Movie movie) {
        context.getContentResolver().insert(URI_MOVIES, toMoviesContentValues(movie));
    }

    public static void saveTrailers(Context context, @NonNull Movie movie) {
        ContentValues[] values = new ContentValues[movie.getTrailers().size()];
        for (int i = 0; i < movie.getTrailers().size(); i++) {
            values[i] = toTrailersContentValues(movie.getId(), movie.getTrailers().get(i));
        }
        context.getContentResolver().bulkInsert(URI_TRAILERS, values);
    }

    public static void saveReviews(Context context, @NonNull Movie movie) {
        ContentValues[] values = new ContentValues[movie.getReviews().size()];
        for (int i = 0; i < movie.getReviews().size(); i++) {
            values[i] = toReviewsContentValues(movie.getId(), movie.getReviews().get(i));
        }
        context.getContentResolver().bulkInsert(URI_REVIEWS, values);
    }

    @NonNull
    public static ContentValues toMoviesContentValues(@NonNull Movie movie) {
        ContentValues values = new ContentValues();
        values.put(Columns.MOVIE_ID, movie.getId());
        values.put(Columns.ORIGINAL_TITLE, movie.getOriginalTitle());
        values.put(Columns.POSTER_PATH, movie.getPosterPath());
        values.put(Columns.VOTE_COUNT, movie.getVoteCount());
        values.put(Columns.VOTE_AVERAGE, movie.getVoteAverage());
        values.put(Columns.RELEASE_DATE, movie.getReleaseDate());
        values.put(Columns.OVERVIEW, movie.getOverview());
        values.put(Columns.RUNTIME, movie.getRuntime());
        return values;
    }

    @NonNull
    public static ContentValues toTrailersContentValues(String id, String url) {
        ContentValues values = new ContentValues();
        values.put(Columns.MOVIE_ID, id);
        values.put(Columns.YOUTUBE_URL, url);
        return values;
    }

    @NonNull
    public static ContentValues toReviewsContentValues(String id, String content) {
        ContentValues values = new ContentValues();
        values.put(Columns.MOVIE_ID, id);
        values.put(Columns.CONTENT, content);
        return values;
    }


    @NonNull
    public static Movie fromMovieCursor(@NonNull Cursor cursor) {
        String movieId = cursor.getString(cursor.getColumnIndex(Columns.MOVIE_ID));
        String originalTitle = cursor.getString(cursor.getColumnIndex(Columns.ORIGINAL_TITLE));
        String posterPath = cursor.getString(cursor.getColumnIndex(Columns.POSTER_PATH));
        String voteCount = cursor.getString(cursor.getColumnIndex(Columns.VOTE_COUNT));
        String voteAverage = cursor.getString(cursor.getColumnIndex(Columns.VOTE_AVERAGE));
        String releaseDate = cursor.getString(cursor.getColumnIndex(Columns.RELEASE_DATE));
        String overview = cursor.getString(cursor.getColumnIndex(Columns.OVERVIEW));
        String runtime = cursor.getString(cursor.getColumnIndex(Columns.RUNTIME));

        return new Movie(movieId,
                originalTitle,
                posterPath,
                voteCount,
                voteAverage,
                releaseDate,
                overview,
                runtime);
    }

    @NonNull
    public static Response listMoviesFromCursor(@NonNull Cursor cursor) {
        Response response = new Response();
        List<Movie> movies = new ArrayList<>();
        response.setMovies(movies);
        if (!cursor.moveToFirst()) {
            return response;
        }
        try {
            do {
                movies.add(fromMovieCursor(cursor));
            } while (cursor.moveToNext());
            response.getMovies().addAll(movies);
            return response;
        } finally {
            cursor.close();
        }
    }

    @NonNull
    public static List<String> listTrairersFromCursor(@NonNull Cursor cursor) {
        List<String> youtube_urls = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return youtube_urls;
        }
        try {
            do {
                youtube_urls.add(cursor.getString(cursor.getColumnIndex(Columns.YOUTUBE_URL)));
            } while (cursor.moveToNext());
            return youtube_urls;
        } finally {
            cursor.close();
        }
    }

    @NonNull
    public static List<String> listReviewsFromCursor(@NonNull Cursor cursor) {
        List<String> youtube_urls = new ArrayList<>();
        if (!cursor.moveToFirst()) {
            return youtube_urls;
        }
        try {
            do {
                youtube_urls.add(cursor.getString(cursor.getColumnIndex(Columns.CONTENT)));
            } while (cursor.moveToNext());
            return youtube_urls;
        } finally {
            cursor.close();
        }
    }

    public static void clearMovies(Context context) {
        context.getContentResolver().delete(URI_MOVIES, null, null);
    }

    public static void clearTrailers(Context context) {
        context.getContentResolver().delete(URI_TRAILERS, null, null);
    }
    public static void clearReviews(Context context) {
        context.getContentResolver().delete(URI_REVIEWS, null, null);
    }

    public interface Columns {
        String MOVIE_ID = "movie_id";
        String ORIGINAL_TITLE = "originalTitle";
        String POSTER_PATH = "poster_path";
        String VOTE_COUNT = "vote_count";
        String VOTE_AVERAGE = "vote_average";
        String RELEASE_DATE = "release_date";
        String OVERVIEW = "overview";
        String RUNTIME = "runtime";

        String CONTENT = "content";
        String YOUTUBE_URL = "youtube_url";
    }

    public interface Requests {

        String MOVIES_TABLE_NAME = "movies";
        String TRAILERS_TABLE_NAME = "trailers";
        String REVIEWS_TABLE_NAME = "reviews";

        String CREATION_MOVIES_REQUEST = "CREATE TABLE IF NOT EXISTS " + MOVIES_TABLE_NAME + " (" +
                Columns.MOVIE_ID + " VARCHAR(10) NOT NULL, " +
                Columns.ORIGINAL_TITLE + " VARCHAR(200), " +
                Columns.POSTER_PATH + " VARCHAR(200), " +
                Columns.VOTE_COUNT + " VARCHAR(10), " +
                Columns.VOTE_AVERAGE + " VARCHAR(10), " +
                Columns.RELEASE_DATE + " VARCHAR(20), " +
                Columns.OVERVIEW + " VARCHAR(500), " +
                Columns.RUNTIME + " VARCHAR(10)" + ");";

        String CREATION_TRAILERS_REQUEST = "CREATE TABLE IF NOT EXISTS " + TRAILERS_TABLE_NAME + " (" +
                Columns.MOVIE_ID + " VARCHAR(10) NOT NULL, " +
                Columns.YOUTUBE_URL + " VARCHAR(200)" + ");";

        String CREATION_REVIEWS_REQUEST = "CREATE TABLE IF NOT EXISTS " +REVIEWS_TABLE_NAME + " (" +
                Columns.MOVIE_ID + " VARCHAR(10) NOT NULL, " +
                Columns.CONTENT + " VARCHAR(2000)" + ");";

        String DROP_MOVIES_REQUEST = "DROP TABLE IF EXISTS " + MOVIES_TABLE_NAME;
        String DROP_TRAILERS_REQUEST = "DROP TABLE IF EXISTS " + TRAILERS_TABLE_NAME;
        String DROP_REVIEWS_REQUEST = "DROP TABLE IF EXISTS " + REVIEWS_TABLE_NAME;
    }

}
