package han.com.datapool;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import com.google.gson.Gson;

/**
 *
 * @author han
 */
public class MySharedPreferences {

    private static final String className = MySharedPreferences.class.getName();
    private static final String SHARED_PREFERENCE_FILE = "my_preference";
    private static final String TRACKING_MODULE = "user.tracking.module";
    private static final String FIRST_TIME = "user.reward.firsttime";
    private static final String REMIND_REWARD = "user.reward.remind";
    private static final String USER_NAME = "user.name";
    private static final String LAST_SYNC = "user.sync";
    private static final String PLAY_LOCAL_MUSIC = "user.music.local";
    private static final String MUSIC_FILES = "user.music.files";
    public static final String STOP_REMINDER = "user.tracking.stop";
    public static final String MUSIC_SONGS = "user.music.songs";
    public static final int TRACKING_MODULE_PEDO_INDEX = 0;
    public static final int TRACKING_MODULE_BOTH_INDEX = 1;
    private static MySharedPreferences instance;

    public static MySharedPreferences getInstance(Activity activity) {
        if (instance == null) {
            instance = new MySharedPreferences(activity);
        }
        return instance;
    }
    private SharedPreferences pref;

    private MySharedPreferences(Activity activity) {
        pref = activity.getSharedPreferences(SHARED_PREFERENCE_FILE, 0);
    }

    public void clean() {
        pref = null;
        instance = null;
    }

    public int getTrackingModule() {
        return pref.getInt(TRACKING_MODULE, 0);
    }

    public boolean setTrackingModule(int module) {
        Editor editor = pref.edit();
        editor.putInt(TRACKING_MODULE, module);
        return editor.commit();
    }

    public boolean getFirstTime() {
        return pref.getBoolean(FIRST_TIME, false);
    }

    public boolean setFirstTime(boolean f) {
        Editor editor = pref.edit();
        editor.putBoolean(FIRST_TIME, f);
        return editor.commit();
    }

    public boolean getRemindReward() {
        return pref.getBoolean(REMIND_REWARD, false);
    }

    public boolean setRemindReward(boolean r) {
        Editor editor = pref.edit();
        editor.putBoolean(REMIND_REWARD, r);
        return editor.commit();
    }

    public String getUserName() {
        return pref.getString(USER_NAME, "");
    }

    public boolean setUserName(String name) {
        Editor editor = pref.edit();
        editor.putString(USER_NAME, name);
        return editor.commit();
    }

    public long getLastSync() {
        return pref.getLong(LAST_SYNC, -1);
    }

    public boolean setLastSync(long time) {
        Editor editor = pref.edit();
        editor.putLong(LAST_SYNC, time);
        return editor.commit();
    }

    public boolean getPlayLocalMusic() {
        return pref.getBoolean(PLAY_LOCAL_MUSIC, true);
    }

    public boolean setPlayLocalMusic(boolean local) {
        Editor editor = pref.edit();
        editor.putBoolean(PLAY_LOCAL_MUSIC, local);
        return editor.commit();
    }

    public boolean getMusicFilesAreLocal() {
        return pref.getBoolean(MUSIC_FILES, false);
    }

    public boolean setMusicFilesAreLocal(boolean local) {
        Editor editor = pref.edit();
        editor.putBoolean(MUSIC_FILES, local);
        return editor.commit();
    }

    public PreferenceItem getPreferenceItem(String itemName, Class<?> objectType) {
        String json = pref.getString(itemName, "");
        if (json.isEmpty()) {
            Log.d(className, itemName + " " + objectType.getName() + " is empty");
            return null;
        } else {
            Gson gson = new Gson();
            PreferenceItem r = (PreferenceItem) gson.fromJson(json, objectType);
            return r;
        }
    }

    public boolean setPreferenceItem(String itemName, PreferenceItem item) {
        Log.d(className, "write " + item.toString());
        Editor editor = pref.edit();
        Gson gson = new Gson();
        String json = gson.toJson(item);
        editor.putString(itemName, json);
        return editor.commit();
    }

    public boolean removePreferenceItem(String itemName) {
        Editor editor = pref.edit();
        editor.remove(itemName);
        return editor.commit();
    }
}
