package han.com.activity.track.goals;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import han.com.datapool.CurrentGoal;
import han.com.db.DatabaseHandler;
import han.com.db.UserGoal;
import han.com.dialog.workout.DialogWorkoutNames;
import han.com.dialog.workout.InterfaceSelectWorkout;
import han.com.utils.Messages;
import han.com.utils.Values;

/**
 *
 * @author hanaldo
 */
public class GoalHelper {

    private Activity activity;
    private TextView workoutTextView, saveButton;
    private ImageView sportTypeImageView;
    private String goalName;
    private int goalOrder;
    private boolean goalAlreadySaved;
    private UserGoal goalData;

    public GoalHelper(Activity activity, TextView workoutTextView, TextView saveButton, ImageView sportTypeImageView) {
        this.activity = activity;
        this.workoutTextView = workoutTextView;
        this.saveButton = saveButton;
        this.sportTypeImageView = sportTypeImageView;
    }

    public void useCurrentGoalData() {
        CurrentGoal.setGoalData(goalData);
        CurrentGoal.setGoalRecord(null);
        CurrentGoal.clearCurrentTrackingInfo();
        Toast.makeText(activity, "Goal is set", Toast.LENGTH_SHORT).show();
    }

    public void resetGoalData() {
        if (!goalAlreadySaved) {
            return;
        }

        goalAlreadySaved = false;
        saveButton.setClickable(true);
        saveButton.setFocusable(true);
        saveButton.setTextColor(Color.parseColor("#ffffff"));
        updateWorkoutDisplay();
    }

    public void setGoalData(UserGoal goalData) {
        this.goalData = goalData;

        goalAlreadySaved = true;
        saveButton.setClickable(false);
        saveButton.setFocusable(false);
        saveButton.setTextColor(Color.DKGRAY);

        Toast.makeText(activity, "Goal \"" + goalName + " " + goalOrder + "\" is saved", Toast.LENGTH_SHORT).show();
    }

    public boolean isGoalAlreadySaved() {
        return goalAlreadySaved;
    }

    public String getGoalName() {
        return goalName;
    }

    public int getGoalOrder() {
        return goalOrder;
    }

    public void updateWorkoutDisplay() {
        if (true) {
            return;//disable the default workout_name reading function
        }
        if (DialogWorkoutNames.lastWorkout != null) {
            Log.d(activity.getClass().getName(), "read last workout");
            goalName = DialogWorkoutNames.lastWorkout;
            int selectedGoalOrder = UserGoal.getGoalNameNextOrder(DatabaseHandler.getInstance(null).getReadableDatabase(), goalName);
            goalOrder = selectedGoalOrder;
            workoutTextView.setText(goalName + " " + goalOrder);
        }
    }

    public void chooseWorkout() {
        DialogWorkoutNames dialog = new DialogWorkoutNames(activity, "Choose Workout");
        dialog.setCallback(new InterfaceSelectWorkout() {
            public void workoutIsSelected(String text, int icon, String selectedGoalName, int selectedGoalOrder) {
                workoutTextView.setText(text);
                goalName = selectedGoalName;
                goalOrder = selectedGoalOrder;
                sportTypeImageView.setImageResource(icon);
            }
        });
        dialog.show();
    }

    public boolean isNameReady(boolean showToast) {
        if (workoutTextView.getText().length() == 0) {
            if (showToast) {
                Toast.makeText(activity, Messages.ERROR_NO_WORKOUT, Toast.LENGTH_SHORT).show();
            }
            return false;
        }
        return true;
    }

    public void finishPreviousActivity() {
        if (activity.getParent() == null) {
            activity.setResult(Values.RESULT_CODE_FINISH);
        } else {
            activity.getParent().setResult(Values.RESULT_CODE_FINISH);
        }
    }
}
