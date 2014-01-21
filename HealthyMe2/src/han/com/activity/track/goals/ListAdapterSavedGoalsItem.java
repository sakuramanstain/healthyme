package han.com.activity.track.goals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import han.com.R;
import han.com.db.UserGoal;
import java.util.List;

/**
 *
 * @author han
 */
public class ListAdapterSavedGoalsItem extends ArrayAdapter<Object[]> {

    private List<Object[]> itemContent;
    private Context context;
    private int listItemResourceId;

    public ListAdapterSavedGoalsItem(Context context, int listItemResourceId, List<Object[]> objects) {
        super(context, listItemResourceId, objects);
        this.context = context;
        this.listItemResourceId = listItemResourceId;
        itemContent = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(listItemResourceId, null);
        }

        Object[] item = itemContent.get(position);
        if (item != null) {
            TextView itemObj = (TextView) view.findViewById(R.id.saved_goals_item);
            TextView itemValue1 = (TextView) view.findViewById(R.id.saved_goals_item_value_1);
            ImageView goalTypeImage = (ImageView) view.findViewById(R.id.act_saved_goal_image);
            ImageView goalSportImage = (ImageView) view.findViewById(R.id.act_saved_goal_sport_image);

            String goalType = (String) item[3];
            if (goalType.equals(UserGoal.GOAL_TYPE_DISTANCE)) {
                goalTypeImage.setImageResource(R.drawable.ic_list_distance);
            } else if (goalType.equals(UserGoal.GOAL_TYPE_TIME)) {
                goalTypeImage.setImageResource(R.drawable.ic_list_time);
            } else if (goalType.equals(UserGoal.GOAL_TYPE_STEP)) {
                goalTypeImage.setImageResource(R.drawable.ic_setting_step);
            } else if (goalType.equals(UserGoal.GOAL_TYPE_CALORIES)) {
                goalTypeImage.setImageResource(R.drawable.ic_list_cal);
            }

            goalSportImage.setImageResource((Integer) item[4]);

            if (itemObj != null) {
                itemObj.setText((String) item[0]);
            }

            if (itemValue1 != null) {
                itemValue1.setText((String) item[1]);
            }
        }

        view.setTag((Long) item[2]);
        return view;
    }
}
