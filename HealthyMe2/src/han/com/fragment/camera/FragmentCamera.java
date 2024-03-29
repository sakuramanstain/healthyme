package han.com.fragment.camera;

import static android.app.Activity.RESULT_OK;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import han.com.R;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author hanaldo
 */
public class FragmentCamera extends Fragment {

    private static final String className = FragmentCamera.class.getName();
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 100;

    private static Uri getOutputMediaFile() {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "HealthyMeCamera");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(className, "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return Uri.fromFile(mediaFile);
    }
    private Uri fileUri;
    private ImageView image1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View myFragmentView = inflater.inflate(R.layout.fragment_camera, container, false);

        image1 = (ImageView) myFragmentView.findViewById(R.id.frag_camera_image1);
        image1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = getOutputMediaFile();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                Log.d(className, fileUri.toString());
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_IMAGE);
            }
        });

        return myFragmentView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            addPhotoToGallery(fileUri);
            Toast.makeText(getActivity(), "Photo saved", Toast.LENGTH_SHORT).show();
        }
    }

    private void addPhotoToGallery(Uri file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(file);
        getActivity().sendBroadcast(mediaScanIntent);
    }
}
