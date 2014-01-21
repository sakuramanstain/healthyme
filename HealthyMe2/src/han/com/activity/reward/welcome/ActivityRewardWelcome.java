package han.com.activity.reward.welcome;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import han.com.R;

/**
 *
 * @author hanaldo
 */
public class ActivityRewardWelcome extends Activity {

    @Override
    public void onCreate(Bundle icicle) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(icicle);
        setContentView(R.layout.activity_reward_welcome);

        LinearLayout top = (LinearLayout) findViewById(R.id.act_reward_top);
        top.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });
    }
}
