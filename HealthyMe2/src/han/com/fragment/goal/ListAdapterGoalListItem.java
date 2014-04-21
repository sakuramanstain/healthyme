package han.com.fragment.goal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import han.com.R;
import han.com.datapool.CurrentGoal;
import han.com.db.DatabaseHandler;
import han.com.db.GoalRecord;
import han.com.db.UserGoal;
import han.com.tts.Speak;
import java.util.List;

/**
 *
 * @author hanaldo
 */
public class ListAdapterGoalListItem extends ArrayAdapter<Object[]> {

    private final List<Object[]> itemContent;
    private final Context context;

    public ListAdapterGoalListItem(Context context, List<Object[]> objects) {
        super(context, R.layout.list_item_goal_info, objects);
        this.context = context;
        itemContent = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Object[] item = itemContent.get(position);
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.list_item_goal_info, null);
        }

        if (item != null) {
            final UserGoal g = (UserGoal) item[0];

            ImageView img1 = (ImageView) view.findViewById(R.id.list_item_goal_info_img1);
            if (g.getIcon() == 0) {
                img1.setImageResource(R.drawable.ic_goal_list_aerobics);
            } else {
                img1.setImageResource(g.getIcon());
            }

            TextView text1 = (TextView) view.findViewById(R.id.list_item_goal_info_text1);
            if (g.getValid() == UserGoal.GOAL_IS_NON_TRACKING) {
                text1.setText(g.getGoalName());
            } else {
                text1.setText(g.getGoalName() + " " + g.getGoalOrder());
            }

            final ImageView img2 = (ImageView) view.findViewById(R.id.list_item_goal_info_img2);
            if (g.isFinishedNow()) {
                img2.setImageResource(R.drawable.ic_goal_list_green_tick);
            } else {
                if (g.getValid() == UserGoal.GOAL_IS_NON_TRACKING) {
                    img2.setImageResource(R.drawable.ic_goal_list_grey_tick);
                } else {
                    img2.setImageResource(R.drawable.ic_goal_list_play);
                }
            }

            img2.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    if (g.isFinishedNow()) {
                        String s = "This goal is finished";
                        if (g.getFrequency().equals(UserGoal.GOAL_FREQUENCY_DAY)) {
                            s += " for today";
                        } else if (g.getFrequency().equals(UserGoal.GOAL_FREQUENCY_WEEK)) {
                            s += " for this week";
                        } else if (g.getFrequency().equals(UserGoal.GOAL_FREQUENCY_MONTH)) {
                            s += " for this month";
                        }
                        Speak.getInstance(null).speak(s);
                        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (g.getValid() == UserGoal.GOAL_IS_VALID || g.getValid() == UserGoal.GOAL_IS_COACH_NEW) {
                        CurrentGoal.setGoalData(g);
                        CurrentGoal.setGoalRecord(null);
                        CurrentGoal.clearCurrentTrackingInfo();
                        Toast.makeText(context, "Goal is set", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(context, ActivityTrack.class);
                        context.startActivity(i);
                    } else if (g.getValid() == UserGoal.GOAL_IS_NON_TRACKING) {
                        GoalRecord record = GoalRecord.addNewGoalRecord(DatabaseHandler.getInstance(null).getWritableDatabase(),
                                g.getGoalId(), System.currentTimeMillis());
                        GoalRecord.endGoal(DatabaseHandler.getInstance(null).getWritableDatabase(),
                                record.getRecordId(), System.currentTimeMillis(), GoalRecord.COMPLETE_SUCCESS, 0, 0, 0);
                        g.setFinishedNow(true);

                        img2.setImageResource(R.drawable.ic_goal_list_green_tick);
                        Speak.getInstance(null).speak("congratulations");
                        Toast.makeText(context, "Congratulations! You have finished the goal", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            view.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    if (g.getValid() == UserGoal.GOAL_IS_NON_TRACKING) {
                        Intent i = new Intent(context, ActivityUpdateGoal.class);
                        i.putExtra("goal.id", String.valueOf(g.getGoalId()));
                        context.startActivity(i);
                    } else {
                        String s = "This goal is for " + g.getGoalName() + " " + (int) g.getGoalValue() + " " + g.getGoalUnit();
                        AlertDialog alertDialog = new AlertDialog.Builder(context)
                                .setTitle("Fitness Goal Details")
                                .setMessage(s)
                                .create();
                        alertDialog.show();
                    }
                }
            });

            view.setOnLongClickListener(new View.OnLongClickListener() {

                public boolean onLongClick(View arg0) {
                    AlertDialog alertDialog = new AlertDialog.Builder(context)
                            .setTitle("Delete This Goal?")
                            .setMessage(g.getGoalName() + " for every " + g.getFrequency())
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    UserGoal.deleteUserGoal(DatabaseHandler.getInstance(null).getWritableDatabase(), g.getGoalId());
                                    FragmentGoalList.getReloadListHandler().sendEmptyMessage(0);
                                    Toast.makeText(context, "Goal is deleted", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .create();
                    alertDialog.show();
                    return true;
                }
            });
        }
        return view;
    }
}
