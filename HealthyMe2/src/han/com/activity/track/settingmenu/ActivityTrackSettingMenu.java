package han.com.activity.track.settingmenu;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.Toast;
import han.com.R;
import han.com.activity.track.history.ActivityOverallHistory;
import han.com.activity.track.reminder.ActivityReminderSetting;
import han.com.activity.track.setting.dialogs.DialogTrackModuleSelection;
import han.com.activity.track.setting.dialogs.DialogTrackPedometerPosition;
import han.com.datapool.CurrentGoal;
import han.com.ui.common.ListAdapterTrackSettingItem;
import han.com.utils.MyWidgets;
import han.com.utils.Values;
import java.util.ArrayList;

/**
 *
 * @author han
 */
public class ActivityTrackSettingMenu extends ListActivity {

    private static final String className = ActivityTrackSettingMenu.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_setting_activity);

        MyWidgets.makeSubActivityTitle(this, "Track Settings", R.drawable.ic_title_setting);

        ArrayList<Object[]> items = new ArrayList<Object[]>(8);

        items.add(new Object[]{"Progress Reminder",
            R.drawable.ic_list_stop,
            ListAdapterTrackSettingItem.LIST_ITEM_CONTENT,
            "Voice reminder when a track starts, ends and midway progress reminder"});
        items.add(new Object[]{null, 0, ListAdapterTrackSettingItem.LIST_ITEM_SEPARATOR});

        items.add(new Object[]{"Tracking Modes",
            R.drawable.ic_setting_mode,
            ListAdapterTrackSettingItem.LIST_ITEM_CONTENT});
        items.add(new Object[]{null, 0, ListAdapterTrackSettingItem.LIST_ITEM_SEPARATOR});

        items.add(new Object[]{"Pedometer Position",
            R.drawable.ic_setting_position,
            ListAdapterTrackSettingItem.LIST_ITEM_CONTENT});
        items.add(new Object[]{null, 0, ListAdapterTrackSettingItem.LIST_ITEM_SEPARATOR});

        items.add(new Object[]{"Statistics",
            R.drawable.ic_setting_stat,
            ListAdapterTrackSettingItem.LIST_ITEM_CONTENT,
            "Statistics of overall tracking activities, distance, time, steps and calories"});

        ListAdapterTrackSettingItem listA = new ListAdapterTrackSettingItem(this, R.layout.list_setting_item, items);

        setListAdapter(listA);

        getListView().setDivider(null);
        getListView().setDividerHeight(0);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 0) {
            Intent i = new Intent(this, ActivityReminderSetting.class);
            startActivity(i);
        } else if (position == 2) {
            if (CurrentGoal.isTrackIsStarted()) {
                Toast.makeText(this, "Please stop your current tracking first", Toast.LENGTH_SHORT).show();
                return;
            }
            DialogTrackModuleSelection dialog = new DialogTrackModuleSelection(ActivityTrackSettingMenu.this, "Choose Tracking Mode");
            dialog.show();
        } else if (position == 4) {
            DialogTrackPedometerPosition dialog = new DialogTrackPedometerPosition(ActivityTrackSettingMenu.this, "Choose Position");
            dialog.show();
        } else if (position == 6) {
            Intent i = new Intent(this, ActivityOverallHistory.class);
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(className, "onActivityResult: " + requestCode + " " + resultCode);
        if (requestCode == Values.REQUEST_CODE && resultCode == Values.RESULT_CODE_FINISH) {
            finish();
        }
    }
}
