package han.com.fragment.goal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import han.com.R;
import java.util.List;

/**
 *
 * @author han
 */
public class ListAdapterGoalIconItem extends ArrayAdapter<Object[]> {

    private final List<Object[]> itemContent;
    private final Context context;

    public ListAdapterGoalIconItem(Context context, List<Object[]> objects) {
        super(context, R.layout.dialog_goal_icons_item, objects);
        this.context = context;
        itemContent = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.dialog_goal_icons_item, null);
        }

        Object[] item = itemContent.get(position);
        if (item != null) {
            ImageView itemImage = (ImageView) view.findViewById(R.id.dialog_goal_icons_item_image1);
            itemImage.setImageResource((Integer) item[0]);
        }

        return view;
    }
}
