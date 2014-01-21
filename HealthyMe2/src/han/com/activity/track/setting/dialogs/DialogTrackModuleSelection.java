package han.com.activity.track.setting.dialogs;

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
public class DialogTrackModuleSelection extends Dialog implements OnItemClickListener {

    private static final String className = DialogTrackModuleSelection.class.getName();
    private Context context;

    public DialogTrackModuleSelection(Context context, String title) {
        super(context);
        this.context = context;
        setContentView(R.layout.dialog_option_selection);
        setTitle(title);

        ListView listView = (ListView) this.findViewById(R.id.list_option_selection);

        listView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice,
                new String[]{"Track Inside and Outside", "Track Outside Only"}));
        listView.setOnItemClickListener(this);

        listView.setItemsCanFocus(false);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        int module = MySharedPreferences.getInstance(null).getTrackingModule();
        if (module != -1) {
            listView.setItemChecked(module, true);
        }

    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            if (MySharedPreferences.getInstance(null).setTrackingModule(0)) {
                Toast.makeText(context, "Using pedometer only", Toast.LENGTH_SHORT).show();
            }
        } else if (position == 1) {
            if (MySharedPreferences.getInstance(null).setTrackingModule(1)) {
                Toast.makeText(context, "Using both pedometer and gps", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
