package han.com.activity.music;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import han.com.R;
import han.com.utils.HttpUtil;
import han.com.utils.MySong;
import java.net.URL;
import org.apache.http.HttpStatus;

/**
 *
 * @author hanaldo
 */
public class AsyncLoadSongDataRemote extends AsyncTask<Void, Boolean, Boolean> {

    private static final String className = AsyncLoadSongDataRemote.class.getName();
    private MySong currentSong;
    private Drawable currentSongArt;
    private String currentSongStreamAddress;
    private ActivityMusic activity;
    private InterfacePostCallBack postCallBack;
    public static String ServerUrl;
    private boolean loadImage;

    public AsyncLoadSongDataRemote(ActivityMusic activity, MySong currentSong, InterfacePostCallBack postCallBack, boolean loadImage) {
        this.activity = activity;
        this.currentSong = currentSong;
        this.postCallBack = postCallBack;
        this.loadImage = loadImage;
        ServerUrl = "http://" + activity.getResources().getString(R.string.server_ip) + ":8080/HealthyMeGCM/getStreamLink";
    }

    public String getCurrentSongStreamAddress() {
        return currentSongStreamAddress;
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        if (currentSong == null) {
            Log.e(className, "no song");
            return false;
        }

        try {
            if (loadImage) {
                if (currentSong.getArtwork().startsWith("http")) {
                    URL imageUrl = new URL(currentSong.getArtwork());
                    currentSongArt = Drawable.createFromStream(imageUrl.openStream(), "src");
                    Log.d(className, "load image: " + currentSong.getArtwork());
                }
                publishProgress(true);
            }

            Object[] response = HttpUtil.httpPostData(ServerUrl + "?link=" + currentSong.getLink(), "");
            if ((Integer) response[0] != HttpStatus.SC_OK) {
                return false;
            }
            currentSongStreamAddress = (String) response[1];

        } catch (Exception ex) {
            Log.e(className, ex.toString());
            return false;
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        if (values[0]) {
            if (activity != null) {
                activity.getSongArt().setImageDrawable(currentSongArt);
                String s = currentSong.getAuthor() + " - " + currentSong.getTitle();
                activity.getSongNameText().setText(s);
            }
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            postCallBack.afterPostExecute(currentSongStreamAddress);
        } else {
            if (activity != null) {
                Toast.makeText(activity, "cannot load: " + currentSong.getTitle(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
