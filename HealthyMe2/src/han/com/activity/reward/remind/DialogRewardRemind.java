package han.com.activity.reward.remind;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import han.com.R;
import han.com.datapool.MySharedPreferences;

/**
 *
 * @author han
 */
public class DialogRewardRemind extends Dialog implements OnItemClickListener {

    private static final String className = DialogRewardRemind.class.getName();
    private Context context;

    public DialogRewardRemind(Context context, String title) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_option_selection);
        setTitle(title);

        ListView listView = (ListView) this.findViewById(R.id.list_option_selection);

        listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice,
                new String[]{"Yes", "No"}));
        listView.setOnItemClickListener(this);

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        boolean play = MySharedPreferences.getInstance(null).getRemindReward();
        if (play) {
            listView.setItemChecked(0, true);
        } else {
            listView.setItemChecked(1, true);
        }

    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            if (MySharedPreferences.getInstance(null).setRemindReward(true)) {
                Toast.makeText(context, "remind reward is on", Toast.LENGTH_SHORT).show();
            }
        } else if (position == 1) {
            if (MySharedPreferences.getInstance(null).setRemindReward(false)) {
                Toast.makeText(context, "remind reward is off", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
