package han.com.activity.reward.remind;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import han.com.R;
import han.com.tts.Speak;
import org.apache.commons.lang3.text.WordUtils;

/**
 *
 * @author hanaldo
 */
public class DialogReportReward extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_reward);

        String rewardName = getIntent().getExtras().getString("dialog_reward_name");
        String rewardContent = getIntent().getExtras().getString("dialog_reward_content");

        ((TextView) findViewById(R.id.dialog_reward_name)).setText(WordUtils.capitalize(rewardName));
        ((TextView) findViewById(R.id.dialog_reward_content)).setText(rewardContent);

        findViewById(R.id.dialog_reward_container).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                finish();
            }
        });

        Speak.getInstance(null).speak(rewardContent);
    }
}
