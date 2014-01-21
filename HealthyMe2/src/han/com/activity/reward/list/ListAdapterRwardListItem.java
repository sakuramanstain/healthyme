package han.com.activity.reward.list;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import han.com.R;
import han.com.activity.reward.adding.ActivityEnterRewardName;
import han.com.db.Reward;
import java.util.List;

/**
 *
 * @author han
 */
public class ListAdapterRwardListItem extends ArrayAdapter<Object[]> {

    private static final String className = ListAdapterRwardListItem.class.getName();
    private List<Object[]> itemContent;
    private Context context;
    private int textViewResourceId;

    public ListAdapterRwardListItem(Context context, int textViewResourceId, List<Object[]> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        itemContent = objects;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(textViewResourceId, null);
        }

        Object[] item = itemContent.get(position);
        final long indexForReward = position + 1;
        Reward reward = (Reward) item[0];
        int totalFinishedCoachGoals = (Integer) item[1];

        if (item != null) {
            TextView itemIndex = (TextView) view.findViewById(R.id.reward_list_item_index);
            itemIndex.setText(String.valueOf(indexForReward));

            View treeTrunk = view.findViewById(R.id.reward_list_tree_trunk);
            View treeBranch = view.findViewById(R.id.reward_list_tree_branch);
            View treeBall = view.findViewById(R.id.reward_list_tree_ball);
            if (indexForReward <= totalFinishedCoachGoals) {
                treeTrunk.setBackground(context.getResources().getDrawable(R.color.my_green));
                treeBranch.setBackground(context.getResources().getDrawable(R.color.my_light_gray));
                treeBall.setBackground(context.getResources().getDrawable(R.drawable.ic_reward_light_ball));

                if (reward != null) {
                    treeBranch.setBackground(context.getResources().getDrawable(R.color.my_green));
                }
            } else {
                treeTrunk.setBackground(context.getResources().getDrawable(R.color.my_gray));
                treeBranch.setBackground(context.getResources().getDrawable(R.color.my_gray));
                treeBall.setBackground(context.getResources().getDrawable(R.drawable.ic_reward_ball));

                if (reward == null) {
                    treeBall.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View arg0) {
                            Intent i = new Intent(context, ActivityEnterRewardName.class);
                            i.putExtra(ActivityEnterRewardName.EXTRA_NAME_GOAL_NUMBER, indexForReward);
                            context.startActivity(i);
                        }
                    });
                }
            }

            TextView rewardName = (TextView) view.findViewById(R.id.reward_list_fruit_text);
            View fruitBranch = view.findViewById(R.id.reward_list_fruit_branch);
            View fruitBall = view.findViewById(R.id.reward_list_fruit_ball);
            if (reward != null) {
                rewardName.setVisibility(View.VISIBLE);
                fruitBranch.setVisibility(View.VISIBLE);
                fruitBall.setVisibility(View.GONE);//change later
                rewardName.setText(reward.getRewardName());
            } else {
                rewardName.setVisibility(View.INVISIBLE);
                fruitBranch.setVisibility(View.INVISIBLE);
                fruitBall.setVisibility(View.GONE);//change later
            }

        }
        return view;
    }
}
