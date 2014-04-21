package han.com.fragment.goal;

import android.app.Dialog;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ListView;
import han.com.R;
import java.util.ArrayList;
import java.util.Collections;

public class DialogGoalIcon extends Dialog {

    public static final Object[][] GoalIcons = new Object[][]{
        new Object[]{R.drawable.ic_goal_type_apple},
        new Object[]{R.drawable.ic_goal_type_beer},
        new Object[]{R.drawable.ic_goal_type_candy},
        new Object[]{R.drawable.ic_goal_type_carrott},
        new Object[]{R.drawable.ic_goal_type_chips},
        new Object[]{R.drawable.ic_goal_type_cigarette},
        new Object[]{R.drawable.ic_goal_type_coffee},
        new Object[]{R.drawable.ic_goal_type_dairy},
        new Object[]{R.drawable.ic_goal_type_grains},
        new Object[]{R.drawable.ic_goal_type_place},
        new Object[]{R.drawable.ic_goal_type_protein},
        new Object[]{R.drawable.ic_goal_type_salt},
        new Object[]{R.drawable.ic_goal_type_softdrink},
        new Object[]{R.drawable.ic_goal_type_television},
        new Object[]{R.drawable.ic_goal_type_water}
    };
    private final ListView listView;

    public DialogGoalIcon(Context context, String title) {
        super(context);
        setContentView(R.layout.dialog_goal_icons);
        setTitle(title);

        ArrayList<Object[]> items = new ArrayList<Object[]>(GoalIcons.length);
        Collections.addAll(items, GoalIcons);

        listView = (ListView) this.findViewById(R.id.dialog_goal_icons_list1);
        ListAdapterGoalIconItem adapter = new ListAdapterGoalIconItem(context, items);
        listView.setAdapter(adapter);
    }

    public void setDialogOnItemClickListener(AdapterView.OnItemClickListener l) {
        listView.setOnItemClickListener(l);
    }
}
