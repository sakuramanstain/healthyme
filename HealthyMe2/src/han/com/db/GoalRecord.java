package han.com.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import han.com.datapool.PreferenceItem;
import han.com.fragment.goal.FragmentGoalList;

/**
 *
 * @author hanaldo
 */
public class GoalRecord implements PreferenceItem {

    private static final String className = GoalRecord.class.getName();
    public static final String TABLE_NAME = "goal_record";
    public static final String COL_RECORD_ID = "record_id";
    public static final String COL_GOAL_ID = "goal_id";
    public static final String COL_START_DATE = "start_date";
    public static final String COL_END_DATE = "end_date";
    public static final String COL_COMPLETE = "complete";
    public static final String COL_TOTAL_TIME = "total_time";
    public static final String COL_TOTAL_STEPS = "total_steps";
    public static final String COL_TOTAL_CALORIES = "total_calories";
    public static final int COMPLETE_FAIL = 0;
    public static final int COMPLETE_SUCCESS = 1;
    public static final int COMPLETE_TEMP = 2;

    public static GoalRecord addNewGoalRecord(SQLiteDatabase db, long goalId, long startDate) {
        ContentValues values = new ContentValues();
        values.put(COL_GOAL_ID, goalId);
        values.put(COL_START_DATE, startDate);
        values.put(COL_COMPLETE, COMPLETE_TEMP);

        long r = db.insert(TABLE_NAME, null, values);
        db.close();

        if (r == -1) {
            Log.d(className, "fail record inserted");
            return null;
        } else {
            Log.d(className, "new record inserted");
            GoalRecord record = new GoalRecord();
            record.setRecordId(r);
            record.setGoalId(goalId);
            record.setStartDate(startDate);
            record.setComplete(COMPLETE_TEMP);
            return record;
        }
    }

    public static void endGoal(SQLiteDatabase db, long recordId, long endTime, int completeType, long totalTime, int totalSteps, float totalCalories) {
        ContentValues values = new ContentValues();
        values.put(COL_END_DATE, endTime);
        values.put(COL_COMPLETE, completeType);
        values.put(COL_TOTAL_TIME, totalTime);
        values.put(COL_TOTAL_STEPS, totalSteps);
        values.put(COL_TOTAL_CALORIES, totalCalories);

        db.update(TABLE_NAME, values, COL_RECORD_ID + "=?", new String[]{String.valueOf(recordId)});
        db.close();
        Log.d(className, "goal record " + recordId + " is ended " + completeType);
        FragmentGoalList.getReloadListHandler().sendEmptyMessage(0);
    }

    public static int getTotalNumberGoalsInCompleteType(SQLiteDatabase db, int completeType) {
        String sql = "select count(*) from " + TABLE_NAME + " where " + COL_COMPLETE + "=" + completeType;
        Log.d(className, "getTotalNumberGoalsInCompleteType: " + sql);

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int r = cursor.getInt(0);

        cursor.close();
        db.close();
        return r;
    }

    public static int getTotalNumberGoals(SQLiteDatabase db) {
        String sql = "select count(*) from " + TABLE_NAME;
        Log.d(className, "getTotalNumberGoals: " + sql);

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int r = cursor.getInt(0);

        cursor.close();
        db.close();
        return r;
    }

    public static float getTotalCompleteGoalValue(SQLiteDatabase db, String goalType) {
        String sql = "select sum(" + UserGoal.COL_GOAL_VALUE + ") from "
                + UserGoal.TABLE_NAME + ", " + TABLE_NAME
                + " where " + COL_COMPLETE + "=" + COMPLETE_SUCCESS
                + " and " + UserGoal.TABLE_NAME + "." + UserGoal.COL_GOAL_ID + "=" + TABLE_NAME + "." + COL_GOAL_ID
                + " and " + UserGoal.TABLE_NAME + "." + UserGoal.COL_GOAL_TYPE + "='" + goalType + "'";

        Log.d(className, "getTotalCompleteGoalValue: " + sql);

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            return 0;
        }

        cursor.moveToFirst();
        float r = cursor.getFloat(0);

        cursor.close();
        db.close();
        return r;
    }

    public static long getTotalCompleteGoalTime(SQLiteDatabase db) {
        String sql = "select sum(" + COL_TOTAL_TIME + ") from "
                + TABLE_NAME
                + " where " + COL_COMPLETE + "=" + COMPLETE_SUCCESS;

        Log.d(className, "getTotalCompleteGoalTime: " + sql);

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            return 0;
        }

        cursor.moveToFirst();
        long r = cursor.getLong(0);

        cursor.close();
        db.close();
        return r;
    }

    public static int getTotalCompleteGoalSteps(SQLiteDatabase db) {
        String sql = "select sum(" + COL_TOTAL_STEPS + ") from "
                + TABLE_NAME
                + " where " + COL_COMPLETE + "=" + COMPLETE_SUCCESS;

        Log.d(className, "getTotalCompleteGoalSteps: " + sql);

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            return 0;
        }

        cursor.moveToFirst();
        int r = cursor.getInt(0);

        cursor.close();
        db.close();
        return r;
    }

    public static float getTotalCompleteGoalCalories(SQLiteDatabase db) {
        String sql = "select sum(" + COL_TOTAL_CALORIES + ") from "
                + TABLE_NAME
                + " where " + COL_COMPLETE + "=" + COMPLETE_SUCCESS;

        Log.d(className, "getTotalCompleteGoalCalories: " + sql);

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            return 0;
        }

        cursor.moveToFirst();
        float r = cursor.getFloat(0);

        cursor.close();
        db.close();
        return r;
    }

    public static long getLastActivity(SQLiteDatabase db) {
        String sql = "select max(" + COL_START_DATE + ") from " + TABLE_NAME;

        Log.d(className, "getLastActivity: " + sql);

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            return -1;
        }

        cursor.moveToFirst();
        long r = cursor.getLong(0);
        if (r == 0) {
            return -1;
        }

        return r;
    }
    private long recordId;
    private long goalId;
    private long startDate;
    private long endDate;
    private int complete;
    private long totalTime;
    private int totalSteps;
    private float totalCalories;

    public GoalRecord() {
    }

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public long getGoalId() {
        return goalId;
    }

    public void setGoalId(long goalId) {
        this.goalId = goalId;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getComplete() {
        return complete;
    }

    public void setComplete(int complete) {
        this.complete = complete;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public float getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(float totalCalories) {
        this.totalCalories = totalCalories;
    }
}
