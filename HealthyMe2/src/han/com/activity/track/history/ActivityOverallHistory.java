package han.com.activity.track.history;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import han.com.R;
import han.com.db.DatabaseHandler;
import han.com.db.GoalRecord;
import han.com.db.UserGoal;
import han.com.tts.Speak;
import han.com.utils.MyWidgets;
import han.com.utils.Values;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 *
 * @author han
 */
public class ActivityOverallHistory extends ListActivity {

    private static final String className = ActivityOverallHistory.class.getName();
    private String tFinished, tGoals, tDistance, tTime, tSteps, tCalories, lActivity, speakLastActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overall_history);

        MyWidgets.makeSubActivityTitle(this, "Overall Performance", R.drawable.ic_setting_stat);

        loadDatabaseValues();

        List<String[]> list = new LinkedList<String[]>();
        list.add(new String[]{"Last Activity", lActivity});
        list.add(new String[]{"Finished Goals", tFinished});
        list.add(new String[]{"Tried Goals", tGoals});
        list.add(new String[]{"Total Distance", tDistance});
        list.add(new String[]{"Total Time", tTime});
        list.add(new String[]{"Total Steps", tSteps});
        list.add(new String[]{"Total Calories", tCalories});
        list.add(new String[0]);

        ListAdapterHistoryItem listAdapter = new ListAdapterHistoryItem(this, R.layout.activity_overall_history_item, list);
        setListAdapter(listAdapter);
    }

    private void loadDatabaseValues() {
        tFinished = String.valueOf(GoalRecord.getTotalNumberGoalsInCompleteType(
                DatabaseHandler.getInstance(null).getReadableDatabase(), GoalRecord.COMPLETE_SUCCESS));

        tGoals = String.valueOf(GoalRecord.getTotalNumberGoals(DatabaseHandler.getInstance(null).getReadableDatabase()));

        tDistance = String.valueOf(GoalRecord.getTotalCompleteGoalValue(DatabaseHandler.getInstance(null).getReadableDatabase(), UserGoal.GOAL_TYPE_DISTANCE));
        tDistance += " km";

        String[] tokens = DurationFormatUtils.formatDuration(
                GoalRecord.getTotalCompleteGoalTime(DatabaseHandler.getInstance(null).getReadableDatabase()), "HH:mm:ss").split(":");
        tTime = tokens[0] + "h " + tokens[1] + "m " + tokens[2] + "s";

        tSteps = String.valueOf(GoalRecord.getTotalCompleteGoalSteps(DatabaseHandler.getInstance(null).getReadableDatabase()));

        tCalories = Values.formatCalories(GoalRecord.getTotalCompleteGoalCalories(DatabaseHandler.getInstance(null).getReadableDatabase()));

        tCalories += " Cal";

        long time = GoalRecord.getLastActivity(DatabaseHandler.getInstance(null).getReadableDatabase());
        lActivity = DateFormatUtils.format(time, "yyyy-MM-dd HH:mm:ss");
        speakLastActivity = DateFormatUtils.format(time, "MM/dd/yyyy");
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (position == 0) {
            Speak.getInstance(null).speak("your last activity is " + speakLastActivity);
        } else if (position == 1) {
            Speak.getInstance(null).speak("you have finished " + tFinished + " goals, that is great");
        } else if (position == 2) {
            Speak.getInstance(null).speak("your have tried " + tGoals + " goals, keep up to good work");
        } else if (position == 3) {
            Speak.getInstance(null).speak("your have travelled " + tDistance + " for your goals");
        } else if (position == 4) {
            String[] tokens = tTime.split(" ");
            String time = tokens[0].split("h")[0] + " hours ";
            time += tokens[1].split("m")[0] + " minutes ";
            time += tokens[2].split("s")[0] + " seconds ";

            Speak.getInstance(null).speak("your have already exercised " + time + " for your goals");
        } else if (position == 5) {
            Speak.getInstance(null).speak("your have made " + tSteps + " steps for your goals, keep up to good work");
        } else if (position == 6) {
            Speak.getInstance(null).speak("your have burned " + tCalories.split("Cal")[0] + " calories so far, that is great");
        }

    }
}
