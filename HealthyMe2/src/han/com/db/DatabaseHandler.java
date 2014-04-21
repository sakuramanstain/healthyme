package han.com.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 *
 * @author han
 */
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "healthyme.sqlite";
    private static DatabaseHandler instance;
    private static final String className = DatabaseHandler.class.getName();

    public static DatabaseHandler getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHandler(context);
        }
        return instance;
    }

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + UserGoal.TABLE_NAME + " ("
                + UserGoal.COL_GOAL_ID + " INTEGER PRIMARY KEY,"
                + UserGoal.COL_GOAL_NAME + " TEXT,"
                + UserGoal.COL_GOAL_ORDER + " INTEGER,"
                + UserGoal.COL_GOAL_INTENSE + " INTEGER,"
                + UserGoal.COL_GOAL_VALUE + " REAL,"
                + UserGoal.COL_GOAL_UNIT + " TEXT,"
                + UserGoal.COL_GOAL_TYPE + " TEXT,"
                + UserGoal.COL_VALID + " INTEGER,"
                + UserGoal.COL_GOAL_RECEIVE_TIME + " INTEGER,"
                + UserGoal.COL_ICON + " INTEGER,"
                + UserGoal.COL_FREQUENCY + " TEXT"
                + ")";
        db.execSQL(sql);
        Log.d(className, sql);

        String sql2 = "CREATE TABLE " + GoalRecord.TABLE_NAME + " ("
                + GoalRecord.COL_RECORD_ID + " INTEGER PRIMARY KEY,"
                + GoalRecord.COL_GOAL_ID + " INTEGER,"
                + GoalRecord.COL_START_DATE + " INTEGER,"
                + GoalRecord.COL_END_DATE + " INTEGER,"
                + GoalRecord.COL_COMPLETE + " INTEGER,"
                + GoalRecord.COL_TOTAL_TIME + " INTEGER,"
                + GoalRecord.COL_TOTAL_STEPS + " INTEGER,"
                + GoalRecord.COL_TOTAL_CALORIES + " REAL"
                + ")";
        db.execSQL(sql2);
        Log.d(className, sql2);

        String sql3 = "CREATE TABLE " + Reward.TABLE_NAME + " ("
                + Reward.COL_REWARD_ID + " INTEGER PRIMARY KEY,"
                + Reward.COL_REWARD_NUMBER + " INTEGER,"
                + Reward.COL_REWARD_NAME + " TEXT,"
                + Reward.COL_REWARD_OBTAINED + " INTEGER"
                + ")";
        db.execSQL(sql3);
        Log.d(className, sql3);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(className, "onUpgrade");
        db.execSQL("DROP TABLE IF EXISTS " + UserGoal.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GoalRecord.TABLE_NAME);
        onCreate(db);
    }

    public void clean() {
        instance.close();
        instance = null;
    }
}
