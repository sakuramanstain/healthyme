package han.com.fragment.goal;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import han.com.R;
import han.com.db.DatabaseHandler;
import han.com.db.UserGoal;
import han.com.utils.MyWidgets;

/**
 *
 * @author hanaldo
 */
public class ActivityUpdateGoal extends Activity implements AdapterView.OnItemClickListener {
    
    public static final Object[][] GoalIcons = new Object[][]{
        new Object[]{R.drawable.ic_goal_type_apple},
        new Object[]{R.drawable.ic_goal_type_beer},
        new Object[]{R.drawable.ic_goal_type_candy},
        new Object[]{R.drawable.ic_goal_type_carrott},
        new Object[]{R.drawable.ic_goal_type_chips},
        new Object[]{R.drawable.ic_goal_type_cigarette},
        new Object[]{R.drawable.ic_goal_type_coffee},
        new Object[]{R.drawable.ic_goal_type_dairy},
        new Object[]{R.drawable.ic_goal_type_grains},
        new Object[]{R.drawable.ic_goal_type_place},
        new Object[]{R.drawable.ic_goal_type_protein},
        new Object[]{R.drawable.ic_goal_type_salt},
        new Object[]{R.drawable.ic_goal_type_softdrink},
        new Object[]{R.drawable.ic_goal_type_television},
        new Object[]{R.drawable.ic_goal_type_water}
    };
    private EditText goalName;
    private RadioGroup goalType;
    private ImageView goalIcon;
    private int chosenGoalIcon;
    private Button saveButton, cancelButton;
    private String mode;
    private DialogGoalIcon dialog;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_goal);
        
        MyWidgets.makeSubActivityTitle(this, "Add an Non-Tracking Goal", R.drawable.ic_title_goal);
        
        String goalID = getIntent().getExtras().getString("goal.id");
        if (goalID.isEmpty()) {
            mode = "new";
            Toast.makeText(this, "new goal", Toast.LENGTH_SHORT).show();
        } else {
            mode = "update";
            Toast.makeText(this, "ok, let's update goal: " + goalID, Toast.LENGTH_SHORT).show();
        }
        
        goalName = (EditText) findViewById(R.id.act_update_goal_edit1);
        goalType = (RadioGroup) findViewById(R.id.act_update_goal_radio_group1);
        goalIcon = (ImageView) findViewById(R.id.act_update_goal_image1);
        saveButton = (Button) findViewById(R.id.act_update_goal_button1);
        cancelButton = (Button) findViewById(R.id.act_update_goal_button2);
        chosenGoalIcon = 0;
        
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
                
                String type = null;
                UserGoal g = new UserGoal();
                switch (goalType.getCheckedRadioButtonId()) {
                    case R.id.act_update_goal_radio1:
                        type = "food";
                        g.setValid(UserGoal.GOAL_IS_NON_TRACKING);
                        break;
                    case R.id.act_update_goal_radio2:
                        type = "fit";
                        g.setValid(UserGoal.GOAL_IS_VALID);
                        break;
                    case R.id.act_update_goal_radio3:
                        type = "other";
                        g.setValid(UserGoal.GOAL_IS_NON_TRACKING);
                        break;
                }
                
                g.setValid(UserGoal.GOAL_IS_NON_TRACKING);
                g.setGoalName(name);
                g.setGoalOrder(-1);
                g.setIcon(chosenGoalIcon);
                UserGoal.addNewUserGoal(DatabaseHandler.getInstance(null).getWritableDatabase(), g);
                Toast.makeText(ActivityUpdateGoal.this, "New goal is added", Toast.LENGTH_SHORT).show();
                ActivityUpdateGoal.this.finish();
            }
        });
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View arg0) {
                ActivityUpdateGoal.this.finish();
            }
        });
    }
    
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        chosenGoalIcon = (Integer) GoalIcons[position][0];
        goalIcon.setImageResource(chosenGoalIcon);
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
