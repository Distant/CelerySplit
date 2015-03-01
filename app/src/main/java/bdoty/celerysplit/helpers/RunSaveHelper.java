package bdoty.celerysplit.helpers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RunSaveHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "runs";
    private static final String TABLE_SEGMENTS = "splits";

    private static final String KEY_NAME = "name";
    private static final String KEY_PB_TIME = "pb_ime";
    private static final String KEY_BEST_SEG = "best_seg";


    public RunSaveHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_SEGMENTS + "("
                + KEY_NAME + " TEXT," + KEY_PB_TIME + " INTEGER,"
                + KEY_BEST_SEG + " INTEGER" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SEGMENTS);

        // Create tables again
        onCreate(db);

    }
}
