package han.com.activity.track.goals;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
public class ActivityGoalSettingStep extends Activity {

    private static final String className = ActivityGoalSettingDistance.class.getName();
    public static final int DIGIT_TYPE_1 = 1;
    public static final int DIGIT_TYPE_2 = 2;
    public static final int DIGIT_TYPE_3 = 3;
    public static final int DIGIT_TYPE_4 = 4;
    private TextView stepTextView;
    private int digit1value = 0;
    private int digit2value = 0;
    private int digit3value = 0;
    private int digit4value = 0;
    private GoalHelper goalHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_goal_setting);
        Log.d(className, "onCreate");

        MyWidgets.makeSubActivityTitle(this, "Step Goal", R.drawable.ic_setting_step);

        goalHelper = new GoalHelper(this,
                (TextView) findViewById(R.id.button_back_workout_name),
                (TextView) findViewById(R.id.button_save_new_goal),
                (ImageView) findViewById(R.id.button_sport_type_image));

        stepTextView = (TextView) findViewById(R.id.act_step_set_step_text);
        stepTextView.setText("0 steps");

        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.act_step_number_picker1);
        numberPicker.setEnabled(true);
        numberPicker.setMaxValue(9);
        numberPicker.setMinValue(0);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setOnValueChangedListener(new ListenerGoalValueChangeStep(this, DIGIT_TYPE_1));
        numberPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        NumberPicker numberPicker2 = (NumberPicker) findViewById(R.id.act_step_number_picker2);
        numberPicker2.setEnabled(true);
        numberPicker2.setMaxValue(9);
        numberPicker2.setMinValue(0);
        numberPicker2.setWrapSelectorWheel(true);
        numberPicker2.setOnValueChangedListener(new ListenerGoalValueChangeStep(this, DIGIT_TYPE_2));
        numberPicker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        NumberPicker numberPicker3 = (NumberPicker) findViewById(R.id.act_step_number_picker3);
        numberPicker3.setEnabled(true);
        numberPicker3.setMaxValue(9);
        numberPicker3.setMinValue(0);
        numberPicker3.setWrapSelectorWheel(true);
        numberPicker3.setOnValueChangedListener(new ListenerGoalValueChangeStep(this, DIGIT_TYPE_3));
        numberPicker3.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

        NumberPicker numberPicker4 = (NumberPicker) findViewById(R.id.act_step_number_picker4);
        numberPicker4.setEnabled(true);
        numberPicker4.setMaxValue(9);
        numberPicker4.setMinValue(0);
        numberPicker4.setWrapSelectorWheel(true);
        numberPicker4.setOnValueChangedListener(new ListenerGoalValueChangeStep(this, DIGIT_TYPE_4));
        numberPicker4.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

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
        } else if (digitType == DIGIT_TYPE_4) {
            digit4value = newDigitValue;
        }

        stepTextView.setText(Values.getStepValue(digit1value, digit2value, digit3value, digit4value) + " steps");
        goalHelper.resetGoalData();
    }

    public void chooseWorkout(View view) {
        goalHelper.chooseWorkout();
    }

    public void saveGoal(View view) {
        if (!goalHelper.isNameReady(true)) {
            return;
        }

        int stepValue = Values.getStepValue(digit1value, digit2value, digit3value, digit4value);
        if (stepValue == 0) {
            Toast.makeText(this, Messages.ERROR_NO_GOAL_VALUE, Toast.LENGTH_SHORT).show();
            return;
        }

        UserGoal newGoal = CurrentGoal.makeGoal(stepValue, UserGoal.GOAL_IS_VALID, goalHelper.getGoalName(), goalHelper.getGoalOrder(), "steps", 0, UserGoal.GOAL_TYPE_STEP);
        goalHelper.setGoalData(newGoal);
    }

    public void useGoal(View view) {
        if (!goalHelper.isNameReady(true)) {
            return;
        }

        if (!goalHelper.isGoalAlreadySaved()) {
            int stepValue = Values.getStepValue(digit1value, digit2value, digit3value, digit4value);
            if (stepValue == 0) {
                Toast.makeText(this, Messages.ERROR_NO_GOAL_VALUE, Toast.LENGTH_SHORT).show();
                return;
            }

            UserGoal newGoal = CurrentGoal.makeGoal(stepValue, UserGoal.GOAL_IS_NOT_VALID, goalHelper.getGoalName(), goalHelper.getGoalOrder(), "steps", 0, UserGoal.GOAL_TYPE_STEP);
            goalHelper.setGoalData(newGoal);
        }
        goalHelper.useCurrentGoalData();
        goalHelper.finishPreviousActivity();
        finish();
    }
}
