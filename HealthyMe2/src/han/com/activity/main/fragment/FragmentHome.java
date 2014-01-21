package han.com.activity.main.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import han.com.R;
import han.com.UserInfo;
import han.com.UserRecord;
import han.com.datapool.MySharedPreferences;
import han.com.db.DatabaseHandler;
import han.com.db.GoalRecord;
import han.com.db.UserGoal;
import han.com.pedometer.Pedometer;
import han.com.resources.ResourceGetter;
import han.com.tts.Speak;
import han.com.utils.HttpUtil;
import han.com.utils.MyWidgets;
import han.com.utils.Values;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.http.HttpStatus;

/**
 *
 * @author han
 */
public class FragmentHome extends Fragment {

    private static final String className = FragmentHome.class.getName();
    private static Handler loadTopValuesHandler;
    public static String DeviceToken;

    public static Handler getLoadTopValuesHandler() {
        return loadTopValuesHandler;
    }
    private TextView totalFinishedGoals, totalCancelledGoals, totalDistance, totalTime, totalSteps, totalCalories, lastActivity;
    private String tFinished, tCancelled, tDistance, tTime, tSteps, tCalories, lActivity;
    private Handler toastSyncHandler;
    private Button sync;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(className, "onCreate");

        loadTopValuesHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    doLoadTopValues();
                }
            }
        };
        loadDatabaseValues();

        toastSyncHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String content = (String) msg.obj;
                Toast.makeText(getActivity(), content, Toast.LENGTH_LONG).show();
                if (msg.what == 1) {
                    return;
                }
                long lastSync = System.currentTimeMillis();
                sync.setText(DateFormatUtils.format(lastSync, "yyyy-MM-dd HH:mm:ss"));
                MySharedPreferences.getInstance(null).setLastSync(lastSync);
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(className, "onDestroy");
        loadTopValuesHandler = null;
    }

    private void doLoadTopValues() {
        loadDatabaseValues();
        displayTopValues();
    }

    private void loadDatabaseValues() {
        tFinished = String.valueOf(GoalRecord.getTotalNumberGoalsInCompleteType(
                DatabaseHandler.getInstance(null).getReadableDatabase(), GoalRecord.COMPLETE_SUCCESS));

        tCancelled = String.valueOf(GoalRecord.getTotalNumberGoalsInCompleteType(
                DatabaseHandler.getInstance(null).getReadableDatabase(), GoalRecord.COMPLETE_FAIL));

        tDistance = String.valueOf(GoalRecord.getTotalCompleteGoalValue(DatabaseHandler.getInstance(null).getReadableDatabase(), UserGoal.GOAL_TYPE_DISTANCE));
        tDistance += " km";

        String[] tokens = DurationFormatUtils.formatDuration(
                GoalRecord.getTotalCompleteGoalTime(DatabaseHandler.getInstance(null).getReadableDatabase()), "HH:mm:ss").split(":");
        tTime = tokens[0] + "h " + tokens[1] + "m " + tokens[2] + "s";

        tSteps = String.valueOf(GoalRecord.getTotalCompleteGoalSteps(DatabaseHandler.getInstance(null).getReadableDatabase()));

        tCalories = Values.formatCalories(GoalRecord.getTotalCompleteGoalCalories(DatabaseHandler.getInstance(null).getReadableDatabase()));

        tCalories += " Cal";

        long time = GoalRecord.getLastActivity(DatabaseHandler.getInstance(null).getReadableDatabase());
        lActivity = DateFormatUtils.format(time, "yyyy-MM-dd HH:mm:ss");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(className, "onCreateView");

//        Context context = new ContextThemeWrapper(getActivity(), R.style.HealthyMeStyle);
//        LayoutInflater localInflater = inflater.cloneInContext(context);

        View myFragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        MyWidgets.makeFragmentTitle(myFragmentView, "Home");

        LinearLayout settingAll = (LinearLayout) myFragmentView.findViewById(R.id.fragment_setting_item_1);
        settingAll.setVisibility(View.VISIBLE);
        settingAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), Pedometer.class);
                startActivity(i);
            }
        });

        myFragmentView.findViewById(R.id.setting_item_music).setVisibility(View.GONE);
        myFragmentView.findViewById(R.id.setting_item_stat).setVisibility(View.GONE);

        final EditText text = (EditText) myFragmentView.findViewById(R.id.speak_text);
        text.setVisibility(View.GONE);

        Button test2 = (Button) myFragmentView.findViewById(R.id.test_main_button2);
        test2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //SingletonListenerStep.getInstance().switchSpeakStep();
                //Toast.makeText(getActivity(), "speak step: " + SingletonListenerStep.getInstance().isSpeakStep(), Toast.LENGTH_SHORT).show();

                String s = text.getText().toString();
                Speak.getInstance(null).speak(s);

            }
        });
        test2.setVisibility(View.GONE);


        Button test3 = (Button) myFragmentView.findViewById(R.id.test_main_button3);
        test3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int SysBackLightValue = (int) (0.2 * 255);
                Settings.System.putInt(getActivity().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, SysBackLightValue);
            }
        });
        test3.setVisibility(View.GONE);

        sync = (Button) myFragmentView.findViewById(R.id.frag_home_sync);
        long lastSync = MySharedPreferences.getInstance(null).getLastSync();
        if (lastSync > 0) {
            sync.setText(DateFormatUtils.format(lastSync, "yyyy-MM-dd HH:mm:ss"));
        }
        sync.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... arg0) {
                        try {
                            registerDevice();
                            Message message = Message.obtain();
                            message.what = 0;
                            message.obj = "Sync success!";
                            toastSyncHandler.sendMessage(message);
                            Speak.getInstance(null).speak("hello there, " + MySharedPreferences.getInstance(null).getUserName());
                            Speak.getInstance(null).speak("sync is successful");
                        } catch (Exception ex) {
                            Log.e(className, ex.toString());
                            Message message = Message.obtain();
                            message.what = 1;
                            message.obj = "Sync fail! " + ex.toString();
                            toastSyncHandler.sendMessage(message);
                            Speak.getInstance(null).speak("hello there, " + MySharedPreferences.getInstance(null).getUserName());
                            Speak.getInstance(null).speak("sorry, the phone cannot sync now, check your network connection please");
                        }
                        return null;
                    }
                }.execute();
            }
        });

        totalFinishedGoals = (TextView) myFragmentView.findViewById(R.id.fragment_home_finished_goals);
        totalCancelledGoals = (TextView) myFragmentView.findViewById(R.id.fragment_home_cancelled_goals);
        totalDistance = (TextView) myFragmentView.findViewById(R.id.fragment_home_total_distance);
        totalTime = (TextView) myFragmentView.findViewById(R.id.fragment_home_total_time);
        totalSteps = (TextView) myFragmentView.findViewById(R.id.fragment_home_total_steps);
        totalCalories = (TextView) myFragmentView.findViewById(R.id.fragment_home_total_calories);
        lastActivity = (TextView) myFragmentView.findViewById(R.id.fragment_home_last_activity);

        displayTopValues();

        return myFragmentView;
    }

    private void displayTopValues() {
        totalFinishedGoals.setText(tFinished);
        totalCancelledGoals.setText(tCancelled);
        totalDistance.setText(tDistance);
        totalTime.setText(tTime);
        totalSteps.setText(tSteps);
        totalCalories.setText(tCalories);
        lastActivity.setText(lActivity);
    }

    private void registerDevice() throws Exception {
        if (DeviceToken == null) {
            throw new Exception("No DeviceToken");
        }
        String userName = MySharedPreferences.getInstance(null).getUserName();
        if (userName.isEmpty()) {
            throw new Exception("No UserName");
        }
        Gson gson = new Gson();
        String userInfoJSON = gson.toJson(new UserInfo(userName, DeviceToken, ""));

        Object[] response = HttpUtil.httpPostData(ResourceGetter.getInstance(null).getServiceAddressRegisterDevice(), userInfoJSON);
        if ((Integer) response[0] != HttpStatus.SC_OK) {
            throw new Exception("http error " + response[0]);
        }

        String recordJSON = gson.toJson(new UserRecord(
                userName,
                GoalRecord.getTotalCompleteGoalValue(DatabaseHandler.getInstance(null).getReadableDatabase(), UserGoal.GOAL_TYPE_DISTANCE),
                GoalRecord.getTotalCompleteGoalSteps(DatabaseHandler.getInstance(null).getReadableDatabase()),
                GoalRecord.getTotalCompleteGoalTime(DatabaseHandler.getInstance(null).getReadableDatabase()) / 1000,
                GoalRecord.getTotalCompleteGoalCalories(DatabaseHandler.getInstance(null).getReadableDatabase())));

        Object[] response2 = HttpUtil.httpPostData(ResourceGetter.getInstance(null).getServiceAddressSyncRecord(), recordJSON);
        if ((Integer) response2[0] != HttpStatus.SC_OK) {
            throw new Exception("http error " + response[0]);
        }
    }
}
