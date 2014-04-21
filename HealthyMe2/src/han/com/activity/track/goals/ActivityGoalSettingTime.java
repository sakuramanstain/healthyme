package han.com.activity.track.goals;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
public class ActivityGoalSettingTime extends Activity implements InterfaceClockSetterValue, AdapterView.OnItemClickListener {
    
    private static final String className = ActivityGoalSettingTime.class.getName();
    private TextView clockValueTextView;
    private float timeValue;
    private GoalHelper goalHelper;
    private DialogGoalFrequency dialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_goal_setting);
        Log.d(className, "onCreate");
        
        MyWidgets.makeSubActivityTitle(this, "Time Goal", R.drawable.ic_list_time);
        
        goalHelper = new GoalHelper(this,
                (TextView) findViewById(R.id.button_back_workout_name),
                (TextView) findViewById(R.id.button_save_new_goal),
                (ImageView) findViewById(R.id.button_sport_type_image));
        
        LinearLayout layout = (LinearLayout) findViewById(R.id.clock_container);
        LayoutParams params = layout.getLayoutParams();
        int height = params.height;
        int width = params.width;
        
        boolean scale = false;
        float scaleVlaue = 0;
        if (Values.DisplayDensity < 2) {
            scale = true;
            scaleVlaue = Values.DisplayDensity / 2.5f;
            height *= Values.DisplayDensity / 1.8f;
            width *= Values.DisplayDensity / 1.8f;
        }
        
        params.height = height;
        params.width = width;
        
        ViewClockSetter c = new ViewClockSetter(this, 600, 600, 50, scale, scaleVlaue);
        
        layout.addView(c);
        
        clockValueTextView = (TextView) findViewById(R.id.act_time_set_mins_text);
        clockValueTextView.setText("0 min");
        
        c.setValueListener(this);
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
    
    public void newValue(float degree) {
        float mins = degree / (360 / 60);
        clockValueTextView.setText((int) mins + " min");
        timeValue = (int) mins;
        goalHelper.resetGoalData();
    }
    
    public void chooseWorkout(View view) {
        goalHelper.chooseWorkout();
    }
    
    public void saveGoal(View view) {
        if (!goalHelper.isNameReady(true)) {
            return;
        }
        
        if (timeValue <= 0) {
            Toast.makeText(this, Messages.ERROR_NO_TIME_VALUE, Toast.LENGTH_SHORT).show();
            return;
        }
        
        dialog = new DialogGoalFrequency(this, "And this goal is for");
        dialog.setDialogOnItemClickListener(this);
        dialog.show();
    }
    
    private void doSaveGoal(String freq) {
        UserGoal newGoal = CurrentGoal.makeGoal(timeValue, UserGoal.GOAL_IS_VALID, goalHelper.getGoalName(), goalHelper.getGoalOrder(), "min", 0, UserGoal.GOAL_TYPE_TIME, freq);
        goalHelper.setGoalData(newGoal);
    }
    
    public void useGoal(View view) {
        if (!goalHelper.isNameReady(true)) {
            return;
        }
        
        if (!goalHelper.isGoalAlreadySaved()) {
            if (timeValue <= 0) {
                Toast.makeText(this, Messages.ERROR_NO_TIME_VALUE, Toast.LENGTH_SHORT).show();
                return;
            }
            
            UserGoal newGoal = CurrentGoal.makeGoal(timeValue, UserGoal.GOAL_IS_NOT_VALID, goalHelper.getGoalName(), goalHelper.getGoalOrder(), "min", 0, UserGoal.GOAL_TYPE_TIME, null);
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
