package han.com.fragment.goal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import han.com.R;
import han.com.db.DatabaseHandler;
import han.com.db.UserGoal;
import java.util.ArrayList;
import java.util.List;

public class FragmentGoalList extends Fragment {

    private static final String className = FragmentGoalList.class.getName();
    private static Handler reloadListHandler;

    public static Handler getReloadListHandler() {
        return reloadListHandler;
    }

    private ListView listView;
    private ArrayList<Object[]> listItems;
    private ListAdapterGoalListItem listAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(className, "onCreate");

        reloadListHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    reloadList();
                }
            }
        };
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
        listAdapter = new ListAdapterGoalListItem(getActivity(), listItems);
        listView.setAdapter(listAdapter);

        TextView text1 = (TextView) myFragmentView.findViewById(R.id.frag_goal_text1);
        text1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //Add new goal
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

    private void reloadList() {
        loadData();
        listAdapter.clear();
        listAdapter.addAll(listItems);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(className, "onResume");
    }
}
