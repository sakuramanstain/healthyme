package han.com.fragment.goal;

import android.app.Dialog;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ListView;
import han.com.R;
import java.util.ArrayList;
import java.util.Collections;

public class DialogGoalIcon extends Dialog {

    private final ListView listView;

    public DialogGoalIcon(Context context, String title) {
        super(context);
        setContentView(R.layout.dialog_goal_icons);
        setTitle(title);

        ArrayList<Object[]> items = new ArrayList<Object[]>(ActivityUpdateGoal.GoalIcons.length);
        Collections.addAll(items, ActivityUpdateGoal.GoalIcons);

        listView = (ListView) this.findViewById(R.id.dialog_goal_icons_list1);
        ListAdapterGoalIconItem adapter = new ListAdapterGoalIconItem(context, items);
        listView.setAdapter(adapter);
    }

    public void setDialogOnItemClickListener(AdapterView.OnItemClickListener l) {
        listView.setOnItemClickListener(l);
    }
}
