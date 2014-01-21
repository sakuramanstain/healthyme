package han.com.dialog.workout;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import han.com.R;
import han.com.datapool.CurrentGoal;
import han.com.db.DatabaseHandler;
import han.com.db.UserGoal;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author han
 */
public class DialogWorkoutNames extends Dialog implements OnItemClickListener {

    private static final String className = DialogWorkoutNames.class.getName();
    public static String lastWorkout = null;
    private InterfaceSelectWorkout callback;

    public DialogWorkoutNames(Context context, String title) {
        super(context);
        setContentView(R.layout.dialog_workout_names);
        setTitle(title);

        ArrayList<Object[]> items = new ArrayList<Object[]>(CurrentGoal.WorkoutNames.length);
        Collections.addAll(items, CurrentGoal.WorkoutNames);

        ListView listView = (ListView) this.findViewById(R.id.list_workout_names);
        ListAdapterWorkoutNamesItem listAdapter = new ListAdapterWorkoutNamesItem(context, R.layout.dialog_workout_names_item, items);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(this);
    }

    public void setCallback(InterfaceSelectWorkout callback) {
        this.callback = callback;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String selectedGoalName = (String) CurrentGoal.WorkoutNames[position][0];
        int selectedGoalOrder = UserGoal.getGoalNameNextOrder(DatabaseHandler.getInstance(null).getReadableDatabase(), selectedGoalName);

        lastWorkout = selectedGoalName;

        this.dismiss();
        if (callback != null) {
            callback.workoutIsSelected(selectedGoalName + " " + selectedGoalOrder,
                    (Integer) CurrentGoal.WorkoutNames[position][1],
                    selectedGoalName,
                    selectedGoalOrder);
        }
    }
}
