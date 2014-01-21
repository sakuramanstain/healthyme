package han.com.dialog.intense;

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
public class ListAdapterIntenseItem extends ArrayAdapter<Object[]> {

    private static final String className = ListAdapterIntenseItem.class.getName();
    private List<Object[]> itemContent;
    private Context context;
    private int textViewResourceId;

    public ListAdapterIntenseItem(Context context, int textViewResourceId, List<Object[]> objects) {
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
            TextView itemText = (TextView) view.findViewById(R.id.intense_names_item);

            if (itemText != null) {
                String text = item[0] + ": " + item[1] + "m/s";
                itemText.setText(text);
            }
        }

        return view;
    }
}
