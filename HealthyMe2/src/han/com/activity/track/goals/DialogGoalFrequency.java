package han.com.activity.track.goals;

import android.app.Dialog;
import android.content.Context;
import android.widget.AdapterView;
import android.widget.ListView;
import han.com.R;
import han.com.db.UserGoal;
import java.util.ArrayList;
import java.util.Collections;

public class DialogGoalFrequency extends Dialog {

    public static final Object[][] GoalFrequencies = new Object[][]{
        new Object[]{"Once", UserGoal.GOAL_FREQUENCY_ONCE},
        new Object[]{"Daily", UserGoal.GOAL_FREQUENCY_DAY},
        new Object[]{"Weekly", UserGoal.GOAL_FREQUENCY_WEEK},
        new Object[]{"Monthly", UserGoal.GOAL_FREQUENCY_MONTH}
    };
    private final ListView listView;

    public DialogGoalFrequency(Context context, String title) {
        super(context);
        setContentView(R.layout.dialog_goal_frequency);
        setTitle(title);

        ArrayList<Object[]> items = new ArrayList<Object[]>(GoalFrequencies.length);
        Collections.addAll(items, GoalFrequencies);

        listView = (ListView) this.findViewById(R.id.dialog_goal_frequency_list1);
        ListAdapterGoalFrequencyItem adapter = new ListAdapterGoalFrequencyItem(context, items);
        listView.setAdapter(adapter);
    }

    public void setDialogOnItemClickListener(AdapterView.OnItemClickListener l) {
        listView.setOnItemClickListener(l);
    }
}
