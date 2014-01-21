package han.com.activity.track.goals;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;
import han.com.R;
import han.com.datapool.CurrentGoal;
import han.com.db.DatabaseHandler;
import han.com.db.UserGoal;
import han.com.utils.MyWidgets;
import han.com.utils.Values;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.text.WordUtils;

/**
 *
 * @author han
 */
public class ActivitySavedGoals extends ListActivity {

    private static final String className = ActivitySavedGoals.class.getName();
    private static Map<String, Integer> sportImages;
    private static Map<String, Integer> sportImagesBlack;

    public static Map<String, Integer> getSportImages() {
        if (sportImages == null) {
            readSportImages();
        }
        return Collections.unmodifiableMap(sportImages);
    }

    public static Map<String, Integer> getSportImagesBlack() {
        if (sportImagesBlack == null) {
            readSportImages();
        }
        return Collections.unmodifiableMap(sportImagesBlack);
    }

    private static void readSportImages() {
        if (sportImages == null) {
            Log.d(className, "read sport images");
            sportImages = new HashMap<String, Integer>(CurrentGoal.WorkoutNames.length);
            sportImagesBlack = new HashMap<String, Integer>(CurrentGoal.WorkoutNames.length);
            for (int i = 0; i < CurrentGoal.WorkoutNames.length; i++) {
                sportImages.put((String) CurrentGoal.WorkoutNames[i][0], (Integer) CurrentGoal.WorkoutNames[i][1]);
                sportImagesBlack.put((String) CurrentGoal.WorkoutNames[i][0], (Integer) CurrentGoal.WorkoutNames[i][2]);
            }
            sportImages.put("Coach", R.drawable.ic_setting_coach);
            sportImagesBlack.put("Coach", R.drawable.ic_setting_coach_black);
        }
    }
    private List<UserGoal> allGoals;
    private int selectedItemIndex;
    private long selectedItemGoalId;
    private ListAdapterSavedGoalsItem listAdapter;
    private String mode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_goals);

        String title = getIntent().getStringExtra("title");
        MyWidgets.makeSubActivityTitle(this, title, R.drawable.ic_title_goal);

        readSportImages();

        mode = getIntent().getStringExtra("mode");

        registerForContextMenu(getListView());

        allGoals = UserGoal.getUserGoalsByName(DatabaseHandler.getInstance(null).getReadableDatabase(), mode);

        refreshList();
    }

    private void refreshList() {
        ArrayList<Object[]> items = new ArrayList<Object[]>(allGoals.size());
        fillGoalList(items);

        listAdapter = new ListAdapterSavedGoalsItem(this, R.layout.activity_saved_goals_item, items);
        setListAdapter(listAdapter);
        selectedItemIndex = -1;
        selectedItemGoalId = -1;
        Log.d(className, "refreshList()");
    }

    private void fillGoalList(ArrayList<Object[]> items) {
        for (UserGoal ug : allGoals) {
            String[] tokens = ug.getGoalType().split("\\.");
            if (tokens.length == 0) {
                Toast.makeText(this, "data error: " + ug.getGoalType(), Toast.LENGTH_SHORT).show();
                return;
            }
            String goalTypeWord = WordUtils.capitalize(tokens[tokens.length - 1]);

            items.add(new Object[]{ug.getGoalName() + " " + ug.getGoalOrder(),
                goalTypeWord + " Goal: " + ug.getGoalValue() + " " + ug.getGoalUnit(),
                ug.getGoalId(),
                ug.getGoalType(),
                sportImages.get(ug.getGoalName())});
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        CurrentGoal.setGoalData(allGoals.get(position));
        CurrentGoal.setGoalRecord(null);
        CurrentGoal.clearCurrentTrackingInfo();
        Toast.makeText(this, "goal is set", Toast.LENGTH_SHORT).show();
        finishPreviousActivity();
        finish();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (mode.equals("coach")) {
            Log.d(className, "cannot delete coach goal");
            return;
        }
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Actions");
        menu.add(0, v.getId(), 0, "Delete");

        AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
        selectedItemIndex = info.position;
        selectedItemGoalId = (Long) info.targetView.getTag();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (selectedItemIndex == -1) {
            return false;
        }
        if (item.getGroupId() == 0) {
            listAdapter.remove(listAdapter.getItem(selectedItemIndex));
            listAdapter.notifyDataSetChanged();
            UserGoal.deleteUserGoal(DatabaseHandler.getInstance(null).getWritableDatabase(), selectedItemGoalId);

            allGoals.remove(selectedItemIndex);
            Toast.makeText(this, "goal is deleted", Toast.LENGTH_SHORT).show();
        }
        selectedItemIndex = -1;
        return true;
    }

    private void finishPreviousActivity() {
        if (getParent() == null) {
            setResult(Values.RESULT_CODE_FINISH);
        } else {
            getParent().setResult(Values.RESULT_CODE_FINISH);
        }
    }
}
