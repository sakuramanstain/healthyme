package han.com.activity.main.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import han.com.R;
import han.com.datapool.MySharedPreferences;
import han.com.tts.Speak;
import han.com.utils.MyWidgets;

/**
 *
 * @author hanaldo
 */
public class WelcomeActivity extends Activity {

    private EditText userNameText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_welcome_name);

        MyWidgets.makeSubActivityTitle(this, "Welcome", R.drawable.ic_launcher);

        userNameText = (EditText) findViewById(R.id.dialog_welcome_name);
    }

    public void clickOk(View view) {
        String userName = userNameText.getText().toString();
        if (userName.isEmpty()) {
            Toast.makeText(this, "Don't cheat please", Toast.LENGTH_SHORT).show();
            return;
        }
        MySharedPreferences.getInstance(null).setUserName(userName);
        Toast.makeText(this, "Hello " + userName, Toast.LENGTH_SHORT).show();
        Speak.getInstance(null).speak("hello, " + userName);
        finish();
    }
}
