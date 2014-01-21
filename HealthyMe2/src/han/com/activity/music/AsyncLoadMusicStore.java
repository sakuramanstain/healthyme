package han.com.activity.music;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import han.com.datapool.MySharedPreferences;
import han.com.music.MusicStore;
import han.com.resources.ResourceGetter;
import han.com.sdcard.ExternalStorageManager;
import han.com.tts.Speak;
import han.com.utils.HttpUtil;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.apache.http.HttpStatus;

/**
 *
 * @author hanaldo
 */
public class AsyncLoadMusicStore extends AsyncTask<Void, Object, Boolean> {

    private static final String className = AsyncLoadMusicStore.class.getName();
    private int totalSongs;
    private int loadedSongs;
    private LinkedList<ListInfo> tags;
    private ActivityMusic activity;

    public AsyncLoadMusicStore(ActivityMusic activity) {
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        totalSongs = 0;
        loadedSongs = 0;
        activity.showSyncProgressDialog();
    }

    @Override
    protected Boolean doInBackground(Void... arg0) {
        String json1;
        try {
            Object[] response = HttpUtil.httpPostData(ResourceGetter.getInstance(null).getServiceAddressAllTags(), "");
            if ((Integer) response[0] != HttpStatus.SC_OK) {
                return false;
            }
            json1 = (String) response[1];
        } catch (Exception ex) {
            Log.e(className, ex.toString());
            return false;
        }

        Gson gson = new Gson();
        Type collectionType = new TypeToken<LinkedList<ListInfo>>() {
        }.getType();

        tags = gson.fromJson(json1, collectionType);
        for (ListInfo tag : tags) {
            totalSongs += tag.getListSize();
        }

        publishProgress(0, 0 + "/" + totalSongs);

        Set<Integer> downloadedAllSongsId = new HashSet<Integer>(totalSongs);

        //download each list
        for (ListInfo list : tags) {
            if (isCancelled()) {
                return false;
            }
            String json2;
            try {
                Object[] response2 = HttpUtil.httpPostData(ResourceGetter.getInstance(null).getServiceAddressTagsSong() + "?tags=" + URLEncoder.encode(list.getListName(), "utf-8"), "");
                if ((Integer) response2[0] != HttpStatus.SC_OK) {
                    return false;
                }
                json2 = (String) response2[1];
            } catch (Exception ex) {
                Log.e(className, ex.toString());
                return false;
            }

            Integer[] songs = gson.fromJson(json2, Integer[].class);
            //download songs
            for (Integer id : songs) {
                try {
                    if (!downloadedAllSongsId.contains(id)) {
                        //if really download files
                        if (MySharedPreferences.getInstance(null).getPlayLocalMusic()) {
                            String songStreamAddress = ResourceGetter.getInstance(null).getServiceAddressSongFile() + "/" + id + ".mp3";
                            String songFilePath = ExternalStorageManager.musicFolder + id + ".mp3";
                            HttpUtil.httpFileDownload(songStreamAddress, songFilePath);

                            downloadedAllSongsId.add(id);
                            Log.d(className, "downloaded file: " + id);
                        }
                    }

                    loadedSongs++;
                    publishProgress(loadedSongs * 100 / totalSongs, loadedSongs + "/" + totalSongs);
                } catch (Exception ex) {
                    Log.e(className, ex.toString());
                    return false;
                }
            }
            MusicStore.getInstance().getAllSongs().getAllSongsByTag().put(list.getListName(), songs);
        }
        MySharedPreferences.getInstance(null).setMusicFilesAreLocal(MySharedPreferences.getInstance(null).getPlayLocalMusic());
        MusicStore.getInstance().getAllSongs().setAllTagsInfo(tags);
        return true;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        activity.getSyncProgressDialog().setProgress((Integer) values[0]);
        activity.getSyncProgressDialog().setMessage("Sync Music" + " " + (String) values[1]);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            MusicStore.getInstance().overwritePreference();
            Toast.makeText(activity, "music synced", Toast.LENGTH_LONG).show();
            Speak.getInstance(null).speak("music is ready");
            activity.updateTagViews();
        } else {
            Toast.makeText(activity, "cannot sync music", Toast.LENGTH_LONG).show();
            Speak.getInstance(null).speak("sorry, we cannot get music now, check your network connection please");
        }
        activity.dismissSyncProgressDialog();
    }
}
