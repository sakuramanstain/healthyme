package han.com.utils;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import han.com.R;

/**
 *
 * @author han
 */
public class MyWidgets {

    private static final String className = MyWidgets.class.getName();
    public static final int NOTIFICATION_ID_NEW_SETTING = 2;
    public static final int NOTIFICATION_ID_NEW_GOAL = 3;
    public static final int NOTIFICATION_ID_GOAL_PROGRESS = 4;
    public static final int NOTIFICATION_ID_WEEK_PROGRESS = 5;
    private static Context context;
    private static NotificationManager notificationManager;

    public static void initContext(Context context) {
        MyWidgets.context = context;
        MyWidgets.notificationManager = (NotificationManager) MyWidgets.context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static void releaseContext() {
        notificationManager.cancelAll();
        notificationManager = null;
        context = null;
    }

    public static void showToast(Activity activity, String message) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast, (ViewGroup) activity.findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.toast_text);
        text.setText(message);

        Toast toast = new Toast(activity);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    public static void makeSubActivityTitle(Activity activity, String titleText, int titleImage) {
        ((TextView) activity.findViewById(R.id.title_sub_act_text)).setText(titleText);
        ((ImageView) activity.findViewById(R.id.title_sub_act_image)).setImageResource(titleImage);
    }

    public static void makeFragmentTitle(View topView, String titleText) {
        ((TextView) topView.findViewById(R.id.title_fragment_text)).setText(titleText);
    }

    public static void showNotification(int icon, int largeIcon, String ticker, String title, String content, String info, int id, boolean ongoing) {
        Notification.Builder notificationBuilder = new Notification.Builder(context);
        notificationBuilder.setSmallIcon(icon)
                .setTicker(ticker)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(title)
                .setContentText(content)
                .setContentInfo(info)
                .setOngoing(ongoing);

        if (largeIcon > -1) {
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon));
        }

        Notification notification = notificationBuilder.build();
        notification.defaults = Notification.DEFAULT_ALL;
        notificationManager.notify(id, notification);
    }

    public static void cancelNotification(int id) {
        notificationManager.cancel(id);
    }

    private MyWidgets() {
    }
}
