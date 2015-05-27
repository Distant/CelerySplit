package philoats.celerysplit.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RunDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "splits.db";

    public static final String TABLE_SEGMENTS = "segments";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_RUN_ID = "run_id";
    public static final String COLUMN_SEGMENT_NAME = "name";
    public static final String COLUMN_PB_TIME = "pb_ime";
    public static final String COLUMN_BEST_SEG = "best_seg";
    public static final String COLUMN_INDEX = "segment_index";

    public static final String TABLE_RUNS = "runs";
    public static final String COLUMN_TITLE = "title";

    private static final String CREATE_SEGMENTS_TABLE = "CREATE TABLE " + TABLE_SEGMENTS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY, "
            + COLUMN_RUN_ID + " INTEGER, "
            + COLUMN_SEGMENT_NAME + " TEXT, "
            + COLUMN_PB_TIME + " INTEGER, "
            + COLUMN_BEST_SEG + " INTEGER, "
            + COLUMN_INDEX + " INTEGER, "
            + "FOREIGN KEY ("+COLUMN_RUN_ID+") REFERENCES "+TABLE_RUNS+" ("+COLUMN_ID+")"
            + ");";

    private static final String CREATE_RUNS_TABLE = "CREATE TABLE " + TABLE_RUNS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_TITLE + " STRING);";

    public RunDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RUNS_TABLE);
        db.execSQL(CREATE_SEGMENTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEGMENTS);
        onCreate(db);
    }
}
