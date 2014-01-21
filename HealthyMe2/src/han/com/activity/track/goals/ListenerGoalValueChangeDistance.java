package han.com.activity.track.goals;

import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

/**
 *
 * @author han
 */
public class ListenerGoalValueChangeDistance implements OnValueChangeListener {

    private ActivityGoalSettingDistance activity;
    private int digitType;

    public ListenerGoalValueChangeDistance(ActivityGoalSettingDistance activity, int digitType) {
        this.activity = activity;
        this.digitType = digitType;
    }

    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (activity != null) {
            activity.update(digitType, newVal);
        }
    }
}
