package han.com.activity.track.goals;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import han.com.R;
import han.com.datapool.CurrentGoal;
import han.com.db.UserGoal;
import han.com.utils.Messages;
import han.com.utils.MyWidgets;
import han.com.utils.Values;

/**
 *
 * @author han
 */
public class ActivityGoalSettingDistance extends Activity implements AdapterView.OnItemClickListener {

    private static final String className = ActivityGoalSettingDistance.class.getName();
    public static final int DIGIT_TYPE_1 = 1;
    public static final int DIGIT_TYPE_2 = 2;
    public static final int DIGIT_TYPE_3 = 3;
    private TextView distanceTextView;
    private int digit1value = 0;
    private int digit2value = 0;
    private int digit3value = 0;
    private GoalHelper goalHelper;
    private DialogGoalFrequency dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_distance_goal_setting);
        Log.d(className, "onCreate");

        MyWidgets.makeSubActivityTitle(this, "Distance Goal", R.drawable.ic_list_distance);

        goalHelper = new GoalHelper(this,
                (TextView) findViewById(R.id.button_back_workout_name),
                (TextView) findViewById(R.id.button_save_new_goal),
                (ImageView) findViewById(R.id.button_sport_type_image));

        distanceTextView = (TextView) findViewById(R.id.act_distance_set_distance_text);
        distanceTextView.setText("00.0 miles");

        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.act_distance_number_picker1);
        numberPicker.setEnabled(true);
        numberPicker.setMaxValue(9);
        numberPicker.setMinValue(0);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setOnValueChangedListener(new ListenerGoalValueChangeDistance(this, DIGIT_TYPE_1));
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        NumberPicker numberPicker2 = (NumberPicker) findViewById(R.id.act_distance_number_picker2);
        numberPicker2.setEnabled(true);
        numberPicker2.setMaxValue(9);
        numberPicker2.setMinValue(0);
        numberPicker2.setWrapSelectorWheel(true);
        numberPicker2.setOnValueChangedListener(new ListenerGoalValueChangeDistance(this, DIGIT_TYPE_2));
        numberPicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        NumberPicker numberPicker3 = (NumberPicker) findViewById(R.id.act_distance_number_picker3);
        numberPicker3.setEnabled(true);
        numberPicker3.setMaxValue(9);
        numberPicker3.setMinValue(0);
        numberPicker3.setWrapSelectorWheel(true);
        numberPicker3.setOnValueChangedListener(new ListenerGoalValueChangeDistance(this, DIGIT_TYPE_3));
        numberPicker3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        NumberPicker numberPicker4 = (NumberPicker) findViewById(R.id.act_distance_number_picker4);
        numberPicker4.setEnabled(true);
        numberPicker4.setMaxValue(0);
        numberPicker4.setMinValue(0);
        numberPicker4.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        numberPicker4.setDisplayedValues(new String[]{"."});
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(className, "onResume");

        goalHelper.updateWorkoutDisplay();
        if (!goalHelper.isNameReady(false)) {
            goalHelper.chooseWorkout();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(className, "onDestroy");
    }

    public void update(int digitType, int newDigitValue) {
        if (digitType == DIGIT_TYPE_1) {
            digit1value = newDigitValue;
        } else if (digitType == DIGIT_TYPE_2) {
            digit2value = newDigitValue;
        } else if (digitType == DIGIT_TYPE_3) {
            digit3value = newDigitValue;
        }

        distanceTextView.setText(Values.getDistanceValue(digit1value, digit2value, digit3value) + " miles");
        goalHelper.resetGoalData();
    }

    public void chooseWorkout(View view) {
        goalHelper.chooseWorkout();
    }

    public void saveGoal(View view) {
        if (!goalHelper.isNameReady(true)) {
            return;
        }

        dialog = new DialogGoalFrequency(this, "And this goal is for");
        dialog.setDialogOnItemClickListener(this);
        dialog.show();
    }

    private void doSaveGoal(String freq) {
        float disValue = Values.getDistanceValue(digit1value, digit2value, digit3value);
        if (disValue == 0) {
            Toast.makeText(this, Messages.ERROR_NO_GOAL_VALUE, Toast.LENGTH_SHORT).show();
            return;
        }

        UserGoal newGoal = CurrentGoal.makeGoal(disValue, UserGoal.GOAL_IS_VALID, goalHelper.getGoalName(), goalHelper.getGoalOrder(), Values.MetricMode, 0, UserGoal.GOAL_TYPE_DISTANCE, freq);
        goalHelper.setGoalData(newGoal);
    }

    public void useGoal(View view) {
        if (!goalHelper.isNameReady(true)) {
            return;
        }

        if (!goalHelper.isGoalAlreadySaved()) {
            float disValue = Values.getDistanceValue(digit1value, digit2value, digit3value);
            if (disValue == 0) {
                Toast.makeText(this, Messages.ERROR_NO_GOAL_VALUE, Toast.LENGTH_SHORT).show();
                return;
            }

            UserGoal newGoal = CurrentGoal.makeGoal(disValue, UserGoal.GOAL_IS_NOT_VALID, goalHelper.getGoalName(), goalHelper.getGoalOrder(), Values.MetricMode, 0, UserGoal.GOAL_TYPE_DISTANCE, null);
            goalHelper.setGoalData(newGoal);
        }
        goalHelper.useCurrentGoalData();
        goalHelper.finishPreviousActivity();
        finish();
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String freq = (String) DialogGoalFrequency.GoalFrequencies[position][1];
        doSaveGoal(freq);
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
