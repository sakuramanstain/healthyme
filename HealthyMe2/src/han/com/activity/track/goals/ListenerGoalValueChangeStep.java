package han.com.activity.track.goals;

import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

/**
 *
 * @author han
 */
public class ListenerGoalValueChangeStep implements OnValueChangeListener {

    private ActivityGoalSettingStep activity;
    private int digitType;

    public ListenerGoalValueChangeStep(ActivityGoalSettingStep activity, int digitType) {
        this.activity = activity;
        this.digitType = digitType;
    }

    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (activity != null) {
            activity.update(digitType, newVal);
        }
    }
}
