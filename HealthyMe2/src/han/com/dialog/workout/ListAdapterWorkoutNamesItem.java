package han.com.dialog.workout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import han.com.R;
import java.util.List;

/**
 *
 * @author han
 */
public class ListAdapterWorkoutNamesItem extends ArrayAdapter<Object[]> {

    private static final String className = ListAdapterWorkoutNamesItem.class.getName();
    private List<Object[]> itemContent;
    private Context context;
    private int textViewResourceId;

    public ListAdapterWorkoutNamesItem(Context context, int textViewResourceId, List<Object[]> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        itemContent = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(textViewResourceId, null);
        }

        Object[] item = itemContent.get(position);
        if (item != null) {
            TextView itemObj = (TextView) view.findViewById(R.id.workout_names_item);
            ImageView itemImage = (ImageView) view.findViewById(R.id.workout_names_item_image);

            itemObj.setText((String) item[0]);
            itemImage.setImageResource((Integer) item[1]);
        }

        return view;
    }
}
