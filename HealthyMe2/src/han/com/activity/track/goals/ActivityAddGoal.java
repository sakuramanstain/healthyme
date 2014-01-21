package han.com.activity.track.goals;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import han.com.R;
import han.com.ui.common.ListAdapterTrackSettingItem;
import han.com.utils.MyWidgets;
import han.com.utils.Values;
import java.util.ArrayList;

/**
 *
 * @author hanaldo
 */
public class ActivityAddGoal extends ListActivity {

    private static final String className = ActivityAddGoal.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_setting_activity);

        MyWidgets.makeSubActivityTitle(this, "Add Goal", R.drawable.ic_title_goal);

        ArrayList<Object[]> items = new ArrayList<Object[]>(8);

        items.add(new Object[]{"Select Goal from Coach", R.drawable.ic_setting_coach, ListAdapterTrackSettingItem.LIST_ITEM_CONTENT});
        items.add(new Object[]{null, 0, ListAdapterTrackSettingItem.LIST_ITEM_SEPARATOR});

        items.add(new Object[]{"Select My Goals", R.drawable.ic_setting_own, ListAdapterTrackSettingItem.LIST_ITEM_CONTENT});
        items.add(new Object[]{null, 0, ListAdapterTrackSettingItem.LIST_ITEM_SEPARATOR});

        items.add(new Object[]{"NEW GOAL", 0, ListAdapterTrackSettingItem.LIST_ITEM_TITLE});

        items.add(new Object[]{"Distance Goal", R.drawable.ic_list_distance, ListAdapterTrackSettingItem.LIST_ITEM_CONTENT});
        items.add(new Object[]{null, 0, ListAdapterTrackSettingItem.LIST_ITEM_SEPARATOR});

        items.add(new Object[]{"Time Goal", R.drawable.ic_list_time, ListAdapterTrackSettingItem.LIST_ITEM_CONTENT});
        items.add(new Object[]{null, 0, ListAdapterTrackSettingItem.LIST_ITEM_SEPARATOR});

        items.add(new Object[]{"Step Goal", R.drawable.ic_setting_step, ListAdapterTrackSettingItem.LIST_ITEM_CONTENT});
        items.add(new Object[]{null, 0, ListAdapterTrackSettingItem.LIST_ITEM_SEPARATOR});

        items.add(new Object[]{"Calories Goal", R.drawable.ic_list_cal, ListAdapterTrackSettingItem.LIST_ITEM_CONTENT});

        ListAdapterTrackSettingItem listA = new ListAdapterTrackSettingItem(this, R.layout.list_setting_item, items);

        setListAdapter(listA);

        getListView().setDivider(null);
        getListView().setDividerHeight(0);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 0) {
            Intent i = new Intent(this, ActivitySavedGoals.class);
            i.putExtra("mode", "coach");
            i.putExtra("title", "Coach Goals");
            startActivityForResult(i, Values.REQUEST_CODE);
        } else if (position == 2) {
            Intent i = new Intent(this, ActivitySavedGoals.class);
            i.putExtra("mode", "own");
            i.putExtra("title", "My Saved Goals");
            startActivityForResult(i, Values.REQUEST_CODE);
        } else if (position == 5) {
            Intent i = new Intent(this, ActivityGoalSettingDistance.class);
            startActivityForResult(i, Values.REQUEST_CODE);
        } else if (position == 7) {
            Intent i = new Intent(this, ActivityGoalSettingTime.class);
            startActivityForResult(i, Values.REQUEST_CODE);
        } else if (position == 9) {
            Intent i = new Intent(this, ActivityGoalSettingStep.class);
            startActivityForResult(i, Values.REQUEST_CODE);
        } else if (position == 11) {
            Intent i = new Intent(this, ActivityGoalSettingCalories.class);
            startActivityForResult(i, Values.REQUEST_CODE);
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
