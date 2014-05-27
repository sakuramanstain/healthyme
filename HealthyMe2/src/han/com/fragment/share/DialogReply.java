package han.com.fragment.share;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import han.com.R;
import han.com.datapool.MySharedPreferences;
import han.com.resources.ResourceGetter;
import org.apache.http.Header;

/**
 *
 * @author han
 */
public class DialogReply extends Dialog {

    private final Context context;
    private final EditText content;
    private final Button send;

    public DialogReply(Context context, final int postId) {
        super(context);
        this.context = context;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_reply);

        content = (EditText) findViewById(R.id.dialog_reply_edit1);
        send = (Button) findViewById(R.id.dialog_reply_button1);

        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String c = content.getText().toString().trim();
                if (c.isEmpty()) {
                    Toast.makeText(DialogReply.this.context, "Please enter your reply content", Toast.LENGTH_SHORT).show();
                    return;
                }

                AsyncHttpClient http = new AsyncHttpClient();
                RequestParams params = new RequestParams();
                params.put("post", String.valueOf(postId));
                params.put("content", c);
                params.put("user", MySharedPreferences.getInstance(null).getUserName());
                http.post(ResourceGetter.getInstance(null).getServiceAddressAddReply(), params, new TextHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                        Toast.makeText(DialogReply.this.context, "Reply sent", Toast.LENGTH_SHORT).show();
                        cancel();
                    }

                    @Override
                    public void onFailure(String responseBody, Throwable error) {
                        Toast.makeText(DialogReply.this.context, "Reply failed", Toast.LENGTH_SHORT).show();
                        cancel();
                    }
                });
            }
        });
    }
}
