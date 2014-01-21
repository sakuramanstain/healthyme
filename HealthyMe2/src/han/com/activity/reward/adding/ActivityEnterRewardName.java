package han.com.activity.reward.adding;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import han.com.R;
import han.com.activity.main.fragment.FragmentReward;
import han.com.db.DatabaseHandler;
import han.com.db.Reward;
import han.com.utils.MyWidgets;

/**
 *
 * @author hanaldo
 */
public class ActivityEnterRewardName extends Activity {

    public static final String EXTRA_NAME_GOAL_NUMBER = "activity.reward.goal.number";
    private long goalNumber;
    private EditText rewardNameText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_enter_name);

        MyWidgets.makeSubActivityTitle(this, "Reward Name", R.drawable.ic_title_reward);

        goalNumber = getIntent().getLongExtra(EXTRA_NAME_GOAL_NUMBER, -1);
        if (goalNumber <= 0) {
            Toast.makeText(this, "goal number is invalid", Toast.LENGTH_SHORT).show();
            finish();
        }

        rewardNameText = (EditText) findViewById(R.id.act_reward_name);
    }

    public void clickOk(View view) {
        String rewardName = rewardNameText.getText().toString();
        if (rewardName.isEmpty()) {
            Toast.makeText(this, "Please type your reward name", Toast.LENGTH_SHORT).show();
            return;
        }

        Reward.addReward(DatabaseHandler.getInstance(null).getWritableDatabase(), goalNumber, rewardName);

        if (FragmentReward.getLoadRewardsHandler() != null) {
            FragmentReward.getLoadRewardsHandler().sendEmptyMessage(0);
        }
        Toast.makeText(this, "new reward is added", Toast.LENGTH_SHORT).show();
        finish();
    }
}
