package han.com.fragment.share;

import android.app.Activity;
import static android.app.Activity.RESULT_OK;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import han.com.R;
import han.com.datapool.MySharedPreferences;
import han.com.resources.ResourceGetter;
import han.com.utils.MyWidgets;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.Header;

/**
 * Can view shares when offline.
 *
 * @author hanaldo
 */
public class ActivityShare extends Activity {

    private static final String className = ActivityShare.class.getName();
    private static final int REQUEST_CODE_SELECT_IMAGE = 101;
    private RadioGroup shareTo;
    private CheckBox includePhotos;
    private ScrollView images;
    private LinearLayout imagesArea;
    private RadioButton shareCoach;
    private RadioButton shareBuddy;
    private RadioButton shareEveryone;
    private EditText content;
    private Button doShare;
    private Map<String, Uri> readyImages;
    private AlertDialog waitDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        MyWidgets.makeSubActivityTitle(this, "Share Something", R.drawable.ic_menu_friends);

        shareTo = (RadioGroup) findViewById(R.id.act_share_radio_group1);
        includePhotos = (CheckBox) findViewById(R.id.act_share_checkbox1);
        imagesArea = (LinearLayout) findViewById(R.id.act_share_layout1);
        shareCoach = (RadioButton) findViewById(R.id.act_share_radio1);
        shareBuddy = (RadioButton) findViewById(R.id.act_share_radio2);
        shareEveryone = (RadioButton) findViewById(R.id.act_share_radio3);
        content = (EditText) findViewById(R.id.act_share_edit1);
        doShare = (Button) findViewById(R.id.act_share_button1);
        readyImages = new HashMap<String, Uri>(1);

        includePhotos.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                boolean checked = includePhotos.isChecked();
                if (checked) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent,
                            "Select Photo"), REQUEST_CODE_SELECT_IMAGE);
                } else {
                    imagesArea.removeAllViews();
                }
            }
        });

        doShare.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                String c = content.getText().toString().trim();
                if (c.isEmpty()) {
                    Toast.makeText(ActivityShare.this, "Please enter your sharing content", Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadShare(MySharedPreferences.getInstance(null).getUserName(), c);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                addImage(data.getData());

            } else if (data.getClipData() != null) {
                ClipData d = data.getClipData();
                for (int i = 0; i < d.getItemCount(); i++) {
                    addImage(d.getItemAt(i).getUri());
                }
            }
        } else if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_CANCELED) {
            includePhotos.setChecked(false);
            readyImages.clear();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void addImage(Uri img) {
        try {
            final ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(
                    ThumbnailUtils.extractThumbnail(
                            Media.getBitmap(getContentResolver(), img), 250, 250));
            imageView.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    v.setVisibility(View.GONE);
                    String tag = (String) v.getTag();
                    readyImages.remove(tag);
                    Toast.makeText(ActivityShare.this, "Remove image: " + tag, Toast.LENGTH_SHORT).show();
                    //remove image
                    if (readyImages.isEmpty()) {
                        includePhotos.setChecked(false);
                    }
                }
            });
            imagesArea.addView(imageView, 250, 250);
            imageView.setTag(img.getPath());
            readyImages.put(img.getPath(), img);
        } catch (Exception e) {
            Log.w(className, e);
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showUploadSuccess() {
        waitDialog.dismiss();
        new AlertDialog.Builder(ActivityShare.this)
                .setTitle("OK")
                .setMessage("Post is uploaded")
                .setOnDismissListener(new DialogInterface.OnDismissListener() {

                    public void onDismiss(DialogInterface dialog) {
                        ActivityShare.this.finish();
                    }
                })
                .create().show();
    }

    private void showUploadFail() {
        waitDialog.dismiss();
        new AlertDialog.Builder(ActivityShare.this)
                .setTitle("Error")
                .setMessage("Cannot upload post")
                .create().show();
    }

    private void uploadShare(String user, String content) {
        waitDialog = new AlertDialog.Builder(this)
                .setTitle("Please wait")
                .setMessage("Sending post to server now...")
                .setCancelable(false)
                .create();
        waitDialog.show();

        final AsyncHttpClient http = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("user", user);
        params.put("content", content);
        http.post(ResourceGetter.getInstance(null).getServiceAddressAddPost(), params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                if (!readyImages.isEmpty()) {
                    Gson g = new Gson();
                    PostResult r = g.fromJson(responseBody, PostResult.class);
                    Uri[] list = new Uri[readyImages.size()];
                    readyImages.values().toArray(list);
                    uploadImages(http, r.getPostId(), 0, list);
                    return;
                }
                showUploadSuccess();
            }

            @Override
            public void onFailure(String responseBody, Throwable error) {
                showUploadFail();
            }
        });
    }

    private void uploadImages(final AsyncHttpClient http, final int postId, final int index, final Uri[] list) {
        if (index >= readyImages.size()) {
            showUploadSuccess();
            return;
        }

        RequestParams params = new RequestParams();
        Toast.makeText(ActivityShare.this, "post id: " + postId, Toast.LENGTH_SHORT).show();
        try {
            InputStream f = getContentResolver().openInputStream(list[index]);
            params.put("imageFile", f);
        } catch (FileNotFoundException ex) {
            Toast.makeText(this, ex.toString(), Toast.LENGTH_SHORT).show();
            return;
        }
        http.post(ResourceGetter.getInstance(null).getServiceAddressPostImage() + "?postId=" + postId, params, new TextHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseBody) {
                Toast.makeText(ActivityShare.this, "image " + index + " ok", Toast.LENGTH_SHORT).show();
                uploadImages(http, postId, index + 1, list);
            }

            @Override
            public void onFailure(String responseBody, Throwable error) {
                showUploadFail();
            }
        });
    }

    private class PostResult {

        private int postId;

        public int getPostId() {
            return postId;
        }
    }
}
