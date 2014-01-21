package han.com.ui.common;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import han.com.R;
import java.util.List;

/**
 *
 * @author han
 */
public class ListAdapterTrackSettingItem extends ArrayAdapter<Object[]> {
    
    private static final String className = ListAdapterTrackSettingItem.class.getName();
    public static final String LIST_ITEM_TITLE = "title";
    public static final String LIST_ITEM_CONTENT = "content";
    public static final String LIST_ITEM_SEPARATOR = "separator";
    private List<Object[]> itemContent;
    private Context context;
    private int textViewResourceId;
    
    public ListAdapterTrackSettingItem(Context context, int textViewResourceId, List<Object[]> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        this.textViewResourceId = textViewResourceId;
        itemContent = objects;
    }
    
    @Override
    public boolean isEnabled(int position) {
        String itemType = (String) itemContent.get(position)[2];
        return itemType.equals(LIST_ITEM_CONTENT);
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
            String itemType = (String) item[2];
            LinearLayout allSection = (LinearLayout) view.findViewById(R.id.list_setting_all_content);
            LinearLayout titleSection = (LinearLayout) view.findViewById(R.id.list_setting_section);
            LinearLayout contentSection = (LinearLayout) view.findViewById(R.id.list_setting_content);
            View separatorSection = view.findViewById(R.id.list_setting_separator);
            
            if (itemType.equals(LIST_ITEM_TITLE)) {
                contentSection.setVisibility(View.GONE);
                separatorSection.setVisibility(View.GONE);
                
                TextView title = (TextView) view.findViewById(R.id.list_setting_section_title);
                title.setText((String) item[0]);
                
            } else if (itemType.equals(LIST_ITEM_CONTENT)) {
                titleSection.setVisibility(View.GONE);
                separatorSection.setVisibility(View.GONE);
                
                TextView contentText = (TextView) view.findViewById(R.id.list_setting_item_text);
                ImageView contentImage = (ImageView) view.findViewById(R.id.list_setting_item_image);
                
                if (contentText != null) {
                    contentText.setText((String) item[0]);
                }
                
                if (contentImage != null) {
                    contentImage.setImageResource((Integer) item[1]);
                }
                
                if (item.length == 4) {
                    TextView subContent = (TextView) view.findViewById(R.id.list_setting_sub_content);
                    subContent.setText((String) item[3]);
                    subContent.setVisibility(View.VISIBLE);
                }
                
            } else if (itemType.equals(LIST_ITEM_SEPARATOR)) {
                titleSection.setVisibility(View.GONE);
                contentSection.setVisibility(View.GONE);
                allSection.setVisibility(View.GONE);
            }
            
            
        }
        return view;
    }
}
