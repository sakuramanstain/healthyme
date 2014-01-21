package han.com.activity.track.goals;

import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

/**
 *
 * @author han
 */
public class ListenerGoalValueChangeCalories implements OnValueChangeListener {

    private ActivityGoalSettingCalories activity;
    private int digitType;

    public ListenerGoalValueChangeCalories(ActivityGoalSettingCalories activity, int digitType) {
        this.activity = activity;
        this.digitType = digitType;
    }

    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (activity != null) {
            activity.update(digitType, newVal);
        }
    }
}
