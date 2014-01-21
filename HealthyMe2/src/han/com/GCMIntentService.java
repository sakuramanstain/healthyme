package han.com;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.gson.Gson;
import han.com.activity.main.activity.MainActivity;
import han.com.activity.main.fragment.FragmentHome;
import han.com.activity.main.fragment.FragmentReward;
import han.com.datapool.MyPedometerPreferences;
import han.com.datapool.MyPedometerValuePack;
import han.com.db.DatabaseHandler;
import han.com.db.UserGoal;
import han.com.utils.MyWidgets;
import org.apache.commons.lang3.time.DateFormatUtils;

public class GCMIntentService extends GCMBaseIntentService {

    private static final String className = GCMIntentService.class.getName();
    public static final String SENDER_ID = "910416930095";
    private Gson gson;

    public GCMIntentService() {
        super(SENDER_ID);
        gson = new Gson();
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.d(className, "Device registered: regId = " + registrationId);
        FragmentHome.DeviceToken = registrationId;
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.d(className, "Device unregistered");

    }

    protected void onReceive() {
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.d(className, "Received message");

        String content = intent.getExtras().getString("content");
        String contentType = intent.getExtras().getString("content_type");

        if (contentType.isEmpty()) {
            return;
        }

        Log.d(className, "Content: " + content);
        Log.d(className, "Content Type: " + contentType);

        if (contentType.equals("goal")) {
            UserGoal g = gson.fromJson(content, UserGoal.class);
            g.setGoalOrder(UserGoal.getGoalNameNextOrder(DatabaseHandler.getInstance(this).getReadableDatabase(), "Coach"));
            if (g.getGoalReceiveTime() <= 0) {
                Log.w(className, "get invalid GoalReceiveTime");
                return;
            }
            Log.d(className, "get a coach goal for: "
                    + g.getGoalReceiveTime()
                    + " "
                    + DateFormatUtils.SMTP_DATETIME_FORMAT.format(g.getGoalReceiveTime()));

            long gid = UserGoal.addNewUserGoal(DatabaseHandler.getInstance(this).getWritableDatabase(), g);
            Log.d(className, "added coach goal: " + gid);

            if (!MainActivity.isHasDestroy()) {
                if (FragmentReward.getLoadRewardsHandler() != null) {
                    FragmentReward.getLoadRewardsHandler().sendEmptyMessage(0);
                }
            }

            MyWidgets.showNotification(R.drawable.ic_notification_reward,
                    -1,
                    "New Goal from Coach",
                    "New Goal from Coach",
                    "You got a new Coach Goal",
                    "",
                    MyWidgets.NOTIFICATION_ID_NEW_GOAL,
                    false);

        } else if (contentType.equals("pedometer")) {
            MyPedometerValuePack pack = gson.fromJson(content, MyPedometerValuePack.class);
            MyPedometerPreferences.getInstance(null).setAll(pack);

            MyWidgets.showNotification(R.drawable.ic_notification_setting,
                    -1,
                    "Pedometer Setting from Coach",
                    "Pedometer Setting from Coach",
                    "Your pedometer setting is updated",
                    "",
                    MyWidgets.NOTIFICATION_ID_NEW_SETTING,
                    false);
        }
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.d(className, "Received deleted messages notification");
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.d(className, "Received error: " + errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        Log.d(className, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }
}
