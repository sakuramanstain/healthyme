package han.com.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import han.com.datapool.PreferenceItem;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author han
 */
public class UserGoal implements PreferenceItem {

    private static final String className = UserGoal.class.getName();
    public static final String TABLE_NAME = "user_goals";
    public static final String COL_GOAL_ID = "goal_id";
    public static final String COL_GOAL_NAME = "goal_name";
    public static final String COL_GOAL_ORDER = "goal_order";
    public static final String COL_GOAL_INTENSE = "goal_intense";
    public static final String COL_GOAL_VALUE = "goal_value";
    public static final String COL_GOAL_UNIT = "goal_unit";
    public static final String COL_GOAL_TYPE = "goal_type";
    public static final String COL_VALID = "valid";
    public static final String COL_GOAL_RECEIVE_TIME = "goal_receive_time";
    public static final String GOAL_TYPE_DISTANCE = "user.goal.type.distance";
    public static final String GOAL_TYPE_STEP = "user.goal.type.step";
    public static final String GOAL_TYPE_CALORIES = "user.goal.type.cal";
    public static final String GOAL_TYPE_TIME = "user.goal.type.time";
    public static final int GOAL_IS_VALID = 1;
    public static final int GOAL_IS_NOT_VALID = 0;
    public static final int GOAL_IS_COACH_NEW = 2;
    public static final int GOAL_IS_COACH_FINISHED = 3;
    public static final String GOAL_NAME_COACH = "Coach";

    public static List<UserGoal> getAllCoachGoals(SQLiteDatabase db, long after, long before) {
        List<UserGoal> userGoalList = new LinkedList<UserGoal>();

        String sql = "select * from " + TABLE_NAME + " where " + COL_GOAL_NAME + "='" + GOAL_NAME_COACH + "'"
                + " and " + COL_GOAL_RECEIVE_TIME + ">" + after
                + " and " + COL_GOAL_RECEIVE_TIME + "<" + before
                + " order by " + COL_GOAL_RECEIVE_TIME + " asc";

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                UserGoal ug = new UserGoal();
                ug.setGoalId(cursor.getInt(0));
                ug.setGoalName(cursor.getString(1));
                ug.setGoalOrder(cursor.getInt(2));
                ug.setGoalIntense(cursor.getInt(3));
                ug.setGoalValue(cursor.getFloat(4));
                ug.setGoalUnit(cursor.getString(5));
                ug.setGoalType(cursor.getString(6));
                ug.setValid(cursor.getInt(7));
                ug.setGoalReceiveTime(cursor.getLong(8));

                userGoalList.add(ug);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return userGoalList;
    }

    public static boolean isFinishedOneCoachGoalToday(SQLiteDatabase db) {
        Calendar calendar = new GregorianCalendar();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);

        calendar.clear();
        calendar.set(year, month, date);

        long today = calendar.getTimeInMillis();
        long tomorrow = today + 24 * 60 * 60 * 1000;

        String sql = "select " + COL_GOAL_ID + " from " + TABLE_NAME + " where " + COL_GOAL_NAME + "='" + GOAL_NAME_COACH + "'"
                + " and " + COL_VALID + "=" + GOAL_IS_COACH_FINISHED
                + " and " + COL_GOAL_RECEIVE_TIME + ">" + today
                + " and " + COL_GOAL_RECEIVE_TIME + "<=" + tomorrow;

