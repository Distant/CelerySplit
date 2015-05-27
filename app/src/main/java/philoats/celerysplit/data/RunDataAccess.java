package philoats.celerysplit.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import philoats.celerysplit.models.Run;
import philoats.celerysplit.models.SplitSet;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RunDataAccess {
    private SQLiteDatabase database;
    private RunDatabaseHelper dbHelper;

    public RunDataAccess(Context context) {
        dbHelper = new RunDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long addRun(SplitSet set) {
        open(); // if -1 just add, if not, delete that id and add


        ContentValues values = new ContentValues();
        values.put(RunDatabaseHelper.COLUMN_TITLE, set.getTitle());
        long insertId = database.insert(RunDatabaseHelper.TABLE_RUNS, null, values);

        ContentValues segmentValues;
        for (int i = 0; i < set.getCount(); i++) {
            segmentValues = new ContentValues();
            segmentValues.put(RunDatabaseHelper.COLUMN_RUN_ID, insertId);
            segmentValues.put(RunDatabaseHelper.COLUMN_SEGMENT_NAME, set.names[i]);
            segmentValues.put(RunDatabaseHelper.COLUMN_PB_TIME, set.pbTimes[i]);
            segmentValues.put(RunDatabaseHelper.COLUMN_BEST_SEG, set.bestSegments[i]);
            segmentValues.put(RunDatabaseHelper.COLUMN_INDEX, i);
            database.insert(RunDatabaseHelper.TABLE_SEGMENTS, null, segmentValues);
        }
        close();
        return insertId;
    }

    public void deleteRun(long _id) {
        open();
        int count = database.delete(RunDatabaseHelper.TABLE_SEGMENTS,
                RunDatabaseHelper.COLUMN_RUN_ID + " = " + _id, null);
        System.out.println("Deleted " + count + " rows");
        database.delete(RunDatabaseHelper.TABLE_RUNS,
                RunDatabaseHelper.COLUMN_ID + " = " + _id, null);

        System.out.println("deleting run # " + _id);
        close();
    }

    public void updateRun(SplitSet set) {
        open();
        if (set.getId() < 0) {
            addRun(set);
        } else {
            System.out.println("updating run # " + set.getId());
            deleteRun(set.getId());
            addRun(set);
        }
        close();
    }

    public Observable<ArrayList<Run>> getRuns() {
        open();
        ArrayList<Run> list = new ArrayList<>();
        Cursor cursor = database.query(RunDatabaseHelper.TABLE_RUNS,
                null, null, null,
                null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            list.add(new Run(Long.parseLong(cursor.getString(0)), cursor.getString(1)));
            cursor.moveToNext();
        }
        cursor.close();
        close();
        return Observable.just(list).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<SplitSet> getSet(Run run) {
        open();
        SplitSet set;
        Cursor cursor = database.query(RunDatabaseHelper.TABLE_SEGMENTS,
                null, RunDatabaseHelper.COLUMN_RUN_ID + " = " + run.get_id(), null,
                null, null, null);

        int count = cursor.getCount();
        String[] names = new String[count];
        long[] pbTimes = new long[count];
        long[] bestTimes = new long[count];

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int index = cursor.getInt(5);
            names[index] = cursor.getString(2);
            pbTimes[index] = cursor.getLong(3);
            bestTimes[index] = cursor.getLong(4);
            cursor.moveToNext();
        }
        set = new SplitSet(run.get_id(), run.getTitle(), names, pbTimes, bestTimes);
        cursor.close();
        close();
        return Observable.just(set).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }
}