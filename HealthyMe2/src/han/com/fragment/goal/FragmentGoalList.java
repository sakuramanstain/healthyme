package han.com.fragment.goal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import han.com.R;
import han.com.db.DatabaseHandler;
import han.com.db.UserGoal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author hanaldo
 */
public class FragmentGoalList extends Fragment {

    private static final String className = FragmentGoalList.class.getName();

    private ListView listView;
    private ArrayList<Object[]> listItems;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(className, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(className, "onDestroy");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(className, "onCreateView");

        View myFragmentView = inflater.inflate(R.layout.fragment_goal_list, container, false);

        listView = (ListView) myFragmentView.findViewById(R.id.frag_goal_list1);
        loadData();
        ListAdapterGoalListItem adapter = new ListAdapterGoalListItem(getActivity(), listItems);
        listView.setAdapter(adapter);

        TextView text1 = (TextView) myFragmentView.findViewById(R.id.frag_goal_text1);
        text1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent i = new Intent(getActivity(), ActivityUpdateGoal.class);
                i.putExtra("goal.id", "");
                getActivity().startActivity(i);
            }
        });

        return myFragmentView;
    }

    private void loadData() {
        List<UserGoal> goals = UserGoal.getAllGoals(DatabaseHandler.getInstance(null).getReadableDatabase());
        listItems = new ArrayList<Object[]>(goals.size());

        for (UserGoal g : goals) {
            listItems.add(new Object[]{g});
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(className, "onResume");
        List<UserGoal> goals = UserGoal.getAllGoals(DatabaseHandler.getInstance(null).getReadableDatabase());
        List<Long> ids = new LinkedList<Long>();
        for (UserGoal g : goals) {
            ids.add(g.getGoalId());
        }
        Toast.makeText(getActivity(), "all goals: " + ids.toString(), Toast.LENGTH_SHORT).show();
    }
}
