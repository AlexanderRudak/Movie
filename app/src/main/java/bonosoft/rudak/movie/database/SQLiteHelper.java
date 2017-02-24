package bonosoft.rudak.movie.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;


public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String CONTENT_AUTHORITY = "bonosoft.rudak.movie.loaders";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private static final String DATABASE_NAME = "bonosoft.rudak.database.db";

    private static final int DATABASE_VERSION = 1;

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(MoviesTable.Requests.CREATION_MOVIES_REQUEST);
        db.execSQL(MoviesTable.Requests.CREATION_TRAILERS_REQUEST);
        db.execSQL(MoviesTable.Requests.CREATION_REVIEWS_REQUEST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(MoviesTable.Requests.DROP_MOVIES_REQUEST);
        db.execSQL(MoviesTable.Requests.DROP_TRAILERS_REQUEST);
        db.execSQL(MoviesTable.Requests.DROP_REVIEWS_REQUEST);
        onCreate(db);
    }
}
