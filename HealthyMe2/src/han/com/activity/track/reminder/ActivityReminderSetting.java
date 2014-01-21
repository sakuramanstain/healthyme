package han.com.activity.track.reminder;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import han.com.R;
import han.com.datapool.MySharedPreferences;
import han.com.tts.Speak;
import han.com.utils.MyWidgets;
import han.com.utils.Values;

/**
 *
 * @author han
 */
public class ActivityReminderSetting extends Activity implements CompoundButton.OnCheckedChangeListener {

    private static final String className = ActivityReminderSetting.class.getName();
    private ProgressReminder progressReminder;
    private Switch sw;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_setting);

        MyWidgets.makeSubActivityTitle(this, "Progress Reminder", R.drawable.ic_title_progress);

        LinearLayout layout = (LinearLayout) findViewById(R.id.reminder_bar_container);

        boolean scale = false;
        float scaleVlaue = 0;
        if (Values.DisplayDensity < 2) {
            Log.d(className, "do scale");
            scale = true;
            scaleVlaue = Values.DisplayDensity / 2.2f;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(630, 300);
            layout.setLayoutParams(params);
        }

        progressReminder = (ProgressReminder) MySharedPreferences.getInstance(null)
                .getPreferenceItem(MySharedPreferences.STOP_REMINDER, ProgressReminder.class);

        ViewReminderSetter c = new ViewReminderSetter(this, progressReminder, scale, scaleVlaue);
        layout.addView(c);
        c.setReminderSwitchCallback(new InterfaceReminderSwitchCallback() {
            public void switchOn() {
                sw.setChecked(true);
                progressReminder.setTurnedOn(true);
            }

            public void switchOff() {
                sw.setChecked(false);
                progressReminder.setTurnedOn(false);
            }
        });

        sw = (Switch) findViewById(R.id.stop_reminder_switch);
        sw.setOnCheckedChangeListener(this);

        if (progressReminder.isTurnedOn()) {
            sw.setChecked(true);
        } else {
            sw.setChecked(false);
        }
    }

    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            progressReminder.setTurnedOn(true);
            Speak.getInstance(null).speak("progress reminder is on");
        } else {
            progressReminder.setTurnedOn(false);
            Speak.getInstance(null).speak("progress reminder is off");
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        MySharedPreferences.getInstance(null).setPreferenceItem(MySharedPreferences.STOP_REMINDER, progressReminder);
    }
}
