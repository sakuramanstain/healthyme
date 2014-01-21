package han.com.sdcard;

import android.os.Environment;
import android.util.Log;
import java.io.File;

/**
 *
 * @author hanaldo
 */
public class ExternalStorageManager {

    private static final String className = ExternalStorageManager.class.getName();
    public static final String musicFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HealthymeMusic/";

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static void makeMusicFolder() {
        File file = new File(musicFolder);

        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e(className, "Music Directory not created");
            }
        }

        Log.d(className, "Music Directory created");
    }

    public static void cleanMusicFolder() {
        File dir = new File(musicFolder);
        if (!dir.exists()) {
            return;
        }
        String[] files = dir.list();
        for (int i = 0; i < files.length; i++) {
            File f = new File(dir, files[i]);
            if (f.delete()) {
                Log.d(className, "delete file: " + f.toString());
            }
        }
    }

    private ExternalStorageManager() {
    }
}
