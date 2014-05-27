package han.com.fragment.share;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import han.com.R;
import han.com.resources.ResourceGetter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.apache.http.Header;

/**
 *
 * @author hanaldo
 */
public class ListAdapterPostItem extends ArrayAdapter<Object[]> {

    private static final String className = ListAdapterPostItem.class.getName();
    private final List<Object[]> itemContent;
    private final Context context;
    private final SimpleDateFormat df = new SimpleDateFormat("yyyy-M-d HH:mm");
    private final SimpleDateFormat df2 = new SimpleDateFormat("EEE MMM d HH:mm");
    private final AsyncHttpClient http = new AsyncHttpClient();

    public ListAdapterPostItem(Context context, List<Object[]> objects) {
        super(context, R.layout.list_item_post, objects);
        this.context = context;
        itemContent = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Object[] item = itemContent.get(position);
        View view = convertView;

        if (view == null) {
            LayoutInflater layoutInflater = LayoutInflater.from(context);
            view = layoutInflater.inflate(R.layout.list_item_post, null);
        }

        if (item != null) {
            final Post p = (Post) item[0];

            TextView content = (TextView) view.findViewById(R.id.list_item_post_text1);
            TextView date = (TextView) view.findViewById(R.id.list_item_post_text3);
            LinearLayout imageArea = (LinearLayout) view.findViewById(R.id.list_item_post_area1);
            ImageView reply = (ImageView) view.findViewById(R.id.list_item_post_image2);
            TextView replyNumber = (TextView) view.findViewById(R.id.list_item_post_text4);
            LinearLayout replyArea = (LinearLayout) view.findViewById(R.id.list_item_post_area3);

            reply.setOnClickListener(new View.OnClickListener() {

                public void onClick(View view) {
                    DialogReply d = new DialogReply(context, p.getId());
                    d.show();
                }
            });

            content.setText(p.getUser() + "\n" + p.getContent());
            date.setText(df.format(new Date(p.getTimeValue())));

            loadImages(p, imageArea);
            loadReplys(p, replyNumber, replyArea);
        }

        return view;
    }

    private void addImageView(LinearLayout area, String imageUri, final String largeImageUri) {
        ImageView v = new ImageView(context);
        area.addView(v, 250, 250);
        v.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(largeImageUri));
                context.startActivity(i);
            }
        });
        ImageLoader.getInstance().displayImage(imageUri, v);
    }

    private void addReply(LinearLayout area, String user, String time, String content) {
        RelativeLayout rl = new RelativeLayout(context);
        rl.setLayoutParams(new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        rl.setBackgroundColor(context.getResources().getColor(R.color.my_gray));

        TextView v = new TextView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        v.setLayoutParams(params);
        v.setText(user);

        TextView v2 = new TextView(context);
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        v2.setLayoutParams(params2);
        v2.setText(time);

        TextView v3 = new TextView(context);
        ViewGroup.LayoutParams params3 = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v3.setLayoutParams(params3);
        v3.setText(content);

        rl.addView(v);
        rl.addView(v2);
        area.addView(rl);
        area.addView(v3);
    }

    private void loadReplys(final Post p, final TextView replyNumber, final LinearLayout area) {
        area.removeAllViews();

        http.get(ResourceGetter.getInstance(null).getServiceAddressGetReply() + "?post=" + p.getId(), new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                GetResult2 r = new Gson().fromJson(responseBody, GetResult2.class);
                p.setReplys(r.replys);
                String number = "(" + r.replys.size() + ")";
                replyNumber.setText(number);

                for (Reply reply : r.replys) {
                    addReply(area, reply.getUser(), df2.format(new Date(reply.getTimeValue())), reply.getContent());
                }
            }

            @Override
            public void onFailure(String responseBody, Throwable error) {
                Toast.makeText(context, "Cannot load replies", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImages(final Post p, final LinearLayout area) {
        area.removeAllViews();

        if (p.getImages() != null) {
            if (p.getImages().isEmpty()) {
                return;
            }
            for (PostImage image : p.getImages()) {
                String url = ResourceGetter.getInstance(null).getServiceAddressBase() + image.getSmall();
                String url2 = ResourceGetter.getInstance(null).getServiceAddressBase() + image.getPath();
                addImageView(area, url, url2);
            }
            return;
        }

        http.get(ResourceGetter.getInstance(null).getServiceAddressGetImages() + "?postId=" + p.getId(), new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                GetResult r = new Gson().fromJson(responseBody, GetResult.class);
                List<PostImage> images = r.images;
                p.setImages(images);

                for (PostImage image : images) {
                    String url = ResourceGetter.getInstance(null).getServiceAddressBase() + image.getSmall();
                    String url2 = ResourceGetter.getInstance(null).getServiceAddressBase() + image.getPath();
                    addImageView(area, url, url2);
                }
            }

            @Override
            public void onFailure(String responseBody, Throwable error) {
                Toast.makeText(context, "Cannot read images", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private class GetResult {

        public List<PostImage> images;
    }

    private class GetResult2 {

        public List<Reply> replys;
    }
}
