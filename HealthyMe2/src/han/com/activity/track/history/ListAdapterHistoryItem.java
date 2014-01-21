package han.com.activity.track.history;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import han.com.R;
import han.com.UserInfo;
import han.com.UserRecord;
import static han.com.activity.main.fragment.FragmentHome.DeviceToken;
import han.com.datapool.MySharedPreferences;
import han.com.db.DatabaseHandler;
import han.com.db.GoalRecord;
import han.com.db.UserGoal;
import han.com.resources.ResourceGetter;
import han.com.tts.Speak;
import han.com.utils.HttpUtil;
import java.util.List;
import org.apache.http.HttpStatus;

/**
 *
 * @author han
 */
public class ListAdapterHistoryItem extends ArrayAdapter<String[]> {

    private static final String className = ListAdapterHistoryItem.class.getName();
    private List<String[]> itemContent;
    private Context context;
    private int listItemResourceId;
    private Handler toastSyncHandler;
    private boolean didAddButton;

    public ListAdapterHistoryItem(final Context context, int listItemResourceId, List<String[]> objects) {
        super(context, listItemResourceId, objects);
        this.context = context;
        this.listItemResourceId = listItemResourceId;
        itemContent = objects;

        toastSyncHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                String content = (String) msg.obj;
                Toast.makeText(context, content, Toast.LENGTH_LONG).show();
                if (msg.what == 1) {
                    return;
                }
                long lastSync = System.currentTimeMillis();
                MySharedPreferences.getInstance(null).setLastSync(lastSync);
            }
        };

        didAddButton = false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(listItemResourceId, null);
        }

        String[] item = itemContent.get(position);

        if (item != null) {
            if (item.length == 0) {
                if (didAddButton) {
                    return view;
                }
                Button b = new Button(context);
                b.setText("Upload Your Performance");
                b.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                ((ViewGroup) view).addView(b);
                didAddButton = true;

                b.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        new AsyncTask<Void, Void, Void>() {
                            @Override
                            protected Void doInBackground(Void... arg0) {
                                try {
                                    registerDevice();
                                    Message message = Message.obtain();
                                    message.what = 0;
                                    message.obj = "Uploading success!";
                                    toastSyncHandler.sendMessage(message);
                                    Speak.getInstance(null).speak("hello there, " + MySharedPreferences.getInstance(null).getUserName());
                                    Speak.getInstance(null).speak("uploading is successful");
                                } catch (Exception ex) {
                                    Log.e(className, ex.toString());
                                    Message message = Message.obtain();
                                    message.what = 1;
                                    message.obj = "Uploading fail! " + ex.toString();
                                    toastSyncHandler.sendMessage(message);
                                    Speak.getInstance(null).speak("hello there, " + MySharedPreferences.getInstance(null).getUserName());
                                    Speak.getInstance(null).speak("sorry, cannot upload now, check your network connection please");
                                }
                                return null;
                            }
                        }.execute();
                    }
                });

                TextView title = (TextView) view.findViewById(R.id.history_item_title);
                TextView value = (TextView) view.findViewById(R.id.history_item_value);
                title.setVisibility(View.GONE);
                value.setVisibility(View.GONE);
            } else {
                TextView title = (TextView) view.findViewById(R.id.history_item_title);
                TextView value = (TextView) view.findViewById(R.id.history_item_value);

                if (title != null) {
                    title.setText(item[0]);
                }

                if (value != null) {
                    value.setText(item[1]);
                }
            }
        }

        return view;
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
