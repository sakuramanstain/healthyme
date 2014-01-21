package han.com.activity.reward.settingmenu;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import han.com.R;
import han.com.activity.reward.welcome.ActivityRewardWelcome;
import han.com.ui.common.ListAdapterTrackSettingItem;
import han.com.utils.MyWidgets;
import java.util.ArrayList;

/**
 *
 * @author han
 */
public class ActivityRewardSettingMenu extends ListActivity {

    private static final String className = ActivityRewardSettingMenu.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_setting_activity);

        MyWidgets.makeSubActivityTitle(this, "Reward Settings", R.drawable.ic_setting_all);

        ArrayList<Object[]> items = new ArrayList<Object[]>(8);
        //items.add(new Object[]{"Reward reminder", R.drawable.ic_list_stop, ListAdapterTrackSettingItem.LIST_ITEM_CONTENT});
        //items.add(new Object[]{null, 0, ListAdapterTrackSettingItem.LIST_ITEM_SEPARATOR});

        items.add(new Object[]{"Reward Instruction", R.drawable.ic_list_question, ListAdapterTrackSettingItem.LIST_ITEM_CONTENT});
        items.add(new Object[]{null, 0, ListAdapterTrackSettingItem.LIST_ITEM_SEPARATOR});

        ListAdapterTrackSettingItem listA = new ListAdapterTrackSettingItem(this, R.layout.list_setting_item, items);

        setListAdapter(listA);

        getListView().setDivider(null);
        getListView().setDividerHeight(0);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position == -1) {
            //DialogRewardRemind dialog = new DialogRewardRemind(this, "Remind you about reward?");
            //dialog.show();
        } else if (position == 0) {
            Intent i = new Intent(this, ActivityRewardWelcome.class);
            startActivity(i);
        }
    }
}
