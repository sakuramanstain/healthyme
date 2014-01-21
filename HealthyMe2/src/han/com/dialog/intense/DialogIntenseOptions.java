package han.com.dialog.intense;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import han.com.R;
import han.com.datapool.CurrentGoal;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author han
 */
public class DialogIntenseOptions extends Dialog implements AdapterView.OnItemClickListener {

    private static final String className = DialogIntenseOptions.class.getName();
    private InterfaceSelectIntense callback;
    public static int lastIntense = 0;
    public static String lastIntenseName = null;

    public DialogIntenseOptions(Context context, String title) {
        super(context);
        setContentView(R.layout.dialog_intense_names);
        setTitle(title);

        ArrayList<Object[]> items = new ArrayList<Object[]>(CurrentGoal.IntenseNames.length);
        Collections.addAll(items, CurrentGoal.IntenseNames);

        ListView listView = (ListView) this.findViewById(R.id.list_intense_names);
        ListAdapterIntenseItem listAdapter = new ListAdapterIntenseItem(context, R.layout.dialog_intense_names_item, items);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
    }

    public void setCallback(InterfaceSelectIntense callback) {
        this.callback = callback;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int selectedIntense = (Integer) CurrentGoal.IntenseNames[position][1];
        lastIntense = selectedIntense;
        lastIntenseName = (String) CurrentGoal.IntenseNames[position][0];

        this.dismiss();
        if (callback != null) {
            callback.intenseIsSelected((String) CurrentGoal.IntenseNames[position][0], selectedIntense);
        }
    }
}