        boolean yes = true;
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            yes = false;
        }
        cursor.close();
        db.close();

        Log.d(className, "finishedOneCoachGoalToday: " + sql);
        Log.d(className, "finishedOneCoachGoalToday: " + yes);

        return yes;
    }

    public static List<UserGoal> getUserGoalsByName(SQLiteDatabase db, String name) {
        List<UserGoal> userGoalList = new LinkedList<UserGoal>();

        String sql = "select * from " + TABLE_NAME + " where " + COL_VALID + "=" + GOAL_IS_VALID
                + " and " + COL_GOAL_NAME + "='" + name + "'";

        if (name.contains("coach")) {
            sql = "select * from " + TABLE_NAME + " where " + COL_VALID + "=" + GOAL_IS_COACH_NEW;
        }

        if (name.contains("own")) {
            sql = "select * from " + TABLE_NAME + " where " + COL_VALID + "=" + GOAL_IS_VALID;
        }

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                UserGoal ug = new UserGoal();
                ug.setGoalId(cursor.getInt(0));
                ug.setGoalName(cursor.getString(1));
                ug.setGoalOrder(cursor.getInt(2));
                ug.setGoalIntense(cursor.getInt(3));
                ug.setGoalValue(cursor.getFloat(4));
                ug.setGoalUnit(cursor.getString(5));
                ug.setGoalType(cursor.getString(6));
                ug.setValid(cursor.getInt(7));

                userGoalList.add(ug);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return userGoalList;
    }

    public static UserGoal loadUserGoal(SQLiteDatabase db, long goalId) {
        String sql = "select * from " + TABLE_NAME + " where " + COL_GOAL_ID + "=" + goalId;
        Cursor cursor = db.rawQuery(sql, null);
        UserGoal ug = new UserGoal();

        if (cursor.moveToFirst()) {
            ug.setGoalId(cursor.getInt(0));
            ug.setGoalName(cursor.getString(1));
            ug.setGoalOrder(cursor.getInt(2));
            ug.setGoalIntense(cursor.getInt(3));
            ug.setGoalValue(cursor.getFloat(4));
            ug.setGoalUnit(cursor.getString(5));
            ug.setGoalType(cursor.getString(6));
            ug.setValid(cursor.getInt(7));
        }
        cursor.close();
        db.close();

        return ug;
    }

    public static UserGoal loadNextGoachGoal(SQLiteDatabase db) {
        String sql = "select * from " + TABLE_NAME
                + " where " + COL_GOAL_NAME + " ='" + GOAL_NAME_COACH + "' and " + COL_VALID + "=" + GOAL_IS_COACH_NEW
                + " order by " + COL_GOAL_RECEIVE_TIME + " asc limit 1";
        Log.d(className, "loadNextGoachGoal: " + sql);

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return null;
        }

        UserGoal ug = new UserGoal();

        if (cursor.moveToFirst()) {
            ug.setGoalId(cursor.getInt(0));
            ug.setGoalName(cursor.getString(1));
            ug.setGoalOrder(cursor.getInt(2));
            ug.setGoalIntense(cursor.getInt(3));
            ug.setGoalValue(cursor.getFloat(4));
            ug.setGoalUnit(cursor.getString(5));
            ug.setGoalType(cursor.getString(6));
            ug.setValid(cursor.getInt(7));
        }
        cursor.close();
        db.close();

        return ug;
    }

    public static long addNewUserGoal(SQLiteDatabase db, UserGoal ug) {
        ContentValues values = new ContentValues();
        values.put(COL_GOAL_NAME, ug.getGoalName());
        values.put(COL_GOAL_ORDER, ug.getGoalOrder());
        values.put(COL_GOAL_INTENSE, ug.getGoalIntense());
        values.put(COL_GOAL_VALUE, ug.getGoalValue());
        values.put(COL_GOAL_UNIT, ug.getGoalUnit());
        values.put(COL_GOAL_TYPE, ug.getGoalType());
        values.put(COL_VALID, ug.getValid());
        values.put(COL_GOAL_RECEIVE_TIME, ug.getGoalReceiveTime());

        long r = db.insert(TABLE_NAME, null, values);
        db.close();
        Log.d(className, "new record inserted");
        return r;
    }

    public static void deleteUserGoal(SQLiteDatabase db, long goalId) {
        ContentValues values = new ContentValues();
        values.put(COL_VALID, GOAL_IS_NOT_VALID);
        db.update(TABLE_NAME, values, COL_GOAL_ID + "=?", new String[]{String.valueOf(goalId)});
        db.close();
        Log.d(className, "goal " + goalId + " is deleted");
    }

    public static void finishCoachGoal(SQLiteDatabase db, long goalId) {
        ContentValues values = new ContentValues();
        values.put(COL_VALID, GOAL_IS_COACH_FINISHED);
        db.update(TABLE_NAME, values, COL_GOAL_ID + "=?", new String[]{String.valueOf(goalId)});
        db.close();
        Log.d(className, "coach goal " + goalId + " is finished");
    }

    public static int getGoalNameNextOrder(SQLiteDatabase db, String goalName) {
        String sql = "select max(" + COL_GOAL_ORDER + ") from " + TABLE_NAME + " where " + COL_GOAL_NAME + " = '" + goalName + "'";

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            return 1;
        }

        cursor.moveToFirst();
        int r = cursor.getInt(0) + 1;

        cursor.close();
        db.close();
        return r;
    }

    public static int getTotalNewCoachGoalsNumber(SQLiteDatabase db) {
        String sql = "select count(*) from " + TABLE_NAME + " where " + COL_VALID + "=" + GOAL_IS_COACH_NEW;
        Log.d(className, "getTotalNewCoachGoalsNumber: " + sql);

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int r = cursor.getInt(0);

        cursor.close();
        db.close();
        return r;
    }

    public static int getTotalFinishedCoachGoalsNumber(SQLiteDatabase db) {
        String sql = "select count(*) from " + TABLE_NAME + " where " + COL_VALID + "=" + GOAL_IS_COACH_FINISHED;
        Log.d(className, "getTotalFinishedCoachGoalsNumber: " + sql);

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int r = cursor.getInt(0);

        cursor.close();
        db.close();
        return r;
    }

    public static int getTotalCoachGoalsNumber(SQLiteDatabase db) {
        String sql = "select count(*) from " + TABLE_NAME + " where " + COL_VALID + "=" + GOAL_IS_COACH_FINISHED
                + " or " + COL_VALID + "=" + GOAL_IS_COACH_NEW;
        Log.d(className, "getTotalCoachGoalsNumber: " + sql);

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToFirst();
        int r = cursor.getInt(0);

        cursor.close();
        db.close();
        return r;
    }
    private long goalId;
    private String goalName;
    private int goalOrder;
    private int goalIntense;
    private float goalValue;
    private String goalUnit;
    private String goalType;
    private int valid;
    private long goalReceiveTime;

    public UserGoal() {
    }

    public long getGoalId() {
        return goalId;
    }

    public void setGoalId(long goalId) {
        this.goalId = goalId;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public int getGoalOrder() {
        return goalOrder;
    }

    public void setGoalOrder(int goalOrder) {
        this.goalOrder = goalOrder;
    }

    public int getGoalIntense() {
        return goalIntense;
    }

    public void setGoalIntense(int goalIntense) {
        this.goalIntense = goalIntense;
    }

    public float getGoalValue() {
        return goalValue;
    }

    public void setGoalValue(float goalValue) {
        this.goalValue = goalValue;
    }

    public String getGoalUnit() {
        return goalUnit;
    }

    public void setGoalUnit(String goalUnit) {
        this.goalUnit = goalUnit;
    }

    public String getGoalType() {
        return goalType;
    }

    public void setGoalType(String goalType) {
        this.goalType = goalType;
    }

    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    public long getGoalReceiveTime() {
        return goalReceiveTime;
    }

    public void setGoalReceiveTime(long goalReceiveTime) {
        this.goalReceiveTime = goalReceiveTime;
    }
}
