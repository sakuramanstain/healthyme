package han.com.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author hanaldo
 */
public class Reward {

    private static final String className = Reward.class.getName();
    public static final String TABLE_NAME = "user_rewards";
    public static final String COL_REWARD_ID = "reward_id";
    public static final String COL_REWARD_NUMBER = "reward_number";
    public static final String COL_REWARD_NAME = "reward_name";
    public static final String COL_REWARD_OBTAINED = "reward_obtained";
    public static final int REWARD_IS_OBTAINED = 1;
    public static final int REWARD_IS_NOT_OBTAINED = 0;
    private long rewardId;
    private long rewardNumber;
    private String rewardName;
    private int rewardObtained;

    public Reward() {
    }

    public long getRewardId() {
        return rewardId;
    }

    public void setRewardId(long rewardId) {
        this.rewardId = rewardId;
    }

    public long getRewardNumber() {
        return rewardNumber;
    }

    public void setRewardNumber(long rewardNumber) {
        this.rewardNumber = rewardNumber;
    }

    public String getRewardName() {
        return rewardName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public int getRewardObtained() {
        return rewardObtained;
    }

    public void setRewardObtained(int rewardObtained) {
        this.rewardObtained = rewardObtained;
    }

    public static boolean addReward(SQLiteDatabase db, long rewardNumber, String rewardName) {
        ContentValues values = new ContentValues();
        values.put(COL_REWARD_NUMBER, rewardNumber);
        values.put(COL_REWARD_NAME, rewardName);
        values.put(COL_REWARD_OBTAINED, REWARD_IS_NOT_OBTAINED);

        long r = db.insert(TABLE_NAME, null, values);
        db.close();

        if (r == -1) {
            Log.d(className, "fail reward inserted");
            return false;
        } else {
            Log.d(className, "new reward inserted");
            return true;
        }
    }

    public static Map<Long, Reward> getAllRewards(SQLiteDatabase db) {
        Map<Long, Reward> rewards = new HashMap<Long, Reward>(100);

        String sql = "select * from " + TABLE_NAME;

        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                Reward reward = new Reward();
                reward.setRewardId(cursor.getLong(0));
                reward.setRewardNumber(cursor.getLong(1));
                reward.setRewardName(cursor.getString(2));
                reward.setRewardObtained(cursor.getInt(3));

                rewards.put(reward.getRewardNumber(), reward);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return rewards;
    }

    public static Reward getObtainedRewardByNumber(SQLiteDatabase db, long rewardNumber) {
        String sql = "select * from " + TABLE_NAME
                + " where " + COL_REWARD_NUMBER + "=" + rewardNumber;

        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() == 0) {
            return null;
        }

        cursor.moveToFirst();
        Reward reward = new Reward();
        reward.setRewardId(cursor.getLong(0));
        reward.setRewardNumber(cursor.getLong(1));
        reward.setRewardName(cursor.getString(2));
        reward.setRewardObtained(cursor.getInt(3));

        return reward;
    }
}
