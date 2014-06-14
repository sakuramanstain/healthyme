package han.com.activity.main.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import han.com.R;
import han.com.activity.reward.list.ListAdapterRwardListItem;
import han.com.activity.reward.welcome.ActivityRewardWelcome;
import han.com.datapool.CurrentGoal;
import han.com.db.DatabaseHandler;
import han.com.db.Reward;
import han.com.db.SectionRecord;
import han.com.db.UserGoal;
import han.com.utils.MyWidgets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.time.DurationFormatUtils;

/**
 *
 * @author han
 */
public class FragmentReward extends Fragment {

    private static final String className = FragmentReward.class.getName();
    private static Handler loadRewardsHandler;
    private static Handler checkWeekHandler;

    public static Handler getLoadRewardsHandler() {
        return loadRewardsHandler;
    }

    public static Handler getCheckWeekHandler() {
        return checkWeekHandler;
    }
    private ListView listView;
    private ListAdapterRwardListItem listAdapter;
    private int totalCoachGoals;
    private Map<Long, Reward> rewards;
    private int totalFinishedCoachGoals;
    private ArrayList<Object[]> listItems;
    private final int[][] flowerIcons = new int[][]{
        new int[]{R.drawable.ic_notification_f1_small, R.drawable.ic_notification_f1},
        new int[]{R.drawable.ic_notification_f2_small, R.drawable.ic_notification_f2},
        new int[]{R.drawable.ic_notification_f3_small, R.drawable.ic_notification_f3},
        new int[]{R.drawable.ic_notification_f4_small, R.drawable.ic_notification_f4},
        new int[]{R.drawable.ic_notification_f5_small, R.drawable.ic_notification_f5}
    };
    private final int maxWeekGoal = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(className, "onCreate");

        loadRewardsHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    doLoadRewards();
                }
            }
        };

        checkWeekHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    checkWeekProgress(false);
                } else if (msg.what == 1) {
                    checkWeekProgress(true);
                }
            }
        };
        FragmentReward.getCheckWeekHandler().sendEmptyMessage(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(className, "onDestroy");
        loadRewardsHandler = null;
    }

    private void doLoadRewards() {
        loadData();
        listAdapter = new ListAdapterRwardListItem(getActivity(), R.layout.fragment_reward_list_item, listItems);
        listView.setAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(className, "onCreateView");

        View myFragmentView = inflater.inflate(R.layout.fragment_reward, container, false);
        MyWidgets.makeFragmentTitle(myFragmentView, "Reward");

        LinearLayout settingAll = (LinearLayout) myFragmentView.findViewById(R.id.fragment_setting_item_1);
        ImageView itemImage = (ImageView) settingAll.findViewWithTag("item_image");
        itemImage.setImageResource(R.drawable.ic_reward_info);
        settingAll.setVisibility(View.VISIBLE);
        settingAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), ActivityRewardWelcome.class);
                getActivity().startActivity(i);
            }
        });

        LinearLayout settingStat = (LinearLayout) myFragmentView.findViewById(R.id.setting_item_stat);
        settingStat.setVisibility(View.VISIBLE);
        settingStat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SectionRecord.getAllSectionTime(new SectionRecord.SectionTimeCallback() {

                    public void getResult(List<SectionRecord> records) {
                        long timeGoal = 0, timeReward = 0, timeCamera = 0, timeShare = 0, lastTime = 0;
                        String lastSection = "";

                        for (SectionRecord r : records) {
                            if (lastSection.equals(SectionRecord.Section_Goal)) {
                                timeGoal += r.getTimeIn() - lastTime;
                            } else if (lastSection.equals(SectionRecord.Section_Reward)) {
                                timeReward += r.getTimeIn() - lastTime;
                            } else if (lastSection.equals(SectionRecord.Section_Camera)) {
                                timeCamera += r.getTimeIn() - lastTime;
                            } else if (lastSection.equals(SectionRecord.Section_Share)) {
                                timeShare += r.getTimeIn() - lastTime;
                            } else if (lastSection.equals(SectionRecord.Section_App_Out)) {
                                lastTime = 0;
                                lastSection = "";
                                continue;
                            }
                            lastTime = r.getTimeIn();
                            lastSection = r.getSectionName();
                        }

                        String s = "Duration in Goal section:\n" + DurationFormatUtils.formatDuration(timeGoal, "HH:mm:ss") + "\n\n";
                        s += "Duration in Reward section:\n" + DurationFormatUtils.formatDuration(timeReward, "HH:mm:ss") + "\n\n";
                        s += "Duration in Camera section:\n" + DurationFormatUtils.formatDuration(timeCamera, "HH:mm:ss") + "\n\n";
                        s += "Duration in Share section:\n" + DurationFormatUtils.formatDuration(timeShare, "HH:mm:ss") + "\n\n";

                        new AlertDialog.Builder(getActivity())
                                .setMessage(s)
                                .setNegativeButton("ok", null)
                                .create().show();
                    }
                });

            }
        });

        myFragmentView.findViewById(R.id.setting_item_music).setVisibility(View.GONE);

        listView = (ListView) myFragmentView.findViewById(R.id.frag_reward_goal_list);
        listView.setDivider(null);
        listView.setDividerHeight(0);

        loadData();
        listAdapter = new ListAdapterRwardListItem(getActivity(), R.layout.fragment_reward_list_item, listItems);
        listView.setAdapter(listAdapter);

        return myFragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(className, "onResume");
        doLoadRewards();
    }

    private void loadData() {
        totalCoachGoals = UserGoal.getTotalCoachGoalsNumber(DatabaseHandler.getInstance(null).getReadableDatabase());
        rewards = Reward.getAllRewards(DatabaseHandler.getInstance(null).getReadableDatabase());
        totalFinishedCoachGoals = UserGoal.getTotalFinishedCoachGoalsNumber(DatabaseHandler.getInstance(null).getReadableDatabase());
        Log.d(className, "all rewards: " + rewards);
        Log.d(className, "totalCoachGoals: " + totalCoachGoals);
        Log.d(className, "totalFinishedCoachGoals: " + totalFinishedCoachGoals);

        listItems = new ArrayList<Object[]>(totalCoachGoals);
        for (int i = 0; i < totalCoachGoals; i++) {
            long indexForReward = i + 1;
            listItems.add(new Object[]{rewards.get(indexForReward), totalFinishedCoachGoals});
        }
    }

    private void checkWeekProgress(boolean newFinish) {
        if (CurrentGoal.isTrackIsStarted()) {
            return;
        }

        Calendar c = new GregorianCalendar();

        int thisYear = c.get(Calendar.YEAR);
        int thisWeekNumber = c.get(Calendar.WEEK_OF_YEAR);

        c.clear();
        c.set(Calendar.WEEK_OF_YEAR, thisWeekNumber);
        c.set(Calendar.YEAR, thisYear);

        long sunday = c.getTime().getTime();
        long nextSunday = sunday + 24 * 60 * 60 * 1000 * 7;

        List<UserGoal> goals = UserGoal.getAllCoachGoals(DatabaseHandler.getInstance(null).getReadableDatabase(),
                sunday,
                nextSunday);

        int finished = 0;
        int total = 0;
        for (UserGoal goal : goals) {
            if (goal.getValid() == UserGoal.GOAL_IS_COACH_FINISHED) {
                finished++;
            }
            total++;
        }

        Log.d(className, "week goals: finished " + finished);
        Log.d(className, "week goals: total " + total);

        if (finished == 0) {
            return;
        }

        if (finished > maxWeekGoal) {
            finished = maxWeekGoal;
        }

        MyWidgets.showNotification(flowerIcons[finished - 1][0],
                flowerIcons[finished - 1][1],
                "Your got " + finished + " flowers",
                "Your have " + finished + " flowers",
                "",
                "",
                MyWidgets.NOTIFICATION_ID_WEEK_PROGRESS,
                true);

        if (newFinish) {
            String s;
            if (finished == 1) {
                s = "Woohoo! Your seed blooms! Good job!";
            } else if (finished >= maxWeekGoal) {
                s = "Big congratulations!! You’ve accomplished all the weekly goals!";
            } else {
                s = "Your exercise makes another seed bloom! Congratulations!";
            }

            Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
        }
    }
}
