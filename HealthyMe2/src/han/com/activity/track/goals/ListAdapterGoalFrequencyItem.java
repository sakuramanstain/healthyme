package han.com.activity.track.goals;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import han.com.R;
import java.util.List;

/**
 *
 * @author han
 */
public class ListAdapterGoalFrequencyItem extends ArrayAdapter<Object[]> {

    private final List<Object[]> itemContent;
    private final Context context;

    public ListAdapterGoalFrequencyItem(Context context, List<Object[]> objects) {
        super(context, R.layout.dialog_goal_frequency_item, objects);
        this.context = context;
        itemContent = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.dialog_goal_frequency_item, null);
        }

        Object[] item = itemContent.get(position);
        if (item != null) {
            TextView t = (TextView) view.findViewById(R.id.dialog_goal_frequency_item_text1);
            t.setText((CharSequence) item[0]);
        }

        return view;
    }
}
