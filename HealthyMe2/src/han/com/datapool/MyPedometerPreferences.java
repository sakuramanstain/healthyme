package han.com.datapool;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 *
 * @author han
 */
public class MyPedometerPreferences {

    private static final String className = MyPedometerPreferences.class.getName();
    private static final String SHARED_PREFERENCE_FILE = "han.com_preferences";
    private static final String sensitivity = "sensitivity";
    private static final String body_weight = "body_weight";
    private static final String units = "units";
    private static final String step_length = "step_length";
    private static final String operation_level = "operation_level";
    private static final String exercise_type = "exercise_type";
    public static final String[][] PEDOMETER_POSITION_VALUES = new String[][]{
        new String[]{"armband", "15"},
        new String[]{"pocket", "33.75"}};
    private static MyPedometerPreferences instance;

    public static MyPedometerPreferences getInstance(Activity activity) {
        if (instance == null) {
            instance = new MyPedometerPreferences(activity);
        }
        return instance;
    }
    private SharedPreferences pref;

    private MyPedometerPreferences(Activity activity) {
        pref = activity.getSharedPreferences(SHARED_PREFERENCE_FILE, 0);
    }

    public void clean() {
        pref = null;
        instance = null;
    }

    public boolean setAll(MyPedometerValuePack pack) {
        Editor editor = pref.edit();
        editor.putString(sensitivity, pack.getSensitivity());
        editor.putString(body_weight, pack.getBodyWeight());
        editor.putString(step_length, pack.getStepLength());
        editor.putString(exercise_type, pack.getExerciseType());
        editor.putString(units, "metric");
        editor.putString(operation_level, "run_in_background");
        return editor.commit();
    }

    public boolean setSensitivity(String s) {
        Editor editor = pref.edit();
        editor.putString(sensitivity, s);
        return editor.commit();
    }

    public String getSensitivity() {
        return pref.getString(sensitivity, "");
    }
}
