package han.com.fragment.goal;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import han.com.R;
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
            text1.setText(g.getGoalName());

            final ImageView img2 = (ImageView) view.findViewById(R.id.list_item_goal_info_img2);
            if (g.getValid() == UserGoal.GOAL_IS_NON_TRACKING) {
                img2.setImageResource(R.drawable.ic_goal_list_grey_tick);
            } else if (g.getValid() == UserGoal.GOAL_IS_NON_TRACKING_FINISHED) {
                img2.setImageResource(R.drawable.ic_goal_list_green_tick);
            } else {
                img2.setImageResource(R.drawable.ic_goal_list_play);
            }
            img2.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    if (g.getValid() != 4) {
                        Toast.makeText(context, "You cannot mark this goal", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    g.setValid(UserGoal.GOAL_IS_NON_TRACKING_FINISHED);
                    img2.setImageResource(R.drawable.ic_goal_list_green_tick);
                    Speak.getInstance(null).speak("great!");
                }
            });

            view.setOnClickListener(new View.OnClickListener() {

                public void onClick(View arg0) {
                    Intent i = new Intent(context, ActivityUpdateGoal.class);
                    i.putExtra("goal.id", String.valueOf(g.getGoalId()));
                    context.startActivity(i);
                }
            });
        }
        return view;
    }
}
