package han.com.fragment.goal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import han.com.R;
import han.com.activity.track.goals.ActivityAddGoal;
import han.com.datapool.CurrentGoal;
import han.com.db.DatabaseHandler;
import han.com.db.UserGoal;
import han.com.tts.Speak;
import han.com.utils.MyWidgets;

/**
 *
 * @author hanaldo
 */
public class ActivityUpdateGoal extends Activity implements AdapterView.OnItemClickListener {

    private static final String className = ActivityUpdateGoal.class.getName();
    public static ActivityUpdateGoal instance;

    private EditText goalName;
    private RadioGroup goalType, goalFrequency;
    private ImageView goalIcon;
    private int chosenGoalIcon;
    private Button saveButton, cancelButton;
    private String mode;
    private DialogGoalIcon dialog;
    private RadioButton typeOther;
    private RadioButton typeFood;
    private RadioButton freqOnce;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        instance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_goal);

        MyWidgets.makeSubActivityTitle(this, "Make an Non-Tracking Goal", R.drawable.ic_title_goal);

        String goalID = getIntent().getExtras().getString("goal.id");
        if (goalID.isEmpty()) {
            mode = "new";
        } else {
            mode = "update";
        }

        goalName = (EditText) findViewById(R.id.act_update_goal_edit1);
        goalType = (RadioGroup) findViewById(R.id.act_update_goal_radio_group1);
        goalFrequency = (RadioGroup) findViewById(R.id.act_update_goal_radio_group2);
        goalIcon = (ImageView) findViewById(R.id.act_update_goal_image1);
        saveButton = (Button) findViewById(R.id.act_update_goal_button1);
        cancelButton = (Button) findViewById(R.id.act_update_goal_button2);
        typeFood = (RadioButton) findViewById(R.id.act_update_goal_radio1);
        typeOther = (RadioButton) findViewById(R.id.act_update_goal_radio3);
        freqOnce = (RadioButton) findViewById(R.id.act_update_goal_radio4);
        chosenGoalIcon = 0;

        final long[] oldGoalId = new long[1];
        if (mode.equals("update")) {
            UserGoal goal = UserGoal.loadUserGoal(DatabaseHandler.getInstance(null).getReadableDatabase(), Integer.parseInt(goalID));
            goalName.setText(goal.getGoalName());
            goalIcon.setImageResource(goal.getIcon());
            if (goal.getGoalType().equals(UserGoal.GOAL_TYPE_FOOD)) {
                typeFood.setChecked(true);
            } else if (goal.getGoalType().equals(UserGoal.GOAL_TYPE_OTHER)) {
                typeOther.setChecked(true);
            }
            oldGoalId[0] = goal.getGoalId();
        }

        goalIcon.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                dialog = new DialogGoalIcon(ActivityUpdateGoal.this, "Choose an icon for your goal");
                dialog.setDialogOnItemClickListener(ActivityUpdateGoal.this);
                dialog.show();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                String name = goalName.getText().toString().trim();
                if (name.isEmpty()) {
                    Toast.makeText(ActivityUpdateGoal.this, "Please enter your goal name", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (chosenGoalIcon == 0) {
                    Toast.makeText(ActivityUpdateGoal.this, "Please choose a goal icon", Toast.LENGTH_SHORT).show();
                    return;
                }

                UserGoal g = new UserGoal();
                switch (goalType.getCheckedRadioButtonId()) {
                    case R.id.act_update_goal_radio1:
                        g.setGoalType(UserGoal.GOAL_TYPE_FOOD);
                        g.setValid(UserGoal.GOAL_IS_NON_TRACKING);
                        break;
                    case R.id.act_update_goal_radio3:
                        g.setGoalType(UserGoal.GOAL_TYPE_OTHER);
                        g.setValid(UserGoal.GOAL_IS_NON_TRACKING);
                        break;
                    default:
                        g.setValid(UserGoal.GOAL_IS_NON_TRACKING);
                        Log.w(className, "goalType is not set");
                        break;
                }

                g.setValid(UserGoal.GOAL_IS_NON_TRACKING);
                g.setGoalName(name);
                g.setGoalOrder(-1);
                g.setIcon(chosenGoalIcon);

                switch (goalFrequency.getCheckedRadioButtonId()) {
                    case R.id.act_update_goal_radio4:
                        g.setFrequency(UserGoal.GOAL_FREQUENCY_ONCE);
                        break;
                    case R.id.act_update_goal_radio5:
                        g.setFrequency(UserGoal.GOAL_FREQUENCY_DAY);
                        break;
                    case R.id.act_update_goal_radio6:
                        g.setFrequency(UserGoal.GOAL_FREQUENCY_WEEK);
                        break;
                    case R.id.act_update_goal_radio7:
                        g.setFrequency(UserGoal.GOAL_FREQUENCY_MONTH);
                        break;
                    default:
                        g.setFrequency(UserGoal.GOAL_FREQUENCY_ONCE);
                        Log.w(className, "goalFrequency is not set");
                        break;
                }

                if (mode.equals("update")) {
                    UserGoal.deleteUserGoal(DatabaseHandler.getInstance(null).getWritableDatabase(), oldGoalId[0]);
                    UserGoal.addNewUserGoal(DatabaseHandler.getInstance(null).getWritableDatabase(), g);
                    Toast.makeText(ActivityUpdateGoal.this, "New goal is updated", Toast.LENGTH_SHORT).show();
                } else {
                    UserGoal.addNewUserGoal(DatabaseHandler.getInstance(null).getWritableDatabase(), g);
                    Toast.makeText(ActivityUpdateGoal.this, "New goal is added", Toast.LENGTH_SHORT).show();
                }

                FragmentGoalList.getReloadListHandler().sendEmptyMessage(0);
                ActivityUpdateGoal.this.finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                ActivityUpdateGoal.this.finish();
            }
        });

        RadioButton fitness = (RadioButton) findViewById(R.id.act_update_goal_radio2);
        fitness.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                if (CurrentGoal.getGoalRecord() != null) {
                    String s = "Please stop your current goal first";
                    Toast.makeText(ActivityUpdateGoal.this, s, Toast.LENGTH_SHORT).show();
                    Speak.getInstance(null).speak(s);
                    return;
                }
                Intent i = new Intent(ActivityUpdateGoal.this, ActivityAddGoal.class);
                startActivity(i);
            }
        });
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        chosenGoalIcon = (Integer) DialogGoalIcon.GoalIcons[position][0];
        goalIcon.setImageResource(chosenGoalIcon);
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        typeOther.setChecked(true);
        freqOnce.setChecked(true);
    }
}
